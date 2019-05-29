package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business;

import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeachPraiseRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeacherPraiseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.UpdatePkState;
import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.config.SpeechCollectiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.dialog.SpeechStartDialog;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page.SpeechCollectiveNo2Pager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2019/4/26.
 * 集体发言2期
 */
public class SpeechCollectiveNo2Bll {
    private RelativeLayout mRootView;
    private String TAG = "SpeechCollectiveNo2Bll";
    private Logger logger = LoggerFactory.getLogger(TAG);
    private boolean addEnergy = false;
    /** 语音提示正在显示 */
    private boolean tipIsShow = false;
    /** 第一次评测 */
    private boolean isFirstSpeech = true;
    /** 语音提示只显示一次 */
    private boolean hasShowTip = false;
    private Context context;
    private LogToFile mLogtf;
    private SpeechEvaluatorUtils mSpeechEvaluatorUtils;
    /** 语音识别出来的文字 */
    private String recognizeStr = "";
    private StringBuilder ansStr = new StringBuilder();
    /** 是否正在录音 */
    private AtomicBoolean isRecord = new AtomicBoolean(false);
    /**
     * 语音保存位置-目录
     */
    private File dir;
    /** 上一次lottie播放的时间 */
    private long lottieLastPlayTime = -1;
    /** 上一次录音的时间 */
    private long lastVolumeTime = -1;
    /** 录音是否结束，用来 */
    private AtomicBoolean isStop = new AtomicBoolean(false);
    private boolean start = false;
    /** 是不是用户手动关闭 */
    private boolean userClose = false;
    private String voiceId;
    private String from;
    private long lastOneLevelTime = -1, lastTwoLevelTime = -1, lastThreeLevelTime = -1;
    /**
     * 日志数据
     */
    private String devicestatus = "0";
    SpeechCollectiveView speechCollectiveView;
    Handler handler = new Handler(Looper.getMainLooper());
    SpeechCollectiveHttp collectiveHttp;
    private SpeechStartDialog speechStartDialog;
    private LiveGetInfo liveGetInfo;
    TeacherPraiseEventReg teacherPraiseEventReg;
    long startTime;

    public SpeechCollectiveNo2Bll(Context context) {
        this.context = context;
        mLogtf = new LogToFile(context, TAG);
        mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
        dir = LiveCacheFile.geCacheFile(context, "speechCollective");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void setCollectiveHttp(SpeechCollectiveHttp collectiveHttp) {
        this.collectiveHttp = collectiveHttp;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public class TeacherPraiseEventReg {

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onRoomH5CloseEvent(final TeacherPraiseEvent event) {
            logger.d("onRoomH5CloseEvent:start=" + event.start);
//            if (speechCollectiveView != null) {
//                speechCollectiveView.setStart(!event.start);
//            }
        }
    }

    public String getVoiceId() {
        return voiceId;
    }

    public String getFrom() {
        return from;
    }

    public void start(String from, String voiceId) {
        this.voiceId = voiceId;
        this.from = from;
        if (start) {
            return;
        }
        start = true;
        mLogtf.d("start:from=" + from + ",voiceId=" + voiceId);
        try {
            String string = ShareDataManager.getInstance().getString(ShareDataConfig.SP_SPEECH_COLLECTION, "{}", ShareDataManager.SHAREDATA_USER);
            logger.d("start:string=" + string);
            JSONObject jsonObject = new JSONObject(string);
            if (jsonObject.has("liveid")) {
                String liveid = jsonObject.getString("liveid");
                if (liveGetInfo.getId().equals(liveid)) {
                    if (voiceId.equals(jsonObject.getString("voiceId"))) {
                        userClose = true;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            ShareDataManager.getInstance().put(ShareDataConfig.SP_SPEECH_COLLECTION, "{}", ShareDataManager.SHAREDATA_USER);
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        addSysTip(true);
        if (teacherPraiseEventReg != null) {
            LiveEventBus.getDefault(context).unregister(teacherPraiseEventReg);
        }
        teacherPraiseEventReg = new TeacherPraiseEventReg();
        LiveEventBus.getDefault(context).register(teacherPraiseEventReg);
        if (speechStartDialog != null) {
            speechStartDialog.cancelDialog();
        }
        speechStartDialog = new SpeechStartDialog(context);
        speechStartDialog.setStart();
        mLogtf.d("start:voiceId=" + voiceId + ",from=" + from);
        addView();
        boolean hasAudidoPermission = XesPermission.hasSelfPermission(context, Manifest.permission.RECORD_AUDIO); //
        // 检查用户麦克风权限
        if (hasAudidoPermission) {
            devicestatus = "1";
            speechCollectiveView.start();
            startEvaluator();
        } else {
            //如果没有麦克风权限，申请麦克风权限
            devicestatus = "0";
            XesPermission.checkPermissionNoAlert(context, getCallBack(), PermissionConfig.PERMISSION_CODE_AUDIO);
        }
    }

    private void addSysTip(boolean open) {
        String teacherType = "主讲";
        if ("f".equals(from)) {
            teacherType = "辅导";
        }
        String status;
        if (open) {
            status = "开启";
        } else {
            status = "关闭";
        }
        String message = teacherType + "老师" + status + "了集体发言";

        LiveMessageBll liveMessageBll = ProxUtil.getProxUtil().get(context, LiveMessageBll.class);
        if (liveMessageBll != null) {
            liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                    message);
        } else {
            com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.LiveMessageBll liveMessageBllOld =
                    ProxUtil.getProxUtil().get(context, com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.LiveMessageBll.class);
            if (liveMessageBllOld != null) {
                liveMessageBllOld.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                        message);
            }
        }
    }

    LiveActivityPermissionCallback getCallBack() {
        return new LiveActivityPermissionCallback() {
            /**
             * 结束
             */
            @Override
            public void onFinish() {
                logger.i("onFinish()");
            }

            /**
             * 用户拒绝某个权限
             */
            @Override
            public void onDeny(String permission, int position) {
                logger.i("onDeny()");
                speechCollectiveView.onDeny();
            }

            /**
             * 用户允许某个权限
             */
            @Override
            public void onGuarantee(String permission, int position) {
                logger.i("onGuarantee()");
                speechCollectiveView.start();
                startEvaluator();
            }
        };
    }

    private void addView() {
        final SpeechCollectiveNo2Pager speechCollectiveNo2Pager = new SpeechCollectiveNo2Pager(context, mRootView);
        speechCollectiveNo2Pager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                mRootView.removeView(basePager.getRootView());
                if (mSpeechEvaluatorUtils != null) {
                    mSpeechEvaluatorUtils.cancel();
                    isRecord.set(false);
                }
                userClose = true;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("liveid", liveGetInfo.getId());
                    jsonObject.put("voiceId", voiceId);
                    ShareDataManager.getInstance().put(ShareDataConfig.SP_SPEECH_COLLECTION, "" + jsonObject, ShareDataManager.SHAREDATA_USER);
                } catch (Exception e) {
                    CrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
        speechCollectiveNo2Pager.setSpeechCollecPresenter(new SpeechCollecPresenter() {
            @Override
            public void onRequest() {
                XesPermission.checkPermissionNoAlert(context, getCallBack(), PermissionConfig.PERMISSION_CODE_AUDIO);
            }
        });
        mRootView.addView(speechCollectiveNo2Pager.getRootView());
        speechCollectiveView = speechCollectiveNo2Pager;
        tipIsShow = true;
        speechCollectiveView.onHaveVolume(new SpeechCollectiveView.OnTipHide() {
            @Override
            public void hide() {
                tipIsShow = false;
            }
        });
    }

    private EvaluatorListener evaluatorListener = new NoVoice();

    private void startEvaluator() {
        isRecord.set(true);
        File saveFile = new File(dir, "speechbul" + System.currentTimeMillis() + ".mp3");
        mSpeechEvaluatorUtils.startSpeechCollectRecognize(saveFile.getPath(), SpeechEvaluatorUtils.RECOGNIZE_CHINESE, evaluatorListener);
    }

    private abstract class BaseNoVoice implements EvaluatorListener {
        @Override
        public void onBeginOfSpeech() {
            logger.i("onBeginOfSpeech");
        }

        @Override
        public void onResult(ResultEntity resultEntity) {
            logger.i("onResult:errorno=" + resultEntity.getErrorNo() + " curString:" + resultEntity.getCurString() + " status:" + resultEntity.getStatus());
            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                if (resultEntity.getErrorNo() > 0) {
                    recognizeError(resultEntity.getErrorNo());
                } else {
                    recognizeSuccess(resultEntity.getCurString(), true);
                }
            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
                    speechCollectiveView.onDeny();
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStop.get()) {
                                startEvaluator();
                            }
                        }
                    }, 1000);
                }
            } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                recognizeSuccess(resultEntity.getCurString(), false);
            }
        }
    }

    private class NoVoice extends BaseNoVoice {
        @Override
        public void onBeginOfSpeech() {
            super.onBeginOfSpeech();
            logger.d("onBeginOfSpeech:isFirstSpeech=" + isFirstSpeech);
            if (isFirstSpeech) {
                startTime = System.currentTimeMillis();
                isFirstSpeech = false;
                handler.postDelayed(timeOut, 8000);
            }
        }

        @Override
        public void onVolumeUpdate(int volume) {
            logger.d("NoVoice:onVolumeUpdate:volume=" + volume);
            performVolume(volume, true);
            if (volume > 1) {
                if (!hasShowTip) {
                    handler.removeCallbacks(timeOut);
                    handler.postDelayed(timeOut, 8000);
                }
                if (tipIsShow) {
                    tipIsShow = false;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            speechCollectiveView.onHaveVolume();
                        }
                    });
                }
            }
        }
    }

    private class HaveTipVoice extends BaseNoVoice {

        @Override
        public void onVolumeUpdate(int volume) {
            logger.d("HaveTipVoice:onVolumeUpdate:volume=" + volume);
            performVolume(volume, true);
        }
    }

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            hasShowTip = true;
            tipIsShow = true;
            evaluatorListener = new HaveTipVoice();
            speechCollectiveView.onNoVolume(new SpeechCollectiveView.OnTipHide() {
                @Override
                public void hide() {
                    tipIsShow = false;
                    logger.d("onNoVolume:hide");
                }
            });
            logger.d("onNoVolume:time=" + (System.currentTimeMillis() - startTime));
        }
    };

    public void onTeacherLevel() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("liveid", liveGetInfo.getId());
            jsonObject.put("voiceId", voiceId);
            ShareDataManager.getInstance().put(ShareDataConfig.SP_SPEECH_COLLECTION, "" + jsonObject, ShareDataManager.SHAREDATA_USER);
        } catch (Exception e) {
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        stop("onTeacherLevel");
    }

    public void stop(String method) {
        mLogtf.d("stop:from=" + from + ",method=" + method + ",voiceId=" + voiceId + ",userClose=" + userClose);
        if (!start) {
            return;
        }
        if (!userClose) {
            addSysTip(false);
        }
        start = false;
        mSpeechEvaluatorUtils.cancel();
        isStop.set(true);
        isRecord.set(false);
        if (!userClose) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    speechStartDialog = new SpeechStartDialog(context);
                    speechStartDialog.setSop();
                    if (speechCollectiveView != null) {
                        mRootView.removeView(speechCollectiveView.getRootView());
                    }
                }
            });
        }
        if (teacherPraiseEventReg != null) {
            LiveEventBus.getDefault(context).unregister(teacherPraiseEventReg);
            teacherPraiseEventReg = null;
        }
        EventBus.getDefault().post(new UpdatePkState(TAG + ":stop"));
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void setBottomContent(final RelativeLayout mRootView) {
        this.mRootView = mRootView;
    }

    /**
     * 识别成功
     *
     * @param str      识别初来的JSONObject---String
     * @param isFinish 识别是否结束
     */
    private void recognizeSuccess(String str, boolean isFinish) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            String content = jsonObject.optString("nbest");
            content = content.replaceAll("。", "");
            if (!TextUtils.isEmpty(content)) {
                recognizeStr = content;
//                tvTitle.setText(content);
//                logger.i("recognizeSuccess:content" + content);
            }
            if (isFinish && isRecord.get()) {
                ansStr.append(recognizeStr);
                final String msg = ansStr.toString();
                ansStr = new StringBuilder();
                recognizeStr = "";
                logger.i("recognizeSuccess");
                mSpeechEvaluatorUtils.cancel();
                isRecord.set(false);
                collectiveHttp.uploadSpeechMsg(voiceId, "" + msg, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        logger.i("onDataSucess:data=" + objData[0]);
                        String sendmsg;
                        if (msg.length() > 15) {
                            sendmsg = msg.substring(0, 15) + "...";
                        } else {
                            sendmsg = msg;
                        }
                        collectiveHttp.sendSpeechMsg(from, voiceId, "" + sendmsg);
                        addEnergy();
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        logger.i("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
                    }
                });
                handler.removeCallbacks(timeOut);
                //第一次八秒提示
//                isFirstSpeech = true;
                startEvaluator();
            }
            // tvTitle.setText("说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什么课程说出你想找什");
            //if(tvTitle.getLineCount()>2){
            //tvTitle.setText(ellipsizeString(tvTitle.getText().toString(),tvTitle));
            //}
//            if (isFinish) {
//                mSpeechEvaluatorUtils.cancel();
//                String s = tvTitle.getText().toString().replaceAll("\\*", "");
//                if (TextUtils.isEmpty(s) || s.startsWith("没听清") || s.length() == 1) {
//                    setStatus(RECERROR);
//                    return;
//                }
//                if (recgonizeCallback != null) {
//                    recgonizeCallback.onDataSucess(content.replaceAll("\\*", ""));
//                    if (mBlurPopupWindow != null) {
//                        mBlurPopupWindow.dismiss();
//                    }
//                }
//                setStatus(SEARCHING);
//                Loger.i("voice search____" + "success");
//            } else {
//                Loger.i("voice search____" + "recording");
//            }
        } catch (Exception e) {
            logger.i("recognizeSuccess" + e.getMessage());
            recognizeError(0);
        }
    }

    private boolean addEnergy() {
        logger.d("addEnergy:pk=" + liveGetInfo.getIsAllowTeamPk());
        if (!addEnergy && "1".equals(liveGetInfo.getIsAllowTeamPk())) {
            addEnergy = true;
//            SpeechEnergyPager speechEnergyPager = new SpeechEnergyPager(context);
//            mRootView.addView(speechEnergyPager.getRootView());
//            speechEnergyPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
//                @Override
//                public void onClose(LiveBasePager basePager) {
//                    mRootView.removeView(basePager.getRootView());
//                    LiveEventBus.getDefault(context).post(new TeacherPraiseEvent(false));
//                    EventBus.getDefault().post(new TeachPraiseRusltulCloseEvent(voiceId));
//                }
//            });
            LiveEventBus.getDefault(context).post(new TeacherPraiseEvent(false));
            EventBus.getDefault().post(new TeachPraiseRusltulCloseEvent(voiceId));
            return true;
        }
        return false;
    }

    private void recognizeError(int code) {
        logger.i("recognizeErrori:code=" + code);
        if (code == 11) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isStop.get()) {
                        startEvaluator();
                    }
                }
            }, 1000);
        }
    }

    /**
     * 解决音量大小
     *
     * @param volume
     */
    private synchronized void performVolume(int volume, boolean isOnline) {
        if (mRootView == null || isStop.get()) {
            return;
        }
        long nowTime = System.currentTimeMillis();
        if (nowTime - lottieLastPlayTime > SpeechCollectiveConfig.LOTTIE_VIEW_INTERVAL && volume > SpeechCollectiveConfig.GOLD_MICROPHONE_VOLUME) {
            mRootView.post(new Runnable() {
                @Override
                public void run() {
//                    showGoldMicroPhoneView();
                    logger.i("lottie view show");
                    //显示金话筒的Lottie View
//                    mGoldView.showLottieView();
                }
            });
            if (lottieLastPlayTime == -1) {
                sendIsGoldMicroPhone(true, true, "");
            }
            lottieLastPlayTime = nowTime;
        }
        if (nowTime - lastVolumeTime > SpeechCollectiveConfig.VOLUME_INTERVAL) {
            ///1挡位
            int gear = 1;
            if (volume < SpeechCollectiveConfig.ONE_GEAR_RIGHT
                    && volume >= SpeechCollectiveConfig.ONE_GEAR_LEFT) {
                List<SoundWaveView.Circle> list = speechCollectiveView.getRipples();
                if (((nowTime - lastOneLevelTime > SpeechCollectiveConfig.GOLD_ONE_LEVEL_INTEVAL)
                        || (lastVolumeTime > lastOneLevelTime) && list.size() == 0)) {
                    gear = 1;
                    lastOneLevelTime = nowTime;
                    speechCollectiveView.addRipple(gear);
                    logger.i("add Ripple level = " + gear);
                }
//                else if (isOnline) {
//                    gear = 1;
//                    lastOneLevelTime = nowTime;
//                    mGoldView.addRipple(gear);
//                    logger.i("add Ripple level = " + gear);
//                }
            } else if (volume > SpeechCollectiveConfig.ONE_GEAR_RIGHT
                    && volume < SpeechCollectiveConfig.TWO_GEAR_RIGHT) {
                //2档
                if (nowTime - lastTwoLevelTime > SpeechCollectiveConfig.GOLD_TWO_LEVEL_INTEVAL) {
                    gear = 2;
                    lastTwoLevelTime = nowTime;
                    lastVolumeTime = nowTime;
                    speechCollectiveView.addRipple(gear);
                    logger.i("add Ripple level = " + gear);
                }
//                else if (isOnline) {
//                    gear = 2;
//                    lastTwoLevelTime = nowTime;
//                    lastVolumeTime = nowTime;
//                    mGoldView.addRipple(gear);
//                    logger.i("add Ripple level = " + gear);
//                }
            } else {
                if (nowTime - lastTwoLevelTime > SpeechCollectiveConfig.GOLD_THREE_LEVEL_INTEVAL) {
                    //3档
                    gear = 3;
                    lastThreeLevelTime = nowTime;
                    lastVolumeTime = nowTime;
                    speechCollectiveView.addRipple(gear);
                    logger.i("add Ripple level = " + gear);
                }
//                else if (isOnline) {
//                    gear = 3;
//                    lastThreeLevelTime = nowTime;
//                    lastVolumeTime = nowTime;
//                    mGoldView.addRipple(gear);
//                    logger.i("add Ripple level = " + gear);
//                }
            }
        }
    }

    private void sendIsGoldMicroPhone(boolean isOpenMicrophone, boolean isGoldMicrophone, String sign) {

    }
}
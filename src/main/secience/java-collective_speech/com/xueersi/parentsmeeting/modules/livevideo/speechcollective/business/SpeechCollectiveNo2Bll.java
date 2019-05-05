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
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.config.SpeechCollectiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.dialog.SpeechStartDialog;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page.SpeechCollectiveNo2Pager;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page.SpeechEnergyPager;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page.SpeechPraisePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

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
    private long lastOneLevelTime = -1, lastTwoLevelTime = -1, lastThreeLevelTime = -1;
    /**
     * 日志数据
     */
    private String devicestatus = "0";
    SpeechCollectiveView speechCollectiveView;
    Handler handler = new Handler(Looper.getMainLooper());
    SpeechCollectiveHttp collectiveHttp;
    private SpeechStartDialog speechStartDialog;

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

    public void start(String roomId) {
        if (start) {
            return;
        }
        start = true;
        if (speechStartDialog != null) {
            speechStartDialog.cancelDialog();
        }
        speechStartDialog = new SpeechStartDialog(context);
        speechStartDialog.setStart();
        mLogtf.d("start:roomId=" + roomId);
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

    ;


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
    }

    private void startEvaluator() {
        isRecord.set(true);
        File saveFile = new File(dir, "speechbul" + System.currentTimeMillis() + ".mp3");
        mSpeechEvaluatorUtils.startSpeechCollectRecognize(saveFile.getPath(), SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                new EvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        logger.i("onBeginOfSpeech");
                        post = false;
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

                    @Override
                    public void onVolumeUpdate(int volume) {
                        logger.d("onVolumeUpdate:volume=" + volume + ",post=" + post);
                        performVolume(volume, true);
                        if (!post && volume < 2) {
                            post = true;
                            handler.removeCallbacks(timeOut);
                            handler.postDelayed(timeOut, 8000);
                        }
                        if (volume > 1) {
                            handler.removeCallbacks(timeOut);
                            handler.postDelayed(timeOut, 8000);
                        }
                    }
                });
    }

    private boolean post = false;
    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            speechCollectiveView.onNoVolume();
            logger.d("onNoVolume");
        }
    };

    public void stop() {
        start = false;
        mLogtf.d("start:stop");
        mSpeechEvaluatorUtils.cancel();
        isStop.set(true);
        isRecord.set(false);
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

    public void onResume() {

    }

    public void onPause() {

    }

    public void setBottomContent(final RelativeLayout mRootView) {
        this.mRootView = mRootView;
//        if (com.xueersi.common.config.AppConfig.DEBUG) {
//            SpeechPraisePager speechPraisePager = new SpeechPraisePager(context);
//            mRootView.addView(speechPraisePager.getRootView());
//            speechPraisePager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
//                @Override
//                public void onClose(LiveBasePager basePager) {
//                    mRootView.removeView(basePager.getRootView());
//                }
//            });
//            SpeechEnergyPager speechEnergyPager = new SpeechEnergyPager(context);
//            mRootView.addView(speechEnergyPager.getRootView());
//            speechEnergyPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
//                @Override
//                public void onClose(LiveBasePager basePager) {
//                    mRootView.removeView(basePager.getRootView());
//                }
//            });
//        }
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
                recognizeStr = "";
                logger.i("recognizeSuccess");
                mSpeechEvaluatorUtils.cancel();
                isRecord.set(false);
                collectiveHttp.uploadSpeechMsg("", "" + ansStr, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        logger.i("onDataSucess:data=" + objData[0]);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        logger.i("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
                    }
                });
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
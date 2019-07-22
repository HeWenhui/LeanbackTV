package com.xueersi.parentsmeeting.modules.livevideoOldIJK.goldmicrophone;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PCMFormat;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.BuildConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCacheFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tal.speech.speechrecognizer.ResultCode.MUTE;
import static com.tal.speech.speechrecognizer.ResultCode.SPEECH_CANCLE;
import static com.xueersi.parentsmeeting.modules.livevideoOldIJK.goldmicrophone.GoldPhoneContract.GOLD_MICROPHONE_VOLUME;
import static com.xueersi.parentsmeeting.modules.livevideoOldIJK.goldmicrophone.GoldPhoneContract.LOTTIE_VIEW_INTERVAL;
import static com.xueersi.parentsmeeting.modules.livevideoOldIJK.goldmicrophone.GoldPhoneContract.MP3_FILE_NAME;
import static com.xueersi.parentsmeeting.modules.livevideoOldIJK.goldmicrophone.GoldPhoneContract.VOLUME_INTERVAL;

/**
 * 幼升小金话筒
 */
public class GoldMicroPhoneBll extends LiveBaseBll implements NoticeAction, GoldPhoneContract.GoldPhonePresenter {
    private boolean testUse = true && BuildConfig.DEBUG;
    /**
     * 录音对象
     */
    private AudioRecord mAudioRecord = null;
    private static final int DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    /**
     * 采样率
     */
    private int DEFAULT_SAMPLING_RATE = 16000;//44100 16000
    /**
     * 设置为单声道
     */
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_DEFAULT;
    /**
     * 音频格式
     */
    private static final PCMFormat DEFAULT_AUDIO_FORMAT = PCMFormat.PCM_16BIT;
    /**
     * 每次读取的字节大小
     */
    private int mBufferSize;
    /** 原始录音数据 */
    private short[] mPCMBuffer;
    /** 语音评测工具类,用来走在线识别 */
    private SpeechEvaluatorUtils mSpeechEvaluatorUtils;
    /** 语音识别出来的文字 */
    private String recognizeStr = new String();
    private StringBuilder ansStr = new StringBuilder();
    GoldPhoneContract.GoldPhoneView mGoldView;
    /** 金话筒标志位 */
    private String sign;
    /** 录音是否结束，用来 */
    private AtomicBoolean isStop = new AtomicBoolean(false);
    File dir;
    /** 是否走在线语音测评 */
    private AtomicBoolean isOnline = new AtomicBoolean(false);
    //是否含有脏词
//    private AtomicBoolean hasSensitiveWords = new AtomicBoolean(false);
    /** 上一次lottie播放的时间 */
    private long lottieLastPlayTime = -1;
    /** 上一次录音的时间 */
    private long lastVolumeTime = -1;

    private long lastOneLevelTime = -1, lastTwoLevelTime = -1, lastThreeLevelTime = -1;
    /** 是否正在录音 */
    private AtomicBoolean isRecord = new AtomicBoolean(false);

    public GoldMicroPhoneBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        dir = LiveCacheFile.geCacheFile(mContext, "gold_microphone_voice");
    }

    private Runnable onLineToOffLineRunnable = new Runnable() {
        @Override
        public void run() {
            if (isOnline.get() && isRecord.get() && mSpeechEvaluatorUtils != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        logger.i("cancel onLine,take offLine");
                        mSpeechEvaluatorUtils.cancel();
                        if (mRootView != null) {
                            mRootView.postDelayed(ru, 500);
                        }
                    }
                });

            }
        }
    };
    private Runnable ru = new Runnable() {
        @Override
        public void run() {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    offLineRecord();
                }
            });
        }
    };

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.ARTS_GOLD_MICROPHONE: {
                int open = data.optInt("open");
                sign = data.optString("sign");
                logger.i("receive arts_gold_microphone open = " + open);
                if (open == 1) {
                    if (mGetInfo.getLiveTopic().getMode().equals(LiveTopic.MODE_TRANING)) {
                        return;
                    }
                    isStop.set(false);

                    showMicroPhoneView();
                    getIsOnlineRecognize(sign);
                    boolean isHasAudioPermission = isHasAudioPermission();
                    sendIsGoldMicroPhone(isHasAudioPermission, false, sign);
                    showGoldSettingView(isHasAudioPermission);
                    if (mRootView != null) {
                        mRootView.postDelayed(onLineToOffLineRunnable, 20000);
                    }
                } else {
                    if (isOnline.get() && !isStop.get()) {
                        logger.i("Content:" + ansStr + recognizeStr);
                        sendNotice(ansStr.append(recognizeStr).toString());
                    }

                    recognizeStr = new String();
                    ansStr = new StringBuilder();
                    //提示关闭语音弹幕
                    if (mGoldView != null) {
                        mGoldView.showCloseView();
                    }
                    stopRecord();
                    logger.i(recognizeStr);

                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.ARTS_GOLD_MICROPHONE};
    }

    private void showOrhideBottom(boolean visible) {
        if (mContext instanceof Activity) {
            //隐藏底部控制栏
            View view = ((Activity) mContext).findViewById(R.id.ll_livevideo_bottom_controller);
            if (view != null) {
                view.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
            //隐藏半身直播右侧的聊天消息区
            View messageView = ((Activity) mContext).findViewById(R.id.rcl_live_halfbody_msg);
            if (messageView != null) {
                messageView.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 得到是否走线上识别
     *
     * @param sign
     */
    public void getIsOnlineRecognize(String sign) {
        getHttpManager().getIsOnlineRecognize(mGetInfo.getId(), sign, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (responseEntity.getJsonObject() instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    isOnline.set(jsonObject.optInt("isGoldMicrophoneToAi") == 0 ? false : true);
                    startAudioRecord();
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                isOnline.set(false);
                startAudioRecord();
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                isOnline.set(false);
                startAudioRecord();
            }
        });
    }

    /**
     * 显示权限View
     */
    private void showGoldSettingView(final boolean hasPermission) {
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (mGoldView != null) {
                    mGoldView.showSettingView(!hasPermission);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoldView != null) {
            mGoldView.onResume();
        }
    }

    /**
     * 是否有语音权限
     *
     * @return
     */
    private synchronized boolean isHasAudioPermission() {
        PackageManager pkm = mContext.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName()));
    }

    /**
     * 显示麦克风的View
     */
    private void showMicroPhoneView() {
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (mGoldView == null) {
                    mGoldView = new MicroPhonePager(mContext, GoldMicroPhoneBll.this);
                }
                if (mGoldView.getRootView().getParent() == null) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mRootView.addView(mGoldView.getRootView(), layoutParams);
                    mGoldView.performAddView();
                    showOrhideBottom(false);
                }
            }
        });
    }

    /**
     * 发送是否是金话筒
     *
     * @param isOpenMicrophone 是否打开了麦克风
     * @param isGoldMicrophone 是否得到了金话筒
     * @param sign             金话筒标志
     */
    private void sendIsGoldMicroPhone(boolean isOpenMicrophone, boolean isGoldMicrophone, String sign) {
        String strOpenMicrophone = isOpenMicrophone ? "1" : "0";
        String strGoldMicrophone = isGoldMicrophone ? "1" : "0";
        logger.i("send gold microphone");
        getHttpManager().sendIsGoldPhone(
                mGetInfo.getId(),
                strOpenMicrophone,
                strGoldMicrophone,
                sign,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("gold microphone send success");
                    }
                });
    }

    // ThreadPoolExecutor
    private Executor executor = Executors.newCachedThreadPool();

    /**
     * 开始录音
     * 只能在工作线程里面调用
     */
    @WorkerThread
    @Override
    public void startAudioRecord() {
        boolean isHasAudioPermission = isHasAudioPermission();
        if (!isHasAudioPermission || isRecord.get()) {
            return;
        }
        logger.i("isOnline = " + isOnline);
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                if (isRecord.get()) {
//                    return;
//                }
                if (!isOnline.get()) {
                    offLineRecord();
                } else {
                    try {
                        onLineRecord();
                    } catch (Exception e) {
                        isOnline.set(false);
                        offLineRecord();
                    }
                }
            }
        });
    }

    @Override
    public void stopAudioRecord() {
        if (mSpeechEvaluatorUtils != null) {
            mSpeechEvaluatorUtils.cancel();
        }
        stopRecord();
    }

    /**
     * 离线记录
     */
    private void offLineRecord() {
        //不走在线，判断下声音大小就可以了
        mBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLING_RATE,
                DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat());
        mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat(),
                mBufferSize);
        mPCMBuffer = new short[mBufferSize];
        try {
            mAudioRecord.startRecording();
            isRecord.set(true);
            while (!isStop.get()) {
//                        if (mAudioRecord != null) {
//                logger.i("read1:" + mBufferSize);
                int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
//                logger.i("read2:" + readSize);
                int volume = calculateRealVolume(mPCMBuffer, readSize);
                logger.i("offline volume = " + volume);
                performVolume(volume, false);
//                        }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mRootView != null) {
                mRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "当前有应用正在使用录音功能，请关掉后再重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * 在线记录
     */
    private void onLineRecord() {
//        SpeechConfig.setStatus(SpeechConfig.SPEECH_CHS_MICROPHONE);
        if (mSpeechEvaluatorUtils == null) {
            mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
        }
        File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/liveSpeech/");
        String path = dir.getPath() + MP3_FILE_NAME;
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mSpeechEvaluatorUtils.startOnlineChsRecognize(
                path,
                SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                evaluatorListener);
        isRecord.set(true);
    }

    private EvaluatorListener evaluatorListener = new EvaluatorListener() {
        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onResult(ResultEntity resultEntity) {
            logger.i("voice search____code:" + resultEntity.getErrorNo() + " curString:" + resultEntity.getCurString() + " status:" + resultEntity.getStatus());
            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                if (resultEntity.getErrorNo() > 0) {
                    recognizeError(resultEntity.getErrorNo());
                } else {
                    recognizeSuccess(resultEntity.getCurString(), true);
                }
            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                recognizeError(resultEntity.getErrorNo());
            } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                recognizeSuccess(resultEntity.getCurString(), false);
            }
        }

        @Override
        public void onVolumeUpdate(int volume) {
            logger.i("online volume = " + String.valueOf(volume));
            performVolume(volume, true);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecord();
    }

    /**
     * 解决音量大小
     *
     * @param volume
     */
    private synchronized void performVolume(int volume, boolean isOnline) {
        if (mGoldView == null || mRootView == null || isStop.get()) {
            return;
        }
        long nowTime = System.currentTimeMillis();
        if (nowTime - lottieLastPlayTime > LOTTIE_VIEW_INTERVAL && volume > GOLD_MICROPHONE_VOLUME) {
            mRootView.post(new Runnable() {
                @Override
                public void run() {
//                    showGoldMicroPhoneView();
                    logger.i("lottie view show");
                    //显示金话筒的Lottie View
                    mGoldView.showLottieView();
                }
            });
            if (lottieLastPlayTime == -1) {
                sendIsGoldMicroPhone(true, true, sign);
            }
            lottieLastPlayTime = nowTime;
        }
        if (nowTime - lastVolumeTime > VOLUME_INTERVAL) {
            ///1挡位
            int gear = 1;
            if (volume < GoldPhoneContract.ONE_GEAR_RIGHT
                    && volume >= GoldPhoneContract.ONE_GEAR_LEFT) {
                List<SoundWaveView.Circle> list = mGoldView.getRipples();
                if (((nowTime - lastOneLevelTime > GoldPhoneContract.GOLD_ONE_LEVEL_INTEVAL)
                        || (lastVolumeTime > lastOneLevelTime) && list.size() == 0)
                ) {
                    gear = 1;
                    lastOneLevelTime = nowTime;
                    mGoldView.addRipple(gear);
                    logger.i("add Ripple level = " + gear);
                }
//                else if (isOnline) {
//                    gear = 1;
//                    lastOneLevelTime = nowTime;
//                    mGoldView.addRipple(gear);
//                    logger.i("add Ripple level = " + gear);
//                }
            } else if (volume > GoldPhoneContract.ONE_GEAR_RIGHT
                    && volume < GoldPhoneContract.TWO_GEAR_RIGHT
            ) {
                //2档
                if (nowTime - lastTwoLevelTime > GoldPhoneContract.GOLD_TWO_LEVEL_INTEVAL) {
                    gear = 2;
                    lastTwoLevelTime = nowTime;
                    lastVolumeTime = nowTime;
                    mGoldView.addRipple(gear);
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
                if (nowTime - lastTwoLevelTime > GoldPhoneContract.GOLD_THREE_LEVEL_INTEVAL) {
                    //3档
                    gear = 3;
                    lastThreeLevelTime = nowTime;
                    lastVolumeTime = nowTime;
                    mGoldView.addRipple(gear);
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

    //识别失败，当前网络不可用，或者大点声说
    private void recognizeError(int errNum) {
        logger.i("voice search____error:" + errNum);
        if (errNum == ResultCode.NO_AUTHORITY) {
//            setStatus(NOPERMISSION);
            if (isRecord.get()) {
                mSpeechEvaluatorUtils.cancel();
                ansStr.append(recognizeStr);
                recognizeStr = "";
                logger.i(" isRecord = " + isRecord.get());
                mSpeechEvaluatorUtils.startOnlineChsRecognize(
                        dir.getPath() + MP3_FILE_NAME,
                        SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                        evaluatorListener);
            }
        } else if (errNum == MUTE || errNum == SPEECH_CANCLE) {
            if (mGoldView != null) {
                mGoldView.getRootView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRecord.get()) {
                            mSpeechEvaluatorUtils.cancel();
                            ansStr.append(recognizeStr);
                            recognizeStr = "";
                            logger.i(" isRecord = " + isRecord.get());
                            mSpeechEvaluatorUtils.startOnlineChsRecognize(
                                    dir.getPath() + MP3_FILE_NAME,
                                    SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                                    evaluatorListener);
                        }
                    }
                }, 300);
            }
        } else {
            if (errNum == ResultCode.WEBSOCKET_TIME_OUT || errNum == ResultCode.NETWORK_FAIL || errNum == ResultCode.WEBSOCKET_CONN_REFUSE) {
                XESToastUtils.showToast(mContext, "当前网络不可用，请检查网络连接");
            }
            mRootView.post(new Runnable() {
                @Override
                public void run() {
                    if (mGoldView != null) {
                        mGoldView.showSpeakLoudly();
                    }
                }
            });
//            setStatus(RECERROR);
        }
//        performVolume(0);

//        } else {
//            try {
//                recognizeSuccess(str2json(tvTitle.getText().toString()), true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
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
            JSONArray array = jsonObject.optJSONArray("sensitiveWords");
            content = content.replaceAll("。", "");
            if (!TextUtils.isEmpty(content)) {
                recognizeStr = content;
//                tvTitle.setText(content);
                logger.i("====voice content" + content);
            }
            if (isFinish && isRecord.get()) {
                ansStr.append(recognizeStr);
                recognizeStr = "";
                logger.i("isFinish = " + isFinish + " isRecord = " + isRecord.get());
                logger.i("restart evaluator");
                mSpeechEvaluatorUtils.cancel();
                mSpeechEvaluatorUtils.startOnlineChsRecognize(
                        dir.getPath() + MP3_FILE_NAME,
                        SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                        evaluatorListener);
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
            logger.i("voice search____" + e.getMessage());
            recognizeError(0);
        }
    }

    /**
     * 计算录音音量
     *
     * @param buffer   buffer
     * @param readSize readSize
     */
    private int calculateRealVolume(short[] buffer, int readSize) {
        double sum = 0;
        for (int i = 0; i < readSize; i++) {
            // 这里没有做运算的优化，为了更加清晰的展示代码
            sum += buffer[i] * buffer[i];
        }
        if (readSize > 0) {
            double amplitude = sum / readSize;
            int volume = (int) Math.sqrt(amplitude);
            volume = (volume * 30 / 10000);
            logger.i("calculate volume :" + volume);
            return volume;
        }
        return 0;
    }

    private void sendNotice(String msg) {
        logger.i("sendNotice");
        if (mLiveBll != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "" + XESCODE.ARTS_GOLD_MICROPHONE_SEND_TEACHER);
//            jsonObject.put("to", uid);
                jsonObject.put("msg", msg);
                jsonObject.put("id", mGetInfo.getStuId());
                jsonObject.put("name", mGetInfo.getStuName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
                mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
            }
//            else {
//                mLiveBll.sendNotice(mLiveBll.getCounTeacherStr(), jsonObject);
//            }
        }
    }

    /**
     * 移除View
     *
     * @param view
     */
    @Override
    public void remove(View view) {
        if (view.getParent() == mRootView) {
            logger.i("remove gold view");
            mRootView.removeView(view);
            stopRecord();
        }
        recognizeStr = "";
        ansStr = new StringBuilder();
        lottieLastPlayTime = -1;
        showOrhideBottom(true);
        mGoldView = null;
    }

    private void stopRecord() {
        if (mRootView != null) {
            mRootView.removeCallbacks(onLineToOffLineRunnable);
        }
        if (mAudioRecord != null && isRecord.get()) {
            mAudioRecord.release();
            isRecord.set(false);
        }
        if (mSpeechEvaluatorUtils != null && isRecord.get()) {
            mSpeechEvaluatorUtils.cancel();
            isRecord.set(false);
        }
        isStop.set(true);
    }

    /**
     * 是否是辅导状态
     */
    private boolean istraning = false;

    @Override
    public void onModeChange(final String oldMode, final String mode, final boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        logger.i("调用了modeChang方法");
        if (mGoldView != null) {
            mGoldView.getRootView().post(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(mContext, "oldMode = " + oldMode + " mode = " + mode + " isPresent = " + isPresent, Toast.LENGTH_SHORT).show();
//        logger.i();
                    if (!LiveTopic.MODE_CLASS.equals(mode)) {
                        istraning = true;
                        if (mGoldView instanceof GoldPhoneContract.CloseTipPresenter) {
                            ((GoldPhoneContract.CloseTipPresenter) mGoldView).removeGoldView();
                        }
                    } else {
                        istraning = false;
                    }
                }
            });

        }
    }
}

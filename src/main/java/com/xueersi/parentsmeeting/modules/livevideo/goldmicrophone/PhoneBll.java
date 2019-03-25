package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.view.View;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PCMFormat;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.BuildConfig;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class PhoneBll extends LiveBaseBll implements NoticeAction, GoldPhoneContract.GoldPhonePresenter {

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
    /**
     * 原始录音数据
     */
    private short[] mPCMBuffer;
    /**
     * 语音评测工具类
     */
    private SpeechEvaluatorUtils mSpeechEvaluatorUtils;

    GoldPhoneContract.GoldPhoneView mGoldView;

    private boolean isDebug = BuildConfig.DEBUG;
    private String sign;

    private Handler mHanler = new Handler(Looper.getMainLooper());

    File dir;

    /** 是否上传至在线 */
    private int isOnline = 0;
    /** 金话筒的音量 */
    private int GOLD_MICROPHONE_VOLUME;

    public PhoneBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        dir = LiveCacheFile.geCacheFile(mContext, "gold_microphone_voice");

        if (isDebug) {
            LiveThreadPoolExecutor.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                sendNotice("");
                            }
                        }
                    }
            );
        }

    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.ARTS_GOLD_MICROPHONE: {
                int open = data.optInt("open");
                sign = data.optString("sign");
                logger.i("receive arts_gold_microphone open = " + open);
                if (open == 1) {
                    showMicroPhoneView();
                    getIsOnlineRecognize(sign);
                    boolean isHasAudioPermission = isHasAudioPermission();
                    sendIsGoldMicroPhone(isHasAudioPermission, false, sign);
                    showGoldSettingView(isHasAudioPermission);
                } else {
                    //提示关闭语音弹幕
                    if (mGoldView != null) {
                        mGoldView.showCloseView();
                    }
                    sendNotice(recognizeStr.toString());
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
                    isOnline = jsonObject.optInt("isGoldMicrophoneToAi");
                    startAudioRecord();
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                startAudioRecord();
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                startAudioRecord();
            }
        });
    }

    /**
     * 显示权限View
     */
    private void showGoldSettingView(boolean isShow) {
        if (!isShow) {
            mGoldView.showSettingView(true);
        } else {
            mGoldView.showSettingView(false);
        }
    }

    /**
     * 是否有语音权限
     *
     * @return
     */
    private boolean isHasAudioPermission() {
        PackageManager pkm = mContext.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName()));
    }

    /**
     * 显示麦克风的View
     */
    private void showMicroPhoneView() {
        if (mGoldView == null) {
            mGoldView = new PhoneView(mContext, this);
        }
        mRootView.addView(mGoldView.getRootView());
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

    /**
     * 开始录音
     * 只能在工作线程里面调用
     */
    @WorkerThread
    @Override
    public void startAudioRecord() {
        if (isOnline != 1) {
            //不走在线，判断下声音大小就可以了
            mBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLING_RATE,
                    DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat());
            mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                    DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat(),
                    mBufferSize);
            mPCMBuffer = new short[mBufferSize];
            mAudioRecord.startRecording();
            while ((true)) {
                if (mAudioRecord != null) {
                    int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
                    int volume = calculateRealVolume(mPCMBuffer, readSize);
                    logger.i("volume = " + volume);
                    performVolume(volume);
                }
            }
        } else {
            if (mSpeechEvaluatorUtils == null) {
                mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
            }
            File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/liveSpeech/");
            String path = dir.getPath() + "/gold_microphone.mp3";
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mSpeechEvaluatorUtils.startOnlineRecognize(path, SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                    new EvaluatorListener() {
                        @Override
                        public void onBeginOfSpeech() {
                        }

                        @Override
                        public void onResult(ResultEntity resultEntity) {
                            logger.i("voice search____" + resultEntity.getErrorNo() + resultEntity.getCurString() + resultEntity.getStatus());
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
                            logger.i(String.valueOf(volume));
                            performVolume(volume);
                        }
                    });
        }
    }

    /**
     * 解决音量大小
     *
     * @param volume
     */
    private void performVolume(int volume) {
        if (volume > GOLD_MICROPHONE_VOLUME) {
            showGoldMicroPhoneView();
        } else {
            ///1挡位
            int gear = 1;
            if (volume < GoldPhoneContract.ONE_GEAR_RIGHT && volume > GoldPhoneContract.ONE_GEAR_LEFT) {
                gear = 1;

            } else if (volume < GoldPhoneContract.TWO_GEAR_RIGHT) {
                //2档
                gear = 2;

            } else {
                //3档
                gear = 3;

            }
        }
    }

    //识别失败，当前网络不可用，或者大点声说
    private void recognizeError(int errNum) {
        logger.i("voice search____error");
        mSpeechEvaluatorUtils.cancel();
//        if () {
        if (errNum == ResultCode.NO_AUTHORITY) {
//            setStatus(NOPERMISSION);

        } else {
            if (errNum == ResultCode.WEBSOCKET_TIME_OUT || errNum == ResultCode.NETWORK_FAIL || errNum == ResultCode.WEBSOCKET_CONN_REFUSE) {
                XESToastUtils.showToast(mContext, "当前网络不可用，请检查网络连接");
            }
            mGoldView.showSpeakLoudly();
//            setStatus(RECERROR);
        }
//        } else {
//            try {
//                recognizeSuccess(str2json(tvTitle.getText().toString()), true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
    }

    /** 语音识别出来的文字 */
    private StringBuilder recognizeStr = new StringBuilder();

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
            if (array != null && array.length() > 0) {
                for (int i = array.length() - 1; i >= 0; i--) {
                    StringBuilder star = new StringBuilder();
                    for (int j = 0; j < array.getString(i).length(); j++) {
                        star.append("*");
                    }
                    content = content.replaceAll(array.getString(i), star.toString());
                }
            }
            content = content.replaceAll("。", "");
            if (!TextUtils.isEmpty(content)) {
                recognizeStr.append(content);
//                tvTitle.setText(content);
                logger.i("====voice content" + content);
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
            volume = (volume > 30 ? 30 : volume);
            logger.i("volume :" + volume);
            return volume;


        }
        return 0;
    }

    /**
     * 显示金话筒的Lottie View
     */
    private void showGoldMicroPhoneView() {

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
                jsonObject.put("name", mGetInfo.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
                mLiveBll.sendMessage(mLiveBll.getMainTeacherStr(), jsonObject);
            } else {
                mLiveBll.sendMessage(mLiveBll.getCounTeacherStr(), jsonObject);
            }
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
            mRootView.removeView(view);
        }
    }
}

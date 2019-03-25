package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.tal.speech.speechrecognizer.PCMFormat;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import org.json.JSONObject;

import java.io.File;

public class PhoneBll extends LiveBaseBll implements NoticeAction, GoldPhoneContract.GoldPhonePresenter {

    GoldPhoneContract.GoldPhoneView mGoldView;

    private String sign;

    private Handler mHanler = new Handler(Looper.getMainLooper());

    File dir;
    /** 金话筒的音量 */
    private int GOLD_MICROPHONE_VOLUME;

    public PhoneBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        dir = LiveCacheFile.geCacheFile(mContext, "gold_microphone_voice");
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.ARTS_GOLD_MICROPHONE: {
                logger.i("arts_gold_microphone open");
                int open = data.optInt("open");
                sign = data.optString("sign");
                if (open == 1) {
                    showMicroPhoneView();
                    startAudioRecord();
                    getIsOnlineRecognize(sign);
                    boolean isHasAudioPermission = isHasAudioPermission();
                    sendIsGoldMicroPhone(isHasAudioPermission, false, sign);
                    showGoldSettingView(isHasAudioPermission);
                } else {
                    //提示关闭语音弹幕
                    if (mGoldView != null) {
                        mGoldView.showCloseView();
                    }
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

    /** 是否上传至在线 */
    private int isOnline = 0;

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
                }
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

    /**
     * 开始录音
     */
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
                    if (volume > GOLD_MICROPHONE_VOLUME) {
                        showGoldMicroPhoneView();
                    } else {

                    }
                    Message msg = mHanler.obtainMessage();

                    msg.obj = volume;
                }

            }
        } else {
            if (mSpeechEvaluatorUtils == null) {
                mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
            }

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

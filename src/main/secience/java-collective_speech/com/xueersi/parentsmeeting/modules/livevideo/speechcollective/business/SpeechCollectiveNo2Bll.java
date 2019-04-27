package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business;

import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.config.SpeechCollectiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page.SpeechCollectiveNo2Pager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

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
    private long lastOneLevelTime = -1, lastTwoLevelTime = -1, lastThreeLevelTime = -1;
    /**
     * 日志数据
     */
    private String devicestatus = "0";
    SpeechCollectiveView speechCollectiveView;
    Handler handler = new Handler(Looper.getMainLooper());

    public SpeechCollectiveNo2Bll(Context context) {
        this.context = context;
        mLogtf = new LogToFile(context, TAG);
        mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
        dir = LiveCacheFile.geCacheFile(context, "speechCollective");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void start(String roomId) {
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
            XesPermission.checkPermissionNoAlert(context, new LiveActivityPermissionCallback() {
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
            }, PermissionConfig.PERMISSION_CODE_AUDIO);
        }
    }

    private void addView() {
        final SpeechCollectiveNo2Pager speechCollectiveNo2Pager = new SpeechCollectiveNo2Pager(context, mRootView);
        speechCollectiveNo2Pager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                mRootView.removeView(basePager.getRootView());
                if (mSpeechEvaluatorUtils != null) {
                    mSpeechEvaluatorUtils.cancel();
                }
            }
        });
        mRootView.addView(speechCollectiveNo2Pager.getRootView());
        speechCollectiveView = speechCollectiveNo2Pager;
    }

    private void startEvaluator() {
        File saveFile = new File(dir, "speechbul" + System.currentTimeMillis() + ".mp3");
        mSpeechEvaluatorUtils.startChineseSpeechBulletRecognize(saveFile.getPath(), SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                new EvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        logger.i("onBeginOfSpeech");
                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {
                        logger.i("onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo() + ",sid=" + resultEntity.getSid());
                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {

                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isStop.get()) {
                                        startEvaluator();
                                    }
                                }
                            }, 1000);
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {

                        }
                    }

                    @Override
                    public void onVolumeUpdate(int volume) {
                        logger.d("onVolumeUpdate:volume=" + volume);
                        performVolume(volume, true);
                        handler.removeCallbacks(timeOut);
                        handler.postDelayed(timeOut, 8000);
                    }
                });
    }

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            speechCollectiveView.onNoVolume();
            logger.d("onNoVolume");
        }
    };

    public void stop() {
        mLogtf.d("start:stop");
//        if (swvView != null) {
//            swvView.setStart(false);
//        }
        isStop.set(true);
        mSpeechEvaluatorUtils.cancel();
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void setBottomContent(RelativeLayout mRootView) {
        this.mRootView = mRootView;
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
                        || (lastVolumeTime > lastOneLevelTime) && list.size() == 0)
                        ) {
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
                    && volume < SpeechCollectiveConfig.TWO_GEAR_RIGHT
                    ) {
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
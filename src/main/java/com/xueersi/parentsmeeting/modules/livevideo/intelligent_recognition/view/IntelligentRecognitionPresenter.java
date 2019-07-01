package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.content.Context;
import android.content.IntentFilter;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.Constants;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionBroadcast;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget.MyObserver;

import java.io.File;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;

public class IntelligentRecognitionPresenter implements IIntelligentRecognitionPresenter<IIntelligentRecognitionView>, MyObserver {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private IIntelligentRecognitionView baseView;

    private Context mContext;

    private IntelligentRecognitionViewModel mViewModel;

    private SpeechUtils mSpeechUtils;

    public IntelligentRecognitionPresenter(Context context) {
        this.mContext = context;
    }

    IntelligentRecognitionBroadcast broadcast;

    /**
     * 注册广播事件
     */
    private void registEvent() {
        broadcast = new IntelligentRecognitionBroadcast();
        broadcast.setIrcReceiver(new IrcReceiverImpl());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FILTER_ACTION);
        mContext.registerReceiver(broadcast, intentFilter);
    }

    @Override
    public void registerMessage() {
        registEvent();
    }

    @Override
    public void unregisterMessage() {
        unregistEvent();
    }

    @Override
    public void setViewModel(IntelligentRecognitionViewModel mViewModel) {
        this.mViewModel = mViewModel;
    }

    @Override
    public void startSpeech() {
        isSpeechStart = true;
        judgeSpeech();
    }

    private void judgeSpeech() {
        if (isSpeechStart && isSpeechReady) {
            startRecordSound();
        }
    }

    public boolean isSpeechStart = false;

    private void unregistEvent() {
        if (broadcast != null) {
            mContext.unregisterReceiver(broadcast);
        }

    }

    private volatile boolean isSpeechReady = false;

    private void onReadyRefresh() {

    }

    private void startRecordSound() {
        String videoSavePath = mContext.getDir("chinese_parterner", Context.MODE_PRIVATE).getPath() + File.separator + "sound.mp3";
        SpeechParamEntity mParam = new SpeechParamEntity();
        mParam.setRecogType(SpeechConfig.SPEECH_CHINESE_EVALUATOR_OFFLINE);
        mParam.setLocalSavePath(videoSavePath);
        mParam.setMultRef(false);
        mParam.setLearning_stage("-1");
        mParam.setEarly_return_sec("90");
        mParam.setVad_pause_sec("5");
        mParam.setVad_max_sec("90");
        mParam.setIsRct("1");
        mSpeechUtils.startRecog(mParam, new EvaluatorListener() {
            @Override
            public void onBeginOfSpeech() {

            }

            @Override
            public void onResult(ResultEntity result) {

            }

            @Override
            public void onVolumeUpdate(int volume) {

            }
        });
//        mParam.setStrEvaluator();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onCreate() {
        createSpeech();
    }

    /** 初始化语音测评模块 */
    private void createSpeech() {
        if (mSpeechUtils == null) {
            mSpeechUtils = SpeechUtils.getInstance(mContext);
        }
        mSpeechUtils = SpeechUtils.getInstance(mContext.getApplicationContext());
        mSpeechUtils.prepar(Constants.ASSESS_PARAM_LANGUAGE_CH, new SpeechEvaluatorUtils.OnFileSuccess() {
            @Override
            public void onFileInit(int code) {

            }

            @Override
            public void onFileSuccess() {
                // 文件初始化完了，评测准备就绪
                logger.i("Speech Success");
                isSpeechReady = true;
                onReadyRefresh();
            }

            @Override
            public void onFileFail() {
                logger.i("Speech Fail");
                isSpeechReady = false;
                XESToastUtils.showToast(mContext.getApplicationContext(), "加载评测模型失败");
            }
        });
    }

    @Override
    public void setView(IIntelligentRecognitionView view) {
        this.baseView = view;
    }

    private class IrcReceiverImpl implements IntelligentRecognitionBroadcast.IRCReceiver {

        @Override
        public void stop(String goldJSON) {
            baseView.receiveStopEvent(goldJSON);
        }
    }
}

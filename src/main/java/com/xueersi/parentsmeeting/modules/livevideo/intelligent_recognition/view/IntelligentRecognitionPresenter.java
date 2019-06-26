package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.content.Context;
import android.content.IntentFilter;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionBroadcast;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget.MyObserver;

import java.io.File;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;

public class IntelligentRecognitionPresenter implements IIntelligentRecognitionPresenter, MyObserver {

    private IIntelligentRecognitionView baseView;

    private Context mContext;

    public IntelligentRecognitionPresenter(Context context, IIntelligentRecognitionView mView) {
        this.baseView = mView;
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

    private void unregistEvent() {
        if (broadcast != null) {
            mContext.unregisterReceiver(broadcast);
        }
    }

    public void startRecordSound() {
        SpeechUtils mSpeechUtils = SpeechUtils.getInstance(mContext);
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
//        mParam.setStrEvaluator();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onCreate() {

    }

    private class IrcReceiverImpl implements IntelligentRecognitionBroadcast.IRCReceiver {

        @Override
        public void stop(String goldJSON) {
            baseView.receiveStopEvent(goldJSON);
        }
    }
}

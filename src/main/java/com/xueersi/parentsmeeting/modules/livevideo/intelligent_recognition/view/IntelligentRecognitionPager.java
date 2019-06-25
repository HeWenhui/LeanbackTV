package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.content.Context;
import android.view.View;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.widget.WaveView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 英语智能测评Pager
 */
public class IntelligentRecognitionPager extends BasePager {

    private WaveView waveView;

    public IntelligentRecognitionPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.activity_intelligent_recognition, null);
        waveView = view.findViewById(R.id.wv_livevideo_intelligent_recognition_energy_bar);
        delayWaveView();
        return view;
    }

    /**
     * 延迟初始化WaveView
     */
    private void delayWaveView() {
        Observable.
                <Boolean>empty().
                delay(400, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        waveView.initialize();
                        waveView.start();
                    }
                });
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
    public void initData() {

    }
}

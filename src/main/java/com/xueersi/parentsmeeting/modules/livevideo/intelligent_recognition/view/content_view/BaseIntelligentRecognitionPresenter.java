package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.Constants;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.SpeechStopEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.IntelligentRecognitionHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.rxutils.RxFilter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.audio.ContentAudioManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.BaseIntelligentRecognitionBll;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentLifecycleObserver;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget.IntelligentRecognitionBroadcast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;

abstract class BaseIntelligentRecognitionPresenter extends
        BaseIntelligentRecognitionBll<IntelligentRecognitionViewModel>
        implements IIntelligentRecognitionPresenter<IIntelligentRecognitionView>,
        IntelligentLifecycleObserver {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

//    protected FragmentActivity mActivity;

    IntelligentRecognitionBroadcast broadcast;

    SpeechUtils mSpeechUtils;

    ContentAudioManager contentAudioManager;
//    protected MediaPlayer mediaPlayer;

//    protected IntelligentRecognitionViewModel mViewModel;

    private IIntelligentRecognitionView baseView;

    protected IntelligentRecognitionRecord mRecord;

    //    private IntelligentRecognitionHttpManager httpManager;
    private IntelligentRecognitionHttpResponseParser intelligentRecognitionHttpResponseParser;
    //    private AtomicBoolean isSpeechStart = new AtomicBoolean(false);
    //第几次语音测评
    private int speechNum = 0;
    /** 当前测评所处状态 */
    private AtomicInteger speechStatus = new AtomicInteger(IntelligentConstants.NOT_SPEECH);
    /** speech模型是否加载成功 */
    private AtomicBoolean isSpeechReady = new AtomicBoolean(false);
    /** 语音测评开始时间 */
    private long speechStartTime;
    /** 纠音开口时长 */
    private long speechRepeatTime;
    /** 本地音频评价文件是否成功加载到内存中 */
    private boolean isEvaluationReady = false;

    private ResultEntity _resultEntity;

    public BaseIntelligentRecognitionPresenter(FragmentActivity context) {
        super(context, IntelligentRecognitionViewModel.class);
        this.mActivity = context;
//        this.mViewModel = ViewModelProviders.of(mActivity).get(IntelligentRecognitionViewModel.class);
        this.mRecord = mViewModel.getRecordData();
        contentAudioManager = new ContentAudioManager(mActivity,
                mRecord.getLiveId(),
                mRecord.getMaterialId());
//        observeScoreLottieFinish();
        //这里做一次转化是因为Boolean可能为null
        Boolean isEvaluationReadyB = mViewModel.getIsEvaluationReady().getValue();
        if (isEvaluationReadyB != null) {
            isEvaluationReady = isEvaluationReadyB;
            if (!isEvaluationReady) {
                observeEvaluationReady();
            } else {
//                isEvaluationReady = true;
                logger.i("isEvaluationReady2:" + isEvaluationReady);
                notifyWaveView();
            }
        } else {
            observeEvaluationReady();
            logger.i("isEvaluationReadyB is null");
        }
    }

    protected abstract void performResultUnity3DAudio(ResultEntity resultEntity);

    private void observeScoreLottieFinish() {
        mViewModel.getIsScorePopWindowFinish().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                logger.i("observeScoreLottieFinish:" + aBoolean);
                Observable.
                        just(aBoolean).
                        filter(RxFilter.filterTrue()).
                        delay(2, TimeUnit.SECONDS).
                        subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                logger.i("isEvaluationReady1:" + aBoolean);
                                performResultUnity3DAudio(_resultEntity);
                            }
                        });
            }
        });
    }

    private void observeEvaluationReady() {
        mViewModel.getIsEvaluationReady().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                isEvaluationReady = aBoolean;
                logger.i("isEvaluationReady1:" + aBoolean);
                if (isEvaluationReady) {
                    notifyWaveView();
                }
            }
        });
    }

    //返回当前是第几个speechNum
    protected int getSpeechNum() {
        return speechNum;
    }

    /**
     * 注册广播事件
     */
    private void registEvent() {
        broadcast = new IntelligentRecognitionBroadcast();
        broadcast.setIrcReceiver(new IrcReceiverImpl());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FILTER_ACTION);
        mActivity.registerReceiver(broadcast, intentFilter);
    }

//    protected IntelligentRecognitionHttpResponseParser getIntelligentRecognitionHttpResponseParser() {
//        if (intelligentRecognitionHttpResponseParser == null) {
//            intelligentRecognitionHttpResponseParser = new IntelligentRecognitionHttpResponseParser();
//        }
//        return intelligentRecognitionHttpResponseParser;
//    }

//    protected IntelligentRecognitionHttpManager getHttpManager() {
//        if (httpManager == null) {
//            httpManager = new IntelligentRecognitionHttpManager(m);
//        }
//        return httpManager;
//    }

    /** 注册消息接受者 */
    @Override
    public void registerMessage() {
        registEvent();
    }

    /** 注销消息接收者 */
    @Override
    public void unregisterMessage() {
        unregistEvent();
    }

    @Override
    public void startSpeech() {
        performStartRecord();
    }

    protected void performStartRecord() {
        if (speechStatus.get() == IntelligentConstants.NOT_SPEECH && isSpeechReady.get()) {
            startRecordSound();
        }
    }

    /**
     * 取消监听
     */
    private void unregistEvent() {
        if (broadcast != null) {
            mActivity.unregisterReceiver(broadcast);
        }
    }


    /**
     * 开始语音测评
     */
    protected void startRecordSound() {
        speechNum++;
//        String videoSavePath = mActivity.getDir("chinese_parterner", Context.MODE_PRIVATE).getPath() + File.separator + "sound.mp3";
        String audioSavePath = Environment.getExternalStorageDirectory() + "/parentsmeeting/livevideo";
        final SpeechParamEntity mParam = new SpeechParamEntity();
        File file = new File(audioSavePath, "sound.mp3");
        logger.i(file.getPath() + " " + file.exists());
        if (!file.exists()) {
            logger.i(" file null or file not exists");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            return;
        }
        mParam.setLocalSavePath(file.getPath());
        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        mParam.setMultRef(false);
        mParam.setPcm(true);
//        if (mViewModel.getIeResultData().getValue() == null) {
//            logger.i("viewModel IEResult is null");
//            return;
//        }
        String aiRecogStr = mViewModel.getRecordData().getContent();
//        if(aiRecogStr)
        if (TextUtils.isEmpty(aiRecogStr) && AppConfig.DEBUG) {
            return;
//        aiRecogStr = "you are right";
        }
        mParam.setStrEvaluator(aiRecogStr);
        logger.i("strRecog:" + aiRecogStr);
        if (mSpeechUtils.isOfflineSuccess()) {
//            Observable.
//                    just(mSpeechUtils.isOfflineSuccess()).
//                    filter(RxFilter.filterTrue()).
//                    subscribe(new Consumer<Boolean>() {
//                        @Override
//                        public void accept(Boolean aBoolean) throws Exception {
            mSpeechUtils.cancel();
            mSpeechUtils.startRecog(mParam, new EvaluatorListener() {
                @Override
                public void onBeginOfSpeech() {
                    if (speechNum >= 2) {
                        speechStatus.set(IntelligentConstants.SPEECH_AGIN);
                    } else {
                        speechStatus.set(IntelligentConstants.SPEECH_ING);
                    }
                    speechStartTime = System.currentTimeMillis();
                    logger.i("speech begin");
                }

                @Override
                public void onResult(ResultEntity result) {

                    if (result == null) {
                        logger.e("result = null");
                        return;
                    }
                    if (AppConfig.DEBUG && result.getStatus() == ResultEntity.SUCCESS) {
                        logger.i("speech result = " + getResultString(result));
                    }

                    if (result.getStatus() == ResultEntity.SUCCESS) {
                        _resultEntity = result;
                        handleResult(result);
                        speechStatus.set(IntelligentConstants.SPEECH_OVER_JUDGE);
                    }
                }

                @Override
                public void onVolumeUpdate(int volume) {
//                                logger.i("speech volume:" + volume);
                    mViewModel.getVolume().setValue(volume);
                }
            });
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            logger.e(throwable.getStackTrace());
//                            throwable.printStackTrace();
//                        }
//                    });
        }
    }

    /**
     * 语音测评准备工作已经完成，可以开始测评了
     */
    private void notifyWaveView() {
        logger.i("notifyWaveView: isSpeechReady:" +
                isSpeechReady.get()
                + " isEvaluationReady:" +
                isEvaluationReady);
        if (isSpeechReady.get() && isEvaluationReady) {
            mViewModel.getIsSpeechReady().postValue(true);
        }
    }

//    private List<PhoneScore> phoneScores;

    /** 处理语音评测结果 */
    protected abstract void handleResult(ResultEntity resultEntity);

    @Override
    public void onDestroy() {
    }

    @Override
    public void onCreate() {
        createSpeech();
        getRemoteEntity();
    }

    /**
     * 获取接口数据
     * {
     * "answer": "friend",
     * "choiceType": 0,
     * "gold": "2",
     * "id": [
     * "3282277"
     * ],
     * "isAllow42": 1,
     * "isTestUseH5": "0",
     * "nonce": "a451339510194553b2198a0f99cbe300",
     * "num": 0,
     * "package_source": "2",
     * "ptype": 30,
     * "refresh": false,
     * "time": "3",
     * "type": "1104"
     * }
     */
    private void getRemoteEntity() {
//        final IntelligentRecognitionHttpManager httpManager = new IntelligentRecognitionHttpManager(mActivity);

        getHttpManager().getIEResult(
                mActivity,
                mRecord.getLiveId(),
                mRecord.getMaterialId(),
                mRecord.getStuId(),
                mRecord.getStuCouId(),
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        IntelligentRecognitionHttpResponseParser intelligentRecognitionHttpResponseParser = new IntelligentRecognitionHttpResponseParser();
                        IEResult ieResult = intelligentRecognitionHttpResponseParser.parseIEResponse(responseEntity);
                        if (ieResult != null) {
                            ViewModelProviders.of(mActivity).get(IntelligentRecognitionViewModel.class).
                                    getIeResultData().setValue(ieResult);
                        } else {

                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.e("getRemoteEntity pmError");
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.e("getRemoteEntity pmFail:" + msg);
                    }
                });
    }

    /** 初始化语音测评模块 */
    private void createSpeech() {
        if (mSpeechUtils == null) {
            mSpeechUtils = SpeechUtils.getInstance(mActivity.getApplicationContext());
        }
        mSpeechUtils = SpeechUtils.getInstance(mActivity.getApplicationContext());
        mSpeechUtils.prepar(Constants.ASSESS_PARAM_LANGUAGE_CH, new SpeechEvaluatorUtils.OnFileSuccess() {
            @Override
            public void onFileInit(int code) {

            }

            @Override
            public void onFileSuccess() {
                // 文件初始化完了，评测准备就绪
                logger.i("Speech Success");
                isSpeechReady.set(true);
                notifyWaveView();
//                mViewModel.getIsSpeechReady().postValue(true);
            }

            @Override
            public void onFileFail() {
                logger.i("Speech Fail");
                isSpeechReady.set(false);
//                mViewModel.getIsSpeechReady().postValue(false);
                XESToastUtils.showToast(mActivity.getApplicationContext(), "加载评测模型失败");
            }
        });
    }

    @Override
    public void setView(IIntelligentRecognitionView view) {
        this.baseView = view;
    }

    private class IrcReceiverImpl implements IntelligentRecognitionBroadcast.IRCReceiver {

        @Override
        public void stop(String jsonString) {
//            String goldJSON = "";
            int type = 0;
            String gold = "0";
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                type = jsonObject.optInt("type");
                gold = jsonObject.optString("gold");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            SpeechStopEntity stopEntity = new SpeechStopEntity();
            stopEntity.setSpeechStatus(getSpeechStatus());
            baseView.receiveStopEvent(stopEntity);
        }
    }

    /**
     * log使用，会去result详细信息
     *
     * @param resultEntity
     * @return
     */
    private String getResultString(ResultEntity resultEntity) {
//        List<PhoneScore> list = resultEntity.getLstPhonemeScore();
        StringBuilder stringBuilder = new StringBuilder();
        if (resultEntity.getLstPhonemeScore() == null) {
            return "Lst null " + getNext(resultEntity);
        }
        for (PhoneScore phoneScore : resultEntity.getLstPhonemeScore()) {
            stringBuilder.append(phoneScore.getWord() + " " + phoneScore.getScore() + " ");
        }
        stringBuilder.append("@@@");
//        if (resultEntity.getScores() == null) {
//            return "scores null" + stringBuilder + getNext(resultEntity);
//        }
//        for (Integer integer : resultEntity.getScores()) {
//            stringBuilder.append(integer + " ");
//        }
        stringBuilder.append("@@@");
        return stringBuilder.toString() + " status:" + resultEntity.getStatus()
                + " curStatus " + resultEntity.getCurStatus()
                + " contScore " + resultEntity.getContScore()
                + " partScore " + resultEntity.getPartScore()
                + " Score " + resultEntity.getScore()
                + " curString " + resultEntity.getCurString()
                + " PronScore " + resultEntity.getPronScore();
    }

    private String getNext(ResultEntity resultEntity) {
        return " status:" + resultEntity.getStatus()
                + " curStatus " + resultEntity.getCurStatus()
                + " contScore " + resultEntity.getContScore()
                + " partScore " + resultEntity.getPartScore()
                + " Score " + resultEntity.getScore()
                + " curString " + resultEntity.getCurString()
                + " PronScore " + resultEntity.getPronScore();
    }

    /**
     * 获取当前测评状态
     *
     * @return
     */
    protected int getSpeechStatus() {
        return speechStatus.get();
    }

    /**
     * 获取语音测评时间
     *
     * @return
     */
    protected long getSpeechTime() {
        return System.currentTimeMillis() - speechStartTime;
    }
}

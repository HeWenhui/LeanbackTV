package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

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
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.RxFilter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.HttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.HttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget.IntelligentRecognitionBroadcast;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget.MyObserver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;

public abstract class BaseIntelligentRecognitionPresenter implements IIntelligentRecognitionPresenter<IIntelligentRecognitionView>, MyObserver {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    protected FragmentActivity mActivity;

    IntelligentRecognitionBroadcast broadcast;

    SpeechUtils mSpeechUtils;

//    protected MediaPlayer mediaPlayer;

    protected IntelligentRecognitionViewModel mViewModel;

    private IIntelligentRecognitionView baseView;

    protected IntelligentRecognitionRecord mRecord;

    private AtomicBoolean isSpeechStart = new AtomicBoolean(false);
    //第几次语音测评
    private int speechNum = 0;
    /** speech模型是否加载成功 */
    private AtomicBoolean isSpeechReady = new AtomicBoolean(false);

    public BaseIntelligentRecognitionPresenter(FragmentActivity context) {
        this.mActivity = context;
        this.mViewModel = ViewModelProviders.of(mActivity).get(IntelligentRecognitionViewModel.class);
        this.mRecord = mViewModel.getRecordData();
    }

    //返回当前是第几个speechNum
    int getSpeechNum() {
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
        if (!isSpeechStart.get() && isSpeechReady.get()) {
            startRecordSound();
        }
    }

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
        String videoSavePath = Environment.getExternalStorageDirectory() + "/parentsmeeting/livevideo";
        final SpeechParamEntity mParam = new SpeechParamEntity();
        File file = new File(videoSavePath, "sound.mp3");
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
        String aiRecogStr = mViewModel.getRecordData().getAnswers();
//        if (TextUtils.isEmpty(aiRecogStr) && AppConfig.DEBUG) {
        aiRecogStr = "you are right";
//        }
        mParam.setStrEvaluator(aiRecogStr);
        logger.i("strRecog:" + aiRecogStr);

        Observable.
                just(mSpeechUtils.isOfflineSuccess()).
                filter(RxFilter.filterTrue()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mSpeechUtils.cancel();
                        mSpeechUtils.startRecog(mParam, new EvaluatorListener() {
                            @Override
                            public void onBeginOfSpeech() {
                                isSpeechStart.set(true);
                                logger.i("speech begin");
                            }

                            @Override
                            public void onResult(ResultEntity result) {
                                if (AppConfig.DEBUG) {
                                    logger.i("speech result = " + getResultString(result));
                                }
                                if (result.getStatus() == ResultEntity.SUCCESS) {
                                    handleResult(result);
                                }
                            }

                            @Override
                            public void onVolumeUpdate(int volume) {
                                logger.i("speech volume:" + volume);
                                mViewModel.getVolume().setValue(volume);
                            }
                        });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logger.e(throwable.getStackTrace());
                        throwable.printStackTrace();
                    }
                });
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
//        getRemoteEntity();
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
//        final HttpManager httpManager = new HttpManager(mActivity);
        HttpManager.getIEResult(
                mActivity,
                mRecord.getLiveId(),
                mRecord.getMaterialId(),
                mRecord.getStuId(),
                mRecord.getStuCouId(),
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        HttpResponseParser httpResponseParser = new HttpResponseParser();
                        IEResult ieResult = httpResponseParser.parseResponse(responseEntity);
                        if (ieResult != null) {
                            ViewModelProviders.
                                    of(mActivity).
                                    get(IntelligentRecognitionViewModel.class).
                                    getIeResultData().
                                    setValue(ieResult);
                        } else {

                        }
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
                mViewModel.getIsSpeechReady().setValue(true);
            }

            @Override
            public void onFileFail() {
                logger.i("Speech Fail");
                isSpeechReady.set(false);
                mViewModel.getIsSpeechReady().setValue(false);
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
        public void stop(String goldJSON) {
            baseView.receiveStopEvent(goldJSON);
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
            return "Lst null" + getNext(resultEntity);
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
}

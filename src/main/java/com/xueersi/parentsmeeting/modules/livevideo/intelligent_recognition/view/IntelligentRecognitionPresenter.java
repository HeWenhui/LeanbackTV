package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Environment;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;

public class IntelligentRecognitionPresenter implements IIntelligentRecognitionPresenter<IIntelligentRecognitionView>, MyObserver {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private FragmentActivity mActivity;

    IntelligentRecognitionBroadcast broadcast;

    private SpeechUtils mSpeechUtils;

    private MediaPlayer mediaPlayer;

    private IntelligentRecognitionViewModel mViewModel;

    private IIntelligentRecognitionView baseView;

    private IntelligentRecognitionRecord mRecord;

    public IntelligentRecognitionPresenter(FragmentActivity context) {
        this.mActivity = context;
        this.mViewModel = ViewModelProviders.of(mActivity).get(IntelligentRecognitionViewModel.class);
        this.mRecord = mViewModel.getRecordData();
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
        isSpeechStart = true;
        performStartRecord();
    }

    private void performStartRecord() {
        if (isSpeechStart && isSpeechReady) {
            startRecordSound();
        }
    }

    public boolean isSpeechStart = false;

    private void unregistEvent() {
        if (broadcast != null) {
            mActivity.unregisterReceiver(broadcast);
        }

    }

    private volatile boolean isSpeechReady = false;

    /**
     * 开始语音测评
     */
    private void startRecordSound() {
//        String videoSavePath = mActivity.getDir("chinese_parterner", Context.MODE_PRIVATE).getPath() + File.separator + "sound.mp3";
        String videoSavePath = Environment.getExternalStorageDirectory() + "/parentsmeeting/livevideo";
        final SpeechParamEntity mParam = new SpeechParamEntity();
        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
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
        mParam.setLocalSavePath(videoSavePath);
        mParam.setMultRef(false);
//        mParam.setLearning_stage("-1");
        mParam.setPcm(true);
        mParam.setLearning_stage("-1");
//        mParam.setEarly_return_sec("90");
        mParam.setVad_pause_sec("3");
        mParam.setVad_max_sec("30");
//        mParam.setIsRct("1");
//        mParam.setLearning_stage("-1");
//        mParam.setEarly_return_sec("10");
//        mParam.setVad_pause_sec("3");
//        mParam.setVad_max_sec("30");
//        mParam.setIsRct("1");
        String aiRecogStr = mViewModel.getRecordData().getAnswers();
//        if (TextUtils.isEmpty(aiRecogStr) && AppConfig.DEBUG) {
        aiRecogStr = "Are you ok";
//        }
        mParam.setStrEvaluator(aiRecogStr);
        logger.i("strRecog:" + aiRecogStr);
        Observable.just(true).delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                mSpeechUtils.startRecog(mParam, new EvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        logger.i("speech begin");
                    }

                    @Override
                    public void onResult(ResultEntity result) {
//                logger.i("speech resultCurString:" + result.getCurString());
//                logger.i("speech resultStatus:" + result.getStatus());
                        if (AppConfig.DEBUG) {
                            logger.i("speech result = " + getResultString(result));
                        }
                        handleResult(result);
                    }

                    @Override
                    public void onVolumeUpdate(int volume) {
                        logger.i("speech volume:" + volume);
                        mViewModel.getVolume().setValue(volume);
                    }
                });
            }
        });

    }

//    private List<PhoneScore> phoneScores;

    /** 处理语音评测结果 */
    private void handleResult(ResultEntity resultEntity) {
        //连贯性
        int contScore = resultEntity.getContScore();
        //准确性
        int pronScore = resultEntity.getPronScore();
        //总得分
        int score = resultEntity.getScore();
        if (score == 100) {
            performPerfact();
        } else if (contScore >= 60 && pronScore >= 60) {
//            performGood();
//            performPerfact();
        } else if (contScore >= 60 && pronScore < 60) {
            if (judgeSpeechStatus(resultEntity) == STATUS_1) {
//                performRepeatSentence();
//                performPerfact();
            } else {
//                performRepeatWord(resultEntity.getLstPhonemeScore());
//                performPerfact();
            }
        } else {
//            performRepeatSentence();
//            performPerfact();
        }
    }

    /** 重读整个句子 */
    private void performRepeatSentence() {
        if (mViewModel.getIeResultData() == null) return;
        final Map<String, String> audioHashMap = mViewModel.getIeResultData().getValue().getAudioHashMap();
        if (audioHashMap == null)
            return;
        final String sentenceKey = mViewModel.getIeResultData().getValue().getSentence();

        Observable.
                just(sentenceKey).
                filter(RxFilter.<String>filterNull()).
                map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return audioHashMap.get(sentenceKey);
                    }
                }).
                filter(RxFilter.<String>filterNull()).
                subscribeOn(Schedulers.io()).
                subscribe(getRepeatWordConsumer());
    }

    /**
     * 重读某个单词，分数最低的某个单词
     * 处理评测结果时，一定不能在同一个线程里面播放音频
     */
    private void performRepeatWord(final List<PhoneScore> phoneScores) {
        sortResult(phoneScores);
//        PhoneScore repeatUrl = getFilterWord(phoneScores);
//        String urlAudio = null;
//        if (repeatUrl == null || repeatUrl.getWord() == null)
//            return urlAudio;
        if (mViewModel.getIeResultData() == null) return;
        Map<String, String> audioHashMap = mViewModel.getIeResultData().getValue().getAudioHashMap();
        if (audioHashMap == null)
            return;
//        Observable.
//                just(audioHashMap).
//                filter(RxFilter.<Map<String, String>>filterNull()).
//                flatMap(new Function<Map<String, String>, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(Map<String, String> stringStringMap) throws Exception {
//                        return Observable.fromArray(stringStringMap);
//                    }
//                })
//                flatMapIterable(new Function<Map<String, String>, Iterable<?>>() {
//                    @Override
//                    public Iterable<?> apply(Map<String, String> stringStringMap) throws Exception {
//                        return stringStringMap.entrySet();
//                    }
//                })
        Observable.
                fromIterable(audioHashMap.entrySet()).
                filter(getRepeatWordPredicate(getFilterWord(phoneScores))).
                subscribeOn(Schedulers.io()).
                map(new Function<Map.Entry<String, String>, String>() {
                    @Override
                    public String apply(Map.Entry<String, String> stringStringEntry) throws Exception {
                        return stringStringEntry.getValue();
                    }
                }).
                filter(RxFilter.<String>filterNull()).
                subscribe(getRepeatWordConsumer());
    }

    /** 得到需要重复的word的filter */
    private Predicate<Map.Entry<String, String>> getRepeatWordPredicate(final PhoneScore phoneScore) {
        return new Predicate<Map.Entry<String, String>>() {
            @Override
            public boolean test(Map.Entry<String, String> stringStringEntry) throws Exception {
                return stringStringEntry.getKey() != null && phoneScore != null
                        && stringStringEntry.getKey().equals(phoneScore.getWord());
            }
        };
    }

    /** 处理重复单词的consumer */
    private Consumer<String> getRepeatWordConsumer() {
        return new Consumer<String>() {
            @Override
            public void accept(String stringStringEntry) throws Exception {
                playAudio(stringStringEntry);
            }
        };
    }

    /** 播放重复的句子 */
    private void playAudio(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取过滤的单词
     * @param list
     * @return
     */
    private PhoneScore getFilterWord(List<PhoneScore> list) {
        PhoneScore repeatWord = null;
        for (PhoneScore phoneScore : list) {
            if (!filterWordList.contains(phoneScore.getWord())) {
                repeatWord = phoneScore;
            }
        }
        return repeatWord;
    }

    private List<String> filterWordList = new ArrayList<>();

    /**
     * 获取排好序的List
     *
     * @param list
     */
    private void sortResult(List<PhoneScore> list) {
        Collections.sort(list, new Comparator<PhoneScore>() {
            @Override
            public int compare(PhoneScore o1, PhoneScore o2) {
                if (o1.getScore() < o2.getScore()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    /** 得到满分点赞 */
    private void performPerfact() {
        logger.i("performPerfact");
        String url = Environment.getExternalStorageDirectory() +
                File.separator + "parentsmeeting" + File.separator + "livevideo" +
                File.separator + "01_01_Well_done.mp3";
        Observable.
                just(url).
                map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {
                        return new File(s);
                    }
                }).
                filter(new Predicate<File>() {
                    @Override
                    public boolean test(File file) throws Exception {
                        return file != null && file.exists();
                    }
                }).
                delay(1, TimeUnit.SECONDS).
                subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                        }
                        logger.i(file.getPath());
                        mediaPlayer.setDataSource(file.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logger.e(throwable.getStackTrace());
                        throwable.printStackTrace();
                    }
                });

//        Observable.
//                <Boolean>just(true).
//                subscribeOn(AndroidSchedulers.mainThread()).
//                doOnNext(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        logger.i("playAction");
////                        UnityCommandPlay.playActionNow("A_YW_DOG_ZKZB2");
//                    }
//                }).
//                observeOn(Schedulers.io()).
//                subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        if (mediaPlayer == null) {
//                            mediaPlayer = new MediaPlayer();
//                        }
////                        try {
////            AssetFileDescriptor fd = mActivity.getAssets().openFd("01_01_Well_done.mp3");
//                        String url = Environment.getExternalStorageDirectory() +
//                                File.separator + "parentsmeeting" + File.separator + "livevideo" +
//                                File.separator + "01_01_Well_done.mp3";
//                        File file = new File(url);
//                        if (!file.exists()) {
//                            logger.e(url + "不存在");
//                            return;
//                        } else {
//                            logger.i(url + " @@@@@");
//                        }
////                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////            String url = "sdcard/parentsmeeting/livevideo/01_01_Well_done.mp3";
//                        mediaPlayer.setDataSource(url);
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                            logger.e(e);
////                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                    }
//                });
    }

    /** 表现良好，双分都大于60 */
    private void performGood() {
        String audio60Url = "";
        playAudio(audio60Url);
    }

    /** 判断当前的测评状态 */
    private int judgeSpeechStatus(ResultEntity resultEntity) {
        //低于60分的单词个数
        int lowNum = 0;
        List<PhoneScore> phoneScores = resultEntity.getLstPhonemeScore();
        if (phoneScores == null) {
            return STATUS_0;
        }
        for (PhoneScore phoneScore : phoneScores) {
            if (phoneScore.getScore() < 60) {
                lowNum++;
            }
        }
        if (lowNum > phoneScores.size() / 2) {
            return STATUS_1;
        } else {
            return STATUS_2;
        }
    }

    //情况0，状态出现异常
    private final int STATUS_0 = 0;
    //情况1,50%及以上的单词分数低于60分
    private final int STATUS_1 = 1;
    //情况2，50%及以下的单词分数低于60分
    private final int STATUS_2 = 2;

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
            return null;
        }
        for (PhoneScore phoneScore : resultEntity.getLstPhonemeScore()) {
            stringBuilder.append(phoneScore.getWord() + " " + phoneScore.getScore() + " ");
        }
        stringBuilder.append("@@@");
        if (resultEntity.getScores() == null) {
            return null;
        }
        for (Integer integer : resultEntity.getScores()) {
            stringBuilder.append(integer + " ");
        }
        stringBuilder.append("@@@");
        return stringBuilder.toString() + " status:" + resultEntity.getStatus()
                + " curStatus " + resultEntity.getCurStatus()
                + " contScore " + resultEntity.getContScore()
                + " partScore " + resultEntity.getPartScore()
                + " Score " + resultEntity.getScore()
                + " curString " + resultEntity.getCurString()
                + " PronScore " + resultEntity.getPronScore();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
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
            mSpeechUtils = SpeechUtils.getInstance(mActivity);
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
                isSpeechReady = true;
                mViewModel.getIsSpeechReady().setValue(true);
            }

            @Override
            public void onFileFail() {
                logger.i("Speech Fail");
                isSpeechReady = false;
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
}

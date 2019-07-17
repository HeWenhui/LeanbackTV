package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view;

import android.support.v4.app.FragmentActivity;

import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.rxutils.CommonRxObserver;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.rxutils.RxFilter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.SpeechScoreEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.IntelligentRecognitionHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.EvaluationAudioPlayerDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.SoundEffectPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.Unity3DPlayManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view.BaseIntelligentRecognitionPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class IntelligentRecognitionPresenter extends BaseIntelligentRecognitionPresenter {

    public IntelligentRecognitionPresenter(FragmentActivity context) {
        super(context);
    }

    private enum SpeechType {
        WORD, SENTENCE
    }

    /** 播放第几个语音 */
    private AtomicInteger audioPlayNum = new AtomicInteger(0);
    /** 当前是第几次测评 */

    //现在是测评哪种类型(句子或者单词)
    private SpeechType speechType;
    //测评的单词
    private String speechWord;
    SoundEffectPlayer soundPlayer;
    //情况0，状态出现异常
    private final int STATUS_0 = 0;
    //情况1,50%及以上的单词分数低于60分
//    private final int STATUS_1 = 1;
    //情况2，50%及以下的单词分数低于60分
//    private final int STATUS_2 = 2;
    //过滤单词表
    List<String> filterWordList = new ArrayList<>();

    private int lastScore = 0;

    /** 获取语音测频的得分 */
    private void getSpeechScore(ResultEntity resultEntity) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject itemJSON = new JSONObject();
            itemJSON.put("entranceTime", getSpeechTime());
            itemJSON.put("score", resultEntity.getScore());
            {
                JSONObject detailItem = new JSONObject();

                detailItem.put("cont_score", resultEntity.getContScore());
                detailItem.put("level", resultEntity.getLevel());
                {
                    JSONObject nbestJSONObj = new JSONObject();
                    for (PhoneScore phoneScore : resultEntity.getLstPhonemeScore()) {
                        nbestJSONObj.put(phoneScore.getWord(), phoneScore.getScore());
                    }
                    detailItem.put("nbest", nbestJSONObj);
                }
                detailItem.put("pron_score", resultEntity.getPronScore());
                detailItem.put("total_score", "");
                itemJSON.put("detail", detailItem);
            }
            jsonObject.put("1", itemJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        IntelligentRecognitionRecord record = mViewModel.getRecordData();
        getHttpManager().getIntelligentSpeechSumbmitResult(
                mActivity.getApplicationContext(),
                record.getStuId(),
                record.getMaterialId(),
                "0",
                jsonObject.toString(),
                record.getLiveId(),
                record.getStuCouId(),
                "",
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        IntelligentRecognitionHttpResponseParser parser = new IntelligentRecognitionHttpResponseParser();
                        mViewModel.getSpeechScoreData().
                                setValue(parser.parseSpeechScore(responseEntity));
                    }
                }
        );

    }

    private void testScoreEntity() {
        if (AppConfig.DEBUG) {
            SpeechScoreEntity speechScoreEntity = new SpeechScoreEntity();
            speechScoreEntity.setStar("12");
            speechScoreEntity.setGold("11");
            speechScoreEntity.setScore("123");
            mViewModel.getSpeechScoreData().setValue(speechScoreEntity);
        }
    }

    private void performResultUnity3DAudio(ResultEntity resultEntity) {
        //连贯性
        int contScore = resultEntity.getContScore();
        //准确性
        int pronScore = resultEntity.getPronScore();
        //总得分
        int score = resultEntity.getScore();
        if (getSpeechNum() == 1) {
            if (score == 100) {
                performPerfact();
            } else if (contScore >= 60 && pronScore >= 60) {
                performGood();
            } else if (contScore >= 60 && pronScore < 60) {
                if (judgeSpeechStatus(resultEntity) == IntelligentConstants.FEED_BACK_SENTENCE_1_0) {
                    lastScore = score;
                    speechType = SpeechType.SENTENCE;
                    performRepeatSentence(IntelligentConstants.FEED_BACK_SENTENCE_1_0);
                } else {
                    speechType = SpeechType.WORD;
                    performRepeateCommon(IntelligentConstants.FEED_BACK_WORD_1, resultEntity.getLstPhonemeScore());
                }
            } else {
                speechType = SpeechType.SENTENCE;
                performRepeatSentence(IntelligentConstants.FEED_BACK_SENTENCE_1_1);
            }
        } else if (getSpeechNum() == 2) {
            if (speechType == SpeechType.SENTENCE) {
                if (score <= lastScore) {
                    performCommon(IntelligentConstants.FEED_BACK_SENTENCE_2_1);
                } else {
                    performCommon(IntelligentConstants.FEED_BACK_SENTENCE_2_0);
                }
            } else {
                if (score >= 60) {
                    performCommon(IntelligentConstants.FEED_BACK_WORD_2_0);
                } else {
                    performCommonList(IntelligentConstants.FEED_BACK_WORD_2_1,
                            Arrays.asList(
                                    speechWord,
                                    EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(IntelligentConstants.FEED_BACK_WORD_2_1)
                            )
                    );
                }
            }
        } else {
            if (score > 60) {
                performCommon(IntelligentConstants.FEED_BACK_WORD_3_0);
            } else {
                performCommonList(IntelligentConstants.FEED_BACK_WORD_3_1,
                        Arrays.asList(
                                speechWord,
                                EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(IntelligentConstants.FEED_BACK_WORD_2_1)
                        )
                );
            }
        }
    }

    /** 处理语音评测结果 */
    @Override
    protected void handleResult(ResultEntity resultEntity) {
        if (resultEntity == null) {
            return;
        }
        getSpeechScore(resultEntity);
        //连贯性
//        int contScore = resultEntity.getContScore();
        //准确性
//        int pronScore = resultEntity.getPronScore();
        //总得分
//        int score = resultEntity.getScore();
        //通知view做出更改
        mViewModel.getIsIntelligentSpeechFinish().postValue(true);
//        testScoreEntity();
//        mViewModel.getSpeechScore().postValue(resultEntity.getScore());
        performResultUnity3DAudio(resultEntity);

    }

    private void performCommonList(int status, List<String> list) {
        Unity3DPlayManager.playUnity3D(status);
        playRepeateAudio(status, list);
    }

    /** 得到满分点赞 */
    private void performPerfact() {
        logger.i("performPerfact");
        performCommon(IntelligentConstants.PERFECT);
//        Observable.
//                just(EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(IntelligentConstants.PERFECT)).
//                subscribeOn(Schedulers.io()).
//                subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        Unity3DPlayManager.playUnity3D(IntelligentConstants.PERFECT);
//                        if (soundPlayer != null) {
//                            soundPlayer.cancle();
//                        }
//                        soundPlayer = new SoundEffectPlayer(s);
//                        soundPlayer.start();
//                    }
//                });

    }

    /** 表现良好，双分都大于60 */
    private void performGood() {
        logger.i("performGood");
        performCommon(IntelligentConstants.GOOD);
    }

    /**
     * 播放音频和uni3D的公共方法
     *
     * @param status
     */
    private void performCommon(final int status) {
        Unity3DPlayManager.playUnity3D(status);
        if (soundPlayer != null) {
            soundPlayer.cancle();
        }
        soundPlayer = new SoundEffectPlayer(
                EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(status));
        soundPlayer.setPlayListener(new SoundEffectPlayer.SoundPlayListener() {
            @Override
            public void onSoundFinish() {
                mViewModel.getIsSpeechJudgeFinish().postValue(status);
            }
        });
        soundPlayer.start();
    }

    private void performRepeateCommon(final int status, List<PhoneScore> phoneScoreList) {
        Unity3DPlayManager.playUnity3D(status);
//        if (mViewModel.getIeResultData() == null) return;
//        final Map<String, String> audioHashMap = mViewModel.getIeResultData().getValue().getAudioHashMap();
//        if (audioHashMap == null)
//            return;
        Observable.
                fromIterable(phoneScoreList).
                filter(new Predicate<PhoneScore>() {
                    @Override
                    public boolean test(PhoneScore phoneScore) throws Exception {
                        return phoneScore != null &&
                                mViewModel.getIeResultData() != null &&
                                mViewModel.getIeResultData().getValue().getAudioHashMap() != null &&
                                mViewModel.getIeResultData().getValue().getAudioHashMap().containsKey(phoneScore.getWord());
                    }
                }).
                map(new Function<PhoneScore, String>() {
                    @Override
                    public String apply(PhoneScore phoneScore) throws Exception {
                        speechWord = mViewModel.getIeResultData().getValue().getAudioHashMap().get(phoneScore.getWord());
                        return speechWord;
                    }
                }).
                subscribe(new CommonRxObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        playRepeateAudio(status, Arrays.asList(
                                s,
                                EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(status)));
                        disposable.dispose();
                    }
                });
    }

    private void playRepeateAudio(final int status, List<String> list) {
        if (soundPlayer != null) {
            soundPlayer.cancle();
        }
        soundPlayer = new SoundEffectPlayer(list);
        audioPlayNum.set(1);
        soundPlayer.setPlayListener(new SoundEffectPlayer.SoundPlayListener() {
            @Override
            public void onSoundFinish() {
                if (audioPlayNum.incrementAndGet() == 2) {
                    mViewModel.getIsSpeechJudgeFinish().postValue(1);
                    performStartRecord();
                }
                mViewModel.getIsSpeechJudgeFinish().postValue(status);
            }
        });
        soundPlayer.start();
    }

    /** 判断当前的测评属于哪种结果 */
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
            return IntelligentConstants.FEED_BACK_SENTENCE_1_0;
        } else {
            return IntelligentConstants.FEED_BACK_WORD_1;
        }
    }

    /** 重读整个句子 */
    private void performRepeatSentence(final int status) {

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
                subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Unity3DPlayManager.playUnity3D(status);
                        audioPlayNum.set(1);
                        SoundEffectPlayer soundEffectPlayer = new SoundEffectPlayer(
                                Arrays.asList(
                                        EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(status),
                                        s
                                ));
                        soundEffectPlayer.setPlayListener(new SoundEffectPlayer.SoundPlayListener() {
                            @Override
                            public void onSoundFinish() {
                                if (audioPlayNum.incrementAndGet() == 2) {
                                    mViewModel.getIsSpeechJudgeFinish().postValue(1);
                                    performStartRecord();
                                }
                            }
                        });
                        soundEffectPlayer.start();
                    }
                });


    }

    /**
     * 重读某个单词，分数最低的某个单词
     * 处理评测结果时，一定不能在同一个线程里面播放音频
     */
    protected void performRepeatWord(final List<PhoneScore> phoneScores, final int status) {
        sortResult(phoneScores);
        if (mViewModel.getIeResultData() == null) return;
        Map<String, String> audioHashMap = mViewModel.getIeResultData().getValue().getAudioHashMap();
        if (audioHashMap == null)
            return;
        Observable.zip(
                Observable.just(EvaluationAudioPlayerDataManager.getInstance().
                        getJudgeAudioUrl(status)).
                        filter(RxFilter.<String>filterNull()),
                Observable.
                        fromIterable(audioHashMap.entrySet()).
                        filter(getRepeatWordPredicate(getFilterWord(phoneScores))).
                        map(new Function<Map.Entry<String, String>, String>() {
                            @Override
                            public String apply(Map.Entry<String, String> stringStringEntry) throws Exception {
                                return stringStringEntry.getValue();
                            }
                        }).
                        filter(RxFilter.<String>filterNull()),
                new BiFunction<String, String, List<String>>() {
                    @Override
                    public List<String> apply(String s, String s2) throws Exception {
                        return Arrays.asList(s, s2);
                    }
                }).
                filter(new Predicate<List<String>>() {
                    @Override
                    public boolean test(List<String> strings) throws Exception {
                        for (String string : strings) {
                            logger.i("play audio url" + string);
                        }
                        return strings != null && strings.size() != 0;
                    }
                }).
                observeOn(Schedulers.io()).
                subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        Unity3DPlayManager.playUnity3D(status);
                        if (soundPlayer != null) {
                            soundPlayer.cancle();
                        }
                        soundPlayer = new SoundEffectPlayer(strings);
                        audioPlayNum.set(1);
                        soundPlayer.setPlayListener(new SoundEffectPlayer.SoundPlayListener() {
                            @Override
                            public void onSoundFinish() {
                                if (audioPlayNum.incrementAndGet() == 2) {
                                    mViewModel.getIsSpeechJudgeFinish().postValue(1);
                                    performStartRecord();
                                }
                            }
                        });
                        soundPlayer.start();
                    }
                });


//        Observable.
//                fromIterable(audioHashMap.entrySet()).
//                filter(getRepeatWordPredicate(getFilterWord(phoneScores))).
//                subscribeOn(Schedulers.io()).
//                map(new Function<Map.Entry<String, String>, String>() {
//                    @Override
//                    public String apply(Map.Entry<String, String> stringStringEntry) throws Exception {
//                        return stringStringEntry.getValue();
//                    }
//                }).
//                filter(RxFilter.<String>filterNull()).
//                subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
////                        List<String> list =
////                                Arrays.asList(
////                                        EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(REPEAT_WORD),
////                                        s);
//                        if (soundPlayer != null) {
//                            soundPlayer.cancle();
//
//                        }
////                        else {
////                        soundPlayer = new SoundEffectPlayer(list);
////                        }
////                        if (soundPlayer != null) {
//                        soundPlayer = new SoundEffectPlayer(
//                                Arrays.asList(
//                                        EvaluationAudioPlayerDataManager.getInstance().getJudgeAudioUrl(REPEAT_WORD),
//                                        s));
////                        }
////                        soundPlayer.setPlayListener(new SoundEffectPlayer.SoundPlayListener() {
////                            @Override
////                            public void onSoundFinish() {
////
////                            }
////                        });
//                        soundPlayer.start();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        logger.e(throwable.getStackTrace());
//                    }
//                });
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
                break;
            }
        }
        return repeatWord;
    }

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
}

package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.support.v4.app.FragmentActivity;

import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants;

public class IntelligentRecognitionPresenter extends BaseIntelligentRecognitionPresenter {
    public IntelligentRecognitionPresenter(FragmentActivity context) {
        super(context);
    }
    /**
     * 当前是第几次测评
     */
    private int speechNum = 0;
    //现在是测评哪种类型(句子或者单词)
    private int speechType = 0;
    /** 处理语音评测结果 */
    @Override
    protected void handleResult(ResultEntity resultEntity) {
        //连贯性
        int contScore = resultEntity.getContScore();
        //准确性
        int pronScore = resultEntity.getPronScore();
        //总得分
        int score = resultEntity.getScore();
        //通知view做出更改
        mViewModel.getIntelligentSpeechResult().postValue(1);
        if (speechNum == 1) {
            if (score == 100) {
                performPerfact();
            } else if (contScore >= 60 && pronScore >= 60) {
                performGood();
            } else if (contScore >= 60 && pronScore < 60) {
                if (judgeSpeechStatus(resultEntity) == STATUS_1) {
                    speechType = 0;
                    performRepeatSentence(IntelligentConstants.FEED_BACK_SENTENCE_1_0);
                } else {
                    speechType = 1;
                    performRepeatWord(resultEntity.getLstPhonemeScore(),
                            IntelligentConstants.FEED_BACK_WORD_1);
                }
            } else {
                speechType = 1;
                performRepeatSentence(IntelligentConstants.FEED_BACK_SENTENCE_1_1);
            }
        } else if (speechNum == 2) {

        } else {

        }
    }

}

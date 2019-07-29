package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.tal.speech.speechrecognizer.PhoneScore;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.SpeechScoreEntity;

import java.util.List;

public class IntelligentRecognitionViewModel extends ViewModel {
    /** 跨进程传递进来的用户数据和试题数据 */
    private IntelligentRecognitionRecord recordData;
    /** 测评返回的数据 */
    private MutableLiveData<IEResult> ieResultData = new MutableLiveData<>();
    /** 语音测评是否准备完成，可以开始测评 */
    private MutableLiveData<Boolean> isSpeechReady = new MutableLiveData<>();
    /** 音量 */
    private MutableLiveData<Integer> volume = new MutableLiveData<>();
    /** 是否收到收题指令 */
    private MutableLiveData<Boolean> isFinish = new MutableLiveData<>();
    /** 语音测评完成，显示结果 */
    private MutableLiveData<Boolean> isIntelligentSpeechFinish = new MutableLiveData<>();
    /** 对语音测评结果的评价完成(unity3D说完话) */
    private MutableLiveData<Integer> isSpeechJudgeFinish = new MutableLiveData<>();
    /** 是否取消测评 */
    private MutableLiveData<Boolean> isCancelSpeech = new MutableLiveData<>();
    /** 语音测评分数 */
    private MutableLiveData<SpeechScoreEntity> speechScoreData = new MutableLiveData<>();
    /** 获取Top3的Data成功 */
    private MutableLiveData<GoldTeamStatus> isTop3DataSuccess = new MutableLiveData<>();
    /** top3是否显示 */
    private MutableLiveData<Boolean> isTop3Show = new MutableLiveData<>();
    private MutableLiveData<List<PhoneScore>> resultPhoneScores = new MutableLiveData<>();
    /** 语音测评的语句是否准备完成 */
    private MutableLiveData<Boolean> isEvaluationReady = new MutableLiveData<>();

    private MutableLiveData<Boolean> isScorePopWindowFinish = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsScorePopWindowFinish() {
        return isScorePopWindowFinish;
    }

    public MutableLiveData<Boolean> getIsEvaluationReady() {
        return isEvaluationReady;
    }

    public MutableLiveData<List<PhoneScore>> getResultPhoneScores() {
        return resultPhoneScores;
    }

    public MutableLiveData<Boolean> getIsTop3Show() {
        return isTop3Show;
    }

    public MutableLiveData<GoldTeamStatus> getIsTop3DataSuccess() {
        return isTop3DataSuccess;
    }

    //    private MutableLiveData<Integer> speechScore = new MutableLiveData<>();

//    public MutableLiveData<Integer> getSpeechScore() {
//        return speechScore;
//    }

    public MutableLiveData<SpeechScoreEntity> getSpeechScoreData() {
        return speechScoreData;
    }

    public MutableLiveData<Boolean> getIsCancelSpeech() {
        return isCancelSpeech;
    }

    public IntelligentRecognitionRecord getRecordData() {
        return recordData;
    }

    public MutableLiveData<Integer> getIsSpeechJudgeFinish() {
        return isSpeechJudgeFinish;
    }

    public MutableLiveData<Boolean> getIsIntelligentSpeechFinish() {
        return isIntelligentSpeechFinish;
    }
//    public MutableLiveData<Boolean> getIsPerface() {
//        return isPerfact;
//    }

//    public void setIsPerface(MutableLiveData<Boolean> isPerface) {
//        this.isPerfact = isPerface;
//    }

    public void setRecordData(IntelligentRecognitionRecord recordData) {
        this.recordData = recordData;
    }

    public MutableLiveData<IEResult> getIeResultData() {
        return ieResultData;
    }

//    public void setIeResultData(MutableLiveData<IEResult> ieResultData) {
//        this.ieResultData = ieResultData;
//    }

    public MutableLiveData<Boolean> getIsSpeechReady() {
        return isSpeechReady;
    }

//    public void setIsSpeechReady(MutableLiveData<Boolean> isSpeechReady) {
//        this.isSpeechReady = isSpeechReady;
//    }

    public MutableLiveData<Integer> getVolume() {
        return volume;
    }

//    public void setVolume(MutableLiveData<Integer> volume) {
//        this.volume = volume;
//    }

    public MutableLiveData<Boolean> getIsFinish() {
        return isFinish;
    }

//    public void setIsFinish(MutableLiveData<Boolean> isFinish) {
//        this.isFinish = isFinish;
//    }
}

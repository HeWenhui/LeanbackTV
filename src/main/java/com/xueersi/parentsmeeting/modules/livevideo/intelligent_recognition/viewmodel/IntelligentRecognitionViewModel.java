package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;

public class IntelligentRecognitionViewModel extends ViewModel {
    /** 跨进程传递进来的用户数据和试题数据 */
    private IntelligentRecognitionRecord recordData;
    /** 测评返回的数据 */
    private MutableLiveData<IEResult> ieResultData = new MutableLiveData<>();
    /** 语音测评是否初始化完成 */
    private MutableLiveData<Boolean> isSpeechReady = new MutableLiveData<>();
    /** 音量 */
    private MutableLiveData<Integer> volume = new MutableLiveData<>();
    /** 当前Activity是否结束 */
    private MutableLiveData<Boolean> isFinish = new MutableLiveData<>();

    public IntelligentRecognitionRecord getRecordData() {
        return recordData;
    }

    public void setRecordData(IntelligentRecognitionRecord recordData) {
        this.recordData = recordData;
    }

    public MutableLiveData<IEResult> getIeResultData() {
        return ieResultData;
    }

    public void setIeResultData(MutableLiveData<IEResult> ieResultData) {
        this.ieResultData = ieResultData;
    }

    public MutableLiveData<Boolean> getIsSpeechReady() {
        return isSpeechReady;
    }

    public void setIsSpeechReady(MutableLiveData<Boolean> isSpeechReady) {
        this.isSpeechReady = isSpeechReady;
    }

    public MutableLiveData<Integer> getVolume() {
        return volume;
    }

    public void setVolume(MutableLiveData<Integer> volume) {
        this.volume = volume;
    }

    public MutableLiveData<Boolean> getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(MutableLiveData<Boolean> isFinish) {
        this.isFinish = isFinish;
    }
}

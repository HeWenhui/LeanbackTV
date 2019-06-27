package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionRecord;

public class IntelligentRecognitionViewModel extends ViewModel {
    private MutableLiveData<IntelligentRecognitionRecord> recordData = new MutableLiveData<>();
    private MutableLiveData<IEResult> ieResultData = new MutableLiveData<>();

    public MutableLiveData<IntelligentRecognitionRecord> getRecordData() {
        return recordData;
    }

    public void setRecordData(MutableLiveData<IntelligentRecognitionRecord> recordData) {
        this.recordData = recordData;
    }

    public MutableLiveData<IEResult> getIeResultData() {
        return ieResultData;
    }

    public void setIeResultData(MutableLiveData<IEResult> ieResultData) {
        this.ieResultData = ieResultData;
    }
}

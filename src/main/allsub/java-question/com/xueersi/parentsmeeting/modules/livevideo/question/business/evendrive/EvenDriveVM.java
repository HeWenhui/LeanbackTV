package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class EvenDriveVM extends ViewModel {
    public MutableLiveData<Integer> isOpenStimulation = new MutableLiveData<>();
    public MutableLiveData<Integer> evenDriveNum = new MutableLiveData<>();
}

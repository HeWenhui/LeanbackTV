package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.top3;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentEvaluationTop3Entity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.BaseIntelligentRecognitionBll;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;

public class IntelligentRecognitionTop3Bll extends BaseIntelligentRecognitionBll<IntelligentRecognitionViewModel> {
    public IntelligentRecognitionTop3Bll(FragmentActivity context) {
        super(context, IntelligentRecognitionViewModel.class);
        addObserver();
    }

    private void addObserver() {
//        IntelligentRecognitionViewModel mViewModel = ViewModelProviders.of()

        mViewModel.getIsFinish().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
//                    IntelligentRecognitionTop3Bll top3Bll = new IntelligentRecognitionTop3Bll(mActivity);
//                    top3Bll.getTop3Data();
                    getTop3Data();
                }
            }
        });
    }


    private void getTop3Data() {

        getHttpManager().getIntelligentTop3Data(
                mViewModel.getRecordData().getLiveId(),
                mViewModel.getRecordData().getMaterialId(),
                mViewModel.getRecordData().getClassId(),
                mViewModel.getRecordData().getTeamId(),
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        IntelligentEvaluationTop3Entity top3Entity =
                                getHttpResponseParser().
                                        parseSpeechTeamRank(responseEntity,
                                                mViewModel.getRecordData().getStuId());

                        mViewModel.getIsTop3DataSuccess().postValue(top3Entity);
                    }
                });
    }
}

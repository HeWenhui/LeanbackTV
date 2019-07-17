package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.top3;

import android.support.v4.app.FragmentActivity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.BaseIntelligentRecognitionBll;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;

public class IntelligentRecognitionTop3Bll extends BaseIntelligentRecognitionBll {
    public IntelligentRecognitionTop3Bll(FragmentActivity context) {
        super(context);
    }

    IntelligentRecognitionViewModel viewModel;

    public void getTop3Data() {

        getHttpManager().getIntelligentTop3Data(
                viewModel.getRecordData().getLiveId(),
                viewModel.getRecordData().getMaterialId(),
                viewModel.getRecordData().getClassId(),
                viewModel.getRecordData().getTeamId(),
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        GoldTeamStatus goldTeamStatus =
                                getHttpResponseParser().
                                        parseSpeechTeamRank(responseEntity,
                                                viewModel.getRecordData().getStuId());

                        viewModel.getIsTop3DataSuccess().postValue(goldTeamStatus);
                    }
                });
    }
}

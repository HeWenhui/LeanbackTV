package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.top3;

import android.support.v4.app.FragmentActivity;

import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.StandSpeechTop3Pager;

public class IntelligentRecognitionTop3View extends StandSpeechTop3Pager {
    private FragmentActivity mActivity;

    public IntelligentRecognitionTop3View(FragmentActivity context, GoldTeamStatus entity) {
        super(context, entity);
        mActivity = context;
    }

    @Override
    public void initData() {
//        addObserver();
        super.initData();
    }

//    private void addObserver() {
//        IntelligentRecognitionViewModel viewModel =
//                ViewModelProviders.
//                        of(mActivity).
//                        get(IntelligentRecognitionViewModel.class);
//
//        viewModel.getIsTop3DataSuccess().observe(mActivity, new Observer<GoldTeamStatus>() {
//            @Override
//            public void onChanged(@Nullable GoldTeamStatus goldTeamStatus) {
//
//            }
//        });
//    }
}

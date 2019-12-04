package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination;

import android.view.View;

public interface IStandExperienceEvaluationContract {
    interface IEvaluationPresenter {
    }

    interface IEvaluationView {
        void showWebView(String url);

        View getRootView();
    }
}

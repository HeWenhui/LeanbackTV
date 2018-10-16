package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.examination;

import android.view.View;

public interface IEvaluationContract {
    interface IEvaluationPresenter {

    }

    interface IEvaluationView {
        void showWebView(String url);

        View getRootView();
    }
}

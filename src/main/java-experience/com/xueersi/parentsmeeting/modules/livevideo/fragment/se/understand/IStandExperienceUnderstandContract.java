package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.understand;

public interface IStandExperienceUnderstandContract {
    interface IUnderStandPresenter extends IUnderStandListener {
        void removeView();
    }

    interface IUnderStandListener {
        void onClick(int sign);
    }
}

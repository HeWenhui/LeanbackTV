package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.understand;

public interface IStandExperienceUnderstandContract {
    interface IUnderStandPresenter extends IUnderStandListener {
        void removeView();
    }

    interface IUnderStandListener {
        void onClick(int sign);
    }
}

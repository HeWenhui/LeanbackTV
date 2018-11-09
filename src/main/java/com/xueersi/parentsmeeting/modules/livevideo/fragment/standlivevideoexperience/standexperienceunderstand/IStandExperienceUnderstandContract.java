package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperienceunderstand;

public interface IStandExperienceUnderstandContract {
    interface IUnderStandPresenter extends IUnderStandListener {
        void removeView();
    }

    interface IUnderStandListener {
        void onClick(int sign);
    }
}

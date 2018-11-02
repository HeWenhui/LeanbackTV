package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperienceunderstand;

public interface IUnderStandContract {
    interface IUnderStandPresenter extends IUnderStandListener {
        void removeView();
    }

    interface IUnderStandListener {
        void onClick(int sign);
    }
}

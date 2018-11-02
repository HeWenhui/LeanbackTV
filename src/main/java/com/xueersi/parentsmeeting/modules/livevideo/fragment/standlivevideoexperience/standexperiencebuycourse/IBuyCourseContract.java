package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperiencebuycourse;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;

public interface IBuyCourseContract {

    interface View {
        void updateView(ExperienceResult mData);

        //        void setPresenter(T presenter);

    }

    interface Presenter{
        //        void setView(T view);
//        void removeStudyFeedBackView();
        void removeBuyCourseView();

        void showNextWindow();
    }
}

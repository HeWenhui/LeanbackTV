package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.buycourse;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity.ExperienceResult;

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

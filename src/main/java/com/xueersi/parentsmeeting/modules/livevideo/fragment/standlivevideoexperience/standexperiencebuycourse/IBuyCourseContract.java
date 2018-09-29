package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperiencebuycourse;

public interface IBuyCourseContract {

    interface View<T extends Presenter> {
//        void setPresenter(T presenter);
    }

    interface Presenter<T extends View> {
        //        void setView(T view);
//        void removeStudyFeedBackView();
        void removeBuyCourseView();

        void showNextWindow();
    }
}

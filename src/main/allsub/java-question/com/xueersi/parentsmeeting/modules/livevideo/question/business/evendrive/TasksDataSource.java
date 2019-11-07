package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

public interface TasksDataSource {
    interface LoadAnimCallBack {
        void onDataNotAvailable(String msg);

        void onDatasLoaded(String num, boolean numChange);
    }

    void getDataSource(EvenDriveAnimRepository.EvenDriveQuestionType question_type,
                       String testId, LoadAnimCallBack loadAnimCallBack);


}

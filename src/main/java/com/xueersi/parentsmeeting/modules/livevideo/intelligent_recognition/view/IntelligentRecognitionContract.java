package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionViewModel;

public interface IntelligentRecognitionContract {
    String FILTER_ACTION = "com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition";

    interface IIntelligentRecognitionPresenter {
        /** 注册监听消息 */
        void registerMessage();

        /** 取消注册监听消息 */
        void unregisterMessage();

        void setViewModel(IntelligentRecognitionViewModel model);

        void startSpeech();
    }

    interface IIntelligentRecognitionView {
        void receiveStopEvent(String goldJSON);

        void setPresenter(IIntelligentRecognitionPresenter mPresenter);
    }
}

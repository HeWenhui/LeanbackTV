package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;

public interface IntelligentRecognitionContract {
    String FILTER_ACTION = "com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition";

    interface BaseView<T> {
        void setPresenter(T presenter);
    }

    interface BasePresenter<V> {
        void setView(V view);
    }

    interface IIntelligentRecognitionPresenter<V> extends BasePresenter<V> {
        /** 注册监听消息 */
        void registerMessage();

        /** 取消注册监听消息 */
        void unregisterMessage();

        void setViewModel(IntelligentRecognitionViewModel model);

        void startSpeech();
    }

    interface IIntelligentRecognitionView<T> extends BaseView<T> {
        void receiveStopEvent(String goldJSON);

    }
}

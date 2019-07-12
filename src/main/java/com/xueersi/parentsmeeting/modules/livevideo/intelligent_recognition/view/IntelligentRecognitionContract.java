package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.SpeechStopEntity;

public interface IntelligentRecognitionContract {
    String FILTER_ACTION = "com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition_sign";
    String intelligent_recognition_sign = "intelligent_recognition_sign";

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

//        void setViewModel(IntelligentRecognitionViewModel model);

        void startSpeech();

//        void setEntity(E entity);
    }

    interface IIntelligentRecognitionView<T> extends BaseView<T> {
        void receiveStopEvent(SpeechStopEntity stopEntity);

    }
}

package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

public interface IntelligentRecognitionContract {
    String INTELLIGENT_RECOGNITION_FILTER_ACTION = "com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition_sign";
    String INTELLIGENT_RECOGNITION_SIGN_KEY = "intelligent_recognition_sign";
    String INTELLIGENT_RECOGNITION_STOP_ONCE = "intelligent_recognition_stop_once";

    interface BaseView<T> {
        void setPresenter(T presenter);
    }

    interface BasePresenter<V> {
        void setView(V view);
    }

    interface IIntelligentRecognitionPresenter<V> extends BasePresenter<V> {
        /** 注册监听消息 */
//        void registerMessage();

        /** 取消注册监听消息 */
//        void unregisterMessage();

//        void setViewModel(IntelligentRecognitionViewModel model);

//        void startSpeech();

//        void setEntity(E entity);
    }

    interface IIntelligentRecognitionView<T> extends BaseView<T> {
//        void receiveStopEvent(SpeechStopEntity stopEntity);

    }
}

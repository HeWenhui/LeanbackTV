package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.view.View;

public interface ISuperSpeakerContract {
    /** 视频录制有效时间 */
    int RECORD_VALID_TIME = 1000;
    /** 视频录制的最大时间 */
    String RECORD_MAX_TIME = "01:00";
    /** 防止连续点击 */
    int RECORD_DOUBLE_CLICK_TIME = 1000;

//    interface IRecordManager {
//        void removeRedPackageView();
//    }

    interface ICameraView {
//        View initView();

//        void updateNum(String num);

        View getView();

        void timeUp();

//        void startPlayVideo();

        void pauseVideo();

        void resumeVideo();

        void stopRecordVideo();
    }

    interface ICameraPresenter {

        /**
         * 是否是强制提交
         *
         * @param isForce 1：是 2：否
         */
        void submitSpeechShow(String isForce, String videoDuration);

        //        void removeView(View view);
        void sendSuperSpeakerCameraStatus();

        void stopRecord();

        @Deprecated
        void uploadSucess(String videoUrl, String audioUrl, String averVocieDecibel);
//        void updateNum(String num);

        //        void timeUp();
        void showAnima();

        void stopLiveVideo();

        void startLiveVideo();
    }

    interface ISuperSpeakerBridge {
        /**
         * 是否是强制提交
         *
         * @param isForce 1：是 2：否
         */
        void submitSpeechShow(String isForce, String averVocieDecibel);

        void removeView(View view);

        void pauseVideo();

        void updateNum(String num);

        void timeUp();

//        void startPlayVideo();

        void resumeVideo();

        boolean containsView();

        void sendSuperSpeakerCameraStatus();

    }

    interface ICommonPresenter {
        /**
         * 是否是强制提交
         *
         * @param isForce 1：是 2：否
         */
        void submitSpeechShow(String isForce);

        /**
         * 移除指定View
         *
         * @param view 指定的view
         */
//        void removeView(View view);

        /**
         * 移除整体View
         */
        void removeCameraView();
    }

    interface IRedPackageView {
        /**
         * 更新金币余额
         *
         * @param num 剩下的金币余额
         */
        void updateNum(String num);

        View getView();
    }

    interface ICommonTip {
        /**
         * 时间到了，结束视频录制
         */
        void timeUp(boolean complete);

        View getView();
    }

    interface IRedPackagePresenter {
        void removeView(View view);
    }

    //    interface ICameraBackPresenter {
//        void removeView(View view);
//
//        void removeCameraView();
//    }
    String SUPER_SPEAKER_EVNT_ID = "super_speaker_event_id";
    String INIT_CAMERA = "initCamera";
    String VIDEO_URL = "video_url";
    String CAMERA_SIZE = "camera_size";
    String LAYOUT_SIZE = "layout_size";
    String CAMERA_INVISIBLE = "camera_invisible";
    String STOP_RECORD = "stop_record";

    String IS_LIVE = "1";
    String IS_PLAYBACK = "2";

    int VIDEO_WIDTH = 1280;
    int VIDEO_HEIGHT = 720;
}

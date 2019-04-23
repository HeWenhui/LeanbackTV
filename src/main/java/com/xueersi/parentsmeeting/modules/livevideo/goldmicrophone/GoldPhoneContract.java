package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.view.View;

import java.util.List;

public interface GoldPhoneContract {
    /** 金话筒的时间间隔 */
    long LOTTIE_VIEW_INTERVAL = 2000;
    /** 金话筒的音量 */
    int GOLD_MICROPHONE_VOLUME = 14;
    String MP3_FILE_NAME = "/gold_microphone.mp3";
    /** 采取数据间隔 */
    long VOLUME_INTERVAL = 150;
    int ONE_GEAR_LEFT = 0;
    int ONE_GEAR_RIGHT = 12;
    int TWO_GEAR_RIGHT = 14;
    int THREE_GEAR_RIGHT = 30;
    int GOLD_ONE_LEVEL_INTEVAL = 1000;
    int GOLD_TWO_LEVEL_INTEVAL = 300;
    int GOLD_THREE_LEVEL_INTEVAL = 200;

    interface GoldPhoneView {
        View getRootView();

        /**
         * 关闭金话筒
         */
        void showCloseView();

        /**
         * 显示设置界面
         *
         * @param isVisible
         */
        void showSettingView(boolean isVisible);

        /**
         * 显示大声说界面
         */
        void showSpeakLoudly();

        /**
         * addView之后的一系列操作
         */
        void performAddView();

        /**
         * 显示金话筒的lottieView
         */
        void showLottieView();

        void addRipple(int level);

        List<SoundWaveView.Circle> getRipples();

        void onResume();
    }

    interface GoldPhonePresenter {
        /**
         * 移除指定view
         *
         * @param view 移除该view
         */
        void remove(View view);

        /**
         * 开启录音
         */
        void startAudioRecord();

        void stopAudioRecord();
    }

    interface CloseTipPresenter {
        /**
         * 移除"关闭弹窗"的View
         *
         * @param view 关闭弹窗
         */
        void removeCloseTipView(View view);

        /**
         * 移除整个金话筒的页面
         */
        void removeGoldView();
    }

    interface CloseTipView {
        View getRootView();
    }
}

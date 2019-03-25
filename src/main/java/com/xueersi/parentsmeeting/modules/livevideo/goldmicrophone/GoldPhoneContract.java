package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.view.View;

public interface GoldPhoneContract {
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
    }

    interface CloseTipPresenter {
        /**
         * 移除"关闭弹窗"的View
         *
         * @param view 关闭弹窗
         */
        void removeCloseView(View view);

        /**
         * 移除整个金话筒的页面
         */
        void removeGoldView();
    }

    interface CloseTipView {
//        void clickYes();

//        void clickNo();

        View getRootView();
    }
}

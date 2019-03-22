package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.view.View;

public interface GoldPhoneContract {
    interface GoldPhoneView {
        View getRootView();

        void showCloseView();

        void showSettingView(boolean isVisible);
    }

    interface GoldPhonePresenter {
        void remove(View view);
    }

    interface CloseTipPresenter {
        void removeCloseView(View view);

        void removeGoldView();
    }

    interface CloseTipView {
//        void clickYes();

//        void clickNo();

        View getRootView();
    }
}

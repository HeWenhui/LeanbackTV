package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.view.View;

public interface GoldPhoneContract {
    interface GoldPhoneView {
        View getRootView();
    }

    interface GoldPhonePresenter {

    }

    interface CloseTipPresenter {
        void removeCloseView();

        void removeGoldView();
    }

    interface CloseTipView {
        void clickYes();

        void clickNo();

        View getRootView();
    }
}

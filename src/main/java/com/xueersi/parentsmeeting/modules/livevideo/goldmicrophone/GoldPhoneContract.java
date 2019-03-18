package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.view.View;

public interface GoldPhoneContract {
    interface GoldPhoneView {
        View getRootView();
    }

    interface GoldPhonePresenter {

    }

    interface CloseTipView {
        void clickYes();

        void clickNo();

        View getRootView();
    }
}

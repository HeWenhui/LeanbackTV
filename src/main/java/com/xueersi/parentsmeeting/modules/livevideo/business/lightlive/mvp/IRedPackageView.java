package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.mvp;

import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

public interface IRedPackageView {

    void setOnPagerClose(LiveBasePager.OnPagerClose onPagerClose);

    void setReceiveGold(ReceiveGold receiveGold);

    void onDismiss();
}

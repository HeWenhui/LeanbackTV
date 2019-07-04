package com.xueersi.parentsmeeting.modules.livevideo.betterme.contract;

import com.xueersi.common.base.BasePager;

public interface OnBettePagerClose {
    void onClose(BasePager basePager);

    void onShow(int pagerType, int duration);

    void onShow(int pagerType);
}

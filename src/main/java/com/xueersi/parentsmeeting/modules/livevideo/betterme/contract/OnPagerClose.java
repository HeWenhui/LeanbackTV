package com.xueersi.parentsmeeting.modules.livevideo.betterme.contract;

import com.xueersi.common.base.BasePager;

public interface OnPagerClose {
    void onClose(BasePager basePager);

    void onNext(int pagerType, int duration);

    void onNext(int pagerType);
}

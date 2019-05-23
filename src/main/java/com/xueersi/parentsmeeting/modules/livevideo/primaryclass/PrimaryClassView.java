package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.view.View;

public interface PrimaryClassView {
    int getKuangjia();

    int getBackImg();

    void decorateItemMy(View view);

    void decorateItemOther(View view);

    void decorateItemEmpty(View view);

    void decorateItemBack(View view);

    void decorateItemMyAddEnergy(View view);
}

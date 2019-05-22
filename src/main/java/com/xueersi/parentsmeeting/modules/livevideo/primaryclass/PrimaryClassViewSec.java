package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

public class PrimaryClassViewSec implements PrimaryClassView {
    public PrimaryClassViewSec(Context context) {
        ProxUtil.getProxUtil().put(context, PrimaryClassView.class, this);
    }

    @Override
    public int getKuangjia() {
        return R.drawable.bg_live_primary_class_kuangjia_img_normal;
    }

    @Override
    public int getBackImg() {
        return R.drawable.bg_livevideo_priclass_normal;
    }
}

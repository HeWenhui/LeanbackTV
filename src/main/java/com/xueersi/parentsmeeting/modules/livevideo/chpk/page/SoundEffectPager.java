package com.xueersi.parentsmeeting.modules.livevideo.chpk.page;

import android.content.Context;

import com.xueersi.common.base.BasePager;

/**
 * Created by yuanwei2 on 2019/8/8.
 */

public abstract class SoundEffectPager extends BasePager {

    public SoundEffectPager() {
    }

    public SoundEffectPager(Context context) {
        super(context);
    }

    public SoundEffectPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    public SoundEffectPager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
    }

    public abstract void releaseSoundRes();

}

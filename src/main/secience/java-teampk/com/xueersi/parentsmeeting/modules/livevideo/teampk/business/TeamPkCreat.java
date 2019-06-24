package com.xueersi.parentsmeeting.modules.livevideo.teampk.business;

import android.content.Intent;

import com.xueersi.parentsmeeting.modules.livevideo.business.BusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class TeamPkCreat implements BusinessCreat {
    @Override
    public Class<? extends LiveBaseBll> getClassName(Intent intent) {
        LiveLoggerFactory.getLogger(this).d("getClass:intent=" + intent);
        return TeamPkBll.class;
    }
}

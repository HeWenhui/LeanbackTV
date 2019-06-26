package com.xueersi.parentsmeeting.modules.livevideo.teampk.business;

import android.content.Intent;

import com.xueersi.parentsmeeting.modules.livevideo.business.BusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.business.ChinesePkBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class TeamPkCreat implements BusinessCreat {
    @Override
    public Class<? extends LiveBaseBll> getClassName(Intent intent) {
        LiveLoggerFactory.getLogger(this).d("getClass:intent=" + intent);
        int pattern = intent.getIntExtra("pattern", 1);
        int useSkin = intent.getIntExtra("useSkin", 0);
        // 语文半身直播 添加 语文pk 业务类
        if ((pattern == LiveVideoConfig.LIVE_TYPE_HALFBODY || pattern == LiveVideoConfig.LIVE_TYPE_HALFBODY_CLASS) && useSkin == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            return ChinesePkBll.class;
        } else {
            return TeamPkBll.class;
        }
    }
}

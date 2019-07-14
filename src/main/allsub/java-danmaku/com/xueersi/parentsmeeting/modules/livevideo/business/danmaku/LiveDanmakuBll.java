package com.xueersi.parentsmeeting.modules.livevideo.business.danmaku;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

public class LiveDanmakuBll extends LiveBaseBll {
    private LiveDanmaku liveDanmaku;

    public LiveDanmakuBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        liveDanmaku = new LiveDanmaku(context);
    }

    @Override
    public void initView() {
        super.initView();
        liveDanmaku.initView(getLiveViewAction());
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        liveDanmaku.onLiveInited(getInfo);
    }
}

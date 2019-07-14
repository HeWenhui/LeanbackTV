package com.xueersi.parentsmeeting.modules.livevideo.business.danmaku;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

import master.flame.danmaku.danmaku.danmaku.model.BaseDanmaku;

public interface LiveDanmakuPro extends LiveProvide {
    void addDanmaku(BaseDanmaku danmaku);
}

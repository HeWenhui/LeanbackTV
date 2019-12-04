package com.xueersi.parentsmeeting.modules.livevideo.business.danmaku;

import android.app.Activity;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
//import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceLiveBackBll;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2019/7/14.
 * 回放体验课弹幕
 */
public class LiveDanmakuBackBll extends LiveBackBaseBll {
    private LiveDanmaku liveDanmaku;

    /** 回放弹幕 */
    public LiveDanmakuBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        liveDanmaku = new LiveDanmaku(activity);
    }

    /** 全身直播体验课弹幕 */
//    public LiveDanmakuBackBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
//        super(activity, liveBackBll);
//        liveDanmaku = new LiveDanmaku(activity);
//    }

    @Override
    public void initView() {
        super.initView();
        liveDanmaku.initView(getLiveViewAction());
        liveDanmaku.onLiveInited(liveGetInfo);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        liveDanmaku.onDestroy();
    }
}

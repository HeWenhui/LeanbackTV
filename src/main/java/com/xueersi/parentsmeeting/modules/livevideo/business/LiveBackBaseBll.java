package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseBll;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;

/**
 * Created by lyqai on 2018/7/17.
 */

public class LiveBackBaseBll extends BaseBll {
    protected Logger logger = LoggerFactory.getLogger(TAG);
    LiveBackBll liveBackBll;
    Activity activity;
    protected RelativeLayout mRootView;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;

    public LiveBackBaseBll(Activity activity, LiveBackBll liveBackBll, RelativeLayout mRootView) {
        super(activity);
    }
}

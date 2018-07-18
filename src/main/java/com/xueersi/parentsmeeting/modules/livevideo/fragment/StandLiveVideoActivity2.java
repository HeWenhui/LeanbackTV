package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity2;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveStandMediaControllerBottom;

/**
 * Created by lyqai on 2018/7/13.
 */

public class StandLiveVideoActivity2 extends LiveVideoActivity2 {
    private String TAG = "StandLiveVideoActivity2Log";
    Logger logger = LoggerFactory.getLogger(TAG);
    LiveStandFrameAnim liveStandFrameAnim;
    boolean startGetInfo = false;
    LiveStandMediaControllerBottom standMediaControllerBottom;
    boolean isSetFirstParam = true;

    public StandLiveVideoActivity2() {
        mLayoutVideo = R.layout.activity_video_live_stand_new;
    }

    @Override
    protected void createLiveVideoAction() {
        liveVideoAction = new StandLiveVideoAction(activity, mLiveBll, mContentView, mode);
    }

    @Override
    protected void createMediaControllerBottom() {
        liveMediaControllerBottom = standMediaControllerBottom = new LiveStandMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        Loger.d(TAG, "onLiveInit");
        standMediaControllerBottom.onModeChange(getInfo.getMode(), getInfo);
    }

    @Override
    public void onModeChange(final String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                standMediaControllerBottom.onModeChange(mode, mGetInfo);
            }
        });
    }

    @Override
    protected void onVideoCreateEnd() {
        startGetInfo = false;
        liveStandFrameAnim = new LiveStandFrameAnim(activity);
        Loger.d(TAG, "onVideoCreateEnd");
        liveStandFrameAnim.check(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                startGetInfo = true;
                View vsLiveStandUpdate = activity.findViewById(R.id.vs_live_stand_update);
                if (vsLiveStandUpdate != null) {
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                } else {
                    vsLiveStandUpdate = activity.findViewById(R.id.rl_live_stand_update);
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                }
                if (activity.isFinishing()) {
                    return;
                }
                StandLiveVideoActivity2.super.onVideoCreateEnd();
            }
        });
    }

    @Override
    protected void startGetInfo() {
        Loger.d(TAG, "startGetInfo:startGetInfo=" + startGetInfo);
        if (startGetInfo) {
            super.startGetInfo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveStandFrameAnim != null) {
            liveStandFrameAnim.onDestory();
        }
    }
}

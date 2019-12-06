package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveStandMediaControllerBottom;

/**
 * Created by linyuqiang on 2018/7/13.
 * 站立直播
 */
public class StandLiveVideoFragment extends LiveVideoFragment {
    private String TAG = "StandLiveVideoFragment";
    Logger logger = LoggerFactory.getLogger(TAG);
    LiveStandFrameAnim liveStandFrameAnim;
    boolean startGetInfo = false;
    protected LiveStandMediaControllerBottom standMediaControllerBottom;
    boolean isSetFirstParam = true;

    public StandLiveVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_stand_new;
    }

    @Override
    protected void createLiveVideoAction() {
        liveVideoAction = new StandLiveVideoAction(activity, mLiveBll, mContentView, mode);
    }

    @Override
    protected void createMediaControllerBottom() {
        liveMediaControllerBottom = standMediaControllerBottom = new LiveStandMediaControllerBottom(activity,
                mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        logger.d("onLiveInit");
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
        logger.d("onVideoCreateEnd");
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
                StandLiveVideoFragment.super.onVideoCreateEnd();
            }
        });
    }

    @Override
    protected void startGetInfo() {
        logger.d("startGetInfo:startGetInfo=" + startGetInfo);
        if (startGetInfo) {
            super.startGetInfo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveStandFrameAnim != null) {
            liveStandFrameAnim.onDestroy();
        }
    }
}

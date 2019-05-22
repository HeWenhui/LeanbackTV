package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.L;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodySceneTransAnim;
import com.xueersi.parentsmeeting.modules.livevideo.business.PrimaryClassLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business.PrimaryClassIrcBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePrimaryClassMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PrimaryClassLiveMediaCtrlTop;

/**
 * Created by linyuqiang on 2018/7/13.
 * 小版体验
 */
public class PrimaryClassVideoFragment extends LiveVideoFragment {
    private String TAG = "PrimaryClassVideoFragment";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private HalfBodySceneTransAnim mTransAnim;
    PrimaryClassLiveMediaCtrlTop primaryClassLiveMediaCtrlTop;
    private LivePrimaryClassMediaControllerBottom mHalfBodyMediaControllerBottom;

    public PrimaryClassVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_primary_class;
    }

    @Override
    protected void createLiveVideoAction() {
        int useSkin = activity.getIntent().getIntExtra("useSkin", 0);
        liveVideoAction = new PrimaryClassLiveVideoAction(activity, mLiveBll, mContentView, rlContent, isArts, mode, useSkin);
    }

    @Override
    protected void addBusiness(Activity activity) {
        super.addBusiness(activity);
        mLiveBll.addBusinessBll(new PrimaryClassIrcBll(activity, mLiveBll));
    }

    @Override
    protected void onVideoCreateEnd() {
        super.onVideoCreateEnd();
        LiveVideoView liveVideoView = (LiveVideoView) videoView;
        liveVideoView.setVideoLayoutInter(new LiveVideoView.VideoLayoutInter() {
            @Override
            public boolean setVideoLayout(int layout, float userRatio, int videoWidth, int videoHeight, float videoRatio) {
                logger.d("setVideoLayout:mode=" + mode + ",layout=" + layout + ",videoWidth=" + videoWidth + ",videoHeight=" + videoHeight);
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                if (lp.width != ViewGroup.LayoutParams.MATCH_PARENT || lp.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    LayoutParamsUtil.setViewLayoutParams(videoView, lp);
                }
                return LiveTopic.MODE_CLASS.equals(mode);
            }
        });
    }

    @Override
    protected void createMediaControlerTop() {
        baseLiveMediaControllerTop = primaryClassLiveMediaCtrlTop = new PrimaryClassLiveMediaCtrlTop(activity, mMediaController, videoFragment);
    }

    @Override
    protected void createMediaControllerBottom() {
        mHalfBodyMediaControllerBottom = new LivePrimaryClassMediaControllerBottom(activity, mMediaController,
                videoFragment);
        liveMediaControllerBottom = mHalfBodyMediaControllerBottom;
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        primaryClassLiveMediaCtrlTop.onModeChange(mode, mGetInfo);
        mHalfBodyMediaControllerBottom.onModeChange(getInfo.getMode(), getInfo);
    }

    @Override
    public void onModeChange(final String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mHalfBodyMediaControllerBottom.onModeChange(mode, mGetInfo);
                primaryClassLiveMediaCtrlTop.onModeChange(mode, mGetInfo);
            }
        });
        showSceneTransAnim(mode, isPresent);
    }

    /**
     * 切流转场动画
     */
    private void showSceneTransAnim(String mode, boolean isPresent) {
        if (mTransAnim == null) {
            mTransAnim = new HalfBodySceneTransAnim(activity, mGetInfo);
        }
        mTransAnim.onModeChange(mode, isPresent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveVideoPoint.getInstance().clear(activity);
    }
}

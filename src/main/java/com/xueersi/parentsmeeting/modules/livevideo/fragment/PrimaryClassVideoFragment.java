package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.view.View;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodySceneTransAnim;
import com.xueersi.parentsmeeting.modules.livevideo.business.PrimaryClassLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business.PrimaryClassIrcBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePrimaryClassMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PrimaryClassLiveMediaCtrlTop;

/**
 * Created by linyuqiang on 2018/7/13.
 * 小版体验
 */
public class PrimaryClassVideoFragment extends LiveVideoFragment {
    private String TAG = "PrimaryClassVideoFragment";
    Logger logger = LoggerFactory.getLogger(TAG);
    private HalfBodySceneTransAnim mTransAnim;
    PrimaryClassLiveMediaCtrlTop primaryClassLiveMediaCtrlTop;
    private LivePrimaryClassMediaControllerBottom mHalfBodyMediaControllerBottom;

    public PrimaryClassVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_primary_class;
    }

    @Override
    protected void createLiveVideoAction() {
        liveVideoAction = new PrimaryClassLiveVideoAction(activity, mLiveBll, mContentView, rlContent, isArts, mode);
    }

    @Override
    protected void addBusiness(Activity activity) {
        super.addBusiness(activity);
        mLiveBll.addBusinessBll(new PrimaryClassIrcBll(activity, mLiveBll));
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
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.fragment.halfbody;

import android.util.Log;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodyLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodySceneTransAnim;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveHalfBodyMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveHalfBodyMediaControllerTop;

/**
 * 半身直播
 *
 * @author chenkun
 * @version 1.0, 2018/10/22 下午2:12
 */

public class HalfBodyLiveVideoFragement extends LiveVideoFragment {

    private static final String TAG = "HalfBodyLiveVideoFragement";
    private LiveHalfBodyMediaControllerBottom mHalfBodyMediaControllerBottom;
    private LiveHalfBodyMediaControllerTop mMediaControllerTop;
    private HalfBodySceneTransAnim mTransAnim;

    public HalfBodyLiveVideoFragement() {
        Log.e(TAG, "=======>HalfBodyLiveVideoFragement created");
        mLayoutVideo = R.layout.activity_video_live_halfbody;

    }

    @Override
    protected void createLiveVideoAction() {
        Log.e(TAG, "=======>createLiveVideoAction created");
        liveVideoAction = new HalfBodyLiveVideoAction(activity, mLiveBll, mContentView, mode);
    }


    @Override
    protected void createMediaControllerBottom() {
        Log.e(TAG, "=======>createMediaControllerBottom");
        mHalfBodyMediaControllerBottom = new LiveHalfBodyMediaControllerBottom(activity, mMediaController,
                videoFragment);
        liveMediaControllerBottom = mHalfBodyMediaControllerBottom;
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void createMediaControlerTop() {
        mMediaControllerTop = new LiveHalfBodyMediaControllerTop(activity,mMediaController,videoFragment);
        baseLiveMediaControllerTop = mMediaControllerTop;
    }

    @Override
    public void setMediaControllerBottomParam() {
        //super.setMediaControllerBottomParam();

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        Log.e(TAG, "=======>onLiveInit");
        mHalfBodyMediaControllerBottom.onModeChange(getInfo.getMode(),getInfo);
        mMediaControllerTop.onModeChange(getInfo.getMode(),getInfo);
    }


    @Override
    public void onModeChange(final String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        // 主/辅 状态切换
        Log.e(TAG, "====>onModeChange:" + mode);
        //延迟 2秒 适配转场动画节奏
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mHalfBodyMediaControllerBottom.onModeChange(mode, mGetInfo);
                mMediaControllerTop.onModeChange(mode,mGetInfo);
            }
        });
        showSceneTransAnim(mode,isPresent);
    }

    /**
     * 切流转场动画
     */
    private void showSceneTransAnim(String mode,boolean isPresent) {
      if(mTransAnim == null){
          mTransAnim = new HalfBodySceneTransAnim(activity);
      }
        mTransAnim.onModeChange(mode,isPresent);
    }


    @Override
    protected void onVideoCreateEnd() {
        super.onVideoCreateEnd();
    }


    @Override
    protected void startGetInfo() {
        super.startGetInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTransAnim != null){
            mTransAnim.release();
        }
    }
}

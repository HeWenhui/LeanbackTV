package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.PrimaryClassLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PrimaryClassLiveMediaCtrlTop;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ImageScale;

/**
 * Created by linyuqiang on 2018/7/13.
 * 小版体验
 */
public class PrimaryClassVideoFragment extends LiveVideoFragment {
    private String TAG = "PrimaryClassVideoFragment";
    Logger logger = LoggerFactory.getLogger(TAG);
    PrimaryClassLiveMediaCtrlTop primaryClassLiveMediaCtrlTop;

    public PrimaryClassVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_primary_class;
    }

    @Override
    protected void createLiveVideoAction() {
        liveVideoAction = new PrimaryClassLiveVideoAction(activity, mLiveBll, mContentView, rlContent, isArts, mode);
    }

    @Override
    protected void createMediaControlerTop() {
        baseLiveMediaControllerTop = primaryClassLiveMediaCtrlTop = new PrimaryClassLiveMediaCtrlTop(activity, mMediaController, videoFragment);
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        primaryClassLiveMediaCtrlTop.onModeChange(mode, mGetInfo);
    }

    @Override
    public void onModeChange(String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        primaryClassLiveMediaCtrlTop.onModeChange(mode, mGetInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

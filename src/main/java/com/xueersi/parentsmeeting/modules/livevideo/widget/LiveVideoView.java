package com.xueersi.parentsmeeting.modules.livevideo.widget;


import android.content.Context;
import android.util.AttributeSet;

import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;

/**
 * Created by lyqai on 2017/8/2.
 */

public class LiveVideoView extends VideoView {

    public LiveVideoView(Context context) {
        super(context);
    }

    public LiveVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public void setVideoLayout(int layout, float userRatio, int videoWidth, int videoHeight, float videoRatio) {
//        mVideoMode = layout;
//        setSurfaceLayout(userRatio, videoWidth, videoHeight, videoRatio);
//    }
//
//    /** 初始化播放画布的界面 */
//    private void setSurfaceLayout(float userRatio, int videoWidth, int videoHeight, float videoAspectRatio) {
//        final View contentView = ((Activity) getContext()).findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        int windowWidth = (r.right - r.left);
//        int windowHeight = ScreenUtils.getScreenHeight();
//        int abs = Math.abs(windowWidth - ScreenUtils.getScreenWidth());
//        if (abs > 0 && abs < 80) {
//            XesMobAgent.videoViewWH(1, windowWidth, ScreenUtils.getScreenWidth());
//        }
//        abs = Math.abs(windowHeight - ScreenUtils.getScreenHeight());
//        if (abs > 0 && abs < 80) {
//            XesMobAgent.videoViewWH(2, windowHeight, ScreenUtils.getScreenHeight());
//        }
//        float windowRatio = windowWidth / (float) windowHeight;
//        float videoRatio = userRatio <= 0.01f ? videoAspectRatio : userRatio;
//        mSurfaceHeight = videoHeight;
//        mSurfaceWidth = videoWidth;
//        int paramsWidth, paramsHeight;
//        if (VIDEO_LAYOUT_ORIGIN == mVideoMode && mSurfaceWidth < windowWidth && mSurfaceHeight < windowHeight) {
//            paramsWidth = (int) (mSurfaceHeight * videoRatio);
//            paramsHeight = mSurfaceHeight;
//        } else if (mVideoMode == VIDEO_LAYOUT_ZOOM) {
//            paramsWidth = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
//            paramsHeight = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
//        } else if (mVideoMode == VIDEO_LAYOUT_SCALE_ZOOM && mVideoHeight > 0) {
//            paramsWidth = (int) (mVideoHeight * videoRatio);
//            paramsHeight = mVideoHeight;
//        } else {
//            boolean full = mVideoMode == VIDEO_LAYOUT_STRETCH;
//            paramsWidth = (full || windowRatio < videoRatio) ? windowWidth : (int) (videoRatio * windowHeight);
//            paramsHeight = (full || windowRatio > videoRatio) ? windowHeight : (int) (windowWidth / videoRatio);
//        }
//        ViewGroup.LayoutParams lp = getLayoutParams();
//        mVideoHeight = paramsHeight;
//        if (lp.width != paramsWidth || lp.height != paramsHeight) {
//            lp.width = paramsWidth;
//            lp.height = paramsHeight;
//            setLayoutParams(lp);
//            //logger.e( "setSurfaceLayout:paramsWidth=" + paramsWidth + ",paramsHeight=" + paramsHeight);
//        }
//        // 固定surface的大小
//        getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
//        // Logger.i("VideoText2","VIDEO: %dx%dx%f, Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f",
//        // videoWidth, videoHeight, videoAspectRatio, mSurfaceWidth,
//        // mSurfaceHeight, lp.width, lp.height, windowWidth, windowHeight,
//        // windowRatio);
//    }
}

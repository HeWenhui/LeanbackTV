package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.widget;

import android.content.Context;

import com.xueersi.parentsmeeting.module.videoplayer.media.CommonGestures;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;

/**
 * 张月毅：站立直播体验课的控制器，要求没有底部控制栏，不可调节进度。
 */
public class StandLiveVideoExperienceMediaController extends MediaController2 {

    public StandLiveVideoExperienceMediaController(Context context, MediaPlayerControl player, boolean isDrawHeight) {
        super(context, player, isDrawHeight);
        if (mPlayer.isLandSpace()) {
            try {
                mControlsLayout.setVisibility(GONE);//设置底部控制栏不可见
            } catch (Exception e) {
                e.printStackTrace();
            }
            mGestures.setTouchListener(standExperienceListener, true);//设置新手势，抵消掉原来的滑动
        }
    }

    @Override
    protected void findViewItems() {
        super.findViewItems();

    }


    /**
     * 站立直播体验课的手势监听事件，覆盖MediaController2的手势监听事件
     */
    private CommonGestures.GestureTouchListener standExperienceListener = new CommonGestures.GestureTouchListener() {

        @Override
        public void onGestureBegin() {
            mTouchListener.onGestureBegin();
        }

        @Override
        public void onGestureEnd() {
            mTouchListener.onGestureEnd();
        }

        @Override
        public void onLeftSlide(float percent) {
            mTouchListener.onLeftSlide(percent);
        }

        @Override
        public void onRightSlide(float percent) {
            mTouchListener.onRightSlide(percent);
        }

        @Override
        public void onSeekControl(float percent) {

        }

        @Override
        public void onSeekTo() {

        }

        @Override
        public boolean canSeek() {
            return false;
        }

        @Override
        public void onSingleTap() {
            mTouchListener.onSingleTap();
        }

        @Override
        public void onDoubleTap() {
//            mTouchListener.onDoubleTap();
        }

        @Override
        public void onScale(float scaleFactor, int state) {
            mTouchListener.onScale(scaleFactor, state);
        }
    };
}

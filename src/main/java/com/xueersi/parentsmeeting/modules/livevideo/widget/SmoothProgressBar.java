package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.SeekBar;

/**
 * Created by lenovo on 2018/12/11.
 *
 * @author yuanwei
 */

public class SmoothProgressBar extends SeekBar {

    public class AnimateHelper {

        /**
         * 开始进度
         */
        private int fromProgress;

        /**
         * 结束进度
         */
        private int stopProgress;

        /**
         * 当前进度
         */
        private int currProgress;

        /**
         * 动画时间
         */
        private long durationTime;

        /**
         * 动画开始时间
         */
        private long startMillis;

        public void startAnimate(int fromProgress, int stopProgress, long durationTime) {
            this.fromProgress = fromProgress;
            this.stopProgress = stopProgress;
            this.currProgress = fromProgress;
            this.durationTime = durationTime;
            this.startMillis = AnimationUtils.currentAnimationTimeMillis();
        }

        public int getCurrProgress() {
            return currProgress;
        }

        public boolean computeOffset() {

            if (currProgress == stopProgress) {
                return false;
            }

            long passtime = AnimationUtils.currentAnimationTimeMillis() - startMillis;

            if (passtime > durationTime) {
                passtime = durationTime;
            }

            double radio = passtime / (double) durationTime;
            currProgress = (int) (fromProgress + (stopProgress - fromProgress) * radio);

            return true;
        }

    }

    private boolean isTouchEnable;

    private AnimateHelper animateHelper;

    public SmoothProgressBar(Context context) {
        super(context);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouchEnable(boolean isTouchEnable) {
        this.isTouchEnable = isTouchEnable;
    }

    public void animateToProgress(int stopProgress) {

        if (animateHelper == null) {
            animateHelper = new AnimateHelper();
        }

        animateHelper.startAnimate(getProgress(), stopProgress, 200);
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (isTouchEnable) {
            return super.dispatchTouchEvent(event);
        }

        return false;
    }

    @Override
    public void computeScroll() {

        if (animateHelper != null && animateHelper.computeOffset()) {
            int currProgress = animateHelper.getCurrProgress();
            setProgress(currProgress);
            postInvalidate();
        }
    }

}

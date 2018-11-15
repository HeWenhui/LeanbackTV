package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * 半身直播 视频加载中动画
 *
 * @author chekun
 * created  at 2018/11/15 16:50
 */
public class VideoLoadingImgView extends ImageView {
    public VideoLoadingImgView(Context context) {
        this(context, null);
    }

    public VideoLoadingImgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public VideoLoadingImgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoadingAnim();
    }

    private void startLoadingAnim() {
        if (getDrawable() != null && getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) getDrawable()).start();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (View.VISIBLE == visibility) {
            startLoadingAnim();
        } else {
            stopLoadingAnim();
        }
    }

    private void stopLoadingAnim() {
        if (getDrawable() != null && getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) getDrawable()).stop();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopLoadingAnim();
        super.onDetachedFromWindow();
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by lyqai on 2018/4/5.
 */

public class ReadyGoImageView extends ImageView {
    FrameAnimation frameAnimation;
    FrameAnimation.AnimationListener animationListener;

    public ReadyGoImageView(Context context) {
        super(context);
    }

    public ReadyGoImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void start() {
        if (frameAnimation == null) {
            frameAnimation = FrameAnimation.createFromAees(getContext(), this, "Images/ready_go", 50, false);
        }
        frameAnimation.setAnimationListener(animationListener);
    }

    public void setAnimationListener(FrameAnimation.AnimationListener listener) {
        this.animationListener = listener;
    }

    public void destory() {
        if (frameAnimation != null) {
            frameAnimation.destory();
        }
    }
}

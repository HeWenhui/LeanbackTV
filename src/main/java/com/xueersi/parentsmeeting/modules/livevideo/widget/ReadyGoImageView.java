package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;

/**
 * Created by linyuqiang on 2018/4/5.
 */

public class ReadyGoImageView extends ImageView {
    FrameAnimation frameAnimation;
    FrameAnimation.AnimationListener animationListener;
    String file1 = "live_stand/frame_anim/ready_go";
    LiveSoundPool liveSoundPool;
    LiveSoundPool.SoundPlayTask task;

    public ReadyGoImageView(Context context) {
        super(context);
    }

    public ReadyGoImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void start(LiveSoundPool liveSoundPool2) {
        if (frameAnimation == null) {
            frameAnimation = FrameAnimation.createFromAees(getContext(), this, file1, 50, false);
        }
        this.liveSoundPool = liveSoundPool2;
        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                animationListener.onAnimationStart();
                task = StandLiveMethod.readyGo(liveSoundPool);
            }

            @Override
            public void onAnimationEnd() {
                animationListener.onAnimationEnd();
                liveSoundPool.stop(task);
            }

            @Override
            public void onAnimationRepeat() {
                animationListener.onAnimationRepeat();
            }
        });
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

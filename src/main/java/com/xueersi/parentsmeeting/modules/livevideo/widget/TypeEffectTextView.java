package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by lenovo on 2019/1/24.
 */

public class TypeEffectTextView extends TextView {

    public interface OnEffectListener {
        public void onEffectFinish();
    }

    private class EffectAnimation implements Runnable {
        private String content;

        private int position;

        private int interval;

        public EffectAnimation(String content, int interval) {
            this.content = content;
            this.interval = interval;
            this.position = -1;
        }

        @Override
        public void run() {
            position = position + 1;
            String substring = content.substring(0, position);
            setText(substring);
            invalidate();

            if (position < content.length()) {
                postDelayed(this, interval);
            } else if (onEffectListener != null) {
                onEffectListener.onEffectFinish();
            }
        }
    }

    private OnEffectListener onEffectListener;

    private EffectAnimation effectAnimation;

    public TypeEffectTextView(Context context) {
        super(context);
    }

    public TypeEffectTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TypeEffectTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnEffectListener(OnEffectListener onEffectListener) {
        this.onEffectListener = onEffectListener;
    }

    public void setTextWidthEffect(String text){
        if (effectAnimation!=null){
            removeCallbacks(effectAnimation);
        }

        effectAnimation = new EffectAnimation(text,100);
        post(effectAnimation);
    }
}

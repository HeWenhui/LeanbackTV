package com.xueersi.parentsmeeting.modules.livevideo.achievement.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class EvhieveProgressBar extends ProgressBar {
    SizeChanged sizeChanged;

    public EvhieveProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSizeChanged(SizeChanged sizeChanged) {
        this.sizeChanged = sizeChanged;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (sizeChanged != null) {
            sizeChanged.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public interface SizeChanged {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }
}

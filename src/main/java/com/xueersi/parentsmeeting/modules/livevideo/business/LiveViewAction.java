package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.view.View;
import android.view.ViewGroup;

public interface LiveViewAction {
    void addView(View child);

    void removeView(View child);

    void addView(View child, ViewGroup.LayoutParams params);

    void addView(View child, int index, ViewGroup.LayoutParams params);
}

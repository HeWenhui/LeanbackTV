package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;

public interface LiveViewAction {
    void addView(View child);

    void addView(LiveVideoLevel level, View child);

    void addView(View child, int width, int height);

    void removeView(View child);

    void addView(View child, ViewGroup.LayoutParams params);

    void addView(LiveVideoLevel level, View child, ViewGroup.LayoutParams params);

//    void addView(View child, int index, ViewGroup.LayoutParams params);

//    void addView(LiveVideoLevel level, View child, int index, ViewGroup.LayoutParams params);

    View inflateView(int resource);

    <T extends View> T findViewById(int id);
}

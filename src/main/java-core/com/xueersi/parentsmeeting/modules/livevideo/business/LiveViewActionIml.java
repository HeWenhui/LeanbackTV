package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;

public class LiveViewActionIml implements LiveViewAction {
    RelativeLayout bottomContent;

    public LiveViewActionIml(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    @Override
    public void addView(View child) {
        bottomContent.addView(child);
    }

    @Override
    public void addView(LiveVideoLevel levelEntity, View child) {
        if (levelEntity == LiveVideoLevel.LEVEL_MES) {
            bottomContent.addView(child, 0);
        } else {
            bottomContent.addView(child);
        }
    }

    @Override
    public void removeView(View child) {
        bottomContent.removeView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        bottomContent.addView(child, params);
    }

    @Override
    public void addView(LiveVideoLevel levelEntity, View child, ViewGroup.LayoutParams params) {
        if (levelEntity == LiveVideoLevel.LEVEL_MES) {
            bottomContent.addView(child, 0, params);
        } else {
            bottomContent.addView(child, params);
        }
    }

//    @Override
//    public void addView(View child, int index, ViewGroup.LayoutParams params) {
//        bottomContent.addView(child, index, params);
//    }
//
//    @Override
//    public void addView(LiveVideoLevel level, View child, int index, ViewGroup.LayoutParams params) {
//        bottomContent.addView(child, index, params);
//    }
}

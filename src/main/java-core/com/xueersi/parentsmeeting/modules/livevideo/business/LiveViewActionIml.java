package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;

public class LiveViewActionIml implements LiveViewAction {
    private Activity activity;
    private RelativeLayout bottomContent;
    private RelativeLayout mContentView;

    public LiveViewActionIml(Activity activity, RelativeLayout mContentView, RelativeLayout bottomContent) {
        this.activity = activity;
        this.mContentView = mContentView;
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

    public View inflateView(int resource) {
        final View view1 = LayoutInflater.from(activity).inflate(resource, bottomContent,
                false);
        return view1;
    }

    public <T extends View> T findViewById(int id) {
        if (mContentView != null) {
            T view = mContentView.findViewById(id);
            if (view != null) {
                return view;
            }
        }
        return activity.findViewById(id);
    }
}

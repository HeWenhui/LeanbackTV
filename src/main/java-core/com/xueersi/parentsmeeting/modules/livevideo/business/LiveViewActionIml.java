package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.util.HashMap;

public class LiveViewActionIml implements LiveViewAction {
    private String TAG = "LiveViewActionIml";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private Activity activity;
    private RelativeLayout bottomContent;
    private RelativeLayout mContentView;
    private HashMap<View, LiveVideoLevel> liveVideoLevelHashMap = new HashMap<>();

    /** 必须在主线程 */
    public LiveViewActionIml(Activity activity, RelativeLayout mContentView, RelativeLayout bottomContent) {
        this.activity = activity;
        this.mContentView = mContentView;
        this.bottomContent = bottomContent;
    }

    @Override
    public void addView(View child) {
        logger.d("addView1:child=" + child);
        bottomContent.addView(child);
    }

    @Override
    public void addView(LiveVideoLevel levelEntity, View child) {
        logger.d("addView2:child=" + child + ",levelEntity=" + levelEntity.getLevel());
        addView(levelEntity, child, null);
    }

    @Override
    public void removeView(final View child) {
        int index = bottomContent.indexOfChild(child);
        ViewParent mParent = child.getParent();
        logger.d("removeView:child=" + child + ",id=" + child.getId() + ",index=" + index + ",parent=" + (mParent == bottomContent));
        bottomContent.removeView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        bottomContent.addView(child, params);
    }

    @Override
    public void addView(LiveVideoLevel levelEntity, View child, ViewGroup.LayoutParams params) {
        logger.d("addView4:child=" + child + ",levelEntity=" + levelEntity.getLevel());
        boolean add = false;
        for (int i = 0; i < bottomContent.getChildCount(); i++) {
            View view = bottomContent.getChildAt(i);
            LiveVideoLevel levelEntity2 = liveVideoLevelHashMap.get(view);
            if (levelEntity2 != null) {
                int index = bottomContent.indexOfChild(view);
                int level = levelEntity2.getLevel();
                logger.d("addView4:levelEntity2=" + level + ",index=" + index);
                if (level >= levelEntity.getLevel()) {
                    add = true;
                    if (params != null) {
                        bottomContent.addView(child, index, params);
                    } else {
                        bottomContent.addView(child, index);
                    }
                    break;
                }
            }
        }
        if (!add) {
            if (params != null) {
                bottomContent.addView(child, params);
            } else {
                bottomContent.addView(child);
            }
        }
        liveVideoLevelHashMap.put(child, levelEntity);
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

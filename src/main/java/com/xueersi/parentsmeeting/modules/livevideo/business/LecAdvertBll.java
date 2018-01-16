package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;

/**
 * Created by lyqai on 2018/1/15.
 */

public class LecAdvertBll implements LecAdvertAction, LecAdvertPagerClose {
    String TAG = "LecAdvertBll";
    Context context;
    RelativeLayout bottomContent;
    LecAdvertPager lecAdvertager;

    public LecAdvertBll(Context context) {
        this.context = context;
    }

    public void initView(RelativeLayout bottomContent, boolean isLand) {
        this.bottomContent = bottomContent;
        if (lecAdvertager != null) {
            ViewGroup group = (ViewGroup) lecAdvertager.getRootView().getParent();
            if (group != null) {
                group.removeView(lecAdvertager.getRootView());
            }
            if (!isLand) {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                bottomContent.addView(lecAdvertager.getRootView(), lp);
            } else {
                int step = lecAdvertager.getStep();
                if (step == 1) {
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    bottomContent.addView(lecAdvertager.getRootView(), lp);
                }
            }
        }
    }

    @Override
    public void start(LecAdvertEntity lecAdvertEntity) {
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                lecAdvertager = new LecAdvertPager(context, LecAdvertBll.this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                bottomContent.addView(lecAdvertager.getRootView(), lp);
            }
        });
    }

    @Override
    public void close() {
        if (lecAdvertager != null) {
            bottomContent.removeView(lecAdvertager.getRootView());
            lecAdvertager = null;
            if (context instanceof ActivityChangeLand) {
                ActivityChangeLand activityChangeLand = (ActivityChangeLand) context;
                activityChangeLand.setAutoOrientation(true);
            }
        }
    }
}

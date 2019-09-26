package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

public class ExperHalfBodyLiveAction extends ExperLiveAction {

    public ExperHalfBodyLiveAction(Activity activity, RelativeLayout mContentView, ExpLiveInfo expLiveInfo) {
        super(activity, mContentView, expLiveInfo);
    }

    @Override
    protected void setLayout() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivTeacherNotpresent.getLayoutParams();
        if (params.width != ViewGroup.LayoutParams.MATCH_PARENT || params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
        }
        params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        if (params.width != ViewGroup.LayoutParams.MATCH_PARENT || params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
        }
    }

}

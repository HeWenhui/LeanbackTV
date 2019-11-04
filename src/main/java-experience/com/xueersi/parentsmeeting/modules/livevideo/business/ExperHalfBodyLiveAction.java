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
        if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
            setFull();
            return;
        }
        super.setLayout();
    }

    @Override
    public void onModeChanged(int mode) {
        super.onModeChanged(mode);
        if (mode == ExperConfig.COURSE_STATE_2) {
            setFull();
        } else {
            setThreeFen();
        }
    }
}

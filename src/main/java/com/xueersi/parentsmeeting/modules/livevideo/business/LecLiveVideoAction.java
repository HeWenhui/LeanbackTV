package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/18.
 * 讲座的加载页
 */
public class LecLiveVideoAction extends LiveVideoAction {
    private String TAG = "LecLiveVideoAction";
    /** 当前界面是否横屏 */
    protected AtomicBoolean mIsLand = new AtomicBoolean(false);

    public LecLiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView) {
        super(activity, mLiveBll, mContentView);
    }

    public void setmIsLand(AtomicBoolean mIsLand) {
        this.mIsLand = mIsLand;
    }

//    @Override
//    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
//
//    }
//
//    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
//
//    }

    @Override
    public void onTeacherNotPresent(boolean isBefore) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int visibility = rlFirstBackgroundView.getVisibility();
                mLogtf.d("onTeacherNotPresent:First=" + visibility);
                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                } else {
                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
                    if (dwTeacherNotpresen == null) {
                        dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_normal);
                    }
                    if (mIsLand.get()) {
                        ivTeacherNotpresent.setBackgroundDrawable(dwTeacherNotpresen);
                    } else {
                        ivTeacherNotpresent.setBackgroundDrawable(dwTeacherNotpresen);
                    }
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
        if (mIsLand.get()) {
            super.setFirstParam(liveVideoPoint);
        } else {
            setFirstParamPort();
        }
    }

    public void setFirstParamPort() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        if (params.rightMargin != 0 || params.bottomMargin != 0 || params.topMargin != 0 || params.width != ViewGroup.LayoutParams.MATCH_PARENT || params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            params.rightMargin = 0;
            params.bottomMargin = 0;
            params.topMargin = 0;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            rlFirstBackgroundView.setLayoutParams(params);
            ivTeacherNotpresent.setLayoutParams(params);
            if (dwTeacherNotpresen == null) {
                dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_normal);
            }
            ivTeacherNotpresent.setBackgroundDrawable(dwTeacherNotpresen);
        }
    }
}

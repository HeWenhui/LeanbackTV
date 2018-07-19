package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/18.
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
                mLogtf.d("onTeacherNotPresent:First=" + rlFirstBackgroundView.getVisibility());
                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                } else {
                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
                    if (mIsLand.get()) {
                        ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
                    } else {
                        ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
                    }
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void setFirstParamPort() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        if (params.rightMargin != RelativeLayout.LayoutParams.MATCH_PARENT || params.bottomMargin != RelativeLayout.LayoutParams.MATCH_PARENT) {
            params.rightMargin = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.bottomMargin = RelativeLayout.LayoutParams.MATCH_PARENT;
            rlFirstBackgroundView.setLayoutParams(params);
            ivTeacherNotpresent.setLayoutParams(params);
            ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
        }
    }
}

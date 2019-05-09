package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

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

    public void onConfigurationChanged() {
        final Button bt = mContentView.findViewById(R.id.bt_course_video_livetimeout);
        if (bt != null) {
            if (bt.getVisibility() == View.VISIBLE) {
                bt.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        bt.getViewTreeObserver().removeOnPreDrawListener(this);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bt.getLayoutParams();
//                        if (mIsLand.get()) {
//                            lp.leftMargin = LiveVideoPoint.getInstance().x3 / 2 - bt.getWidth() / 2;
//                            lp.topMargin = LiveVideoPoint.getInstance().screenHeight * 2 / 3 - 40;
//                        } else {
//                            lp.leftMargin = ScreenUtils.getScreenWidth() / 2 - bt.getWidth() / 2;
//                            lp.topMargin = ScreenUtils.getScreenWidth() * 9 / 16 * 2 / 3 - 40;
//                        }
//                        if (tvLoadingHint != null) {
//                            int[] outLocation = new int[2];
//                            tvLoadingHint.getLocationInWindow(outLocation);
//                            lp.topMargin = outLocation[1] + tvLoadingHint.getHeight() + 20;
//                        } else {
//                            lp.topMargin = LiveVideoPoint.getInstance().screenHeight * 2 / 3 - 40;
//                        }
                        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                        logger.d("onConfigurationChanged:mIsLand=" + mIsLand.get() + ",left=" + lp.leftMargin + "," + lp.topMargin);
                        bt.setLayoutParams(lp);
                        return false;
                    }
                });
            }
        }
    }

//    @Override
//    public void onLiveTimeOut() {
//        final Button bt = mContentView.findViewById(R.id.bt_course_video_livetimeout);
//        if (bt != null) {
//            bt.setVisibility(View.VISIBLE);
//            bt.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bt.getLayoutParams();
////                    if (mIsLand.get()) {
////                        lp.leftMargin = LiveVideoPoint.getInstance().x3 / 2 - bt.getWidth() / 2;
////                    } else {
////                        lp.leftMargin = ScreenUtils.getScreenWidth() / 2 - bt.getWidth() / 2;
////                    }
////                    if (tvLoadingHint != null) {
////                        int[] outLocation = new int[2];
////                        tvLoadingHint.getLocationInWindow(outLocation);
////                        lp.topMargin = outLocation[1] + tvLoadingHint.getHeight() + 20;
////                    } else {
////                        lp.topMargin = LiveVideoPoint.getInstance().screenHeight * 2 / 3 - 40;
////                    }
//                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//                    bt.setLayoutParams(lp);
//                    logger.d("onLiveTimeOut:mIsLand=" + mIsLand.get() + ",left=" + lp.leftMargin + "," + lp.topMargin);
//                    bt.getViewTreeObserver().removeOnPreDrawListener(this);
//                    return false;
//                }
//            });
//            bt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mLiveBll.liveGetPlayServer();
//                    v.setVisibility(View.GONE);
//                }
//            });
//        } else {
//            XESToastUtils.showToast(activity, "老师不在直播间,请退出直播间重试");
//        }
//    }

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

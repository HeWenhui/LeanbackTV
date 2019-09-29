package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

public class ExperStandLiveAction extends ExperLiveAction {

    /** 老师不在直播间背景图 */
    protected Drawable dwTeacherNotpresenBefore;
    /** 老师不在直播间背景图 */
    protected Drawable dwTeacherNotpresenAfter;
    /** 老师不在直播间背景图 */
    protected Drawable dwTeacherNotpresenDoing;
    private LinearLayout ll_course_video_loading;
    private ImageView iv_course_video_loading_bg;

    public ExperStandLiveAction(Activity activity, RelativeLayout mContentView, ExpLiveInfo expLiveInfo) {
        super(activity, mContentView, expLiveInfo);
        ll_course_video_loading = mContentView.findViewById(R.id.ll_course_video_loading);
        iv_course_video_loading_bg = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
            ll_course_video_loading.setVisibility(View.GONE);
            iv_course_video_loading_bg.setVisibility(View.GONE);
            if (dwTeacherNotpresenBefore == null) {
                dwTeacherNotpresenBefore = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before);
            }
            rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenBefore);
        } else {
            ll_course_video_loading.setVisibility(View.VISIBLE);
            iv_course_video_loading_bg.setVisibility(View.VISIBLE);
        }
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
    protected void setThreeFen() {
        super.setThreeFen();
        logger.d("setThreeFen");
        if (ll_course_video_loading != null) {
            ll_course_video_loading.setVisibility(View.VISIBLE);
        }
        if (iv_course_video_loading_bg != null) {
            iv_course_video_loading_bg.setVisibility(View.VISIBLE);
        }
        rlFirstBackgroundView.setBackgroundColor(0xff000000);
    }

    private void setFull() {
        logger.d("setFull");
        if (ll_course_video_loading != null) {
            ll_course_video_loading.setVisibility(View.GONE);
        }
        if (iv_course_video_loading_bg != null) {
            iv_course_video_loading_bg.setVisibility(View.GONE);
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivTeacherNotpresent.getLayoutParams();
        logger.d("setFull:width=" + params.width + ",height=" + params.height);
        if (params.width != ViewGroup.LayoutParams.MATCH_PARENT || params.height != ViewGroup.LayoutParams.MATCH_PARENT || params.rightMargin != 0 || params.leftMargin != 0) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.leftMargin = 0;
            params.rightMargin = 0;
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
        }
        params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        logger.d("setFull:width=" + params.width + ",height=" + params.height);
        if (params.width != ViewGroup.LayoutParams.MATCH_PARENT || params.height != ViewGroup.LayoutParams.MATCH_PARENT || params.rightMargin != 0 || params.leftMargin != 0) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.leftMargin = 0;
            params.rightMargin = 0;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
        }
    }

    boolean isFirst = true;

    @Override
    public void resultFailed(VideoPlayState videoPlayState, int arg1, int arg2) {
        super.resultFailed(videoPlayState, arg1, arg2);
        if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
            if (dwTeacherNotpresenBefore == null) {
                dwTeacherNotpresenBefore = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before);
            }
            rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenBefore);
        } else {
            rlFirstBackgroundView.setBackgroundColor(0xff000000);
        }
    }

    public void onModeChanged(int mode) {
        logger.d("onModeChanged:mode=" + mode);
        if (mode == ExperConfig.COURSE_STATE_1) {
            // 课前状态,辅导老师在直播间就播放直播
            if (dwTeacherNotpresenBefore == null) {
                dwTeacherNotpresenBefore = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before);
            }
            rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenBefore);
        } else if (mode == ExperConfig.COURSE_STATE_2) {
            // 课中状态,播放回放视频
            if (isFirst) {
                isFirst = false;
                if (dwTeacherNotpresenBefore == null) {
                    dwTeacherNotpresenBefore = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before);
                }
                rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenBefore);
            } else {
                if (dwTeacherNotpresenDoing == null) {
                    dwTeacherNotpresenDoing = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before_doing);
                }
                rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenDoing);
            }
        } else if (mode == ExperConfig.COURSE_STATE_3) {
            // 课后状态,辅导老师在直播间就播放直播
            if (dwTeacherNotpresenAfter == null) {
                dwTeacherNotpresenAfter = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_after);
            }
            rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenAfter);
        } else if (mode == ExperConfig.COURSE_STATE_4) {
            // 结束状态
            ivTeacherNotpresent.setImageResource(R.drawable.live_free_play_end);
        } else {
            // 等待状态
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_open_late);
        }
        if (mode == ExperConfig.COURSE_STATE_2) {
            setFull();
        } else {
            setThreeFen();
        }
        if (mode == ExperConfig.COURSE_STATE_1 || mode == ExperConfig.COURSE_STATE_3) {
            if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                rlFirstBackgroundView.setVisibility(View.VISIBLE);
            }
            if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                ivTeacherNotpresent.setVisibility(View.GONE);
            }
        } else if (mode == ExperConfig.COURSE_STATE_2) {
            if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                rlFirstBackgroundView.setVisibility(View.VISIBLE);
            }
            if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                ivTeacherNotpresent.setVisibility(View.GONE);
            }
        } else {
            if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                rlFirstBackgroundView.setVisibility(View.GONE);
            }
            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
            }
        }
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONObject;

/**
 * @author chenkun
 *         老师点赞
 */
public class TeacherPraiseBll extends LiveBaseBll implements NoticeAction {

    private Activity mActivity;
    private LottieAnimationView animationView;
    private ViewGroup decorView;
    private View praiseRootView;
    private boolean isAnimStart;


    public TeacherPraiseBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mActivity = context;
    }

    /**
     * 显示 老师点赞
     */
    public void showTeacherPraise() {
        try {
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAnimStart) {
                            isAnimStart = true;
                            decorView = (ViewGroup) mActivity.getWindow().getDecorView();
                            praiseRootView = View.inflate(mActivity, R.layout.teacher_praise_layout, null);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            decorView.addView(praiseRootView, lp);
                            animationView = praiseRootView.findViewById(R.id.lav_teacher_priase);
                            startAnim();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            isAnimStart = false;
        }
    }

    private void startAnim() {

        if (animationView == null) {
            return;
        }

        String lottieResPath = "team_pk/pkresult/teacher_praise/images";
        String lottieJsonPath = "team_pk/pkresult/teacher_praise/data.json";

         // 兼容语文半身直播 老师点赞动画
        if (mGetInfo.getIsArts() == 2 || (mGetInfo.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY
                && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH)) {
            lottieResPath = "chinesePk/praise/images";
            lottieJsonPath = "chinesePk/praise/data.json";
        } else {
            lottieResPath = "team_pk/pkresult/teacher_praise/images";
            lottieJsonPath = "team_pk/pkresult/teacher_praise/data.json";
        }

        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mActivity));
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mActivity);
            }
        });
        animationView.playAnimation();
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
                if (studyReportAction != null) {
                    studyReportAction.cutImageAndVideo(LiveVideoConfig.STUDY_REPORT.TYPE_PRAISE, decorView, false, false);
                }
                closeTeacherPriase();
            }
        });

    }

    private void closeTeacherPriase() {
        isAnimStart = false;
        try {
            if (decorView != null && praiseRootView != null) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(praiseRootView);
                        decorView = null;
                        praiseRootView = null;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private int[] noticeCodes = {
            XESCODE.TEACHER_PRAISE
    };

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {

        switch (type) {
            case XESCODE.TEACHER_PRAISE:
                showTeacherPraise();
                break;

            default:
                break;
        }
    }


    @Override
    public void onDestory() {
        super.onDestory();
        closeTeacherPriase();
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }
}

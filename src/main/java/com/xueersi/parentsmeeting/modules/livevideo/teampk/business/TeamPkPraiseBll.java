package com.xueersi.parentsmeeting.modules.livevideo.teampk.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BezierEvaluator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 小理战队pk 二期  答题表扬，徽章 动效
 *
 * @author chekun
 * created  at 2019/2/15 13:25
 */
public class TeamPkPraiseBll {

    private Activity mActivity;
    private TeamPkBll mPkBll;
    private boolean isAnimStart;

    private ViewGroup decorView;
    private View praiseRootView;
    private LottieAnimationView animView;

    /**
     * lottie动效资源路径
     **/
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/teacher_praise/";
    private String mResPath;
    private String mJsonFilePath;
    private String mAnimScriptCacheKey;

    private static final int BADGE_LOW_BOUND = 0;
    private static final int BADGE_UP_BOUND = 10;
    /**
     * lottie 动画 脚本缓存key
     */
    private static final String LOTTIE_JSON_PRAISE = "teacherPraise";

    private static final String LOTTIE_JSON_BADGE = "badge";

    public TeamPkPraiseBll(Activity activity, TeamPkBll pkBll) {
        mActivity = activity;
        mPkBll = pkBll;
    }

    /**
     * @param sourceNick
     * @param target
     * @param data
     * @param type
     */
    public void onPraise(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.TEAM_PK_TEACHER_PRAISE:
                int praiseType = data.optInt("praiseType", 0);
                if (isMyTeam(data)) {
                    showBadge(praiseType);
                }
                break;
            case XESCODE.TEAM_PK_PARISE_ANWSER_RIGHT:
                showPraise();
                break;
            default:
                break;
        }
    }

    /**
     * 展示老师表扬动画
     **/
    private void showPraise() {
        mResPath = "team_pk/pkresult/teacher_praise/images";
        mJsonFilePath = "team_pk/pkresult/teacher_praise/data.json";
        mAnimScriptCacheKey = LOTTIE_JSON_PRAISE;
        addPraiseView();
    }

    /**
     * 是否是本队
     *
     * @param data
     * @return
     */
    private boolean isMyTeam(JSONObject data) {
        boolean result = false;
        String classTeamId = mPkBll.getRoomInitInfo().getStudentLiveInfo().getClassId() +"-"+mPkBll.getRoomInitInfo().getStudentLiveInfo().getTeamId();
        try {
            JSONArray jsonArray = data.getJSONArray("teamList");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (classTeamId.equals(jsonArray.getString(i))) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 显示徽章动效
     *
     * @param badgeType 徽章类型
     */
    private void showBadge(int badgeType) {
        if (badgeType > BADGE_LOW_BOUND && badgeType < BADGE_UP_BOUND) {
            mResPath = LOTTIE_RES_ASSETS_ROOTDIR + "badge_" + badgeType + "/images";
            mJsonFilePath = LOTTIE_RES_ASSETS_ROOTDIR + "badge_" + badgeType + "/data.json";
            mAnimScriptCacheKey = LOTTIE_JSON_BADGE;
            addPraiseView();
        }
    }

    private void addPraiseView() {
        try {
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAnimStart) {
                            isAnimStart = true;
                            decorView = (ViewGroup) mActivity.getWindow().getDecorView();
                            praiseRootView = View.inflate(mActivity, R.layout.teampk_teacher_praise_layout, null);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            decorView.addView(praiseRootView, lp);
                            animView = praiseRootView.findViewById(R.id.lav_teacher_priase);
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
        animView.useHardwareAcceleration(true);
        final LottieEffectInfo effectInfo = new LottieEffectInfo(mResPath, mJsonFilePath);
        animView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mActivity),mAnimScriptCacheKey);
        animView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mActivity);
            }
        });
        animView.playAnimation();
        animView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
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
}

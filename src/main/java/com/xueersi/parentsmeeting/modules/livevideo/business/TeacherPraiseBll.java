package com.xueersi.parentsmeeting.modules.livevideo.business;

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
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * @author chenkun
 * 老师点赞
 */
public class TeacherPraiseBll {
    /**
     * lottie 资源相对 asset 中的路径
     */
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/pkresult/";
    private Activity mActivity;
    private LottieAnimationView animationView;
    private ViewGroup decorView;
    private View praiseRootView;
    private boolean isAnimStart;

    public TeacherPraiseBll(Activity activity) {
        mActivity = activity;
    }

    /**
     * 显示 老师点赞
     */
    public void showTeacherPraise() {
        try {
            if (!isAnimStart) {
                isAnimStart = true;
                decorView = (ViewGroup) mActivity.getWindow().getDecorView();
                praiseRootView = View.inflate(mActivity, R.layout.teacher_praise_layout, null);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                decorView.addView(praiseRootView, lp);
                animationView = praiseRootView.findViewById(R.id.lav_teacher_priase);
                startAnim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isAnimStart = false;
        }
    }

    private void startAnim() {
        if (animationView != null) {
            String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "teacher_praise/images";
            String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "teacher_praise/data.json";
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
                    closeTeacherPriase();
                }
            });
        }
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

    public void onDestroy() {
        closeTeacherPriase();
    }

}

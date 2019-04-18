package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
*半身直播 主/辅 讲切流转场动画
*@author chekun
*created  at 2018/11/6 9:53
*/
public class HalfBodySceneTransAnim {

    private final Activity mContext;
    private LottieAnimationView animationView;
    private ViewGroup decorView;
    private boolean isAnimStart;
    /**
     * lottie 资源相对 asset 中的路径
     */
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "halfbody_live_translate";

    /**
     * lottie 文科 主/辅讲  切换动画
     */
    private static final String LOTTIE_RES_ASSETS_ROOTDIR_ARTS = "halfbody_live_translate_arts";
    /**直播间初始参数*/
    private final LiveGetInfo mGetInfo;


    public HalfBodySceneTransAnim(Activity context, LiveGetInfo liveInfo){
        mContext = context;
        mGetInfo = liveInfo;

    }
    /**
     * 主辅讲状态切换
     * @param mode
     * @param isPresent 老师是否在直播间
     */
    public void onModeChange(String mode,boolean isPresent){
        showAnim();
    }

    /**
     * 展示lottie 转场动画
     */
    private void showAnim() {
        try {
            if (mContext != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAnimStart) {
                            isAnimStart = true;
                            decorView = (ViewGroup) mContext.getWindow().getDecorView();
                            animationView = new LottieAnimationView(mContext);
                            animationView.useHardwareAcceleration(true);
                            animationView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            decorView.addView(animationView, lp);
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
        if (animationView != null) {
            String lottieResPath = null;
            String lottieJsonPath = null;
            if(mGetInfo != null && mGetInfo.getIsArts() == HalfBodyLiveConfig.LIVE_TYPE_CHINESE){
                lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR_ARTS  +"/images";
                lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR_ARTS +"/data.json";
            }else{
                 lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
                 lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
            }
            final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
            animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
            animationView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return effectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                            mContext);
                }
            });
            animationView.playAnimation();
            animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    closeAnim();
                }
            });
        }
    }

    private void closeAnim() {
        isAnimStart = false;
        try {
            if (decorView != null && animationView != null) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(animationView);
                        decorView = null;
                        animationView = null;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相关资源
     */
    public void release(){
        closeAnim();
    }



}

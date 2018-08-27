package com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

import org.json.JSONObject;

/**
 * @author chenkun
 * 老师点赞
 */
public class TeacherPraiseBll extends LiveBaseBll implements NoticeAction {
    /**
     * lottie 资源相对 asset 中的路径
     */
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/pkresult/";
    private Activity mActivity;
    private LottieAnimationView animationView;
    private ViewGroup decorView;
    private View praiseRootView;
    private boolean isAnimStart;
    private boolean isPse;
    private final float SCALE_ANIM_FACTOR = 0.40f;
    /**文科表扬UI 展示时间*/
    private final long PARISE_UI_DISPLAY_DURATION = 4*1000;

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
                if(mCloseTask != null){
                    praiseRootView.removeCallbacks(mCloseTask);
                    mCloseTask = null;
                }
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


    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        isPse = getInfo != null && getInfo.getSmallEnglish();
    }

    private int[] noticeCodes = {
            XESCODE.TEACHER_PRAISE,
            XESCODE.ARTS_TEACHER_PRAISE,
            130
    };

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {

        switch (type) {
            case XESCODE.TEACHER_PRAISE:
                showTeacherPraise();
                String nonce = data.optString("nonce", "");
                TeamPkLog.receiveVoicePraise(mLiveBll, nonce);
                break;
            case XESCODE.ARTS_TEACHER_PRAISE:
                // TODO: 2018/8/24  展示文科答题表扬
                showArtsTeacherPraise();
                break;
            case 130:
                String commondStr = data.optString("msg");
                if("3".equals(commondStr)){
                     showArtsTeacherPraise();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 展示文科答题 老师表扬
     */
    private void showArtsTeacherPraise() {
        try {
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAnimStart) {
                            isAnimStart = true;
                            decorView = (ViewGroup) mActivity.getWindow().getDecorView();
                            int layoutId = isPse?R.layout.arts_pseteacher_praise_layout:R.layout.arts_teacher_praise_layout;
                            praiseRootView = View.inflate(mActivity, layoutId, null);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            decorView.addView(praiseRootView, lp);
                            View targetView = praiseRootView.findViewById(R.id.iv_arts_pse_teacher_praise);
                            palyAnim(targetView);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            isAnimStart = false;
        }
    }

    private Runnable mCloseTask;
    private void palyAnim(View targetView) {
        ScaleAnimation animation = (ScaleAnimation) AnimationUtils.loadAnimation(mContext,R.anim.anim_live_artsteahcer__praise);
        animation.setInterpolator(new SpringScaleInterpolator(SCALE_ANIM_FACTOR));
        targetView.startAnimation(animation);
        mCloseTask = new Runnable() {
            @Override
            public void run() {
                closeTeacherPriase();
            }
        };
        praiseRootView.postDelayed(mCloseTask,PARISE_UI_DISPLAY_DURATION);
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

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
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BezierEvaluator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;

import org.json.JSONObject;

/**
 * 小理战队pk 二期  答题表扬，徽章 动效
 *
 * @author chekun
 * created  at 2019/2/15 13:25
 */
public class TeamPkPraiseBll {

    private final Activity mActivtiy;
    private final TeamPkBll mPkBll;
    private boolean isAnimStart;

    private ViewGroup decorView;
    private View praiseRootView;

    private LottieAnimationView anwserAnimView;
    private TextView tvEnergy;
    private ImageView ivEnergy;

    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/teacher_praise/";

    private int mEnergy;
    /**
     * 能量动画开始时间点
     */
    private static final float ENERGY_ANIM_ENTER_FRACTION = 0.45f;
    private SoundPoolHelper soundPoolHelper;

    public TeamPkPraiseBll(Activity activity, TeamPkBll pkBll) {
        mActivtiy = activity;
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
            case XESCODE.TEAM_PK_PARISE_ANWSER_RIGHT:
                String strCmd = data.optString("msg");
                if (strCmd.equals("1")) {
                    showAnswerRightAnim(10);
                } else if (strCmd.equals("2")) {
                    showBadge(1);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示徽章动效
     *
     * @param badgeType 徽章类型
     */
    private void showBadge(int badgeType) {


    }


    /**
     * 展示 超难题答对 动画
     *
     * @param energy
     */
    private void showAnswerRightAnim(int energy) {
        mEnergy = energy;
        try {
            if (mActivtiy != null) {
                mActivtiy.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAnimStart) {
                            isAnimStart = true;
                            decorView = (ViewGroup) mActivtiy.getWindow().getDecorView();
                            praiseRootView = View.inflate(mActivtiy, R.layout.page_livevideo_teampk_teacher_praise, null);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            decorView.addView(praiseRootView, lp);
                            anwserAnimView = praiseRootView.findViewById(R.id.lav_teampk_praise_anwser_right);
                            ivEnergy = praiseRootView.findViewById(R.id.iv_teampk_praise_energy);
                            tvEnergy = praiseRootView.findViewById(R.id.tv_teampk_praise_energy);
                            playAnwserRightAnim();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            isAnimStart = false;
        }
    }


    private boolean energyAnimRuning = false;
    private TeamPkStateLayout teamPKStateLayout;

    private void playAnwserRightAnim() {
        if (anwserAnimView != null) {
            String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "anwser_right/images";
            String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "anwser_right/data.json";

            final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
            anwserAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mActivtiy));
            anwserAnimView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return effectInfo.fetchBitmapFromAssets(anwserAnimView, lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                            mActivtiy);
                }
            });
            anwserAnimView.playAnimation();
            anwserAnimView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Log.e("PraiseBll", "======>onAnimationUpdate:" + animation.getAnimatedFraction());
                    if (animation.getAnimatedFraction() > ENERGY_ANIM_ENTER_FRACTION && !energyAnimRuning) {
                        energyAnimRuning = true;
                       // playEnergyEnterAnim();
                    }
                }
            });
        }
    }

    /**
     * 能量入场动画
     *
     * @param energy
     */
    private void playEnergyEnterAnim() {
        ivEnergy.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mActivtiy, R.anim.anim_livevido_teampk_aq_award);
        scaleAnimation.setDuration(500);
        ivEnergy.startAnimation(scaleAnimation);

        tvEnergy.setVisibility(View.VISIBLE);
        AnimationSet animationSet = (AnimationSet) AnimationUtils.
                loadAnimation(mActivtiy, R.anim.anim_livevideo_teampk_energy_in);

        tvEnergy.startAnimation(animationSet);
        tvEnergy.setText("+" + mEnergy);
        tvEnergy.startAnimation(animationSet);
        tvEnergy.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 能量图标动画
                if(mEnergy > 0){
                    teamPKStateLayout = decorView.findViewById(R.id.tpkL_teampk_pkstate_root);
                    if (teamPKStateLayout != null) {
                        TeamPkProgressBar pkProgressBar = teamPKStateLayout.findViewById(R.id.tpb_teampk_pkstate_energy_bar);
                        Rect endRect = pkProgressBar.getSliderDrawRect();
                        if (endRect != null) {
                            playFlayAnim(ivEnergy, endRect);
                        }
                    }else{
                        closePraiseView();
                    }
                }else{
                    closePraiseView();
                }
            }
        }, 1000);

    }


    /**
     * 飞行动画时间
     */
    private final int FLY_ANIM_DURATION = 700;
    private int controlOffsetX;
    private int controlOffsetY;

    /**
     * @param anchorView
     * @param targetRect 目标view 的绘制区域
     */
    private void playFlayAnim(ImageView anchorView, Rect targetRect) {

        // 把view 添加到 docerview中
        final ImageView flyView = new ImageView(mActivtiy);
        flyView.setImageDrawable(anchorView.getDrawable());
        flyView.setScaleType(ImageView.ScaleType.FIT_XY);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(anchorView.getLayoutParams()
                .width, anchorView.getLayoutParams().height);
        decorView.addView(flyView, lp);

        int[] des = new int[2];
        des[0] = targetRect.left;
        des[1] = targetRect.top;

        int[] startPosition = new int[2];
        anchorView.getLocationInWindow(startPosition);
        Point startPoint = new Point(startPosition[0], startPosition[1]);
        int offsetX = 0;
        int offsetY = 0;
        offsetX = (targetRect.width() - flyView.getLayoutParams().width) / 2;
        offsetY = (targetRect.height() - flyView.getLayoutParams().height) / 2;
        final float endScale = targetRect.width() / (float) flyView.getLayoutParams().width;

        Point endPoint = new Point(des[0] + offsetX, des[1] + offsetY);

        int controlX = (startPoint.x + endPoint.x) / 2 - controlOffsetX;
        int controlY = endPoint.y - controlOffsetY;
        Point controlPoint = new Point(controlX, controlY);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BezierEvaluator(controlPoint)
                , startPoint, endPoint);
        valueAnimator.setDuration(FLY_ANIM_DURATION);
        valueAnimator.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point) animation.getAnimatedValue();
                flyView.setX(point.x);
                flyView.setY(point.y);
                float scale = 1 - (1 - endScale) * animation.getAnimatedFraction();
                flyView.setScaleX(scale);
                flyView.setScaleY(scale);
            }
        });


        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                doAnimEnd();
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(flyView);
                    }
                });
            }
        });
    }

    boolean animEndCalled = false;
    /**
     * 默认音量大小
     */
    private static final float DEFAULT_VOLUME = 0.8f;
    private void doAnimEnd() {
        if (!animEndCalled) {
            animEndCalled = true;
            // 0 播发音效
            playMusic(R.raw.coin_get, DEFAULT_VOLUME, false);
            // 1 聊天区域状态更新
            if (teamPKStateLayout != null && mPkBll != null) {
                teamPKStateLayout.updateData(mEnergy, 0, 0);
                teamPKStateLayout.showEnergyMyContribute(mEnergy);
                // TODO: 2019/2/17  调用添加能量接口 ，并刷新pk状态栏
            }
            closePraiseView();
        }
    }
    private void playMusic(int resId, float volume, boolean loop) {
        if (soundPoolHelper == null) {
            soundPoolHelper = new SoundPoolHelper(mActivtiy, 1, AudioManager.STREAM_MUSIC);
        }
        soundPoolHelper.playMusic(resId, volume, loop);
    }


    private void closePraiseView() {
        isAnimStart = false;
        energyAnimRuning = false;
        tvEnergy = null;
        ivEnergy = null;
        anwserAnimView = null;
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

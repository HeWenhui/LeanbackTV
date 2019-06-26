package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BezierEvaluator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;


/**
 * 战队pk 实时答题
 *
 * @author chekun
 * created  at 2018/4/17 16:26
 */
public class TeamPkAqResultPager extends TeamPkBasePager {

    private RelativeLayout rlQuestionRootView;
    private ImageView ivEnergy;
    private ImageView ivCoin;
    private TextView tvEnergy;
    private TextView tvCoin;
    private TeamPkStateLayout teamPKStateLayout;
    private ViewGroup decorView;
    private TeamPkProgressBar pkProgressBar;
    /**
     * 飞行动画时间
     */
    private final int FLY_ANIM_DURATION = 700;
    private int controlOffsetX;
    private int controlOffsetY;
    private ScaleAnimation scaleAnimation;

    /**
     * 默认音量大小
     */
    private static final float DEFAULT_VOLUME = 0.8f;
    private int mGoldNum;
    private int mEnergy;
    /**
     * 投票奖励
     */
    public static final int AWARD_TYPE_VOTE = 1;
    /**
     * 答题奖励
     */
    public static final int AWARD_TYPE_QUESTION = 2;
    /**
     * 集体发言奖励
     */
    public static final int AWARD_TYPE_SPEECH = 3;


    /**
     * 奖励类型
     */
    int awardType;
    private RelativeLayout rlVotRootView;
    private TextView tvVoteEnergy;
    private ImageView ivVoteEnergy;
    private final TeamPkBll mTeamPkBll;
    private SoundPoolHelper soundPoolHelper;

    /**
     * 飞星动画 控制点坐标  单位dp
     */
    private static final int CONTROL_X_DP = 70;
    private static final int CONTROL_Y_DP = 120;

    /**
     * 缩放动画弹性系数
     */
    private static final float SCALE_ANIM_FACTOR = 0.23f;

    public TeamPkAqResultPager(Context context, int type, TeamPkBll teamPKBll) {
        super(context);
        this.awardType = type;
        mTeamPkBll = teamPKBll;
    }

    @Override
    public View initView() {

        controlOffsetX = SizeUtils.Dp2Px(mContext, CONTROL_X_DP);
        controlOffsetY = SizeUtils.Dp2Px(mContext, CONTROL_Y_DP);

        final View view = View.inflate(mContext, R.layout.page_livevideo_teampk_aq_result, null);
        rlQuestionRootView = view.findViewById(R.id.rl_answer_question_award_root);
        ivEnergy = view.findViewById(R.id.iv_answer_question_energy);
        ivCoin = view.findViewById(R.id.iv_answer_question_coin);
        tvEnergy = view.findViewById(R.id.tv_answer_question_energy);
        tvCoin = view.findViewById(R.id.tv_answer_question_coin);

        rlVotRootView = view.findViewById(R.id.rl_vote_award_root);
        tvVoteEnergy = view.findViewById(R.id.tv_vote_award_energy);
        ivVoteEnergy = view.findViewById(R.id.iv_vote_award_energy);


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mView.getMeasuredWidth() > 0) {
                    try {
                        //延迟200 解决播放动画时卡顿问题
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (awardType == AWARD_TYPE_QUESTION) {
                                    showQuestionAwardAnim();
                                } else if (awardType == AWARD_TYPE_VOTE) {
                                    showVoteAwardAnim();
                                } else if (awardType == AWARD_TYPE_SPEECH) {
                                    showVoteAwardAnim();
                                }
                            }
                        }, 200);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });

        return view;
    }


    /**
     * 设置数据
     *
     * @param goldNum
     * @param energy
     */
    public void setData(int goldNum, int energy) {
        mGoldNum = goldNum < 0 ? 0 : goldNum;
        mEnergy = energy < 0 ? 0 : energy;
    }


    private void playMusic(int resId, float volume, boolean loop) {
        if (soundPoolHelper == null) {
            soundPoolHelper = new SoundPoolHelper(mContext, 1, AudioManager.STREAM_MUSIC);
        }
        soundPoolHelper.playMusic(resId, volume, loop);
    }


    /**
     * 展示  答题奖励动画
     */
    private void showQuestionAwardAnim() {
        tvCoin.setText("+" + mGoldNum);
        tvEnergy.setText("+" + mEnergy);

        scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_aq_award);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(SCALE_ANIM_FACTOR));
        rlVotRootView.setVisibility(View.GONE);
        rlQuestionRootView.setVisibility(View.VISIBLE);
        rlQuestionRootView.startAnimation(scaleAnimation);

        //能量不在飞
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlQuestionRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startQuestionAwardFlyAnim();
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void showVoteAwardAnim() {
        tvVoteEnergy.setText("+" + mEnergy);
        scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_aq_award);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(SCALE_ANIM_FACTOR));
        rlQuestionRootView.setVisibility(View.INVISIBLE);
        rlVotRootView.setVisibility(View.VISIBLE);
        rlVotRootView.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlVotRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startVoteAwardFlyAnim();
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    @Override
    public void initData() {

    }

    /**
     * 执行飞星 动画
     */
    private void startQuestionAwardFlyAnim() {

        if (mGoldNum > 0 || mEnergy > 0) {
            decorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
            teamPKStateLayout = decorView.findViewById(R.id.tpkL_teampk_pkstate_root);
            if (teamPKStateLayout == null) {
                return;
            }
            // 能量图标动画
            pkProgressBar = teamPKStateLayout.findViewById(R.id.tpb_teampk_pkstate_energy_bar);
            Rect endRect = pkProgressBar.getSliderDrawRect();
            if (endRect != null && mEnergy > 0) {
                playFlayAnim(ivEnergy, endRect);
            }
            //金币图标动画
            ImageView ivTargetCoin = teamPKStateLayout.findViewById(R.id.iv_teampk_pkstate_coin);
            Rect coinEndRect = new Rect();
            int[] location = new int[2];
            ivTargetCoin.getLocationInWindow(location);
            coinEndRect.left = location[0];
            coinEndRect.top = location[1];
            coinEndRect.right = coinEndRect.left + ivTargetCoin.getLayoutParams().width;
            coinEndRect.bottom = coinEndRect.top + ivTargetCoin.getLayoutParams().height;
            if (mGoldNum > 0) {
                playFlayAnim(ivCoin, coinEndRect);
            }
        } else {
            closePager();
        }
    }

    /**
     * 投票 能量飞行 动画
     */
    private void startVoteAwardFlyAnim() {
        if (mEnergy > 0) {
            decorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
            teamPKStateLayout = decorView.findViewById(R.id.tpkL_teampk_pkstate_root);
            if (teamPKStateLayout != null) {
                // 能量图标动画
                pkProgressBar = teamPKStateLayout.findViewById(R.id.tpb_teampk_pkstate_energy_bar);
                Rect endRect = pkProgressBar.getSliderDrawRect();
                if (endRect != null) {
                    playFlayAnim(ivVoteEnergy, endRect);
                } else {
                    mTeamPkBll.updatePkStateLayout(true);
                    closePager();
                }
            } else {
                mTeamPkBll.updatePkStateLayout(true);
                closePager();
            }
        } else {
            mTeamPkBll.updatePkStateLayout(true);
            closePager();
        }
    }


    /**
     * @param anchorView
     * @param targetRect 目标view 的绘制区域
     */
    private void playFlayAnim(ImageView anchorView, Rect targetRect) {

        // 把view 添加到 docerview中
        final ImageView flyView = new ImageView(mContext);
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

    private void doAnimEnd() {
        if (!animEndCalled) {
            animEndCalled = true;
            // 0 播发音效
            playMusic(R.raw.coin_get, DEFAULT_VOLUME, false);
            // 1 聊天区域状态更新
            if (teamPKStateLayout != null && mTeamPkBll != null) {
                teamPKStateLayout.updateData(mEnergy, 0, mGoldNum);
                teamPKStateLayout.showEnergyMyContribute(mEnergy);
                //投票题 动画结束刷新右侧总能量
                if (awardType == AWARD_TYPE_VOTE) {
                    mTeamPkBll.updatePkStateLayout(true);
                }

            }
            closePager();
        }

    }

    private void closePager() {
        try {
            if (mView.getParent() != null) {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRes();
    }

    /**
     * 清除资源
     */
    private void releaseRes() {
        if (soundPoolHelper != null) {
            soundPoolHelper.release();
        }
    }

}

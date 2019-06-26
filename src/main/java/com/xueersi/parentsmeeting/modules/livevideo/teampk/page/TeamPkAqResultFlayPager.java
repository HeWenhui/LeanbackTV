package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BezierEvaluator;

/**
 * 战队pk 实时答题
 *
 * @author chekun
 * created  at 2018/4/17 16:26
 */
public class TeamPkAqResultFlayPager extends TeamPkBasePager {

    private RelativeLayout rlQuestionRootView;
    private ImageView ivEnergy;
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
    //    private RelativeLayout rlVotRootView;
//    private TextView tvVoteEnergy;
//    private ImageView ivVoteEnergy;
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
    private int[] startPosition;

    public TeamPkAqResultFlayPager(Context context, int type, TeamPkBll teamPKBll, int[] startPosition) {
        super(context);
        this.awardType = type;
        mTeamPkBll = teamPKBll;
        this.startPosition = startPosition;
    }

    @Override
    public View initView() {

        controlOffsetX = SizeUtils.Dp2Px(mContext, CONTROL_X_DP);
        controlOffsetY = SizeUtils.Dp2Px(mContext, CONTROL_Y_DP);

        final View view = View.inflate(mContext, R.layout.page_livevideo_teampk_aq_result_flay, null);
        rlQuestionRootView = view.findViewById(R.id.rl_answer_question_award_root);
        ivEnergy = view.findViewById(R.id.iv_answer_question_energy);

//        rlVotRootView = view.findViewById(R.id.rl_vote_award_root);
//        tvVoteEnergy = view.findViewById(R.id.tv_vote_award_energy);
//        ivVoteEnergy = view.findViewById(R.id.iv_vote_award_energy);


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mView.getMeasuredWidth() > 0) {
                    try {
                        //延迟200 解决播放动画时卡顿问题
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startVoteAwardFlyAnim();
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


    @Override
    public void initData() {

    }

    /**
     * 投票 能量飞行 动画
     */
    private void startVoteAwardFlyAnim() {
        if (mEnergy > 0) {
            decorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
            teamPKStateLayout = decorView.findViewById(R.id.tpkL_teampk_pkstate_root);
            if (teamPKStateLayout == null) {
                return;
            }
            pkProgressBar = teamPKStateLayout.findViewById(R.id.tpb_teampk_pkstate_energy_bar);
            Rect endRect = pkProgressBar.getSliderDrawRect();
            if (endRect != null) {
                playFlayAnim(ivEnergy, endRect);
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
        int[] des = new int[2];
        des[0] = targetRect.left;
        des[1] = targetRect.top;
//        startPosition[0] = startPosition[0] - ivEnergy.getWidth() / 2;
//        startPosition[1] = startPosition[1] - ivEnergy.getHeight() / 2;
        ivEnergy.getLocationOnScreen(startPosition);
        Point startPoint = new Point(startPosition[0], startPosition[1]);
        int offsetX = (targetRect.width() - ivEnergy.getLayoutParams().width) / 2;
        int offsetY = (targetRect.height() - ivEnergy.getLayoutParams().height) / 2;
        final float endScale = targetRect.width() / (float) ivEnergy.getLayoutParams().width;

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
                ivEnergy.setX(point.x);
                ivEnergy.setY(point.y);
                float scale = 1 - (1 - endScale) * animation.getAnimatedFraction();
                ivEnergy.setScaleX(scale);
                ivEnergy.setScaleY(scale);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                doAnimEnd();
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(ivEnergy);
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

    @Override
    public boolean isFullScreenMode() {
        return true;
    }
}

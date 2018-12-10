package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;

/**
 * 战队pk  右侧状态栏
 *
 * @author chekun
 *         created  at 2018/4/16 18:38
 */
public class TeamPkStateLayout extends FrameLayout {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private TeamPkProgressBar pkProgressBar;
    private SmoothAddNumTextView tvMyTeamEnergy;
    private SmoothAddNumTextView tvOtherTeamEnergy;
    private SmoothAddNumTextView tvCoin;

    private long mMyTeamEnergy;
    private long mOtherTeamEnergy;
    private long mCoinNum;
    /**
     * 当前pk状态
     */
    private TextView tvState;
    private View statBarRootView;

    /**
     * 是否显示顶部 pk状态
     */
    private boolean showPopWindow;

    /**
     * 顶部pk状态栏 (打成平手 ，全力追赶...)高度
     */
    private static final int STATE_BAR_HEIGHT = 17;
    /**
     * 状态兰底部 margin 值
     */
    private static final int STATE_BAR_BOTTOM_MARGIN = 6;

    /**
     * pk状态栏显示时间
     */
    private static final long PK_STATE_DISPLAY_DURATION = 30 * 1000;

    /**
     * 我贡献了多少能量显示时间
     */
    private static final long ENERGY_MY_CONTRIBUTION_DURATION = 4 * 1000;

    private boolean dataInited = false;
    private TextView tvEnergyMyContribution;
    private TeamPkBll mTeamPkBll;

    public TeamPkStateLayout(@NonNull Context context) {
        super(context);
        initView();
    }


    public TeamPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TeamPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        if (LiveVideoConfig.isPrimary) {
            LayoutInflater.from(getContext()).inflate(R.layout.team_pspk_state_layout, this);
        } else if (LiveVideoConfig.isSmallChinese) {
            LayoutInflater.from(getContext()).inflate(R.layout.chinese_pk_state_layout, this);
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.team_pk_state_layout, this);
        }
        pkProgressBar = findViewById(R.id.tpb_teampk_pkstate_energy_bar);
        tvMyTeamEnergy = findViewById(R.id.tv_teampk_pkstate_myteam_energy);
        tvOtherTeamEnergy = findViewById(R.id.tv_teampk_pkstate_otherteam_energy);
        tvCoin = findViewById(R.id.tv_teampk_pkstate_coin_num);
        pkProgressBar.setMaxProgress(100);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //logger.e( "===========>:onGlobalLayout"+TeamPkStateLayout.this.getMeasuredWidth());
                if (TeamPkStateLayout.this.getMeasuredWidth() > 0) {
                    try {
                        addPkStatBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        TeamPkStateLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        TeamPkStateLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }


    /**
     * 添加视屏区域 pk 状态UI
     */
    private void addPkStatBar() {
        statBarRootView = View.inflate(getContext(), R.layout.team_pk_state_bar_layout, null);
        ViewGroup viewGroup = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
        ViewGroup rootView = viewGroup.findViewById(R.id.rl_livevideo_message_root);
        if (rootView != null) {
            int stateBarHeight = SizeUtils.Dp2Px(getContext(), STATE_BAR_HEIGHT);
            int gapAbovePkStateLayout = SizeUtils.Dp2Px(getContext(), STATE_BAR_BOTTOM_MARGIN);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(this.getMeasuredWidth(), stateBarHeight);
            int[] location = new int[2];
            this.getLocationInWindow(location);
            lp.topMargin = location[1] - (gapAbovePkStateLayout + stateBarHeight);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootView.addView(statBarRootView, lp);
            tvState = statBarRootView.findViewById(R.id.tv_answer_question_state);
            tvState.setVisibility(GONE);
            tvEnergyMyContribution = statBarRootView.findViewById(R.id.tv_teampk_pkstate_energy_mycontribution);
            tvEnergyMyContribution.setVisibility(GONE);

        }
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        //关联显示状态
        if (statBarRootView != null) {
            statBarRootView.setVisibility(visibility);
        }
    }

    /**
     * 刷新 能量，金币
     *
     * @param ownEnergyAdd   本队新增能量值
     * @param coinAdd        本地增加金币值
     * @param otherEnergyAdd 对手增加能量值
     */
    public void updateData(int ownEnergyAdd, int otherEnergyAdd, int coinAdd) {
        mMyTeamEnergy = mMyTeamEnergy + ownEnergyAdd;
        mOtherTeamEnergy = mOtherTeamEnergy + otherEnergyAdd;
        mCoinNum = mCoinNum + coinAdd;
        logger.e("====>updateData22222:" + mMyTeamEnergy + ":" + mOtherTeamEnergy + ":" + mCoinNum);
        if (mTeamPkBll != null && coinAdd > 0) {
            TeamPkLog.showMyGold(mTeamPkBll.getLiveBll(), mCoinNum + "");
        }
        //正 增长 显示动画 ，负增涨 不显示动画
        if (ownEnergyAdd > 0) {
            tvMyTeamEnergy.smoothAddNum(ownEnergyAdd);
        } else {
            if (tvMyTeamEnergy.isAnimRunning()) {
                tvMyTeamEnergy.cancelAnim();
            }
            tvMyTeamEnergy.setText(mMyTeamEnergy + "");
        }
        if (otherEnergyAdd > 0) {
            tvOtherTeamEnergy.smoothAddNum(otherEnergyAdd);
        } else {
            if (tvOtherTeamEnergy.isAnimRunning()) {
                tvOtherTeamEnergy.cancelAnim();
            }
            tvOtherTeamEnergy.setText(mOtherTeamEnergy + "");
        }
        if (coinAdd > 0) {
            tvCoin.smoothAddNum(coinAdd);
        } else {
            if (tvCoin.isAnimRunning()) {
                tvCoin.cancelAnim();
            }
            tvCoin.setText(mCoinNum + "");
        }
        float ratio;
        if ((mMyTeamEnergy + mOtherTeamEnergy) > 0) {
            ratio = mMyTeamEnergy / (float) (mMyTeamEnergy + mOtherTeamEnergy);
        } else {
            ratio = 0.5f;
        }
        upDataSateText(ratio);
        int addProgress = (int) (ratio * 100 + 0.5f) - pkProgressBar.getProgress();
        if (addProgress > 0) {
            pkProgressBar.smoothAddProgress(addProgress);
            logger.e("====>updateData smoothAddProgress:" + addProgress + ":" + pkProgressBar.getProgress());

        } else {
           /* if (pkProgressBar.isAnimRunning()) {
                pkProgressBar.cancel();
            }*/
            pkProgressBar.setProgress((int) (ratio * 100));
            logger.e("====>updateData setProgress:" + (int) (ratio * 100));
        }
    }


    /**
     * 绑定数据
     *
     * @param coinNum         当前战队 金币总数
     * @param myTeamEnergy    当前战队 能量值
     * @param otherTeamEnergy 当前对手能量值
     * @param showPopWindow   是否显示顶部进度状态
     */
    public void bindData(long coinNum, long myTeamEnergy, long otherTeamEnergy, boolean showPopWindow) {
        logger.e("====> PkstateLayout bindData 111:" + coinNum + ":" + myTeamEnergy + ":" + otherTeamEnergy);
        logger.e("====> PkstateLayout bindData 333:" + mCoinNum + ":" + mMyTeamEnergy + ":" +
                mOtherTeamEnergy);
        this.showPopWindow = showPopWindow;
        if (!dataInited) {
            initData(coinNum, myTeamEnergy, otherTeamEnergy);
            dataInited = true;
        } else {
            int addCoin = (int) (coinNum - mCoinNum);
            int ownEnergyAdd = (int) (myTeamEnergy - mMyTeamEnergy);
            int otherEnergyAdd = (int) (otherTeamEnergy - mOtherTeamEnergy);
            logger.e("====> PkstateLayout bindData 222:" + addCoin + ":" + ownEnergyAdd + ":" +
                    otherEnergyAdd);
            updateData(ownEnergyAdd, otherEnergyAdd, addCoin);
        }
    }

    private void initData(long coinNum, long myTeamAnergy, long otherTeamAnergy) {
        this.mCoinNum = coinNum;
        this.mMyTeamEnergy = myTeamAnergy;
        this.mOtherTeamEnergy = otherTeamAnergy;
        tvCoin.setText(mCoinNum + "");
        tvMyTeamEnergy.setText(mMyTeamEnergy + "");
        tvOtherTeamEnergy.setText(otherTeamAnergy + "");

        if (mTeamPkBll != null && mCoinNum > 0) {
            TeamPkLog.showMyGold(mTeamPkBll.getLiveBll(), mCoinNum + "");
        }

        float ratio;
        if ((mMyTeamEnergy + mOtherTeamEnergy) > 0) {
            ratio = mMyTeamEnergy / (float) (mMyTeamEnergy + mOtherTeamEnergy);
        } else {
            ratio = 0.5f;
        }
        upDataSateText(ratio);
        int currentProgress = (int) (ratio * 100);
        pkProgressBar.setProgress(currentProgress);
    }

    private static final float HALF_PROGRESS = 0.5f;

    private void upDataSateText(float ratio) {
        if (this.showPopWindow) {
            this.showPopWindow = false;
            if (ratio > HALF_PROGRESS) {
                tvState.setText("暂时领先");
                tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_lead_bg);
            } else if (ratio < HALF_PROGRESS) {
                tvState.setText("全力追赶");
                tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_follow_bg);
            } else if (ratio == HALF_PROGRESS) {
                tvState.setText("打成平手");
                tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_lead_bg);
            }
            showPkSateBar();
        }

    }


    /**
     * 显示 准备战斗提示
     */
    public void showPkReady() {
        tvState.setText("准备战斗");
        tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_ready_bg);
        showPkSateBar();
    }


    /**
     * 淡入 淡出展示  当前pk 状态
     */
    private void showPkSateBar() {
        showViewWithFadeInOutEffect(tvState, PK_STATE_DISPLAY_DURATION);
    }


    /**
     * 淡入淡出 显示我贡献的能量值
     *
     * @param energy
     */
    public void showEnergyMyContribute(int energy) {
        if (tvEnergyMyContribution != null) {
            tvEnergyMyContribution.setVisibility(VISIBLE);
            energy = energy < 0 ? 0 : energy;
            tvEnergyMyContribution.setText("我贡献了" + energy + "个能量");
            showViewWithFadeInOutEffect(tvEnergyMyContribution, ENERGY_MY_CONTRIBUTION_DURATION);
        }
    }


    /**
     * 淡入淡出显示  控件
     *
     * @param targetView
     * @param duratrion
     */
    private void showViewWithFadeInOutEffect(final View targetView, long duratrion) {
        if (targetView == null) {
            return;
        }
        targetView.setVisibility(VISIBLE);
        AlphaAnimation alphaIn = (AlphaAnimation) AnimationUtils.
                loadAnimation(getContext(), R.anim.anim_livevido_teampk_pkstate_in);
        targetView.startAnimation(alphaIn);
        targetView.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation alphaOut = (AlphaAnimation) AnimationUtils.
                        loadAnimation(getContext(), R.anim.anim_livevido_teampk_pkstate_out);
                alphaOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        targetView.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                targetView.startAnimation(alphaOut);
            }
        }, duratrion);
    }


    public void setTeamPkBll(TeamPkBll teamPkBll) {
        mTeamPkBll = teamPkBll;
    }


}

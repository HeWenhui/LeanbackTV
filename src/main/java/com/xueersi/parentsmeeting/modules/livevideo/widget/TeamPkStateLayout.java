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
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.TeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

/**
 * 战队pk  右侧状态栏
 *
 * @author chekun
 * created  at 2018/4/16 18:38
 */
public class TeamPkStateLayout extends FrameLayout {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    protected TeamPkProgressBar pkProgressBar;
    protected SmoothAddNumTextView tvMyTeamEnergy;
    protected SmoothAddNumTextView tvOtherTeamEnergy;
    protected SmoothAddNumTextView tvCoin;

    protected long mMyTeamEnergy;
    protected long mOtherTeamEnergy;
    protected long mCoinNum;
    /**
     * 当前pk状态
     */
    protected TextView tvState;
    protected View statBarRootView;

    /**
     * 是否显示顶部 pk状态
     */
    protected boolean showPopWindow;

    /**
     * 顶部pk状态栏 (打成平手 ，全力追赶...)高度
     */
    protected static final int STATE_BAR_HEIGHT = 17;
    /**
     * 状态兰底部 margin 值
     */
    protected static final int STATE_BAR_BOTTOM_MARGIN = 6;

    /**
     * pk状态栏显示时间
     */
    protected static final long PK_STATE_DISPLAY_DURATION = 30 * 1000;

    /**
     * 我贡献了多少能量显示时间
     */
    protected static final long ENERGY_MY_CONTRIBUTION_DURATION = 4 * 1000;

    private ContextLiveAndBackDebug liveAndBackDebug;
    private boolean dataInited = false;
    protected TextView tvEnergyMyContribution;
//    protected TeamPkBll liveAndBackDebug;

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

    protected void initView() {
        liveAndBackDebug = new ContextLiveAndBackDebug(getContext());
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
                        //语文沿用之前老样式
                        if(LiveVideoConfig.isSmallChinese){
                            addPkStatBar();
                        }else{
                            addNewPkStatBar();
                        }
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
    protected void addPkStatBar() {
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
            int rightMargin = (LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x4);
            lp.rightMargin = rightMargin > 0 ? rightMargin : 0;
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootView.addView(statBarRootView, lp);
            tvState = statBarRootView.findViewById(R.id.tv_answer_question_state);
            tvState.setVisibility(GONE);
            tvEnergyMyContribution = statBarRootView.findViewById(R.id.tv_teampk_pkstate_energy_mycontribution);
            tvEnergyMyContribution.setVisibility(GONE);
            //监听布局变化设置边距
            statBarRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int rightMargin = (LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x4);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) statBarRootView.getLayoutParams();
                    if (lp.rightMargin != rightMargin) {
                        lp.rightMargin = rightMargin;
                        LayoutParamsUtil.setViewLayoutParams(statBarRootView, lp);
                    }
                }
            });
        }
    }


    /**
     * 理科pk二期新状态栏
     */
    private void addNewPkStatBar() {
        tvState = findViewById(R.id.tv_answer_question_state);
        tvState.setVisibility(GONE);
        statBarRootView = View.inflate(getContext(), R.layout.team_pk_newstate_bar_layout, null);
        ViewGroup viewGroup = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
        ViewGroup rootView = viewGroup.findViewById(R.id.rl_livevideo_message_root);
        if (rootView != null) {
            int stateBarHeight = SizeUtils.Dp2Px(getContext(), 19);
            int gapAbovePkStateLayout = SizeUtils.Dp2Px(getContext(), 5);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(this.getMeasuredWidth(), stateBarHeight);
            int[] location = new int[2];
            this.getLocationInWindow(location);
            lp.topMargin = location[1] - (gapAbovePkStateLayout + stateBarHeight);
            int rightMargin = (LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x4);
            lp.rightMargin = rightMargin > 0 ? rightMargin : 0;
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootView.addView(statBarRootView, lp);
            tvEnergyMyContribution = statBarRootView.findViewById(R.id.tv_teampk_pkstate_energy_mycontribution);
            tvEnergyMyContribution.setVisibility(GONE);

            //监听布局变化设置边距
            statBarRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int rightMargin = (LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x4);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) statBarRootView.getLayoutParams();
                    if (lp.rightMargin != rightMargin) {
                        lp.rightMargin = rightMargin;
                        LayoutParamsUtil.setViewLayoutParams(statBarRootView, lp);
                    }
                }
            });
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
        if (liveAndBackDebug != null && coinAdd > 0) {
            TeamPkLog.showMyGold(liveAndBackDebug, mCoinNum + "");
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
        updatePkState(ratio);
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

        if (liveAndBackDebug != null && mCoinNum > 0) {
            TeamPkLog.showMyGold(liveAndBackDebug, mCoinNum + "");
        }

        float ratio;
        if ((mMyTeamEnergy + mOtherTeamEnergy) > 0) {
            ratio = mMyTeamEnergy / (float) (mMyTeamEnergy + mOtherTeamEnergy);
        } else {
            ratio = 0.5f;
        }
        updatePkState(ratio);
        int currentProgress = (int) (ratio * 100);
        pkProgressBar.setProgress(currentProgress);
    }

    protected static final float HALF_PROGRESS = 0.5f;

    protected void updatePkState(float ratio) {
        if (this.showPopWindow) {
            this.showPopWindow = false;
            //语文pk还用老样式
            if(LiveVideoConfig.isSmallChinese){
                if (ratio > HALF_PROGRESS) {
                    tvState.setText("暂时领先");
                    tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_lead_bg);
                } else if (ratio < HALF_PROGRESS) {
                    tvState.setText("全力追赶");
                    tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_follow_bg);
                } else if (ratio == HALF_PROGRESS) {
                    tvState.setText("");
                    tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_lead_bg);
                }
            }else{
                // 理科pk 新样式
                if (ratio > HALF_PROGRESS) {
                    tvState.setText("领先");
                } else if (ratio < HALF_PROGRESS) {
                    tvState.setText("追赶");
                } else if (ratio == HALF_PROGRESS) {
                    tvState.setText("平手");
                }
            }
            showPkSateBar();
        }

    }


    /**
     * 显示 准备战斗提示
     */
    public void showPkReady() {
        //语文沿用一期pk样式
        if(LiveVideoConfig.isSmallChinese){
            tvState.setText("准备战斗");
            tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_ready_bg);
        }else{
            // 理科pk二期新样式
            tvState.setText("准备");
        }
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
    protected void showViewWithFadeInOutEffect(final View targetView, long duratrion) {
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


//    public void setTeamPkBll(TeamPkBll teamPkBll) {
//        liveAndBackDebug = teamPkBll;
//    }

    /**
     * 返回当前pk 结果
     * @return
     */
    public int getLatesPkState(){
        int result = 0;
        if(mMyTeamEnergy > mOtherTeamEnergy){
            result = TeamPkConfig.PK_STATE_LEAD;
        }else if(mMyTeamEnergy < mOtherTeamEnergy){
            result = TeamPkConfig.PK_STATE_BEHIND;
        }else {
            result = TeamPkConfig.PK_STATE_DRAW;
        }
        return  result;
    }
}

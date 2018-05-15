package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.SizeUtils;

/**
 * 战队pk  右侧状态栏
 */
public class TeamPKStateLayout extends FrameLayout {

    private TeamPkProgressBar pkProgressBar;
    private SmoothAddNumTextView tvMyteamEnergy;
    private SmoothAddNumTextView tvOtherteamEnergy;
    private SmoothAddNumTextView tvCoin;

    private long mMyteamAnergy;
    private long mOtherTeamAnergy;
    private long mCoinNum;
    private TextView tvState;  // 当前pk状态
    private View statBarRootView;

    public static final int PK_STATE_FOLLOW = 1; // 全力追赶
    public static final int PK_STATE_LEAD = 2;    //暂时领先
    public static final int pk_state_replay = 3;  // 回放中
    private boolean dataInited = false;

    public TeamPKStateLayout(@NonNull Context context) {
        super(context);
        initView();
    }


    public TeamPKStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TeamPKStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.team_pk_state_layout, this);
        pkProgressBar = findViewById(R.id.tpb_teampk_pkstate_energy_bar);
        tvMyteamEnergy = findViewById(R.id.tv_teampk_pkstate_myteam_energy);
        tvOtherteamEnergy = findViewById(R.id.tv_teampk_pkstate_otherteam_energy);
        tvCoin = findViewById(R.id.tv_teampk_pkstate_coin_num);
        pkProgressBar.setMaxProgress(100);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    addPkStatBar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TeamPKStateLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 添加视屏区域 pk 状态UI
     */
    private void addPkStatBar() {
        statBarRootView = View.inflate(getContext(), R.layout.team_pk_state_bar_layout, null);
        ViewGroup rootView = (ViewGroup) this.getParent().getParent();
        int stateBarHeight = SizeUtils.Dp2Px(getContext(), 17);
        int gapAbovePkStateLayout = SizeUtils.Dp2Px(getContext(), 6);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(this.getLayoutParams().width, stateBarHeight);
        int[] location = new int[2];
        this.getLocationInWindow(location);
        lp.topMargin = location[1] - (gapAbovePkStateLayout + stateBarHeight);
        rootView.addView(statBarRootView, lp);
        tvState = statBarRootView.findViewById(R.id.tv_answer_question_state);
        tvState.setVisibility(GONE);
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
     * @param ownEnergyAdd 本队新增能量值
     * @param coinCrement  本地增加金币值
     * @param otherEnergyAdd 对手增加能量值
     */
    public void updateData(int ownEnergyAdd, int otherEnergyAdd,int coinCrement) {
        Loger.e("coinNum","====>updateData:"+ownEnergyAdd+":"+otherEnergyAdd+":"+coinCrement);
        Loger.e("coinNum","====>updateData3333:"+mMyteamAnergy+":"+mOtherTeamAnergy+":"+mCoinNum);
        mMyteamAnergy = mMyteamAnergy + ownEnergyAdd;
        mOtherTeamAnergy = mOtherTeamAnergy+otherEnergyAdd;
        mCoinNum = mCoinNum + coinCrement;
        Loger.e("coinNum","====>updateData22222:"+mMyteamAnergy+":"+mOtherTeamAnergy+":"+mCoinNum);

        if(ownEnergyAdd > 0){
            tvMyteamEnergy.smoothAddNum(ownEnergyAdd);
        }else{
            if(tvMyteamEnergy.isAnimRunning()){
                tvMyteamEnergy.cancleAnim();
            }
            tvMyteamEnergy.setText(mMyteamAnergy+"");
        }
        if(otherEnergyAdd > 0){
            tvOtherteamEnergy.smoothAddNum(otherEnergyAdd);
        }else{
            if(tvOtherteamEnergy.isAnimRunning()){
                tvOtherteamEnergy.cancleAnim();
            }
            tvOtherteamEnergy.setText(mOtherTeamAnergy+"");
        }
        if(coinCrement >0){
            tvCoin.smoothAddNum(coinCrement);
        }else {
            if(tvCoin.isAnimRunning()){
                tvCoin.cancleAnim();
            }
            tvCoin.setText(mCoinNum+"");
        }
        float ratio;
        if ((mMyteamAnergy + mOtherTeamAnergy) > 0) {
            ratio = mMyteamAnergy / (float) (mMyteamAnergy + mOtherTeamAnergy);
        }else{
            ratio = 0.5f;
        }
        upDataSateText(ratio);
        int addProgress = (int) (ratio * 100 + 0.5f) - pkProgressBar.getProgress();
        if(addProgress > 0){
            pkProgressBar.smoothAddProgress(addProgress);
        }else{
            pkProgressBar.setProgress((int) (ratio * 100));
        }
    }

     private boolean showPopWindow;
    /**
     * 绑定数据
     *
     * @param coinNum         当前战队 金币总数
     * @param myTeamAnergy    当前战队 能量值
     * @param otherTeamAnergy 当前对手能量值
     * @param  showPopWindow   是否显示顶部进度状态
     */
    public void bindData(long coinNum, long myTeamAnergy, long otherTeamAnergy,boolean showPopWindow) {
        Loger.e("coinNum","====> PkstateLayout bindData 111:"+coinNum+":"+ myTeamAnergy+":"+ otherTeamAnergy);
        Loger.e("coinNum","====> PkstateLayout bindData 333:"+mCoinNum+":"+ mMyteamAnergy+":"+ mOtherTeamAnergy);
        this.showPopWindow = showPopWindow;
        if(!dataInited){
            dataInited = true;
            initData(coinNum, myTeamAnergy, otherTeamAnergy);
        }else{
             int addCoin = (int) (coinNum - mCoinNum);
             int ownEnergyAdd = (int) (myTeamAnergy - mMyteamAnergy);
             int otherEnergyAdd =(int) (otherTeamAnergy - mOtherTeamAnergy);
            Loger.e("coinNum","====> PkstateLayout bindData 222:"+addCoin+":"+ ownEnergyAdd+":"+ otherEnergyAdd);
            updateData(ownEnergyAdd,otherEnergyAdd,addCoin);
        }
    }

    private void initData(long coinNum, long myTeamAnergy, long otherTeamAnergy) {
        this.mCoinNum = coinNum;
        this.mMyteamAnergy = myTeamAnergy;
        this.mOtherTeamAnergy = otherTeamAnergy;
        tvCoin.setText(mCoinNum + "");
        tvMyteamEnergy.setText(mMyteamAnergy + "");
        tvOtherteamEnergy.setText(otherTeamAnergy + "");
        float ratio = 0;
        if ((mMyteamAnergy + mOtherTeamAnergy) > 0) {
            ratio = mMyteamAnergy / (float) (mMyteamAnergy + mOtherTeamAnergy);
        } else {
            if (ratio == 0) {
                ratio = 0.5f;
            }
        }
        upDataSateText(ratio);
        int currentProgress = (int) (ratio * 100);
        pkProgressBar.setProgress(currentProgress);
    }

    private void upDataSateText(float ratio) {
        //tvState.setVisibility(ratio != 0.5f?VISIBLE:GONE);
        if(this.showPopWindow){
            this.showPopWindow = false;
            tvState.setVisibility(VISIBLE);
            tvState.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvState.setVisibility(GONE);
                }
            },30*1000);
            //Logger.e("teamPkStateLayout", "======>upDataSateText:" + ratio);
            if (mMyteamAnergy == 0 && mOtherTeamAnergy == 0) {
                tvState.setText("准备战斗");
                tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_ready_bg);
            } else {
                if (ratio > 0.5f) {
                    tvState.setText("暂时领先");
                    tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_lead_bg);
                } else if (ratio < 0.5f) {
                    tvState.setText("全力追赶");
                    tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_follow_bg);
                } else if (ratio == 0.5f) {
                    tvState.setText("打成平手");
                    tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_lead_bg);
                }
            }
        }

    }

}

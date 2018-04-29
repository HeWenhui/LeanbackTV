package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.utils.uikit.SizeUtils;

/**
 * 战队pk  右侧状态栏
 *
 */
public class TeamPKStateLayout extends FrameLayout {

    private TeamPkProgressBar pkProgressBar;
    private SmoothAddNumTextView tvMyteamEnergy;
    private TextView tvOtherteamEnergy;
    private SmoothAddNumTextView tvCoin;

    private long mMyteamAnergy;
    private long mOtherTeamAnergy;
    private long mCoinNum;
    private TextView tvState;  // 当前pk状态
    private View statBarRootView;

    public static final int PK_STATE_FOLLOW = 1; // 全力追赶
    public static final int PK_STATE_LEAD = 2;    //暂时领先
    public static final int pk_state_replay = 3;  // 回放中

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


    private void initView( ) {
        LayoutInflater.from(getContext()).inflate(R.layout.team_pk_state_layout,this);
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
                }catch (Exception e){
                    e.printStackTrace();
                }
                TeamPKStateLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 添加视屏区域 pk 状态UI
     */
    private void addPkStatBar(){
        statBarRootView = View.inflate(getContext(), R.layout.team_pk_state_bar_layout,null);
       // ViewGroup docerView = (ViewGroup) ((Activity)getContext()).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) this.getParent().getParent();
        int stateBarHeight = SizeUtils.Dp2Px(getContext(),17);
        int gapAbovePkStateLayout = SizeUtils.Dp2Px(getContext(),6);
      //  FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(this.getLayoutParams().width,stateBarHeight);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(this.getLayoutParams().width,stateBarHeight);
        int []location = new int[2];
        this.getLocationInWindow(location);
        lp.topMargin = location[1] - (gapAbovePkStateLayout+stateBarHeight);
        //docerView.addView(statBarRootView,lp);
        rootView.addView(statBarRootView,lp);
        tvState = statBarRootView.findViewById(R.id.tv_answer_question_state);
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        //关联显示状态
        if(statBarRootView != null){
            statBarRootView.setVisibility(visibility);
        }
    }

    /**
     *  刷新 能量，金币
     * @param energrCrement
     * @param coinCrement
     */
    public void updateData(int energrCrement,int coinCrement ){
        mMyteamAnergy += energrCrement;
        mCoinNum += coinCrement;
        tvMyteamEnergy.smoothAddNum(energrCrement);
        tvCoin.smoothAddNum(coinCrement);

        float ratio = 0;
        if((mMyteamAnergy+mOtherTeamAnergy) >0){
            ratio = mMyteamAnergy /(float)(mMyteamAnergy+mOtherTeamAnergy);
        }
        upDataSateText(ratio);
        int addProgress = (int) (ratio * 100+0.5f) - pkProgressBar.getProgress();
        pkProgressBar.smoothAddProgress(addProgress);
    }


    /**
     * 绑定数据
     * @param coinNum       当前战队 金币总数
     * @param myTeamAnergy  当前战队 能量值
     * @param otherTeamAnergy 当前对手能量值
     */
    public void bindData(long coinNum , long myTeamAnergy, long otherTeamAnergy){
        this.mCoinNum = coinNum;
        this.mMyteamAnergy = myTeamAnergy;
        this.mOtherTeamAnergy = otherTeamAnergy;
        tvCoin.setText(mCoinNum+"");
        tvMyteamEnergy.setText(mMyteamAnergy+"");
        tvOtherteamEnergy.setText(otherTeamAnergy+"");
        float ratio  = 0;
        if((mMyteamAnergy+mOtherTeamAnergy)>0){
            ratio  = mMyteamAnergy/(float)(mMyteamAnergy+mOtherTeamAnergy);
        }
        if(ratio == 0){
            ratio = 0.5f;
        }
        upDataSateText(ratio);
        int currentProgress = (int) (ratio*100);
        pkProgressBar.setProgress(currentProgress);
    }

    private void upDataSateText(float ratio) {
        tvState.setVisibility(ratio != 0.5f?VISIBLE:GONE);
        if(ratio == 0){
            tvState.setText("准备战斗");
            tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_ready_bg);
        }else if(ratio > 0.5f){
            tvState.setText("暂时领先");
            tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_lead_bg);
        }else if(ratio < 0.5){
            tvState.setText("全力追赶");
            tvState.setBackgroundResource(R.drawable.shape_livevideo_teampk_statebar_follow_bg);
        }
    }


    /**
     * 更新pk 状态
     * @param state 
     */
    public  void upDatePkStateBar(int state){
        // TODO: 2018/4/21 更新显示 状态文案 + 背景
    }

}

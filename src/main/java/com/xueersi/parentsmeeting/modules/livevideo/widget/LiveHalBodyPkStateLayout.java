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
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 半身直播 战队Pk 状态栏
 *
 * @author chenkun
 * @version 1.0, 2018/10/25 下午4:15
 */

public class LiveHalBodyPkStateLayout extends TeamPkStateLayout {


    public LiveHalBodyPkStateLayout(@NonNull Context context) {
        super(context);
    }

    public LiveHalBodyPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveHalBodyPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void initView() {
        // TODO: 2018/10/25  初始化 布局文件
        LayoutInflater.from(getContext()).inflate(R.layout.halfbody_team_pk_state_layout, this);
        pkProgressBar = findViewById(R.id.tpb_teampk_pkstate_energy_bar);
        tvMyTeamEnergy = findViewById(R.id.tv_teampk_pkstate_myteam_energy);
        tvOtherTeamEnergy = findViewById(R.id.tv_teampk_pkstate_otherteam_energy);
        tvCoin = findViewById(R.id.tv_teampk_pkstate_coin_num);
        pkProgressBar.setMaxProgress(100);
        pkProgressBar.setProgress(50);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(LiveHalBodyPkStateLayout.this.getMeasuredWidth() > 0){
                    try {
                        addPkStatBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        LiveHalBodyPkStateLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        LiveHalBodyPkStateLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });

    }


    @Override
    protected void addPkStatBar() {
        // TODO: 2018/10/25 添加 PK 状态
        statBarRootView = View.inflate(getContext(), R.layout.team_pk_state_bar_halfbody_layout, null);
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
}

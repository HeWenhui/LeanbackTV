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
import android.widget.ImageView;
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


    private View vContributionCotanier;
    private ImageView ivPkState;

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
        LayoutInflater.from(getContext()).inflate(R.layout.halfbody_team_pk_state_layout, this);
        pkProgressBar = findViewById(R.id.tpb_teampk_pkstate_energy_bar);
        tvMyTeamEnergy = findViewById(R.id.tv_teampk_pkstate_myteam_energy);
        tvOtherTeamEnergy = findViewById(R.id.tv_teampk_pkstate_otherteam_energy);
        tvCoin = findViewById(R.id.tv_teampk_pkstate_coin_num);
        pkProgressBar.setMaxProgress(100);
        pkProgressBar.setProgress(50);

        vContributionCotanier = findViewById(R.id.rl_live_halfbody_energy_contribution);
        tvEnergyMyContribution = findViewById(R.id.tv_live_halfbody_energy_contribution);
        ivPkState = findViewById(R.id.iv_live_halfbody_pk_state);

    }


    @Override
    protected void addPkStatBar() {
    }


    @Override
    public void showPkReady() {
        ivPkState.setImageResource(R.drawable.live_halfbody_pk_ready);
        showViewWithFadeInOutEffect(ivPkState,PK_STATE_DISPLAY_DURATION);
    }

    @Override
    public void showEnergyMyContribute(int energy){
        vContributionCotanier.setVisibility(VISIBLE);
        energy = energy < 0?0:energy;
        tvEnergyMyContribution.setText("我贡献了"+energy+"个能量");
        showViewWithFadeInOutEffect(vContributionCotanier,ENERGY_MY_CONTRIBUTION_DURATION);

    }
    @Override
    public void updatePkState(float ratio){
        if (this.showPopWindow) {
            this.showPopWindow = false;
            if (ratio > HALF_PROGRESS) {
                ivPkState.setImageResource(R.drawable.live_halfbody_pk_lead);
                showViewWithFadeInOutEffect(ivPkState,PK_STATE_DISPLAY_DURATION);
            } else if (ratio < HALF_PROGRESS) {
                ivPkState.setImageResource(R.drawable.live_halfbody_pk_follow);
                showViewWithFadeInOutEffect(ivPkState,PK_STATE_DISPLAY_DURATION);
            }
        }
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PkAddEnergy;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PkUpdatePkState;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentCoinAndTotalEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;

/**
 * 半身直播-小班体验 战队Pk 状态栏
 *
 * @author linyuqiang
 * @version 1.0, 2019/5/16 下午4:15
 */

public class LiveHalBodyPrimaryCnPkStateLayout extends TeamPkStateLayout {
    protected View vContributionCotanier;

    public LiveHalBodyPrimaryCnPkStateLayout(@NonNull Context context) {
        super(context);
    }

    public LiveHalBodyPrimaryCnPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveHalBodyPrimaryCnPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override
    protected void initView() {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        pkProgressBar = findViewById(R.id.tpb_teampk_pkstate_energy_bar);
        tvMyTeamEnergy = findViewById(R.id.tv_teampk_pkstate_myteam_energy);
        tvOtherTeamEnergy = findViewById(R.id.tv_teampk_pkstate_otherteam_energy);
        tvCoin = findViewById(R.id.tv_teampk_pkstate_coin_num);
        pkProgressBar.setMaxProgress(100);
        pkProgressBar.setProgress(50);

        vContributionCotanier = findViewById(R.id.rl_live_halfbody_energy_contribution);
        tvEnergyMyContribution = findViewById(R.id.tv_live_halfbody_energy_contribution);
        Typeface fontFace = FontCache.getTypeface(getContext(), "fangzhengcuyuan.ttf");
        tvMyTeamEnergy.setTypeface(fontFace);
        tvOtherTeamEnergy.setTypeface(fontFace);
    }

    @Override
    public void showEnergyMyContribute(int energy) {
        vContributionCotanier.setVisibility(VISIBLE);
        energy = energy < 0 ? 0 : energy;
        tvEnergyMyContribution.setText("我贡献了" + energy + "个能量");
        showViewWithFadeInOutEffect(vContributionCotanier, ENERGY_MY_CONTRIBUTION_DURATION,View.INVISIBLE);
        LiveEventBus.getDefault(getContext()).post(new PkAddEnergy(false, energy));
    }

    @Override
    public void bindData(StudentCoinAndTotalEnergyEntity mCurrentPkState, boolean showPopWindow) {
        super.bindData(mCurrentPkState, showPopWindow);
        LiveEventBus.getDefault(getContext()).post(new PkAddEnergy(true, mCurrentPkState.getStuEnergy()));
    }

    @Override
    public void showPkReady() {

    }

    @Override
    protected void updatePkState(float ratio) {
        LiveEventBus.getDefault(getContext()).post(new PkUpdatePkState(ratio));
    }

    /**
     * 获取布局layout
     *
     * @return
     */
    protected int getLayoutId() {
        return R.layout.halfbody_arts_primary_teampk_state_layout;
    }

}

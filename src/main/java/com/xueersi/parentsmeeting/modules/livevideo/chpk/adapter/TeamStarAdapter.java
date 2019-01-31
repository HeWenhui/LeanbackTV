package com.xueersi.parentsmeeting.modules.livevideo.chpk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;

import java.util.List;

/**
 * Created by lenovo on 2019/1/28.
 */

public  class TeamStarAdapter extends RecyclerView.Adapter<TeamStarHolder> {

    List<TeamEnergyAndContributionStarEntity.ContributionStar> mData;

    public TeamStarAdapter(List<TeamEnergyAndContributionStarEntity.ContributionStar> data) {
        mData = data;
    }

    @Override
    public TeamStarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TeamStarHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livevideo_chpk_teamstar, parent, false));
    }

    @Override
    public void onBindViewHolder(TeamStarHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
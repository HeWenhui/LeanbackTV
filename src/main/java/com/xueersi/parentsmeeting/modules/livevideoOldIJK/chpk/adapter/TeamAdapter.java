package com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;

/**
 * Created by lenovo on 2019/1/25.
 */

public class TeamAdapter extends RecyclerView.Adapter<TeamHolder> {

    private Context context;

    private TeamPkTeamInfoEntity teamInfo;

    public TeamAdapter(Context context, TeamPkTeamInfoEntity teamInfo) {
        this.context = context;
        this.teamInfo = teamInfo;
    }

    @Override
    public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TeamHolder(LayoutInflater.from(context).inflate(R.layout.item_teampk_team, parent, false));
    }

    @Override
    public void onBindViewHolder(TeamHolder holder, int position) {
        ImageLoader.with(context).load(teamInfo.getTeamLogoList().get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivTeamLogo);
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (teamInfo != null && teamInfo.getTeamLogoList() != null) {
            itemCount = teamInfo.getTeamLogoList().size();
        }

        return itemCount;
    }
}

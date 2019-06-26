package com.xueersi.parentsmeeting.modules.livevideo.chpk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by lenovo on 2019/1/25.
 */

public class TeamHolder extends RecyclerView.ViewHolder {

    ImageView ivTeamLogo;

    public TeamHolder(View itemView) {
        super(itemView);
        ivTeamLogo = itemView.findViewById(R.id.iv_teampk_team_logo);
    }

}

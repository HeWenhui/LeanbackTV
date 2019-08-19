package com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;

/**
 * Created by lenovo on 2019/1/25.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberHolder> {
    private Context context;

    private TeamPkTeamInfoEntity teamInfo;

    public MemberAdapter(Context context, TeamPkTeamInfoEntity teamInfo) {
        this.context = context;
        this.teamInfo = teamInfo;
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberHolder(LayoutInflater.from(context).inflate(R.layout.item_chpk_member, parent, false));
    }

    @Override
    public void onBindViewHolder(MemberHolder holder, int position) {
        TeamPkTeamInfoEntity.StudentEntity student = teamInfo.getTeamMembers().get(position);
        ImageLoader.with(context).load(student.getImg()).asCircle().into(holder.ivHead);
        holder.tvName.setText(student.getUserName());
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (teamInfo != null && teamInfo.getTeamMembers() != null) {
            itemCount = teamInfo.getTeamMembers().size();
        }
        return itemCount;
    }
}

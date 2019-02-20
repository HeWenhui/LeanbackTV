package com.xueersi.parentsmeeting.modules.livevideo.chpk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by lenovo on 2019/1/25.
 */

public  class MemberHolder extends RecyclerView.ViewHolder {

    public final ImageView ivHead;
    public final TextView tvName;

    public MemberHolder(View itemView) {
        super(itemView);
        ivHead = itemView.findViewById(R.id.iv_teampk_member_head);
        tvName = itemView.findViewById(R.id.tv_teampk_member_name);
    }

}
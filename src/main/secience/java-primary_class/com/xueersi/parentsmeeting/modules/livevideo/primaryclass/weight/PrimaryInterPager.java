package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

public class PrimaryInterPager extends LiveBasePager {
    TextView tv_livevideo_primary_team_inter_left;
    TextView tv_livevideo_primary_team_inter_right;
    OnItemClick onItemClick;
    public static int TYPE_1 = 1;
    public static int TYPE_2 = 2;

    public PrimaryInterPager(Context context, OnItemClick onItemClick) {
        super(context);
        this.onItemClick = onItemClick;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_primary_class_team_inter, null);
        tv_livevideo_primary_team_inter_left = view.findViewById(R.id.tv_livevideo_primary_team_inter_left);
        tv_livevideo_primary_team_inter_right = view.findViewById(R.id.tv_livevideo_primary_team_inter_right);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initListener() {
        super.initListener();
        tv_livevideo_primary_team_inter_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(TYPE_1);
            }
        });
        tv_livevideo_primary_team_inter_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(TYPE_2);
            }
        });
    }

    interface OnItemClick {
        void onClick(int type);
    }
}

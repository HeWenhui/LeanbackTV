package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MyRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;

import java.util.List;

/**
 * 中学激励系统里面的连对页面
 */
public class MiddleScienceEvenDrivePager extends BasePager {

    private TextView tvMygroup;

    private TextView tvEven;//    tv_livevideo_middle_science_even;

    private View vMyGroup, vEvenLine, vGroupsLine;

    private TextView tvGroups;//tv_livevideo_middle_science_groups

    private ListView lvEven;

    public MiddleScienceEvenDrivePager(Context context) {
        super(context, false);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_livevideo_middle_science_even_drive, null);

        tvMygroup = view.findViewById(R.id.tv_livevideo_middle_science_mygroup);
        tvEven = view.findViewById(R.id.tv_livevideo_middle_science_even);
        tvGroups = view.findViewById(R.id.tv_livevideo_middle_science_groups);
        vMyGroup = view.findViewById(R.id.v_livevideo_rank_mygroup);
        vEvenLine = view.findViewById(R.id.v_livevideo_rank_mygroup);
        vGroupsLine = view.findViewById(R.id.v_livevideo_rank_mygroup);

        lvEven = view.findViewById(R.id.lv_livevideo_middle_science_list);

        return view;
    }

    @Override
    public void initData() {

    }

    public void updataData(MyRankEntity myRankEntity) {
//        lvEven.setAdapter(new);
        List<RankEntity> rankEntities = myRankEntity.getRankEntities();
        for (int item = 0; item < rankEntities.size(); item++) {

        }

    }

}

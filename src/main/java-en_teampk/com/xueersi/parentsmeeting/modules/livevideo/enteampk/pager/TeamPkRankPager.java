package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.View;
import android.widget.GridView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.EnTeamItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkRankPager extends LiveBasePager {
    private GridView gv_livevideo_en_teampk_rank;
    CommonAdapter<EnTeamEntity> teamAdapter;
    ArrayList<EnTeamEntity> teamEntitys = new ArrayList<>();

    public TeamPkRankPager(Context context) {
        super(context);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_en_team_rank, null);
        gv_livevideo_en_teampk_rank = view.findViewById(R.id.gv_livevideo_en_teampk_rank);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        for (int i = 0; i < 6; i++) {
            EnTeamEntity teamEntity = new EnTeamEntity();
            teamEntity.name = "测试我" + i;
            teamEntitys.add(teamEntity);
        }
        teamAdapter = new CommonAdapter<EnTeamEntity>(teamEntitys) {
            @Override
            public AdapterItemInterface<EnTeamEntity> getItemView(Object type) {
                return new EnTeamItem();
            }
        };
        gv_livevideo_en_teampk_rank.setAdapter(teamAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberItem;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberStarItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkLeadPager extends LiveBasePager {
    private RelativeLayout rl_livevideo_en_teampk_lead_bottom;

    public TeamPkLeadPager(Context context) {
        super(context);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_lead, null);
        rl_livevideo_en_teampk_lead_bottom = view.findViewById(R.id.rl_livevideo_en_teampk_lead_bottom);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup group = (ViewGroup) mView;
                final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_win, group, false);
                group.addView(view);
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup group = (ViewGroup) mView;
                        group.removeView(view);
                        View layout_livevideo_en_team_lead_star = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_star, rl_livevideo_en_teampk_lead_bottom, false);
                        rl_livevideo_en_teampk_lead_bottom.addView(layout_livevideo_en_team_lead_star);
                        GridView gv_livevideo_en_teampk_lead_star = layout_livevideo_en_team_lead_star.findViewById(R.id.gv_livevideo_en_teampk_lead_star);
                        ArrayList<TeamMemberEntity> myTeamEntitys = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            TeamMemberEntity teamEntity = new TeamMemberEntity();
                            teamEntity.name = "测试" + i;
                            myTeamEntitys.add(teamEntity);
                        }
                        CommonAdapter<TeamMemberEntity> myTeamAdapter = new CommonAdapter<TeamMemberEntity>(myTeamEntitys) {
                            @Override
                            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                                return new TeamMemberStarItem();
                            }
                        };
                        gv_livevideo_en_teampk_lead_star.setAdapter(myTeamAdapter);
                    }
                }, 2500);
            }
        }, 2000);
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}

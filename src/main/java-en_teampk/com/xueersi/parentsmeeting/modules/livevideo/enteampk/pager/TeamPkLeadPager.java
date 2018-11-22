package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberItem;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberStarItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkLeadPager extends LiveBasePager {
    EnTeamPkRankEntity enTeamPkRankEntity;
    private RelativeLayout rl_livevideo_en_teampk_lead_bottom;
    private ProgressBar pg_livevideo_en_teampk_lead;
    private ImageView iv_livevideo_en_teampk_mine;
    private ImageView iv_livevideo_en_teampk_other;

    public TeamPkLeadPager(Context context, EnTeamPkRankEntity enTeamPkRankEntity) {
        super(context);
        this.enTeamPkRankEntity = enTeamPkRankEntity;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_lead, null);
        rl_livevideo_en_teampk_lead_bottom = view.findViewById(R.id.rl_livevideo_en_teampk_lead_bottom);
        pg_livevideo_en_teampk_lead = view.findViewById(R.id.pg_livevideo_en_teampk_lead);
        iv_livevideo_en_teampk_mine = view.findViewById(R.id.iv_livevideo_en_teampk_mine);
        iv_livevideo_en_teampk_other = view.findViewById(R.id.iv_livevideo_en_teampk_other);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        int[] res = EnTeamPkConfig.TEAM_RES;
        iv_livevideo_en_teampk_mine.setImageResource(enTeamPkRankEntity.getMyTeam());
        int win = 0;
        if (enTeamPkRankEntity.getMyTeam() == enTeamPkRankEntity.getApkTeamId()) {
            win = enTeamPkRankEntity.getaCurrentScore() - enTeamPkRankEntity.getbCurrentScore();
            iv_livevideo_en_teampk_other.setImageResource(enTeamPkRankEntity.getMyTeam());
        } else {
            win = enTeamPkRankEntity.getbCurrentScore() - enTeamPkRankEntity.getaCurrentScore();
            iv_livevideo_en_teampk_other.setImageResource(enTeamPkRankEntity.getBpkTeamId());
        }
        if (win >= 0) {
            final ViewGroup group = (ViewGroup) mView;
            final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_win, group, false);
            group.addView(view);
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    group.removeView(view);
                    showRank();
                }
            }, 100);
        } else {
            showRank();
        }
    }

    private void showRank() {
        View layout_livevideo_en_team_lead_star = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_star, rl_livevideo_en_teampk_lead_bottom, false);
        rl_livevideo_en_teampk_lead_bottom.addView(layout_livevideo_en_team_lead_star);
        GridView gv_livevideo_en_teampk_lead_star = layout_livevideo_en_team_lead_star.findViewById(R.id.gv_livevideo_en_teampk_lead_star);
        ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
        CommonAdapter<TeamMemberEntity> myTeamAdapter = new CommonAdapter<TeamMemberEntity>(myTeamEntitys) {
            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberStarItem();
            }
        };
        gv_livevideo_en_teampk_lead_star.setAdapter(myTeamAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkRankResultPager extends LiveBasePager {
    private GridView gv_livevideo_en_teampk_rank_mine;
    private GridView gv_livevideo_en_teampk_rank_other;
    private Button bt_livevideo_en_teampk_rank_start;
    private ImageView iv_livevideo_en_teampk_rank_score;
    private RelativeLayout rv_livevideo_en_teampk_rank_score_tip;
    private OnStartClick onStartClick;
    CommonAdapter<TeamMemberEntity> myTeamAdapter;
    CommonAdapter<TeamMemberEntity> otherTeamAdapter;
    ArrayList<TeamMemberEntity> myTeamEntitys = new ArrayList<>();
    ArrayList<TeamMemberEntity> otherTeamEntitys = new ArrayList<>();

    public TeamPkRankResultPager(Context context) {
        super(context);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_en_team_rank_result, null);
        gv_livevideo_en_teampk_rank_mine = view.findViewById(R.id.gv_livevideo_en_teampk_rank_mine);
        gv_livevideo_en_teampk_rank_other = view.findViewById(R.id.gv_livevideo_en_teampk_rank_other);
        bt_livevideo_en_teampk_rank_start = view.findViewById(R.id.bt_livevideo_en_teampk_rank_start);
        iv_livevideo_en_teampk_rank_score = view.findViewById(R.id.iv_livevideo_en_teampk_rank_score);
        rv_livevideo_en_teampk_rank_score_tip = view.findViewById(R.id.rv_livevideo_en_teampk_rank_score_tip);
        return view;
    }

    @Override
    public void initListener() {
        bt_livevideo_en_teampk_rank_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XESToastUtils.showToast(mContext, "start");
                if (onStartClick != null) {
                    onStartClick.onClick();
                }
            }
        });
        iv_livevideo_en_teampk_rank_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rv_livevideo_en_teampk_rank_score_tip.getVisibility() == View.VISIBLE) {
                    rv_livevideo_en_teampk_rank_score_tip.setVisibility(View.GONE);
                } else {
                    rv_livevideo_en_teampk_rank_score_tip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void initData() {
        for (int i = 0; i < 6; i++) {
            TeamMemberEntity teamEntity = new TeamMemberEntity();
            teamEntity.name = "测试我" + i;
            myTeamEntitys.add(teamEntity);
        }
        for (int i = 0; i < 6; i++) {
            TeamMemberEntity teamEntity = new TeamMemberEntity();
            teamEntity.name = "测试他" + i;
            otherTeamEntitys.add(teamEntity);
        }
        myTeamAdapter = new CommonAdapter<TeamMemberEntity>(myTeamEntitys) {
            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberItem();
            }
        };
        gv_livevideo_en_teampk_rank_mine.setAdapter(myTeamAdapter);
        otherTeamAdapter = new CommonAdapter<TeamMemberEntity>(otherTeamEntitys) {
            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberItem();
            }
        };
        gv_livevideo_en_teampk_rank_other.setAdapter(otherTeamAdapter);
    }

    public interface OnStartClick {
        void onClick();
    }
}

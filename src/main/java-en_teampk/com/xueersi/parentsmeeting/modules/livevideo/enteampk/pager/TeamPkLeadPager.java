package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberStarItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamPkLeadPager extends LiveBasePager {
    private EnTeamPkRankEntity enTeamPkRankEntity;
    private RelativeLayout rlTeampkLeadBottom;
    private ProgressBar pgTeampkLead;
    private ImageView ivTeampkMine;
    private ImageView ivTeampkOther;
    private TextView ivTeampkLeadFireAddLeft;
    private TextView tvTeampkLeadScoreLeft;
    private TextView ivTeampkLeadFireAddRight;
    private TextView tvTeampkLeadScoreRight;
    private int pattern;

    public TeamPkLeadPager(Context context, EnTeamPkRankEntity enTeamPkRankEntity, int pattern) {
        super(context, false);
        this.pattern = pattern;
        mView = initView();
        this.enTeamPkRankEntity = enTeamPkRankEntity;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_lead, null);
        if (pattern == 2) {
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_16_9);
        } else {
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_4_3);
        }
        rlTeampkLeadBottom = view.findViewById(R.id.rl_livevideo_en_teampk_lead_bottom);
        pgTeampkLead = view.findViewById(R.id.pg_livevideo_en_teampk_lead);
        ivTeampkMine = view.findViewById(R.id.iv_livevideo_en_teampk_mine);
        ivTeampkOther = view.findViewById(R.id.iv_livevideo_en_teampk_other);
        ivTeampkLeadFireAddLeft = view.findViewById(R.id.iv_livevideo_en_teampk_lead_fire_add_left);
        tvTeampkLeadScoreLeft = view.findViewById(R.id.tv_livevideo_en_teampk_lead_score_left);
        ivTeampkLeadFireAddRight = view.findViewById(R.id.iv_livevideo_en_teampk_lead_fire_add_right);
        tvTeampkLeadScoreRight = view.findViewById(R.id.tv_livevideo_en_teampk_lead_score_right);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        int[] res = EnTeamPkConfig.TEAM_RES;
        ivTeampkMine.setImageResource(res[enTeamPkRankEntity.getMyTeam()]);
        int win = 0;
        if (enTeamPkRankEntity.getMyTeam() == enTeamPkRankEntity.getApkTeamId()) {
            win = enTeamPkRankEntity.getaCurrentScore() - enTeamPkRankEntity.getbCurrentScore();
            ivTeampkOther.setImageResource(res[enTeamPkRankEntity.getMyTeam()]);
            ivTeampkLeadFireAddLeft.setText("" + enTeamPkRankEntity.getaCurrentScore());
            tvTeampkLeadScoreLeft.setText("" + enTeamPkRankEntity.getaTotalScore());
            ivTeampkLeadFireAddRight.setText("" + enTeamPkRankEntity.getbCurrentScore());
            tvTeampkLeadScoreRight.setText("" + enTeamPkRankEntity.getbTotalScore());
        } else {
            win = enTeamPkRankEntity.getbCurrentScore() - enTeamPkRankEntity.getaCurrentScore();
            ivTeampkOther.setImageResource(res[enTeamPkRankEntity.getBpkTeamId()]);
            ivTeampkOther.setImageResource(res[enTeamPkRankEntity.getMyTeam()]);
            ivTeampkLeadFireAddLeft.setText("" + enTeamPkRankEntity.getbCurrentScore());
            tvTeampkLeadScoreLeft.setText("" + enTeamPkRankEntity.getbTotalScore());
            ivTeampkLeadFireAddRight.setText("" + enTeamPkRankEntity.getaCurrentScore());
            tvTeampkLeadScoreRight.setText("" + enTeamPkRankEntity.getaTotalScore());
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
        View layout_livevideo_en_team_lead_star = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_star, rlTeampkLeadBottom, false);
        rlTeampkLeadBottom.addView(layout_livevideo_en_team_lead_star);
        GridView gv_livevideo_en_teampk_lead_star = layout_livevideo_en_team_lead_star.findViewById(R.id.gv_livevideo_en_teampk_lead_star);
        ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
        CommonAdapter<TeamMemberEntity> myTeamAdapter = new CommonAdapter<TeamMemberEntity>(myTeamEntitys) {
            HashMap<TeamMemberEntity, LottieAnimationView> map = new HashMap<>();

            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberStarItem(mContext, map);
            }
        };
        gv_livevideo_en_teampk_lead_star.setAdapter(myTeamAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}

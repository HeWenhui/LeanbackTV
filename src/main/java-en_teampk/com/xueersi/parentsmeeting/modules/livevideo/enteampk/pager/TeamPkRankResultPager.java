package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkRankResultPager extends LiveBasePager {
    private RelativeLayout gv_livevideo_en_teampk_rank_mine;
    private RelativeLayout gv_livevideo_en_teampk_rank_other;
    private Button bt_livevideo_en_teampk_rank_start;
    private ImageView iv_livevideo_en_teampk_rank_score;
    private LinearLayout ll_livevideo_en_teampk_rank_score_tip;
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
        ll_livevideo_en_teampk_rank_score_tip = view.findViewById(R.id.ll_livevideo_en_teampk_rank_score_tip);
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
                if (ll_livevideo_en_teampk_rank_score_tip.getVisibility() == View.VISIBLE) {
                    ll_livevideo_en_teampk_rank_score_tip.setVisibility(View.GONE);
                } else {
                    ll_livevideo_en_teampk_rank_score_tip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void initData() {
        for (int i = 0; i < 6; i++) {
            TeamMemberEntity teamEntity = new TeamMemberEntity();
            if (i == 2) {
                teamEntity.isMy = true;
                teamEntity.name = "我";
            } else {
                teamEntity.name = "测试左" + i;
            }
            myTeamEntitys.add(teamEntity);
        }
        for (int i = 0; i < 6; i++) {
            TeamMemberEntity teamEntity = new TeamMemberEntity();
            teamEntity.name = "测试右" + i;
            otherTeamEntitys.add(teamEntity);
        }
        myTeamAdapter = new CommonAdapter<TeamMemberEntity>(myTeamEntitys) {
            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberItem();
            }
        };
        for (int i = 0; i < myTeamAdapter.getCount(); i++) {
            View view = myTeamAdapter.getView(i, null, gv_livevideo_en_teampk_rank_mine);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
            if (i > 2) {
                lp.topMargin = (int) (70 * ScreenUtils.getScreenDensity());
            }
            lp.leftMargin = (int) ((i % 3) * (73 * ScreenUtils.getScreenDensity()));
            gv_livevideo_en_teampk_rank_mine.addView(view, lp);
        }
        otherTeamAdapter = new CommonAdapter<TeamMemberEntity>(otherTeamEntitys) {
            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberItem();
            }
        };
        for (int i = 0; i < otherTeamAdapter.getCount(); i++) {
            View view = otherTeamAdapter.getView(i, null, gv_livevideo_en_teampk_rank_other);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
            if (i > 2) {
                lp.topMargin = (int) (70 * ScreenUtils.getScreenDensity());
            }
            lp.leftMargin = (int) ((i % 3) * (73 * ScreenUtils.getScreenDensity()));
            gv_livevideo_en_teampk_rank_other.addView(view, lp);
        }
        String[] tips = {"+10", "+5", "按比例增加"};
        for (int i = 0; i < tips.length; i++) {
            View tipView = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_en_tip, ll_livevideo_en_teampk_rank_score_tip, false);
            TextView tv_livevideo_en_teampk_rank_score_tip = tipView.findViewById(R.id.tv_livevideo_en_teampk_rank_score_tip);
            tv_livevideo_en_teampk_rank_score_tip.setText(tips[i]);
            ll_livevideo_en_teampk_rank_score_tip.addView(tipView);
        }
    }

    public interface OnStartClick {
        void onClick();
    }
}

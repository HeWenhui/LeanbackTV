package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkRankResultPager extends LiveBasePager {
    private ImageView ivTeampkMine;
    private ImageView ivTeampkOther;
    private RelativeLayout rlRankMine;
    private RelativeLayout rlRankOther;
    private Button btRankStart;
    private ImageView ivRankScore;
    private LinearLayout llScoreTip;
    private OnStartClick onStartClick;
    private CommonAdapter<TeamMemberEntity> myTeamAdapter;
    private CommonAdapter<TeamMemberEntity> otherTeamAdapter;
    private ArrayList<TeamMemberEntity> myTeamEntitys = new ArrayList<>();
    private ArrayList<TeamMemberEntity> otherTeamEntitys = new ArrayList<>();
    private PkTeamEntity pkTeamEntity;

    public TeamPkRankResultPager(Context context, PkTeamEntity pkTeamEntity) {
        super(context);
        this.pkTeamEntity = pkTeamEntity;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_en_team_rank_result, null);
        ivTeampkMine = view.findViewById(R.id.iv_livevideo_en_teampk_mine);
        ivTeampkOther = view.findViewById(R.id.iv_livevideo_en_teampk_other);
        rlRankMine = view.findViewById(R.id.rl_livevideo_en_teampk_rank_mine);
        rlRankOther = view.findViewById(R.id.rl_livevideo_en_teampk_rank_other);
        btRankStart = view.findViewById(R.id.bt_livevideo_en_teampk_rank_start);
        ivRankScore = view.findViewById(R.id.iv_livevideo_en_teampk_rank_score);
        llScoreTip = view.findViewById(R.id.ll_livevideo_en_teampk_rank_score_tip);
        return view;
    }

    @Override
    public void initListener() {
        btRankStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onStartClick != null) {
                    onStartClick.onClick();
                }
            }
        });
        ivRankScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llScoreTip.getVisibility() == View.VISIBLE) {
                    llScoreTip.setVisibility(View.GONE);
                } else {
                    llScoreTip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void initData() {
        int[] res = EnTeamPkConfig.TEAM_RES;
        if (pkTeamEntity.getMyTeam() == pkTeamEntity.getaId()) {
            ivTeampkMine.setImageResource(res[pkTeamEntity.getaId()]);
            ivTeampkOther.setImageResource(res[pkTeamEntity.getbId()]);
            myTeamEntitys = pkTeamEntity.getaTeamMemberEntity();
            otherTeamEntitys = pkTeamEntity.getbTeamMemberEntity();
        } else {
            ivTeampkMine.setImageResource(res[pkTeamEntity.getbId()]);
            ivTeampkOther.setImageResource(res[pkTeamEntity.getaId()]);
            myTeamEntitys = pkTeamEntity.getbTeamMemberEntity();
            otherTeamEntitys = pkTeamEntity.getaTeamMemberEntity();
        }
        myTeamAdapter = new CommonAdapter<TeamMemberEntity>(myTeamEntitys) {
            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberItem(rlRankMine);
            }
        };
        for (int i = 0; i < myTeamAdapter.getCount(); i++) {
            View view = myTeamAdapter.getView(i, null, rlRankMine);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
            if (i > 2) {
                lp.topMargin = (int) (70 * ScreenUtils.getScreenDensity());
            }
            lp.leftMargin = (int) ((i % 3) * (73 * ScreenUtils.getScreenDensity()));
            rlRankMine.addView(view, lp);
        }
        otherTeamAdapter = new CommonAdapter<TeamMemberEntity>(otherTeamEntitys) {
            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberItem(rlRankOther);
            }
        };
        for (int i = 0; i < otherTeamAdapter.getCount(); i++) {
            View view = otherTeamAdapter.getView(i, null, rlRankOther);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
            if (i > 2) {
                lp.topMargin = (int) (70 * ScreenUtils.getScreenDensity());
            }
            lp.leftMargin = (int) ((i % 3) * (73 * ScreenUtils.getScreenDensity()));
            rlRankOther.addView(view, lp);
        }
        String[] tips = {"+10", "+5", "按比例增加"};
        for (int i = 0; i < tips.length; i++) {
            View tipView = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_en_tip, llScoreTip, false);
            TextView tv_livevideo_en_teampk_rank_score_tip = tipView.findViewById(R.id.tv_livevideo_en_teampk_rank_score_tip);
            tv_livevideo_en_teampk_rank_score_tip.setText(tips[i]);
            llScoreTip.addView(tipView);
        }
        rlRankMine.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (rlRankMine.getChildCount() == myTeamEntitys.size()) {
                    for (int i = 0; i < myTeamEntitys.size(); i++) {
                        TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                        if (teamMemberEntity.isMy) {
                            break;
                        }
                    }
                    rlRankMine.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return false;
            }
        });
    }

    public interface OnStartClick {
        void onClick();
    }
}

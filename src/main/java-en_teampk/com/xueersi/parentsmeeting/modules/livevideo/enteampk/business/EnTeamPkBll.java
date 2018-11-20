package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkLeadPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

public class EnTeamPkBll extends BaseBll implements EnTeamPkAction {
    private Handler handler = new Handler(Looper.getMainLooper());
    private RelativeLayout rootView;
    private TeamPkRankPager teamPkRankPager;
    private TeamPkRankResultPager teamPkRankResultPager;
    private TeamPkLeadPager teamPkLeadPager;
    private EnTeamPkHttp enTeamPkHttp;

    public EnTeamPkBll(Context context) {
        super(context);
    }

    public void setEnTeamPkHttp(EnTeamPkHttp enTeamPkHttp) {
        this.enTeamPkHttp = enTeamPkHttp;
    }

    public void setRootView(RelativeLayout rootView) {
        this.rootView = rootView;
    }

    @Override
    public void onRankStart() {
        enTeamPkHttp.reportStuInfo(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objects) {
                if (teamPkRankPager == null) {
                    teamPkRankPager = new TeamPkRankPager(mContext);
                }
                teamPkRankPager.setOnTeamSelect(new TeamPkRankPager.OnTeamSelect() {
                    @Override
                    public void onTeamSelect(PkTeamEntity pkTeamEntity) {
                        rootView.removeView(teamPkRankPager.getRootView());
                        if (teamPkRankResultPager == null) {
                            teamPkRankResultPager = new TeamPkRankResultPager(mContext, pkTeamEntity);
                        }
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                        rootView.addView(teamPkRankResultPager.getRootView(), layoutParams);
                    }
                });
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                rootView.addView(teamPkRankPager.getRootView(), layoutParams);
                enTeamPkHttp.getSelfTeamInfo(new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objects) {
                        PkTeamEntity pkTeamEntity = (PkTeamEntity) objects[0];
                        teamPkRankPager.setPkTeamEntity(pkTeamEntity);
                    }
                });
            }
        });
    }

    @Override
    public void onRankResult() {

    }

    @Override
    public void onRankLead() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (teamPkLeadPager == null) {
                    teamPkLeadPager = new TeamPkLeadPager(mContext);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                rootView.addView(teamPkLeadPager.getRootView(), layoutParams);
            }
        });
    }
}

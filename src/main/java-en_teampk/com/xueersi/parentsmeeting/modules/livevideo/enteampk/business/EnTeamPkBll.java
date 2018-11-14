package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseBll;
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

    public EnTeamPkBll(Context context) {
        super(context);
    }

    public void setRootView(RelativeLayout rootView) {
        this.rootView = rootView;
    }

    @Override
    public void onRankStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (teamPkRankPager == null) {
                    teamPkRankPager = new TeamPkRankPager(mContext);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                rootView.addView(teamPkRankPager.getRootView(), layoutParams);
            }
        });
    }

    @Override
    public void onRankResult() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (teamPkRankResultPager == null) {
                    teamPkRankResultPager = new TeamPkRankResultPager(mContext);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                rootView.addView(teamPkRankResultPager.getRootView(), layoutParams);
            }
        });
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

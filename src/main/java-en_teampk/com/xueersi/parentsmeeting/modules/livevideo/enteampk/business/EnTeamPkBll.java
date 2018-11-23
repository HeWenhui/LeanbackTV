package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkLeadPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

public class EnTeamPkBll extends BaseBll implements EnTeamPkAction, EnglishPkUpdata {
    private Handler handler = new Handler(Looper.getMainLooper());
    int getSelfTeamInfoTimes = 1;
    private RelativeLayout rootView;
    private TeamPkRankPager teamPkRankPager;
    private boolean teamEnd = false;
    private TeamPkRankResultPager teamPkRankResultPager;
    private TeamPkLeadPager teamPkLeadPager;
    private EnTeamPkHttp enTeamPkHttp;
    private LiveGetInfo getInfo;

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
    public void onLiveInited(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        enTeamPkHttp.reportStuInfo(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objects) {

            }
        });
    }

    @Override
    public void onRankStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                teamPkRankPager = new TeamPkRankPager(mContext);
                teamPkRankPager.setOnTeamSelect(new TeamPkRankPager.OnTeamSelect() {
                    @Override
                    public void onTeamSelect(PkTeamEntity pkTeamEntity) {
                        rootView.removeView(teamPkRankPager.getRootView());
                        teamPkRankResultPager = new TeamPkRankResultPager(mContext, pkTeamEntity);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                        rootView.addView(teamPkRankResultPager.getRootView(), layoutParams);
                    }
                });
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                rootView.addView(teamPkRankPager.getRootView(), layoutParams);
                enTeamPkHttp.getSelfTeamInfo(new AbstractBusinessDataCallBack() {
                    AbstractBusinessDataCallBack callBack = this;

                    @Override
                    public void onDataSucess(Object... objects) {
                        if (!teamEnd) {
                            PkTeamEntity pkTeamEntity = (PkTeamEntity) objects[0];
                            teamPkRankPager.setPkTeamEntity(pkTeamEntity);
                        }
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        logger.d("onDataFail:errStatus=" + errStatus + ",teamEnd=" + teamEnd);
                        if (!teamEnd) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    enTeamPkHttp.getSelfTeamInfo(callBack);
                                }
                            }, (getSelfTeamInfoTimes++) * 1000);
                        }
                    }
                });
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (teamPkRankPager != null && teamPkRankPager.getPkTeamEntity() != null) {
                    return;
                }
                teamEnd = true;
                if (teamPkRankPager != null) {
                    rootView.removeView(teamPkRankPager.getRootView());
                }
            }
        }, 30000);
    }

    @Override
    public void onRankResult() {

    }

    @Override
    public void onRankLead(final EnTeamPkRankEntity enTeamPkRankEntity) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                teamPkLeadPager = new TeamPkLeadPager(mContext, enTeamPkRankEntity);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                rootView.addView(teamPkLeadPager.getRootView(), layoutParams);
            }
        });
    }

    @Override
    public void updataEnglishPkGroup() {
        enTeamPkHttp.updataEnglishPkGroup(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objects) {

            }
        });
    }

}

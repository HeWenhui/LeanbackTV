package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.config.AppConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkLeadPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
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
    int pattern;
    private boolean destory = false;
    private String mode;
    private LiveGetInfo.EnglishPk englishPk;
    private PkTeamEntity pkTeamEntity;

    public EnTeamPkBll(Context context) {
        super(context);
    }

    public void setEnTeamPkHttp(EnTeamPkHttp enTeamPkHttp) {
        this.enTeamPkHttp = enTeamPkHttp;
    }

    public void setRootView(RelativeLayout rootView) {
        this.rootView = rootView;
    }

    public void setPkTeamEntity(PkTeamEntity pkTeamEntity) {
        this.pkTeamEntity = pkTeamEntity;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        pattern = getInfo.getPattern();
        mode = getInfo.getMode();
        englishPk = getInfo.getEnglishPk();
        if (englishPk.hasGroup == 0) {
            enTeamPkHttp.reportStuInfo(new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objects) {

                }
            });
        } else {
            if (pkTeamEntity == null) {
                enTeamPkHttp.getEnglishPkRank(new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objects) {
                        pkTeamEntity = (PkTeamEntity) objects[0];
                    }
                });
            }
        }
//        addTop();
    }

    private void addTop() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_join, rootView, false);
        TextView tv_livevideo_en_teampk_top_name = view.findViewById(R.id.tv_livevideo_en_teampk_top_name);
        ImageView iv_livevideo_en_teampk_top_img = view.findViewById(R.id.iv_livevideo_en_teampk_top_img);
        if (pkTeamEntity != null) {
            tv_livevideo_en_teampk_top_name.setText("欢迎加入" + EnTeamPkConfig.TEAM_NAMES[pkTeamEntity.getMyTeam()]);
            iv_livevideo_en_teampk_top_img.setImageResource(EnTeamPkConfig.TEAM_RES[pkTeamEntity.getMyTeam()]);
        } else {
            if (AppConfig.DEBUG) {
                tv_livevideo_en_teampk_top_name.setText("欢迎加入" + EnTeamPkConfig.TEAM_NAMES[0]);
                iv_livevideo_en_teampk_top_img.setImageResource(EnTeamPkConfig.TEAM_RES[0]);
            }
        }
        rootView.addView(view);
    }

    @Override
    public void onRankStart() {
        if (englishPk.canUsePK == 1 && englishPk.hasGroup == 0) {
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
                            pkTeamEntity = (PkTeamEntity) objects[0];
                            logger.d("onRankStart:onDataSucess:Entity=" + pkTeamEntity);
                            if (pkTeamEntity == null) {
                                return;
                            }
                            if (!teamEnd) {
                                teamPkRankPager.setPkTeamEntity(pkTeamEntity);
                            } else {
                                addTop();
                            }
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            super.onDataFail(errStatus, failMsg);
                            logger.d("onDataFail:errStatus=" + errStatus + ",destory=" + destory);
                            if (!destory) {
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
    }

    @Override
    public void onModeChange(String mode) {
        this.mode = mode;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            teamEnd = true;
            if (teamPkRankPager != null) {
                rootView.removeView(teamPkRankPager.getRootView());
            }
        }
    }

    @Override
    public void onRankResult() {

    }

    @Override
    public void onRankLead(final EnTeamPkRankEntity enTeamPkRankEntity) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                teamPkLeadPager = new TeamPkLeadPager(mContext, enTeamPkRankEntity, pattern);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                if (pattern != 2) {
                    layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                }
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

    @Override
    public void destory() {
        destory = true;
    }
}

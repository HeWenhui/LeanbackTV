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
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
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
    /** 得到本组信息重试 */
    int getSelfTeamInfoTimes = 1;
    private RelativeLayout rootView;
    private TeamPkRankPager teamPkRankPager;
    private boolean teamEnd = false;
    private TeamPkRankResultPager teamPkRankResultPager;
    private TeamPkLeadPager teamPkLeadPager;
    private EnTeamPkHttp enTeamPkHttp;
    private LiveGetInfo getInfo;
    private int pattern;
    private boolean destory = false;
    private String mode;
    private LiveGetInfo.EnglishPk englishPk;
    private PkTeamEntity pkTeamEntity;
    private int reportTimes = 1;
    private LogToFile mLogtf;

    public EnTeamPkBll(Context context) {
        super(context);
        mLogtf = new LogToFile(context, TAG);
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
        if (pkTeamEntity == null) {
            if (englishPk.hasGroup == 0) {
                reportStuInfo();
            } else {
                getEnglishPkGroup();
            }
        }
//        addTop();
    }

    private void reportStuInfo() {
        enTeamPkHttp.reportStuInfo(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objects) {
                if (pkTeamEntity == null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!destory && pkTeamEntity == null) {
                                getEnglishPkGroup();
                            }
                        }
                    }, 2000);
                }
            }
        });
    }

    private void getEnglishPkGroup() {
        enTeamPkHttp.getEnglishPkGroup(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objects) {
                pkTeamEntity = (PkTeamEntity) objects[0];
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                mLogtf.d("onDataFail:destory=" + destory + ",errStatus=" + errStatus + ",times=" + reportTimes);
                if (!destory && errStatus == 1) {
                    if (reportTimes++ > 3) {
                        return;
                    }
                    reportStuInfo();
                }
            }
        });
    }

    private void addTop() {
        logger.d("addTop:myteam=" + pkTeamEntity.getMyTeam());
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_join, rootView, false);
        TextView tv_livevideo_en_teampk_top_name = view.findViewById(R.id.tv_livevideo_en_teampk_top_name);
        ImageView iv_livevideo_en_teampk_top_img = view.findViewById(R.id.iv_livevideo_en_teampk_top_img);
        if (pkTeamEntity != null) {
            tv_livevideo_en_teampk_top_name.setText("欢迎加入" + EnTeamPkConfig.TEAM_NAMES[pkTeamEntity.getMyTeam()]);
            iv_livevideo_en_teampk_top_img.setImageResource(EnTeamPkConfig.TEAM_RES[pkTeamEntity.getMyTeam()]);
        } else {
//            if (AppConfig.DEBUG) {
//                tv_livevideo_en_teampk_top_name.setText("欢迎加入" + EnTeamPkConfig.TEAM_NAMES[0]);
//                iv_livevideo_en_teampk_top_img.setImageResource(EnTeamPkConfig.TEAM_RES[0]);
//            }
            return;
        }
        rootView.addView(view);
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.removeView(view);
            }
        }, 2000);
    }

    @Override
    public void onRankStart() {
        mLogtf.d("onRankStart:can=" + englishPk.canUsePK + ",has=" + englishPk.hasGroup + ",mode=" + mode);
        if (englishPk.canUsePK == 1 && englishPk.hasGroup == 0 && pkTeamEntity == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (LiveTopic.MODE_TRANING.equals(mode)) {
                        teamPkRankPager = new TeamPkRankPager(mContext);
                        teamPkRankPager.setOnTeamSelect(new TeamPkRankPager.OnTeamSelect() {
                            @Override
                            public void onTeamSelect(PkTeamEntity pkTeamEntity) {
                                rootView.removeView(teamPkRankPager.getRootView());
                                teamPkRankResultPager = new TeamPkRankResultPager(mContext, pkTeamEntity);
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                                rootView.addView(teamPkRankResultPager.getRootView(), layoutParams);
                                teamPkRankResultPager.setOnStartClick(new TeamPkRankResultPager.OnStartClick() {
                                    @Override
                                    public void onClick() {
                                        if (teamPkRankResultPager != null) {
                                            rootView.removeView(teamPkRankResultPager.getRootView());
                                            teamPkRankResultPager = null;
                                        }
                                    }
                                });
                            }
                        });
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                        rootView.addView(teamPkRankPager.getRootView(), layoutParams);
                        enTeamPkHttp.getEnglishPkGroup(new AbstractBusinessDataCallBack() {
                            AbstractBusinessDataCallBack callBack = this;

                            @Override
                            public void onDataSucess(Object... objects) {
                                pkTeamEntity = (PkTeamEntity) objects[0];
                                logger.d("onRankStart:onDataSucess:Entity=null?" + (pkTeamEntity == null) + ",teamEnd=" + teamEnd);
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
                                logger.d("onRankStart:onDataFail:errStatus=" + errStatus + ",destory=" + destory);
                                if (!destory || getSelfTeamInfoTimes > 9) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            enTeamPkHttp.getEnglishPkGroup(callBack);
                                        }
                                    }, (getSelfTeamInfoTimes++) * 1000);
                                }
                            }
                        });
                    } else {
                        enTeamPkHttp.getEnglishPkGroup(new AbstractBusinessDataCallBack() {
                            AbstractBusinessDataCallBack callBack = this;

                            @Override
                            public void onDataSucess(Object... objects) {
                                pkTeamEntity = (PkTeamEntity) objects[0];
                                logger.d("onRankStart:onDataSucess:Entity=" + pkTeamEntity);
                                if (pkTeamEntity == null) {
                                    return;
                                }
                                addTop();
                            }

                            @Override
                            public void onDataFail(int errStatus, String failMsg) {
                                super.onDataFail(errStatus, failMsg);
                                logger.d("onDataFail:errStatus=" + errStatus + ",destory=" + destory);
                                if (!destory || getSelfTeamInfoTimes > 9) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            enTeamPkHttp.getEnglishPkGroup(callBack);
                                        }
                                    }, (getSelfTeamInfoTimes++) * 1000);
                                }
                            }
                        });
                    }
                }
            });
            if (LiveTopic.MODE_TRANING.equals(mode)) {
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
                }, 10000);
            }
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
    public void onRankLead(final EnTeamPkRankEntity enTeamPkRankEntity, final int type) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                teamPkLeadPager = new TeamPkLeadPager(mContext, enTeamPkRankEntity, type, pattern);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                if (pattern != 2) {
                    layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                }
                rootView.addView(teamPkLeadPager.getRootView(), layoutParams);
                final TeamPkLeadPager finalTeamPkLeadPager = teamPkLeadPager;
                teamPkLeadPager = null;
                int delay = type == TeamPkLeadPager.TEAM_TYPE_2 ? 10000 : 5000;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rootView.removeView(finalTeamPkLeadPager.getRootView());
                    }
                }, delay);
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

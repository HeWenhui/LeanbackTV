package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.EnglishPk;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.SmallEnglishMicTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkLeadPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkRankResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;

public class EnTeamPkBll extends BaseBll implements EnTeamPkAction, EnglishPkUpdata {
    private Handler handler = new Handler(Looper.getMainLooper());
    /** 得到本组信息重试 */
    private int getSelfTeamInfoTimes = 1;
    private LiveViewAction liveViewAction;
    private Handler mHandler = new Handler(Looper.getMainLooper());
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
    /** 分队仪式开始 */
    private boolean isRankStart = false;
    private PkTeamEntity pkTeamEntity;
    private int reportTimes = 1;
    /** 显示上方提示 */
    private boolean hasAddTop = false;
    private LogToFile mLogtf;

    public EnTeamPkBll(Context context, String liveId) {
        super(context);
        mLogtf = new LogToFile(context, TAG);
        String string = LiveShareDataManager.getInstance().getString(ShareDataConfig.LIVE_ENPK_MY_TOP, "");
        if (("" + string).contains((liveId + ","))) {
            hasAddTop = true;
        }
    }

    public boolean isHasAddTop() {
        return hasAddTop;
    }

    public void setEnTeamPkHttp(EnTeamPkHttp enTeamPkHttp) {
        this.enTeamPkHttp = enTeamPkHttp;
    }

    public void setRootView(LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
    }

    @Override
    public void setPkTeamEntity(PkTeamEntity pkTeamEntity) {
        PkTeamEntity oldPkTeamEntity = this.pkTeamEntity;
        this.pkTeamEntity = pkTeamEntity;
//        if (AppConfig.DEBUG) {
//            teamPkRankResultPager = new TeamPkRankResultPager(mContext, pkTeamEntity);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//            layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
//            rootView.addView(teamPkRankResultPager.getRootView(), layoutParams);
//            teamPkRankResultPager.setOnStartClick(new TeamPkRankResultPager.OnStartClick() {
//                @Override
//                public void onClick() {
//                    if (teamPkRankResultPager != null) {
//                        rootView.removeView(teamPkRankResultPager.getRootView());
//                        teamPkRankResultPager = null;
//                    }
//                }
//            });
//        }
        //主讲的时候，没有分队，显示上部栏
        if (oldPkTeamEntity == null && pkTeamEntity != null) {
            mLogtf.d("setPkTeamEntity:getInfo=null?" + (getInfo == null) + ",isRankStart=" + isRankStart + ",mode=" + mode + ",where=" + pkTeamEntity.getCreateWhere());
            if (getInfo != null && !isRankStart && LiveTopic.MODE_CLASS.equals(mode)) {
                if (pkTeamEntity.getCreateWhere() != PkTeamEntity.CREATE_TYPE_LOCAL) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addTop("setPkTeamEntity");
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        pattern = getInfo.getPattern();
        mode = getInfo.getMode();
        englishPk = getInfo.getEnglishPk();
        if (pkTeamEntity == null) {
            if (englishPk.hasGroup == EnglishPk.HAS_GROUP_NO) {
                reportStuInfo();
            } else {
                getEnglishPkGroup();
            }
        }
//        addTop();
    }

    @Override
    public void hideTeam() {
        if (teamPkLeadPager != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (teamPkLeadPager != null) {
                        liveViewAction.removeView(teamPkLeadPager.getRootView());
                        teamPkLeadPager = null;
                    }
                }
            });
        }
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
                PkTeamEntity pkTeamEntity = (PkTeamEntity) objects[0];
                setPkTeamEntity(pkTeamEntity);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                mLogtf.d("onDataFail:destory=" + destory + ",errStatus=" + errStatus + ",times=" + reportTimes);
                if (pkTeamEntity != null) {
                    return;
                }
                if (!destory && errStatus == 1) {
                    if (reportTimes++ > 3) {
                        return;
                    }
                    reportStuInfo();
                }
            }
        });
    }

    public void saveTop() {
        String string = LiveShareDataManager.getInstance().getString(ShareDataConfig.LIVE_ENPK_MY_TOP, "");
        String[] liveIds = string.split(",");
        if (liveIds.length > 6) {
            string = "";
        }
        LiveShareDataManager.getInstance().put(ShareDataConfig.LIVE_ENPK_MY_TOP, string + "" + getInfo.getId() + ",");
    }

    /**
     * 上方提示
     *
     * @param method
     */
    private void addTop(String method) {
        mLogtf.d("addTop:myteam=" + pkTeamEntity.getMyTeam() + ",method=" + method + ",hasAddTop=" + hasAddTop);
        if (hasAddTop) {
            return;
        }
        saveTop();
        hasAddTop = true;
        final View view = liveViewAction.inflateView(R.layout.layout_livevideo_en_team_join);
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
        liveViewAction.addView(view);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                liveViewAction.removeView(view);
            }
        }, 2000);
    }

    @Override
    public void onRankStart(final boolean showPk) {
        if (LiveTopic.MODE_TRANING.equals(mode) && showPk) {
            isRankStart = true;
        }
        if (pkTeamEntity == null) {
            mLogtf.d("onRankStart:can=" + englishPk.canUsePK + ",has=" + englishPk.hasGroup + ",mode=" + mode + ",showPk=" + showPk);
        } else {
            mLogtf.d("onRankStart:can=" + englishPk.canUsePK + ",has=" + englishPk.hasGroup + ",mode=" + mode + ",showPk=" + showPk + ",where=" + pkTeamEntity.getCreateWhere());
        }
        if (englishPk.canUsePK == 1 && (pkTeamEntity == null || pkTeamEntity.getCreateWhere() != PkTeamEntity.CREATE_TYPE_LOCAL)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (LiveTopic.MODE_TRANING.equals(mode) && showPk) {
                        saveTop();
                        hasAddTop = true;
                        teamPkRankPager = new TeamPkRankPager(mContext);
                        teamPkRankPager.setOnTeamSelect(new TeamPkRankPager.OnTeamSelect() {
                            @Override
                            public void onTeamSelect(PkTeamEntity pkTeamEntity) {
                                liveViewAction.removeView(teamPkRankPager.getRootView());
                                teamPkRankResultPager = new TeamPkRankResultPager(mContext, pkTeamEntity);
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                                liveViewAction.addView(teamPkRankResultPager.getRootView(), layoutParams);
                                teamPkRankResultPager.setOnStartClick(new TeamPkRankResultPager.OnStartClick() {
                                    @Override
                                    public void onClick() {
                                        if (teamPkRankResultPager != null) {
                                            liveViewAction.removeView(teamPkRankResultPager.getRootView());
                                            teamPkRankResultPager = null;
                                        }
                                    }
                                });
                            }
                        });
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                        liveViewAction.addView(teamPkRankPager.getRootView(), layoutParams);
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
                                    addTop("onRankStart1");
                                }
                            }

                            @Override
                            public void onDataFail(int errStatus, String failMsg) {
                                super.onDataFail(errStatus, failMsg);
                                logger.d("onRankStart:onDataFail:errStatus=" + errStatus + ",destory=" + destory);
                                if (pkTeamEntity != null) {
                                    return;
                                }
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
                                PkTeamEntity oldPkTeamEntity = EnTeamPkBll.this.pkTeamEntity;
                                pkTeamEntity = (PkTeamEntity) objects[0];
                                logger.d("onRankStart:onDataSucess:Entity=" + pkTeamEntity + ",oldPkTeamEntity=null?" + (oldPkTeamEntity == null));
                                if (pkTeamEntity == null) {
                                    return;
                                }
                                addTop("onRankStart2");
                            }

                            @Override
                            public void onDataFail(int errStatus, String failMsg) {
                                super.onDataFail(errStatus, failMsg);
                                logger.d("onDataFail:errStatus=" + errStatus + ",destory=" + destory);
                                if (pkTeamEntity != null) {
                                    return;
                                }
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
                            liveViewAction.removeView(teamPkRankPager.getRootView());
                        }
                    }
                }, 10000);
            }
        }
    }

    @Override
    public void onModeChange(String mode, boolean haveTeamRun) {
        hideTeam();
        this.mode = mode;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            teamEnd = true;
            if (teamPkRankPager != null) {
                liveViewAction.removeView(teamPkRankPager.getRootView());
            }
            if (pkTeamEntity == null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pkTeamEntity == null) {
                            reportStuInfo();
                        }
                    }
                }, 2000);
            } else {
                if (haveTeamRun) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addTop("onModeChange");
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onRankResult() {

    }

    long before = 0;
    PraiseRunnable praiseRunnable = new PraiseRunnable();

    class PraiseRunnable implements Runnable {
        String testId;
        ArrayList<TeamMemberEntity> myTeamEntitys;

        @Override
        public void run() {
            logger.d("praiseRunnable");
            before = 0;
            final ArrayList<TeamMemberEntity> reportTeamEntitys = new ArrayList<>();
            for (int i = 0; i < myTeamEntitys.size(); i++) {
                TeamMemberEntity reportTeamMemberEntity = new TeamMemberEntity();
                TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                reportTeamMemberEntity.copy(teamMemberEntity);
                teamMemberEntity.thisPraiseCount = 0;
                reportTeamEntitys.add(reportTeamMemberEntity);
            }
            enTeamPkHttp.reportStuLike(testId, reportTeamEntitys, new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {

                }

                @Override
                public void onDataFail(int errStatus, String failMsg) {
                    super.onDataFail(errStatus, failMsg);
                    for (int i = 0; i < myTeamEntitys.size(); i++) {
                        TeamMemberEntity reportTeamMemberEntity = reportTeamEntitys.get(i);
                        TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                        teamMemberEntity.thisPraiseCount += reportTeamMemberEntity.thisPraiseCount;
                    }
                }
            });
        }
    }

    @Override
    public void onRankLead(final EnTeamPkRankEntity enTeamPkRankEntity, final String testId, final int type) {
        if (enTeamPkRankEntity.getNoShow() == 1) {
            int win = enTeamPkRankEntity.getMyTeamTotal() - enTeamPkRankEntity.getOpTeamTotal();
            String s = "";
            if (win > 0) {
                s = "1";
                int lastM = enTeamPkRankEntity.getMyTeamTotal() - enTeamPkRankEntity.getMyTeamCurrent();
                int lastO = enTeamPkRankEntity.getOpTeamTotal() - enTeamPkRankEntity.getOpTeamCurrent();
                int lastWin = lastM - lastO;
                if (lastWin <= 0) {
                    s = "2";
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SmallEnglishMicTipDialog smallEnglishMicTipDialog = new SmallEnglishMicTipDialog(mContext);
                            smallEnglishMicTipDialog.setText("恭喜反超对手");
                            smallEnglishMicTipDialog.showDialogAutoClose(2000);
                        }
                    });
//                    XESToastUtils.showToast(mContext, "恭喜反超对手");
                }
            }
            mLogtf.d("onRankLead:s=" + s);
            //全身直播得仪式结束以后，请求本场成就
            if (pattern == LiveVideoConfig.LIVE_PATTERN_2) {
                UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
                if (updateAchievement != null) {
                    updateAchievement.getStuGoldCount("onRankLead", UpdateAchievement.GET_TYPE_TEAM);
                }
            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    teamPkLeadPager = new TeamPkLeadPager(mContext, enTeamPkRankEntity, testId, type, pattern, new TeamPkLeadPager.OnClose() {
                        @Override
                        public void close(BasePager basePager) {
                            liveViewAction.removeView(basePager.getRootView());
                        }
                    });
                    teamPkLeadPager.setOnStudyClick(new TeamPkLeadPager.OnStudyClick() {
                        @Override
                        public void onStudyClick(ArrayList<TeamMemberEntity> myTeamEntitys) {
                            praiseRunnable.testId = testId;
                            praiseRunnable.myTeamEntitys = myTeamEntitys;
                            handler.removeCallbacks(praiseRunnable);
                            handler.postDelayed(praiseRunnable, 1000);
                        }
                    });
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    if (pattern != LiveVideoConfig.LIVE_PATTERN_2) {
                        layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                    }
                    liveViewAction.addView(teamPkLeadPager.getRootView(), layoutParams);
                    teamPkLeadPager.getRootView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View view) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View view) {
                            //全身直播得仪式结束以后，请求本场成就
                            if (pattern == LiveVideoConfig.LIVE_PATTERN_2) {
                                UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
                                if (updateAchievement != null) {
                                    updateAchievement.getStuGoldCount("onViewDetachedFromWindow", UpdateAchievement.GET_TYPE_TEAM);
                                }
                            }
                        }
                    });
                }
            });
        }
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
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (teamPkLeadPager != null) {
            if (pattern != 2) {
                View mView = teamPkLeadPager.getRootView();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                int rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x3;
                if (layoutParams.rightMargin != rightMargin) {
                    layoutParams.rightMargin = rightMargin;
                    mView.setLayoutParams(layoutParams);
                    teamPkLeadPager.setVideoLayout(liveVideoPoint);
                }
            }
        }
    }

    @Override
    public void onStuLike(String testId, final ArrayList<TeamMemberEntity> teamMemberEntities) {
        if (teamPkLeadPager != null) {
            if (TextUtils.equals(testId, teamPkLeadPager.getTestId())) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (teamPkLeadPager != null) {
                            teamPkLeadPager.onStuLike(teamMemberEntities);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void destory() {
        destory = true;
    }
}

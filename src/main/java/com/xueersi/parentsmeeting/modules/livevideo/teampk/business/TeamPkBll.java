package com.xueersi.parentsmeeting.modules.livevideo.teampk.business;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.TeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ScienceAnswerResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentCoinAndTotalEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkStar;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkStuProgress;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.NativeVoteRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeachPraiseRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.UpdatePkState;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.entity.RedPackageEvent;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.http.LocalTeamPkTeamInfo;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkAqResultFlayPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.event.TeamPkTeamInfoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.http.TeamPkHttp;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkAqResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkAwardPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkContributionPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkEndPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkImprovePager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkStarsPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkTeamSelectPager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.page.TeamPkTeamSelectingPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @author chekun
 * created  at 2018/4/12
 * 战队PK 相关业务处理
 */
public class TeamPkBll extends LiveBaseBll implements NoticeAction, TopicAction, MessageAction {
    static{
        Log.d("TeamPkBll","TeamPkBll:static");
    }
    public static final String TEAMPK_URL_FIFTE = "http://addenergyandgold.com/";
    /**
     * 开宝箱类型 班级宝箱列表
     */
    private static final int CHEST_TYPE_CLASS = 1;
    /**
     * 开宝箱类型 学生自己宝箱
     */
    private static final int CHEST_TYPE_STUDENT = 2;
    /**
     * 投票题 奖励能量
     */
    private static final int VOTE_ADD_ENERGY = 3;
    /**
     * 老师表扬 奖励能量
     */
    public static final int VOTE_TEACHER_PRAISE_ENERGY = 5;
    private Activity mActivity;
    /**
     * 战队PK rootView
     */
    private RelativeLayout rlTeamPkContent;

    /**
     * 展示飞星动画等顶层UI 父布局
     **/
    private RelativeLayout rlTopLayerContent;
    private TeamPkHttp teamPkHttp;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo roomInitInfo;
    private boolean primaryClass = false;
    private LiveHttpResponseParser mHttpResponseParser;
    private TeamPkTeamInfoEntity teamInfoEntity;
    private TeamPkBasePager mFocusPager;

    private TeamPkBasePager mTopLayerPager;

    private static final String OPEN_STATE_OPEN = "1";

    private static final String OPEN_STATE_CLOSE = "0";


    /**
     * 战队名称
     **/
    String mTeamName = "";
    /**
     * pk对手
     */
    private static final int PK_RESULT_TYPE_ADVERSARY = 1;
    /**
     * 学生 当场次答题pk 结果
     */
    private static final int PK_RESULT_TYPE_FINAL_PKRESULT = 2;
    /**
     * 学生 每题的PK 结果
     */
    private static final int PK_RESULT_TYPE_PKRESULT = 3;
    @Deprecated
    private boolean isTopicHandled = false;

    private boolean isWin;
    private TeamPkStateLayout pkStateRootView;
    /**
     * 直播间内答题 H5 答题结果页面关闭事件队列
     */
    private List<LiveRoomH5CloseEvent> h5CloseEvents;

    /**
     * 是否是带碎片的直播间
     */
    private boolean isAIPartner;

    /**
     * log埋点 nonce
     * 主要记录 老师结束答题时下发的 nonce  作为 埋点上传log 参数
     */
    private String nonce;
    /**
     * 当前pk状态
     */
    private StudentCoinAndTotalEnergyEntity mCurrentPkState;

    /**
     * 当前老师模式
     */
    private String mTeacherMode = LiveTopic.MODE_TRANING;
    /**
     * 战队成员信息
     **/
    private List<TeamMate> mTeamMates;
    private TeamPkPraiseBll mPraiseBll;
    /**
     * 分队仪式是否是有notice 触发的
     */
    private boolean teamSelectByNotice = false;


    public TeamPkBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mActivity = context;
    }


    //    public LiveHttpManager getmHttpManager() {
//        return mHttpManager;
//    }
    public String getLiveId() {
        return roomInitInfo.getId();
    }

    private void setRoomInitInfo(LiveGetInfo roomInfo) {
        roomInitInfo = roomInfo;
    }

    public LiveGetInfo getRoomInitInfo() {
        return roomInitInfo;
    }


    private void attachToRootView() {
        initData();
        rlTeamPkContent = new RelativeLayout(mActivity);
        rlTeamPkContent.setId(R.id.rl_livevideo_content_teampk);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addView(rlTeamPkContent, params);
        rlTopLayerContent = new RelativeLayout(mActivity);
        rlTopLayerContent.setId(R.id.rl_livevideo_content_teampk_top);
        addView(rlTopLayerContent, params);

        showPkStateLayout();
        registLayoutListener();
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (isAvailable) {
            this.mTeacherMode = mode;
            if (isHalfBodyLiveRoom()) {
                //延时5秒 适配切屏动画
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showPkStateLayout();
                    }
                }, 5000);
            }
        }
    }

    /**
     * 是否是半身直播 直播间
     *
     * @return
     */
    public boolean isHalfBodyLiveRoom() {
        //logger.e( "========>isHalfBodyLiveRoom:" + roomInitInfo + ":" + roomInitInfo.getPattern());
        return roomInitInfo != null && (roomInitInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY || primaryClass);
    }

    /**
     * 显示 场次答题PK 结果
     * notice  topic  中通知调用
     */
    public void showPkResult() {
        getStuPkResult();
    }

    private void getStuPkResult() {
        getTeamPkHttp().stuPKResult(mLiveId, getNewTeamId("getStuPkResult"),
                roomInitInfo.getStudentLiveInfo().getClassId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        StudentPkResultEntity resultEntity = mHttpResponseParser.parseStuPkResult(responseEntity);

                        if (resultEntity != null && resultEntity.getMyTeamResultInfo() != null && resultEntity.getCompetitorResultInfo() != null) {
                            TeamPkBll.this.isWin = resultEntity.getMyTeamResultInfo().getEnergy() >= resultEntity
                                    .getCompetitorResultInfo().getEnergy();
                            showPkResultScene(resultEntity, PK_RESULT_TYPE_FINAL_PKRESULT);
                            TeamPkLog.showPkResult(contextLiveAndBackDebug, isWin);
                        } else {
                            XESToastUtils.showToast(mActivity, "获取场次pk结果失败");
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }
                });
    }

    /**
     * 显示开宝箱场景
     */
    public void showOpenBoxScene(boolean isWin) {
        showAwardGetScene(CHEST_TYPE_STUDENT, null, isWin);
    }


    /**
     * 显示贡献之星页面
     *
     * @param data
     */
    public void showContributionPage(TeamEnergyAndContributionStarEntity data) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkEndPager)) {
            TeamPkContributionPager contributionPager = new TeamPkContributionPager(mActivity, TeamPkBll.this, data);
            addPager(contributionPager);
        }
    }

    public boolean isWin() {
        return isWin;
    }

    /**
     * 从topic 中恢复 开宝箱场景
     */
//    public void resumeOpenBoxScene() {
//        // 请求接口 获取胜负关系
//        mHttpManager.liveStuGoldAndTotalEnergy(mLiveBll.getLiveId(),
//                roomInitInfo.getStudentLiveInfo().getTeamId(),
//                roomInitInfo.getStudentLiveInfo().getClassId(),
//                roomInitInfo.getStuId(), new HttpCallBack() {
//                    @Override
//                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                        StudentCoinAndTotalEnergyEntity energyEntity = mHttpResponseParser.parseStuCoinAndTotalEnergy
//                                (responseEntity);
//                        if (energyEntity != null) {
//                            showOpenBoxScene(energyEntity.getMyEnergy() >= energyEntity.getCompetitorEnergy());
//                        }
//                    }
//
//                    @Override
//                    public void onPmFailure(Throwable error, String msg) {
//                        super.onPmFailure(error, msg);
//                    }
//                });
//    }

    /**
     * 显示 战队宝箱领取情况
     */
    public void showClassChest() {
        getClassChestResult();
    }

    /**
     * 获取战队开宝箱结果
     */
    private void getClassChestResult() {
        getTeamPkHttp().getClassChestResult(mLiveId, roomInitInfo.getStuId(),
                getNewTeamId("getClassChestResult"), roomInitInfo.getStudentLiveInfo().getClassId()
                , isAIPartner, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        ClassChestEntity classChestEntity = mHttpResponseParser.parseClassChest(responseEntity);
                        showAwardGetScene(CHEST_TYPE_CLASS, classChestEntity, isWin);
                        TeamPkLog.showClassGoldInfo(contextLiveAndBackDebug, classChestEntity.isMe());
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }
                });

    }


    /**
     * 获取每题的 pk 结果
     *
     * @param event
     */
    private void getEnergyNumAndContributionStar(LiveRoomH5CloseEvent event) {
        String testId = "";
        String testPlan = "";
        if (event.getH5Type() == LiveRoomH5CloseEvent.H5_TYPE_EXAM) {
            testPlan = event.getId();
        } else {
            testId = event.getId();
        }
        final String eventId = getLogEventId(event.getH5Type());
        EnglishH5Entity englishH5Entity = event.getEnglishH5Entity();
        boolean newCourseWare = englishH5Entity != null && englishH5Entity.getNewEnglishH5();
        if (newCourseWare) {
            getTeamPkHttp().teamEnergyNumAndContributionmulStar(mLiveId,
                    getNewTeamId("getClassChestResult"),
                    roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), englishH5Entity.getReleasedPageInfos(),
                    englishH5Entity.getClassTestId(), englishH5Entity.getPackageSource(), new
                            HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    TeamEnergyAndContributionStarEntity entity = mHttpResponseParser
                                            .parseTeanEnergyAndContribution(responseEntity);
                                    //展示当前pk 结果
                                    if (entity != null && entity.getMyTeamEngerInfo() != null && entity.getCompetitorEngerInfo() != null) {
                                        showPkResultScene(entity, PK_RESULT_TYPE_PKRESULT);
                                        if (mLiveBll != null) {
                                            TeamPkLog.showPerTestPk(contextLiveAndBackDebug, entity.isMe(), getNonce(), eventId,
                                                    entity.getMyTeamEngerInfo().getTeamName());
                                        }
                                    } else {
                                        XESToastUtils.showToast(mActivity, "获取贡献之星失败");
                                    }
                                }
                            });

        } else {
            getTeamPkHttp().teamEnergyNumAndContributionStar(mLiveId,
                    getNewTeamId("teamEnergyNumAndContributionStar"),
                    roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), testId, testPlan, new
                            HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    TeamEnergyAndContributionStarEntity entity = mHttpResponseParser
                                            .parseTeanEnergyAndContribution(responseEntity);
                                    if (entity != null && entity.getCompetitorEngerInfo() != null && entity.getMyTeamEngerInfo() != null) {
                                        showPkResultScene(entity, PK_RESULT_TYPE_PKRESULT);
                                        if (mLiveBll != null) {
                                            TeamPkLog.showPerTestPk(contextLiveAndBackDebug, entity.isMe(), getNonce(), eventId,
                                                    entity.getMyTeamEngerInfo().getTeamName());
                                        }
                                    } else {
                                        XESToastUtils.showToast(mActivity, "获取贡献之星失败");
                                    }
                                }
                            });
        }
    }

    /**
     * 获取答题结果 埋点统计eventId
     *
     * @param h5Type
     * @return
     */
    private String getLogEventId(int h5Type) {
        String eventId;
        switch (h5Type) {
            case LiveRoomH5CloseEvent.H5_TYPE_EXAM:
                eventId = "live_exam";
                break;
            case LiveRoomH5CloseEvent.H5_TYPE_COURSE:
                eventId = "live_h5waretest";
                break;
            case LiveRoomH5CloseEvent.H5_TYPE_INTERACTION:
                eventId = "live_h5test";
                break;
            default:
                eventId = "";
        }
        return eventId;
    }

    @Deprecated
    public void setTopicHandled(boolean topicHandled) {
        isTopicHandled = topicHandled;
    }

    @Deprecated
    public boolean isTopicHandled() {
        return isTopicHandled;
    }

    private void initData() {
        mHttpResponseParser = new LiveHttpResponseParser(mActivity);
        EventBus.getDefault().register(this);

    }

    public LiveHttpResponseParser getmHttpResponseParser() {
        return mHttpResponseParser;
    }


    /**
     * 开启分队仪式
     *
     * @param primary             小班体验
     * @param showTeamSelectScene
     */
    public void startTeamSelect(boolean primary, boolean showTeamSelectScene) {
        getTeamInfo(primary, showTeamSelectScene);
    }

    public void stopTeamSelect() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mFocusPager != null && mFocusPager instanceof TeamPkTeamSelectPager) {
                    ((TeamPkTeamSelectPager) mFocusPager).closeTeamSelectPager();
                } else if (mFocusPager != null && mFocusPager instanceof TeamPkTeamSelectingPager) {
                    ((TeamPkTeamSelectingPager) mFocusPager).closeTeamSelectPager();
                }
            }
        });
    }

    public TeamPkHttp getTeamPkHttp() {
        if (teamPkHttp == null) {
            teamPkHttp = new TeamPkHttp(activity, mHttpManager);
        }
        return teamPkHttp;
    }

    /**
     * 获取战队信息
     *
     * @param primary             小班体验
     * @param showTeamSelectScene 显示分队仪式
     */
    private void getTeamInfo(final boolean primary, final boolean showTeamSelectScene) {
        HttpCallBack callBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (primary) {
                    TeamPkTeamInfoEntity teamInfoEntityres = parseTeamInfoPrimary(responseEntity);
                    if (teamInfoEntityres == null) {
                        return;
                    }
                    teamInfoEntity = teamInfoEntityres;
                } else {
                    teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                }
                if (showTeamSelectScene) {
                    showTeamSelectScene(primary);
                }
                if (primary) {
                    LiveEventBus.getDefault(mContext).post(new TeamPkTeamInfoEvent(teamInfoEntity, responseEntity));
                    logger.d("getTeamInfo:runnables=" + runnables.size());
                    while (!runnables.isEmpty()) {
                        Runnable runnable = runnables.remove(0);
                        runnable.run();
                    }
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
            }
        };
        if (primary) {
            getTeamPkHttp().getMyTeamInfo(roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(), LiveAppUserInfo.getInstance().getPsimId(), callBack);
        } else {
            getTeamPkHttp().getTeamInfo(roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStudentLiveInfo().getTeamId(), callBack);
        }
    }

    /**
     * 显示分队仪式 场景
     */
    private void showTeamSelectScene(boolean primary) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkTeamSelectingPager)) {
            logger.e("====>showTeamSelectScene:" + mFocusPager);
            if (mFocusPager != null && mFocusPager instanceof TeamPkTeamSelectPager) {
                return;
            }
            TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(mActivity, this, roomInitInfo);
            addPager(teamSelectPager);
            teamSelectPager.setData(teamInfoEntity);
            teamSelectPager.startTeamSelect();
            TeamPkLog.showCreateTeam(contextLiveAndBackDebug);
        }
    }

    /**
     * 中途进入战斗选择
     */
    public void enterTeamSelectScene() {
        TeamPkLog.clickFastEnter(contextLiveAndBackDebug);
        if (primaryClass) {
            getTeamPkHttp().getMyTeamInfo(roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(), LiveAppUserInfo.getInstance().getPsimId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            TeamPkTeamInfoEntity teamInfoEntityres = parseTeamInfoPrimary(responseEntity);
                            if (teamInfoEntityres == null) {
                                return;
                            }
                            teamInfoEntity = teamInfoEntityres;
                            showTeamSelectedSence();
                        }
                    });
        } else {
            getTeamPkHttp().getTeamInfo(roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                            showTeamSelectedSence();
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                        }
                    });
        }

    }


    /**
     * 显示 战队已选中 UI
     */
    private void showTeamSelectedSence() {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkTeamSelectPager)) {
            TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(mActivity, this, roomInitInfo);
            addPager(teamSelectPager);
            teamSelectPager.setData(teamInfoEntity);
            teamSelectPager.showTeamSelectedScene(true);
            TeamPkLog.showCreateTeam(contextLiveAndBackDebug);
        }
    }

    /**
     * 显示开宝箱
     *
     * @param type
     * @param data
     * @param isWin
     */
    public void showAwardGetScene(int type, final Object data, final boolean isWin) {
        //   if (mFocusPager == null || !(mFocusPager instanceof TeamPkAwardPager)) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkAwardPager)) {
            if (type == CHEST_TYPE_CLASS) {
                //从pk结果页面直接跳到 贡献之星
                if (mFocusPager != null && mFocusPager instanceof TeamPkResultPager) {
                    ((TeamPkResultPager) mFocusPager).closePkResultPager();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TeamPkAwardPager awardGetPager = new TeamPkAwardPager(mActivity, TeamPkBll.this);
                            addPager(awardGetPager);
                            awardGetPager.showClassChest((ClassChestEntity) data);
                        }
                    }, 1000);
                } else {
                    TeamPkAwardPager awardGetPager = new TeamPkAwardPager(mActivity, this);
                    addPager(awardGetPager);
                    awardGetPager.showClassChest((ClassChestEntity) data);
                }

            } else if (type == CHEST_TYPE_STUDENT) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TeamPkAwardPager awardGetPager = new TeamPkAwardPager(mActivity, TeamPkBll.this);
                        addPager(awardGetPager);
                        awardGetPager.showBoxLoop();
                    }
                }, 1000);
            }
        } else if (mFocusPager != null && (mFocusPager instanceof TeamPkAwardPager)) {
            //由开宝箱直接切换到幸运之星页面
            if (type == CHEST_TYPE_CLASS) {
                ((TeamPkAwardPager) mFocusPager).closeAwardPager();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TeamPkAwardPager awardGetPager = new TeamPkAwardPager(mActivity, TeamPkBll.this);
                        addPager(awardGetPager);
                        awardGetPager.showClassChest((ClassChestEntity) data);
                    }
                }, 1000);
            }
        }
    }

    /**
     * 显示分队进行中
     */
    public void showTeamSelecting() {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkTeamSelectPager)) {
            logger.e("====>showTeamSelecting:");
            rlTeamPkContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    TeamPkTeamSelectingPager selectingPager = new TeamPkTeamSelectingPager(mActivity, TeamPkBll
                            .this);
                    addPager(selectingPager);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        rlTeamPkContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        rlTeamPkContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }

    /**
     * 展示pk 信息场景
     *
     * @param data
     */
    public void showPkResultScene(Object data, int type) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkResultPager)) {
            TeamPkResultPager resultPager = new TeamPkResultPager(mActivity, this);
            addPager(resultPager);
            setRight(resultPager);
            switch (type) {
                case PK_RESULT_TYPE_ADVERSARY:
                    resultPager.showPkAdversary((TeamPkAdversaryEntity) data);
                    break;
                case PK_RESULT_TYPE_FINAL_PKRESULT:
                    resultPager.showFinalPkResult(((StudentPkResultEntity) data));
                    break;
                case PK_RESULT_TYPE_PKRESULT:
                    resultPager.showCurrentResult((TeamEnergyAndContributionStarEntity) data);
                    if (data != null && TextUtils.isEmpty(mTeamName)) {
                        mTeamName = ((TeamEnergyAndContributionStarEntity) data).getMyTeamEngerInfo().getTeamName();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 小班体验右侧距离，暂时没用
     *
     * @param resultPager
     */
    private void setRight(TeamPkResultPager resultPager) {
//        if (roomInitInfo.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY_CLASS) {
//            final View view = resultPager.getRootView();
//            final PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal = mContentView.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
//            final PrimaryKuangjiaImageView.OnSizeChange onSizeChange = new PrimaryKuangjiaImageView.OnSizeChange() {
//                @Override
//                public void onSizeChange(int width, int height) {
//                    float scale = (float) width / 1334f;
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                    int rightMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (216 * scale);
//                    if (rightMargin != params.rightMargin) {
//                        params.rightMargin = rightMargin;
//                        LayoutParamsUtil.setViewLayoutParams(view, params);
//                    }
//                }
//            };
//            ivLivePrimaryClassKuangjiaImgNormal.addSizeChange(onSizeChange);
//            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//                @Override
//                public void onViewAttachedToWindow(View view) {
//
//                }
//
//                @Override
//                public void onViewDetachedFromWindow(View view) {
//                    ivLivePrimaryClassKuangjiaImgNormal.removeSizeChange(onSizeChange);
//                }
//            });
//        }
    }

    /**
     * 关闭当前页面
     */
    public void closeCurrentPager() {
        if (mFocusPager != null) {
            mFocusPager.onDestroy();
            post(new Runnable() {
                @Override
                public void run() {
                    if (mFocusPager != null) {
                        rlTeamPkContent.removeView(mFocusPager.getRootView());
                        mFocusPager = null;
                    }
                }
            });
        }
    }

    /**
     * 展示聊天 区域上方 战队pk 状态UI
     */
    private void showPkStateLayout() {
        // step 1
        ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
        pkStateRootView = viewGroup.findViewById(R.id.tpkL_teampk_pkstate_root);
        if (pkStateRootView != null) {
            pkStateRootView.setVisibility(View.VISIBLE);
            // pkStateRootView.setTeamPkBll(this);
            // 设置当前pk 状态,兼容 半身直播 主辅导态来回切换
            if (mCurrentPkState != null) {
                pkStateRootView.bindData(mCurrentPkState, false);
            }

        }
        // step 2  初始化 又测 pk 状态栏
        updatePkStateLayout(false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRedPackageEvent(RedPackageEvent event) {
        if (event.getStateCode() == RedPackageEvent.STATE_CODE_SUCCESS) {
            if (mLiveId.equals(event.getLiveId())) {
                updatePkStateLayout(false);
            }
        }
    }

    ArrayList<Runnable> runnables = new ArrayList<>();

    /**
     * 刷新pk状态栏
     *
     * @param showPopWindow 是否展示顶部  进度描述:领先，打平 .....
     */
    public void updatePkStateLayout(final boolean showPopWindow) {
        if (primaryClass) {
            if (teamInfoEntity != null) {
                getPkState(showPopWindow);
            } else {
                runnables.add(new Runnable() {
                    @Override
                    public void run() {
                        getPkState(showPopWindow);
                    }
                });
            }
        } else {
            getPkState(showPopWindow);
        }
    }

    private void getPkState(final boolean showPopWindow) {
        //logger.e("=====> getPkState:" + roomInitInfo.getStuId() + ":" + mHttpManager);
        getTeamPkHttp().liveStuGoldAndTotalEnergy(mLiveId,
                getNewTeamId("getPkState"),
                roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStuId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        mCurrentPkState = mHttpResponseParser.parseStuCoinAndTotalEnergy(responseEntity);
                        if (pkStateRootView != null && mCurrentPkState != null) {
                            pkStateRootView.bindData(mCurrentPkState, showPopWindow);
                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                    }
                });
    }

    /**
     * 显示实时答题 奖励
     */
    public void showAnswerQuestionAward(int goldNum, int energyNum, String id) {
        TeamPkAqResultPager aqAwardPager = new TeamPkAqResultPager(mActivity,
                TeamPkAqResultPager.AWARD_TYPE_QUESTION, this);
        // addPager(aqAwardPager);
        addTopLayerPager(aqAwardPager);
        aqAwardPager.setData(goldNum, energyNum);
        TeamPkLog.showAddPower(contextLiveAndBackDebug, id, energyNum + "");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoteResultUIColse(NativeVoteRusltulCloseEvent event) {
        int addEnergy = event.isStuVoted() ? VOTE_ADD_ENERGY : 0;
        showVoteEnergyAnim(addEnergy, event.getVoteId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTeachPraiseUIColse(TeachPraiseRusltulCloseEvent event) {
        int addEnergy = VOTE_TEACHER_PRAISE_ENERGY;
        logger.d("onTeachPraiseUIColse:addEnergy=" + addEnergy);
        showSpeechAnimSuc(addEnergy, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePkState(UpdatePkState event) {
        logger.d("onUpdatePkState:event=" + event.getWhere());
        updatePkStateLayout(true);
    }

    /**
     * 展示 投票加能量 动画
     */
    private void showVoteEnergyAnim(int addEnergy, String voteId) {
        logger.e("========> showVoteEnergyAnim:" + voteId + ":" + addEnergy);
        //上报服务器 增加加能量
        getTeamPkHttp().addPersonAndTeamEnergy(mLiveId, addEnergy,
                getNewTeamId("showVoteEnergyAnim"),
                roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), voteId, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    }
                });
        TeamPkAqResultPager aqAwardPager = new TeamPkAqResultPager(mActivity, TeamPkAqResultPager.AWARD_TYPE_VOTE,
                this);
        //addPager(aqAwardPager);
        addTopLayerPager(aqAwardPager);
        aqAwardPager.setData(0, addEnergy);
        TeamPkLog.showAddPower(contextLiveAndBackDebug, voteId, addEnergy + "");
    }

    private void showSpeechAnimSuc(final int addEnergy, final TeachPraiseRusltulCloseEvent event) {
        final String voteId = event.getVoiceId();
        logger.e("========> showSpeechAnimSuc:" + voteId + ":" + addEnergy);
        //上报服务器 增加加能量
        mHttpManager.addPersonAndTeamEnergy(mLiveId, addEnergy,
                roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), voteId, new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        boolean addBack = event.isAddBack();
                        if (addBack) {
                            TeamPkAqResultPager aqAwardPager = new TeamPkAqResultPager(mActivity, TeamPkAqResultPager.AWARD_TYPE_SPEECH,
                                    TeamPkBll.this);
                            //addPager(aqAwardPager);
                            addTopLayerPager(aqAwardPager);
                            aqAwardPager.setData(0, addEnergy);
                        } else {
                            int[] startPosition = event.getStartPosition();
                            TeamPkAqResultFlayPager aqAwardPager = new TeamPkAqResultFlayPager(mActivity, TeamPkAqResultPager.AWARD_TYPE_SPEECH,
                                    TeamPkBll.this, startPosition);
                            //addPager(aqAwardPager);
                            addTopLayerFullPager(aqAwardPager);
                            aqAwardPager.setData(0, addEnergy);
                        }
                        TeamPkLog.showAddPower(contextLiveAndBackDebug, voteId, addEnergy + "");
                    }
                });
    }

    /**
     * @return
     */
    private int getRightMargin() {
        int returnValue = 0;
        if (!isFullScreenMode()) {
            returnValue = LiveVideoPoint.getInstance().getRightMargin();
        }
        return returnValue;
    }

    /**
     * 当时是否是全屏模式
     *
     * @return
     */
    private boolean isFullScreenMode() {
        boolean result = isHalfBodyLiveRoom() && mTeacherMode != null && mTeacherMode.equals(LiveTopic.MODE_CLASS);
        return result;
    }


    /**
     * 添加顶层 UI
     */
    private void addTopLayerPager(TeamPkBasePager pager) {
        if (mTopLayerPager != null) {
            mTopLayerPager.onDestroy();
        }
        rlTopLayerContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int rightMargin = getRightMargin();
        params.rightMargin = rightMargin;
        rlTopLayerContent.addView(pager.getRootView(), params);
        mTopLayerPager = pager;
    }

    /**
     * 添加顶层 UI 全屏
     */
    private void addTopLayerFullPager(TeamPkBasePager pager) {
        if (mTopLayerPager != null) {
            mTopLayerPager.onDestroy();
        }
        rlTopLayerContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlTopLayerContent.addView(pager.getRootView(), params);
        mTopLayerPager = pager;
    }

    private void addPager(TeamPkBasePager aqAwardPager) {
        if (mFocusPager != null) {
            mFocusPager.onDestroy();
        }
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int rightMargin = getRightMargin();
        params.rightMargin = rightMargin;
        rlTeamPkContent.addView(aqAwardPager.getRootView(), params);
        mFocusPager = aqAwardPager;
    }

    /**
     * 添加全屏模式 页面
     *
     * @param pager
     */
    private void addFullScreenPager(TeamPkBasePager pager) {
        if (mFocusPager != null) {
            mFocusPager.onDestroy();
        }
        rlTeamPkContent.removeAllViews();
        pager.setFullScreenMode(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlTeamPkContent.addView(pager.getRootView(), params);
        mFocusPager = pager;
    }

    private void registLayoutListener() {
        registContentLayerLayoutListener();
        registTopLayerLayoutListener();
    }

    /**
     * 注册普通内容 展示容器  layout 监听
     */
    private void registContentLayerLayoutListener() {
        rlTeamPkContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isFullScreenMode()) {
                    if (mFocusPager != null && !mFocusPager.isFullScreenMode()) {
                        int rightMargin = LiveVideoPoint.getInstance().getRightMargin();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFocusPager.getRootView()
                                .getLayoutParams();
                        if (rightMargin != params.rightMargin) {
                            params.rightMargin = rightMargin;
                            LayoutParamsUtil.setViewLayoutParams(mFocusPager.getRootView(), params);
                        }
                    }
                }
            }
        });
    }

    /**
     * 注册 TopLayer layout 监听
     */
    private void registTopLayerLayoutListener() {
        rlTopLayerContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener
                () {
            @Override
            public void onGlobalLayout() {
                if (!isFullScreenMode()) {
                    if (mTopLayerPager != null && !mTopLayerPager.isFullScreenMode()) {
                        int rightMargin = LiveVideoPoint.getInstance().getRightMargin();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTopLayerPager.getRootView()
                                .getLayoutParams();
                        if (rightMargin != params.rightMargin) {
                            params.rightMargin = rightMargin;
                            LayoutParamsUtil.setViewLayoutParams(mTopLayerPager.getRootView(), params);
                        }
                    }
                }
            }
        });
    }


    /**
     * activity stop
     */
    @Override
    public void onStop() {
        if (mFocusPager != null) {
            mFocusPager.onStop();
        }

        if (mTopLayerPager != null) {
            mTopLayerPager.onStop();
        }

        logger.e("======>onStop");
    }


    @Override
    public void onPause() {
        super.onPause();
        logger.e("======>onStop");
    }

    @Override
    public void onResume() {
        if (mFocusPager != null) {
            mFocusPager.onResume();
        }
        if (mTopLayerPager != null) {
            mTopLayerPager.onResume();
        }
        logger.e("======>onResume");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.e("======>onDestroy");
        if (mFocusPager != null) {
            mFocusPager.onDestroy();
            mFocusPager = null;
        }
        if (mTopLayerPager != null) {
            mTopLayerPager.onDestroy();
            mTopLayerPager = null;
        }
        isTopicHandled = false;
        if (mPraiseBll != null) {
            mPraiseBll.releas();
        }
        EventBus.getDefault().unregister(this);
    }


    private boolean isAvailable;

    @Override
    public void onLiveInited(LiveGetInfo data) {
        if (data != null && "1".equals(data.getIsAllowTeamPk())) {
            primaryClass = data.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY_CLASS;
            mHttpManager = getHttpManager();
            setRoomInitInfo(data);
            attachToRootView();
            roomInitInfo = data;
            isAIPartner = roomInitInfo.getIsAIPartner() == 1;
            isAvailable = true;
            this.mTeacherMode = mLiveBll.getMode();
            getTeamMates();
        } else {
            mLiveBll.removeBusinessBll(this);
        }
    }

    /**
     * 获取战队成员信息
     */
    private void getTeamMates() {
        if (primaryClass) {
            //获得旧的数据
            final TeamPkTeamInfoEntity saveTeamInfoEntity = getTeamPkTeamInfo();
            getTeamPkHttp().getMyTeamInfo(roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(), LiveAppUserInfo.getInstance().getPsimId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            TeamPkTeamInfoEntity teamInfoEntityres = parseTeamInfoPrimary(responseEntity);
                            if (teamInfoEntityres == null) {
                                return;
                            }
                            onGetTeam(teamInfoEntityres, responseEntity);
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            //网络失败取旧的
                            if (saveTeamInfoEntity != null) {
                                onGetTeam(saveTeamInfoEntity, null);
                            }
                        }

                        private void onGetTeam(TeamPkTeamInfoEntity teamInfoEntityres, ResponseEntity responseEntity) {
                            teamInfoEntity = teamInfoEntityres;
                            mTeamMates = teamInfoEntity.getTeamInfo().getResult();
                            if (mTeamMates != null) {
                                logger.d("getTeamMates:mTeamMates=" + mTeamMates.size());
                            } else {
                                logger.d("getTeamMates:mTeamMates=null");
                            }
                            LiveEventBus.getDefault(mContext).post(new TeamPkTeamInfoEvent(teamInfoEntity, responseEntity));
                            logger.d("getMyTeamInfo:runnables=" + runnables.size());
                            while (!runnables.isEmpty()) {
                                Runnable runnable = runnables.remove(0);
                                runnable.run();
                            }
                        }
                    });
        } else {
            getTeamPkHttp().getTeamMates(roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStudentLiveInfo()
                    .getTeamId(), new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    mTeamMates = mHttpResponseParser.parseTeamMates(responseEntity);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                }
            });
        }
    }

    /**
     *
     */
    private LiveRoomH5CloseEvent latestAnswerRecord = null;
    //最近一次答题信息
    private LiveRoomH5CloseEvent latesH5CloseEvent;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomH5CloseEvent(final LiveRoomH5CloseEvent event) {
        latesH5CloseEvent = event;
        if (event.getQuestionType() == 1) {
         //是大题互动结果页加金币动效
            showAnswerQuestionAward(event.getmGoldNum(), event.getmEnergyNum(), event.getId());
            return;
        }
        logger.e("=======>:onRoomH5CloseEvent:" + event.getId() + ":"
                + event.getmGoldNum() + ":" + event.getmEnergyNum() + ":" + event.isCloseByTeacher());
        if (h5CloseEvents == null) {
            h5CloseEvents = new ArrayList<LiveRoomH5CloseEvent>();
        }
        // 只有答题结果页面才会初始化 energyNum 和 goldNum
        if (event.getmEnergyNum() != -1 && event.getmGoldNum() != -1) {
            h5CloseEvents.add(event);
            LiveRoomH5CloseEvent cacheEvent = null;
            if (h5CloseEvents.get(0).isCloseByTeacher()) {
                cacheEvent = h5CloseEvents.remove(0);
                //step  1 显示飞星动画
                showAnswerQuestionAward(cacheEvent.getmGoldNum(), cacheEvent.getmEnergyNum(), event.getId());
                //step  2 显示pk 结果
                final LiveRoomH5CloseEvent finalCacheEvent = cacheEvent;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getEnergyNumAndContributionStar(finalCacheEvent);
                    }
                }, 3000);
            } else {
                cacheEvent = h5CloseEvents.get(0);
                showAnswerQuestionAward(cacheEvent.getmGoldNum(), cacheEvent.getmEnergyNum(), event.getId());
            }
        } else {
            //未展示答题结果
            if (event.isCloseByTeacher()) {
                getEnergyNumAndContributionStar(event);
            } else {
                h5CloseEvents.add(event);
            }
        }
    }


    /**
     * 获取最近一次答题结果信息
     *
     * @return
     */
    public LiveRoomH5CloseEvent getLatesH5CloseEvent() {

        return latesH5CloseEvent;
    }

    /**
     * 显示当前的pk 结果
     */
    public void showCurrentPkResult() {
        if (h5CloseEvents == null || h5CloseEvents.size() == 0) {
            return;
        }
        LiveRoomH5CloseEvent cacheEvent = h5CloseEvents.remove(0);
        getEnergyNumAndContributionStar(cacheEvent);
    }


    /**
     * 开始pk对手选择
     */
    public void startSelectAdversary() {
        getPkAdversary();
    }

    /**
     * 获取PK 对手信息
     */
    private void getPkAdversary() {
        String teamId = getNewTeamId("getPkAdversary");
        if (TeamPkConfig.DEAF_TEAM_ID.equals(teamId)) {
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    getPkAdversary();
                }
            });
            startTeamSelect(true, false);
            return;
        }
        getTeamPkHttp().getPkAdversary(roomInitInfo.getStudentLiveInfo().getClassId(),
                teamId, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        TeamPkAdversaryEntity pkAdversaryEntity = mHttpResponseParser.
                                parsePkAdversary(responseEntity);
                        if (pkAdversaryEntity != null && pkAdversaryEntity.getSelf() != null && pkAdversaryEntity.getOpponent() != null) {
                            if (mLiveBll != null) {
                                try {
                                    long teamId = Long.parseLong(pkAdversaryEntity.getOpponent().getTeamId());
                                    long classId = Long.parseLong(pkAdversaryEntity.getOpponent().getClassId());
                                    boolean isComputer = (teamId < 0 && classId < 0);
                                    TeamPkLog.showOpponent(contextLiveAndBackDebug, isComputer, pkAdversaryEntity.getSelf().getTeamName(),
                                            pkAdversaryEntity.getOpponent().getTeamName(), pkAdversaryEntity.getOpponent()
                                                    .getTeamId(), pkAdversaryEntity.getOpponent().getClassId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            showPkResultScene(pkAdversaryEntity, PK_RESULT_TYPE_ADVERSARY);
                        } else {
                            XESToastUtils.showToast(mActivity, "pk对手信息获取失败");
                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }
                }
        );
    }

    /**
     * 结束队伍选择
     */
    public void stopSelectAdversary() {
        if (mFocusPager != null && mFocusPager instanceof TeamPkResultPager) {
            ((TeamPkResultPager) mFocusPager).closePkResultPager();
        }
    }

    /**
     * 关闭 每题的 pk 结果展示
     */
    public void closeCurrentPkResult() {
       /* if (mFocusPager != null && mFocusPager instanceof TeamPkResultPager) {
            ((TeamPkResultPager) mFocusPager).closePkResultPager();
        }*/
        if (mFocusPager != null && mFocusPager instanceof TeamPkContributionPager) {
            ((TeamPkContributionPager) mFocusPager).startAutoClose();
        }
    }


    /**
     * 上传服务端 学生分队准备ok
     */
    public void sendStudentReady() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.TEAM_PK_STUDENT_READY);
            jsonObject.put("stuId", "" + roomInitInfo.getStuId());
            sendNoticeToCoun(jsonObject);
            sendNoticeToMain(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 设置 埋点统计nonce
     */
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return TextUtils.isEmpty(nonce) ? "" : nonce;
    }


    ///////////////////////////消息通讯相关///////////////////////////////////////
    private int[] noticeCodes = {
            XESCODE.STOPQUESTION,
            XESCODE.EXAM_STOP,
            XESCODE.ENGLISH_H5_COURSEWARE,
            XESCODE.TEAM_PK_TEAM_SELECT,
            XESCODE.TEAM_PK_GROUP,
            XESCODE.TEAM_PK_SELECT_PKADVERSARY,
            XESCODE.TEAM_PK_PUBLIC_PK_RESULT,
            XESCODE.TEAM_PK_PUBLIC_CONTRIBUTION_STAR,
            XESCODE.TEAM_PK_EXIT_PK_RESULT,
            XESCODE.MULTIPLE_H5_COURSEWARE,
            XESCODE.TEAM_PK_BLACK_RANK_LIST,
            XESCODE.TEAM_PK_STAR_RANK_LIST,
            XESCODE.TEAM_PK_PK_END,
            XESCODE.TEACHER_PRAISE,
            XESCODE.TEAM_PK_PARISE_ANWSER_RIGHT,
            XESCODE.TEAM_PK_TEACHER_PRAISE,
            XESCODE.SENDQUESTION,
            XESCODE.EXAM_START
    };


    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.e("=======>onNotice :" + type + ":" + data);
        if (isAvailable) {
            String nonce = "";
            String open = "";
            switch (type) {
                case XESCODE.EXAM_START:
                case XESCODE.SENDQUESTION:
                    //收到发题指令 清空上一次 答题结果记录
                    answerResult = null;
                    break;
                case XESCODE.ENGLISH_H5_COURSEWARE: {
                    String status = data.optString("status");
                    if ("on".equals(status)) {
                        //收到发题指令 清空上一次 答题结果记录
                        answerResult = null;
                    } else if ("off".equals(status)) {
                        setNonce(data.optString("nonce", ""));
                        showCurrentPkResult();
                    }
                }
                break;
                case XESCODE.MULTIPLE_H5_COURSEWARE:
                    boolean isOpen = data.optBoolean("open");
                    if (isOpen) {
                        //收到发题指令 清空上一次 答题结果记录
                        answerResult = null;
                    } else {
                        setNonce(data.optString("nonce", ""));
                        showCurrentPkResult();
                    }
                    break;
                case XESCODE.STOPQUESTION:
                case XESCODE.EXAM_STOP:
                    setNonce(data.optString("nonce", ""));
                    showCurrentPkResult();
                    break;
                case XESCODE.TEAM_PK_GROUP: {
                    String status = data.optString("status");
                    nonce = data.optString("nonce", "");
                    teamSelectByNotice = true;
                    if ("on".equals(status)) {
                        startTeamSelect(true, true);
                        TeamPkLog.receiveCreateTeam(contextLiveAndBackDebug, nonce, true);
                    } else if ("off".equals(status)) {
                        //自动结束，不取消分队，但是需要去掉快速入口
                        if (mFocusPager instanceof TeamPkTeamSelectingPager) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    closeCurrentPager();
                                }
                            });
                        }
//                        stopTeamSelect();
//                        TeamPkLog.receiveCreateTeam(mLiveBll, nonce, false);
                    }
                    break;
                }
                case XESCODE.TEAM_PK_TEAM_SELECT:
                    open = data.optString("open");
                    nonce = data.optString("nonce", "");
                    teamSelectByNotice = true;
                    if (OPEN_STATE_OPEN.equals(open)) {
                        startTeamSelect(false, true);
                        TeamPkLog.receiveCreateTeam(contextLiveAndBackDebug, nonce, true);
                    } else if (OPEN_STATE_CLOSE.equals(open)) {
                        stopTeamSelect();
                        TeamPkLog.receiveCreateTeam(contextLiveAndBackDebug, nonce, false);
                    }
                    break;
                case XESCODE.TEAM_PK_SELECT_PKADVERSARY:
                    open = data.optString("open");
                    nonce = data.optString("nonce", "");
                    if (OPEN_STATE_OPEN.equals(open)) {
                        startSelectAdversary();
                        TeamPkLog.receiveMatchOpponent(contextLiveAndBackDebug, nonce, true);
                    }
                    break;

                case XESCODE.TEAM_PK_PUBLIC_PK_RESULT:
                    nonce = data.optString("nonce", "");
                    TeamPkLog.receivePkResult(contextLiveAndBackDebug, nonce, true);
                    showPkResult();

                    break;
                case XESCODE.TEAM_PK_PUBLIC_CONTRIBUTION_STAR:

                    nonce = data.optString("nonce", "");
                    TeamPkLog.receiveClassBoxInfo(contextLiveAndBackDebug, nonce, true);
                    showClassChest();
                    break;

                case XESCODE.TEAM_PK_EXIT_PK_RESULT:
                    //closeCurrentPkResult();
                    break;
                case XESCODE.TEAM_PK_BLACK_RANK_LIST:
                    //closeStarts();
                    nonce = data.optString("nonce");
                    TeamPkLog.receivePkStarList(contextLiveAndBackDebug, nonce, "1");
                    closeCurrentPager();
                    setNonce(nonce);
                    getProgressStudent();
                    break;

                case XESCODE.TEAM_PK_STAR_RANK_LIST:
                    //关闭 幸运星页面
                    //closeClassChest();
                    nonce = data.optString("nonce", "");
                    TeamPkLog.receivePkStarList(contextLiveAndBackDebug, nonce, "0");
                    closeCurrentPager();
                    setNonce(nonce);
                    getStusStars();
                    break;

                case XESCODE.TEAM_PK_PK_END:
                    TeamPkLog.showPkFinished(contextLiveAndBackDebug, data.optString("nonce", ""));
                    showPkEndToast();
                    break;
                case XESCODE.TEACHER_PRAISE:
                    TeamPkLog.receiveVoicePraise(contextLiveAndBackDebug, data.optString("nonce", ""));
                    break;
                case XESCODE.TEAM_PK_PARISE_ANWSER_RIGHT:
                    boolean isDouble = data.optInt("isDouble", 0) == 1;
                    if (mPraiseBll == null) {
                        mPraiseBll = new TeamPkPraiseBll(mActivity, this);
                    }
                    mPraiseBll.onPraise(sourceNick, target, data, type);
                    //表扬全对 刷新右侧状态栏
                    if (isDouble) {
                        updatePkStateLayout(false);
                    }
                    break;
                case XESCODE.TEAM_PK_TEACHER_PRAISE:
                    if (mPraiseBll == null) {
                        mPraiseBll = new TeamPkPraiseBll(mActivity, this);
                    }
                    mPraiseBll.onPraise(sourceNick, target, data, type);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }


    @Override
    public void onTopic(LiveTopic data, JSONObject jsonObject, boolean modeChange) {
        if (isAvailable) {
            // 战队pk  topic 逻辑
            LiveTopic.TeamPkEntity teamPkEntity = data.getTeamPkEntity();
            if (teamPkEntity != null) {
                //恢复战队pk 相关状态
                int openBoxStateCode = 0;
                int alloteamStateCode = 0;
                int allotpkmanStateCode = 0;
                int pkStepCode = 0;

                if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) && teamPkEntity.getRoomInfo1() != null) {
                    openBoxStateCode = teamPkEntity.getRoomInfo1().getOpenbox();
                    alloteamStateCode = teamPkEntity.getRoomInfo1().getAlloteam();
                    allotpkmanStateCode = teamPkEntity.getRoomInfo1().getAllotpkman();
                    pkStepCode = teamPkEntity.getRoomInfo1().getPKStep();
                    logger.e("====>onTopic teampk main_teacher_info:" + openBoxStateCode + ":" +
                            alloteamStateCode + ":" + allotpkmanStateCode);
                } else {
                    if (teamPkEntity.getRoomInfo2() != null) {
                        openBoxStateCode = teamPkEntity.getRoomInfo2().getOpenbox();
                        alloteamStateCode = teamPkEntity.getRoomInfo2().getAlloteam();
                        allotpkmanStateCode = teamPkEntity.getRoomInfo2().getAllotpkman();
                        pkStepCode = teamPkEntity.getRoomInfo2().getPKStep();
                        logger.e("====>onTopic teampk assist_teacher_info:" + openBoxStateCode + ":" +
                                alloteamStateCode + ":" + allotpkmanStateCode + ":" + pkStepCode);
                    }
                }
                String status = "off";
                if (primaryClass) {
                    try {
                        JSONObject room_2 = jsonObject.getJSONObject("room_2");
                        status = room_2.getString("split_team_status");
                    } catch (JSONException e) {
                        logger.e("onTopic:split_team_status", e);
                    }
                }
                if (!isTopicHandled() && alloteamStateCode == 1 || "on".equals(status)) {
                    setTopicHandled(true);
                    if (!teamSelectByNotice) {
                        showTeamSelecting();
                    }
                    logger.e("====>onTopic showTeamSelecting:");
                    return;
                }
                if (allotpkmanStateCode == 1 && !isTopicHandled()) {
                    setTopicHandled(true);
                    startSelectAdversary();
                    logger.e("====>onTopic startSelectAdversary:");
                    return;
                }

                if ((pkStepCode == 1 || openBoxStateCode == 1) && !isTopicHandled()) {
                    setTopicHandled(true);
                    showPkResult();
                    logger.e("====>onTopic showPkResult:");
                    return;
                }

                if (TeamPkConfig.TOPIC_PKSTEP_BLACK_RANK_LIST == pkStepCode && !isTopicHandled()) {
                    setTopicHandled(true);
                    getProgressStudent();
                } else if (TeamPkConfig.TOPIC_PKSTEP_STAR_RANK_LIST == pkStepCode && !isTopicHandled()) {
                    setTopicHandled(true);
                    getStusStars();
                }
                setTopicHandled(true);
            }
        }

    }

    /**
     * 是否是AlPartner 直播间
     *
     * @return
     */
    public boolean isAIPartner() {
        return isAIPartner;
    }


    /**
     * 展示明星榜
     */
    private void getStusStars() {
        getTeamPkHttp().getTeamPkStarStudents(mLiveId,
                roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStudentLiveInfo().getCourseId(),
                getNewTeamId("getStusStars"), new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        List<TeamPkStar> data = mHttpResponseParser.parseTeamPkStar(responseEntity);
                        if (data != null && data.size() > 0) {
                            showStars(data);
                            TeamPkLog.showPkStarList(contextLiveAndBackDebug, getNonce(), "0");
                            if (TextUtils.isEmpty(mTeamName)) {
                                mTeamName = data.get(0).getTeamName();
                            }
                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        String errorMsg = responseEntity.getErrorMsg();
                        XESToastUtils.showToast(mActivity, TextUtils.isEmpty(errorMsg) ? "明星榜数据获取失败" : errorMsg);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        XESToastUtils.showToast(mActivity, "明星榜数据获取失败");
                    }
                });

    }

    /**
     * 展示明星榜UI
     *
     * @param data
     */
    private void showStars(List<TeamPkStar> data) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkStarsPager)) {
            TeamPkStarsPager startsPager = new TeamPkStarsPager(mActivity, data, TeamPkBll.this);
            addPager(startsPager);
        }
    }


    /**
     * 获取进步榜
     */
    private void getProgressStudent() {
        getTeamPkHttp().getTeamPkProgressStudent(mLiveId,
                roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStudentLiveInfo().getCourseId(),
                new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        List<TeamPkStuProgress> data = mHttpResponseParser.parseTeamPkProgressStu(responseEntity);
                        if (data != null && data.size() > 0) {
                            showStuProgressList(data);
                            TeamPkLog.showPkStarList(contextLiveAndBackDebug, getNonce(), "1");
                            if (TextUtils.isEmpty(mTeamName)) {
                                mTeamName = data.get(0).getTeamName();
                            }
                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        String errorMsg = responseEntity.getErrorMsg();
                        XESToastUtils.showToast(mActivity, TextUtils.isEmpty(errorMsg) ? "黑马榜数据获取失败" : errorMsg);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        XESToastUtils.showToast(mActivity, TextUtils.isEmpty(msg) ? "黑马榜数据获取失败" : msg);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        XESToastUtils.showToast(mActivity, "进步榜数据获取失败");
                    }
                });
    }

    /**
     * 显示进步榜
     *
     * @param data
     */
    private void showStuProgressList(List<TeamPkStuProgress> data) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkImprovePager)) {
            TeamPkImprovePager startsPager = new TeamPkImprovePager(mActivity, data, TeamPkBll.this);
            addPager(startsPager);
        }
    }

    /**
     * 显示 pk 总结 结束弹框
     */
    private void showPkEndToast() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mFocusPager == null || !(mFocusPager instanceof TeamPkEndPager)) {
                    TeamPkEndPager startsPager = new TeamPkEndPager(mActivity, TeamPkBll.this);
                    addFullScreenPager(startsPager);
                }
            }
        });
    }


    /**
     * 在线用户聊天id列表
     **/
    private List<String> onLineChatIds = new ArrayList<>();


    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect(IRCConnection connection) {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String
            message) {

    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {

    }

    @Override
    public void onUserList(String channel, User[] users) {
        for (User user : users) {
            if (!onLineChatIds.contains(user.getNick())) {
                onLineChatIds.add(user.getNick());
            }
        }
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        if (!onLineChatIds.contains(sender)) {
            onLineChatIds.add(sender);
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        if (onLineChatIds.contains(sourceNick)) {
            onLineChatIds.remove(sourceNick);
        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }


    /**
     * 获取本队在线队员列表
     *
     * @return
     */
    public List<TeamMate> getOnlineTeamMates() {
        List<TeamMate> resultList = null;
        // 除去自己
        if (mTeamMates != null && mTeamMates.size() > 1) {
            resultList = new ArrayList<TeamMate>();
            String stuId = null;
            TeamMate onLineTeamMate = null;
            for (int i = 0; i < mTeamMates.size(); i++) {
                //除去自己
                stuId = mTeamMates.get(i).getId();
                if (stuId != null && !stuId.equals(LiveAppUserInfo.getInstance().getStuId())) {
                    for (int j = 0; j < onLineChatIds.size(); j++) {
                        if (onLineChatIds.get(j).contains(stuId)) {
                            onLineTeamMate = new TeamMate();
                            onLineTeamMate.setName(mTeamMates.get(i).getName());
                            onLineTeamMate.setId(stuId);
                            resultList.add(onLineTeamMate);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    private List<String> prasieTextList;

    /**
     * 获取点赞文案
     *
     * @return
     */
    public List<String> getPraiseText() {
        if (prasieTextList == null || prasieTextList.size() == 0) {
            prasieTextList = new ArrayList<String>();
            //大拇指1
            prasieTextList.add("\uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC4D");
            //大拇指2
            prasieTextList.add("\uD83D\uDC4D\uD83D\uDC4D");
            //爱心
            prasieTextList.add("\u2764\u2764\u2764");
            //大笑
            prasieTextList.add("\uD83D\uDE04");
            prasieTextList.add("我们的贡献之星超棒~！");
            prasieTextList.add("一起加油！");
            prasieTextList.add("神一样的队友！");
            prasieTextList.add("一个大大的赞~");
            prasieTextList.add("你们是最棒的！");
            prasieTextList.add("祝贺你！");
            prasieTextList.add("加油加小心," + mTeamName + "稳赢！");
            prasieTextList.add("超爱" + mTeamName + "的大家~");
        }
        return prasieTextList;
    }

    /**
     * 用户最近一次答题 答题结果
     **/
    ScienceAnswerResult answerResult;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScineceAnswerResutlEvent(AnswerResultEvent event) {
        logger.e("========>onAnswerResult_LiveVideo:" + event.toString());
        try {
            JSONObject jsonObject = new JSONObject(event.getData());
            String id = jsonObject.optString("id");
            int isRight = jsonObject.optInt("isRight", -1);
            if (!TextUtils.isEmpty(id)) {
                answerResult = new ScienceAnswerResult();
                answerResult.setId(id);
                answerResult.setIsRight(isRight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最近一次 答题结果
     *
     * @return
     */
    public ScienceAnswerResult getCurrentAnswerResult() {
        return answerResult;
    }


    /**
     * 获取最近一次 pk 胜负关系
     *
     * @return
     */
    public int getLatesPkState() {
        int result = TeamPkConfig.PK_STATE_DRAW;
        if (pkStateRootView != null) {
            result = pkStateRootView.getLatesPkState();
        }
        return result;
    }


    public String getNewTeamId(String method) {
        String teamId;
        if (primaryClass) {
            if (teamInfoEntity == null) {
                return TeamPkConfig.DEAF_TEAM_ID;
            }
            try {
                teamId = teamInfoEntity.getTeamInfo().getTeamId();
            } catch (Exception e) {
                teamId = TeamPkConfig.DEAF_TEAM_ID;
                LiveCrashReport.postCatchedException(new LiveException(TAG + ":" + method, e));
            }
        } else {
            teamId = roomInitInfo.getStudentLiveInfo().getTeamId();
        }
        mLogtf.d("getNewTeamId:primaryClass=" + primaryClass + ",method=" + method + ",teamId=" + teamId);
        return teamId;
    }

    private TeamPkTeamInfoEntity getTeamPkTeamInfo() {
        ResponseEntity responseEntity = LocalTeamPkTeamInfo.getTeamPkTeamInfo(mShareDataManager, mLiveId);
        if (responseEntity != null) {
            TeamPkTeamInfoEntity teamInfoEntity = getTeamPkHttp().setOldTeamPkTeamInfo(responseEntity);
            return teamInfoEntity;
        }
        return null;
    }

    private TeamPkTeamInfoEntity parseTeamInfoPrimary(ResponseEntity responseEntity) {
        TeamPkTeamInfoEntity teamPkTeamInfoEntity = getTeamPkHttp().getTeamPKHttpResponseParser().parseTeamInfoPrimary(responseEntity);
        if (teamPkTeamInfoEntity != null) {
            LocalTeamPkTeamInfo.saveTeamPkTeamInfo(mShareDataManager, responseEntity, mLiveId);
        }
        return teamPkTeamInfoEntity;
    }
}

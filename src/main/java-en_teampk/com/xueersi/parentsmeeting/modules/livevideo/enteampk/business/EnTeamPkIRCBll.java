package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewTreeObserver;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeTeamPKContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.EnglishPk;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.event.ClassEndEvent;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.http.EnTeamPkHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkLeadPager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpDispatch;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpRunnable;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linyuqiang
 * created  at 2018/11/6
 * 英语战队PK 相关业务处理
 */
public class EnTeamPkIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, MessageAction,
        BetterMeTeamPKContract {
    private EnTeamPkAction enTeamPkAction;
    private String unique_id;
    private boolean psOpen = false;
    private PkTeamEntity pkTeamEntity;
    private VideoQuestionLiveEntity videoQuestionLiveEntity;
    private boolean mIsShow = false;
    private Runnable stopRunnable = null;
    /** 最大延迟时间 */
    private static long maxdelayMillis = 3000;
    private long stopQuestTime;
    private AtomicBoolean firstConnect = new AtomicBoolean(false);
    private Runnable reportStuInfoRun;
    /** 通知了eventBus */
    private boolean haveTeamRun = false;
    private ClassEndRec classEndRec;
    private ClassEndReg classEndReg;
    private boolean isEnglishPkTotalRank = false;
    private int classInt = 0;
    private EnTeamPkHttpManager enTeamPkHttpManager;
    private TcpDispatch tcpDispatch;
    private boolean destory = false;
    private InteractiveTeam mInteractiveTeam;
    private ArrayList<TeamMemberEntity> entities = new ArrayList<>();
    private TeamMessageAction teamMessageAction;

    public EnTeamPkIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        LiveGetInfo.EnglishPk englishPk = getInfo.getEnglishPk();
        logger.d("onLiveInited:use=" + englishPk.canUsePK + ",has=" + englishPk.hasGroup);
//        if (com.xueersi.common.config.AppConfig.DEBUG) {
//            englishPk.canUsePK = 1;
//            englishPk.isTwoLose = 1;
//            englishPk.hasGroup = 0;
//        }
        if (englishPk.canUsePK == 0) {
            mLiveBll.removeBusinessBll(this);
            return;
        }
        ProxUtil.getProxUtil().put(mContext, BetterMeTeamPKContract.class, this);
        unique_id = mGetInfo.getId() + "_" + mGetInfo.getStudentLiveInfo().getClassId();
        logger.d("onLiveInited:unique_id=" + unique_id);
        EnTeamPkBll teamPkBll = new EnTeamPkBll(activity, mGetInfo.getId());
        teamPkBll.setRootView(getLiveViewAction());
        teamPkBll.setEnTeamPkHttp(new EnTeamPkHttpImp());
        enTeamPkAction = teamPkBll;
        teamPkBll.onLiveInited(getInfo);
        EnTeamPkQuestionShowAction enTeamPkQuestionShowAction = new EnTeamPkQuestionShowAction();
        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(enTeamPkQuestionShowAction);
        }
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(enTeamPkQuestionShowAction);
        }
        parseTeamInter();
        if (englishPk.hasGroup != EnglishPk.HAS_GROUP_NO) {
            try {
                String string = mShareDataManager.getString(ShareDataConfig.LIVE_ENPK_MY_TEAM, "{}", ShareDataManager.SHAREDATA_USER);
                JSONObject jsonObject = new JSONObject(string);
                if (jsonObject.has(getInfo.getId())) {
                    ResponseEntity responseEntity = new ResponseEntity();
                    responseEntity.setJsonObject(jsonObject.getJSONObject(getInfo.getId()));
                    pkTeamEntity = parsegetSelfTeamInfo(responseEntity);
                    logger.d("onLiveInited:pkTeamEntity=null?" + (pkTeamEntity == null));
                    if (pkTeamEntity != null) {
                        pkTeamEntity.setCreateWhere(PkTeamEntity.CREATE_TYPE_LOCAL);
                    }
                    teamPkBll.setPkTeamEntity(pkTeamEntity);
                }
            } catch (Exception e) {
                pkTeamEntity = null;
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
//        if (com.xueersi.common.config.AppConfig.DEBUG) {
//            java.util.Random random = new java.util.Random();
//            EnTeamPkRankEntity enTeamPkRankEntity = new EnTeamPkRankEntity();
//            enTeamPkRankEntity.setApkTeamId(2);
//            ArrayList<TeamMemberEntity> memberEntities = enTeamPkRankEntity.getMemberEntities();
//            for (int i = 0; i < 7; i++) {
//                TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
//                teamMemberEntity.id = 100 + i;
//                if (i == 2) {
//                    teamMemberEntity.isMy = true;
//                }
//                teamMemberEntity.headurl = "https://xesfile.xesimg.com/user/h/57375.jpg";
//                teamMemberEntity.name = "测试测试测试测试测试" + i;
//                teamMemberEntity.energy = 110 + i;
//                memberEntities.add(teamMemberEntity);
//            }
//            enTeamPkRankEntity.setBpkTeamId(3);
//            enTeamPkRankEntity.setMyTeamCurrent(3);
//            enTeamPkRankEntity.setMyTeamTotal(154);
//            enTeamPkRankEntity.setOpTeamCurrent(3);
//            enTeamPkRankEntity.setOpTeamTotal(154);
//            enTeamPkAction.onRankLead(enTeamPkRankEntity, "1", TeamPkLeadPager.TEAM_TYPE_1);
//        }
        if (getInfo.getStudentLiveInfo() != null) {
            String classId = getInfo.getStudentLiveInfo().getClassId();
            try {
                mLogtf.d("onLiveInited:classInt=" + classId);
                classInt = Integer.parseInt(classId);
                if (classInt < 0) {
                    classEndReg = new ClassEndReg(mContext, getInfo);
                    classEndRec = new ClassEndRec();
                    LiveEventBus.getDefault(mContext).register(classEndRec);
                }
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
        boolean hasAddTop = teamPkBll.isHasAddTop();
        if (hasAddTop && englishPk.hasGroup == EnglishPk.HAS_GROUP_TRAN) {
            poseEvent();
        }
    }

    private void registTcp() {
        TcpMessageReg tcpMessageReg = ProxUtil.getProxUtil().get(mContext, TcpMessageReg.class);
        if (tcpMessageReg != null) {
            if (teamMessageAction == null) {
                teamMessageAction = new TeamMessageAction();
                tcpMessageReg.registTcpMessageAction(teamMessageAction);
            }
        }
    }

    @Override
    public void onArtsExtLiveInited(LiveGetInfo getInfo) {
        ArtsExtLiveInfo artsExtLiveInfo = getInfo.getArtsExtLiveInfo();
        int isGroupGmaeCourseWare = artsExtLiveInfo.getIsGroupGameCourseWare();
        logger.d("onArtsExtLiveInited:isGroupGmaeCourseWare=" + isGroupGmaeCourseWare);
        if (isGroupGmaeCourseWare == 1) {
            registTcp();
            if (pkTeamEntity != null) {
                startTeam("onArtsExtLiveInited");
            }
        }
    }

    @Override
    public void onPKStart(boolean showPk) {
        if (!psOpen) {
            psOpen = true;
            if (enTeamPkAction != null) {
                enTeamPkAction.onRankStart(showPk);
            }
        }
    }

    @Override
    public void onPKEnd() {
        getEnglishPkTotalRank();
    }

    private class ClassEndRec {
        @Subscribe(threadMode = ThreadMode.POSTING)
        public void onClassEndEvent(ClassEndEvent endEvent) {
            mLogtf.d("onClassEndEvent:isEnglishPkTotalRank=" + isEnglishPkTotalRank);
            getEnglishPkTotalRank();
        }
    }

    class EnTeamPkQuestionShowAction implements QuestionShowAction {

        @Override
        public void onQuestionShow(VideoQuestionLiveEntity questionLiveEntity, boolean isShow) {
            mIsShow = isShow;
            if (isShow) {
                logger.d("onQuestionShow:isShow");
                videoQuestionLiveEntity = questionLiveEntity;
            } else {
                if (stopRunnable != null) {
                    Runnable runnable = stopRunnable;
                    stopRunnable = null;
                    long delayMillis = System.currentTimeMillis() - stopQuestTime;
                    if (delayMillis > maxdelayMillis) {
                        runnable.run();
                    } else {
                        delayMillis = maxdelayMillis - delayMillis;
                        if (delayMillis < 0) {
                            runnable.run();
                        } else {
                            postDelayed(runnable, delayMillis);
                        }
                    }
                    logger.d("onQuestionShow:delayMillis=" + delayMillis);
                } else {
                    logger.d("onQuestionShow:notShow");
                }
            }
            if (enTeamPkAction != null) {
                enTeamPkAction.hideTeam();
            }
        }
    }

    private void saveTeam(ResponseEntity responseEntity) {
        String string = mShareDataManager.getString(ShareDataConfig.LIVE_ENPK_MY_TEAM, "{}", ShareDataManager.SHAREDATA_USER);
        try {
            JSONObject jsonObject = new JSONObject(string);
            jsonObject.put(mGetInfo.getId(), responseEntity.getJsonObject());
            mShareDataManager.put(ShareDataConfig.LIVE_ENPK_MY_TEAM, jsonObject.toString(), ShareDataManager.SHAREDATA_USER);
        } catch (JSONException e) {
            mShareDataManager.put(ShareDataConfig.LIVE_ENPK_MY_TEAM, "{}", ShareDataManager.SHAREDATA_USER);
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

    class EnTeamPkHttpImp implements EnTeamPkHttp {
        int getSelfTeamInfoTimes = 1;
        int getEnglishPkGroupTimes = 1;
        int reportStuLike = 1;

        @Override
        public void getSelfTeamInfo(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            getHttpManager().getSelfTeamInfo(mGetInfo.getStuId(), unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getSelfTeamInfo:onPmSuccess" + responseEntity.getJsonObject());
                    pkTeamEntity = parsegetSelfTeamInfo(responseEntity);
                    abstractBusinessDataCallBack.onDataSucess(pkTeamEntity);
                    if (pkTeamEntity != null) {
                        saveTeam(responseEntity);
                    }
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("getSelfTeamInfo:onPmError=" + responseEntity.getErrorMsg());
                    abstractBusinessDataCallBack.onDataFail(1, responseEntity.getErrorMsg());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    if (error instanceof SocketTimeoutException) {
                        logger.e("getSelfTeamInfo:onPmFailure(Timeout)msg=" + msg);
                    } else {
                        logger.e("getSelfTeamInfo:onPmFailure" + msg, error);
                    }
                    abstractBusinessDataCallBack.onDataFail(0, msg);
                }
            });
        }

        @Override
        public void reportStuInfo(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            final String mode = LiveTopic.MODE_CLASS.equals(mGetInfo.getMode()) ? "1" : "0";
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //s_lliveID_liveType_stuID_sex
                    String connectNickname = mLiveBll.getConnectNickname();
                    String nick_name;
                    if (!StringUtils.isEmpty(connectNickname)) {
                        nick_name = connectNickname;
                    } else {
                        nick_name = "s_3_" + mGetInfo.getId() + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
                    }
                    mLogtf.d("reportStuInfo:nick_name=" + nick_name + ",mode=" + mGetInfo.getMode());
                    LiveGetInfo.EnglishPk englishPk = mGetInfo.getEnglishPk();
                    StuSegmentEntity stuSegmentEntity = mGetInfo.getBetterMe().getStuSegment();
                    String segmentType = "";
                    String segment = "";
                    String star = "";
                    String sumCount = "";
                    if (stuSegmentEntity != null) {
                        segmentType = "" + stuSegmentEntity.getSegmentType();
                        segment = stuSegmentEntity.getSegment();
                        star = "" + stuSegmentEntity.getStar();
                        sumCount = "" + stuSegmentEntity.getSumCount();
                    }
                    getHttpManager().reportStuInfo(mode, mGetInfo.getStuId(), mGetInfo.getStandLiveName(), mGetInfo.getStuImg(), "" + englishPk.historyScore, "" + englishPk.isTwoLose, nick_name, unique_id, segmentType, segment, star, sumCount, new HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) {
                            logger.d("reportStuInfo:onPmSuccess" + responseEntity.getJsonObject());
                            if (abstractBusinessDataCallBack != null) {
                                abstractBusinessDataCallBack.onDataSucess(responseEntity);
                            }
//                    if (AppConfig.DEBUG) {
//                        if (enTeamPkAction != null) {
//                            enTeamPkAction.onRankStart();
//                        }
//                    }
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            logger.e("reportStuInfo:onPmError" + responseEntity.getErrorMsg());
//                    if (getSelfTeamInfoTimes > 10) {
//                        return;
//                    }
//                    postDelayedIfNotFinish(new Runnable() {
//                        @Override
//                        public void run() {
//                            reportStuInfo(abstractBusinessDataCallBack);
//                        }
//                    }, (getSelfTeamInfoTimes++) * 1000);
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            if (error instanceof SocketTimeoutException) {
                                logger.e("reportStuInfo:onPmFailure(Timeout)msg=" + msg + ",times=" + getSelfTeamInfoTimes);
                            } else {
                                logger.e("reportStuInfo:onPmFailure:msg=" + msg + ",times=" + getSelfTeamInfoTimes, error);
                            }
                            if (getSelfTeamInfoTimes > 10) {
                                return;
                            }
                            postDelayedIfNotFinish(new Runnable() {
                                @Override
                                public void run() {
                                    reportStuInfo(abstractBusinessDataCallBack);
                                }
                            }, (getSelfTeamInfoTimes++) * 1000);
                        }
                    });
                }
            };
            mLogtf.d("reportStuInfo:mode=" + mode);
            if (mode.equals("1")) {//主讲模式。等irc连接2秒
                reportStuInfoRun = runnable;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Runnable freportStuInfoRun = reportStuInfoRun;
                        mLogtf.d("reportStuInfo:reportStuInfoRun=null?" + (reportStuInfoRun == null));
                        reportStuInfoRun = null;
                        if (freportStuInfoRun != null) {
                            freportStuInfoRun.run();
                        }
                    }
                }, 2000);
            } else {//辅导模式。直接上传
                runnable.run();
            }
        }

        @Override
        public void updataEnglishPkGroup(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            getHttpManager().updataEnglishPkGroup("", "", "", "", "", new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("updataEnglishPkGroup:onPmSuccess=" + responseEntity.getJsonObject());
                    abstractBusinessDataCallBack.onDataSucess();
                }
            });
        }

        @Override
        public void getEnglishPkGroup(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            mLogtf.d("getEnglishPkGroup:haveTeamRun=" + haveTeamRun);
            if (haveTeamRun) {
                haveTeamRun = false;
                poseEvent();
            }
            if (pkTeamEntity != null) {
                abstractBusinessDataCallBack.onDataSucess(pkTeamEntity);
                return;
            }
            getHttpManager().getEnglishPkGroup(new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getEnglishPkGroup:onPmSuccess" + responseEntity.getJsonObject());
                    pkTeamEntity = parsegetSelfTeamInfo(responseEntity);
                    abstractBusinessDataCallBack.onDataSucess(pkTeamEntity);
                    if (pkTeamEntity != null) {
                        saveTeam(responseEntity);
                    }
//                    if (AppConfig.DEBUG) {
//                        abstractBusinessDataCallBack.onDataFail(1, responseEntity.getErrorMsg());
//                    }
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("getEnglishPkGroup:onPmError=" + responseEntity.getErrorMsg());
                    abstractBusinessDataCallBack.onDataFail(1, responseEntity.getErrorMsg());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    if (error instanceof SocketTimeoutException) {
                        logger.e("getEnglishPkGroup:onPmFailure(Timeout)msg=" + msg);
                    } else {
                        logger.e("getEnglishPkGroup:onPmFailure" + msg, error);
                    }
                    if (getEnglishPkGroupTimes > 10 || pkTeamEntity != null) {
                        abstractBusinessDataCallBack.onDataFail(0, msg);
                        return;
                    }
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getEnglishPkGroup(abstractBusinessDataCallBack);
                        }
                    }, (getEnglishPkGroupTimes++) * 1000);
                }
            });
        }

        @Override
        public void reportStuLike(final String testId, ArrayList<TeamMemberEntity> myTeamEntitys, final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < myTeamEntitys.size(); i++) {
                TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("stu_id", "" + teamMemberEntity.id);
                    jsonObject.put("like_num", teamMemberEntity.thisPraiseCount);
                    String oldNickName = teamMemberEntity.nickName;
                    if (StringUtils.isEmpty(oldNickName)) {
                        for (int j = 0; j < uservector.size(); j++) {
                            TeamMemberEntity user = uservector.get(j);
                            if (user.nickName.contains("_" + teamMemberEntity.id + "_")) {
                                teamMemberEntity.nickName = user.nickName;
                                break;
                            }
                        }
                    }
                    if (StringUtils.isEmpty(teamMemberEntity.nickName)) {
                        jsonObject.put("nick_name", "" + teamMemberEntity.getNick_name());
                    } else {
                        jsonObject.put("nick_name", "" + teamMemberEntity.nickName);
                    }
                    jsonArray.put(jsonObject);
                    logger.d("reportStuLike:praiseCount=" + teamMemberEntity.thisPraiseCount + ",old=" + oldNickName + ",new=" + teamMemberEntity.nickName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final String like_info = jsonArray.toString();
            String connectNickname = mLiveBll.getConnectNickname();
            final String nick_name;
            if (!StringUtils.isEmpty(connectNickname)) {
                nick_name = connectNickname;
            } else {
                nick_name = "s_3_" + mGetInfo.getId() + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
            }
            getHttpManager().reportStuLike(unique_id, mGetInfo.getStuId(), nick_name, "" + pkTeamEntity.getPkTeamId(), testId, like_info, new HttpCallBack() {
                HttpCallBack callBack = this;

                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("reportStuLike:onPmSuccess=" + responseEntity.getJsonObject());
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.d("reportStuLike:onPmError=" + responseEntity.getErrorMsg());
                    abstractBusinessDataCallBack.onDataFail(1, "" + responseEntity.getErrorMsg());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    logger.d("reportStuLike:onPmFailure:msg=" + msg + ",reportStuLike=" + reportStuLike, error);
                    if (reportStuLike > 3) {
                        abstractBusinessDataCallBack.onDataFail(0, "" + msg);
                        return;
                    }
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getHttpManager().reportStuLike(unique_id, mGetInfo.getStuId(), nick_name, "" + pkTeamEntity.getPkTeamId(), testId, like_info, callBack);
                        }
                    }, (reportStuLike++) * 1000);
                }
            });
        }
    }

    public EnTeamPkHttpManager getEnTeamPkHttpManager() {
        if (enTeamPkHttpManager == null) {
            enTeamPkHttpManager = new EnTeamPkHttpManager(getHttpManager());
        }
        return enTeamPkHttpManager;
    }

    private PkTeamEntity parsegetSelfTeamInfo(ResponseEntity responseEntity) {
        PkTeamEntity pkTeamEntity2 = getHttpResponseParser().parsegetSelfTeamInfo(responseEntity, mGetInfo.getStuId());
        if (pkTeamEntity == null && pkTeamEntity2 != null) {
            LiveGetInfo.EnglishPk englishPk = mGetInfo.getEnglishPk();
            int oldHasGroup = englishPk.hasGroup;
            mLogtf.d("parsegetSelfTeamInfo:psOpen=" + psOpen + ",mode=" + mGetInfo.getMode() + ",oldHasGroup=" + oldHasGroup);
            if (psOpen || LiveTopic.MODE_CLASS.equals(mGetInfo.getMode())) {
                englishPk.hasGroup = EnglishPk.HAS_GROUP_MAIN;
                if (oldHasGroup != EnglishPk.HAS_GROUP_MAIN) {
                    mLiveBll.postEvent(EnPkTeam.class, pkTeamEntity2);
                }
            } else {
                if (oldHasGroup != EnglishPk.HAS_GROUP_MAIN) {
                    haveTeamRun = true;
                }
            }
//            if (tcpDispatch != null) {
//                tcpDispatch.setPid(pkTeamEntity2.getPkTeamId());
//            }
            ArtsExtLiveInfo artsExtLiveInfo = mGetInfo.getArtsExtLiveInfo();
            if (artsExtLiveInfo != null) {
                int isGroupGmaeCourseWare = artsExtLiveInfo.getIsGroupGameCourseWare();
                if (isGroupGmaeCourseWare == 1) {
                    registTcp();
                    startTeam("parsegetSelfTeamInfo1");
                }
            } else if (mInteractiveTeam != null) {
                startTeam("parsegetSelfTeamInfo2");
            }
        }
        return pkTeamEntity2;
    }

    private ArrayList<TcpRunnable> tcpRun = new ArrayList<>();

    private void startTeam(String method) {
        mLogtf.d("startTeam:method=" + method + ",Team=null?" + (getStuActiveTeam == null));
        if (getStuActiveTeam == null) {
            getStuActiveTeam = new GetStuActiveTeam() {
                @Override
                public InteractiveTeam getStuActiveTeam(boolean forseGet, AbstractBusinessDataCallBack callBack) {
                    EnTeamPkIRCBll.this.getStuActiveTeam(forseGet, callBack);
                    return mInteractiveTeam;
                }

                @Override
                public PkTeamEntity getPkTeamEntity() {
                    return pkTeamEntity;
                }
            };
            putInstance(GetStuActiveTeam.class, getStuActiveTeam);
            if (mInteractiveTeam == null) {
                getEnTeamPkHttpManager().reportInteractiveInfo(mGetInfo.getStuId(), unique_id, true, new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        logger.d("reportInteractiveInfo:onPmSuccess:json=" + responseEntity.getJsonObject());
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.d("reportInteractiveInfo:onPmFailure:msg=" + msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.d("reportInteractiveInfo:onPmError:msg=" + responseEntity.getErrorMsg());
                    }
                });
            }
            mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    boolean have = XesPermission.checkPermission(activity, new LiveActivityPermissionCallback() {

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onDeny(String permission, int position) {

                        }

                        @Override
                        public void onGuarantee(String permission, int position) {

                        }
                    }, PermissionConfig.PERMISSION_CODE_CAMERA);
                    logger.d("initView:have=" + have);
                    mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
    }

    private GetStuActiveTeam getStuActiveTeam = null;

    private class TeamMessageAction implements TcpMessageAction {
        @Override
        public void onMessage(short type, int operation, String msg) {
            mLogtf.d("onMessage:type=" + type + ",operation=" + operation + ",msg=" + msg);
            switch (type) {
                case TcpConstants.TEAM_TYPE: {
                    if (operation == TcpConstants.TEAM_OPERATION_SEND) {
                        try {
                            InteractiveTeam interactiveTeam = getEnTeamPkHttpManager().parseInteractiveTeam(mGetInfo.getStuId(), new JSONObject(msg));
                            if (interactiveTeam != null) {
                                mInteractiveTeam = interactiveTeam;
                                entities = interactiveTeam.getEntities();
                                mLogtf.d("onMessage(TEAM_TYPE):entities=" + entities.size());
                            } else {
                                mLogtf.d("onMessage:interactiveTeam=null");
                            }
                            saveTeamInter(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                            mLogtf.e("onMessage(TEAM_TYPE)" + e.getMessage(), e);
                        }
                    }
                    break;
                }
            }
        }

        @Override
        public short[] getMessageFilter() {
            return new short[]{TcpConstants.TEAM_TYPE};
        }
    }

    private void parseTeamInter() {
        try {
            String string = mShareDataManager.getString(ShareDataConfig.LIVE_ENPK_MY_TEAM_INTER, "{}", ShareDataManager.SHAREDATA_USER);
            JSONObject jsonObject = new JSONObject(string);
            if (jsonObject.has(mGetInfo.getId())) {
                JSONObject liveObj = jsonObject.getJSONObject(mGetInfo.getId());
                InteractiveTeam interactiveTeam = getEnTeamPkHttpManager().parseInteractiveTeam(mGetInfo.getStuId(), liveObj);
                if (interactiveTeam != null) {
                    mInteractiveTeam = interactiveTeam;
                    entities = mInteractiveTeam.getEntities();
                }
            }
        } catch (Exception e) {
            mShareDataManager.put(ShareDataConfig.LIVE_ENPK_MY_TEAM_INTER, "{}", ShareDataManager.SHAREDATA_USER);
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

    private void saveTeamInter(String msg) {
        try {
            String string = mShareDataManager.getString(ShareDataConfig.LIVE_ENPK_MY_TEAM_INTER, "{}", ShareDataManager.SHAREDATA_USER);
            JSONObject jsonObject = new JSONObject(string);
            JSONObject liveObj = new JSONObject(msg);
            jsonObject.put(mGetInfo.getId(), liveObj);
            mShareDataManager.put(ShareDataConfig.LIVE_ENPK_MY_TEAM_INTER, "" + jsonObject, ShareDataManager.SHAREDATA_USER);
        } catch (Exception e) {
            mShareDataManager.put(ShareDataConfig.LIVE_ENPK_MY_TEAM_INTER, "{}", ShareDataManager.SHAREDATA_USER);
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

    /**
     * 强制更新小组信息
     *
     * @param forseGet
     * @param callBack
     */
    public void getStuActiveTeam(boolean forseGet, final AbstractBusinessDataCallBack callBack) {
        if (mInteractiveTeam == null) {
            mLogtf.d("getStuActiveTeam:forseGet=" + forseGet + ",mInteractiveTeam=null?true");
        } else {
            mLogtf.d("getStuActiveTeam:forseGet=" + forseGet + ",mInteractiveTeam.size=" + mInteractiveTeam.getEntities().size());
        }
        if (!forseGet && mInteractiveTeam != null) {
            if (callBack != null) {
                callBack.onDataSucess(mInteractiveTeam);
            }
            return;
        }
        if (mInteractiveTeam != null && mInteractiveTeam.getEntities().size() == 3) {
            if (callBack != null) {
                callBack.onDataSucess(mInteractiveTeam);
            }
            return;
        }
        getEnTeamPkHttpManager().getStuActiveTeam(unique_id, mGetInfo.getStuId(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                final Object object = objData[1];
                if (object instanceof JSONObject) {
                    mInteractiveTeam = (InteractiveTeam) objData[0];
                    entities = mInteractiveTeam.getEntities();
                    if (callBack != null) {
                        callBack.onDataSucess(mInteractiveTeam);
                    }
                    String msg = "" + object;
                    saveTeamInter(msg);
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                if (callBack != null) {
                    callBack.onDataFail(errStatus, failMsg);
                }
            }
        });
    }

    private void poseEvent() {
        post(new Runnable() {
            @Override
            public void run() {
                LiveGetInfo.EnglishPk englishPk = mGetInfo.getEnglishPk();
                int oldHasGroup = englishPk.hasGroup;
                if (oldHasGroup != EnglishPk.HAS_GROUP_MAIN) {
                    englishPk.hasGroup = EnglishPk.HAS_GROUP_MAIN;
                    mLiveBll.postEvent(EnPkTeam.class, pkTeamEntity);
                }
            }
        });
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN:
                logger.d("onNotice:XCR_ROOM_TEAMPK_OPEN");
                if (mGetInfo.getBetterMe().isUseBetterMe() && !mGetInfo.getBetterMe().isArriveLate()) {
                    break;
                }
                if (!psOpen) {
                    psOpen = true;
                    if (enTeamPkAction != null) {
                        enTeamPkAction.onRankStart(true);
                    }
                }
                break;
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_RESULT:
                if (mGetInfo.getBetterMe().isUseBetterMe() && !mGetInfo.getBetterMe().isArriveLate()) {
                    break;
                }
                logger.d("onNotice:XCR_ROOM_TEAMPK_RESULT:pkTeamEntity=" + pkTeamEntity);
                getEnglishPkTotalRank();
                break;
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_GO:
                logger.d("onNotice:XCR_ROOM_TEAMPK_GO:data=" + data);
                if (pkTeamEntity == null) {
                    try {
                        ResponseEntity responseEntity = new ResponseEntity();
                        responseEntity.setJsonObject(data.getJSONObject("teamInfo"));
                        pkTeamEntity = parsegetSelfTeamInfo(responseEntity);
                        enTeamPkAction.setPkTeamEntity(pkTeamEntity);
                        if (pkTeamEntity != null) {
                            pkTeamEntity.setCreateWhere(PkTeamEntity.CREATE_TYPE_IRC);
                            saveTeam(responseEntity);
                        }
                    } catch (Exception e) {
                        logger.d("XCR_ROOM_TEAMPK_GO", e);
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
                break;
            case XESCODE.ARTS_STOP_QUESTION:
                onCourseEnd();
                break;
//            case XESCODE.STOPQUESTION: {
//                onQuestionEnd();
//            }
//            break;
            case XESCODE.ARTS_H5_COURSEWARE:
                String status = data.optString("status", "off");
                if ("off".equals(status)) {
                    onQuestionEnd();
                }
                break;
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_STULIKE:
                logger.d("XCR_ROOM_TEAMPK_STULIKE:data=" + data);
                if (enTeamPkAction != null) {
                    try {
                        JSONObject likeInfoObj = data.getJSONObject("like_info");
                        String testId = likeInfoObj.getString("test_id");
                        ArrayList<TeamMemberEntity> teamMemberEntities = new ArrayList<>();
                        JSONArray stuLikeInfoArray = likeInfoObj.getJSONArray("stu_like_info");
                        for (int i = 0; i < stuLikeInfoArray.length(); i++) {
                            JSONObject stuLikeInfoobj = stuLikeInfoArray.getJSONObject(i);
                            TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                            teamMemberEntity.id = stuLikeInfoobj.getInt("stu_id");
                            teamMemberEntity.praiseCount = stuLikeInfoobj.getInt("like_num");
                            teamMemberEntities.add(teamMemberEntity);
                        }
                        enTeamPkAction.onStuLike(testId, teamMemberEntities);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case XESCODE.CLASSBEGIN:
                boolean end = data.optBoolean("end", false);
                mLogtf.d("CLASSBEGIN:end=" + end + ",classInt=" + classInt);
                if (mGetInfo.getBetterMe().isUseBetterMe() && !mGetInfo.getBetterMe().isArriveLate()) {
                    break;
                }
                if (end) {
                    try {
                        if (classInt < 0) {
                            getEnglishPkTotalRank();
                        }
                    } catch (Exception e) {
                        mLogtf.e("CLASSBEGIN", e);
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
                break;
            case XESCODE.READPACAGE:
                if (enTeamPkAction != null) {
                    enTeamPkAction.hideTeam();
                }
                break;
            default:
                break;
        }
    }

    private void onCourseEnd() {
        if (videoQuestionLiveEntity != null &&
                LiveQueConfig.EN_INTELLIGENT_EVALUTION.equals(videoQuestionLiveEntity.getArtType())) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final VideoQuestionLiveEntity old = videoQuestionLiveEntity;
                if (old != null) {
                    if (old.isTUtor()) {
                        return;
                    }
                    videoQuestionLiveEntity = null;
                    if (pkTeamEntity != null) {
                        final String teamId = "" + pkTeamEntity.getPkTeamId();
                        final String testId = ("" + old.id).replace(",", "-");
                        mLogtf.d("onCourseEnd:isShow:old=" + old.id + ",testId=" + testId + ",teamId=" + teamId);
                        getHttpManager().updataEnglishPkByTestId(teamId, testId, new HttpCallBack(false) {
                            AtomicInteger tryCount = new AtomicInteger(5);
                            HttpCallBack callBack = this;

                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                logger.d("onCourseEnd:onPmSuccess=" + responseEntity.getJsonObject());
                                EnTeamPkRankEntity enTeamPkRankEntity = getHttpResponseParser().parseUpdataEnglishPkByTestId(responseEntity, mGetInfo.getStuId());
                                if (pkTeamEntity != null && enTeamPkRankEntity != null) {
                                    ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
                                    for (int i = 0; i < myTeamEntitys.size(); i++) {
                                        TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                                        if (teamMemberEntity.isMy) {
                                            myTeamEntitys.remove(i);
                                            myTeamEntitys.add(0, teamMemberEntity);
                                            break;
                                        }
                                    }
                                    enTeamPkRankEntity.setMyTeam(pkTeamEntity.getMyTeam());
                                    updateEnpk(enTeamPkRankEntity);
                                    if (enTeamPkAction != null) {
                                        enTeamPkAction.onRankLead(enTeamPkRankEntity, testId, TeamPkLeadPager.TEAM_TYPE_1);
                                    }
                                }
                            }

                            @Override
                            public void onPmError(ResponseEntity responseEntity) {
                                super.onPmError(responseEntity);
                                logger.e("onCourseEnd:onPmError=" + responseEntity.getErrorMsg());
                            }

                            @Override
                            public void onPmFailure(Throwable error, String msg) {
                                super.onPmFailure(error, msg);
                                if (error instanceof SocketTimeoutException) {
                                    logger.e("onCourseEnd:onPmFailure(Timeout)msg=" + msg + ",try=" + tryCount.get());
                                } else {
                                    logger.e("onCourseEnd:onPmFailure=" + msg + ",try=" + tryCount.get(), error);
                                }
                                if (tryCount.decrementAndGet() > 0) {
                                    postDelayedIfNotFinish(new Runnable() {
                                        @Override
                                        public void run() {
                                            getHttpManager().updataEnglishPkByTestId(teamId, testId, callBack);
                                        }
                                    }, 1000);
                                }
                            }
                        });
                    } else {
                        mLogtf.d("onCourseEnd:isShow:old=null?" + old.id + ",pkTeamEntity=null");
                    }
                } else {
                    mLogtf.d("onCourseEnd:isShow:old=null,pkTeamEntity=null?" + (pkTeamEntity == null));
                }
            }
        };
        logger.d("onCourseEnd:isShow=" + mIsShow);
        stopQuestTime = System.currentTimeMillis();
        if (!mIsShow) {
            postDelayed(runnable, maxdelayMillis);
        } else {
            stopRunnable = runnable;
        }
    }

    private void onQuestionEnd() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final VideoQuestionLiveEntity old = videoQuestionLiveEntity;
                mLogtf.d("onQuestionEnd:isShow:old=null?" + (old == null) + ",pkTeamEntity=null?" + (pkTeamEntity == null));
                if (old != null) {
                    videoQuestionLiveEntity = null;
                    if (pkTeamEntity != null) {
                        String teamId = "" + pkTeamEntity.getPkTeamId();
                        final String testId = old.id;
                        mLogtf.d("onQuestionEnd:testId=" + testId + ",teamId=" + teamId);
                        if (TextUtils.equals(LiveQueConfig.EN_COURSE_TYPE_21, old.type)) {
                            //全身直播得仪式结束以后，请求本场成就
                            if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
                                UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
                                if (updateAchievement != null) {
                                    updateAchievement.getStuGoldCount("onQuestionEnd", UpdateAchievement.GET_TYPE_VOTE);
                                }
                            }
                        } else {
                            getHttpManager().updataEnglishPkByTestId(teamId, testId, new HttpCallBack(false) {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) {
                                    logger.d("onQuestionEnd:onPmSuccess" + responseEntity.getJsonObject());
                                    EnTeamPkRankEntity enTeamPkRankEntity = getHttpResponseParser().parseUpdataEnglishPkByTestId(responseEntity, mGetInfo.getStuId());
                                    if (pkTeamEntity != null && enTeamPkRankEntity != null) {
                                        ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
                                        for (int i = 0; i < myTeamEntitys.size(); i++) {
                                            TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                                            if (teamMemberEntity.isMy) {
                                                myTeamEntitys.remove(i);
                                                myTeamEntitys.add(0, teamMemberEntity);
                                                break;
                                            }
                                        }
                                        enTeamPkRankEntity.setMyTeam(pkTeamEntity.getMyTeam());
                                        updateEnpk(enTeamPkRankEntity);
                                        if (enTeamPkAction != null) {
                                            enTeamPkAction.onRankLead(enTeamPkRankEntity, testId, TeamPkLeadPager.TEAM_TYPE_1);
                                        }
                                    }
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    super.onPmError(responseEntity);
                                    mLogtf.d("onQuestionEnd:onPmError:testId=" + testId + "," + responseEntity.getErrorMsg());
                                }

                                @Override
                                public void onPmFailure(Throwable error, String msg) {
                                    super.onPmFailure(error, msg);
                                    logger.e("onQuestionEnd:onPmFailure" + msg, error);
                                }
                            });
                        }
                    }
                }
            }
        };
        logger.d("onQuestionEnd:isShow=" + mIsShow);
        stopQuestTime = System.currentTimeMillis();
        if (!mIsShow) {
            postDelayed(runnable, maxdelayMillis);
        } else {
            stopRunnable = runnable;
        }
    }

    private void getEnglishPkTotalRank() {
        mLogtf.d("getEnglishPkTotalRank:pkTeamEntity=null?" + (pkTeamEntity == null) + ",is=" + isEnglishPkTotalRank);
        if (pkTeamEntity != null) {
            if (classEndReg != null) {
                classEndReg.destory();
                classEndReg = null;
            }
            if (isEnglishPkTotalRank) {
                return;
            }
            isEnglishPkTotalRank = true;
            final String teamId = "" + pkTeamEntity.getPkTeamId();
            getHttpManager().getEnglishPkTotalRank(teamId, "", new HttpCallBack(false) {
                AtomicInteger tryCount = new AtomicInteger(5);
                HttpCallBack callBack = this;

                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getEnglishPkTotalRank:onPmSuccess=" + responseEntity.getJsonObject());
                    EnTeamPkRankEntity enTeamPkRankEntity = getHttpResponseParser().parseUpdataEnglishPkByTestId(responseEntity, mGetInfo.getStuId());
                    if (pkTeamEntity != null && enTeamPkRankEntity != null) {
                        ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
                        for (int i = 0; i < myTeamEntitys.size(); i++) {
                            TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                            if (teamMemberEntity.isMy) {
                                myTeamEntitys.remove(i);
                                myTeamEntitys.add(0, teamMemberEntity);
                                break;
                            }
                        }
                        enTeamPkRankEntity.setMyTeam(pkTeamEntity.getMyTeam());
                        updateEnpk(enTeamPkRankEntity);
                        if (enTeamPkAction != null) {
                            enTeamPkAction.onRankLead(enTeamPkRankEntity, "-1-end", TeamPkLeadPager.TEAM_TYPE_2);
                        }
                    }
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                    logger.e("getEnglishPkTotalRank:onPmError=" + responseEntity.getErrorMsg());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                    logger.e("getEnglishPkTotalRank:onPmFailure=" + msg, error);
                    if (tryCount.decrementAndGet() > 0) {
                        postDelayedIfNotFinish(new Runnable() {
                            @Override
                            public void run() {
                                getHttpManager().getEnglishPkTotalRank(teamId, "", callBack);
                            }
                        }, 1000);
                    }
                }
            });
        }
    }

    private void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity) {
        UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
        if (updateAchievement != null) {
            updateAchievement.updateEnpk(enTeamPkRankEntity);
        }
    }

    @Override
    public int[] getNoticeFilter() {
        // XESCODE.STOPQUESTION
        return new int[]{XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN, XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_RESULT, XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_GO, XESCODE.READPACAGE,
                XESCODE.ARTS_STOP_QUESTION, XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_STULIKE, XESCODE.ARTS_H5_COURSEWARE, XESCODE.CLASSBEGIN};
    }

    /**
     * 辅导态满12人分队，教师端未开启战队分配，切换主讲分队，再用辅导切换到辅导态，会再次展示分队仪式
     *
     * @param liveTopic
     * @param jsonObject
     * @param modeChange
     */
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        //辅导模式，战队PK在小目标后显示
        if (LiveTopic.MODE_TRANING.equals(mGetInfo.getMode()) && mGetInfo.getBetterMe().isUseBetterMe() && !mGetInfo
                .getBetterMe().isArriveLate()) {
            return;
        }
        //退出重进不显示分队仪式
        try {
            JSONObject room_2 = jsonObject.getJSONObject("room_2");
            JSONObject teamPKObj = room_2.optJSONObject("teamPK");
            if (teamPKObj != null) {
                boolean status = teamPKObj.optBoolean("status", false);
                if (status) {
                    logger.d("onTopic:psOpen=" + psOpen);
                    if (!psOpen) {
                        psOpen = true;
                        //firstTopic>1,说明不是退出重进
                        if (enTeamPkAction != null) {
                            enTeamPkAction.onRankStart(false);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        mLogtf.d("onModeChange:haveTeamRun=" + haveTeamRun);
        boolean oldHaveTeamRun = haveTeamRun;
        if (haveTeamRun) {
            haveTeamRun = false;
            poseEvent();
        }
        if (enTeamPkAction != null) {
            enTeamPkAction.onModeChange(mode, oldHaveTeamRun);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destory = true;
        if (classEndReg != null) {
            classEndReg.destory();
            classEndReg = null;
        }
        if (classEndRec != null) {
            LiveEventBus.getDefault(mContext).unregister(classEndRec);
        }
        if (enTeamPkAction != null) {
            enTeamPkAction.destory();
        }
        if (tcpDispatch != null) {
            tcpDispatch.stop();
            tcpDispatch = null;
        }
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect(IRCConnection connection) {
        if (!firstConnect.get()) {
            firstConnect.set(true);
            mLogtf.d("onConnect:reportStuInfoRun=null?" + (reportStuInfoRun == null));
            Runnable freportStuInfoRun = reportStuInfoRun;
            reportStuInfoRun = null;
            if (freportStuInfoRun != null) {
                freportStuInfoRun.run();
            }
        }
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
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {

    }

    private Vector<TeamMemberEntity> uservector = new Vector<>();

    @Override
    public void onUserList(String channel, User[] users) {
        int old = users.length;
        int old2 = uservector.size();
        a:
        for (int i = 0; i < users.length; i++) {
            User user = users[i];
            if (isMyTeam(user.getNick())) {
                for (int j = 0; j < uservector.size(); j++) {
                    TeamMemberEntity olduser = uservector.get(j);
                    if (olduser.nickName.equals(user.getNick())) {
                        continue a;
                    }
                }
                TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                teamMemberEntity.nickName = user.getNick();
                uservector.addElement(teamMemberEntity);
            }
        }
        logger.d("onUserList:old=" + old + ",old2=" + old2 + ",new=" + uservector.size());
    }

    private boolean isMyTeam(String sender) {
        if (pkTeamEntity != null) {
            ArrayList<TeamMemberEntity> myTeamEntitys = pkTeamEntity.getaTeamMemberEntity();
            boolean isMy = false;
            for (int i = 0; i < myTeamEntitys.size(); i++) {
                TeamMemberEntity teamMemberEntity = myTeamEntitys.get(i);
                if (sender.contains("_" + teamMemberEntity.id + "_")) {
                    isMy = true;
                    teamMemberEntity.nickName = sender;
                    mLogtf.d("isMyTeam:sender=" + sender);
                    break;
                }
            }
            return isMy;
        }
        return true;
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        if (isMyTeam(sender)) {
            TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
            teamMemberEntity.nickName = sender;
            uservector.addElement(teamMemberEntity);
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        for (int j = 0; j < uservector.size(); j++) {
            TeamMemberEntity user = uservector.get(j);
            if (("" + sourceNick).equals(user.nickName)) {
                uservector.remove(j);
                mLogtf.d("onQuit:sourceNick=" + sourceNick);
                break;
            }
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        super.setVideoLayout(liveVideoPoint);
        if (enTeamPkAction != null) {
            enTeamPkAction.setVideoLayout(liveVideoPoint);
        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }
}

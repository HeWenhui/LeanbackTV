package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.app.Activity;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.x;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import ren.yale.android.cachewebviewlib.utils.JsonWrapper;

/**
 * @author linyuqiang
 * created  at 2018/11/6
 * 英语战队PK 相关业务处理
 */
public class EnTeamPkIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private EnTeamPkAction enTeamPkAction;
    private String unique_id;
    private boolean psOpen = false;
    private PkTeamEntity pkTeamEntity;
    private VideoQuestionLiveEntity videoQuestionLiveEntity;

    public EnTeamPkIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        unique_id = mGetInfo.getId() + "_" + mGetInfo.getStudentLiveInfo().getClassId();
        LiveGetInfo.EnglishPk englishPk = getInfo.getEnglishPk();
        logger.d("onLiveInited:unique_id=" + unique_id + ",use==" + englishPk.canUsePK + ",has=" + englishPk.hasGroup);
        if (AppConfig.DEBUG) {
            englishPk.canUsePK = 1;
            englishPk.hasGroup = 0;
        }
        EnTeamPkBll teamPkBll = new EnTeamPkBll(activity);
        teamPkBll.setRootView(mRootView);
        teamPkBll.setEnTeamPkHttp(new EnTeamPkHttpImp());
        if (englishPk.hasGroup == 1) {
            String string = mShareDataManager.getString(ShareDataConfig.LIVE_ENPK_MY_TEAM, "{}", ShareDataManager.SHAREDATA_USER);
            try {
                JSONObject jsonObject = new JSONObject(string);
                if (jsonObject.has(getInfo.getId())) {
                    ResponseEntity responseEntity = new ResponseEntity();
                    responseEntity.setJsonObject(jsonObject.getJSONObject(getInfo.getId()));
                    pkTeamEntity = getHttpResponseParser().parsegetSelfTeamInfo(responseEntity, mGetInfo.getStuId());
                    teamPkBll.setPkTeamEntity(pkTeamEntity);
                }
            } catch (Exception e) {
                pkTeamEntity = null;
                CrashReport.postCatchedException(e);
            }
        }
        enTeamPkAction = teamPkBll;
        enTeamPkAction.onLiveInited(getInfo);
        EnTeamPkQuestionShowAction enTeamPkQuestionShowAction = new EnTeamPkQuestionShowAction();
        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(enTeamPkQuestionShowAction);
        }
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(enTeamPkQuestionShowAction);
        }
//        if (AppConfig.DEBUG) {
//            EnTeamPkRankEntity enTeamPkRankEntity = new EnTeamPkRankEntity();
//            enTeamPkRankEntity.setApkTeamId(2);
//            enTeamPkRankEntity.setaCurrentScore(13);
//            enTeamPkRankEntity.setaTotalScore(31);
//            ArrayList<TeamMemberEntity> memberEntities = enTeamPkRankEntity.getMemberEntities();
//            for (int i = 0; i < 4; i++) {
//                TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
//                teamMemberEntity.id = 100 + i;
//                if (i == 0) {
//                    teamMemberEntity.isMy = true;
//                }
//                teamMemberEntity.name = "测试" + i;
//                teamMemberEntity.energy = 10 + i;
//                memberEntities.add(teamMemberEntity);
//            }
//            enTeamPkRankEntity.setBpkTeamId(3);
//            enTeamPkRankEntity.setbCurrentScore(14);
//            enTeamPkRankEntity.setbTotalScore(34);
//            enTeamPkAction.onRankLead(enTeamPkRankEntity);
//        }
    }

    class EnTeamPkQuestionShowAction implements QuestionShowAction {

        @Override
        public void onQuestionShow(VideoQuestionLiveEntity questionLiveEntity, boolean isShow) {
            if (isShow) {
                logger.d("onQuestionShow:isShow");
                videoQuestionLiveEntity = questionLiveEntity;
            }
        }
    }

    class EnTeamPkHttpImp implements EnTeamPkHttp {
        int getSelfTeamInfoTimes = 1;

        private void saveTeam(ResponseEntity responseEntity) {
            String string = mShareDataManager.getString(ShareDataConfig.LIVE_ENPK_MY_TEAM, "{}", ShareDataManager.SHAREDATA_USER);
            try {
                JSONObject jsonObject = new JSONObject(string);
                jsonObject.put(mGetInfo.getId(), responseEntity.getJsonObject());
                mShareDataManager.put(ShareDataConfig.LIVE_ENPK_MY_TEAM, jsonObject.toString(), ShareDataManager.SHAREDATA_USER);
            } catch (JSONException e) {
                mShareDataManager.put(ShareDataConfig.LIVE_ENPK_MY_TEAM, "{}", ShareDataManager.SHAREDATA_USER);
                CrashReport.postCatchedException(e);
            }
        }

        @Override
        public void getSelfTeamInfo(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            getHttpManager().getSelfTeamInfo(mGetInfo.getStuId(), unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getSelfTeamInfo:onPmSuccess" + responseEntity.getJsonObject());
                    pkTeamEntity = getHttpResponseParser().parsegetSelfTeamInfo(responseEntity, mGetInfo.getStuId());
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
            //s_lliveID_liveType_stuID_sex
            String nick_name = "s_" + mGetInfo.getId() + "_3_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
            logger.d("reportStuInfo:nick_name=" + nick_name);
            LiveGetInfo.EnglishPk englishPk = mGetInfo.getEnglishPk();
            getHttpManager().reportStuInfo(mGetInfo.getStuId(), mGetInfo.getStuName(), mGetInfo.getStuImg(), "" + englishPk.historyScore, "" + englishPk.isTwoLose, nick_name, unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("reportStuInfo:onPmSuccess" + responseEntity.getJsonObject());
                    abstractBusinessDataCallBack.onDataSucess(responseEntity);
                    if (AppConfig.DEBUG) {
                        if (enTeamPkAction != null) {
                            enTeamPkAction.onRankStart();
                        }
                    }
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("reportStuInfo:onPmError" + responseEntity.getErrorMsg());
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
        public void getEnglishPkRank(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            getHttpManager().getEnglishPkRank(new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getEnglishPkRank:onPmSuccess" + responseEntity.getJsonObject());
                    pkTeamEntity = getHttpResponseParser().parsegetSelfTeamInfo(responseEntity, mGetInfo.getStuId());
                    abstractBusinessDataCallBack.onDataSucess(pkTeamEntity);
                    if (pkTeamEntity != null) {
                        saveTeam(responseEntity);
                    }
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("getEnglishPkRank:onPmError=" + responseEntity.getErrorMsg());
                    abstractBusinessDataCallBack.onDataFail(1, responseEntity.getErrorMsg());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    if (error instanceof SocketTimeoutException) {
                        logger.e("getEnglishPkRank:onPmFailure(Timeout)msg=" + msg);
                    } else {
                        logger.e("getEnglishPkRank:onPmFailure" + msg, error);
                    }
                    abstractBusinessDataCallBack.onDataFail(0, msg);
                }
            });
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.XCR_ROOM_TEAMPK_OPEN:
                logger.d("onNotice:XCR_ROOM_TEAMPK_OPEN");
                if (!psOpen) {
                    psOpen = true;
                    if (enTeamPkAction != null) {
                        enTeamPkAction.onRankStart();
                    }
                }
                break;
            case XESCODE.XCR_ROOM_TEAMPK_RESULT:
                logger.d("onNotice:XCR_ROOM_TEAMPK_RESULT:pkTeamEntity=" + pkTeamEntity);
                if (pkTeamEntity != null) {
                    String teamId = "" + pkTeamEntity.getMyTeam();
                    getHttpManager().updataEnglishPkByTestId(teamId, "", new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) {
                            logger.d("updataEnglishPkByTestId:onPmSuccess" + responseEntity.getJsonObject());
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            logger.e("updataEnglishPkByTestId:onPmError" + responseEntity.getErrorMsg());
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            logger.e("updataEnglishPkByTestId:onPmFailure" + msg, error);
                        }
                    });
                }
                break;
            case XESCODE.STOPQUESTION: {
                VideoQuestionLiveEntity old = videoQuestionLiveEntity;
                mLogtf.d("STOPQUESTION:isShow:old=null?" + (old == null) + ",pkTeamEntity=null?" + (pkTeamEntity == null));
                if (old != null) {
                    videoQuestionLiveEntity = null;
                    if (pkTeamEntity != null) {
                        String teamId = "" + pkTeamEntity.getMyTeam();
                        getHttpManager().updataEnglishPkByTestId(teamId, old.id, new HttpCallBack() {
                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                logger.d("updataEnglishPkByTestId:onPmSuccess" + responseEntity.getJsonObject());
                                EnTeamPkRankEntity enTeamPkRankEntity = getHttpResponseParser().parseUpdataEnglishPkByTestId(responseEntity);
                                if (pkTeamEntity != null && enTeamPkRankEntity != null) {
                                    enTeamPkRankEntity.setMyTeam(pkTeamEntity.getMyTeam());
                                    if (enTeamPkAction != null) {
                                        enTeamPkAction.onRankLead(enTeamPkRankEntity);
                                    }
                                }
                            }

                            @Override
                            public void onPmError(ResponseEntity responseEntity) {
                                super.onPmError(responseEntity);
                                logger.e("updataEnglishPkByTestId:onPmError" + responseEntity.getErrorMsg());
                            }

                            @Override
                            public void onPmFailure(Throwable error, String msg) {
                                super.onPmFailure(error, msg);
                                logger.e("updataEnglishPkByTestId:onPmFailure" + msg, error);
                            }
                        });
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.XCR_ROOM_TEAMPK_OPEN, XESCODE.XCR_ROOM_TEAMPK_RESULT, XESCODE.STOPQUESTION};
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        try {
            JSONObject room_2 = jsonObject.getJSONObject("room_2");
            JSONObject teamPKObj = room_2.optJSONObject("teamPK");
            if (teamPKObj != null) {
                boolean status = teamPKObj.optBoolean("status", false);
                if (status) {
                    logger.d("onTopic:psOpen=" + psOpen);
                    if (!psOpen) {
                        psOpen = true;
                        if (enTeamPkAction != null) {
                            enTeamPkAction.onRankStart();
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
        if (enTeamPkAction != null) {
            enTeamPkAction.onModeChange(mode);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (enTeamPkAction != null) {
            enTeamPkAction.destory();
        }
    }
}

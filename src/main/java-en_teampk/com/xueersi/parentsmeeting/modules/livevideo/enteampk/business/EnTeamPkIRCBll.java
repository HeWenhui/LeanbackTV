package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.app.Activity;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;

import org.json.JSONException;
import org.json.JSONObject;

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

    public EnTeamPkIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        logger.d("onLiveInited");
        unique_id = mGetInfo.getId() + "_" + mGetInfo.getStudentLiveInfo().getClassId();
        EnTeamPkBll teamPkBll = new EnTeamPkBll(activity);
        teamPkBll.setRootView(mRootView);
        teamPkBll.setEnTeamPkHttp(new EnTeamPkHttpImp());
        enTeamPkAction = teamPkBll;
        LiveGetInfo.EnglishPk englishPk = mGetInfo.getEnglishPk();
        enTeamPkAction.onLiveInited(getInfo);
//        enTeamPkAction.onRankLead();
        EnTeamPkQuestionShowAction enTeamPkQuestionShowAction = new EnTeamPkQuestionShowAction();
        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(enTeamPkQuestionShowAction);
        }
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(enTeamPkQuestionShowAction);
        }
    }

    class EnTeamPkQuestionShowAction implements QuestionShowAction {
        VideoQuestionLiveEntity videoQuestionLiveEntity;

        @Override
        public void onQuestionShow(VideoQuestionLiveEntity questionLiveEntity, boolean isShow) {
            if (isShow) {
                logger.d("onQuestionShow:isShow");
                this.videoQuestionLiveEntity = questionLiveEntity;
            } else {
                VideoQuestionLiveEntity old = videoQuestionLiveEntity;
                mLogtf.d("onQuestionShow:isShow:old=null?" + (old == null) + ",pkTeamEntity=null?" + (pkTeamEntity == null));
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
        }
    }

    class EnTeamPkHttpImp implements EnTeamPkHttp {
        int getSelfTeamInfoTimes = 1;

        @Override
        public void getSelfTeamInfo(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            getHttpManager().getSelfTeamInfo(mGetInfo.getStuId(), unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getSelfTeamInfo:onPmSuccess" + responseEntity.getJsonObject());
                    pkTeamEntity = getHttpResponseParser().parsegetSelfTeamInfo(responseEntity, mGetInfo.getStuId());
                    abstractBusinessDataCallBack.onDataSucess(pkTeamEntity);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("getSelfTeamInfo:onPmError" + responseEntity.getErrorMsg());
                    abstractBusinessDataCallBack.onDataFail(1, responseEntity.getErrorMsg());
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getSelfTeamInfo(abstractBusinessDataCallBack);
                        }
                    }, (getSelfTeamInfoTimes++) * 1000);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    logger.e("getSelfTeamInfo:onPmFailure" + msg, error);
                    abstractBusinessDataCallBack.onDataFail(0, msg);
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getSelfTeamInfo(abstractBusinessDataCallBack);
                        }
                    }, (getSelfTeamInfoTimes++) * 1000);
                }
            });
        }

        @Override
        public void reportStuInfo(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            //s_lliveID_liveType_stuID_sex
            String nick_name = "s_" + mGetInfo.getId() + "_3_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
            LiveGetInfo.EnglishPk englishPk = mGetInfo.getEnglishPk();
            getHttpManager().reportStuInfo(mGetInfo.getStuId(), mGetInfo.getStuName(), mGetInfo.getStuImg(), "" + englishPk.historyScore, "" + englishPk.isTwoLose, nick_name, unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("reportStuInfo:onPmSuccess" + responseEntity.getJsonObject());
                    abstractBusinessDataCallBack.onDataSucess(responseEntity);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("reportStuInfo:onPmError" + responseEntity.getErrorMsg());
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            reportStuInfo(abstractBusinessDataCallBack);
                        }
                    }, (getSelfTeamInfoTimes++) * 1000);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    logger.e("reportStuInfo:onPmFailure" + msg, error);
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
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.XCR_ROOM_TEAMPK_OPEN, XESCODE.XCR_ROOM_TEAMPK_RESULT};
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        try {
            JSONObject room_2 = jsonObject.getJSONObject("room_2");
            JSONObject teamPKObj = room_2.optJSONObject("teamPK");
            if (teamPKObj != null) {
                boolean status = teamPKObj.optBoolean("stastus", false);
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
}

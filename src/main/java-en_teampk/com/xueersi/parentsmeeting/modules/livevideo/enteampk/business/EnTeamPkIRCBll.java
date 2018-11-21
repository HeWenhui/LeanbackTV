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
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

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
//        enTeamPkAction.onRankLead();
    }

    class EnTeamPkHttpImp implements EnTeamPkHttp {
        @Override
        public void getSelfTeamInfo(final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            getHttpManager().getSelfTeamInfo(mGetInfo.getStuId(), unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getSelfTeamInfo:onPmSuccess" + responseEntity.getJsonObject());
                    PkTeamEntity pkTeamEntity = getHttpResponseParser().parsegetSelfTeamInfo(responseEntity, mGetInfo.getStuId());
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
                    }, 2000);
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
                    }, 1000);
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
                    }, 2000);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    logger.e("reportStuInfo:onPmFailure" + msg, error);
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            reportStuInfo(abstractBusinessDataCallBack);
                        }
                    }, 1000);
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
                logger.d("onNotice:XCR_ROOM_TEAMPK_RESULT");
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

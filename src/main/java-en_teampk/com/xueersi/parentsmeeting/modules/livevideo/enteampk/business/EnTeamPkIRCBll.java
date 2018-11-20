package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.app.Activity;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

/**
 * @author linyuqiang
 * created  at 2018/11/6
 * 英语战队PK 相关业务处理
 */
public class EnTeamPkIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private EnTeamPkAction enTeamPkAction;
    private String unique_id;

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
        enTeamPkAction.onRankStart();
//        enTeamPkAction.onRankLead();
    }

    class EnTeamPkHttpImp implements EnTeamPkHttp {
        @Override
        public void getSelfTeamInfo(AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            getHttpManager().getSelfTeamInfo(mGetInfo.getStuId(), unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("getSelfTeamInfo:onPmSuccess" + responseEntity.getJsonObject());
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("getSelfTeamInfo:onPmError" + responseEntity.getErrorMsg());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    logger.e("getSelfTeamInfo:onPmFailure" + msg, error);
                }
            });
        }

        @Override
        public void reportStuInfo(AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
            //s_lliveID_liveType_stuID_sex
            String nick_name = "s_" + mGetInfo.getId() + "_3_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
            LiveGetInfo.EnglishPk englishPk = mGetInfo.getEnglishPk();
            getHttpManager().reportStuInfo(mGetInfo.getStuId(), mGetInfo.getStuName(), mGetInfo.getStuImg(), "" + englishPk.historyScore, "" + englishPk.isTwoLose, nick_name, unique_id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    logger.d("reportStuInfo:onPmSuccess" + responseEntity.getJsonObject());
                    enTeamPkAction.onRankResult();
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.e("reportStuInfo:onPmError" + responseEntity.getErrorMsg());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    logger.e("reportStuInfo:onPmFailure" + msg, error);
                }
            });
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {

    }

    @Override
    public int[] getNoticeFilter() {
        return new int[0];
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }
}

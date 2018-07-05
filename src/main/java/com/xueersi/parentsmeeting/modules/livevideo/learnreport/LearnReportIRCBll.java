package com.xueersi.parentsmeeting.modules.livevideo.learnreport;

import android.app.Activity;
import android.os.Environment;
import android.view.ViewGroup;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.business.LearnReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LearnReportBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallBll;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by lyqai on 2018/7/5.
 */

public class LearnReportIRCBll extends LiveBaseBll implements NoticeAction {
    /** 学习报告事件 */
    private LearnReportAction mLearnReportAction;
    private LogToFile mLogtf;
    /**
     * 签到成功 状态码
     */
    private static final int SIGN_STATE_CODE_SUCCESS = 2;

    public LearnReportIRCBll(Activity context, LiveBll2 liveBll, ViewGroup rootView) {
        super(context, liveBll, rootView);
        mLogtf = new LogToFile(context, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLearnReportAction = new LearnReportBll(context);
    }


    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (mLiveType == LiveBll2.LIVE_TYPE_LIVE && mGetInfo.getStudentLiveInfo() != null) {
            if (mGetInfo.getStudentLiveInfo().getEvaluateStatus() == 1) {
                mLogtf.d("onGetInfoSuccess:getLearnReport");
                getLearnReport(1, 1000);
            }
            mLogtf.d("onGetInfoSuccess:getSignStatus=" + mGetInfo.getStudentLiveInfo().getSignStatus());
            //  根据 借口返回状态  判断是否显示签到
        }
    }

    @Override
    public void onNotice(JSONObject data, int type) {
        switch (type) {
            case XESCODE.LEARNREPORT: {
                getLearnReport(2, 1000);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.LEARNREPORT};
    }

    /**
     * 获取学习报告
     */
    private synchronized void getLearnReport(final int from, final long delayTime) {
        XesMobAgent.liveLearnReport("request:" + from);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getLearnReport:enstuId=" + enstuId + ",liveType=" + mLiveType + ",liveId=" + mLiveId + "," +
                "delayTime=" + delayTime);
        getHttpManager().getLearnReport(enstuId, mLiveId, mLiveType, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                LearnReportEntity learnReportEntity = getHttpResponseParser().parseLearnReport(responseEntity);
                if (learnReportEntity != null) {
                    learnReportEntity.getStu().setStuName(mGetInfo.getStuName());
                    learnReportEntity.getStu().setTeacherName(mGetInfo.getTeacherName());
                    learnReportEntity.getStu().setTeacherIMG(mGetInfo.getTeacherIMG());
                    if (mLearnReportAction != null) {
                        mLearnReportAction.onLearnReport(learnReportEntity);
                    }
                }
                XesMobAgent.liveLearnReport("request-ok:" + from);
                mLogtf.d("getLearnReport:onPmSuccess:learnReportEntity=" + (learnReportEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                XesMobAgent.liveLearnReport("request-fail:" + from);
                mLogtf.d("getLearnReport:onPmFailure=" + error + ",msg=" + msg + ",delayTime=" + delayTime);
                if (delayTime < 15000) {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getLearnReport(3, delayTime + 5000);
                        }
                    }, delayTime);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                XesMobAgent.liveLearnReport("request-error:" + from);
                mLogtf.d("getLearnReport:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }


}

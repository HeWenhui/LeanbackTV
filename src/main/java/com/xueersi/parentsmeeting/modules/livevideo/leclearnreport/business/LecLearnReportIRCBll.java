package com.xueersi.parentsmeeting.modules.livevideo.leclearnreport.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LecLearnReportBll;

import org.json.JSONObject;

/**
 * Created by lyqai on 2018/7/18.
 */
public class LecLearnReportIRCBll extends LiveBaseBll implements NoticeAction, LecLearnReportHttp {
    LecLearnReportBll learnReportBll;

    public LecLearnReportIRCBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
    }

    @Override
    public void onNotice(JSONObject data, int type) {
        switch (type) {
            case XESCODE.LEC_LEARNREPORT: {
                if (learnReportBll == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            learnReportBll = new LecLearnReportBll(activity);
                            learnReportBll.setLiveId(mLiveId);
                            learnReportBll.setLiveBll(LecLearnReportIRCBll.this);
                            learnReportBll.setmShareDataManager(mShareDataManager);
                            onLearnReport(mLiveId);
                        }
                    });
                } else {
                    onLearnReport(mLiveId);
                }
                break;
            }
            default:
                break;
        }
    }

    void onLearnReport(String liveId) {
        learnReportBll.onLearnReport(mLiveId);
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.LEC_LEARNREPORT};
    }

    @Override
    public void getLecLearnReport(final long delayTime, final AbstractBusinessDataCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getLecLearnReport:enstuId=" + enstuId + ",liveType=" + mLiveType + ",liveId=" + mLiveId + "," +
                "delayTime=" + delayTime);
        getHttpManager().getLearnReport(enstuId, mLiveId, mLiveType, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                LearnReportEntity learnReportEntity = getHttpResponseParser().parseLecLearnReport(responseEntity);
                if (learnReportEntity != null) {
                    learnReportEntity.getStu().setStuName(mGetInfo.getStuName());
                    learnReportEntity.getStu().setTeacherName(mGetInfo.getTeacherName());
                    learnReportEntity.getStu().setTeacherIMG(mGetInfo.getTeacherIMG());
                    callBack.onDataSucess(learnReportEntity);
                }
                mLogtf.d("getLecLearnReport:onPmSuccess:learnReportEntity=" + (learnReportEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getLecLearnReport:onPmFailure=" + error + ",msg=" + msg + ",delayTime=" + delayTime);
                if (delayTime < 15000) {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getLecLearnReport(delayTime + 5000, callBack);
                        }
                    }, delayTime);
                } else {
                    callBack.onDataFail(0, msg);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getLecLearnReport:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }
}

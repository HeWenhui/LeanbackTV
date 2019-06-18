package com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business;

import android.app.Activity;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LearnPsReportBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import org.json.JSONObject;

/**
 * Created by lyqai on 2018/7/5.
 */

public class LearnReportIRCBll extends LiveBaseBll implements NoticeAction {
    /** 学习报告事件 */
    private LearnReportAction mLearnReportAction;
    private LearnReportAction mLearnPsReportAction;
    /**
     * 签到成功 状态码
     */
    private static final int SIGN_STATE_CODE_SUCCESS = 2;

    public LearnReportIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }


    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE && mGetInfo.getStudentLiveInfo() != null) {
            if (mGetInfo.getStudentLiveInfo().getEvaluateStatus() == 1) {
                mLogtf.d("onGetInfoSuccess:getLearnReport");
                getLearnReport(1, 1000);
            }
            mLogtf.d("onGetInfoSuccess:getSignStatus=" + mGetInfo.getStudentLiveInfo().getSignStatus());
            //  根据 借口返回状态  判断是否显示签到
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
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
        final String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
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
                    if (LiveVideoConfig.isPrimary && mLearnPsReportAction == null) {
                        LearnPsReportBll reportBll = new LearnPsReportBll(activity);
                        reportBll.initView(mRootView);
                        reportBll.setLiveBll(new LearnReportHttp() {
                            @Override
                            public void sendTeacherEvaluate(int[] score, HttpCallBack requestCallBack) {
                                LearnReportIRCBll.this.sendTeacherEvaluate(score, requestCallBack);
                            }

                            @Override
                            public void showToast(String errorMsg) {
                                mLiveBll.showToast(errorMsg);
                            }
                        });
                        mLearnReportAction = reportBll;
                        mLearnReportAction.onLearnReport(learnReportEntity);
                    } else {
                        if (mLearnReportAction == null) {
                            LearnReportBll reportBll = new LearnReportBll(activity);
                            reportBll.initView(mRootView);
                            reportBll.setLiveBll(new LearnReportHttp() {
                                @Override
                                public void sendTeacherEvaluate(int[] score, HttpCallBack requestCallBack) {
                                    LearnReportIRCBll.this.sendTeacherEvaluate(score, requestCallBack);
                                }

                                @Override
                                public void showToast(String errorMsg) {
                                    mLiveBll.showToast(errorMsg);
                                }
                            });
                            mLearnReportAction = reportBll;
                        }
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

    /**
     * 提交教师评价
     */
    public synchronized void sendTeacherEvaluate(int[] score, final HttpCallBack requestCallBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("sendTeacherEvaluate:enstuId=" + enstuId + ",liveId=" + mLiveId);
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().sendTeacherEvaluate(enstuId, mLiveId, classId, score, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                requestCallBack.onPmSuccess(responseEntity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                requestCallBack.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                requestCallBack.onPmError(responseEntity);
                //onLiveError(responseEntity);
            }
        });
    }
}

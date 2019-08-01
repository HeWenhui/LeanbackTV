package com.xueersi.parentsmeeting.modules.livevideoOldIJK.leclearnreport.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business.LecLearnReportBll;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/18.
 */
public class LecLearnReportIRCBll extends LiveBaseBll implements NoticeAction, LecLearnReportHttp, TopicAction {
    LecLearnReportBll learnReportBll;

    public LecLearnReportIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
        if (mainRoomstatus.isOpenFeedback()) {
            if (learnReportBll == null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        learnReportBll = new LecLearnReportBll(activity);
                        learnReportBll.setLiveId(mLiveId);
                        learnReportBll.setLiveBll(LecLearnReportIRCBll.this);
                        learnReportBll.setmShareDataManager(mShareDataManager);
                        learnReportBll.initView(mRootView);
                        onLearnReport(mLiveId);
                    }
                });
            } else {
                learnReportBll.onLearnReport(mLiveId);
            }
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
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
                            learnReportBll.initView(mRootView);
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
        String enstuId = LiveAppUserInfo.getInstance().getEnstuId();
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

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        if (learnReportBll != null) {
            learnReportBll.initView(bottomContent);
        }
    }

}

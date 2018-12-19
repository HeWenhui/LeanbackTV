package com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

/**
 * 英语小目标 presenter层
 *
 * @author zhangyuansun
 * created  at 2018/11/28
 */
public class BetterMeIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, BetterMeContract.BetterMePresenter {
    BetterMeContract.BetterMeView mBetterMeView;
    StuSegmentEntity mStuSegmentEntity;
    AimRealTimeValEntity mAimRealTimeValEntity;
    StuAimResultEntity mStuAimResultEntity;

    public BetterMeIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mBetterMeView = new BetterMePager(mContext);
        mBetterMeView.setPresenter(this);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {

    }

    @Override
    public int[] getNoticeFilter() {
        return new int[0];
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        mBetterMeView.setRootView(mRootView);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }

    /**
     * 获取学生段位信息
     */
    @Override
    public void getStuSegment() {
        getHttpManager().getStuSegment(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getStuSegment:onPmSuccess():json=" + responseEntity.getJsonObject());
                mStuSegmentEntity = getHttpResponseParser().parseStuSegmentInfo(responseEntity);
                if (mStuSegmentEntity != null && mAimRealTimeValEntity != null) {
                    mBetterMeView.showReceiveTargetPager(mStuSegmentEntity, mAimRealTimeValEntity);
                    mStuSegmentEntity = null;
                    mAimRealTimeValEntity = null;
                }
            }
        });
    }

    /**
     * 获取学生这节课小目标
     */
    @Override
    public void getBetterMe() {
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getBetterMe(liveId, courseId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getBetterMe:onPmSuccess():json=" + responseEntity.getJsonObject());
                mAimRealTimeValEntity = getHttpResponseParser().parseBetterMeInfo(responseEntity);
                if (mStuSegmentEntity != null && mAimRealTimeValEntity != null) {
                    mBetterMeView.showReceiveTargetPager(mStuSegmentEntity, mAimRealTimeValEntity);
                    mStuSegmentEntity = null;
                    mAimRealTimeValEntity = null;
                }
            }
        });
    }

    /**
     * 实时获取学生目标完成度
     */
    @Override
    public void getStuAimResult() {
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getStuAimResult(liveId, courseId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getBetterMe:onPmSuccess():json=" + responseEntity.getJsonObject());
                mStuAimResultEntity = getHttpResponseParser().parseStuAimResultInfo(responseEntity);
                if (mStuAimResultEntity != null) {
                    mBetterMeView.showCompleteTargetPager(mStuAimResultEntity);
                }
            }
        });
    }
}

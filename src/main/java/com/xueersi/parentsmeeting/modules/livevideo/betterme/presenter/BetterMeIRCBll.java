package com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMeViewImp;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 英语小目标 presenter层
 *
 * @author zhangyuansun
 * created  at 2018/11/28
 */
public class BetterMeIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, BetterMeContract
        .BetterMePresenter {
    BetterMeContract.BetterMeView mBetterMeView;
    StuSegmentEntity mStuSegmentEntity;
    BetterMeEntity mBetterMeEntity;
    StuAimResultEntity mStuAimResultEntity;
    private boolean isArriveLate = false;
    /**
     * 小目标接口开关
     */
    private boolean isUseBetterMe = true;
    /**
     * 是否展示过本场小目标
     */
    private boolean isShowBetterMe = false;

    public BetterMeIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mBetterMeView = new BetterMeViewImp(mContext);
        mBetterMeView.setPresenter(this);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN: {
                getBetterMe();
                break;
            }
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_RESULT: {
                getStuAimResult();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN,
                XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_RESULT
        };
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        mBetterMeView.setRootView(mRootView);
    }

    @Override
    public void onArtsExtLiveInited(LiveGetInfo getInfo) {
        super.onArtsExtLiveInited(getInfo);
//        this.isArriveLate = getInfo.getArtsExtLiveInfo().isArriveLate();
//        this.isUseBetterMe = getInfo.getArtsExtLiveInfo().isUseBetterMe();
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("onTopic():jsonObject=" + jsonObject);
        try {
            JSONObject room_2 = jsonObject.getJSONObject("room_2");
            JSONObject teamPKObj = room_2.optJSONObject("teamPK");
            if (teamPKObj != null) {
                boolean status = teamPKObj.optBoolean("status", false);
                if (status) {
                    getBetterMe();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取学生这节课小目标
     */
    @Override
    synchronized public void getBetterMe() {
        if (isShowBetterMe || !isUseBetterMe) {
            return;
        }
        isShowBetterMe = true;
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getBetterMe(liveId, courseId, new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getBetterMe:onPmSuccess():json=" + responseEntity.getJsonObject());
                mBetterMeEntity = getHttpResponseParser().parseBetterMeInfo(responseEntity);
                if (mBetterMeEntity != null) {
                    if (mStuSegmentEntity != null) {
                        if (mBetterMeEntity.isFirstReceive()) {
                            mBetterMeView.showIntroductionPager();
                        } else {
                            mBetterMeView.showReceiveTargetPager();
                        }
                    } else {
                        getStuSegment();
                    }
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }

            @Override
            public void onFailure(String postUrl, Exception e, String msg) {
                super.onFailure(postUrl, e, msg);
            }
        });
    }

    /**
     * 小目标：获取小目标结果
     */
    @Override
    synchronized public void getStuAimResult() {
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

    /**
     * 小目标：获取学生段位信息
     */
    @Override
    synchronized public void getStuSegment() {
        getHttpManager().getStuSegment(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getStuSegment:onPmSuccess():json=" + responseEntity.getJsonObject());
                mStuSegmentEntity = getHttpResponseParser().parseStuSegmentInfo(responseEntity);
                if (mStuSegmentEntity != null) {
                    if (mBetterMeEntity.isFirstReceive()) {
                        mBetterMeView.showIntroductionPager();
                    } else {
                        mBetterMeView.showReceiveTargetPager();
                    }
                }
            }
        });
    }

    /**
     * 小目标：实时获取学生目标完成度
     */
    public void getStuAimRealTimeVal() {
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getStuAimRealTimeVal(liveId, courseId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getStuAimRealTimeVal:onPmSuccess():json=" + responseEntity.getJsonObject());
                AimRealTimeValEntity aimRealTimeValEntity = getHttpResponseParser().parseAimRealTimeValInfo
                        (responseEntity);
                if (aimRealTimeValEntity != null) {
                    mBetterMeView.onBetterMeUpdate(aimRealTimeValEntity);
                }
            }
        });
    }

    /**
     * 小目标：获取本场小目标实体
     */
    @Override
    public BetterMeEntity getBetterMeEntity() {
        return mBetterMeEntity;
    }

    /**
     * 小目标：获取段位实体
     */
    @Override
    public StuSegmentEntity getStuSegmentEntity() {
        return mStuSegmentEntity;
    }

}

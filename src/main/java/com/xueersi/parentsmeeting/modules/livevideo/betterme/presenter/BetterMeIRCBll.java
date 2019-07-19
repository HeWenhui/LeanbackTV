package com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.OtherBllEntrance;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMeViewImpl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
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
    private BetterMeContract.BetterMeView mBetterMeView;
    private StuSegmentEntity mStuSegmentEntity;
    private BetterMeEntity mBetterMeEntity;
    private StuAimResultEntity mStuAimResultEntity;
    private AimRealTimeValEntity mAimRealTimeValEntity;
    private boolean isArriveLate = false;
    /**
     * 小目标接口开关
     */
    private boolean isUseBetterMe = false;
    /**
     * 是否展示过本场小目标
     */
    private boolean isShowBetterMe = false;
    private boolean teamPKStatus = false;

    public BetterMeIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mBetterMeView = new BetterMeViewImpl(mContext);
        mBetterMeView.setPresenter(this);
        putInstance(BetterMeContract.BetterMePresenter.class,this);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        logger.d("onModeChange(): oldMode = " + oldMode + " mode = " + mode);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.d("onNotice(): data = " + data);
        switch (type) {
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN: {
                getBetterMe(true);
                break;
            }
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_RESULT: {
                getStuAimResult();
                break;
            }
            case XESCODE.ARTS_STOP_QUESTION:
                onQuestionEnd();
                break;
            case XESCODE.ARTS_H5_COURSEWARE:
                String status = data.optString("status", "off");
                if ("off".equals(status)) {
                    onQuestionEnd();
                }
                break;
            case XESCODE.STOPQUESTION:
                //自传互动题
            case XESCODE.EXAM_STOP:
                onQuestionEnd();
                break;
            case XESCODE.MODECHANGE:{
                if (mAimRealTimeValEntity != null) {
                    OtherBllEntrance.EnglishAchievent.updateBetterMe(mContext, mAimRealTimeValEntity, false);
                } else {
                    updateBetterMe(false);
                }
                //为兼容批调场次，需自己监听切流
                if (!teamPKStatus && LiveTopic.MODE_CLASS.equals(mGetInfo.getMode())) {
                    getBetterMe(false);
                }
            }
            default:
                break;
        }
    }

    private void onQuestionEnd() {
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(mGetInfo.getEnglishBetterMe().aimType) || BetterMeConfig
                .TYPE_PARTICIPATERATE.equals(mGetInfo.getEnglishBetterMe().aimType)) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateBetterMe(true);
                }
            }, 5000);
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        mBetterMeView.setRootView(mRootView);
        this.isUseBetterMe = getInfo.getEnglishBetterMe().isUseBetterMe;
        this.isArriveLate = getInfo.getEnglishBetterMe().isArriveLate;
        logger.d("isUseBetterMe = " + isUseBetterMe + "; isArriveLate = " + isArriveLate);
        //从本地读取本场是否开启了小目标
        isShowBetterMe = mShareDataManager.getString(ShareDataConfig.LIVE_BETTERME_OPEN, "", ShareDataManager
                .SHAREDATA_USER).equals(mGetInfo.getId());
        updateBetterMe(false);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("onTopic(): jsonObject = " + jsonObject);
        logger.d("mode = " + liveTopic.getMode());
        try {
            JSONObject room_2 = jsonObject.getJSONObject("room_2");
            JSONObject teamPKObj = room_2.optJSONObject("teamPK");
            if (teamPKObj != null) {
                teamPKStatus = teamPKObj.optBoolean("status", false);
                if (teamPKStatus) {
                    getBetterMe(false);
                }
            }
            logger.d("teamPK: status = " + teamPKStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取学生这节课小目标
     */
    @Override
    synchronized public void getBetterMe(final boolean isNotice) {
        //小目标接口开关
        if (!isUseBetterMe) {
            return;
        }
        //迟到
        if (isArriveLate) {
            return;
        }
        if (isShowBetterMe) {
            return;
        }
        isShowBetterMe = true;
        storageBetterMe();
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getBetterMe(liveId, courseId, new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getBetterMe:onPmSuccess():json=" + responseEntity.getJsonObject());
                mBetterMeEntity = getHttpResponseParser().parseBetterMeInfo(responseEntity);
                if (mBetterMeEntity != null) {
                    if (mStuSegmentEntity != null) {
                        onBetterMeSuccess(isNotice);
                    } else {
                        getStuSegment(isNotice);
                    }
                } else {
                    OtherBllEntrance.EnglishTeamPK.startPK(mContext, isNotice);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                OtherBllEntrance.EnglishTeamPK.startPK(mContext, isNotice);
            }

            @Override
            public void onFailure(String postUrl, Exception e, String msg) {
                super.onFailure(postUrl, e, msg);
                OtherBllEntrance.EnglishTeamPK.startPK(mContext, isNotice);
            }
        });
    }

    private void onBetterMeSuccess(boolean isNotice) {
        mGetInfo.getEnglishBetterMe().aimType = mBetterMeEntity.getAimType();
        OtherBllEntrance.EnglishAchievent.receiveBetterMe(mContext, mBetterMeEntity, isNotice);
        if (isNotice) {
            if (mBetterMeEntity.isFirstReceive()) {
                mBetterMeView.showIntroductionPager();
            } else {
                mBetterMeView.showReceiveTargetPager();
            }
        }
    }

    //在本地存储本场是否开启了小目标
    private void storageBetterMe() {
        mShareDataManager.put(ShareDataConfig.LIVE_BETTERME_OPEN, mGetInfo.getId(), ShareDataManager
                .SHAREDATA_USER);
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
                } else {
                    OtherBllEntrance.EnglishTeamPK.endPK(mContext);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                OtherBllEntrance.EnglishTeamPK.endPK(mContext);
            }

            @Override
            public void onFailure(String postUrl, Exception e, String msg) {
                super.onFailure(postUrl, e, msg);
                OtherBllEntrance.EnglishTeamPK.endPK(mContext);
            }
        });
    }

    /**
     * 小目标：获取学生段位信息
     */
    @Override
    synchronized public void getStuSegment(final boolean isNotice) {
        getHttpManager().getStuSegment(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getStuSegment:onPmSuccess():json=" + responseEntity.getJsonObject());
                mStuSegmentEntity = getHttpResponseParser().parseStuSegmentInfo(responseEntity);
                if (mStuSegmentEntity != null) {
                    onBetterMeSuccess(isNotice);
                } else {
                    OtherBllEntrance.EnglishTeamPK.startPK(mContext, isNotice);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                OtherBllEntrance.EnglishTeamPK.startPK(mContext, isNotice);
            }

            @Override
            public void onFailure(String postUrl, Exception e, String msg) {
                super.onFailure(postUrl, e, msg);
                OtherBllEntrance.EnglishTeamPK.startPK(mContext, isNotice);
            }
        });
    }

    /**
     * 更新小目标
     */
    @Override
    synchronized public void updateBetterMe(final boolean isShowBubble) {
        logger.d("updateBetterMe");
        //接口开关
        if (!isUseBetterMe) {
            return;
        }
        //迟到
        if (isArriveLate) {
            return;
        }
        if (!isShowBetterMe) {
            return;
        }
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getStuAimRealTimeVal(liveId, courseId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getStuAimRealTimeVal:onPmSuccess():json=" + responseEntity
                        .getJsonObject());
                AimRealTimeValEntity aimRealTimeValEntity = getHttpResponseParser().parseAimRealTimeValInfo
                        (responseEntity);
                if (aimRealTimeValEntity != null) {
                    mAimRealTimeValEntity = aimRealTimeValEntity;
                    mGetInfo.getEnglishBetterMe().aimType = aimRealTimeValEntity.getType();
                    OtherBllEntrance.EnglishAchievent.updateBetterMe(mContext, aimRealTimeValEntity, isShowBubble);
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

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN,
                XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_RESULT,
                XESCODE.ARTS_STOP_QUESTION,
                XESCODE.ARTS_H5_COURSEWARE,
                XESCODE.STOPQUESTION,
                XESCODE.EXAM_STOP,
                XESCODE.MODECHANGE
        };
    }
}

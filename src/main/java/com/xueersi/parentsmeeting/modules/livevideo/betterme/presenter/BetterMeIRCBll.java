package com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeTeamPKContract;
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
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

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
    private boolean isArriveLate = false;
    /**
     * 小目标接口开关
     */
    private boolean isUseBetterMe = false;
    /**
     * 是否展示过本场小目标
     */
    private boolean isShowBetterMe = false;
    private boolean status = false;

    public BetterMeIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mBetterMeView = new BetterMeViewImpl(mContext);
        mBetterMeView.setPresenter(this);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (status) {
            return;
        }
        if (oldMode.equals(LiveTopic.MODE_TRANING) && mode.equals(LiveTopic.MODE_CLASS)) {
            getBetterMe(XESCODE.MODECHANGE);
        } else if (oldMode.equals(LiveTopic.MODE_CLASS) && mode.equals(LiveTopic.MODE_TRANING)) {
            getStuAimResult();
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN: {
                getBetterMe(XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN);
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
        this.isUseBetterMe = getInfo.getEnglishBetterMe().isUseBetterMe;
        this.isArriveLate = getInfo.getEnglishBetterMe().isArriveLate;
        logger.d("isUseBetterMe = " + isUseBetterMe + "; isArriveLate = " + isArriveLate);
        String liveId = mShareDataManager.getString(ShareDataConfig.LIVE_BETTERME_RECEIVED, "", ShareDataManager
                .SHAREDATA_USER);
        if ((liveId).equals(mGetInfo.getId())) {
            isShowBetterMe = true;
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("onTopic():jsonObject=" + jsonObject);
        try {
            JSONObject room_2 = jsonObject.getJSONObject("room_2");
            JSONObject teamPKObj = room_2.optJSONObject("teamPK");
            if (teamPKObj != null) {
                boolean status = teamPKObj.optBoolean("status", false);
                this.status = status;
                if (status) {
                    getBetterMe(0);
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
    synchronized public void getBetterMe(final int type) {
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
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getBetterMe(liveId, courseId, new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getBetterMe:onPmSuccess():json=" + responseEntity.getJsonObject());
                mBetterMeEntity = getHttpResponseParser().parseBetterMeInfo(responseEntity);
                if (mBetterMeEntity != null) {
                    if (mStuSegmentEntity != null) {
                        onBetterMeSuccess(type);
                    } else {
                        getStuSegment(type);
                    }
                } else {
                    onBetterMeFailure(type);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                onBetterMeFailure(type);
            }

            @Override
            public void onFailure(String postUrl, Exception e, String msg) {
                super.onFailure(postUrl, e, msg);
                onBetterMeFailure(type);
            }
        });
    }

    private void onBetterMeSuccess(int type) {
        boolean isNotice = type == XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN;
        boolean isModeChange = type == XESCODE.MODECHANGE;
        if (isNotice || isModeChange) {
            if (mBetterMeEntity.isFirstReceive()) {
                mBetterMeView.showIntroductionPager();
            } else {
                mBetterMeView.showReceiveTargetPager();
            }
        }
        if (ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class) != null) {
            ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class).onReceiveBetterMe
                    (mBetterMeEntity, isNotice || isModeChange);
        }
        mShareDataManager.put(ShareDataConfig.LIVE_BETTERME_RECEIVED, mGetInfo.getId(), ShareDataManager
                .SHAREDATA_USER);
    }

    private void onBetterMeFailure(int type) {
        boolean isNotice = type == XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN;
        if (ProxUtil.getProxUtil().get(mContext, BetterMeTeamPKContract.class) != null) {
            ProxUtil.getProxUtil().get(mContext, BetterMeTeamPKContract.class).onPKStart(isNotice);
        }
    }

    private void onSegmentFailure(int type) {
        boolean isNotice = type == XESCODE.EnTeamPk.XCR_ROOM_TEAMPK_OPEN;
        if (ProxUtil.getProxUtil().get(mContext, BetterMeTeamPKContract.class) != null) {
            ProxUtil.getProxUtil().get(mContext, BetterMeTeamPKContract.class).onPKStart(isNotice);
        }
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
                    onResultFailure();
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                onResultFailure();
            }

            @Override
            public void onFailure(String postUrl, Exception e, String msg) {
                super.onFailure(postUrl, e, msg);
                onResultFailure();
            }
        });
    }

    private void onResultFailure() {
        ProxUtil.getProxUtil().get(mContext, BetterMeTeamPKContract.class).onPKEnd();
    }

    /**
     * 小目标：获取学生段位信息
     */
    @Override
    synchronized public void getStuSegment(final int type) {
        getHttpManager().getStuSegment(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getStuSegment:onPmSuccess():json=" + responseEntity.getJsonObject());
                mStuSegmentEntity = getHttpResponseParser().parseStuSegmentInfo(responseEntity);
                if (mStuSegmentEntity != null) {
                    onBetterMeSuccess(type);
                } else {
                    onSegmentFailure(type);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                onSegmentFailure(type);
            }

            @Override
            public void onFailure(String postUrl, Exception e, String msg) {
                super.onFailure(postUrl, e, msg);
                onSegmentFailure(type);
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

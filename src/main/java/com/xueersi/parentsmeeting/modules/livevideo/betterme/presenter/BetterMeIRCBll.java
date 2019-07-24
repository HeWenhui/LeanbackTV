package com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.BetterExit;
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

    private static int FROM_NOTICE = 1;
    private static int FROM_TOPIC = 2;
    private static int FROM_MODE_CHANGE = 3;
    private static int FROM_LIVE_INITED = 4;

    private LiveGetInfo.BetterMe betterMe;

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
                getBetterMe(FROM_NOTICE);
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
                //如果辅导老师没有发小目标，主讲老师切流
                if (!teamPKStatus && LiveTopic.MODE_CLASS.equals(mGetInfo.getMode())) {
                    getBetterMe(FROM_MODE_CHANGE);
                }
            }
            default:
                break;
        }
    }

    private void onQuestionEnd() {
        String aimType = "";
        if (betterMe.getTarget() != null) {
            aimType = betterMe.getTarget().getAimType();
        } else if (betterMe.getCurrent() != null) {
            aimType = betterMe.getCurrent().getType();
        }
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(aimType) || BetterMeConfig
                .TYPE_PARTICIPATERATE.equals(aimType)) {
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
        //从本地读取本场是否开启了小目标
        mBetterMeView.setRootView(mRootView);
        betterMe = getInfo.getBetterMe();
        this.isUseBetterMe = betterMe.isUseBetterMe();
        this.isArriveLate = betterMe.isArriveLate();
        logger.d("isUseBetterMe = " + isUseBetterMe + "; isArriveLate = " + isArriveLate);
        isShowBetterMe = mShareDataManager.getString(ShareDataConfig.LIVE_BETTERME_OPEN, "", ShareDataManager
                .SHAREDATA_USER).equals(mGetInfo.getId());
        //如果辅导老师没有发小目标，并且进入直播间是主讲态
        if (!teamPKStatus && LiveTopic.MODE_CLASS.equals(mGetInfo.getMode())) {
            getBetterMe(FROM_LIVE_INITED);
        }
        updateBetterMe(false);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("onTopic(): jsonObject = " + jsonObject);
        logger.d("onTopic(): mode = " + liveTopic.getMode());
        try {
            JSONObject room_2 = jsonObject.getJSONObject("room_2");
            JSONObject teamPKObj = room_2.optJSONObject("teamPK");
            if (teamPKObj != null) {
                teamPKStatus = teamPKObj.optBoolean("status", false);
                if (teamPKStatus) {
                    getBetterMe(FROM_TOPIC);
                }
            }
            logger.d("onTopic(): teamPK:status = " + teamPKStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取学生这节课小目标
     */
    @Override
    synchronized public void getBetterMe(final int method) {
        final boolean showPk = method == FROM_NOTICE;
        final boolean showBetterMeDialog = (method == FROM_NOTICE) || (method == FROM_TOPIC && LiveTopic.MODE_TRANING
                .equals(mGetInfo.getMode()));
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
        getHttpManager().getBetterMe(liveId, courseId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getBetterMe:onPmSuccess():json=" + responseEntity.getJsonObject());
                mBetterMeEntity = getHttpResponseParser().parseBetterMeInfo(responseEntity);
                if (mBetterMeEntity != null) {
                    if (mStuSegmentEntity != null) {
                        onBetterMeSuccess(showPk, showBetterMeDialog);
                    } else {
                        getStuSegment(method);
                    }
                } else {
                    BetterExit.EnglishTeamPK.startPK(mContext, showPk);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.i("getBetterMe:onPmFailure():error=" + msg);
                super.onPmFailure(error, msg);
                BetterExit.EnglishTeamPK.startPK(mContext, showPk);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.i("getBetterMe:onPmError():error=" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
                BetterExit.EnglishTeamPK.startPK(mContext,showPk);
        }
        });
    }

    private void onBetterMeSuccess(boolean showPK, final boolean showBetterMeDialog) {
        mGetInfo.getBetterMe().setTarget(mBetterMeEntity);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                BetterExit.EnglishAchievent.receiveBetterMe(mContext, mBetterMeEntity, !showBetterMeDialog);
            }
        }, 1000);
        if (showBetterMeDialog) {
            if (mBetterMeEntity.isFirstReceive()) {
                mBetterMeView.showIntroductionPager(showPK);
            } else {
                mBetterMeView.showReceiveTargetPager(showPK);
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
        //小目标接口开关
        if (!isUseBetterMe) {
            return;
        }
        //迟到
        if (isArriveLate) {
            return;
        }
        String liveId = mLiveBll.getLiveId();
        String courseId = mLiveBll.getCourseId();
        getHttpManager().getStuAimResult(liveId, courseId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getStuAimResult:onPmSuccess():json=" + responseEntity.getJsonObject());
                mStuAimResultEntity = getHttpResponseParser().parseStuAimResultInfo(responseEntity);
                if (mStuAimResultEntity != null) {
                    mBetterMeView.showCompleteTargetPager(mStuAimResultEntity);
                } else {
                    BetterExit.EnglishTeamPK.endPK(mContext);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.i("getStuAimResult:onPmFailure():error=" + msg);
                super.onPmFailure(error, msg);
                BetterExit.EnglishTeamPK.endPK(mContext);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.i("getStuAimResult:onPmError():error=" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
                BetterExit.EnglishTeamPK.endPK(mContext);
            }
        });
    }

    /**
     * 小目标：获取学生段位信息
     */
    @Override
    synchronized public void getStuSegment(int method) {
        final boolean showPk = method == FROM_NOTICE;
        final boolean showBetterMeDialog = method == FROM_NOTICE || method == FROM_TOPIC;
        getHttpManager().getStuSegment(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("getStuSegment:onPmSuccess():json=" + responseEntity.getJsonObject());
                mStuSegmentEntity = getHttpResponseParser().parseStuSegmentInfo(responseEntity);
                if (mStuSegmentEntity != null) {
                    mGetInfo.getBetterMe().setStuSegment(mStuSegmentEntity);
                    onBetterMeSuccess(showPk, showBetterMeDialog);
                } else {
                    BetterExit.EnglishTeamPK.startPK(mContext, showPk);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.i("getStuSegment:onPmFailure():error=" + msg);
                super.onPmFailure(error, msg);
                BetterExit.EnglishTeamPK.startPK(mContext, showPk);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.i("getStuSegment:onPmError():error=" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
                BetterExit.EnglishTeamPK.startPK(mContext, showPk);
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
                    mGetInfo.getBetterMe().setCurrent(mAimRealTimeValEntity);
                    BetterExit.EnglishAchievent.updateBetterMe(mContext, aimRealTimeValEntity, isShowBubble);
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

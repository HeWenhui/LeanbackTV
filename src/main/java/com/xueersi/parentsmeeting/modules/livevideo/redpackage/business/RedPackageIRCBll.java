package com.xueersi.parentsmeeting.modules.livevideo.redpackage.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.RedPackageAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONObject;

/**
 * Created by lyqai on 2018/7/5.
 */
public class RedPackageIRCBll extends LiveBaseBll implements NoticeAction {
    RedPackageAction redPackageAction;

    public RedPackageIRCBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (mGetInfo.getPattern() == 2) {
            RedPackageStandBll redPackageStandBll = new RedPackageStandBll(activity, true, mLiveBll);
            redPackageStandBll.setReceiveGold(new RedPackageAction.ReceiveGoldStand() {
                @Override
                public void getReceiveGoldTeamStatus(int operateId, AbstractBusinessDataCallBack callBack) {
                    RedPackageIRCBll.this.getReceiveGoldTeamStatus(operateId, callBack);
                }

                @Override
                public void getReceiveGoldTeamRank(int operateId, AbstractBusinessDataCallBack callBack) {
                    RedPackageIRCBll.this.getReceiveGoldTeamRank(operateId, callBack);
                }

                @Override
                public void onReceiveGold() {
                    UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
                    if (updateAchievement != null) {
                        updateAchievement.getStuGoldCount();
                    }
                }

                @Override
                public void sendReceiveGold(int operateId, String liveId, AbstractBusinessDataCallBack callBack) {
                    RedPackageIRCBll.this.sendReceiveGold(operateId, liveId, callBack);
                }
            });
            redPackageAction = redPackageStandBll;
        } else {
            RedPackageBll redPackageBll = new RedPackageBll(activity);
            redPackageBll.setVSectionID(mLiveId);
            redPackageBll.initView(mRootView);
            redPackageBll.setReceiveGold(new RedPackageAction.ReceiveGold() {
                @Override
                public void sendReceiveGold(int operateId, String liveId, AbstractBusinessDataCallBack callBack) {
                    RedPackageIRCBll.this.sendReceiveGold(operateId, liveId, callBack);
                }
            });
            redPackageAction = redPackageBll;
        }
    }

    public void sendReceiveGold(final int operateId, String liveId, final AbstractBusinessDataCallBack callBack) {
        final String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("sendReceiveGold:enstuId=" + enstuId + ",operateId=" + operateId + ",liveId=" + liveId);
        getHttpManager().sendReceiveGold(mLiveType, enstuId, operateId, liveId, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("sendReceiveGold:onPmSuccess=" + responseEntity.getJsonObject().toString() + ",operateId=" +
                        operateId);
                VideoResultEntity entity = getHttpResponseParser().redPacketParseParser(responseEntity);
                entity.setHttpUrl(url);
                entity.setHttpRes("" + responseEntity.getJsonObject());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("sendReceiveGold:onPmFailure=" + msg + ",operateId=" + operateId);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("sendReceiveGold:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" + operateId);
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 领取红包-站立直播
     *
     * @param operateId
     * @param callBack
     */
    public void getReceiveGoldTeamStatus(final int operateId, final AbstractBusinessDataCallBack callBack) {
        mLogtf.d("sendReceiveGoldStand:operateId=" + operateId + ",liveId=" + mLiveId);
        getHttpManager().getReceiveGoldTeamStatus(operateId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamStatus:onPmSuccess=" + responseEntity.getJsonObject().toString() + "," +
                        "operateId=" + operateId);
                GoldTeamStatus entity = getHttpResponseParser().redGoldTeamStatus(responseEntity, mGetInfo.getStuId(),
                        mGetInfo.getHeadImgPath());
                entity.setHttpUrl(url);
                entity.setHttpRes("" + responseEntity.getJsonObject());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getReceiveGoldTeamStatus:onPmFailure=" + msg + ",operateId=" + operateId);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamStatus:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" +
                        operateId);
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    public void getReceiveGoldTeamRank(final int operateId, final AbstractBusinessDataCallBack callBack) {
        mLogtf.d("getReceiveGoldTeamRank:operateId=" + operateId + ",liveId=" + mLiveId);
        getHttpManager().getReceiveGoldTeamRank(operateId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamRank:onPmSuccess=" + responseEntity.getJsonObject().toString() + "," +
                        "operateId=" +
                        operateId);
                GoldTeamStatus entity = getHttpResponseParser().redGoldTeamStatus(responseEntity, mGetInfo.getStuId(),
                        mGetInfo.getHeadImgPath());
                entity.setHttpUrl(url);
                entity.setHttpRes("" + responseEntity.getJsonObject());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getReceiveGoldTeamRank:onPmFailure=" + msg + ",operateId=" + operateId);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamRank:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" +
                        operateId);
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    private static final String DEFULT_VOICE_CHAT_STATE = "off";

    private boolean isRedPageAvaliable() {
        boolean result = true;
        try {
            //与 语音接麦 功能互斥
            Object targetParam = mLiveBll.getBusinessShareParam("voiceChatStatus");
            if (targetParam != null) {
                String voiceChatStatus = (String) targetParam;
                result = DEFULT_VOICE_CHAT_STATE.equals(voiceChatStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onNotice(JSONObject data, int type) {
        switch (type) {
            case XESCODE.READPACAGE:
                if (isRedPageAvaliable() && redPackageAction != null) {
                    redPackageAction.onReadPackage(data.optInt("id"), null);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.READPACAGE};
    }
}

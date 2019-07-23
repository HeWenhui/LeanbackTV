package com.xueersi.parentsmeeting.modules.livevideo.redpackage.business;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.ui.dataload.DataLoadEntity;

/**
 * Created by：WangDe on 2018/8/30 10:44
 */
public class RedPackageExperienceBll extends LiveBackBaseBll {
    RedPackageAction redPackageAction;
    String termId;

    public RedPackageExperienceBll(Activity activity, LiveBackBll liveBackBll,String termId) {
        super(activity, liveBackBll);
        this.termId = termId;
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_REDPACKET};
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity,
                             LiveBackBll.ShowQuestion showQuestion) {
        if (redPackageAction == null) {
            RedPackageBll redPackageBll = new RedPackageBll(activity, liveGetInfo, false);
            redPackageBll.setVSectionID(mVideoEntity.getSectionId());
            redPackageBll.initView(mRootView);
            redPackageBll.setReceiveGold(new RedPackageAction.ReceiveGold() {
                @Override
                public void sendReceiveGold(int operateId, String liveId, AbstractBusinessDataCallBack callBack) {
                    RedPackageExperienceBll.this.sendReceiveGold(questionEntity, operateId, liveId, callBack);
                }
            });
            redPackageAction = redPackageBll;

        }
        try {
            mRootView.setVisibility(View.VISIBLE);
            int operateId = Integer.parseInt(questionEntity.getvQuestionID());
            redPackageAction.onReadPackage(operateId, new RedPackageAction.OnReceivePackage() {
                @Override
                public void onReceivePackage(int operateId) {

                }
            });
        } catch (Exception e) {
            logger.e("onReadPackage", e);
        }
    }

    private void sendReceiveGold(VideoQuestionEntity questionEntity, int operateId, String liveId,
                                 AbstractBusinessDataCallBack callBack) {
        questionEntity.setAnswered(true);
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        // 获取红包
        if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {
            BaseBll.postDataLoadEvent(loadEntity.beginLoading());
            getRedPacket(loadEntity, mVideoEntity.getLiveId(), "" + operateId, callBack);
        } else if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
            VideoResultEntity entity = new VideoResultEntity();
            entity.setGoldNum(0);
            callBack.onDataSucess(entity);
        } else {
            BaseBll.postDataLoadEvent(loadEntity.beginLoading());
            getLivePlayRedPacket(loadEntity, mVideoEntity.getLiveId(),termId,"" + operateId, callBack);
        }
        XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil
                        .REDPACKET_GRAB,
                XesMobAgent.XES_VIDEO_INTERACTIVE);
    }

    public void getRedPacket(final DataLoadEntity dataLoadEntity, final String liveId, final String operateId, final
    AbstractBusinessDataCallBack callBack) {
        // 网络加载数据
        getCourseHttpManager().getRedPacket(operateId, liveId,
                new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = getCourseHttpResponseParser()
                                .redPacketParseParser(responseEntity);
                        postDataLoadEvent(dataLoadEntity.webDataSuccess());
                        callBack.onDataSucess(entity);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        callBack.onDataFail(0, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        callBack.onDataFail(1, responseEntity.getErrorMsg());
                    }
                });
    }

    public void getLivePlayRedPacket(final DataLoadEntity dataLoadEntity, final String liveId, final String termId, final String
            operateId, final AbstractBusinessDataCallBack callBack) {
        // 网络加载数据
        getCourseHttpManager().getLivePlayRedPackets(operateId, termId,liveId,
                new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = getCourseHttpResponseParser()
                                .redPacketParseParser(responseEntity);
                        postDataLoadEvent(dataLoadEntity.webDataSuccess());
                        callBack.onDataSucess(entity);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        callBack.onDataFail(0, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        callBack.onDataFail(1, responseEntity.getErrorMsg());
                    }
                });
    }
}

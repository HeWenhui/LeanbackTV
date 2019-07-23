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
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.ui.dataload.DataLoadEntity;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/17.
 */
public class RedPackagePlayBackBll extends LiveBackBaseBll {
    RedPackageAction redPackageAction;
    int pattern = 0;

    public RedPackagePlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        pattern = liveGetInfo.getPattern();
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_REDPACKET};
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {
        if (redPackageAction == null) {
            if (pattern == 2) {
                String showName = "";
                String headUrl = "";
                MyUserInfoEntity mMyInfo = UserBll.getInstance().getMyUserInfoEntity();
                if (!StringUtils.isEmpty(mMyInfo.getEnglishName())) {
                    showName = mMyInfo.getEnglishName();
                } else if (!StringUtils.isEmpty(mMyInfo.getRealName())) {
                    showName = mMyInfo.getRealName();
                } else if (!StringUtils.isEmpty(mMyInfo.getNickName())) {
                    showName = mMyInfo.getNickName();
                }
                headUrl = mMyInfo.getHeadImg();
                RedPackageStandBll redPackageStandBll;
                redPackageStandBll = new RedPackageStandBll(activity, false, liveBackBll);
                redPackageStandBll.setVSectionID(mVideoEntity.getLiveId());
                redPackageStandBll.setUserName(showName);
                redPackageStandBll.setHeadUrl(headUrl);
                redPackageStandBll.initView(mRootView);
                redPackageStandBll.setReceiveGold(new RedPackageAction.ReceiveGoldStand() {
                    @Override
                    public void getReceiveGoldTeamStatus(int operateId, AbstractBusinessDataCallBack callBack) {

                    }

                    @Override
                    public void getReceiveGoldTeamRank(int operateId, AbstractBusinessDataCallBack callBack) {

                    }

                    @Override
                    public void onReceiveGold() {

                    }

                    @Override
                    public void sendReceiveGold(int operateId, String liveId, final AbstractBusinessDataCallBack callBack) {
                        // 网络加载数据
                        getCourseHttpManager().getLivePlayRedPacket( "" + operateId, liveId,
                                new HttpCallBack(false) {

                                    @Override
                                    public void onPmSuccess(ResponseEntity responseEntity) {
                                        VideoResultEntity entity = getCourseHttpResponseParser()
                                                .redPacketParseParser(responseEntity);
                                        callBack.onDataSucess(entity);
//                        EventBus.getDefault().post(new PlaybackVideoEvent.OnGetRedPacket(entity));
                                    }

                                    @Override
                                    public void onPmFailure(Throwable error, String msg) {
                                        XESToastUtils.showToast(mContext, msg);
                                        callBack.onDataFail(0, msg);
                                    }

                                    @Override
                                    public void onPmError(ResponseEntity responseEntity) {
                                        XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                                        callBack.onDataFail(1, responseEntity.getErrorMsg());
                                    }
                                });
                    }
                });
                redPackageAction = redPackageStandBll;
            } else {
                RedPackageBll redPackageBll = new RedPackageBll(activity, liveGetInfo, false);
                redPackageBll.setVSectionID(mVideoEntity.getSectionId());
                redPackageBll.initView(mRootView);
                redPackageBll.setReceiveGold(new RedPackageAction.ReceiveGold() {
                    @Override
                    public void sendReceiveGold(int operateId, String liveId, AbstractBusinessDataCallBack callBack) {
                        RedPackagePlayBackBll.this.sendReceiveGold(questionEntity, operateId, liveId, callBack);
                    }
                });
                redPackageAction = redPackageBll;
            }
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

    private void sendReceiveGold(VideoQuestionEntity questionEntity, int operateId, String liveId, AbstractBusinessDataCallBack callBack) {
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
            getLivePlayRedPacket(loadEntity, mVideoEntity.getLiveId(), "" + operateId, callBack);
        }
        XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil
                        .REDPACKET_GRAB,
                XesMobAgent.XES_VIDEO_INTERACTIVE);
    }

    public void getRedPacket(final DataLoadEntity dataLoadEntity, final String liveId, final String operateId, final AbstractBusinessDataCallBack callBack) {
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

    public void getLivePlayRedPacket(final DataLoadEntity dataLoadEntity, final String liveId, final String operateId, final AbstractBusinessDataCallBack callBack) {
        // 网络加载数据
        getCourseHttpManager().getLivePlayRedPacket(operateId, liveId,
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

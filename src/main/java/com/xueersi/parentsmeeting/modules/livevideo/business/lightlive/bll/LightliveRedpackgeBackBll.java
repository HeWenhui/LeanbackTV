package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageAction;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll
 * @ClassName: LightliveRedpackgeBackBll
 * @Description: 轻直播回放红包
 * @Author: WangDe
 * @CreateDate: 2019/12/27 17:54
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/27 17:54
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightliveRedpackgeBackBll extends LiveBackBaseBll {

    LightLiveRedPackageBll redPackageBll;

    public LightliveRedpackgeBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void initView() {
        super.initView();
        if (redPackageBll != null){
            redPackageBll.initView(mRootView, getLiveViewAction(),mIsLand);
        }
    }


    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_REDPACKET};
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {

        if(redPackageBll == null){
            redPackageBll = new LightLiveRedPackageBll(activity,liveGetInfo,false);
            redPackageBll.setVSectionID(mVideoEntity.getLiveId());
            redPackageBll.initView(mRootView, getLiveViewAction(),mIsLand);
            redPackageBll.setReceiveGold(new RedPackageAction.ReceiveGold() {
                @Override
                public void sendReceiveGold(int operateId, String liveId, AbstractBusinessDataCallBack callBack) {
                    getRedpackage(operateId,liveId,callBack);
                }
            });
        }
        mRootView.setVisibility(View.VISIBLE);
        int operateId = Integer.parseInt(questionEntity.getvQuestionID());
        redPackageBll.onReadPackage(operateId, null);
    }

    private void getRedpackage(int operateId, String liveId, final AbstractBusinessDataCallBack callBack){
        LightLiveHttpManager lightLiveHttpManager = new LightLiveHttpManager(getmHttpManager());
        lightLiveHttpManager.getLiveBackRedpackage(liveId,String.valueOf(operateId), new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                LiveHttpResponseParser  liveHttpResponseParser = new LiveHttpResponseParser(mContext);
                VideoResultEntity entity = liveHttpResponseParser.redPacketParseParser(responseEntity);
                entity.setHttpUrl(url);
                entity.setHttpRes("" + responseEntity.getJsonObject());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveIntegratedCfg;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BigLiveEnterParam;

/**
 * Created by dqq on 2019/7/11.
 */
public class DispatcherHttpManager extends BaseHttpBusiness {

    public DispatcherHttpManager(Context context) {
        super(context);
    }

    public void deductStuGolds(String liveId, String termId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addHeaderParam("Host","laoshi.xueersi.com");
        params.addBodyParam("liveId", liveId);
        // 直播 直播辅导
        params.addBodyParam("termId", termId);
        sendPost(ShareBusinessConfig.URL_EXPERIENCE_LIVE_INFO, params, requestCallBack);
//        sendPost("http://10.97.14.61/science/AutoLive/getAutoLiveInfo", params, requestCallBack);
    }


    public void publicLiveCourseQuestion(String liveId, String teacherId, String timeStr, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("teacherId", teacherId);
        params.addBodyParam("timeStr", timeStr);
        sendPost(DispatcherConfig.URL_PUBLIC_LIVE_COURSE_QUESTION, params, requestCallBack);
    }


    /**
     * 大班整合回放入口接口
     * @param planId
     * @param bizId
     * @param stuCouId
     * @param requestCallBack
     */
    public void publicBigLivePlayBackEnter(int planId, int bizId, int stuCouId,int acceptPlanVersion, HttpCallBack requestCallBack){

        BigLiveEnterParam param = new BigLiveEnterParam();
        param.setBizId(bizId);
        param.setPlanId(planId);
        param.setStuCouId(stuCouId);
        param.setAcceptPlanVersion(acceptPlanVersion);
        sendJsonPost(LiveIntegratedCfg.LIVE_PLAY_BACK_ENTER,param,requestCallBack);
    }

    /**
     * 讲座是否大班场次
     * @param liveId
     * @param requestCallBack
     */
    public void publicLiveIsGrayLecture(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        sendPost(DispatcherConfig.URL_BIGLIVE_BIG_LIVE_BUSINESS_TEST, params, requestCallBack);
    }

    /**
     *  直播 是否是大班直播场次
     * @param planId
     * @param bizId
     * @param callBack
     */
    public void bigLivePlanVersion(int planId,int bizId,HttpCallBack callBack){
        BigLivePlanVersionParam param = new BigLivePlanVersionParam();
        param.setBizId(bizId);
        param.setPlanId(planId);
        sendJsonPost(DispatcherConfig.URL_BIGLIVE_LIVE_GARY,param,callBack);
    }

}

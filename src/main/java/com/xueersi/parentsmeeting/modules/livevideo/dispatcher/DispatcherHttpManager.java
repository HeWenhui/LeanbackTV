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

    /**
     * 扣除金币
     *
     * @param enStuId
     * @param requestCallBack
     */
    public void deductStuGold(String enStuId, String stuCouId, String courseId, String liveId, int isGetPlanInfo,
                              HttpCallBack
                                      requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("liveId", liveId);
        // 直播 直播辅导
        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("isGetPlanInfo", "" + isGetPlanInfo);
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(ShareBusinessConfig.URL_STUDY_GET_LIVE_COURSE_TEST_INFO_FOR_PLAYBACK, params, requestCallBack);
    }

    /**
     * 文科新课件平台回放事件的新接口
     *
     * @param liveId
     * @param requestCallBack
     */
    public void artscoursewarenewpoint(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        sendPost("https://app.arts.xueersi.com/v2/playback/getEvent", params, requestCallBack);
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
     * 大班整合讲座回放入口接口
     * @param planId
     * @param bizId
     * @param stuCouId
     * @param requestCallBack
     */
    public void publicBigLiveCourseQuestion(int planId,int bizId,int stuCouId,HttpCallBack requestCallBack){

        BigLiveEnterParam param = new BigLiveEnterParam();
        param.setBizId(bizId);
        param.setPlanId(planId);
        param.setStuCouId(stuCouId);
        sendJsonPost(LiveIntegratedCfg.LIVE_PLAY_BACK_ENTER,param,requestCallBack);
    }

}

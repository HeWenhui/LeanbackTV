package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http
 * @ClassName: LightLiveHttpManager
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/26 18:29
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/26 18:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveHttpManager {
    LiveHttpManager liveHttpManager;

    public LightLiveHttpManager(LiveHttpManager liveHttpManager){
        this.liveHttpManager = liveHttpManager;
    }

    /**
     * 获取优惠券列表
     * @param courseId
     * @param httpCallBack
     */
    public void getCouponList(String liveId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        liveHttpManager.sendPost(LiveHttpConfig.URL_LIGHTLIVE_COUPON, params, httpCallBack);
    }

    /**
     * 领取优惠券
     * @param couponId
     * @param httpCallBack
     */
    public void getCouponGet(String couponId, HttpCallBack httpCallBack) {
//        String url = "http://10.90.71.143:8080/mockjsdata/44/LiveLecture/stuReceiveCoupon";
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("couponId", couponId);
//        LiveHttpConfig.URL_LIGHTLIVE_GET_COUPON,
        liveHttpManager.sendPost(LiveHttpConfig.URL_LIGHTLIVE_GET_COUPON, params, httpCallBack);
    }

    public void getCourseList(String liveId, HttpCallBack httpCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        liveHttpManager.sendPost(LiveHttpConfig.URL_LIGHTLIVE_GET_COURSE, params, httpCallBack);
    }

    public void getWechatInfo(String lectureId ,HttpCallBack httpCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("lectureId", lectureId);
        liveHttpManager.sendPost(LiveHttpConfig.URL_LIGHTLIVE_GET_WECHAT, params, httpCallBack);
    }

    public void reportLogin(String liveid){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveid);
        liveHttpManager.sendPost(LiveHttpConfig.URL_LIGHTLIVE_REPORT_LOGIN, params, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

            }
        });
    }

    public void getLiveBackMessage(String liveId,String startTime,HttpCallBack httpCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("startTime", startTime);
        liveHttpManager.sendPost(LiveHttpConfig.URL_LIGHTLIVE_GET_MESSAGE,params,httpCallBack);
    }

    public void getLiveBackRedpackage(String liveId,String operateId,HttpCallBack httpCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("operateId", operateId);
        liveHttpManager.sendPost(LiveHttpConfig.URL_LIGHTLIVE_GET_PLAYBACK_REDPACKAGE,params,httpCallBack);
    }




}

package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LightLiveRoomInfoPager;

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
        String url = "";
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
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("couponId", couponId);
        liveHttpManager.sendJsonPost(LiveHttpConfig.URL_LIGHTLIVE_GET_COUPON, paramMap, httpCallBack);
    }


}

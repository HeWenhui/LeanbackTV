package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http
 * @ClassName: LightLiveHttpResponseParser
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/26 18:40
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/26 18:40
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveHttpResponseParser extends HttpResponseParser {

    public List<CouponEntity> parserCouponList(ResponseEntity responseEntity) {
        if (responseEntity == null) return null;

        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        if (jsonObject == null) return null;

        JSONArray couponList = jsonObject.optJSONArray("list");
        if (couponList == null || couponList.length() == 0) {
            return null;
        }

        List<CouponEntity> couponListEntities = new ArrayList<>();
        for (int i = 0; i < couponList.length(); i++) {
            JSONObject listObj = couponList.optJSONObject(i);
            if (listObj == null) continue;
            CouponEntity couponListEntity = new CouponEntity();
            couponListEntity.setFaceText(listObj.optString("faceValue"));
            couponListEntity.setId(listObj.optInt("id"));
            couponListEntity.setName(listObj.optString("name"));
            couponListEntity.setMoneyIcon(listObj.optString("reduceType"));
            couponListEntity.setGetedText(listObj.optString("userNum"));
            couponListEntity.setValidDate(listObj.optString("validDate"));

            couponListEntity.setTitle(listObj.optString("title","满1000减100"));
            couponListEntity.setReduceText(listObj.optString("reduceText","满1000可用"));
            couponListEntity.setStatus(listObj.optInt("status"));

            couponListEntities.add(couponListEntity);
        }
        return couponListEntities;

    }
}

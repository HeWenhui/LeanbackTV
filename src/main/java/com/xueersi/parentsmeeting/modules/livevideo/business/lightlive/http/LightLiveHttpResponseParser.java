package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.LPWeChatEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

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

            couponListEntity.setTitle(listObj.optString("title"));
            couponListEntity.setReduceText(listObj.optString("reduceText"));
            couponListEntity.setStatus(listObj.optInt("status"));

            couponListEntities.add(couponListEntity);
        }
        return couponListEntities;

    }

    public List<CourseEntity> parserCourseList(ResponseEntity responseEntity) {
        if (responseEntity == null) return null;

        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        if (jsonObject == null) return null;

        JSONArray courseList = jsonObject.optJSONArray("list");
        if (courseList == null || courseList.length() == 0) {
            return null;
        }
        List<CourseEntity> entities = new ArrayList<>();
        for (int i = 0; i < courseList.length(); i++) {
            JSONObject listObj = null;
            try {
                listObj = courseList.getJSONObject(i);
                if (listObj == null) continue;
                CourseEntity courseEntity = new CourseEntity();
                courseEntity.setCourseID(listObj.optString("courseId"));
                courseEntity.setSubjectName(listObj.optString("subjectName"));
                courseEntity.setCourseOrignPrice(Integer.parseInt(listObj.optString("price")));
                courseEntity.setCoursePrice(Integer.parseInt(listObj.optString("resale")));
                courseEntity.setCourseDifficulity(Integer.parseInt(listObj.optString("difficultyId", "0")));
                courseEntity.setSecondTitle(listObj.optString("secondTitle"));
                courseEntity.setCourseName(listObj.optString("courseName"));
                courseEntity.setLiveShowTime(listObj.optString("schooltimeName"));
                entities.add(courseEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return entities;
    }

    public LPWeChatEntity parserWeChat(ResponseEntity responseEntity) {
        LPWeChatEntity entity = new LPWeChatEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        entity.setId(jsonObject.optInt("id"));
        entity.setTipType(jsonObject.optInt("tipInfo"));
        entity.setTipInfo(jsonObject.optString("tipInfo"));
        entity.setExistWx(jsonObject.optInt("existWx"));
        entity.setTeacherWx(jsonObject.optString("teaWx"));
        entity.setTeacherName(jsonObject.optString("teaName"));
        entity.setTeacherImg(jsonObject.optString("teaImg"));
        return entity;
    }
}

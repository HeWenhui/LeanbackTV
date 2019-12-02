package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseTeacherEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

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
//        JSONArray couponList = jsonObject;
        if (couponList == null || couponList.length() == 0) {
            return null;
        }
        List<CouponEntity> couponListEntities = new ArrayList<>();
        for (int i = 0; i < couponList.length(); i++) {
            JSONObject listObj = couponList.optJSONObject(i);
            if (listObj == null) continue;
            CouponEntity couponEntity = new CouponEntity();
            couponEntity.setTitle(listObj.optString("reduceTextForDetail"));
            couponEntity.setContent(listObj.optString("content"));

            couponEntity.setId(listObj.optInt("id"));
            couponEntity.setMoneyIcon(listObj.optString("moneyIcon"));
            couponEntity.setFaceText(listObj.optString("faceText"));
            couponEntity.setReduceText(listObj.optString("reduceText"));
            couponEntity.setName(listObj.optString("name"));
            couponEntity.setValidDate(listObj.optString("validDate"));
            couponEntity.setButtonText(listObj.optString("buttonText"));
            couponEntity.setGetedText(listObj.optString("getedText"));
            couponEntity.setStatus(listObj.optInt("status"));

            couponListEntities.add(couponEntity);
        }
        return couponListEntities;

    }

    public List<CourseEntity> parserCourseList(ResponseEntity responseEntity) {
        {
            List<CourseEntity> courseEntities = new ArrayList<>();
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            JSONArray courseList = data.optJSONArray("courseList");
            if (courseList != null && courseList.length() > 0) {
                for (int i = 0; i < courseList.length(); i++) {
                    CourseEntity itemEntity = new CourseEntity();
                    JSONObject jsonObject = courseList.optJSONObject(i);
                    if (jsonObject == null) {
                        continue;
                    }
                    itemEntity.setCourseId(jsonObject.optString("courseId"));
                    itemEntity.setCourseName(jsonObject.optString("courseName"));
//                    itemEntity.setCourseType(jsonObject.optString("courseType"));
                    itemEntity.setSecondTitle(jsonObject.optString("subName"));


                    // 学科
                    JSONArray subjectArray = jsonObject.optJSONArray("subjects");
                    if (subjectArray != null || subjectArray.length() > 0) {
                        List<CourseEntity.SubJect> subjects = new ArrayList<>();
                        for (int j = 0; j < subjectArray.length(); j++) {
                            JSONObject subject = subjectArray.optJSONObject(j);
                            if (jsonObject == null) {
                                continue;
                            }
                            CourseEntity.SubJect attributeEntity = new CourseEntity.SubJect();
                            attributeEntity.setId(jsonObject.optString("id"));
                            attributeEntity.setName(jsonObject.optString("name"));
                            subjects.add(attributeEntity);
                        }
                        itemEntity.setSubJects(subjects);
                    }

                    // 价格
                    JSONObject price = jsonObject.optJSONObject("price");
                    if (price != null) {
                        String orignPrice = price.optString("originPrice","0");
                        if (orignPrice.isEmpty()){
                            orignPrice = "0";
                        }
                        itemEntity.setCourseOrignPrice(Integer.parseInt(orignPrice));
                        String salePrice = price.optString("resale","0");
                        if (salePrice.isEmpty()){
                            salePrice = "0";
                        }
                        itemEntity.setCoursePrice(Integer.parseInt(salePrice));
                    }

                    // 难度
                    JSONObject diffJson = jsonObject.optJSONObject("difficulty");
                    if (diffJson != null) {
                        itemEntity.setCourseDifficulity(diffJson.optInt("alias"));
                    }

                    // 讲数
                    JSONObject syllabusNum = jsonObject.optJSONObject("syllabusNum");
                    if (syllabusNum != null) {
                        itemEntity.setChapterCount(syllabusNum.optString("desc"));
                    }

                    // 销售信息
                    JSONObject saletimes = jsonObject.optJSONObject("saletimes");
                    if (saletimes != null) {
                        itemEntity.setDeadTime(saletimes.optString("reminder"));
                    }

                    itemEntity.setLiveShowTime(jsonObject.optString("schoolTimeName"));

                    // 中教信息
                    ArrayList<CourseTeacherEntity> chineseTeacher = parserTeacherEntity(jsonObject.optJSONArray("chineseTeacher"));
                    itemEntity.setLstMainTeacher(chineseTeacher);

                    // 外教信息
                    ArrayList<CourseTeacherEntity> foreignTeacher = parserTeacherEntity(jsonObject.optJSONArray("foreignTeacher"));
                    itemEntity.setLstForeignTeacher(foreignTeacher);

                    //班级信息
                    JSONObject classObj = jsonObject.optJSONObject("class");
                    if (classObj != null) {
                        itemEntity.setRemainPeople(classObj.optString("leftNum"));
                        itemEntity.setShowCounselorTeacher(classObj.optInt("showCounselorTeacher"));
                        JSONObject counselor = classObj.optJSONObject("counselor");
                        if (counselor != null) {
                            ArrayList<CourseTeacherEntity> courseTeacher = new ArrayList<>();
                            CourseTeacherEntity teacherEntity = getTeacherEntity(counselor);
                            if (teacherEntity != null){
                                courseTeacher.add(teacherEntity);
                                itemEntity.setLstCoachTeacher(courseTeacher);
                            }
                        }
                    }

                    //促销信息，拼团活动等
                    JSONObject promotion = jsonObject.optJSONObject("promotion");
                    if (promotion != null) {
                        itemEntity.setType(promotion.optInt("type"));
                    }
                    itemEntity.setIsFull(jsonObject.optString("isFull"));
                    itemEntity.setExcTeacherCourse(jsonObject.optInt("excTeacherCourse"));

                    courseEntities.add(itemEntity);
                }

                return courseEntities;


            }
            return null;
        }
//        if (responseEntity == null) return null;
//
//        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//        if (jsonObject == null) return null;
//
//        int status =  jsonObject.optInt("status");
//        if (status == 0){
//            return null;
//        }
//
//        JSONArray courseList = jsonObject.optJSONArray("list");
//        if (courseList == null || courseList.length() == 0) {
//            return null;
//        }
//        List<CourseEntity> entities = new ArrayList<>();
//        for (int i = 0; i < courseList.length(); i++) {
//            JSONObject listObj = null;
//            try {
//                listObj = courseList.getJSONObject(i);
//                if (listObj == null) continue;
//                CourseEntity courseEntity = new CourseEntity();
//                courseEntity.setCourseId(listObj.optString("courseId"));
//                courseEntity.setSubjectName(listObj.optString("subjectName"));
//                String orignPrice = listObj.optString("price","0");
//                if (orignPrice.isEmpty()){
//                    orignPrice = "0";
//                }
//                courseEntity.setCourseOrignPrice(Integer.parseInt(orignPrice));
//                courseEntity.setCoursePrice(Integer.parseInt(listObj.optString("resaleSale","0")));
//                courseEntity.setCourseDifficulity(Integer.parseInt(listObj.optString("difficultyId", "0")));
//                courseEntity.setSecondTitle(listObj.optString("secondTitle"));
//                courseEntity.setCourseName(listObj.optString("courseName"));
//                courseEntity.setLiveShowTime(listObj.optString("schooltimeName"));
//                courseEntity.setChapterCount("共" + listObj.optString("planCount") + "讲");
//                courseEntity.setRemainPeople(listObj.optString("leftNum"));
//                courseEntity.setDeadTime(listObj.optString("reminder"));
//                if(listObj.has("teacherInfo")){
//                    JSONArray mainArray = listObj.optJSONArray("teacherInfo");
//                    ArrayList<CourseTeacherEntity> mainTeacherEntities = new ArrayList<>();
//                    for (int j = 0; j < mainArray.length(); j++) {
//                        JSONObject mainObject =  mainArray.optJSONObject(j);
//                        CourseTeacherEntity entity = new CourseTeacherEntity();
//                        entity.setTeacherName(mainObject.optString("teacher_name"));
//                        entity.setTeacherImg(mainObject.optString("avatar"));
//                        entity.setTeacherHint(mainObject.optString("type_name"));
//                        mainTeacherEntities.add(entity);
//                    }
//                    courseEntity.setLstMainTeacher(mainTeacherEntities);
//                }
//                if(listObj.has("counselorInfo")){
//                    JSONArray mainArray = listObj.optJSONArray("counselorInfo");
//                    ArrayList<CourseTeacherEntity> coachTeacherEntities = new ArrayList<>();
//                    for (int j = 0; j < mainArray.length(); j++) {
//                        JSONObject coachObject =  mainArray.optJSONObject(j);
//                        CourseTeacherEntity entity = new CourseTeacherEntity();
//                        entity.setTeacherName(coachObject.optString("teacher_name"));
//                        entity.setTeacherImg(coachObject.optString("avatar"));
//                        entity.setTeacherHint(coachObject.optString("type_name"));
//                        if("专属老师".equals(entity.getTeacherHint()) && "7".equals(coachObject.optString("teacher_type"))){
//                            courseEntity.setExcTeacherCourse(1);
//                        }
//                        coachTeacherEntities.add(entity);
//                    }
//                    courseEntity.setLstCoachTeacher(coachTeacherEntities);
//                }
//                entities.add(courseEntity);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return entities;
    }
    protected ArrayList<CourseTeacherEntity> parserTeacherEntity(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.length() > 0) {
            ArrayList<CourseTeacherEntity> teacherEntities = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject != null) {
                    CourseTeacherEntity teacherEntity = getTeacherEntity(jsonObject);
                    teacherEntities.add(teacherEntity);
                }
            }
            return teacherEntities;
        }
        return null;
    }
    protected CourseTeacherEntity getTeacherEntity(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        CourseTeacherEntity teacherEntity = new CourseTeacherEntity();
        teacherEntity.setTeacherId(jsonObject.optString("id"));
        teacherEntity.setTeacherName(jsonObject.optString("name"));
        teacherEntity.setTeacherHint(jsonObject.optString("typeName"));
        if (teacherEntity.getTeacherId() == null && teacherEntity.getTeacherName() == null && teacherEntity.getTeacherHint() == null){
            return null;
        }
        ArrayList<String> strings = new ArrayList<>();
        JSONArray avatars = jsonObject.optJSONArray("avatars");
        if (avatars != null && avatars.length() > 0) {
            for (int j = 0; j < avatars.length(); j++) {
                strings.add(avatars.optString(j));
            }
        }
        if (!strings.isEmpty()){
            teacherEntity.setTeacherImg(strings.get(0));
        }
        return teacherEntity;
    }
}

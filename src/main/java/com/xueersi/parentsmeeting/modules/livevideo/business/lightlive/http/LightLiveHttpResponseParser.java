package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseTeacherEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.LPWeChatEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveBackMsgEntity;

import org.json.JSONArray;
import org.json.JSONException;
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

    /**
     * 解析优化券
     *
     * @param responseEntity
     * @return
     */
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

    /**
     * 解析推荐课程
     *
     * @param responseEntity
     * @return
     */
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
                            attributeEntity.setId(subject.optString("id"));
                            attributeEntity.setName(subject.optString("name"));
                            subjects.add(attributeEntity);
                        }
                        itemEntity.setSubJects(subjects);
                    }

                    // 价格
                    JSONObject price = jsonObject.optJSONObject("price");
                    if (price != null) {
                        String orignPrice = price.optString("originPrice", "0");
                        if (orignPrice.isEmpty()) {
                            orignPrice = "0";
                        }
                        itemEntity.setCourseOrignPrice(Integer.parseInt(orignPrice));
                        String salePrice = price.optString("resale", "0");
                        if (salePrice.isEmpty()) {
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
                            if (teacherEntity != null) {
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
        if (teacherEntity.getTeacherId() == null && teacherEntity.getTeacherName() == null && teacherEntity.getTeacherHint() == null) {
            return null;
        }
        ArrayList<String> strings = new ArrayList<>();
        JSONArray avatars = jsonObject.optJSONArray("avatars");
        if (avatars != null && avatars.length() > 0) {
            for (int j = 0; j < avatars.length(); j++) {
                strings.add(avatars.optString(j));
            }
        }
        if (!strings.isEmpty()) {
            teacherEntity.setTeacherImg(strings.get(0));
        }
        return teacherEntity;
    }

    public LPWeChatEntity getLPWeChat(ResponseEntity responseEntity) {
        LPWeChatEntity lpEntity = new LPWeChatEntity();

        JSONObject lpInfo = (JSONObject) responseEntity.getJsonObject();
        lpEntity.setTipType(lpInfo.optInt("tipType"));
        lpEntity.setTipInfo(lpInfo.optString("tipInfo"));
        lpEntity.setWxQrUrl(lpInfo.optString("wxQrUrl"));
        lpEntity.setExistWx(lpInfo.optInt("existWx"));
        if (lpInfo.has("wxInfo")) {
            JSONObject teaInfo = lpInfo.optJSONObject("wxInfo");
            lpEntity.setTeacherWx(teaInfo.optString("teaWx"));
            lpEntity.setTeacherName(teaInfo.optString("teaName"));
            lpEntity.setTeacherImg(teaInfo.optString("teaImg"));
        }
        return lpEntity;
    }

    /**
     * 解析回放聊天数据
     * @param responseEntity
     * @return
     */
    public ArrayList<LiveBackMsgEntity> parserBackMessageInfo(ResponseEntity responseEntity) {
        ArrayList<LiveBackMsgEntity> entities = new ArrayList<>();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        try {
            if (jsonObject.has("list")) {
                JSONArray msgList = jsonObject.getJSONArray("list");
                for (int i = 0; i < msgList.length(); i++) {
                    LiveBackMsgEntity entity = new LiveBackMsgEntity();
                    JSONObject temp = msgList.getJSONObject(i);
                    entity.setSender(temp.optString("sender"));
                    entity.setId(temp.optLong("id"));
                    entity.setReceiver(temp.optString("receiver"));
                    entity.setChannel(temp.optInt("channel"));
                    entity.setNotice(temp.optInt("notice"));
                    String sender = entity.getSender();
                    if (sender != null) {
                        sender = sender.substring(0, sender.length() - 2);
                        if (sender.startsWith("t")) {
                            entity.setFrom(LiveBackMsgEntity.MESSAGE_TEACHER);
                        } else if (sender.substring(sender.lastIndexOf("_") + 1).equals(LiveAppUserInfo.getInstance().getStuId())) {
                            entity.setFrom(LiveBackMsgEntity.MESSAGE_MINE);
                        } else {
                            entity.setFrom(LiveBackMsgEntity.MESSAGE_CLASS);
                        }
                    }
                    if (temp.has("text")) {
                        JSONObject text = temp.optJSONObject("text");
                        entity.setText(text.optString("msg"));
                        entity.setType(text.optString("type"));
                        entity.setName(text.optString("name"));
                        entity.setHeadImg(text.optString("path"));
                        entity.setEvenNum(text.optString("evenexc"));
                    }
                    entities.add(entity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entities;
    }
}

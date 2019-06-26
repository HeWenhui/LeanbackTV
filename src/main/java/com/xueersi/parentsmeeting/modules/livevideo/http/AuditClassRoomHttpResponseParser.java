package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.config.AuditRoomConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AuditClassRoomEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.UserScoreEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 旁听课堂网络请求解析类
 * Created by hua on 2016-12-6.
 */

public class AuditClassRoomHttpResponseParser extends HttpResponseParser {


    /**
     * 旁听课堂数据解析
     */
    public AuditClassRoomEntity parserAuditClassRoomUserScore(ResponseEntity responseEntity) {
        AuditClassRoomEntity auditClassRoomEntity = new AuditClassRoomEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            auditClassRoomEntity.setCheckInTime(data.optString("signTime"));
            auditClassRoomEntity.setPreTestCorrectRate(data.optString("examRate"));
            auditClassRoomEntity.setQuestionRateCorrectRate(data.optString("testRate"));
            auditClassRoomEntity.setMineRate(data.optString("myRank"));
            auditClassRoomEntity.setTeamRate(data.optString("myTeamRank"));
            auditClassRoomEntity.setClassRate(data.optString("myClassRank"));
            auditClassRoomEntity.setTitle(data.optString("name"));
            // 我的排名
            auditClassRoomEntity.setMineRateList(parseRateData(data.optJSONArray("teamStuRankList"), AuditRoomConfig.RATE_MY));
            // 我的小组排名
            auditClassRoomEntity.setTeamRateList(parseRateData(data.optJSONArray("teamRankList"), AuditRoomConfig.RATE_TEAM));
            // 我的班级排名
            auditClassRoomEntity.setClassRateList(parseRateData(data.optJSONArray("classRankList"), AuditRoomConfig.RATE_CLASS));
            // 互动提对错情况
            auditClassRoomEntity.setQuestionDetailList(parseRateData(data.optJSONArray("testDetail"), 0));
            // 语音题目得分
            auditClassRoomEntity.setVoiceQuestionDetailList(parseRateData(data.optJSONArray("speechDetail"), 0));

        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parserAuditClassRoomUserScore", e.getMessage());
            return null;

        }
        return auditClassRoomEntity;
    }

    /**
     * 排名数据解析
     *
     * @param mineRateArray
     * @return
     */
    private List<UserScoreEntity> parseRateData(JSONArray mineRateArray, int type) {
        if (mineRateArray == null || mineRateArray.length() == 0) {
            return null;
        }
        List<UserScoreEntity> scoreEntityList = new ArrayList<>();
        UserScoreEntity mineRate = null;
        JSONObject mineRateObj = null;
        for (int i = 0; i < mineRateArray.length(); i++) {
            mineRateObj = mineRateArray.optJSONObject(i);
            mineRate = new UserScoreEntity();
            mineRate.setUserName(mineRateObj.optString("stuName"));
            mineRate.setIndex(i + 1);
            mineRate.setCorrectRate(mineRateObj.optString("rate"));
            mineRate.setQuestionStatus(mineRateObj.optInt("isRight"));
            mineRate.setScore(mineRateObj.optString("score"));
            mineRate.setMyScore(mineRateObj.optBoolean("isMy", false));
            mineRate.setTeamName(mineRateObj.optString("teamName"));
            mineRate.setClassName(mineRateObj.optString("className"));
            mineRate.setQuestionId(i + 1);
            mineRate.setDataType(type);
            scoreEntityList.add(mineRate);
        }
        return scoreEntityList;
    }

    /**
     * 是否有旁听课堂数据解析
     */
    public LiveCourseEntity parserHasAuditClassRoom(ResponseEntity responseEntity) {
        LiveCourseEntity liveCourseEntity = new LiveCourseEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            liveCourseEntity.setPreLiveId(data.optString("preLiveId"));
            JSONObject teacher = data.optJSONObject("teacherInfo");
            if (teacher != null) {
                liveCourseEntity.setTeacherHeadImg(teacher.optString("imgUrl"));
            }
            liveCourseEntity.setStuCouId(data.optString("stu_cou_id"));
            // 当前直播讲数据
            JSONObject liveJson = data.optJSONObject("curLiveInfo");

            if (liveJson != null) {
                liveCourseEntity.setLiveHint(liveJson.optString("name"));
                liveCourseEntity.setLiveId(liveJson.optString("id"));
                if (!TextUtils.isEmpty(liveJson.optString("etime"))) {
                    liveCourseEntity.setLiveEndTime(Long.parseLong(liveJson.optString("etime")) * 1000);
                }
//                liveCourseEntity.setAllowVisitLive(liveJson.optInt("allowVisitLive", 0));
                liveCourseEntity.setHasLiveCourse(AuditRoomConfig.LIVE_COURSE_HAS);
            }
            // 下一直播讲数据
            JSONObject nextLiveJson = data.optJSONObject("nextLiveInfo");
            if (nextLiveJson != null) {
                liveCourseEntity.setNextLiveHint(nextLiveJson.optString("name"));
                liveCourseEntity.setNextLiveId(nextLiveJson.optString("id"));
                if (!TextUtils.isEmpty(nextLiveJson.optString("stime"))) {
                    liveCourseEntity.setNextLiveTime(Long.parseLong(nextLiveJson.optString("stime")) * 1000);
                }
                if (!TextUtils.isEmpty(nextLiveJson.optString("etime"))) {
                    liveCourseEntity.setNextEndLiveTime(Long.parseLong(nextLiveJson.optString("etime")) * 1000);
                }
                if (liveCourseEntity.getNextLiveTime() > 0) {
                    Calendar ca = Calendar.getInstance();
                    long loopTime = liveCourseEntity.getNextLiveTime() - ca.getTimeInMillis();
                    if (loopTime > 0) {
                        liveCourseEntity.setLoopTime(loopTime);
                    }
                }
                liveCourseEntity.setAllowVisitNextLive(nextLiveJson.optInt("allowVisitLive", 0));
            }

        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parserHasAuditClassRoom", e.getMessage());
            return null;

        }
        return liveCourseEntity;
    }

    /**
     * 是否有旁听课堂数据解析
     */
    public LiveCourseEntity parserHasAuditClassRoom(JSONObject data) {
        LiveCourseEntity liveCourseEntity = new LiveCourseEntity();
        try {
            /*liveCourseEntity.setPreLiveId(data.optString("preLiveId"));
            // 当前直播讲数据
            liveCourseEntity.setTeacherHeadImg(data.optString("imgUrl"));
            liveCourseEntity.setLiveHint(data.optString("liveHint"));
            liveCourseEntity.setLiveId(data.optString("liveId"));
            Calendar ca = Calendar.getInstance();
            liveCourseEntity.setLiveEndTime(data.optLong("liveEndTime"));
            if (!TextUtils.isEmpty(data.optString("liveId")) && ca.getTimeInMillis() > liveCourseEntity.getLiveEndTime()) {
                liveCourseEntity.setHasLiveCourse(AuditRoomConfig.LIVE_COURSE_NONE);
                liveCourseEntity.setPreLiveId(data.optString("liveId"));
                return liveCourseEntity;
            } else if (!TextUtils.isEmpty(data.optString("liveId")) && ca.getTimeInMillis() < liveCourseEntity.getLiveEndTime()) {
                liveCourseEntity.setAllowVisitLive(data.optInt("allowVisitLive", 0));
                liveCourseEntity.setHasLiveCourse(AuditRoomConfig.LIVE_COURSE_HAS);
                liveCourseEntity.setLiveId(data.optString("liveId"));
                return liveCourseEntity;
            }

            // 下一直播讲数据
            liveCourseEntity.setNextLiveHint(data.optString("nextLiveHint"));
            liveCourseEntity.setNextLiveId(data.optString("nextLiveId"));
            liveCourseEntity.setNextLiveTime(data.optLong("nextLiveTime"));
            liveCourseEntity.setNextEndLiveTime(data.optLong("nextEndLiveTime"));

            if (!TextUtils.isEmpty(liveCourseEntity.getNextLiveId()) && liveCourseEntity.getNextLiveTime() > 0) {
                long loopTime = liveCourseEntity.getNextLiveTime() - ca.getTimeInMillis();
                if (liveCourseEntity.getNextLiveTime() > ca.getTimeInMillis()) {
                    liveCourseEntity.setLoopTime(loopTime);
                    liveCourseEntity.setHasLiveCourse(AuditRoomConfig.LIVE_COURSE_NONE);
                } else if (liveCourseEntity.getNextLiveTime() < ca.getTimeInMillis() && ca.getTimeInMillis() < liveCourseEntity.getNextEndLiveTime()) {
                    liveCourseEntity.setLoopTime(0);
                    liveCourseEntity.setLiveHint(data.optString("nextLiveHint"));
                    liveCourseEntity.setLiveId(data.optString("nextLiveId"));
                    liveCourseEntity.setHasLiveCourse(AuditRoomConfig.LIVE_COURSE_HAS);
                    liveCourseEntity.setNextLiveId("");
                    liveCourseEntity.setAllowVisitLive(data.optInt("allowVisitNextLive", 0));
                    liveCourseEntity.setLiveEndTime(data.optLong("nextEndLiveTime"));
                } else if (liveCourseEntity.getNextEndLiveTime() < ca.getTimeInMillis()) {
                    liveCourseEntity.setHasLiveCourse(AuditRoomConfig.LIVE_COURSE_NONE);
                    liveCourseEntity.setNextLiveId("");
                    liveCourseEntity.setLiveId("");
                    liveCourseEntity.setPreLiveId(data.optString("nextLiveId"));
                }
            }*/
            liveCourseEntity = JSON.parseObject(data.toString(), LiveCourseEntity.class);
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parserHasAuditClassRoom", e.getMessage());
            return null;

        }
        return liveCourseEntity;
    }

}

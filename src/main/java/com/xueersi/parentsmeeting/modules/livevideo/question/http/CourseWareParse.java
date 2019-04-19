package com.xueersi.parentsmeeting.modules.livevideo.question.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseWareParse {
    public NewCourseSec parseSec(ResponseEntity responseEntity) {
        try {
            NewCourseSec newCourseSec = new NewCourseSec();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            newCourseSec.setIsAnswer(jsonObject.optInt("isAnswer"));
            newCourseSec.setIsGame(jsonObject.optInt("isGame"));
            newCourseSec.setReleaseTime(jsonObject.optLong("releaseTime", System.currentTimeMillis()));
            newCourseSec.setEndTime(jsonObject.optLong("endTime", System.currentTimeMillis()));
            ArrayList<NewCourseSec.Test> tests = newCourseSec.getTests();
            JSONArray array = jsonObject.getJSONArray("testInfos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject testObj = array.getJSONObject(i);
                NewCourseSec.Test test = new NewCourseSec.Test();
                test.setId(testObj.getString("id"));
                test.setPreviewPath(testObj.getString("previewPath"));
                test.setJson(testObj);
                tests.add(test);
            }
            return newCourseSec;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 小学理科互动题 - 解析学生作答情况列表
     *
     * @param responseEntity
     * @return
     */
    public PrimaryScienceAnswerResultEntity parseStuTestResult(ResponseEntity responseEntity) {
        try {
            PrimaryScienceAnswerResultEntity resultEntity = new PrimaryScienceAnswerResultEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            resultEntity.setType(jsonObject.getInt("type"));
            resultEntity.setGold(jsonObject.getInt("gold"));

            List<PrimaryScienceAnswerResultEntity.Answer> answerList = resultEntity.getAnswerList();
            JSONArray array = jsonObject.getJSONArray("answerLists");
            int energy = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject answerObject = array.getJSONObject(i);
                JSONArray myAnswerArray = answerObject.getJSONArray("stuAnswer");
                JSONArray rightAnswerArray = answerObject.getJSONArray("rightAnswer");
                for (int j = 0; j < myAnswerArray.length(); j++) {
                    PrimaryScienceAnswerResultEntity.Answer answer = new PrimaryScienceAnswerResultEntity.Answer();
                    if (j == 0) {
                        answer.setAmswerNumber(i + 1);
                    }
                    JSONObject myAnswerObject = myAnswerArray.getJSONObject(j);
                    answer.setMyAnswer(myAnswerObject.getString("answer"));
                    answer.setRight(myAnswerObject.getInt("right"));
                    answer.setRightAnswer(rightAnswerArray.get(j).toString());
                    answerList.add(answer);
                }
                if (answerObject.has("isRight")) {
                    int isRight = answerObject.getInt("isRight");
                    energy += isRight == 2 ? 10 : 5;
                }
            }
            resultEntity.setEnergy(energy);
            return resultEntity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NewCourseSec parseEn(ResponseEntity responseEntity) {
        try {
            NewCourseSec newCourseSec = new NewCourseSec();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            newCourseSec.setIsAnswer(jsonObject.optInt("isAnswer"));
            newCourseSec.setReleaseTime(jsonObject.optLong("releaseTime", System.currentTimeMillis()));
            ArrayList<NewCourseSec.Test> tests = newCourseSec.getTests();
            JSONArray array = jsonObject.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject testObj = array.getJSONObject(i);
                NewCourseSec.Test test = new NewCourseSec.Test();
                test.setId(testObj.getString("testId"));
                test.setTestType(testObj.getString("testType"));
                test.setPreviewPath(testObj.getString("previewPath"));
                test.setJson(testObj);
                tests.add(test);
            }
            return newCourseSec;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析小组互动题
     *
     * @param responseEntity
     * @return
     */
    public GroupGameTestInfosEntity parseGroupGameTestInfo(ResponseEntity responseEntity) {
        try {
            GroupGameTestInfosEntity groupGameTestInfos = new GroupGameTestInfosEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            groupGameTestInfos.setReleaseTime(jsonObject.optLong("releaseTime", System.currentTimeMillis()));
            groupGameTestInfos.setOperateTimeStamp(jsonObject.optLong("operateTimeStamp", System.currentTimeMillis()));
            groupGameTestInfos.setTimeStamp(jsonObject.optLong("timeStamp", System.currentTimeMillis()));
            groupGameTestInfos.setAnswered(jsonObject.optBoolean("isAnswered"));
            List<GroupGameTestInfosEntity.TestInfoEntity> testInfolist = new ArrayList<>();
            JSONArray array = jsonObject.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject testObj = array.getJSONObject(i);
                GroupGameTestInfosEntity.TestInfoEntity testinfo = new GroupGameTestInfosEntity.TestInfoEntity();
                testinfo.setTestId(testObj.getString("testId"));
                testinfo.setTestType(testObj.getInt("testType"));
                testinfo.setPreviewPath(testObj.getString("previewPath"));
                testinfo.setSingleCount(testObj.getInt("singleCount"));
                testinfo.setTotalTime(testObj.getInt("totalTime"));
                testinfo.setStemLength(testObj.getInt("stemLength"));
                testinfo.setGameModel(testObj.optInt("gameModel", LiveQueConfig.GAME_MODEL_1));
                List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = new ArrayList<>();
                JSONArray answers = testObj.getJSONArray("answers");
                for (int j = 0; j < answers.length(); j++) {
                    JSONObject answerObj = answers.getJSONObject(j);
                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = new GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity();
                    answer.setId(answerObj.getInt("id"));
                    answer.setText(answerObj.getString("text"));
                    answer.setSingleTime(answerObj.getInt("singleTime"));
                    answerList.add(answer);
                }
                testinfo.setAnswerList(answerList);
                testInfolist.add(testinfo);
            }
            groupGameTestInfos.setTestInfoList(testInfolist);
            return groupGameTestInfos;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GroupGameTestInfosEntity parseCleanUpTestInfo(ResponseEntity responseEntity) {
        try {
            GroupGameTestInfosEntity groupGameTestInfos = new GroupGameTestInfosEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            groupGameTestInfos.setReleaseTime(jsonObject.optLong("releaseTime", System.currentTimeMillis()));
            groupGameTestInfos.setOperateTimeStamp(jsonObject.optLong("operateTimeStamp", System.currentTimeMillis()));
            groupGameTestInfos.setTimeStamp(jsonObject.optLong("timeStamp", System.currentTimeMillis()));
            groupGameTestInfos.setAnswered(jsonObject.optBoolean("isAnswered"));
            List<GroupGameTestInfosEntity.TestInfoEntity> testInfolist = new ArrayList<>();
            JSONArray array = jsonObject.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject testObj = array.getJSONObject(i);
                GroupGameTestInfosEntity.TestInfoEntity testinfo = new GroupGameTestInfosEntity.TestInfoEntity();
                testinfo.setTestId(testObj.getString("testId"));
                testinfo.setTestType(testObj.getInt("testType"));
                testinfo.setPreviewPath(testObj.getString("previewPath"));
                testinfo.setAnswerLimitTime(testObj.getInt("answerLimitTime"));
//                testinfo.setSingleCount(testObj.getInt("singleCount"));
                testinfo.setTotalTime(testObj.getInt("totalTime"));
                testinfo.setStemLength(testObj.getInt("stemLength"));
                testinfo.setGameModel(testObj.optInt("gameModel", LiveQueConfig.GAME_MODEL_1));
                List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = new ArrayList<>();
                JSONArray answers = testObj.getJSONArray("answers");
                for (int j = 0; j < answers.length(); j++) {
                    JSONObject answerObj = answers.getJSONObject(j);
                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = new GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity();
                    answer.setId(answerObj.getInt("id"));
                    answer.setText(answerObj.getString("text"));
                    answerList.add(answer);
                }
                testinfo.setAnswerList(answerList);
                testInfolist.add(testinfo);
            }
            groupGameTestInfos.setTestInfoList(testInfolist);
            return groupGameTestInfos;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

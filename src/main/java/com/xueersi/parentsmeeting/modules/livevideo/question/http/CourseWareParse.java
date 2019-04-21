package com.xueersi.parentsmeeting.modules.livevideo.question.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultEntity;
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

    public BigResultEntity parseBigResult(ResponseEntity responseEntity) {
        try {
            BigResultEntity bigResultEntity = new BigResultEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            bigResultEntity.setIsRight(jsonObject.getInt("isRight"));
            bigResultEntity.setGold(jsonObject.getInt("gold"));
            return bigResultEntity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

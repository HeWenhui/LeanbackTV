package com.xueersi.parentsmeeting.modules.livevideo.question.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultItemEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseWareParse {
    String TAG = "CourseWareParse";
    Logger logger = LiveLoggerFactory.getLogger(TAG);

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
            logger.e("parseSec", e);
            MobAgent.httpResponseParserError(TAG, "parseSec", e.getMessage());
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
            logger.e("parseStuTestResult", e);
            MobAgent.httpResponseParserError(TAG, "parseStuTestResult", e.getMessage());
        }
        return null;
    }

    public ChineseAISubjectResultEntity paresChiAIStuTestResult(ResponseEntity responseEntity) {
        ChineseAISubjectResultEntity resultEntity = new ChineseAISubjectResultEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            resultEntity.setGold(jsonObject.optInt("gold"));
            resultEntity.setEnergy(jsonObject.optInt("energy"));
            resultEntity.setTotalScore(jsonObject.optDouble("totalScore"));
            resultEntity.setTestType(jsonObject.optString("testType"));
            resultEntity.setIsRight(jsonObject.optInt("isRight"));
            resultEntity.setIsAnswered(jsonObject.optInt("isAnswered"));
            JSONObject answerLists = jsonObject.getJSONObject("answerLists");

            JSONArray stuAnswerArray = answerLists.getJSONArray("stuAnswer");
            List<ChineseAISubjectResultEntity.StuAnswer> stuAnswers = new ArrayList<>();
            for (int i = 0; i < stuAnswerArray.length(); i++) {
                JSONObject answerJson = stuAnswerArray.getJSONObject(i);
                ChineseAISubjectResultEntity.StuAnswer stuAnswer = new ChineseAISubjectResultEntity.StuAnswer();
                stuAnswer.setAnswer(answerJson.optString("answer"));
                stuAnswer.setScoreKey(answerJson.optString("soreKey"));
                stuAnswer.setScore(answerJson.optString("score"));
                stuAnswer.setId(answerJson.optString("id"));
                stuAnswers.add(stuAnswer);
            }
            resultEntity.setStuAnswers(stuAnswers);

            JSONArray rightAnswerArray = answerLists.getJSONArray("rightAnswer");
            List<String> rightAnswers = new ArrayList<>();
            for (int i = 0; i < rightAnswerArray.length(); i++) {
                rightAnswers.add(rightAnswerArray.getString(i));
            }
            resultEntity.setRightAnswers(rightAnswers);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultEntity;
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
            logger.e("parseEn", e);
            MobAgent.httpResponseParserError(TAG, "parseEn", e.getMessage());
        }
        return null;
    }


    /**
     * 解析小组互动题
     *
     * @param responseEntity
     * @return
     */
    public GroupGameTestInfosEntity parseGroupGameTestInfo(ResponseEntity responseEntity, String type) {
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
                if (LiveQueConfig.EN_COURSE_TYPE_SOLITAIRE.equals(type)) {
                    testinfo.setGameOrder(testObj.optString("gameOrder"));
                }
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
            MobAgent.httpResponseParserError(TAG, "parseGroupGameTestInfo", e.getMessage());
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
            MobAgent.httpResponseParserError(TAG, "parseCleanUpTestInfo", e.getMessage());
        }
        return null;
    }

    public BigResultEntity parseBigResult(ResponseEntity responseEntity) {
        try {
            BigResultEntity bigResultEntity = new BigResultEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            bigResultEntity.setIsRight(jsonObject.getInt("is_right"));
            bigResultEntity.setGold(jsonObject.getInt("gold"));
            ArrayList<BigResultItemEntity> bigResultItemEntityArrayList = bigResultEntity.getBigResultItemEntityArrayList();
            JSONArray each_question = jsonObject.getJSONArray("each_question");
            for (int i = 0; i < each_question.length(); i++) {
                JSONObject question = each_question.getJSONObject(i);
                BigResultItemEntity bigResultItemEntity = new BigResultItemEntity();
                bigResultItemEntity.standAnswer = question.optString("rightAnswer");
                bigResultItemEntity.youAnswer = question.optString("userAnswer");
                if (question.has("is_right")) {
                    bigResultItemEntity.rightType = question.getInt("is_right");
                } else {
                    bigResultItemEntity.rightType = question.getInt("isRight");
                }
                bigResultItemEntityArrayList.add(bigResultItemEntity);
            }
            return bigResultEntity;
        } catch (JSONException e) {
            logger.e("parseBigResult", e);
            MobAgent.httpResponseParserError(TAG, "parseBigResult", e.getMessage());
        }
        return null;
    }
}

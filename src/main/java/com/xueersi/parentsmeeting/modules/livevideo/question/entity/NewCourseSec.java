package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewCourseSec {
    private int isAnswer;
    private int isGame;
    private long releaseTime;//1552621945
    private long endTime;//1552622125
    private ArrayList<Test> tests = new ArrayList<>();

    public ArrayList<Test> getTests() {
        return tests;
    }

    public int getIsAnswer() {
        return isAnswer;
    }

    public void setIsAnswer(int isAnswer) {
        this.isAnswer = isAnswer;
    }

    public int getIsGame() {
        return isGame;
    }

    public void setIsGame(int isGame) {
        this.isGame = isGame;
    }

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public static class Test {
        String id;
        String testType;
        String previewPath;
        String hasAnswer;
        JSONObject json;
        JSONArray userAnswerContent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTestType() {
            return testType;
        }

        public void setTestType(String testType) {
            this.testType = testType;
        }

        public String getPreviewPath() {
            return previewPath;
        }

        public void setPreviewPath(String previewPath) {
            this.previewPath = previewPath;
        }

        public String getHasAnswer() {
            return hasAnswer;
        }

        public void setHasAnswer(String hasAnswer) {
            this.hasAnswer = hasAnswer;
        }

        public JSONObject getJson() {
            return json;
        }

        public void setJson(JSONObject json) {
            this.json = json;
        }

        public JSONArray getUserAnswerContent() {
            return userAnswerContent;
        }

        public void setUserAnswerContent(JSONArray userAnswerContent) {
            this.userAnswerContent = userAnswerContent;
        }
    }
}
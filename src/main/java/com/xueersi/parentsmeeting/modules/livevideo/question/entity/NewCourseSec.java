package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewCourseSec {
    private int isAnswer;
    private long releaseTime;
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

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public static class Test {
        String id;
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

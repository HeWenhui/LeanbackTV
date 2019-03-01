package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import java.util.ArrayList;

public class NewCourseSec {
    ArrayList<Test> tests = new ArrayList<>();

    public ArrayList<Test> getTests() {
        return tests;
    }

    public static class Test {
        String id;
        String previewPath;
        String hasAnswer;

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
    }
}

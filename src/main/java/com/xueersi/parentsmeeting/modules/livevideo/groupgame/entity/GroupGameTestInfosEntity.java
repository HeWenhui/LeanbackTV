package com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity;

import java.util.List;

/**
 * @Date on 2019/3/13 15:59
 * @Author zhangyuansun
 * @Description
 */
public class GroupGameTestInfosEntity {

    /**
     * releaseTime : 3
     * timeStamp : 1552359683
     * list : [{"testType":2,"previewPath":"","answerLimitTime":"200","singleTime":60,"singleCount":5,"totalTime":1500,"answers":[{"id":0,"text":"orange"},{"id":1,"text":"strawberry"},{"id":2,"text":"apple"},{"id":3,"text":"banana"},{"id":4,"text":"pear"}]},{"testId":"59964_73943","testType":7,"previewPath":"","answerLimitTime":"200","singleTime":60,"singleCount":5,"totalTime":1500,"answers":[{"id":0,"text":"orange"},{"id":1,"text":"strawberry"},{"id":2,"text":"apple"},{"id":3,"text":"banana"},{"id":4,"text":"pear"}]}]
     */

    /**
     * 教师端设置时间
     */
    private long releaseTime;
    /**
     * 拉题时间戳
     */
    private long timeStamp;
    private List<TestInfoEntity> testInfoList;

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<TestInfoEntity> getTestInfoList() {
        return testInfoList;
    }

    public void setTestInfoList(List<TestInfoEntity> testInfoList) {
        this.testInfoList = testInfoList;
    }

    public static class TestInfoEntity {
        /**
         * testType : 2
         * previewPath :
         * answerLimitTime : 200
         * singleTime : 60
         * singleCount : 5
         * totalTime : 1500
         * answers : [{"id":0,"text":"orange"},{"id":1,"text":"strawberry"},{"id":2,"text":"apple"},{"id":3,"text":"banana"},{"id":4,"text":"pear"}]
         * testId : 59964_73943
         */

        /**
         * 1-在线教研填空，2-在线教研选择，8-在线教研语文主观题，4-在线教研语音测评，5-在线教研roleplay，6-在线教研语文跟读，7-本地上传普通，9-本地上传课前测，10-本地上传课中测，11-本地上传出门考，12-本地上传游戏，13-本地上传互动题，14-本地上传语音测评
         */
        private int testType;
        /**
         * 试题页面
         */
        private String previewPath;
        /**
         * 答题时间限制
         */
        private int answerLimitTime;
        /**
         * 单题次数
         */
        private int singleCount;
        /**
         * 作答总时间
         */
        private int totalTime;
        /**
         * 总题数
         */
        private int stemLength;
        /**
         * 试题id
         */
        private String testId;
        /**
         * 正确答案数组
         */
        private List<AnswersEntity> answerList;

        public int getTestType() {
            return testType;
        }

        public void setTestType(int testType) {
            this.testType = testType;
        }

        public String getPreviewPath() {
            return previewPath;
        }

        public void setPreviewPath(String previewPath) {
            this.previewPath = previewPath;
        }

        public int getAnswerLimitTime() {
            return answerLimitTime;
        }

        public void setAnswerLimitTime(int answerLimitTime) {
            this.answerLimitTime = answerLimitTime;
        }

        public int getSingleCount() {
            return singleCount;
        }

        public void setSingleCount(int singleCount) {
            this.singleCount = singleCount;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(int totalTime) {
            this.totalTime = totalTime;
        }

        public int getStemLength() {
            return stemLength;
        }

        public void setStemLength(int stemLength) {
            this.stemLength = stemLength;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

        public List<AnswersEntity> getAnswerList() {
            return answerList;
        }

        public void setAnswerList(List<AnswersEntity> answerList) {
            this.answerList = answerList;
        }

        public static class AnswersEntity {
            /**
             * id : 0
             * text : orange
             */

            /**
             * 答案id
             */
            private int id;
            /**
             * 正确答案文本
             */
            private String text;
            /**
             * 单题作答时长
             */
            private int singleTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public int getSingleTime() {
                return singleTime;
            }

            public void setSingleTime(int singleTime) {
                this.singleTime = singleTime;
            }
        }
    }
}

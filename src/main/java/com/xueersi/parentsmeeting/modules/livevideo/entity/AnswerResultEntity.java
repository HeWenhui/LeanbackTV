package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;

import java.util.ArrayList;
import java.util.List;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;

/**
 * 文科 答题结果
 *
 * @author chenkun
 * @version 1.0, 2018/8/16 上午10:20
 */

public class AnswerResultEntity {

    /** 文科新课件平台 老课件 答题结果 */
    public static final int RESULT_TYPE_OLD_COURSE_WARE = 1;
    /** 文科课件平台 新课件 */
    public static final int RESULT_TYPE_NEW_COURSE_WARE = 2;
    //英语1v2语音评测结果页
    public static final int RESULT_TYPE_YINYU_1V2_VOICE_TEST= 3;
    /** 英语1v2 小组互动 多人模式 */
    public static final int RESULT_TYPE_GROUP_CLASS_GAME_MULTI = 4;
    /** 英语1v2 小组互动 单人模式 */
    public static final int RESULT_TYPE_GROUP_CLASS_GAME_SINGLE = 5;

    public int isVoice = 0;
    private String liveId;
    private String stuId;
    /** 后端生成的虚拟id */
    private String virtualId;
    /** 试题个数 */
    private int testCount;
    /** 答题状态 0 :全错 1: 对一半  2:全对 */
    private int isRight;
    /** 获得的金币数 */
    private int gold;
    /** 获得的能量数 */
    private int energy;
    /** 正确率 */
    private double rightRate;
    private long createTime;
    /** 答题结果对应的试题类型 */
    private int type;

    //得分
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getIdArray() {
        return idArray;
    }

    public void setIdArray(List<String> idArray) {
        this.idArray = idArray;
    }

    /** 档次答题所有试题id集合 */
    private List<String> idArray;

    private int resultType;

    private List<Answer> answerList;

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getVirtualId() {
        return virtualId;
    }

    public void setVirtualId(String virtualId) {
        this.virtualId = virtualId;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getIsRight() {
        return isRight;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public double getRightRate() {
        return rightRate;
    }

    public void setRightRate(double rightRate) {
        this.rightRate = rightRate;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    /** 试题类型  1 填空题 */
    public static int TEST_TYPE_1 = 1;
    /** 试题类型  2选择题 */
    public static int TEST_TYPE_2 = 2;

    public static class Answer {
        private String liveId;
        private String stuId;
        private String testId;
        private String testSrc;
        /** 试题类型  1 填空题   2选择题 */
        private int testType;
        /** 用户自己 选择题答案 */
        private List<String> choiceList;
        /** 用户自己 填空题答案 */
        private List<String> blankList;
        /** 标准答案 */
        private List<String> rightAnswers;

        /** 这道小题是否正确 0:全错 1:对一半 2:全对 */
        private int isRight;
        /** 这道小题的正确率 */
        private double rightRate;
        private long createTime;

        public String getLiveId() {
            return liveId;
        }

        public void setLiveId(String liveId) {
            this.liveId = liveId;
        }

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

        public String getTestSrc() {
            return testSrc;
        }

        public void setTestSrc(String testSrc) {
            this.testSrc = testSrc;
        }

        public int getTestType() {
            return testType;
        }

        public void setTestType(int testType) {
            this.testType = testType;
        }

        public List<String> getChoiceList() {
            return choiceList;
        }

        public void setChoiceList(List<String> choiceList) {
            this.choiceList = choiceList;
        }

        public List<String> getBlankList() {
            return blankList;
        }

        public void setBlankList(List<String> blankList) {
            this.blankList = blankList;
        }

        public List<String> getRightAnswers() {
            return rightAnswers;
        }

        public void setRightAnswers(List<String> rightAnswers) {
            this.rightAnswers = rightAnswers;
        }

        public int getIsRight() {
            return isRight;
        }

        public void setIsRight(int isRight) {
            this.isRight = isRight;
        }

        public double getRightRate() {
            return rightRate;
        }

        public void setRightRate(double rightRate) {
            this.rightRate = rightRate;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

    }

    public static AnswerResultEntity getAnswerResultEntity(VideoQuestionLiveEntity videoQuestionLiveEntity, VideoResultEntity entity) {
        AnswerResultEntity answerResultEntity = new AnswerResultEntity();
        answerResultEntity.isVoice = 1;
        ArrayList<AnswerResultEntity.Answer> answerList = new ArrayList<>();
        answerResultEntity.setAnswerList(answerList);
        answerResultEntity.setIsRight(entity.getResultType());
        answerResultEntity.setGold(entity.getGoldNum());
        answerResultEntity.setEnergy(entity.getEnergy());
        {
            AnswerResultEntity.Answer answer = new AnswerResultEntity.Answer();
            List<String> rightAnswers = new ArrayList<>();
            rightAnswers.add(entity.getStandardAnswer());
            answer.setRightAnswers(rightAnswers);
            answer.setIsRight(entity.getResultType());
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                answer.setTestType(AnswerResultEntity.TEST_TYPE_2);
                List<String> choiceList = new ArrayList<>();
                choiceList.add(entity.getYourAnswer());
                answer.setChoiceList(choiceList);
            } else {
                List<String> choiceList = new ArrayList<>();
                choiceList.add(entity.getYourAnswer());
                answer.setChoiceList(choiceList);
                answer.setTestType(AnswerResultEntity.TEST_TYPE_1);
                answer.setBlankList(choiceList);
            }
            answerList.add(answer);
        }
        return answerResultEntity;
    }

}

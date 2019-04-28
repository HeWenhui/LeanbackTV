package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 互动题
 *
 * @author linyuqiang
 */
public class VideoQuestionLiveEntity extends BaseVideoQuestionEntity {
    /**
     *
     */
    private static final long serialVersionUID = -3457837665761010917L;
    private static String TAG = "VideoQuestionLiveEntity";
    public double time;
    public String id;
    /** 聊天消息notice类型 */
    public int noticeType;
    public double gold;
    public int num;
    /** 互动题类型 */
    public String type;
    /** 英语互动题类型,新课件,避免和type冲突 */
    private String artType;
    /** 当type=1时为选择题，choiceType 1：单选；2：多选，num为选择题数量 */
    public String choiceType;
    /** 题目来源 */
    public String srcType = "";
    /** 答案列表 */
    private final List<AnswerEntity> mAnswerEntityLst = new ArrayList<>();
    /** 互动题是不是用h5 */
    public boolean isTestUseH5;
    /** 语音评测二期-是不是用 */
    public String isAllow42;
    /** 语音评测二期-评测内容 */
    public String speechContent;
    /** h5课件用-课件地址 */
    public EnglishH5Entity englishH5Entity = new EnglishH5Entity();
    /** h5课件用-课件类型 */
    public String courseware_type;
    /** 是不是语音答题 */
    private String isVoice;
    /** 语音答题,1.选择2.填空 */
    public String questiontype;
    /** 语音答题,评测内容 */
    public String assess_ref;
    /** 多人连麦 Notice */
    public String multiRolePlay;
    /** 不为空是role play */
    public String roles = "";
    public int examSubmit;
    /** 插入视频的时间点，秒为单位 */
    private int vQuestionInsretTime;
    /** 结束时间 */
    private int vEndTime;
    /** 测试题日期 */
    private String answerDay;
    /** 体验课订单ID */
    private String termId;
    private boolean isLive = true;
    /** H5语音答题的题型*/
    public String voiceType;
    /**
     * 1.在线教研
     * 2 设计组
     */
    public int package_socurce;

    /**文科在线教研数据**/
    private H5OnlineTechEntity onlineTechEntity;

    /**语文主观题独有。总分数**/
    public String totalScore;

    /**语音评测独有。答案**/
    public String answer;
    /** 年级阶段的标识 */
    private String educationstage = "";
    private String newCourseTestIdSec = null;

    public VideoQuestionLiveEntity() {
    }

    public String getIsVoice() {
        return isVoice;
    }

    public void setIsVoice(String isVoice) {
        //isVoice = "0";
        this.isVoice = isVoice;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    /** 辅导态 */
    public boolean isTUtor = false;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id=" + id);
        builder.append(",time=" + time);
        builder.append(",gold=" + gold);
        builder.append(",num=" + num);
        builder.append(",type=" + type);
        builder.append(",choiceType=" + choiceType);
        if ("1".equals(isAllow42)) {
            builder.append(",speechContent=" + speechContent);
        }
        if ("1".equals(getIsVoice())) {
            builder.append(",questiontype=" + questiontype);
        }
        return builder.toString();
    }

    @Override
    public List<AnswerEntity> getAnswerEntityLst() {
        if (mAnswerEntityLst.isEmpty()) {
            editShowQuestion();
        }
        return mAnswerEntityLst;
    }

    @Override
    public String getvQuestionID() {
        return id;
    }


    /**
     * 是否是 文科新课件平台 答题
     * */

    public void setNewArtsCourseware(boolean newH5Course) {
        englishH5Entity.setArtsNewH5Courseware(newH5Course);
    }

    public boolean isNewArtsH5Courseware() {
        return englishH5Entity.isArtsNewH5Courseware();
    }

    /**
     * 设置选择题显示数据
     */
    private void editShowQuestion() {
        int vBlankSize = 1;
        vBlankSize = num;
        for (int i = 0; i < vBlankSize; i++) {
            AnswerEntity answerLiveEntity = new AnswerEntity();
            mAnswerEntityLst.add(answerLiveEntity);
        }
    }

    public void addAnswerEntity(AnswerEntity answerEntity) {
        if (answerEntity != null) {
            mAnswerEntityLst.add(answerEntity);
        }
    }

    @Override
    public int getvBlankSize() {
        return num;
    }

    public String getArtType() {
        return artType;
    }

    public void setArtType(String artType) {
        this.artType = artType;
    }

    public String getUrl() {
        return englishH5Entity.getUrl();
    }

    public void setUrl(String url) {
        englishH5Entity.setUrl(url);
    }

    public int getvQuestionInsretTime() {
        return vQuestionInsretTime;
    }

    public void setvQuestionInsretTime(int vQuestionInsretTime) {
        this.vQuestionInsretTime = vQuestionInsretTime;
    }

    public int getvEndTime() {
        return vEndTime;
    }

    public void setvEndTime(int vEndTime) {
        this.vEndTime = vEndTime;
    }

    public String getAnswerDay() {
        return answerDay;
    }

    public void setAnswerDay(String answerDay) {
        this.answerDay = answerDay;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public void setOnlineTechEntity(H5OnlineTechEntity onlineTechEntity) {
        this.onlineTechEntity = onlineTechEntity;
    }

    public H5OnlineTechEntity getOnlineTechEntity() {
        return onlineTechEntity;
    }

    public void setLiveType(int liveType){
        englishH5Entity.setLiveType(liveType);
    }
    public int getLiveType(){
        return  englishH5Entity.getLiveType();
    }


    public String getEducationstage() {
        return educationstage;
    }

    public void setEducationstage(String educationstage) {
        this.educationstage = educationstage;
    }

    public String getNewCourseTestIdSec() {
        return newCourseTestIdSec;
    }

    public void setNewCourseTestIdSec(String newCourseTestIdSec) {
        this.newCourseTestIdSec = newCourseTestIdSec;
    }

    public boolean isTUtor() {
        return isTUtor;
    }

    public void setTUtor(boolean TUtor) {
        isTUtor = TUtor;
    }
}

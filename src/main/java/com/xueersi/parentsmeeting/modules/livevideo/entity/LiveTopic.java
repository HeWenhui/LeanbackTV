package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.xesalib.utils.log.Loger;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linyuqiang on 2016/1/11. 直播课获取播放器缓存接口
 */
public class LiveTopic {
    private String TAG = "LiveTopicLog";

    /**
     * disable_speaking : [] id : 1360 mode : in-class status :
     * {"classbegin":false,"openbarrage":true,"openchat":true} topic :
     * {"answer":
     * "","content":"","gold_count":0,"id":"invalid","num":0,"publish_time"
     * :1452407139656,"status":1,"time":3,"type":""}
     */
    public LiveTopic() {
        Loger.i(TAG, "LiveTopic");
    }

    /**
     * 主讲老师
     */
    public static final String MODE_CLASS = "in-class";
    /**
     * 辅导老师
     */
    public static final String MODE_TRANING = "in-training";
    /**
     * classbegin : false openbarrage : true openchat : true 当前直播主讲状态
     */
    private final RoomStatusEntity mainRoomstatus = new RoomStatusEntity();
    /**
     * classbegin : false openbarrage : true openchat : true 当前直播辅导状态
     */
    private final RoomStatusEntity coachRoomstatus = new RoomStatusEntity();
    /**
     * answer : content : gold_count : 0 id : invalid num : 0 publish_time :
     * 1452407139656 status : 1 time : 3 type : 当前直播互动题状态
     */
    private TopicEntity topic;
    VideoQuestionLiveEntity videoQuestionLiveEntity;
    /** 当前禁言用户列表,存id */
    private List<String> disableSpeaking;
    /** 是否被禁言 */
    private boolean isDisable;

    public void setTopic(TopicEntity topic) {
        this.topic = topic;
    }

    public void setVideoQuestionLiveEntity(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (this.videoQuestionLiveEntity == null && videoQuestionLiveEntity == null) {
            Loger.d(TAG, "setVideoQuestionLiveEntity,extra");
        }
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
    }

    public void setDisableSpeaking(List<String> disable_speaking) {
        this.disableSpeaking = disable_speaking;
    }

    public RoomStatusEntity getMainRoomstatus() {
        return mainRoomstatus;
    }

    public TopicEntity getTopic() {
        return topic;
    }

    public VideoQuestionLiveEntity getVideoQuestionLiveEntity() {
        return videoQuestionLiveEntity;
    }

    public List<String> getDisableSpeaking() {
        return disableSpeaking;
    }

    public RoomStatusEntity getCoachRoomstatus() {
        return coachRoomstatus;
    }

    public void setMode(String mode) {
        coachRoomstatus.setMode(mode);
    }

    public String getMode() {
        return coachRoomstatus.getMode();
    }

    public void copy(LiveTopic liveTopic) {
        topic = liveTopic.topic;
        isDisable = liveTopic.isDisable;
        mainRoomstatus.copy(liveTopic.getMainRoomstatus());
        coachRoomstatus.copy(liveTopic.getCoachRoomstatus());
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    /**
     * @author linyuqiang 当前直播状态
     */
    public static class RoomStatusEntity {
        private int id = 0;
        /** 课程是否开始,默认为false */
        private boolean classbegin;
        /** 是否开启弹幕 ，默认为false */
        private boolean openbarrage;
        /** 是否开启聊天区 ，默认为true */
        private boolean openchat;
        /** 未知用途 */
        private boolean isCalling;
        /** 当前的直播模式 */
        private String mode = MODE_TRANING;
        private boolean haveExam = false;
        private String examStatus = "";
        private String examNum = "";
        private String onmic = "off";
        private String openhands = "off";
        private String room;
        private final ArrayList<ClassmateEntity> classmateEntities = new ArrayList<>();
        private JSONArray students;
        private boolean classmateChange = true;
        private boolean openDbEnergy;

        public RoomStatusEntity() {
            classbegin = false;
            openbarrage = false;
            openchat = true;
        }

        private void copy(RoomStatusEntity roomStatusEntity) {
            id = roomStatusEntity.id;
            classbegin = roomStatusEntity.classbegin;
            openbarrage = roomStatusEntity.openbarrage;
            openchat = roomStatusEntity.openchat;
            isCalling = roomStatusEntity.isCalling;
            mode = roomStatusEntity.mode;
            haveExam = roomStatusEntity.haveExam;
            examStatus = roomStatusEntity.examStatus;
            examNum = roomStatusEntity.examNum;
            onmic = roomStatusEntity.onmic;
            openhands = roomStatusEntity.openhands;
            room = roomStatusEntity.room;
            students = roomStatusEntity.students;
            classmateChange = roomStatusEntity.classmateChange;
            classmateEntities.clear();
            for (ClassmateEntity entity : roomStatusEntity.classmateEntities) {
                classmateEntities.add(entity);
            }
        }

        public void setClassbegin(boolean classbegin) {
            this.classbegin = classbegin;
        }

        public void setOpenbarrage(boolean openbarrage) {
            this.openbarrage = openbarrage;
        }

        public void setOpenchat(boolean openchat) {
            this.openchat = openchat;
        }

        public boolean isClassbegin() {
            return classbegin;
        }

        public boolean isOpenbarrage() {
            return openbarrage;
        }

        public boolean isOpenchat() {
            return openchat;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public boolean isCalling() {
            return isCalling;
        }

        public void setCalling(boolean calling) {
            isCalling = calling;
        }

        public boolean isHaveExam() {
            return haveExam;
        }

        public void setHaveExam(boolean haveExam) {
            this.haveExam = haveExam;
        }

        public String getExamStatus() {
            return examStatus;
        }

        public void setExamStatus(String examStatus) {
            this.examStatus = examStatus;
        }

        public String getExamNum() {
            return examNum;
        }

        public void setExamNum(String examNum) {
            this.examNum = examNum;
        }

        public String getOnmic() {
            return onmic;
        }

        public void setOnmic(String onmic) {
            this.onmic = onmic;
        }

        public String getOpenhands() {
            return openhands;
        }

        public void setOpenhands(String openhands) {
            this.openhands = openhands;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public ArrayList<ClassmateEntity> getClassmateEntities() {
            return classmateEntities;
        }

        public JSONArray getStudents() {
            return students;
        }

        public void setStudents(JSONArray students) {
            this.students = students;
        }

        public boolean isClassmateChange() {
            return classmateChange;
        }

        public void setClassmateChange(boolean classmateChange) {
            this.classmateChange = classmateChange;
        }

        public boolean isOpenDbEnergy() {
            return openDbEnergy;
        }

        public void setOpenDbEnergy(boolean openDbEnergy) {
            this.openDbEnergy = openDbEnergy;
        }
    }

    /**
     * @author linyuqiang 当前直播互动题状态
     */
    public static class TopicEntity {
        /** 当前互动题的答案 */
        private String answer;
        /** 当前互动题的内容 */
        private String content;
        /** 当前互动题的金币数 */
        private int gold_count;
        /** 当前互动题的Id，invalid表示互动不可用 */
        private String id;
        /** 填空题的空数 */
        private int num;
        /** 当前互动题发布时间 */
        private long publish_time;
        /** 备用字段 */
        private int status;
        /** 做题时间，单位是分钟 */
        private int time;
        /** 互动题类型，1是选择，2是填空 */
        private String type;
        /** 当type=1时为选择题，choiceType 1：单选；2：多选，num为选择题数量 */
        public String choiceType;
        /** 题目来源 */
        private String srcType = "";
        /** 是否加载H5 */
        private boolean isTestUseH5;
        private String isAllow42;
        private String speechContent;

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setGold_count(int gold_count) {
            this.gold_count = gold_count;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public void setPublish_time(long publish_time) {
            this.publish_time = publish_time;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getChoiceType() {
            return choiceType;
        }

        public void setChoiceType(String choiceType) {
            this.choiceType = choiceType;
        }

        public String getAnswer() {
            return answer;
        }

        public String getContent() {
            return content;
        }

        public int getGold_count() {
            return gold_count;
        }

        public String getId() {
            return id;
        }

        public int getNum() {
            return num;
        }

        public long getPublish_time() {
            return publish_time;
        }

        public int getStatus() {
            return status;
        }

        public int getTime() {
            return time;
        }

        public String getType() {
            return type;
        }

        public String getSrcType() {
            return srcType;
        }

        public void setSrcType(String srcType) {
            this.srcType = srcType;
        }

        public boolean isTestUseH5() {
            return isTestUseH5;
        }

        public void setTestUseH5(boolean testUseH5) {
            isTestUseH5 = testUseH5;
        }

        public String getIsAllow42() {
            return isAllow42;
        }

        public void setIsAllow42(String isAllow42) {
            this.isAllow42 = isAllow42;
        }

        public String getSpeechContent() {
            return speechContent;
        }

        public void setSpeechContent(String speechContent) {
            this.speechContent = speechContent;
        }
    }
}
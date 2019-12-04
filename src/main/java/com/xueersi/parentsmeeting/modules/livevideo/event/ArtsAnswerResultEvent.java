package com.xueersi.parentsmeeting.modules.livevideo.event;


import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GroupClassAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerResultStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultEntity;

/**
 * 文科答题结果 事件
 *
 * @author chekun
 * created  at 2018/9/6 14:07
 */
public class ArtsAnswerResultEvent {

    /** h5 js回调待会的原始数据 */
    private String dataStr;
    SpeechResultEntity speechResultEntity;
    AnswerResultStateListener answerResultStateListener;
    private String testId;
    private int isRight;
    /** 1-强制收题，0-自己提交*/
    private int isforce;
    /** 互动题打开时间*/
    private long entranceTime;

    private boolean isPlayBack;
    /** 体验课题目*/
    private boolean isExper = false;
    private int mType;
    /** 新课件互动题 */
    private VideoQuestionLiveEntity detailInfo;
    /** 新课件是否是预加载 */
    private boolean ispreload;

    private  AnswerResultEntity mAnswerResultEntity;
    private GroupClassAnswerResultEntity mGroupClassAnswerResultEntity;

    /** js回调 传回答案 */
    public static final int TYPE_H5_ANSWERRESULT = 1;

    /** 本地答题 */
    public static final int TYPE_NATIVE_ANSWERRESULT = 2;

    /** 本地语音答题 填空 选择 */
    public static final int TYPE_VOICE_SELECT_BLANK = 4;

    /** js回传rolePlay答题结果 */
    public static final int TYPE_ROLEPLAY_ANSWERRESULT = 3;

    /** 本地上传语音答题 填空选择 */
    public static final int TYPE_NATIVE_UPLOAD_VOICE_SELECT_BLANK = 5;

    /** 投票结束通知 */
    public static final int TYPE_H5_VOTE_RESULT = 6;
    /**
     * @param dataStr 结果数据   type 为1时  dataStr 为答案原始数据  type为2时  为试题id
     * @param type    答题结果类型
     */

    /** 英语1v2 语音点名反馈字段 */
    private int interactType;
    public ArtsAnswerResultEvent(String dataStr, int type) {
        this.dataStr = dataStr;
        this.mType = type;
    }


    public ArtsAnswerResultEvent(AnswerResultEntity answerResultEntity) {
        this.mAnswerResultEntity=answerResultEntity;
    }

    public ArtsAnswerResultEvent(GroupClassAnswerResultEntity groupClassAnswerResultEntity) {
        this.mGroupClassAnswerResultEntity = groupClassAnswerResultEntity;
    }

    public AnswerResultEntity getAnswerResultEntity() {
        return mAnswerResultEntity;
    }

    public void setAnswerResultEntity(AnswerResultEntity mAnswerResultEntity) {
        this.mAnswerResultEntity = mAnswerResultEntity;
    }

    public GroupClassAnswerResultEntity getGroupClassAnswerResultEntity() {
        return mGroupClassAnswerResultEntity;
    }

    public void setGroupClassAnswerResultEntity(GroupClassAnswerResultEntity GroupClassAnswerResultEntity) {
        this.mGroupClassAnswerResultEntity = GroupClassAnswerResultEntity;
    }

    public SpeechResultEntity getSpeechResultEntity() {
        return speechResultEntity;
    }

    public void setSpeechResultEntity(SpeechResultEntity speechResultEntity) {
        this.speechResultEntity = speechResultEntity;
    }

    public String getDataStr() {
        return dataStr;
    }

    public int getType() {
        return this.mType;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return testId;
    }

    public void setAnswerResultStateListener(AnswerResultStateListener answerResultStateListener) {
        this.answerResultStateListener = answerResultStateListener;
    }

    public AnswerResultStateListener getAnswerResultStateListener() {
        return answerResultStateListener;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public int getIsRight() {
        return isRight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj == this) {
            return true;
        }
        if (obj != null && obj instanceof ArtsAnswerResultEvent) {
            ArtsAnswerResultEvent target = (ArtsAnswerResultEvent) obj;
            if (dataStr.equals(target.getDataStr()) && mType == target.getType()) {
                return true;
            }
        }
        return false;
    }

    public VideoQuestionLiveEntity getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(VideoQuestionLiveEntity detailInfo) {
        this.detailInfo = detailInfo;
    }

    public boolean isIspreload() {
        return ispreload;
    }

    public void setIspreload(boolean ispreload) {
        this.ispreload = ispreload;
    }

    public int getIsforce() {
        return isforce;
    }

    public void setIsforce(int isforce) {
        this.isforce = isforce;
    }

    public long getEntranceTime() {
        return entranceTime;
    }

    public void setEntranceTime(long entranceTime) {
        this.entranceTime = entranceTime;
    }

    public boolean isPlayBack() {
        return isPlayBack;
    }

    public void setPlayBack(boolean playBack) {
        isPlayBack = playBack;
    }

    public void setExper(boolean exper) {
        isExper = exper;
    }

    public boolean isExper() {
        return isExper;
    }

    @Override
    public String toString() {
        return "ArtsAnswerResultEvent{" +
                "dataStr='" + dataStr + '\'' +
                ", mType=" + mType +
                '}';
    }

    public int getInteractType() {
        return interactType;
    }

    public void setInteractType(int interactType) {
        this.interactType = interactType;
    }
}

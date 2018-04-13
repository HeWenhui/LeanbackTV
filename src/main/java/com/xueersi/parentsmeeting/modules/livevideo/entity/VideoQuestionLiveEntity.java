package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.parentsmeeting.entity.AnswerEntity;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;

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
    String TAG = "VideoQuestionLiveEntity";
    public double time;// NUMBER: 3.00
    public String id;// STRING: 50
    public double gold;// NUMBER: 3.00
    public int num;// NUMBER: 3.00
    public String type;// STRING: 2
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
    public String url;
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

    public VideoQuestionLiveEntity() {
    }

    public String getIsVoice() {
        return isVoice;
    }

    public void setIsVoice(String isVoice) {
        //isVoice = "0";
        this.isVoice = isVoice;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nid=" + id);
        builder.append("\ntime=" + time);
        builder.append("\ngold=" + gold);
        builder.append("\nnum=" + num);
        builder.append("\ntype=" + type);
        builder.append("\nchoiceType=" + choiceType);
        if ("1".equals(isAllow42)) {
            builder.append("\nspeechContent=" + speechContent);
        }
        if ("1".equals(getIsVoice())) {
            builder.append("\nquestiontype=" + questiontype);
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
     * 设置选择题显示数据
     */
    private void editShowQuestion() {
        int vBlankSize = num;
        for (int i = 0; i < vBlankSize; i++) {
            AnswerEntity answerLiveEntity = new AnswerEntity();
            mAnswerEntityLst.add(answerLiveEntity);
        }
    }

    @Override
    public int getvBlankSize() {
        return num;
    }
}

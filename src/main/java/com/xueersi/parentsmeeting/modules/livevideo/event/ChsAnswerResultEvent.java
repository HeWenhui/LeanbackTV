package com.xueersi.parentsmeeting.modules.livevideo.event;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;

/**
 * FileName: ChsAnswerResultEvent
 * Author: WangDe
 * Date: 2019/4/25 13:40
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class ChsAnswerResultEvent {
    /** h5 js回调待会的原始数据 */
    private String dataStr;

    private String testId;
    /** 新课件互动题 */
    private VideoQuestionLiveEntity detailInfo;
    /** 新课件是否是预加载 */
    private boolean ispreload;

    private boolean isPlayBack;

    private int mType;

    private ChineseAISubjectResultEntity resultEntity;
    /** js回调 语文ai主观题传回答案 */
    public static final int TYPE_AI_CHINESE_ANSWERRESULT = 1;

    public ChsAnswerResultEvent(String dataStr,int type){
        this.dataStr = dataStr;
        this.mType = type;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
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

    public boolean isPlayBack() {
        return isPlayBack;
    }

    public void setPlayBack(boolean playBack) {
        isPlayBack = playBack;
    }

    public ChineseAISubjectResultEntity getResultEntity() {
        return resultEntity;
    }

    public void setResultEntity(ChineseAISubjectResultEntity resultEntity) {
        this.resultEntity = resultEntity;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }
}

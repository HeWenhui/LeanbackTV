package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 访问英语智能测评需要的接口参数
 * wiki :http://wiki.xesv5.com/pages/viewpage.action?pageId=18918969
 */
public class IntelligentRecognitionRecord implements Parcelable {
    //场次id
    private String liveId;
    //素材id
    private String materialId;
    //学生id
    private String stuId;
    //学生购课id
    private String stuCouId;
    //直播0， 回放1
    private String isPlayBack;
    //第一次作答详细数据
    private String answers;
    //纠音详情
    private String correctCase;
    //重读详情
    private String rereadCase;
    //题目时长
    private String answerTime;
    //使用的端
    private String useClient;
    //端的版本号
    private String useClientVer;

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuCouId() {
        return stuCouId;
    }

    public void setStuCouId(String stuCouId) {
        this.stuCouId = stuCouId;
    }

    public String getIsPlayBack() {
        return isPlayBack;
    }

    public void setIsPlayBack(String isPlayBack) {
        this.isPlayBack = isPlayBack;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getCorrectCase() {
        return correctCase;
    }

    public void setCorrectCase(String correctCase) {
        this.correctCase = correctCase;
    }

    public String getRereadCase() {
        return rereadCase;
    }

    public void setRereadCase(String rereadCase) {
        this.rereadCase = rereadCase;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }

    public String getUseClient() {
        return useClient;
    }

    public void setUseClient(String useClient) {
        this.useClient = useClient;
    }

    public String getUseClientVer() {
        return useClientVer;
    }

    public void setUseClientVer(String useClientVer) {
        this.useClientVer = useClientVer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.liveId);
        dest.writeString(this.materialId);
        dest.writeString(this.stuId);
        dest.writeString(this.stuCouId);
        dest.writeString(this.isPlayBack);
        dest.writeString(this.answers);
        dest.writeString(this.correctCase);
        dest.writeString(this.rereadCase);
        dest.writeString(this.answerTime);
        dest.writeString(this.useClient);
        dest.writeString(this.useClientVer);
    }

    public IntelligentRecognitionRecord() {
    }

    protected IntelligentRecognitionRecord(Parcel in) {
        this.liveId = in.readString();
        this.materialId = in.readString();
        this.stuId = in.readString();
        this.stuCouId = in.readString();
        this.isPlayBack = in.readString();
        this.answers = in.readString();
        this.correctCase = in.readString();
        this.rereadCase = in.readString();
        this.answerTime = in.readString();
        this.useClient = in.readString();
        this.useClientVer = in.readString();
    }

    public static final Parcelable.Creator<IntelligentRecognitionRecord> CREATOR = new Parcelable.Creator<IntelligentRecognitionRecord>() {
        @Override
        public IntelligentRecognitionRecord createFromParcel(Parcel source) {
            return new IntelligentRecognitionRecord(source);
        }

        @Override
        public IntelligentRecognitionRecord[] newArray(int size) {
            return new IntelligentRecognitionRecord[size];
        }
    };
}

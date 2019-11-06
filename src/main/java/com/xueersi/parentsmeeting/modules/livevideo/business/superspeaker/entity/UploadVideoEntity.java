package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity;

import android.os.Parcel;
import android.os.Parcelable;

/** 上传视频成功，调用后台接口需要的参数 */
public class UploadVideoEntity implements Parcelable {

    private String liveId;

    private String stuCouId;

    private String stuId;

    private String isPlayBack;

    private String testId;

    private String srcType;

    private String video_url;

    private String voice_url;

    private String isUpload;

    private String averVocieDecibel;

    private String audioLocalUrl;

    private String videoLocalUrl;
    /** 采样率，创建short数组大小用 */
    private int sampleRate;

    private String uploadVideoSetKey;

    public UploadVideoEntity() {
    }

    public String getAudioLocalUrl() {
        return audioLocalUrl;
    }

    public void setAudioLocalUrl(String audioLocalUrl) {
        this.audioLocalUrl = audioLocalUrl;
    }

    public String getVideoLocalUrl() {
        return videoLocalUrl;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setVideoLocalUrl(String videoLocalUrl) {
        this.videoLocalUrl = videoLocalUrl;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getStuCouId() {
        return stuCouId;
    }

    public void setStuCouId(String stuCouId) {
        this.stuCouId = stuCouId;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getIsPlayBack() {
        return isPlayBack;
    }

    public void setIsPlayBack(String isPlayBack) {
        this.isPlayBack = isPlayBack;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVoice_url() {
        return voice_url;
    }

    public void setVoice_url(String voice_url) {
        this.voice_url = voice_url;
    }

    public String getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(String isUpload) {
        this.isUpload = isUpload;
    }

    public String getAverVocieDecibel() {
        return averVocieDecibel;
    }

    public void setAverVocieDecibel(String averVocieDecibel) {
        this.averVocieDecibel = averVocieDecibel;
    }

    public String getUploadVideoSetKey() {
        return uploadVideoSetKey;
    }

    public void setUploadVideoSetKey(String uploadVideoSetKey) {
        this.uploadVideoSetKey = uploadVideoSetKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.liveId);
        dest.writeString(this.stuCouId);
        dest.writeString(this.stuId);
        dest.writeString(this.isPlayBack);
        dest.writeString(this.testId);
        dest.writeString(this.srcType);
        dest.writeString(this.video_url);
        dest.writeString(this.voice_url);
        dest.writeString(this.isUpload);
        dest.writeString(this.averVocieDecibel);
        dest.writeString(this.audioLocalUrl);
        dest.writeString(this.videoLocalUrl);
        dest.writeInt(this.sampleRate);
        dest.writeString(this.uploadVideoSetKey);
    }

    protected UploadVideoEntity(Parcel in) {
        this.liveId = in.readString();
        this.stuCouId = in.readString();
        this.stuId = in.readString();
        this.isPlayBack = in.readString();
        this.testId = in.readString();
        this.srcType = in.readString();
        this.video_url = in.readString();
        this.voice_url = in.readString();
        this.isUpload = in.readString();
        this.averVocieDecibel = in.readString();
        this.audioLocalUrl = in.readString();
        this.videoLocalUrl = in.readString();
        this.sampleRate = in.readInt();
        this.uploadVideoSetKey = in.readString();
    }

    public static final Creator<UploadVideoEntity> CREATOR = new Creator<UploadVideoEntity>() {
        @Override
        public UploadVideoEntity createFromParcel(Parcel source) {
            return new UploadVideoEntity(source);
        }

        @Override
        public UploadVideoEntity[] newArray(int size) {
            return new UploadVideoEntity[size];
        }
    };
}

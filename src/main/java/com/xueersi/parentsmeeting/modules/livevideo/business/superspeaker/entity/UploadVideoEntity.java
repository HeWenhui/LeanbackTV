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

    public void setVideoLocalUrl(String videoLocalUrl) {
        this.videoLocalUrl = videoLocalUrl;
    }

    protected UploadVideoEntity(Parcel in) {
        liveId = in.readString();
        stuCouId = in.readString();
        stuId = in.readString();
        isPlayBack = in.readString();
        testId = in.readString();
        srcType = in.readString();
        video_url = in.readString();
        voice_url = in.readString();
        isUpload = in.readString();
        averVocieDecibel = in.readString();
        audioLocalUrl = in.readString();
        videoLocalUrl = in.readString();
    }

    public static final Creator<UploadVideoEntity> CREATOR = new Creator<UploadVideoEntity>() {
        @Override
        public UploadVideoEntity createFromParcel(Parcel in) {
            return new UploadVideoEntity(in);
        }

        @Override
        public UploadVideoEntity[] newArray(int size) {
            return new UploadVideoEntity[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(liveId);
        dest.writeString(stuCouId);
        dest.writeString(stuId);
        dest.writeString(isPlayBack);
        dest.writeString(testId);
        dest.writeString(srcType);
        dest.writeString(video_url);
        dest.writeString(voice_url);
        dest.writeString(isUpload);
        dest.writeString(averVocieDecibel);
        dest.writeString(audioLocalUrl);
        dest.writeString(videoLocalUrl);
    }
}

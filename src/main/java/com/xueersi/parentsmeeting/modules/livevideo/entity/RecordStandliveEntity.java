package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @Date on 2019/9/29 15:58
 * @Author zhangyuansun
 * @Description getinfo下发字段
 * 英语1v2小组课
 */
public class RecordStandliveEntity {
    /*
     * 距离场次开始时间的差值，负数代表还未开始的时间
     */
    private int diffBegin;
    /*
     * 录播课的地址
     */
    private String recordUrl;
    /*
     * 录播课的地址(不包括域名)
     */
    private String videoPath;
    /*
     * 录播课的打点数据
     */
    private String metaDataUrl;
    /*
     * 1为真流2为假流
     */
    private int partnerType;
    /*
     * 视频id
     */
    private int videoId;

    public int getDiffBegin() {
        return diffBegin;
    }

    public void setDiffBegin(int diffBegin) {
        this.diffBegin = diffBegin;
    }

    public String getRecordUrl() {
        return recordUrl;
    }

    public void setRecordUrl(String recordUrl) {
        this.recordUrl = recordUrl;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getMetaDataUrl() {
        return metaDataUrl;
    }

    public void setMetaDataUrl(String metaDataUrl) {
        this.metaDataUrl = metaDataUrl;
    }

    public int getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(int partnerType) {
        this.partnerType = partnerType;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}

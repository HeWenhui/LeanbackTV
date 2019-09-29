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
     * 录播课的打点数据
     */
    private String metaDataUrl;

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

    public String getMetaDataUrl() {
        return metaDataUrl;
    }

    public void setMetaDataUrl(String metaDataUrl) {
        this.metaDataUrl = metaDataUrl;
    }
}

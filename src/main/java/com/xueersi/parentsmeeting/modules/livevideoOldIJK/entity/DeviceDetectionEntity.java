package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

/**
 * Created by ZhangYuansun on 2018/9/12
 * 低端设备检测信息实体
 */

public class DeviceDetectionEntity {
    /**
     * 不符合要求的数量
     */
    private int unMatchCount;
    /**
     * 不符合要求的描述信息
     */
    private String unMatchDesc;
    /**
     * 系统版本
     */
    private String versionCurrent;
    /**
     * 系统要求
     */
    private String versionNotice;
    /**
     * 内存大小
     */
    private String memoryCurrent;
    /**
     * 内存要求
     */
    private String memoryNotice;

    public int getUnMatchCount() {
        return unMatchCount;
    }

    public void setUnMatchCount(int unMatchCount) {
        this.unMatchCount = unMatchCount;
    }

    public String getUnMatchDesc() {
        return unMatchDesc;
    }

    public void setUnMatchDesc(String unMatchDesc) {
        this.unMatchDesc = unMatchDesc;
    }

    public String getVersionCurrent() {
        return versionCurrent;
    }

    public void setVersionCurrent(String versionCurrent) {
        this.versionCurrent = versionCurrent;
    }

    public String getVersionNotice() {
        return versionNotice;
    }

    public void setVersionNotice(String versionNotice) {
        this.versionNotice = versionNotice;
    }

    public String getMemoryCurrent() {
        return memoryCurrent;
    }

    public void setMemoryCurrent(String memoryCurrent) {
        this.memoryCurrent = memoryCurrent;
    }

    public String getMemoryNotice() {
        return memoryNotice;
    }

    public void setMemoryNotice(String memoryNotice) {
        this.memoryNotice = memoryNotice;
    }
}

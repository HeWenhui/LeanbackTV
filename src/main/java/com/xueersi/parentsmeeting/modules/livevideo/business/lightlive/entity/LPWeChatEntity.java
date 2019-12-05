package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity;

import android.text.TextUtils;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity
 * @ClassName: LPWeChatEntity
 * @Description: 联系老师实体
 * @Author: WangDe
 * @CreateDate: 2019/11/29 16:23
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/29 16:23
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LPWeChatEntity {
    public static final int OFFICIAL_ACCOUNTS = 1;

    public static final int WECHAT_GROUP = 2;

    public static final int TEACHER_WECHAT = 3;

    private int id;
    /**
     * 类型 1公众号 2加群 3加老师微信
     */
    private int tipType;
    /**
     * 提示文案
     */
    private String tipInfo;

    private int existWx;
    /**
     * 老师微信
     */
    private String teacherWx;
    /**
     * 老师名字
     */
    private String teacherName;
    /**
     * 老师头像
     */
    private String teacherImg;
    /**
     * 2的时候为群二维码Url
     */
    private String wxQrUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipType() {
        return tipType;
    }

    public void setTipType(int tipType) {
        this.tipType = tipType;
    }

    public String getTipInfo() {
        return tipInfo;
    }

    public void setTipInfo(String tipInfo) {
        this.tipInfo = tipInfo;
    }

    public int getExistWx() {
        return existWx;
    }

    public void setExistWx(int existWx) {
        this.existWx = existWx;
    }

    public String getTeacherWx() {
        return teacherWx;
    }

    public void setTeacherWx(String teacherWx) {
        this.teacherWx = teacherWx;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherImg() {
        return teacherImg;
    }

    public void setTeacherImg(String teacherImg) {
        this.teacherImg = teacherImg;
    }

    public String getWxQrUrl() {
        return wxQrUrl;
    }

    public void setWxQrUrl(String wxQrUrl) {
        this.wxQrUrl = wxQrUrl;
    }

    public boolean hasData() {
        if (tipType == WECHAT_GROUP) {
            if (!TextUtils.isEmpty(wxQrUrl)  && !TextUtils.isEmpty(teacherWx)&& !TextUtils.isEmpty(tipInfo)) {
                return true;
            }
        } else if (TEACHER_WECHAT == tipType) {
            if (!TextUtils.isEmpty(teacherName) && !TextUtils.isEmpty(teacherWx)&& !TextUtils.isEmpty(tipInfo)) {
                return true;
            }
        }
        return false;
    }
}

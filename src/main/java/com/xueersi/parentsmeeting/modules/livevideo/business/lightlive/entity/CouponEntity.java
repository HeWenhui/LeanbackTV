package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity;

import java.io.Serializable;

/**
 * @Author: chengpengfei
 * @CreateDate: 2019/10/28 10:46
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/28 10:46
 */
public class CouponEntity implements Serializable {

    private String title;//	string	券文字（满xx减xx）
    private String content;//	string	备用

    /**
     * description:券详情
     */

    private int id;//	integer 优惠券ID

    private String moneyIcon;//	string币符号（返回￥或空,默认展示就行）

    private String faceText;//	string现金券抵用额（￥xx, xx折）

    private String reduceText;//	string现金券满减文案（满xx元xx,满xx无减xx折）

    private String name;//	string 优惠券名称

    private String validDate;//	string 优惠券有效期(开始日期-结束日期)

    private String buttonText;//	string按钮文案

    private String getedText;//	string已领取xx张文案

    private int status;//	状态 1立即领取或继续领取（使用buttonText） 2已领取(使用icon)


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoneyIcon() {
        return moneyIcon;
    }

    public void setMoneyIcon(String moneyIcon) {
        this.moneyIcon = moneyIcon;
    }

    public String getFaceText() {
        return faceText;
    }

    public void setFaceText(String faceText) {
        this.faceText = faceText;
    }

    public String getReduceText() {
        return reduceText;
    }

    public void setReduceText(String reduceText) {
        this.reduceText = reduceText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getGetedText() {
        return getedText;
    }

    public void setGetedText(String getedText) {
        this.getedText = getedText;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

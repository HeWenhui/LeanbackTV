package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chengpengfei
 * @CreateDate: 2019/10/28 10:43
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/28 10:43
 */
public class CouponModuleEntity implements Serializable {


    private String titleIcon;//	string	领券icon
    private String title;//	string	标题（领券）
    private String buttonText;//	string	按钮文字（去领取）
    private List<CouponEntity> list;//	array	券列表（展示数量由后端控制，有多少展示多少）

    public String getTitleIcon() {
        return titleIcon;
    }

    public void setTitleIcon(String titleIcon) {
        this.titleIcon = titleIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public List<CouponEntity> getList() {
        return list;
    }

    public void setList(List<CouponEntity> list) {
        this.list = list;
    }
}

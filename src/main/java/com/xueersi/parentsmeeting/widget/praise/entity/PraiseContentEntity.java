package com.xueersi.parentsmeeting.widget.praise.entity;

import java.util.List;

public class PraiseContentEntity {
    /** 标题 */
    private String tittle;
    /** 名字 */
    private String name;
    /** 是否上榜 */
    private int status;
    /** 布局类型 */
    private int viewType;
    /** 列占位 */
    private int itemSpan;
    /** 表扬榜样式 */
    private int praiseStyle;

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getItemSpan() {
        return itemSpan;
    }

    public void setItemSpan(int itemSpan) {
        this.itemSpan = itemSpan;
    }

    public int getPraiseStyle() {
        return praiseStyle;
    }

    public void setPraiseStyle(int praiseStyle) {
        this.praiseStyle = praiseStyle;
    }

}

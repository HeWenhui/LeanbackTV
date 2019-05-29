package com.xueersi.parentsmeeting.widget.praise.entity;

import java.util.List;

public class PraiseEntity {
    /** 标题*/
    private  String tittle;
    /** 名字 */
    private  String name;
    /** 是否上榜 */
    private   int status;
    /** 布局类型 */
    private    int viewType;
    /** 列占位*/
    private  int itemSpan;
    /** 表扬榜样式*/
    private int praiseStyle;
    /** 榜单类型  1=课清, 2=口述题, 3=互动, 4=随堂测, 5=晨读, 6=预习, 7=演讲家*/
    private int praiseType;
    /** 结果样式 1=表扬*/
    private int resultType;
    /** 榜单名*/
    private String praiseName;
    /** 老师名*/
    private String teacherName;
    /** 老师头像地址*/
    private String teacherHeadImage;
    /** 鼓励语*/
    private String encouraging ;
    /** 我再表扬榜中位置 */
    private  int position;
    /** 内容类型 */
    private List<PraiseContentEntity> contentEntityList;

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

    public int getPraiseType() {
        return praiseType;
    }

    public void setPraiseType(int praiseType) {
        this.praiseType = praiseType;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public String getPraiseName() {
        return praiseName;
    }

    public void setPraiseName(String praiseName) {
        this.praiseName = praiseName;
    }

    public List<PraiseContentEntity> getContentEntityList() {
        return contentEntityList;
    }

    public void setContentEntityList(List<PraiseContentEntity> contentEntityList) {
        this.contentEntityList = contentEntityList;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherHeadImage() {
        return teacherHeadImage;
    }

    public void setTeacherHeadImage(String teacherHeadImage) {
        this.teacherHeadImage = teacherHeadImage;
    }

    public String getEncouraging() {
        return encouraging;
    }

    public void setEncouraging(String encouraging) {
        this.encouraging = encouraging;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

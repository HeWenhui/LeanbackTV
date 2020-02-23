package com.xueersi.parentsmeeting.modules.livevideo.event;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;

/**
 * 直播间内  h5 关闭事件
 *@author  chenkun
 * */
public class LiveRoomH5CloseEvent {
    /**h5 返回的 金币*/
    private int mGoldNum;
    /**h5返回的 能量*/
    private int mEnergyNum;
    private int h5Type;
    /**互动题*/
    public  static  final int H5_TYPE_INTERACTION = 1;
    /**测试卷*/
    public  static  final int H5_TYPE_EXAM = 2;
    /**课件*/
    public  static  final int H5_TYPE_COURSE= 3;
    /** testId/ testPlan*/
    private String id;
    /** 是否是老师发起的关闭*/
    private boolean closeByTeacher;
    /** 页面 */
    private BasePager basePager;
    /**
     * 是否是理科新课件平台
     */
    private boolean scienceNewCourseWare;
    private EnglishH5Entity englishH5Entity;
    /**
     * 此次作答是否是 强制提交
     */
    private boolean forceSubmit;
    /**
     * 兼容大题互动飞金币动效，大题互动：1
     */
    private int questionType;

    public void setForceSubmit(boolean forceSubmit) {
        this.forceSubmit = forceSubmit;
    }

    public boolean isForceSubmit() {
        return forceSubmit;
    }

    public boolean isScienceNewCourseWare() {
        return scienceNewCourseWare;
    }

    public void setScienceNewCourseWare(boolean scienceNewCourseWare) {
        this.scienceNewCourseWare = scienceNewCourseWare;
    }

    public EnglishH5Entity getEnglishH5Entity() {
        return englishH5Entity;
    }

    public void setEnglishH5Entity(EnglishH5Entity englishH5Entity) {
        this.englishH5Entity = englishH5Entity;
    }


    public void setCloseByTeahcer(boolean closeByTeacher) {
        this.closeByTeacher = closeByTeacher;
    }

    public boolean isCloseByTeacher() {
        return closeByTeacher;
    }

    public LiveRoomH5CloseEvent(){
    }

    public LiveRoomH5CloseEvent(int mGoldNum, int mEnergyNum,int h5Type,String id) {
        this.mGoldNum = mGoldNum;
        this.mEnergyNum = mEnergyNum;
        this.h5Type = h5Type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setH5Type(int h5Type) {
        this.h5Type = h5Type;
    }

    public int getH5Type() {
        return h5Type;
    }

    public int getmGoldNum() {
        return mGoldNum;
    }

    public void setmGoldNum(int mGoldNum) {
        this.mGoldNum = mGoldNum;
    }

    public int getmEnergyNum() {
        return mEnergyNum;
    }

    public void setmEnergyNum(int mEnergyNum) {
        this.mEnergyNum = mEnergyNum;
    }

    public void setBasePager(BasePager basePager) {
        this.basePager = basePager;
    }

    public BasePager getBasePager() {
        return basePager;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;
import java.util.List;

public class FeedBackEntity {
    /** 主讲老师名 */
    private String mainName;
    /** 主讲老师名 */
    private String mainHeadImage;
    /** 主讲评价内容 */
    List<List<EvaluateContent>> mainContentList = new ArrayList<>();

    /** 辅导老师名 */
    private String tutorName;
    /** 辅导老师名 */
    private String tutorHeadImage;
    /** 辅导评价内容 */
    List<List<EvaluateContent>> tutorContentList = new ArrayList<>();


    /** 是否打开 */
    private boolean isOpen;
    /** 是否有辅导老师 */
    private boolean haveTutor;
    /** 是否有文字输入 */
    private boolean haveInput;
    /** 评价内容 */
    List<List<String>> feedBackList = new ArrayList<>();

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isHaveTutor() {
        return haveTutor;
    }

    public void setHaveTutor(boolean haveTutor) {
        this.haveTutor = haveTutor;
    }

    public boolean isHaveInput() {
        return haveInput;
    }

    public void setHaveInput(boolean haveInput) {
        this.haveInput = haveInput;
    }

    public List<List<String>> getFeedBackList() {
        return feedBackList;
    }

    public void setFeedBackList(List<List<String>> feedBackList) {
        this.feedBackList = feedBackList;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public String getMainHeadImage() {
        return mainHeadImage;
    }

    public void setMainHeadImage(String mainHeadImage) {
        this.mainHeadImage = mainHeadImage;
    }

    public List<List<EvaluateContent>> getMainContentList() {
        return mainContentList;
    }

    public void setMainContentList(List<List<EvaluateContent>> mainContentList) {
        this.mainContentList = mainContentList;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getTutorHeadImage() {
        return tutorHeadImage;
    }

    public void setTutorHeadImage(String tutorHeadImage) {
        this.tutorHeadImage = tutorHeadImage;
    }

    public List<List<EvaluateContent>> getTutorContentList() {
        return tutorContentList;
    }

    public void setTutorContentList(List<List<EvaluateContent>> tutorContentList) {
        this.tutorContentList = tutorContentList;
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by Tang on 2018/1/4.
 */

public class FullMarkListEntity {

    /**
     * id : 15559
     * rate : 100
     * answer_time : 1
     * stuName : 冠华
     */

    private String id;
    private int rate;
    private String answer_time;
    private String stuName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getAnswer_time() {
        return answer_time;
    }

    public void setAnswer_time(String answer_time) {
        this.answer_time = answer_time;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }
}

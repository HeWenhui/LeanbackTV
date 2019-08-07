package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.text.TextUtils;

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
    private String rate;
    private String answer_time;
    private String stuName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAnswer_time() {
        try {
            int t = Integer.parseInt(answer_time);
            if (t >= 60) {
                answer_time = t / 60 + "分" + t % 60 + "秒";
            } else {
                answer_time = t + "秒";
            }
        } catch (Exception e) {
            answer_time = "";
        }
        return answer_time;
    }

    public void setAnswer_time(String answer_time) {
        this.answer_time = answer_time;
    }

    public String getStuName() {
        if (TextUtils.isEmpty(stuName)) {
            stuName = LiveAppUserInfo.getInstance().getUsernameDefault();
        }
        if (stuName.length() > 4) {
            stuName = stuName.substring(0, 3) + "...";
        }
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }
}

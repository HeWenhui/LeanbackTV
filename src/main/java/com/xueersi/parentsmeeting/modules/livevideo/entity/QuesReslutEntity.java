package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by linyuqiang on 2017/8/10.
 * 填空题结果和是不是有空没填
 */
public class QuesReslutEntity {
    private boolean haveEmpty = false;
    private String result;

    public boolean isHaveEmpty() {
        return haveEmpty;
    }

    public void setHaveEmpty(boolean haveEmpty) {
        this.haveEmpty = haveEmpty;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

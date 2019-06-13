package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class EvaluateContent {

    /** 内容 */
    private String text;
    /** 是否被选中 */
    private boolean selectFlag;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }
}

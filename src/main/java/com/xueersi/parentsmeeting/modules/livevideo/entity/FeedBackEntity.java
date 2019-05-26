package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;
import java.util.List;

public class FeedBackEntity {
   /** 是否打开*/
    boolean isOpen;
    /** 是否有辅导老师*/
    boolean haveTutor;
    /** 是否有文字输入*/
    boolean haveInput;
    /**评价内容*/
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
}

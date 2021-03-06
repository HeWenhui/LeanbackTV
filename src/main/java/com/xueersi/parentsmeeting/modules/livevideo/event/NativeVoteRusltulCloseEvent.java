package com.xueersi.parentsmeeting.modules.livevideo.event;


/**
 * 直播间内 投票题关闭本地 结果展示UI 事件
 * @author chenkun
 */
public class NativeVoteRusltulCloseEvent {
    /**用户是否参与了投票*/
    boolean stuVoted;

    /**投票id*/
    String  voteId;

    public NativeVoteRusltulCloseEvent(boolean stuVoted,String voteId){
        this.stuVoted = stuVoted;
        this.voteId = voteId;
    }

    public void setStuVoted(boolean stuVoted) {
        this.stuVoted = stuVoted;
    }

    public boolean isStuVoted() {
        return stuVoted;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getVoteId() {
        return voteId;
    }


}

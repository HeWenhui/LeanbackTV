package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by lyqai on 2017/12/27.
 */

public interface LiveVoteAction {
    void voteStart(int choiceType, int choiceNum);

    void voteStop();
}

package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

/**
 * Created by linyuqiang on 2017/12/27.
 */

public interface LiveVoteAction {
    void voteStart(LiveTopic.VoteEntity voteEntity);

    void voteJoin(LiveTopic.VoteEntity voteEntity, int answer);

    void voteStop(LiveTopic.VoteEntity voteEntity);

    void onCancle();
}

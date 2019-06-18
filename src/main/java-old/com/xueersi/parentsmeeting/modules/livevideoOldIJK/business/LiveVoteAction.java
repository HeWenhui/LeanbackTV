package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

/**
 * Created by lyqai on 2017/12/27.
 */

public interface LiveVoteAction {
    void voteStart(LiveTopic.VoteEntity voteEntity);

    void voteJoin(LiveTopic.VoteEntity voteEntity, int answer);

    void voteStop(LiveTopic.VoteEntity voteEntity);

    void onCancle();
}

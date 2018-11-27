package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

public interface EnTeamPkAction {
    void onLiveInited(LiveGetInfo getInfo);

    void onRankStart();

    void onRankResult();

    void onRankLead(EnTeamPkRankEntity enTeamPkRankEntity);

    void onModeChange(String mode);

    void destory();
}

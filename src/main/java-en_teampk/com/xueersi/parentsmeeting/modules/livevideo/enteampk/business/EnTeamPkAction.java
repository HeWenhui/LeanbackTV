package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

public interface EnTeamPkAction {
    void onLiveInited(LiveGetInfo getInfo);

    void setPkTeamEntity(PkTeamEntity pkTeamEntity);

    void onRankStart();

    void onRankResult();

    void onRankLead(EnTeamPkRankEntity enTeamPkRankEntity, int type);

    void onModeChange(String mode);

    void destory();
}

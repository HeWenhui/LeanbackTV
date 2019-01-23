package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.ArrayList;

public interface EnTeamPkAction {
    void onLiveInited(LiveGetInfo getInfo);

    void setPkTeamEntity(PkTeamEntity pkTeamEntity);

    void onRankStart();

    void onRankResult();

    void onStuLike(String testId, ArrayList<TeamMemberEntity> teamMemberEntities);

    void onRankLead(EnTeamPkRankEntity enTeamPkRankEntity, String testId, int type);

    void onModeChange(String mode);

    void destory();
}

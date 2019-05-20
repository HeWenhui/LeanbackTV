package com.xueersi.parentsmeeting.modules.livevideo.teampk.event;

import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;

public class TeamPkTeamInfoEvent {
    private TeamPkTeamInfoEntity teamInfoEntity;

    public TeamPkTeamInfoEvent(TeamPkTeamInfoEntity teamInfoEntity) {
        this.teamInfoEntity = teamInfoEntity;
    }

    public TeamPkTeamInfoEntity getTeamInfoEntity() {
        return teamInfoEntity;
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.teampk.event;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;

public class TeamPkTeamInfoEvent {
    private TeamPkTeamInfoEntity teamInfoEntity;
    private ResponseEntity responseEntity;

    public TeamPkTeamInfoEvent(TeamPkTeamInfoEntity teamInfoEntity, ResponseEntity responseEntity) {
        this.teamInfoEntity = teamInfoEntity;
        this.responseEntity = responseEntity;
    }

    public TeamPkTeamInfoEntity getTeamInfoEntity() {
        return teamInfoEntity;
    }

    public ResponseEntity getResponseEntity() {
        return responseEntity;
    }
}

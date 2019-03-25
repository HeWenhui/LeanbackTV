package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

import java.util.ArrayList;

public interface EnTeamPkAction {
    void onLiveInited(LiveGetInfo getInfo);

    void hideTeam();

    void setPkTeamEntity(PkTeamEntity pkTeamEntity);

    /**
     * 显示分队仪式，从notice显示，topic不显示
     *
     * @param showPk
     */
    void onRankStart(boolean showPk);

    void onRankResult();

    void onStuLike(String testId, ArrayList<TeamMemberEntity> teamMemberEntities);

    void onRankLead(EnTeamPkRankEntity enTeamPkRankEntity, String testId, int type);

    void onModeChange(String mode, boolean haveTeamRun);

    void setVideoLayout(LiveVideoPoint liveVideoPoint);

    void destory();
}

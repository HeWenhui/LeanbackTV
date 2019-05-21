package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;

public interface PrimaryItemView {
    void onTeam(String uid, TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity);

    void onModeChange(String mode);

    void onMessage(int type, boolean open);

    void onMessage(boolean videoopen, boolean audioopen);

    void onResume();

    void onPause();

    void onDestroy();
}

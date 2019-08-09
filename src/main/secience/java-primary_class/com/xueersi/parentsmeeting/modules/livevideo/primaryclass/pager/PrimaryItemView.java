package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;

public interface PrimaryItemView {

    void onLiveInited(final LiveGetInfo getInfo);

    void onTeam(String uid, TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity);

    void updateTeam(TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity);

    void onCheckPermission();

    void onModeChange(String mode);

    void onMessage(int type, boolean open);

    void onMessage(boolean videoopen, boolean audioopen);

    void onResume();

    void onPause();

    void updatePkState(float ratio);

    void onAddEnergy(boolean first, int energy);

    void onDestroy();
}

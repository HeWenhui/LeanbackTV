package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.PrimaryClassEntity;

public interface PrimaryItemView {
    void onTeam(String uid, PrimaryClassEntity primaryClassEntity);

    void onMessage(int type, boolean open);

    void onMessage(boolean audioopen, boolean videoopen);

    void onResume();

    void onPause();

    void onDestroy();
}

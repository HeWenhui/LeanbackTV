package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;

public class PrimaryTeamEmptyItem extends BasePrimaryTeamItem {
    private boolean enableVideo = true;
    private boolean enableAudio = true;

    public PrimaryTeamEmptyItem(Context context, TeamMember entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_empty_video;
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        super.doRenderRemoteUi(surfaceV);
    }

}

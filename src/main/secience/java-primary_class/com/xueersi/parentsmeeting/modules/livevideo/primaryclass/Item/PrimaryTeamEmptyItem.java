package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;

public class PrimaryTeamEmptyItem extends BasePrimaryTeamItem {
    private boolean enableVideo = true;
    private boolean enableAudio = true;

    public PrimaryTeamEmptyItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_empty_video;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
        primaryClassView.decorateItemEmpty(root);
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        super.doRenderRemoteUi(surfaceV);
    }

}

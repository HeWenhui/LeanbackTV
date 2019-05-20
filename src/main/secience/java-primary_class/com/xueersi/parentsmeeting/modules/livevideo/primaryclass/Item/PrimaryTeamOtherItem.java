package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;

public class PrimaryTeamOtherItem extends BasePrimaryTeamPeopleItem {
    private boolean enableVideo = true;
    private boolean enableAudio = true;

    public PrimaryTeamOtherItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_other_video;
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        super.doRenderRemoteUi(surfaceV);
    }

    @Override
    public void bindListener() {
        super.bindListener();
        iv_livevideo_primary_team_voice_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        enableAudio = !enableAudio;
                        cloudWorkerThreadPool.getRtcEngine().enableRemoteAudio(entity.getIdInt(), enableAudio);
                    }
                });
            }
        });
        tv_livevideo_primary_team_people_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNameClick.onNameClick(entity, tv_livevideo_primary_team_people_name);
            }
        });
    }

    @Override
    public void updateViews(TeamMate entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        tv_livevideo_primary_team_people_name.setText(entity.getName());
    }

}

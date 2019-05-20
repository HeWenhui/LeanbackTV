package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.VoiceImageView;

public class PrimaryTeamMyItem extends BasePrimaryTeamPeopleItem {

    private boolean enableVideo = true;
    private boolean enableAudio = true;

    public PrimaryTeamMyItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_video;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
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
                        cloudWorkerThreadPool.getRtcEngine().muteLocalAudio(!enableAudio);
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

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        super.doRenderRemoteUi(surfaceV);
    }

    @Override
    public void updateViews(TeamMember entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        tv_livevideo_primary_team_people_name.setText(entity.getStuName());
    }

    @Override
    public void onReport() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_primary_class_team_item_report, rl_livevideo_primary_team_tip, false);
        rl_livevideo_primary_team_tip.addView(view);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_livevideo_primary_team_tip.removeView(view);
            }
        }, 2000);
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class BasePrimaryTeamItem implements AdapterItemInterface<TeamMate> {
    protected String TAG = getClass().getSimpleName();
    protected Context mContext;
    protected TeamMate entity;
    protected CloudWorkerThreadPool cloudWorkerThreadPool;
    protected LogToFile mLogtf;
    protected int uid;

    public BasePrimaryTeamItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        this.mContext = context;
        this.entity = entity;
        this.cloudWorkerThreadPool = workerThread;
        this.uid = uid;
        mLogtf = new LogToFile(context, TAG);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_video;
    }

    @Override
    public void initViews(View root) {

    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(TeamMate entity, int position, Object objTag) {

    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {

    }

    public void reportAudioVolumeOfSpeaker(int volume) {

    }

    public void onReport() {

    }
}

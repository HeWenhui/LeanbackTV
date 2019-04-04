package com.xueersi.parentsmeeting.modules.livevideo.groupgame.item;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

import io.agora.rtc.RtcEngine;

public abstract class BaseCourseGroupItem implements AdapterItemInterface<TeamMemberEntity> {
    static int[] VIDEO_RES = {R.drawable.livevide_course_group_video_no, R.drawable.livevide_course_group_video_dis, R.drawable.livevide_course_group_video_enable};
    static int[] AUDIO_RES = {R.drawable.livevide_course_group_audio_no, R.drawable.livevide_course_group_audio_dis, R.drawable.livevide_course_group_audio_enable};
    protected RelativeLayout rlCourseItemVideo;
    protected ImageView ivCourseItemVideoHead;
    protected TextView tvCourseItemFire;
    protected TextView rlCourseItemName;
    protected ImageView ivCourseItemVideo;
    protected ImageView ivCourseItemAudio;
    protected WorkerThread workerThread;
    protected int uid;
    protected Context mContext;
    protected TeamMemberEntity entity;
    public static int voiceStartFrame = 14;
    public static int voiceMaxFrame = 0;

    public BaseCourseGroupItem(Context context, TeamMemberEntity entity, WorkerThread workerThread, int uid) {
        this.mContext = context;
        this.entity = entity;
        this.workerThread = workerThread;
        this.uid = uid;
    }

    @Override
    public abstract int getLayoutResId();

    @Override
    public void initViews(View root) {
        rlCourseItemVideo = root.findViewById(R.id.rl_livevideo_course_item_video);
        ivCourseItemVideoHead = root.findViewById(R.id.iv_livevideo_course_item_video_head);
        rlCourseItemName = root.findViewById(R.id.rl_livevideo_course_item_name);
        ivCourseItemVideo = root.findViewById(R.id.iv_livevideo_course_item_video);
        ivCourseItemAudio = root.findViewById(R.id.iv_livevideo_course_item_audio);
        tvCourseItemFire = root.findViewById(R.id.tv_livevideo_course_item_fire);
    }

    public abstract void doRenderRemoteUi(SurfaceView surfaceV);

    public abstract void onUserOffline();

    @Override
    public abstract void bindListener();

    public TeamMemberEntity getEntity() {
        return entity;
    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        rlCourseItemName.setText(entity.name);
        tvCourseItemFire.setText("" + entity.energy);
    }

    public void onVolumeUpdate(int volume) {

    }

    public void onOtherDis(int type, boolean enable) {

    }

    public void onScene() {
        tvCourseItemFire.setText("" + entity.energy);
    }
}

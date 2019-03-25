package com.xueersi.parentsmeeting.modules.livevideo.groupgame.item;

import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class CourseGroupItem implements AdapterItemInterface<TeamMemberEntity> {
    static int[] VIDEO_RES = {R.drawable.livevide_course_group_video_no, R.drawable.livevide_course_group_video_dis, R.drawable.livevide_course_group_video_enable};
    static int[] AUDIO_RES = {R.drawable.livevide_course_group_audio_no, R.drawable.livevide_course_group_audio_dis, R.drawable.livevide_course_group_audio_enable};
    private RelativeLayout rl_livevideo_course_item_video;
    ImageView iv_livevideo_course_item_video_head;
    private TextView rl_livevideo_course_item_name;
    private ImageView iv_livevideo_course_item_video;
    private ImageView iv_livevideo_course_item_audio;
    WorkerThread workerThread;

    public CourseGroupItem(WorkerThread workerThread) {
        this.workerThread = workerThread;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_h5_courseware_group_people;
    }

    @Override
    public void initViews(View root) {
        rl_livevideo_course_item_video = root.findViewById(R.id.rl_livevideo_course_item_video);
        iv_livevideo_course_item_video_head = root.findViewById(R.id.iv_livevideo_course_item_video_head);
        rl_livevideo_course_item_name = root.findViewById(R.id.rl_livevideo_course_item_name);
        iv_livevideo_course_item_video = root.findViewById(R.id.iv_livevideo_course_item_video);
        iv_livevideo_course_item_audio = root.findViewById(R.id.iv_livevideo_course_item_audio);
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        iv_livevideo_course_item_video_head.setVisibility(View.GONE);
        rl_livevideo_course_item_video.addView(surfaceV, 0);
    }

    public void onUserJoined(int uid, int elapsed) {
        iv_livevideo_course_item_video_head.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        rl_livevideo_course_item_name.setText(entity.name);
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).into(iv_livevideo_course_item_video_head);
        iv_livevideo_course_item_video.setImageResource(VIDEO_RES[0]);
        iv_livevideo_course_item_audio.setImageResource(AUDIO_RES[0]);
    }
}

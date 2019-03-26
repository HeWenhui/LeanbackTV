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

import io.agora.rtc.RtcEngine;

public class CourseGroupItem implements AdapterItemInterface<TeamMemberEntity> {
    static int[] VIDEO_RES = {R.drawable.livevide_course_group_video_no, R.drawable.livevide_course_group_video_dis, R.drawable.livevide_course_group_video_enable};
    static int[] AUDIO_RES = {R.drawable.livevide_course_group_audio_no, R.drawable.livevide_course_group_audio_dis, R.drawable.livevide_course_group_audio_enable};
    private RelativeLayout rlCourseItemVideo;
    private ImageView ivCourseItemVideoHead;
    private TextView rlCourseItemName;
    private ImageView ivCourseItemVideo;
    private ImageView ivCourseItemAudio;
    private WorkerThread workerThread;
    private boolean enableVideo = true;
    private boolean enableAudio = true;
    private boolean isMe;
    private int uid;

    public CourseGroupItem(WorkerThread workerThread, int uid, boolean isMe) {
        this.workerThread = workerThread;
        this.uid = uid;
        this.isMe = isMe;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_h5_courseware_group_people;
    }

    @Override
    public void initViews(View root) {
        rlCourseItemVideo = root.findViewById(R.id.rl_livevideo_course_item_video);
        ivCourseItemVideoHead = root.findViewById(R.id.iv_livevideo_course_item_video_head);
        rlCourseItemName = root.findViewById(R.id.rl_livevideo_course_item_name);
        ivCourseItemVideo = root.findViewById(R.id.iv_livevideo_course_item_video);
        ivCourseItemAudio = root.findViewById(R.id.iv_livevideo_course_item_audio);
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        ivCourseItemVideoHead.setVisibility(View.GONE);
        rlCourseItemVideo.addView(surfaceV, 0);
    }

    public void onUserOffline() {
        ivCourseItemVideoHead.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindListener() {
        ivCourseItemVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableVideo = !enableVideo;
                    if (isMe) {
                        rtcEngine.enableLocalVideo(enableVideo);
                    } else {
                        rtcEngine.muteRemoteVideoStream(uid, enableVideo);
                    }
                }
            }
        });
        ivCourseItemAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableAudio = !enableAudio;
                    if (isMe) {
                        if (enableAudio) {
                            rtcEngine.enableAudio();
                        } else {
                            rtcEngine.disableAudio();
                        }
                    } else {
                        rtcEngine.muteRemoteAudioStream(uid, enableAudio);
                    }
                }
            }
        });
    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        rlCourseItemName.setText(entity.name);
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).into(ivCourseItemVideoHead);
        ivCourseItemVideo.setImageResource(VIDEO_RES[0]);
        ivCourseItemAudio.setImageResource(AUDIO_RES[0]);
    }
}

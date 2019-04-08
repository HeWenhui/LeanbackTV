package com.xueersi.parentsmeeting.modules.livevideo.groupgame.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.config.GroupGameConfig;
import com.xueersi.ui.adapter.AdapterItemInterface;

import io.agora.rtc.RtcEngine;

public class CourseGroupOtherItem extends BaseCourseGroupItem {
    /** 用户下方的控制 */
    private RelativeLayout rlCourseItemCtrl;
    /** 用户下方的加载中 */
    private TextView tvCourseItemLoad;
    private boolean enableVideo = true;
    private boolean enableAudio = true;

    public CourseGroupOtherItem(Context context, TeamMemberEntity entity, WorkerThread workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_h5_courseware_group_people;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
        root.setBackgroundResource(R.drawable.app_zbhd_shipingkuang);
        rlCourseItemCtrl = root.findViewById(R.id.rl_livevideo_course_item_ctrl);
        tvCourseItemLoad = root.findViewById(R.id.tv_livevideo_course_item_load);
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        ivCourseItemVideoHead.setVisibility(View.GONE);
        rlCourseItemVideo.addView(surfaceV, 0);
        rlCourseItemCtrl.setVisibility(View.VISIBLE);
        tvCourseItemLoad.setVisibility(View.GONE);
    }

    @Override
    public void onUserJoined() {
        super.onUserJoined();
    }

    public void onUserOffline() {
        ivCourseItemVideoHead.setVisibility(View.VISIBLE);
        rlCourseItemCtrl.setVisibility(View.GONE);
        tvCourseItemLoad.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindListener() {
        ivCourseItemVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableVideo = !enableVideo;
                    if (enableVideo) {
                        ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
                    } else {
                        ivCourseItemVideo.setImageResource(VIDEO_RES[1]);
                    }
                    rtcEngine.muteRemoteVideoStream(uid, enableVideo);
//                    if (onVideoAudioClick != null) {
//                        onVideoAudioClick.onVideoClick(enableVideo);
//                    }
                }
            }
        });
        ivCourseItemAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableAudio = !enableAudio;
                    rtcEngine.muteRemoteAudioStream(uid, enableAudio);
//                    if (onVideoAudioClick != null) {
//                        onVideoAudioClick.onAudioClick(enableAudio);
//                    }
                }
            }
        });
    }

    public TeamMemberEntity getEntity() {
        return entity;
    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).into(ivCourseItemVideoHead);
        ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
        ivCourseItemAudio.setImageResource(AUDIO_RES[2]);
    }

    public void onVolumeUpdate(int volume) {

    }

    public void onOtherDis(int type, boolean enable) {
        logger.d("onOtherDis:uid=" + uid + ",type=" + type + ",enable=" + enable);
        if (type == GroupGameConfig.OPERATION_VIDEO) {
            if (enable) {
                if (enableVideo) {
                    ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
                } else {
                    ivCourseItemVideo.setImageResource(VIDEO_RES[1]);
                }
            } else {
                ivCourseItemVideo.setImageResource(VIDEO_RES[0]);
            }
            ivCourseItemVideo.setEnabled(enable);
        } else if (type == GroupGameConfig.OPERATION_AUDIO) {
            if (enable) {
                if (enableAudio) {
                    ivCourseItemAudio.setImageResource(AUDIO_RES[2]);
                } else {
                    ivCourseItemAudio.setImageResource(AUDIO_RES[1]);
                }
            } else {
                ivCourseItemAudio.setImageResource(AUDIO_RES[0]);
            }
            ivCourseItemAudio.setEnabled(enable);
        }
    }

    public void onScene() {
        tvCourseItemFire.setText("" + entity.energy);
    }
}

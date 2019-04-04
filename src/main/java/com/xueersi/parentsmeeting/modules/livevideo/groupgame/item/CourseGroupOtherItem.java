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

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
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
                    rtcEngine.muteRemoteVideoStream(uid, enableVideo);
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

    private Handler handler = new Handler(Looper.getMainLooper());


    public void onVolumeUpdate(int volume) {

    }

    public void onOtherDis(int type, boolean enable) {
        if (type == 1) {
            if (enable) {
                ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
            } else {
                ivCourseItemVideo.setImageResource(VIDEO_RES[0]);
            }
            ivCourseItemVideo.setEnabled(enable);
        } else if (type == 2) {
            if (enable) {
                ivCourseItemAudio.setImageResource(AUDIO_RES[2]);
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

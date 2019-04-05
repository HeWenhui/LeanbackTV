package com.xueersi.parentsmeeting.modules.livevideo.groupgame.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

import io.agora.rtc.RtcEngine;

public class CourseGroupMyItem extends BaseCourseGroupItem {
    /** 自己头像禁用 */
    private ImageView ivCourseItemVideoDis;
    private boolean enableVideo = true;
    private boolean enableAudio = true;
    private int progress = 0;

    public CourseGroupMyItem(Context context, TeamMemberEntity entity, WorkerThread workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_h5_courseware_group_my;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
        root.setBackgroundResource(R.drawable.app_zbhd_shipingkuang);
        ivCourseItemVideoDis = root.findViewById(R.id.iv_livevideo_course_item_audio_dis);
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
                    rtcEngine.enableLocalVideo(enableVideo);
                    if (onVideoAudioClick != null) {
                        onVideoAudioClick.onVideoClick(enableVideo);
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
                    if (enableAudio) {
                        ivCourseItemVideoDis.setVisibility(View.GONE);
                        rtcEngine.enableAudio();
                    } else {
                        rtcEngine.disableAudio();
                        final LottieAnimationView animationView = (LottieAnimationView) ivCourseItemAudio;
                        stopRun.animationView = animationView;
                        stopRun.startProgress = voiceStartFrame;
                        startRun.startProgress = 0;
                        handler.removeCallbacks(startRun);
                        handler.removeCallbacks(progRun);
                        if (progress > 0) {
                            handler.postDelayed(stopRun, 10);
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ivCourseItemVideoDis.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                    }
                    if (onVideoAudioClick != null) {
                        onVideoAudioClick.onAudioClick(enableAudio);
                    }
                }
            }
        });
    }

    public TeamMemberEntity getEntity() {
        return entity;
    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        rlCourseItemName.setText(entity.name);
        tvCourseItemFire.setText("" + entity.energy);
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).into(ivCourseItemVideoHead);
        String lottieResPath = "group_game_mult/images";
        String lottieJsonPath = "group_game_mult/data.json";
        final LottieEffectInfo lottieEffectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        final LottieAnimationView animationView = (LottieAnimationView) ivCourseItemAudio;
        animationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext), "group_game_mult");
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return lottieEffectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper());
    private StartRun startRun = new StartRun();
    private StopRun stopRun = new StopRun();
    private ProgRun progRun = new ProgRun();

    class StartRun implements Runnable {
        private int startProgress = 0;
        LottieAnimationView animationView;

        @Override
        public void run() {
            if (startProgress < voiceStartFrame) {
                startProgress += 1;
                progress = startProgress;
                animationView.setFrame(startProgress);
                handler.postDelayed(this, 10);
            }
        }
    }

    class StopRun implements Runnable {
        private int startProgress = voiceStartFrame;
        LottieAnimationView animationView;

        @Override
        public void run() {
            if (startProgress >= 0) {
                startProgress -= 1;
                progress = startProgress;
                animationView.setFrame(startProgress);
                handler.postDelayed(this, 10);
            }
        }
    }

    class ProgRun implements Runnable {
        private int startProgress = voiceStartFrame;
        private int stopProgress = 0;
        LottieAnimationView animationView;

        @Override
        public void run() {
            int progress;
            if (startProgress > stopProgress) {
                startProgress -= 1;
                progress = startProgress;
            } else if (startProgress < stopProgress) {
                startProgress += 1;
                progress = startProgress;
            } else {
                return;
            }
            animationView.setFrame(progress);
            handler.postDelayed(this, 100);
        }
    }

    public void onVolumeUpdate(int volume) {
        if (!enableAudio) {
            return;
        }
        if (ivCourseItemVideoDis.getVisibility() != View.GONE) {
            ivCourseItemVideoDis.setVisibility(View.GONE);
        }
        final LottieAnimationView animationView = (LottieAnimationView) ivCourseItemAudio;
        if (progress < voiceStartFrame) {
            startRun.animationView = animationView;
            handler.removeCallbacks(stopRun);
            handler.postDelayed(startRun, 10);
        } else {
            progRun.animationView = animationView;
            if (voiceMaxFrame == 0) {
                voiceMaxFrame = (int) animationView.getMaxFrame();
            }
            progress = (int) (voiceStartFrame + (float) (volume * voiceMaxFrame) / 30.0f);
            progRun.stopProgress = progress;
            handler.postDelayed(progRun, 30);
        }
    }

    public void onOtherDis(int type, boolean enable) {

    }

    public void onScene() {
        tvCourseItemFire.setText("" + entity.energy);
    }
}

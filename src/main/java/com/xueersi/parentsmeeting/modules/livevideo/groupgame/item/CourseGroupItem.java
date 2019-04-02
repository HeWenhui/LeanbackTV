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
    private Context mContext;
    private int progress = 0;
    public static int voiceStartFrame = 14;
    public static int voiceMaxFrame = 0;

    public CourseGroupItem(Context context, WorkerThread workerThread, int uid, boolean isMe) {
        this.mContext = context;
        this.workerThread = workerThread;
        this.uid = uid;
        this.isMe = isMe;
    }

    @Override
    public int getLayoutResId() {
        if (isMe) {
            return R.layout.item_livevideo_h5_courseware_group_my;
        }
        return R.layout.item_livevideo_h5_courseware_group_people;
    }

    @Override
    public void initViews(View root) {
        rlCourseItemVideo = root.findViewById(R.id.rl_livevideo_course_item_video);
        ivCourseItemVideoHead = root.findViewById(R.id.iv_livevideo_course_item_video_head);
        rlCourseItemName = root.findViewById(R.id.rl_livevideo_course_item_name);
        ivCourseItemVideo = root.findViewById(R.id.iv_livevideo_course_item_video);
        ivCourseItemAudio = root.findViewById(R.id.iv_livevideo_course_item_audio);
        if (uid == -1) {
            ImageView imageView = new ImageView(root.getContext());
            imageView.setImageResource(R.drawable.pc_zbhd_shipingkuang_ufo);
            ((ViewGroup) root).addView(imageView);
        } else {
            root.setBackgroundResource(R.drawable.app_zbhd_shipingkuang);
        }
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
        if (uid != -1) {
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
                                final LottieAnimationView animationView = (LottieAnimationView) ivCourseItemAudio;
                                stopRun.animationView = animationView;
                                stopRun.startProgress = voiceStartFrame;
                                startRun.startProgress = 0;
                                handler.removeCallbacks(startRun);
                                handler.removeCallbacks(progRun);
                                if (progress > 0) {
                                    handler.postDelayed(stopRun, 10);
                                }
                            }
                        } else {
                            rtcEngine.muteRemoteAudioStream(uid, enableAudio);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        rlCourseItemName.setText(entity.name);
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).into(ivCourseItemVideoHead);
        if (isMe) {
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
        } else {
            ivCourseItemVideo.setImageResource(VIDEO_RES[0]);
            ivCourseItemAudio.setImageResource(AUDIO_RES[0]);
        }
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

    public void onScene() {

    }
}

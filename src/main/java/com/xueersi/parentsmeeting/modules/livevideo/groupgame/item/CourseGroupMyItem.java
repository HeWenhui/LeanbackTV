package com.xueersi.parentsmeeting.modules.livevideo.groupgame.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

import java.io.IOException;

import io.agora.rtc.RtcEngine;

public class CourseGroupMyItem extends BaseCourseGroupItem {
    /** 自己头像禁用 */
    private ImageView ivCourseItemVideoDis;
    private RelativeLayout rlVideoTip;
    private boolean enableVideo = true;
    private boolean enableAudio = true;
    private int progress = 0;
    public static int voiceStartFrame = 14;
    public static int voiceMaxFrame = 0;
    private int oldEnergy;
    private long videoStartTime;
    private long audioStartTime;
    private String lottieResPath = "group_game_mult/images";
    private String lottieJsonPath = "group_game_mult/data.json";
    private Bitmap bitmap6;
    //    private Bitmap bitmap7;
    private Bitmap bitmap7Small;
    private Bitmap bitmap8;
    private Bitmap bitmap9;
    LottieEffectInfo lottieEffectInfo;
    OpenImageAssetDelegate openImageAssetDelegate;
    CloseImageAssetDelegate closeImageAssetDelegate;

    public CourseGroupMyItem(Context context, TeamMemberEntity entity, WorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
        oldEnergy = entity.energy;
        videoStartTime = System.currentTimeMillis();
        audioStartTime = System.currentTimeMillis();
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
        rlVideoTip = root.findViewById(R.id.rl_livevideo_course_item_video_tip);
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        rlCourseItemVideoHead.setVisibility(View.GONE);
        boolean remove = false;
        if (rlCourseItemVideo.getChildCount() > 0) {
            View view = rlCourseItemVideo.getChildAt(0);
            if (view instanceof SurfaceView) {
                rlCourseItemVideo.removeView(view);
                remove = true;
            }
        }
        mLogtf.d("doRenderRemoteUi:remove=" + remove + ",uid=" + uid);
        rlCourseItemVideo.addView(surfaceV, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceV.setOutlineProvider(new TextureVideoViewOutlineProvider(headCornerSize));
            surfaceV.setClipToOutline(true);
        }
    }

    public void onUserOffline() {
        mLogtf.d("onUserOffline:uid=" + uid);
        rlCourseItemVideoHead.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindListener() {
        ivCourseItemVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableVideo = !enableVideo;
                    workerThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            rtcEngine.enableLocalVideo(enableVideo);
                        }
                    });
                    if (enableVideo) {
                        rlCourseItemVideoHead.setVisibility(View.GONE);
                        videoStartTime = System.currentTimeMillis();
                        ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
                    } else {
                        rlCourseItemVideoHead.setVisibility(View.VISIBLE);
                        ivCourseItemVideo.setImageResource(VIDEO_RES[1]);
                        videoTime += (System.currentTimeMillis() - videoStartTime);
                    }
                    if (onVideoAudioClick != null) {
                        onVideoAudioClick.onVideoClick(enableVideo);
                    }
                }
            }
        });
        ivCourseItemAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableAudio = !enableAudio;
                    final LottieAnimationView animationView = (LottieAnimationView) ivCourseItemAudio;
                    if (enableAudio) {
                        ivCourseItemVideoDis.setVisibility(View.GONE);
                        workerThread.execute(new Runnable() {
                            @Override
                            public void run() {
//                                rtcEngine.enableAudio();
                                rtcEngine.muteLocalAudioStream(false);
                            }
                        });
//                        ivCourseItemAudio.setImageResource(AUDIO_RES[2]);
                        audioStartTime = System.currentTimeMillis();
//                        Bitmap bitmap1 = animationView.updateBitmap("image_7", bitmap6);
//                        Bitmap bitmap2 = animationView.updateBitmap("image_9", bitmap8);
//                        logger.d("enableAudio(true):bitmap1=null?" + (bitmap1 == null) + ",bitmap2=null?" + (bitmap2 == null));
                        createOpen(animationView);
                    } else {
                        audioTime += (System.currentTimeMillis() - audioStartTime);
                        workerThread.execute(new Runnable() {
                            @Override
                            public void run() {
//                                rtcEngine.disableAudio();
                                rtcEngine.muteLocalAudioStream(true);
                            }
                        });
                        stopRun.animationView = animationView;
                        stopRun.startProgress = voiceStartFrame;
                        startRun.startProgress = 0;
                        createClose(animationView);
                        handler.removeCallbacks(startRun);
                        handler.removeCallbacks(progRun);
                        if (progress > 0) {
                            handler.postDelayed(stopRun, 10);
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                ivCourseItemVideoDis.setImageResource(AUDIO_RES[1]);
//                                ivCourseItemVideoDis.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
//                        Bitmap bitmap1 = animationView.updateBitmap("image_7", bitmap7);
//                        Bitmap bitmap2 = animationView.updateBitmap("image_9", bitmap9);
//                        logger.d("enableAudio(false):bitmap1=null?" + (bitmap1 == null) + ",bitmap2=null?" + (bitmap2 == null));
                        XESToastUtils.showToast(mContext, "小伙伴听不到你的声音啦，但不影响答题哦");
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
    public long getVideoTime() {
        long oldVideoTime = videoTime;
        if (enableVideo) {
            videoTime += (System.currentTimeMillis() - videoStartTime);
        }
        logger.d("getVideoTime:oldVideoTime=" + oldVideoTime + ",videoTime=" + videoTime);
        return super.getVideoTime();
    }

    @Override
    public long getAudioTime() {
        long oldAudioTime = audioTime;
        if (enableAudio) {
            audioTime += (System.currentTimeMillis() - audioStartTime);
        }
        logger.d("getAudioTime:oldAudioTime=" + oldAudioTime + ",audioTime=" + audioTime);
        return super.getAudioTime();
    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        rlCourseItemName.setText(entity.name);
        tvCourseItemFire.setText("" + entity.energy);
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).into(ivCourseItemVideoHead);
        createBitmap6();
        createBitmap7Small();
        createBitmap8();
        createBitmap9();
        lottieEffectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        final LottieAnimationView animationView = (LottieAnimationView) ivCourseItemAudio;
        createOpen(animationView);
        boolean have = XesPermission.checkPermission(mContext, PermissionConfig.PERMISSION_CODE_CAMERA);
        if (have) {
            ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
        } else {
            ivCourseItemVideo.setEnabled(false);
            ivCourseItemVideo.setImageResource(VIDEO_RES[0]);
        }
    }

    private void createBitmap6() {
        try {
            if (bitmap6 != null && !bitmap6.isRecycled()) {
                return;
            }
            Bitmap bitmap7 = BitmapFactory.decodeStream(mContext.getAssets().open(lottieResPath + "/img_7.png"));
            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(lottieResPath + "/img_6.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap7.getWidth(), bitmap7.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(bitmap, (bitmap7.getWidth() - bitmap.getWidth()) / 2, (bitmap7.getHeight() - bitmap.getHeight()) / 2, null);
            bitmap.recycle();
            bitmap7.recycle();
            bitmap6 = creatBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createBitmap7Small() {
        try {
            if (bitmap7Small != null && !bitmap7Small.isRecycled()) {
                return;
            }
            Bitmap bitmap6 = BitmapFactory.decodeStream(mContext.getAssets().open(lottieResPath + "/img_6.png"));
            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(lottieResPath + "/img_7.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap6.getWidth(), bitmap6.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(bitmap, (bitmap6.getWidth() - bitmap.getWidth()) / 2, (bitmap6.getHeight() - bitmap.getHeight()) / 2, null);
            bitmap.recycle();
            bitmap6.recycle();
            bitmap7Small = creatBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBitmap8() {
        if (bitmap8 != null && !bitmap8.isRecycled()) {
            return;
        }
        try {
            bitmap8 = BitmapFactory.decodeStream(mContext.getAssets().open(lottieResPath + "/img_8.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBitmap9() {
        if (bitmap9 != null && !bitmap9.isRecycled()) {
            return;
        }
        try {
            bitmap9 = BitmapFactory.decodeStream(mContext.getAssets().open(lottieResPath + "/img_9.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createOpen(LottieAnimationView animationView) {
        createBitmap6();
        createBitmap8();
        animationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext), "group_game_mult_open");
        openImageAssetDelegate = new OpenImageAssetDelegate(animationView, lottieEffectInfo);
        animationView.setImageAssetDelegate(openImageAssetDelegate);
    }

    private void createClose(LottieAnimationView animationView) {
        createBitmap7Small();
        createBitmap9();
        animationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext), "group_game_mult_close");
        closeImageAssetDelegate = new CloseImageAssetDelegate(animationView, lottieEffectInfo);
        animationView.setImageAssetDelegate(closeImageAssetDelegate);
    }

    class OpenImageAssetDelegate implements ImageAssetDelegate {
        LottieAnimationView animationView;
        LottieEffectInfo lottieEffectInfo;

        public OpenImageAssetDelegate(LottieAnimationView animationView, LottieEffectInfo lottieEffectInfo) {
            this.animationView = animationView;
            this.lottieEffectInfo = lottieEffectInfo;
        }

        @Override
        public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
            logger.d("Open:fetchBitmap:id=" + lottieImageAsset.getId());
            if (lottieImageAsset.getId().equals("image_7")) {
                return bitmap6;
            }
            if (lottieImageAsset.getId().equals("image_9")) {
                return bitmap8;
            }
            return lottieEffectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                    lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                    mContext);
        }
    }

    class CloseImageAssetDelegate implements ImageAssetDelegate {
        LottieAnimationView animationView;
        LottieEffectInfo lottieEffectInfo;

        public CloseImageAssetDelegate(LottieAnimationView animationView, LottieEffectInfo lottieEffectInfo) {
            this.animationView = animationView;
            this.lottieEffectInfo = lottieEffectInfo;
        }

        @Override
        public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
            logger.d("Close:fetchBitmap:id=" + lottieImageAsset.getId());
            if (lottieImageAsset.getId().equals("image_6")) {
                return bitmap7Small;
            }
            if (lottieImageAsset.getId().equals("image_8")) {
                return bitmap9;
            }
            return lottieEffectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                    lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                    mContext);
        }
    }

    @Override
    public void onDestory() {
        if (bitmap6 != null) {
            bitmap6.recycle();
        }
        if (bitmap7Small != null) {
            bitmap7Small.recycle();
        }
        if (bitmap8 != null) {
            bitmap8.recycle();
        }
        if (bitmap9 != null) {
            bitmap9.recycle();
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
//        if (ivCourseItemVideoDis.getVisibility() != View.GONE) {
//            ivCourseItemVideoDis.setVisibility(View.GONE);
//        }
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

    @Override
    public void onOpps() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_h5_courseware_group_tip_opps, rlVideoTip, false);
        TextView tv_livevideo_course_item_video_energy = view.findViewById(R.id.tv_livevideo_course_item_video_energy);
        rlVideoTip.addView(view);
        tv_livevideo_course_item_video_energy.setText("Oops");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlVideoTip.removeView(view);
            }
        }, 1000);
    }

    public void onScene(String method) {
        //和旧的不相等，才会提示
        mLogtf.d("onScene:method=" + method + "," + oldEnergy + ",energy=" + entity.energy);
        if (entity.energy - oldEnergy != 0) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_h5_courseware_group_tip_energy, rlVideoTip, false);
            TextView tv_livevideo_course_item_video_energy = view.findViewById(R.id.tv_livevideo_course_item_video_energy);
            rlVideoTip.addView(view);
            tv_livevideo_course_item_video_energy.setText("+" + (entity.energy - oldEnergy));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rlVideoTip.removeView(view);
                }
            }, 1000);
        }
//        rlVideoTip.setVisibility(View.VISIBLE);
        tvCourseItemFire.setText("" + entity.energy);
        oldEnergy = entity.energy;
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.groupgame.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.config.GroupGameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.AgoraUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.TextureVideoViewOutlineProvider;

import io.agora.rtc.RtcEngine;

public class CourseGroupOtherItem extends BaseCourseGroupItem {
    /** 用户下方的控制 */
    private RelativeLayout rlCourseItemCtrl;
    /** 用户下方的加载中 */
    private TextView tvCourseItemLoad;
    private boolean enableVideo = true;
    private boolean enableAudio = true;
    private long videoStartTime;
    private long audioStartTime;
    private boolean onLine = false;
    private int state = 0;
    private int oldEnergy;

    public CourseGroupOtherItem(Context context, TeamMemberEntity entity, WorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
        oldEnergy = entity.getEnergy();
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
        state = AgoraUtils.REMOTE_VIDEO_STATE_STARTING;
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
        rlCourseItemCtrl.setVisibility(View.VISIBLE);
//        tvCourseItemLoad.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceV.setOutlineProvider(new TextureVideoViewOutlineProvider(headCornerSize));
            surfaceV.setClipToOutline(true);
        }
    }

    @Override
    public void onRemoteVideoStateChanged(int state) {
        this.state = state;
        if (AgoraUtils.isPlay(state) && enableVideo) {
            ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
            rlCourseItemVideoHead.setVisibility(View.GONE);
        } else {
            rlCourseItemVideoHead.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserJoined() {
        onLine = true;
        mLogtf.d("onUserJoined" + uid);
        if (videoTime == 0) {
            videoStartTime = System.currentTimeMillis();
        }
        if (audioTime == 0) {
            audioStartTime = System.currentTimeMillis();
        }
//        tvCourseItemLoad.setText("获取中");
        rlCourseItemCtrl.setVisibility(View.VISIBLE);
        tvCourseItemLoad.setVisibility(View.GONE);
    }

    public void onUserOffline() {
        onLine = false;
        mLogtf.d("onUserOffline:uid=" + uid);
        rlCourseItemVideoHead.setVisibility(View.VISIBLE);
        rlCourseItemCtrl.setVisibility(View.GONE);
        tvCourseItemLoad.setText("已离线");
        tvCourseItemLoad.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindListener() {
        ivCourseItemVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableVideo = !enableVideo;
                    if (enableVideo) {
                        if (AgoraUtils.isPlay(state)) {
                            ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
                            rlCourseItemVideoHead.setVisibility(View.GONE);
                        }
                    } else {
                        ivCourseItemVideo.setImageResource(VIDEO_RES[1]);
                        rlCourseItemVideoHead.setVisibility(View.VISIBLE);
                    }
                    workerThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            rtcEngine.muteRemoteVideoStream(uid, !enableVideo);
                        }
                    });
//                    if (onVideoAudioClick != null) {
//                        onVideoAudioClick.onVideoClick(enableVideo);
//                    }
                }
            }
        });
        ivCourseItemAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RtcEngine rtcEngine = workerThread.getRtcEngine();
                if (rtcEngine != null) {
                    enableAudio = !enableAudio;
                    if (enableAudio) {
                        ivCourseItemAudio.setImageResource(AUDIO_RES[2]);
                    } else {
                        ivCourseItemAudio.setImageResource(AUDIO_RES[1]);
                    }
                    workerThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            rtcEngine.muteRemoteAudioStream(uid, !enableAudio);
                        }
                    });
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
    public void updateViews(final TeamMemberEntity entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        final String headurl = entity.headurl;
//        ImageLoader.with(ContextManager.getContext()).load(headurl).rectRoundCorner(headCornerSize).into(ivCourseItemVideoHead, new SingleConfig.BitmapListener() {
//            @Override
//            public void onSuccess(Drawable drawable) {
//                mLogtf.d("onSuccess:uid=" + uid + ",headurl=" + headurl);
//            }
//
//            @Override
//            public void onFail() {
//                mLogtf.d("onFail:uid=" + uid + ",headurl=" + headurl);
//            }
//        });
        ImageLoader.with(ContextManager.getContext()).load(headurl).rectRoundCorner(headCornerSize / 2).into(ivCourseItemVideoHead);
//        ImageLoader.with(ContextManager.getContext()).load(headurl).into(ivCourseItemVideoHead);
        ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
        ivCourseItemAudio.setImageResource(AUDIO_RES[2]);
    }

    public void onVolumeUpdate(int volume) {

    }

    @Override
    public long getVideoTime() {
        long oldVideoTime = videoTime;
        if (onLine && enableVideo) {
            videoTime += (System.currentTimeMillis() - videoStartTime);
        }
        logger.d("getVideoTime:oldVideoTime=" + oldVideoTime + ",videoTime=" + videoTime);
        return super.getVideoTime();
    }

    @Override
    public long getAudioTime() {
        long oldAudioTime = audioTime;
        if (onLine && enableAudio) {
            audioTime += (System.currentTimeMillis() - audioStartTime);
        }
        logger.d("getAudioTime:oldAudioTime=" + oldAudioTime + ",audioTime=" + audioTime);
        return super.getAudioTime();
    }

    public void onOtherDis(int type, boolean enable) {
        logger.d("onOtherDis:uid=" + uid + ",type=" + type + ",enable=" + enable);
        if (type == GroupGameConfig.OPERATION_VIDEO) {
            if (enable) {
                if (enableVideo) {
                    ivCourseItemVideo.setImageResource(VIDEO_RES[2]);
//                    rlCourseItemVideoHead.setVisibility(View.GONE);
                } else {
                    ivCourseItemVideo.setImageResource(VIDEO_RES[1]);
                }
                videoStartTime = System.currentTimeMillis();
            } else {
                ivCourseItemVideo.setImageResource(VIDEO_RES[0]);
                if (videoStartTime != 0) {
                    videoTime += (System.currentTimeMillis() - videoStartTime);
                }
                rlCourseItemVideoHead.setVisibility(View.VISIBLE);
            }
            ivCourseItemVideo.setEnabled(enable);
        } else if (type == GroupGameConfig.OPERATION_AUDIO) {
            if (enable) {
                if (enableAudio) {
                    ivCourseItemAudio.setImageResource(AUDIO_RES[2]);
                } else {
                    ivCourseItemAudio.setImageResource(AUDIO_RES[1]);
                }
                audioStartTime = System.currentTimeMillis();
            } else {
                ivCourseItemAudio.setImageResource(AUDIO_RES[0]);
                if (audioStartTime != 0) {
                    audioTime += (System.currentTimeMillis() - audioStartTime);
                }
            }
            ivCourseItemAudio.setEnabled(enable);
        }
    }

    public void onScene(String method) {
        tvCourseItemFire.setText("" + entity.getEnergy());
        try {
            mLogtf.d("onScene:method=" + method + ",energy=" + oldEnergy + "," + entity.getEnergy());
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        oldEnergy = entity.getEnergy();
    }

    @Override
    public void onPause() {
        super.onPause();
        final RtcEngine rtcEngine = workerThread.getRtcEngine();
        if (rtcEngine != null) {
            workerThread.execute(new Runnable() {
                @Override
                public void run() {
                    rtcEngine.muteRemoteAudioStream(uid, true);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final RtcEngine rtcEngine = workerThread.getRtcEngine();
        if (rtcEngine != null) {
            workerThread.execute(new Runnable() {
                @Override
                public void run() {
                    rtcEngine.muteRemoteAudioStream(uid, !enableAudio);
                }
            });
        }
    }
}

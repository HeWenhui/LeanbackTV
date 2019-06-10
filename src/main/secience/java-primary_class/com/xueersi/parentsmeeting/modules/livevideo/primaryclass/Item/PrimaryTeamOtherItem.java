package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;

import com.xes.ps.rtcstream.RTCEngine;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config.PrimaryClassConfig;

public class PrimaryTeamOtherItem extends BasePrimaryTeamPeopleItem {

    public PrimaryTeamOtherItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_other_video;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
        primaryClassView.decorateItemOther(root);
    }

    @Override
    public void bindListener() {
        super.bindListener();
//        iv_livevideo_primary_team_voice_open.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (audioStatus) {
//                    enableAudio = !enableAudio;
//                    if (enableAudio) {
//                        iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_normal);
//                    } else {
//                        voiceImageView.reset();
//                        iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_zero_normal);
//                    }
//                }
//                cloudWorkerThreadPool.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (audioStatus) {
//                            cloudWorkerThreadPool.getRtcEngine().enableRemoteAudio(uid, !enableAudio);
//                        }
//                    }
//                });
//            }
//        });
        tv_livevideo_primary_team_people_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNameClick.onNameClick(entity, tv_livevideo_primary_team_people_name);
            }
        });
    }

    @Override
    public void updateViews(TeamMate entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        tv_livevideo_primary_team_people_name.setText(entity.getName());
    }

    @Override
    public void onVideo() {
        entity.setLook(!entity.isLook());
//        if (!entity.isLook()) {
//            haveVideo = false;
//        }
        if (videoStatus && entity.isLook()) {
            cloudWorkerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                    if (mRtcEngine != null) {
                        mRtcEngine.muteRemoteVideo(uid, false);
                    }
                }
            });
        } else {
            cloudWorkerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                    if (mRtcEngine != null) {
                        mRtcEngine.muteRemoteVideo(uid, true);
                    }
                }
            });
        }
        if (audioStatus && entity.isLook()) {
            cloudWorkerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                    if (mRtcEngine != null) {
                        mRtcEngine.enableRemoteAudio(uid, false);
                    }
                }
            });
        } else {
            cloudWorkerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                    if (mRtcEngine != null) {
                        mRtcEngine.enableRemoteAudio(uid, true);
                    }
                }
            });
        }
        setStatus();
    }

    private void setStatus() {
        mLogtf.d("setStatus:uid=" + uid + ",isLook=" + entity.isLook() + ",videoStatus=" + videoStatus + ",audioStatus=" + audioStatus + ",haveVideo=" + haveVideo);
        if (entity.isLook()) {
            if (videoStatus && haveVideo) {
                rl_livevideo_course_item_video_ufo.setVisibility(View.GONE);
                cl_livevideo_course_item_video.setVisibility(View.VISIBLE);
            }
            iv_livevideo_primary_team_voice_open.setVisibility(View.VISIBLE);
            if (audioStatus) {
                voiceImageView.setVisibility(View.VISIBLE);
                iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_normal);
            } else {
                iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_zero_normal);
            }
        } else {
            rl_livevideo_course_item_video_ufo.setVisibility(View.VISIBLE);
            cl_livevideo_course_item_video.setVisibility(View.GONE);
            iv_livevideo_primary_team_voice_open.setVisibility(View.GONE);
            voiceImageView.setVisibility(View.GONE);
        }
    }

    private boolean haveVideo = false;

    @Override
    public void doRenderRemoteUi(SurfaceView surfaceV) {
        super.doRenderRemoteUi(surfaceV);
        rl_livevideo_course_item_video_ufo.setVisibility(View.GONE);
        cl_livevideo_course_item_video.setVisibility(View.VISIBLE);
        haveVideo = true;
    }

    @Override
    public void didOfflineOfUid(String method, final boolean join) {
        mLogtf.d("didOfflineOfUid:uid=" + uid + ",method=" + method + ",join=" + join + ",haveVideo=" + haveVideo);
        handler.post(new Runnable() {
            @Override
            public void run() {
                rl_livevideo_course_item_video_off.setVisibility(join ? View.GONE : View.VISIBLE);
                if (join) {
                    if (!haveVideo) {
                        rl_livevideo_course_item_video_ufo.setVisibility(View.VISIBLE);
                    }
                    if (audioStatus) {
                        handler.postDelayed(noMicRun, 2000);
                    }
                } else {
                    haveVideo = false;
                }
            }
        });
//        if (join && entity.isLook()) {
//            cl_livevideo_course_item_video.setVisibility(View.VISIBLE);
//            iv_livevideo_primary_team_voice_open.setVisibility(View.VISIBLE);
//        } else {
//            cl_livevideo_course_item_video.setVisibility(View.GONE);
//            iv_livevideo_primary_team_voice_open.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onReport() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_primary_class_team_item_report, rl_livevideo_primary_team_tip, false);
        rl_livevideo_primary_team_tip.addView(view);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_livevideo_primary_team_tip.removeView(view);
            }
        }, 2000);
    }

    /** 麦克风故障 */
    private Runnable noMicRun = new Runnable() {
        @Override
        public void run() {
            tv_livevideo_primary_team_nomic.setVisibility(View.VISIBLE);
        }
    };

    public void remotefirstAudioRecvWithUid(int uid) {
        handler.removeCallbacks(noMicRun);
    }

    @Override
    public void onOtherDis(int type, final boolean enable) {
        super.onOtherDis(type, enable);
        if (type == PrimaryClassConfig.MMTYPE_VIDEO) {
            mLogtf.d("onOtherDis:uid=" + uid + ",MMTYPE_VIDEO=" + entity.isLook());
            if (entity.isLook()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (enable) {
                            rl_livevideo_course_item_video_ufo.setVisibility(View.GONE);
                            cl_livevideo_course_item_video.setVisibility(View.VISIBLE);
                        } else {
                            rl_livevideo_course_item_video_ufo.setVisibility(View.VISIBLE);
                            cl_livevideo_course_item_video.setVisibility(View.GONE);
                        }
                    }
                });
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                        if (mRtcEngine != null) {
                            mRtcEngine.muteRemoteVideo(uid, !enable);
                        }
                    }
                });
            } else {
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                        if (mRtcEngine != null) {
                            mRtcEngine.muteRemoteVideo(uid, true);
                        }
                    }
                });
            }
        } else {
            mLogtf.d("onOtherDis:uid=" + uid + ",MMTYPE_AUDIO=" + entity.isLook());
            if (entity.isLook()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        voiceImageView.reset();
                        if (enable) {
                            iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_normal);
                            voiceImageView.setVisibility(View.VISIBLE);
                        } else {
                            iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_zero_normal);
                            voiceImageView.setVisibility(View.GONE);
                        }
                    }
                });
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                        if (mRtcEngine != null) {
                            mRtcEngine.enableRemoteAudio(uid, !enable);
                        }
                    }
                });
            } else {
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                        if (mRtcEngine != null) {
                            mRtcEngine.enableRemoteAudio(uid, false);
                        }
                    }
                });
            }
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                setStatus();
            }
        });
    }
}

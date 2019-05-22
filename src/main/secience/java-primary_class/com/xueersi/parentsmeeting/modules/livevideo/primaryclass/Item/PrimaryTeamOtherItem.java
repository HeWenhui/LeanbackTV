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
    private boolean enableVideo = true;
    private boolean enableAudio = true;

    public PrimaryTeamOtherItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_other_video;
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        super.doRenderRemoteUi(surfaceV);
    }

    @Override
    public void bindListener() {
        super.bindListener();
        iv_livevideo_primary_team_voice_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (audioStatus) {
                            enableAudio = !enableAudio;
                            cloudWorkerThreadPool.getRtcEngine().enableRemoteAudio(uid, enableAudio);
                        }
                    }
                });
            }
        });
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
        if (videoStatus) {
            if (entity.isLook()) {
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                        if (mRtcEngine != null) {
                            mRtcEngine.muteRemoteVideo(uid, true);
                        }
                    }
                });
            } else {
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                        if (mRtcEngine != null) {
                            mRtcEngine.muteRemoteVideo(uid, false);
                        }
                    }
                });
            }
        }
        if (audioStatus) {
            if (entity.isLook()) {
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
        }
        mLogtf.d("onVideo:isLook=" + entity.isLook());
        if (entity.isLook()) {
            if (videoStatus) {
                rl_livevideo_course_item_video_ufo.setVisibility(View.GONE);
                cl_livevideo_course_item_video.setVisibility(View.VISIBLE);
            }
            iv_livevideo_primary_team_voice_open.setVisibility(View.VISIBLE);
            voiceImageView.setVisibility(View.VISIBLE);
        } else {
            rl_livevideo_course_item_video_ufo.setVisibility(View.VISIBLE);
            cl_livevideo_course_item_video.setVisibility(View.GONE);
            iv_livevideo_primary_team_voice_open.setVisibility(View.GONE);
            voiceImageView.setVisibility(View.GONE);
        }
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

    @Override
    public void onOtherDis(int type, final boolean enable) {
        super.onOtherDis(type, enable);
        if (type == PrimaryClassConfig.MMTYPE_VIDEO) {
            mLogtf.d("onOtherDis:MMTYPE_VIDEO=" + entity.isLook());
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
            }
        } else {
            cloudWorkerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                    if (mRtcEngine != null) {
                        mRtcEngine.enableRemoteAudio(uid, !enable);
                    }
                }
            });
        }
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.xes.ps.rtcstream.RTCEngine;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config.PrimaryClassConfig;

public class PrimaryTeamMyItem extends BasePrimaryTeamPeopleItem {
    private boolean enableVideo = true;
    private boolean enableAudio = true;
    private int totalEnergy;
    private TextView iv_livevideo_primary_team_energy;

    public PrimaryTeamMyItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
        totalEnergy = entity.getEnergy();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_video;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
        iv_livevideo_primary_team_energy = root.findViewById(R.id.tv_livevideo_primary_team_energy);
    }

    @Override
    public void bindListener() {
        super.bindListener();
        iv_livevideo_primary_team_voice_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioStatus) {
                    enableAudio = !enableAudio;
                    if (enableAudio) {
                        iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_normal);
                    } else {
                        voiceImageView.reset();
                        iv_livevideo_primary_team_voice_open.setImageResource(R.drawable.xuesheng_icon_maikefeng_zero_normal);
                    }
                }
                cloudWorkerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (audioStatus) {
                            cloudWorkerThreadPool.getRtcEngine().muteLocalAudio(!enableAudio);
                        }
                    }
                });
            }
        });
//        tv_livevideo_primary_team_people_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onNameClick.onNameClick(entity, tv_livevideo_primary_team_people_name);
//            }
//        });
    }

    @Override
    public void updateViews(TeamMate entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        tv_livevideo_primary_team_people_name.setText(entity.getName());
        iv_livevideo_primary_team_energy.setText("" + totalEnergy);
    }

    public void onAddEnergy(int energy) {
        mLogtf.d("onAddEnergy:energy=" + energy);
        totalEnergy += energy;
        iv_livevideo_primary_team_energy.setText("" + totalEnergy);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_primary_class_team_item_energy, rl_livevideo_primary_team_tip, false);
        TextView tv_livevideo_primary_team_energy = view.findViewById(R.id.tv_livevideo_primary_team_energy);
        tv_livevideo_primary_team_energy.setText("" + energy);
        rl_livevideo_primary_team_tip.addView(view);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_livevideo_primary_team_tip.removeView(view);
            }
        }, 2000);
    }

    @Override
    public void didOfflineOfUid(final boolean join) {
        logger.d("didOfflineOfUid:join=" + join + ",thread=" + Thread.currentThread());
        handler.post(new Runnable() {
            @Override
            public void run() {
                rl_livevideo_course_item_video_off.setVisibility(join ? View.GONE : View.VISIBLE);
                logger.d("didOfflineOfUid:join=" + join + ",visibility=" + rl_livevideo_course_item_video_off.getVisibility());
            }
        });
//        if (join) {
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

    @Override
    public void onOtherDis(int type, final boolean enable) {
        super.onOtherDis(type, enable);
        if (type == PrimaryClassConfig.MMTYPE_VIDEO) {
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
                        mRtcEngine.enableLocalVideo(enable);
                    }
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    voiceImageView.reset();
                }
            });
            cloudWorkerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    RTCEngine mRtcEngine = cloudWorkerThreadPool.getRtcEngine();
                    if (mRtcEngine != null) {
                        mRtcEngine.muteLocalAudio(!enable);
                    }
                }
            });
        }
    }
}

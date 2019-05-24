package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xes.ps.rtcstream.RTCEngine;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePagerState;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.BasePrimaryTeamItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.BasePrimaryTeamPeopleItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamEmptyItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamMyItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.PrimaryClassView;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business.PrimaryClassInter;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config.PrimaryClassConfig;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.PrimaryKuangjiaImageView;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.agora.rtc.RtcEngine;

public class PrimaryItemPager extends LiveBasePager implements PrimaryItemView {
    private LinearLayout ll_livevideo_primary_team_content;
    private RelativeLayout rl_livevideo_primary_team_content;
    private ImageView ivPkState;
    private TextView tv_livevideo_primary_team_name_mid;
    private TextView tv_livevideo_primary_team_name;
    private ImageView iv_livevideo_primary_team_icon;
    private RelativeLayout mContentView;
    private PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal;
    private View cl_livevideo_primary_team_inter;
    private CloudWorkerThreadPool workerThread;
    private TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity;
    private HashMap<String, BasePrimaryTeamItem> courseGroupItemHashMap = new HashMap<>();
    private HashMap<String, SurfaceView> surfaceViewHashMap = new HashMap<>();
    private HashMap<String, Boolean> userStat = new HashMap<>();
    private String mode;
    private int stuid;
    private float scale;
    private PrimaryClassInter primaryClassInter;
    private boolean videoStatus = false;
    private boolean audioStatus = false;
    private PrimaryClassView primaryClassView;

    public PrimaryItemPager(Context context, RelativeLayout mContentView, String mode) {
        super(context);
        this.mContentView = mContentView;
        this.mode = mode;
        primaryClassView = ProxUtil.getProxUtil().get(mContext, PrimaryClassView.class);
        initData();
    }

    public void setPrimaryClassInter(PrimaryClassInter primaryClassInter) {
        this.primaryClassInter = primaryClassInter;
    }

    @Override
    public View initView() {
        Activity activity = (Activity) mContext;
        View view = activity.findViewById(R.id.rl_livevideo_primary_content);
        ll_livevideo_primary_team_content = view.findViewById(R.id.ll_livevideo_primary_team_content);
        rl_livevideo_primary_team_content = view.findViewById(R.id.rl_livevideo_primary_team_content);
        iv_livevideo_primary_team_icon = view.findViewById(R.id.iv_livevideo_primary_team_icon);
        tv_livevideo_primary_team_name_mid = view.findViewById(R.id.tv_livevideo_primary_team_name_mid);
        tv_livevideo_primary_team_name = view.findViewById(R.id.tv_livevideo_primary_team_name);
        cl_livevideo_primary_team_inter = view.findViewById(R.id.cl_livevideo_primary_team_inter);
        ivPkState = view.findViewById(R.id.iv_live_halfbody_pk_state);
        return view;
    }

    private void setLayout() {
        setImageViewWidth();
    }

    public void setImageViewWidth() {
        ivLivePrimaryClassKuangjiaImgNormal.addSizeChange(new PrimaryKuangjiaImageView.OnSizeChange() {
            @Override
            public void onSizeChange(int width, int height) {
                scale = (float) width / 1334f;
                primaryClassView.decorateItemPagerView(rl_livevideo_primary_team_content, iv_livevideo_primary_team_icon, ll_livevideo_primary_team_content, tv_livevideo_primary_team_name_mid, width, height);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        logger.d("initData:primaryClassView=" + primaryClassView);
        ivLivePrimaryClassKuangjiaImgNormal = mContentView.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        primaryClassView.decorateItemPager(mView);
        addItem();
        setLayout();
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            mView.setVisibility(View.INVISIBLE);
        } else {
            rl_livevideo_primary_team_content.setVisibility(View.INVISIBLE);
            ivPkState.setVisibility(View.VISIBLE);
            handler.postDelayed(ivPkStateRun, 10000);
        }
    }

    private Runnable ivPkStateRun = new Runnable() {

        @Override
        public void run() {
            if (teamInfoEntity != null) {
                ivPkState.setVisibility(View.GONE);
                rl_livevideo_primary_team_content.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onModeChange(final String mode) {
        this.mode = mode;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    mView.setVisibility(View.VISIBLE);
                    rl_livevideo_primary_team_content.setVisibility(View.INVISIBLE);
                    ivPkState.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(ivPkStateRun);
                    handler.postDelayed(ivPkStateRun, 10000);
                    if (teamInfoEntity != null) {
                        joinChannel();
                    } else {
                        addItem();
                    }
                } else {
                    mView.setVisibility(View.INVISIBLE);
                    courseGroupItemHashMap.clear();
                    ll_livevideo_primary_team_content.removeAllViews();
                    if (workerThread != null) {
                        workerThread.leaveChannel();
                        workerThread.exit();
                    }
                }
            }
        });
    }

    @Override
    public void onTeam(String uid, TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity) {
        this.teamInfoEntity = teamInfoEntity;
        this.stuid = Integer.parseInt(uid);
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            mView.setVisibility(View.VISIBLE);
            if (teamInfoEntity != null) {
//                List<TeamMate> result = teamInfoEntity.getResult();
//                for (int i = 0; i < result.size(); i++) {
//                    TeamMate teamMate = result.get(i);
//                    if (teamMate.getIdInt() == stuid) {
//                        break;
//                    } else {
//                        result.remove(i);
//                        i--;
//                    }
//                }
                joinChannel();
            } else {
                addItem();
            }
        }
    }

    @Override
    public void updateTeam(TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity) {
        List<TeamMate> result = teamInfoEntity.getResult();
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        int margin = (int) (10 * scale);
        for (int mateIndex = 0; mateIndex < result.size(); mateIndex++) {
            TeamMate teamMate = result.get(mateIndex);
            BasePrimaryTeamItem teamMatePrimaryTeamItem = courseGroupItemHashMap.get(teamMate.getId());
            if (teamMatePrimaryTeamItem == null) {
                for (int childIndex = 0; childIndex < ll_livevideo_primary_team_content.getChildCount(); childIndex++) {
                    View child = ll_livevideo_primary_team_content.getChildAt(childIndex);
                    BasePrimaryTeamItem basePrimaryTeamItem = (BasePrimaryTeamItem) child.getTag();
                    if (basePrimaryTeamItem instanceof PrimaryTeamEmptyItem) {
                        int index = ll_livevideo_primary_team_content.indexOfChild(child);
                        PrimaryTeamOtherItem otherItem = new PrimaryTeamOtherItem(mContext, teamMate, workerThread, teamMate.getIdInt());
                        otherItem.setOnNameClick(onNameClick);
                        basePrimaryTeamItem = otherItem;
                        View convertView = mInflater.inflate(basePrimaryTeamItem.getLayoutResId(), ll_livevideo_primary_team_content, false);
                        convertView.setTag(basePrimaryTeamItem);
                        basePrimaryTeamItem.initViews(convertView);
                        basePrimaryTeamItem.updateViews(teamMate, index, teamMate);
                        basePrimaryTeamItem.bindListener();
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) convertView.getLayoutParams();
                        lp.height = (int) (149 * scale);
                        lp.bottomMargin = margin;
                        ll_livevideo_primary_team_content.removeView(child);
                        ll_livevideo_primary_team_content.addView(convertView, index, lp);
                        courseGroupItemHashMap.put("" + teamMate.getId(), basePrimaryTeamItem);
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, videoStatus);
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, audioStatus);
                        SurfaceView surfaceView = surfaceViewHashMap.remove(teamMate.getId());
                        if (surfaceView != null) {
                            workerThread.getRtcEngine().setupRemoteVideo(surfaceView, teamMate.getIdInt());
                            otherItem.doRenderRemoteUi(surfaceView);
                        }
                        otherItem.didOfflineOfUid(userStat.containsKey(teamMate.getId()));
                        break;
                    }
                }
            }
        }
    }

    private void joinChannel() {
        workerThread = new CloudWorkerThreadPool(mContext, teamInfoEntity.getToken());
        workerThread.setOnEngineCreate(new CloudWorkerThreadPool.OnEngineCreate() {
            @Override
            public void onEngineCreate(RTCEngine mRtcEngine) {
                logger.d("onEngineCreate:mRtcEngine=" + (mRtcEngine == null));
                if (mRtcEngine != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addItem();
                            workerThread.execute(new Runnable() {
                                @Override
                                public void run() {
                                    workerThread.getRtcEngine().setVideoEncoderConfiguration(200, 152, RTCEngine.RTCEngineVideoBitrate.VIDEO_BITRATE_100);
//                            workerThread.getRtcEngine().enableAudioVolumeIndication(500, 3);
                                }
                            });
                            workerThread.joinChannel(new CloudWorkerThreadPool.OnJoinChannel() {
                                @Override
                                public void onJoinChannel(int joinChannel) {
                                    logger.d("onJoinChannel:joinChannel=" + joinChannel);
                                }
                            });
                        }
                    });
                } else {
                    XESToastUtils.showToast(mContext, "加入房间失败");
                }
            }
        });
        workerThread.setEnableLocalVideo(true);
        workerThread.setEnableLocalAudio(false);
        workerThread.eventHandler().addEventHandler(listener);
        workerThread.start();
        tv_livevideo_primary_team_name.setText(teamInfoEntity.getTeamName());
        tv_livevideo_primary_team_name_mid.setText("欢迎加入 “" + teamInfoEntity.getTeamName() + "”");
        tv_livevideo_primary_team_name_mid.setVisibility(View.VISIBLE);
        ImageLoader.with(mContext.getApplicationContext()).load(teamInfoEntity.getImg()).into(iv_livevideo_primary_team_icon);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_livevideo_primary_team_name_mid.setVisibility(View.GONE);
            }
        }, 2000);
    }

    @Override
    public void updatePkState(float ratio) {
        mLogtf.d("updatePkState:ratio=" + ratio);
        if (ratio > TeamPkStateLayout.HALF_PROGRESS) {
            ivPkState.setImageResource(R.drawable.bg_live_ke_energy_zanshi_icon_normal);
        } else if (ratio < TeamPkStateLayout.HALF_PROGRESS) {
            ivPkState.setImageResource(R.drawable.bg_live_ke_energy_quanli_icon_normal);
        } else {
            return;
        }
        rl_livevideo_primary_team_content.setVisibility(View.INVISIBLE);
        ivPkState.setVisibility(View.VISIBLE);
        handler.removeCallbacks(ivPkStateRun);
        handler.postDelayed(ivPkStateRun, 10000);
    }

    private int totalEnergy;

    @Override
    public void onAddEnergy(boolean first, int energy) {
        totalEnergy += energy;
        mLogtf.d("onAddEnergy:first=" + first + ",energy=" + energy);
        PrimaryTeamMyItem basePrimaryTeamItem = (PrimaryTeamMyItem) courseGroupItemHashMap.get("" + stuid);
        if (basePrimaryTeamItem != null) {
            basePrimaryTeamItem.onAddEnergy(first, energy);
        }
    }

    private void addItem() {
//        if (!courseGroupItemHashMap.isEmpty()) {
//            return;
//        }
        courseGroupItemHashMap.clear();
        ll_livevideo_primary_team_content.removeAllViews();
        List<TeamMate> result;
        if (teamInfoEntity != null) {
            result = teamInfoEntity.getResult();
        } else {
            result = new ArrayList<>();
        }
        logger.d("addItem:size=" + result.size());
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        int margin = (int) (10 * scale);
        for (int i = 0; i < 4; i++) {
            TeamMate teamMember = null;
            BasePrimaryTeamItem basePrimaryTeamItem;
            boolean isMe = false;
            if (i < result.size()) {
                teamMember = result.get(i);
                int uid = Integer.parseInt(teamMember.getId());
                if (stuid == Integer.parseInt(teamMember.getId())) {
                    teamMember.setEnergy(totalEnergy);
                    PrimaryTeamMyItem myItem = new PrimaryTeamMyItem(mContext, teamMember, workerThread, uid);
//                    myItem.setOnNameClick(onNameClick);
                    basePrimaryTeamItem = myItem;
                    isMe = true;
                } else {
                    PrimaryTeamOtherItem otherItem = new PrimaryTeamOtherItem(mContext, teamMember, workerThread, uid);
                    otherItem.setOnNameClick(onNameClick);
                    basePrimaryTeamItem = otherItem;
                }
            } else {
                basePrimaryTeamItem = new PrimaryTeamEmptyItem(mContext, null, workerThread, -1);
            }
            View convertView = mInflater.inflate(basePrimaryTeamItem.getLayoutResId(), ll_livevideo_primary_team_content, false);
            convertView.setTag(basePrimaryTeamItem);
            basePrimaryTeamItem.initViews(convertView);
            basePrimaryTeamItem.updateViews(teamMember, i, teamMember);
            basePrimaryTeamItem.bindListener();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) convertView.getLayoutParams();
            lp.height = (int) (149 * scale);
            lp.bottomMargin = margin;
            if (isMe) {
                ll_livevideo_primary_team_content.addView(convertView, 0, lp);
            } else {
                ll_livevideo_primary_team_content.addView(convertView, lp);
            }
            if (teamMember == null) {
                courseGroupItemHashMap.put("empty" + i, basePrimaryTeamItem);
            } else {
                courseGroupItemHashMap.put("" + teamMember.getId(), basePrimaryTeamItem);
                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, videoStatus);
                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, audioStatus);
            }
        }
    }

    BasePrimaryTeamPeopleItem.OnNameClick onNameClick = new BasePrimaryTeamPeopleItem.OnNameClick() {
        @Override
        public void onNameClick(final TeamMate finalEntity, TextView tvName) {
            if (cl_livevideo_primary_team_inter.getVisibility() == View.VISIBLE) {
                cl_livevideo_primary_team_inter.setVisibility(View.GONE);
            } else {
                cl_livevideo_primary_team_inter.setVisibility(View.VISIBLE);
                int[] loc = ViewUtil.getLoc(tvName, (ViewGroup) mView);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cl_livevideo_primary_team_inter.getLayoutParams();
                lp.leftMargin = loc[0] - (cl_livevideo_primary_team_inter.getWidth() - tvName.getWidth()) / 2;
                lp.topMargin = loc[1] - cl_livevideo_primary_team_inter.getHeight();
                cl_livevideo_primary_team_inter.setLayoutParams(lp);
                TextView tv_livevideo_primary_team_inter_left = cl_livevideo_primary_team_inter.findViewById(R.id.tv_livevideo_primary_team_inter_left);
                tv_livevideo_primary_team_inter_left.setText(finalEntity.isLook() ? "不看ta" : "显示ta");
                tv_livevideo_primary_team_inter_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cl_livevideo_primary_team_inter.setVisibility(View.GONE);
                        BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + finalEntity.getId());
                        if (basePrimaryTeamItem != null) {
                            basePrimaryTeamItem.onVideo();
                        }
                    }
                });
                final TextView tv_livevideo_primary_team_inter_right = cl_livevideo_primary_team_inter.findViewById(R.id.tv_livevideo_primary_team_inter_right);
                tv_livevideo_primary_team_inter_right.setText("举报");
                tv_livevideo_primary_team_inter_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cl_livevideo_primary_team_inter.setVisibility(View.GONE);
                        primaryClassInter.reportNaughtyBoy(finalEntity, new PrimaryClassInter.ReportNaughtyBoy() {
                            @Override
                            public void onReport(TeamMate entity) {
                                if (finalEntity == entity) {
                                    tv_livevideo_primary_team_inter_right.setText("已举报");
                                    BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + entity.getId());
                                    if (basePrimaryTeamItem != null) {
                                        basePrimaryTeamItem.onReport();
                                    }
                                }
                            }

                            @Override
                            public void onReportError(TeamMate entity) {
                                if (finalEntity == entity && AppConfig.DEBUG) {
                                    tv_livevideo_primary_team_inter_right.setText("已举报");
                                    BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + entity.getId());
                                    if (basePrimaryTeamItem != null) {
                                        basePrimaryTeamItem.onReport();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }
    };

    private void doRenderRemoteUi(final int uid, final BasePrimaryTeamItem courseGroupItem) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SurfaceView surfaceV = RtcEngine.CreateRendererView(mContext);
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                workerThread.getRtcEngine().setupRemoteVideo(surfaceV, uid);
                courseGroupItem.doRenderRemoteUi(surfaceV);
            }
        });
    }

    private RTCEngine.IRtcEngineEventListener listener = new RTCEngine.IRtcEngineEventListener() {
        @Override
        public void remotefirstVideoRecvWithUid(int uid) {
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            if (basePrimaryTeamItem != null) {
                doRenderRemoteUi(uid, basePrimaryTeamItem);
            } else {
                logger.d("remotefirstVideoRecvWithUid:uid=" + uid);
                SurfaceView surfaceV = RtcEngine.CreateRendererView(mContext);
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                surfaceViewHashMap.put("" + uid, surfaceV);
            }
        }

        @Override
        public void remoteUserJoinWitnUid(int uid) {
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            if (basePrimaryTeamItem != null) {
                basePrimaryTeamItem.didOfflineOfUid(true);
            } else {
                userStat.put("" + uid, true);
                logger.d("remoteUserJoinWitnUid:uid=" + uid);
                primaryClassInter.getMyTeamInfo();
            }
        }

        @Override
        public void didOfflineOfUid(int uid) {
            surfaceViewHashMap.remove("" + uid);
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            if (basePrimaryTeamItem != null) {
                basePrimaryTeamItem.didOfflineOfUid(false);
            } else {
                userStat.remove("" + uid);
            }
        }

        @Override
        public void didAudioMuted(int uid, boolean muted) {

        }

        @Override
        public void didVideoMuted(int uid, boolean muted) {

        }

        @Override
        public void didOccurError(RTCEngine.RTCEngineErrorCode code) {

        }

        @Override
        public void onConnectionLost() {

        }

        @Override
        public void localUserJoindWithUid(int uid) {
            if (stuid == uid) {
                BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
                if (basePrimaryTeamItem != null) {
                    preview(basePrimaryTeamItem);
                    basePrimaryTeamItem.didOfflineOfUid(true);
                }
            }
        }

        @Override
        public void reportAudioVolumeOfSpeaker(int uid, int volume) {
            BasePrimaryTeamItem basePrimaryTeamItem;
            if (0 == uid) {
                basePrimaryTeamItem = courseGroupItemHashMap.get("" + stuid);
            } else {
                basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            }
            if (basePrimaryTeamItem != null) {
                basePrimaryTeamItem.reportAudioVolumeOfSpeaker(volume);
            }
        }

        @Override
        public void remotefirstAudioRecvWithUid(int uid) {

        }
    };

    private void preview(final BasePrimaryTeamItem courseGroupItem) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SurfaceView surfaceV = RtcEngine.CreateRendererView(mContext);
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                workerThread.preview(true, surfaceV);
                courseGroupItem.doRenderRemoteUi(surfaceV);
            }
        });
    }

    @Override
    public void onMessage(int type, boolean open) {
        logger.d("onMessage:type=" + type + ",open=" + open);
        if (type == PrimaryClassConfig.MMTYPE_VIDEO) {
            if (videoStatus != open) {
                videoStatus = open;
                if (open) {
                    if (mState == LiveBasePagerState.RESUMED) {
                        foreach(new ItemCall() {
                            @Override
                            public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, true);
                            }
                        });
                    }
                } else {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, false);
                        }
                    });
                }
            }
        } else if (type == PrimaryClassConfig.MMTYPE_AUDIO) {
            if (audioStatus != open) {
                audioStatus = open;
                if (open) {
                    if (mState == LiveBasePagerState.RESUMED) {
                        foreach(new ItemCall() {
                            @Override
                            public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, true);
                            }
                        });
                    }
                } else {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, false);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onMessage(boolean videoopen, boolean audioopen) {
        logger.d("onMessage:videoopen=" + videoopen + ",audioopen=" + audioopen);
        if (videoStatus != videoopen) {
            videoStatus = videoopen;
            if (videoopen) {
                if (mState == LiveBasePagerState.RESUMED) {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, true);
                        }
                    });
                }
            } else {
                foreach(new ItemCall() {
                    @Override
                    public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, false);
                    }
                });
            }
        }
        if (audioStatus != audioopen) {
            audioStatus = audioopen;
            if (audioopen) {
                if (mState == LiveBasePagerState.RESUMED) {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, true);
                        }
                    });
                }
            } else {
                foreach(new ItemCall() {
                    @Override
                    public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, false);
                    }
                });
            }
        }
    }

    public void foreach(ItemCall itemCall) {
        Set<String> keys = courseGroupItemHashMap.keySet();
        for (String key : keys) {
            if (!key.startsWith("empty")) {
                BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get(key);
                itemCall.onItem(basePrimaryTeamItem);
            }
        }
    }

    interface ItemCall {
        void onItem(BasePrimaryTeamItem basePrimaryTeamItem);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoStatus) {
            foreach(new ItemCall() {
                @Override
                public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                    basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, true);
                }
            });
        }
        if (audioStatus) {
            foreach(new ItemCall() {
                @Override
                public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                    basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, true);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        foreach(new ItemCall() {
            @Override
            public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, false);
            }
        });
        foreach(new ItemCall() {
            @Override
            public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (workerThread != null) {
            workerThread.exit();
        }
    }

}

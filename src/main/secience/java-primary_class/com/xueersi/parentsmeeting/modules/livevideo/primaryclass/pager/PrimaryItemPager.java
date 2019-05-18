package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xes.ps.rtcstream.RTCEngine;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.BasePrimaryTeamItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.BasePrimaryTeamPeopleItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamEmptyItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamMyItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business.PrimaryClassInter;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.PrimaryClassEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamInfo;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.PrimaryKuangjiaImageView;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

import io.agora.rtc.RtcEngine;

public class PrimaryItemPager extends LiveBasePager implements PrimaryItemView {
    private LinearLayout ll_livevideo_primary_team_content;
    private TextView tv_livevideo_primary_team_name;
    private RelativeLayout mContentView;
    private PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal;
    View cl_livevideo_primary_team_inter;
    private CloudWorkerThreadPool workerThread;
    private PrimaryClassEntity primaryClassEntity;
    private HashMap<String, BasePrimaryTeamItem> courseGroupItemHashMap = new HashMap<>();
    private int stuid;
    float scale;
    PrimaryClassInter primaryClassInter;

    public PrimaryItemPager(Context context, RelativeLayout mContentView) {
        super(context);
        this.mContentView = mContentView;
        initData();
    }

    public void setPrimaryClassInter(PrimaryClassInter primaryClassInter) {
        this.primaryClassInter = primaryClassInter;
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_primary_class_team, null);
        ll_livevideo_primary_team_content = view.findViewById(R.id.ll_livevideo_primary_team_content);
        tv_livevideo_primary_team_name = view.findViewById(R.id.tv_livevideo_primary_team_name);
        cl_livevideo_primary_team_inter = view.findViewById(R.id.cl_livevideo_primary_team_inter);
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
                {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_primary_team_name.getLayoutParams();
                    int lpwidth = (int) (191 * scale);
                    int lpheight = (int) (54 * scale);
                    int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1124 * scale);
                    int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (26 * scale);
                    if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                        lp.width = lpwidth;
                        lp.height = lpheight;
                        lp.leftMargin = leftMargin;
                        lp.topMargin = topMargin;
                        tv_livevideo_primary_team_name.setLayoutParams(lp);
                    }
                }
                {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ll_livevideo_primary_team_content.getLayoutParams();
                    int lpwidth = (int) (193 * scale);
                    int lpheight = (int) (630 * scale);
                    int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1128 * scale);
                    int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (102 * scale);
                    if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                        lp.width = lpwidth;
                        lp.height = lpheight;
                        lp.leftMargin = leftMargin;
                        lp.topMargin = topMargin;
                        ll_livevideo_primary_team_content.setLayoutParams(lp);
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        ivLivePrimaryClassKuangjiaImgNormal = mContentView.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        setLayout();
    }

    @Override
    public void onTeam(String uid, PrimaryClassEntity primaryClassEntity) {
        this.primaryClassEntity = primaryClassEntity;
        this.stuid = Integer.parseInt(uid);
        workerThread = new CloudWorkerThreadPool(mContext, primaryClassEntity.getToken());
        workerThread.setOnEngineCreate(new CloudWorkerThreadPool.OnEngineCreate() {
            @Override
            public void onEngineCreate(RTCEngine mRtcEngine) {
                logger.d("onEngineCreate:mRtcEngine=" + (mRtcEngine == null));
                if (mRtcEngine != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addItem();
                        }
                    });
                    workerThread.execute(new Runnable() {
                        @Override
                        public void run() {
//                            workerThread.getRtcEngine().enableAudioVolumeIndication(500, 3);
                        }
                    });
                    workerThread.joinChannel(new CloudWorkerThreadPool.OnJoinChannel() {
                        @Override
                        public void onJoinChannel(int joinChannel) {
                            logger.d("onJoinChannel:joinChannel=" + joinChannel);
                        }
                    });
                } else {
                    XESToastUtils.showToast(mContext, "加入房间失败");
                }
            }
        });
        workerThread.setEnableLocalVideo(true);
        workerThread.eventHandler().addEventHandler(listener);
        workerThread.start();
        TeamInfo teamInfo = primaryClassEntity.getTeamInfo();
        tv_livevideo_primary_team_name.setText(teamInfo.getTeamName());
    }

    private void addItem() {
        ArrayList<TeamMember> teamMembers = primaryClassEntity.getTeamInfo().getTeamMembers();
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        int margin = (int) (11 * scale);
        for (int i = 0; i < 4; i++) {
            TeamMember teamMember = null;
            BasePrimaryTeamItem basePrimaryTeamItem;
            if (i < teamMembers.size()) {
                teamMember = teamMembers.get(i);
                if (stuid == teamMember.getStuId()) {
                    PrimaryTeamMyItem myItem = new PrimaryTeamMyItem(mContext, teamMember, workerThread, teamMember.getStuId());
                    myItem.setOnNameClick(onNameClick);
                    basePrimaryTeamItem = myItem;
                } else {
                    PrimaryTeamOtherItem otherItem = new PrimaryTeamOtherItem(mContext, teamMember, workerThread, teamMember.getStuId());
                    otherItem.setOnNameClick(onNameClick);
                    basePrimaryTeamItem = otherItem;
                }
            } else {
                basePrimaryTeamItem = new PrimaryTeamEmptyItem(mContext, null, workerThread, -1);
            }
            View convertView = mInflater.inflate(basePrimaryTeamItem.getLayoutResId(), ll_livevideo_primary_team_content, false);
            basePrimaryTeamItem.initViews(convertView);
            basePrimaryTeamItem.updateViews(teamMember, i, teamMember);
            basePrimaryTeamItem.bindListener();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) convertView.getLayoutParams();
            lp.height = (int) (149 * scale);
            lp.bottomMargin = margin;
            convertView.setBackgroundColor(0xa663a4aF);
            ll_livevideo_primary_team_content.addView(convertView, lp);
            if (teamMember == null) {
                courseGroupItemHashMap.put("empty" + i, basePrimaryTeamItem);
            } else {
                courseGroupItemHashMap.put("" + teamMember.getStuId(), basePrimaryTeamItem);
            }
        }
    }

    BasePrimaryTeamPeopleItem.OnNameClick onNameClick = new BasePrimaryTeamPeopleItem.OnNameClick() {
        @Override
        public void onNameClick(final TeamMember finalEntity, TextView tvName) {
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
                tv_livevideo_primary_team_inter_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                final TextView tv_livevideo_primary_team_inter_right = cl_livevideo_primary_team_inter.findViewById(R.id.tv_livevideo_primary_team_inter_right);
                if (finalEntity.isReport()) {
                    tv_livevideo_primary_team_inter_right.setText("已举报");
                    tv_livevideo_primary_team_inter_right.setOnClickListener(null);
                } else {
                    tv_livevideo_primary_team_inter_right.setText("举报");
                    tv_livevideo_primary_team_inter_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            primaryClassInter.reportNaughtyBoy(finalEntity, new PrimaryClassInter.ReportNaughtyBoy() {
                                @Override
                                public void onReport(TeamMember entity) {
                                    entity.setReport(true);
                                    if (finalEntity == entity) {
                                        tv_livevideo_primary_team_inter_right.setText("已举报");
                                    }
                                }
                            });
                        }
                    });
                }
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
            }
        }

        @Override
        public void remoteUserJoinWitnUid(int uid) {

        }

        @Override
        public void didOfflineOfUid(int uid) {

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
                }
            }
        }

        @Override
        public void reportAudioVolumeOfSpeaker(int uid, int volume) {
            if (0 == uid) {
                BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
                if (basePrimaryTeamItem != null) {
                    basePrimaryTeamItem.reportAudioVolumeOfSpeaker(volume);
                }
            }
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
    }

    @Override
    public void onMessage(boolean videoopen, boolean audioopen) {
        logger.d("onMessage:videoopen=" + videoopen + ",audioopen=" + audioopen);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        workerThread.exit();
    }
}

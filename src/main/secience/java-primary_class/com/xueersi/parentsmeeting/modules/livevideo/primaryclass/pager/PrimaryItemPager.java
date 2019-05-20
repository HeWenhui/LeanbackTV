package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

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
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.BasePrimaryTeamItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.BasePrimaryTeamPeopleItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamEmptyItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamMyItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item.PrimaryTeamOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business.PrimaryClassInter;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.PrimaryKuangjiaImageView;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.agora.rtc.RtcEngine;

public class PrimaryItemPager extends LiveBasePager implements PrimaryItemView {
    private LinearLayout ll_livevideo_primary_team_content;
    private RelativeLayout rl_livevideo_primary_team_content;
    private TextView tv_livevideo_primary_team_name_mid;
    private TextView tv_livevideo_primary_team_name;
    private ImageView iv_livevideo_primary_team_icon;
    private RelativeLayout mContentView;
    private PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal;
    private View cl_livevideo_primary_team_inter;
    private CloudWorkerThreadPool workerThread;
    private TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity;
    private HashMap<String, BasePrimaryTeamItem> courseGroupItemHashMap = new HashMap<>();
    private String mode;
    private int stuid;
    float scale;
    PrimaryClassInter primaryClassInter;

    public PrimaryItemPager(Context context, RelativeLayout mContentView, String mode) {
        super(context);
        this.mContentView = mContentView;
        this.mode = mode;
        initData();
    }

    public void setPrimaryClassInter(PrimaryClassInter primaryClassInter) {
        this.primaryClassInter = primaryClassInter;
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_primary_class_team, null);
        ll_livevideo_primary_team_content = view.findViewById(R.id.ll_livevideo_primary_team_content);
        rl_livevideo_primary_team_content = view.findViewById(R.id.rl_livevideo_primary_team_content);
        iv_livevideo_primary_team_icon = view.findViewById(R.id.iv_livevideo_primary_team_icon);
        tv_livevideo_primary_team_name_mid = view.findViewById(R.id.tv_livevideo_primary_team_name_mid);
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
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_livevideo_primary_team_content.getLayoutParams();
                    int lpwidth = (int) (191 * scale);
                    int lpheight = (int) (54 * scale);
                    int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1124 * scale);
                    int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (26 * scale);
                    if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                        lp.width = lpwidth;
                        lp.height = lpheight;
                        lp.leftMargin = leftMargin;
                        lp.topMargin = topMargin;
                        rl_livevideo_primary_team_content.setLayoutParams(lp);
                    }
                    RelativeLayout.LayoutParams lpImg = (RelativeLayout.LayoutParams) iv_livevideo_primary_team_icon.getLayoutParams();
                    int lpImgWidth = (int) (49 * scale);
                    int lpImgHeight = (int) (46 * scale);
                    if (lpImg.width != lpImgWidth || lpImg.height != lpImgHeight) {
                        lpImg.width = lpImgWidth;
                        lpImg.height = lpImgHeight;
                        iv_livevideo_primary_team_icon.setLayoutParams(lpImg);
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
                {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_primary_team_name_mid.getLayoutParams();
                    int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (114 * scale);
                    if (lp.topMargin != topMargin) {
                        lp.topMargin = topMargin;
                        tv_livevideo_primary_team_name_mid.setLayoutParams(lp);
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
        addItem();
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            mView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onModeChange(final String mode) {
        this.mode = mode;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    mView.setVisibility(View.VISIBLE);
                    if (teamInfoEntity != null) {
                        joinChannel();
                    }
                } else {
                    mView.setVisibility(View.GONE);
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
                joinChannel();
            } else {
                addItem();
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
        tv_livevideo_primary_team_name.setText(teamInfoEntity.getTeamName());
        tv_livevideo_primary_team_name_mid.setText("欢迎加入 “" + teamInfoEntity.getTeamName() + "”");
        tv_livevideo_primary_team_name_mid.setVisibility(View.VISIBLE);
        ImageLoader.with(mContext.getApplicationContext()).load(teamInfoEntity.getImg()).into(iv_livevideo_primary_team_icon);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                tv_livevideo_primary_team_name_mid.setText("");
            }
        }, 2000);
    }

    private void addItem() {
        courseGroupItemHashMap.clear();
        ll_livevideo_primary_team_content.removeAllViews();
        List<TeamMate> result;
        if (teamInfoEntity != null) {
            result = teamInfoEntity.getResult();
        } else {
            result = new ArrayList<>();
        }
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        int margin = (int) (11 * scale);
        for (int i = 0; i < 4; i++) {
            TeamMate teamMember = null;
            BasePrimaryTeamItem basePrimaryTeamItem;
            if (i < result.size()) {
                teamMember = result.get(i);
                int uid = Integer.parseInt(teamMember.getId());
                if (stuid == Integer.parseInt(teamMember.getId())) {
                    PrimaryTeamMyItem myItem = new PrimaryTeamMyItem(mContext, teamMember, workerThread, uid);
                    myItem.setOnNameClick(onNameClick);
                    basePrimaryTeamItem = myItem;
                } else {
                    PrimaryTeamOtherItem otherItem = new PrimaryTeamOtherItem(mContext, teamMember, workerThread, uid);
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
                courseGroupItemHashMap.put("" + teamMember.getId(), basePrimaryTeamItem);
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
                tv_livevideo_primary_team_inter_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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
                                }
                            }

                            @Override
                            public void onReportError(TeamMate entity) {
                                BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + entity.getId());
                                if (basePrimaryTeamItem != null) {
                                    basePrimaryTeamItem.onReport();
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
        if (workerThread != null) {
            workerThread.exit();
        }
    }
}

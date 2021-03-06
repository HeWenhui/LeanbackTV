package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xes.ps.rtcstream.listener.RTCConnectionStateType;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xes.ps.rtcstream.RTCEngine;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AgoraUpload;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
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

/**
 * 小班体验，基础布局
 */
public class PrimaryItemPager extends LiveBasePager implements PrimaryItemView {
    /** 战队小组成员 */
    private LinearLayout llPrimaryTeamContent;
    /** 右上角战队名字,icon */
    private RelativeLayout rl_livevideo_primary_team_content;
    /** 战队状态 */
    private ImageView ivPkState;
    /** 战队名字，中间提示 */
    private TextView tvPrimaryTeamNameMid;
    /** 战队名字 */
    private TextView tvPrimaryTeamName;
    /** 战队icon */
    private ImageView ivPrimaryTeamIcon;
    private LiveViewAction liveViewAction;
    private PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal;
    /** 用户交互，不看他，举报 */
    private View clPrimaryTeamInter;
    /** 用户交互，不看他 */
    private TextView tvPrimaryTeamInterLeft;
    private CloudWorkerThreadPool workerThread;
    private AgoraUpload agoraUpload;
    private String fileFullPath;
    private boolean leaveChannel = true;
    private TeamPkTeamInfoEntity.TeamInfoEntity teamInfoEntity;
    private HashMap<String, BasePrimaryTeamItem> courseGroupItemHashMap = new HashMap<>();
    /** 后进入用户的视频布局 */
    private HashMap<String, SurfaceView> surfaceViewHashMap = new HashMap<>();
    /** 用户麦克风状态 */
    private HashMap<String, Boolean> userVoiceStat = new HashMap<>();
    /** 用户在线状态 */
    private HashMap<String, Boolean> userOnLineStat = new HashMap<>();
    private String mode;
    private String liveId;
    private int stuid;
    private boolean showTeamMid = false;
    private String stuName;
    private float scale;
    private float scaleX;
    private PrimaryClassInter primaryClassInter;
    /** 视频默认开 */
    private boolean videoStatus = true;
    /** 音频默认开 */
    private boolean audioStatus = false;
    /** 切到后台 */
    private boolean havepause = false;
    /** 切到后台时，有相机权限 */
    private boolean havecamera;
    /** 切到后台时，有麦克风权限 */
    private boolean haveaudio;
    private PrimaryClassView primaryClassView;

    public PrimaryItemPager(Context context, LiveViewAction liveViewAction, String mode) {
        super(context);
        this.liveViewAction = liveViewAction;
        this.mode = mode;
        primaryClassView = ProxUtil.getProxUtil().get(mContext, PrimaryClassView.class);
        agoraUpload = new AgoraUpload(mContext);
        initData();
        initListener();
    }

    public void setPrimaryClassInter(PrimaryClassInter primaryClassInter) {
        this.primaryClassInter = primaryClassInter;
    }

    @Override
    public View initView() {
        Activity activity = (Activity) mContext;
        View view = activity.findViewById(R.id.rl_livevideo_primary_content);
        llPrimaryTeamContent = view.findViewById(R.id.ll_livevideo_primary_team_content);
        rl_livevideo_primary_team_content = view.findViewById(R.id.rl_livevideo_primary_team_content);
        ivPrimaryTeamIcon = view.findViewById(R.id.iv_livevideo_primary_team_icon);
        tvPrimaryTeamNameMid = view.findViewById(R.id.tv_livevideo_primary_team_name_mid);
        tvPrimaryTeamName = view.findViewById(R.id.tv_livevideo_primary_team_name);
        clPrimaryTeamInter = view.findViewById(R.id.cl_livevideo_primary_team_inter);
        tvPrimaryTeamInterLeft = clPrimaryTeamInter.findViewById(R.id.tv_livevideo_primary_team_inter_left);
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
                scaleX = (float) width / 2001f;
                primaryClassView.decorateItemPagerView(rl_livevideo_primary_team_content, ivPrimaryTeamIcon, llPrimaryTeamContent, tvPrimaryTeamNameMid, width, height);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        logger.d("initData:mode=" + mode);
        ivLivePrimaryClassKuangjiaImgNormal = liveViewAction.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        primaryClassView.decorateItemPager(mView, ivLivePrimaryClassKuangjiaImgNormal);
        addItem();
        setLayout();
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            mView.setVisibility(View.INVISIBLE);
        } else {
            rl_livevideo_primary_team_content.setVisibility(View.INVISIBLE);
            ivPkState.setVisibility(View.VISIBLE);
            mainHandler.postDelayed(ivPkStateRun, 10000);
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        logger.d("onLiveInited:mode=" + getInfo.getMode() + "," + mode);
        stuName = getInfo.getStuName();
        liveId = getInfo.getId();
        if (!getInfo.getMode().equals(mode)) {
            mode = getInfo.getMode();
            if (LiveTopic.MODE_TRANING.equals(mode)) {
                mView.setVisibility(View.INVISIBLE);
            } else {
                rl_livevideo_primary_team_content.setVisibility(View.INVISIBLE);
                ivPkState.setVisibility(View.VISIBLE);
                mainHandler.postDelayed(ivPkStateRun, 10000);
            }
        }
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    private void hideInter(boolean show) {
        if (show) {
            mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    int top = clPrimaryTeamInter.getTop();
                    int left = clPrimaryTeamInter.getLeft();
                    int width = clPrimaryTeamInter.getWidth();
                    int height = clPrimaryTeamInter.getHeight();
                    logger.d("onTouch:x=" + x + ",y=" + y + ",top=" + top + ",left=" + left + ",width=" + width + ",height=" + height);
                    if (x >= left && x <= x + width && y >= top && y <= top + height) {

                    } else {
                        clPrimaryTeamInter.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
        } else {
            mView.setOnTouchListener(null);
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
        mLogtf.d("onModeChange:mode=" + mode);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                clPrimaryTeamInter.setVisibility(View.GONE);
                hideInter(false);
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    mView.setVisibility(View.VISIBLE);
                    rl_livevideo_primary_team_content.setVisibility(View.INVISIBLE);
                    ivPkState.setVisibility(View.VISIBLE);
                    mainHandler.removeCallbacks(ivPkStateRun);
                    mainHandler.postDelayed(ivPkStateRun, 10000);
                    if (teamInfoEntity != null) {
                        joinChannel();
                    } else {
                        addItem();
                    }
                } else {
                    mView.setVisibility(View.INVISIBLE);
                    courseGroupItemHashMap.clear();
                    llPrimaryTeamContent.removeAllViews();
                    leaveChannel = true;
                    if (workerThread != null) {
                        workerThread.leaveChannel();
                        workerThread.exit();
                        agoraUpload.upload(fileFullPath, liveId);
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
        int live_primary_right_head_gap = primaryClassView.getLive_primary_right_head_gap();
        int margin = (int) (live_primary_right_head_gap * scaleX);
        int live_primary_right_item_height = primaryClassView.getLive_primary_right_item_height();
        for (int mateIndex = 0; mateIndex < result.size(); mateIndex++) {
            TeamMate teamMate = result.get(mateIndex);
            BasePrimaryTeamItem teamMatePrimaryTeamItem = courseGroupItemHashMap.get(teamMate.getId());
            if (teamMatePrimaryTeamItem == null) {
                for (int childIndex = 0; childIndex < llPrimaryTeamContent.getChildCount(); childIndex++) {
                    View child = llPrimaryTeamContent.getChildAt(childIndex);
                    BasePrimaryTeamItem basePrimaryTeamItem = (BasePrimaryTeamItem) child.getTag();
                    if (basePrimaryTeamItem instanceof PrimaryTeamEmptyItem) {
                        int index = llPrimaryTeamContent.indexOfChild(child);
                        PrimaryTeamOtherItem otherItem = new PrimaryTeamOtherItem(mContext, teamMate, workerThread, teamMate.getIdInt());
                        otherItem.setOnNameClick(onNameClick);
                        otherItem.setIndex(index);
                        basePrimaryTeamItem = otherItem;
                        View convertView = mInflater.inflate(basePrimaryTeamItem.getLayoutResId(), llPrimaryTeamContent, false);
                        convertView.setTag(basePrimaryTeamItem);
                        basePrimaryTeamItem.initViews(convertView);
                        basePrimaryTeamItem.updateViews(teamMate, index, teamMate);
                        basePrimaryTeamItem.bindListener();
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) convertView.getLayoutParams();
                        lp.height = (int) (live_primary_right_item_height * scaleX);
                        lp.bottomMargin = margin;
                        llPrimaryTeamContent.removeView(child);
                        llPrimaryTeamContent.addView(convertView, index, lp);
                        courseGroupItemHashMap.put("" + teamMate.getId(), basePrimaryTeamItem);
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, videoStatus, mState);
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, audioStatus, mState);
                        SurfaceView surfaceView = surfaceViewHashMap.remove(teamMate.getId());
                        if (surfaceView != null) {
                            workerThread.getRtcEngine().setupRemoteVideo(surfaceView, teamMate.getIdInt());
                            otherItem.doRenderRemoteUi(surfaceView);
                        }
                        boolean onLineStat = userOnLineStat.containsKey("" + teamMate.getId());
                        otherItem.didOfflineOfUid("updateTeam", onLineStat);
                        boolean voiceStat = userVoiceStat.containsKey("" + teamMate.getId());
                        if (voiceStat) {
                            otherItem.remotefirstAudioRecvWithUid(teamMate.getIdInt());
                        }
                        mLogtf.d("updateTeam:id=" + teamMate.getId() + ",index=" + index + ",onLineStat=" + onLineStat + ",voiceStat=" + voiceStat);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onCheckPermission() {
        if (!leaveChannel) {
            boolean camera = XesPermission.checkPermissionHave(mContext, PermissionConfig.PERMISSION_CODE_CAMERA);
            boolean audio = XesPermission.checkPermissionHave(mContext, PermissionConfig.PERMISSION_CODE_AUDIO);
            if (havepause) {
                havepause = false;
                if (havecamera == camera && haveaudio == audio) {
                    return;
                }
            }
            mLogtf.d("onCheckPermission:camera=" + camera + ",audio=" + audio);
            if (camera || audio) {
                BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + stuid);
                if (basePrimaryTeamItem instanceof PrimaryTeamMyItem) {
                    PrimaryTeamMyItem myItem = (PrimaryTeamMyItem) basePrimaryTeamItem;
                    if (audio) {
                        myItem.onCheckPermission(PrimaryClassConfig.MMTYPE_AUDIO);
                    }
                    if (camera) {
                        myItem.onCheckPermission(PrimaryClassConfig.MMTYPE_VIDEO);
                    }
                }
                if (teamInfoEntity != null && workerThread != null) {
//                    courseGroupItemHashMap.clear();
//                    llPrimaryTeamContent.removeAllViews();
                    workerThread.leaveChannel();
                    workerThread.joinChannel(new CloudWorkerThreadPool.OnJoinChannel() {
                        @Override
                        public void onJoinChannel(int joinChannel) {

                        }
                    });
                }
            }
        }
    }

    private void joinChannel() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                addItem();
            }
        });
        workerThread = new CloudWorkerThreadPool(mContext, teamInfoEntity.getToken());
        workerThread.setOnEngineCreate(new CloudWorkerThreadPool.OnEngineCreate() {
            @Override
            public void onEngineCreate(RTCEngine mRtcEngine, String path) {
                mLogtf.d("onEngineCreate:mRtcEngine=" + (mRtcEngine == null));
                if (mRtcEngine != null) {
                    fileFullPath = path;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            workerThread.execute(new Runnable() {
                                @Override
                                public void run() {
                                    workerThread.getRtcEngine().setVideoEncoderConfiguration(PrimaryClassConfig.VIDEO_WIDTH, PrimaryClassConfig.VIDEO_HEIGHT,
                                            RTCEngine.RTCEngineVideoBitrate.VIDEO_BITRATE_100, RTCEngine.RTC_ORIENTATION_MODE.RTC_ORIENTATION_MODE_FIXED_LANDSCAPE);
//                                    VideoEncoderConfiguration.VideoDimensions dimensions = new VideoEncoderConfiguration.VideoDimensions(PrimaryClassConfig.VIDEO_WIDTH, PrimaryClassConfig.VIDEO_HEIGHT);
//                                    VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(dimensions,
//                                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10,
//                                            VideoEncoderConfiguration.STANDARD_BITRATE,
//                                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE);
//                                    RtcEngine mRtcEngine = workerThread.getAgoraRtcEngine();
//                                    if (mRtcEngine != null) {
//                                        mRtcEngine.setVideoEncoderConfiguration(configuration);
//                                    }

                                    RTCEngine mRtcEngine = workerThread.getRtcEngine();
                                    if (mRtcEngine != null) {
                                        mRtcEngine.setRemoteMirror(true);
                                    }
                                }
                            });
                            leaveChannel = false;
                            /*final RtcEngine rtcEngine = workerThread.getAgoraRtcEngine();
                            if (rtcEngine == null) {
                                workerThread.execute(new Runnable() {
                                    @Override
                                    public void run() {
//                                        JSONObject jsonObject = new JSONObject();
//                                        try {
//                                            jsonObject.put("che.video.enableRemoteViewMirror", true);
//                                            int set = rtcEngine.setParameters(jsonObject.toString());
//                                            logger.d("setParameters:set=" + set);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
                                        RTCEngine mRtcEngine = workerThread.getRtcEngine();
                                        if (mRtcEngine != null) {
                                            mRtcEngine.setRemoteMirror(true);
//                                            mRtcEngine.setMirror(false);
                                        }
                                    }
                                });
                            }*/
                            workerThread.joinChannel(new CloudWorkerThreadPool.OnJoinChannel() {
                                @Override
                                public void onJoinChannel(int joinChannel) {
                                    logger.d("onJoinChannel:joinChannel=" + joinChannel);
                                    setAudioMode("joinChannel");
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
        workerThread.setEnableLocalAudio(audioStatus);
        workerThread.eventHandler().addEventHandler(listener);
        workerThread.start();
        rl_livevideo_primary_team_content.setVisibility(View.VISIBLE);
        ivPkState.setVisibility(View.GONE);
        tvPrimaryTeamName.setText(teamInfoEntity.getTeamName());
        if (!showTeamMid) {
            showTeamMid = true;
            if (!teamInfoEntity.isFromLocal()) {
                tvPrimaryTeamNameMid.setText("欢迎加入 “" + teamInfoEntity.getTeamName() + "”");
                tvPrimaryTeamNameMid.setVisibility(View.VISIBLE);
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvPrimaryTeamNameMid.setVisibility(View.GONE);
                    }
                }, 2000);
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvPrimaryTeamNameMid.getLayoutParams();
//                int leftMargin = (ScreenUtils.getScreenWidth() - ivLivePrimaryClassKuangjiaImgNormal.getWidth()) / 2 + (int) (1121 * scale);
//                lp.leftMargin = leftMargin / 2;
//                tvPrimaryTeamNameMid.setLayoutParams(lp);
            }
        }
//        ivPrimaryTeamIcon
        ImageLoader.with(mContext.getApplicationContext()).load(teamInfoEntity.getImg()).into(ivPrimaryTeamIcon);
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
        mainHandler.removeCallbacks(ivPkStateRun);
        mainHandler.postDelayed(ivPkStateRun, 10000);
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
        llPrimaryTeamContent.removeAllViews();
        List<TeamMate> result;
        if (teamInfoEntity != null) {
            result = teamInfoEntity.getResult();
            mLogtf.d("addItem:size=" + result.size());
        } else {
            result = new ArrayList<>();
            //把自己加入
//            TeamMate myTeam = new TeamMate();
//            myTeam.setId("" + stuid);
//            myTeam.setIdInt(stuid);
//            myTeam.setName(stuName);
//            result.add(myTeam);
        }
        logger.d("addItem:size=" + result.size());
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        int live_primary_right_head_gap = primaryClassView.getLive_primary_right_head_gap();
        int margin = (int) (live_primary_right_head_gap * scaleX);
        int live_primary_right_item_height = primaryClassView.getLive_primary_right_item_height();
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
                    otherItem.setIndex(i);
                    basePrimaryTeamItem = otherItem;
                }
            } else {
                basePrimaryTeamItem = new PrimaryTeamEmptyItem(mContext, null, workerThread, -1);
            }
            View convertView = mInflater.inflate(basePrimaryTeamItem.getLayoutResId(), llPrimaryTeamContent, false);
            convertView.setTag(basePrimaryTeamItem);
            basePrimaryTeamItem.initViews(convertView);
            basePrimaryTeamItem.updateViews(teamMember, i, teamMember);
            basePrimaryTeamItem.bindListener();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) convertView.getLayoutParams();
            lp.height = (int) (live_primary_right_item_height * scaleX);
            lp.bottomMargin = margin;
            if (isMe) {
                llPrimaryTeamContent.addView(convertView, 0, lp);
            } else {
                llPrimaryTeamContent.addView(convertView, lp);
            }
            if (teamMember == null) {
                courseGroupItemHashMap.put("empty" + i, basePrimaryTeamItem);
            } else {
                courseGroupItemHashMap.put("" + teamMember.getId(), basePrimaryTeamItem);
                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, videoStatus, mState);
                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, audioStatus, mState);
            }
        }
    }

    BasePrimaryTeamPeopleItem.OnNameClick onNameClick = new BasePrimaryTeamPeopleItem.OnNameClick() {
        @Override
        public void onNameClick(final TeamMate finalEntity, TextView tvName) {
            if (clPrimaryTeamInter.getVisibility() == View.VISIBLE) {
                clPrimaryTeamInter.setVisibility(View.GONE);
                hideInter(false);
            } else {
                clPrimaryTeamInter.setVisibility(View.VISIBLE);
                hideInter(true);
                int[] loc = ViewUtil.getLoc(tvName, (ViewGroup) mView);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) clPrimaryTeamInter.getLayoutParams();
                lp.leftMargin = loc[0] - (clPrimaryTeamInter.getWidth() - tvName.getWidth()) / 2;
                lp.topMargin = loc[1] - clPrimaryTeamInter.getHeight();
                clPrimaryTeamInter.setLayoutParams(lp);
                tvPrimaryTeamInterLeft.setText(finalEntity.isLook() ? "不看ta" : "显示ta");
                tvPrimaryTeamInterLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clPrimaryTeamInter.setVisibility(View.GONE);
                        hideInter(false);
                        BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + finalEntity.getId());
                        if (basePrimaryTeamItem != null) {
                            basePrimaryTeamItem.onVideo();
                        }
                    }
                });
                final TextView tv_livevideo_primary_team_inter_right = clPrimaryTeamInter.findViewById(R.id.tv_livevideo_primary_team_inter_right);
                tv_livevideo_primary_team_inter_right.setText("举报");
                tv_livevideo_primary_team_inter_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clPrimaryTeamInter.setVisibility(View.GONE);
                        hideInter(false);
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

    private void doRenderRemoteUi(final long uid, final BasePrimaryTeamItem courseGroupItem) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                SurfaceView surfaceV = workerThread.getRtcEngine().createRendererView();
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                workerThread.getRtcEngine().setupRemoteVideo(surfaceV, uid);
                courseGroupItem.doRenderRemoteUi(surfaceV);
            }
        });
    }

    private RTCEngine.IRtcEngineEventListener listener = new RTCEngine.IRtcEngineEventListener() {
        @Override
        public void remotefirstVideoRecvWithUid(long uid) {
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            mLogtf.d("remotefirstVideoRecvWithUid:uid=" + uid + ",item=" + (basePrimaryTeamItem == null));
            if (basePrimaryTeamItem != null) {
                doRenderRemoteUi(uid, basePrimaryTeamItem);
            } else {
                //后进入用户，暂存视频布局
                SurfaceView surfaceV = workerThread.getRtcEngine().createRendererView();
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                surfaceViewHashMap.put("" + uid, surfaceV);
            }
        }

        @Override
        public void remoteUserJoinWitnUid(long uid) {
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            if (basePrimaryTeamItem != null) {
                basePrimaryTeamItem.didOfflineOfUid("remoteUserJoinWitnUid", true);
            } else {
                //后进入用户，不在本地，请求接口
                userOnLineStat.put("" + uid, true);
                mLogtf.d("remoteUserJoinWitnUid:uid=" + uid);
                primaryClassInter.getMyTeamInfo();
            }
        }

        @Override
        public void didOfflineOfUid(long uid) {
            surfaceViewHashMap.remove("" + uid);
            userVoiceStat.remove("" + uid);
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            if (basePrimaryTeamItem != null) {
                basePrimaryTeamItem.didOfflineOfUid("didOfflineOfUid", false);
            } else {
                userOnLineStat.remove("" + uid);
            }
        }

        @Override
        public void didAudioMuted(long uid, boolean muted) {

        }

        @Override
        public void didVideoMuted(long uid, boolean muted) {

        }

        @Override
        public void didOccurError(RTCEngine.RTCEngineErrorCode code) {
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + stuid);
            mLogtf.d("didOccurError:code=" + code + ",item=" + (basePrimaryTeamItem == null));
            if (basePrimaryTeamItem instanceof PrimaryTeamMyItem) {
                PrimaryTeamMyItem myItem = (PrimaryTeamMyItem) basePrimaryTeamItem;
                myItem.didOccurError(code);
            }
        }

        @Override
        public void connectionChangedToState(RTCConnectionStateType state, String reason){

        }

        @Override
        public void localUserJoindWithUid(long uid) {
            if (stuid == uid) {
                BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
                if (basePrimaryTeamItem != null) {
                    preview(basePrimaryTeamItem);
                    basePrimaryTeamItem.didOfflineOfUid("localUserJoindWithUid", true);
                }
            }
        }

        @Override
        public void reportAudioVolumeOfSpeaker(long uid, int volume) {
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
        public void remotefirstAudioRecvWithUid(long uid) {
            userVoiceStat.put("" + uid, true);
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            mLogtf.d("remotefirstAudioRecvWithUid:uid=" + uid + ",item=" + (basePrimaryTeamItem == null));
            if (basePrimaryTeamItem instanceof BasePrimaryTeamPeopleItem) {
                BasePrimaryTeamPeopleItem peopleItem = (BasePrimaryTeamPeopleItem) basePrimaryTeamItem;
                peopleItem.remotefirstAudioRecvWithUid(uid);
            }
        }

        @Override
        public void onRemoteVideoStateChanged(long uid, int state) {
            BasePrimaryTeamItem basePrimaryTeamItem = courseGroupItemHashMap.get("" + uid);
            mLogtf.d("onRemoteVideoStateChanged:uid=" + uid + ",state=" + state + ",item=" + (basePrimaryTeamItem == null));
            if (basePrimaryTeamItem instanceof PrimaryTeamOtherItem) {
                PrimaryTeamOtherItem otherItem = (PrimaryTeamOtherItem) basePrimaryTeamItem;
                otherItem.onRemoteVideoStateChanged(uid, state);
            }
        }

        @Override
        public void onOnceLastMileQuality(RTCEngine.RTC_LASTMILE_QUALITY lastmileQuality) {

        }

        @Override
        public void reportRtcStats(RTCEngine.ReportRtcStats stats) {

        }
    };

    private void preview(final BasePrimaryTeamItem courseGroupItem) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                SurfaceView surfaceV = workerThread.getRtcEngine().createRendererView();
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                workerThread.preview(true, surfaceV);
                courseGroupItem.doRenderRemoteUi(surfaceV);
            }
        });
    }

    @Override
    public void onMessage(int type, boolean open) {
        mLogtf.d("onMessage:type=" + type + ",open=" + open + ",state=" + mState);
        if (type == PrimaryClassConfig.MMTYPE_VIDEO) {
            if (videoStatus != open) {
                videoStatus = open;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvPrimaryTeamInterLeft.setEnabled(videoStatus);
                    }
                });
                if (open) {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, true, mState);
                        }
                    });
                } else {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, false, mState);
                        }
                    });
                }
            }
        } else if (type == PrimaryClassConfig.MMTYPE_AUDIO) {
            if (audioStatus != open) {
                audioStatus = open;
                setAudioMode("onMessage");
                if (open) {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, true, mState);
                        }
                    });
                } else {
                    foreach(new ItemCall() {
                        @Override
                        public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, false, mState);
                        }
                    });
                }
            }
        }
    }

    private void setAudioMode(final String method) {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    AudioManager mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
                    int mode = mAM.getMode();
                    mLogtf.d("setAudioMode:mode=" + mode + ",open=" + audioStatus + ",method=" + method);
                    if (audioStatus) {
                        mAM.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    } else {
                        mAM.setMode(AudioManager.MODE_NORMAL);
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        }, 1000);
    }

    @Override
    public void onMessage(boolean videoopen, boolean audioopen) {
        mLogtf.d("onMessage:videoopen=" + videoopen + ",audioopen=" + audioopen + ",state=" + mState);
        if (videoStatus != videoopen) {
            videoStatus = videoopen;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    tvPrimaryTeamInterLeft.setEnabled(videoStatus);
                }
            });
            if (videoopen) {
                foreach(new ItemCall() {
                    @Override
                    public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, true, mState);
                    }
                });
            } else {
                foreach(new ItemCall() {
                    @Override
                    public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, false, mState);
                    }
                });
            }
        }
        if (audioStatus != audioopen) {
            audioStatus = audioopen;
            if (audioopen) {
                foreach(new ItemCall() {
                    @Override
                    public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, true, mState);
                    }
                });
            } else {
                foreach(new ItemCall() {
                    @Override
                    public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, false, mState);
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
        if (workerThread != null && LiveTopic.MODE_CLASS.equals(mode)) {
            mLogtf.d("onResume:havepause=" + havepause);
            if (havepause) {
                workerThread.joinChannel(new CloudWorkerThreadPool.OnJoinChannel() {
                    @Override
                    public void onJoinChannel(int joinChannel) {
                        PrimaryTeamMyItem basePrimaryTeamItem = (PrimaryTeamMyItem) courseGroupItemHashMap.get("" + stuid);
                        if (basePrimaryTeamItem != null) {
//                        basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, audioStatus, mState);
                            basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, audioStatus, mState);
                        }
                        setAudioMode("onResume");
                    }
                });
            }
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onCheckPermission();
                }
            }, 500);
        }
//        if (videoStatus) {
//            foreach(new ItemCall() {
//                @Override
//                public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
//                    basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, true);
//                }
//            });
//        }
//        if (audioStatus) {
//            foreach(new ItemCall() {
//                @Override
//                public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
//                    basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, true);
//                }
//            });
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (workerThread != null) {
            workerThread.leaveChannel();
            boolean camera = XesPermission.checkPermissionHave(mContext, PermissionConfig.PERMISSION_CODE_CAMERA);
            boolean audio = XesPermission.checkPermissionHave(mContext, PermissionConfig.PERMISSION_CODE_AUDIO);
            havecamera = camera;
            haveaudio = audio;
            havepause = true;
        }
//        foreach(new ItemCall() {
//            @Override
//            public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
//                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_VIDEO, false);
//            }
//        });
//        foreach(new ItemCall() {
//            @Override
//            public void onItem(BasePrimaryTeamItem basePrimaryTeamItem) {
//                basePrimaryTeamItem.onOtherDis(PrimaryClassConfig.MMTYPE_AUDIO, false);
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (workerThread != null) {
            workerThread.exit();
            agoraUpload.upload(fileFullPath, liveId);
        }
    }

}

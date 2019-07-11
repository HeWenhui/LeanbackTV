package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.BusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewActionIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllBllConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.switchflow.SwitchFlowBll;
import com.xueersi.parentsmeeting.modules.livevideo.switchflow.SwitchFlowRoutePager;
import com.xueersi.parentsmeeting.modules.livevideo.switchflow.SwitchFlowView;
import com.xueersi.parentsmeeting.modules.livevideo.switchflow.SwitchRouteSuccessDialog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TripleScreenBasePlayerFragment;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoFragment extends LiveFragmentBase implements VideoAction, BaseLiveMessagePager.OnMsgUrlClick {
    private String TAG = "LiveVideoFragment";
    Logger logger = LiveLoggerFactory.getLogger(TAG);

    public LiveVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_new;
    }

    protected LiveViewAction liveViewAction;
    protected RelativeLayout bottomContent;
    protected String vStuCourseID;
    protected String courseId;
    /** 小学英语 */
    private boolean isSmallEnglish;
    private LiveVideoSAConfig liveVideoSAConfig;
    /** 是不是文理 */
    public boolean IS_SCIENCE = true;
    /** 是不是文科 */
    protected int isArts;

    protected BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    protected BaseLiveMediaControllerBottom liveMediaControllerBottom;

    /** onPause状态不暂停视频 */
    PauseNotStopVideoIml pauseNotStopVideoIml;
    protected String mode = LiveTopic.MODE_TRANING;
    /** 播放器的Fragment */
    protected LivePlayerFragment liveVideoPlayFragment;
    /** 是否是三分屏或者全身直播 */
    protected int pattern;
    /** 切流加载中的布局 */
//    private ConstraintLayout layoutVideoFailRetry;
    /** 切流加载中的按钮o */
    private Button btnVideoFailRetry;
    /** 是否上传切流埋点日志 */
    private boolean isSwitchUpload = false;
    /** 用户严重处于哪条线路,比list中的实际多1 */
    private int userEyePos = 1;
    /** 当前实际所在线路，从0开始计数 */
//    private int nowPos = 0;
    /** 当前切换线路的线路总数 */
    private int totalSwitchRouteNum = 0;
    private int isGoldMicrophone;

    private int useSuperSpeakerShow;

    /** {@link #onActivityCreated(Bundle)} */
    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        boolean onVideoCreate = super.onVideoCreate(savedInstanceState);
        if (onVideoCreate) {
            isArts = activity.getIntent().getIntExtra("isArts", -1);
            isSmallEnglish = activity.getIntent().getBooleanExtra("isSmallEnglish", false);
            isGoldMicrophone = activity.getIntent().getIntExtra("isGoldMicrophone", 0);
            useSuperSpeakerShow = activity.getIntent().getIntExtra("useSuperSpeakerShow", 0);
            //logger.e("========>:onVideoCreate 22222229999000:");
            pattern = activity.getIntent().getIntExtra("pattern", LiveVideoConfig.LIVE_PATTERN_COMMON);
            String mode2 = activity.getIntent().getStringExtra("mode");
            if (mode2 != null) {
                mode = mode2;
            }
            mLiveBll.addBusinessShareParam("isArts", isArts);
            createLiveVideoAction();
            liveVideoAction.setFirstParam(LiveVideoPoint.getInstance());
            long before = System.currentTimeMillis();
            initAllBll();
            logger.d("onVideoCreate:time2=" + (System.currentTimeMillis() - before));
            before = System.currentTimeMillis();
            addBusiness(activity);
            logger.d("onVideoCreate:time3=" + (System.currentTimeMillis() - before));
            if ((pattern == LiveVideoConfig.LIVE_PATTERN_COMMON)) {
                //根据不同直播显示不同加载中动画
                setLoadingView();
            }
        }
        return onVideoCreate;
    }

    /** 设置显示的加载动画 */
    protected void setLoadingView() {
        liveVideoPlayFragment = (LivePlayerFragment) getChildFragmentManager().findFragmentByTag("LivePlayerFragment");
        if (LiveVideoConfig.isSmallChinese) {
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_PRIMARY_CHINESE_LOADING);
        } else if (LiveVideoConfig.isPrimary) {
            mLogtf.i("primary_science_loading");
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_PRIMARY_SCIENCE_LOADING);
        } else if (isSmallEnglish) {
            mLogtf.i("primary_english_loading");
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_PRIMARY_ENGLISH_LOADING);
        } else {
            mLogtf.i("other loading");
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_MIDDLE_LOADING);
        }
        liveVideoPlayFragment.overrideHandlerCallBack();
    }

    @Override
    protected void onBusinessCreate() {
        super.onBusinessCreate();
        bottomContent.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child.getId() == View.NO_ID) {
                    logger.d("onChildViewAdded:child=" + child);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
        List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.initViewF(liveViewAction, bottomContent, mIsLand, mContentView);
        }
    }

    @Override
    protected void startGetInfo() {
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
        LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
        if (mGetInfo != null) {
            mode = mGetInfo.getMode();
        }
        mLiveBll.getInfo(mGetInfo);
    }

    @Override
    protected void onGlobalLayoutListener() {
        super.onGlobalLayoutListener();
        setMediaControllerBottomParam();
    }


    /** 三分屏使用新的的Loading,在{@link #loadView(int)} 中调用 */
//    @Override
//    protected LivePlayerFragment getFragment() {
//        LiveLivePlayerPlayFragment liveVideoPlayFragment = new LiveLivePlayerPlayFragment();
//        liveVideoPlayFragment.setLiveFragmentBase(this);
//        return liveVideoPlayFragment;
//    }

    /**
     * 添加 直播间内 所需的功能模块
     *
     * @param activity
     */
    protected void addBusiness(Activity activity) {
        ProxUtil.getProxUtil().put(activity, BaseLiveMediaControllerTop.class, baseLiveMediaControllerTop);
        ProxUtil.getProxUtil().put(activity, BaseLiveMediaControllerBottom.class, liveMediaControllerBottom);
        ProxUtil.getProxUtil().put(activity, LivePlayAction.class, this);
        //是文科
        ArrayList<BllConfigEntity> bllConfigEntities;
        if (isArts == LiveVideoSAConfig.ART_EN) {
            bllConfigEntities = AllBllConfig.getLiveBusinessArts();
        } else if (isArts == LiveVideoSAConfig.ART_CH) {
            bllConfigEntities = AllBllConfig.getLiveBusinessCn();
        } else {
            bllConfigEntities = AllBllConfig.getLiveBusinessScience(activity.getIntent());
        }
        if ((pattern == LiveVideoConfig.LIVE_PATTERN_COMMON)) {
            addSwitchFlowBll();
            initSwitchFlowListener();
        }
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                mLiveBll.addBusinessBll(liveBaseBll);
            }
        }
    }

    protected LiveBaseBll creatBll(BllConfigEntity bllConfigEntity) {
        String className = "";
        try {
            className = bllConfigEntity.className;
            Class<?> c = Class.forName(className);
            Class<? extends LiveBaseBll> clazz;
            if (BusinessCreat.class.isAssignableFrom(c)) {
                Class<? extends BusinessCreat> creatClazz = (Class<? extends BusinessCreat>) c;
                BusinessCreat businessCreat = creatClazz.newInstance();
                clazz = businessCreat.getClassName(activity.getIntent());
                if (clazz == null) {
                    return null;
                }
            } else if (LiveBaseBll.class.isAssignableFrom(c)) {
                clazz = (Class<? extends LiveBaseBll>) c;
            } else {
                return null;
            }
            Constructor<? extends LiveBaseBll> constructor = clazz.getConstructor(new Class[]{Activity.class, LiveBll2.class});
            LiveBaseBll liveBaseBll = constructor.newInstance(activity, mLiveBll);
            logger.d("creatBll:business=" + className);
            return liveBaseBll;
        } catch (Exception e) {
            logger.d("creatBll:business=" + className, e);
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return null;
    }

    /** 加载切流的Bll */
    private SwitchFlowBll switchFlowBll;
    /** 点击重新加载按钮 */
//    private volatile boolean isSwitchReloadShow;
    /** 当前处于什么状态 */
    private int switchFlowStatus = LiveVideoAction.SWITCH_FLOW_NORMAL;

    /** 视频播放成功 */
    @Override
    protected void onPlayOpenSuccess() {
        super.onPlayOpenSuccess();
        //如果之前是正在切流的状态
        if ((pattern == LiveVideoConfig.LIVE_PATTERN_COMMON)) {
            if (liveVideoAction != null) {//处于后台时这里容易null,所以加上非空判断
                liveVideoAction.onPlaySuccess();
            }
            if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_ROUTE_SWITCH) {
                //用户眼中的线路，比实际线路大1
                if (isSwitchUpload) {
                    UmsAgentManager.umsAgentCustomerBusiness(getActivity(), getActivity().getResources().getString(R.string
                            .livevideo_switch_flow_170711));
                }
                if (LiveVideoConfig.isPrimary || isSmallEnglish || LiveVideoConfig.isSmallChinese) {
                    SwitchRouteSuccessDialog switchRouteSuccessDialog = new SwitchRouteSuccessDialog(activity);
                    switchRouteSuccessDialog.updateView(this.userEyePos);
                    switchRouteSuccessDialog.showDialogAutoClose(2000);
                } else {
                    XESToastUtils.showToast(activity, "线路" + this.userEyePos + "切换成功");
                }
                mLogtf.i("route " + this.userEyePos + "(add 1) switch success");
            } else if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_RELOAD) {
                if (isSwitchUpload) {
                    UmsAgentManager.umsAgentCustomerBusiness(getActivity(), getActivity().getResources().getString(R.string
                            .livevideo_switch_flow_17079));
                }
            }
        }
        resetStatus();
    }

    /** 重置切换线路的标志位 */
    private void resetStatus() {
        switchFlowStatus = LiveVideoAction.SWITCH_FLOW_NORMAL;
        if (liveVideoAction != null) {
            liveVideoAction.setVideoSwitchFlowStatus(switchFlowStatus, 0);
        }
    }


    private void addSwitchFlowBll() {
        if (getSwitchFlowView() == null) {
            return;
        }
        switchFlowBll = new SwitchFlowBll(activity, mLiveBll);
        mLiveBll.addBusinessBll(switchFlowBll);
        //设置线路总数
        switchFlowBll.setListRoute(totalSwitchRouteNum);

        liveVideoAction.setVideoSwitchFlowStatus(switchFlowStatus, userEyePos);
        //设置最多的bll
        switchFlowBll.setmView(getSwitchFlowView(), liveMediaControllerBottom,
                new SwitchFlowView.IReLoad() {
                    @Override
                    public void reLoad() {
//                        isSwitchReloadShow = true;
                        mLogtf.i("switchFlowView click reload");
                        if (!mLiveBll.isPresent()) {
                            if (mContentView.findViewById(R.id.iv_course_video_teacher_notpresent) != null) {
                                mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.GONE);
                            }
                        }
                        switchFlowStatus = LiveVideoAction.SWITCH_FLOW_RELOAD;
                        isSwitchUpload = true;
                        //1.重新加载,显示加载中
                        if (!MediaPlayer.getIsNewIJK()) {
                            rePlay(false);
                        }
//                        else {
//                            psRePlay(false);
//                        }
                        //2. 自动切流
                        liveVideoAction.setVideoSwitchFlowStatus(switchFlowStatus, userEyePos);
                        if (mGetInfo != null && mGetInfo.getLiveTopic() != null) {
                            //调度里面会重新走replay
                            mLiveVideoBll.liveGetPlayServer(mGetInfo.getLiveTopic().getMode(), false);
                        }
                    }
                },
                new SwitchFlowRoutePager.ItemClickListener() {
                    @Override
                    public void itemClick(int pos) {
                        mLogtf.i("switchFlowView click switch,click pos=" + pos);
                        switchFlowStatus = LiveVideoAction.SWITCH_FLOW_ROUTE_SWITCH;
                        isSwitchUpload = true;
                        if (!mLiveBll.isPresent()) {
                            if (mContentView.findViewById(R.id.iv_course_video_teacher_notpresent) != null) {
                                mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.GONE);
                            }
                        }
                        //todo 显示线路切换中的字样
                        changeLine(pos);
                        userEyePos = pos + 1;
                        liveVideoAction.setVideoSwitchFlowStatus(switchFlowStatus, userEyePos);
                        liveVideoAction.rePlay(false);
//                        liveVideoAction.setVideoSwitchFlowStatus(LiveAc);
//                        tvLoadingTint.setText("线路" + String.valueOf(pos) + "切换中...");
                    }
                });
    }

    /**
     * 获取切流的btn，
     * 在{@link #addSwitchFlowBll()}里面调用
     * {@link SwitchFlowBll}}
     */
    private SwitchFlowView getSwitchFlowView() {
//        switchFlowView =
        return liveMediaControllerBottom.getSwitchFlowView();
    }

    private void initSwitchFlowListener() {
        btnVideoFailRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.i("点击重试按钮");
                if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_RELOAD) {
                    mLogtf.i("click again btn,SWITCH_FLOW_RELOAD");
                    //1.重新加载,显示加载中
                    if (!MediaPlayer.getIsNewIJK()) {
                        rePlay(false);
                    } else {
                        psRePlay(false);
                    }
                    //2. 自动切流
                    if (mGetInfo != null && mGetInfo.getLiveTopic() != null) {
                        mLiveVideoBll.liveGetPlayServer(mGetInfo.getLiveTopic().getMode(), false);
                    }
                } else if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_ROUTE_SWITCH) {
                    mLogtf.i("click again btn,SWITCH_FLOW_ROUTE_SWITCH");
                    if (!MediaPlayer.getIsNewIJK()) {
                        rePlay(false);
                    } else {
                        changeLine(userEyePos - 1);
                    }
                } else {
                    mLogtf.i("click again btn,other");
                    if (!MediaPlayer.getIsNewIJK()) {
                        rePlay(false);
                    } else {
                        psRePlay(false);
                    }
                }
                if (!mLiveBll.isPresent()) {
                    mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void showLongMediaController() {
        super.showLongMediaController();
    }

    @Override
    protected void initView() {
        bottomContent = mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        liveViewAction = new LiveViewActionIml(activity, mContentView, bottomContent);

//        tvLoadingTint = mContentView.findViewById(R.id.tv_course_video_loading_content);

        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        createMediaControlerTop();
        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        createMediaControllerBottom();

        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, liveMediaControllerBottom, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        pattern = activity.getIntent().getIntExtra("pattern", LiveVideoConfig.LIVE_PATTERN_COMMON);
        if ((pattern == LiveVideoConfig.LIVE_PATTERN_COMMON)) {
            btnVideoFailRetry = mContentView.findViewById(R.id.btn_livevideo_switch_flow_retry_btn);
        }
        //如果是三分屏，则需要添加加载中的监听器
    }

    @Override
    protected void onFail(int arg1, int arg2) {
        super.onFail(arg1, arg2);
        isSwitchUpload = false;
        if (liveVideoAction != null) {
            MediaErrorInfo mediaErrorInfo = videoFragment.getMediaErrorInfo();
            liveVideoAction.onFail(mediaErrorInfo);
            switch (arg2) {
                case MediaErrorInfo.PSDispatchFailed: {
//                        if (mediaListener != null) {
//                            mediaListener.getPServerListFail(getMediaErrorInfo());
//                        }
                    //调度失败，建议重新访问playLive或者playVod频道不存在
                    //调度失败，延迟1s再次访问调度

                    if ((pattern == LiveVideoConfig.LIVE_PATTERN_COMMON) && switchFlowBll != null) {
//            if (server != null) {
//                        switchFlowBll.setListRoute(0);
                        logger.i("0");
//        } else {
//            switchFlowBll.setListRoute(null);
//            logger.i("null");
//        }
                    }
                }
                break;
                case MediaErrorInfo.PSServer403: {

                }
                break;
                default:
                    break;
            }
        }
    }


    /** 新psijk已经不再使用 更新调度的list，无论成功失败都会走 */
    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        super.onLiveStart(server, cacheData, modechange);
        if ((pattern == LiveVideoConfig.LIVE_PATTERN_COMMON) && switchFlowBll != null) {
            if (server != null) {
                switchFlowBll.setListRoute(server.getPlayserver());
                logger.i(server.getPlayserver().size());
            } else {
                switchFlowBll.setListRoute(null);
                logger.i("null");
            }
        }
    }

    /**
     * 更新线路数目,由
     * {@link
     * com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll
     * #mPlayListener
     * #getPSServerList(int, int, boolean)}
     * 调用
     *
     * @param cur        当前播放线路索引(由0开始算)
     * @param total      所有播放线路总数
     * @param modeChange
     */
    @Override
    public void getPSServerList(int cur, int total, boolean modeChange) {
        super.getPSServerList(cur, total, modeChange);
        this.totalSwitchRouteNum = total;
//        this.userEyePos = cur + 1;
        if ((pattern == LiveVideoConfig.LIVE_PATTERN_COMMON) && switchFlowBll != null) {
//            if (total != 0) {
            switchFlowBll.setListRoute(total);
//                logger.i(total);
//            } else {
//                switchFlowBll.setListRoute(0);
            logger.i("total = " + total);
//            }
        }
    }

    /**
     * 获取调度接口失败
     */
//    @Override
//    public void getPServerListFail() {
//        if ((pattern == 1) && switchFlowBll != null) {
////            if (server != null) {
//            switchFlowBll.setListRoute(0);
//            logger.i("0");
////        } else {
////            switchFlowBll.setListRoute(null);
////            logger.i("null");
////        }
//        }
//
//    }
    protected void createMediaControllerBottom() {
        Intent intent = activity.getIntent();
        LiveVideoConfig.isPrimary = intent.getBooleanExtra("isPrimary", false);
        LiveVideoConfig.isSmallChinese = intent.getBooleanExtra("isSmallChinese", false);
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }

    protected void createMediaControlerTop() {
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
    }


    @Override
    public boolean initData() {
        Intent intent = activity.getIntent();
        courseId = intent.getStringExtra("courseId");
        vStuCourseID = intent.getStringExtra("vStuCourseID");
        mVSectionID = intent.getStringExtra("vSectionID");
        mVideoType = MobEnumUtil.VIDEO_LIVE;
        if (TextUtils.isEmpty(mVSectionID)) {
            Toast.makeText(activity, "直播场次不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
        XesMobAgent.enterLiveRoomFrom(from);
        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
            LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
            if (mGetInfo != null) {
                mode = mGetInfo.getMode();
            }
            mLiveBll = new LiveBll2(activity, vStuCourseID, courseId, mVSectionID, from, mGetInfo);
        } else if (liveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
            mLiveBll = new LiveBll2(activity, mVSectionID, intent.getStringExtra("currentDutyId"), liveType, from);
        } else {
            Toast.makeText(activity, "直播类型不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
        ProxUtil.getProxUtil().put(activity, LiveBll2.class, mLiveBll);
        return true;
    }

    private void initAllBll() {
        logger.d("====>initAllBll:" + bottomContent);
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        setMediaControllerBottomParam();
        videoFragment.setIsAutoOrientation(false);
        mLiveBll.addBusinessShareParam("videoView", videoView);
        mLiveBll.addBusinessShareParam("mMediaController", mMediaController);
        mLiveBll.addBusinessShareParam("liveMediaControllerBottom", liveMediaControllerBottom);
        pauseNotStopVideoIml = new PauseNotStopVideoIml(activity);
    }

    /**
     * 控制栏下面距离视频底部的尺寸
     */
    public void setMediaControllerBottomParam() {
        //控制栏下面距离视频底部的尺寸
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
        int topGap = liveVideoPoint.y2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsLand.get()) {
            mMediaController.setControllerBottom(liveMediaControllerBottom, false);
            setMediaControllerBottomParam();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveStop) {
            mHaveStop = false;
            VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
            if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
                return;
            }

            if (!pauseNotStopVideoIml.getPause()) {
                setFirstBackgroundVisible(View.VISIBLE);
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!MediaPlayer.getIsNewIJK()) {
                                        rePlay(false);
                                    } else {
                                        psRePlay(false);
                                    }
                                }
                            });
                        }
                    }
                });
            }
            pauseNotStopVideoIml.setPause(false);
        }
        if (mLiveBll != null) {
            mLiveBll.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHaveStop = true;
        VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
        if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
            return;
        }
        if (!pauseNotStopVideoIml.getPause()) {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        if (isInitialized()) {
                            if (openSuccess) {
                                mLiveVideoBll.stopPlayDuration();
                                logger.d("onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
                            }
                            vPlayer.releaseSurface();
                            vPlayer.stop();
                        } else {
                            logger.d("onPause:isInitialized=false");
                        }
                        isPlay = false;
                    }
                }
            });
        }
        if (mLiveBll != null) {
            mLiveBll.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLiveBll != null) {
            mLiveBll.onStop();
        }
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        mode = mGetInfo.getMode();
        liveVideoSAConfig = mLiveBll.getLiveVideoSAConfig();
        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
        liveMediaControllerBottom.setVisibility(View.VISIBLE);
        long before = System.currentTimeMillis();
        mMediaController.setFileName(getInfo.getName());
        logger.d("onLiveInit:time3=" + (System.currentTimeMillis() - before));
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        LiveVideoFragment.this.mode = mGetInfo.getMode();
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (isInitialized()) {
                    mLiveVideoBll.stopPlayDuration();
                    vPlayer.releaseSurface();
                    vPlayer.stop();
                }
                isPlay = false;
                if (liveVideoAction != null) {
                    liveVideoAction.onModeChange(mode, isPresent);
                }
            }
        });
    }

    @Override
    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
        if (liveVideoAction != null) {
            liveVideoAction.onPlayError(errorCode, playErrorCode);
        }
    }

    /**
     * 第一次播放，或者播放失败，重新播放
     *
     * @param modechange
     */
    @Deprecated
    @Override
    public void rePlay(boolean modechange) {

    }

    /** PSIJK使用的rePlay */
    @Override
    public void psRePlay(boolean modeChange) {
        mLogtf.d("rePlay:mHaveStop=" + mHaveStop);
        if (mGetInfo == null || liveVideoAction == null) {//上次初始化尚未完成
            return;
        }
        VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
        if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
            return;
        }
        liveVideoAction.rePlay(modeChange);
        mLiveVideoBll.psRePlay(modeChange);
    }

    @Override
    public void changeLine(int pos) {
        if (mLiveVideoBll != null) {
            mLiveVideoBll.changeLine(pos);
        }
    }

    @Override
    public void changeNextLine() {
        if (mLiveVideoBll != null) {
            mLiveVideoBll.changeNextLine();
        }
//        }
    }

    @Override
    public void changeNowLine() {
        if (mLiveVideoBll != null) {
            mLiveVideoBll.changeNowLine();
        }
    }

    @Override
    public void onMsgUrlClick(String url) {
//        onPauseNotStopVideo = true;
    }

    /**
     * 使用psijk
     */
}

package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVoteBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.RankBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.ArtsPraiseListBll;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseListIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerRankIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechCollectiveIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechFeedBackIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.worddictation.business.WordDictationIRCBll;

import java.util.List;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoFragment extends LiveFragmentBase implements VideoAction, BaseLiveMessagePager.OnMsgUrlClick {
    private String TAG = "LiveVideoFragment";
    Logger logger = LoggerFactory.getLogger(TAG);

    public LiveVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_new;
    }

    RelativeLayout bottomContent;
    protected String vStuCourseID;
    protected String courseId;

    private LiveVideoSAConfig liveVideoSAConfig;
    /** 是不是文理 */
    public boolean IS_SCIENCE = true;
    /** 是不是文科 */
    private int isArts;

    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    protected BaseLiveMediaControllerBottom liveMediaControllerBottom;

    /** onPause状态不暂停视频 */
    PauseNotStopVideoIml pauseNotStopVideoIml;
    private LiveIRCMessageBll liveIRCMessageBll;
    protected String mode = LiveTopic.MODE_TRANING;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        boolean onVideoCreate = super.onVideoCreate(savedInstanceState);
        if (onVideoCreate) {
            isArts = activity.getIntent().getIntExtra("isArts", -1);
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
        }
        return onVideoCreate;
    }

    @Override
    protected void onBusinessCreate() {
        super.onBusinessCreate();
        List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.initViewF(bottomContent, mIsLand, mContentView);
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

    /**
     * 添加 直播间内 所需的功能模块
     *
     * @param activity
     */
    private void addBusiness(Activity activity) {
        //是文科
        if (isArts == 1) {
            liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll);
            liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            mLiveBll.addBusinessBll(liveIRCMessageBll);
            mLiveBll.addBusinessBll(new RollCallIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveAchievementIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RankBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new EnglishH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LearnReportIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new UnderstandIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new ArtsPraiseListBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new WordDictationIRCBll(activity, mLiveBll));
        } else {
            liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll);
            liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            mLiveBll.addBusinessBll(liveIRCMessageBll);
            mLiveBll.addBusinessBll(new TeamPkBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RollCallIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RankBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new EnglishH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new TeacherPraiseBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveVoteBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveAutoNoticeIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new AnswerRankIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LearnReportIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new SpeechFeedBackIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new SpeechCollectiveIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveRemarkIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new UnderstandIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new SpeechBulletScreenIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new PraiseListIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new PraiseInteractionBll(activity, mLiveBll));
        }
        VideoChatIRCBll videoChatIRCBll = new VideoChatIRCBll(activity, mLiveBll);
        videoChatIRCBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
        videoChatIRCBll.setLiveFragmentBase(this);
        mLiveBll.addBusinessBll(videoChatIRCBll);
        mLiveBll.setLiveIRCMessageBll(liveIRCMessageBll);
    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
    }

    @Override
    public void showLongMediaController() {
        super.showLongMediaController();
    }

    @Override
    protected void initView() {
        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        logger.e("========>:initView:" + bottomContent);
        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
        createMediaControllerBottom();
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
    }

    protected void createMediaControllerBottom() {
        Intent intent = activity.getIntent();
        LiveVideoConfig.isPrimary = intent.getBooleanExtra("isPrimary", false);
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
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
                                    rePlay(false);
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
    @Override
    public void rePlay(boolean modechange) {
        mLogtf.d("rePlay:mHaveStop=" + mHaveStop);
        if (mGetInfo == null || liveVideoAction == null) {//上次初始化尚未完成
            return;
        }
        VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
        if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
            return;
        }
        liveVideoAction.rePlay(modechange);
        mLiveVideoBll.rePlay(modechange);
    }

    @Override
    public void onMsgUrlClick(String url) {
//        onPauseNotStopVideo = true;
    }

}

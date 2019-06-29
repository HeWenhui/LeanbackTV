//package com.xueersi.parentsmeeting.modules.livevideo.activity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Looper;
//import android.os.Message;
//import android.text.SpannableString;
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.TextUtils;
//import android.text.style.AbsoluteSizeSpan;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.view.ViewTreeObserver;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.xueersi.common.base.BaseApplication;
//import com.xueersi.common.base.BaseBll;
//import com.xueersi.common.business.AppBll;
//import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
//import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
//import com.xueersi.common.entity.AnswerEntity;
//import com.xueersi.common.entity.BaseVideoQuestionEntity;
//import com.xueersi.common.entity.FooterIconEntity;
//import com.xueersi.common.event.AppEvent;
//import com.xueersi.common.logerhelper.MobEnumUtil;
//import com.xueersi.common.logerhelper.XesMobAgent;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.lib.framework.utils.NetWorkHelper;
//import com.xueersi.lib.framework.utils.ScreenUtils;
//import com.xueersi.lib.framework.utils.TimeUtils;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.lib.framework.utils.string.StringUtils;
//import com.xueersi.lib.imageloader.ImageLoader;
//import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
//import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
//import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
//import com.xueersi.parentsmeeting.module.videoplayer.media.VideoActivity;
//import com.xueersi.parentsmeeting.module.videoplayer.widget.LivePlaybackMediaController;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.dialog.RedPacketAlertDialog;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LecturePeopleEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageGroupEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
//import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.PutQuestion;
//import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
//import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveQuestionPager;
//import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5PlaybackPager;
//import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionFillInBlankLivePager;
//import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionMulitSelectLivePager;
//import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionSelectLivePager;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LectureLivePlaybackMediaController;
//import com.xueersi.ui.adapter.XsBaseAdapter;
//import com.xueersi.ui.dataload.DataLoadEntity;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 直播回放播放页
// *
// * @author Hua
// */
//@SuppressLint("HandlerLeak")
//@SuppressWarnings("unchecked")
//public class LectureLivePlayBackVideoActivity extends VideoActivity implements LivePlaybackMediaController.OnPointClick {
//
//    String TAG = "LectureLivePlayBackVideoActivityLog";
//
//    /** 互动题的布局 */
//    private RelativeLayout rlQuestionContent;
//
//    /** 初始进入播放器时的预加载界面 */
//    private RelativeLayout rlFirstBackgroundView;
//
//    /** 当前是否正在显示互动题 */
//    private boolean mIsShowQuestion = false;
//    /** 当前是否正在显示红包 */
//    private boolean mIsShowRedpacket = false;
//    /** 是不是点击返回键或者点周围,取消互动题,而没有使用getPopupWindow */
//    boolean mIsBackDismiss = true;
//    /** 视频节对象 */
//    VideoLivePlayBackEntity mVideoEntity;
//
//    /** 是否显示移动网络提示 */
//    private boolean mIsShowMobileAlert = true;
//
//    /** 我的课程业务层 */
//    LectureLivePlayBackBll lectureLivePlayBackBll;
//
//    /** 声明PopupWindow对象的引用 */
//    private PopupWindow mPopupWindow;
//
//    /** 试题对错弹框 */
//    PopupWindow mAnswerPopupWindow;
//
////    /** 统一的加载动画 */
////    private LoadingDialog mProgressDialog;
//
//    /** 红包弹窗 */
//    private RedPacketAlertDialog mRedPacketDialog;
//
//    /** 互动题 */
//    private VideoQuestionEntity mQuestionEntity;
//    /** 红包id */
//    private String mRedPacketId;
//
//    /** 播放路径名 */
//    private String mWebPath;
//
//    /** 节名称 */
//    private String mSectionName;
//
//    /** 显示互动题 */
//    private static final int SHOW_QUESTION = 0;
//    /** 没有互动题 */
//    private static final int NO_QUESTION = 1;
//
//    /** 加载视频提示 */
//    private ImageView ivLoading;
//    private TextView tvLoadingContent;
//
//    /** 填空题布局 */
//    QuestionFillInBlankLivePager mVideoCourseQuestionPager;
//    /** 从哪个页面跳转 */
//    String where;
//    /** 本地视频 */
//    boolean islocal;
//    ListView lvMessage;
//    ArrayList<LivePlayBackMessageEntity> allLiveMessageEntities;
//    ArrayList<LivePlayBackMessageEntity> showLiveMessageEntities;
//    MessageAdapter messageAdapter;
//    File dir;
//    private ScanRunnable scanRunnable;
//    private Handler scanHandler;
//    /** 当前时间，豪妙 */
//    private long currentMsg = 0;
//    /** 第一条消息，微妙 */
//    private long firstMsg = -1, lastMsg = -1;
//    private int messageSize;
//    private long messageTotal;
//    private boolean isEnd = false;
//    /** 滚屏锁住 */
//    private boolean lockCheck = false;
//    /** 在线人数 */
//    ArrayList<LecturePeopleEntity> onlineNumList;
//    /** 时间点 */
//    ArrayList<VideoQuestionEntity> timeEntities = new ArrayList<>();
//
//    @Override
//    protected void onVideoCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onVideoCreate(savedInstanceState);
//        AppBll.getInstance().registerAppEvent(this);
//        // 设置不可自动横竖屏
//        setAutoOrientation(false);
//        Intent intent = getIntent();
//        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
//        islocal = intent.getBooleanExtra("islocal", false);
//
//        // 加载互动题和视频列表
//        initView();
//        // 请求相应数据
//        initData();
//
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    /** 初始化互动题和竖屏时下方的列表布局 */
//    @Override
//    protected void attachMediaController() {
//        if (mMediaController != null) {
//            mMediaController.setWindowLayoutType();
//            mMediaController.release();
//        }
//
//        // 设置当前是否为横屏
//        LectureLivePlaybackMediaController mMediaController = new LectureLivePlaybackMediaController(this, this);
//        this.mMediaController = mMediaController;
//        mMediaController.setAnchorView(videoView.getRootView());
//        // 设置播放器横竖屏切换按钮不显示
//        mMediaController.setAutoOrientation(false);
//        // 播放下一个按钮不显示
//        mMediaController.setPlayNextVisable(false);
//        setFileName(); // 设置视频显示名称
//        showLongMediaController();
//        if (mIsShowQuestion || mIsShowRedpacket) {
//            mMediaController.release();
//        }
//        mMediaController.setOnLockClick(new LectureLivePlaybackMediaController.OnLockCheckedChange() {
//            @Override
//            public void onLockCheckedChange(boolean check) {
//                lockCheck = check;
//                lvMessage.setTag(lockCheck ? "1" : "0");
//                lvMessage.setVisibility(check ? View.GONE : View.VISIBLE);
//            }
//        });
//        if (lockCheck) {
//            mMediaController.setLockCheck();
//        }
//        if (messageTotal > 0) {
//            mMediaController.setLockCheckVisibility(View.VISIBLE);
//        }
//        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
//        if (lstVideoQuestion != null && lstVideoQuestion.size() != 0) {
//            mMediaController.setVideoQuestions("playback" + mVideoEntity.getvLivePlayBackType() + "-", lstVideoQuestion, vPlayer.getDuration());
//        }
//    }
//
//    @Override
//    protected void showRefresyLayout(int arg1, int arg2) {
//        super.showRefresyLayout(arg1, arg2);
//        if (rlQuestionContent != null) {
//            rlQuestionContent.removeAllViews();
//        }
//    }
//
//    /** 加载旋转屏时相关布局 */
//    @Override
//    protected void loadLandOrPortView() {
//        super.loadLandOrPortView();
//    }
//
//    private void initView() {
//        // 预加载布局
//        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
//
//        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
//        updateLoadingImage();
//        tvLoadingContent = (TextView) findViewById(R.id.tv_course_video_loading_content);
//        // 预加载布局中退出事件
//        ImageView ivBack = (ImageView) findViewById(R.id.iv_course_video_back);
//
//        if (ivBack != null) {
//            findViewById(R.id.iv_course_video_back).setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    LectureLivePlayBackVideoActivity.this.onBackPressed();
//                }
//            });
//        }
//
//        if (mIsLand) {
//            // 加载横屏时互动题的列表布局
//            rlQuestionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
//        } else {
//            if (rlQuestionContent != null) {
//                rlQuestionContent.removeAllViews();
//                rlQuestionContent = null;
//            }
//        }
//        if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
//            RelativeLayout rlQuestionContent = (RelativeLayout) findViewById(R.id.rl_course_video_record_question_content);
//            rlQuestionContent.setVisibility(View.VISIBLE);
//            View view = getLayoutInflater().inflate(R.layout.page_liveplayback_message_land, rlQuestionContent, false);
//            rlQuestionContent.addView(view);
//            LayoutParams lp = view.getLayoutParams();
//            int screenWidth = ScreenUtils.getScreenWidth();
//            lp.width = screenWidth / 2;
//            view.setLayoutParams(lp);
//            lvMessage = (ListView) view.findViewById(R.id.lv_livevideo_message);
//            lvMessage.setTag("0");
//        }
//    }
//
//    /** 竖屏时填充视频列表布局 */
//    protected void initData() {
//        BaseApplication baseApplication = (BaseApplication) getApplication();
//        mRedPacketDialog = new RedPacketAlertDialog(this, baseApplication, false);
//        lectureLivePlayBackBll = new LectureLivePlayBackBll(LectureLivePlayBackVideoActivity.this, "");
//        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
//        where = getIntent().getStringExtra("where");
//        // 如果加载不出来
//        if (tvLoadingContent != null) {
//            tvLoadingContent.setText("正在获取视频资源，请稍候");
//        }
//        // 设置播放进度
//        setmLastVideoPositionKey(mVideoEntity.getVideoCacheKey());
//        // mCourseBll.getQuestionLivePlay(section);
//
//        // 视频名
//        mSectionName = mVideoEntity.getPlayVideoName();
//        // 统计视频播放key
//        mVisitTimeKey = mVideoEntity.getVisitTimeKey();
//        // 播放器统计时长发送间隔
//        setmSendPlayVideoTime(mVideoEntity.getvCourseSendPlayVideoTime());
//        // 播放视频
//        mWebPath = mVideoEntity.getVideoPath();
////        if (BuildConfig.DEBUG && CourseInfoLiveActivity.isTest) {
////            mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
////        }
////        mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
//        if (islocal) {
//            // 互动题播放地址
//            playNewVideo(Uri.parse(mWebPath), mSectionName);
//        } else {
//            getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
//                    if (AppBll.getInstance(LectureLivePlayBackVideoActivity.this).isNetWorkAlert()) {
//                        // 互动题播放地址
//                        AppBll.getInstance(mBaseApplication);
//                        playNewVideo(Uri.parse(mWebPath), mSectionName);
//                    } else {
//                        AppBll.getInstance(mBaseApplication);
//                    }
//                    return false;
//                }
//            });
//            //测试红包自动关闭
////            rlFirstBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
////                @Override
////                public boolean onPreDraw() {
////                    rlFirstBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
////                    initRedPacketResult(10);
////                    return false;
////                }
////            });
//            //测试试卷
////            mQuestionEntity = new VideoQuestionEntity();
////            mQuestionEntity.setvQuestionID("2");
////            mQuestionEntity.setvEndTime(120);
////            showExam();
//        }
////        onlineNumList = new ArrayList<>();
////        for (int i = 0; i < 1000; i += 10) {
////            LecturePeopleEntity lecturePeopleEntity = new LecturePeopleEntity();
////            lecturePeopleEntity.id = i * 1000;
////            lecturePeopleEntity.onlineNum = i;
////            onlineNumList.add(lecturePeopleEntity);
////        }
//        try {
//            JSONArray jsonArray = new JSONArray(mVideoEntity.getOnlineNums());
//            if (jsonArray.length() > 0) {
//                onlineNumList = new ArrayList<>();
//                LecturePeopleEntity lecturePeopleEntity;
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    lecturePeopleEntity = new LecturePeopleEntity();
//                    lecturePeopleEntity.id = jsonObject.getLong("id");
//                    lecturePeopleEntity.onlineNum = jsonObject.getInt("onlineNum");
//                    onlineNumList.add(lecturePeopleEntity);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            JSONArray streamTimesArray = new JSONArray(mVideoEntity.getStreamTimes());
//            if (streamTimesArray.length() > 0) {
//                VideoQuestionEntity questionEntity;
//                for (int i = 0; i < streamTimesArray.length(); i++) {
//                    questionEntity = new VideoQuestionEntity();
//                    JSONObject streamTimeJson = streamTimesArray.getJSONObject(i);
//                    int vCategory = streamTimeJson.optInt("category");
//                    questionEntity.setvCategory(vCategory);
//                    questionEntity.setvQuestionInsretTime(streamTimeJson.optInt("begintime"));
//                    timeEntities.add(questionEntity);
//                }
//                mVideoEntity.setGotoClassTime(timeEntities.get(0).getvQuestionInsretTime());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
//            messageSize = (int) (ScreenUtils.getScreenDensity() * 16);
//            allLiveMessageEntities = new ArrayList<>();
//            showLiveMessageEntities = new ArrayList<>();
//            messageAdapter = new MessageAdapter(this, showLiveMessageEntities);
//            lvMessage.setAdapter(messageAdapter);
//            dir = new File(getCacheDir(), "lecture/" + mVideoEntity.getLiveId());
//            dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/livemessage/lecture/" + mVideoEntity.getLiveId());
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            mPlayVideoControlHandler.postDelayed(new Runnable() {
//                Runnable r;
//                String start = "" + (mVideoEntity.getGotoClassTime() * 1000000);
//
//                @Override
//                public void run() {
//                    r = this;
//                    if (isFinishing()) {
//                        return;
//                    }
//                    //"2L11144"
//                    lectureLivePlayBackBll.getLiveLectureMsgs(dir, "2L" + mVideoEntity.getLiveId(), start, timeEntities, new GetLiveLectureMsgs() {
//                        @Override
//                        public void getLiveLectureMsgs(final LiveMessageGroupEntity liveMessageGroupEntity) {
//                            final ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
//                            if (liveMessageGroupEntity.count == 0) {
//                                isEnd = true;
//                                return;
//                            }
//                            messageTotal += liveMessageGroupEntity.count;
//                            //显示关闭弹幕
//                            LectureLivePlaybackMediaController mediaController = (LectureLivePlaybackMediaController) mMediaController;
//                            if (mediaController != null) {
//                                mediaController.setLockCheckVisibility(View.VISIBLE);
//                            }
//                            //从列表最后一条+1开始
//                            start = "" + (liveMessageGroupEntity.lastid + 1);
//                            mPlayVideoControlHandler.postDelayed(r, 1000);
//                            if (lastMsg == -1 && !liveMessageEntities.isEmpty()) {
//                                lastMsg = liveMessageEntities.get(liveMessageEntities.size() - 1).getId();
//                            }
//                            if (firstMsg == -1 && !liveMessageEntities.isEmpty()) {
//                                LivePlayBackMessageEntity firstMessageEntity = null;
//                                LivePlayBackMessageEntity messageEntity = liveMessageEntities.get(0);//先试一下第一条消息
//                                long delayed = messageEntity.getId() - mVideoEntity.getGotoClassTime() * 1000000;
//                                if (delayed < 0) {
//                                    for (int i = 0; i < liveMessageEntities.size(); i++) {
//                                        messageEntity = liveMessageEntities.get(i);
//                                        delayed = messageEntity.getId() - mVideoEntity.getGotoClassTime() * 1000000;
//                                        if (delayed >= 0) {
//                                            firstMessageEntity = messageEntity;
//                                            break;
//                                        }
//                                    }
//                                } else {
//                                    firstMessageEntity = messageEntity;
//                                }
//                                logger.i( "getLiveLectureMsgs:delayed=" + delayed);
//                                if (firstMessageEntity != null) {
//                                    firstMsg = delayed;
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onPmFailure() {
//                            mPlayVideoControlHandler.postDelayed(r, 1000);
//                        }
//                    });
//                }
//            }, 1000);
//        }
//    }
//
//    class ScanRunnable implements Runnable {
//        HandlerThread handlerThread = new HandlerThread("ScanRunnable");
//
//        ScanRunnable() {
//            logger.i( "ScanRunnable");
//            handlerThread.start();
//            scanHandler = new Handler(handlerThread.getLooper());
//        }
//
//        void exit() {
//            handlerThread.quit();
//        }
//
//        @Override
//        public void run() {
//            if (isFinishing()) {
//                return;
//            }
//            if (lastMsg != -1) {
//                if (lastMsg < currentMsg * 1000) {
//                    logger.i( "ScanRunnable:lastMsg=" + lastMsg);
//                    return;
//                }
//            }
//            long nextTime = -1;
//            boolean needFind = true;
//            long gotoClassTime = mVideoEntity.getGotoClassTime();
//            long start = (gotoClassTime * 1000 + currentMsg) * 1000;
//            if (!allLiveMessageEntities.isEmpty()) {
//                if (allLiveMessageEntities.get(0).getId() < start && allLiveMessageEntities.get(allLiveMessageEntities.size() - 1).getId() > start) {
//                    needFind = false;
//                }
//            }
//            logger.i( "ScanRunnable:needFind=" + needFind + ",currentMsg=" + currentMsg);
//            if (needFind) {
//                File[] files = dir.listFiles();
//                ArrayList<LivePlayBackMessageEntity> newArrayList = new ArrayList<>();
//                if (files != null) {
//                    int firstFile = 0;//找到当前播放的前一条消息文件
//                    for (int i = 0; i < files.length; i++) {
//                        File file = files[i];
//                        long time = Long.parseLong(file.getName());
//                        if (time > currentMsg * 1000) {//找到
//                            firstFile = i;
//                            break;
//                        }
//                    }
//                    if (firstFile > 0) {//找到以后，往前减一
//                        firstFile--;
//                    }
//                    logger.i( "ScanRunnable:firstFile=" + firstFile);
//                    int current = -1;//找到当前文件
//                    LivePlayBackMessageEntity nextMessageEntity = null;
//                    for (int i = firstFile; i < files.length; i++) {
//                        File file = files[i];
//                        String fileName = file.getName();
//                        LiveMessageGroupEntity liveMessageGroupEntity = lectureLivePlayBackBll.getLiveLectureMsgsFromFile(file);
//                        if (liveMessageGroupEntity != null) {
//                            ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
//                            if (!liveMessageEntities.isEmpty()) {
//                                if (liveMessageEntities.get(0).getId() < start && liveMessageEntities.get(liveMessageEntities.size() - 1).getId() > start) {
//                                    current = i;
//                                    newArrayList.addAll(liveMessageEntities);
//                                    nextTime = -1;
//                                    logger.i( "ScanRunnable:fileName=" + fileName + ",size=" + liveMessageEntities.size());
//                                    if (newArrayList.size() > 200) {//如果当前文件记录大于200，不查询
//                                        break;
//                                    }
//                                } else {
//                                    if (current != -1 && i > current && newArrayList.size() < 200) {//如果当前文件记录小于200，继续往后查
//                                        newArrayList.addAll(liveMessageEntities);
//                                        nextTime = -1;
//                                        logger.i( "ScanRunnable:fileName=" + fileName + ",next");
//                                    } else {
//                                        if (newArrayList.isEmpty()) {//如果没有找到
//                                            for (int j = 0; j < liveMessageEntities.size(); j++) {
//                                                LivePlayBackMessageEntity messageEntity = liveMessageEntities.get(j);
//                                                if (messageEntity.getId() > currentMsg * 1000) {
//                                                    if (nextMessageEntity == null) {
//                                                        nextMessageEntity = messageEntity;
//                                                        nextTime = messageEntity.getId() - start;
//                                                        break;
//                                                    }
//                                                }
//                                            }
//                                            logger.i( "ScanRunnable:fileName=" + fileName + ",current=" + current
//                                                    + ",other:i=" + i + ",all=" + newArrayList.size() + ",nextTime=" + nextTime);
//                                        }
//                                    }
//                                }
//                            } else {
//                                logger.i( "ScanRunnable:fileName=" + fileName + ",isSpace");
//                            }
//                        } else {
//                            logger.i( "ScanRunnable:fileName=" + fileName + ",liveMessageGroupEntity=null");
//                        }
//                    }
//                    if (newArrayList.size() > 5) {
//                        allLiveMessageEntities.clear();
//                    }
//                    allLiveMessageEntities.addAll(newArrayList);
//                }
//            }
//            if (!allLiveMessageEntities.isEmpty()) {
//                for (int j = 0; j < allLiveMessageEntities.size(); j++) {
//                    LivePlayBackMessageEntity livePlayBackMessageEntity = allLiveMessageEntities.get(j);
//                    if (livePlayBackMessageEntity.getText().getCharSequence() == null) {
//                        SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(livePlayBackMessageEntity.getText().getMsg(), LectureLivePlayBackVideoActivity.this, messageSize);
//                        livePlayBackMessageEntity.getText().setCharSequence(sBuilder);
//                    }
//                }
//                final ArrayList<LivePlayBackMessageEntity> list = new ArrayList<>();
//                int oldsize = showLiveMessageEntities.size();//上次数据数量
//                boolean change = true;//和上次数据是否一样
//                if (nextTime == -1) {
//                    LivePlayBackMessageEntity nextMessageEntity = null;
//                    for (int i = allLiveMessageEntities.size() - 1; i >= 0; i--) {
//                        LivePlayBackMessageEntity messageEntity = allLiveMessageEntities.get(i);
//                        if (messageEntity.getId() < start + 100000) {//从后往前找
//                            list.add(0, messageEntity);
//                            if (nextMessageEntity == null && i != allLiveMessageEntities.size() - 1) {
//                                nextMessageEntity = allLiveMessageEntities.get(i + 1);
//                            }
//                        }
//                        if (list.size() > 5) {
//                            break;
//                        }
//                    }
//                    if (nextMessageEntity != null) {
//                        nextTime = nextMessageEntity.getId() - list.get(list.size() - 1).getId();
//                    }
//                }
//                if (oldsize == list.size() && oldsize > 0) {
//                    if (list.get(0) == showLiveMessageEntities.get(0)) {
//                        change = false;
//                    }
//                }
//                logger.i( "postDelayed:start=" + start + ",list=" + list.size() + ",nextTime=" + nextTime + ",change=" + change);
//                if (change) {
//                    mPlayVideoControlHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            showLiveMessageEntities.clear();
//                            showLiveMessageEntities.addAll(list);
//                            messageAdapter.notifyDataSetChanged();
//                            if (!showLiveMessageEntities.isEmpty()) {
//                                lvMessage.setSelection(showLiveMessageEntities.size() - 1);
//                            }
//                        }
//                    });
//                }
//            } else {
//                if (isEnd) {
//                    logger.i( "ScanRunnable:allLiveMessageEntities.isSpace,isEnd" + ",nextTime=" + nextTime);
////                    nextTime = -1;
////                    return;
//                }
//            }
//            if (isFinishing()) {
//                return;
//            }
//            logger.i( "ScanRunnable:allLiveMessageEntities=" + allLiveMessageEntities.size() + ",nextTime=" + nextTime);
//            if (nextTime == -1) {
//                lectureLivePlayBackBll.saveLiveLectureMsgs(dir, "2L" + mVideoEntity.getLiveId(), "" + start, timeEntities);
//                scanHandler.postDelayed(this, 300);
//            } else {
////                lectureLivePlayBackBll.saveLiveLectureMsgs(dir, "2L" + mVideoEntity.getLiveId(), "" + start, timeEntities);
//                scanHandler.postDelayed(this, nextTime / 1000);
//            }
//        }
//    }
//
//    public interface GetLiveLectureMsgs {
//        void getLiveLectureMsgs(LiveMessageGroupEntity liveMessageGroupEntity);
//
//        void onPmFailure();
//    }
//
//    @Override
//    protected void onPlayOpenStart() {
//        if (rlFirstBackgroundView != null) {
//            rlFirstBackgroundView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onPlayOpenSuccess() {
//        if (rlFirstBackgroundView != null) {
//            rlFirstBackgroundView.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    protected void resultFailed(int arg1, int arg2) {
//        super.resultFailed(arg1, arg2);
//        if (arg2 != 0 && mVideoEntity != null) {
//            if ("PublicLiveDetailActivity".equals(where)) {//公开直播
//                XesMobAgent.onOpenFail(where + ":playback1", mVideoEntity.getLiveId(), mWebPath, arg2);
//            }
//        }
//    }
//
//    @Override
//    protected String getVideoKey() {
//        if (!islocal && mVideoEntity != null) {
//            if ("PublicLiveDetailActivity".equals(where)) {
//                return mVideoEntity.getLiveId();
//            }
//        }
//        return super.getVideoKey();
//    }
//
//    /** seek完成 */
//    @Override
//    protected void onSeekComplete() {
//        long lastCurrentMsg = currentMsg;
//        long currentPosition = vPlayer.getCurrentPosition();
//        currentMsg = currentPosition;
//        if (timeEntities.size() > 0) {
//            long gotoClassTime = mVideoEntity.getGotoClassTime();
//            for (int i = 0; i < timeEntities.size(); i++) {
//                VideoQuestionEntity videoQuestionEntity = timeEntities.get(i);
//                if (videoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_VIDEO_START) {
//                    if (i < timeEntities.size() - 1) {
//                        VideoQuestionEntity nextVideoQuestionEntity = timeEntities.get(i + 1);
//                        if (nextVideoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_VIDEO_END) {
//                            int cha = nextVideoQuestionEntity.getvQuestionInsretTime() - videoQuestionEntity.getvQuestionInsretTime();
//                            if (gotoClassTime * 1000 + currentMsg > (long) nextVideoQuestionEntity.getvQuestionInsretTime() * 1000) {
//                                currentMsg += cha;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (currentMsg - currentPosition != 0) {
//            logger.i( "onSeekComplete:currentPosition=" + currentPosition + ",allCha=" + (currentMsg - currentPosition));
//        }
//        if (scanRunnable != null) {
//            scanHandler.removeCallbacks(scanRunnable);
//            scanHandler.post(scanRunnable);
//        }
//    }
//
//    /** 视频播放进度实时获取 */
//    @Override
//    protected void playingPosition(long currentPosition, long duration) {
//        super.playingPosition(currentPosition, duration);
//        if (NetWorkHelper.getNetWorkState(mContext) == NetWorkHelper.NO_NETWORK) {
//            return;
//        }
//        currentMsg = currentPosition;
//        if (firstMsg > 0) {
//            if (scanRunnable == null && currentMsg > firstMsg / 1000 - 2000) {
//                scanRunnable = new ScanRunnable();
//                scanHandler.post(scanRunnable);
//            }
//        }
//        if (timeEntities.size() > 0) {
//            long gotoClassTime = mVideoEntity.getGotoClassTime();
//            int time = 0;
//            for (int i = 0; i < timeEntities.size(); i++) {
//                VideoQuestionEntity videoQuestionEntity = timeEntities.get(i);
//                if (videoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_VIDEO_START) {
//                    if (i < timeEntities.size() - 1) {
//                        VideoQuestionEntity nextVideoQuestionEntity = timeEntities.get(i + 1);
//                        if (nextVideoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_VIDEO_END) {
//                            int cha = nextVideoQuestionEntity.getvQuestionInsretTime() - videoQuestionEntity.getvQuestionInsretTime();
//                            if (gotoClassTime * 1000 + currentMsg > (long) nextVideoQuestionEntity.getvQuestionInsretTime() * 1000) {
//                                currentMsg += cha;
//                                time++;
//                            }
//                        }
//                    }
//                }
//            }
//            if (currentMsg - currentPosition != 0) {
//                logger.i( "playingPosition:currentPosition=" + currentPosition + ",allCha=" + (currentMsg - currentPosition) + ",time=" + time);
//            }
//        }
//        if (onlineNumList != null) {
//            long gotoClassTime = mVideoEntity.getGotoClassTime();
//            for (int i = 0; i < onlineNumList.size(); i++) {
//                LecturePeopleEntity entity = onlineNumList.get(i);
//                if ((entity.id - gotoClassTime) * 1000 > currentMsg) {
//                    LectureLivePlaybackMediaController mediaController = (LectureLivePlaybackMediaController) mMediaController;
//                    int onlineNum;
//                    if (i == 0) {
//                        onlineNum = entity.onlineNum;
//                    } else {
//                        LecturePeopleEntity lastEntity = onlineNumList.get(i - 1);
//                        double a;
//                        if (entity.id - lastEntity.id == 0) {
//                            a = 1;
//                        } else {
//                            a = (double) (entity.onlineNum - lastEntity.onlineNum) / (double) (entity.id - lastEntity.id);
//                        }
//                        double b = entity.onlineNum - a * entity.id;
//                        onlineNum = (int) (a * (gotoClassTime + currentMsg / 1000) + b);
//                    }
//                    if (mediaController != null) {
//                        mediaController.setPeopleCount(onlineNum);
//                    }
//                    break;
//                }
//            }
//        }
//        scanQuestion(currentPosition); // 扫描互动题
//    }
//
//    @Override
//    public void onOnPointClick(VideoQuestionEntity videoQuestionEntity, long position) {
//        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
//        mQuestionEntity = videoQuestionEntity;
//        mQuestionEntity.setClick(true);
//        showQuestion(oldQuestionEntity);
//    }
//
//    /** 扫描是否有需要弹出的互动题 */
//    public void scanQuestion(long position) {
//
//        if (!mIsLand || vPlayer == null || !vPlayer.isPlaying()) {
//            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
//            return;
//        }
//
//        // 互动题结束，隐藏互动题
//        if (mQuestionEntity != null && mQuestionEntity.getvEndTime() != 0
//                && mQuestionEntity.getvEndTime() == TimeUtils.gennerSecond(position)) {
//            // 如果是互动题，则提示时间结束
//            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()
//                    && !mQuestionEntity.isAnswered()) {
//                XESToastUtils.showToast(this, "答题时间结束...");
//                mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
//            }
//        }
//        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
//        getPlayQuetion(TimeUtils.gennerSecond(position));
//        showQuestion(oldQuestionEntity);
//    }
//
//    private void showQuestion(VideoQuestionEntity oldQuestionEntity) {
//        if (oldQuestionEntity == null && mQuestionEntity != null && !mQuestionEntity.isAnswered()) {
//            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
//                if (vPlayer != null) {
//                    vPlayer.pause();
//                }
//                mQuestionEntity.setAnswered(true);
//                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
//                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
//                verifyCancelAlertDialog.setVerifyBtnListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (vPlayer != null) {
//                            vPlayer.start();
//                        }
//                        showExam();
//                    }
//                });
//                verifyCancelAlertDialog.setCancelBtnListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        seekTo(mQuestionEntity.getvEndTime() * 1000);
//                    }
//                });
//                verifyCancelAlertDialog.showDialog();
//                return;
//            }
//        }
//        // 有交互信息并且没有互动题
//        if (mQuestionEntity != null && !mQuestionEntity.isAnswered() && !mIsShowQuestion) {
//            // 互动题
//            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()) {
//                if (!(mMediaController != null && mMediaController.isShow())) {
//                    // 红包隐藏
//                    redPacketHide();
//                    showQestion();
//                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_SHOW,
//                            XesMobAgent.XES_VIDEO_INTERACTIVE);
//                }
//                // 红包
//            } else if (LocalCourseConfig.CATEGORY_REDPACKET == mQuestionEntity.getvCategory()) {
//                if (("" + mRedPacketId).equals(mQuestionEntity.getvQuestionID())) {
//                    return;
//                }
//                mRedPacketId = mQuestionEntity.getvQuestionID();
//                showRedPacket(mQuestionEntity);
//                XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil.REDPACKET_SHOW,
//                        XesMobAgent.XES_VIDEO_INTERACTIVE);
//            } else if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
//                // 红包隐藏
//                redPacketHide();
//                showExam();
//            }
//            // 互动题结束
//        }
//    }
//
//    /** 红包隐藏 */
//    public void redPacketHide() {
//        mRedPacketId = "";
//        mIsShowRedpacket = false;
//        mRedPacketDialog.cancelDialog();
//    }
//
//    /** 显示互动题 */
//    private void showQestion() {
//        final long before = System.currentTimeMillis();
//        mPlayVideoControlHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                logger.i( "showQestion:time=" + (System.currentTimeMillis() - before));
//                if (rlQuestionContent != null && mQuestionEntity != null) {
//                    // 填空题
//                    if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(mQuestionEntity.getvQuestionType())) {
//                        showFillBlankQuestion();
//                        // 选择题
//                    } else if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(mQuestionEntity.getvQuestionType())) {
//                        if ("1".equals(mQuestionEntity.getChoiceType())) {   // 单项选择题
//                            showSelectQuestion();
//                        } else if ("2".equals(mQuestionEntity.getChoiceType())) {   // 多项选择题
//                            showMulitSelectQuestion();
//                        } else {
//                            XESToastUtils.showToast(LectureLivePlayBackVideoActivity.this, "不支持的试题类型，可能需要升级版本");
//                            return;
//                        }
//                    } else {
//                        XESToastUtils.showToast(LectureLivePlayBackVideoActivity.this, "不支持的试题类型，可能需要升级版本");
//                        return;
//                    }
//                    mPlayVideoControlHandler.sendEmptyMessage(SHOW_QUESTION);
//                }
//            }
//        });
//    }
//
//    private void showExam() {
//
//    }
//
//    /**
//     * 填空题
//     */
//    private void showFillBlankQuestion() {
//        mVideoCourseQuestionPager = new QuestionFillInBlankLivePager(LectureLivePlayBackVideoActivity.this, mQuestionEntity);
//        mVideoCourseQuestionPager.setPutQuestion(new PutQuestion() {
//            @Override
//            public void onPutQuestionResult(BaseLiveQuestionPager baseLiveQuestionPager, BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
//                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
//                sendQuestionResult(result, mQuestionEntity);
//            }
//        });
//        rlQuestionContent.removeAllViews();
//        rlQuestionContent.addView(mVideoCourseQuestionPager.getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT));
//        rlQuestionContent.setVisibility(View.VISIBLE);
//    }
//
//    /** 显示选择题 */
//    public void showSelectQuestion() {
//        QuestionSelectLivePager questionSelectPager = new QuestionSelectLivePager(LectureLivePlayBackVideoActivity.this,
//                mQuestionEntity);
//        questionSelectPager.setPutQuestion(new PutQuestion() {
//            @Override
//            public void onPutQuestionResult(BaseLiveQuestionPager baseLiveQuestionPager, BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
//                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
//                sendQuestionResult(result, mQuestionEntity);
//            }
//        });
//        rlQuestionContent.removeAllViews();
//        rlQuestionContent.addView(questionSelectPager.getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT));
//        rlQuestionContent.setVisibility(View.VISIBLE);
//    }
//
//    /** 显示选择题 */
//    public void showMulitSelectQuestion() {
//        QuestionMulitSelectLivePager questionSelectPager = new QuestionMulitSelectLivePager(LectureLivePlayBackVideoActivity.this,
//                mQuestionEntity);
//        questionSelectPager.setPutQuestion(new PutQuestion() {
//            @Override
//            public void onPutQuestionResult(BaseLiveQuestionPager baseLiveQuestionPager, BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
//                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
//                sendQuestionResult(result, mQuestionEntity);
//            }
//        });
//        rlQuestionContent.removeAllViews();
//        rlQuestionContent.addView(questionSelectPager.getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT));
//        rlQuestionContent.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * 主界面响应事件
//     *
//     * @param event
//     * @author zouhao
//     * @Create at: 2015-5-6 上午11:13:22 //
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(PlaybackVideoEvent event) {
//        if (event instanceof PlaybackVideoEvent.OnQuesionDown) {
//            BaseVideoQuestionEntity questionEntity = ((PlaybackVideoEvent.OnQuesionDown) event).getVideoQuestionEntity();
//            // 填空题答案保存
//            if (questionEntity != null && questionEntity.getAnswerEntityLst() != null
//                    && questionEntity.getAnswerEntityLst().size() != 0) {
//                saveQuestionAnswer(questionEntity.getAnswerEntityLst());
//                // 选择题答案保存
//            } else {
//                if (this.mQuestionEntity != null) {
//                    if (this.mQuestionEntity.getvQuestionID().equals(questionEntity.getvQuestionID())) {
//                        this.mQuestionEntity = (VideoQuestionEntity) questionEntity;
//                    }
//                }
//            }
//            getPopupWindow();
//            mMediaController.setWindowLayoutType();
//            mMediaController.release();
//        } else if (event instanceof PlaybackVideoEvent.OnPlayVideoWebError) {
//            String result = ((PlaybackVideoEvent.OnPlayVideoWebError) event).getResult();
//            // 如果没有结果提示显示
//            if (TextUtils.isEmpty(result)) {
//                initRedPacketFirstResult(0, "金币+" + 0 + "枚金币");
//            } else {
//                localQuesitonResult(result);
//            }
//        } else if (event instanceof PlaybackVideoEvent.OnGetRedPacket) {
//            VideoResultEntity entity = ((PlaybackVideoEvent.OnGetRedPacket) event).getVideoResultEntity();
//            // 获取金币成功
//            if (entity.getResultType() == 1) {
//                initRedPacketResult(entity.getGoldNum());
//                // 已经获取过金币
//            } else if (entity.getResultType() == 0) {
//                initRedPacketOtherResult();
//            }
//        } else if (event instanceof PlaybackVideoEvent.OnAnswerReslut) {
//            VideoResultEntity entity = ((PlaybackVideoEvent.OnAnswerReslut) event).getVideoResultEntity();
//            answerResultChk(entity);
//        }
//    }
//
//    /**
//     * 发送试题答案
//     *
//     * @param result
//     */
//    private void sendQuestionResult(String result, VideoQuestionEntity questionEntity) {
//        if (questionEntity == null) {
//            return;
//        }
//        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
//        loadEntity.setLoadingTip(R.string.loading_tip_default);
//        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
//        lectureLivePlayBackBll.saveQuestionResult(loadEntity, questionEntity.getSrcType(), questionEntity.getvQuestionID(), result,
//                questionEntity.getAnswerDay(), mVideoEntity.getLiveId(), mVideoEntity.getvLivePlayBackType());
//        questionEntity.setAnswered(true);
//        questionViewGone();
//        XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_ANSWER,
//                XesMobAgent.XES_VIDEO_INTERACTIVE);
//    }
//
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent event) {
//        if (islocal) {
//            return;
//        }
//        if (event.netWorkType == NetWorkHelper.MOBILE_STATE) {
//            if (AppBll.getInstance().getAppInfoEntity().isNotificationOnlyWIFI()) {
//                EventBus.getDefault().post(new AppEvent.OnlyWIFIEvent());
//            } else if (AppBll.getInstance().getAppInfoEntity().isNotificationMobileAlert()) {
//                EventBus.getDefault().post(new AppEvent.NowMobileEvent());
//            }
//        }
//    }
//
//    /**
//     * 只在WIFI下使用激活
//     *
//     * @param onlyWIFIEvent
//     * @author zouhao
//     * @Create at: 2015-9-24 下午1:57:04
//     */
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent.OnlyWIFIEvent onlyWIFIEvent) {
//        stopShowRefresyLayout();
//    }
//
//    /**
//     * 开启了3G/4G提醒
//     *
//     * @param event
//     * @author zouhao
//     * @Create at: 2015-10-12 下午1:49:22
//     */
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onNowMobileEvent(AppEvent.NowMobileEvent event) {
//        if (mIsShowMobileAlert) {
//            mIsShowMobileAlert = false;
//            boolean pause = false;
//            final boolean initialized = isInitialized();
//            if (initialized) {
//                if (vPlayer.isPlaying()) {
//                    vPlayer.pause();
//                    pause = true;
//                }
//            }
//            final boolean finalPause = pause;
//            logger.i( "onNowMobileEvent:initialized=" + initialized + ",pause=" + pause);
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(LectureLivePlayBackVideoActivity.this, mBaseApplication, false,
//                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
//                    cancelDialog.setCancelBtnListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            onUserBackPressed();
//                        }
//                    });
//                    cancelDialog.setVerifyBtnListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            logger.i( "onNowMobileEvent:onClick:initialized=" + initialized + ",finalPause=" + finalPause);
//                            if (initialized) {
//                                if (finalPause) {
//                                    if (vPlayer != null) {
//                                        vPlayer.start();
//                                    }
//                                }
//                            } else {
//                                if (StringUtils.isSpace(mWebPath)) {
//                                    XESToastUtils.showToast(LectureLivePlayBackVideoActivity.this, "视频资源错误，请您尝试重新播放课程");
//                                    onUserBackPressed();
//                                } else {
//                                    playNewVideo(Uri.parse(mWebPath), mSectionName);
//                                }
//                            }
//                        }
//                    });
//                    cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo("您当前使用的是3G/4G网络，是否继续观看？",
//                            VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
//                }
//            });
//        }
//    }
//
//    /**
//     * 创建弹起互动题按钮
//     */
//    protected void initPopuptWindow() {
//        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_visible, null, false);
//        // 创建PopupWindow
//        mPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT, true);
//        mPopupWindow.getContentView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        RelativeLayout rlQuestionUp = (RelativeLayout) popupWindow_view.findViewById(R.id.rl_pop_question_visible);
//        RelativeLayout rlWindowDismiss = (RelativeLayout) popupWindow_view.findViewById(R.id
//                .rl_pop_question_visible_dismiss);
//        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
//        mPopupWindow.setBackgroundDrawable(dw);
//        mPopupWindow.setOutsideTouchable(true);
//        // 试题显示
//        rlQuestionUp.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 关闭试题提示栏
//                getPopupWindow();
//                // 立即显示试题
//                showQestion();
//            }
//        });
//        // 试题提示栏消失监听
//        rlWindowDismiss.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 试题栏消失，视频控制栏显示
//                getPopupWindow();
//                // 试题消失
//                questionViewGone();
//
//            }
//        });
//        // 这里是位置显示方式,在屏幕底部
//        mPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
//        rlQuestionContent.removeAllViews();
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                if (mIsBackDismiss) {
//                    mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
//                }
//                mIsBackDismiss = true;
//            }
//        });
//    }
//
//    /***
//     * 获取PopupWindow实例
//     */
//    private void getPopupWindow() {
//        if (null != mPopupWindow) {
//            mIsBackDismiss = false;
//            mPopupWindow.dismiss();
//            mPopupWindow = null;
//            return;
//        } else {
//            initPopuptWindow();
//        }
//    }
//
//    /**
//     * 试题布局隐藏
//     */
//    private void questionViewGone() {
//        new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                mIsShowQuestion = false;
//                attachMediaController();
//                if (rlQuestionContent != null) {
//                    rlQuestionContent.removeAllViews();
//                }
//            }
//        }.sendEmptyMessageDelayed(0, 1000); // 延迟1秒钟消失
//    }
//
//    /**
//     * 红包布局隐藏
//     */
//    private void redPacketViewGone() {
//        new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                mIsShowRedpacket = false;
//                attachMediaController();
//            }
//        }.sendEmptyMessageDelayed(0, 1000); // 延迟1秒钟消失
//    }
//
//    /**
//     * 显示红包
//     */
//    private void showRedPacket(final VideoQuestionEntity mQuestionEntity) {
//        mIsShowRedpacket = true;
//        // 如果视频控制栏显示
//        if (mMediaController != null) {
//            mMediaController.release();
//        }
//        mRedPacketDialog.setRedPacketConfirmListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.bt_livevideo_redpackage_cofirm) {
//                    mQuestionEntity.setAnswered(true);
//                    DataLoadEntity loadEntity = new DataLoadEntity(mContext);
//                    loadEntity.setLoadingTip(R.string.loading_tip_default);
//                    // 获取红包
//                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {
//                        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
//                        lectureLivePlayBackBll.getRedPacket(loadEntity, mVideoEntity.getLiveId(), mRedPacketId);
//                    } else if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
//                        publicLiveCourseRedPacket();
//                    } else {
//                        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
//                        lectureLivePlayBackBll.getLivePlayRedPacket(loadEntity, mVideoEntity.getLiveId(), mRedPacketId);
//                    }
//                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil.REDPACKET_GRAB,
//                            XesMobAgent.XES_VIDEO_INTERACTIVE);
//                }
//                redPacketViewGone();
//            }
//        }).showDialog();
//
//
//    }
//
//    /**
//     * 公开直播红包逻辑
//     */
//    public void publicLiveCourseRedPacket() {
//        initRedPacketFirstResult(0, "金币+" + 0 + "枚金币");
//    }
//
//    /**
//     * 获取互动题
//     *
//     * @param playPosition
//     */
//    private void getPlayQuetion(int playPosition) {
//        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
//        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
//            return;
//        }
//        int startTime, endTime;
//
//        boolean hasQuestionShow = false;
//        for (int i = 0; i < lstVideoQuestion.size(); i++) {
//            VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
//            if (videoQuestionEntity.isAnswered()) {
//                continue;
//            }
//            startTime = videoQuestionEntity.getvQuestionInsretTime();
//            endTime = videoQuestionEntity.getvEndTime();
//            // 红包只有开始时间
//            if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
//                if (startTime == playPosition) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            } else if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
//                // 互动题在开始时间和结束时间之间
//                if (startTime <= playPosition && playPosition < endTime) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
//                // 互动题在开始时间和结束时间之间
//                if (startTime == playPosition) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            }
//        }
////        logger.i( "getPlayQuetion:playPosition=" + playPosition + ",hasQuestionShow=" + hasQuestionShow + ",mQuestionEntity=" + (mQuestionEntity != null));
//        // 如果没有互动题则移除
//        if (!hasQuestionShow && mQuestionEntity != null) {
//            startTime = mQuestionEntity.getvQuestionInsretTime();
//            //播放器seekto的误差
//            logger.i( "getPlayQuetion:isClick=" + mQuestionEntity.isClick() + ",playPosition=" + playPosition + ",startTime=" + startTime);
//            if (mQuestionEntity.isClick()) {
//                if (startTime - playPosition >= 0 && startTime - playPosition < 5) {
//                    return;
//                }
//            }
//            mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
//        }
//    }
//
//    /**
//     * 获取红包成功
//     *
//     * @param goldNum
//     */
//    private void initRedPacketResult(int goldNum) {
//        String msg = "+" + goldNum + "金币";
//        View popupWindow_view = getLayoutInflater().inflate(R.layout.dialog_red_packet_success, null, false);
//        SpannableString msp = new SpannableString(msg);
//        float screenDensity = ScreenUtils.getScreenDensity();
//        // 字体
//        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_gold);
//        tvGoldHint.setText(msp);
//        popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAnswerPopupWindow.dismiss();
//            }
//        });
//        // 创建PopupWindow
//        mAnswerPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT, true);
//        // 这里是位置显示方式,在屏幕底部
//        mAnswerPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
//        popupWindow_view.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (mAnswerPopupWindow != null) {
//                    mAnswerPopupWindow.dismiss();
//                }
//            }
//        });
//        final TextView tvAutoclose = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
//        final AtomicInteger count = new AtomicInteger(3);
//        mPlayVideoControlHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                count.set(count.get() - 1);
//                if (count.get() == 0) {
//                    mAnswerPopupWindow.dismiss();
//                } else {
//                    if (mAnswerPopupWindow != null && mAnswerPopupWindow.isShowing()) {
//                        tvAutoclose.setText(count.get() + "秒自动关闭");
//                        mPlayVideoControlHandler.postDelayed(this, 1000);
//                    }
//                }
//            }
//        }, 1000);
//    }
//
//    /**
//     * 获取红包成功
//     *
//     * @param goldNum
//     * @param msg
//     */
//    private void initRedPacketFirstResult(int goldNum, String msg) {
//        msg = "+" + goldNum + "金币";
//        View popupWindow_view = getLayoutInflater().inflate(R.layout.dialog_red_packet_success, null, false);
//        popupWindow_view.setBackgroundColor(getResources().getColor(R.color.mediacontroller_bg));
//        SpannableString msp = new SpannableString(msg);
//        float screenDensity = ScreenUtils.getScreenDensity();
//        // 字体
//        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_gold);
//        tvGoldHint.setText(msp);
////        rlRedpacketContent.addView(view);
////        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                rlRedpacketContent.removeAllViews();
////            }
////        });
//        ImageView ivRedpackageLight = (ImageView) popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_light);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_livevideo_light_rotate);
//        ivRedpackageLight.startAnimation(animation);
//        initQuestionAnswerReslut(popupWindow_view);
//    }
//
//    /**
//     * 以获取过红包
//     */
//    private void initRedPacketOtherResult() {
//        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_redpacket_other, null, false);
//        initQuestionAnswerReslut(popupWindow_view);
//    }
//
//    /**
//     * 互动题回答正确
//     *
//     * @param goldNum
//     */
//    private void initAnswerPartRightResult(int goldNum) {
//        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
//        popupWindow_view.findViewById(R.id.iv_pop_question_answer_right).setBackgroundResource(R.drawable.bg_pop_question_answer_type3);
//        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
//        tvGoldHint.setText("" + goldNum);
//        initQuestionAnswerReslut(popupWindow_view);
//    }
//
//    /**
//     * 互动题回答正确
//     *
//     * @param goldNum
//     */
//    private void initAnswerRightResult(int goldNum) {
//        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
//        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
//        tvGoldHint.setText("" + goldNum);
//        initQuestionAnswerReslut(popupWindow_view);
//    }
//
//    /**
//     * 互动题回答错误
//     */
//    private void initAnswerWrongResult() {
//        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_wrong, null, false);
//        initQuestionAnswerReslut(popupWindow_view);
//    }
//
//    /**
//     * 创建互动题作答，抢红包结果提示PopupWindow
//     */
//    protected void initQuestionAnswerReslut(View popupWindow_view) {
//        // 创建PopupWindow
//        mAnswerPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT, true);
//        // 这里是位置显示方式,在屏幕底部
//        mAnswerPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
//        popupWindow_view.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (mAnswerPopupWindow != null) {
//                    mAnswerPopupWindow.dismiss();
//                }
//            }
//        });
//        disMissAnswerPopWindow();
//    }
//
//    /** 回答问题结果提示框延迟三秒消失 */
//    public void disMissAnswerPopWindow() {
//        mPlayVideoControlHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mAnswerPopupWindow != null) {
//                    try {
//                        mAnswerPopupWindow.dismiss();
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//        }, 3000);// 延迟3秒钟消失
//    }
//
//    /** 保存学生填空题答案 */
//    private void saveQuestionAnswer(List<AnswerEntity> answerEntityLst) {
//        if (mQuestionEntity != null) {
//            mQuestionEntity.setAnswerEntityLst(answerEntityLst);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        AppBll.getInstance().unRegisterAppEvent(this);
//        super.onDestroy();
//        if (scanRunnable != null) {
//            scanRunnable.exit();
//        }
//        if (mAnswerPopupWindow != null) {
//            try {
//                mAnswerPopupWindow.dismiss();
//                mAnswerPopupWindow = null;
//            } catch (Exception e) {
//
//            }
//        }
//    }
//
//    @Override
//    protected void resultComplete() {
//        // 播放完毕直接退出
//        onUserBackPressed();
//    }
//
//    private Handler mPlayVideoControlHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SHOW_QUESTION:
//                    mIsShowQuestion = true;
//                    if (mMediaController != null) {
//                        mMediaController.setWindowLayoutType();
//                        mMediaController.release();
//                    }
//                    lvMessage.setVisibility(View.GONE);
//                    break;
//                case NO_QUESTION:
//                    if (mVideoCourseQuestionPager != null) {
//                        mVideoCourseQuestionPager.hideInputMode();
//                    }
//                    mQuestionEntity = null;
//                    questionViewGone();
//                    postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if ("0".equals(lvMessage.getTag())) {
//                                lvMessage.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }, 1000);
//                    if (mPopupWindow != null) {
//                        mPopupWindow.dismiss();
//                        mPopupWindow = null;
//                    }
//            }
//        }
//    };
//
//    @Override
//    protected void onRefresh() {
//        if (AppBll.getInstance(this).isNetWorkAlert()) {
//            loadView(mLayoutVideo);
//            initView();
//            initData();
//        }
//        AppBll.getInstance(mBaseApplication);
//    }
//
//    /**
//     * 互动题结果解析
//     *
//     * @param entity
//     */
//    private void answerResultChk(VideoResultEntity entity) {
//        // 回答正确提示
//        if (entity.getResultType() == 1) {
//            initAnswerRightResult(entity.getGoldNum());
//            // 回答错误提示
//        } else if (entity.getResultType() == 2) {
//            initAnswerWrongResult();
//            // 填空题部分正确提示
//        } else if (entity.getResultType() == 3) {
//            initAnswerPartRightResult(entity.getGoldNum());
//        }
//        mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
//    }
//
//    /**
//     * 互动题本地结果验证
//     *
//     * @param result
//     */
//    private void localQuesitonResult(String result) {
//        boolean isRight = true;
//        VideoResultEntity entity = new VideoResultEntity();
//        try {
//            if (mQuestionEntity != null) {
//                // 选择题
//                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(mQuestionEntity.getvQuestionType())) {
//                    if (!TextUtils.equals(mQuestionEntity.getvQuestionAnswer(), result)) {
//                        isRight = false;
//                    }
//                    // 填空题
//                } else {
//                    int rightNum = 0;
//                    JSONArray jsonArray = new JSONArray(result);
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        String stuAnswer = jsonArray.getString(i).trim();
//                        String rightAnswer = mQuestionEntity.getAnswerEntityLst().get(i).getRightAnswer();
//                        if (TextUtils.equals(stuAnswer, rightAnswer)) {
//                            rightNum++;
//                        }
//                    }
//                    if (rightNum == 0) {
//                        isRight = false;
//                    } else if (rightNum != jsonArray.length()) {
//                        entity.setRightNum(rightNum);
//                    }
//
//                }
//            }
//            // 回答正确
//            if (isRight) {
//                entity.setGoldNum(0);
//                entity.setResultType(1);
//                // 填空题部分正确
//                if (entity.getRightNum() != 0) {
//                    entity.setResultType(3);
//                }
//            } else {
//                // 回答错误
//                entity.setGoldNum(0);
//                entity.setResultType(2);
//            }
//            answerResultChk(entity);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    class MessageAdapter extends XsBaseAdapter<LivePlayBackMessageEntity> {
//
//        public MessageAdapter(Context context, List<LivePlayBackMessageEntity> list) {
//            super(context, list);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView tvMessageItem;
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(R.layout.item_livevideo_message_land, parent, false);
//            }
//            tvMessageItem = (TextView) convertView.findViewById(R.id.tv_livevideo_message_item);
//            LivePlayBackMessageEntity messageEntity = (LivePlayBackMessageEntity) getItem(position);
//            LivePlayBackMessageEntity.Text text = messageEntity.getText();
//            tvMessageItem.setText(text.getName() + ": ");
//            tvMessageItem.append(text.getCharSequence());
//            return convertView;
//        }
//    }
//
//    public void stopExam() {
//        mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
//        if (mQuestionEntity != null && mIsShowQuestion) {
//            seekTo(mQuestionEntity.getvEndTime() * 1000);
//        }
//    }
//
//    /**
//     * 跳转到播放器
//     *
//     * @param context
//     * @param bundle
//     */
//    public static void intentTo(Activity context, Bundle bundle, String where) {
//        intentTo(context, bundle, where, VIDEO_REQUEST);
//    }
//
//    /**
//     * 跳转到播放器
//     *
//     * @param context
//     * @param bundle
//     * @param requestCode
//     */
//    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
//        Intent intent = new Intent(context, LectureLivePlayBackVideoActivity.class);
//        intent.putExtras(bundle);
//        intent.putExtra("where", where);
//        context.startActivityForResult(intent, requestCode);
//    }
//
//    @Override
//    protected void updateIcon() {
//        updateLoadingImage();
//        updateRefreshImage();
//    }
//
//    protected void updateLoadingImage() {
//        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
//        if (footerIconEntity != null) {
//            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
//            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
//                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
//        }
//    }
//}

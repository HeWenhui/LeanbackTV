package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.base.BaseBll;
import com.xueersi.parentsmeeting.business.AppBll;
import com.xueersi.parentsmeeting.entity.AnswerEntity;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.FooterIconEntity;
import com.xueersi.parentsmeeting.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.MobEnumUtil;
import com.xueersi.parentsmeeting.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.PutQuestion;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.RedPacketAlertDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExPerienceLiveMessage;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageGroupEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.EnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExamQuestionPlaybackPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExamQuestionPlaybackPagers;
import com.xueersi.parentsmeeting.modules.livevideo.page.H5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionFillInBlankLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionMulitSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionWebPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SubjectResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LectureLivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VP;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.xesalib.umsagent.UmsConstants;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.utils.time.TimeUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;
import com.xueersi.xesalib.view.layout.dataload.DataLoadEntity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE;

/**
 * Created by Administrator on 2018/3/6.
 */

public class ExperienceLiveVideoActivity extends LiveVideoActivityBase implements VideoAction,BaseLiveMediaControllerBottom.MediaChildViewClick{
    QuestionBll questionBll;
    private RelativeLayout rlLiveMessageContent;
    LiveMessageBll liveMessageBll;
    /** 横屏聊天信息 */
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    private ScanRunnable scanRunnable;
    private Handler scanHandler;
    private List<ExPerienceLiveMessage.LiveExMsg> mMsgs;
    private LiveMessagePager mLiveMessagePager;
    private Long timer = 0L;
    /** 视频宽度 */
    public static final float VIDEO_WIDTH = 1280f;
    /** 视频高度 */
    public static final float VIDEO_HEIGHT = 720f;
    /** 视频宽高比 */
    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;

    @Override
    public void onTeacherNotPresent(boolean isBefore) {

    }

    @Override
    public void onTeacherQuit(boolean isQuit) {

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {

    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {

    }

    @Override
    public void onClassTimoOut() {

    }

    @Override
    public void onModeChange(String mode, boolean isPresent) {

    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {

    }

    @Override
    public void onLiveDontAllow(String msg) {

    }

    @Override
    public void onMediaViewClick(View child) {

    }

    // 03.17 定时获取聊天记录的任务
    class ScanRunnable implements Runnable{
        HandlerThread handlerThread = new HandlerThread("ScanRunnable");

        ScanRunnable() {
            Loger.i(TAG, "ScanRunnable");
            handlerThread.start();
            scanHandler = new Handler(handlerThread.getLooper());
        }

        void exit() {
            handlerThread.quit();
        }
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
//            initOldMessage(mVideoEntity.getLiveId(),mVideoEntity.getCourseId(),timer + Long.parseLong(mVideoEntity.getVisitTimeKey()));
            initOldMessage(mVideoEntity.getLiveId(),mVideoEntity.getCourseId(),timer + 2960L);
            timer = timer + 10;
//            if(mMessage != null && mMessage.getMsg()!=null && mMessage.getMsg().size()>0){
//                mMsgs = new ArrayList<>();
//                for(int i = 0 ; i < mMessage.getMsg().size() ; i++){
//                    if("130".equals(mMessage.getMsg().get(i).getText().getType())){
//                        mMsgs.add(mMessage.getMsg().get(i));
//                    }
//                }
//            }
//            if(mMsgs.size() > 0){
//                for(int i = 0 ; i < mMsgs.size() ; i++){
//                    mLiveMessagePager.addMessage(mMsgs.get(i).getText().getBy(),LiveMessageEntity.MESSAGE_TIP,mMsgs.get(i).getText().getMsg());
//                }
//                mMsgs.clear();
//            }
            Log.e("Duncan","timer:" + timer);
            scanHandler.postDelayed(this, 10000);


        }
    }

    private String TAG = "ExpericenceLiveVideoActivityLog";
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    LiveMediaControllerBottom liveMediaControllerBottom;
    /** 初始进入播放器时的预加载界面 */
    /** 老师不在直播间 */
    private ImageView ivTeacherNotpresent;
    RelativeLayout bottomContent;
    RelativeLayout praiselistContent;
    /** 缓冲提示 */
    private TextView tvLoadingHint;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    /** 初始进入播放器时的预加载界面 */
    private RelativeLayout rlFirstBackgroundView;
    /** 是不是播放失败 */
    boolean resultFailed = false;
    /** 当前是否正在显示互动题 */
    private boolean mIsShowQuestion = false;
    /** 当前是否正在显示红包 */
    private boolean mIsShowRedpacket = false;
    /** 当前是否正在显示对话框 */
    private boolean mIsShowDialog = false;
    /** 是不是点击返回键或者点周围,取消互动题,而没有使用getPopupWindow */
    boolean mIsBackDismiss = true;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    String beforeAttach;

    /** 是否显示移动网络提示 */
    private boolean mIsShowMobileAlert = true;

    /** 我的课程业务层 */
    LectureLivePlayBackBll lectureLivePlayBackBll;

    /** 声明PopupWindow对象的引用 */
    private PopupWindow mPopupWindow;

    /** 试题对错弹框 */
    PopupWindow mAnswerPopupWindow;

//    /** 统一的加载动画 */
//    private LoadingDialog mProgressDialog;

    /** 红包弹窗 */
    private RedPacketAlertDialog mRedPacketDialog;

    /** 互动题 */
    private VideoQuestionEntity mQuestionEntity;
    /** 互动题为空的异常 */
    private Exception questionEntityNullEx;
    /** 各种互动题的页面 */
    /** 语音答题的页面 */
    private VoiceAnswerPager voiceAnswerPager;
    /** 普通互动题，h5显示页面 */
    private QuestionWebPager questionWebPager;
    /** 课前测的页面 */
    private ExamQuestionPlaybackPagers examQuestionPlaybackPager;
    /** 语音评测，role play的页面 */
    private BaseSpeechAssessmentPager speechQuestionPlaybackPager;
    /** nb实验的页面 */
    private H5CoursewarePager h5CoursewarePager;
    /** 英语课件的页面 */
    private EnglishH5CoursewarePager englishH5CoursewarePager;
    /** 文科主观题结果的页面 */
    private SubjectResultPager subjectResultPager;
    /** 讲座购课广告的页面 */
    private LecAdvertPager lecAdvertPager;
    /** 填空题布局 */
    QuestionFillInBlankLivePager mVideoCourseQuestionPager;

    /** 红包id */
    private String mRedPacketId;
    /** 播放路径名 */
    private String mWebPath;
    /** 节名称 */
    private String mSectionName;
    /** 显示互动题 */
    private static final int SHOW_QUESTION = 0;
    /** 没有互动题 */
    private static final int NO_QUESTION = 1;
    /** 加载视频提示 */
    private ImageView ivLoading;
    private TextView tvLoadingContent;
    /** 从哪个页面跳转 */
    String where;
    int isArts;
    /** 区分文理appid */
    String appID = UmsConstants.LIVE_APP_ID_BACK;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE;
    /** 本地视频 */
    boolean islocal;
    static int times = -1;
    long createTime;
    String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;
    private LiveBll mLiveBll;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    /** 当前时间，豪妙 */
    private long currentMsg = 0;
    private ExPerienceLiveMessage mMessage;
    private Boolean send = false;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        Intent intent = getIntent();
        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        islocal = intent.getBooleanExtra("islocal", false);
        initAllBll();
        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
//        initView();
        loadData();
        return true;
    }

    private void initAllBll() {
        questionBll = new QuestionBll(this, mVideoEntity.getStuCourseId());
        mLiveBll = new LiveBll(this, mVideoEntity.getSectionId(), mVideoEntity.getChapterId(), 1, 0);
        liveMessageBll = new LiveMessageBll(this, 1);

    }

    private void initView() {
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        liveMediaControllerBottom = new LiveMediaControllerBottom(this, mMediaController, this);
        liveMediaControllerBottom.experience();
        int topGap = (ScreenUtils.getScreenHeight() - videoView.getLayoutParams().height) / 2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (liveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            liveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
        mMediaController.setControllerBottom(liveMediaControllerBottom,false);
//        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
//        ivTeacherNotpresent.setImageResource(R.drawable.);
        bottomContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);

        praiselistContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_praiselist_content);
        praiselistContent.setVisibility(View.VISIBLE);
        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        tvLoadingHint.setText("获取课程信息");
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
        if (mIsLand) {
            // 加载横屏时互动题的列表布局
            rlQuestionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_contents);
        } else {
            if (rlQuestionContent != null) {
                rlQuestionContent.removeAllViews();
                rlQuestionContent = null;
            }
        }
        final ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        setFirstParam(lp);
        // 03.08 尝试添加聊天的布局页面
//        initMessagePager(bottomContent);
    }

    private void setFirstParam(ViewGroup.LayoutParams lp) {
        final View contentView = findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        int rightMargin = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (screenWidth - lp.width) / 2);
        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
            params.rightMargin = rightMargin;
            params.bottomMargin = params.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
//            rlFirstBackgroundView.setLayoutParams(params);
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
//            ivTeacherNotpresent.setLayoutParams(params);
        }

    }

    private void initMessagePager(RelativeLayout bottomContent) {
        rlLiveMessageContent = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlLiveMessageContent, params);

        long before = System.currentTimeMillis();
        mLiveMessagePager = new LiveMessagePager(this, questionBll, liveMediaControllerBottom, liveMessageLandEntities, null);
//        mLiveMessagePager = liveMessagePager;
        Loger.d(TAG, "initViewLive:time1=" + (System.currentTimeMillis() - before));

//        mLiveMessagePager.getInfo = getInfo;
//        mLiveMessagePager.urlclick = urlclick;
//        liveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setMessageBll(liveMessageBll);
        mLiveMessagePager.setLiveBll(mLiveBll);
        mLiveMessagePager.onModeChange(mLiveBll.getMode());
        // 03.08 设置假的聊天连接成功信号
        mLiveMessagePager.onConnects();
        mLiveMessagePager.setIsRegister(true);
        // 03.13 设置假的上课人数
        if(mMessage != null && mMessage.getOnlineNum().size()!=0 && mMessage.getOnlineNum().get(0).getOnlineNum()!=null){
            mLiveMessagePager.showPeopleCount(Integer.parseInt(mMessage.getOnlineNum().get(0).getOnlineNum()));
        }else{
            mLiveMessagePager.showPeopleCount(8);
        }
//        if (mode != null) {
//            mLiveMessagePager.onopenchat(openchat, mode, false);
//        }
        rlLiveMessageContent.addView(mLiveMessagePager.getRootView(), params);
    }

    private void loadData() {
        BaseApplication baseApplication = (BaseApplication) getApplication();
        mRedPacketDialog = new RedPacketAlertDialog(this, baseApplication, false);
        lectureLivePlayBackBll = new LectureLivePlayBackBll(ExperienceLiveVideoActivity.this, "");
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        where = getIntent().getStringExtra("where");
        isArts = getIntent().getIntExtra("isArts", 0);
        if (isArts == 1) {
            appID = UmsConstants.ARTS_APP_ID_BACK;
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            appID = UmsConstants.LIVE_APP_ID_BACK;
            IS_SCIENCE = true;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        // 如果加载不出来
        if (tvLoadingContent != null) {
            tvLoadingContent.setText("正在获取视频资源，请稍候");
        }
        // 视频名
        mSectionName = mVideoEntity.getPlayVideoName();
        // 播放视频
        mWebPath = mVideoEntity.getVideoPath();
        playNewVideo(Uri.parse(mWebPath), mSectionName);

    }

    public interface GetExperienceLiveMsgs {
        void getLiveExperienceMsgs(ExPerienceLiveMessage liveMessageGroupEntity);

        void onPmFailure();
    }

    private void initOldMessage(String liveId,String classId,Long start) {
        lectureLivePlayBackBll.getExperienceMsgs(liveId,classId,start,new GetExperienceLiveMsgs(){
            @Override
            public void getLiveExperienceMsgs(ExPerienceLiveMessage liveMessageGroupEntity) {
                mMessage = liveMessageGroupEntity;
                sendMessage();
            }

            @Override
            public void onPmFailure() {

            }
        } );
    }

    private void sendMessage() {
        if(mMessage != null && mMessage.getMsg()!=null && mMessage.getMsg().size()>0){
            mMsgs = new ArrayList<>();
            for(int i = 0 ; i < mMessage.getMsg().size() ; i++){
                if("130".equals(mMessage.getMsg().get(i).getText().getType())){
                    mMsgs.add(mMessage.getMsg().get(i));
                }
            }
            if(mMsgs.size() > 0){
                send = true;
            }
        }
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, ExperienceLiveVideoActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onPlayOpenSuccess() {
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(View.GONE);
//            seekTo(Long.parseLong(mVideoEntity.getVisitTimeKey()));
            seekTo(2960000);
            initView();
            initMessagePager(bottomContent);
        }
        if (mQuestionEntity != null) {
            Loger.d(TAG, "onPlayOpenSuccess:showQuestion:isAnswered=" + mQuestionEntity.isAnswered() + "," +
                    "mIsShowQuestion=" + mIsShowQuestion);
//            showQuestion(mQuestionEntity);
        }
    }

    @Override
    protected void onPlayOpenStart() {
        setFirstBackgroundVisible(View.VISIBLE);
        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);

    }

    public void setFirstBackgroundVisible(int visible) {
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    /**
     * 试题布局隐藏
     */
    private void questionViewGone() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mIsShowQuestion = false;
                if (rlQuestionContent != null) {
                    rlQuestionContent.removeAllViews();
                }
            }
        }.sendEmptyMessageDelayed(0, 1000); // 延迟1秒钟消失
    }



    protected void updateLoadingImage() {
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
        }
    }

    // 03.13 根据播放的进度获取互动题
    @Override
    protected void playingPosition(long currentPosition, long duration) {
        super.playingPosition(currentPosition, duration);
        if (NetWorkHelper.getNetWorkState(mContext) == NetWorkHelper.NO_NETWORK) {
            return;
        }
        currentMsg = currentPosition;
        // 扫描互动题
        scanQuestion(currentPosition);
        Log.e("Duncan","currentPosition:" + currentPosition);
        // 获取聊天记录
        if (scanRunnable == null) {
                scanRunnable = new ScanRunnable();
                scanHandler.post(scanRunnable);
            }
        //发送聊天记录
        if(send && mMsgs.size() > 0){
            for(int i = 0 ; i < mMsgs.size() ; i++){
                if(currentMsg/1000 == mMsgs.get(i).getReleative_time() ){
                    mLiveMessagePager.addMessage(mMsgs.get(i).getText().getName(),LiveMessageEntity.MESSAGE_TIP,mMsgs.get(i).getText().getMsg());
                    mMsgs.remove(i);
                }
            }
        }

    }


    /** 扫描是否有需要弹出的互动题 */
    public void scanQuestion(long position) {

        if (!mIsLand || vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }

        // 互动题结束，隐藏互动题
        if (mQuestionEntity != null && mQuestionEntity.getvEndTime() != 0
                && mQuestionEntity.getvEndTime() == TimeUtils.gennerSecond(position)) {
            // 如果是互动题，则提示时间结束
            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()
                    && !mQuestionEntity.isAnswered()) {
                XESToastUtils.showToast(this, "答题时间结束...");
                mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
            }
        }

        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        getPlayQuetion(TimeUtils.gennerSecond(position));
        showQuestion(oldQuestionEntity);
    }

    private void showQuestion(VideoQuestionEntity oldQuestionEntity) {
        if (oldQuestionEntity == null && mQuestionEntity != null && !mQuestionEntity.isAnswered()) {
            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
                if (vPlayer != null) {
                    vPlayer.pause();
                }
                mQuestionEntity.setAnswered(true);
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vPlayer != null) {
                            vPlayer.start();
                        }
                        showExam();
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seekTo(mQuestionEntity.getvEndTime() * 1000);
                    }
                });
                verifyCancelAlertDialog.showDialog();
                return;
            }
        }
        // 有交互信息并且没有互动题
        if (mQuestionEntity != null && !mQuestionEntity.isAnswered() && !mIsShowQuestion) {
            // 互动题
            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()) {
                if (!(mMediaController != null && mMediaController.isShow())) {
                    // 红包隐藏
                    redPacketHide();
                    showQestion();
                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_SHOW,
                            XesMobAgent.XES_VIDEO_INTERACTIVE);
                }
                // 红包
            } else if (LocalCourseConfig.CATEGORY_REDPACKET == mQuestionEntity.getvCategory()) {
                if (("" + mRedPacketId).equals(mQuestionEntity.getvQuestionID())) {
                    return;
                }
                mRedPacketId = mQuestionEntity.getvQuestionID();
                showRedPacket(mQuestionEntity);
                XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil.REDPACKET_SHOW,
                        XesMobAgent.XES_VIDEO_INTERACTIVE);
            } else if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
                // 红包隐藏
                redPacketHide();
                showExam();
            }
            // 互动题结束
        }
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final VideoQuestionEntity mQuestionEntity) {
        mIsShowRedpacket = true;
        // 如果视频控制栏显示
        mRedPacketDialog.setRedPacketConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.bt_livevideo_redpackage_cofirm) {
                    mQuestionEntity.setAnswered(true);
                    DataLoadEntity loadEntity = new DataLoadEntity(mContext);
                    loadEntity.setLoadingTip(R.string.loading_tip_default);
                    // 获取红包
                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {
                        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
                        lectureLivePlayBackBll.getRedPacket(loadEntity, mVideoEntity.getLiveId(), mRedPacketId);
                    } else if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
                        publicLiveCourseRedPacket();
                    } else {
                        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
                        lectureLivePlayBackBll.getLivePlayRedPackets(loadEntity,mRedPacketId,mVideoEntity.getLiveId(),mVideoEntity.getChapterId());
                    }
                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil.REDPACKET_GRAB,
                            XesMobAgent.XES_VIDEO_INTERACTIVE);
                }
                redPacketViewGone();
            }
        }).showDialog();
    }

    /**
     * 公开直播红包逻辑
     */
    public void publicLiveCourseRedPacket() {
        initRedPacketFirstResult(0, "金币+" + 0 + "枚金币");
    }

    /**
     * 红包布局隐藏
     */
    private void redPacketViewGone() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mIsShowRedpacket = false;
            }
        }.sendEmptyMessageDelayed(0, 1000); // 延迟1秒钟消失
    }

    /**
     * 获取互动题
     *
     * @param playPosition
     */
    private void getPlayQuetion(int playPosition) {
        Log.e("Duncan","getPlayQuetion:" + playPosition);
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        int startTime, endTime;

        boolean hasQuestionShow = false;
        for (int i = 0; i < lstVideoQuestion.size(); i++) {
            VideoQuestionEntity videoQuestionEntity = null;
            if(lstVideoQuestion.get(i) != null){
                videoQuestionEntity = lstVideoQuestion.get(i);
            }else{
                continue;
            }
//            if (videoQuestionEntity.isAnswered()) {
//                continue;
//            }
            startTime = videoQuestionEntity.getvQuestionInsretTime();
            endTime = videoQuestionEntity.getvEndTime();
            // 红包只有开始时间
            if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
                // 互动题在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
                // 互动题在开始时间和结束时间之间
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            }
        }
//        Loger.i(TAG, "getPlayQuetion:playPosition=" + playPosition + ",hasQuestionShow=" + hasQuestionShow + ",mQuestionEntity=" + (mQuestionEntity != null));
        if (mQuestionEntity != null) {
            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
                if (mQuestionEntity.getvEndTime() < playPosition) {
                    if (examQuestionPlaybackPager != null) {
                        examQuestionPlaybackPager.examSubmitAll();
                        if (vPlayer != null) {
                            vPlayer.pause();
                        }
                        Loger.i(TAG, "getPlayQuetion:examSubmitAll:playPosition=" + playPosition);
                    }
                }
                return;
            }
        }
        // 如果没有互动题则移除
        if (!hasQuestionShow && mQuestionEntity != null) {
            startTime = mQuestionEntity.getvQuestionInsretTime();
            //播放器seekto的误差
            Loger.i(TAG, "getPlayQuetion:isClick=" + mQuestionEntity.isClick() + ",playPosition=" + playPosition + ",startTime=" + startTime);
            if (mQuestionEntity.isClick()) {
                if (startTime - playPosition >= 0 && startTime - playPosition < 5) {
                    return;
                }
            }
            mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
        }
    }

    @Override
    protected void resultComplete() {
        // 播放完毕直接退出
//        onUserBackPressed();
        // 03.20 直播结束后，显示结束的提示图片
        ivTeacherNotpresent.setVisibility(View.VISIBLE);
        ivTeacherNotpresent.setImageResource(R.drawable.live_free_play_end);

    }

    private void showExam() {
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    mPlayVideoControlHandler.sendEmptyMessage(SHOW_QUESTION);
                    examQuestionPlaybackPager = new ExamQuestionPlaybackPagers(ExperienceLiveVideoActivity.this, mVideoEntity.getLiveId(), mQuestionEntity.getvQuestionID(), false, "");
                    rlQuestionContent.removeAllViews();
                    rlQuestionContent.addView(examQuestionPlaybackPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    rlQuestionContent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /** 显示互动题 */
    private void showQestion() {
        final long before = System.currentTimeMillis();
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                Loger.i(TAG, "showQestion:time=" + (System.currentTimeMillis() - before));
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    // 填空题
                    if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(mQuestionEntity.getvQuestionType())) {
                        showFillBlankQuestion();
                        // 选择题
                    } else if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(mQuestionEntity.getvQuestionType())) {
                        if ("1".equals(mQuestionEntity.getChoiceType())) {   // 单项选择题
                            showSelectQuestion();
                        } else if ("2".equals(mQuestionEntity.getChoiceType())) {   // 多项选择题
                            showMulitSelectQuestion();
                        } else {
                            XESToastUtils.showToast(ExperienceLiveVideoActivity.this, "不支持的试题类型，可能需要升级版本");
                            return;
                        }
                    } else {
                        XESToastUtils.showToast(ExperienceLiveVideoActivity.this, "不支持的试题类型，可能需要升级版本");
                        return;
                    }
                    mPlayVideoControlHandler.sendEmptyMessage(SHOW_QUESTION);
                }
            }
        });
    }

    /**
     * 填空题
     */
    private void showFillBlankQuestion() {
        mVideoCourseQuestionPager = new QuestionFillInBlankLivePager(ExperienceLiveVideoActivity.this, mQuestionEntity);
        mVideoCourseQuestionPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(mVideoCourseQuestionPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
    }

    /** 显示选择题 */
    public void showSelectQuestion() {
        QuestionSelectLivePager questionSelectPager = new QuestionSelectLivePager(ExperienceLiveVideoActivity.this,
                mQuestionEntity);
        questionSelectPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(questionSelectPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
    }

    /** 显示多选题 */
    public void showMulitSelectQuestion() {
        QuestionMulitSelectLivePager questionSelectPager = new QuestionMulitSelectLivePager(ExperienceLiveVideoActivity.this,
                mQuestionEntity);
        questionSelectPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(questionSelectPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
    }
    /** 红包隐藏 */
    public void redPacketHide() {
        mRedPacketId = "";
        mIsShowRedpacket = false;
        mRedPacketDialog.cancelDialog();
    }

    /**
     * 获取红包成功
     *
     * @param goldNum
     * @param msg
     */
    private void initRedPacketFirstResult(int goldNum, String msg) {
        msg = "+" + goldNum + "金币";
        View popupWindow_view = getLayoutInflater().inflate(R.layout.dialog_red_packet_success, null, false);
        popupWindow_view.setBackgroundColor(getResources().getColor(R.color.mediacontroller_bg));
        SpannableString msp = new SpannableString(msg);
        float screenDensity = ScreenUtils.getScreenDensity();
        // 字体
        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_gold);
        tvGoldHint.setText(msp);
//        rlRedpacketContent.addView(view);
//        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlRedpacketContent.removeAllViews();
//            }
//        });
        ImageView ivRedpackageLight = (ImageView) popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_light);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_livevideo_light_rotate);
        ivRedpackageLight.startAnimation(animation);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    protected void initQuestionAnswerReslut(View popupWindow_view) {
        // 创建PopupWindow
        mAnswerPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT, true);
        // 这里是位置显示方式,在屏幕底部
        mAnswerPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAnswerPopupWindow != null) {
                    mAnswerPopupWindow.dismiss();
                }
            }
        });
        disMissAnswerPopWindow();
    }

    /** 回答问题结果提示框延迟三秒消失 */
    public void disMissAnswerPopWindow() {
        mPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAnswerPopupWindow != null) {
                    try {
                        mAnswerPopupWindow.dismiss();
                    } catch (Exception e) {

                    }
                }
            }
        }, 3000);// 延迟3秒钟消失
    }

    /**
     * 发送试题答案
     *
     * @param result
     */
    private void sendQuestionResult(String result, VideoQuestionEntity questionEntity) {
        if (questionEntity == null) {
            return;
        }
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
        lectureLivePlayBackBll.saveQuestionResults(loadEntity, questionEntity.getSrcType(), questionEntity.getvQuestionID(), result,
                questionEntity.getAnswerDay(), mVideoEntity.getLiveId(), mVideoEntity.getvLivePlayBackType());
        questionEntity.setAnswered(true);
        questionViewGone();
        XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_ANSWER,
                XesMobAgent.XES_VIDEO_INTERACTIVE);
    }

    private Handler mPlayVideoControlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_QUESTION:
                    mIsShowQuestion = true;
                    if (mMediaController != null) {
                        mMediaController.showLong();
                    }
                    break;
                case NO_QUESTION:
                    if (mVideoCourseQuestionPager != null) {
                        mVideoCourseQuestionPager.hideInputMode();
                    }
                    mQuestionEntity = null;
                    questionViewGone();
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
            }
        }
    };

    /**
     * 主界面响应事件
     *
     * @param event
     * @author zouhao
     * @Create at: 2015-5-6 上午11:13:22 //
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PlaybackVideoEvent event) {
        if (event instanceof PlaybackVideoEvent.OnQuesionDown) {
            BaseVideoQuestionEntity questionEntity = ((PlaybackVideoEvent.OnQuesionDown) event).getVideoQuestionEntity();
            // 填空题答案保存
            if (questionEntity != null && questionEntity.getAnswerEntityLst() != null
                    && questionEntity.getAnswerEntityLst().size() != 0) {
                saveQuestionAnswer(questionEntity.getAnswerEntityLst());
                // 选择题答案保存
            } else {
                if (this.mQuestionEntity != null) {
                    if (this.mQuestionEntity.getvQuestionID().equals(questionEntity.getvQuestionID())) {
                        this.mQuestionEntity = (VideoQuestionEntity) questionEntity;
                    }
                }
            }

        } else if (event instanceof PlaybackVideoEvent.OnPlayVideoWebError) {
            String result = ((PlaybackVideoEvent.OnPlayVideoWebError) event).getResult();
            // 如果没有结果提示显示
            if (TextUtils.isEmpty(result)) {
                initRedPacketFirstResult(0, "金币+" + 0 + "枚金币");
            } else {
                localQuesitonResult(result);
            }
        } else if (event instanceof PlaybackVideoEvent.OnGetRedPacket) {
            VideoResultEntity entity = ((PlaybackVideoEvent.OnGetRedPacket) event).getVideoResultEntity();
            // 获取金币成功
            if (entity.getResultType() == 1) {
                initRedPacketResult(entity.getGoldNum());
                // 已经获取过金币
            } else if (entity.getResultType() == 0) {
                initRedPacketOtherResult();
            }
        } else if (event instanceof PlaybackVideoEvent.OnAnswerReslut) {
            VideoResultEntity entity = ((PlaybackVideoEvent.OnAnswerReslut) event).getVideoResultEntity();
            answerResultChk(entity);
        }
    }

    /** 保存学生填空题答案 */
    private void saveQuestionAnswer(List<AnswerEntity> answerEntityLst) {
        if (mQuestionEntity != null) {
            mQuestionEntity.setAnswerEntityLst(answerEntityLst);
        }
    }

    /**
     * 互动题本地结果验证
     *
     * @param result
     */
    private void localQuesitonResult(String result) {
        boolean isRight = true;
        VideoResultEntity entity = new VideoResultEntity();
        try {
            if (mQuestionEntity != null) {
                // 选择题
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(mQuestionEntity.getvQuestionType())) {
                    if (!TextUtils.equals(mQuestionEntity.getvQuestionAnswer(), result)) {
                        isRight = false;
                    }
                    // 填空题
                } else {
                    int rightNum = 0;
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String stuAnswer = jsonArray.getString(i).trim();
                        String rightAnswer = mQuestionEntity.getAnswerEntityLst().get(i).getRightAnswer();
                        if (TextUtils.equals(stuAnswer, rightAnswer)) {
                            rightNum++;
                        }
                    }
                    if (rightNum == 0) {
                        isRight = false;
                    } else if (rightNum != jsonArray.length()) {
                        entity.setRightNum(rightNum);
                    }

                }
            }
            // 回答正确
            if (isRight) {
                entity.setGoldNum(0);
                entity.setResultType(1);
                // 填空题部分正确
                if (entity.getRightNum() != 0) {
                    entity.setResultType(3);
                }
            } else {
                // 回答错误
                entity.setGoldNum(0);
                entity.setResultType(2);
            }
            answerResultChk(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 互动题结果解析
     *
     * @param entity
     */
    private void answerResultChk(VideoResultEntity entity) {
        // 回答正确提示
        if (entity.getResultType() == 1) {
            initAnswerRightResult(entity.getGoldNum());
            // 回答错误提示
        } else if (entity.getResultType() == 2) {
            initAnswerWrongResult();
            // 填空题部分正确提示
        } else if (entity.getResultType() == 3) {
            initAnswerPartRightResult(entity.getGoldNum());
        }
        mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
    }

    /**
     * 互动题回答正确
     *
     * @param goldNum
     */
    private void initAnswerPartRightResult(int goldNum) {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
        popupWindow_view.findViewById(R.id.iv_pop_question_answer_right).setBackgroundResource(R.drawable.bg_pop_question_answer_type3);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
        tvGoldHint.setText("" + goldNum);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 互动题回答正确
     *
     * @param goldNum
     */
    private void initAnswerRightResult(int goldNum) {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
        tvGoldHint.setText("" + goldNum);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 互动题回答错误
     */
    private void initAnswerWrongResult() {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_wrong, null, false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 获取红包成功
     *
     * @param goldNum
     */
    private void initRedPacketResult(int goldNum) {
        String msg = "+" + goldNum + "金币";
        View popupWindow_view = getLayoutInflater().inflate(R.layout.dialog_red_packet_success, null, false);
        SpannableString msp = new SpannableString(msg);
        float screenDensity = ScreenUtils.getScreenDensity();
        // 字体
        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_gold);
        tvGoldHint.setText(msp);
        popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerPopupWindow.dismiss();
            }
        });
        // 创建PopupWindow
        mAnswerPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT, true);
        // 这里是位置显示方式,在屏幕底部
        mAnswerPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAnswerPopupWindow != null) {
                    mAnswerPopupWindow.dismiss();
                }
            }
        });
        final TextView tvAutoclose = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
        final AtomicInteger count = new AtomicInteger(3);
        mPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count.set(count.get() - 1);
                if (count.get() == 0) {
                    mAnswerPopupWindow.dismiss();
                } else {
                    if (mAnswerPopupWindow != null && mAnswerPopupWindow.isShowing()) {
                        tvAutoclose.setText(count.get() + "秒自动关闭");
                        mPlayVideoControlHandler.postDelayed(this, 1000);
                    }
                }
            }
        }, 1000);
    }

    /**
     * 以获取过红包
     */
    private void initRedPacketOtherResult() {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_redpacket_other, null, false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scanRunnable != null) {
            scanRunnable.exit();
        }
    }

    /** 刷新界面重新加载视频 */
    protected void onRefresh() {
        if (mIsEnalbePlayer) {
            loadView(mLayoutVideo);
            Log.e("Duncan","refresh");
        }
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LivePlayBackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.MoreChoiceItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.RedPacketAlertDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionPlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackagePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

/**
 * Created by lyqai on 2018/7/23.
 */
public class LiveBackVideoFragment extends LiveVideoFragmentBase implements ActivityChangeLand {
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
    /** 视频进度 */
    private String mLastVideoPositionKey;
    /** 播放器统计时长发送间隔 */
    protected int mSendPlayVideoTime = 180;
    /** 购课id */
    protected String stuCourId;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    /** 播放路径名 */
    private String mWebPath;
    /** 是否显示无网络提示 */
    private boolean mIsShowNoWifiAlert = true;
    /** 节名称 */
    private String mSectionName;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    LiveBackBll liveBackBll;
    /** 我的课程业务层 */
    LectureLivePlayBackBll lectureLivePlayBackBll;
    /** 统计视频播放key */
    protected String mVisitTimeKey;
    private TextView tvLoadingContent;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    /** 初始进入播放器时的预加载界面 */
    private RelativeLayout rlFirstBackgroundView;
    private RelativeLayout bottom;
    /** 加载视频提示 */
    private ImageView ivLoading;

    protected boolean onVideoCreate(Bundle savedInstanceState) {
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        createTime = System.currentTimeMillis();
        AppBll.getInstance().registerAppEvent(this);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        initView();
        initData();
        return true;
    }

    private void initView() {
        // 预加载布局
        rlFirstBackgroundView = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_first_backgroud);
        bottom = (RelativeLayout) mContentView.findViewById(R.id.live_play_back_bottom);
        ivLoading = (ImageView) mContentView.findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingContent = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        ImageView ivBack = (ImageView) mContentView.findViewById(R.id.iv_course_video_back);

        if (ivBack != null) {
            mContentView.findViewById(R.id.iv_course_video_back).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        // 加载横屏时互动题的列表布局
        rlQuestionContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        // 加载竖屏时显示更多课程广告的布局
//        rlAdvanceContent = (RelativeLayout) mContentView.findViewById(R.id.rl_livevideo_playback);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMoreChoice = inflater.inflate(R.layout.layout_lecture_livevideoback, null);
//        mApplyNumber = (TextView) mMoreChoice.findViewById(R.id.tv_apply_number);
//        mMorecourse = (ListView) mMoreChoice.findViewById(R.id.morecourse_list);
//        ImageButton back = (ImageButton) mMoreChoice.findViewById(R.id.ib_back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 04.11 横竖屏的切换
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//        });
//        rlQuestionContent.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
//            @Override
//            public void onChildViewAdded(View parent, View child) {
//                Loger.d(TAG, "onChildViewAdded");
//            }
//
//            @Override
//            public void onChildViewRemoved(View parent, View child) {
//                Loger.d(TAG, "onChildViewRemoved");
//            }
//        });
    }

    /** 竖屏时填充视频列表布局 */
    protected void initData() {
        Intent intent = activity.getIntent();
        where = intent.getStringExtra("where");
        isArts = intent.getIntExtra("isArts", 0);
        if (isArts == 1) {
            appID = UmsConstants.ARTS_APP_ID_BACK;
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            appID = UmsConstants.LIVE_APP_ID_BACK;
            IS_SCIENCE = true;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        islocal = intent.getBooleanExtra("islocal", false);
        stuCourId = mVideoEntity.getStuCourseId();
        lectureLivePlayBackBll = new LectureLivePlayBackBll(activity, stuCourId);
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        where = intent.getStringExtra("where");
        isArts = intent.getIntExtra("isArts", 0);
        if (isArts == 1) {
            appID = UmsConstants.ARTS_APP_ID_BACK;
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            appID = UmsConstants.LIVE_APP_ID_BACK;
            IS_SCIENCE = true;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        lectureLivePlayBackBll.setLiveVideoSAConfig(liveVideoSAConfig);
        // 如果加载不出来
        if (tvLoadingContent != null) {
            tvLoadingContent.setText("正在获取视频资源，请稍候");
        }
        // 设置播放进度
        setmLastVideoPositionKey(mVideoEntity.getVideoCacheKey());
        // mCourseBll.getQuestionLivePlay(section);
        // 视频名
        mSectionName = mVideoEntity.getPlayVideoName();
        // 统计视频播放key
        mVisitTimeKey = mVideoEntity.getVisitTimeKey();
        // 播放器统计时长发送间隔
        setmSendPlayVideoTime(mVideoEntity.getvCourseSendPlayVideoTime());
        // 播放视频
        mWebPath = mVideoEntity.getVideoPath();
        liveBackBll = new LiveBackBll(activity, mVideoEntity);
        liveBackBll.setStuCourId(stuCourId);
//        if (CourseInfoLiveActivity.isTest) {
//            mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
//        }
//        if (AppConfig.DEBUG) {
//            mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
//        }
        addBusiness(activity);
        if (islocal) {
            // 互动题播放地址
            playNewVideo(Uri.parse(mWebPath), mSectionName);
        } else {
            activity.getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                    .OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    activity.getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                    BaseApplication baseApplication = (BaseApplication) activity.getApplication();
                    if (AppBll.getInstance(activity).isNetWorkAlert()) {
                        // 互动题播放地址
                        AppBll.getInstance(baseApplication);
                        playNewVideo(Uri.parse(mWebPath), mSectionName);
                    } else {
                        mIsShowNoWifiAlert = false;
                        AppBll.getInstance(baseApplication);
                    }
                    return false;
                }
            });
//            if (AppConfig.DEBUG) {
//                List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
//                VideoQuestionEntity videoQuestionEntity = new VideoQuestionEntity();
//                videoQuestionEntity.setvQuestionType("39804");
//                videoQuestionEntity.setvCategory(LocalCourseConfig.CATEGORY_LEC_ADVERT);
//                videoQuestionEntity.setvQuestionInsretTime(600);
//                videoQuestionEntity.setvEndTime(1600);
//                lstVideoQuestion.add(videoQuestionEntity);
//            }
            //测试红包自动关闭
//            rlFirstBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener
// () {
//                @Override
//                public boolean onPreDraw() {
//                    rlFirstBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
//                    initRedPacketResult(10);
//                    return false;
//                }
//            });
            //测试试卷
//            mQuestionEntity = new VideoQuestionEntity();
//            mQuestionEntity.setvQuestionID("2");
//            mQuestionEntity.setvEndTime(120);
//            showExam();
        }

        ProxUtil.getProxUtil().put(activity, ActivityChangeLand.class, this);
    }

    private void addBusiness(Activity activity) {
        QuestionPlayBackBll questionPlayBackBll = new QuestionPlayBackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(questionPlayBackBll);
        RedPackagePlayBackBll redPackagePlayBackBll = new RedPackagePlayBackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(redPackagePlayBackBll);
        liveBackBll.onCreate();
    }

    /** 发送统计时长Key */
    protected void setmLastVideoPositionKey(String mLastVideoPositionKey) {
        this.mLastVideoPositionKey = mLastVideoPositionKey;
    }

    /** 播放器统计时长发送间隔 */
    protected void setmSendPlayVideoTime(int mSendPlayVideoTime) {
        this.mSendPlayVideoTime = mSendPlayVideoTime;
    }

    protected void updateLoadingImage() {
        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
        }
    }
}

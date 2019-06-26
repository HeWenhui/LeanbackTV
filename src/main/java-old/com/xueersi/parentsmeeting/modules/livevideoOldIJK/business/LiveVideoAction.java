package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.PlayErrorCodeLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2018/7/18.
 * 普通三分屏的加载页
 */
public class LiveVideoAction implements VideoAction {
    private final String TAG = getClass().getSimpleName();
    Logger logger = LoggerFactory.getLogger(TAG);
    protected WeakHandler mHandler = new WeakHandler(null);
    Activity activity;
    /** 初始进入播放器时的预加载界面 */
    protected RelativeLayout rlFirstBackgroundView;
    /** 老师不在直播间 */
    protected ImageView ivTeacherNotpresent;
    /** 老师不在直播间背景图 */
    protected Drawable dwTeacherNotpresen;
    PlayErrorCode lastPlayErrorCode;
    RelativeLayout mContentView;
    protected TextView tvLoadingHint;
    /** 缓冲提示 */
    private ImageView ivLoading;
    /** 视频连接 */
    private final String playLoad = "正在获取视频资源，请稍后";
    /** 连接老师加载-主讲 */
    private final String mainTeacherLoad = "正在连接主讲老师，请耐心等候";
    /** 连接老师加载-辅导 */
    private final String coachTeacherLoad = "正在连接辅导老师，请耐心等候";
    /** 直播类型 */
    protected int liveType;
    protected LiveGetInfo mGetInfo;
    protected LiveBll2 mLiveBll;
    protected LogToFile mLogtf;
    /** 切换线路layout */
    private ConstraintLayout layoutSwitchFlow;
    /** 切流失败的文字提示 */
    private FangZhengCuYuanTextView tvSwitchFlowRetry;
    private LinearLayout linearLayout;
    /** 切流失败的重试按钮 */
//    private Button btnSwitchFlowRetry;
    public final static int SWITCH_FLOW_NORMAL = 0;
    //线程切换中
    public final static int SWITCH_FLOW_ROUTE_SWITCH = 1;
    //重试中
    public final static int SWITCH_FLOW_RELOAD = 1 << 1;
    /** 切换视频流的状态 */
    private int videoSwitchFlowStatus = SWITCH_FLOW_NORMAL;

    private int pattern = 0;

    private Button btnRetry;

    private boolean isSmallEnglish;

    private boolean isExperience = false;

    public LiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView) {
        this.activity = activity;
        this.mLiveBll = mLiveBll;
        liveType = mLiveBll.getLiveType();
        this.mContentView = mContentView;
        rlFirstBackgroundView = mContentView.findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = mContentView.findViewById(R.id.iv_course_video_teacher_notpresent);
        tvLoadingHint = mContentView.findViewById(R.id.tv_course_video_loading_content);
        ivLoading = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        tvLoadingHint.setText("正在获取视频资源，请稍后");


        linearLayout = mContentView.findViewById(R.id.layout_livevideo_switch_flow_logo);

//        btnSwitchFlowRetry = mContentView.findViewById(R.id.btn_livevideo_switch_flow_retry_btn);

        mLogtf = new LogToFile(activity, TAG);
        updateLoadingImage();

        pattern = activity.getIntent().getIntExtra("pattern", 2);
        isExperience = activity.getIntent().getBooleanExtra("isExperience", false);
        isSmallEnglish = activity.getIntent().getBooleanExtra("isSmallEnglish", false);
        if (pattern == 1 && !isExperience) {
            layoutSwitchFlow = mContentView.findViewById(R.id.layout_livevideot_triple_screen_fail_retry);
            tvSwitchFlowRetry = mContentView.findViewById(R.id.fzcy_livevideo_switch_flow_retry_text);
            setVideoLayout();
            btnRetry = mContentView.findViewById(R.id.btn_livevideo_switch_flow_retry_btn);
            switchFlowViewChangeBtn();
        }
    }

    private void switchFlowViewChangeBtn() {
        Drawable drawable = activity.getResources().getDrawable(R.drawable.selector_livevideo_primary_science_retry_btn);
        if (LiveVideoConfig.isSmallChinese) {
            drawable = activity.getResources().getDrawable(R.drawable.selector_livevideo_primary_chs_retry_btn);
        } else if (LiveVideoConfig.isPrimary) {
            drawable = activity.getResources().getDrawable(R.drawable.selector_livevideo_primary_science_retry_btn);
        } else if (isSmallEnglish) {
            drawable = activity.getResources().getDrawable(R.drawable.selector_livevideo_small_english_retry_btn);
        }
        btnRetry.setBackground(drawable);
    }

    /** 设置重试按钮的 */
    private void setVideoLayout() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layoutSwitchFlow.getLayoutParams();

//        layoutParams.width = liveVideoPoint.x3 - liveVideoPoint.x2;
        layoutParams.rightMargin = liveVideoPoint.getRightMargin();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutSwitchFlow.setLayoutParams(layoutParams);
    }

    public void onPlaySuccess() {
        if (layoutSwitchFlow != null) {
            layoutSwitchFlow.setVisibility(View.GONE);
        }
    }

    /** 0代表不是切换线路，正数代表切换的线路 */
    private AtomicInteger route = new AtomicInteger(0);

    public void setVideoSwitchFlowStatus(int status, int route) {
        this.videoSwitchFlowStatus = status;
//        this.route = route;
        this.route.set(route);
    }

    /**
     * 设置蓝屏界面
     */
    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlFirstBackgroundView.getLayoutParams();
        int rightMargin = liveVideoPoint.getRightMargin();
        int topMargin = liveVideoPoint.y2;
        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
            params.rightMargin = rightMargin;
            params.bottomMargin = params.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
        }
    }

    private int times = 0;

    public void onPlayError() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (times > 3) {
                    tvLoadingHint.setText("您的手机暂时不支持播放直播");
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
                } else {
                    rePlay(false);
                }
                times++;
            }
        });
    }

    public void rePlay(final boolean modechange) {
        LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean isPresent = mLiveBll.isPresent();
                if (isPresent) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (tvLoadingHint != null) {
                                mLogtf.d("rePlay:liveType=" + liveType + ",mode=" + mGetInfo.getLiveTopic().getMode()
                                        + ",lastPlayErrorCode=" + lastPlayErrorCode);
                                lastPlayErrorCode = null;
                                if (!modechange) {
                                    if (pattern == 1 && !isExperience) {
//                                        linearLayout.setVisibility(View.VISIBLE);
//                                        layoutSwitchFlow.setVisibility(View.GONE);
                                        logger.i("显示linearLayout,layoutSwitchFlow隐藏");

//                                        logger.i();
                                        if (videoSwitchFlowStatus == SWITCH_FLOW_RELOAD) {
//                                        mContentView.findViewById(R.id.layout_livevideot_triple_screen_fail_retry).setVisibility(View.VISIBLE);
                                            //网校logo
//                                            mContentView.findViewById(R.id.layout_livevideo_switch_flow_logo).setVisibility(View.VISIBLE);
                                            tvLoadingHint.setText(playLoad);
                                            linearLayout.setVisibility(View.VISIBLE);
                                            layoutSwitchFlow.setVisibility(View.GONE);
                                            logger.i("");
                                        } else if (videoSwitchFlowStatus == SWITCH_FLOW_ROUTE_SWITCH) {
//                                            mContentView.findViewById(R.id.layout_livevideo_switch_flow_logo).setVisibility(View.VISIBLE);
                                            String strRoute = "";
                                            if (route.get() == 1) {
                                                strRoute = "一";
                                            } else if (route.get() == 2) {
                                                strRoute = "二";
                                            } else if (route.get() == 3) {
                                                strRoute = "三";
                                            } else if (route.get() == 4) {
                                                strRoute = "四";
                                            }
                                            linearLayout.setVisibility(View.VISIBLE);
                                            layoutSwitchFlow.setVisibility(View.GONE);
                                            tvLoadingHint.setText("线路" + strRoute + "切换中...");
                                        }
                                    } else {
                                        tvLoadingHint.setText(playLoad);
                                    }
                                }
                            }
                        }
                    });
                } else {

                    logger.i("老师不在直播间");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tvLoadingHint != null && !modechange) {
                                if (pattern == 1 && !isExperience) {
                                    logger.i("显示linearLayout,layoutSwitchFlow隐藏");
//                                        logger.i();
                                    if (videoSwitchFlowStatus == SWITCH_FLOW_RELOAD) {
//                                        mContentView.findViewById(R.id.layout_livevideot_triple_screen_fail_retry).setVisibility(View.VISIBLE);
                                        //网校logo
//                                            mContentView.findViewById(R.id.layout_livevideo_switch_flow_logo).setVisibility(View.VISIBLE);
                                        tvLoadingHint.setText(playLoad);
                                        linearLayout.setVisibility(View.VISIBLE);
                                        layoutSwitchFlow.setVisibility(View.GONE);
                                        logger.i("");
                                    } else if (videoSwitchFlowStatus == SWITCH_FLOW_ROUTE_SWITCH) {
//                                            mContentView.findViewById(R.id.layout_livevideo_switch_flow_logo).setVisibility(View.VISIBLE);
                                        String strRoute = "";
                                        if (route.get() == 1) {
                                            strRoute = "一";
                                        } else if (route.get() == 2) {
                                            strRoute = "二";
                                        } else if (route.get() == 3) {
                                            strRoute = "三";
                                        } else if (route.get() == 4) {
                                            strRoute = "四";
                                        }

                                        linearLayout.setVisibility(View.VISIBLE);
                                        layoutSwitchFlow.setVisibility(View.GONE);
                                        tvLoadingHint.setText("线路" + strRoute + "切换中...");
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void playComplete() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                int visibility = rlFirstBackgroundView.getVisibility();
                logger.d("playComplete:First=" + visibility);
                if (tvLoadingHint != null) {
                    PlayErrorCode playErrorCode = PlayErrorCode.TEACHER_LEAVE_200;
                    lastPlayErrorCode = playErrorCode;
                    tvLoadingHint.setVisibility(View.VISIBLE);
                    int netWorkState = NetWorkHelper.getNetWorkState(activity);
                    if (netWorkState == NetWorkHelper.NO_NETWORK) {
                        tvLoadingHint.setText(PlayErrorCode.PLAY_NO_WIFI.getTip());
                    } else {
                        tvLoadingHint.setText("视频播放失败[" + playErrorCode.getCode() + "]");
                    }
                    LiveTopic.RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
                    if (status != null) {
                        mLogtf.d("playComplete:classbegin=" + status.isClassbegin());
                    }
                    //统计日志
                    PlayErrorCodeLog.livePlayError(mLiveBll, playErrorCode);
                }
            }
        });
    }


    public void onFail(final int arg1, final int arg2) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                //如果是三分屏
                if (isSmallEnglish || LiveVideoConfig.isPrimary || LiveVideoConfig.isSmallChinese) {
                    if (videoSwitchFlowStatus == SWITCH_FLOW_ROUTE_SWITCH) {
                        UmsAgentManager.umsAgentCustomerBusiness(activity, activity.getResources().getString(R.string
                                .livevideo_switch_flow_170712));
                        if (!mLiveBll.isPresent()) {
                            if (mContentView.findViewById(R.id.iv_course_video_teacher_notpresent) != null) {
                                mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.VISIBLE);
                            }
                        }
                        linearLayout.setVisibility(View.GONE);
                        layoutSwitchFlow.setVisibility(View.VISIBLE);
                        String strRoute = "一";
                        if (route.get() == 1) {
                            strRoute = "一";
                        } else if (route.get() == 2) {
                            strRoute = "二";
                        } else if (route.get() == 3) {
                            strRoute = "三";
                        } else if (route.get() == 4) {
                            strRoute = "四";
                        }
                        tvSwitchFlowRetry.setText("线路" + strRoute + "切换失败");
                    } else if (videoSwitchFlowStatus == SWITCH_FLOW_RELOAD) {
                        UmsAgentManager.umsAgentCustomerBusiness(activity, activity.getResources().getString(R.string
                                .livevideo_switch_flow_170710));
                        if (!mLiveBll.isPresent()) {
                            if (mContentView.findViewById(R.id.iv_course_video_teacher_notpresent) != null) {
                                mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.VISIBLE);
                            }
                        }
                        linearLayout.setVisibility(View.GONE);
                        layoutSwitchFlow.setVisibility(View.VISIBLE);
                        tvSwitchFlowRetry.setText("加载失败");
                    }
                }
//                else {
//                    linearLayout.setVisibility(View.VISIBLE);
//                    layoutSwitchFlow.setVisibility(View.GONE);
//                }
                if (tvLoadingHint != null) {
                    PlayErrorCode playErrorCode = PlayErrorCode.getError(arg2);
                    lastPlayErrorCode = playErrorCode;
                    if (tvLoadingHint != null) {
                        tvLoadingHint.setVisibility(View.VISIBLE);
                        int netWorkState = NetWorkHelper.getNetWorkState(activity);
                        if (netWorkState == NetWorkHelper.NO_NETWORK) {
                            playErrorCode = PlayErrorCode.PLAY_NO_WIFI;
                            tvLoadingHint.setText(PlayErrorCode.PLAY_NO_WIFI.getTip());
                        } else {
                            tvLoadingHint.setText("视频播放失败[" + playErrorCode.getCode() + "]");
                        }
                    }
//                    if (mLiveBll.isPresent()) {
//                        if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo
// .getLiveTopic().getMode())) {
//                            tvLoadingHint.setText(mainTeacherLoad);
//                        } else {
//                            tvLoadingHint.setText(coachTeacherLoad);
//                        }
//                    }
                    LiveTopic.RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
                    if (status != null) {
                        mLogtf.d("onFail:classbegin=" + status.isClassbegin());
                    }
                    //统计日志
                    PlayErrorCodeLog.livePlayError(mLiveBll, playErrorCode);
                }
            }
        });
    }

    @Override
    public void onTeacherNotPresent(boolean isBefore) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int visibility = rlFirstBackgroundView.getVisibility();
                mLogtf.d("onTeacherNotPresent:First=" + visibility);
                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                } else {
                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
                    if (dwTeacherNotpresen == null) {
                        if (LiveVideoConfig.isSmallChinese) {
                            dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable.
                                    livevideo_small_chinese_zw_dengdaida_bg_psnormal);
                        } else if (LiveVideoConfig.isPrimary) {
                            dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable
                                    .livevideo_zw_dengdaida_bg_psnormal);
                        } else if (mGetInfo != null && mGetInfo.getSmallEnglish()) {//如果是小学英语
                            dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable
                                    .livevideo_small_english_zw_dengdaida_bg_psnormal);
                        }  else {
                            dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable
                                    .livevideo_zw_dengdaida_bg_normal);
                        }
                    }
                    ivTeacherNotpresent.setBackgroundDrawable(dwTeacherNotpresen);
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View
                            .INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onTeacherQuit(boolean isQuit) {

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        this.mGetInfo = getInfo;
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        final AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (change.get()) {
                    setFirstBackgroundVisible(View.VISIBLE);
                }
                if (tvLoadingHint != null) {
                    if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo
                            .getLiveTopic().getMode())) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        });
    }

    @Override
    public void onLiveTimeOut() {
//        final Button bt = mContentView.findViewById(R.id.bt_course_video_livetimeout);
//        if (bt != null) {
//            bt.setVisibility(View.VISIBLE);
//            bt.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bt.getLayoutParams();
//                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
////                    lp.leftMargin = LiveVideoPoint.getInstance().x3 / 2 - bt.getWidth() / 2;
////                    if (tvLoadingHint != null) {
////                        int[] outLocation = new int[2];
////                        tvLoadingHint.getLocationInWindow(outLocation);
////                        lp.topMargin = outLocation[1] + tvLoadingHint.getHeight() + 20;
////                    } else {
////                        lp.topMargin = LiveVideoPoint.getInstance().screenHeight * 2 / 3 - 40;
////                    }
//                    bt.setLayoutParams(lp);
//                    bt.getViewTreeObserver().removeOnPreDrawListener(this);
//                    return false;
//                }
//            });
//            bt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mLiveBll.liveGetPlayServer();
//                    v.setVisibility(View.GONE);
//                }
//            });
//        } else {
//            XESToastUtils.showToast(activity, "老师不在直播间,请退出直播间重试");
//        }
    }

    @Override
    public void onClassTimoOut() {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "你来晚了，下课了，等着看回放吧";
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                lastPlayErrorCode = null;
                setFirstBackgroundVisible(View.VISIBLE);
                if (isPresent) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                }
                if (tvLoadingHint != null) {
                    if (LiveTopic.MODE_CLASS.endsWith(mode)) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        };
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "" + responseEntity.getErrorMsg();
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    @Override
    public void onLiveDontAllow(final String msg) {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
        XESToastUtils.showToast(activity, "将在3秒内退出");
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("msg", msg);
                activity.setResult(ShareBusinessConfig.LIVE_USER_ERROR, intent);
                activity.finish();
            }
        }, 3000);
    }

    @Override
    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
        lastPlayErrorCode = playErrorCode;
        if (playErrorCode != null && ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
            setFirstBackgroundVisible(View.VISIBLE);
            if (tvLoadingHint != null) {
                tvLoadingHint.setVisibility(View.VISIBLE);
                tvLoadingHint.setText(playErrorCode.getTip());
            }
        }
        //统计日志
        if (playErrorCode != null) {
            PlayErrorCodeLog.livePlayError(mLiveBll, playErrorCode);
        }
    }

    public void updateLoadingImage() {
        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class,
                false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable
                        .livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into
                        (ivLoading);
            }
        }
    }

    public void setFirstBackgroundVisible(int visible) {
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void onDestory() {
        dwTeacherNotpresen = null;
    }
}

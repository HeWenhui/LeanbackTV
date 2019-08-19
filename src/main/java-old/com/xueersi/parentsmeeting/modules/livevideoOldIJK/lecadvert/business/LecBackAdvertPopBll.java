package com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.event.MiniEvent;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.MoreChoiceItem;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.PauseNotStopVideoInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatWindowManager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/1/15.
 * 讲座回放广告弹窗
 */
public class LecBackAdvertPopBll {
    private String TAG = "LecBackAdvertPopBll";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private LecBackAdvertHttp lecBackAdvertHttp;
    private WeakHandler handler = new WeakHandler(null);
    private Activity activity;
    private ViewGroup mParent;
    private Boolean picinpic = false;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    private List<MoreChoice.Choice> mChoices = new ArrayList<>();
    private ListView mMorecourse;
    private CommonAdapter<MoreChoice.Choice> mCourseAdapter;
    private MoreChoice mData;
    private TextView mApplyNumber;
    private View mMoreChoice;
    /** 更多课程广告的布局 */
    private RelativeLayout rlAdvanceContent;
    /** 当前界面是否横屏 */
    protected AtomicBoolean mIsLand;
    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;
    private View mFloatView;
    private PopupWindow mPopupWindows;
    private BroadcastReceiver receiver;
    LiveAndBackDebug liveAndBackDebug;

    public LecBackAdvertPopBll(Activity activity) {
        this.activity = activity;
        EventBus.getDefault().register(this);
        liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
    }

    public void setmVideoEntity(VideoLivePlayBackEntity mVideoEntity) {
        this.mVideoEntity = mVideoEntity;
    }

    public void setLecBackAdvertHttp(LecBackAdvertHttp lecBackAdvertHttp) {
        this.lecBackAdvertHttp = lecBackAdvertHttp;
    }

    public void initView(RelativeLayout rlQuestionContent, AtomicBoolean mIsLand) {
        this.mIsLand = mIsLand;
        this.rlQuestionContent = rlQuestionContent;
        // 加载竖屏时显示更多课程广告的布局
        rlAdvanceContent = (RelativeLayout) activity.findViewById(R.id.rl_livevideo_playback);
        LayoutInflater inflater = LayoutInflater.from(activity);
        mMoreChoice = inflater.inflate(R.layout.layout_lecture_livevideoback, null);
        mApplyNumber = (TextView) mMoreChoice.findViewById(R.id.tv_apply_number);
        mMorecourse = (ListView) mMoreChoice.findViewById(R.id.morecourse_list);
        ImageButton back = (ImageButton) mMoreChoice.findViewById(R.id.ib_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 04.11 横竖屏的切换
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refreshadvertisementlist");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 04.12 弹出广告的时候，需要刷新广告列表
                lecBackAdvertHttp.getMoreCourseChoices(mVideoEntity.getLiveId(), getDataCallBack);
            }
        };
        activity.registerReceiver(receiver, intentFilter);
        // 04.04 更多课程的数据加载
        if (mCourseAdapter == null) {
            mCourseAdapter = new CommonAdapter<MoreChoice.Choice>(mChoices) {
                @Override
                public AdapterItemInterface<MoreChoice.Choice> getItemView(Object type) {
                    MoreChoiceItem morelistItem = new MoreChoiceItem(activity, mData);
                    return morelistItem;
                }
            };
            mMorecourse.setAdapter(mCourseAdapter);
        }
        // 04.12 第一次进入的时候，就去请求回放的所有广告信息
        lecBackAdvertHttp.getMoreCourseChoices(mVideoEntity.getLiveId(), getDataCallBack);
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }

    public void getMoreCourseChoices() {
        lecBackAdvertHttp.getMoreCourseChoices(mVideoEntity.getLiveId(), getDataCallBack);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    AbstractBusinessDataCallBack getDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            // 04.04 获取到数据之后的逻辑处理
            if (objData.length > 0) {
                mData = (MoreChoice) objData[0];
                mChoices.clear();
                mChoices.addAll(mData.getCases());
                LiveVideoConfig.MORE_COURSE = mChoices.size();
                mApplyNumber.setText(Html.fromHtml("<font color='#333333'>正在报名中</font>" + "<font color='#F13232'>" + "  " + mChoices.size() + "</font>"));
                mCourseAdapter.updateData(mChoices);
            }
        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlQuestionContent.getLayoutParams();
        logger.d( "onConfigurationChanged:mIsLand=" + mIsLand);
        if (mIsLand.get()) {
            lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            lp.addRule(RelativeLayout.BELOW, 0);
            if (mPopupWindows != null) {
                mPopupWindows.dismiss();
                mPopupWindows = null;
            }
            if (LiveVideoConfig.MORE_COURSE > 0) {
                showPopupwindowboard();
            }
            rlAdvanceContent.setVisibility(View.GONE);
        } else {
            lp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            lp.addRule(RelativeLayout.BELOW, R.id.rl_course_video_content);
            if (mPopupWindows != null) {
                mPopupWindows.dismiss();
                mPopupWindows = null;
            }
            // 04.11 获取更多课程信息
            lecBackAdvertHttp.getMoreCourseChoices(mVideoEntity.getLiveId(), getDataCallBack);
            // 04.11 展示更多课程报名的列表信息
            rlAdvanceContent.setVisibility(View.VISIBLE);
            rlAdvanceContent.setLayoutParams(lp);
            rlAdvanceContent.removeAllViews();
            rlAdvanceContent.addView(mMoreChoice);
        }
        rlQuestionContent.setLayoutParams(lp);
    }

    private void showPopupwindowboard() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        mFloatView = inflater.inflate(R.layout.livemessage_jumpboard, null);
        mPopupWindows = new PopupWindow(mFloatView, 360, 90, false);
        mPopupWindows.setOutsideTouchable(false);
        mPopupWindows.showAtLocation(mFloatView, Gravity.BOTTOM | Gravity.LEFT, ScreenUtils.getScreenWidth() - 420, 160);
        // 03.29 横竖屏的切换
        mFloatView.findViewById(R.id.switch_orientation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断当前屏幕方向
                ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand.class);
                activityChangeLand.changeLOrP();
            }
        });
        TextView totalnum = (TextView) mFloatView.findViewById(R.id.tv_apply_totalnum);
        totalnum.setText(LiveVideoConfig.MORE_COURSE + "");
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(MiniEvent event) {
        logger.d("onEvent:mIsLand=" + mIsLand.get());
        if ("Order".equals(event.getMin())) {
            if (mIsLand.get()) {
                final String courseId = event.getCourseId();
                final String classId = event.getClassId();
                //判断当前屏幕方向
                ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand.class);
                activityChangeLand.changeLOrP();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createRealVideo(courseId, classId);
                    }
                }, 500);
            } else {
                createRealVideo(event.getCourseId(), event.getClassId());
            }
            // 添加点击立即报名的日志
            StableLogHashMap logHashMap = new StableLogHashMap("clickEnroll");
            logHashMap.put("adsid", "" + event.getAdId());
            logHashMap.addSno("5").addStable("2");
            logHashMap.put("extra", "点击了立即报名");
            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
            LiveVideoConfig.LECTUREADID = event.getAdId();
        }
        if ("ConfirmClick".equals(event.getMin())) {
            // 添加用户点击提交订单日志
            StableLogHashMap logHashMap = new StableLogHashMap("clickSubmitOrder");
            logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
            logHashMap.addSno("6").addStable("2");
            logHashMap.put("extra", "点击了立即支付");
            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
        }
        if ("OrderPaySuccess".equals(event.getMin())) {
            // 添加用户购买成功的日志
            StableLogHashMap logHashMap = new StableLogHashMap("purchaseSucceed");
            logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
            logHashMap.addSno("7").addStable("2");
            logHashMap.put("orderid", event.getCourseId());
            logHashMap.put("extra", "用户支付成功");
            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
        }
    }

    private void createRealVideo(String courseId, String classId) {
        boolean isPermission = XesPermission.applyFloatWindow(activity);

        //有对应权限或者系统版本小于7.0
        if (isPermission || Build.VERSION.SDK_INT < 24) {
            mParent = (ViewGroup) videoView.getParent();
            if (mParent != null) {
                mParent.removeView(videoView);
            }
            //开启悬浮窗
            OtherModulesEnter.intentToOrderConfirmActivity(activity, courseId + "-" + classId, 100, "LectureLiveVideoActivity");
            FloatWindowManager.addView(activity, videoView, 2);
            PauseNotStopVideoInter pauseNotStopVideoIml = ProxUtil.getProxUtil().get(activity, PauseNotStopVideoInter.class);
            pauseNotStopVideoIml.setPause(true);
            picinpic = true;
        }
    }

    void onRestart() {
        if (picinpic) {
            ViewGroup parents = (ViewGroup) videoView.getParent();
            if (parents != null) {
                parents.removeView(videoView);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mParent.addView(videoView, params);
            }
            picinpic = !picinpic;
        }
    }

    void onStop() {
        PauseNotStopVideoInter pauseNotStopVideoIml = ProxUtil.getProxUtil().get(activity, PauseNotStopVideoInter.class);
        pauseNotStopVideoIml.setPause(false);
    }

    void onNewIntent(Intent intent) {
        ViewGroup parents = (ViewGroup) videoView.getParent();
        if (parents != null) {
            parents.removeView(videoView);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mParent.addView(videoView, params);
        }
    }

    void onDestroy() {
        activity.unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
    }

}

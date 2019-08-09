package com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xueersi.common.event.MiniEvent;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.PauseNotStopVideoInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatWindowManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/31.
 */

public class LecAdvertPopBll {
    private WeakHandler handler = new WeakHandler(null);
    private Activity activity;
    private ViewGroup mParent;
    /** 当前界面是否横屏 */
    protected AtomicBoolean mIsLand;
    private PopupWindow mPopupWindows;
    private View mFloatView;
    private Boolean picinpic = false;
    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;
    LiveAndBackDebug liveAndBackDebug;

    LecAdvertPopBll(Activity activity) {
        this.activity = activity;
        EventBus.getDefault().register(this);
        liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
    }

    void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }

    public void setmIsLand(AtomicBoolean mIsLand) {
        this.mIsLand = mIsLand;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(MiniEvent event) {
        if ("Order".equals(event.getMin())) {
            final String courseId = event.getCourseId();
            final String classId = event.getClassId();
            if (mIsLand.get()) {
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
            PauseNotStopVideoInter onPauseNotStopVideo = ProxUtil.getProxUtil().get(activity, PauseNotStopVideoInter.class);
            onPauseNotStopVideo.setPause(true);
            // 添加点击立即报名的日志
            StableLogHashMap logHashMap = new StableLogHashMap("clickEnroll");
            logHashMap.put("adsid", "" + event.getAdId());
            logHashMap.addSno("5").addStable("2");
            logHashMap.put("extra", "点击了立即报名");
            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
            LiveVideoConfig.LECTUREADID = event.getAdId();
        }
        if ("Invisible".equals(event.getMin())) {
            if (mPopupWindows != null && mPopupWindows.isShowing()) {
                mPopupWindows.dismiss();
            }
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
        if ("Advertisement".equals(event.getMin())) {
            // 收到广告指令就弹出面板抽屉
            if (mIsLand.get() && LiveVideoConfig.MORE_COURSE > 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mPopupWindows != null) {
                            mPopupWindows.dismiss();
                            mPopupWindows = null;
                        }
                        showPopupwindow();
                    }
                }, 1000);
            }
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
            FloatWindowManager.addView(activity, videoView, 1);
            picinpic = true;
        }

    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (mIsLand.get()) {
            if (mPopupWindows != null) {
                mPopupWindows = null;
            }
            if (LiveVideoConfig.MORE_COURSE > 0) {
                showPopupwindow();
            }
        } else {
            if (mPopupWindows != null && mPopupWindows.isShowing()) {
                mPopupWindows.dismiss();
            }

        }
    }

    private void showPopupwindow() {
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
                LiveVideoConfig.isloading = true;
            }
        });
        TextView totalnum = (TextView) mFloatView.findViewById(R.id.tv_apply_totalnum);
        totalnum.setText(LiveVideoConfig.MORE_COURSE + "");
    }

    void onResume() {
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
}

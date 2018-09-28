package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience;

import android.app.Activity;
import android.os.Build;
import android.view.ViewGroup;

import com.xueersi.common.event.MiniEvent;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoInter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatWindowManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 将VideoView转化为小窗口
 */
public class VideoPopView {

    private VideoView videoView;

    private Activity activity;

    private MiniEvent event;
    //是否开启了小窗口
    private Boolean isShow = false;


    public VideoPopView(Activity mContext, MiniEvent miniEvent) {
        this.activity = mContext;
        event = miniEvent;
    }

    public MiniEvent getEvent() {
        return event;
    }

    public void setEvent(MiniEvent event) {
        this.event = event;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(MiniEvent event) {
        if ("OrderPaySuccess".equals(event.getMin())) {
            // 添加用户购买成功的日志
            StableLogHashMap logHashMap = new StableLogHashMap("purchaseSucceed");
            logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
            logHashMap.addSno("7").addStable("2");
            logHashMap.put("orderid", event.getCourseId());
            logHashMap.put("extra", "用户支付成功");
//            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
        }
    }

    public void popupVideoView() {
        if (event != null) {
            final String courseId = event.getCourseId();
            final String classId = event.getClassId();
//            if (mIsLand.get()) {
            //判断当前屏幕方向
            ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand.class);
            activityChangeLand.changeLOrP();
            videoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createRealVideo(courseId, classId);
                }
            }, 500);
//            } else {
//                createRealVideo(event.getCourseId(), event.getClassId());
//            }
            PauseNotStopVideoInter onPauseNotStopVideo = ProxUtil.getProxUtil().get(activity, PauseNotStopVideoInter
                    .class);
            onPauseNotStopVideo.setPause(true);
            // 添加点击立即报名的日志
            StableLogHashMap logHashMap = new StableLogHashMap("clickEnroll");
            logHashMap.put("adsid", "" + event.getAdId());
            logHashMap.addSno("5").addStable("2");
            logHashMap.put("extra", "点击了立即报名");
//            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
            LiveVideoConfig.LECTUREADID = event.getAdId();
        }
    }

    private ViewGroup mParent;

    private void createRealVideo(String courseId, String classId) {
        boolean isPermission = XesPermission.applyFloatWindow(activity);
        //有对应权限或者系统版本小于7.0
        if (isPermission || Build.VERSION.SDK_INT < 24) {
            if (videoView.getParent() != null) {
                mParent = (ViewGroup) videoView.getParent();
                if (mParent != null) {
                    mParent.removeView(videoView);
                }
            }
            //开启悬浮窗
            OtherModulesEnter.intentToOrderConfirmActivity(activity, courseId + "-" + classId, 100,
                    "LivePlaybackVideoActivity");
            FloatWindowManager.addView(activity, videoView, 1);
            isShow = true;
        }

    }

    public void onResume() {
        if (isShow) {//还原VideoView
            ViewGroup parents = (ViewGroup) videoView.getParent();
            if (parents != null) {
                parents.removeView(videoView);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mParent.addView(videoView, params);
            }
            isShow = false;
        }
    }
}

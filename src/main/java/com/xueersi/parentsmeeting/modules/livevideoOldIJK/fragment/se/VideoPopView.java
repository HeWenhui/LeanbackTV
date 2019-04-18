package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;

import com.xueersi.common.event.AppEvent;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.PauseNotStopVideoInter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.StandExperienceRecommondCourseEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatLayout;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatWindowManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 将VideoView转化为小窗口
 */
public class VideoPopView {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private VideoView videoView;

    private Context activity;
    //是否开启了小窗口
    private Boolean isShow = false;

    public VideoPopView(Context activity, VideoView videoView) {
        this.activity = activity;
        this.videoView = videoView;
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnPaySuccessEvent event) {
//        if ("OrderPaySuccess".equals(event.getMin())) {

        // 添加用户购买成功的日志
        StableLogHashMap logHashMap = new StableLogHashMap("purchaseSucceed");
        logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
        logHashMap.addSno("7").addStable("2");
//            logHashMap.put("orderid", event.getCourseId());
        logger.i("支付成功");
        logHashMap.put("extra", "用户支付成功");
//            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
//        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent( AppEvent.OnPaySuccessEvent event) {
//        turnToOrder(event);
//    }

    public void turnToOrder(StandExperienceRecommondCourseEvent event) {
        if (event != null) {
            if ("Order".equals(event.getTip())) {
                final String courseId = event.getCourseId();
                final String classId = event.getClassId();
                logger.i("跳转到订单页面");
//            if (mIsLand.get()) {
                //判断当前屏幕方向
//                ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand
// .class);
//                activityChangeLand.changeLOrP();
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
                if (onPauseNotStopVideo != null) {
                    onPauseNotStopVideo.setPause(true);
                }
                // 添加点击立即报名的日志
                StableLogHashMap logHashMap = new StableLogHashMap("clickEnroll");
//            logHashMap.put("adsid", "" + event.getAdId());
                logHashMap.addSno("5").addStable("2");
                logHashMap.put("extra", "点击了立即报名");
//            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
//            LiveVideoConfig.LECTUREADID = event.getAdId();
            }
        }
    }

    private ViewGroup mParent;

    /**
     * 跳转到购课页面，同时开启悬浮窗
     *
     * @param courseId
     * @param classId
     */
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
            logger.i("开启悬浮窗");
            logger.i("courseId = " + courseId + " - classId = " + classId);
            //跳转到支付页面
            OtherModulesEnter.intentToOrderConfirmActivity((Activity) activity, courseId + "-" + classId, 100,
                    "LivePlaybackVideoActivity");
            //开启悬浮窗
            FloatWindowManager.addView(activity, videoView, FloatLayout.INTENT_TO_LivePlaybackVideoActivity);
            isShow = true;
        } else {
            logger.i("没有权限并且系统大于等于24");
        }

    }

    /**
     * Fragment进入onResume的时候从悬浮窗中(FloatLayout)移出这个videoView;
     */
    public void onResume() {
        if (isShow) {//还原VideoView
            ViewGroup parents = (ViewGroup) videoView.getParent();
            if (parents != null) {
                logger.i("移出原来的浮窗videoView");
                parents.removeView(videoView);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mParent.addView(videoView, params);
                logger.i("恢复原状");
            }
            isShow = false;
        }
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

import com.tencent.bugly.crashreport.BuglyLog;
import com.xueersi.common.base.XesActivity;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.FileLogger;
import com.xueersi.parentsmeeting.module.audio.AudioPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.LiveLogUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.LiveLogBill;
import com.xueersi.parentsmeeting.modules.livevideo.service.LiveService;
import com.xueersi.ui.dataload.DataLoadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;


/***
 * 视频播放主界面
 *
 * @author 林玉强
 */
public class LiveVideoActivityBase extends XesActivity {
    private String TAG = "LiveVideoActivityBaseLog";
    /** 当前界面是否横屏 */
    protected boolean mIsLand = false;
    LiveVideoFragmentBase liveVideoFragmentBase;

    // endregion

    // region 生命周期及系统调用
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        FileLogger.runActivity = this;
        super.onCreate(savedInstanceState);
        // 统计视频点击某个视频
        XesMobAgent.userMarkVideoClick();
        // 注册事件
        EventBus.getDefault().register(this);
        loadView(R.layout.activity_video_live_frag);
        try {
            Intent intent = new Intent(this, LiveService.class);
            intent.putExtra("livepid", android.os.Process.myPid());
            intent.putExtra("liveintent", getIntent().getExtras());
            startService(intent);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        BuglyLog.i(TAG, "onCreate");
        LiveLogBill.getInstance().initLiveLog();
        if (AppConfig.DEBUG) {
            Map<String, String> map = new HashMap<>();
            map.put("liveId", getIntent().getStringExtra("vSectionID"));
            map.put("live_start_pause", getIntent().getStringExtra("vSectionID"));
            UmsAgentManager.umsAgentDebug(this, LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map);
        }
        LiveLogBill.getInstance().setLiveId(getIntent().getStringExtra("vSectionID"));
        LiveLogBill.getInstance().openLiveLog();
//        FloatWindowManager.addView(this,new Button(this),2);
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
        if (liveVideoFragmentBase != null) {
            liveVideoFragmentBase.setRequestedOrientation(requestedOrientation);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        BuglyLog.i(TAG, "onResume");
        FileLogger.runActivity = this;
        //关闭系统后台声音
//        AudioPlayer.requestAudioFocus(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BuglyLog.i(TAG, "onPause");
//        AudioPlayer.abandAudioFocus(this);
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONPAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        BuglyLog.i(TAG, "onStop");
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONSTOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BuglyLog.i(TAG, "onDestroy");
        // 注销事件
        EventBus.getDefault().unregister(this);
        stopService(new Intent(this, LiveService.class));
//        System.exit(0);
        if (FileLogger.runActivity == this) {
            FileLogger.runActivity = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mIsLand = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE; //
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public final void onBackPressed() {
        BuglyLog.i(TAG, "onBackPressed");
        if (liveVideoFragmentBase != null) {
            liveVideoFragmentBase.onBackPressed();
        }
    }

    /**
     * 用户点击返回，判断是不是程序崩溃
     */
    protected void onUserBackPressed() {
        finish(LiveVideoConfig.VIDEO_CANCLE);
    }

    // endregion

    // region 播放管理业务

    /** 加载界面 */
    protected void loadView(int id) {
//        setContentView(id);
        getWindow().setBackgroundDrawable(null);
        liveVideoFragmentBase = (LiveVideoFragmentBase) getFragmentManager().findFragmentByTag("liveVideo");
        if (liveVideoFragmentBase == null) {
            liveVideoFragmentBase = getFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            // TODO
            fragmentTransaction.add(android.R.id.content, liveVideoFragmentBase, "liveVideo");
//            fragmentTransaction.add(R.id.rl_course_video_contentview, liveVideoFragmentBase, "liveVideo");
            fragmentTransaction.commit();
        } else {
            restoreFragment(liveVideoFragmentBase);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 使屏幕保持长亮
    }

    protected void restoreFragment(LiveVideoFragmentBase liveVideoFragmentBase) {

    }

    protected LiveVideoFragmentBase getFragment() {
        return new LiveVideoFragmentBase();
    }

    /**
     * 改变界面加载数据状态
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataLoadEvent(AppEvent.OnDataLoadingEvent event) {
        if (event.dataLoadEntity != null) {
            DataLoadManager.newInstance().loadDataStyle(this, event.dataLoadEntity);
        }
    }

    protected void updateRefreshImage() {

    }
}

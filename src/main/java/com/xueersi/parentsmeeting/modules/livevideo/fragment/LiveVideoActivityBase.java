package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.xueersi.common.base.XesActivity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.util.audio.AudioPlayer;
import com.xueersi.lib.log.FileLogger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoFragment;
import com.xueersi.ui.dataload.DataLoadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/***
 * 视频播放主界面
 *
 * @author 林玉强
 */
public class LiveVideoActivityBase extends XesActivity {
    private String TAG = "LiveVideoActivityBaseLog";
    /** 所在的Activity是否已经onCreated */
    private boolean mCreated = false;
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
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
        liveVideoFragmentBase.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mCreated)
            return;
    }


    @Override
    public void onResume() {
        super.onResume();
        FileLogger.runActivity = this;
        //关闭系统后台声音
        AudioPlayer.requestAudioFocus(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        AudioPlayer.abandAudioFocus(this);
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONPAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mCreated)
            return;
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONSTOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mCreated)
            return;
        // 注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mIsLand = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE; //
    }

    @Override
    public final void onBackPressed() {
        liveVideoFragmentBase.onBackPressed();
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
        setContentView(id);
        getWindow().setBackgroundDrawable(null);
        liveVideoFragmentBase = getFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.rl_course_video_contentview, liveVideoFragmentBase, "liveVideo");
        fragmentTransaction.commit();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 使屏幕保持长亮
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

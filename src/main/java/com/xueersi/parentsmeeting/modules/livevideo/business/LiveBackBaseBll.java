package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseBll;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/17.
 * 直播回放总bll
 */
public class LiveBackBaseBll extends BaseBll implements LiveViewAction {
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    protected LiveBackBll liveBackBll;
    protected Activity activity;
    protected RelativeLayout mRootViewBottom;
    /** 过时，使用LiveViewAction和实现的方法替代*/
    @Deprecated
    protected RelativeLayout mRootView;
    /** 视频节对象,尽量使用liveGetInfo */
    protected VideoLivePlayBackEntity mVideoEntity;
    protected LiveGetInfo liveGetInfo;
    protected AtomicBoolean mIsLand;
    private Handler mHandler = LiveMainHandler.getMainHandler();
    protected final int mLiveType;
    protected LiveVideoPoint liveVideoPoint;
    private LiveViewAction liveViewAction;

    public LiveBackBaseBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity);
        this.activity = activity;
        this.liveBackBll = liveBackBll;
        mLiveType = liveBackBll.getLiveType();
    }

    public final void onCreateF(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String,
            Object> businessShareParamMap) {
        this.mVideoEntity = mVideoEntity;
        this.liveGetInfo = liveGetInfo;
        onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
    }


    public final void initViewF(LiveViewAction liveViewAction, RelativeLayout rlQuestionContentBottom, RelativeLayout bottomContent, AtomicBoolean
            mIsLand) {
        this.liveViewAction = liveViewAction;
        mRootViewBottom = rlQuestionContentBottom;
        mRootView = bottomContent;
        this.mIsLand = mIsLand;
        initView();
    }

    public void initView() {

    }

    public void onConfigurationChanged(Configuration newConfig) {

    }

    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {

    }

    /** 回放event事件 */
    public int[] getCategorys() {
        return new int[0];
    }

    /** 回放事件开始 */
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {

    }

    /** 回放事件结束 */
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {

    }

    public LivePlayBackHttpManager getCourseHttpManager() {
        return liveBackBll.getCourseHttpManager();
    }

    public LivePlayBackHttpResponseParser getCourseHttpResponseParser() {
        return liveBackBll.getCourseHttpResponseParser();
    }

    public LiveHttpManager getmHttpManager() {
        return liveBackBll.getmHttpManager();
    }

    protected void onRestart() {

    }

    protected void onStop() {

    }

    protected void onNewIntent(Intent intent) {

    }

    protected void onResume() {

    }

    /**
     * activity onDestroy
     */
    public void onDestroy() {

    }

    public void onPositionChanged(long position) {

    }

    public void onPausePlayer() {
    }

    public void onStartPlayer() {
    }

    public void setSpeed(float speed) {
    }

    public <T> T getInstance(Class<T> clazz) {
        return ProxUtil.getProxUtil().get(mContext, clazz);
    }

    public <T> T removeInstance(Class<T> clazz) {
        return ProxUtil.getProxUtil().remove(mContext, clazz);
    }

    public <T> void putInstance(Class<T> clazz, T object) {
        ProxUtil.getProxUtil().put(mContext, clazz, object);
    }

    public final void setVideoLayoutF(LiveVideoPoint liveVideoPoint) {
        this.liveVideoPoint = liveVideoPoint;
        setVideoLayout(liveVideoPoint);
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {

    }

    public LiveViewAction getLiveViewAction() {
        return liveViewAction;
    }

    public void addView(View child) {
        liveViewAction.addView(child);
    }

    public void addView(LiveVideoLevel level, View child) {
        liveViewAction.addView(level, child);
    }

    @Override
    public void addView(View child, int width, int height) {
        liveViewAction.addView(child, width, height);
    }

    public void removeView(View child) {
        liveViewAction.removeView(child);
    }

    public void addView(View child, ViewGroup.LayoutParams params) {
        liveViewAction.addView(child, params);
    }

    public void addView(LiveVideoLevel level, View child, ViewGroup.LayoutParams params) {
        liveViewAction.addView(level, child, params);
    }

    public View inflateView(int resource) {
        return liveViewAction.inflateView(resource);
    }

    public <T extends View> T findViewById(int id) {
        return liveViewAction.findViewById(id);
    }

    public final boolean post(Runnable r) {
        return mHandler.post(r);
    }

    public final boolean postDelayed(Runnable r, long uptimeMillis) {
        return mHandler.postDelayed(r, uptimeMillis);
    }

    public void removeCallbacks(Runnable action) {
        mHandler.removeCallbacks(action);
    }
}

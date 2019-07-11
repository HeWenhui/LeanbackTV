package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BaseBll;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveActivityState;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 直播间 bll 基类
 *
 * @author chekun
 * created  at 2018/6/20 9:34
 */
public class LiveBaseBll extends BaseBll {

    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    protected RelativeLayout mRootView;
    protected RelativeLayout rlMessageBottom;
    protected RelativeLayout mContentView;
    protected LiveBll2 mLiveBll;
    protected LiveAndBackDebug contextLiveAndBackDebug;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    protected LiveGetInfo mGetInfo;
    protected String mLiveId;
    protected final int mLiveType;
    protected Activity activity;
    protected LogToFile mLogtf;
    protected LiveVideoPoint liveVideoPoint;
    private AtomicBoolean mIsLand;
    protected int mState = LiveActivityState.INITIALIZING;
    private boolean mDestroyed;
    private LiveViewAction liveViewAction;

    public LiveBaseBll(Activity context, LiveBll2 liveBll) {
        super(context);
        this.activity = context;
        contextLiveAndBackDebug = new ContextLiveAndBackDebug(context);
        mLiveBll = liveBll;
        mLiveId = liveBll.getLiveId();
        mLiveType = liveBll.getLiveType();
        mLogtf = new LogToFile(context, TAG);
    }

    /**
     * 为兼容 回放  新增此构造函数
     *
     * @param context
     * @param liveId
     * @param liveType
     */
    public LiveBaseBll(Activity context, String liveId, int liveType) {
        super(context);
        this.activity = context;
        contextLiveAndBackDebug = new ContextLiveAndBackDebug(context);
        this.mLiveId = liveId;
        this.mLiveType = liveType;
        mLogtf = new LogToFile(TAG);
    }

    public LiveAndBackDebug getLiveAndBackDebug() {
        return contextLiveAndBackDebug;
    }

    /**
     * 获取网络请求对象
     */
    public LiveHttpManager getHttpManager() {
        LiveHttpManager manager = null;
        if (mLiveBll != null) {
            manager = mLiveBll.getHttpManager();
        }
        return manager;
    }

    public LiveHttpResponseParser getHttpResponseParser() {
        if (mLiveBll != null) {
            return mLiveBll.getHttpResponseParser();
        } else {
            return null;
        }
    }


    /**
     * 发送直播间聊天消息
     */
    public void sendMsg(JSONObject jsonObject) {
        if (mLiveBll != null) {
            mLiveBll.sendMessage(jsonObject);
        }
    }


    /**
     * 发送 notice 消息
     *
     * @param jsonObject
     * @param target     notice 接收放   如果 target 为null 将广播给所以用户
     */
    public void sendNotice(JSONObject jsonObject, String target) {
        if (mLiveBll != null) {
            mLiveBll.sendNotice(target, jsonObject);
            logger.i("发送IRC" + target + " obj = " + jsonObject.toString());
        }
    }

    /**
     * 向主讲发送消息
     *
     * @param jsonObject 消息内容
     */
    public void sendNoticeToMain(JSONObject jsonObject) {
        if (mLiveBll != null) {
            mLiveBll.sendNoticeToMain(jsonObject);
        }
    }

    /**
     * 向主讲发送消息
     *
     * @param jsonObject 消息内容
     */
    public void sendMessageMain(JSONObject jsonObject) {
        if (mLiveBll != null) {
            mLiveBll.sendMessageMain(jsonObject);
        }
    }

    /**
     * 向辅导发送消息
     *
     * @param jsonObject 消息内容
     */
    public void sendNoticeToCoun(JSONObject jsonObject) {
        if (mLiveBll != null) {
            mLiveBll.sendNoticeToCoun(jsonObject);
        }
    }

    /**
     * 向辅导发送消息
     *
     * @param jsonObject 消息内容
     */
    public void sendMessageCoun(JSONObject jsonObject) {
        if (mLiveBll != null) {
            mLiveBll.sendMessageCoun(jsonObject);
        }
    }

    public final void setVideoLayoutF(LiveVideoPoint liveVideoPoint) {
        this.liveVideoPoint = liveVideoPoint;
        setVideoLayout(liveVideoPoint);
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {

    }

    /**
     * 上传 系统日志
     *
     * @param eventId
     * @param data
     */
    public void umsAgentDebugSys(String eventId, Map<String, String> data) {
        if (contextLiveAndBackDebug != null) {
            contextLiveAndBackDebug.umsAgentDebugSys(eventId, data);
        }
    }

    /**
     * 上传交互日志
     *
     * @param eventId
     * @param data
     */
    public void umsAgentDebugInter(String eventId, Map<String, String> data) {
        if (contextLiveAndBackDebug != null) {
            contextLiveAndBackDebug.umsAgentDebugInter(eventId, data);
        }
    }


    /**
     * 上传 展现日志
     *
     * @param eventId
     * @param data
     */
    public void umsAgentDebugPv(String eventId, Map<String, String> data) {
        if (contextLiveAndBackDebug != null) {
            contextLiveAndBackDebug.umsAgentDebugPv(eventId, data);
        }
    }


    /**
     * 直播间初始化完成
     *
     * @param getInfo 直播间初始化参数
     */
    public void onLiveInited(LiveGetInfo getInfo) {
        this.mGetInfo = getInfo;
    }

    public void onArtsExtLiveInited(LiveGetInfo getInfo) {

    }

    public final void initViewF(LiveViewAction liveViewAction, RelativeLayout rlMessageBottom, RelativeLayout bottomContent, AtomicBoolean mIsLand, RelativeLayout mContentView) {
        this.mRootView = bottomContent;
        this.liveViewAction = liveViewAction;
        this.rlMessageBottom = rlMessageBottom;
        this.mContentView = mContentView;
        this.mIsLand = mIsLand;
        initView(bottomContent, mIsLand);
    }

    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {

    }

    /**
     * 直播间创建
     */
    public void onCreate(HashMap<String, Object> data) {
        mState = LiveActivityState.CREATED;
    }

    public void onStart() {
        mState = LiveActivityState.STARTED;
    }

    /**
     * activity onPause
     */
    public void onPause() {
        mState = LiveActivityState.STARTED;
    }

    /**
     * activity onStop
     */
    public void onStop() {
        mState = LiveActivityState.STOPPED;
    }

    /**
     * activity onResume
     */
    public void onResume() {
        mState = LiveActivityState.RESUMED;
    }

    /**
     * activity onDestroy
     */
    public void onDestroy() {
        mState = LiveActivityState.INITIALIZING;
        mDestroyed = true;
    }

    /**
     * Returns true if the final {@link #onDestroy()} call has been made
     * on the Activity, so this instance is now dead.
     */
    public boolean isDestroyed() {
        return mDestroyed;
    }

    ///公共管理View 添加、移除、虚拟键引起布局 变化相关

    /**
     * 弹出toast，判断activity是不是在活动
     *
     * @param text
     */
    public void showToast(String text) {
        ActivityStatic activityStatic = (ActivityStatic) mContext;
        if (activityStatic.isResume()) {
            XESToastUtils.showToast(mContext, text);
        }
    }

    public void onModeChange(String oldMode, String mode, boolean isPresent) {

    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        ActivityStatic activityStatic = (ActivityStatic) mContext;
        if (activityStatic.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
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

    public LiveViewAction getLiveViewAction() {
        return liveViewAction;
    }

    public void addView(View child) {
        liveViewAction.addView(child);
    }

    public void addView(LiveVideoLevel level, View child) {
        liveViewAction.addView(level, child);
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

//    public void addView(View child, int index, ViewGroup.LayoutParams params) {
//        liveViewAction.addView(child, index, params);
//    }

//    public void addView(LiveVideoLevel level, View child, int index, ViewGroup.LayoutParams params) {
//        liveViewAction.addView(level, child, index, params);
//    }

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

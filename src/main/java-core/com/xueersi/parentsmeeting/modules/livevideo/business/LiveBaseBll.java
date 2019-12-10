package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseBll;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveActivityState;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEnvironment;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpAction;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
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
public class LiveBaseBll extends BaseBll implements LiveViewAction {

    protected Logger logger = LiveLoggerFactory.getLogger(getClass().getSimpleName());
    /** 过时，使用LiveViewAction和实现的方法替代 */
    @Deprecated
    protected RelativeLayout mRootView;
    protected RelativeLayout mContentView;
    protected LiveBll2 mLiveBll;
    protected LiveAndBackDebug contextLiveAndBackDebug;
    private Handler mHandler = LiveMainHandler.getMainHandler();
    protected LiveGetInfo mGetInfo;
    protected String mLiveId;
    protected final int mLiveType;
    protected Activity activity;
    protected LogToFile mLogtf;
    protected LiveVideoPoint liveVideoPoint;
    protected AtomicBoolean mIsLand;
    protected int mState = LiveActivityState.INITIALIZING;
    private boolean mDestroyed;
    protected LiveViewAction liveViewAction;

    protected int pluginId = -1;
    /**
     * notice 辅导标识
     **/
    protected static final String NOTICE_KEY_F = "f";


    public LiveBaseBll(Activity context, LiveBll2 liveBll) {
        super(context);
        this.activity = context;
        contextLiveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
        mLiveBll = liveBll;
        if (liveBll != null) {
            mLiveId = liveBll.getLiveId();
            mLiveType = liveBll.getLiveType();
        } else {
            mLiveId = "0";
            mLiveType = 3;
        }
        mLogtf = new LogToFile(context, TAG);
    }

    public LiveBaseBll(LiveEnvironment liveEnvironment, LiveBll2 liveBll) {
        super(liveEnvironment.getActivity());
        this.activity = liveEnvironment.getActivity();
        contextLiveAndBackDebug = liveEnvironment.getLiveAndBackDebug();
        mLiveBll = liveBll;
        mLiveId = liveBll.getLiveId();
        mLiveType = liveBll.getLiveType();
        mLogtf = liveEnvironment.createLogToFile(TAG);
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
        contextLiveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
        this.mLiveId = liveId;
        this.mLiveType = liveType;
        mLogtf = new LogToFile(context, TAG);
    }

    /**
     * 为兼容 回放  新增此构造函数
     *
     * @param liveEnvironment
     * @param liveId
     * @param liveType
     */
    public LiveBaseBll(LiveEnvironment liveEnvironment, String liveId, int liveType) {
        super(liveEnvironment.getActivity());
        this.activity = liveEnvironment.getActivity();
        contextLiveAndBackDebug = liveEnvironment.getLiveAndBackDebug();
        this.mLiveId = liveId;
        this.mLiveType = liveType;
        mLogtf = liveEnvironment.createLogToFile(TAG);
    }

    public LiveAndBackDebug getLiveAndBackDebug() {
        return contextLiveAndBackDebug;
    }


    public View getContentView() {
        return mRootView;
    }

    /**
     * 获取网络请求对象
     */
    protected final LiveHttpAction getLiveHttpAction() {
        LiveHttpAction liveHttpAction = null;
        if (mLiveBll != null) {
            liveHttpAction = mLiveBll.getLiveHttpAction();
        }
        return liveHttpAction;
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
     * 是否是辅导态
     *
     * @return
     */
    protected boolean isInTraningMode() {
        return LiveTopic.MODE_TRANING.equals(mLiveBll != null ? mLiveBll.getMode() : "");
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
    @CallSuper
    public void onLiveInited(LiveGetInfo getInfo) {
        this.mGetInfo = getInfo;
    }

    public void onArtsExtLiveInited(LiveGetInfo getInfo) {

    }

    public final void initViewF(LiveViewAction liveViewAction, RelativeLayout bottomContent, AtomicBoolean mIsLand, RelativeLayout mContentView) {
        this.mRootView = bottomContent;
        this.liveViewAction = liveViewAction;
        this.mContentView = mContentView;
        this.mIsLand = mIsLand;
        initView();
    }

    public void initView() {

    }

    /**
     * 直播间创建
     */
    @CallSuper
    public void onCreate(HashMap<String, Object> data) {
        mState = LiveActivityState.CREATED;
    }

    @CallSuper
    public void onStart() {
        mState = LiveActivityState.STARTED;
    }

    /**
     * activity onPause
     */
    @CallSuper
    public void onPause() {
        mState = LiveActivityState.STARTED;
    }

    /**
     * activity onStop
     */
    @CallSuper
    public void onStop() {
        mState = LiveActivityState.STOPPED;
    }

    /**
     * activity onResume
     */
    @CallSuper
    public void onResume() {
        mState = LiveActivityState.RESUMED;
    }

    /**
     * activity onDestroy
     */
    @CallSuper
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

    public void setPluginId(int pluginId) {
        this.pluginId = pluginId;
    }

    public int getPluginId() {
        return pluginId;
    }
}

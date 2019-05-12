package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewParent;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.AllLiveBasePagerInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/7/5.
 * 直播基础pager
 */
public class LiveBasePager<T> extends BasePager<T> implements LiveAndBackDebug {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    protected LogToFile mLogtf;
    protected LiveAndBackDebug mLiveBll;
    protected LivePagerBack livePagerBack;
    protected BaseVideoQuestionEntity baseVideoQuestionEntity;
    protected OnPagerClose onPagerClose;
    protected Handler handler = new Handler(Looper.getMainLooper());

    public LiveBasePager(Context context) {
        super(context);
    }

    public BaseVideoQuestionEntity getBaseVideoQuestionEntity() {
        return baseVideoQuestionEntity;
    }

    public void setBaseVideoQuestionEntity(BaseVideoQuestionEntity baseVideoQuestionEntity) {
        this.baseVideoQuestionEntity = baseVideoQuestionEntity;
    }

    /***
     * 构造函数
     *
     * @param context   上下文对象
     * @param isNewView 是否初始化布局
     */
    public LiveBasePager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    /***
     * 构造函数
     *
     * @param context   上下文对象
     * @param obj       模型实体
     * @param isNewView 是否初始化布局
     */
    public LiveBasePager(Context context, T obj, boolean isNewView) {
        init(context);
        mEntity = obj;
        if (isNewView) {
            mView = initView();
        }
    }

    public void setLivePagerBack(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

    public void setOnPagerClose(OnPagerClose onPagerClose) {
        this.onPagerClose = onPagerClose;
    }

    public LivePagerBack getLivePagerBack() {
        return livePagerBack;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mLogtf = new LogToFile(context, TAG);
        mLogtf.addCommon("creattime", "" + System.currentTimeMillis());
        AllLiveBasePagerInter allLiveBasePagerInter = ProxUtil.getProxUtil().get(context, AllLiveBasePagerInter.class);
        if (allLiveBasePagerInter != null) {
            allLiveBasePagerInter.addLiveBasePager(this);
        }
    }

    @Override
    public View initView() {
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mContext != null) {
            AllLiveBasePagerInter allLiveBasePagerInter = ProxUtil.getProxUtil().get(mContext, AllLiveBasePagerInter.class);
            if (allLiveBasePagerInter != null) {
                allLiveBasePagerInter.removeLiveBasePager(this);
            }
        }
    }

    public LiveAndBackDebug getLiveAndBackDebug() {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        return mLiveBll;
    }

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        if (mLiveBll != null) {
            mLiveBll.umsAgentDebugSys(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        if (mLiveBll != null) {
            mLiveBll.umsAgentDebugInter(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        if (mLiveBll != null) {
            mLiveBll.umsAgentDebugPv(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        if (mLiveBll != null) {
            mLiveBll.umsAgentDebugSys(eventId, stableLogHashMap);
        }
    }

    @Override
    public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        if (mLiveBll != null) {
            mLiveBll.umsAgentDebugInter(eventId, stableLogHashMap);
        }
    }

    @Override
    public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        if (mLiveBll != null) {
            mLiveBll.umsAgentDebugPv(eventId, stableLogHashMap);
        }
    }

    public boolean onUserBackPressed() {
        if (mView != null) {
            ViewParent parent = mView.getParent();
            if (parent == null) {
                mLogtf.d("onUserBackPressed:parent=null");
                return false;
            }
        }
        if (livePagerBack != null) {
            livePagerBack.onBack(this);
            return true;
        }
        return false;
    }

    /**
     * pager关闭
     */
    public interface OnPagerClose {
        void onClose(LiveBasePager basePager);
    }

    public static class WrapOnPagerClose implements LiveBasePager.OnPagerClose {
        OnPagerClose onPagerClose;

        public WrapOnPagerClose(LiveBasePager.OnPagerClose onPagerClose) {
            this.onPagerClose = onPagerClose;
        }

        @Override
        public void onClose(LiveBasePager basePager) {
            onPagerClose.onClose(basePager);
        }
    }

    /**
     * 是不是有父布局
     *
     * @return
     */
    public boolean isAttach() {
        ViewParent parent = mView.getParent();
        return parent != null;
    }
}

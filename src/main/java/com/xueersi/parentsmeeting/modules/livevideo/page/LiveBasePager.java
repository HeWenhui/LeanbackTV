package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.os.Environment;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.AllLiveBasePagerInter;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.io.File;
import java.util.Map;

/**
 * Created by linyuqiang on 2018/7/5.
 * 直播基础pager
 */
public class LiveBasePager<T> extends BasePager<T> implements LiveAndBackDebug {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    protected LogToFile mLogtf;
    protected LiveAndBackDebug mLiveBll;

    public LiveBasePager(Context context) {
        super(context);
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

    @Override
    protected void init(Context context) {
        super.init(context);
        mLogtf = new LogToFile(context, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
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

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        mLiveBll.umsAgentDebugSys(eventId, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        mLiveBll.umsAgentDebugInter(eventId, mData);
    }

    @Override
    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
        if (mLiveBll == null) {
            mLiveBll = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
        }
        mLiveBll.umsAgentDebugPv(eventId, mData);
    }
}

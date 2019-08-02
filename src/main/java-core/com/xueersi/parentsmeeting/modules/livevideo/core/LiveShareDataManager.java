package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;

import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * 直播的存储
 */
public class LiveShareDataManager {
    private LiveGetInfo liveGetInfo;

    public static LiveShareDataManager getInstance(Context context) {
        LiveShareDataManager mInstance = ProxUtil.getProxUtil().get(context, LiveShareDataManager.class);
        if (mInstance == null) {
            mInstance = new LiveShareDataManager();
            ProxUtil.getProxUtil().put(context, LiveShareDataManager.class, mInstance);
        }
        return mInstance;
    }

    public LiveGetInfo getLiveGetInfo() {
        return liveGetInfo;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    private static LiveShareDataManager mInstance;

    public static LiveShareDataManager getInstance() {
        if (mInstance == null) {
            synchronized (LiveShareDataManager.class) {
                if (mInstance == null) {
                    mInstance = new LiveShareDataManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 返回String
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String defValue) {
        return ShareDataManager.getInstance().getString(key, defValue, ShareDataManager.SHAREDATA_USER);
    }

    /**
     * 存String
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        ShareDataManager.getInstance().put(key, value, ShareDataManager.SHAREDATA_USER);
    }
}

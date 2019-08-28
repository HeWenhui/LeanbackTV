package com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol;

import android.content.Context;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.base.XueErSiRunningEnvironment;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.JsonUtil;

import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LiveModuleConfigInfo;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LivePlugin;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.xueersi.common.sharedata.ShareDataManager.SHAREDATA_NOT_CLEAR;


/**
 * 直播 Plugin Config Bill
 *
 * @author shixiaoqiang
 */
public class LiveBusinessPluginManager extends BaseBll {


    private static LiveBusinessPluginManager mInstance;
    public LiveModuleConfigInfo mLiveModuleConfigInfo;


    public static LiveBusinessPluginManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LiveBusinessPluginManager.class) {
                if (mInstance == null) {
                    mInstance = new LiveBusinessPluginManager(context);
                }
            }
        }
        return mInstance;
    }


    private LiveBusinessPluginManager(Context context) {
        super(context);
        getLiveModuleConfigInfo();
    }


    //---------------------------------以下为存储----------------------------------------

    /**
     * 获取直播Plugin配置信息
     *
     * @return
     */
    public LiveModuleConfigInfo getLiveModuleConfigInfo() {

        if (mLiveModuleConfigInfo == null) {
            String json = mShareDataManager.getString(LivePluginGrayConfig.LIVE_PLUGIN_CONFIG_INFO, "",
                    ShareDataManager.SHAREDATA_NOT_CLEAR);
            mLiveModuleConfigInfo = JsonUtil.getEntityFromJson(json, LiveModuleConfigInfo.class);
        }

        if(mLiveModuleConfigInfo!=null){
            logger.i(" save LiveModuleConfigInfo is:" + mLiveModuleConfigInfo.toString());
        }

        return mLiveModuleConfigInfo;
    }



    /**
     * 根据moduleId 查找 Plugin
     *
     * @param moduleId
     * @return
     */
    public LivePlugin getLivePluginByModuleId(int moduleId) {
        LivePlugin plugin = null;
        LiveModuleConfigInfo info = getLiveModuleConfigInfo();
        if (info != null && info.plugins != null) {
            List<LivePlugin> plugins = info.plugins;
            for (int i = 0; i < plugins.size(); i++) {
                if (moduleId == plugins.get(i).moduleId) {
                    plugin = plugins.get(i);
                    break;
                }
            }
        }

        return plugin;
    }


    /**
     * 根据pluginName 查找 Plugin
     *
     * @param pluginName
     * @return
     */
    public LivePlugin getLivePluginByPluginName(String pluginName) {
        LivePlugin plugin = null;
        LiveModuleConfigInfo info = getLiveModuleConfigInfo();
        if (info != null && info.plugins != null) {
            List<LivePlugin> plugins = info.plugins;
            for (int i = 0; i < plugins.size(); i++) {
                if (pluginName.equals(plugins.get(i).pluginName)) {
                    plugin = plugins.get(i);
                    break;
                }
            }
        }
        return plugin;
    }


    /**
     *
     * 根据moudlid key 返回属性
     * @param moudleId
     * @param key
     * @return
     */
    public String getProperties(int moudleId,String key){
        LivePlugin plugin = LiveBusinessPluginManager.getInstance(XueErSiRunningEnvironment.sAppContext).
                getLivePluginByModuleId(LivePluginGrayConfig.MOUDLE_GIFT);
        if(plugin!=null) {
            Map<String,String > maplist = plugin.properties;
            if(maplist!=null) {
                return maplist.get(key);
            }
        }
        return "";
    }



    /**
     *
     * 根据moudlid 功能是否打开
     * @param moudleId
     * @param key
     * @return
     */
    public boolean isMoudleAllowed(int moudleId){
        LivePlugin plugin = LiveBusinessPluginManager.getInstance(XueErSiRunningEnvironment.sAppContext).
                getLivePluginByModuleId(LivePluginGrayConfig.MOUDLE_GIFT);
        if(plugin!=null) {
            return plugin.isAllowed;
        }
        return false;
    }
}

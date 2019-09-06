package com.xueersi.parentsmeeting.modules.livevideo.liveLog;

import com.xueersi.common.base.AppOpenConfigEntity;
import com.xueersi.common.base.AppUpBusiConfigInterface;

/**
 * 直播监控日志 app启动初始化
 */
public class LiveApmLogAppUpConfig implements AppUpBusiConfigInterface {


    @Override
    public void init() {
        LiveLogBill.getInstance().initLiveLog(); //初始化直播日志
        LiveLogBill.getInstance().openAppLiveLog();//
        LiveLogBill.getInstance().initLiveBisLog();//初始化直播业务日志
    }

    @Override
    public AppOpenConfigEntity getConfig() {

        AppOpenConfigEntity entity = new AppOpenConfigEntity();
        entity.delayTime = 200; //200毫秒
        entity.onMainThread = true;
        return entity;
    }


}

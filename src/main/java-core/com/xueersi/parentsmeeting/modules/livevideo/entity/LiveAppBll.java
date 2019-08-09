package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.common.business.AppBll;

public class LiveAppBll {
    private static LiveAppBll mInstance;

    private LiveAppBll() {

    }

    public static synchronized LiveAppBll getInstance() {
        if (mInstance == null) {
            synchronized (LiveAppBll.class) {
                if (mInstance == null) {
                    mInstance = new LiveAppBll();
                }
            }
        }
        return mInstance;
    }

    public boolean isNetWorkAlert() {
        return AppBll.getInstance().isNetWorkAlert();
    }

    public boolean isNotificationOnlyWIFI() {
        return AppBll.getInstance().getAppInfoEntity().isNotificationOnlyWIFI();
    }

    public boolean isNotificationMobileAlert() {
        return AppBll.getInstance().getAppInfoEntity().isNotificationMobileAlert();
    }

    public void registerAppEvent(Object obj) {
        AppBll.getInstance().registerAppEvent(obj);
    }

    public void unRegisterAppEvent(Object obj){
        AppBll.getInstance().unRegisterAppEvent(obj);
    }
}

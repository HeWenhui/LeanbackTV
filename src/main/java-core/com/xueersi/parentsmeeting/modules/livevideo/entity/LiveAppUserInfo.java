package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;

public class LiveAppUserInfo {
    private static LiveAppUserInfo mInstance;

    public static synchronized LiveAppUserInfo getInstance() {
        if (mInstance == null) {
            synchronized (UserBll.class) {
                if (mInstance == null) {
                    mInstance = new LiveAppUserInfo();
                }
            }
        }
        return mInstance;
    }

    public String getStuId() {
        return UserBll.getInstance().getMyUserInfoEntity().getStuId();
    }

    public String getNickName() {
        return UserBll.getInstance().getMyUserInfoEntity().getNickName();
    }

    /** 用户在登陆时使用的帐号（有可能是帐号有可能是手机，在注册时给值，还有登陆时记录值） */
    public String getLoginUserName() {
        return AppBll.getInstance().getAppInfoEntity().getLoginUserName();
    }

    public String getEnglishName() {
        return UserBll.getInstance().getMyUserInfoEntity().getEnglishName();
    }

    public String getRealName() {
        return UserBll.getInstance().getMyUserInfoEntity().getRealName();
    }

    public int getSex() {
        return UserBll.getInstance().getMyUserInfoEntity().getSex();
    }

    public String getHeadImg() {
        return UserBll.getInstance().getMyUserInfoEntity().getHeadImg();
    }
}

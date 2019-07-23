package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.framework.utils.string.StringUtils;

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

    public String getPsimId() {
        return UserBll.getInstance().getMyUserInfoEntity().getPsimId();
    }

    public String getPsimPwd() {
        return UserBll.getInstance().getMyUserInfoEntity().getPsimPwd();
    }

    public String getPsAppId() {
        return UserBll.getInstance().getMyUserInfoEntity().getPsAppId();
    }

    public String getPsAppClientKey() {
        return UserBll.getInstance().getMyUserInfoEntity().getPsAppClientKey();
    }

    public String getAreaCode() {
        return UserBll.getInstance().getMyUserInfoEntity().getAreaCode();
    }

    /**
     * 全身直播名字
     *
     * @return
     */
    public String getStandShowName() {
        String showName = "";
        if (!StringUtils.isEmpty(LiveAppUserInfo.getInstance().getEnglishName())) {
            showName = LiveAppUserInfo.getInstance().getEnglishName();
        } else if (!StringUtils.isEmpty(LiveAppUserInfo.getInstance().getRealName())) {
            showName = LiveAppUserInfo.getInstance().getRealName();
        } else if (!StringUtils.isEmpty(LiveAppUserInfo.getInstance().getNickName())) {
            showName = LiveAppUserInfo.getInstance().getNickName();
        }
        return showName;
    }

    public String getSessionId() {
        return UserBll.getInstance().getMyUserInfoEntity().getSessionId();
    }

    public String getChatName() {
        return UserBll.getInstance().getMyUserInfoEntity().getChatName();
    }

    public String getGradeCode() {
        return UserBll.getInstance().getMyUserInfoEntity().getGradeCode();
    }
}

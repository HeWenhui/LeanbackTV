package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.security.PrivateKey;

public class LiveAppUserInfo {
    private static LiveAppUserInfo mInstance;
    private String mPsAppKey;
    private String mPsAppId;
    private String mPsimId;
    private String mPsimPwd;
    private String mIrcNick;

    public static synchronized LiveAppUserInfo getInstance() {
        if (mInstance == null) {
            synchronized (LiveAppUserInfo.class) {
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

    /**
     * 用户在登陆时使用的帐号（有可能是帐号有可能是手机，在注册时给值，还有登陆时记录值）
     */
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
        return  !StringUtils.isEmpty(mPsimId)?mPsimId:UserBll.getInstance().getMyUserInfoEntity().getPsimId();
    }

    public String getPsimPwd() {
        return !StringUtils.isEmpty(mPsimPwd)?mPsimPwd:UserBll.getInstance().getMyUserInfoEntity().getPsimPwd();
    }

    public String getPsAppId() {

        return !StringUtils.isEmpty(mPsAppId)? mPsAppId:UserBll.getInstance().getMyUserInfoEntity().getPsAppId();
    }

    public String getPsAppClientKey() {
        return !StringUtils.isEmpty(mPsAppKey)?mPsAppKey:UserBll.getInstance().getMyUserInfoEntity().getPsAppClientKey();
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

    public String getChildName() {
        return AppBll.getInstance().getAppInfoEntity().getChildName();
    }


    /**
     * 获取平台用户token
     *
     * @return
     */
    public String getTalToken() {
        return UserBll.getInstance().getTalToken();
    }

    /**
     * 获取用户当前的Token,间接调用AppBll
     *
     * @return
     */
    @Deprecated
    public String getUserToken() {
//        return AppBll.getInstance().getUserToken();
        return "";
    }

    /**
     * 获取用户当前的Token，间接调用AppBll
     *
     * @return
     */
    @Deprecated
    public String getUserRfh() {
//        return AppBll.getInstance().getUserRfh();
        return "";
    }

    public String getUsernameDefault() {
        String username_default = ContextManager.getContext().getResources().getString(R.string.username_default);
        return username_default;
    }

    /**
     * 获取名字
     *
     * @return
     */
    public String getShowName() {
        String showName = "";
       if (!StringUtils.isEmpty(LiveAppUserInfo.getInstance().getRealName())) {
            showName = LiveAppUserInfo.getInstance().getRealName();
        } else if (!StringUtils.isEmpty(LiveAppUserInfo.getInstance().getNickName())) {
            showName = LiveAppUserInfo.getInstance().getNickName();
        } else {
           showName = getUsernameDefault();
       }
        return showName;
    }

    /**
     * 设置磐石key
     *
     * @param psAppKey
     */
    public void setPsAppKey(String psAppKey) {
        mPsAppKey = psAppKey;
    }

    /**
     * 设置psAppid
     * @param psAppid
     */
    public void setPsAppId(String psAppid) {
        mPsAppId = psAppid;
    }


    /**
     * 设置psimId
     * @param psimId
     */
    public void setPsimId(String psimId){
        mPsimId = psimId;
    }

    /**
     * 设置psimPwd
     * @param psimPwd
     */
    public void setPsimPwd(String psimPwd){
        mPsimPwd = psimPwd;

    }

    /**
     * 退出直播间时 清理缓存信息
     */
    public void clearCachData(){
        mPsAppKey = null;
        mPsAppId = null;
        mPsimId = null;
        mPsimPwd = null;
        mIrcNick = null;
    }


    public void setIrcNick(String ircNick){
        mIrcNick = ircNick;
    }

    public String getIrcNick(){
        return mIrcNick;
    }

}

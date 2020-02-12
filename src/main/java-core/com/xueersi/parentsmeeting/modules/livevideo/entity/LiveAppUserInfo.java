package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.text.TextUtils;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.LoginRegistersConfig;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

public class LiveAppUserInfo {
    private static LiveAppUserInfo mInstance;
    private String mPsAppKey;
    private String mPsAppId;
    private String mPsimId;
    private String mPsimPwd;
    // 小组课专用英文名
    private String mGroupClassName;
    private String mIrcNick;
    ShareDataManager mShareDataManager;
    private LiveAppUserInfo(){
        init();
    }
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
    public void init(){
        BaseApplication  mBaseApplication = (BaseApplication) BaseApplication.getContext().getApplicationContext();
        mShareDataManager = mBaseApplication.getShareDataManager();
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

    public String getName() {
        String showName = "";
        if (!StringUtils.isEmpty(LiveAppUserInfo.getInstance().getRealName())) {
            showName = LiveAppUserInfo.getInstance().getRealName();
        } else if (!StringUtils.isEmpty(LiveAppUserInfo.getInstance().getNickName())) {
            showName = LiveAppUserInfo.getInstance().getNickName();
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

    public void setGroupClassName(String groupClassName){
        mGroupClassName = groupClassName;
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

    /**
     * 2020/2/12 修改，当前字段只针对小组课使用。并不通用。
     */
    public String getEnglishNameProcess() {
        String englishName = mShareDataManager.getString(LoginRegistersConfig.SP_USER_ENGLISH_NAME, "",
                ShareDataManager.SHAREDATA_USER);
        return !TextUtils.isEmpty(englishName)? englishName:mGroupClassName;
    }
    public int getSexProcess() {
        return mShareDataManager.getInt(LoginRegistersConfig.SP_USER_SEX, 3,
                ShareDataManager.SHAREDATA_USER);
    }
    public String getHeadImageProcess(){
       return mShareDataManager.getString(LoginRegistersConfig.SP_USER_MOBILE_PHONE, "",
               ShareDataManager
                       .SHAREDATA_USER);
    }

    public String getEnglishNameAudio() {
        return mShareDataManager.getString(LoginRegistersConfig.SP_USER_ENGLISH_NAME_AUDIO_PATH, "",
                ShareDataManager
                        .SHAREDATA_USER);
    }

    public void setEnglishNameAudio(String englishNameAudio) {
        UserBll.getInstance().saveUserNameAudio(englishNameAudio);
    }

    public boolean isSupportedEnglishName(){
        return mShareDataManager.getBoolean(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_CHECK, false,
                ShareDataManager.SHAREDATA_USER);
    }
}

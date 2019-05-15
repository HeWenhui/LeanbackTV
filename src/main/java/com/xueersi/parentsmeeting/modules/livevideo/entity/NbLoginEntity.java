package com.xueersi.parentsmeeting.modules.livevideo.entity;
/**
*乐步 登录信息 Model
*@author chekun
*created  at 2019/4/3 17:02
*/
public class NbLoginEntity {
    /**
     * 乐步返回token 信息
     */
    String mToken;
    /**
     * 乐步返回appId
     */
    String mAppId;
    /**
     * 乐步返回用户信息
     */
    NbUserInfo mUserInfo;

    /**
     * NB 用户信息Model
     */
    public static class NbUserInfo{
        /**乐步放用户id**/
        private String userId;
        /**乐步放用户姓名**/
        private String userName;
        /**乐步放用户昵称**/
        private String nickName;

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getNickName() {
            return nickName;
        }
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public void setAppId(String appId) {
        this.mAppId = appId;
    }

    public String getAppId() {
        return mAppId;
    }

    public void setUserInfo(NbUserInfo userInfo) {
        this.mUserInfo = userInfo;
    }

    public NbUserInfo getmUserInfo() {
        return mUserInfo;
    }
}

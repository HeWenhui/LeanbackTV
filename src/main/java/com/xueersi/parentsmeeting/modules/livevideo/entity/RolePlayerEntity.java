package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * RolePlayer角色扮演数据
 * Created by zouhao on 2018/3/30.
 */
public class RolePlayerEntity {

    /** RolePlayer倒计时秒数 */
    private long countDownSecond;
    /** 所有的角色信息 */
    private List<RolePlayerHead> lstRoleInfo = new ArrayList<>();
    /** 所有的对话信息 */
    private List<RolePlayerMessage> lstRolePlayerMessage = new ArrayList<>();

    public long getCountDownSecond() {
        return countDownSecond;
    }

    public void setCountDownSecond(long countDownSecond) {
        this.countDownSecond = countDownSecond;
    }

    public List<RolePlayerHead> getLstRoleInfo() {
        return lstRoleInfo;
    }


    public List<RolePlayerMessage> getLstRolePlayerMessage() {
        return lstRolePlayerMessage;
    }


    /**
     * 角色信息
     */
    public static class RolePlayerHead {

        /** 用户昵称 */
        private String nickName;
        /** 头像地址 */
        private String headImg;
        /** 角色名称 */
        private String roleName;
        /** 是否是自己扮演的角色 */
        private boolean isSelfRole;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public boolean isSelfRole() {
            return isSelfRole;
        }

        public void setSelfRole(boolean selfRole) {
            isSelfRole = selfRole;
        }
    }

    /**
     * 对话信息
     */
    public static class RolePlayerMessage {

        /** 角色信息 */
        private RolePlayerHead rolePlayer;
        /** 朗读的短语 */
        private String readMsg;
        /** 用来朗读倒计时的最长时长 */
        private int maxReadTime;
        /** 剩下的秒数 */
        private int endReadTime;
        /** 是否已赞 */
        private boolean isFavour;
        /** 测评分数 */
        private int speechScore;
        /** 当前对话所处的状态 RolePlayerMessageStatus */
        private int msgStatus;
        /** 网络播放地址（用于回放） */
        private String webVoiceUrl;

        public RolePlayerMessage(RolePlayerHead head, String msg, int maxTime) {
            this.rolePlayer = head;
            this.readMsg = msg;
            this.maxReadTime = maxTime;
            this.endReadTime=maxTime;
            this.msgStatus = RolePlayerMessageStatus.WAIT_NORMAL;
        }


        public RolePlayerHead getRolePlayer() {
            return rolePlayer;
        }

        public void setRolePlayer(RolePlayerHead rolePlayer) {
            this.rolePlayer = rolePlayer;
        }

        public String getReadMsg() {
            return readMsg;
        }

        public void setReadMsg(String readMsg) {
            this.readMsg = readMsg;
        }

        public int getMaxReadTime() {
            return maxReadTime;
        }

        public void setMaxReadTime(int maxReadTime) {
            this.maxReadTime = maxReadTime;
            this.endReadTime = maxReadTime;
        }

        public boolean isFavour() {
            return isFavour;
        }

        public void setFavour(boolean favour) {
            isFavour = favour;
        }

        public int getSpeechScore() {
            return speechScore;
        }

        public void setSpeechScore(int speechScore) {
            this.speechScore = speechScore;
        }

        public int getMsgStatus() {
            return msgStatus;
        }

        public void setMsgStatus(int msgStatus) {
            this.msgStatus = msgStatus;
        }

        public String getWebVoiceUrl() {
            return webVoiceUrl;
        }

        public void setWebVoiceUrl(String webVoiceUrl) {
            this.webVoiceUrl = webVoiceUrl;
        }

        public int getEndReadTime() {
            return endReadTime;
        }

        public void setEndReadTime(int endReadTime) {
            this.endReadTime = endReadTime;
        }
    }

    /**
     * 对话信息的状态，根据不同状态，信息条呈现不同的
     */
    public static class RolePlayerMessageStatus {

        /** 等待朗读中 */
        public static final int WAIT_NORMAL = 1;
        /** 进入角色朗读状态 */
        public static final int BEGIN_ROLEPLAY = 2;
        /** 朗读完成 */
        public static final int END_ROLEPLAY = 3;
    }

}

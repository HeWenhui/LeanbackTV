package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.tal.speech.speechrecognizer.PhoneScore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RolePlayer角色扮演数据
 * Created by zouhao on 2018/3/30.
 */
public class RolePlayerEntity {

    /**
     * 自己的角色ID
     */
    private int selfRoleId;
    /**
     * 试题ID
     */
    private String testId;
    /**
     * 小组编号
     */
    private int teamId;
    /**
     * 直播ID
     */
    private int liveId;

    /**
     * RolePlayer倒计时秒数
     */
    private long countDownSecond;
    /**
     * 收到的点赞数
     */
    private int pullDZCount;
    /**
     * 金币数
     */
    private int goldCount;
    private int energy;

    /**
     * 自己最后一段话的index
     */
    private int selfLastIndex;

    /**
     * 已提交取得结果
     */
    private boolean isResult;

    /**
     * 是否是文科新课件平台
     **/
    private boolean isNewArts;

    /**
     * 所有的角色信息
     */
    private List<RolePlayerHead> lstRoleInfo = new ArrayList<>();

    private Map<String, RolePlayerHead> mapRoleHeadInfo = new HashMap<>();

    /**
     * 所有的对话信息
     */
    private List<RolePlayerMessage> lstRolePlayerMessage = new ArrayList<>();
    private int resultStar;
    private double selfSpeechTime;
    private JSONObject jsonObject;

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

    public int getSelfRoleId() {
        return selfRoleId;
    }

    public void setSelfRoleId(int selfRoleId) {
        this.selfRoleId = selfRoleId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public Map<String, RolePlayerHead> getMapRoleHeadInfo() {
        return mapRoleHeadInfo;
    }

    public int getPullDZCount() {
        return pullDZCount;
    }

    public void setPullDZCount(int pullDZCount) {
        this.pullDZCount = pullDZCount;
    }

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public int getGoldCount() {
        return goldCount;
    }

    public void setGoldCount(int goldCount) {
        this.goldCount = goldCount;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getSelfLastIndex() {
        return selfLastIndex;
    }

    public void setSelfLastIndex(int selfLastIndex) {
        this.selfLastIndex = selfLastIndex;
    }

    public boolean isResult() {
        return isResult;
    }

    public void setResult(boolean result) {
        isResult = result;
    }

    public boolean isNewArts() {
        return isNewArts;
    }

    public void setNewArts(boolean newArts) {
        isNewArts = newArts;
    }

    /**
     * 返回自己的角色
     *
     * @return
     */
    public RolePlayerHead getSelfRoleHead() {
        for (RolePlayerHead head : lstRoleInfo) {
            if (head.isSelfRole()) {
                return head;
            }
        }
        return null;
    }

    /**
     * 返回结果排名
     *
     * @return
     */
    public List<RolePlayerHead> getResultRoleList() {

        List<RolePlayerHead> lstPM = new ArrayList<>();
        lstPM.clear();
        lstPM.addAll(lstRoleInfo);
       /* RolePlayerHead selfHead = null;
        for (RolePlayerHead head : lstRoleInfo) {
            int i = 0;
            boolean isResult = false;
            if (head.isSelfRole()) {
                selfHead = head;
            }
            for (i = 0; i < lstPM.size(); i++) {
                if (head.getSpeechScore() > lstPM.get(i).getSpeechScore()) {
                    isResult = true;
                }
                break;
            }
            if (isResult) {
                lstPM.add(i, head);
            } else {
                lstPM.add(head);
            }
        }*/
        Collections.sort(lstPM, new StuComparator());//按分数排序

        for (int i = 0; i < lstPM.size(); i++) {
            RolePlayerHead head = lstPM.get(i);
            if (head.isSelfRole() && i>2) {
                //前3名中没有自己
                lstPM.add(2, head);
                break;
            }
        }


        return lstPM;

    }

    /**
     * 设置自己每一句的朗读有效时间
     * @param selfSpeechTime
     */
    public void setSelfValidSpeechTime(double selfSpeechTime) {
        this.selfSpeechTime += selfSpeechTime;
    }

    public double getSelfValidSpeechTime() {
        return selfSpeechTime;
    }

    public void setJson(JSONObject json) {
        jsonObject = json;
    }

    public JSONObject getJson() {
        return jsonObject;
    }


    /**
     * 角色信息
     */
    public static class RolePlayerHead {

        /**
         * 用户昵称
         */
        private String nickName;
        /**
         * 头像地址
         */
        private String headImg;
        /**
         * 角色名称
         */
        private String roleName;
        /**
         * 是否是自己扮演的角色
         */
        private boolean isSelfRole;
        /**
         * 角色ID
         */
        private int roleId;
        /**
         * 平均分
         */
        private int speechScore;
        /**
         * 流畅性
         */
        private int fluency;
        /**
         * 准确性
         */
        private int accuracy;
        /**
         * 结果页显示的星星个数
         */
        private int resultStar;


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

        public int getRoleId() {
            return roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }


        public int getSpeechScore() {
            return speechScore;
        }

        public void setSpeechScore(int speechScore) {
            if (speechScore <= 1) {
                return;
            }

            this.speechScore = ((speechScore + this.speechScore) / (this.speechScore == 0 ? 1 : 2));

        }

        public int getFluency() {
            return fluency;
        }

        public void setFluency(int fluency) {
            if (fluency <= 1) {
                return;
            }

            this.fluency = ((fluency + this.fluency) / (this.fluency == 0 ? 1 : 2));

        }

        public int getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(int accuracy) {
            if (accuracy <= 1) {
                return;
            }

            this.accuracy = ((accuracy + this.accuracy) / (this.accuracy == 0 ? 1 : 2));

        }

        public void setResultStar(int resultStar) {
            this.resultStar  = resultStar;
        }

        public int getResultStar() {
            return resultStar;
        }


    }
    public class StuComparator implements Comparator<RolePlayerHead>{

        @Override
        public int compare(RolePlayerHead o1, RolePlayerHead o2) {
            if(o1.speechScore<o2.speechScore)
                return 1;
            else if(o1.speechScore>o2.speechScore)
                return -1;
            return 0;
        }
    }
    /**
     * 对话信息
     */
    public static class RolePlayerMessage {

        /**
         * 对话的音频地址，主要是人机使用到
         */
        private String audio;
        /**
         * 角色信息
         */
        private RolePlayerHead rolePlayer;
        /**
         * 朗读的短语
         */
        private String readMsg;
        /**
         * 用来朗读倒计时的最长时长
         */
        private int maxReadTime;

        /**
         * 自己朗读的具体时长
         */
        private double selfValidSpeechTime;

        /**
         * 剩下的秒数
         */
        private int endReadTime;
        /**
         * 是否已赞
         */
        private boolean isFavour;
        /**
         * 测评分数
         */
        private int speechScore;
        /**
         * 流畅度
         */
        private int fluency;
        /**
         * 准确度
         */
        private int accuracy;
        /**
         * 当前对话所处的状态 RolePlayerMessageStatus
         */
        private int msgStatus;
        /**
         * 网络播放地址（用于回放）
         */
        private String webVoiceUrl;
        /**
         * 是否被点赞
         */
        private boolean isDZ;
        /**
         * 每个音素的分数
         */
        private List<PhoneScore> lstPhoneScore = new ArrayList<>();
        /**
         * 下标
         */
        private int position;
        /**
         * 级别
         */
        private int level;
        private String testId;
        private boolean mUnClick = true;


        /**
         *
         * @param head
         * @param msg
         * @param maxTime
         * @param audio 对应的音频地址
         */
        public RolePlayerMessage(RolePlayerHead head, String msg, int maxTime,String audio) {
            this.rolePlayer = head;
            this.readMsg = msg;
            this.maxReadTime = maxTime;
            this.endReadTime = maxTime;
            this.msgStatus = RolePlayerMessageStatus.WAIT_NORMAL;
            this.audio = audio;
        }

        public RolePlayerMessage(RolePlayerHead head, String msg, int maxTime) {
            this.rolePlayer = head;
            this.readMsg = msg;
            this.maxReadTime = maxTime;
            this.endReadTime = maxTime;
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
            this.rolePlayer.setSpeechScore(speechScore);
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

        public boolean isDZ() {
            return isDZ;
        }

        public void setDZ(boolean DZ) {
            isDZ = DZ;
        }

        public List<PhoneScore> getLstPhoneScore() {
            return lstPhoneScore;
        }

        public void setLstPhoneScore(List<PhoneScore> lstPhoneScore) {
            this.lstPhoneScore = lstPhoneScore;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getFluency() {
            return fluency;
        }

        public void setFluency(int fluency) {
            this.fluency = fluency;
            this.rolePlayer.setFluency(fluency);
        }

        public int getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(int accuracy) {
            this.accuracy = accuracy;
            this.rolePlayer.setAccuracy(accuracy);
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * 返回星星数
         *
         * @return
         */
        public int getStars() {
            if (speechScore >= 0 && speechScore < 40) {
                return 1;
            }
            if (speechScore >= 40 && speechScore < 60) {
                return 2;
            }
            if (speechScore >= 60 && speechScore < 75) {
                return 3;
            }
            if (speechScore >= 75 && speechScore < 90) {
                return 4;
            }
            if (speechScore >= 90) {
                return 5;
            }
            return 0;
        }

        /**
         * 对话信息也存入试题id
         * @param testId
         */
        public void setTestId(String testId) {
            this.testId = testId;
        }

        public String getTestId() {
            return testId;
        }

        public void setUnClick(boolean unClick) {
            mUnClick = unClick;
        }

        public boolean getUnClick() {
            return mUnClick;
        }


        public String getAudio(){
            return audio;
        }

        public void setSelfValidSpeechTime(double selfValidSpeechTime) {
            this.selfValidSpeechTime = selfValidSpeechTime;
        }

        public double getSelfValidSpeechTime(){
            return this.selfValidSpeechTime;
        }
    }

    /**
     * 对话信息的状态，根据不同状态，信息条呈现不同的
     */
    public static class RolePlayerMessageStatus {

        /**
         * 等待朗读中
         */
        public static final int WAIT_NORMAL = 1;
        /**
         * 进入角色朗读状态
         */
        public static final int BEGIN_ROLEPLAY = 2;
        /**
         * 朗读完成
         */
        public static final int END_ROLEPLAY = 3;
        /**
         * 测评结束
         */
        public static final int END_SPEECH = 4;
        /**
         * 空
         */
        public static final int EMPTY = 5;
        /**
         * 结果弹窗,去掉点赞按钮
         */
        public static final int CANCEL_DZ = 6;

        /**
         * 对话结束后，停止刷新界面
         */
        public static final int STOP_UPDATE = -1;

        /**
         * 通知当前正在播放音频的索引，方便，退出界面的时候，关闭音频
         */
        public static final int CUR_PLAYING_ITEM_INDEX = 500;
    }

    /**
     * 通过预埋的Index号来找对应的消息
     *
     * @return
     */
    public RolePlayerMessage getMessageByIndex(int index) {
        for (RolePlayerMessage message : lstRolePlayerMessage) {
            if (message.getPosition() == index) {
                return message;
            }
        }
        return null;
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linyuqiang 视频初始化
 */
public class LiveGetInfo {

    /**
     * testInfo : [] stuName : xiaoqi stuId : 5002290 uname :
     * 137161435128336@talwx.com stuSex : 0 stuImg :
     * http://s01.xesimg.com:81/sys/100019/small.jpg studentLiveInfo :
     * {"groupId":"57","classId":"50","shutupStatus":"1"} teacherId : 947
     * teacherName : 玛莎莎 stat : 1 talkHost : 124.243.202.5 talkPort : 16692
     * newTalkConf :
     * [{"host":"124.243.202.5","port":"16692","pwd":"xueersi.com"}
     * ,{"host":"124.243.202.5"
     * ,"port":"16692","pwd":"xueersi.com"},{"host":"124.243.202.5"
     * ,"port":"16692","pwd":"xueersi.com"}] gslbServerUrl :
     * http://114.113.220.43/xueersi_gslb/live logServerUrl :
     * http://114.113.220.43/xueersi/live/log liveTopic : [] followType :
     * {"2":5,"3":50,"4":100} headImgUrl :
     * ["http://head01.xesimg.com/","http://head02.xesimg.com/"
     * ,"http://head03.xesimg.com/","http://head04.xesimg.com/"] rtmpUrl :
     * rtmp://livestudy.xescdn.com/live_server hbTime : 300 clientLog :
     * http://netlog.xesv5.com:10011/10011.gif id : 249 name : 测1 instructions :
     * 测1 notice : 测1 liveType : 3 liveTime : 21:00 23:00 nowTime : 1452164919
     */

    /** 用户名称 */
    private String stuName;
    /** 用户Id */
    private String stuId;
    /** 用户拼音，现阶段取用户名 */
    private String uname;
    /** 用户性别 */
    private String stuSex;
    /** 用户头像 */
    private String stuImg;
    /** groupId : 57 classId : 50 shutupStatus : 1直播信息 */
    private StudentLiveInfoEntity studentLiveInfo;
    /** 主讲教师id */
    private String mainTeacherId;
    /** 主讲教师 */
    private final MainTeacherInfo mainTeacherInfo = new MainTeacherInfo();
    /** 辅导教师id */
    private String teacherId;
    /** 辅导教师名称 */
    private String teacherName;
    /** 辅导教师头像 */
    private String teacherIMG;
    /** 直播状态，1：无老师，2：有老师，3：已上课 */
    private int stat;
    /** 聊天服务器地址 */
    private String talkHost;
    /** 聊天服务器密码 */
    private String talkPwd;
    /** 聊天服务器端口 */
    private String talkPort;
    private String roomId;
    /** 直播调度URL */
    private String gslbServerUrl;
    /** 直播日志收集URL */
    private String logServerUrl;
    /**
     * 2 : 5 3 : 50 4 : 100 直播送花类型与金币对应关系
     */
    private FollowTypeEntity followType;
    /** RTMP服务器地址 */
    private String rtmpUrl;
    /** RTMP服务器地址 */
    private String[] rtmpUrls;
    /** 用户心跳时间间隔 */
    private int hbTime;
    /** 记录客户端日志地址 */
    private String clientLog;
    /** 直播id */
    private String id;
    /** 直播名称 */
    private String name;
    /** 直播说明 */
    private String instructions;
    /** 直播公告 */
    private String notice;
    /** 直播类型 3是直播课程 */
    private int liveType;
    /** 直播开始时间 */
    private String liveTime;
    /** 直播开始时间 */
    private long sTime;
    /** 直播开始时间 */
    private long eTime;
    /** 当前时间 */
    private double nowTime;
    private List<TestInfoEntity> testInfo = new ArrayList<TestInfoEntity>();
    /**
     * host : 124.243.202.5 port : 16692 pwd : xueersi.com 备用用户聊天服务配置
     */
    private List<NewTalkConfEntity> newTalkConf;
    /** 用户头像服务器地址 */
    private List<String> headImgUrl;
    private String headImgPath;
    /** 用户头像类型middle.jpg */
    private String imgSizeType;
    private String headImgVersion;
    private String channelname;
    private String studentChannelname;
    /** 关闭聊天 */
    private boolean isCloseChat = false;
    /** 数据缓存 */
    private final LiveTopic liveTopic;
    /** 主讲老师加密 */
    private String skeyPlayT;
    /** 辅导老师加密 */
    private String skeyPlayF;
    /** 语音评测地址 */
    private String speechEvalUrl;
    /** 聊天中老师连接是否可以点击 */
    private int urlClick;
    private boolean allowLinkMic;
    private int stuLinkMicNum;
    private ArrayList<String> teamStuIds = new ArrayList<>();
    private int isArts;
    private int isEnglish;
    private int isAllowStar;
    private int starCount;
    private int goldCount;
    private String testPaperUrl;
    private boolean blockChinese;
    private String subjectiveTestAnswerResult;
    /** 当前的直播模式 */
    private String mode = LiveTopic.MODE_TRANING;
    private TotalOpeningLength totalOpeningLength;
    /** 是否显示满分榜 */
    private String is_show_ranks;
    /** 是否显示智能私信 */
    private String isShowCounselorWhisper;
    /** 是否有标记点功能 */
    private String isShowMarkPoint;

    public String getIsShowMarkPoint() {
        return isShowMarkPoint;
    }

    public void setIsShowMarkPoint(String isShowMarkPoint) {
        this.isShowMarkPoint = isShowMarkPoint;
    }

    public String getIsShowCounselorWhisper() {
        return isShowCounselorWhisper;
    }

    public void setIsShowCounselorWhisper(String isShowCounselorWhisper) {
        this.isShowCounselorWhisper = isShowCounselorWhisper;
    }

    public String getIs_show_ranks() {
        return is_show_ranks;
    }

    public void setIs_show_ranks(String is_show_ranks) {
        this.is_show_ranks = is_show_ranks;
    }

    public LiveGetInfo(LiveTopic liveTopic) {
        this.liveTopic = liveTopic;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setStuSex(String stuSex) {
        this.stuSex = stuSex;
    }

    public void setStuImg(String stuImg) {
        this.stuImg = stuImg;
    }

    public void setStudentLiveInfo(StudentLiveInfoEntity studentLiveInfo) {
        this.studentLiveInfo = studentLiveInfo;
    }

    public String getMainTeacherId() {
        return mainTeacherId;
    }

    public void setMainTeacherId(String mainTeacherId) {
        this.mainTeacherId = mainTeacherId;
    }

    public MainTeacherInfo getMainTeacherInfo() {
        return mainTeacherInfo;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherIMG(String teacherIMG) {
        this.teacherIMG = teacherIMG;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public void setTalkHost(String talkHost) {
        this.talkHost = talkHost;
    }

    public void setTalkPort(String talkPort) {
        this.talkPort = talkPort;
    }

    public String getTalkPwd() {
        return talkPwd;
    }

    public void setTalkPwd(String talkPwd) {
        this.talkPwd = talkPwd;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setGslbServerUrl(String gslbServerUrl) {
        this.gslbServerUrl = gslbServerUrl;
    }

    public void setLogServerUrl(String logServerUrl) {
        this.logServerUrl = logServerUrl;
    }

    public void setFollowType(FollowTypeEntity followType) {
        this.followType = followType;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public void setRtmpUrls(String[] rtmpUrls) {
        this.rtmpUrls = rtmpUrls;
    }

    public void setHbTime(int hbTime) {
        this.hbTime = hbTime;
    }

    public void setClientLog(String clientLog) {
        this.clientLog = clientLog;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }

    public void setNowTime(double nowTime) {
        this.nowTime = nowTime;
    }

    public void setTestInfo(List<TestInfoEntity> testInfo) {
        this.testInfo = testInfo;
    }

    public void setNewTalkConf(List<NewTalkConfEntity> newTalkConf) {
        this.newTalkConf = newTalkConf;
    }

    public void setHeadImgUrl(List<String> headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getHeadImgPath() {
        return headImgPath;
    }

    public void setHeadImgPath(String headImgPath) {
        this.headImgPath = headImgPath;
    }

    public String getImgSizeType() {
        return imgSizeType;
    }

    public void setImgSizeType(String imgSizeType) {
        this.imgSizeType = imgSizeType;
    }

    public String getHeadImgVersion() {
        return headImgVersion;
    }

    public void setHeadImgVersion(String headImgVersion) {
        this.headImgVersion = headImgVersion;
    }

    public String getStuName() {
        return stuName;
    }

    public String getStuId() {
        return stuId;
    }

    public String getUname() {
        return uname;
    }

    public String getStuSex() {
        return stuSex;
    }

    public String getStuImg() {
        return stuImg;
    }

    public StudentLiveInfoEntity getStudentLiveInfo() {
        return studentLiveInfo;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherIMG() {
        return teacherIMG;
    }

    public int getStat() {
        return stat;
    }

    public String getTalkHost() {
        return talkHost;
    }

    public String getTalkPort() {
        return talkPort;
    }

    public String getGslbServerUrl() {
        return gslbServerUrl;
    }

    public String getLogServerUrl() {
        return logServerUrl;
    }

    public FollowTypeEntity getFollowType() {
        return followType;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public String[] getRtmpUrls() {
        return rtmpUrls;
    }

    public int getHbTime() {
        return hbTime;
    }

    public String getClientLog() {
        return clientLog;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getNotice() {
        return notice;
    }

    public int getLiveType() {
        return liveType;
    }

    public String getLiveTime() {
        return liveTime;
    }

    public long getsTime() {
        return sTime;
    }

    public void setsTime(long sTime) {
        this.sTime = sTime;
    }

    public long geteTime() {
        return eTime;
    }

    public void seteTime(long eTime) {
        this.eTime = eTime;
    }

    public double getNowTime() {
        return nowTime;
    }

    public List<TestInfoEntity> getTestInfo() {
        return testInfo;
    }

    public List<NewTalkConfEntity> getNewTalkConf() {
        return newTalkConf;
    }

    public LiveTopic getLiveTopic() {
        return liveTopic;
    }

    public List<String> getHeadImgUrl() {
        return headImgUrl;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public String getStudentChannelname() {
        return studentChannelname;
    }

    public void setStudentChannelname(String studentChannelname) {
        this.studentChannelname = studentChannelname;
    }

    public boolean isCloseChat() {
        return isCloseChat;
    }

    public void setCloseChat(boolean closeChat) {
        isCloseChat = closeChat;
    }

    public String getSkeyPlayT() {
        return skeyPlayT;
    }

    public void setSkeyPlayT(String skeyPlayT) {
        this.skeyPlayT = skeyPlayT;
    }

    public String getSkeyPlayF() {
        return skeyPlayF;
    }

    public void setSkeyPlayF(String skeyPlayF) {
        this.skeyPlayF = skeyPlayF;
    }

    public String getSpeechEvalUrl() {
        return speechEvalUrl;
    }

    public void setSpeechEvalUrl(String speechEvalUrl) {
        this.speechEvalUrl = speechEvalUrl;
    }

    public int getUrlClick() {
        return urlClick;
    }

    public void setUrlClick(int urlClick) {
        this.urlClick = urlClick;
    }

    public boolean isAllowLinkMic() {
        return allowLinkMic;
    }

    public void setAllowLinkMic(boolean allowLinkMic) {
//        allowLinkMic = false;
        this.allowLinkMic = allowLinkMic;
    }

    public int getStuLinkMicNum() {
        return stuLinkMicNum;
    }

    public void setStuLinkMicNum(int stuLinkMicNum) {
        this.stuLinkMicNum = stuLinkMicNum;
    }

    public int getIsArts() {
        return isArts;
    }

    public void setIsArts(int isArts) {
        this.isArts = isArts;
    }

    public int getIsEnglish() {
        return isEnglish;
    }

    public void setIsEnglish(int isEnglish) {
        this.isEnglish = isEnglish;
    }

    public int getIsAllowStar() {
        return isAllowStar;
    }

    public void setIsAllowStar(int isAllowStar) {
        this.isAllowStar = isAllowStar;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getGoldCount() {
        return goldCount;
    }

    public void setGoldCount(int goldCount) {
        this.goldCount = goldCount;
    }

    public String getTestPaperUrl() {
        return testPaperUrl;
    }

    public void setTestPaperUrl(String testPaperUrl) {
        this.testPaperUrl = testPaperUrl;
    }

    public boolean getBlockChinese() {
        return blockChinese;
    }

    public void setBlockChinese(boolean blockChinese) {
        this.blockChinese = blockChinese;
    }

    public String getSubjectiveTestAnswerResult() {
        return subjectiveTestAnswerResult;
    }

    public void setSubjectiveTestAnswerResult(String subjectiveTestAnswerResult) {
        this.subjectiveTestAnswerResult = subjectiveTestAnswerResult;
    }

    public TotalOpeningLength getTotalOpeningLength() {
        return totalOpeningLength;
    }

    public void setTotalOpeningLength(TotalOpeningLength totalOpeningLength) {
        this.totalOpeningLength = totalOpeningLength;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ArrayList<String> getTeamStuIds() {
        return teamStuIds;
    }

    public static class MainTeacherInfo {
        String teacherId;//"teacherId":"1434",
        String teacherName; //"teacherName":"小琪老师",
        String teacherImg;//  "teacherImg":"http:\/\/r03.xesimg.com\/teacher\/2017\/05\/26\/14957949678348.png",
        String subject_digits;//   "subject_digits":"0"

        public String getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(String teacherId) {
            this.teacherId = teacherId;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getTeacherImg() {
            return teacherImg;
        }

        public void setTeacherImg(String teacherImg) {
            this.teacherImg = teacherImg;
        }

        public String getSubject_digits() {
            return subject_digits;
        }

        public void setSubject_digits(String subject_digits) {
            this.subject_digits = subject_digits;
        }
    }

    /**
     * @author linyuqiang 直播信息
     */
    public static class StudentLiveInfoEntity {
        private String courseId;
        /** 组id */
        private String groupId;
        /** 课id，加入聊天服务的房间 */
        private String classId;
        private String shutupStatus;
        /** 弹出学习报告 1弹，0不弹 */
        private int evaluateStatus;
        /** 弹出点名 是否签到，0未开始，1老师开始签到，2未结束且已签到,3签到失败 */
        private int signStatus;
        /** 班级分组，组id */
        private String teamId;
        /** 购课url */
        private String buyCourseUrl;
        /** 试听总时间 */
        private long userModeTotalTime;
        /** 试听还有多长时间 */
        private long userModeTime;
        /** 是不是试听 */
        private boolean isExpe = false;

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public void setShutupStatus(String shutupStatus) {
            this.shutupStatus = shutupStatus;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getClassId() {
            return classId;
        }

        public void setExpe(boolean expe) {
            isExpe = expe;
        }

        public boolean isExpe() {
            return isExpe;
        }

        public String getShutupStatus() {
            return shutupStatus;
        }

        public int getEvaluateStatus() {
            return evaluateStatus;
        }

        public void setEvaluateStatus(int evaluateStatus) {
            this.evaluateStatus = evaluateStatus;
        }

        public int getSignStatus() {
            return signStatus;
        }

        public void setSignStatus(int signStatus) {
            this.signStatus = signStatus;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getBuyCourseUrl() {
            return buyCourseUrl;
        }

        public void setBuyCourseUrl(String buyCourseUrl) {
            this.buyCourseUrl = buyCourseUrl;
        }

        public long getUserModeTotalTime() {
            return userModeTotalTime;
        }

        public void setUserModeTotalTime(long userModeTotalTime) {
            this.userModeTotalTime = userModeTotalTime;
        }

        public long getUserModeTime() {
            return userModeTime;
        }

        public void setUserModeTime(long userModeTime) {
            this.userModeTime = userModeTime;
        }
    }

    /**
     * @author linyuqiang 直播送花类型与金币对应关系
     */
    public static class FollowTypeEntity {
        private int Int2;
        private int Int3;
        private int Int4;

        public void setInt2(int Int2) {
            this.Int2 = Int2;
        }

        public void setInt3(int Int3) {
            this.Int3 = Int3;
        }

        public void setInt4(int Int4) {
            this.Int4 = Int4;
        }

        public int getInt2() {
            return Int2;
        }

        public int getInt3() {
            return Int3;
        }

        public int getInt4() {
            return Int4;
        }
    }

    /**
     * @author linyuqiang 备用用户聊天服务配置
     */
    public static class NewTalkConfEntity {
        private String host;
        private String port;
        private String pwd;

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public String getPwd() {
            return pwd;
        }
    }

    /**
     * @author linyuqiang 互动题信息
     */
    public static class TestInfoEntity {
        /** 互动题ID */
        public String id;
        /** 互动题类型，1：单选，2：填空 */
        public String type;
        /** 互动题地址 */
        public String content;
        /** 音频地址 */
        public String audio;
        /** 互动题当题空数，填空题用 */
        public int num;

    }

    public static class TotalOpeningLength {
        public double duration = 0;
        public int speakingNum = 0;
        public String speakingLen = "";
    }
}

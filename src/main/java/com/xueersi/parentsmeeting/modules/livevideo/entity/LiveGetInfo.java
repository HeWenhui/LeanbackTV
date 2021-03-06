package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LiveModuleConfigInfo;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LivePlugin;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.SubGroupEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.SubMemberEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.LPWeChatEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 用户名称
     */
    private String stuName;
    /**
     * 用户Id
     */
    private String stuId;
    /**
     * 用户购课Id
     */
    private String stuCouId;
    /**
     * 用户拼音，现阶段取用户名
     */
    private String uname;
    /**
     * 用户英文名
     */
    private String en_name;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户性别
     */
    private String stuSex;
    /**
     * 用户头像
     */
    private String stuImg;
    /**
     * groupId : 57 classId : 50 shutupStatus : 1直播信息
     */
    private StudentLiveInfoEntity studentLiveInfo;
    /**
     * 主讲教师id
     */
    private String mainTeacherId;
    /**
     * 主讲教师
     */
    private final MainTeacherInfo mainTeacherInfo = new MainTeacherInfo();
    /**
     * 辅导教师id
     */
    private String teacherId;
    /**
     * 辅导教师名称
     */
    private String teacherName;
    /**
     * 辅导教师头像
     */
    private String teacherIMG;
    /**
     * 直播状态，1：无老师，2：有老师，3：已上课
     */
    private int stat;
    private String roomId;
    /**
     * 直播调度URL
     */
    private String gslbServerUrl;
    /**
     * 直播日志收集URL
     */
    private String logServerUrl;
    /**
     * 2 : 5 3 : 50 4 : 100 直播送花类型与金币对应关系
     */
    private FollowTypeEntity followType;
    /**
     * RTMP服务器地址
     */
    private String rtmpUrl;
    /**
     * RTMP服务器地址
     */
    private String[] rtmpUrls;
    /**
     * 用户心跳时间间隔
     */
    private int hbTime;
    /**
     * 记录客户端日志地址 2019-1-14作废
     */
    @Deprecated
    private String clientLog;
    /**
     * 直播id
     * liveId
     */
    private String id;
    /**
     * 直播名称
     */
    private String name;
    /**
     * 直播说明
     */
    private String instructions;
    /**
     * 直播公告
     */
    private String notice;
    /**
     * 直播类型 3是直播课程
     */
    private int liveType;
    /**
     * 直播开始时间
     */
    private String liveTime;
    /**
     * 直播开始时间
     */
    private long sTime;
    /**
     * 直播开始时间
     */
    private long eTime;
    /**
     * 当前时间
     */
    private long nowTime;
    private List<TestInfoEntity> testInfo = new ArrayList<TestInfoEntity>();
    private ArrayList<TalkConfHost> newTalkConfHosts;
    /**
     * 用户头像服务器地址
     */
    private List<String> headImgUrl;
    private String headImgPath;
    /**
     * 用户头像类型middle.jpg
     */
    private String imgSizeType;
    private String headImgVersion;
    private String channelname;
    private String studentChannelname;
    /**
     * 关闭聊天
     */
    private boolean isCloseChat = false;
    /**
     * 数据缓存
     */
    private final LiveTopic liveTopic;
    /**
     * 主讲老师加密
     */
    private String skeyPlayT;
    /**
     * 辅导老师加密
     */
    private String skeyPlayF;
    /**
     * 语音评测地址
     */
    private String speechEvalUrl;
    /**
     * 聊天中老师连接是否可以点击
     */

    /**
     * 直播入口接口 返回session ID
     **/
    public String sessionId;

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
    }

    /**
     * 大班整合新增 业务id
     **/
    public int bizId;


    private int urlClick;
    private boolean allowLinkMic;
    /**
     * 2018新接麦
     */
    private int allowLinkMicNew;
    private int stuLinkMicNum;
    private int stuPutUpHandsNum;
    private ArrayList<String> teamStuIds = new ArrayList<>();
    private int isArts;
    private int isEnglish;
    private int isAllowStar;
    private int starCount;
    private int goldCount;
    private String testPaperUrl;
    private boolean blockChinese;
    private String subjectiveTestAnswerResult;
    //是否是小英
    private boolean smallEnglish;
    /**
     * 声网appid
     */
    private String appid;
    private boolean primaryChinese;
    /**
     * 当前的直播模式
     */
    private String mode = LiveTopic.MODE_TRANING;
    private TotalOpeningLength totalOpeningLength;
    private BetterMe betterMe = new BetterMe();
    private EnglishPk englishPk = new EnglishPk();
    private EnPkEnergy enpkEnergy = new EnPkEnergy();
    /**
     * 是否显示满分榜
     */
    private String is_show_ranks;
    /**
     * 是否显示智能私信
     */
    private String isShowCounselorWhisper;
    /**
     * 是否有标记点功能
     */
    private String isShowMarkPoint;
    /**
     * 是否有精彩瞬间截图
     */
    private int allowSnapshot;
    /**
     * 1-普通直播，2-全身直播,6半身直播
     */
    private int pattern = 1;
    /**
     * 全身直播语音答题和评测小组排名请求时间
     */
    private String requestTime;

    /**
     * 语文，英语是否使用皮肤
     **/
    private int useSkin;

    /**
     * 是否是 teampk 直播间
     * 1 :是pk 直播间  0 :非pk直播间
     */
    private String isAllowTeamPk;

    /**
     * 是否是AI 伴侣直播间
     */
    private int isAIPartner;
    private String[] subjectIds;//所有学科id
    /**
     * 学科，都是2 的倍数
     */
    private String subject_digits;
    /**
     * 小学理科改版，教育阶段，区分献花
     */
    private String educationStage;
    /**
     * 直播课年级
     */
    private int grade;
    private String gradeIds;

    /**
     * 一发多题的动态接口
     */
    private String mulpreload;
    private String mulh5url;
    private String getCourseWareHtmlNew;
    private String getCourseWareHtmlZhongXueUrl;
    /**
     * 是否是高三理科
     */
    private int isSeniorOfHighSchool;
    /**
     * 是否显示文科表扬榜信息
     */
    private int showArtsPraise;
    /**
     * 是否是小学理科
     */
    private int isPrimarySchool;
    /**
     * 是否是幼教
     */
    private int isYouJiao;
    /**
     * 是否开启集语音互动功能 1,开启，0 不开启
     */
    private int isVoiceInteraction;

    /**
     * 点赞送礼物，礼物特效对应的扣除金币个数
     */
    private ArrayList<Integer> praiseGift = new ArrayList();

    /**
     * 点赞送礼物，礼物特效对应的弹出概率
     */
    private ArrayList<Double> praiseGiftRate = new ArrayList();

    //连续点赞多长时间弹出礼物
    private int praiseAutoCutTime = 5;

    //暂停点赞多长时间弹出礼物
    private int praiseAutoBarrageTime = 1;
    /**
     * 走新课件预加载
     */
    private boolean newCourse = false;
    /**
     * 走新课件预加载,接口返回
     */
    private int isNewProject = 0;

    // add by William on 2018/12/5  专属老师用
    public EPlanInfoBean ePlanInfo;
    /**
     * 语文AI主观题AI接口
     */
    private String subjectiveItem2AIUrl;

    /**
     * 直播key
     */
    private String livePluginKey;
    /**
     * 是否是幼教
     */
    private boolean preschool;


    /**
     * 大班整合灰控接口
     **/
    private String initModuleUrl;

    /**
     * 聊天信息接口
     **/
    private String getChatRecordUrl;
    /**
     * 扫点信息接口
     **/
    private String getMetadataUrl;
    private int isFlatfish=1;

    /**老直播间是否开启消峰功能**/
    private int isEliminationPeak;

    /**消峰配置信息**/
    private HashMap<Integer,Long> eliminationMap;


    /** 轻直播公告*/
    private String gentlyNotice;
    /** 是否是轻直播 0否 1是*/
//    private boolean isGently;

//    private LPWeChatEntity lpWeChatEntity;
    /** 聊天人数增加开关 1开 0关*/
//    private int chatSwitch;

    private String streamTimes;
//
//    public String getGentlyNotice() {
//        return gentlyNotice;
//    }
//
//    public void setGentlyNotice(String gentlyNotice) {
//        this.gentlyNotice = gentlyNotice;
//    }

//    public boolean isGently() {
//        return isGently;
//    }
//
//    public void setIsGently(boolean isGently) {
//        this.isGently = isGently;
//    }
//
//    public LPWeChatEntity getLpWeChatEntity() {
//        return lpWeChatEntity;
//    }
//
//    public void setLpWeChatEntity(LPWeChatEntity lpWeChatEntity) {
//        this.lpWeChatEntity = lpWeChatEntity;
//    }

    public String getStreamTimes() {
        return streamTimes;
    }

    public void setStreamTimes(String streamTimes) {
        this.streamTimes = streamTimes;
    }

//    public int getChatSwitch() {
//        return chatSwitch;
//    }
//
//    public void setChatSwitch(int chatSwitch) {
//        this.chatSwitch = chatSwitch;
//    }


    public int getIsEliminationPeak() {
        return isEliminationPeak;
    }

    public void setIsEliminationPeak(int isEliminationPeak) {
        this.isEliminationPeak = isEliminationPeak;
    }


    public HashMap<Integer, Long> getEliminationMap() {
        return eliminationMap;
    }

    public void setEliminationMap(HashMap<Integer, Long> eliminationMap) {
        this.eliminationMap = eliminationMap;
    }



    public LiveStatus getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(LiveStatus liveStatus) {
        this.liveStatus = liveStatus;
    }

    /**大班整合 直播间状态**/
    private LiveStatus liveStatus = new LiveStatus();

    private EvenDriveInfo evenDriveInfo;

    public static class EvenDriveInfo {
        private int isOpenStimulation;
        private int evenNum;
        private int highestNum;

        public int getIsOpenStimulation() {
            return isOpenStimulation;
        }

        public int getHighestNum() {
            return highestNum;
        }

        public void setHighestNum(int highestNum) {
            this.highestNum = highestNum;
        }

        public void setIsOpenStimulation(int isOpenStimulation) {
            this.isOpenStimulation = isOpenStimulation;
        }

        public int getEvenNum() {
            return evenNum;
        }

        public void setEvenNum(int evenNum) {
            this.evenNum = evenNum;
        }
    }

    public EvenDriveInfo getEvenDriveInfo() {
        return evenDriveInfo;
    }

    public void setEvenDriveInfo(EvenDriveInfo evenDriveEntity) {
        this.evenDriveInfo = evenDriveEntity;
    }

    //    private int isOpenStimulation;

//    public int getIsOpenStimulation() {
//        return isOpenStimulation;
//    }


//    private AObservable aObservable = new AObservable();


//    public static class AObservable extends Observable {
//        private int isOpenStimulation;
//
//        public void setIsOpenStimulation(int isOpenStimulation) {
//            this.isOpenStimulation = isOpenStimulation;
//            setChanged();
//        }
//
//        public int getIsOpenStimulation() {
//            return isOpenStimulation;
//        }
//    }

//    public void addAObserver(Observer ab) {
//        if (aObservable != null && ab != null) {
//            aObservable.addObserver(ab);
//        }
//    }

//    public void setIsOpenStimulation(int isOpenStimulation) {
//        this.isOpenStimulation = isOpenStimulation;
//        if (aObservable != null) {
//            aObservable.setIsOpenStimulation(isOpenStimulation);
//            aObservable.notifyObservers();
//            aObservable.deleteObservers();
//        }
//    }

    public String getSubjectiveItem2AIUrl() {
        return subjectiveItem2AIUrl;
    }

    public void setSubjectiveItem2AIUrl(String subjectiveItem2AIUrl) {
        this.subjectiveItem2AIUrl = subjectiveItem2AIUrl;
    }

    /**
     * 是否支持连对激励 0：关闭 1：打开
     */
    private int isOpenNewCourseWare;
    /**
     * 连对榜接口地址
     */
    private String getEvenPairListUrl;
    /**
     * 点赞接口地址
     */
    private String getThumbsUpUrl;

    private String getJournalUrl;

    private VideoConfigEntity videoConfigEntity;

    private boolean showHightFeedback;

    /**
     * 灰度控制开关控制
     */
    LiveModuleConfigInfo liveModuleConfigInfo;

    /** 分组信息 */
    SubGroupEntity subGroupEntity;

    /** 英语1v2小组课*/
    private RecordStandliveEntity recordStandliveEntity;
    /** getinfo创建时间*/
    private long creatTime;

    /** 1v2提分机制 分数下限 */
    private int minToFakeScore;
    /** 1v2提分机制 分数上限 */
    private int maxToFakeScore;
    /** 1v2提分机制 目标分数 */
    private int fakeScore;
    /** 1v2是否使用云平台调度 */
    private boolean use1v2Sdk;
    /** 视频播放协议*/
    private int protocol;

    //讲座订阅推荐课程字段
    /** 系列讲座id*/
    private int seriesLectureId;
    /** 系列讲座名称*/
    private String seriesLectureName;
    /** 是否订阅该系列讲座*/
    private int isSeriesLectureSub;
    /** 气泡开关*/
    private int bubbleSwitch;
    /** 气泡文字*/
    private String bubbleText;
    /** 弹窗主标题*/
    private String popupMainTitle;
    /** 弹窗副标题*/
    private String popupSubTitle;
    /** 订阅成功标题*/
    private String subSuccessTitle;
    /** irc businessId*/
    private String liveTypeId;

    public VideoConfigEntity getVideoConfigEntity() {
        return videoConfigEntity;
    }

    public void setVideoConfigEntity(VideoConfigEntity videoConfigEntity) {
        this.videoConfigEntity = videoConfigEntity;
    }

    public String getGetJournalUrl() {
        return getJournalUrl;
    }

    public void setGetJournalUrl(String getJournalUrl) {
        this.getJournalUrl = getJournalUrl;
    }

    public int getIsOpenNewCourseWare() {
        return isOpenNewCourseWare;
    }

    public void setIsOpenNewCourseWare(int isOpenNewCourseWare) {
        this.isOpenNewCourseWare = isOpenNewCourseWare;
    }

    public String getGetEvenPairListUrl() {
        return getEvenPairListUrl;
    }

    public void setGetEvenPairListUrl(String getEvenPairListUrl) {
        this.getEvenPairListUrl = getEvenPairListUrl;
    }

    public String getGetThumbsUpUrl() {
        return getThumbsUpUrl;
    }

    public void setGetThumbsUpUrl(String getThumbsUpUrl) {
        this.getThumbsUpUrl = getThumbsUpUrl;
    }

    private int useGoldMicroPhone;

    private int useSuperSpeakerShow;

    public int getUseSuperSpeakerShow() {
        return useSuperSpeakerShow;
    }

    public void setUseSuperSpeakerShow(int useSuperSpeakerShow) {
        this.useSuperSpeakerShow = useSuperSpeakerShow;
    }

    public int isUseGoldMicroPhone() {
        return useGoldMicroPhone;
    }

    public void setUseGoldMicroPhone(int useGoldMicroPhone) {
        this.useGoldMicroPhone = useGoldMicroPhone;
    }

    public int getPraiseAutoCutTime() {
        return praiseAutoCutTime;
    }

    public void setPraiseAutoCutTime(int praiseAutoCutTime) {
        this.praiseAutoCutTime = praiseAutoCutTime;
    }

    public int getPraiseAutoBarrageTime() {
        return praiseAutoBarrageTime;
    }

    public void setPraiseAutoBarrageTime(int praiseAutoBarrageTime) {
        this.praiseAutoBarrageTime = praiseAutoBarrageTime;
    }

    public ArrayList<Double> getPraiseGiftRate() {
        return praiseGiftRate;
    }

    public void setPraiseGiftRate(ArrayList<Double> praiseGiftRate) {
        this.praiseGiftRate = praiseGiftRate;
    }

    public ArrayList<Integer> getPraiseGift() {
        return praiseGift;
    }

    public int getIsVoiceInteraction() {
        return isVoiceInteraction;
    }

    public void setIsVoiceInteraction(int isVoiceInteraction) {
        this.isVoiceInteraction = isVoiceInteraction;
    }

    public int getShowArtsPraise() {
        return showArtsPraise;
    }

    public void setShowArtsPraise(int showArtsPraise) {
        this.showArtsPraise = showArtsPraise;
    }

    public void setSmallEnglish(boolean smallEnglish) {
        this.smallEnglish = smallEnglish;
    }

    public boolean getSmallEnglish() {
        return smallEnglish;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public boolean isPrimaryChinese() {
        return primaryChinese;
    }

    public void setPrimaryChinese(boolean primaryChinese) {
        this.primaryChinese = primaryChinese;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setIsAIPartner(int isAIPartner) {
        this.isAIPartner = isAIPartner;
    }

    public int getIsAIPartner() {
        return isAIPartner;
    }

    public void setIsAllowTeamPk(String isAllowTeamPk) {
        this.isAllowTeamPk = isAllowTeamPk;
    }

    public String getIsAllowTeamPk() {
        return isAllowTeamPk;
    }

    public String getIsShowMarkPoint() {
        return isShowMarkPoint;
    }

    public void setIsShowMarkPoint(String isShowMarkPoint) {
        this.isShowMarkPoint = isShowMarkPoint;
    }

    public int getAllowSnapshot() {
        return allowSnapshot;
    }

    public void setAllowSnapshot(int allowSnapshot) {
        this.allowSnapshot = allowSnapshot;
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

    public void setIsSeniorOfHighSchool(int isSeniorOfHighSchool) {
        this.isSeniorOfHighSchool = isSeniorOfHighSchool;
    }

    public int getIsSeniorOfHighSchool() {
        return isSeniorOfHighSchool;
    }

    public int getIsPrimarySchool() {
        return isPrimarySchool;
    }

    public void setIsPrimarySchool(int isPrimarySchool) {
        this.isPrimarySchool = isPrimarySchool;
    }

    public int getIsYouJiao() {
        return isYouJiao;
    }

    public void setIsYouJiao(int isYouJiao) {
        this.isYouJiao = isYouJiao;
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

    public String getStuCouId() {
        return stuCouId;
    }

    public void setStuCouId(String stuCouId) {
        this.stuCouId = stuCouId;
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

    @Deprecated
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

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public void setTestInfo(List<TestInfoEntity> testInfo) {
        this.testInfo = testInfo;
    }

    public void setNewTalkConfHosts(ArrayList<TalkConfHost> newTalkConfHosts) {
        this.newTalkConfHosts = newTalkConfHosts;
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

    public void setUseSkin(int useSkin) {
        this.useSkin = useSkin;
    }

    public int getUseSkin() {
        return useSkin;
    }

    /**
     * 站立直播名字
     */
    public String getStandLiveName() {
        if (!StringUtils.isEmpty(en_name)) {
            return en_name;
        } else if (!StringUtils.isEmpty(stuName)) {
            return stuName;
        } else if (!StringUtils.isEmpty(nickname)) {
            return nickname;
        }
        return uname;
    }

    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public Long getNowTime() {
        return nowTime;
    }

    public List<TestInfoEntity> getTestInfo() {
        return testInfo;
    }

    public ArrayList<TalkConfHost> getNewTalkConfHosts() {
        return newTalkConfHosts;
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

    public int getAllowLinkMicNew() {
        return allowLinkMicNew;
    }

    public void setAllowLinkMicNew(int allowLinkMicNew) {
        this.allowLinkMicNew = allowLinkMicNew;
    }

    public int getStuLinkMicNum() {
        return stuLinkMicNum;
    }

    public void setStuLinkMicNum(int stuLinkMicNum) {
        this.stuLinkMicNum = stuLinkMicNum;
    }

    public int getStuPutUpHandsNum() {
        return stuPutUpHandsNum;
    }

    public void setStuPutUpHandsNum(int stuPutUpHandsNum) {
        this.stuPutUpHandsNum = stuPutUpHandsNum;
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

    public BetterMe getBetterMe() {
        return betterMe;
    }

    public void setBetterMe(BetterMe betterMe) {
        this.betterMe = betterMe;
    }

    public EnglishPk getEnglishPk() {
        return englishPk;
    }

    public void setEnglishPk(EnglishPk englishPk) {
        this.englishPk = englishPk;
    }

    public EnPkEnergy getEnpkEnergy() {
        return enpkEnergy;
    }

    public void setEnpkEnergy(EnPkEnergy enpkEnergy) {
        this.enpkEnergy = enpkEnergy;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getPattern() {
        return pattern;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public ArrayList<String> getTeamStuIds() {
        return teamStuIds;
    }

    public void setSubjectIds(String[] subjectIds) {
        this.subjectIds = subjectIds;
    }

    public String[] getSubjectIds() {
        return subjectIds;
    }

    public String getSubject_digits() {
        return subject_digits;
    }

    public void setSubject_digits(String subject_digits) {
        this.subject_digits = subject_digits;
    }

    public String getEducationStage() {
        return educationStage;
    }

    public void setEducationStage(String educationStage) {
        this.educationStage = educationStage;
    }

    public boolean isNewCourse() {
        return newCourse;
    }

    public void setNewCourse(boolean newCourse) {
        this.newCourse = newCourse;
    }

    public int getIsNewProject() {
        return isNewProject;
    }

    public void setIsNewProject(int isNewProject) {
        this.isNewProject = isNewProject;
    }

    public void setPreschool(boolean preschool) {
        this.preschool = preschool;
    }

    public boolean isPreschool() {
        return preschool;
    }

    public String getGetChatRecordUrl() {
        return getChatRecordUrl;
    }

    public void setGetChatRecordUrl(String getChatRecordUrl) {
        this.getChatRecordUrl = getChatRecordUrl;
    }

    public String getGetMetadataUrl() {
        return getMetadataUrl;
    }

    public void setGetMetadataUrl(String getMetadataUrl) {
        this.getMetadataUrl = getMetadataUrl;
    }

    public void setIsFlatfish(int isFlatfish) {
        this.isFlatfish = isFlatfish;
    }

    public int getIsFlatfish() {
        return isFlatfish;
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
        /**
         * 组id
         */
        private String groupId;
        /**
         * 课id，加入聊天服务的房间
         */
        private String classId;
        private String shutupStatus;
        /**
         * 弹出学习报告 1弹，0不弹
         */
        private int evaluateStatus;
        /**
         * 弹出点名 是否签到，0未开始，1老师开始签到，2未结束且已签到,3签到失败
         */
        private int signStatus;
        /**
         * 班级分组，组id
         */
        private String teamId;
        /**
         * 购课url
         */
        private String buyCourseUrl;
        /**
         * 试听总时间
         */
        private long userModeTotalTime;
        /**
         * 试听还有多长时间
         */
        private long userModeTime;
        /**
         * 是不是试听
         */
        private boolean isExpe = false;
        /**
         * 童音测评
         */
        private String learning_stage = "";
        private int goldNum;

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

        public String getLearning_stage() {
            return learning_stage;
        }

        public void setLearning_stage(String learning_stage) {
            this.learning_stage = learning_stage;
        }

        public int getGoldNum() {
            return goldNum;
        }

        public void setGoldNum(int goldNum) {
            this.goldNum = goldNum;
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
        /**
         * 互动题ID
         */
        public String id;
        /**
         * 互动题类型，1：单选，2：填空
         */
        public String type;
        /**
         * 互动题地址
         */
        public String content;
        /**
         * 音频地址
         */
        public String audio;
        /**
         * 互动题当题空数，填空题用
         */
        public int num;

    }

    public static class TotalOpeningLength {
        public double duration = 0;
        public int speakingNum = 0;
        public String speakingLen = "";
    }

    EvaluateTeacherEntity evaluateTeacherEntity;

    public static class EvaluateTeacherEntity {
        boolean evaluateIsOpen = false;
        long evaluateTime;

        public boolean isEvaluateIsOpen() {
            return evaluateIsOpen;
        }

        public void setEvaluateIsOpen(boolean evaluateIsOpen) {
            this.evaluateIsOpen = evaluateIsOpen;
        }

        public long getEvaluateTime() {
            return evaluateTime;
        }

        public void setEvaluateTime(long evaluateTime) {
            this.evaluateTime = evaluateTime;
        }
    }

    public void setEvaluateTeacherEntity(EvaluateTeacherEntity evaluateTeacherEntity) {
        this.evaluateTeacherEntity = evaluateTeacherEntity;
    }

    public EvaluateTeacherEntity getEvaluateTeacherEntity() {
        return evaluateTeacherEntity;
    }

    public String getMulpreload() {
        return mulpreload;
    }

    public void setMulpreload(String mulpreload) {
        this.mulpreload = mulpreload;
    }

    public String getMulh5url() {
        return mulh5url;
    }

    public void setMulh5url(String mulh5url) {
        this.mulh5url = mulh5url;
    }

    public String getGetCourseWareHtmlNew() {
        return getCourseWareHtmlNew;
    }

    public void setGetCourseWareHtmlNew(String getCourseWareHtmlNew) {
        this.getCourseWareHtmlNew = getCourseWareHtmlNew;
    }

    public String getGetCourseWareHtmlZhongXueUrl() {
        return getCourseWareHtmlZhongXueUrl;
    }

    public void setGetCourseWareHtmlZhongXueUrl(String getCourseWareHtmlZhongXueUrl) {
        this.getCourseWareHtmlZhongXueUrl = getCourseWareHtmlZhongXueUrl;
    }

    /**
     * 直播间额外参数信息
     */
    private ArtsExtLiveInfo artsExtLiveInfo;

    public void setArtsExtLiveInfo(ArtsExtLiveInfo artsExtLiveInfo) {
        this.artsExtLiveInfo = artsExtLiveInfo;
    }

    public ArtsExtLiveInfo getArtsExtLiveInfo() {
        return artsExtLiveInfo;
    }

    public static class EPlanInfoBean {
        public String ePlanId;
        public String eTeacherId;
        public String eClassId;
        public String fakePlanId;
    }

    public static class BetterMe {
        private boolean isArriveLate;
        private boolean isUseBetterMe;
        //段位信息
        private StuSegmentEntity stuSegment;
        //本场小目标
        private BetterMeEntity target;
        //小目标实时完成值
        private AimRealTimeValEntity current;

        public boolean isArriveLate() {
            return isArriveLate;
        }

        public void setArriveLate(boolean arriveLate) {
            isArriveLate = arriveLate;
        }

        public boolean isUseBetterMe() {
            return isUseBetterMe;
        }

        public void setUseBetterMe(boolean useBetterMe) {
            isUseBetterMe = useBetterMe;
        }

        public StuSegmentEntity getStuSegment() {
            return stuSegment;
        }

        public void setStuSegment(StuSegmentEntity stuSegment) {
            this.stuSegment = stuSegment;
        }

        public BetterMeEntity getTarget() {
            return target;
        }

        public void setTarget(BetterMeEntity target) {
            this.target = target;
        }

        public AimRealTimeValEntity getCurrent() {
            return current;
        }

        public void setCurrent(AimRealTimeValEntity current) {
            this.current = current;
        }
    }

    /**
     * https://wiki.xesv5.com/pages/viewpage.action?pageId=14027645
     */
    public static class EnglishPk {
        public int canUsePK;//: 1,  // 是否可以使用战队pk

        // 能使用条件：英语 && 直播 && 标准课 && 不迟到 （时间在小于等于一个小时，1/3   大于一个小时，1/6） && 双优英语 带学段的

        public int historyScore;//10, // 历史场次(最近三场)计算的评估值

        public int isTwoLose;//1,  // 是否连输两场      0 => 正常  1 => 连输两场

        public int hasGroup;//0    // 是否已经进行分组，0 => 未分组 1=> 已分组 如果该字段为1 就不用再请求go重新分组
    }

    public static class EnPkEnergy {
        public int me;//: 10,
        public int myTeam;//":80,
        public int opTeam;//":100
    }

    public boolean isShowHightFeedback() {
        return showHightFeedback;
    }

    public void setShowHightFeedback(boolean showHightFeedback) {
        this.showHightFeedback = showHightFeedback;
    }
    /*************************************************大班整合新增相关字段
     * ****************************************************************/

    /**
     * 磐石id
     **/
    private String psId;
    /**
     * 磐石密码
     **/
    private String psPwd;


    /**
     * 主讲流名称
     **/
    private String mainTeacherVieo;

    /**
     * 辅导流名称
     **/
    private String counselorTeacherVideo;

    /**
     * irc 昵称
     **/
    private String ircNick;
    /**
     * irc 房间
     **/
    private List<String> ircRoomList;
    /**
     * irc 房间JsonArray
     **/
    private String ircRoomsJson;

    /**
     * 业务接口配置
     **/
    private HashMap<String, String> urlMap;

    /**
     * 云平台appId
     **/
    private String psAppId;
    /**
     * 云平台 appkey
     **/
    private String psAppKey;

    /**
     * 是否是大班整合直播
     **/
    private boolean bigLive;


    /**大班整合小学**/
    private boolean bigLivePrimarySchool = true;

    public int getSkinType() {
        return skinType;
    }

    public void setSkinType(int skinType) {
        this.skinType = skinType;
    }

    /**
     * 大班整合 皮肤类型
     */
    private int skinType;

    public String getMainTeacherVieo() {
        return mainTeacherVieo;
    }

    public void setMainTeacherVieo(String mainTeacherVieo) {
        this.mainTeacherVieo = mainTeacherVieo;
    }

    public String getCounselorTeacherVideo() {
        return counselorTeacherVideo;
    }

    public void setCounselorTeacherVideo(String counselorTeacherVideo) {
        this.counselorTeacherVideo = counselorTeacherVideo;
    }

    public String getIrcNick() {
        return ircNick;
    }

    public void setIrcNick(String ircNick) {
        this.ircNick = ircNick;
    }

    public List<String> getIrcRoomList() {
        return ircRoomList;
    }

    public void setIrcRoomList(List<String> ircRoomList) {
        this.ircRoomList = ircRoomList;
    }

    public String getIrcRoomsJson() {
        return ircRoomsJson;
    }

    public void setIrcRoomsJson(String ircRoomsJson) {
        this.ircRoomsJson = ircRoomsJson;
    }

    public String getPsAppId() {
        return psAppId;
    }

    public void setPsAppId(String psAppId) {
        this.psAppId = psAppId;
    }

    public String getPsAppKey() {
        return psAppKey;
    }

    public void setPsAppKey(String psAppKey) {
        this.psAppKey = psAppKey;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }

    public String getPsId() {
        return psId;
    }


    public void setPsPwd(String psPwd) {
        this.psPwd = psPwd;
    }

    public String getPsPwd() {
        return psPwd;
    }


    public void setBigLive(boolean bigLive) {
        this.bigLive = bigLive;
    }

    public boolean isBigLive() {
        return bigLive;
    }

    public String getLivePluginKey() {
        return livePluginKey;
    }

    public void setLivePluginKey(String livePluginKey) {
        this.livePluginKey = livePluginKey;
    }

    public LiveModuleConfigInfo getLiveModuleConfigInfo() {
        return liveModuleConfigInfo;
    }

    public void setLiveModuleConfigInfo(LiveModuleConfigInfo liveModuleConfigInfo) {
        this.liveModuleConfigInfo = liveModuleConfigInfo;
    }

    /**
     * 大班整合直播状态
     **/
    public static class LiveStatus {
        /**
         * 老师是否点击了上课
         **/
        private boolean startClass;

        /**当前视频流模式：课前辅导/主讲/课后辅导 */
        private int streamMode;

        public boolean isStartClass() {
            return startClass;
        }

        public void setStartClass(boolean startClass) {
            this.startClass = startClass;
        }

        public int getStreamMode() {
            return streamMode;
        }

        public void setStreamMode(int streamMode) {
            this.streamMode = streamMode;
        }
    }

    public RecordStandliveEntity getRecordStandliveEntity() {
        return recordStandliveEntity;
    }

    public void setRecordStandliveEntity(RecordStandliveEntity recordStandliveEntity) {
        this.recordStandliveEntity = recordStandliveEntity;
    }

    /**
     * 根据moduleId 查找 Plugin
     *
     * @param moduleId
     * @return
     */
    public LivePlugin getLivePluginByModuleId(int moduleId) {
        LivePlugin plugin = null;
        LiveModuleConfigInfo info = getLiveModuleConfigInfo();
        if (info != null && info.plugins != null) {
            List<LivePlugin> plugins = info.plugins;
            for (int i = 0; i < plugins.size(); i++) {
                if (moduleId == plugins.get(i).moduleId) {
                    plugin = plugins.get(i);
                    break;
                }
            }
        }

        return plugin;
    }


    /**
     * 根据pluginName 查找 Plugin
     *
     * @param pluginName
     * @return
     */
    public LivePlugin getLivePluginByPluginName(String pluginName) {
        LivePlugin plugin = null;
        LiveModuleConfigInfo info = getLiveModuleConfigInfo();
        if (info != null && info.plugins != null) {
            List<LivePlugin> plugins = info.plugins;
            for (int i = 0; i < plugins.size(); i++) {
                if (pluginName.equals(plugins.get(i).pluginName)) {
                    plugin = plugins.get(i);
                    break;
                }
            }
        }
        return plugin;
    }


    /**
     * 根据moudlid key 返回属性
     *
     * @param moudleId
     * @param key
     * @return
     */
    public String getProperties(int moudleId, String key) {
        LivePlugin plugin = getLivePluginByModuleId(moudleId);
        if (plugin != null) {
            Map<String, String> maplist = plugin.properties;
            if (maplist != null) {
                return maplist.get(key);
            }
        }
        return "";
    }


    /**
     * 根据moudlid 功能是否打开
     *
     * @param moudleId
     * @param key
     * @return
     */
    public boolean isMoudleAllowed(int moudleId) {
        LivePlugin plugin = getLivePluginByModuleId(moudleId);
        if (plugin != null) {
            return plugin.isAllowed;
        }
        return false;
    }

    public void setInitModuleUrl(String initModuleUrl) {
        this.initModuleUrl = initModuleUrl;
    }

    public String getInitModuleUrl() {
        return initModuleUrl;
    }

    public String getGradeIds() {
        return gradeIds;
    }

    public void setGradeIds(String gradeIds) {
        this.gradeIds = gradeIds;
    }

    public SubGroupEntity getSubGroupEntity() {
        return subGroupEntity;
    }

    public void setSubGroupEntity(SubGroupEntity subGroupEntity) {
        this.subGroupEntity = subGroupEntity;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public boolean isBigLivePrimarySchool() {
        return skinType == 1;
    }
    public void setTeamStuIds(ArrayList<String> teamStuIds) {
        this.teamStuIds = teamStuIds;
    }

    public int getMinToFakeScore() {
        return minToFakeScore;
    }

    public void setMinToFakeScore(int minToFakeScore) {
        this.minToFakeScore = minToFakeScore;
    }

    public int getMaxToFakeScore() {
        return maxToFakeScore;
    }

    public void setMaxToFakeScore(int maxToFakeScore) {
        this.maxToFakeScore = maxToFakeScore;
    }

    public int getFakeScore() {
        return fakeScore;
    }

    public void setFakeScore(int fakeScore) {
        this.fakeScore = fakeScore;
    }

    public boolean isUse1v2Sdk() {
        return use1v2Sdk;
    }

    public void setUse1v2Sdk(boolean use1v2Sdk) {
        this.use1v2Sdk = use1v2Sdk;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getSeriesLectureId() {
        return seriesLectureId;
    }

    public void setSeriesLectureId(int seriesLectureId) {
        this.seriesLectureId = seriesLectureId;
    }

    public String getSeriesLectureName() {
        return seriesLectureName;
    }

    public void setSeriesLectureName(String seriesLectureName) {
        this.seriesLectureName = seriesLectureName;
    }

    public int getIsSeriesLectureSub() {
        return isSeriesLectureSub;
    }

    public void setIsSeriesLectureSub(int isSeriesLectureSub) {
        this.isSeriesLectureSub = isSeriesLectureSub;
    }

    public int getBubbleSwitch() {
        return bubbleSwitch;
    }

    public void setBubbleSwitch(int bubbleSwitch) {
        this.bubbleSwitch = bubbleSwitch;
    }

    public String getBubbleText() {
        return bubbleText;
    }

    public void setBubbleText(String bubbleText) {
        this.bubbleText = bubbleText;
    }

    public String getPopupMainTitle() {
        return popupMainTitle;
    }

    public void setPopupMainTitle(String popupMainTitle) {
        this.popupMainTitle = popupMainTitle;
    }

    public String getPopupSubTitle() {
        return popupSubTitle;
    }

    public void setPopupSubTitle(String popupSubTitle) {
        this.popupSubTitle = popupSubTitle;
    }

    public String getSubSuccessTitle() {
        return subSuccessTitle;
    }

    public void setSubSuccessTitle(String subSuccessTitle) {
        this.subSuccessTitle = subSuccessTitle;
    }

    public String getLiveTypeId() {
        return liveTypeId;
    }

    public void setLiveTypeId(String liveTypeId) {
        this.liveTypeId = liveTypeId;
    }
}

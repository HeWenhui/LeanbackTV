package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.AllLiveBasePagerIml;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveLog;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveOnLineLogs;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveUidRx;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.MediaControllerAction;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.remark.business.OnItemClick;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/17.
 */
public class LiveBackBll extends BaseBll implements LiveAndBackDebug, LivePlaybackMediaController.OnPointClick {
    protected String TAG = "LiveBackBll";
    protected Logger logger = LiveLoggerFactory.getLogger(getClass().getSimpleName());
    protected Activity activity;
    private LiveGetInfo mGetInfo;
    LiveHttpManager mHttpManager;
    private AllLiveBasePagerIml allLiveBasePagerIml;
    /**
     * 购课id
     */
    protected String stuCourId;
    /**
     * 视频节对象
     */
    protected VideoLivePlayBackEntity mVideoEntity;
    /**
     * 从哪个页面跳转
     */
    String where;
    int isArts;
    int mLiveType;
    /**
     * 区分文理appid
     */
    protected String appID = UmsConstants.LIVE_APP_ID_BACK;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE = false;
    /**
     * 播放器核心服务
     */
    protected PlayerService vPlayer;
    /**
     * 互动题
     */
    protected VideoQuestionEntity mQuestionEntity;
    private HashMap<VideoQuestionEntity, VideoQuestionLiveEntity> liveEntityHashMap = new HashMap<>();
    /**
     * 显示互动题
     */
    private static final int SHOW_QUESTION = 0;
    /**
     * 没有互动题
     */
    private static final int NO_QUESTION = 1;
    /**
     * 当前是否正在显示互动题
     */
    private boolean mIsShowQuestion = false;
    protected ArrayList<LiveBackBaseBll> liveBackBaseBlls = new ArrayList<>();
    protected SparseArray<LiveBackBaseBll> array = new SparseArray<>();
    /**
     * 直播间内模块间 数据共享池
     */
    private HashMap<String, Object> businessShareParamMap = new HashMap<String, Object>();
    private AtomicBoolean mIsLand = new AtomicBoolean(true);
    private LivePlayBackHttpManager mCourseHttpManager;
    private LivePlayBackHttpResponseParser mCourseHttpResponseParser;
    /**
     * 本地视频
     */
    boolean islocal;
    /**
     * 2 代表全身直播
     */
    private int pattern = 1;
    protected ShowQuestion showQuestion;
    private LiveUidRx liveUidRx;
    LogToFile logToFile;
    private LiveLog liveLog;
    /**
     * 是否是体验课
     */
    private Boolean isExperience;

    public LiveBackBll(Activity activity, VideoLivePlayBackEntity mVideoEntity) {
        super(activity);
        logger.setLogMethod(false);
        this.activity = activity;
        this.mVideoEntity = mVideoEntity;
        ProxUtil.getProxUtil().put(activity, LiveAndBackDebug.class, this);
        Intent intent = activity.getIntent();
        isArts = intent.getIntExtra("isArts", 0);
        islocal = intent.getBooleanExtra("islocal", false);
        pattern = intent.getIntExtra("pattern", 0);
        isExperience = intent.getBooleanExtra("isExperience", false);
        where = intent.getStringExtra("where");
        if ("LivePlayBackActivity".equals(where)) {//直播辅导
            mLiveType = LiveVideoConfig.LIVE_TYPE_TUTORIAL;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
            mLiveType = LiveVideoConfig.LIVE_TYPE_LECTURE;
            appID = UmsConstants.OPERAT_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        } else {
            if (islocal) {
                if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {//直播辅导下载
                    mLiveType = LiveVideoConfig.LIVE_TYPE_TUTORIAL;
                } else {//直播课下载
                    mLiveType = LiveVideoConfig.LIVE_TYPE_LIVE;
                }
            } else {
                mLiveType = LiveVideoConfig.LIVE_TYPE_LIVE;
            }
            if (isArts == 1) {
                appID = UmsConstants.ARTS_APP_ID_BACK;
                IS_SCIENCE = false;
                liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
            } else if (isArts == 2) {
                appID = UmsConstants.LIVE_BACK_CN_ID;
                IS_SCIENCE = false;
                liveVideoSAConfig = new LiveVideoSAConfig(LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST);
                try {
                    List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
                    int oldSize = -1;
                    if (lstVideoQuestion != null) {
                        oldSize = lstVideoQuestion.size();
                        for (int i = 0; i < lstVideoQuestion.size(); i++) {
                            VideoQuestionEntity questionEntity = lstVideoQuestion.get(i);
                            //战队pk分队
                            if (questionEntity.getvCategory() == 23 || questionEntity.getvCategory() == 25) {
                                lstVideoQuestion.remove(i);
                                i--;
                            }
                        }
                        int size = lstVideoQuestion.size();
                        if (size != oldSize) {
                            try {
                                HashMap<String, String> hashMap = new HashMap();
                                hashMap.put("logtype", "removepk");
                                hashMap.put("livetype", "" + mLiveType);
                                hashMap.put("where", "" + where);
                                hashMap.put("liveid", "" + mVideoEntity.getLiveId());
                                hashMap.put("size", oldSize + "-" + size);
                                UmsAgentManager.umsAgentDebug(activity, TAG, hashMap);
                            } catch (Exception e) {
                                CrashReport.postCatchedException(e);
                            }
                        }
                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
            } else {
                appID = UmsConstants.LIVE_APP_ID_BACK;
                IS_SCIENCE = true;
                liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
                try {
                    List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
                    int oldSize = -1;
                    if (lstVideoQuestion != null) {
                        oldSize = lstVideoQuestion.size();
                        for (int i = 0; i < lstVideoQuestion.size(); i++) {
                            VideoQuestionEntity questionEntity = lstVideoQuestion.get(i);
                            //战队pk分队
                            if (questionEntity.getvCategory() == 23 || questionEntity.getvCategory() == 25) {
                                lstVideoQuestion.remove(i);
                                i--;
                            }
                        }
                        int size = lstVideoQuestion.size();
                        if (size != oldSize) {
                            try {
                                HashMap<String, String> hashMap = new HashMap();
                                hashMap.put("logtype", "removepk");
                                hashMap.put("livetype", "" + mLiveType);
                                hashMap.put("where", "" + where);
                                hashMap.put("liveid", "" + mVideoEntity.getLiveId());
                                hashMap.put("size", oldSize + "-" + size);
                                UmsAgentManager.umsAgentDebug(activity, TAG, hashMap);
                            } catch (Exception e) {
                                CrashReport.postCatchedException(e);
                            }
                        }
                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
            }
        }
        liveLog = new LiveLog(activity, mLiveType, mVideoEntity.getLiveId(), getPrefix());
        ProxUtil.getProxUtil().put(activity, LiveOnLineLogs.class, liveLog);
        logToFile = new LogToFile(activity, TAG);
        mCourseHttpManager = new LivePlayBackHttpManager(activity);
        if (liveVideoSAConfig != null) {
            mCourseHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        }
        mCourseHttpManager.addBodyParam("liveId", mVideoEntity.getLiveId());
        mCourseHttpResponseParser = new LivePlayBackHttpResponseParser();
        allLiveBasePagerIml = new AllLiveBasePagerIml(activity);
        showQuestion = new LiveShowQuestion();
        liveUidRx = new LiveUidRx(activity, false);
        mHttpManager = new LiveHttpManager(activity);
        if (liveVideoSAConfig != null) {
            mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        }
        mHttpManager.addBodyParam("liveId", mVideoEntity.getLiveId());
        if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_RECORDED) {
            try {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("logtype", "recorded");
                hashMap.put("livetype", "" + mLiveType);
                hashMap.put("where", "" + where);
                hashMap.put("contextname", "" + intent.getStringExtra("contextname"));
                hashMap.put("bundle", "" + intent.getExtras());
                hashMap.put("liveid", "" + mVideoEntity.getLiveId());
                UmsAgentManager.umsAgentDebug(activity, TAG, hashMap);
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
            }
        }
    }

    public Boolean getExperience() {
        return isExperience;
    }

    public int getLiveType() {
        return mLiveType;
    }

    public int getPattern() {
        return pattern;
    }

    public int getIsArts() {
        return isArts;
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    public PlayerService getvPlayer() {
        return vPlayer;
    }

    public void setStuCourId(String stuCourId) {
        this.stuCourId = stuCourId;
        mCourseHttpManager.addBodyParam("stuCouId", stuCourId);
        mHttpManager.addBodyParam("stuCouId", stuCourId);
    }

    public void addBusinessBll(LiveBackBaseBll bll) {
        liveBackBaseBlls.add(bll);
        int[] categorys = bll.getCategorys();
        if (categorys != null) {
            for (int i = 0; i < categorys.length; i++) {
                array.put(categorys[i], bll);
            }
        }
    }

    public void removeBusinessBll(LiveBackBaseBll bll) {
        liveBackBaseBlls.remove(bll);
        int[] categorys = bll.getCategorys();
        if (categorys != null) {
            for (int i = 0; i < categorys.length; i++) {
                array.remove(categorys[i]);
            }
        }
    }

    public void onCreate() {
        LiveGetInfo liveGetInfo = new LiveGetInfo(null);
        mGetInfo = liveGetInfo;
        liveGetInfo.setId(mVideoEntity.getLiveId());
        liveGetInfo.setUname(AppBll.getInstance().getAppInfoEntity().getChildName());
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        liveGetInfo.setStuId(userInfoEntity.getStuId());
        liveGetInfo.setStuCouId(stuCourId);
        if (liveVideoSAConfig != null) {
            liveGetInfo.setSubjectiveTestAnswerResult(
                    (isArts == 2) ?
                            liveVideoSAConfig.inner.chsSubjectiveTestAnswerResult :
                            liveVideoSAConfig.inner.subjectiveTestAnswerResult);
        }
        liveGetInfo.setTestPaperUrl("https://live.xueersi.com/Live/getMultiTestPaper");
        liveGetInfo.setIs_show_ranks("0");
        liveGetInfo.setLiveType(mLiveType);
        liveGetInfo.setIsArts(isArts);
        LiveGetInfo.MainTeacherInfo mainTeacherInfo = liveGetInfo.getMainTeacherInfo();
        mainTeacherInfo.setTeacherId(mVideoEntity.getMainTeacherId());
        mainTeacherInfo.setTeacherName(mVideoEntity.getMainTeacherName());
        mainTeacherInfo.setTeacherImg(mVideoEntity.getMainTeacherImg());
        liveGetInfo.setTeacherId(mVideoEntity.getTutorTeacherId());
        liveGetInfo.setTeacherName(mVideoEntity.getTutorTeacherName());
        liveGetInfo.setTeacherIMG(mVideoEntity.getTutorTeacherImg());
        MyUserInfoEntity mMyInfo = UserBll.getInstance().getMyUserInfoEntity();
        if (!StringUtils.isEmpty(mMyInfo.getEnglishName())) {
            liveGetInfo.setEn_name(mMyInfo.getEnglishName());
        } else if (!StringUtils.isEmpty(mMyInfo.getRealName())) {
            liveGetInfo.setStuName(mMyInfo.getRealName());
        } else if (!StringUtils.isEmpty(mMyInfo.getNickName())) {
            liveGetInfo.setNickname(mMyInfo.getNickName());
        }
        //解析性别
        liveGetInfo.setStuSex(mMyInfo.getSex() + "");
        liveGetInfo.setHeadImgPath(mMyInfo.getHeadImg());

        LiveGetInfo.StudentLiveInfoEntity studentLiveInfoEntity = new LiveGetInfo.StudentLiveInfoEntity();
        studentLiveInfoEntity.setClassId(mVideoEntity.getClassId());
        studentLiveInfoEntity.setCourseId(mVideoEntity.getCourseId());

        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            studentLiveInfoEntity.setLearning_stage(mVideoEntity.getLearning_stage());
        }

        mGetInfo.setStudentLiveInfo(studentLiveInfoEntity);

        liveGetInfo.setPattern(pattern);
        try {
            String getInfoStr = mVideoEntity.getGetInfoStr();
            if (getInfoStr != null) {
                JSONObject liveInfo = new JSONObject(getInfoStr);
                liveGetInfo.setSmallEnglish("1".equals(liveInfo.optString("useSkin")));
                liveGetInfo.setPrimaryChinese("2".equals(liveInfo.optString("useSkin")));
                if (liveGetInfo.getStudentLiveInfo() != null) {
                    liveGetInfo.getStudentLiveInfo().setClassId(liveInfo.optString("class_id"));
                }
                //解析学科id
                if (liveInfo.has("subject_ids")) {
                    String strSubjIds = liveInfo.getString("subject_ids");
                    String[] arrSubjIds = strSubjIds.split(",");
                    liveGetInfo.setSubjectIds(arrSubjIds);
                }
                mCourseHttpResponseParser.parseLiveGetInfo(liveInfo, liveGetInfo, mLiveType, isArts);
            }
        } catch (Exception e) {
            logger.e("onCreate", e);
        }
        if (liveLog != null) {
            liveLog.setGetInfo(liveGetInfo);
        }
        String clientLog = mShareDataManager.getString(LiveVideoConfig.SP_LIVEVIDEO_CLIENT_LOG, LiveVideoConfig
                .URL_LIVE_ON_LOAD_LOGS, ShareDataManager.SHAREDATA_NOT_CLEAR);
        liveGetInfo.setClientLog(clientLog);
        liveUidRx.setLiveGetInfo(liveGetInfo);
        liveUidRx.onCreate();
        ArrayList<LiveBackBaseBll> templiveBackBaseBlls = new ArrayList<>(liveBackBaseBlls);
        for (LiveBackBaseBll liveBackBaseBll : templiveBackBaseBlls) {
            liveBackBaseBll.onCreateF(mVideoEntity, liveGetInfo, businessShareParamMap);
        }
        templiveBackBaseBlls.clear();
    }

    public ArrayList<LiveBackBaseBll> getLiveBackBaseBlls() {
        return liveBackBaseBlls;
    }

    public String getStuCourId() {
        return stuCourId;
    }

    public LivePlayBackHttpManager getCourseHttpManager() {
        return mCourseHttpManager;
    }

    public LivePlayBackHttpResponseParser getCourseHttpResponseParser() {
        return mCourseHttpResponseParser;
    }

    public LiveHttpManager getmHttpManager() {
        return mHttpManager;
    }

    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    private OnItemClick onItemClick;

    public OnItemClick getOnItemClick() {
        if (onItemClick == null) {
            onItemClick = new OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
                    if (position < lstVideoQuestion.size()) {
                        VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(position);
                        boolean same = mQuestionEntity == videoQuestionEntity;
                        logger.d("onItemClick:position=" + position + "" + videoQuestionEntity.getvQuestionID() + ",start=" + videoQuestionEntity.getvQuestionInsretTime()
                                + ",isAnswered=" + videoQuestionEntity.isAnswered() + ",same=" + same);
                        if (!same) {
                            videoQuestionEntity.setAnswered(false);
                        }
                    }
                }
            };
        }
        return onItemClick;
    }

    /**
     * 扫描是否有需要弹出的互动题
     */
    public void scanQuestion(long position) {

        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        int playPosition = TimeUtils.gennerSecond(position);
        logger.d("scanQuestion:playPosition=" + playPosition);
        mQuestionEntity = getPlayQuetion(TimeUtils.gennerSecond(position));
        //旧题不为空，新题和旧题不一样
        if (oldQuestionEntity != null && oldQuestionEntity != mQuestionEntity) {
            //新题为空，旧题是点击过去的。
            if (mQuestionEntity == null && oldQuestionEntity.isClick()) {
                if (playPosition < oldQuestionEntity.getvEndTime()) {
                    mQuestionEntity = oldQuestionEntity;
                    logger.d("scanQuestion:isClick");
                    return;
                }
            }
            LiveBackBaseBll liveBackBaseBll = array.get(oldQuestionEntity.getvCategory());
            if (liveBackBaseBll != null) {
                logger.d("scanQuestion:onQuestionEnd:id=" + oldQuestionEntity.getvCategory());
                Log.e("mqtt", "关闭上一题" + "position:" + position);
                liveBackBaseBll.onQuestionEnd(oldQuestionEntity);
            }
            showQuestion.onHide(oldQuestionEntity);
        }
        if (mQuestionEntity != null && oldQuestionEntity != mQuestionEntity && !mQuestionEntity.isAnswered()) {
            mQuestionEntity.setAnswered(true);
            logger.d("scanQuestion:showQuestion");
            Log.e("Duncan", "showQuestion:" + position);
            showQuestion(oldQuestionEntity, showQuestion);
            if (LocalCourseConfig.CATEGORY_REDPACKET != mQuestionEntity.getvCategory()) {
                LiveVideoConfig.isAITrue = false;
            }
        }
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.onPositionChanged(playPosition);
        }
    }

    @Override
    public void onOnPointClick(VideoQuestionEntity videoQuestionEntity, long position) {
        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        mQuestionEntity = videoQuestionEntity;
        mQuestionEntity.setClick(true);
        mQuestionEntity.setAnswered(true);
        showQuestion(oldQuestionEntity, showQuestion);
    }

    class LiveShowQuestion implements ShowQuestion {
        LiveShowQuestion() {
            ProxUtil.getProxUtil().put(activity, ShowQuestion.class, this);
        }

        @Override
        public void onShow(boolean isShow, VideoQuestionLiveEntity videoQuestionLiveEntity) {
            liveEntityHashMap.put(mQuestionEntity, videoQuestionLiveEntity);
            if (isShow) {
                mIsShowQuestion = true;
                MediaControllerAction mediaControllerAction = ProxUtil.getProxUtil().get(activity,
                        MediaControllerAction.class);
                if (mediaControllerAction != null) {
                    mediaControllerAction.release();
                }

            }
        }

        @Override
        public void onHide(BaseVideoQuestionEntity baseVideoQuestionEntity) {
            logToFile.d("onHide:mQuestionEntity=" + mQuestionEntity + ",baseVideoQuestionEntity=" +
                    baseVideoQuestionEntity);
            if (mQuestionEntity != null && baseVideoQuestionEntity != null) {
                VideoQuestionLiveEntity videoQuestionLiveEntity = liveEntityHashMap.get(mQuestionEntity);
                if (videoQuestionLiveEntity != null) {
                    logToFile.d("onHide:vCategory=" + mQuestionEntity.getvCategory() + ",id=" +
                            videoQuestionLiveEntity.getvQuestionID() + ",id2=" + baseVideoQuestionEntity
                            .getvQuestionID());
                    if (videoQuestionLiveEntity != baseVideoQuestionEntity) {
                        return;
                    }
                }
            }
            mIsShowQuestion = false;
            MediaControllerAction mediaControllerAction = ProxUtil.getProxUtil().get(activity, MediaControllerAction
                    .class);
            if (mediaControllerAction != null) {
                mediaControllerAction.attachMediaController();
            }

        }
    }

    /**
     * 获取互动题
     *
     * @param playPosition
     */
    private boolean standexperienceRecommondCourseIsShow = false;

    private VideoQuestionEntity getPlayQuetion(int playPosition) {
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return null;
        }
        int startTime, endTime;
        VideoQuestionEntity mQuestionEntity = null;
        boolean hasQuestionShow = false;
        int index = 0;
        for (int i = 0; i < lstVideoQuestion.size(); i++) {
            VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
            startTime = videoQuestionEntity.getvQuestionInsretTime();
            endTime = videoQuestionEntity.getvEndTime();
            // 红包只有开始时间
            if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionEntity.getvQuestionType())) {//语音评测。在那个点弹出
                    // 在开始时间和结束时间之间
                    if (startTime <= playPosition && playPosition < endTime) {
//                    if (startTime == playPosition) {
                        mQuestionEntity = videoQuestionEntity;
                        hasQuestionShow = true;
                        index = i;
                        break;
                    }
                } else {
                    // 互动题在开始时间和结束时间之间
                    if (startTime <= playPosition && playPosition < endTime) {
                        mQuestionEntity = videoQuestionEntity;
                        hasQuestionShow = true;
                        index = i;
                        break;
                    }
                }
            } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_NB_ADDEXPERIMENT == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
                    LiveVideoConfig.isMulLiveBack = false;
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE == videoQuestionEntity.getvCategory() ||
                    LocalCourseConfig.CATEGORY_TUTOR_EVENT_35 == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
                    LiveVideoConfig.isMulLiveBack = true;
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_LEC_ADVERT == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime == playPosition) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
                    LiveVideoConfig.isMulLiveBack = false;
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    Log.e("Duncan", "i:" + i + "playPosition:" + playPosition);
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_QUESTIONBLL_NEWARTSWARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
                    LiveVideoConfig.isMulLiveBack = false;
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    Log.e("Duncan", "i:" + i + "playPosition:" + playPosition);
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_UNDERSTAND == videoQuestionEntity.getvCategory()) {//懂了吗
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_RECOMMOND_COURSE == videoQuestionEntity.getvCategory()) {//推荐课程
                if (standexperienceRecommondCourseIsShow) {
                    continue;
                }
                if (startTime <= playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    standexperienceRecommondCourseIsShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_BIG_TEST == videoQuestionEntity.getvCategory()) {//大题互动
                if (startTime <= playPosition && playPosition < endTime) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_SUPER_SPEAKER == videoQuestionEntity.getvCategory()) {//大题互动
                if (startTime <= playPosition && playPosition < endTime) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    index = i;
                    break;
                }

            }
        }
        if (mQuestionEntity != null) {
            mQuestionEntity.setIndex(index);
        }
        return mQuestionEntity;
    }

    private void showQuestion(VideoQuestionEntity oldQuestionEntity, ShowQuestion showQuestion) {
        LiveBackBaseBll liveBackBaseBll = array.get(mQuestionEntity.getvCategory());
        logger.i("showQuestion :" + liveBackBaseBll);
        if (liveBackBaseBll != null) {
            liveBackBaseBll.showQuestion(oldQuestionEntity, mQuestionEntity, showQuestion);
        } else {
            try {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("logtype", "showQuestion");
                hashMap.put("livetype", "" + mLiveType);
                hashMap.put("where", "" + where);
                hashMap.put("liveid", "" + mVideoEntity.getLiveId());
                hashMap.put("category", "" + mQuestionEntity.getvCategory());
                UmsAgentManager.umsAgentDebug(activity, LogConfig.LIVE_BACK_CATEGORY_UNKNOW, hashMap);
            } catch (Exception e) {
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    public interface ShowQuestion {
        void onShow(boolean isShow, VideoQuestionLiveEntity videoQuestionLiveEntity);

        void onHide(BaseVideoQuestionEntity baseVideoQuestionEntity);
    }

    /**
     * 各模块 调用此方法 暴露自己需要和其他模块共享的参数
     *
     * @param key
     * @param value
     */
    public void addBusinessShareParam(String key, Object value) {
        synchronized (businessShareParamMap) {
            businessShareParamMap.put(key, value);
        }
    }

    /**
     * 各模块 调用此方法 暴露自己需要和其他模块共享的参数
     *
     * @param key
     */
    public void removeBusinessShareParam(String key) {
        synchronized (businessShareParamMap) {
            businessShareParamMap.remove(key);
        }
    }

    /**
     * 各模块调用此方法  查找其他模块暴露的 参数信息
     *
     * @param key
     * @return
     */
    public Object getBusinessShareParam(String key) {
        synchronized (businessShareParamMap) {
            return businessShareParamMap.get(key);
        }
    }

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("clits", "" + System.currentTimeMillis());
//        Loger.d(mContext, eventId, mData, true);
        UmsAgentManager.umsAgentDebug(activity, appID, eventId, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventId, final Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(activity, appID, UmsConstants.uploadBehavior, mData);
    }

    @Override
    public void umsAgentDebugPv(String eventId, final Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(activity, appID, UmsConstants.uploadShow, mData);
    }

    @Override
    public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventid", "" + eventId);
        setAnalysis(analysis);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadSystem, mData, analysis);
    }

    @Override
    public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventid", "" + eventId);
        setAnalysis(analysis);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData, analysis);
    }

    @Override
    public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventid", "" + eventId);
        setAnalysis(analysis);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadShow, mData, analysis);
    }

    /**
     * 上传log 添加 公共参数
     *
     * @param analysis
     */
    private void setAnalysis(Map<String, String> analysis) {
        if (!analysis.containsKey("success")) {
            analysis.put("success", "true");
        }
        if (!analysis.containsKey("errorcode")) {
            analysis.put("errorcode", "0");
        }
        if (!analysis.containsKey("duration")) {
            analysis.put("duration", "0");
        }
        if (!analysis.containsKey("modulekey")) {
            analysis.put("modulekey", "");
        }
        if (!analysis.containsKey("moduleid")) {
            analysis.put("moduleid", "");
        }
        analysis.put("timestamp", "" + System.currentTimeMillis());
        analysis.put("userid", mGetInfo.getStuId());
        analysis.put("planid", mVideoEntity.getLiveId());
        analysis.put("clientip", IpAddressUtil.USER_IP);
        analysis.put("traceid", "" + UUID.randomUUID());
        analysis.put("platform", "android");
    }

    public boolean onUserBackPressed() {
        boolean onUserBackPressed = allLiveBasePagerIml.onUserBackPressed();
        return onUserBackPressed;
    }

    public boolean isShowQuestion() {
        return mIsShowQuestion;
    }

    public void onRestart() {
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.onRestart();
        }
    }

    public void onStop() {
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.onStop();
        }
    }

    public void onReusme() {
        for (LiveBackBaseBll liveBackBaseBll : liveBackBaseBlls) {
            liveBackBaseBll.onResume();
        }
    }

    public void onNewIntent(Intent intent) {
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.onNewIntent(intent);
        }
    }

    public void onDestory() {
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.onDestory();
        }
        allLiveBasePagerIml.onDestory();
        businessShareParamMap.clear();
        liveBackBaseBlls.clear();
        if (liveUidRx != null) {
            liveUidRx.onDestory();
        }
    }

    public void onPausePlayer() {
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.onPausePlayer();
        }
    }

    public void onStartPlayer() {
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.onStartPlayer();
        }
    }

    public void setSpeed(float speed) {
        for (LiveBackBaseBll businessBll : liveBackBaseBlls) {
            businessBll.setSpeed(speed);
        }
    }

    public String getPrefix() {
        return "LB";
    }

    /**
     * 获取直播间初始换参数
     *
     * @return
     */
    public LiveGetInfo getRommInitData() {
        return mGetInfo;
    }
}

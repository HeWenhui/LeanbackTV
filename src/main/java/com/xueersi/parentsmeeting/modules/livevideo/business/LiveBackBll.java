package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.SparseArray;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.lib.analytics.umsagent.UmsAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.AllLiveBasePagerIml;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveOnLineLogs;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveUidRx;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.MediaControllerAction;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveLogCallback;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;

import org.json.JSONObject;
import org.xutils.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/17.
 */
public class LiveBackBll implements LiveAndBackDebug, LivePlaybackMediaController.OnPointClick, LiveOnLineLogs {
    private String TAG = "LiveBackBll";
    Logger logger = LoggerFactory.getLogger(TAG);
    Activity activity;
    private LiveGetInfo mGetInfo;
    LiveHttpManager mHttpManager;
    private AllLiveBasePagerIml allLiveBasePagerIml;
    /** 购课id */
    protected String stuCourId;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    /** 从哪个页面跳转 */
    String where;
    int isArts;
    int mLiveType;
    /** 区分文理appid */
    String appID = UmsConstants.LIVE_APP_ID_BACK;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE = false;
    /** 播放器核心服务 */
    protected PlayerService vPlayer;
    /** 互动题 */
    private VideoQuestionEntity mQuestionEntity;
    private HashMap<VideoQuestionEntity, VideoQuestionLiveEntity> liveEntityHashMap = new HashMap<>();
    /** 显示互动题 */
    private static final int SHOW_QUESTION = 0;
    /** 没有互动题 */
    private static final int NO_QUESTION = 1;
    /** 当前是否正在显示互动题 */
    private boolean mIsShowQuestion = false;
    ArrayList<LiveBackBaseBll> liveBackBaseBlls = new ArrayList<>();
    SparseArray<LiveBackBaseBll> array = new SparseArray<>();
    /** 直播间内模块间 数据共享池 */
    private HashMap<String, Object> businessShareParamMap = new HashMap<String, Object>();
    private AtomicBoolean mIsLand = new AtomicBoolean(true);
    private LivePlayBackHttpManager mCourseHttpManager;
    private LivePlayBackHttpResponseParser mCourseHttpResponseParser;
    /** 本地视频 */
    boolean islocal;
    private int pattern = 1;
    ShowQuestion showQuestion;
    private LiveUidRx liveUidRx;
    LogToFile logToFile;

    public LiveBackBll(Activity activity, VideoLivePlayBackEntity mVideoEntity) {
        logger.setLogMethod(false);
        this.activity = activity;
        this.mVideoEntity = mVideoEntity;
        ProxUtil.getProxUtil().put(activity, LiveAndBackDebug.class, this);
        Intent intent = activity.getIntent();
        isArts = intent.getIntExtra("isArts", 0);
        islocal = intent.getBooleanExtra("islocal", false);
        pattern = intent.getIntExtra("pattern", 0);
        if ("LivePlayBackActivity".equals(where)) {//直播辅导
            mLiveType = LiveVideoConfig.LIVE_TYPE_TUTORIAL;
        } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
            mLiveType = LiveVideoConfig.LIVE_TYPE_LECTURE;
            appID = UmsConstants.OPERAT_APP_ID;
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
            } else {
                appID = UmsConstants.LIVE_APP_ID_BACK;
                IS_SCIENCE = true;
                liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
            }
        }
        logToFile = new LogToFile(this, TAG);
        ProxUtil.getProxUtil().put(activity, LiveOnLineLogs.class, this);
        mCourseHttpManager = new LivePlayBackHttpManager(activity);
        mCourseHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        mCourseHttpResponseParser = new LivePlayBackHttpResponseParser();
        allLiveBasePagerIml = new AllLiveBasePagerIml(activity);
        showQuestion = new LiveShowQuestion();
        liveUidRx = new LiveUidRx(activity, false);
        mHttpManager = new LiveHttpManager(activity);
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
            liveGetInfo.setSubjectiveTestAnswerResult(liveVideoSAConfig.inner.subjectiveTestAnswerResult);
        }
        liveGetInfo.setTestPaperUrl("https://live.xueersi.com/Live/getMultiTestPaper");
        liveGetInfo.setIs_show_ranks("0");
        liveGetInfo.setLiveType(mLiveType);
        liveGetInfo.setIsArts(isArts);
        MyUserInfoEntity mMyInfo = UserBll.getInstance().getMyUserInfoEntity();
        if (!StringUtils.isEmpty(mMyInfo.getEnglishName())) {
            liveGetInfo.setEn_name(mMyInfo.getEnglishName());
        } else if (!StringUtils.isEmpty(mMyInfo.getRealName())) {
            liveGetInfo.setStuName(mMyInfo.getRealName());
        } else if (!StringUtils.isEmpty(mMyInfo.getNickName())) {
            liveGetInfo.setNickname(mMyInfo.getNickName());
        }
        liveGetInfo.setHeadImgPath(mMyInfo.getHeadImg());
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = new LiveGetInfo.StudentLiveInfoEntity();
            studentLiveInfo.setLearning_stage(mVideoEntity.getLearning_stage());
            liveGetInfo.setStudentLiveInfo(studentLiveInfo);
        }
        liveGetInfo.setPattern(pattern);
        try {
            String getInfoStr = mVideoEntity.getGetInfoStr();
            JSONObject liveInfo = new JSONObject(getInfoStr);
            liveGetInfo.setSmallEnglish("1".equals(liveInfo.optString("useSkin")));
        } catch (Exception e) {
            logger.e("onCreate", e);
        }
        liveUidRx.setLiveGetInfo(liveGetInfo);
        liveUidRx.onCreate();
        for (LiveBackBaseBll liveBackBaseBll : liveBackBaseBlls) {
            liveBackBaseBll.onCreateF(mVideoEntity, liveGetInfo, businessShareParamMap);
        }
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

    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    /** 扫描是否有需要弹出的互动题 */
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
                liveBackBaseBll.onQuestionEnd(oldQuestionEntity);
            }
            showQuestion.onHide(oldQuestionEntity);
        }
        if (mQuestionEntity != null && oldQuestionEntity != mQuestionEntity && !mQuestionEntity.isAnswered()) {
            mQuestionEntity.setAnswered(true);
            logger.d("scanQuestion:showQuestion");
            showQuestion(oldQuestionEntity, showQuestion);
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
                mediaControllerAction.release();
            }
        }

        @Override
        public void onHide(BaseVideoQuestionEntity baseVideoQuestionEntity) {

            logToFile.d("onHide:mQuestionEntity=" + mQuestionEntity + ",baseVideoQuestionEntity=" + baseVideoQuestionEntity);
            if (mQuestionEntity != null && baseVideoQuestionEntity != null) {
                VideoQuestionLiveEntity videoQuestionLiveEntity = liveEntityHashMap.get(mQuestionEntity);
                if (videoQuestionLiveEntity != null) {
                    logToFile.d("onHide:vCategory=" + mQuestionEntity.getvCategory() + ",id=" + videoQuestionLiveEntity.getvQuestionID() + ",id2=" + baseVideoQuestionEntity.getvQuestionID());
                    if (videoQuestionLiveEntity != baseVideoQuestionEntity) {
                        return;
                    }

                }
            }
            mIsShowQuestion = false;
            MediaControllerAction mediaControllerAction = ProxUtil.getProxUtil().get(activity, MediaControllerAction
                    .class);
            mediaControllerAction.attachMediaController();
        }
    }

    /**
     * 获取互动题
     *
     * @param playPosition
     */
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
                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionEntity.getvQuestionType()))
                {//语音评测。在那个点弹出
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
            } else if(LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE == videoQuestionEntity.getvCategory()){
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
            }
        }
        if (mQuestionEntity != null) {
            mQuestionEntity.setIndex(index);
        }
        return mQuestionEntity;
    }

    private void showQuestion(VideoQuestionEntity oldQuestionEntity, ShowQuestion showQuestion) {
        LiveBackBaseBll liveBackBaseBll = array.get(mQuestionEntity.getvCategory());
        if (liveBackBaseBll != null) {
            liveBackBaseBll.showQuestion(oldQuestionEntity, mQuestionEntity, showQuestion);
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

    @Override
    public String getPrefix() {
        return "LB";
    }

    /**
     * 播放器异常日志
     *
     * @param str
     */
    @Override
    public void getOnloadLogs(String TAG, String str) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String bz = UserBll.getInstance().getMyUserInfoEntity().getUserType() == 1 ? "student" : "teacher";
        PackageManager packageManager = activity.getPackageManager();
        PackageInfo packInfo = null;
        String filenam = "f";
        try {
            packInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo != null) {//else不会发生
            filenam = packInfo.versionCode + "";
        }
        filenam = Build.VERSION.SDK_INT + "&" + filenam;
        if (mGetInfo == null) {
            UmsAgent.onEvent(activity, LogerTag.DEBUG_VIDEO_LIVEMSG, LogerTag.DEBUG_VIDEO_LIVEMSG, 0, str);
            return;
        }
        LiveLogCallback liveLogCallback = new LiveLogCallback();
        RequestParams params = mHttpManager.liveOnloadLogs(mGetInfo.getClientLog(), "a" + mLiveType, mGetInfo.getId()
                , mGetInfo.getUname(), enstuId,
                mGetInfo.getStuId(), mGetInfo.getTeacherId(), filenam, str, bz, liveLogCallback);
        liveLogCallback.setParams(params);
    }
}

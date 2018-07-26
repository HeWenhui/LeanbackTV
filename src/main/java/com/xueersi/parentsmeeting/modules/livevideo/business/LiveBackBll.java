package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/17.
 */
public class LiveBackBll implements LiveAndBackDebug, AllLiveBasePagerInter {
    private String TAG = "LiveBackBll";
    Activity activity;
    ArrayList<LiveBasePager> liveBasePagers = new ArrayList<>();
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
    /** 显示互动题 */
    private static final int SHOW_QUESTION = 0;
    /** 没有互动题 */
    private static final int NO_QUESTION = 1;
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

    public LiveBackBll(Activity activity, VideoLivePlayBackEntity mVideoEntity) {
        this.activity = activity;
        this.mVideoEntity = mVideoEntity;
        ProxUtil.getProxUtil().put(activity, LiveAndBackDebug.class, this);
        ProxUtil.getProxUtil().put(activity, AllLiveBasePagerInter.class, this);
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
        mCourseHttpManager = new LivePlayBackHttpManager(activity);
        mCourseHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        mCourseHttpResponseParser = new LivePlayBackHttpResponseParser();
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

    public void onCreate() {
        LiveGetInfo liveGetInfo = new LiveGetInfo(null);
        liveGetInfo.setId(mVideoEntity.getLiveId());
        liveGetInfo.setUname(AppBll.getInstance().getAppInfoEntity().getChildName());
        liveGetInfo.setStuId(stuCourId);
        if (liveVideoSAConfig != null) {
            liveGetInfo.setSubjectiveTestAnswerResult(liveVideoSAConfig.inner.subjectiveTestAnswerResult);
        }
        liveGetInfo.setTestPaperUrl("http://live.xueersi.com/Live/getMultiTestPaper");
        liveGetInfo.setIs_show_ranks("0");
        liveGetInfo.setLiveType(mLiveType);
        MyUserInfoEntity mMyInfo = UserBll.getInstance().getMyUserInfoEntity();
        if (!StringUtils.isEmpty(mMyInfo.getEnglishName())) {
            liveGetInfo.setEn_name(mMyInfo.getEnglishName());
        } else if (!StringUtils.isEmpty(mMyInfo.getRealName())) {
            liveGetInfo.setStuName(mMyInfo.getRealName());
        } else if (!StringUtils.isEmpty(mMyInfo.getNickName())) {
            liveGetInfo.setNickname(mMyInfo.getNickName());
        }
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = new LiveGetInfo.StudentLiveInfoEntity();
            studentLiveInfo.setLearning_stage(mVideoEntity.getLearning_stage());
            liveGetInfo.setStudentLiveInfo(studentLiveInfo);
        }
        liveGetInfo.setPattern(pattern);
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
//    private Handler mPlayVideoControlHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SHOW_QUESTION:
//                    if (resultFailed) {
//                        Loger.d(TAG, "handleMessage:SHOW_QUESTION.msg=" + msg.obj + ",resultFailed=" + resultFailed);
//                        return;
//                    }
//                    mIsShowQuestion = true;
//                    if (mMediaController != null) {
//                        mMediaController.setWindowLayoutType();
//                        mMediaController.release();
//                        Loger.d(TAG, "handleMessage:SHOW_QUESTION:msg=" + msg.obj);
//                    }
//                    break;
//                case NO_QUESTION:
//                    if (mVideoCourseQuestionPager != null) {
//                        mVideoCourseQuestionPager.hideInputMode();
//                    }
//                    Object obj = msg.obj;
//                    Loger.d(TAG, "handleMessage:NO_QUESTION=" + msg.arg1 + "," + (obj == mQuestionEntity));
//                    setQuestionEntity(null);
//                    questionViewGone("NO_QUESTION");
//                    if (mPopupWindow != null) {
//                        mPopupWindow.dismiss();
//                        mPopupWindow = null;
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    /** 扫描是否有需要弹出的互动题 */
    public void scanQuestion(long position) {
        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        mQuestionEntity = getPlayQuetion(TimeUtils.gennerSecond(position));
        if (oldQuestionEntity != null && oldQuestionEntity != mQuestionEntity) {
            LiveBackBaseBll liveBackBaseBll = array.get(oldQuestionEntity.getCategory());
            if (liveBackBaseBll != null) {
                liveBackBaseBll.onQuestionEnd(oldQuestionEntity);
            }
        }
        if (mQuestionEntity != null && oldQuestionEntity != mQuestionEntity && mQuestionEntity.isAnswered()) {
            showQuestion(oldQuestionEntity);
        }
    }

    public void onOnPointClick(VideoQuestionEntity videoQuestionEntity, long position) {
        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        mQuestionEntity = videoQuestionEntity;
        mQuestionEntity.setClick(true);
        showQuestion(oldQuestionEntity);
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
        for (int i = 0; i < lstVideoQuestion.size(); i++) {
            VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
            if (videoQuestionEntity.isAnswered()) {
                continue;
            }
            startTime = videoQuestionEntity.getvQuestionInsretTime();
            endTime = videoQuestionEntity.getvEndTime();
            // 红包只有开始时间
            if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionEntity.getvQuestionType())) {//语音评测。在那个点弹出
                    // 在开始时间和结束时间之间
                    if (startTime <= playPosition && playPosition < endTime) {
//                    if (startTime == playPosition) {
                        mQuestionEntity = videoQuestionEntity;
                        hasQuestionShow = true;
                        break;
                    }
                } else {
                    // 互动题在开始时间和结束时间之间
                    if (startTime <= playPosition && playPosition < endTime) {
                        mQuestionEntity = videoQuestionEntity;
                        hasQuestionShow = true;
                        break;
                    }
                }
            } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_LEC_ADVERT == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime == playPosition) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            }
        }
        return mQuestionEntity;
//        Loger.i(TAG, "getPlayQuetion:playPosition=" + playPosition + ",hasQuestionShow=" + hasQuestionShow + ",
// mQuestionEntity=" + (mQuestionEntity != null));
//        if (mQuestionEntity != null) {
//            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
//                if (mQuestionEntity.getvEndTime() < playPosition) {
//                    if (examQuestionPlaybackPager != null) {
//                        examQuestionPlaybackPager.examSubmitAll();
//                        if (vPlayer != null) {
//                            vPlayer.pause();
//                        }
//                        Loger.i(TAG, "getPlayQuetion:examSubmitAll:playPosition=" + playPosition);
//                    }
//                }
//                return;
//            } else if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()) {
//                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(mQuestionEntity.getvQuestionType())) {
//                    if (mQuestionEntity.getvEndTime() < playPosition) {
//                        if (speechQuestionPlaybackPager != null) {
//                            speechQuestionPlaybackPager.examSubmitAll();
//                            if (vPlayer != null) {
//                                vPlayer.pause();
//                            }
//                            Loger.i(TAG, "getPlayQuetion:examSubmitAll:playPosition=" + playPosition);
//                        }
//                    }
//                    return;
//                } else {
//                    if (mQuestionEntity.getvEndTime() < playPosition) {
//                        if (questionWebPager != null && mQuestionEntity.getvQuestionID().equals(questionWebPager
//                                .getTestId())) {
//                            questionWebPager.examSubmitAll();
//                            if (vPlayer != null) {
//                                vPlayer.pause();
//                            }
//                            Loger.i(TAG, "getPlayQuetion:examSubmitAll2:playPosition=" + playPosition);
//                            return;
//                        }
//                    }
//                }
//            } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
//                if (mQuestionEntity.getvEndTime() < playPosition) {
//                    stopH5Exam();
//                }
//                return;
//            } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
//                if (mQuestionEntity.getvEndTime() < playPosition) {
//                    if (englishH5CoursewarePager != null) {
//                        englishH5CoursewarePager.submitData();
//                        if (vPlayer != null) {
//                            vPlayer.pause();
//                        }
//                        Loger.i(TAG, "getPlayQuetion:submitData:playPosition=" + playPosition);
//                    }
//                }
//                return;
//            }
//        }
        // 如果没有互动题则移除
//        if (!hasQuestionShow && mQuestionEntity != null) {
//            startTime = mQuestionEntity.getvQuestionInsretTime();
//            //播放器seekto的误差
//            Loger.i(TAG, "getPlayQuetion:isClick=" + mQuestionEntity.isClick() + ",playPosition=" + playPosition + "," +
//                    "startTime=" + startTime);
//            if (mQuestionEntity.isClick()) {
//                if (startTime - playPosition >= 0 && startTime - playPosition < 5) {
//                    return;
//                }
//            }
//            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 8, 8, mQuestionEntity);
//            mPlayVideoControlHandler.sendMessage(msg);
//        }
    }

    private void showQuestion(VideoQuestionEntity oldQuestionEntity) {
        LiveBackBaseBll liveBackBaseBll = array.get(mQuestionEntity.getvCategory());
        if (liveBackBaseBll != null) {
            liveBackBaseBll.showQuestion(oldQuestionEntity, mQuestionEntity);
        }
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
     * @param value
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
        Loger.d(TAG, "onUserBackPressed:liveBasePagers=" + liveBasePagers.size());
        ArrayList<LiveBasePager> liveBasePagersTemp = new ArrayList<>(liveBasePagers);
        for (int i = liveBasePagersTemp.size() - 1; i >= 0; i--) {
            LiveBasePager liveBasePager = liveBasePagersTemp.get(i);
            Loger.d(TAG, "onUserBackPressed:liveBasePager=" + liveBasePager);
        }
        return false;
    }

    @Override
    public void addLiveBasePager(LiveBasePager liveBasePager) {
        liveBasePagers.add(liveBasePager);
    }

    @Override
    public void removeLiveBasePager(LiveBasePager liveBasePager) {
        liveBasePagers.remove(liveBasePager);
    }
}

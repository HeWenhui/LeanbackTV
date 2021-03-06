package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack.SimpleVPlayerListener;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.SysLogLable;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.share.business.biglive.config.BigLiveCfg;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEnvironment;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveLog;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveOnLineLogs;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HalfBodyLiveStudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveBusinessResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveGetPlayServer;
import com.xueersi.parentsmeeting.modules.livevideo.video.TeacherIsPresent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.xutils.common.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 处理IRC消息，视频调度
 *
 * @author linyuqiang
 */
public class AuditClassLiveBll extends BaseBll implements LiveAndBackDebug {
    private String TAG = "AuditClassLiveBllLog";
    private static final int MSG_TYPE_DISPATCH_NOTICE = 1;
    String liveListenEventid = LiveVideoConfig.LIVE_LISTEN;
    private AuditVideoAction mVideoAction;
    private RoomAction mRoomAction;
    private AuditClassAction auditClassAction;
    private LiveEnvironment liveEnvironment;
    private LiveHttpManager mHttpManager;
    private LiveVideoSAConfig liveVideoSAConfig;
    private LiveHttpResponseParser mHttpResponseParser;
    private IAuditIRCMessage mIRCMessage;
    private String courseId;
    private String mLiveId;
    private String mStuCouId;
    private String mCurrentDutyId;
    public final int mLiveType;
    private LiveGetInfo mGetInfo;
    private final LiveTopic mLiveTopic = new LiveTopic();
    private Handler mHandler = LiveMainHandler.getMainHandler();
    private LogToFile mLogtf;
    /** 主讲教师 */
    private Teacher mMainTeacher;
    /** 主讲教师名字 */
    private String mMainTeacherStr = null;
    private String mMainTeacherStatus = "on";
    /** 辅导教师 */
    private Teacher mCounteacher;
    /** 渠道前缀 */
    private final String CNANNEL_PREFIX = "x_";
    /** 主讲老师前缀 */
    public static final String TEACHER_PREFIX = "t_";
    /** 辅导老师前缀 */
    public static String COUNTTEACHER_PREFIX = "f_";
    private final String ROOM_MIDDLE = "L";
    private Callback.Cancelable mCataDataCancle;
    private Callback.Cancelable mGetPlayServerCancle, mGetStudentPlayServerCancle;
    /** 学习记录提交时间间隔 */
    private int mHbTime = 300, mHbCount = 0;
    private AtomicInteger mOpenCount = new AtomicInteger(0);
    /** 录播课的直播 */
    public final static int LIVE_TYPE_TUTORIAL = 1;
    /** 公开直播 */
    public final static int LIVE_TYPE_LECTURE = 2;
    /** 直播课的直播 */
    public final static int LIVE_TYPE_LIVE = 3;
    /** 用户心跳解析错误 */
    private int userOnlineError = 0;
    private PlayServerEntity mServer;
    private PlayServerEntity.PlayserverEntity playserverEntity;
    /** 网络类型 */
    private int netWorkType;
    /** 调度是不是在无网络下失败 */
    private boolean liveGetPlayServerError = false;
    /** 学生是不是流畅模式 */
    AtomicBoolean fluentMode = new AtomicBoolean(false);
    /** 是不是有分组 */
    private boolean haveTeam = false;
    /** 区分文理appid */
    String appID = UmsConstants.LIVE_APP_ID;
    /** 直播调度 */
    private LiveGetPlayServer liveGetPlayServer;
    private LiveLog liveLog;
    private boolean isHalfBodyLive = false;
    private boolean isBigLive = false;

    private int isFlatfish = 0;
    private LiveBusinessResponseParser mBigLiveHttpParser;
    /**notice 分发handler 用于消峰处理**/
    private Handler mDispathcHandler;
    private HandlerThread mNoticeDispatchHT;

    public AuditClassLiveBll(Context context, String vStuCourseID, String courseId, String vSectionID, int form) {
        super(context);
        this.mLiveId = vSectionID;
        this.mStuCouId = vStuCourseID;
        this.mLiveType = LIVE_TYPE_LIVE;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("courseId", courseId);
        mHttpManager.addBodyParam("stuCouId", vStuCourseID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        netWorkType = NetWorkHelper.getNetWorkState(context);
        mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        liveLog = new LiveLog(context, mLiveType, mLiveId, "AC");
        ProxUtil.getProxUtil().put(mContext, LiveOnLineLogs.class, liveLog);
        mLogtf = new LogToFile(context, TAG);
        isBigLive = mBaseActivity.getIntent().getBooleanExtra("isBigLive", false);
        if (isBigLive) {
            mHttpManager.addBodyParam("planId", vSectionID);
        } else {
            mHttpManager.addBodyParam("liveId", vSectionID);
        }
    }

    /**
     * 播放器数据初始化
     */
    public void getInfo() {
        mLogtf.d("getInfo:liveId=" + mLiveId);
        if (isBigLive) {
            getInfoBig();
        } else {
            getInfoOld();
        }
    }

    private void getInfoOld() {
        HttpCallBack callBack = new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getInfo:onPmSuccess" + responseEntity.getJsonObject());
                JSONObject object = (JSONObject) responseEntity.getJsonObject();
                mGetInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, mLiveType, LiveVideoBusinessConfig.ENTER_FROM_1);
                onGetInfoSuccess(mGetInfo);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getInfo:onPmFailure=" + msg);
                onLiveFailure("初始化失败", new Runnable() {

                    @Override
                    public void run() {
                        getInfoOld();
                    }
                });
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getInfo:onPmError=" + responseEntity.getErrorMsg());
                onLiveError(responseEntity);
            }
        };
        if (mLiveType == LIVE_TYPE_LIVE) {// 直播
            mHttpManager.liveGetInfo("", mLiveId, 1, callBack);
        }
    }

    private void getInfoBig() {
        if (mBigLiveHttpParser == null) {
            mBigLiveHttpParser = new LiveBusinessResponseParser();
        }
        int planId = Integer.parseInt(mLiveId);
        int iStuCouId = Integer.parseInt(mStuCouId);
        mHttpManager.bigLiveEnter(planId, LiveBusinessResponseParser.getBizIdFromLiveType(LiveVideoConfig.LIVE_TYPE_LIVE),
                iStuCouId, BigLiveCfg.BIGLIVE_CURRENT_ACCEPTPLANVERSION, new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        //LiveBusinessResponseParser mHttpResponseParser = new LiveBusinessResponseParser();
                        JSONObject object = (JSONObject) responseEntity.getJsonObject();
                        mGetInfo = mBigLiveHttpParser.parseLiveEnter(object, mLiveTopic, LiveVideoConfig.LIVE_TYPE_LIVE, 0);
                        if (mGetInfo == null) {
                            XESToastUtils.showToast("服务器异常");
                            mBaseActivity.finish();
                            return;
                        }
                        onGetInfoSuccess(mGetInfo);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast("初始化失败");
                        onLiveFailure("初始化失败", new Runnable() {

                            @Override
                            public void run() {
                                getInfoBig();
                            }
                        });
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        int status = responseEntity.getmStatus();
                        if (10 != status) {
                            XESToastUtils.showToast(responseEntity.getErrorMsg());
                            mBaseActivity.finish();
                        }
                    }
                });
    }


    public boolean isHalfBodyLive() {
        return isHalfBodyLive;
    }

    /**
     * 设置是否是半身直播
     *
     * @param halfBodyLive
     */
    public void setHalfBodyLive(boolean halfBodyLive) {
        this.isHalfBodyLive = halfBodyLive;
    }

    public void setVideoAction(AuditVideoAction videoAction) {
        this.mVideoAction = videoAction;
    }

    public void setAuditClassAction(AuditClassAction auditClassAction) {
        this.auditClassAction = auditClassAction;
    }

    public void setLiveEnvironment(LiveEnvironment liveEnvironment) {
        this.liveEnvironment = liveEnvironment;
    }

    private final AuditIRCCallback mIRCcallback = new AuditIRCCallback() {

        String lastTopicstr = "";

        @Override
        public void onStartConnect() {
            if (mRoomAction != null) {
                mRoomAction.onStartConnect();
            }
        }

        @Override
        public void onRegister() {
            mLogtf.d("onRegister");
            if (mRoomAction != null) {
                mRoomAction.onRegister();
            }
        }

        @Override
        public void onChannelInfo(String channel, int userCount, String topic) {
            mLogtf.i("onChannelInfo:userCount=" + userCount);
            onTopic(channel, topic, "", 0, true, channel);
        }

        @Override
        public void onTopic(String channel, String topicstr, String setBy, long date, boolean changed, String channelId) {
            if(isBigLive){
                dispatcherBigLiveTopic(channel,topicstr,setBy,date,changed,channelId);
            }else {
                dispatcherOldLiveTopic(channel,topicstr,setBy,date,changed,channelId);
            }
        }

        String lastNotice = "";
        String lastTopic = "";
        String lastChannel = "";
        String lastSetBy = "";
        long lastDate = 0;
        boolean lastChanged;
        String lastChannelId;

        /**
         * 是否还存在缓存notice
         * @return
         */
        private boolean hasCachNotice(){
            boolean hascachNotice =  mDispathcHandler != null &&  mDispathcHandler.hasMessages(MSG_TYPE_DISPATCH_NOTICE);
            // Log.e("ckTrac","===>LiveBll2:"+hascachNotice);
            return hascachNotice;
        }

        private void dispatcherBigLiveTopic(String channel, String topicstr, String setBy, long date, boolean changed, String channelId){
            if (lastTopicstr.equals(topicstr) && TextUtils.isEmpty(lastTopic)) {
                mLogtf.i(TAG+" onTopic(equals):topicstr=" + topicstr);
                return;
            }
            lastTopic = topicstr;
            lastChannel = channel;
            lastSetBy = setBy;
            lastDate = date;
            lastChanged = changed;
            lastChannelId = channelId;
            sendTopic(channel, topicstr, setBy, date, changed, channelId);
        }

        public void sendTopic(String channel, String topicstr, String setBy, long date, boolean changed,
                              String channelId) {
            logger.e(TAG+"======>onTopic_111:" + topicstr);
            if (hasCachNotice() || TextUtils.isEmpty(topicstr)) {
                return;
            }
            logger.e(TAG+"======>sendTopic_222:" + topicstr);
            lastTopicstr = topicstr;
            JSONTokener jsonTokener = null;
            try {
                if(!"TOPIC".equals(topicstr)){
                    jsonTokener = new JSONTokener(topicstr);
                    JSONObject jsonObject = new JSONObject(jsonTokener);
                    LiveTopic liveTopic = mBigLiveHttpParser.parseBigLiveTopic(mLiveTopic, jsonObject, mLiveType);
                    boolean teacherModeChanged = !mLiveTopic.getMode().equals(liveTopic.getMode());
                    mLogtf.d(SysLogLable.receivedMessageOfTopic,TAG+" bigLive sendTopic mLiveType : "+mLiveType+" ,teacherModeChanged: "+teacherModeChanged);
                    ////直播相关//////
                    //if (mLiveType == LIVE_TYPE_LIVE) {
                    //模式切换
                    onTeacherMode(liveTopic.getMode());
                    //}
                    //////////////
                    if (teacherModeChanged) {
                        mLiveTopic.setMode(liveTopic.getMode());
                        Loger.setDebug(true);
                        //  Loger.d("___channel: "+channel+"  mode: "+liveTopic.getMode()+"  topic:  "+topicstr);
                        //mGetInfo.setMode(liveTopic.getMode());
                    }

                    // mLiveTopic.copy(liveTopic);
                }
            } catch (Exception e) {
                try {
                    if (jsonTokener != null) {
                        mLogtf.e(TAG+" onTopic:token=" + jsonTokener, e);
                    } else {
                        mLogtf.e(TAG+" onTopic", e);
                    }
                } catch (Exception e2) {
                    mLogtf.e(TAG+" onTopic", e);
                }
            }
        }

        private void dispatcherOldLiveTopic(String channel, String topicstr, String setBy, long date, boolean changed, String channelId){
            if (lastTopicstr.equals(topicstr)) {
                mLogtf.i(TAG+" onTopic(equals):topicstr=" + topicstr);
                return;
            }

            logger.e(TAG+"======>onTopic:" + topicstr);
            if ( hasCachNotice()  || TextUtils.isEmpty(topicstr)) {
                return;
            }
            lastTopicstr = topicstr;
            JSONTokener jsonTokener = null;
            try {
                if(!"TOPIC".equals(topicstr)){
                    jsonTokener = new JSONTokener(topicstr);
                    JSONObject jsonObject = new JSONObject(jsonTokener);
                    LiveTopic liveTopic = mHttpResponseParser.parseLiveTopic(mLiveTopic, jsonObject, mLiveType);
                    boolean teacherModeChanged = !mLiveTopic.getMode().equals(liveTopic.getMode());
                    mLogtf.d(SysLogLable.receivedMessageOfTopic,TAG+" dispatcherOldLiveTopic mLiveType : "+mLiveType+" ,teacherModeChanged: "+teacherModeChanged);
                    ////直播相关//////
                    //if (mLiveType == LIVE_TYPE_LIVE) {
                    //模式切换
                    if (!(mLiveTopic.getMode().equals(liveTopic.getMode()))) {
                        onTeacherModeChange(liveTopic.getMode());
                    }
                    if (mVideoAction != null) {
                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())) {
                            if (mGetInfo.getStudentLiveInfo().isExpe()) {
                                mVideoAction.onTeacherNotPresent(true);
                            }
                        }
                    }
                    //}
                    //////////////
                    if (teacherModeChanged) {

                        mLiveTopic.setMode(liveTopic.getMode());
                        Loger.setDebug(true);
                        //  Loger.d("___channel: "+channel+"  mode: "+liveTopic.getMode()+"  topic:  "+topicstr);
                        //mGetInfo.setMode(liveTopic.getMode());
                    }
                    //mLiveTopic.copy(liveTopic);
                }
            } catch (Exception e) {
                try {
                    if (jsonTokener != null) {
                        mLogtf.e(TAG+" onTopic:token=" + jsonTokener, e);
                    } else {
                        mLogtf.e(TAG+" onTopic", e);
                    }
                } catch (Exception e2) {
                    mLogtf.e(TAG+" onTopic", e);
                }
            }
        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                             final String notice, String channelId) {
            try {
                final JSONObject object = new JSONObject(notice);
                final int mtype = object.getInt("type");
                mLogtf.d(SysLogLable.receivedMessageOfNotic,TAG+"onNotice isBigLive : "+isBigLive+" ,sourceNick: "+sourceNick+
                        " ,sourceLogin: "+sourceLogin+" ,sourceHostname: "+sourceHostname+" ,target: "+target+
                        " ,notice: "+notice+" ,channelId: "+channelId);
                if(isBigLive){
                    com.xueersi.lib.log.Loger.e(TAG, "=======>onNotice:" + mtype + ":" + object.toString());
                    dispatchNoitce(sourceNick, target, object, mtype);
                }else {
                    long seiTimetamp = object.optLong("vts", -1);
                    com.xueersi.lib.log.Loger.e(TAG, "=======>onNotice:" + mtype + ":" + this);
                    //分发消息
                    oldLiveDispatchNotice(sourceNick, target, object, mtype, seiTimetamp);
                }

            } catch (Exception e) {
                logger.e(TAG+"onNotice", e);
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        /**上一次收到notice的真实时间**/
        long lastNoticeTime = 0L;
        /**上一次notice 消峰延时时间**/
        long lastDelayTime = 0L;

        /**
         * 分发notice 指令
         * @param sourceNick
         * @param target
         * @param object
         * @param mtype
         * @throws JSONException
         */
        private void dispatchNoitce(final String sourceNick, final String target, final JSONObject object,
                                    final int mtype) throws JSONException {
            long timeDelay = getDelayTimeByType(mtype);
            long noticeTimeOffset = 0L;
            //计算2次notice 之间的时间差

            if(lastNoticeTime > 0){
                noticeTimeOffset = System.currentTimeMillis() - lastNoticeTime;
                noticeTimeOffset = noticeTimeOffset > 0 ? noticeTimeOffset:0;
            }
            lastNoticeTime = System.currentTimeMillis();

            // 消峰规则算出需要消峰
            if(timeDelay >= 0){
                //上一次延时时间
                if(lastDelayTime > 0){
                    //保证 延时消息有序
                    timeDelay = Math.max((lastDelayTime - noticeTimeOffset+100),timeDelay);
                    //(lastDelayTime - noticeTimeOffset) > 0 ? (lastDelayTime + timeDelay):timeDelay;
                }
                initDispathcHandler();
                lastDelayTime = timeDelay;
            }
            //
            //  Log.e("EliminationRule","====>BigLive_notice_timeDelay:type="+mtype+":"+timeDelay);
            Runnable dispatchNoticeTask = new Runnable() {

                @Override
                public void run() {
                    if (XESCODE.LIVE_BUSINESS_MODE_CHANGE == mtype) {
                        String mode = object.optString("mode");
                        onTeacherMode(mode);
                    }

                    if (UselessNotice.isUsed(mtype)) {
                        try {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("logtype", "onNotice");
                            hashMap.put("livetype", "" + mLiveType);
                            hashMap.put("liveid", "" + mLiveId);
                            hashMap.put("arts", "" + mGetInfo.getIsArts());
                            hashMap.put("pattern", "" + mGetInfo.getPattern());
                            hashMap.put("type", "" + mtype);
                            UmsAgentManager.umsAgentDebug(mContext, LogConfig.LIVE_NOTICE_UNKNOW, hashMap);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                }
            };

            if(mDispathcHandler != null && timeDelay > 0 ){
                Message message = Message.obtain(mDispathcHandler,dispatchNoticeTask);
                message.what = MSG_TYPE_DISPATCH_NOTICE;
                mDispathcHandler.sendMessageDelayed(message,timeDelay);
            }else{
                dispatchNoticeTask.run();
            }
        }

        /**
         * 老师模式变换
         * @param mode
         */
        private void onTeacherMode(String mode) {
            try {
                mLogtf.d(SysLogLable.switchLiveMode,TAG+" onTeacherMode oldMode : "+mLiveTopic.getMode()+" ,mode : "+mode);
                //com.xueersi.lib.log.Loger.e(TAG, "=======>onTeacherMode11111:");
                if (!(mLiveTopic.getMode().equals(mode))) {
                    //com.xueersi.lib.log.Loger.e(TAG, "=======>onTeacherMode2222:");
                    mLiveTopic.setMode(mode);
                    //mGetInfo.setMode(mode);
                    if (mVideoAction != null) {
                        mVideoAction.onModeChange(mode, true);
                        //Log.e(TAG, " onTeacherMode====>mVideoAction.onModeChange" + mLiveTopic.getMode() + ":" + mode);
                    }
                    liveGetPlayServer(true);
                    //更新学生信息
                    getStudentLiveInfo();
                }
            } catch (Exception e) {
                e.printStackTrace();
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        /**
         * 分发notice 消息
         * @param sourceNick
         * @param target
         * @param object
         * @param mtype
         * @param seiTimetamp
         */
        private void oldLiveDispatchNotice(final String sourceNick, final String target,
                                           final JSONObject object, final int mtype, final long seiTimetamp) {
            //////////////////////
            long timeDelay = oldLiveGetTimeByType(mtype);
            long noticeTimeOffset = 0L;
            //计算2次notice 之间的时间差
            if(lastNoticeTime > 0){
                noticeTimeOffset = System.currentTimeMillis() - lastNoticeTime;
                noticeTimeOffset = noticeTimeOffset > 0 ? noticeTimeOffset:0;
            }
            lastNoticeTime = System.currentTimeMillis();

            // 消峰规则算出需要消峰
            if(timeDelay >= 0){
                //上一次延时时间
                if(lastDelayTime > 0){
                    //保证 延时消息有序
                    timeDelay = Math.max((lastDelayTime-noticeTimeOffset+100),timeDelay);//(lastDelayTime - noticeTimeOffset) > 0 ? (lastDelayTime + timeDelay):timeDelay;
                }
                initDispathcHandler();
                lastDelayTime = timeDelay;
            }
            // Log.e("EliminationRule","====>OldLive_notice_timeDelay:type="+mtype+":"+timeDelay);

            Runnable dispatchTask = new Runnable() {
                @Override
                public void run() {
                    // Log.e("EliminationRule","====>OldLive_notice_realdispatch_notice_1:type="+mtype);

                    if(XESCODE.MODECHANGE == mtype){
                        // Log.e("EliminationRule","====>OldLive_notice_realdispatch_notice_2:type="+mtype);
                        String mode = object.optString("mode");
                        onTeacherModeChange(mode);
                    }

                    if (UselessNotice.isUsed(mtype)) {
                        try {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("logtype", "onNotice");
                            hashMap.put("livetype", "" + mLiveType);
                            hashMap.put("liveid", "" + mLiveId);
                            hashMap.put("arts", "" + mGetInfo.getIsArts());
                            hashMap.put("pattern", "" + mGetInfo.getPattern());
                            hashMap.put("type", "" + mtype);
                            UmsAgentManager.umsAgentDebug(mContext, LogConfig.LIVE_NOTICE_UNKNOW, hashMap);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                }
            };

            if(timeDelay > 0 && mDispathcHandler != null){
                Message message = Message.obtain(mDispathcHandler,dispatchTask);
                message.what = MSG_TYPE_DISPATCH_NOTICE;
                mDispathcHandler.sendMessageDelayed(message,timeDelay);
            }else{
                dispatchTask.run();
            }
        }

        /**
         * 老直播间 获取消峰时间控制
         * @param mtype
         * @return
         */
        private long oldLiveGetTimeByType(int mtype){
            long reuslt = -1L;
            if(mGetInfo != null && mGetInfo.getIsEliminationPeak() == 1){
                reuslt = getDelayTimeByType(mtype);
            }
            return reuslt;
        }

        /**
         * 大班获取延时时间
         * @param
         * @return
         */
        private long getDelayTimeByType(int mtype) {
            //默认值-1区分是否有消峰配置
            long delayTime = -1L;
            if(mGetInfo != null && mGetInfo.getEliminationMap() != null && mGetInfo.getEliminationMap().size() > 0){
                Long time = mGetInfo.getEliminationMap().get(mtype);
                if(time != null){
                    Random random = new Random();
                    delayTime = (long) (Math.random() * time);
                }
            }
            // Log.e("ckTrac","======>LiveBll2:getDelayTimeByType="+delayTime);
            return delayTime;
        }

        /**
         * 初始化 分发handler
         */
        private void initDispathcHandler() {
            if(mDispathcHandler == null){
                if(mNoticeDispatchHT != null){
                    mNoticeDispatchHT.quit();
                }
                mNoticeDispatchHT = new HandlerThread("AuditClassLiveBll_Notice_Dispatch");
                mNoticeDispatchHT.start();
                mDispathcHandler = new Handler(mNoticeDispatchHT.getLooper());
            }
        }

        private void onTeacherModeChange(String mode){
            //String mode = object.optString("mode");
            String oldMode = mLiveTopic.getMode();
            mLogtf.d(SysLogLable.switchLiveMode,TAG+" onTeacherModeChange oldMode : "+oldMode+" ,mode: "+mode);
            if (!oldMode.equals(mode)) {
                mLiveTopic.setMode(mode);
                //mGetInfo.setMode(mode);
                if (mVideoAction != null) {
                    mVideoAction.onModeChange(mode, true);
                    //Log.e(TAG, " onTeacherModeChange====>mVideoAction.onModeChange" + oldMode + ":" + mode);
                }
                liveGetPlayServer(true);
                //更新学生信息
                getStudentLiveInfo();
            }
        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {
            if (mRoomAction != null) {
                mRoomAction.onMessage(target, sender, login, hostname, text, "");
            }
        }

        @Override
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                     String message) {

        }

        @Override
        public void onStudentLeave(boolean leave, String stuPushStatus) {
            if (mVideoAction != null) {
                mVideoAction.onStudentLeave(leave, stuPushStatus);
            }
        }

        @Override
        public void onStudentError(String status, String msg) {
            if ("fluentMode".equals(status)) {
                fluentMode.set(true);
            }
            if (mVideoAction != null) {
                mVideoAction.onStudentError(status, msg);
            }
        }

        @Override
        public void onStudentPrivateMessage(String sender, String login, String hostname, String target, String message) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                int type = jsonObject.getInt("type");
                switch (type) {
                    case XESCODE.STUDENT_REPLAY: {
                        String playUrl = jsonObject.optString("playUrl");
                        mMainTeacherStatus = jsonObject.optString("mainTeacher");
                        String coachTeacher = jsonObject.optString("coachTeacher");
                        if (coachTeacher.equals("off")) {
                            mCounteacher.isLeave = true;
                        } else {
                            mCounteacher.isLeave = false;
                        }
                        String mode = jsonObject.optString("mode", LiveTopic.MODE_CLASS);
                        mLogtf.d("STUDENT_REPLAY:mode=" + mode + ",oldMode=" + mLiveTopic.getMode());
                        if (!(mLiveTopic.getMode().equals(mode))) {
                            if (mVideoAction != null) {
                                mVideoAction.onModeChange(mode, true);
                            }
                            mLiveTopic.setMode(mode);
                            liveGetPlayServer(true);
                        }
                        if (mVideoAction != null) {
                            mVideoAction.onStudentLiveUrl(playUrl);
                        }
                        mLogtf.d("onStudentPrivateMessage:playUrl=" + playUrl);

                        //旁听日志
                        StableLogHashMap logHashMap = new StableLogHashMap("startPlay");
                        logHashMap.put("nickname", sender);
                        logHashMap.put("playurl", playUrl);
                        String nonce = jsonObject.optString("nonce");
                        logHashMap.addSno("4").addNonce(nonce).addExY().addStable("1");
                        umsAgentDebugSys(liveListenEventid, logHashMap.getData());
                    }
                    break;
                    case XESCODE.STUDENT_MODECHANGE: {
                        String mode = jsonObject.optString("mode", LiveTopic.MODE_CLASS);
                        mLogtf.d("STUDENT_MODECHANGE:mode=" + mode + ",oldMode=" + mLiveTopic.getMode());
                        if (!(mLiveTopic.getMode().equals(mode))) {
                            if (mVideoAction != null) {
                                mVideoAction.onModeChange(mode, true);
                            }
                            mLiveTopic.setMode(mode);
                            liveGetPlayServer(true);
                        }
                    }
                    break;
                    case XESCODE.STUDENT_UPDATE: {
                        getStudentLiveInfo();
                    }
                    break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnect(IRCConnection connection, boolean isQuitting) {
            mLogtf.d("onDisconnect:isQuitting=" + isQuitting);
            if (mRoomAction != null) {
                mRoomAction.onDisconnect();
            }
        }

        @Override
        public void onConnect(IRCConnection connection) {
            if (mRoomAction != null) {
                mRoomAction.onConnect();
            }
        }

        @Override
        public void onUserList(String channel, User[] users) {
            String s = "onUserList:channel=" + channel + ",users=" + users.length;
            boolean haveMainTeacher = false;//主讲老师
            boolean haveCounteacher = false;//辅导老师
            ArrayList<User> arrayList = new ArrayList<>();
            for (int i = 0; i < users.length; i++) {
                User user = users[i];
                String _nick = user.getNick();
                if (_nick != null && _nick.length() > 2) {
                    if (_nick.startsWith(TEACHER_PREFIX)) {
                        s += ",mainTeacher=" + _nick;
                        haveMainTeacher = true;
                        synchronized (mIRCcallback) {
                            mMainTeacher = new Teacher(_nick);
                            mMainTeacherStr = _nick;
                        }
                        if (LiveTopic.MODE_CLASS.endsWith(mLiveTopic.getMode())
                                && mVideoAction != null) {
                            mVideoAction.onTeacherQuit(false);
                        }
                    } else if (_nick.startsWith(COUNTTEACHER_PREFIX)) {
                        haveCounteacher = true;
                        mCounteacher.isLeave = false;
                        s += ",counteacher=" + _nick;
                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())
                                && mVideoAction != null) {
                            mVideoAction.onTeacherQuit(false);
                        }
                    } else {
                        boolean isMyTeam = isMyTeam(user.getNick());
                        if (isMyTeam) {
                            arrayList.add(user);
                        }
                    }
                } else {
                    s += ",else=" + _nick;
                }
            }
            if (!haveCounteacher) {
                mCounteacher.isLeave = true;
            }
            if (arrayList.isEmpty()) {// 学生人数为空
                s += ",arrayList=isSpace";
            }
            s += ",haveMainTeacher=" + haveMainTeacher;
            if (mLiveType == LIVE_TYPE_LIVE) {
                s += ",haveCounteacher=" + haveCounteacher;
            }
            mLogtf.d(s);
            if (mRoomAction != null) {
                User[] users2 = new User[arrayList.size()];
                arrayList.toArray(users2);
                mRoomAction.onUserList(channel, users2);
            }
        }

        /**是不是自己组的人*/
        private boolean isMyTeam(String sender) {
            boolean isMyTeam = true;
            ArrayList<String> teamStuIds = mGetInfo.getTeamStuIds();
            if (mLiveType == AuditClassLiveBll.LIVE_TYPE_LIVE && !teamStuIds.isEmpty()) {
                isMyTeam = false;
                String split[] = sender.split("_");
                if (split.length > 4) {
                    String uid = split[3];
                    for (int j = 0; j < teamStuIds.size(); j++) {
                        String string = teamStuIds.get(j);
                        if (("" + string).equals(uid)) {
                            isMyTeam = true;
                            break;
                        }
                    }
                }
            }
            return isMyTeam;
        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {
            logger.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
            if (sender.startsWith(TEACHER_PREFIX)) {
                synchronized (mIRCcallback) {
                    mMainTeacher = new Teacher(sender);
                    mMainTeacherStr = sender;
                }
                mLogtf.d("onJoin:mainTeacher:target=" + target + ",mode=" + mLiveTopic.getMode());
                if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(false);
                }
            } else if (sender.startsWith(COUNTTEACHER_PREFIX)) {
                mCounteacher.isLeave = false;
                mLogtf.d("onJoin:Counteacher:target=" + target + ",mode=" + mLiveTopic.getMode());
                if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(false);
                }
            } else {
                if (mRoomAction != null) {
//                    if (sender.startsWith(LiveBll.TEACHER_PREFIX) || sender.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//                        //老师不计算在内
//                        return;
//                    }
                    boolean isMyTeam = isMyTeam(sender);
                    if (isMyTeam) {
                        mRoomAction.onJoin(target, sender, login, hostname);
                    }
                }
            }
        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel) {
            logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                    + sourceHostname + ",reason=" + reason);
            if (sourceNick.startsWith(TEACHER_PREFIX)) {
                synchronized (mIRCcallback) {
                    mMainTeacher = null;
                }
                mLogtf.d("onQuit:mainTeacher quit");
                if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(true);
                }
            } else if (sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
                mCounteacher.isLeave = true;
                mLogtf.d("onQuit:Counteacher quit");
                if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(true);
                }
            } else {
                if (mRoomAction != null) {
//                    if (sourceNick.startsWith(LiveBll.TEACHER_PREFIX) || sourceNick.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//                        //老师不计算在内
//                        return;
//                    }
                    boolean isMyTeam = isMyTeam(sourceNick);
                    if (isMyTeam) {
                        mRoomAction.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
                    }
                }
            }
        }

        @Override
        public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
                           String recipientNick, String reason) {
            mLogtf.d("onKick:target=" + target + ",kickerNick=" + kickerNick + ",kickerLogin=" + kickerLogin
                    + ",kickerHostname=" + kickerHostname + ",reason=" + reason);
            if (mRoomAction != null) {
                mRoomAction.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
            }
        }

        @Override
        public void onUnknown(String line) {
        }
    };

    /** 当前状态，老师是不是在直播间 */
    public boolean isPresent() {
        return isPresent(mLiveTopic.getMode());
    }

    /**
     * 当前状态
     *
     * @param mode 模式
     */
    private boolean isPresent(String mode) {
        boolean isPresent = true;
//        if (mIRCMessage != null && mIRCMessage.isConnected()) {
//            if (LiveTopic.MODE_CLASS.endsWith(mode)) {
//                isPresent = mMainTeacherStatus.equals("on");
//            } else {
//                isPresent = !mCounteacher.isLeave;
//            }
//        }
        return isPresent;
    }

    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    /**
     * 请求房间状态成功
     */
    private void onGetInfoSuccess(LiveGetInfo mGetInfo) {

        if (mGetInfo == null) {
            onLiveFailure("服务器异常", null);
            return;
        }
        //isFlatfish = mGetInfo.getIsFlatfish();
        if (isBigLive) {
            if (mHttpManager != null) {
                mHttpManager.addHeaderParams("switch-grade", mGetInfo.getGrade() + "");
                String subjectId = (mGetInfo.getSubjectIds() != null && mGetInfo.getSubjectIds().length > 0) ?
                        mGetInfo.getSubjectIds()[0] : "";
                mHttpManager.addHeaderParams("switch-subject", subjectId);
                mHttpManager.addHeaderParams("planId", mGetInfo.getId());
                mHttpManager.addHeaderParams("bizId", mLiveType + "");
                mHttpManager.addHeaderParams("SESSIONID", AppBll.getInstance().getLiveSessionId());
                //Log.e("ckTrac","====>LiveBll2_initBigLiveRoom:"+ AppBll.getInstance().getLiveSessionId());
                String classId = mGetInfo.getStudentLiveInfo() != null ? mGetInfo.getStudentLiveInfo().getClassId() : "0";
                int iClassId = 0;
                try {
                    iClassId = Integer.parseInt(classId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String strStuCouId = TextUtils.isEmpty(mStuCouId) ? "" : mStuCouId;
                mHttpManager.addBusinessParams("stuCouId", strStuCouId);
                mHttpManager.addBusinessParams("classId", iClassId);
                mHttpManager.addBusinessParams("isPlayback", 0);
            }
        }
        liveLog.setGetInfo(mGetInfo);
       /* if (isChineseHalfBodyLive(mGetInfo)) {
            ResponseEntity responseEntity = new ResponseEntity();
            responseEntity.setErrorMsg("语文半身直播旁听暂不支持，程序员哥哥正在夜以继日的开发哦!");
            onLiveError(responseEntity);
            return;
        }*/

        if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
            ResponseEntity responseEntity = new ResponseEntity();
            responseEntity.setErrorMsg("家长旁听暂不支持全身直播，程序员哥哥正在夜以继日的开发哦!");
            onLiveError(responseEntity);
            return;
        }
        if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_EN) {
            appID = UmsConstants.ARTS_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH) {
            appID = UmsConstants.LIVE_CN_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST);
        } else {
            appID = UmsConstants.LIVE_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        if (mGetInfo.getStat() == 1) {
            if (mVideoAction != null) {
                mVideoAction.onTeacherNotPresent(true);
            }
            mLogtf.d("onGetInfoSuccess:onTeacherNotPresent");
        }
        mCounteacher = new Teacher(mGetInfo.getTeacherName());
        String s = "onGetInfoSuccess:stat=" + mGetInfo.getStat();
        if (mVideoAction != null) {
            mVideoAction.onLiveInit(mGetInfo);
        }
        String channel = "";
        if (mLiveType == LIVE_TYPE_TUTORIAL) {
            channel = "1" + ROOM_MIDDLE + mGetInfo.getId();
        } else if (mLiveType == LIVE_TYPE_LECTURE) {
            channel = "2" + ROOM_MIDDLE + mGetInfo.getId();
        } else {
            StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
            courseId = studentLiveInfo.getCourseId();
            if (!StringUtils.isEmpty(studentLiveInfo.getTeamId()) && !"0".equals(studentLiveInfo.getTeamId())) {
                haveTeam = true;
            }
            channel = mGetInfo.getId() + "-" + studentLiveInfo.getClassId();
        }
        s += ",liveType=" + mLiveType + ",channel=" + channel;
        String nickname;
        if (isBigLive) {
            nickname = mGetInfo.getIrcNick().replace("s_","");
        } else {
            nickname = mGetInfo.getLiveType() + "_"
                    + mGetInfo.getId() + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
        }
        mIRCMessage = new NewAuditIRCMessage(liveEnvironment, nickname, mGetInfo.getId(), mGetInfo.getStudentLiveInfo().getClassId(), mGetInfo.getLiveTypeId(),channel);
        ((NewAuditIRCMessage) mIRCMessage).setLiveAndBackDebug(this);
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();
        // logger.d( s);
        mLogtf.d(s);
        mLogtf.d("onGetInfoSuccess:mode=" + mLiveTopic.getMode());
        liveGetPlayServerFirst();
    }

    /**
     * 是否是 语文半身直播
     *
     * @return
     */
    private boolean isChineseHalfBodyLive(LiveGetInfo liveGetInfo) {
        return liveGetInfo != null && liveGetInfo.getPattern()
                == LiveVideoConfig.LIVE_TYPE_HALFBODY
                && liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH;
    }


    /**
     * 签名
     */
    public synchronized void getStudentLiveInfo() {
        if (isBigLive) {
            getBigLiveStudentLiveInfo();
        } else {
            if (isHalfBodyLive) {
                getHalfBodyLiveStudentLiveInfo();
            } else {
                getNorLiveStudentLiveInfo();
            }
        }
    }

    /**
     * 普通直播 获取旁听数据
     */
    private void getNorLiveStudentLiveInfo() {
        mHttpManager.getStudentLiveInfo(mLiveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                String oldMode = mLiveTopic.getMode();
                StudyInfo studyInfo = mHttpResponseParser.parseStudyInfo(responseEntity, oldMode);
                if (auditClassAction != null) {
                    auditClassAction.onGetStudyInfo(studyInfo);
                }
                String mode = studyInfo.getMode();
                mLogtf.d(TAG+" getStudentLiveInfo:onPmSuccess:" + responseEntity.getJsonObject() + ",mode=" + oldMode + "," + mode);
                if (!oldMode.equals(mode)) {
                    if (mVideoAction != null) {
                        mVideoAction.onModeChange(mode, true);
                    }
                    mLiveTopic.setMode(mode);
                    liveGetPlayServer(true);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.e("getStudentLiveInfo:onPmFailure:msg=" + msg, error);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.e("getStudentLiveInfo:onPmError:errorMsg=" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 大班直播 获取旁听数据
     */
    private void getBigLiveStudentLiveInfo() {
        StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        mHttpManager.getBigStudentLiveInfo(mStuCouId, mLiveId, studentLiveInfo.getClassId(), studentLiveInfo.getTeamId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                String oldMode = mLiveTopic.getMode();
                StudyInfo studyInfo = mHttpResponseParser.parseStudyInfo(responseEntity, oldMode);
                if (auditClassAction != null) {
                    auditClassAction.onGetStudyInfo(studyInfo);
                }
                String mode = studyInfo.getMode();
                mLogtf.d(TAG+" getBigLiveStudentLiveInfo:onPmSuccess:" + responseEntity.getJsonObject() + ",mode=" + oldMode + "," + mode);
                if (!oldMode.equals(mode)) {
                    if (mVideoAction != null) {
                        mVideoAction.onModeChange(mode, true);
                    }
                    mLiveTopic.setMode(mode);
                    liveGetPlayServer(true);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.e("getBigLiveStudentLiveInfo:onPmFailure:msg=" + msg, error);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.e("getBigLiveStudentLiveInfo:onPmError:errorMsg=" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取半身直播  旁听数据
     */
    public synchronized void getHalfBodyLiveStudentLiveInfo() {

        mHttpManager.getHalfBodyStuLiveInfo(mLiveId, mStuCouId, mGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH,
                new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        String oldMode = mLiveTopic.getMode();
                        HalfBodyLiveStudyInfo stuLiveInfo = mHttpResponseParser.parseStuHalfbodyLiveInfo(responseEntity, oldMode);
                        if (auditClassAction != null) {
                            auditClassAction.onGetStudyInfo(stuLiveInfo);
                        }

                        String mode = stuLiveInfo.getMode();
                        Log.e(TAG, "getHalfBodyStuLiveInfo=====>oldMode:" + oldMode + ":" + mode);
                        if (!oldMode.equals(mode)) {
                            if (mVideoAction != null) {
                                mVideoAction.onModeChange(mode, true);
                                Log.e("parseLiveInfo", " getHalfBodyStuLiveInfo====>mVideoAction.onModeChange" + oldMode + ":" + mode);
                            }
                            mLiveTopic.setMode(mode);
                            liveGetPlayServer(true);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        logger.e("getHalfBodyLiveStudentLiveInfo:onPmFailure:msg=" + msg, error);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.e("getHalfBodyLiveStudentLiveInfo:onPmError:errorMsg=" + responseEntity.getErrorMsg());
                    }
                });
    }


    /** 第一次调度，不判断老师状态 */
    public void liveGetPlayServerFirst() {
        liveGetPlayServer = new LiveGetPlayServer((Activity) mContext, new TeacherIsPresent() {

            @Override
            public boolean isPresent() {
                return AuditClassLiveBll.this.isPresent();
            }
        }, mLiveType, mGetInfo, mLiveTopic) {

            @Override
            public void liveGetPlayServer(String mode, boolean modechange) {
                logger.d("liveGetPlayServer:fluentMode=" + fluentMode.get());
                if (fluentMode.get()) {
                    return;
                }
                super.liveGetPlayServer(mode, modechange);
            }
        };
        liveGetPlayServer.setVideoAction(mVideoAction);
        liveGetPlayServer(mLiveTopic.getMode(), false);
    }

    /**
     * 调度，使用LiveTopic的mode
     *
     * @param modechange
     */
    public void liveGetPlayServer(boolean modechange) {
        new Thread() {
            @Override
            public void run() {
                boolean isPresent = isPresent(mLiveTopic.getMode());
                mLogtf.d("liveGetPlayServer:isPresent=" + isPresent);
                if (!isPresent && mVideoAction != null) {
                    mVideoAction.onTeacherNotPresent(true);
                }
            }
        }.start();
        liveGetPlayServer(mLiveTopic.getMode(), modechange);
    }

//    private long lastGetPlayServer;

    private void liveGetPlayServer(final String mode, final boolean modechange) {
        if (fluentMode.get()) {
            liveGetPlayServer.onDestroy();
            return;
        }
        liveGetPlayServer.liveGetPlayServer(mode, modechange);
//        if (netWorkType == NetWorkHelper.NO_NETWORK) {
//            liveGetPlayServerError = true;
//            return;
//        }
//        liveGetPlayServerError = false;
//        final long before = SystemClock.elapsedRealtime();
//        // http://gslb.xueersi.com/xueersi_gslb/live?cmd=live_get_playserver&userid=000041&username=xxxxxx
//        // &channelname=88&remote_ip=116.76.97.244
//        if (LiveTopic.MODE_CLASS.equals(mode)) {
//            String channelname = "";
//            if (mLiveType != 3) {
//                channelname = CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId() + "_"
//                        + mGetInfo.getTeacherId();
//            } else {
//                channelname = CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId();
//            }
//            mGetInfo.setChannelname(channelname);
//        } else {
//            mGetInfo.setChannelname(CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId() + "_"
//                    + mGetInfo.getTeacherId());
//        }
//        if (livePlayLog != null) {
//            livePlayLog.setChannelname(mGetInfo.getChannelname());
//        }
//        final String serverurl = mGetInfo.getGslbServerUrl() + "?cmd=live_get_playserver&userid=" + mGetInfo.getStuId()
//                + "&username=" + mGetInfo.getUname() + "&channelname=" + mGetInfo.getChannelname();
//        mLogtf.d("liveGetPlayServer:serverurl=" + serverurl);
//        if (mGetPlayServerCancle != null) {
//            mGetPlayServerCancle.cancel();
//            mGetPlayServerCancle = null;
//        }
//        mLogtf.d("liveGetPlayServer:modeTeacher=" + getModeTeacher());
//        final URLDNS urldns = new URLDNS();
//        mGetPlayServerCancle = mHttpManager.liveGetPlayServer(urldns, serverurl, new CommonRequestCallBack<String>() {
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                mLogtf.d("liveGetPlayServer:onError:ex=" + ex + ",isOnCallback=" + isOnCallback);
//                long time = SystemClock.elapsedRealtime() - before;
//                if (ex instanceof HttpException) {
//                    HttpException error = (HttpException) ex;
//                    if (error.getCode() >= 300) {
//                        livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode20, 20, "", urldns, serverurl);
//                        mLogtf.d("liveGetPlayServer:onError:code=" + error.getCode() + ",time=" + time);
//                        if (time < 15000) {
//                            if (mVideoAction != null && mLiveTopic != null) {
//                                mVideoAction.onLiveStart(null, mLiveTopic, modechange);
//                            }
//                            return;
//                        }
//                    }
//                } else {
//                    if (ex instanceof UnknownHostException) {
//                        livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode10, 10, "", urldns, serverurl);
//                    } else {
//                        if (ex instanceof SocketTimeoutException) {
//                            livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode15, PlayFailCode.TIME_OUT, "", urldns, serverurl);
//                        }
//                    }
//                    mLogtf.e("liveGetPlayServer:onError:isOnCallback=" + isOnCallback, ex);
//                }
//                long now = System.currentTimeMillis();
//                if (now - lastGetPlayServer < 5000) {
//                    postDelayedIfNotFinish(new Runnable() {
//                        @Override
//                        public void run() {
//                            mLogtf.d("liveGetPlayServer:onError retry1");
//                            liveGetPlayServer(modechange);
//                        }
//                    }, 1000);
//                } else {
//                    lastGetPlayServer = now;
//                    onLiveFailure("直播调度失败", new Runnable() {
//                        @Override
//                        public void run() {
//                            mLogtf.d("liveGetPlayServer:onError retry2");
//                            liveGetPlayServer(modechange);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onSuccess(String result) {
////                logger.i( "liveGetPlayServer:onSuccess:result=" + result);
//                String s = "liveGetPlayServer:onSuccess";
//                try {
//                    JSONObject object = new JSONObject(result);
//                    PlayServerEntity server = mHttpResponseParser.parsePlayerServer(object);
//                    if (server != null) {
//                        if (livePlayLog != null) {
//                            long time = SystemClock.elapsedRealtime() - before;
//                            livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode0, 0, server.getCipdispatch(), urldns, serverurl);
//                        }
//                        s += ",mode=" + mode + ",server=" + server.getAppname() + ",rtmpkey=" + server.getRtmpkey();
//                        if (LiveTopic.MODE_CLASS.equals(mode)) {
//                            mGetInfo.setSkeyPlayT(server.getRtmpkey());
//                        } else {
//                            mGetInfo.setSkeyPlayF(server.getRtmpkey());
//                        }
//                        mServer = server;
//                        if (mVideoAction != null && mLiveTopic != null) {
//                            mVideoAction.onLiveStart(server, mLiveTopic, modechange);
//                        }
//                    } else {
//                        s += ",server=null";
//                        onLiveFailure("直播调度失败", new Runnable() {
//
//                            @Override
//                            public void run() {
//                                liveGetPlayServer(modechange);
//                            }
//                        });
//                    }
//                    mLogtf.d(s);
//                } catch (JSONException e) {
//                    MobAgent.httpResponseParserError(TAG, "liveGetPlayServer", result + "," + e.getMessage());
//                    // logger.e( "liveGetPlayServer", e);
//                    mLogtf.e("liveGetPlayServer", e);
//                    onLiveFailure("直播调度失败", new Runnable() {
//
//                        @Override
//                        public void run() {
//                            liveGetPlayServer(modechange);
//                        }
//                    });
//                }
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//            }
//
//        });
    }

    public void startVideo() {
        if (mIRCMessage != null) {
            mIRCMessage.startVideo();
        }
    }

    private long lastGetStudentPlayServer;


    /**
     * activity退出
     */
    public void onDestroy() {
        mVideoAction = null;
        mRoomAction = null;
        if (mCataDataCancle != null) {
            mCataDataCancle.cancel();
            mCataDataCancle = null;
        }
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
        if (mIRCMessage != null) {
            mIRCMessage.setCallback(null);
            mIRCMessage.destory();
        }
        if (liveGetPlayServer != null) {
            liveGetPlayServer.onDestroy();
        }
    }

    private void onLiveFailure(String msg, Runnable runnable) {
        if (runnable == null) {
            showToast(msg);
        } else {
            showToast(msg + "，稍后重试");
            postDelayedIfNotFinish(runnable, 1000);
        }
    }

    private void onLiveError(ResponseEntity responseEntity) {
        if (mVideoAction != null) {
            mVideoAction.onLiveError(responseEntity);
        }
    }

    private SimpleVPlayerListener mVideoListener = new SimpleVPlayerListener() {
        long openStartTime;
        long bufferStartTime;
        boolean isOpenSuccess = false;

        @Override
        public void onOpenStart() {
            isOpenSuccess = false;
            mOpenCount.set(mOpenCount.get() + 1);
            openStartTime = System.currentTimeMillis();
            mLogtf.d("onOpenStart");
        }

        @Override
        public void onOpenSuccess() {
            isOpenSuccess = true;
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenSuccess:openTime=" + openTime);
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenFailed:openTime=" + openTime + "," + getModeTeacher()
                    + ",NetWorkState=" +
                    NetWorkHelper.getNetWorkState(mContext));
        }

        @Override
        public void onBufferStart() {
            bufferStartTime = System.currentTimeMillis();
            mLogtf.d("onBufferStart:ModeTeacher=" + getModeTeacher() + ",NetWorkState=" +
                    NetWorkHelper.getNetWorkState(mContext));
        }

        @Override
        public void onBufferComplete() {
            long bufferTime = System.currentTimeMillis() - bufferStartTime;
            mLogtf.d("onBufferComplete:bufferTime=" + bufferTime);
        }

        @Override
        public void onPlaybackComplete() {
            mLogtf.d("onPlaybackComplete:ModeTeacher=" + getModeTeacher() + "," +
                    "NetWorkState=" + NetWorkHelper.getNetWorkState(mContext));
        }
    };

    public boolean isDisable() {
        return mLiveTopic.isDisable();
    }

    public boolean isConnected() {
        if (mIRCMessage == null) {
            return false;
        }
        return mIRCMessage.isConnected();
    }

    /**
     * 直播修复
     *
     * @param isbuffer true是缓冲超时，false是视频打开超时
     */
    public void repair(boolean isbuffer) {

    }

    /** 得到老师名字 */
    public String getModeTeacher() {
        String mainnick = "null";
        synchronized (mIRCcallback) {
            if (mMainTeacher != null) {
                mainnick = mMainTeacher.get_nick();
            }
        }
        if (mCounteacher == null) {
            return "mode=" + getMode() + ",mainnick=" + mainnick + ",coun=null";
        } else {
            return "mode=" + getMode() + ",mainnick=" + mainnick + ",coun.isLeave=" + mCounteacher.isLeave;
        }
    }

    /** 得到当前模式 */
    public String getMode() {
        String mode;
        if (mLiveType == LIVE_TYPE_LIVE) {
            if (mLiveTopic == null) {
                mode = LiveTopic.MODE_CLASS;
            } else {
                mode = mLiveTopic.getMode();
            }
        } else {
            mode = LiveTopic.MODE_CLASS;
        }
        return mode;
    }

    public String getNickname() {
        return mIRCMessage.getNickname();
    }

    static HashMap<String, String> channelAndRoomid = new HashMap();

    public void getToken(final LicodeToken licodeToken) {
        final String id = "x_" + mLiveType + "_" + mGetInfo.getId();
        String roomid = channelAndRoomid.get(id);
        logger.i("getToken:id=" + id + ",roomid=null?" + (roomid == null));
        if (roomid != null) {
            mHttpManager.getToken(roomid, mGetInfo.getStuId(), new Callback.CacheCallback<String>() {

                @Override
                public boolean onCache(String result) {
                    return false;
                }

                @Override
                public void onSuccess(String result) {
                    licodeToken.onToken(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    licodeToken.onError(ex);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            return;
        }
        String url = "https://test-rtc.xesimg.com:12443/room?id=" + id
                + "&userId=" + mGetInfo.getStuId() + "&name=" + mGetInfo.getStuName() + "&role=presenter&url=" + mGetInfo.getStuImg();
        mHttpManager.getRoomid(url, new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                int roomidIndex = result.indexOf("roomId");
                if (roomidIndex != -1) {
                    result = result.substring(roomidIndex + 8);
                }
                roomidIndex = result.indexOf("\"");
                if (roomidIndex != -1) {
                    result = result.substring(0, roomidIndex);
                }
                logger.i("getToken:getRoomid:onSuccess:result=" + result);
                channelAndRoomid.put(id, result);
                mHttpManager.getToken(result, mGetInfo.getStuId(), new CacheCallback<String>() {

                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }

                    @Override
                    public void onSuccess(String result) {
                        licodeToken.onToken(result);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        licodeToken.onError(ex);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                logger.e("getToken:getRoomid:onError", ex);
                licodeToken.onError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            logger.i("onNetWorkChange:liveGetPlayServerError=" + liveGetPlayServerError);
            if (liveGetPlayServer != null) {
                liveGetPlayServer.onNetWorkChange(netWorkType);
            }
//            if (liveGetPlayServerError) {
//                liveGetPlayServerError = false;
//                liveGetPlayServer(mLiveTopic.getMode(), false);
//            }
        }
        if (mIRCMessage != null) {
            mIRCMessage.onNetWorkChange(netWorkType);
        }
    }

    /**
     * 弹出toast，判断Video是不是在活动
     *
     * @param text
     */
    public void showToast(String text) {
        ActivityStatic activityStatic = (ActivityStatic) mContext;
        if (activityStatic.isResume()) {
            XESToastUtils.showToast(mContext, text);
        }
    }

    /**
     * 接口失败，重新请求，判断video是不是存活
     *
     * @param r           重新请求的事件
     * @param delayMillis
     */
    private void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        ActivityStatic activityStatic = (ActivityStatic) mContext;
        if (activityStatic.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public SimpleVPlayerListener getVideoListener() {
        return mVideoListener;
    }

    public void setPlayserverEntity(PlayServerEntity.PlayserverEntity playserverEntity) {
        this.playserverEntity = playserverEntity;
    }

    public enum MegId {
        MEGID_12102("12102", "startplay"), MEGID_12103("12103", "fail"),
        MEGID_12107("12107", "bufreconnect"), MEGID_12137("12137", "bufreconnect"),
        MEGID_12130("12130", "delay");
        String msgid;
        String detail;

        MegId(String msgid, String detail) {
            this.msgid = msgid;
            this.detail = detail;
        }
    }

    /** 使用第三方视频提供商提供的调度接口获得第三方播放域名对应的包括ip地址的播放地址 */
    public void dns_resolve_stream(final PlayServerEntity.PlayserverEntity playserverEntity, final PlayServerEntity mServer, String channelname, final AbstractBusinessDataCallBack callBack) {
        if (StringUtils.isEmpty(playserverEntity.getIp_gslb_addr())) {
            callBack.onDataFail(3, "empty");
            return;
        }
        final StringBuilder url;
        final String provide = playserverEntity.getProvide();
        if ("wangsu".equals(provide)) {
            url = new StringBuilder("http://" + playserverEntity.getIp_gslb_addr());
        } else if ("ali".equals(provide)) {
            url = new StringBuilder("http://" + playserverEntity.getIp_gslb_addr() + "/dns_resolve_stream");
        } else {
            callBack.onDataFail(3, "other");
            return;
        }
        HttpRequestParams entity = new HttpRequestParams();
//        curl -v ip_gslb_addr里的地址 -H "WS_URL:livewangsu.xescdn.com/live_server/x_3_55873" -H "WS_RETIP_NUM:1" -H "WS_URL_TYPE:3"
        if ("wangsu".equals(provide)) {
            String WS_URL = playserverEntity.getAddress() + "/" + mServer.getAppname() + "/" + channelname;
            entity.addHeaderParam("WS_URL", WS_URL);
            entity.addHeaderParam("WS_RETIP_NUM", "1");
            entity.addHeaderParam("WS_URL_TYPE", "3");
        } else {
            url.append("?host=" + playserverEntity.getAddress());
            url.append("&stream=" + channelname);
            url.append("&app=" + mServer.getAppname());
        }
//        entity.addBodyParam("host", playserverEntity.getAddress());
//        entity.addBodyParam("stream", channelname);
//        entity.addBodyParam("app", mServer.getAppname());
        final AtomicBoolean haveCall = new AtomicBoolean();
        final AbstractBusinessDataCallBack dataCallBack = new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                logger.d("dns_resolve_stream:onDataSucess:haveCall=" + haveCall.get() + ",objData=" + objData[0]);
                if (!haveCall.get()) {
                    haveCall.set(true);
                    callBack.onDataSucess(objData);
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("dns_resolve_stream:onDataFail:haveCall=" + haveCall.get() + ",errStatus=" + errStatus + ",failMsg=" + failMsg);
                if (!haveCall.get()) {
                    haveCall.set(true);
                    callBack.onDataFail(errStatus, failMsg);
                }
            }
        };
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                dataCallBack.onDataFail(0, "timeout");
            }
        }, 2000);
        mHttpManager.sendGetNoBusiness(url.toString(), entity, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                logger.i("dns_resolve_stream:onFailure=", e);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataCallBack.onDataFail(0, "onFailure");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int code = response.code();
                        String r = "";
                        try {
                            r = response.body().string();
                            logger.i("dns_resolve_stream:onResponse:url=" + url + ",response=" + code + "," + r);
                            if (response.code() >= 200 && response.code() <= 300) {
                                if ("wangsu".equals(provide)) {
//                        rtmp://111.202.83.208/live_server/x_3_55873?wsiphost=ipdb&wsHost=livewangsu.xescdn.com
                                    String url = r.replace("\n", "");
                                    int index1 = url.substring(7).indexOf("/");
                                    if (index1 != -1) {
                                        String host = url.substring(7, 7 + index1);
                                        playserverEntity.setIpAddress(host);
                                    }
                                    dataCallBack.onDataSucess(provide, url);
                                    return;
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(r);
                                        String host = jsonObject.getString("host");
                                        JSONArray ipArray = jsonObject.optJSONArray("ips");
                                        String ip = ipArray.getString(0);
                                        String url = "rtmp://" + ip + "/" + host + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
                                        playserverEntity.setIpAddress(ip);
                                        dataCallBack.onDataSucess(provide, url);
                                        mLogtf.d("dns_resolve_stream:ip_gslb_addr=" + playserverEntity.getIp_gslb_addr() + ",ip=" + ip);
                                        return;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dataCallBack.onDataFail(1, r);
                    }
                });
            }
        });
    }

    /**
     * 调试信息
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebugSys(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getUname());
        StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", courseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("clits", "" + System.currentTimeMillis());
//        Loger.d(mContext, eventId, mData, true);
        UmsAgentManager.umsAgentDebug(mContext, appID, eventId, mData);
    }

    /**
     * 交互日志
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebugInter(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getStuName());
        StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", courseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData);
    }

    /**
     * 展现日志
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebugPv(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getStuName());
        StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", courseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadShow, mData);
    }

    @Override
    public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {

    }

    @Override
    public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {

    }

    @Override
    public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {

    }
}

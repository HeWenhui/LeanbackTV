package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.http.DownloadCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.ui.dataload.PageDataLoadEntity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.Callback;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;

/**
 * 处理IRC消息，视频调度
 *
 * @author linyuqiang
 */
public class LiveBll extends BaseBll implements LiveAndBackDebug, IRCState {
    private String TAG = "LiveBllLog";
    /** 点名 */
    private RollCallAction mRollCallAction;
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser mHttpResponseParser;
    private NewIRCMessage mIRCMessage;
    private String vStuCourseID;
    private String courseId;
    private String mLiveId;
    private String mCurrentDutyId;
    public final int mLiveType;
    private LiveGetInfo mGetInfo;
    private final LiveTopic mLiveTopic = new LiveTopic();
    private Handler mHandler = LiveMainHandler.getMainHandler();
    private LogToFile mLogtf;
    private Callback.Cancelable mCataDataCancle;
    private Callback.Cancelable mGetPlayServerCancle;

    /**
     * 是不是有分组
     */
    private boolean haveTeam = false;
    private int form;
    /**
     * 区分文理appid
     */
    String appID = UmsConstants.LIVE_APP_ID;
    public static boolean isAllowTeamPk = false;

    public LiveBll(Context context, String vStuCourseID, String courseId, String vSectionID, int form, LiveGetInfo
            liveGetInfo) {
        super(context);
        this.vStuCourseID = vStuCourseID;
        this.courseId = courseId;
        this.mLiveId = vSectionID;
        this.mLiveType = LiveVideoConfig.LIVE_TYPE_LIVE;
        this.form = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("courseId", courseId);
        mHttpManager.addBodyParam("stuCouId", vStuCourseID);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpManager.addBodyParam("form", "" + form);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(context, TAG);
        mLogtf.clear();
        if (liveGetInfo != null) {
            mLiveTopic.setMode(liveGetInfo.getMode());
        }
    }

    public LiveBll(Context context, String vSectionID, int type, int form) {
        super(context);
        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.form = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(context, TAG);
        mLogtf.clear();
        if (type != LiveVideoConfig.LIVE_TYPE_LIVE) {
            mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        }
    }

    public LiveBll(Context context, String vSectionID, String currentDutyId, int type, int form) {
        super(context);
        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.mCurrentDutyId = currentDutyId;
        this.form = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(context, TAG);
        mLogtf.clear();
        if (type != LiveVideoConfig.LIVE_TYPE_LIVE) {
            mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        }
    }

    /**
     * 是否是 高三 理科直播 （展示不同聊天 内容：高三理科 以 班级为单位展示,）
     *
     * @return
     */
    @Override
    public boolean isSeniorOfHighSchool() {
        return mGetInfo != null && mGetInfo.getIsSeniorOfHighSchool() == 1;
    }

    /**
     * activity退出
     */
    public void onDestroy() {
        if (mRollCallAction != null) {
            mRollCallAction.forceCloseRollCall();
        }
        mRollCallAction = null;
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
        isAllowTeamPk = false;
    }

    @Override
    public void praiseTeacher(final String formWhichTeacher, String ftype, String educationStage, final HttpCallBack callBack) {
        String teacherId = mGetInfo.getMainTeacherInfo().getTeacherId();
        mHttpManager.praiseTeacher(mLiveType, mLiveId, teacherId, ftype, educationStage, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("praiseTeacher:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                if (responseEntity.getJsonObject() instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        sendFlowerMessage(jsonObject.getInt("type"), formWhichTeacher);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onPmSuccess(responseEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("onPmFailure:msg=" + msg);
                callBack.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("praiseTeacher:onPmFailure:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onPmError(responseEntity);
            }
        });
    }

    @Override
    public boolean isDisable() {
        return mLiveTopic.isDisable();
    }

    public boolean isConnected() {
        if (mIRCMessage == null) {
            return false;
        }
        return mIRCMessage.isConnected();
    }

    @Override
    public boolean isHaveTeam() {
        return haveTeam;
    }

    /**
     * 是否开启聊天
     */
    @Override
    public boolean openchat() {
        boolean openchat;
        if (LiveTopic.MODE_CLASS.equals(getMode())) {
            openchat = mLiveTopic.getMainRoomstatus().isOpenchat();
        } else {
            openchat = mLiveTopic.getCoachRoomstatus().isOpenchat();
        }
        mLogtf.d("openchat:getMode=" + getMode() + ",isOpenchat=" + openchat);
        return openchat;
    }

    @Override
    public boolean sendMessage(String msg, String s, Map<String, String> map) {
        return false;
    }

    private SendMsgListener mSendMsgListener;

    public void setSendMsgListener(SendMsgListener listener) {
        mSendMsgListener = listener;
    }

    /** 发送消息回调 */
    public interface SendMsgListener {
        void onMessageSend(String msg, String targetName);
    }

    /**
     * 发生聊天消息
     */
    @Override
    public boolean sendMessage(String msg, String name) {
        if (mSendMsgListener != null) {
            mSendMsgListener.onMessageSend(msg, name);
        }

        if (mLiveTopic.isDisable()) {
            return false;
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.TEACHER_MESSAGE);
                if (StringUtils.isEmpty(name)) {
                    name = mGetInfo.getStuName();
                }
                jsonObject.put("name", name);
                jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
                jsonObject.put("version", "" + mGetInfo.getHeadImgVersion());
                jsonObject.put("msg", msg);
                if (haveTeam) {
                    StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                    String teamId = studentLiveInfo.getTeamId();
                    jsonObject.put("from", "android_" + teamId);
                    jsonObject.put("to", teamId);
                }
                mIRCMessage.sendMessage(jsonObject.toString());
            } catch (Exception e) {
                // logger.e( "understand", e);
                UmsAgentManager.umsAgentException(ContextManager.getContext(), "livevideo_livebll_sendMessage", e);
                mLogtf.e("sendMessage", e);
            }
            return true;
        }
    }

    /**
     * 是否开启献花
     */
    @Override
    public boolean isOpenbarrage() {
        return mLiveTopic.getMainRoomstatus().isOpenbarrage();
    }

    /**
     * 理科主讲是否开启献花
     */
    @Override
    public boolean isOpenZJLKbarrage() {
        return mLiveTopic.getCoachRoomstatus().isZJLKOpenbarrage();
    }

    /**
     * 理科辅导老师是否开启献花
     */
    @Override
    public boolean isOpenFDLKbarrage() {
        return mLiveTopic.getCoachRoomstatus().isFDLKOpenbarrage();
    }

    /**
     * 发生献花消息
     */
    public void sendFlowerMessage(int ftype, String frommWhichTeacher) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.FLOWERS);
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("ftype", ftype);

            if (frommWhichTeacher != null) {
                jsonObject.put("to", frommWhichTeacher);
            }
            mIRCMessage.sendMessage(jsonObject.toString());
//            mIRCMessage.sendMessage(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendFlowerMessage", e);
        }
    }

    /**
     * 得到当前模式
     */
    @Override
    public String getMode() {
        String mode;
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            mode = mLiveTopic.getMode();
        } else {
            mode = LiveTopic.MODE_CLASS;
        }
        return mode;
    }


    /**
     * 得到当前理科的notice模式
     */
    @Override
    public String getLKNoticeMode() {
        String mode;
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (mLiveTopic == null) {
                mode = LiveTopic.MODE_CLASS;
            } else {
                mode = mLiveTopic.getLKNoticeMode();
            }
        } else {
            mode = LiveTopic.MODE_CLASS;
        }
        return mode;
    }

    public String getConnectNickname() {
        return mIRCMessage.getConnectNickname();
    }


    public String getNickname() {
        return mIRCMessage.getNickname();
    }

    public String getStuName() {
        return mGetInfo.getStuName();
    }

    public void setNotOpeningNum() {
        mHttpManager.setNotOpeningNum(mGetInfo.getId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("setNotOpeningNum:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                logger.e("setNotOpeningNum:onFailure", e);
                super.onFailure(call, e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("setNotOpeningNum:onPmError" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
            }
        });
    }

    public void getCourseWareUrl(HttpCallBack requestCallBack) {
        mHttpManager.getCourseWareUrl(requestCallBack);
    }

    public Call download(final String url, final String saveDir, DownloadCallBack downloadCallBack) {
        return mHttpManager.download(url, saveDir, downloadCallBack);
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
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
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
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
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
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
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

    // 03.22 上传体验课播放器的心跳时间
    public void uploadExperiencePlayTime(String liveId, String termId, Long hbtime) {
        mHttpManager.uploadExperiencePlayingTime(liveId, termId, hbtime, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.e("uploadexperiencetime:" + responseEntity.getJsonObject());
            }
        });
    }

    // 04.04 获取更多课程
    @Override
    public void getMoreChoice(final PageDataLoadEntity pageDataLoadEntity, final AbstractBusinessDataCallBack
            getDataCallBack) {
        mHttpManager.getMoreChoiceCount(mLiveId, new HttpCallBack(pageDataLoadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.e("responseEntity:" + responseEntity);
                MoreChoice choiceEntity = mHttpResponseParser.parseMoreChoice(responseEntity);
                if (choiceEntity != null) {
                    getDataCallBack.onDataSucess(choiceEntity);
                }
            }
        });
    }

    public void setChatOpen(boolean open) {
        if (LiveTopic.MODE_CLASS.equals(getMode())) {
            mLiveTopic.getMainRoomstatus().setOpenchat(open);
        } else {
            mLiveTopic.getCoachRoomstatus().setOpenchat(open);
        }
    }
}
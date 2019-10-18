package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;

import com.hwl.log.xrsLog.XrsLog;
import com.hwl.log.xrsNetworkLog.XrsNetLog;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog.LiveBusiLog;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog.LiveBusiLogEntity;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog.LiveBusiLogSendLogRunnable;

import java.util.Map;
import java.util.UUID;

public class LiveDebugBigClassIml implements LiveAndBackDebug, LiveDebugGetInfo {
    private Context mContext;
    private int mLiveType;
    private LiveGetInfo mGetInfo;
    private String appID;
    private String mLiveId;
    private String mCourseId;
    private boolean playBack;
    LiveBusiLogEntity logEntity = new LiveBusiLogEntity();

    public LiveDebugBigClassIml(Context mContext, int liveType, String liveId, String courseId,boolean playBack) {
        this.mContext = mContext;
        mLiveType = liveType;
        mLiveId = liveId;
        mCourseId = courseId;
        this.playBack = playBack;
    }

    @Override
    public void onGetInfo(LiveGetInfo mGetInfo, String appID) {
        this.mGetInfo = mGetInfo;
        this.appID = appID;
        if(logEntity ==null) {
            logEntity = new LiveBusiLogEntity();
        }
        logEntity.businessAppId = appID;

    }

    ///日志上传相关
    @Override
    public void umsAgentDebugSys(String eventtype, Map<String, String> mData) {
        if (mGetInfo == null) {
            return;
        }
        setLogParam(eventtype, mData);
        logEntity.logType = LiveBusiLogSendLogRunnable.LOGTYPE_SYS;
        logEntity.mData = mData;
        LiveBusiLog.log(logEntity);
      //  UmsAgentManager.umsAgentDebug(mContext, appID, eventtype, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventtype, Map<String, String> mData) {
        if (mGetInfo == null) {
            return;
        }
        setLogParam(eventtype, mData);
        logEntity.logType = LiveBusiLogSendLogRunnable.LOGTYPE_CLICK;
        logEntity.mData = mData;
        LiveBusiLog.log(logEntity);
        //UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.typePv, mData);
    }

    @Override
    public void umsAgentDebugPv(String eventtype, Map<String, String> mData) {
        if (mGetInfo == null) {
            return;
        }
        setLogParam(eventtype, mData);
        logEntity.logType = LiveBusiLogSendLogRunnable.LOGTYPE_PV;
        logEntity.mData = mData;
        LiveBusiLog.log(logEntity);
      //  UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.typeShow, mData);
    }

    @Override
    public void umsAgentDebugSys(String eventtype, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventtype", "" + eventtype);
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        setAnalysis(analysis);
        logEntity.logType = LiveBusiLogSendLogRunnable.LOGTYPE_SYS;
        logEntity.mData = mData;
        LiveBusiLog.log(logEntity);
     //   UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadSystem, mData, analysis);
    }

    @Override
    public void umsAgentDebugInter(String eventtype, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventtype", "" + eventtype);
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        setAnalysis(analysis);
        logEntity.logType = LiveBusiLogSendLogRunnable.LOGTYPE_CLICK;
        logEntity.mData = mData;
        LiveBusiLog.log(logEntity);
       // UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.typePv, mData, analysis);
    }

    private String getMode() {
        return LiveTopic.MODE_CLASS;
    }

    @Override
    public void umsAgentDebugPv(String eventtype, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventtype", "" + eventtype);
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        mData.put("teacherMode ",LiveTopic.MODE_CLASS.equals(getMode()) ? "0" : "1");
        setAnalysis(analysis);
        logEntity.logType = LiveBusiLogSendLogRunnable.LOGTYPE_PV;
        logEntity.mData = mData;
        LiveBusiLog.log(logEntity);
       // UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.typeShow, mData, analysis);
    }

    /**
     * 上传log 添加 公共参数
     *
     * @param eventtype
     * @param mData
     */
    private void setLogParam(String eventtype, Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getUname());
        mData.put("playBack",playBack?"1":"0");
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", mCourseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        String educationstage = mGetInfo.getEducationStage();
        if (LiveVideoConfig.EDUCATION_STAGE_1.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_2.equals(educationstage)) {
            mData.put("gradejudgment", "primary");
        } else if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
            mData.put("gradejudgment", "middle");
        }
        String subjectId = "";
        if(mGetInfo.getSubjectIds() != null && mGetInfo.getSubjectIds().length > 0){
            subjectId = mGetInfo.getSubjectIds()[0];
        }
        mData.put("subject", "" + subjectId);
        mData.put("ip", "" + IpAddressUtil.USER_IP);
        mData.put("liveid", mLiveId);
        mData.put("grade",mGetInfo.getGrade()+"");
        mData.put("livetype", "" + mLiveType);
        mData.put("eventtype", "" + eventtype);
        mData.put("clits", "" + System.currentTimeMillis());
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
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
        analysis.put("planid", mLiveId);
        analysis.put("clientip", IpAddressUtil.USER_IP);
        analysis.put("traceid", "" + UUID.randomUUID());
        analysis.put("platform", "android");
    }

}

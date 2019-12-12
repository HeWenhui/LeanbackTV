package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.Map;
import java.util.UUID;

public class LiveAndBackDebugIml implements LiveAndBackDebug, LiveDebugGetInfo {
    private Context mContext;
    private int mLiveType;
    private LiveGetInfo mGetInfo;
    private String appID;
    private String mLiveId;
    private String mCourseId;

    public LiveAndBackDebugIml(Context mContext, int liveType, String liveId, String courseId) {
        this.mContext = mContext;
        mLiveType = liveType;
        mLiveId = liveId;
        mCourseId = courseId;
    }

    @Override
    public void onGetInfo(LiveGetInfo mGetInfo, String appID) {
        this.mGetInfo = mGetInfo;
        this.appID = appID;
    }

    ///日志上传相关
    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        if (mGetInfo == null) {
            return;
        }
        setLogParam(eventId, mData);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadSystem, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        if (mGetInfo == null) {
            return;
        }
        setLogParam(eventId, mData);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData);
    }

    @Override
    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
        if (mGetInfo == null) {
            return;
        }
        setLogParam(eventId, mData);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadShow, mData);
    }

    @Override
    public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventid", "" + eventId);
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        setAnalysis(analysis);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadSystem, mData, analysis);
    }

    @Override
    public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventid", "" + eventId);
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        setAnalysis(analysis);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData, analysis);
    }

    private String getMode() {
        return null;
    }

    @Override
    public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {
        Map<String, String> mData = stableLogHashMap.getData();
        Map<String, String> analysis = stableLogHashMap.getAnalysis();
        mData.put("eventid", "" + eventId);
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        setAnalysis(analysis);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadShow, mData, analysis);
    }

    /**
     * 上传log 添加 公共参数
     *
     * @param eventId
     * @param mData
     */
    private void setLogParam(String eventId, Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getUname());
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
        mData.put("subject", "" + mGetInfo.getSubject_digits());
        mData.put("ip", "" + IpAddressUtil.USER_IP);
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("eventid", "" + eventId);
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

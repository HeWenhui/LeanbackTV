package com.xueersi.parentsmeeting.modules.livevideo.message;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.IIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.ui.dataload.PageDataLoadEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuanwei2 on 2019/6/4.
 */

public class ExperienceIrcState implements IRCState {

    private LiveGetInfo mGetInfo;

    private LiveTopic mLiveTopic;

    private IIRCMessage mIRCMessage;

    private VideoLivePlayBackEntity playBackEntity;

    private LiveHttpManager mHttpManager;

    private LiveHttpResponseParser mHttpResponseParser;

    public ExperienceIrcState(LiveGetInfo mGetInfo, LiveTopic mLiveTopic, IIRCMessage mIRCMessage, VideoLivePlayBackEntity playBackEntity, LiveHttpManager mHttpManager) {
        this.mGetInfo = mGetInfo;
        this.mLiveTopic = mLiveTopic;
        this.mIRCMessage = mIRCMessage;
        this.playBackEntity = playBackEntity;
        this.mHttpManager = mHttpManager;
        mHttpResponseParser = new LiveHttpResponseParser(null);
    }

    public void setChatOpen(boolean open) {
        if (LiveTopic.MODE_CLASS.equals(getMode())) {
            mLiveTopic.getMainRoomstatus().setOpenchat(open);
        } else {
            mLiveTopic.getCoachRoomstatus().setOpenchat(open);
        }
    }

    /**
     * 发生献花消息
     */
    protected void sendFlowerMessage(int ftype, String frommWhichTeacher) {
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
            // logger.e( "understand", e)
        }
    }

    @Override
    public String getMode() {
        return LiveTopic.MODE_TRANING;
    }

    /**
     * 是否开启献花
     */
    @Override
    public boolean isOpenbarrage() {
        return mLiveTopic.getMainRoomstatus().isOpenbarrage();
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

        return openchat;
    }

    @Override
    public boolean sendMessage(String msg, String s) {
        if (mLiveTopic.isDisable()) {
            return false;
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.TEACHER_MESSAGE);
                String name = mGetInfo.getStuName();
                jsonObject.put("name", name);
                jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
                jsonObject.put("version", "" + mGetInfo.getHeadImgVersion());
                jsonObject.put("msg", msg);
                mIRCMessage.sendMessage(jsonObject.toString());
            } catch (Exception e) {
                // logger.e( "understand", e);
                UmsAgentManager.umsAgentException(BaseApplication.getContext(), "livevideo_livebll_sendMessage", e);
            }
            return true;
        }
    }


    @Override
    public void praiseTeacher(final String formWhichTeacher, String ftype, String educationStage, final HttpCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String teacherId = mGetInfo.getMainTeacherInfo().getTeacherId();
        mHttpManager.praiseTeacher(mGetInfo.getLiveType(), enstuId, playBackEntity.getLiveId(), teacherId, ftype, educationStage, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
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
                callBack.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                callBack.onPmError(responseEntity);
            }
        });
    }
    @Override
    public boolean isDisable() {
        return mLiveTopic.isDisable();
    }

    @Override
    public boolean isHaveTeam() {
        return false;
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

    @Override
    public void getMoreChoice(final PageDataLoadEntity pageDataLoadEntity, final AbstractBusinessDataCallBack getDataCallBack) {
        mHttpManager.getMoreChoiceCount(playBackEntity.getLiveId(), new HttpCallBack(pageDataLoadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                MoreChoice choiceEntity = mHttpResponseParser.parseMoreChoice(responseEntity);
                if (choiceEntity != null) {
                    getDataCallBack.onDataSucess(choiceEntity);
                }
            }
        });
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
     * 得到当前理科的notice模式
     */
    @Override
    public String getLKNoticeMode() {
        String mode;

        if (mGetInfo.getLiveType() == LiveVideoConfig.LIVE_TYPE_LIVE) {
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

}

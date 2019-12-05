package com.xueersi.parentsmeeting.modules.livevideo.rollcall.business;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.IIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IrcAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.http.ExperienceBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanwei2 on 2019/5/31.
 */

public class ExpRollCallBll extends LiveBackBaseBll implements NoticeAction, TopicAction, RollCallHttp {

    private RollCallBll rollCallBll;

    private ExperienceBusiness mHttpManager;

    private ExpLiveInfo expLiveInfo;
    private String signInUrl;

    private int expLiveId;

    private String orderId;

    public ExpRollCallBll(Activity activity, LiveBackBll liveBackBll, ExpLiveInfo expLiveInfo, String orderId) {
        super(activity, liveBackBll);
        this.rollCallBll = new RollCallBll(activity);
        this.mHttpManager = new ExperienceBusiness(activity);
        this.rollCallBll.setRollCallHttp(this);
        this.expLiveInfo = expLiveInfo;
        this.signInUrl = expLiveInfo.getSignInUrl();
        this.expLiveId = expLiveInfo.getExpLiveId();
        this.orderId = orderId;
    }

    public void openSignAuto(ClassSignEntity classSignEntity) {
        rollCallBll.onRollCall(false);
        rollCallBll.onRollCall(classSignEntity);
    }

    @Override
    public void initView() {
        super.initView();
        rollCallBll.initView(getLiveViewAction());
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        rollCallBll.setLiveGetInfo(liveGetInfo);
        liveGetInfo.getStudentLiveInfo().setSignStatus(expLiveInfo.getIsSignIn());
    }

    @Override
    public void userSign(String liveId, String classId, String teacherId, HttpCallBack requestCallBack) {
        mHttpManager.expUserSign(signInUrl, expLiveId, orderId, requestCallBack);
    }

    @Override
    public void sendRollCallNotice(JSONObject data, String targetName) {
        IrcAction ircAction= ProxUtil.getProvide(activity,IrcAction.class);
        if (targetName != null) {
            ircAction.sendNotice(targetName, data.toString());
        } else {
            ircAction.sendNotice(data.toString());
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.e("=======>onNotice:" + type);
        try {
            switch (type) {
                case XESCODE.CLASSBEGIN:
                    //开始上课  结束签到相关逻辑
                    rollCallBll.forceCloseRollCall();
                    break;
                case XESCODE.ROLLCALL://点名

                    rollCallBll.onRollCall(false);
                    if (liveGetInfo.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGNED) {
                        ClassSignEntity classSignEntity = new ClassSignEntity();
                        classSignEntity.setStuName(liveGetInfo.getStuName());
                        classSignEntity.setTeacherName(liveGetInfo.getTeacherName());
                        classSignEntity.setTeacherIMG(liveGetInfo.getTeacherIMG());
                        classSignEntity.setStatus(1);
                        rollCallBll.onRollCall(classSignEntity);
                    }
                    break;
                case XESCODE.STOPROLLCALL://结束点名

                    rollCallBll.onRollCall(true);
                    //noinspection AlibabaUndefineMagicConstant
                    if (liveGetInfo.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGNED) {
                        liveGetInfo.getStudentLiveInfo().setSignStatus(3);
                        ClassSignEntity classSignEntity = new ClassSignEntity();
                        classSignEntity.setStuName(liveGetInfo.getStuName());
                        classSignEntity.setTeacherName(liveGetInfo.getTeacherName());
                        classSignEntity.setTeacherIMG(liveGetInfo.getTeacherIMG());
                        classSignEntity.setStatus(liveGetInfo.getStudentLiveInfo().getSignStatus());
                        rollCallBll.onRollCall(classSignEntity);
                    }
                    break;

                case XESCODE.CLASS_MATEROLLCALL://其他学生点名

                    if (RollCallBll.IS_SHOW_CLASSMATE_SIGN) {
                        List<String> headImgUrl = liveGetInfo.getHeadImgUrl();
                        ClassmateEntity classmateEntity = new ClassmateEntity();
                        String id = data.optString("id");
                        classmateEntity.setId(id);
                        classmateEntity.setName(data.getString("name"));
                        if (!headImgUrl.isEmpty()) {
                            try {
                                String img = headImgUrl.get(0) + "/" + data.getString("path") + "/" +
                                        liveGetInfo.getImgSizeType() + "?" + data.getString("Version");
                                classmateEntity.setImg(img);
                            } catch (JSONException e) {
                                MobAgent.httpResponseParserError(TAG, "onNotice:setImg", e.getMessage());
                            }
                        }
                        rollCallBll.onClassmateRollCall(classmateEntity);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.CLASSBEGIN, XESCODE.ROLLCALL, XESCODE.STOPROLLCALL, XESCODE.CLASS_MATEROLLCALL};
    }

    private int[] filters;

    public void dispatcNotice(String sourceNick, String target, JSONObject data, int type) {

        if (filters == null) {
            filters = getNoticeFilter();
        }

        boolean regist = false;

        for (int value : filters) {
            if (value == type) {
                regist = true;
                break;
            }
        }

        if (regist) {
            onNotice(sourceNick, target, data, type);
        }

    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject json, boolean modeChange) {

        if (expLiveInfo.getIsSignIn() == 2) {
            return;
        }
        try {

            if (!json.has("room_2")) {
                return;
            }

            json = json.getJSONObject("room_2");

            if (!json.has("isCalling")) {
                return;
            }

            boolean isCalling = json.getBoolean("isCalling");

            if (isCalling) {
                ClassSignEntity classSignEntity = new ClassSignEntity();
                classSignEntity.setStuName(liveGetInfo.getStuName());
                classSignEntity.setTeacherName(liveGetInfo.getTeacherName());
                classSignEntity.setTeacherIMG(liveGetInfo.getTeacherIMG());
                classSignEntity.setStatus(1);
                openSignAuto(classSignEntity);
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }
}
package com.xueersi.parentsmeeting.modules.livevideo.rollcall.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/10.
 */

public class RollCallIRCBll extends LiveBaseBll implements NoticeAction, RollCallHttp {
    RollCallBll rollCallBll;

    public RollCallIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        rollCallBll = new RollCallBll(context);
        rollCallBll.setRollCallHttp(this);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        rollCallBll.onLiveInited(getInfo, mRootView, mLiveType);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        rollCallBll.initView(bottomContent);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.e( "=======>onNotice:" + type);
        try {
            switch (type) {
                case XESCODE.CLASSBEGIN:
                    //开始上课  结束签到相关逻辑
                    rollCallBll.forceCloseRollCall();
                    break;
                case XESCODE.ROLLCALL://点名

                    rollCallBll.onRollCall(false);
                    if (mGetInfo.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGNED) {
                        ClassSignEntity classSignEntity = new ClassSignEntity();
                        classSignEntity.setStuName(mGetInfo.getStuName());
                        classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                        classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                        classSignEntity.setStatus(1);
                        rollCallBll.onRollCall(classSignEntity);
                    }
                    break;
                case XESCODE.STOPROLLCALL://结束点名

                    rollCallBll.onRollCall(true);
                    //noinspection AlibabaUndefineMagicConstant
                    if (mGetInfo.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGNED) {
                        mGetInfo.getStudentLiveInfo().setSignStatus(3);
                        ClassSignEntity classSignEntity = new ClassSignEntity();
                        classSignEntity.setStuName(mGetInfo.getStuName());
                        classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                        classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                        classSignEntity.setStatus(mGetInfo.getStudentLiveInfo().getSignStatus());
                        rollCallBll.onRollCall(classSignEntity);
                    }
                    break;

                case XESCODE.CLASS_MATEROLLCALL://其他学生点名

                    if (RollCallBll.IS_SHOW_CLASSMATE_SIGN) {
                        List<String> headImgUrl = mGetInfo.getHeadImgUrl();
                        ClassmateEntity classmateEntity = new ClassmateEntity();
                        String id = data.optString("id");
                        classmateEntity.setId(id);
                        classmateEntity.setName(data.getString("name"));
                        if (!headImgUrl.isEmpty()) {
                            try {
                                String img = headImgUrl.get(0) + "/" + data.getString("path") + "/" +
                                        mGetInfo.getImgSizeType() + "?" + data.getString("Version");
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
    public void onDestroy() {
        super.onDestroy();
        rollCallBll.forceCloseRollCall();
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.CLASSBEGIN, XESCODE.ROLLCALL,
                XESCODE.STOPROLLCALL, XESCODE.CLASS_MATEROLLCALL};
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        rollCallBll.setVideoLayout(liveVideoPoint);
    }

    @Override
    public void userSign(String enstuId, String liveId, String classId, String teacherId, HttpCallBack
            requestCallBack) {
        getHttpManager().userSign(enstuId, liveId, classId, teacherId, requestCallBack);
    }

    @Override
    public void sendRollCallNotice(JSONObject jsonObject, String o) {
        sendNotice(jsonObject, o);
    }
}

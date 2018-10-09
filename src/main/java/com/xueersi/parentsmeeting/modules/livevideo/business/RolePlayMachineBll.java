package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayMachinePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class RolePlayMachineBll extends RolePlayerBll implements RolePlayAction{
    private RolePlayerHttpManager mRolePlayerHttpManager;
    private RolePlayerHttpResponseParser mRolePlayerHttpResponseParser;

    /**
     * 直播基础BLL
     */
    private LiveAndBackDebug mLiveBll;

    private final LiveGetInfo mLiveGetInfo;

    /**
     * 直播ID
     */
    private String mLiveId;
    /**
     * 购课ID
     */
    private String mStuCouId;

    RolePlayerEntity mRolePlayerEntity;

    RelativeLayout mBottomContent;

    RolePlayMachinePager mRolePlayMachinePager;

    public RolePlayMachineBll(Context context, RelativeLayout bottomContent, LiveAndBackDebug liveBll, LiveGetInfo liveGetInfo) {
        super(context, bottomContent, liveBll, liveGetInfo);

        this.mLiveBll = liveBll;
        this.mLiveGetInfo = liveGetInfo;
        mLiveId = mLiveGetInfo.getId();
        mStuCouId = mLiveGetInfo.getStuCouId();
        mRolePlayerEntity = new RolePlayerEntity();
        mRolePlayerHttpManager = new RolePlayerHttpManager(mContext);
        mRolePlayerHttpResponseParser = new RolePlayerHttpResponseParser();

        mBottomContent = bottomContent;

    }

    @Override
    public void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        //super.teacherPushTest(videoQuestionLiveEntity);

        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        //拉取试题
        requestTestInfos();

        //addPagerToWindow();
    }

    /**
     * 获取分组信息后去请求试题
     */
    @Override
    public void requestTestInfos() {
        Loger.i("RolePlayerDemoTest", "请求试题信息 mRolePlayerEntity.toString() = " + videoQuestionLiveEntity.id);
        mRolePlayerHttpManager.requestRolePlayTestInfos(mLiveId, mStuCouId, videoQuestionLiveEntity.id, new
                HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        mRolePlayerEntity = mRolePlayerHttpResponseParser.parserRolePlayInfos(responseEntity);
                        Loger.i("RolePlayerDemoTest", "服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                        Loger.i("RolePlayerDemoTest", "服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                ".getLstRolePlayerMessage()" +
                                ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                mRolePlayerEntity.toString());

                        //将roleplay pager挂载到直播窗口
                       // addPagerToWindow();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        Loger.i("RolePlayerDemoTest", "onPmError:" + responseEntity.getErrorMsg());
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        Loger.i("RolePlayerDemoTest", "onPmFailure:" + msg);
                    }
                });
    }

    /**
     * 将roleplay pager挂载到直播窗口
     */
    private void addPagerToWindow() {
        if(mRolePlayMachinePager == null){
            mRolePlayMachinePager = new RolePlayMachinePager(mContext,
                    videoQuestionLiveEntity, mLiveGetInfo.getId(), videoQuestionLiveEntity.id, mLiveGetInfo.getStuId(),
                    true, videoQuestionLiveEntity.nonce, null, null, false, null,null,mLiveGetInfo);
        }
        mRolePlayMachinePager.initData();
        if(mBottomContent != null && mRolePlayMachinePager != null && mRolePlayMachinePager.getRootView().getParent() == null){
            Loger.i("RolePlayerDemoTest", "mBottomContent = " + mBottomContent);
            mBottomContent.addView(mRolePlayMachinePager.getRootView());
        }
    }

    public RolePlayerEntity getRoleEntry() {
        return mRolePlayerEntity;
    }

    public void setRolePlayMachinePager(RolePlayMachinePager rolePlayMachinePager) {
        this.mRolePlayMachinePager = rolePlayMachinePager;

    }

    @Override
    public synchronized void requestResult() {
        Loger.i("RolePlayerDemoTest", "提交结果");
        Loger.i("RolePlayerDemoTestlog", "用户提交结果,记录日志");
        //提交结果的时候，记录日志信息
        //RolePlayLog.sno6(mLiveBll, mRolePlayerEntity, mContext);
        mRolePlayerEntity.setResult(true);
        JSONObject obj = new JSONObject();
        try {
            RolePlayerEntity.RolePlayerHead rolePlayerHead = mRolePlayerEntity.getSelfRoleHead();
            String roleName = null;
            if (rolePlayerHead != null) {
                roleName = rolePlayerHead.getRoleName();
            }
            obj.put("type", 1);
            obj.put("roler", roleName);
            JSONArray arrAnswer = new JSONArray();
            //TODO:
            int i = 1;
            for (RolePlayerEntity.RolePlayerMessage message : mRolePlayerEntity.getLstRolePlayerMessage()) {
                JSONObject objAn = new JSONObject();
                objAn.put("sentenceNum", i);
                objAn.put("entranceTime", message.getMaxReadTime());
                objAn.put("score", message.getSpeechScore());

                if (message.getRolePlayer().isSelfRole() && message.getRolePlayer().getSpeechScore() > 1) {
                    JSONObject objData = new JSONObject();
                    objData.put("cont_score", message.getFluency());
                    objData.put("pron_score", message.getAccuracy());
                    objData.put("total_score", message.getSpeechScore());
                    objData.put("level", message.getLevel());
                    objAn.put("alldata", objData);
                } else {
                    objAn.put("alldata", "");

                }
                arrAnswer.put(objAn);
                i++;
            }
            obj.put("answers", arrAnswer);
            Loger.i("RolePlayerDemoTest", "mStuCouId = " + mStuCouId + " mLiveId = " + mLiveId + " mRolePlayerEntity" +
                    ".getTestId() = " + mRolePlayerEntity.getTestId()
                    + " obj = " + obj.toString());

            mRolePlayerHttpManager.requestResult(mStuCouId, mLiveId, mRolePlayerEntity.getTestId(), roleName, obj
                    .toString(), new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    int gold = jsonObject.optInt("gold");
                    mRolePlayerEntity.setGoldCount(gold);
                    Loger.i("RolePlayerDemoTest", "onPmSuccess: gold  =" + gold);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    Loger.i("RolePlayerDemoTest", "onFailure: e.getMessage()  =" + e.getMessage() + "取消点赞");
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    Loger.i("RolePlayerDemoTest", "onPmError ");
                    super.onPmError(responseEntity);
                    if(responseEntity != null){
                        Loger.i("RolePlayerDemoTest", "onPmError: " + responseEntity.getErrorMsg() + ":"+responseEntity.isJsonError());
                    }
//                    if (mRolePlayMachinePager != null) {
//                        mRolePlayMachinePager.recoverListScrollAndCancelDZ();
//                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void cancelDZ() {
        //super.cancelDZ();
        RolePlayerEntity tempRolePlayerEntity = mRolePlayerEntity;
        if (tempRolePlayerEntity == null || mRolePlayMachinePager == null) {
            Loger.i("RolePlayerDemoTest", " roleplay界面已经销毁，数据为空，不再向下执行 ");
            return;
        }
        List<RolePlayerEntity.RolePlayerMessage> rolePlayerMessages = tempRolePlayerEntity.getLstRolePlayerMessage();
        for (int i = 0; i < rolePlayerMessages.size(); i++) {
            RolePlayerEntity.RolePlayerHead head = mRolePlayerEntity.getLstRolePlayerMessage().get(i).getRolePlayer();
            //if (!head.isSelfRole()) {
            mRolePlayerEntity.getLstRolePlayerMessage().get(i).setMsgStatus(RolePlayerEntity
                    .RolePlayerMessageStatus.CANCEL_DZ);
            mRolePlayMachinePager.updateRolePlayList(rolePlayerMessages.get(i));
            // }

        }
    }

    @Override
    public void onStopQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, String nonce) {
        Loger.i("RolePlayerDemoTest", "老师收题了,断开socket ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mBottomContent != null && mRolePlayMachinePager != null) {
                    mBottomContent.removeView(mRolePlayMachinePager.getRootView());
                    mRolePlayMachinePager.onDestroy();
                    mRolePlayMachinePager = null;
                    AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
                    if (audioRequest != null) {
                        audioRequest.release();
                    }
                    UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext,UpdateAchievement.class);
                    if (updateAchievement != null) {
                        updateAchievement.getStuGoldCount();
                    }
                }
            }
        });
    }
}

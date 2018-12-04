package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.speech.SpeechUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayMachinePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class RolePlayMachineBll extends RolePlayerBll implements RolePlayMachineAction{
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

    /**
     * 是否开始了人机
     */
    private boolean isGoToRobot;

    /**
     * 标记是否有权限进人机
     */
    private boolean isCanRolePlay = true;

    public RolePlayMachineBll(Context context, RelativeLayout bottomContent, LiveAndBackDebug liveBll, LiveGetInfo liveGetInfo) {
        super(context, bottomContent, liveBll, liveGetInfo);

        this.mLiveBll = liveBll;
        this.mLiveGetInfo = liveGetInfo;
        mLiveId = mLiveGetInfo.getId();
        mStuCouId = mLiveGetInfo.getStuCouId();
        mRolePlayerEntity = new RolePlayerEntity();
        mRolePlayerHttpManager = new RolePlayerHttpManager(mContext);
        mRolePlayerHttpResponseParser = new RolePlayerHttpResponseParser();

        //mBottomContent = bottomContent;

    }
    /**
     * 领读指令触发
     */
    @Override
    public void teacherRead(String liveId, String stuCouId, final String nonce) {
        logger.i( TAG+"人机领读");
        isGoToRobot = false;
        mRolePlayerEntity = null;
        this.mLiveId = liveId;
        this.mStuCouId = stuCouId;

        final List<PermissionItem> unList = new ArrayList<>();

        List<PermissionItem> unPermissionItems = XesPermission.checkPermissionUnPerList(mContext, new
                LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {
                        logger.i( "onFinish");

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        isCanRolePlay = false;
                        XESToastUtils.showToast(mContext, "没开启录音权限无法参与RolePlayer");

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        logger.i( "开启了" + permission + "权限");
                        unList.remove(0);
                        if (unList.isEmpty()) {
                            if (SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess()) {
                                isCanRolePlay = true;
                                logger.i( "开启了录音权限，且离线加载成功开始去人机");
                                if (isGoToRobot) {
                                    return;
                                }
                                goToRobot();
                            }else {
                                isCanRolePlay = false;
                            }
                        }

                    }
                }, PermissionConfig.PERMISSION_CODE_AUDIO);

        logger.i( "unpermissionItems " + unPermissionItems.size() + "  SpeechEvaluatorUtils" +
                ".isOfflineSuccess() = " + SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess());

        unList.addAll(unPermissionItems);
        if (unList.isEmpty()) {
            if (SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess()) {
                logger.i( "开启了录音拍照权限，且离线加载成功开始去人机");
                isCanRolePlay = true;
                if (isGoToRobot) {
                    return;
                }
                goToRobot();
            } else {
                isCanRolePlay = false;
                logger.i( "没有权限或者离线包失败,不能进行roleplay");
            }
        }


    }
    @Override
    public void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        //super.teacherPushTest(videoQuestionLiveEntity);
        if(!isCanRolePlay){
            logger.i( "没有权限或者离线包失败,不能进行roleplay");
            return;
        }

        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        //拉取试题
        boolean isNew = videoQuestionLiveEntity.isNewArtsH5Courseware();
        if(isNew){
            requestNewArtsTestInfos();
        }else{
            requestTestInfos();
        }
        //addPagerToWindow();
    }

    /**
     * 获取分组信息后去请求试题
     */
    @Override
    public void requestTestInfos() {
        if(videoQuestionLiveEntity != null){
            logger.i("请求试题信息 mRolePlayerEntity.toString() = " + videoQuestionLiveEntity.id);
        }

        mRolePlayerHttpManager.requestRolePlayTestInfos(mLiveId, mStuCouId, videoQuestionLiveEntity.id, new
                HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        mRolePlayerEntity = mRolePlayerHttpResponseParser.parserRolePlayGroupAndTestInfos(responseEntity);
                        if(responseEntity != null && responseEntity.getJsonObject() != null){
                            logger.i("人机服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                            logger.i( "人机服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                    ".getLstRolePlayerMessage()" +
                                    ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                    mRolePlayerEntity.toString());
                        }

                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        if(responseEntity != null){
                            logger.i( "onPmError:人机" + responseEntity.getErrorMsg());
                        }
                        //有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
                        pmErrorAfterpmSuccess();

                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.i( "onPmFailure:人机" + msg);
                    }
                });
    }

    /**
     * 有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
     */
    private void pmErrorAfterpmSuccess() {

        //有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
        if(mRolePlayerEntity == null){
            onStopQuestion(null,null);
        }else{
            List<RolePlayerEntity.RolePlayerHead> rolePlayerHeads = mRolePlayerEntity.getLstRoleInfo();
            List<RolePlayerEntity.RolePlayerMessage> rolePlayerMessages = mRolePlayerEntity.getLstRolePlayerMessage();
            if(rolePlayerHeads == null || rolePlayerMessages == null || rolePlayerHeads.size() <=0 || rolePlayerMessages.size() <= 0){
                //角色信息，或者试题信息没有的时候，结束当前界面
                onStopQuestion(null,null);
            }
        }
    }

    // 文科新课件平台获取分组和试题信息
    private void requestNewArtsTestInfos() {
        if (mRolePlayerEntity != null) {
            mRolePlayerHttpManager.requestNewArtsRolePlayTestInfos(mLiveId, mStuCouId, videoQuestionLiveEntity.id, mLiveGetInfo.getStuId(),new
                    HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            mRolePlayerEntity =  mRolePlayerHttpResponseParser.parserNewRolePlayGroupAndTestInfos(responseEntity);
                            if(responseEntity != null && responseEntity.getJsonObject() != null){
                                logger.i( "新课件平台人机分组和试题 服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                                logger.i( "服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                        ".getLstRolePlayerMessage()" +
                                        ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                        mRolePlayerEntity.toString());
                            }
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            if(responseEntity != null){
                                logger.i( "onPmError: 新课件平台人机分组和试题" + responseEntity.getErrorMsg());
                            }
                            //有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
                            pmErrorAfterpmSuccess();

                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            logger.i( "onPmFailure: 新课件平台人机分组和试题" + msg);
                        }
                    });
        }
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
            logger.i("mBottomContent = " + mBottomContent);
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
        logger.i( "提交结果");
        logger.i("用户提交结果,记录日志");
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
            logger.i("mStuCouId = " + mStuCouId + " mLiveId = " + mLiveId + " mRolePlayerEntity" +
                    ".getTestId() = " + mRolePlayerEntity.getTestId()
                    + " obj = " + obj.toString());

            mRolePlayerHttpManager.requestResult(mStuCouId, mLiveId, videoQuestionLiveEntity.id, roleName, obj
                    .toString(), new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    int gold = jsonObject.optInt("gold");
                    mRolePlayerEntity.setGoldCount(gold);
                    logger.i( "onPmSuccess: gold  =" + gold);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    logger.i( "onFailure: e.getMessage()  =" + e.getMessage() + "取消点赞");
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.i("onPmError ");
                    super.onPmError(responseEntity);
                    if(responseEntity != null){
                        logger.i("onPmError: " + responseEntity.getErrorMsg() + ":"+responseEntity.isJsonError());
                    }

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
            logger.i(" roleplay界面已经销毁，数据为空，不再向下执行 ");
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
        logger.i( "onStopQuestion 老师收题了,断开socket ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                closeCurPage();
            }
        });
    }

    @Override
    public void closeCurPage() {
        logger.i( "onStopQuestion 关闭当前页面 "+mBottomContent+":"+mRolePlayMachinePager);
        if (mBottomContent != null && mRolePlayMachinePager != null) {
            logger.i( "onStopQuestion 关闭当前页面 ");
            mBottomContent.removeView(mRolePlayMachinePager.getRootView());
            mRolePlayMachinePager.relaseCurrentPage();
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
    @Override
    public String getQuestionId() {
        if (videoQuestionLiveEntity != null) {
            return videoQuestionLiveEntity.id;
        }
        return "";
    }

    public void setBottomView(RelativeLayout bottomView) {
        this.mBottomContent = bottomView;
    }
}

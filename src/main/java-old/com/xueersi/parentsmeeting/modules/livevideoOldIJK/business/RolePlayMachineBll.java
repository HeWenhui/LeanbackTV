package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.VoiceAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.RolePlayMachinePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.RolePlayStandMachinePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.RolePlayLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class RolePlayMachineBll extends RolePlayerBll implements RolePlayMachineAction {
    private RolePlayerHttpManager mRolePlayerHttpManager;
    private RolePlayerHttpResponseParser mRolePlayerHttpResponseParser;

    /**
     * 直播基础BLL
     */
    private LiveAndBackDebug mLiveBll;

    private final LiveGetInfo mLiveGetInfo;
    /**
     * ture 直播，false 回放
     */
    private boolean mIsLive;
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

    RolePlayStandMachinePager mRolePlayStandMachinePager;

    /**
     * 是否开始了人机
     */
    private boolean isGoToRobot;

    /**
     * 标记是否有权限进人机
     */
    private boolean isCanRolePlay = true;

    public RolePlayMachineBll(Context context, RelativeLayout bottomContent, LiveAndBackDebug liveBll, LiveGetInfo liveGetInfo, boolean islive) {
        super(context, bottomContent, liveBll, liveGetInfo);
        mIsLive = islive;
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
        logger.i(TAG + "人机领读");
        isGoToRobot = false;
        mRolePlayerEntity = null;
        this.mLiveId = liveId;
        this.mStuCouId = stuCouId;

        final List<PermissionItem> unList = new ArrayList<>();

        List<PermissionItem> unPermissionItems = XesPermission.checkPermissionUnPerList(mContext, new
                LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {
                        logger.i("onFinish");

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        isCanRolePlay = false;
                        XESToastUtils.showToast(mContext, "没开启录音权限无法参与RolePlayer");

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        logger.i("开启了" + permission + "权限");
                        unList.remove(0);
                        if (unList.isEmpty()) {
                            if (SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess()) {
                                isCanRolePlay = true;
                                logger.i("开启了录音权限，且离线加载成功开始去人机");
                                if (isGoToRobot) {
                                    return;
                                }
                                goToRobot();
                            } else {
                                isCanRolePlay = false;
                            }
                        }

                    }
                }, PermissionConfig.PERMISSION_CODE_AUDIO);

        logger.i("unpermissionItems " + unPermissionItems.size() + "  SpeechEvaluatorUtils" +
                ".isOfflineSuccess() = " + SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess());

        unList.addAll(unPermissionItems);
        if (unList.isEmpty()) {
            if (SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess()) {
                logger.i("开启了录音拍照权限，且离线加载成功开始去人机");
                isCanRolePlay = true;
                if (isGoToRobot) {
                    return;
                }
                goToRobot();
            } else {
                isCanRolePlay = false;
                logger.i("没有权限或者离线包失败,不能进行roleplay");
            }
        }


    }

    @Override
    public void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        //super.teacherPushTest(videoQuestionLiveEntity);
        if (!isCanRolePlay) {
            logger.i("没有权限或者离线包失败,不能进行roleplay");
            return;
        }

        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        //拉取试题
        boolean isNew = videoQuestionLiveEntity.isNewArtsH5Courseware();
        if (isNew) {
            requestNewArtsTestInfos();
        } else {
            requestTestInfos();
        }
        //addPagerToWindow();
    }

    /**
     * 获取分组信息后去请求试题
     */
    @Override
    public void requestTestInfos() {
        if (videoQuestionLiveEntity != null) {
            logger.i("请求试题信息 mRolePlayerEntity.toString() = " + videoQuestionLiveEntity.id);
        }

        mRolePlayerHttpManager.requestRolePlayTestInfos(mLiveId, mStuCouId, videoQuestionLiveEntity.id, new
                HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        mRolePlayerEntity = mRolePlayerHttpResponseParser.parserRolePlayGroupAndTestInfos(responseEntity);
                        if (responseEntity != null && responseEntity.getJsonObject() != null) {
                            logger.i("人机服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                            logger.i("人机服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                    ".getLstRolePlayerMessage()" +
                                    ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                    mRolePlayerEntity.toString());
                            //只在人机的时候，在数据回来后展示对话
                            if(mRolePlayMachinePager != null){
                                mRolePlayMachinePager.initData();
                            }
                            if(mRolePlayStandMachinePager != null){
                                mRolePlayStandMachinePager.initData();
                            }
                        }

                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        if (responseEntity != null) {
                            logger.i("onPmError:人机" + responseEntity.getErrorMsg());
                        }
                        if (mRolePlayerEntity == null) {
                            //有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
                            pmErrorAfterpmSuccess();
                        }

                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.i("onPmFailure:人机" + msg);
                    }
                });
    }

    /**
     * 有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
     */
    private void pmErrorAfterpmSuccess() {

        //有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
        if (mRolePlayerEntity == null) {
            logger.i("pmErrorAfterpmSuccess");
            onStopQuestion(null, null);
        } else {
            List<RolePlayerEntity.RolePlayerHead> rolePlayerHeads = mRolePlayerEntity.getLstRoleInfo();
            List<RolePlayerEntity.RolePlayerMessage> rolePlayerMessages = mRolePlayerEntity.getLstRolePlayerMessage();
            if (rolePlayerHeads == null || rolePlayerMessages == null || rolePlayerHeads.size() <= 0 || rolePlayerMessages.size() <= 0) {
                logger.i("pmErrorAfterpmSuccess");
                //角色信息，或者试题信息没有的时候，结束当前界面
                onStopQuestion(null, null);
            }
        }
    }

    // 文科新课件平台获取分组和试题信息
    private void requestNewArtsTestInfos() {
        if (mRolePlayerEntity != null) {
            mRolePlayerHttpManager.requestNewArtsRolePlayTestInfos(mLiveId, mStuCouId, videoQuestionLiveEntity.id, mLiveGetInfo.getStuId(), new
                    HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            mRolePlayerEntity = mRolePlayerHttpResponseParser.parserNewRolePlayGroupAndTestInfos(responseEntity);
                            if (responseEntity != null && responseEntity.getJsonObject() != null) {
                                logger.i("新课件平台人机分组和试题 服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                               if(mRolePlayerEntity != null){
                                   logger.i("服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                           ".getLstRolePlayerMessage()" +
                                           ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                           mRolePlayerEntity.toString());
                                   //只在人机的时候，在数据回来后展示对话
                                   if(mRolePlayStandMachinePager != null){
                                       mRolePlayStandMachinePager.initData();
                                   }
                                   if(mRolePlayMachinePager != null){
                                       mRolePlayMachinePager.initData();
                                   }

                               }

                            }
                            logger.i("onPmSuccess" + mRolePlayerEntity+":"+responseEntity);
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            if (responseEntity != null) {
                                logger.i("onPmError: 新课件平台人机分组和试题" + responseEntity.getErrorMsg());
                            }
                            if (mRolePlayerEntity == null) {
                                //有时会发生onPmSuccess执行之后onPmError又回调导致，无法进入roleplay的问题
                                pmErrorAfterpmSuccess();
                            }

                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            logger.i("onPmFailure: 新课件平台人机分组和试题" + msg);
                        }
                    });
        }
    }


    public RolePlayerEntity getRoleEntry() {
        return mRolePlayerEntity;
    }

    @Override
    public synchronized void requestResult() {
        logger.i("提交结果");
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

                if (message.getRolePlayer().isSelfRole()) {
                    JSONObject objAn = new JSONObject();
                    JSONObject objData = new JSONObject();
                    objAn.put("sentenceNum", i);
                    objAn.put("entranceTime", (int)message.getSelfValidSpeechTime());
                    objAn.put("score", message.getSpeechScore());
                    objData.put("cont_score", message.getFluency());
                    objData.put("pron_score", message.getAccuracy());
                    objData.put("total_score", message.getSpeechScore());
                    objData.put("level", message.getLevel());
                    objAn.put("alldata", objData);
                    arrAnswer.put(objAn);
                }

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
                    int energy = jsonObject.optInt("energy");
                    mRolePlayerEntity.setGoldCount(gold);
                    mRolePlayerEntity.setJson(jsonObject);
                    mRolePlayerEntity.setEnergy(energy);
                    logger.i("onPmSuccess: gold  =" + gold+",energy="+energy);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    logger.i("onFailure: e.getMessage()  =" + e.getMessage() + "取消点赞");
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.i("onPmError ");
                    super.onPmError(responseEntity);
                    if (responseEntity != null) {
                        logger.i("onPmError: " + responseEntity.getErrorMsg() + ":" + responseEntity.isJsonError());
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 文科新课件平台提交结果
     */
    public synchronized void requestNewArtsResult() {
        logger.i("提交结果");
        logger.i("用户提交结果,记录日志");
        //提交结果的时候，记录日志信息
        RolePlayLog.sno6(mLiveBll, mRolePlayerEntity, mContext);
        mRolePlayerEntity.setResult(true);
        JSONObject obj = new JSONObject();
        try {
            RolePlayerEntity.RolePlayerHead rolePlayerHead = mRolePlayerEntity.getSelfRoleHead();
            String roleName = null;
            if (rolePlayerHead != null) {
                roleName = rolePlayerHead.getRoleName();
                logger.i("提交结果:"+roleName+":"+rolePlayerHead.getNickName()+
                        ":"+rolePlayerHead.getRoleName()+":"+rolePlayerHead.getSpeechScore()
                );
            }
            obj.put("type", mIsLive ? 1 : 2);
            obj.put("roler", roleName);
            JSONArray arrAnswer = new JSONArray();
            //TODO:
            int i = 1;
            for (RolePlayerEntity.RolePlayerMessage message : mRolePlayerEntity.getLstRolePlayerMessage()) {


                if (message.getRolePlayer().isSelfRole()) {
                    JSONObject objAn = new JSONObject();
                    objAn.put("sentenceNum", i);
                    objAn.put("entranceTime", (int)message.getSelfValidSpeechTime());
                    objAn.put("score", message.getSpeechScore());
                    JSONObject objData = new JSONObject();
                    objData.put("cont_score", message.getFluency());
                    objData.put("pron_score", message.getAccuracy());
                    objData.put("total_score", message.getSpeechScore());
                    objData.put("level", message.getLevel());
                    objAn.put("alldata", objData);
                    arrAnswer.put(objAn);
                }

                i++;
            }
            obj.put("answers", arrAnswer);
            logger.i("mStuCouId = " + mStuCouId + " mLiveId = " + mLiveId + " mRolePlayerEntity" +
                    ".getTestId() = " + mRolePlayerEntity.getTestId()
                    + " obj = " + obj.toString());

            mRolePlayerHttpManager.requestNewArtsResult(mStuCouId, mLiveId, mRolePlayerEntity.getTestId(), roleName, obj
                    .toString(), mIsLive ? 1 : 2, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    int gold = jsonObject.optInt("gold");
                    int scores = jsonObject.optInt("scores");
                    int energy = jsonObject.optInt("energy");
                    mRolePlayerEntity.setGoldCount(gold);
                    mRolePlayerEntity.setJson(jsonObject);
                    mRolePlayerEntity.setEnergy(energy);
                    // 发送已答过的状态
                    EventBus.getDefault().post(new ArtsAnswerResultEvent(mRolePlayerEntity.getTestId(), ArtsAnswerResultEvent.TYPE_NATIVE_ANSWERRESULT));
                    EventBus.getDefault().post(new VoiceAnswerResultEvent(mRolePlayerEntity.getTestId(), scores));
                    logger.i("onPmSuccess: gold  =" + gold);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    logger.i("onFailure: e.getMessage()  =" + e.getMessage() + "取消点赞");
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.i("onPmError: responseEntity=" + responseEntity.getErrorMsg() + ",提交结果失败，但是要释放资源");
                    super.onPmError(responseEntity);
                    if (mRolePlayMachinePager != null) {
                        mRolePlayMachinePager.recoverListScrollAndCancelDZ();
                    }
                    if (mRolePlayStandMachinePager != null) {
                        mRolePlayStandMachinePager.recoverListScrollAndCancelDZ();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStopQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, String nonce) {
        mLogtf.i("onStopQuestion 老师收题了,断开socket,this=" + hashCode());
        if (mRolePlayMachinePager != null) {
            mRolePlayMachinePager.stopSpeech();
        }
        if (mRolePlayStandMachinePager != null) {
            mRolePlayStandMachinePager.stopSpeech();
        }
        mHertHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeCurPage();
            }
        }, 200);
    }

    @Override
    public void closeCurPage() {
        mLogtf.i("closeCurPage:bottomContent=null?" + (mBottomContent == null) + ",pager=null?" + (mRolePlayMachinePager == null) + "," + (mRolePlayStandMachinePager == null));
        if (mBottomContent != null && mRolePlayMachinePager != null) {
            logger.i("onStopQuestion 关闭当前页面 ");
            //让pager自己移除
//            mBottomContent.removeView(mRolePlayMachinePager.getRootView());
            mRolePlayMachinePager.relaseCurrentPage();
            mRolePlayMachinePager.onDestroy();
            mRolePlayMachinePager = null;
            AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
            if (audioRequest != null) {
                audioRequest.release();
            }
            //这里不再请求金币
//            UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
//            if (updateAchievement != null) {
//                updateAchievement.getStuGoldCount();
//            }
        }
        if (mBottomContent != null && mRolePlayStandMachinePager != null) {
            logger.i("onStopQuestion 关闭当前页面 ");
            //让pager自己移除
//            mBottomContent.removeView(mRolePlayMachinePager.getRootView());
            mRolePlayStandMachinePager.relaseCurrentPage();
            mRolePlayStandMachinePager.onDestroy();
            mRolePlayStandMachinePager = null;
            AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
            if (audioRequest != null) {
                audioRequest.release();
            }
            //这里不再请求金币
//            UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
//            if (updateAchievement != null) {
//                updateAchievement.getStuGoldCount();
//            }
        }
        mBottomContent = null;
        mRolePlayMachinePager = null;
        mRolePlayStandMachinePager = null;
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

    /**
     * 设置人机的pager
     *
     * @param rolePlayMachinePager
     */
    public void setRolePlayMachinePager(RolePlayMachinePager rolePlayMachinePager) {
        this.mRolePlayMachinePager = rolePlayMachinePager;

    }

    /**
     * 站立式直播的人机pager
     *
     * @param rolePlayStandMachinePager
     */
    public void setRolePlayStandMachinePager(RolePlayStandMachinePager rolePlayStandMachinePager) {
        this.mRolePlayStandMachinePager = rolePlayStandMachinePager;
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

    public void cancelStandLiveDZ() {
        RolePlayerEntity tempRolePlayerEntity = mRolePlayerEntity;
        if (tempRolePlayerEntity == null || mRolePlayStandMachinePager == null) {
            logger.i(" roleplay界面已经销毁，数据为空，不再向下执行 ");
            return;
        }
        List<RolePlayerEntity.RolePlayerMessage> rolePlayerMessages = tempRolePlayerEntity.getLstRolePlayerMessage();
        for (int i = 0; i < rolePlayerMessages.size(); i++) {
            RolePlayerEntity.RolePlayerHead head = mRolePlayerEntity.getLstRolePlayerMessage().get(i).getRolePlayer();
            //if (!head.isSelfRole()) {
            mRolePlayerEntity.getLstRolePlayerMessage().get(i).setMsgStatus(RolePlayerEntity
                    .RolePlayerMessageStatus.CANCEL_DZ);
            mRolePlayStandMachinePager.updateRolePlayList(rolePlayerMessages.get(i));
            // }
        }
    }
}

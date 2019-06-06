package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.VoiceAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.RolePlayerPager;
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


/**
 * RolePlayer业务类
 * Created by zouhao on 2018/4/12.
 */
public class RolePlayerBll extends BaseBll implements RolePlayAction {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    protected LogToFile mLogtf;
    private final LiveGetInfo mLiveGetInfo;
    /**
     * 是否已经通过权限判断进入连接WebSocket
     */
    private boolean isBeginConnWebSocket;

    /**
     * WebSocket连接类
     */
    private WebSocketConn mWebSocket;
    /**
     * 麦克风权限拒绝；没有分组；没有试题返回的时候的错误回调，帮助进人机
     */
    OnError onError;
    /**
     * 分组成功之后,通知直接多人，不再走人机的逻辑
     */
    OnGroupSuc onGroupSuc;
    /**
     * 连接地址
     */
    private String webSocketUrl = "ws://wsarts.xueersi" +
            ".com/roleplay/index?userId=%1$s&role=1&cookie=%2$s&liveId=%3$s&xes_rfh=%4$s";

    /**
     * RolePlayer数据实体
     */
    private RolePlayerEntity mRolePlayerEntity;

    /**
     * 直播ID
     */
    private String mLiveId;
    /**
     * 购课ID
     */
    private String mStuCouId;
    VideoQuestionLiveEntity videoQuestionLiveEntity;
    /**
     * 基础布局
     */
    private RelativeLayout bottomContent;
    /**
     * 直播基础BLL
     */
    private LiveAndBackDebug mLiveBll;

    private RolePlayerPager mRolePlayerPager;

    private RolePlayerHttpManager mRolePlayerHttpManager;

    private RolePlayerHttpResponseParser mRolePlayerHttpResponseParser;

    private boolean isGoToRobot;//是否开始了人机

    public RolePlayerBll(Context context, RelativeLayout bottomContent, LiveAndBackDebug liveBll, LiveGetInfo liveGetInfo) {
        super(context);
        this.bottomContent = bottomContent;
        this.mLiveBll = liveBll;
        this.mLiveGetInfo = liveGetInfo;
        mRolePlayerHttpManager = new RolePlayerHttpManager(mContext);
        mRolePlayerHttpResponseParser = new RolePlayerHttpResponseParser();
        mLogtf=new LogToFile(context, getClass().getSimpleName());
    }

    /**
     * 领读指令触发
     */
    @Override
    public void teacherRead(String liveId, String stuCouId, final String nonce) {
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

                        if (isGoToRobot) {
                            return;
                        }
                        XESToastUtils.showToast(mContext, "没开启录音权限无法参与RolePlayer");
                        logger.i( "没开启录音权限无法参与RolePlayer");
                        goToRobot();

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        logger.i( "开启了" + permission + "权限");
                        unList.remove(0);
                        if (unList.isEmpty()) {
                            if (SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess()) {
                                logger.i( "开启了录音拍照权限，且离线加载成功开始去请求分组");
                                beginConWebSocket(nonce);
                            } else {
                                if (isGoToRobot) {
                                    return;
                                }
                                logger.i( "没有权限或者离线包失败，走人机");
                                goToRobot();
                            }
                        }

                    }
                }, PermissionConfig.PERMISSION_CODE_AUDIO, PermissionConfig.PERMISSION_CODE_CAMERA);

        logger.i( "unpermissionItems " + unPermissionItems.size() + "  SpeechEvaluatorUtils" +
                ".isOfflineSuccess() = " + SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess());

        unList.addAll(unPermissionItems);
        if (unList.isEmpty()) {
            if (SpeechUtils.getInstance(mContext.getApplicationContext()).isOfflineSuccess()) {
                logger.i( "开启了录音拍照权限，且离线加载成功开始去请求分组");
                beginConWebSocket(nonce);
            } else {
                if (isGoToRobot) {
                    return;
                }
                logger.i( "没有权限或者离线包失败，走人机");
                goToRobot();
            }
        }


    }

    /**
     * 教师发题指令
     */
    @Override
    public void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        //拉取试题a
        if(videoQuestionLiveEntity.isNewArtsH5Courseware()){
            requestNewArtsTestInfos();
        }else{
            requestTestInfos();
        }
        mLogtf.d( "teacherPushTest:mRolePlayerEntity=null?"+(mRolePlayerEntity==null));
        if(mRolePlayerPager == null){
            mRolePlayerPager = new RolePlayerPager(mContext, mRolePlayerEntity, true, this, mLiveGetInfo);
            mRolePlayerPager.initData();
            if (bottomContent != null) {
                bottomContent.addView(mRolePlayerPager.getRootView());
            }
        }
        //用户弹出答题框
        logger.i( "用户弹出答题框,记录日志");
        RolePlayLog.sno4(mLiveBll, videoQuestionLiveEntity, mContext);

    }

    // 文科新课件平台获取试题信息
    private void requestNewArtsTestInfos() {
        if (mRolePlayerEntity != null) {
            mRolePlayerHttpManager.requestNewArtsRolePlayTestInfos(mLiveId, mStuCouId, mRolePlayerEntity.getTestId(), mLiveGetInfo.getStuId(),new
                    HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            mRolePlayerEntity = mRolePlayerHttpResponseParser.parserNewArtsMutRolePlayTestInfos(responseEntity, mRolePlayerEntity);
                            if(responseEntity != null && responseEntity.getJsonObject() != null){
                                logger.i( "多人新课件服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                                mLogtf.i( "多人新课件服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                        ".getLstRolePlayerMessage()" +
                                        ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                        mRolePlayerEntity.toString());
                            }

                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            if(responseEntity != null){
                                mLogtf.i( "onPmError:多人新课件" + responseEntity.getErrorMsg());
                            }

                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            mLogtf.i( "onPmFailure:多人新课件" + msg);
                        }
                    });
        }
    }

    @Override
    public String getQuestionId() {
        if (videoQuestionLiveEntity != null) {
            return videoQuestionLiveEntity.id;
        }
        return "";
    }

    @Override
    public void onStopQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, String nonce) {
        mLogtf.i( "onStopQuestion 老师收题了,断开socket,this="+hashCode());
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
            mWebSocket = null;
        }
        if(mRolePlayerPager != null){
            mRolePlayerPager.stopSpeech();
        }
        mHertHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeCurPage();
            }
        },200);
    }

    /**
     * 关掉当前页面
     */
    public void closeCurPage() {
        mLogtf.i( "closeCurPage:bottomContent=null?"+(bottomContent==null)+",pager=null?"+(mRolePlayerPager==null));
        if (bottomContent != null && mRolePlayerPager != null) {
            logger.i( "onStopQuestion 关闭当前页面 ");
            bottomContent.removeView(mRolePlayerPager.getRootView());
            mRolePlayerPager.onDestroy();
            mRolePlayerPager = null;
            AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
            if (audioRequest != null) {
                audioRequest.release();
            }
            UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext,UpdateAchievement.class);
            if (updateAchievement != null) {
                updateAchievement.getStuGoldCount("closeCurPage", UpdateAchievement.GET_TYPE_QUE);
            }
        }
        //bottomContent = null;
       // mRolePlayerPager = null;
    }

    /**
     * 帮助进入人机的错误回调
     *
     * @param onError
     */
    @Override
    public void setOnError(OnError onError) {
        this.onError = onError;
    }

    @Override
    public void setOnGroupSuc(OnGroupSuc onGroupSuc) {
        this.onGroupSuc = onGroupSuc;
    }


    /**
     * 进人机
     */
    @Override
    public void goToRobot() {
        isGoToRobot = true;
        mLogtf.d( "进人机");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //移除roleplay界面，并释放该界面资源
                if (bottomContent != null && mRolePlayerPager != null) {
                    bottomContent.removeView(mRolePlayerPager.getRootView());
                    mRolePlayerPager.onDestroy();
                    mRolePlayerPager = null;
                    logger.d( "移除了原生页面");
                }
            }
        });
        if (videoQuestionLiveEntity != null) {
            VideoQuestionLiveEntity oldvideoQuestionLiveEntity = videoQuestionLiveEntity;
            oldvideoQuestionLiveEntity.multiRolePlay = "0";
            videoQuestionLiveEntity = null;
            onError.onError(oldvideoQuestionLiveEntity);

        }
    }

    @Override
    public void onGoToRobot() {
        logger.d( "进入人机；断开socket");
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
            mWebSocket = null;
        }
    }


    /**
     * 开始连接WebSocket
     *
     * @param nonce
     */
    private void beginConWebSocket(final String nonce) {
        if (isBeginConnWebSocket) {
            return;
        }

        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
        }

        mWebSocket = new WebSocketConn();
        webSocketUrl = String.format(webSocketUrl, UserBll.getInstance().getMyUserInfoEntity().getStuId(), AppBll
                .getInstance().getUserToken(), mLiveId, AppBll.getInstance().getUserRfh());
        //webSocketUrl = String.format(webSocketUrl, "1237", "1111111", "1234");
        logger.i( "websocket:" + webSocketUrl);
        mWebSocket.connect(webSocketUrl, new WebSocketConn.WebSocketCallBack() {
            @Override
            public void onOpen() {
                isBeginConnWebSocket = true;
                logger.i( "open");
                logger.i( "学生连接socket成功,记录日志");
                RolePlayLog.sno2(mLiveBll, mContext, nonce);

            }

            @Override
            public void onMessage(String result) {
                //logger.i( "result:" + result);
                onMessageParse(result);
            }


            @Override
            public void onClose() {
                logger.i( "close");
                isBeginConnWebSocket = false;
            }

            @Override
            public void onError(Throwable throwable) {
                logger.i( "onError");
                isBeginConnWebSocket = false;
            }
        });
    }


    /**
     * 心跳每隔10秒发一次
     */
    private final int HERT_PING = 100;
    Handler mHertHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HERT_PING) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("msg", "");
                    sendWebSMessage(2, 0, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                mHertHandler.sendEmptyMessageDelayed(HERT_PING, 10000);
            }
        }
    };

    /**
     * 发送心跳
     */
    private void hertPing() {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mHertHandler.removeMessages(HERT_PING);
            mHertHandler.sendEmptyMessageDelayed(HERT_PING, 10000);
        }
    }

    /**
     * 发送消息
     *
     * @param acid
     * @param to
     * @param msgObj
     */
    private void sendWebSMessage(int acid, int to, JSONObject msgObj) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("acid", acid);
            JSONArray arr = new JSONArray();
            arr.put(to);
            obj.put("to", arr);
            obj.put("msg", msgObj.toString());
            if (mWebSocket != null && mWebSocket.isOpen()) {
                mWebSocket.sendMsg(obj.toString().getBytes("utf-8"));
                //mWebSocket.sendMsg(testData.toString().getBytes("utf-8"));
                logger.i( "send :" + obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息，此方法给需要通知组内成员的接口调用
     *
     * @param acid
     * @param tos        小组成员信息,用于获取小组内所有角色suid
     * @param selfRoleId 当前用户的suid
     * @param msgObj
     */
    private void sendWebSMessage(int acid, List<RolePlayerEntity.RolePlayerHead> tos, int selfRoleId, JSONObject
            msgObj) {
        logger.i( "sendWebSMessage : acid = " + acid + " tos.size() = " + tos.size() + " " +
                "selfRoleId = " + selfRoleId);
        JSONObject obj = new JSONObject();
        try {
            obj.put("acid", acid);
            JSONArray arr = new JSONArray();
            for (RolePlayerEntity.RolePlayerHead to : tos) {
                if (to.getRoleId() != selfRoleId) {
                    //通知小组内除了自己的组员
                    arr.put(to.getRoleId());
                }
            }

            obj.put("to", arr);
            obj.put("msg", msgObj.toString());
            if (mWebSocket != null && mWebSocket.isOpen()) {
                mWebSocket.sendMsg(obj.toString().getBytes("utf-8"));
                //mWebSocket.sendMsg(testData.toString().getBytes("utf-8"));
                logger.i( "send :" + obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * webSocket连接成功
     */
    public void connectSuccess(int suid) {
        if (suid != 0) {
            if ((mRolePlayerEntity == null || mRolePlayerEntity.getLstRoleInfo().size() < 0 || mRolePlayerEntity
                    .getLstRolePlayerMessage().size() < 0)) {
                mRolePlayerEntity = new RolePlayerEntity();
                mRolePlayerEntity.setSelfRoleId(suid);
                mRolePlayerEntity.setLiveId(Integer.parseInt(mLiveId));

            }
            hertPing();

        }


    }

    /**
     * 正常的消息解析
     *
     * @param result
     */
    private void onMessageParse(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            int acid = jsonObject.optInt("acid");
            int from = jsonObject.optInt("from");
            String str = jsonObject.optString("msg");
            JSONObject msgObj = new JSONObject(str);
            //JSONObject msgObj = jsonObject.getJSONObject("msg");
            //logger.i( "parse 开始解析消息 acid = " + acid + " from " + from + " msg " + msgObj.toString());
            switch (acid) {
                case 1:
                    //边接成功，获取stuid
                    connectSuccess(msgObj.optInt("suid"));
                    break;
                case 2:
                    //心跳返回
                    break;
                case 4:
                    //用户自定义消息
                    int type = msgObj.optInt("type");
                    logger.i( "收到用户自定义消息 type = " + type);
                    switch (type) {
                        case 120:
                            //收到对方的MP3文件地址
                            String mp3Url = msgObj.optString("content");
                            logger.i( "收到 " + from + " 的MP3文件地址 " + mp3Url + " mRolePlayerEntity" +
                                    ".getLstRolePlayerMessage().size() = " + mRolePlayerEntity
                                    .getLstRolePlayerMessage().size());
                            int index = msgObj.optInt("index");
                            if (!TextUtils.isEmpty(mp3Url) && index >= 0 && index < mRolePlayerEntity
                                    .getLstRolePlayerMessage().size()) {
                                mRolePlayerEntity.getLstRolePlayerMessage().get(index).setWebVoiceUrl(mp3Url);
                            }
                            break;
                        case 100:
                            //收到对方的点赞
                            logger.i( "收到 " + from + " 的点赞 mRolePlayerEntity.getLstRoleInfo()" +
                                    ".size() = " + mRolePlayerEntity.getLstRoleInfo().size());
                            for (RolePlayerEntity.RolePlayerHead head : mRolePlayerEntity.getLstRoleInfo()) {
                                if (head.getRoleId() == from) {
                                    final RolePlayerEntity.RolePlayerHead mHead = head;
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mHead != null) {
                                                mRolePlayerPager.showDZ(mHead.getNickName());
                                            }
                                        }
                                    });
                                    break;
                                }
                            }


                            break;
                        case 110:
                            //收到对方读完的消息

                            final int position = msgObj.optInt("index");

                            String strData = msgObj.optString("data");
                            JSONObject jsonData = new JSONObject(strData);
                            int totalScore = jsonData.optInt("totalScore");
                            int fluency = jsonData.optInt("fluency");
                            int accuracy = jsonData.optInt("accuracy");

                            logger.i( "收到 " + from + " 读完 " + position + " totalScore = " +
                                    totalScore + " fluency = " + fluency + " accuracy = " + accuracy);

                            if (position >= 0 && position < mRolePlayerEntity
                                    .getLstRolePlayerMessage().size()) {
                                final RolePlayerEntity.RolePlayerMessage message = mRolePlayerEntity
                                        .getMessageByIndex(position);
                                if (message != null) {
                                    message.setSpeechScore(totalScore);
                                    message.setFluency(fluency);
                                    message.setAccuracy(accuracy);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRolePlayerPager.updateRolePlayList(message);
                                            //对方提前读完
                                            mRolePlayerPager.nextRextMessage(position);
                                        }
                                    });

                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case 2000:
                    //分组结果
                    logger.i( "group success result:" + result);
                    if (mRolePlayerEntity != null) {

                        mRolePlayerEntity.getLstRoleInfo().clear();
                        String testId = msgObj.optString("testId");

                        mRolePlayerEntity.setTestId(testId);
                        mRolePlayerEntity.setTeamId(msgObj.optInt("team"));
                        JSONArray arrRole = msgObj.optJSONArray("teamUsers");
                        if (arrRole != null && arrRole.length() > 0) {
                            for (int i = 0; i < arrRole.length(); i++) {
                                JSONObject objRole = arrRole.getJSONObject(i);
                                RolePlayerEntity.RolePlayerHead head = new RolePlayerEntity.RolePlayerHead();
                                head.setNickName(objRole.optString("name"));
                                head.setHeadImg(objRole.optString("img"));
                                head.setRoleName(objRole.optString("role"));
                                head.setRoleId(objRole.optInt("suid"));
                                if (head.getRoleId() == mRolePlayerEntity.getSelfRoleId()) {
                                    head.setSelfRole(true);
                                }
                                mRolePlayerEntity.getMapRoleHeadInfo().put(head.getRoleName(), head);
                                mRolePlayerEntity.getLstRoleInfo().add(head);
                            }
                            //分组成功的回调，通知，QuestionBll进多人
                            if(onGroupSuc != null){
                                onGroupSuc.onGroupSuc();
                                logger.d("oldijk multi_people_onGroupSuc:callback send");
                            }
                        }


                    }
                    break;
                case -1:
                    //异常情况
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            logger.i( "onMessageParse JSONException : " + e.getMessage());
        }

    }


    /**
     * 获取分组信息后去请求试题
     */
    public void requestTestInfos() {
        if (mRolePlayerEntity != null) {
            mRolePlayerHttpManager.requestRolePlayTestInfos(mLiveId, mStuCouId, mRolePlayerEntity.getTestId(), new
                    HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            mRolePlayerHttpResponseParser.parserMutRolePlayTestInfos(responseEntity, mRolePlayerEntity);
                            if(responseEntity != null && responseEntity.getJsonObject() != null){
                                logger.i( "多人服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                                logger.i( "多人服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                        ".getLstRolePlayerMessage()" +
                                        ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                        mRolePlayerEntity.toString());
                            }

                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            if(responseEntity != null){
                                logger.i( "onPmError:多人" + responseEntity.getErrorMsg());
                            }

                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            logger.i( "onPmFailure:多人" + msg);
                        }
                    });
        }
    }


    /**
     * 提交结果
     */
    public synchronized void requestResult() {
        logger.i( "提交结果");
        logger.i( "用户提交结果,记录日志");
        //提交结果的时候，记录日志信息
        RolePlayLog.sno6(mLiveBll, mRolePlayerEntity, mContext);
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
            logger.i( "mStuCouId = " + mStuCouId + " mLiveId = " + mLiveId + " mRolePlayerEntity" +
                    ".getTestId() = " + mRolePlayerEntity.getTestId()
                    + " obj = " + obj.toString());

            mRolePlayerHttpManager.requestResult(mStuCouId, mLiveId, mRolePlayerEntity.getTestId(), roleName, obj
                    .toString(), new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    int gold = jsonObject.optInt("gold");
                    int energy = jsonObject.optInt("energy");
                    mRolePlayerEntity.setGoldCount(gold);
                    mRolePlayerEntity.setEnergy(energy);
                    logger.i( "onPmSuccess: gold  =" + gold+",energy="+energy);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    logger.i( "onFailure: e.getMessage()  =" + e.getMessage() + "取消点赞");
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.i( "onPmError: responseEntity.toString()  =" + responseEntity.toString
                            () + "提交结果失败，但是要释放资源");
                    super.onPmError(responseEntity);
                    if (mRolePlayerPager != null) {
                        mRolePlayerPager.recoverListScrollAndCancelDZ();
                        mRolePlayerPager.leaveChannel();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public RolePlayerEntity getRoleEntry() {
        return mRolePlayerEntity;
    }

    /**
     * 文科新课件平台提交结果
     */
    public synchronized void requestNewArtsResult() {
        logger.i( "提交结果");
        logger.i( "用户提交结果,记录日志");
        //提交结果的时候，记录日志信息
        RolePlayLog.sno6(mLiveBll, mRolePlayerEntity, mContext);
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
            logger.i( "mStuCouId = " + mStuCouId + " mLiveId = " + mLiveId + " mRolePlayerEntity" +
                    ".getTestId() = " + mRolePlayerEntity.getTestId()
                    + " obj = " + obj.toString());

            mRolePlayerHttpManager.requestNewArtsResult(mStuCouId, mLiveId, mRolePlayerEntity.getTestId(), roleName, obj
                    .toString(), 1, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    int gold = jsonObject.optInt("gold");
                    int scores = jsonObject.optInt("scores");
                    int energy = jsonObject.optInt("energy");
                    mRolePlayerEntity.setGoldCount(gold);
                    mRolePlayerEntity.setEnergy(energy);
                    // 发送已答过的状态
                    EventBus.getDefault().post(new ArtsAnswerResultEvent(mRolePlayerEntity.getTestId(),ArtsAnswerResultEvent.TYPE_NATIVE_ANSWERRESULT));
                    EventBus.getDefault().post(new VoiceAnswerResultEvent(mRolePlayerEntity.getTestId(),scores));
                    logger.i( "onPmSuccess: gold  =" + gold);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    logger.i( "onFailure: e.getMessage()  =" + e.getMessage() + "取消点赞");
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    logger.i( "onPmError: responseEntity.toString()  =" + responseEntity.toString
                            () + "提交结果失败，但是要释放资源");
                    super.onPmError(responseEntity);
                    if (mRolePlayerPager != null) {
                        mRolePlayerPager.recoverListScrollAndCancelDZ();
                        mRolePlayerPager.leaveChannel();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 返回结果之后，去掉点赞按钮
     */
    public synchronized void cancelDZ() {

        RolePlayerEntity tempRolePlayerEntity = mRolePlayerEntity;
        if (tempRolePlayerEntity == null || mRolePlayerPager == null) {
            logger.i( " roleplay界面已经销毁，数据为空，不再向下执行 ");
            return;
        }
        List<RolePlayerEntity.RolePlayerMessage> rolePlayerMessages = tempRolePlayerEntity.getLstRolePlayerMessage();
        for (int i = 0; i < rolePlayerMessages.size(); i++) {
            RolePlayerEntity.RolePlayerHead head = mRolePlayerEntity.getLstRolePlayerMessage().get(i).getRolePlayer();
            //if (!head.isSelfRole()) {
            mRolePlayerEntity.getLstRolePlayerMessage().get(i).setMsgStatus(RolePlayerEntity
                    .RolePlayerMessageStatus.CANCEL_DZ);
            mRolePlayerPager.updateRolePlayList(rolePlayerMessages.get(i));
            // }

        }
    }

    /**
     * 上传文件到阿里云,成功后发通知
     */
    public void uploadFileToAliCloud(String filePath, final RolePlayerEntity.RolePlayerMessage message,
                                     final RolePlayerEntity entity, final int selfRoleId) {
        logger.i( " 上传文件到阿里云,成功后发通知 uploadFileToAliCloud：filePath = " + filePath + " selfRoleId =" +
                " " + selfRoleId);
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(filePath);
        uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadEntity.setCloudPath(CloudDir.MICROPHONE);
        new XesCloudUploadBusiness(mContext).asyncUpload(uploadEntity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {

            }

            @Override
            public void onSuccess(XesCloudResult result) {
                message.setWebVoiceUrl(result.getHttpPath());
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", 120);
                    obj.put("content", result.getHttpPath());
                    obj.put("index", message.getPosition());
                    sendWebSMessage(3, entity.getLstRoleInfo(), selfRoleId, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(XesCloudResult result) {
            }
        });
    }

    /**
     * 自己朗读完毕
     *
     * @param stars
     * @param totalScore
     * @param fluency
     * @param accuracy
     * @param entity     用来获取所有角色的suid
     * @param selfRoleId 当前用户的suid
     */
    public void selfReadEnd(int stars, int totalScore, int fluency, int accuracy, int index, RolePlayerEntity entity,
                            int selfRoleId) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("type", 110);
            obj.put("content", "finish");
            obj.put("index", index);
            JSONObject data = new JSONObject();
            data.put("stars", stars);
            data.put("totalScore", totalScore);
            data.put("fluency", fluency);
            data.put("accuracy", accuracy);
            obj.put("data", data.toString());

            List<RolePlayerEntity.RolePlayerHead> rolePlayerHeads = entity.getLstRoleInfo();
            int roleSize = rolePlayerHeads.size();
            logger.i( "rolePlayerHeads:" + roleSize);
            sendWebSMessage(3, rolePlayerHeads, selfRoleId, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给他人点赞
     *
     * @param toId
     * @param index
     */
    public void toOtherDZ(int toId, int index) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", 100);
            obj.put("content", "parise");
            obj.put("index", index);
            sendWebSMessage(3, toId, obj);
        } catch (JSONException e) {
            e.printStackTrace();
            logger.i( "给他人点赞发生异常：" + e.getMessage());
        }
    }

    public void realease() {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
        }
    }

    /**
     * 方便测试
     *
     * @param entity
     */
    public void setRolePlayEntity(RolePlayerEntity entity) {
        this.mRolePlayerEntity = entity;
    }

    public void setRolePlayPager(RolePlayerPager pager) {
        this.mRolePlayerPager = pager;
    }


    public RolePlayerPager getRolePlayPager() {
        return mRolePlayerPager;
    }

    public RelativeLayout getBottomView() {
        return bottomContent;
    }
}

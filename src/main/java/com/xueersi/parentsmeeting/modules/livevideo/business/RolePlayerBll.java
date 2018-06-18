package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.AppBll;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayerPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayLog;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.permission.PermissionCallback;
import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

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
     * 连接地址
     */
    private String webSocketUrl = "ws://wsarts.xueersi" +
            ".com/roleplay/index?userId=%1$s&role=1&cookie=%2$s&liveId=%3$s";

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
    private LiveBll mLiveBll;

    private RolePlayerPager mRolePlayerPager;

    private RolePlayerHttpManager mRolePlayerHttpManager;

    private RolePlayerHttpResponseParser mRolePlayerHttpResponseParser;
    private boolean mIsCancelDZ = false;//是否已经取消了点赞
    private boolean mIsBeginSocket;
    private boolean isGoToRobot;//是否开始了人机

    public RolePlayerBll(Context context, RelativeLayout bottomContent, LiveBll liveBll, LiveGetInfo liveGetInfo) {
        super(context);
        this.bottomContent = bottomContent;
        this.mLiveBll = liveBll;
        this.mLiveGetInfo = liveGetInfo;
        mRolePlayerHttpManager = new RolePlayerHttpManager(mContext);
        mRolePlayerHttpResponseParser = new RolePlayerHttpResponseParser();
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
                PermissionCallback() {

                    @Override
                    public void onFinish() {
                        Loger.i("RolePlayerDemoTest", "onFinish");

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                        if (isGoToRobot) {
                            return;
                        }
                        XESToastUtils.showToast(mContext, "没开启录音权限无法参与RolePlayer");
                        Loger.i("RolePlayerDemoTest", "没开启录音权限无法参与RolePlayer");
                        goToRobot();

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        Loger.i("RolePlayerDemoTest", "开启了" + permission + "权限");
                        unList.remove(0);
                        if (unList.isEmpty()) {
                            if (SpeechEvaluatorUtils.isOfflineSuccess()) {
                                Loger.i("RolePlayerDemoTest", "开启了录音拍照权限，且离线加载成功开始去请求分组");
                                beginConWebSocket(nonce);
                            } else {
                                if (isGoToRobot) {
                                    return;
                                }
                                Loger.i("RolePlayerDemoTest", "没有权限或者离线包失败，走人机");
                                goToRobot();
                            }
                        }

                    }
                }, PermissionConfig.PERMISSION_CODE_AUDIO, PermissionConfig.PERMISSION_CODE_CAMERA);

        Loger.i("RolePlayerDemoTest", "unpermissionItems " + unPermissionItems.size() + "  SpeechEvaluatorUtils" +
                ".isOfflineSuccess() = " + SpeechEvaluatorUtils.isOfflineSuccess());

        unList.addAll(unPermissionItems);
        if (unList.isEmpty()) {
            if (SpeechEvaluatorUtils.isOfflineSuccess()) {
                Loger.i("RolePlayerDemoTest", "开启了录音拍照权限，且离线加载成功开始去请求分组");
                beginConWebSocket(nonce);
            } else {
                if (isGoToRobot) {
                    return;
                }
                Loger.i("RolePlayerDemoTest", "没有权限或者离线包失败，走人机");
                goToRobot();
            }
        }


        /*  Loger.i("RolePlayerDemoTest", "isHasVideo "+isHasVideo+" SpeechEvaluatorUtils.isOfflineFail() "
        +SpeechEvaluatorUtils.isOfflineFail());
          if (isHasVideo && !SpeechEvaluatorUtils.isOfflineFail()) {
                Loger.i("RolePlayerDemoTest", "isHasVideo = " + isHasVideo+" 拍照，录音，权限授予；离线加载成功，请求分组");
                beginConWebSocket();
                mIsBeginSocket = true;
            } else {
                Loger.i("RolePlayerDemoTest", "isHasVideo = " + isHasVideo+" 拍照，录音，权限未授予；或离线加载失败，不再请求分组，进人机");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        XesPermission.checkPermission(mContext, PermissionConfig.PERMISSION_CODE_AUDIO,
                        PermissionConfig.PERMISSION_CODE_CAMERA);
                        if (mIsBeginSocket) {
                            Loger.i("RolePlayerDemoTest", "延迟了3秒去再次请求权限");
                            beginConWebSocket();
                        }
                    }
                }, 3000);
            }*/

       /* boolean isHasVideo = XesPermission.checkPermission(mContext, new PermissionCallback() {

            @Override
            public void onFinish() {
                Loger.i("RolePlayerDemoTest", "没开启录音权限无法参与RolePlayer");
            }

            @Override
            public void onDeny(String permission, int position) {
                XESToastUtils.showToast(mContext, "没开启录音权限无法参与RolePlayer");
                Loger.i("RolePlayerDemoTest", "没开启录音权限无法参与RolePlayer");
            }

            @Override
            public void onGuarantee(String permission, int position) {
                Loger.i("RolePlayerDemoTest", "开启了录音权限，开始去请求分组");
                //beginConWebSocket();
            }
        }, PermissionConfig.PERMISSION_CODE_AUDIO,PermissionConfig.PERMISSION_CODE_CAMERA);
        Loger.i("RolePlayerDemoTest", "isHasVideo "+isHasVideo+" SpeechEvaluatorUtils.isOfflineFail() "
        +SpeechEvaluatorUtils.isOfflineFail());
        if (isHasVideo && !SpeechEvaluatorUtils.isOfflineFail()) {
            Loger.i("RolePlayerDemoTest", "isHasVideo = " + isHasVideo+" 拍照，录音，权限授予；离线加载成功，请求分组");
            beginConWebSocket();
            mIsBeginSocket = true;
        } else {
            Loger.i("RolePlayerDemoTest", "isHasVideo = " + isHasVideo+" 拍照，录音，权限未授予；或离线加载失败，不再请求分组，进人机");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    XesPermission.checkPermission(mContext, PermissionConfig.PERMISSION_CODE_AUDIO,PermissionConfig
                    .PERMISSION_CODE_CAMERA);
                    if (mIsBeginSocket) {
                        Loger.i("RolePlayerDemoTest", "延迟了3秒去再次请求权限");
                        beginConWebSocket();
                    }
                }
            }, 3000);
        }*/

    }

    /**
     * 教师发题指令
     */
    @Override
    public void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        //拉取试题a
        requestTestInfos();
        mRolePlayerPager = new RolePlayerPager(mContext, mRolePlayerEntity, true, this, mLiveGetInfo, mLiveBll);
        mRolePlayerPager.initData();
        if (bottomContent != null) {
            bottomContent.addView(mRolePlayerPager.getRootView());
        }
        //用户弹出答题框
        Loger.i("RolePlayerDemoTestlog", "用户弹出答题框,记录日志");
        RolePlayLog.sno4(mLiveBll, videoQuestionLiveEntity, mContext);

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
        Loger.i("RolePlayerDemoTest", "老师收题了,断开socket ");
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
            mWebSocket = null;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (bottomContent != null) {
                    bottomContent.removeView(mRolePlayerPager.getRootView());
                    mRolePlayerPager.onDestroy();
                    mRolePlayerPager = null;
                    if (mContext instanceof AudioRequest) {
                        AudioRequest audioRequest = (AudioRequest) mContext;
                        audioRequest.release();
                    }
                }
            }
        });
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

    /**
     * 进人机
     */
    @Override
    public void goToRobot() {
        isGoToRobot = true;
        Loger.d(TAG, "进人机");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //移除roleplay界面，并释放该界面资源
                if (bottomContent != null && mRolePlayerPager != null) {
                    bottomContent.removeView(mRolePlayerPager.getRootView());
                    mRolePlayerPager.onDestroy();
                    mRolePlayerPager = null;
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
        Loger.d(TAG, "进入人机；断开socket");
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
                .getInstance().getUserToken(), mLiveId);
        //webSocketUrl = String.format(webSocketUrl, "1237", "1111111", "1234");
        Loger.i("RolePlayerDemoTest", "websocket:" + webSocketUrl);
        mWebSocket.connect(webSocketUrl, new WebSocketConn.WebSocketCallBack() {
            @Override
            public void onOpen() {
                isBeginConnWebSocket = true;
                Loger.i("RolePlayerDemoTest", "open");
                Loger.i("RolePlayerDemoTestlog", "学生连接socket成功,记录日志");
                RolePlayLog.sno2(mLiveBll, mContext, nonce);

            }

            @Override
            public void onMessage(String result) {
                Loger.i("RolePlayerDemoTest", "result:" + result);
                onMessageParse(result);
            }


            @Override
            public void onClose() {
                Loger.i("RolePlayerDemoTest", "close");
                isBeginConnWebSocket = false;
            }

            @Override
            public void onError() {
                Loger.i("RolePlayerDemoTest", "onError");
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
                Loger.i("RolePlayerDemoTest", "send :" + obj.toString());
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
        Loger.i("RolePlayerDemoTest", "sendWebSMessage : acid = " + acid + " tos.size() = " + tos.size() + " " +
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
                Loger.i("RolePlayerDemoTest", "send :" + obj.toString());
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
            Loger.i("RolePlayerDemoTest", "parse 开始解析消息 acid = " + acid + " from " + from + " msg " + msgObj.toString
                    ());
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
                    Loger.i("RolePlayerDemoTest", "收到用户自定义消息 type = " + type);
                    switch (type) {
                        case 120:
                            //收到对方的MP3文件地址
                            String mp3Url = msgObj.optString("content");
                            Loger.i("RolePlayerDemoTest", "收到 " + from + " 的MP3文件地址 " + mp3Url + " mRolePlayerEntity" +
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
                            Loger.i("RolePlayerDemoTest", "收到 " + from + " 的点赞 mRolePlayerEntity.getLstRoleInfo()" +
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

                            Loger.i("RolePlayerDemoTest", "收到 " + from + " 读完 " + position + " totalScore = " +
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
                        }
                    }
                    break;
                case -1:
                    //异常情况
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Loger.i("RolePlayerDemoTest", "onMessageParse JSONException : " + e.getMessage());
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
                            mRolePlayerHttpResponseParser.parserRolePlayTestInfos(responseEntity, mRolePlayerEntity);
                            Loger.i("RolePlayerDemoTest", "服务器试题信息返回 " + responseEntity.getJsonObject().toString());
                            Loger.i("RolePlayerDemoTest", "服务器试题信息返回以后，解析到的角色对话长度 mRolePlayerEntity" +
                                    ".getLstRolePlayerMessage()" +
                                    ".size() = " + mRolePlayerEntity.getLstRolePlayerMessage().size() + "/ " +
                                    mRolePlayerEntity.toString());
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
    }

    /**
     * 提交结果
     */
    public synchronized void requestResult() {
        Loger.i("RolePlayerDemoTest", "提交结果");
        Loger.i("RolePlayerDemoTestlog", "用户提交结果,记录日志");
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
                    .toString(), new HttpCallBack() {
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
                    Loger.i("RolePlayerDemoTest", "onPmError: responseEntity.toString()  =" + responseEntity.toString
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
            Loger.i("RolePlayerDemoTest", " roleplay界面已经销毁，数据为空，不再向下执行 ");
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
        Loger.i("RolePlayerDemoTest", " 上传文件到阿里云,成功后发通知 uploadFileToAliCloud：filePath = " + filePath + " selfRoleId =" +
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
            Loger.i("RolePlayerDemoTest", "rolePlayerHeads:" + roleSize);
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
            Loger.i("RolePlayerDemoTest", "给他人点赞发生异常：" + e.getMessage());
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


}

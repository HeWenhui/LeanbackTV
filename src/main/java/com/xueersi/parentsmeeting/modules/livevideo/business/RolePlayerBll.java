package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.tal.speech.speechrecognizer.PhoneScore;
import com.xueersi.parentsmeeting.base.BaseBll;
import com.xueersi.parentsmeeting.business.AppBll;
import com.xueersi.parentsmeeting.cloud.XesCloudUploadBusiness;
import com.xueersi.parentsmeeting.cloud.config.CloudDir;
import com.xueersi.parentsmeeting.cloud.config.XesCloudConfig;
import com.xueersi.parentsmeeting.cloud.entity.CloudUploadEntity;
import com.xueersi.parentsmeeting.cloud.entity.XesCloudResult;
import com.xueersi.parentsmeeting.cloud.listener.XesStsUploadListener;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayerPager;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.permission.PermissionCallback;
import com.xueersi.parentsmeeting.permission.XesPermission;
import com.xueersi.parentsmeeting.permission.config.PermissionConfig;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * RolePlayer业务类
 * Created by zouhao on 2018/4/12.
 */
public class RolePlayerBll extends BaseBll implements RolePlayAction {

    /** 是否已经通过权限判断进入连接WebSocket */
    private boolean isBeginConnWebSocket;

    /** WebSocket连接类 */
    private WebSocketConn mWebSocket;

    /** 连接地址 */
    private String webSocketUrl = "ws://10.99.1.206:9590/roleplay/index?userId=%1$s&role=1&cookie=%2$s&liveId=%3$s";

    /** RolePlayer数据实体 */
    private RolePlayerEntity mRolePlayerEntity;

    /** 直播ID */
    private String mLiveId;
    /** 购课ID */
    private String mStuCouId;
    VideoQuestionLiveEntity videoQuestionLiveEntity;
    /** 基础布局 */
    private RelativeLayout bottomContent;
    /** 直播基础BLL */
    private LiveBll mLiveBll;

    private RolePlayerPager mRolePlayerPager;

    private RolePlayerHttpManager mRolePlayerHttpManager;
    private RolePlayerHttpResponseParser mRolePlayerHttpResponseParser;

    public RolePlayerBll(Context context, RelativeLayout bottomContent, LiveBll liveBll) {
        super(context);
        this.bottomContent = bottomContent;
        this.mLiveBll = liveBll;
        mRolePlayerHttpManager = new RolePlayerHttpManager(mContext);
        mRolePlayerHttpResponseParser = new RolePlayerHttpResponseParser();
    }

    /**
     * 领读指令触发
     */
    @Override
    public void teacherRead(String liveId, String stuCouId) {
        this.mLiveId = liveId;
        this.mStuCouId = stuCouId;
        boolean isHasVideo = XesPermission.checkPermissionNoAlert(mContext, new PermissionCallback() {

            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {
                XESToastUtils.showToast(mContext, "没开启录音权限无法参与RolePlayer");
            }

            @Override
            public void onGuarantee(String permission, int position) {
                beginConWebSocket();
            }
        }, PermissionConfig.PERMISSION_CODE_AUDIO);

        if (isHasVideo) {
            beginConWebSocket();
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (XesPermission.checkPermissionNoAlert(mContext, PermissionConfig.PERMISSION_CODE_AUDIO)) {
                        beginConWebSocket();
                    }
                }
            }, 3000);
        }

    }

    /**
     * 教师发题指令
     */
    @Override
    public void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        //拉取试题
        requestTestInfos();
        mRolePlayerPager = new RolePlayerPager(mContext, mRolePlayerEntity, true, this);
        mRolePlayerPager.initData();
        bottomContent.addView(mRolePlayerPager.getRootView());
    }

    @Override
    public String getQuestionId() {
        if (videoQuestionLiveEntity != null) {
            return videoQuestionLiveEntity.id;
        }
        return "";
    }

    @Override
    public void onStopQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
            mWebSocket = null;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                bottomContent.removeView(mRolePlayerPager.getRootView());
            }
        });
    }

    @Override
    public void onGoToRobot() {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
            mWebSocket = null;
        }
    }

    /**
     * 开始连接WebSocket
     */
    private void beginConWebSocket() {
        if (isBeginConnWebSocket) {
            return;
        }
        isBeginConnWebSocket = true;

        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
        }
        mWebSocket = null;

        if (mWebSocket == null) {
            mWebSocket = new WebSocketConn();
            webSocketUrl = String.format(webSocketUrl, UserBll.getInstance().getMyUserInfoEntity().getStuId(), AppBll.getInstance().getUserToken(), mLiveId);
            //webSocketUrl = String.format(webSocketUrl, "1237", "1111111", "1234");
            Loger.i("RolePlayerDemoTest", "websocket:" + webSocketUrl);
            mWebSocket.connect(webSocketUrl, new WebSocketConn.WebSocketCallBack() {
                @Override
                public void onOpen() {
                    Loger.i("RolePlayerDemoTest", "open");
                }

                @Override
                public void onMessage(String result) {
                    Loger.i("RolePlayerDemoTest", "result:" + result);
                    onMessageParse(result);
                }

                @Override
                public void onClose() {
                    Loger.i("RolePlayerDemoTest", "close");
                }

                @Override
                public void onError() {
                    Loger.i("RolePlayerDemoTest", "onError");
                }
            });
        }
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
            obj.put("msg", msgObj);
            if (mWebSocket != null && mWebSocket.isOpen()) {
                mWebSocket.sendMsg(obj.toString().getBytes("utf-8"));
                Loger.i("RolePlayerDemoTest", "send:" + obj.toString());
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
            mRolePlayerEntity = new RolePlayerEntity();
            mRolePlayerEntity.setSelfRoleId(suid);
            mRolePlayerEntity.setLiveId(Integer.parseInt(mLiveId));
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
            JSONObject msgObj = jsonObject.getJSONObject("msg");

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
                    switch (type) {
                        case 120:
                            //收到对方的MP3文件地址
                            String mp3Url = msgObj.optString("content");
                            int index = msgObj.optInt("index");
                            if (!TextUtils.isEmpty(mp3Url) && index >= 0 && index < mRolePlayerEntity.getLstRolePlayerMessage().size()) {
                                mRolePlayerEntity.getLstRolePlayerMessage().get(index).setWebVoiceUrl(mp3Url);
                            }
                            break;
                        case 100:
                            //收到对方的点赞

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
                            int position = msgObj.optInt("index");
                            JSONObject obj = msgObj.optJSONObject("data");
                            int totalScore = obj.optInt("totalScore");
                            int fluency = obj.optInt("fluency");
                            int accuracy = obj.optInt("accuracy");

                            if (totalScore > 0 && position >= 0 && position < mRolePlayerEntity.getLstRolePlayerMessage().size()) {
                                final RolePlayerEntity.RolePlayerMessage message = mRolePlayerEntity.getMessageByIndex(position);
                                if (message != null) {
                                    message.setSpeechScore(totalScore);
                                    message.setFluency(fluency);
                                    message.setAccuracy(accuracy);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRolePlayerPager.updateRolePlayList(message);
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
                        mRolePlayerEntity.setTestId(msgObj.optString("testId"));
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
        }

    }


    /**
     * 获取分组信息后去请求试题
     */
    public void requestTestInfos() {
        if (mRolePlayerEntity != null) {
            mRolePlayerHttpManager.requestRolePlayTestInfos(mLiveId, mStuCouId, mRolePlayerEntity.getTestId(), new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    Loger.i("RolePlayerDemoTest", responseEntity.getJsonObject().toString());
                    mRolePlayerHttpResponseParser.parserRolePlayTestInfos(responseEntity, mRolePlayerEntity);
                }
            });
        }
    }

    /**
     * 提交结果
     */
    public void requestResult() {
        mRolePlayerEntity.setResult(true);
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", 1);
            obj.put("roler", mRolePlayerEntity.getSelfRoleHead().getRoleName());
            JSONArray arrAnswer = new JSONArray();
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

            mRolePlayerHttpManager.requestResult(mStuCouId, mLiveId, mRolePlayerEntity.getTestId(), mRolePlayerEntity.getSelfRoleHead().getRoleName(), obj.toString(), new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    int gold = jsonObject.optInt("gold");

                    mRolePlayerEntity.setGoldCount(gold);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 上传文件到阿里云,成功后发通知
     */
    public void uploadFileToAliCloud(String filePath, final RolePlayerEntity.RolePlayerMessage message) {
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
                    sendWebSMessage(3, 1, obj);
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
     */
    public void selfReadEnd(int stars, int totalScore, int fluency, int accuracy, int index) {

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
            obj.put("data", data);
            sendWebSMessage(3, 1, obj);
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
        }
    }

    public void realease() {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
        }
    }
}

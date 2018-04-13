package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.BaseBll;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.RolePlayerHttpResponseParser;
import com.xueersi.parentsmeeting.permission.PermissionCallback;
import com.xueersi.parentsmeeting.permission.XesPermission;
import com.xueersi.parentsmeeting.permission.config.PermissionConfig;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * RolePlayer业务类
 * Created by zouhao on 2018/4/12.
 */
public class RolePlayerBll extends BaseBll implements RolePlayAction{

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

    /** 基础布局 */
    private RelativeLayout bottomContent;
    /** 直播基础BLL */
    private LiveBll mLiveBll;

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
            new Handler().postDelayed(new Runnable() {
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
    public void teacherPushTest() {


    }

    /**
     * 开始连接WebSocket
     */
    private void beginConWebSocket() {
        if (isBeginConnWebSocket) {
            return;
        }
        isBeginConnWebSocket = true;

        if (mWebSocket == null) {
            mWebSocket = new WebSocketConn();
            //webSocketUrl = String.format(webSocketUrl, UserBll.getInstance().getMyUserInfoEntity().getStuId(), AppBll.getInstance().getUserToken(), mLiveId);
            webSocketUrl = String.format(webSocketUrl, "1237", "1111111", "1234");
            mWebSocket.connect(webSocketUrl, new WebSocketConn.WebSocketCallBack() {
                @Override
                public void onOpen() {

                }

                @Override
                public void onMessage(String result) {
                    Loger.i("RolePlayerDemoTest", "result:" + result);
                    onMessageParse(result);
                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError() {

                }
            });
        }
    }


    private final int HERT_PING = 100;
    Handler mHertHandler = new Handler() {
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
     * 正常的消息解析
     *
     * @param result
     */
    private void onMessageParse(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            int acid = jsonObject.optInt("acid");
            JSONObject msgObj = jsonObject.getJSONObject("msg");
            switch (acid) {
                case 1:
                    //边接成功，获取stuid
                    int selfRoleId = msgObj.optInt("suid");
                    if (selfRoleId != 0) {
                        mRolePlayerEntity = new RolePlayerEntity();
                        mRolePlayerEntity.setSelfRoleId(selfRoleId);
                        hertPing();
                    }
                    break;
                case 2:
                    //心跳返回
                    break;
                case 4:
                    //用户自定义消息
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
                            requestTestInfos();
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
                    mRolePlayerHttpResponseParser.parserRolePlayTestInfos(responseEntity, mRolePlayerEntity);
                }
            });
        }

    }

}

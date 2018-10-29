package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RelativeLayout;

import com.tencent.cos.xml.utils.StringUtils;
import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStartChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linyuqiang on 2018/10/17.
 * 2018理科接麦
 * 聊天消息 https://wiki.xesv5.com/pages/viewpage.action?pageId=13839927
 * 接口 https://wiki.xesv5.com/pages/viewpage.action?pageId=13841181
 * 蓝湖 https://lanhuapp.com/url/r2Ezg
 */
public class VideoAudioChatBll implements VideoAudioChatAction {
    private static String TAG = "VideoAudioChatBll";
    protected Logger logger = LiveLoggerFactory.getLogger(getClass().getSimpleName());
    private String eventId = LiveVideoConfig.LIVE_LINK_MIRCO;
    private Activity activity;
    private VideoChatEvent videoChatEvent;
    private VideoAudioChatHttp videoChatHttp;
    private LiveAndBackDebug liveAndBackDebug;
    private LiveGetInfo getInfo;
    private boolean raisehand = false;
    /**
     * 暂时没用
     */
    private int times = 0;
    private LogToFile mLogtf;
    private long startTime;
    private AgoraVideoChatInter videoChatInter;
    private ChatTipBll chatTipBll;
    private RelativeLayout bottomContent;
    /**
     * 麦克风权限
     */
    private boolean isHasPermission = true;
    /**
     * 举麦权限提示
     */
    private boolean permissionPrompt = false;
    /**
     * 举麦失败
     */
    private boolean isFail = false;
    private boolean isSuccess = false;
    /**
     * 举麦包含我
     */
    private boolean containMe = false;
    /**
     * 连麦状态
     */
    private String openNewMic = "off";
    /**
     * 连麦状态
     */
    private String onMic = "off";

    /**
     * 房间号
     */
    private String room = "";
    /*举麦耳机提示*/
    private boolean headsetPrompt = false;
    /**
     * 连麦人数
     */
    private ArrayList<ClassmateEntity> allClassmateEntities = new ArrayList<>();
    /**
     * 连麦人数变化
     */
    private boolean classmateChange = true;
    /**
     * 举手人数
     */
    private int raiseHandCount = 0;
    /**
     * 举手来源
     */
    private String from = "";
    int micType = 0;
    /**
     * 接麦耳机判断
     */
    private boolean hasWiredHeadset = false;
    private WiredHeadsetReceiver wiredHeadsetReceiver;
    private String onmicStatus = "off";
    private ArrayList<VideoChatStartChange.ChatStartChange> chatStatusChanges = new ArrayList<>();

    public VideoAudioChatBll(Activity activity, VideoChatEvent videoChatEvent) {
        this.activity = activity;
        this.videoChatEvent = videoChatEvent;
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        ProxUtil.getProxUtil().put(activity, VideoChatStartChange.class, new VideoChatStartChange() {
            @Override
            public void addVideoChatStatrtChange(ChatStartChange chatStartChange) {
                chatStatusChanges.add(chatStartChange);
            }

            @Override
            public void removeVideoChatStatrtChange(ChatStartChange chatStartChange) {
                chatStatusChanges.remove(chatStartChange);
            }
        });
        chatTipBll = new ChatTipBll(activity);
        chatTipBll.setVideoChatEvent(videoChatEvent);
    }

    public void setVideoChatHttp(VideoAudioChatHttp videoChatHttp) {
        this.videoChatHttp = videoChatHttp;
        chatTipBll.setVideoChatHttp(videoChatHttp);
    }

    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        this.liveAndBackDebug = liveAndBackDebug;
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
        chatTipBll.setRootView(bottomContent);
//        videoChatPager = new VideoChatPager(activity);
//        bottomContent.addView(videoChatPager.getRootView());
    }

    public void onLiveInit(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        boolean allowLinkMic = getInfo.isAllowLinkMic();
        if (allowLinkMic) {
            wiredHeadsetReceiver = new WiredHeadsetReceiver();
            activity.registerReceiver(wiredHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        }
        chatTipBll.setGetInfo(getInfo);
//        startRecord();
    }

    private class WiredHeadsetReceiver extends BroadcastReceiver {
        private static final int STATE_UNPLUGGED = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int HAS_MIC = 1;

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("state", STATE_UNPLUGGED);
            int microphone = intent.getIntExtra("microphone", HAS_NO_MIC);
            String name = intent.getStringExtra("name");
            logger.d("WiredHeadsetReceiver.onReceive:a=" + intent.getAction() + ", s="
                    + (state == STATE_UNPLUGGED ? "unplugged" : "plugged") + ", m="
                    + (microphone == HAS_MIC ? "mic" : "no mic") + ", n=" + name);
            hasWiredHeadset = (state == STATE_PLUGGED);
        }
    }

//    public void startRecord(final String room, final String nonce) {
//        if (videoChatInter != null) {
//            videoChatInter.updateUser(classmateChange, allClassmateEntities);
//            return;
//        }
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                videoChatInter = new AgoraChatPager(activity, liveAndBackDebug, getInfo, videoChatEvent);
//                startTime = System.currentTimeMillis();
//                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        height);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                getInfo.setStuLinkMicNum(getInfo.getStuLinkMicNum() + 1);
//                if (isHasPermission) {
//                    videoChatInter.startRecord("startRecord", room, nonce, false);
//                }
//            }
//        };
//        checkPermissionUnPerList(new OnJoinPermissionFinish(openNewMic, room, from, containMe, runnable));
//        if (isHasPermission) {
//            runnable.run();
//        }
//    }

    public void setControllerBottom(final BaseLiveMediaControllerBottom liveMediaControllerBottom) {
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
//        Button btRaiseHands = liveMediaControllerBottom.findViewById(R.id.bt_livevideo_voicechat_raise_hands);
//        btRaiseHands.setVisibility(View.GONE);
    }

    @Override
    public void raisehand(String status, String room, String from, String nonce, int micType, int msgFrom) {
        logger.d("raisehand:status=" + status + ",from=" + from + ",nonce=" + nonce);
        openNewMic = status;
        this.micType = micType;
        this.from = from;
        if ("on".equals(status)) {
            chatTipBll.raisehand(room, from, nonce, micType);
        } else {
            chatTipBll.stopRecord("raisehand");
        }
    }

    @Override
    public void onJoin(final String openNewMic, final String room, final boolean classmateChange,
                       final
                       ArrayList<ClassmateEntity> classmateEntities, final String from, int type) {
        boolean change = false;
        boolean containMeChange = false;
        boolean onMicChange = false;
        if (!this.openNewMic.equals(openNewMic)) {
            change = true;
            onMicChange = true;
            this.openNewMic = openNewMic;
        }
        if (classmateChange) {
            change = true;
        }
        if (!this.room.equals(room)) {
            change = true;
            this.room = room;
        }
        this.micType = type;
//        if (!this.from.equals(from)) {
//            change = true;
//            this.from = from;
//        }

        boolean contain = false;
        ArrayList<ClassmateEntity> oldclassmateEntities = new ArrayList<>(allClassmateEntities);
        allClassmateEntities.clear();
        if (classmateEntities.isEmpty()) {
            onMic = "off";
        } else {
            onMic = "on";
        }
        for (ClassmateEntity classmateEntity : classmateEntities) {
            int index = oldclassmateEntities.indexOf(classmateEntity);
            if (index != -1) {
                ClassmateEntity oldClassmateEntity = oldclassmateEntities.get(index);
                if (StringUtils.isEmpty(classmateEntity.getName())) {
                    classmateEntity.setName(oldClassmateEntity.getName());
                }
                if (StringUtils.isEmpty(classmateEntity.getImg())) {
                    classmateEntity.setImg(oldClassmateEntity.getImg());
                }
            }
            allClassmateEntities.add(classmateEntity);
            if ((classmateEntity.getId() + "").equals(getInfo.getStuId())) {
                contain = true;
                classmateEntity.setMe(true);
//                break;
            }
        }
        if (containMe != contain) {
            containMe = contain;
            change = true;
            containMeChange = true;
        }
        String log = "onmic=" + openNewMic + ",room=" + room + ",onMic=" + onMic + ",containMeChange=" + containMeChange + ",contain=" + contain + ",size=" + allClassmateEntities.size() + ",from=" + from;
        if (change) {
            mLogtf.d("onJoin1:" + log);
//            if (onMicChange) {
//                raisehand(onmic, room, from, "", 2);
//            }
            if (containMeChange && "on".equals(this.openNewMic)) {
                if (contain) {
                    getInfo.setStuLinkMicNum(getInfo.getStuLinkMicNum() + 1);
                    AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                    if (audioRequest != null) {
                        audioRequest.request(null);
                    }
                } else {
                    AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                    if (audioRequest != null) {
                        audioRequest.release();
                    }
                }
                chatTipBll.startMicro(onMic, "", room, from, contain, micType);
                for (VideoChatStartChange.ChatStartChange chatStatusChange : chatStatusChanges) {
                    chatStatusChange.onVideoChatStartChange(contain);
                }
            } else {
                if (onMicChange) {
                    if ("on".equals(this.openNewMic)) {
                        if ("on".equals(onMic)) {
                            chatTipBll.startMicro(onMic, "", room, from, contain, micType);
                        } else {
                            chatTipBll.raisehand("", from, "", type);
                        }
                    } else {
                        chatTipBll.stopRecord("onJoin");
                    }
                }
            }
            if ("on".equals(this.openNewMic)) {
                chatTipBll.onClassmateChange(classmateEntities, false);
            }
//            if (classmateChange) {
//                chatTipBll.onClassmateChange(classmateEntities);
//            }
//            chatTipBll.onJoin(onmic, openhands, room, classmateChange, classmateEntities, from);
        } else {
            logger.d("onJoin2:" + log);
        }
    }

    @Override
    public void onStuMic(String status, final String room, ArrayList<ClassmateEntity> onmicClassmateEntities, ArrayList<ClassmateEntity> offmicClassmateEntities, final String from, int msgFrom) {
        logger.d("onStuMic:status=" + status + ",room=" + room + ",onmic=" + onmicClassmateEntities.size() + ",offmic=" + offmicClassmateEntities.size());
        boolean contain;
        if ("off".equals(status)) {
            contain = containMe;
            for (ClassmateEntity classmateEntity : offmicClassmateEntities) {
                int index = allClassmateEntities.indexOf(classmateEntity);
                if (index != -1) {
                    allClassmateEntities.remove(index);
                    if ((classmateEntity.getId() + "").equals(getInfo.getStuId())) {
                        contain = false;
                    }
                }
            }
        } else {
            contain = false;
            ArrayList<ClassmateEntity> oldclassmateEntities = new ArrayList<>(allClassmateEntities);
            allClassmateEntities.clear();
            for (ClassmateEntity classmateEntity : onmicClassmateEntities) {
                int index = oldclassmateEntities.indexOf(classmateEntity);
                if (index != -1) {
                    ClassmateEntity oldClassmateEntity = oldclassmateEntities.get(index);
                    if (StringUtils.isEmpty(classmateEntity.getName())) {
                        classmateEntity.setName(oldClassmateEntity.getName());
                    }
                    if (StringUtils.isEmpty(classmateEntity.getImg())) {
                        classmateEntity.setImg(oldClassmateEntity.getImg());
                    }
                }
                allClassmateEntities.add(classmateEntity);
                if ((classmateEntity.getId() + "").equals(getInfo.getStuId())) {
                    contain = true;
                    classmateEntity.setMe(true);
//                break;
                }
            }
        }
        logger.d("onStuMic:status=" + status + ",room=" + room + ",all=" + allClassmateEntities.size());
        if (allClassmateEntities.size() == 0) {
            onMic = "off";
        } else {
            onMic = "on";
        }
        this.room = room;

        boolean containMeChange = false;
        if (containMe != contain) {
            containMe = contain;
            containMeChange = true;
        }
        if (containMeChange) {
            if (containMe) {
                getInfo.setStuLinkMicNum(getInfo.getStuLinkMicNum() + 1);
                AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                if (audioRequest != null) {
                    audioRequest.request(null);
                }
                chatTipBll.startMicro(onMic, "", room, from, true, micType);
            } else {
                AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                if (audioRequest != null) {
                    audioRequest.release();
                }
                chatTipBll.startMicro(onMic, "", room, from, false, micType);
            }
        }
        chatTipBll.onClassmateChange(allClassmateEntities, false);
    }

    public void startMicro(String status, String nonce, boolean contain, String room, String from, int msgFrom) {
        logger.d("startMicro:status=" + status + ",nonce=" + nonce + ",contain=" + contain + ",from=" + from + ",msgFrom=" + msgFrom);
        if ("on".equals(status)) {

        } else {
            AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
            if (audioRequest != null) {
                audioRequest.release();
            }
        }
        for (VideoChatStartChange.ChatStartChange chatStatusChange : chatStatusChanges) {
            chatStatusChange.onVideoChatStartChange(true);
        }
    }

    @Override
    public void quit(String status, String room, String from, int msgFrom) {
        logger.d("quit:status=" + status + ",room=" + room + ",from=" + from + ",msgFrom=" + msgFrom);
        chatTipBll.stopRecord("quit");
    }

    @Override
    public void raiseHandCount(int num) {
        logger.d("raiseHandCount:num=" + num);
        chatTipBll.raiseHandCount(num);
    }

    public void onNetWorkChange(int netWorkType) {
        if (videoChatInter != null) {
            videoChatInter.onNetWorkChange(netWorkType);
        }
    }

    public void stopRecord() {
        chatTipBll.stopRecord("stopRecord");
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
        for (VideoChatStartChange.ChatStartChange chatStatusChange : chatStatusChanges) {
            chatStatusChange.onVideoChatStartChange(false);
        }
    }

    public void onDestroy() {
        if (wiredHeadsetReceiver != null) {
            activity.unregisterReceiver(wiredHeadsetReceiver);
            wiredHeadsetReceiver = null;
        }
        if (videoChatInter != null) {
            bottomContent.removeView(videoChatInter.getRootView());
            stopRecord();
            videoChatInter = null;
            mLogtf.d("MIC_TIME:onDestroy:time=" + (System.currentTimeMillis() - startTime));
        }
        chatTipBll.destory();
        chatStatusChanges.clear();
    }

    /**
     * 弹出toast，判断Video是不是在活动
     *
     * @param text
     */
    public void showToast(String text) {
        if (activity instanceof ActivityStatic) {
            ActivityStatic activityStatic = (ActivityStatic) activity;
            if (activityStatic.isResume()) {
                XESToastUtils.showToast(activity, text);
            }
        }
    }

    interface OnPermissionFinish {
        void onFinish();
    }

    private void checkPermissionUnPerList(final OnPermissionFinish onPermissionFinish) {
        final List<PermissionItem> unList = new ArrayList<>();
        List<PermissionItem> unList2 = XesPermission.checkPermissionUnPerList(activity, new
                LiveActivityPermissionCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        unList.remove(0);
                        if (unList.isEmpty()) {
                            isHasPermission = true;
                            onPermissionFinish.onFinish();
                        }
                    }
                }, PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO);
        unList.addAll(unList2);
        isHasPermission = unList.isEmpty();
    }
}


package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.ViewGroup;
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
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatInter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.page.AgoraChatPager;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStartChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linyuqiang on 2018/10/17.
 * 直播接麦
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
    private VideoChatInter videoChatInter;
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

    public void startRecord(final String room, final String nonce) {
        if (videoChatInter != null) {
            videoChatInter.updateUser(classmateChange, allClassmateEntities);
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                videoChatInter = new AgoraChatPager(activity, liveAndBackDebug, getInfo, videoChatEvent);
                startTime = System.currentTimeMillis();
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                getInfo.setStuLinkMicNum(getInfo.getStuLinkMicNum() + 1);
                if (isHasPermission) {
                    videoChatInter.startRecord("startRecord", room, nonce, false);
                }
            }
        };
        checkPermissionUnPerList(new OnJoinPermissionFinish(onMic, room, from, containMe, runnable));
        if (isHasPermission) {
            runnable.run();
        }
    }

    public void setControllerBottom(final BaseLiveMediaControllerBottom liveMediaControllerBottom) {
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
//        Button btRaiseHands = liveMediaControllerBottom.findViewById(R.id.bt_livevideo_voicechat_raise_hands);
//        btRaiseHands.setVisibility(View.GONE);
    }

    @Override
    public void raisehand(String status, String room, String from, String nonce, int msgFrom) {
        logger.d("raisehand:status=" + status + ",from=" + from + ",nonce=" + nonce);
        onmicStatus = status;
        this.from = from;
        if ("on".equals(status)) {
            chatTipBll.raisehand(room, from, nonce);
        } else {
            chatTipBll.stopRecord();
        }
    }

    @Override
    public void onJoin(final String onmic, final String room, final boolean classmateChange,
                       final
                       ArrayList<ClassmateEntity> classmateEntities, final String from) {
        boolean change = false;
        boolean containMeChange = false;
        boolean onMicChange = false;
        if (!onMic.equals(onmic)) {
            change = true;
            onMicChange = true;
            onMic = onmic;
        }
        if (classmateChange) {
            change = true;
        }
        if (!this.room.equals(room)) {
            change = true;
            this.room = room;
        }
//        if (!this.from.equals(from)) {
//            change = true;
//            this.from = from;
//        }

        boolean contain = false;
        ArrayList<ClassmateEntity> oldclassmateEntities = new ArrayList<>(allClassmateEntities);
        allClassmateEntities.clear();
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
//                break;
            }
        }
        if (containMe != contain) {
            containMe = contain;
            change = true;
            containMeChange = true;
        }
        String log = "onmic=" + onmic + ",room=" + room + ",containMeChange=" + containMeChange + ",from=" + from;
        if (change) {
            mLogtf.d("onJoin1:" + log);
//            if (onMicChange) {
//                raisehand(onmic, room, from, "", 2);
//            }
            if (containMeChange && "on".equals(onMic)) {
                if (contain) {
                    getInfo.setStuLinkMicNum(getInfo.getStuLinkMicNum() + 1);
                    AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                    if (audioRequest != null) {
                        audioRequest.request(null);
                    }
                    chatTipBll.startMicro("", room, from, true);
                } else {
                    AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                    if (audioRequest != null) {
                        audioRequest.release();
                    }
                    chatTipBll.startMicro("", room, from, false);
                }
                for (VideoChatStartChange.ChatStartChange chatStatusChange : chatStatusChanges) {
                    chatStatusChange.onVideoChatStartChange(contain);
                }
            } else {
                if (onMicChange) {
                    if ("on".equals(onMic)) {
                        chatTipBll.raisehand("", from, "");
                    } else {
                        chatTipBll.stopRecord();
                    }
                }
            }
            if ("on".equals(onMic)) {
                chatTipBll.onClassmateChange(classmateEntities);
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
    public void onStuMic(String status, final String room, ArrayList<ClassmateEntity> classmateEntities, final String from, int msgFrom) {
        logger.d("onStuMic:status=" + status + ",room=" + room + ",size=" + classmateEntities.size() + ",msgFrom=" + msgFrom);
        onMic = status;
        this.room = room;
        boolean contain = false;
        ArrayList<ClassmateEntity> oldclassmateEntities = new ArrayList<>(allClassmateEntities);
        allClassmateEntities.clear();
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
//                break;
            }
        }
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
                chatTipBll.startMicro("", room, from, true);
            } else {
                AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                if (audioRequest != null) {
                    audioRequest.release();
                }
                chatTipBll.startMicro("", room, from, false);
            }
        }
        chatTipBll.onClassmateChange(classmateEntities);
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
        chatTipBll.stopRecord();
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
        chatTipBll.stopRecord();
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

    RaiseHandPermissionFinish currentPermission;

    /**
     * 举手权限回调
     */
    private class RaiseHandPermissionFinish implements OnPermissionFinish {
        String status;
        String from;
        Runnable runnable;

        public RaiseHandPermissionFinish(String status, String from, Runnable runnable) {
            this.status = status;
            this.from = from;
            this.runnable = runnable;
            currentPermission = this;
        }

        @Override
        public void onFinish() {
            if (status.equals(VideoAudioChatBll.this.onmicStatus) && from.equals(VideoAudioChatBll.this.from)) {
                if (currentPermission == this) {
                    runnable.run();
                }
            }
        }
    }

    OnJoinPermissionFinish currentPermission2;

    /**
     * 加入房间权限回调
     */
    private class OnJoinPermissionFinish implements OnPermissionFinish {
        String onmic;
        String room;
        String from;
        boolean contain;
        Runnable runnable;

        public OnJoinPermissionFinish(String onmic, String room, String from, boolean contain,
                                      Runnable
                                              runnable) {
            this.onmic = onmic;
            this.room = room;
            this.from = from;
            this.contain = contain;
            this.runnable = runnable;
            currentPermission2 = this;
        }

        @Override
        public void onFinish() {
            if (onmic.equals(VideoAudioChatBll.this.onMic)
                    && room.equals(VideoAudioChatBll.this.room) && from.equals(VideoAudioChatBll.this.from) && contain ==
                    containMe) {
                if (currentPermission2 == this) {
                    runnable.run();
                }
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


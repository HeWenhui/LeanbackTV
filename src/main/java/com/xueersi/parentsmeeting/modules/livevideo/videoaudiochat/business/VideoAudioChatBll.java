package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.tencent.cos.xml.utils.StringUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStartChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.ArrayList;

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
    private ChatTipBll chatTipBll;
    private LiveViewAction liveViewAction;
    /** 举麦包含我 */
    private boolean containMe = false;
    /** 连麦状态 */
    private String openNewMic = "off";
    /** 连麦状态 */
    private String onMic = "off";
    /** 房间号 */
    private String room = "";
    /** 连麦人数 */
    private ArrayList<ClassmateEntity> allClassmateEntities = new ArrayList<>();
    /** 举手来源 */
    private String from = "";
    int micType = 0;
    private String linkmicid;
    boolean startMic;
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
                if (startMic) {
                    chatStartChange.onVideoChatStartChange(true);
                }
            }

            @Override
            public void removeVideoChatStatrtChange(ChatStartChange chatStartChange) {
                chatStatusChanges.remove(chatStartChange);
            }
        });
    }

    public void setVideoChatHttp(VideoAudioChatHttp videoChatHttp) {
        this.videoChatHttp = videoChatHttp;
    }

    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        this.liveAndBackDebug = liveAndBackDebug;
    }

    private void createChatTipBll(String method) {
        if (chatTipBll != null) {
            return;
        }
        mLogtf.d("createChatTipBll:method=" + method);
        ChatTipBll chatTipBll = new ChatTipBll(activity);
        chatTipBll.setVideoChatEvent(videoChatEvent);
        chatTipBll.setVideoChatHttp(videoChatHttp);
        chatTipBll.setRootView(liveViewAction);
        chatTipBll.setGetInfo(getInfo);
        chatTipBll.setMicType(micType);
        chatTipBll.setLinkmicid(linkmicid);
        this.chatTipBll = chatTipBll;
    }

    public void initView(LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
//        videoChatPager = new VideoChatPager(activity);
//        bottomContent.addView(videoChatPager.getRootView());
    }

    public void onLiveInit(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
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
    public void raisehand(String status, String room, String from, String nonce, int micType, String linkmicid, int msgFrom) {
        logger.d("raisehand:status=" + status + ",from=" + from + ",nonce=" + nonce);
        openNewMic = status;
        this.micType = micType;
        this.linkmicid = linkmicid;
        this.from = from;
        if ("on".equals(status)) {
            createChatTipBll("raisehand");
            chatTipBll.raisehand(room, from, nonce, micType);
        } else {
            if (chatTipBll != null) {
                chatTipBll.stopRecord("raisehand", false, nonce);
                if (containMe) {
                    onVideoChatStartChange(false, "raisehand");
                }
            }
            chatTipBll = null;
        }
    }

    @Override
    public void onJoin(final String openNewMic, final String room, final boolean classmateChange,
                       final
                       ArrayList<ClassmateEntity> classmateEntities, final String from, int type, String linkmicid) {
        boolean change = false;
        boolean peopleChange = false;
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
        this.linkmicid = linkmicid;
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
            } else {
                peopleChange = true;
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
            peopleChange = true;
        }
        String log = "onmic=" + openNewMic + ",room=" + room + ",onMic=" + onMic + ",peopleChange=" + peopleChange + ",contain=" + contain + ",size=" + allClassmateEntities.size() + ",from=" + from + ",linkmicid=" + linkmicid;
        if (change) {
            mLogtf.d("onJoin1:" + log);
//            if (onMicChange) {
//                raisehand(onmic, room, from, "", 2);
//            }
            if (peopleChange && "on".equals(this.openNewMic)) {
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
                createChatTipBll("onJoin:startMicro");
                chatTipBll.startMicro(onMic, room, from, contain, micType, "");
                onVideoChatStartChange(contain, "onJoin:startMicro");
            } else {
                if (onMicChange) {
                    if ("on".equals(this.openNewMic)) {
                        if ("on".equals(onMic)) {
                            createChatTipBll("onJoin:startMicro2");
                            chatTipBll.startMicro(onMic, room, from, contain, micType, "");
                        } else {
                            createChatTipBll("onJoin:raisehand");
                            chatTipBll.raisehand("", from, "", type);
                        }
                    } else {
                        if (chatTipBll != null) {
                            chatTipBll.stopRecord("onJoin", false, "");
                            chatTipBll = null;
                        }
                    }
                }
            }
            if ("on".equals(this.openNewMic)) {
                if (chatTipBll != null) {
                    chatTipBll.onClassmateChange(classmateEntities, false);
                }
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
    public void onStuMic(String status, final String room, ArrayList<ClassmateEntity> onmicClassmateEntities, ArrayList<ClassmateEntity> offmicClassmateEntities, final String from, int msgFrom, String nonce) {
        logger.d("onStuMic:status=" + status + ",room=" + room + ",onmic=" + onmicClassmateEntities.size() + ",offmic=" + offmicClassmateEntities.size());
        boolean contain = containMe;
        boolean peopleChange = false;
        boolean containMeChange = false;
        if ("off".equals(status)) {
            contain = containMe;
            for (ClassmateEntity classmateEntity : offmicClassmateEntities) {
                int index = allClassmateEntities.indexOf(classmateEntity);
                if (index != -1) {
                    allClassmateEntities.remove(index);
                    peopleChange = true;
                    if ((classmateEntity.getId() + "").equals(getInfo.getStuId())) {
                        contain = false;
                    }
                }
            }
        } else {
//            contain = false;
//            ArrayList<ClassmateEntity> oldclassmateEntities = new ArrayList<>(allClassmateEntities);
//            allClassmateEntities.clear();
            for (ClassmateEntity classmateEntity : offmicClassmateEntities) {
                int index = allClassmateEntities.indexOf(classmateEntity);
                if (index != -1) {
                    allClassmateEntities.remove(index);
                    peopleChange = true;
                    if ((classmateEntity.getId() + "").equals(getInfo.getStuId())) {
                        contain = false;
                    }
                }
            }
            for (ClassmateEntity classmateEntity : onmicClassmateEntities) {
                int index = allClassmateEntities.indexOf(classmateEntity);
                if (index != -1) {
                    ClassmateEntity oldClassmateEntity = allClassmateEntities.get(index);
                    if (StringUtils.isEmpty(classmateEntity.getName())) {
                        classmateEntity.setName(oldClassmateEntity.getName());
                    }
                    if (StringUtils.isEmpty(classmateEntity.getImg())) {
                        classmateEntity.setImg(oldClassmateEntity.getImg());
                    }
                } else {
                    peopleChange = true;
                    allClassmateEntities.add(classmateEntity);
                }
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

        if (containMe != contain) {
            containMe = contain;
            peopleChange = true;
            containMeChange = true;
        }
        createChatTipBll("onStuMic");
        if (peopleChange) {
            if (containMe) {
                getInfo.setStuLinkMicNum(getInfo.getStuLinkMicNum() + 1);
                AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                if (audioRequest != null) {
                    audioRequest.request(null);
                }
                chatTipBll.startMicro(onMic, room, from, true, micType, nonce);
            } else {
                AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                if (audioRequest != null) {
                    audioRequest.release();
                }
                chatTipBll.startMicro(onMic, room, from, false, micType, nonce);
            }
            if (containMeChange) {
                onVideoChatStartChange(contain, "onStuMic");
            }
        }
        chatTipBll.onClassmateChange(allClassmateEntities, false);
    }

    @Override
    public void quit(String status, String room, String from, int msgFrom) {
        logger.d("quit:status=" + status + ",room=" + room + ",from=" + from + ",msgFrom=" + msgFrom);
        if (chatTipBll != null) {
            mLogtf.d("quit:from=" + from + ",msgFrom=" + msgFrom + ",openNewMic=" + openNewMic + ",containMe=" + containMe + ",onMic=" + onMic);
            openNewMic = "off";
            containMe = false;
            onMic = "off";
            allClassmateEntities.clear();
            chatTipBll.stopRecord("quit", false, "");
            chatTipBll = null;
        }
    }

    @Override
    public void raiseHandCount(int num) {
        logger.d("raiseHandCount:num=" + num);
        createChatTipBll("raiseHandCount");
        chatTipBll.raiseHandCount(num);
    }

    public void onNetWorkChange(int netWorkType) {
        if (chatTipBll != null) {
            chatTipBll.onNetWorkChange(netWorkType);
        }
    }

    public void stopRecord() {
        if (chatTipBll != null) {
            chatTipBll.stopRecord("stopRecord", true, "");
            chatTipBll.destory();
            chatTipBll = null;
        }
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
        onVideoChatStartChange(false, "stopRecord");
    }

    private void onVideoChatStartChange(boolean start, String method) {
        startMic = start;
        mLogtf.d("onVideoChatStartChange:start=" + start + ",method=" + method + ",micType=" + micType);
        for (VideoChatStartChange.ChatStartChange chatStatusChange : chatStatusChanges) {
            chatStatusChange.onVideoChatStartChange(start);
        }
    }

    public void onDestroy() {
        if (chatTipBll != null) {
            stopRecord();
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

    interface OnPermissionFinish {
        void onFinish();
    }

    @Override
    public void onConnect() {
        if (chatTipBll != null) {
            chatTipBll.onConnect();
        }
    }

    @Override
    public void onDisconnect() {
        if (chatTipBll != null) {
            chatTipBll.onDisconnect();
        }
    }

}


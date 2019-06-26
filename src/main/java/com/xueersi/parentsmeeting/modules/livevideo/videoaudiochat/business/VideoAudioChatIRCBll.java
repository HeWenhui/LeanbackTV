package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.app.Activity;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateReg;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoAudioChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStatusChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/11.
 */
public class VideoAudioChatIRCBll extends LiveBaseBll implements VideoChatEvent, NoticeAction, TopicAction, VideoAudioChatHttp, MessageAction {
    private VideoAudioChatBll videoChatAction;
    private LiveFragmentBase liveFragmentBase;
    /** 接麦已经连接老师 */
    private AtomicBoolean startRemote = new AtomicBoolean(false);
    public static final String DEFULT_VOICE_CHAT_STATE = "off";
    private String voiceChatStatus = DEFULT_VOICE_CHAT_STATE;
    private ArrayList<VideoChatStatusChange.ChatStatusChange> chatStatusChanges = new ArrayList<>();
    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;
    private String lastNewLinkMicT = "";
    private String lastNewLinkMicF = "";

    public VideoAudioChatIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        putInstance(VideoChatStatusChange.class, new VideoChatStatusChange() {

            @Override
            public void addVideoChatStatusChange(ChatStatusChange chatStatusChange) {
                chatStatusChanges.add(chatStatusChange);
            }

            @Override
            public void removeVideoChatStatusChange(ChatStatusChange chatStatusChange) {
                chatStatusChanges.remove(chatStatusChange);
            }
        });
    }

    public void setLiveFragmentBase(LiveFragmentBase liveFragmentBase) {
        this.liveFragmentBase = liveFragmentBase;
    }

    public void setLiveMediaControllerBottom(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
        this.baseLiveMediaControllerBottom = baseLiveMediaControllerBottom;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        boolean allowLinkMic = (1 == getInfo.getAllowLinkMicNew());
        if (allowLinkMic) {
            VideoAudioChatBll videoChatBll = new VideoAudioChatBll(activity, this);
            if (rlMessageBottom != null) {
                videoChatBll.initView(rlMessageBottom);
            } else {
                videoChatBll.initView(mRootView);
            }
            videoChatBll.setControllerBottom(baseLiveMediaControllerBottom);
            videoChatBll.setLiveAndBackDebug(contextLiveAndBackDebug);
            videoChatBll.setVideoChatHttp(this);
            videoChatBll.onLiveInit(getInfo);
            videoChatAction = videoChatBll;

            if (baseLiveMediaControllerBottom instanceof LiveUIStateReg) {
                LiveUIStateReg halfBodyMediaControllerBottom = (LiveUIStateReg)
                        baseLiveMediaControllerBottom;
                halfBodyMediaControllerBottom.addLiveUIStateListener(onViewChange);
            }

            putInstance(VideoChatEvent.class, this);
        } else {
            mLiveBll.removeBusinessBll(this);
        }
    }

    private LiveUIStateListener onViewChange = new LiveUIStateListener() {
        @Override
        public void onViewChange(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
            videoChatAction.setControllerBottom(baseLiveMediaControllerBottom);
        }
    };

    public boolean isChat() {
        return startRemote.get();
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (videoChatAction != null) {
            videoChatAction.onDestroy();
        }
        chatStatusChanges.clear();
    }

    @Override
    public void setVolume(float left, float right) {
        liveFragmentBase.setVolume(left, right);
    }

    @Override
    public void showLongMediaController() {
        liveFragmentBase.showLongMediaController();
    }

    @Override
    public AtomicBoolean getStartRemote() {
        return startRemote;
    }

    @Override
    public void stopPlay() {
        liveFragmentBase.stopPlayer();
    }

    @Override
    public void rePlay(boolean b) {
        if (!MediaPlayer.getIsNewIJK()) {
            liveFragmentBase.rePlay(b);
        } else {
            liveFragmentBase.changeNowLine();
        }
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (videoChatAction != null) {
            videoChatAction.quit("off", "", "change", LiveVideoConfig.IRC_TYPE_NOTICE);
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (videoChatAction != null) {
            if (modeChange) {
                videoChatAction.quit("off", "", "change", LiveVideoConfig.IRC_TYPE_TOPIC);
            }
            String oldVoiceChatStatus = voiceChatStatus;
            try {
                if (LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
                    JSONObject room_1 = jsonObject.getJSONObject("room_1");
                    JSONObject newLinkMic = room_1.optJSONObject("newLinkMic");
                    if (newLinkMic != null && !(newLinkMic.toString()).equals(lastNewLinkMicT)) {
                        lastNewLinkMicT = newLinkMic.toString();
                        String openNewMic = newLinkMic.optString("openNewMic", "off");
                        String room = newLinkMic.getString("room");
                        int micType = newLinkMic.optInt("type", 0);
                        String linkmicid = newLinkMic.optString("linkmicid");
                        ArrayList<ClassmateEntity> classmateEntities = new ArrayList<>();
                        if ("on".equals(openNewMic)) {
                            JSONArray students = newLinkMic.getJSONArray("students");
                            for (int i = 0; i < students.length(); i++) {
                                ClassmateEntity classmateEntity = new ClassmateEntity();
                                JSONObject stuObj = students.getJSONObject(i);
                                classmateEntity.setId(stuObj.optString("id"));
                                classmateEntity.setPlace(stuObj.optInt("place", i));
                                classmateEntities.add(classmateEntity);
                            }
                        }
                        startLinkmicid = linkmicid;
                        voiceChatStatus = openNewMic;
                        videoChatAction.onJoin(openNewMic, room, true, classmateEntities, "t", micType, linkmicid);
                    }
                } else {
                    JSONObject room_2 = jsonObject.getJSONObject("room_2");
                    JSONObject newLinkMic = room_2.optJSONObject("newLinkMic");
                    if (newLinkMic != null && !(newLinkMic.toString()).equals(lastNewLinkMicF)) {
                        lastNewLinkMicF = newLinkMic.toString();
                        String openNewMic = newLinkMic.optString("openNewMic", "off");
                        String room = newLinkMic.getString("room");
                        int micType = newLinkMic.optInt("type", 0);
                        String linkmicid = newLinkMic.optString("linkmicid");
                        ArrayList<ClassmateEntity> classmateEntities = new ArrayList<>();
                        if ("on".equals(openNewMic)) {
                            JSONArray students = newLinkMic.getJSONArray("students");
                            for (int i = 0; i < students.length(); i++) {
                                ClassmateEntity classmateEntity = new ClassmateEntity();
                                JSONObject stuObj = students.getJSONObject(i);
                                classmateEntity.setId(stuObj.optString("id"));
                                classmateEntity.setPlace(stuObj.optInt("place", i));
                                classmateEntities.add(classmateEntity);
                            }
                        }
                        startLinkmicid = linkmicid;
                        voiceChatStatus = openNewMic;
                        videoChatAction.onJoin(openNewMic, room, true, classmateEntities, "f", micType, linkmicid);
                    }
                }
                if (!oldVoiceChatStatus.equals(voiceChatStatus)) {
                    for (int i = 0; i < chatStatusChanges.size(); i++) {
                        chatStatusChanges.get(i).onVideoChatStatusChange(voiceChatStatus);
                    }
                }
            } catch (Exception e) {
                logger.e("onTopic", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    private String linkMicNonce = "";
    private String startLinkmicid = "";

    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        String msg = "onNotice";
        switch (type) {
            case XESCODE.AgoraChat.RAISE_HAND: {
                String from = object.optString("from", "t");
                String room = object.optString("room");
                int micType = object.optInt("mictype", 0);
                msg += ",RAISE_HAND:from=" + from + ",mode=" + mLiveBll.getMode();
                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                    String status = object.optString("status", "off");
                    voiceChatStatus = status;
                    String linkmicid = object.optString("linkmicid");
                    if (videoChatAction != null) {
                        msg += "RAISE_HAND:status=" + status;
                        videoChatAction.raisehand(status, room, from, object.optString("nonce"), micType, linkmicid, 1);
                    }
                    for (int i = 0; i < chatStatusChanges.size(); i++) {
                        chatStatusChanges.get(i).onVideoChatStatusChange(status);
                    }
                    if ("on".equals(status)) {
                        linkMicNonce = object.optString("nonce");
                        startLinkmicid = linkmicid;
                        VideoAudioChatLog.getRaiseHandMsgSno2(contextLiveAndBackDebug, micType == 0 ? "audio" : "video", linkmicid, linkMicNonce);
                    } else {
                        String nonce = object.optString("nonce");
                        VideoAudioChatLog.getCloseMsgSno12(contextLiveAndBackDebug, startLinkmicid, micType == 0 ? "audio" : "video", nonce);
                    }
                }
            }
            break;
            case XESCODE.AgoraChat.RAISE_HAND_COUNT: {
                String from = object.optString("from", "t");
                msg += ",RAISE_HAND_COUNT:from=" + from + ",mode=" + mLiveBll.getMode();
                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                    if (videoChatAction != null) {
                        int count = object.optInt("num", 0);
                        videoChatAction.raiseHandCount(count);
                    }
                }
                break;
            }
            case XESCODE.AgoraChat.STUDY_ONMIC: {
                try {
                    String from = object.optString("from", "t");
                    String status = object.getString("status");
                    String room = object.optString("room");
                    String nonce = object.optString("nonce");
                    int micType = object.optInt("type", 0);
                    msg += ",STUDY_ONMIC:from=" + from + ",mode=" + mLiveBll.getMode();
                    ArrayList<ClassmateEntity> onmicClassmateEntities = new ArrayList<>();
                    ArrayList<ClassmateEntity> offmicClassmateEntities = new ArrayList<>();
                    JSONArray offStudents = object.getJSONArray("offmic");
                    for (int i = 0; i < offStudents.length(); i++) {
                        ClassmateEntity classmateEntity = new ClassmateEntity();
                        JSONObject jsonObject = offStudents.getJSONObject(i);
                        classmateEntity.setId(jsonObject.optString("id"));
                        classmateEntity.setName(jsonObject.optString("name"));
                        classmateEntity.setImg(jsonObject.optString("img"));
                        offmicClassmateEntities.add(classmateEntity);
                    }
                    if ("on".equals(status)) {
                        JSONArray onStudents = object.getJSONArray("onmic");
                        for (int i = 0; i < onStudents.length(); i++) {
                            ClassmateEntity classmateEntity = new ClassmateEntity();
                            JSONObject jsonObject = onStudents.getJSONObject(i);
                            classmateEntity.setId(jsonObject.optString("id"));
                            classmateEntity.setName(jsonObject.optString("name"));
                            classmateEntity.setImg(jsonObject.optString("img"));
                            classmateEntity.setPlace(jsonObject.optInt("place"));
                            onmicClassmateEntities.add(classmateEntity);
                        }
                    }
                    if (videoChatAction != null) {
                        videoChatAction.onStuMic(status, room, onmicClassmateEntities, offmicClassmateEntities, from, 1, nonce);
                    }
                } catch (Exception e) {

                }
//                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
//                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
//                    if (videoChatAction != null) {
//                        int count = object.optInt("num", 0);
//                        videoChatAction.raiseHandCount(count);
//                    }
//                }
                break;
            }
            default:
                break;
        }
        logger.d("onNotice:msg=" + msg);
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.AgoraChat.RAISE_HAND, XESCODE.AgoraChat.STUDY_ONMIC, XESCODE.AgoraChat.RAISE_HAND_COUNT};
    }

    @Override
    public void requestMicro(String nonce, String room, String from) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.AgoraChat.STU_RAISE_HAND);
            jsonObject.put("status", "on");
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("img", mGetInfo.getStuImg());

            jsonObject.put("camera", 0);
            jsonObject.put("linkNum", "" + mGetInfo.getStuLinkMicNum());
            jsonObject.put("raiseNum", "" + mGetInfo.getStuPutUpHandsNum());

            jsonObject.put("nonce", nonce);
            if ("t".equals(from)) {
                sendNoticeToMain(jsonObject);
            } else {
                sendNoticeToCoun(jsonObject);
            }
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("requestMicro", e);
        }
    }

    @Override
    public void giveupMicro(String from) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.AgoraChat.STU_RAISE_HAND);
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("status", "off");
            if ("t".equals(from)) {
                sendNoticeToMain(jsonObject);
            } else {
                sendNoticeToCoun(jsonObject);
            }
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("giveupMicro", e);
        }
    }

    @Override
    public void praise(String uid, int likes) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.AgoraChat.PRAISE_STU);
            jsonObject.put("to", uid);
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("likes", "" + likes);
            if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
                sendMessageMain(jsonObject);
            } else {
                sendMessageCoun(jsonObject);
            }
//            mLiveBll.sendMessage(jsonObject);
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("praise", e);
        }
    }

    @Override
    public void sendNetWorkQuality(int quality) {
        logger.d("sendNetWorkQuality:quality=" + quality);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.AgoraChat.PRAISE_STU);
            jsonObject.put("id", mGetInfo.getStuId());

            jsonObject.put("quality", quality);
            if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
                sendMessageMain(jsonObject);
            } else {
                sendMessageCoun(jsonObject);
            }
//            mLiveBll.sendMessage(jsonObject);
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendNetWorkQuality", e);
        }
    }

    @Override
    public void getStuInfoByIds(final String uid, final AbstractBusinessDataCallBack abstractBusinessDataCallBack) {
        getHttpManager().getStuInfoByIds(uid, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getStuInfoByIds:onPmSuccess=" + responseEntity.getJsonObject());
                HashMap<String, ClassmateEntity> classmateEntityHashMap = getHttpResponseParser().parseStuInfoByIds(uid, responseEntity);
                abstractBusinessDataCallBack.onDataSucess(classmateEntityHashMap);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.e("getStuInfoByIds:onPmFailure=" + msg, error);
                abstractBusinessDataCallBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d("getStuInfoByIds:onPmError=" + responseEntity.getErrorMsg());
                abstractBusinessDataCallBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void chatHandAdd(HttpCallBack call) {
        if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC) {
            getHttpManager().addStuPutUpHandsNum(mGetInfo.getStuId(), call);
        }
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect(IRCConnection connection) {
        if (videoChatAction != null) {
            videoChatAction.onConnect();
        }
    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {
        if (videoChatAction != null) {
            videoChatAction.onDisconnect();
        }
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {

    }

    @Override
    public void onUserList(String channel, User[] users) {

    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }
}

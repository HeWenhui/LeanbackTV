package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.business;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateReg;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/11.
 */
public class VideoChatIRCBll extends LiveBaseBll implements VideoChatEvent, NoticeAction, TopicAction, VideoChatHttp {
    private VideoChatBll videoChatAction;
    private LiveFragmentBase liveFragmentBase;
    /** 接麦已经连接老师 */
    private AtomicBoolean startRemote = new AtomicBoolean(false);
    public static final String DEFULT_VOICE_CHAT_STATE = "off";
    private String voiceChatStatus = DEFULT_VOICE_CHAT_STATE;
    private ArrayList<VideoChatStatusChange.ChatStatusChange> chatStatusChanges = new ArrayList<>();
    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;

    public VideoChatIRCBll(Activity context, LiveBll2 liveBll) {
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
        boolean allowLinkMic = getInfo.isAllowLinkMic();
        if (allowLinkMic) {
            VideoChatBll videoChatBll = new VideoChatBll(activity, this);
            videoChatBll.initView(mRootView);
            videoChatBll.setControllerBottom(baseLiveMediaControllerBottom);
            videoChatBll.setLiveAndBackDebug(mLiveBll);
            videoChatBll.setVideoChatHttp(this);
            videoChatBll.onLiveInit(getInfo);
            videoChatAction = videoChatBll;

            if(baseLiveMediaControllerBottom instanceof LiveUIStateReg){
                LiveUIStateReg halfBodyMediaControllerBottom = (LiveUIStateReg)
                        baseLiveMediaControllerBottom;
                halfBodyMediaControllerBottom.addLiveUIStateListener(onViewChange);
            }

            putInstance(VideoChatEvent.class, this);
        } else {
            mLiveBll.removeBusinessBll(this);
        }
    }

    private com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener onViewChange = new com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener() {
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
        liveFragmentBase.rePlay(b);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (videoChatAction != null) {
            if (modeChange) {
                videoChatAction.quit("off", "", "change");
            } else {
                String oldVoiceChatStatus = voiceChatStatus;
                if (LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
                    LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
                    voiceChatStatus = mainRoomstatus.getOpenhands();
                    videoChatAction.onJoin(mainRoomstatus.getOnmic(), mainRoomstatus.getOpenhands(),
                            mainRoomstatus.getRoom(), mainRoomstatus.isClassmateChange(), mainRoomstatus
                                    .getClassmateEntities(), "t");
                } else {
                    LiveTopic.RoomStatusEntity coachRoomstatus = liveTopic.getCoachRoomstatus();
                    coachRoomstatus = liveTopic.getCoachRoomstatus();
                    voiceChatStatus = coachRoomstatus.getOpenhands();
                    videoChatAction.onJoin(coachRoomstatus.getOnmic(), coachRoomstatus.getOpenhands(),
                            coachRoomstatus.getRoom(), coachRoomstatus.isClassmateChange(), coachRoomstatus
                                    .getClassmateEntities(), "f");
                }
                if (!oldVoiceChatStatus.equals(voiceChatStatus)) {
                    for (int i = 0; i < chatStatusChanges.size(); i++) {
                        chatStatusChanges.get(i).onVideoChatStatusChange(voiceChatStatus);
                    }
                }
            }
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        String msg = "onNotice";
        switch (type) {
            case XESCODE.RAISE_HAND: {
                String from = object.optString("from", "t");
                msg += ",RAISE_HAND:from=" + from + ",mode=" + mLiveBll.getMode();
                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                    String status = object.optString("status", "off");
                    voiceChatStatus = status;
                    if (videoChatAction != null) {
                        msg += "RAISE_HAND:status=" + status;
                        videoChatAction.raisehand(status, from, object.optString("nonce"));
                    }
                    for (int i = 0; i < chatStatusChanges.size(); i++) {
                        chatStatusChanges.get(i).onVideoChatStatusChange(status);
                    }
                }
            }
            break;
            case XESCODE.RAISE_HAND_SELF: {
                String from = object.optString("from", "t");
                msg += ",RAISE_HAND_SELF:from=" + from + ",mode=" + mLiveBll.getMode();
                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                    if (videoChatAction != null) {
                        String status = object.optString("status", "off");
                        int num = object.optInt("num", 0);
                        msg += "RAISE_HAND_SELF:status=" + status + ",num=" + num;
                        videoChatAction.raiseHandStatus(status, num, from);
                    }
                }
            }
            break;
            case XESCODE.REQUEST_ACCEPT: {
                String from = object.optString("from", "t");
                msg += ",REQUEST_ACCEPT:from=" + from + ",mode=" + mLiveBll.getMode();
                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                    if (videoChatAction != null) {
                        videoChatAction.requestAccept(from, object.optString("nonce"));
                    }
                }
            }
            break;
            case XESCODE.START_MICRO: {
                String from = object.optString("from", "t");
                msg += ",START_MICRO:from=" + from + ",mode=" + mLiveBll.getMode();
                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                    if (videoChatAction != null) {
                        String room = object.optString("room");
                        String status = object.optString("status", "off");
                        String nonce = object.optString("nonce", "");
                        boolean contain = false;
                        if (status.equals("on")) {
                            JSONArray students = object.optJSONArray("students");
                            if (students != null) {
                                for (int i = 0; i < students.length(); i++) {
                                    try {
                                        if (mGetInfo.getStuId().equals(students.getString(i))) {
                                            contain = true;
                                            break;
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                        videoChatAction.startMicro(status, nonce, contain, room, from);
                    }
                }
            }
            break;
            case XESCODE.ST_MICRO: {
                String from = object.optString("from", "t");
                String status = object.optString("status", "off");
                msg += ",ST_MICRO:from=" + from + ",mode=" + mLiveBll.getMode() + ",status=" + status;
                if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) || "f".equals(from) &&
                        LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                    if (videoChatAction != null) {
                        String room = object.optString("room");
                        videoChatAction.quit(status, room, from);
                    }
                }
                break;
            }
            case XESCODE.RAISE_HAND_COUNT: {
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
            default:
                break;
        }
        logger.d("onNotice:msg=" + msg);
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.RAISE_HAND, XESCODE.RAISE_HAND_SELF, XESCODE.REQUEST_ACCEPT, XESCODE.START_MICRO,
                XESCODE.ST_MICRO, XESCODE.RAISE_HAND_COUNT};
    }

    @Override
    public void requestMicro(String nonce, String from) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.REQUEST_MICRO);
            jsonObject.put("status", "on");
            jsonObject.put("network", "normal");
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("img", mGetInfo.getStuImg());
            jsonObject.put("courseid", mLiveBll.getCourseId());
            jsonObject.put("nonce", nonce);
            jsonObject.put("times", mGetInfo.getStuLinkMicNum());
            if ("t".equals(from)) {
                mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
            } else {
                mLiveBll.sendNotice(mLiveBll.getCounTeacherStr(), jsonObject);
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
            jsonObject.put("type", "" + XESCODE.REQUEST_MICRO);
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("status", "off");
            if ("t".equals(from)) {
                mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
            } else {
                mLiveBll.sendNotice(mLiveBll.getCounTeacherStr(), jsonObject);
            }
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("giveupMicro", e);
        }
    }

    @Override
    public void chatHandAdd(HttpCallBack call) {
        if (mGetInfo.getIsArts() == 0) {
            getHttpManager().chatHandAdd(call);
        }
    }
}

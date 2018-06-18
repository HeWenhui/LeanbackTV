package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Environment;

import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lyqai on 2018/3/20.
 */

public abstract class BaseIRCCallback implements IRCCallback {
    private final String TAG = "BaseIRCCallback";
    /** 主讲老师前缀 */
    public static final String TEACHER_PREFIX = "t_";
    /** 辅导老师前缀 */
    public static String COUNTTEACHER_PREFIX = "f_";
    private LogToFile mLogtf;
    private RoomAction mRoomAction;
    private LiveGetInfo mGetInfo;
    private final LiveTopic mLiveTopic;
    private VideoAction mVideoAction;
    /** 主讲教师 */
    private Teacher mMainTeacher;
    /** 主讲教师名字 */
    private String mMainTeacherStr = null;
    /** 辅导教师 */
    private Teacher mCounteacher;
    /** 辅导教师IRC */
    private String mCounTeacherStr = null;
    public final int mLiveType;

    BaseIRCCallback(int mLiveType, LiveTopic mLiveTopic) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        this.mLiveType = mLiveType;
        this.mLiveTopic = mLiveTopic;
    }

    @Override
    public void onStartConnect() {
        if (mRoomAction != null) {
            mRoomAction.onStartConnect();
        }
    }

    @Override
    public void onRegister() {
        mLogtf.d("onRegister");
        if (mRoomAction != null) {
            mRoomAction.onRegister();
        }
    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {
        mLogtf.i("onChannelInfo:userCount=" + userCount);
        onTopic(channel, topic, "", 0, true);
    }

    @Override
    public abstract void onTopic(String channel, String topicstr, String setBy, long date, boolean changed);

    String lastNotice = "";
    String voiceChatStatus = "off";

    @Override
    public abstract void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                                  final String notice);

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {
        if (mRoomAction != null) {
            mRoomAction.onMessage(target, sender, login, hostname, text, "");
        }
    }

    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                 String message) {
        if (mRoomAction != null) {
            mRoomAction.onPrivateMessage(isSelf, sender, login, hostname, target, message);
        }
    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {
        mLogtf.d("onDisconnect:isQuitting=" + isQuitting);
        if (mRoomAction != null) {
            mRoomAction.onDisconnect();
        }
    }

    @Override
    public void onConnect(IRCConnection connection) {
        if (mRoomAction != null) {
            mRoomAction.onConnect();
        }
    }

    @Override
    public void onUserList(String channel, User[] users) {
        String s = "onUserList:channel=" + channel + ",users=" + users.length;
        boolean haveMainTeacher = false;//主讲老师
        boolean haveCounteacher = false;//辅导老师
        ArrayList<User> arrayList = new ArrayList<>();
        for (int i = 0; i < users.length; i++) {
            User user = users[i];
            String _nick = user.getNick();
            if (_nick != null && _nick.length() > 2) {
                if (_nick.startsWith(TEACHER_PREFIX)) {
                    s += ",mainTeacher=" + _nick;
                    haveMainTeacher = true;
                    synchronized (this) {
                        mMainTeacher = new Teacher(_nick);
                        mMainTeacherStr = _nick;
                    }
                    if (LiveTopic.MODE_CLASS.endsWith(mLiveTopic.getMode())
                            && mVideoAction != null) {
                        mVideoAction.onTeacherQuit(false);
                    }
                } else if (_nick.startsWith(COUNTTEACHER_PREFIX)) {
                    mCounTeacherStr = _nick;
                    haveCounteacher = true;
                    mCounteacher.isLeave = false;
                    s += ",counteacher=" + _nick;
                    if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())
                            && mVideoAction != null) {
                        mVideoAction.onTeacherQuit(false);
                    }
                } else {
                    boolean isMyTeam = isMyTeam(user.getNick());
                    if (isMyTeam) {
                        arrayList.add(user);
                    }
                }
            } else {
                s += ",else=" + _nick;
            }
        }
        if (!haveCounteacher) {
            mCounteacher.isLeave = true;
        }
        if (arrayList.isEmpty()) {// 学生人数为空
            s += ",arrayList=isSpace";
        }
        s += ",haveMainTeacher=" + haveMainTeacher;
        if (mLiveType == LiveBll.LIVE_TYPE_LIVE) {
            s += ",haveCounteacher=" + haveCounteacher;
        }
        mLogtf.d(s);
        if (mRoomAction != null) {
            User[] users2 = new User[arrayList.size()];
            arrayList.toArray(users2);
            mRoomAction.onUserList(channel, users2);
        }
    }

    /** 是不是自己组的人 */
    private boolean isMyTeam(String sender) {
        boolean isMyTeam = true;
        ArrayList<String> teamStuIds = mGetInfo.getTeamStuIds();
        if (mLiveType == LiveBll.LIVE_TYPE_LIVE && !teamStuIds.isEmpty()) {
            isMyTeam = false;
            String split[] = sender.split("_");
            if (split.length > 4) {
                String uid = split[3];
                for (int j = 0; j < teamStuIds.size(); j++) {
                    String string = teamStuIds.get(j);
                    if (("" + string).equals(uid)) {
                        isMyTeam = true;
                        break;
                    }
                }
            }
        }
        return isMyTeam;
    }

    public void onJoin(String target, String sender, String login, String hostname) {
        Loger.d(TAG, "onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
        if (sender.startsWith(TEACHER_PREFIX)) {
            synchronized (this) {
                mMainTeacher = new Teacher(sender);
                mMainTeacherStr = sender;
            }
            mLogtf.d("onJoin:mainTeacher:target=" + target + ",mode=" + mLiveTopic.getMode());
            if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                mVideoAction.onTeacherQuit(false);
            }
        } else if (sender.startsWith(COUNTTEACHER_PREFIX)) {
            mCounTeacherStr = sender;
            mCounteacher.isLeave = false;
            mLogtf.d("onJoin:Counteacher:target=" + target + ",mode=" + mLiveTopic.getMode());
            if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                mVideoAction.onTeacherQuit(false);
            }
        } else {
            if (mRoomAction != null) {
//                    if (sender.startsWith(LiveBll.TEACHER_PREFIX) || sender.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//                        //老师不计算在内
//                        return;
//                    }
                boolean isMyTeam = isMyTeam(sender);
                if (isMyTeam) {
                    mRoomAction.onJoin(target, sender, login, hostname);
                }
            }
        }
    }

    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        Loger.d(TAG, "onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                + sourceHostname + ",reason=" + reason);
        if (sourceNick.startsWith(TEACHER_PREFIX)) {
            synchronized (this) {
                mMainTeacher = null;
            }
            mLogtf.d("onQuit:mainTeacher quit");
            if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                mVideoAction.onTeacherQuit(true);
            }
        } else if (sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
            mCounteacher.isLeave = true;
            mLogtf.d("onQuit:Counteacher quit");
            if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                mVideoAction.onTeacherQuit(true);
            }
        } else {
            if (mRoomAction != null) {
//                    if (sourceNick.startsWith(LiveBll.TEACHER_PREFIX) || sourceNick.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//                        //老师不计算在内
//                        return;
//                    }
                boolean isMyTeam = isMyTeam(sourceNick);
                if (isMyTeam) {
                    mRoomAction.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
                }
            }
        }
    }

    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
                       String recipientNick, String reason) {
        mLogtf.d("onKick:target=" + target + ",kickerNick=" + kickerNick + ",kickerLogin=" + kickerLogin
                + ",kickerHostname=" + kickerHostname + ",reason=" + reason);
        if (mRoomAction != null) {
            mRoomAction.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        }
    }

}

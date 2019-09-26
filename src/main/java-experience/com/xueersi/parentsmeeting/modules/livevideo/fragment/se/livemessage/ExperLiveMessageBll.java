package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.livemessage;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RoomAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.ExperLiveMessageStandPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

public class ExperLiveMessageBll implements KeyboardUtil.OnKeyboardShowingListener, RoomAction {
    private String TAG = "ExperLiveMessageBll";
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    /**
     * 消息
     */
    private BaseLiveMessagePager mLiveMessagePager;
    Activity activity;
    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;
    /**
     * 横屏聊天信息
     */
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    private LogToFile logToFile;

    ExperLiveMessageBll(Activity activity) {
        this.activity = activity;
        logToFile = new LogToFile(activity, TAG);
        peopleCount.setContext(activity);
    }

    public void setBaseLiveMediaControllerBottom(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
        this.baseLiveMediaControllerBottom = baseLiveMediaControllerBottom;
    }

    public BaseLiveMessagePager initExper(LiveViewAction liveViewAction, int mode) {
        boolean isRegister = false;
        if (mLiveMessagePager != null) {
            liveViewAction.removeView(mLiveMessagePager.getRootView());
            isRegister = mLiveMessagePager.isRegister();
        }
        if (mode == ExperConfig.COURSE_STATE_2) {
            ExperLiveMessageStandPager experLiveMessageStandPager = new ExperLiveMessageStandPager(
                    activity,
                    this,
                    baseLiveMediaControllerBottom,
                    liveMessageLandEntities,
                    null);
            experLiveMessageStandPager.setStarGoldImageViewVisible(false);
            mLiveMessagePager = experLiveMessageStandPager;
        } else {
            LiveMessagePager liveMessagePager = new LiveMessagePager(activity, baseLiveMediaControllerBottom, liveMessageLandEntities, null);
            mLiveMessagePager = liveMessagePager;
        }
        mLiveMessagePager.setPeopleCount(peopleCount);
        if (peopleCount.get() > 0) {
            mLiveMessagePager.onUserList("", new User[peopleCount.get()]);
        }
        mLiveMessagePager.setIsRegister(isRegister);
        liveViewAction.addView(mLiveMessagePager.getRootView());
        return mLiveMessagePager;
    }

    @Override
    public void onKeyboardShowing(boolean isShowing) {

    }

    private List<String> users = new ArrayList<>();

    @Override
    public void onStartConnect() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onStartConnect();
        }
    }

    @Override
    public void onConnect() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onConnect();
        }
    }

    @Override
    public void onRegister() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onRegister();
        }
    }

    @Override
    public void onDisconnect() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onDisconnect();
        }
    }

    @Override
    public void onUserList(String channel, User[] users) {
        for (User user : users) {
            if (!this.users.contains(user.getNick())) {
                this.users.add(user.getNick());
            }
        }
        logToFile.d("onUserList:users=" + users.length + ",new=" + this.users.size());
        peopleCount.set(this.users.size(), new Exception());
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onUserList(channel, users);
        }
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        if (!users.contains(sender)) {
            peopleCount.set(peopleCount.get() + 1, new Exception(sender));
            users.add(sender);
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onJoin(target, sender, login, hostname);
            }
        } else {
            logToFile.d("onJoin(!contains):sender=" + sender);
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        if (users.contains(sourceNick)) {
            peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
            users.remove(sourceNick);
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
            }
        } else {
            logToFile.d("onQuit(!contains):sender=" + sourceNick);
        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        }
    }

    @Override
    public void onDisable(boolean disable, boolean fromNotice) {

    }

    @Override
    public void onOtherDisable(String id, String name, boolean disable) {

    }

    @Override
    public void onopenchat(boolean openchat, String mode, boolean fromNotice) {

    }

    @Override
    public void onOpenbarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage, boolean isFDLKOpenbarrage) {

    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }
}

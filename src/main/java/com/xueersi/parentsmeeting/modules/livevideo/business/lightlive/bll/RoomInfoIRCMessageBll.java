package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.xueersi.common.base.XrsCrashReport;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.LightLiveRoomInfoPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.agora.rtc.internal.RtcEngineMessage;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive
 * @ClassName: RoomInfoIRCMessageBll
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/23 16:19
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/23 16:19
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RoomInfoIRCMessageBll extends LiveBaseBll implements MessageAction, NoticeAction {

    private LinearLayout infoLayout;
    private LightLiveRoomInfoPager lightLiveRoomInfoPager;
    private int num;
    private List<String> users = new ArrayList<>();

    /**
     * 当前显示的人数
     */
    protected XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    /**
     * 从irc 用户列表返回人数
     */
    private int userListNum;
    /**
     * 从Notice返回的人数
     */
    private int noticeNum;
    /**
     * 公告
     */
    private String mNotice;
    /**
     * 是否增加人数
     */
//    private boolean chatSwitch;
    private final int NUM_FROM_USERLIST = 1;

    private final int NUM_FROM_NOTICE = 2;

    private final long TOTAL_TIME = 58000;

    private RoomInfoCountDownTimer lastCountDownTimer;


    public RoomInfoIRCMessageBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        lightLiveRoomInfoPager = new LightLiveRoomInfoPager(mContext);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo != null) {
            if (!getInfo.getGentlyNotice().isEmpty()) {
                mNotice = "公告: " + getInfo.getGentlyNotice();
                if (lightLiveRoomInfoPager != null) {
                    lightLiveRoomInfoPager.setTvNotice(mNotice);
                }
            }
//            chatSwitch = getInfo.getChatSwitch() == 1;
        }

    }

    @Override
    public void initView() {
        infoLayout = mContentView.findViewById(R.id.ll_live_room_info);
        if (!mIsLand.get()) {
            if (lightLiveRoomInfoPager != null) {
                infoLayout.removeAllViews();
                infoLayout.setVisibility(View.VISIBLE);
                infoLayout.addView(lightLiveRoomInfoPager.getRootView());
                ViewGroup.LayoutParams params = lightLiveRoomInfoPager.getRootView().getLayoutParams();
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                params.height = SizeUtils.Dp2Px(mContext, 38);
                lightLiveRoomInfoPager.getRootView().setLayoutParams(params);
                lightLiveRoomInfoPager.setTvNotice(mNotice);
            }
        } else {
            infoLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect(IRCConnection connection) {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {

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
        this.users.clear();
        for (User user : users) {
            String _nick = user.getNick();
            if (_nick != null && (_nick.startsWith(LiveMessageConfig.TEACHER_PREFIX)
                    || _nick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX))) {
                continue;
            }
            this.users.add(user.getNick());
        }
        userListNum = this.users.size();
        setShowCount(NUM_FROM_USERLIST);

    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        if (sender != null && !users.contains(sender) && !sender.startsWith(LiveMessageConfig.TEACHER_PREFIX)
                && !sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
            XrsCrashReport.d(TAG, "onJoin:sender=" + sender + ",get=" + peopleCount.get() + ",users=" + users.size() + ",this=" + this);
            users.add(sender);
            userListNum++;
            peopleCount.set(peopleCount.get() + 1, new Exception());
            setShowCount(NUM_FROM_USERLIST);
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
//        if (sourceNick != null && users.contains(sourceNick) && !sourceNick.startsWith(LiveMessageConfig.TEACHER_PREFIX)
//                && !sourceNick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
//            boolean remove = users.remove(sourceNick);
//            XrsCrashReport.d(TAG, "onQuit:sourceNick=" + sourceNick + ",get=" + peopleCount.get() + ",remove=" + remove + ",users=" + users.size() + ",this=" + this);
//            if (remove) {
//                peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
//                lightLiveRoomInfoPager.setTvCount("在线" + getShowCount() + "人");
//            }
//        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }

    private int getShowCount() {
        int count;
        count = peopleCount.get();
        return count;
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.LIGHTLIVE_ROOM_STUDENT_NUM: {
                noticeNum = data.optInt("num");
                setShowCount(NUM_FROM_NOTICE);
                break;
            }
            case XESCODE.LIGHTLIVE_ROOM_NOTICE: {
                String notice = data.optString("notice");
                if (notice != null) {
                    mNotice = notice;
                    if (lightLiveRoomInfoPager != null) {
                        lightLiveRoomInfoPager.setHasClose(false);
                        lightLiveRoomInfoPager.setTvNotice(mNotice);
                    }
                }
                break;
            }
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.LIGHTLIVE_ROOM_NOTICE, XESCODE.LIGHTLIVE_ROOM_STUDENT_NUM};
    }

    public void setShowCount(int from) {
        if (NUM_FROM_USERLIST == from) {
            if (userListNum >= peopleCount.get()) {
                peopleCount.set(userListNum, new Exception());
                lightLiveRoomInfoPager.setTvCount("人气值" + peopleCount.get());
            }
        } else if (NUM_FROM_NOTICE == from) {
            if (noticeNum > peopleCount.get()) {
                if (lastCountDownTimer != null) {
                    lastCountDownTimer.cancel();
                }
                post(new Runnable() {
                    @Override
                    public void run() {
                        int num = noticeNum - peopleCount.get();
                        if (num <= 0) {
                            num = 1;
                        }
                        lastCountDownTimer = new RoomInfoCountDownTimer(TOTAL_TIME / num);
                        lastCountDownTimer.start();
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lastCountDownTimer != null) {
            lastCountDownTimer.cancel();
            lastCountDownTimer = null;
        }
    }

    /**
     * 58S缓慢增加人数
     */
    class RoomInfoCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public RoomInfoCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public RoomInfoCountDownTimer(long countDownInterval) {
            super(TOTAL_TIME, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            peopleCount.set(peopleCount.get() + 1, new Exception());
            lightLiveRoomInfoPager.setTvCount("人气值" + peopleCount.get());
        }

        @Override
        public void onFinish() {
        }
    }

}

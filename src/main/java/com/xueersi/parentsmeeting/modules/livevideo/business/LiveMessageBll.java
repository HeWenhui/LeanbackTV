package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveMessageLandPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveMessagePortPager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.xesalib.utils.log.Loger;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2016/9/23.
 * 聊天消息，一些进入房间状态的消息
 */
public class LiveMessageBll implements RoomAction {
    private String TAG = "LiveMessageBll";
    /** 消息 */
    private BaseLiveMessagePager mLiveMessagePager;
    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;
    private Activity activity;
    private Handler mHandler = new Handler();
    public QuestionBll questionBll;
    private RelativeLayout rlLiveMessageContent;
    private LiveBll mLiveBll;
    private boolean openchat;
    private String mode = null;
    /** 横屏聊天信息 */
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    /** 竖屏聊天信息 */
    private ArrayList<LiveMessageEntity> liveMessagePortEntities = new ArrayList<>();
    /** 直播类型 */
    private int liveType;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    /** 聊天中老师连接是否可以点击 */
    public int urlclick = 0;
    public LiveGetInfo getInfo;

    public LiveMessageBll(Activity activity, int liveType) {
        this.activity = activity;
        this.liveType = liveType;
    }

    public void setQuestionBll(QuestionBll questionBll) {
        this.questionBll = questionBll;
        questionBll.setLiveMessageBll(this);
    }

    public void setLiveBll(LiveBll mLiveBll) {
        this.mLiveBll = mLiveBll;
        if (mLiveMessagePager != null) {
            mLiveMessagePager.setLiveBll(mLiveBll);
        }
    }

    public BaseLiveMediaControllerBottom getLiveMediaControllerBottom() {
        return baseLiveMediaControllerBottom;
    }

    public void setLiveMediaControllerBottom(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
        this.baseLiveMediaControllerBottom = baseLiveMediaControllerBottom;
    }

    public View getView() {
        return rlLiveMessageContent;
    }

    public void initView(RelativeLayout bottomContent, boolean isLand) {
        rlLiveMessageContent = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlLiveMessageContent, params);
        String text = null;
        boolean isRegister = false;
        boolean isHaveFlowers = false;
        boolean isCloseChat = false;
        BaseLiveMessagePager oldLiveMessagePager = mLiveMessagePager;
        if (mLiveMessagePager != null) {
            text = mLiveMessagePager.getMessageContentText();
            isRegister = mLiveMessagePager.isRegister();
            isHaveFlowers = mLiveMessagePager.isHaveFlowers();
            isCloseChat = mLiveMessagePager.isCloseChat();
            mLiveMessagePager.pool.shutdown();
        }
        if (isLand) {
//            if (liveType == LiveBll.LIVE_TYPE_LECTURE) {
//                mLiveMessagePager = new LiveMessageLandPager(activity, questionBll, baseLiveMediaControllerBottom, liveMessageLandEntities, liveMessagePortEntities);
//            } else {
//                mLiveMessagePager = new LiveMessagePager(activity, questionBll, baseLiveMediaControllerBottom, liveMessageLandEntities);
//            }
            if (liveType == LiveBll.LIVE_TYPE_LECTURE) {
                LiveMessagePager liveMessagePager =
                        new LiveMessagePager(activity, questionBll, baseLiveMediaControllerBottom, liveMessageLandEntities, liveMessagePortEntities);
                mLiveMessagePager = liveMessagePager;
            } else {
                long before = System.currentTimeMillis();
                LiveMessagePager liveMessagePager = new LiveMessagePager(activity, questionBll, baseLiveMediaControllerBottom, liveMessageLandEntities, null);
                mLiveMessagePager = liveMessagePager;
                Loger.d(TAG, "initView:time1=" + (System.currentTimeMillis() - before));
            }
        } else {
            mLiveMessagePager = new LiveMessagePortPager(activity, questionBll, liveMessagePortEntities, liveMessageLandEntities);
        }
        mLiveMessagePager.getInfo = getInfo;
        mLiveMessagePager.urlclick = urlclick;
        mLiveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setMessageBll(this);
        mLiveMessagePager.setLiveBll(mLiveBll);
        mLiveMessagePager.onModeChange(mLiveBll.getMode());
        mLiveMessagePager.setIsRegister(isRegister);
        if (peopleCount.get() > 0) {
            mLiveMessagePager.onUserList("", new User[peopleCount.get()]);
        }
        if (text != null) {
            mLiveMessagePager.setEtMessageContentText(text);
        } else {
            mLiveMessagePager.setEtMessageContentText("");
        }
        mLiveMessagePager.setHaveFlowers(isHaveFlowers);
        mLiveMessagePager.closeChat(isCloseChat);
        if (mode != null) {
            mLiveMessagePager.onopenchat(openchat, mode, false);
        }
        rlLiveMessageContent.addView(mLiveMessagePager.getRootView(), params);
        //如果旧的聊天数据没解析完，更新新的Adapter
        if (oldLiveMessagePager != null) {
            oldLiveMessagePager.setOtherMessageAdapter(mLiveMessagePager.getMessageAdapter());
        }
        if (oldLiveMessagePager instanceof LiveMessageLandPager) {
            oldLiveMessagePager.setHaveFlowers(false);
        }
    }

    public void setLiveGetInfo(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        urlclick = getInfo.getUrlClick();
        if (mLiveMessagePager != null) {
            mLiveMessagePager.urlclick = urlclick;
            mLiveMessagePager.getInfo = getInfo;
        }
    }

    /**
     * 开始倒计时
     *
     * @param time 倒计时时间
     * @return
     */
    public Runnable startCountDown(final String tag, final int time) {
        final AtomicInteger atomicInteger = new AtomicInteger(time);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mLiveMessagePager != null) {
                    mLiveMessagePager.countDown(tag, atomicInteger.get());
                }
                if (atomicInteger.get() > 0) {
                    atomicInteger.set(atomicInteger.get() - 1);
                    rlLiveMessageContent.postDelayed(this, 1000);
                }
            }
        };
        rlLiveMessageContent.post(runnable);
        return runnable;
    }

    public void onTitleShow(boolean show) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onTitleShow(show);
        }
    }

    public void closeChat(boolean close) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.closeChat(close);
        }
    }

    public boolean onBack() {
        return mLiveMessagePager.onBack();
    }

    public void onDestroy() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.pool.shutdown();
        }
    }

    @Override
    public void onUserList(String channel, User[] users) {
        peopleCount.set(users.length, new Exception());
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onUserList(channel, users);
        }
    }

    @Override
    public void onMessage(final String target, final String sender, final String login, final String hostname, final String text) {
        if (!"NOTICE".equals(target) && mLiveMessagePager.isCloseChat()) {//只看老师
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLiveMessagePager != null) {
                    mLiveMessagePager.onMessage(target, sender, login, hostname, text);
                }
            }
        });
    }

    public void onModeChange(final String mode, final boolean isPresent) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onModeChange(mode);
        }
    }

    public void onPrivateMessage(boolean isSelf, final String sender, String login, String hostname, String target, final String
            message) {
        if (isSelf && "T".equals(message)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra("msg", "您的帐号已在其他设备登录，请重新进入直播间");
                    activity.setResult(ShareBusinessConfig.LIVE_USER_KICK, intent);
                    activity.finish();
                }
            });
        } else {
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onPrivateMessage(isSelf, sender, login, hostname, target, message);
            }
        }
    }

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
    public void onJoin(String target, String sender, String login, String hostname) {
//        if (sender.startsWith(LiveBll.TEACHER_PREFIX) || sender.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//            //老师不计算在内
//            return;
//        }
        peopleCount.set(peopleCount.get() + 1, new Exception(sender));
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onJoin(target, sender, login, hostname);
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
//        if (sourceNick.startsWith(LiveBll.TEACHER_PREFIX) || sourceNick.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//            //老师不计算在内
//            return;
//        }
        peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
                       String recipientNick, String reason) {

    }

    @Override
    public void onDisable(boolean disable, boolean fromNotice) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onDisable(disable, fromNotice);
        }
    }

    @Override
    public void onopenchat(boolean openchat, String mode, boolean fromNotice) {
        this.openchat = openchat;
        this.mode = mode;
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onopenchat(openchat, mode, fromNotice);
        }
    }

    @Override
    public void onOpenbarrage(final boolean openbarrage, boolean fromNotice) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onOpenbarrage(openbarrage, fromNotice);
        }
    }

    @Override
    public void videoStatus(String status) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.videoStatus(status);
        }
    }

    public void setVideoLayout(int width, int height) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.setVideoWidthAndHeight(width, height);
        }
    }

    public void onGetMyGoldDataEvent(String goldNum) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onGetMyGoldDataEvent(goldNum);
        }
    }

    public void showInput() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onQuestionHide();
        }
    }

    public void hideInput() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onQuestionShow();
        }
    }

    public void addMessage(String sender, int type, String text) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.addMessage(sender, type, text);
        }
    }
}
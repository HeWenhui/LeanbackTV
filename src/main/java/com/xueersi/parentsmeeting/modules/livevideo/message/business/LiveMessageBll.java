package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.RoomAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.parentsmeeting.modules.livevideo.message.KeyBordAction;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.HalfBodyArtsLiveMsgPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.HalfBodyLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.HalfBodyPrimaryLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessageLandPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePortPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessageStandPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.PreSchoolLiveMainMsgPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.SmallChineseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.SmallEnglishLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LivePsMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.PreSchoolLiveTrainMsgPager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by linyuqiang on 2016/9/23.
 * 聊天消息，一些进入房间状态的消息
 */
public class LiveMessageBll implements RoomAction, QuestionShowAction, KeyBordAction, KeyboardShowingReg, LiveMessageSend,
        KeyboardUtil.OnKeyboardShowingListener {
    private String TAG = "LiveMessageBll";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    /**
     * 消息
     */
    private BaseLiveMessagePager mLiveMessagePager;

//    private BaseSmallEnglishLiveMessagePager mSmallEnglishLiveMessagePager;

    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;

    private BaseLiveMediaControllerTop baseLiveMediaControllerTop;

    private Activity activity;
    private Handler mHandler = new Handler();
    private RelativeLayout rlLiveMessageContent;
    private IRCState mLiveBll;
    private boolean openchat;
    /**
     * 是不是正在答题
     */
    private int isAnaswer = -1;
    private String mode = null;
    /**
     * 横屏聊天信息
     */
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    /**
     * 竖屏聊天信息
     */
    private ArrayList<LiveMessageEntity> liveMessagePortEntities = new ArrayList<>();
    /**
     * 直播类型
     */
    private int liveType;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    /**
     * 聊天中老师连接是否可以点击
     */
    public int urlclick = 0;
    public LiveGetInfo getInfo;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    ArrayList<KeyboardUtil.OnKeyboardShowingListener> keyboardShowingListeners = new ArrayList<>();
    //是否启用小英MMD皮肤
    private boolean isSmallEnglish = false;

    private List<String> users = new ArrayList<>();

    public LiveMessageBll(Activity activity, int liveType) {
        this.activity = activity;
        this.liveType = liveType;
        ProxUtil.getProxUtil().put(activity, LiveMessageSend.class, this);
        ProxUtil.getProxUtil().put(activity, KeyBordAction.class, this);
        ProxUtil.getProxUtil().put(activity, KeyboardShowingReg.class, this);
    }

    public void setLiveBll(IRCState mLiveBll) {
        this.mLiveBll = mLiveBll;
        if (mLiveMessagePager != null) {
            mLiveMessagePager.setIrcState(mLiveBll);
        }
    }

    public BaseLiveMediaControllerBottom getLiveMediaControllerBottom() {
        return baseLiveMediaControllerBottom;
    }

    public void setLiveMediaControllerBottom(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
        this.baseLiveMediaControllerBottom = baseLiveMediaControllerBottom;
    }

    public void setBaseLiveMediaControllerTop(BaseLiveMediaControllerTop controllerTop) {
        this.baseLiveMediaControllerTop = controllerTop;
    }

    public View getView() {
        return rlLiveMessageContent;
    }

    /**
     * 站立直播聊天
     *
     * @param liveViewAction
     */
    public void initViewLiveStand(LiveViewAction liveViewAction) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (rlLiveMessageContent == null) {
            rlLiveMessageContent = new RelativeLayout(activity);
            liveViewAction.addView(LiveVideoLevel.LEVEL_MES, rlLiveMessageContent, params);
        } else {
            rlLiveMessageContent.removeAllViews();
        }
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
            mLiveMessagePager.onDestroy();
        }

        long before = System.currentTimeMillis();
        liveMessageLandEntities.clear();

        LiveMessageStandPager liveMessagePager = new LiveMessageStandPager(activity, this,
                baseLiveMediaControllerBottom, liveMessageLandEntities, null);
        mLiveMessagePager = liveMessagePager;
        logger.d("initViewLiveStand:time1=" + (System.currentTimeMillis() - before));

        mLiveMessagePager.setGetInfo(getInfo);
        mLiveMessagePager.urlclick = urlclick;
        mLiveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setIrcState(mLiveBll);
        mLiveMessagePager.onModeChange(mLiveBll.getMode());

        if (text != null) {
            mLiveMessagePager.setEtMessageContentText(text);
        } else {
            mLiveMessagePager.setEtMessageContentText("");
        }
        mLiveMessagePager.setIsRegister(isRegister);
        if (peopleCount.get() > 0) {
            mLiveMessagePager.onUserList("", new User[peopleCount.get()]);
        }
        mLiveMessagePager.closeChat(isCloseChat);
        if (isAnaswer != -1) {//这表示收到过答题变化
            mLiveMessagePager.onQuestionShow(null, isAnaswer == 1);
        }
        if (mode != null) {
            mLiveMessagePager.onopenchat(openchat, mode, false);
        }
        final View view = mLiveMessagePager.getRootView();
        view.setVisibility(View.INVISIBLE);
        rlLiveMessageContent.addView(view, params);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 半身直播 聊天
     *
     * @param liveViewAction
     */
    public void initHalfBodyLive(final LiveViewAction liveViewAction) {
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (rlLiveMessageContent == null) {
            rlLiveMessageContent = new RelativeLayout(activity);
            //调整 消息面板的层级
            liveViewAction.addView(LiveVideoLevel.LEVEL_MES, rlLiveMessageContent, params);
        } else {
            //调整 消息面板的层级
            //rlLiveMessageContent.removeAllViews();
            rlLiveMessageContent.removeAllViewsInLayout();
            liveViewAction.removeView(rlLiveMessageContent);
            liveViewAction.addView(LiveVideoLevel.LEVEL_MES, rlLiveMessageContent, params);
        }

        String text = null;
        boolean isRegister = false;
        boolean isHaveFlowers = false;
        boolean isCloseChat = false;
        BaseLiveMessagePager oldLiveMessagePager = mLiveMessagePager;
        //拷贝状态
        if (mLiveMessagePager != null) {
            text = mLiveMessagePager.getMessageContentText();
            isRegister = mLiveMessagePager.isRegister();
            isHaveFlowers = mLiveMessagePager.isHaveFlowers();
            isCloseChat = mLiveMessagePager.isCloseChat();
            mLiveMessagePager.onDestroy();
        }


        long before = System.currentTimeMillis();
        BaseLiveMessagePager liveMessagePager = null;

        //根据不同的直播类型创建不同皮肤
        if (getInfo != null && getInfo.isPreschool()) {
            // 幼教
            liveMessagePager = new PreSchoolLiveMainMsgPager(activity, this,
                    null, baseLiveMediaControllerBottom, baseLiveMediaControllerTop, liveMessageLandEntities, null);
        } else {
            if (getInfo != null && getInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
                // 语文
                if (getInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY_CLASS) {
                    liveMessagePager = new HalfBodyPrimaryLiveMessagePager(activity, this,
                            null, baseLiveMediaControllerBottom, liveMessageLandEntities, null, HalfBodyLiveConfig.SKIN_TYPE_CH);
                } else {
                    liveMessagePager = new HalfBodyArtsLiveMsgPager(activity, this,
                            null, baseLiveMediaControllerBottom, liveMessageLandEntities, null);
                }
            } else {
                // 理科
                if (getInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY) {
                    liveMessagePager = new HalfBodyLiveMessagePager(activity, this,
                            null, baseLiveMediaControllerBottom, baseLiveMediaControllerTop, liveMessageLandEntities, null);
                } else {
                    liveMessagePager = new HalfBodyPrimaryLiveMessagePager(activity, this,
                            null, baseLiveMediaControllerBottom, liveMessageLandEntities, null, 0);
                }
            }
        }

        mLiveMessagePager = liveMessagePager;
        mLiveMessagePager.setGetInfo(getInfo);
        mLiveMessagePager.urlclick = urlclick;
        mLiveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setIrcState(mLiveBll);
        mLiveMessagePager.onModeChange(mLiveBll.getMode());
        mLiveMessagePager.closeChat(isCloseChat);
        if (text != null) {
            mLiveMessagePager.setEtMessageContentText(text);
        } else {
            mLiveMessagePager.setEtMessageContentText("");
        }
        mLiveMessagePager.setIsRegister(isRegister);
        if (peopleCount.get() > 0) {
            mLiveMessagePager.onUserList("", new User[peopleCount.get()]);
        }
        if (isAnaswer != -1) {
            //这表示收到过答题变化
            mLiveMessagePager.onQuestionShow(null, isAnaswer == 1);
        }
        if (mode != null) {
            mLiveMessagePager.onopenchat(openchat, mode, false);
        }
        final View view = mLiveMessagePager.getRootView();
        view.setVisibility(View.INVISIBLE);
        rlLiveMessageContent.addView(view, params);


        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    public void initViewLive(LiveViewAction liveViewAction) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (rlLiveMessageContent == null) {
            rlLiveMessageContent = new RelativeLayout(activity);
            liveViewAction.addView(LiveVideoLevel.LEVEL_MES, rlLiveMessageContent, params);
        } else {
            //rlLiveMessageContent.removeAllViews();
            rlLiveMessageContent.removeAllViewsInLayout();
        }
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
            mLiveMessagePager.onDestroy();
            if (mLiveMessagePager instanceof LiveMessageStandPager) {
                liveMessageLandEntities.clear();
            }
        }

        long before = System.currentTimeMillis();
        if (!isSmallEnglish) {
            if (LiveVideoConfig.isSmallChinese) {//如果是语文
                SmallChineseLiveMessagePager chineseLiveMessagePager = new SmallChineseLiveMessagePager(activity, this, null, baseLiveMediaControllerBottom
                        , liveMessageLandEntities, liveMessagePortEntities);
                mLiveMessagePager = chineseLiveMessagePager;

            } else if (getInfo != null && getInfo.isPreschool()) {
                PreSchoolLiveTrainMsgPager liveMessagePager = new PreSchoolLiveTrainMsgPager(activity, this, null,
                        baseLiveMediaControllerBottom, liveMessageLandEntities, null);
                mLiveMessagePager = liveMessagePager;
            } else if (LiveVideoConfig.isPrimary) {
                LivePsMessagePager liveMessagePager = new LivePsMessagePager(activity, this, null,
                        baseLiveMediaControllerBottom, liveMessageLandEntities, null);
                mLiveMessagePager = liveMessagePager;
            } else {
                LiveMessagePager liveMessagePager = new LiveMessagePager(activity, null,
                        baseLiveMediaControllerBottom, liveMessageLandEntities, null);
                mLiveMessagePager = liveMessagePager;
            }
        } else {
            SmallEnglishLiveMessagePager sEnglishLiveMessagePager = new SmallEnglishLiveMessagePager(activity, this,
                    null, baseLiveMediaControllerBottom, liveMessageLandEntities, null);
            mLiveMessagePager = sEnglishLiveMessagePager;
        }


        logger.d("initViewLive:time1=" + (System.currentTimeMillis() - before));

        mLiveMessagePager.setGetInfo(getInfo);
        mLiveMessagePager.urlclick = urlclick;
        mLiveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setIrcState(mLiveBll);
        mLiveMessagePager.onModeChange(mLiveBll.getMode());

        if (text != null) {
            mLiveMessagePager.setEtMessageContentText(text);
        } else {
            mLiveMessagePager.setEtMessageContentText("");
        }
        mLiveMessagePager.setIsRegister(isRegister);
        if (peopleCount.get() > 0) {
            mLiveMessagePager.onUserList("", new User[peopleCount.get()]);
        }
        mLiveMessagePager.closeChat(isCloseChat);
        if (mode != null) {
            mLiveMessagePager.onopenchat(openchat, mode, false);
        }
        rlLiveMessageContent.addView(mLiveMessagePager.getRootView(), params);


    }

    public void initView(LiveViewAction liveViewAction, boolean isLand) {
        rlLiveMessageContent = new RelativeLayout(activity);
        rlLiveMessageContent.setId(R.id.iv_livevideo_message_content1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        liveViewAction.addView(LiveVideoLevel.LEVEL_MES, rlLiveMessageContent, params);
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
            mLiveMessagePager.onDestroy();
        }
        if (isLand) {
//            if (liveType == LiveBll.LIVE_TYPE_LECTURE) {
//                mLiveMessagePager = new LiveMessageLandPager(activity, questionBll, baseLiveMediaControllerBottom,
// liveMessageLandEntities, liveMessagePortEntities);
//            } else {
//                mLiveMessagePager = new LiveMessagePager(activity, questionBll, baseLiveMediaControllerBottom,
// liveMessageLandEntities);
//            }
            if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
                LiveMessagePager liveMessagePager =
                        new LiveMessagePager(activity, null, baseLiveMediaControllerBottom, liveMessageLandEntities, liveMessagePortEntities);
                mLiveMessagePager = liveMessagePager;
            } else {
                long before = System.currentTimeMillis();
                if (!isSmallEnglish && !LiveVideoConfig.isSmallChinese) {
                    LiveMessagePager liveMessagePager = new LiveMessagePager(activity, null,
                            baseLiveMediaControllerBottom, liveMessageLandEntities, null);
                    mLiveMessagePager = liveMessagePager;
                } else if (LiveVideoConfig.isSmallChinese) {
                    SmallChineseLiveMessagePager chineseLiveMessagePager = new SmallChineseLiveMessagePager(activity,
                            this, null, baseLiveMediaControllerBottom
                            , liveMessageLandEntities, null);
                    mLiveMessagePager = chineseLiveMessagePager;
                } else {
                    SmallEnglishLiveMessagePager liveMessagePager = new SmallEnglishLiveMessagePager(activity, this,
                            null, baseLiveMediaControllerBottom, liveMessageLandEntities, null);
                    mLiveMessagePager = liveMessagePager;
                }
                logger.d("initView:time1=" + (System.currentTimeMillis() - before));
            }
        } else {
            mLiveMessagePager = new LiveMessagePortPager(activity, this, liveMessagePortEntities,
                    liveMessageLandEntities);
        }

        mLiveMessagePager.setGetInfo(getInfo);
        mLiveMessagePager.urlclick = urlclick;
        mLiveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setIrcState(mLiveBll);
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
        if (getInfo != null) {
            isSmallEnglish = getInfo.getSmallEnglish();
        }
        urlclick = getInfo.getUrlClick();
        if (mLiveMessagePager != null) {
            mLiveMessagePager.urlclick = urlclick;
            mLiveMessagePager.setGetInfo(getInfo);
        }

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
            mLiveMessagePager.onDestroy();
        }
        keyboardShowingListeners.clear();
    }

    @Override
    public void onUserList(String channel, User[] users) {
        for (User user : users) {
            if (!this.users.contains(user.getNick())) {
                this.users.add(user.getNick());
            }
        }
  /*      StringBuilder sb = new StringBuilder();
        for (User user : users){
            sb.append(user.getNick()+"__");
        }
        Loger.d("___join: userList: size "+users.length+"___content : "+sb.toString());*/
        peopleCount.set(users.length, new Exception());
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onUserList(channel, users);
        }
    }

    @Override
    public void onMessage(final String target, final String sender, final String login, final String hostname, final
    String text, final String headurl) {
        if (!"NOTICE".equals(target) && mLiveMessagePager.isCloseChat()) {//只看老师
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLiveMessagePager != null) {
                    mLiveMessagePager.onMessage(target, sender, login, hostname, text, headurl);
                }
            }
        });
    }

    public void onModeChange(final String mode, final boolean isPresent) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onModeChange(mode);
        }
    }

    @Override
    public void onPrivateMessage(boolean isSelf, final String sender, String login, String hostname, String target,
                                 final String
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
        //  Loger.d("____join:  "+sender+"___peoplecount:  "+peopleCount);
        if (!users.contains(sender)) {
            peopleCount.set(peopleCount.get() + 1, new Exception(sender));
            users.add(sender);
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onJoin(target, sender, login, hostname);
            }
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
//        if (sourceNick.startsWith(LiveBll.TEACHER_PREFIX) || sourceNick.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//            //老师不计算在内
//            return;
//        }
        if (users.contains(sourceNick)) {
            peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
            users.remove(sourceNick);
        }
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
    public void onOtherDisable(String id, String name, boolean disable) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onOtherDisable(id, name, disable);
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
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onOpenVoicebarrage(openbarrage, fromNotice);
        }
    }

    /**
     * 理科辅导老师开启关闭鲜花
     *
     * @param openFDbarrage
     * @param fromNotice
     */
    @Override
    public void onFDOpenbarrage(boolean openFDbarrage, boolean fromNotice) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onFDOpenbarrage(openFDbarrage, fromNotice);
        }

    }

    @Override
    public void videoStatus(String status) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.videoStatus(status);
        }
    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage, boolean isFDLKOpenbarrage) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onTeacherModeChange(oldMode, mode, isShowNoticeTips, iszjlkOpenbarrage,
                    isFDLKOpenbarrage);
        }
    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onOpenVoiceNotic(openVoice, type);
        }
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.setVideoLayout(liveVideoPoint);
        }
    }

    public void onGetMyGoldDataEvent(String goldNum) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onGetMyGoldDataEvent(goldNum);
        }
    }

    @Override
    public void showInput() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onQuestionHide();
        }
    }

    @Override
    public void hideInput() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onQuestionShow();
        }
    }

    public void addMessage(String sender, int type, String text) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.addMessage(sender, type, text, "");
        }
    }

    /**
     * 设置连对num
     *
     * @param nowEvenNum      当前连对数
     * @param highestRightNum 最高连对数
     */
    public void setEvenNum(String nowEvenNum, String highestRightNum) {
        if (mLiveMessagePager instanceof LiveMessagePager) {
            ((LiveMessagePager) mLiveMessagePager).setEvenText(nowEvenNum, highestRightNum);
        }
    }

    @Override
    public void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isShow) {
        isAnaswer = isShow ? 1 : 0;
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onQuestionShow(videoQuestionLiveEntity, isShow);
        }
    }

    @Override
    public void onKeyboardShowing(boolean isShowing) {
        for (KeyboardUtil.OnKeyboardShowingListener listener : keyboardShowingListeners) {
            listener.onKeyboardShowing(isShowing);
        }
    }

    @Override
    public void addKeyboardShowing(KeyboardUtil.OnKeyboardShowingListener listener) {
        keyboardShowingListeners.add(listener);
    }

    @Override
    public void removeKeyboardShowing(KeyboardUtil.OnKeyboardShowingListener listener) {
        keyboardShowingListeners.remove(listener);
    }

    /**
     * @param nicker joiner's nick
     * @return 是否已经加入房间
     */
    private boolean contains(String nicker) {
        StringBuilder sb = new StringBuilder();
        for (String user : users) {
            sb.append(user);
        }
        // Loger.d("___bug 44 : users:  "+sb.toString()+"____nicker:  "+nicker);
        for (String user : users) {
            if (user.equals(nicker)) {
                return true;
            }
        }
        return false;
    }
}

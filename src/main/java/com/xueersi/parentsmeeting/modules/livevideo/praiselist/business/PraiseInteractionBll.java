package com.xueersi.parentsmeeting.modules.livevideo.praiselist.business;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PraiseMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.ArtsPraiseHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.PraiseInteractionPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 初高中理科点赞互动
 */

public class PraiseInteractionBll extends LiveBaseBll implements NoticeAction, TopicAction, MessageAction {

    private LiveBll2 mLiveBll;
    private RelativeLayout rlPraiseContentView;
    private PraiseInteractionPager praiseInteractionPager;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo mRoomInitData;
    private ArtsPraiseHttpResponseParser mParser;

    //同班同学特效礼物
    private Stack<PraiseMessageEntity> otherSpecialGiftStack = new Stack<>();
    //同班同学点赞
    private Stack<PraiseMessageEntity> otherPraiseStack = new Stack<>();
    //我送出的礼物特效
    private Stack<PraiseMessageEntity> mySpecialGiftStack = new Stack<>();
    //班级点赞的数量
    private Stack<PraiseMessageEntity> classPraiseStack = new Stack<>();

    //是否开启点赞
    private boolean isOpen = false;
    //主讲或者辅导
    private String from;

    private Timer timer;
    private int goldNum;


    public PraiseInteractionBll(Context context, LiveBll2 liveBll) {
        super((Activity) context, liveBll);
        logger.d("PraiseInteractionBll construct");
        mLiveBll = liveBll;

    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        EventBus.getDefault().register(this);

    }

    public void attachToRootView() {
        rlPraiseContentView = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRootView.addView(rlPraiseContentView, params);
    }

    private class SpecailGiftTimerTask extends TimerTask {

        @Override
        public void run() {

            PraiseMessageEntity praiseMessageEntity = null;
            if (!mySpecialGiftStack.isEmpty()) {
                praiseMessageEntity = mySpecialGiftStack.pop();
            } else if (!otherSpecialGiftStack.isEmpty()) {
                praiseMessageEntity = otherSpecialGiftStack.pop();
            }

            if (!otherPraiseStack.isEmpty()) {
                praiseMessageEntity = otherPraiseStack.pop();
            }
            if (praiseMessageEntity != null && praiseInteractionPager != null) {
                logger.d("specail gift size=" + classPraiseStack.size() + ",message=" + praiseMessageEntity
                        .getMessageContent());
                praiseInteractionPager.appendBarraige(praiseMessageEntity);
            }
        }
    }

    private class ClassPraiseTimerTask extends TimerTask {

        @Override
        public void run() {
            if (!classPraiseStack.isEmpty()) {
                PraiseMessageEntity praiseMessageEntity = classPraiseStack.pop();
                logger.d("class praise size=" + classPraiseStack.size() + ",message=" + praiseMessageEntity
                        .getMessageContent());
                praiseInteractionPager.appendBarraige(praiseMessageEntity);
            }
        }
    }


    /**
     * 显示点赞互动
     */
    private void openPraise() {
        if (!isOpen) {
            LiveMessageBll liveMessageBll = ProxUtil.getProxUtil().get(activity, LiveMessageBll.class);
            if (liveMessageBll != null) {
                String type = "主讲";
                if ("f".equals(from)) {
                    type = "辅导";
                }
                liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                        type + "老师开启了点赞功能!");
            }
            isOpen = true;
            praiseInteractionPager = new PraiseInteractionPager(mContext, goldNum, this, mLiveBll);
            rlPraiseContentView.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int rightMargin = getRightMargin();
            params.rightMargin = rightMargin;
            rlPraiseContentView.addView(praiseInteractionPager.getRootView(), params);

            praiseInteractionPager.startEnterStarAnimation();

            timer = new Timer(true);
            timer.schedule(new SpecailGiftTimerTask(), 5000, 5000);
            timer.schedule(new ClassPraiseTimerTask(), 10000, 10000);
        }
    }

    public void sendGiftDeductGold(int type, HttpCallBack httpCallBack) {
        logger.d("type=" + type);
        String liveId = mRoomInitData.getId();
        String courseId = mRoomInitData.getStudentLiveInfo().getCourseId();
        String teacherId = mRoomInitData.getMainTeacherId();
        if ("f".equals(from)) {
            teacherId = mRoomInitData.getTeacherId();
        }
        mLiveBll.getHttpManager().praiseSendGift(liveId, mRoomInitData.getStuId(),
                mRoomInitData.getStuCouId(), type, teacherId, httpCallBack);
    }

    /**
     * {"ltype":1, "name":"student_name","id":"12453", "value": 123,"type":"266", "to":"t/f"}
     */
    public void sendPrivateMessage(int messageType, int value) {
        try {
            logger.d("messageType=" + messageType + ",value=" + value);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ltype", messageType);
            jsonObject.put("name", mRoomInitData.getStuName());
            jsonObject.put("id", mRoomInitData.getStuId());
            jsonObject.put("value", value);
            jsonObject.put("type", XESCODE.PRAISE_MESSAGE);
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mRoomInitData.getStudentLiveInfo();
            if (studentLiveInfo != null) {
                jsonObject.put("classid", studentLiveInfo.getClassId());
            }
            jsonObject.put("to", from);
            sendMsg(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void insetOtherSpecialGift(PraiseMessageEntity praiseMessageEntity) {
        otherSpecialGiftStack.push(praiseMessageEntity);
    }

    public void insetOtherPraise(PraiseMessageEntity praiseMessageEntity) {
        otherPraiseStack.push(praiseMessageEntity);
    }

    public void insertMySpecialGift(int giftType) {
        PraiseMessageEntity praiseMessageEntity = new PraiseMessageEntity();
        praiseMessageEntity.setMessageType(PraiseMessageEntity.TYPE_SPECIAL_GIFT);
        praiseMessageEntity.setGiftType(giftType);
        String messageContent = "";
        if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_PHYSICAL) {
            messageContent = mRoomInitData.getStuName() + "同学给老师点亮了星空!";
        } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
            messageContent = mRoomInitData.getStuName() + "同学送老师一瓶魔法水!";

        } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_MATH) {
            messageContent = mRoomInitData.getStuName() + "同学为老师放飞了气球!";
        }
        praiseMessageEntity.setMessageContent(messageContent);
        mySpecialGiftStack.push(praiseMessageEntity);
    }

    /**
     * 我的点赞
     */
    public void pushMyPraise(int praiseNumAmount) {
        PraiseMessageEntity praiseMessageEntity = new PraiseMessageEntity();
        praiseMessageEntity.setMessageType(PraiseMessageEntity.TYPE_PRAISE);
        praiseMessageEntity.setMessageContent(mRoomInitData.getStuName() + ":点了" + praiseNumAmount + "个赞!");
        praiseInteractionPager.appendBarraige(praiseMessageEntity);
        sendPrivateMessage(PraiseMessageEntity.TYPE_PRAISE, praiseNumAmount);

    }

    /**
     * 获取同班同学的特效礼物
     */
    public Stack<PraiseMessageEntity> getOtherSpecialGift() {

        return otherSpecialGiftStack;
    }

    /**
     * 获取同班同学的点赞
     */
    public Stack<PraiseMessageEntity> getOtherPraiseStack() {

        return otherPraiseStack;
    }

    /**
     * 我送出的礼物特效
     */
    public Stack<PraiseMessageEntity> getMySpecialGift() {

        return mySpecialGiftStack;
    }

    /**
     * 获取班级点赞的数量
     */
    public Stack<PraiseMessageEntity> getClassPraise() {

        return classPraiseStack;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
        if (!TextUtils.isEmpty(event.goldNum)) {
            goldNum = Integer.valueOf(event.goldNum);
            if (praiseInteractionPager != null) {
                praiseInteractionPager.setGoldNum(goldNum);
            }
        }

    }


    private void closePraise() {
        if (isOpen == true) {
            LiveMessageBll liveMessageBll = ProxUtil.getProxUtil().get(activity, LiveMessageBll.class);
            if (liveMessageBll != null) {
                String type = "主讲";
                if ("f".equals(from)) {
                    type = "辅导";
                }
                liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                        type + "老师关闭了点赞功能!");
            }
            isOpen = false;
            if (timer != null) {
                timer.cancel();
            }
            closePager();
        }
    }


    private int getRightMargin() {
        return LiveVideoPoint.getInstance().getRightMargin();
    }


    @Override
    public void onStop() {
        if (praiseInteractionPager != null) {
            praiseInteractionPager.onStop();
        }
    }

    @Override
    public void onResume() {
        if (praiseInteractionPager != null) {
            praiseInteractionPager.onResume();
        }
    }

    @Override
    public void onDestory() {
        if (praiseInteractionPager != null) {
            praiseInteractionPager.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }


    public void closePager() {
        if (rlPraiseContentView != null) {
            praiseInteractionPager.closePraise();
            rlPraiseContentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rlPraiseContentView.removeAllViews();
                }
            }, 1000);
        }
    }


    /**
     * notice 指令集
     */
    private int[] noticeCodes = {
            XESCODE.PRAISE_SWITCH,
    };

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        logger.d("onLiveInited");
        if (getInfo != null) {
            mHttpManager = getHttpManager();
            mRoomInitData = getInfo;
            attachToRootView();
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {

    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.PRAISE_SWITCH:
                logger.d("data=" + data);
                from = data.optString("from");
                final boolean open = data.optBoolean("open");
                rlPraiseContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (open) {
                            openPraise();
                        } else {
                            closePraise();
                        }
                    }
                });

                break;
            default:
                break;
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("onTopic message=" + jsonObject);
        LiveTopic.RoomStatusEntity mainRoomstatus = null;
        if (LiveTopic.MODE_CLASS.equals(liveTopic.getMode()) || "t".equals(from)) {
            from = "t";
            mainRoomstatus = liveTopic.getMainRoomstatus();
        } else {
            mainRoomstatus = liveTopic.getCoachRoomstatus();
            from = "f";
        }
        if (mainRoomstatus != null) {
            final boolean openlike = mainRoomstatus.isOpenlike();
            rlPraiseContentView.post(new Runnable() {
                @Override
                public void run() {
                    if (openlike) {
                        openPraise();
                    } else {
                        closePraise();
                    }
                }
            });

        }

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
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String
            message) {
        try {
            logger.d("message=" + message);
            JSONObject jsonObject = new JSONObject(message);
            int type = jsonObject.optInt("type");
            if (type == XESCODE.PRAISE_MESSAGE) {
                PraiseMessageEntity praiseMessageEntity = new PraiseMessageEntity();
                praiseMessageEntity.setMessageType(jsonObject.optInt("ltype"));
                praiseMessageEntity.setUserName(jsonObject.optString("name"));
                praiseMessageEntity.setUserId(jsonObject.optString("id"));
                praiseMessageEntity.setFrom(jsonObject.optString("to"));
                if (praiseMessageEntity.getMessageType() == PraiseMessageEntity.TYPE_SPECIAL_GIFT) {
                    praiseMessageEntity.setGiftType(jsonObject.optInt("value"));
                    int giftType = praiseMessageEntity.getGiftType();
                    String messageContent = "";
                    if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_PHYSICAL) {
                        messageContent = praiseMessageEntity.getUserName() + "同学给老师点亮了星空!";
                    } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
                        messageContent = praiseMessageEntity.getUserName() + "同学送老师一瓶魔法水!";

                    } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_MATH) {
                        messageContent = praiseMessageEntity.getUserName() + "同学为老师放飞了气球!";
                    }
                    praiseMessageEntity.setMessageContent(messageContent);
                    otherSpecialGiftStack.push(praiseMessageEntity);
                } else if (praiseMessageEntity.getMessageType() == PraiseMessageEntity.TYPE_PRAISE) {
                    praiseMessageEntity.setPraiseNum(jsonObject.optLong("value"));
                    if (otherPraiseStack.contains(praiseMessageEntity)) {
                        otherPraiseStack.remove(praiseMessageEntity);
                    }
                    praiseMessageEntity.setMessageContent(praiseMessageEntity.getUserName() + ": 点了" +
                            praiseMessageEntity.getPraiseNum() + "个赞!");
                    otherPraiseStack.push(praiseMessageEntity);
                }
            } else if (type == XESCODE.PRAISE_CLASS_NUM) {
                //{"from":"t/f","type":"267","value":[{"likeNum":2000,"name":"新平台数学啊"},{"likeNum":2000,"name":"教师聊天室"}]}
                JSONArray jsonArray = jsonObject.optJSONArray("value");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonNumObject = jsonArray.getJSONObject(i);
                    PraiseMessageEntity praiseMessageEntity = new PraiseMessageEntity();
                    praiseMessageEntity.setMessageType(PraiseMessageEntity.TYPE_CLASS);

                    praiseMessageEntity.setUserName(jsonNumObject.optString("name"));
                    praiseMessageEntity.setPraiseNum(jsonNumObject.optLong("likeNum"));
                    praiseMessageEntity.setFrom(jsonObject.optString("from"));
                    praiseMessageEntity.setMessageContent(praiseMessageEntity.getUserName() + "班共点了" +
                            praiseMessageEntity.getPraiseNum() + "个赞");
                    classPraiseStack.push(praiseMessageEntity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }
}

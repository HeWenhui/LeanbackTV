package com.xueersi.parentsmeeting.modules.livevideo.praiselist.business;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
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
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageSend;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.PraiseInteractionPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ArtsPraiseHttpResponseParser mParser;

    //同班同学特效礼物
    private Stack<PraiseMessageEntity> otherSpecialGiftStack = new Stack<>();
    //同班同学点赞
    private Stack<PraiseMessageEntity> otherPraiseStack = new Stack<>();
    //我送出的礼物特效
    private Stack<PraiseMessageEntity> mySpecialGiftStack = new Stack<>();


    //是否开启点赞
    private boolean isOpen = false;

    private Timer timer;
    private int goldNum;



    //统计埋点
    private Map<String, String> userLogMap = new HashMap<String, String>();


    public PraiseInteractionBll(Activity activity, LiveBll2 liveBll) {
        super(activity, liveBll);
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
        params.leftMargin = LiveVideoPoint.getInstance().x2;
        addView(rlPraiseContentView, 0, params);
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
            if (praiseMessageEntity != null && praiseInteractionPager != null) {
                praiseInteractionPager.appendBarraige(praiseMessageEntity);
            }

            if (!otherPraiseStack.isEmpty()) {
                PraiseMessageEntity praiseMessageOtherEntity = otherPraiseStack.pop();
                if (praiseMessageOtherEntity != null && praiseInteractionPager != null) {
                    praiseInteractionPager.appendBarraige(praiseMessageOtherEntity);
                }
            }

        }
    }


    /**
     * 显示点赞互动
     */
    private void openPraise() {
        if (!isOpen) {
            userLogMap.clear();
            userLogMap.put("openPraise", "goldnum=" + goldNum);

            removeCallbacks(delayRemoveRunalbe);
            isOpen = true;
            praiseInteractionPager = new PraiseInteractionPager(mContext, goldNum, this, contextLiveAndBackDebug,mGetInfo);
            rlPraiseContentView.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int rightMargin = getRightMargin();
            params.rightMargin = rightMargin;
            rlPraiseContentView.addView(praiseInteractionPager.getRootView(), params);

            praiseInteractionPager.openPraise();

            timer = new Timer(true);
            timer.schedule(new SpecailGiftTimerTask(), 5000, 5000);
        }
    }

    public void sendGiftDeductGold(int type, HttpCallBack httpCallBack) {
        logger.d("type=" + type);
        String liveId = mGetInfo.getId();
        String courseId = mGetInfo.getStudentLiveInfo().getCourseId();
        String teacherId = mGetInfo.getMainTeacherId();
        if (LiveTopic.MODE_TRANING.equals(mGetInfo.getMode())) {
            teacherId = mGetInfo.getTeacherId();
        }
        mLiveBll.getHttpManager().praiseSendGift(liveId, mGetInfo.getStuId(),
                mGetInfo.getStuCouId(), type, teacherId, httpCallBack);
    }

    /**
     * {"ltype":1, "name":"student_name","id":"12453", "value": 123,"type":"266", "to":"t/f"}
     */
    public void sendPrivateMessage(int messageType, int value) {
        try {
            logger.d("messageType=" + messageType + ",value=" + value);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ltype", messageType);
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("value", value);
            jsonObject.put("type", String.valueOf(XESCODE.PRAISE_MESSAGE));
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
            if (studentLiveInfo != null) {
                jsonObject.put("classid", studentLiveInfo.getClassId());
            }
            String to = "t";
            if (LiveTopic.MODE_TRANING.equals(mGetInfo.getMode())) {
                to = "f";
            }
            jsonObject.put("to", to);
            sendMsg(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 我自己送出的特效礼物
     *
     * @param giftType
     */
    public void insertMySpecialGift(int giftType) {
        PraiseMessageEntity praiseMessageEntity = new PraiseMessageEntity();
        praiseMessageEntity.setMessageType(PraiseMessageEntity.TYPE_SPECIAL_GIFT);
        praiseMessageEntity.setGiftType(giftType);
        praiseMessageEntity.setSortKey(PraiseMessageEntity.SORT_KEY_MY_GIFT);
        String messageContent = "";
        if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_PHYSICAL) {
            messageContent = mGetInfo.getStuName() + "同学给老师点亮了星空";
        } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
            messageContent = mGetInfo.getStuName() + "同学送老师一瓶魔法水";

        } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_MATH) {
            messageContent = mGetInfo.getStuName() + "同学为老师放飞了气球";
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
        praiseMessageEntity.setSortKey(PraiseMessageEntity.SORT_KEY_MY_PRAISE);
        praiseMessageEntity.setUserName(mGetInfo.getStuName());
        praiseMessageEntity.setMessageContent("点了" + praiseNumAmount + "个赞");
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
        if (!TextUtils.isEmpty(event.goldNum)) {
            userLogMap.put("reciveGoldNum", "goldnum=" + goldNum);
            goldNum = Integer.valueOf(event.goldNum);
            if (praiseInteractionPager != null) {
                praiseInteractionPager.setGoldNum(goldNum);
            }
        }

    }


    private void closePraise() {
        if (isOpen == true) {
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), this.getClass().getSimpleName(),
                    userLogMap);
            isOpen = false;
            otherSpecialGiftStack.clear();
            otherPraiseStack.clear();
            mySpecialGiftStack.clear();
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
    public void onDestroy() {
        if (praiseInteractionPager != null) {
            praiseInteractionPager.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }

    private Runnable delayRemoveRunalbe = new Runnable() {
        @Override
        public void run() {
            rlPraiseContentView.removeAllViews();
        }
    };

    public void closePager() {
        if (rlPraiseContentView != null) {
            praiseInteractionPager.closePraise();
            postDelayed(delayRemoveRunalbe, 1000);
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
        mHttpManager = getHttpManager();
        attachToRootView();
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        logger.d("onModeChange oldMode=" + oldMode + ",mode=" + mode);
        rlPraiseContentView.post(new Runnable() {
            @Override
            public void run() {
                closePraise();
            }
        });
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.d("onNotice data=" + data + ",mode=" + mGetInfo.getMode());

        switch (type) {
            case XESCODE.PRAISE_SWITCH:
                String from = data.optString("from");
                final boolean open = data.optBoolean("open");
                if (isFilterMessage(from)) {
                    return;
                }
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

                LiveMessageSend liveMessageSend = ProxUtil.getProxUtil().get(activity, LiveMessageSend.class);
                if (liveMessageSend != null) {
                    String teacherType = "主讲";
                    if ("f".equals(from)) {
                        teacherType = "辅导";
                    }
                    String status = "关闭";
                    if (open) {
                        status = "开启";
                    }
                    String message = teacherType + "老师" + status + "了点赞功能";
                    liveMessageSend.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                            message);

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("onTopic message=" + jsonObject + ",mode=" + mGetInfo.getMode());
        //如果是切流，原来模式是主讲，需要主动关闭点赞功能
        LiveTopic.RoomStatusEntity mainRoomstatus = null;
        if (LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
            mainRoomstatus = liveTopic.getMainRoomstatus();
        } else {
            mainRoomstatus = liveTopic.getCoachRoomstatus();
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

    private boolean isFilterMessage(String from) {
        if (LiveTopic.MODE_TRANING.equals(mGetInfo.getMode()) && "t".equals(from)) {
            return true;
        }
        if (LiveTopic.MODE_CLASS.equals(mGetInfo.getMode()) && "f".equals(from)) {
            return true;
        }
        return false;
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
                        messageContent = praiseMessageEntity.getUserName() + "同学给老师点亮了星空";

                    } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
                        messageContent = praiseMessageEntity.getUserName() + "同学送老师一瓶魔法水";

                    } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_MATH) {
                        messageContent = praiseMessageEntity.getUserName() + "同学为老师放飞了气球";
                    }
                    praiseMessageEntity.setMessageContent(messageContent);
                    praiseMessageEntity.setSortKey(PraiseMessageEntity.SORT_KEY_OTHER_GIFT);
                    otherSpecialGiftStack.push(praiseMessageEntity);
                } else if (praiseMessageEntity.getMessageType() == PraiseMessageEntity.TYPE_PRAISE) {
                    if (otherPraiseStack.size() > 10) {
                        otherPraiseStack.remove(0);
                    }
                    praiseMessageEntity.setPraiseNum(jsonObject.optLong("value"));
                    if (otherPraiseStack.contains(praiseMessageEntity)) {
                        otherPraiseStack.remove(praiseMessageEntity);
                    }
                    praiseMessageEntity.setMessageContent("点了" +
                            praiseMessageEntity.getPraiseNum() + "个赞");
                    praiseMessageEntity.setSortKey(PraiseMessageEntity.SORT_KEY_STUDENT_PRAISE);
                    otherPraiseStack.push(praiseMessageEntity);

                }
            } else if (type == XESCODE.PRAISE_CLASS_NUM) {
                //{"from":"t/f","type":"267","value":[{"likeNum":2000,"name":"新平台数学啊"},{"likeNum":2000,"name":"教师聊天室"}]}
                JSONArray jsonArray = jsonObject.optJSONArray("value");
                //班级点赞的数量
                List<PraiseMessageEntity> classPraiseStack = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonNumObject = jsonArray.getJSONObject(i);
                    PraiseMessageEntity praiseMessageEntity = new PraiseMessageEntity();
                    praiseMessageEntity.setMessageType(PraiseMessageEntity.TYPE_CLASS);
                    praiseMessageEntity.setSortKey(PraiseMessageEntity.SORT_KEY_CLASS_PRAISE);
                    praiseMessageEntity.setUserName(jsonNumObject.optString("name"));
                    praiseMessageEntity.setPraiseNum(jsonNumObject.optLong("likeNum"));
                    praiseMessageEntity.setFrom(jsonObject.optString("from"));
                    praiseMessageEntity.setMessageContent(praiseMessageEntity.getUserName() + "班共点了" +
                            praiseMessageEntity.getPraiseNum() + "个赞");
                    classPraiseStack.add(praiseMessageEntity);
                }
                if (praiseInteractionPager != null && !classPraiseStack.isEmpty()) {
                    praiseInteractionPager.appendBarraiges(classPraiseStack);
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

package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.drawable.DrawableHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.FlowerItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.UserGoldTotal;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.widget.button.CompoundButtonGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * Created by linyuqiang on 2016/8/2.
 * 直播聊天横屏-直播讲座
 */
public class LiveMessageLandPager extends BaseLiveMessagePager {
    private String TAG = "LiveMessageLandPager";
    /** 聊天，默认开启 */
    private Button btMesOpen;
    /** 献花，默认关闭 */
    private Button btMessageFlowers;
    /** 聊天，默认打开 */
    private CheckBox cbMessageClock;
    /** 聊天消息 */
    private ListView lvMessage;
    private View rlInfo;
    private View rlMessageContent;
    private Button btMessageSend;
    private Button btMessageExpress;
    private CommonAdapter<LiveMessageEntity> messageAdapter;
    private CommonAdapter<LiveMessageEntity> otherMessageAdapter;
    private boolean isTouch = false;
    /** 聊天字体大小，最多13个汉字 */
    private int messageSize = 0;
    private PopupWindow mFlowerWindow;
    private View flowerContentView;
    private TextView tvMessageGoldLable;
    private TextView tvMessageGold;
    private String goldNum;
    /** 上次发送消息时间 */
    private long lastSendMsg;
    private BaseLiveMediaControllerBottom lectureMediaControllerBottom;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    private ImageView ivExpressionCancle;
    /** 横屏的时候，也添加竖屏的消息 */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;

    public LiveMessageLandPager(Context context, BaseLiveMediaControllerBottom
            lectureMediaControllerBottom,
                                ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context);
        this.lectureMediaControllerBottom = lectureMediaControllerBottom;
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;
        nameColors[2] = Color.WHITE;
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_message_land, null);
        lvMessage = (ListView) mView.findViewById(R.id.lv_livevideo_message);
        lvMessage.setTag(R.id.cb_livevideo_message_clock, 0);
        lvMessage.setTag(0);
        rlInfo = mView.findViewById(R.id.rl_livevideo_info);
        rlMessageContent = mView.findViewById(R.id.rl_livevideo_message_content2);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_message_send);
        switchFSPanelLinearLayout = (KPSwitchFSPanelLinearLayout) mView.findViewById(R.id
                .rl_livevideo_message_panelroot);
        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
        return mView;
    }

    @Override
    public void initListener() {
        btMesOpen = lectureMediaControllerBottom.getBtMesOpen();
        btMessageFlowers = lectureMediaControllerBottom.getBtMessageFlowers();
        cbMessageClock = lectureMediaControllerBottom.getCbMessageClock();
        btMesOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlMessageContent.setVisibility(View.VISIBLE);
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
            }
        });
        etMessageContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent
                        .KEYCODE_ENTER)) {
                    btMessageSend.performClick();
                    return true;
                }
                return false;
            }
        });
        btMessageFlowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goldNum == null) {
                    OtherModulesEnter.requestGoldTotal(mContext);
                }
                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                    if (!ircState.isOpenbarrage()) {
                        XESToastUtils.showToast(mContext, "老师未开启献花");
                        return;
                    }
                } else {
                    XESToastUtils.showToast(mContext, "辅导模式不能献花");
                    return;
                }
                mFlowerWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                isHaveFlowers = true;
            }
        });
        btMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.i("onClick:time=" + (System.currentTimeMillis() - lastSendMsg));
                Editable editable = etMessageContent.getText();
                String msg = editable.toString();
                if (!StringUtils.isSpace(msg)) {
                    if (getInfo != null && getInfo.getBlockChinese() && isChinese(msg)) {
                        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_CHINESE, "");
                        onTitleShow(true);
                        return;
                    }
                    if (ircState.openchat()) {
                        if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                            boolean send = ircState.sendMessage(msg, "");
                            if (send) {
                                etMessageContent.setText("");
                                addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, "");
                                lastSendMsg = System.currentTimeMillis();
                                onTitleShow(true);
                            } else {
                                XESToastUtils.showToast(mContext, "你已被禁言!");
                            }
                        } else {
                            //暂时去掉3秒发言，信息提示
//                                addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "3秒后才能再次发言，要认真听课哦!");
                            XESToastUtils.showToast(mContext, ((SEND_MSG_INTERVAL - System.currentTimeMillis() + lastSendMsg) / 1000) + "秒后才能再次发言，要认真听课哦!");
                        }
                    } else {
                        XESToastUtils.showToast(mContext, "老师未开启聊天");
                    }
                } else {
                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_EMPTY, "");
                }
            }
        });
        lvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isTouch = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent
                        .ACTION_CANCEL) {
                    isTouch = false;
                }
                return false;
            }
        });
        KeyboardUtil.attach((Activity) mContext, switchFSPanelLinearLayout, new KeyboardUtil
                .OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                logger.i("onKeyboardShowing:isShowing=" + isShowing);
                if (!isShowing && switchFSPanelLinearLayout.getVisibility() == View.GONE) {
                    onTitleShow(true);
                }
                keyboardShowing = isShowing;
                if (keyboardShowing) {
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                }
            }
        });
        KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, btMessageExpress, etMessageContent,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        if (switchToPanel) {
                            btMessageExpress.setBackgroundResource(R.drawable.im_input_jianpan_icon_normal);
                            etMessageContent.clearFocus();
                        } else {
                            btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                            etMessageContent.requestFocus();
                        }
                    }
                });
        cbMessageClock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lvMessage.setTag(1);
                    lvMessage.setVisibility(View.GONE);
                } else {
                    int show = (int) lvMessage.getTag(R.id.cb_livevideo_message_clock);
                    if (show == 0) {
                        lvMessage.setVisibility(View.VISIBLE);
                    } else {
                        lvMessage.setVisibility(View.GONE);
                    }
                    lvMessage.setTag(0);
                }
            }
        });
    }

    int c = 0;

    @Override
    public void initData() {
        super.initData();
        if (getInfoGoldNum == 0) {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    UserGoldTotal.requestGoldTotal(mContext);
                }
            });
        } else {
            goldNum = "" + getInfoGoldNum;
        }
        btMessageFlowers.setTag("0");
        btMessageFlowers.setAlpha(0.4f);
        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
        ivExpressionCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                etMessageContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
            }
        });
        showExpressionView(true);
        int screenWidth = ScreenUtils.getScreenWidth();
        messageSize = (int) (ScreenUtils.getScreenDensity() * 16);
        ViewGroup.LayoutParams lp = rlInfo.getLayoutParams();
        lp.width = screenWidth / 2;
//        rlInfo.setLayoutParams(lp);
        LayoutParamsUtil.setViewLayoutParams(rlInfo, lp);
        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities) {
            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                return new AdapterItemInterface<LiveMessageEntity>() {
                    TextView tvMessageItem;

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_livevideo_message_land;
                    }

                    @Override
                    public void initViews(View root) {
                        tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
                    }

                    @Override
                    public void bindListener() {

                    }

                    @Override
                    public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
                        String sender = entity.getSender();
                        SpannableString spanttt = new SpannableString(sender + ": ");
                        int color;
                        switch (entity.getType()) {
                            case LiveMessageEntity.MESSAGE_MINE:
                            case LiveMessageEntity.MESSAGE_TEACHER:
                            case LiveMessageEntity.MESSAGE_TIP:
                            case LiveMessageEntity.MESSAGE_CLASS:
                                color = nameColors[entity.getType()];
                                break;
                            default:
                                color = nameColors[0];
                                break;
                        }
                        CharacterStyle characterStyle = new ForegroundColorSpan(color);
                        spanttt.setSpan(characterStyle, 0, sender.length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (urlclick == 1 && LiveMessageEntity.MESSAGE_TEACHER == entity.getType()) {
                            tvMessageItem.setAutoLinkMask(Linkify.WEB_URLS);
                            tvMessageItem.setText(entity.getText());
                            urlClick(tvMessageItem);
                            CharSequence text = tvMessageItem.getText();
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(text);
                        } else {
                            tvMessageItem.setAutoLinkMask(0);
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(entity.getText());
                        }
                    }
                };
            }
        };
        lvMessage.setAdapter(messageAdapter);
        initDanmaku();
        ArrayList<FlowerEntity> flowerEntities = new ArrayList<>();
        flowerEntities.add(new FlowerEntity(FLOWERS_SMALL, flowsDrawTips[0], "1支玫瑰", 10));
        flowerEntities.add(new FlowerEntity(FLOWERS_MIDDLE, flowsDrawTips[1], "1束玫瑰", 50));
        flowerEntities.add(new FlowerEntity(FLOWERS_BIG, flowsDrawTips[2], "1束蓝色妖姬", 100));
        mFlowerWindow = new PopupWindow(mContext);
        mFlowerWindow.setBackgroundDrawable(DrawableHelper.bitmap2drawable(null));
        mFlowerWindow.setOutsideTouchable(true);
        mFlowerWindow.setFocusable(true);
        flowerContentView = View.inflate(mContext, R.layout.pop_livevideo_message_flower, null);
        mFlowerWindow.setContentView(flowerContentView);
        mFlowerWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isHaveFlowers = false;
            }
        });
        tvMessageGold = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold);
        tvMessageGoldLable = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold_lable);
        LinearLayout llMessageFlower = (LinearLayout) flowerContentView.findViewById(R.id.ll_livevideo_message_flower);
        LayoutInflater factory = LayoutInflater.from(mContext);
        CompoundButtonGroup group = new CompoundButtonGroup();
        for (int i = 0; i < flowerEntities.size(); i++) {
            final FlowerEntity entity = flowerEntities.get(i);
            FlowerItem flowerItem = new FlowerItem(mContext);
            View root = factory.inflate(flowerItem.getLayoutResId(), llMessageFlower, false);
            flowerItem.initViews(root);
            flowerItem.updateViews(flowerEntities.get(i), i, null);
            llMessageFlower.addView(root);
            group.addCheckBox((CheckBox) root.findViewById(R.id.ck_livevideo_message_flower), new CompoundButton
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        flowerContentView.setTag(entity);
                    } else {
                        FlowerEntity entity2 = (FlowerEntity) flowerContentView.getTag();
                        if (entity == entity2) {
                            flowerContentView.setTag(null);
                        }
                    }
                }
            });
        }
        flowerContentView.findViewById(R.id.bt_livevideo_message_flowersend).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionStatic questionStatic = ProxUtil.getProxUtil().get(mContext, QuestionStatic.class);
                if (questionStatic != null && questionStatic.isAnaswer()) {
                    XESToastUtils.showToast(mContext, "正在答题，不能献花");
                    return;
                }
                final FlowerEntity entity = (FlowerEntity) flowerContentView.getTag();
                if (entity != null) {
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        if (ircState.isOpenbarrage()) {
                            ircState.praiseTeacher("", entity.getFtype() + "", "", new HttpCallBack(false) {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) {
                                    if (goldNum == null) {
                                        OtherModulesEnter.requestGoldTotal(mContext);
                                    } else {
                                        if (responseEntity.getJsonObject() instanceof JSONObject) {
                                            try {
                                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                                int gold = Integer.parseInt(goldNum);
                                                goldNum = "" + (gold - jsonObject.getInt("gold"));
                                                onGetMyGoldDataEvent(goldNum);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    addDanmaKuFlowers(entity.getFtype(), getInfo.getStuName());
                                    mView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mFlowerWindow.dismiss();
                                        }
                                    }, 1000);
                                }

                                @Override
                                public void onPmFailure(Throwable error, String msg) {
                                    mFlowerWindow.dismiss();
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    mFlowerWindow.dismiss();
                                }
                            });
//                        liveBll.sendFlowerMessage(entity.getFtype());
                        } else {
                            XESToastUtils.showToast(mContext, "老师未开启献花");
                        }
                    } else {
                        XESToastUtils.showToast(mContext, "辅导模式不能献花");
                    }
                } else {
                    XESToastUtils.showToast(mContext, "请选择一束花");
                }
            }
        });
        mFlowerWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mFlowerWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onTitleShow(boolean show) {
        if (rlMessageContent.getVisibility() != View.GONE) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
            rlMessageContent.setVisibility(View.GONE);
        }
        switchFSPanelLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void closeChat(final boolean close) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (close) {
                    lvMessage.setVisibility(View.GONE);
                    cbMessageClock.setChecked(true);
                } else {
                    lvMessage.setVisibility(View.VISIBLE);
                    cbMessageClock.setChecked(false);
                }
            }
        });
    }

    @Override
    public boolean isCloseChat() {
        return cbMessageClock.isChecked();
    }

    /** 聊天开始连接 */
    @Override
    public void onStartConnect() {
    }

    @Override
    public void setIsRegister(boolean isRegister) {
        super.setIsRegister(isRegister);
    }

    @Override
    public void setHaveFlowers(boolean haveFlowers) {
        super.setHaveFlowers(haveFlowers);
        if (haveFlowers) {
            mFlowerWindow.showAtLocation(btMessageFlowers, Gravity.BOTTOM, 0, 0);
        } else {
            mFlowerWindow.dismiss();
        }
    }

    /** 聊天连上 */
    @Override
    public void onConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
            }
        });
    }

    /** 聊天进入房间 */
    @Override
    public void onRegister() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = true;
            }
        });
    }

    /** 聊天断开 */
    @Override
    public void onDisconnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = false;
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, DISCONNECT, "");
            }
        });
    }

    @Override
    public void onUserList(String channel, final User[] users) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        if (sender.startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
            sender = "主讲老师";
        } else if (sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
            sender = "辅导老师";
        }
        addMessage(sender, LiveMessageEntity.MESSAGE_TEACHER, text, headurl);
    }

    @Override
    public void onPrivateMessage(boolean isSelf, final String sender, String login, String hostname, String target,
                                 final String message) {
        if (isCloseChat()) {
            return;
        }
        mView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    int type = jsonObject.getInt("type");
                    if (type == XESCODE.TEACHER_MESSAGE) {
                        addMessage(jsonObject.getString("name"), LiveMessageEntity.MESSAGE_CLASS, jsonObject
                                .getString("msg"), "");
                    } else if (type == XESCODE.FLOWERS) {
                        //{"ftype":2,"name":"林玉强","type":"110"}
                        addDanmaKuFlowers(jsonObject.getInt("ftype"), jsonObject.getString("name"));
                    }
                } catch (JSONException e) {
                    addMessage(sender, LiveMessageEntity.MESSAGE_CLASS, message, "");
                }
            }
        });
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

    /** 被禁言 */
    @Override
    public void onDisable(final boolean disable, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "你被老师禁言了");
                    }
                    btMesOpen.setAlpha(0.4f);
                    btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                } else {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "老师解除了你的禁言");
                    }
                    if (ircState.openchat()) {
                        btMesOpen.setAlpha(1.0f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    } else {
                        btMesOpen.setAlpha(0.4f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    }
                }
            }
        });
    }

    /** 关闭开启聊天 */
    @Override
    public void onopenchat(final boolean openchat, final String mode, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isDisable()) {
                    btMesOpen.setAlpha(0.4f);
                    btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                } else {
                    if (openchat) {
                        btMesOpen.setAlpha(1.0f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    } else {
                        btMesOpen.setAlpha(0.4f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    }
                    if (fromNotice) {
                        if (LiveTopic.MODE_CLASS.equals(mode)) {
                            XESToastUtils.showToast(mContext, "主讲老师" + (openchat ? "打开" : "关闭") + "了聊天区");
                        } else {
                            XESToastUtils.showToast(mContext, "辅导老师" + (openchat ? "打开" : "关闭") + "了聊天区");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onModeChange(final String mode) {
        mView.post(new Runnable() {

            @Override
            public void run() {
                // 主讲模式可以献花
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    if (ircState.isOpenbarrage()) {
                        btMessageFlowers.setTag("1");
                        btMessageFlowers.setAlpha(1.0f);
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                    } else {
                        btMessageFlowers.setTag("0");
                        btMessageFlowers.setAlpha(0.4f);
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                    }
                } else {
                    btMessageFlowers.setTag("0");
                    btMessageFlowers.setAlpha(0.4f);
                    btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                }
            }
        });
    }

    /** 关闭开启弹幕 */
    @Override
    public void onOpenbarrage(final boolean openbarrage, final boolean fromNotice) {

    }

    @Override
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

    }

    /**
     * 理科辅导老师开启/关闭献花
     *
     * @param openbarrage
     * @param fromNotice
     */
    @Override
    public void onFDOpenbarrage(final boolean openbarrage, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                    if (openbarrage) {
                        if (fromNotice) {
                            XESToastUtils.showToast(mContext, "老师开启了献花");
                        }
                        btMessageFlowers.setTag("1");
                        btMessageFlowers.setAlpha(1.0f);
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                    } else {
                        if (fromNotice) {
                            XESToastUtils.showToast(mContext, "老师关闭了献花");
                        }
                        btMessageFlowers.setTag("0");
                        btMessageFlowers.setAlpha(0.4f);
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                    }
                }
            }
        });
    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage,
                                    boolean isFDLKOpenbarrage) {

    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }

    /*添加聊天信息，超过120，移除60个*/
    @Override
    public void addMessage(final String sender, final int type, final String text, String headUrl) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                final SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                                .chatSendContentDeal(text), mContext,
                        messageSize);
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (liveMessageEntities.size() > 29) {
                            liveMessageEntities.remove(0);
                        }
                        if (otherLiveMessageEntities.size() > 29) {
                            otherLiveMessageEntities.remove(0);
                        }
                        LiveMessageEntity entity = new LiveMessageEntity(sender, type, sBuilder);
                        liveMessageEntities.add(entity);
                        otherLiveMessageEntities.add(entity);
                        messageAdapter.notifyDataSetChanged();
                        if (otherMessageAdapter != null) {
                            otherMessageAdapter.notifyDataSetChanged();
                        }
                        if (!isTouch) {
                            lvMessage.setSelection(lvMessage.getCount() - 1);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
        tvMessageGold.setText(goldNum);
        tvMessageGold.setVisibility(View.VISIBLE);
        tvMessageGoldLable.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuestionHide() {
        int isCheck = (int) lvMessage.getTag();
        if (isCheck == 1) {
            lvMessage.setVisibility(View.GONE);
        } else {
            lvMessage.setVisibility(View.VISIBLE);
        }
        lvMessage.setTag(R.id.cb_livevideo_message_clock, 0);
    }

    @Override
    public void onQuestionShow() {
        lvMessage.setTag(R.id.cb_livevideo_message_clock, 1);
        lvMessage.setVisibility(View.INVISIBLE);
    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {
        this.otherMessageAdapter = otherMessageAdapter;
    }
}

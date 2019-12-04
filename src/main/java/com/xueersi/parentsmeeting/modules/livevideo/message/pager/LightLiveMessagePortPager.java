package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwl.bury.xrsbury.XrsBury;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.LPWeChatEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.TeacherWechatDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.dialog.ConfirmAlertDialog;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.message.pager
 * @ClassName: LightLiveMessagePortPager
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/22 16:43
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/22 16:43
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveMessagePortPager extends BaseLiveMessagePager {
    private String TAG = "LiveMessagePortPager";
    /**
     * 聊天，默认打开
     */
    private CheckBox cbMessageTeacher;
    /**
     * 聊天，清空数据
     */
    private ImageView ivMessageClean;
    /**
     * 聊天消息
     */
    private ListView lvMessage;
    private Button btMessageExpress;
    private CommonAdapter<LiveMessageEntity> messageAdapter;
    private CommonAdapter<LiveMessageEntity> otherMessageAdapter;
    private boolean isTouch = false;
    /**
     * 聊天字体大小，最多13个汉字
     */
    private int messageSize = 0;
    private TextView tvMessageDisable;
    /**
     * 表情布局
     */
    private View expressContentView;
    /**
     * 上次发送消息时间
     */
    private long lastSendMsg;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    private ImageView ivExpressionCancle;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    /**
     * 竖屏的时候，也添加横屏的消息
     */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;
    /**
     * 聊天倒计时标记
     */
    private String COUNT_TAG_MSG = "msg";
    private Activity liveVideoActivity;
    private TeacherWechatDialog wechatDialog;
    private String mTeacherName;
    private String mTeacherWechat;
    private String mTeacherHeadImg;
    private String mQrcodeImg;
    private TextView tvTeacherWeChat;
    /** 联系老师实体*/
    private LPWeChatEntity weChatEntity;
    boolean isShowWeChat;
    private VerifyCancelAlertDialog cleanMessageDialog;

    public LightLiveMessagePortPager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                     ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.keyboardShowingListener = keyboardShowingListener;
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_light_message_port, null);
        lvMessage = (ListView) mView.findViewById(R.id.lv_livevideo_message);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        tvMessageDisable = (TextView) mView.findViewById(R.id.tv_livevideo_message_disable);
        tvMessageDisable.setTag(MESSAGE_SEND_DEF);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_message_express);
        switchFSPanelLinearLayout = (KPSwitchFSPanelLinearLayout) mView.findViewById(R.id
                .rl_livevideo_message_panelroot);
        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
        cbMessageTeacher = (CheckBox) mView.findViewById(R.id.cb_livevideo_message_teacher);
        ivMessageClean = (ImageView) mView.findViewById(R.id.iv_livevideo_message_clean);
        tvTeacherWeChat = mView.findViewById(R.id.tv_livevideo_teacher_wechat);
        return mView;
    }

    @Override
    public void setGetInfo(LiveGetInfo getInfo) {
        super.setGetInfo(getInfo);
        if (getInfo != null && getInfo.getLpWeChatEntity() != null){
            weChatEntity = getInfo.getLpWeChatEntity();
           if (getInfo.getLpWeChatEntity().getTipType() == LPWeChatEntity.WECHAT_GROUP && getInfo.getLpWeChatEntity().getExistWx() == 1){
                isShowWeChat = true;
                tvTeacherWeChat.setVisibility(View.VISIBLE);
                tvTeacherWeChat.setText("加班级群");
               XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_008));
            }else if (getInfo.getLpWeChatEntity().getTipType() == LPWeChatEntity.TEACHER_WECHAT && getInfo.getLpWeChatEntity().getExistWx() == 1){
                isShowWeChat = true;
                tvTeacherWeChat.setVisibility(View.VISIBLE);
                XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_006));
            }else {
                isShowWeChat = false;
                tvTeacherWeChat.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void initListener() {

        tvMessageDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        etMessageContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent
                        .KEYCODE_ENTER)) {
                    logger.i("onClick:time=" + (System.currentTimeMillis() - lastSendMsg));
                    Editable editable = etMessageContent.getText();
                    String msg = editable.toString();
                    if (!StringUtils.isSpace(msg)) {
                        if (getInfo != null && getInfo.getBlockChinese() && isChinese(msg)) {
                            addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_CHINESE, "");
                            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                                    .INPUT_METHOD_SERVICE);
                            mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        }
                        if (ircState.openchat()) {
                            if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {

                                String name = LiveAppUserInfo.getInstance().getName();
                                if (name == null || name.isEmpty()){
                                    name = getInfo.getStuName();
                                }
                                boolean send = ircState.sendMessage(msg, name);
                                if (send) {
                                    startCountDown(COUNT_TAG_MSG, (int) (SEND_MSG_INTERVAL / 1000));
                                    etMessageContent.setText("");
                                    addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, "");
                                    lastSendMsg = System.currentTimeMillis();
                                    if(actionId == EditorInfo.IME_ACTION_SEND){
                                        KPSwitchConflictUtil.hidePanelAndKeyboard(switchFSPanelLinearLayout);
                                        onKeyBoardShow(false);
                                    }
                                } else {
                                    XESToastUtils.showToast(mContext, "你已被禁言!");
                                }
                            } else {
                                //暂时去掉3秒发言，信息提示
//                                addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "3秒后才能再次发言，要认真听课哦!");
                                long time;
                                try {
                                    time = Long.parseLong(tvMessageDisable.getText().subSequence(1, 2).toString());
                                } catch (Exception e) {
                                    time = SEND_MSG_INTERVAL / 1000;
                                }
                                XESToastUtils.showToast(mContext, time + "秒后才能再次发言，要认真听课哦!");
                            }
                        } else {
                            XESToastUtils.showToast(mContext, "老师未开启聊天");
                        }
                    } else {
                        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_EMPTY, "");

                    }
                    return true;
                }
                return false;
            }
        });
        etMessageContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyBoardShow(true);
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
                    onKeyBoardShow(false);
                }
                keyboardShowing = isShowing;
                keyboardShowingListener.onKeyboardShowing(isShowing);
                if (isShowing){
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                }
            }
        });
        KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, btMessageExpress, etMessageContent, new KPSwitchConflictUtil.SwitchClickListener() {
            @Override
            public void onClickSwitch(boolean switchToPanel) {
                onKeyBoardShow(true);
                if (switchToPanel) {
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_jianpan_icon_normal);
                    etMessageContent.clearFocus();
                } else {
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                    etMessageContent.requestFocus();
                }
            }
        });
        ivMessageClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ConfirmAlertDialog
                XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_014));
                cleanMessage();
            }
        });
        cbMessageTeacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    XESToastUtils.showToast(mContext, "只看老师消息");
                    XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_013),1);
                } else {
                    XESToastUtils.showToast(mContext, "接收全部消息");
                    XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_013),0);
                }
            }
        });

        tvTeacherWeChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (weChatEntity != null){
                    showWeChatDialog();
                }
            }
        });


    }

    @Override
    public void initData() {
        super.initData();
        Resources resources = mContext.getResources();
        nameColors = new int[]{resources.getColor(R.color.COLOR_FF5E50), resources.getColor(R.color.COLOR_FF5E50),
                resources.getColor(R.color.COLOR_666666), resources.getColor(R.color.COLOR_FE9B43)};
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
        messageSize = (int) (ScreenUtils.getScreenDensity() * 15);
        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities) {
            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                return new AdapterItemInterface<LiveMessageEntity>() {
                    TextView tvMessageItem;

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_livevideo_lightlive_message;
                    }

                    @Override
                    public void initViews(View root) {
                        tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
                        tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
                        tvMessageItem.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
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
                            if (LiveMessageEntity.MESSAGE_MINE == entity.getType()){
                                SpannableString meSpan = new SpannableString(entity.getText());
                                CharacterStyle meStyle = new ForegroundColorSpan(color);
                                meSpan.setSpan(meStyle, 0, entity.getText().length() , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                tvMessageItem.append(meSpan);
                            }else {
                                tvMessageItem.append(entity.getText());
                            }
                        }

                    }
                };
            }
        };
        lvMessage.setAdapter(messageAdapter);
        expressContentView = mView.findViewById(R.id.layout_chat_expression);
        lvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (keyboardShowing) {
                    KeyboardUtil.hideKeyboard(etMessageContent);
                }
                return false;
            }
        });

    }

    @Override
    public void onTitleShow(boolean show) {

    }

    @Override
    public void countDown(String tag, int time) {
        if (COUNT_TAG_MSG.equals(tag)) {
            if ((int) tvMessageDisable.getTag() == MESSAGE_SEND_DEF) {
                if (time > 0) {
                    //etMessageContent.setVisibility(View.GONE);
                    tvMessageDisable.setVisibility(View.VISIBLE);
                } else {
                    //etMessageContent.setVisibility(View.VISIBLE);
                    tvMessageDisable.setVisibility(View.GONE);
                }
                tvMessageDisable.setText("请" + time + "秒后再次发言");
            }
        }
    }

    @Override
    public void closeChat(final boolean close) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (close) {
                    cbMessageTeacher.setChecked(true);
                } else {
                    cbMessageTeacher.setChecked(false);
                }
            }
        });
    }

    @Override
    public boolean isCloseChat() {
        return cbMessageTeacher.isChecked();
    }

    /**
     * 聊天开始连接
     */
    @Override
    public void onStartConnect() {

    }

    @Override
    public void setIsRegister(boolean isRegister) {
        super.setIsRegister(isRegister);
    }

    /**
     * 聊天连上
     */
    @Override
    public void onConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
            }
        });
    }

    /**
     * 聊天进入房间
     */
    @Override
    public void onRegister() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = true;
            }
        });
    }

    /**
     * 聊天断开
     */
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
//                        addDanmaKuFlowers(jsonObject.getInt("ftype"), jsonObject.getString("name"));
                        addFlowers(jsonObject.optString("id"), jsonObject.getString("name"), LiveMessageEntity
                                .MESSAGE_FLOWERS, jsonObject.optInt("ftype"));
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

    /**
     * 被禁言
     */
    public void onDisable(final boolean disable, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "你被老师禁言了");
                    }
                    //etMessageContent.setVisibility(View.GONE);
                    tvMessageDisable.setVisibility(View.VISIBLE);
                    tvMessageDisable.setText("你被老师禁言了");
                    tvMessageDisable.setTag(MESSAGE_SEND_DIS);
                } else {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "老师解除了你的禁言");
                    }
                    if (ircState.openchat()) {
                        //etMessageContent.setVisibility(View.VISIBLE);
                        tvMessageDisable.setVisibility(View.GONE);
                        tvMessageDisable.setTag(MESSAGE_SEND_DEF);
                    } else {
                        //etMessageContent.setVisibility(View.GONE);
                        tvMessageDisable.setVisibility(View.VISIBLE);
                        tvMessageDisable.setTag(MESSAGE_SEND_CLO);
                        if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                            tvMessageDisable.setText("主讲老师关闭了聊天区");
                        } else {
                            tvMessageDisable.setText("辅导老师关闭了聊天区");
                        }
                    }
                }
            }
        });
    }

    /**
     * 关闭开启聊天
     */
    @Override
    public void onopenchat(final boolean openchat, final String mode, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isDisable()) {
                    //etMessageContent.setVisibility(View.GONE);
                    tvMessageDisable.setVisibility(View.VISIBLE);
                    tvMessageDisable.setText("你被老师禁言了");
                    tvMessageDisable.setTag(MESSAGE_SEND_DIS);
                } else {
                    if (openchat) {
                        //etMessageContent.setVisibility(View.VISIBLE);
                        tvMessageDisable.setVisibility(View.GONE);
                        tvMessageDisable.setTag(MESSAGE_SEND_DEF);
                    } else {
                        //etMessageContent.setVisibility(View.GONE);
                        tvMessageDisable.setVisibility(View.VISIBLE);
                        tvMessageDisable.setTag(MESSAGE_SEND_CLO);
                        if (LiveTopic.MODE_CLASS.equals(mode)) {
                            tvMessageDisable.setText("主讲老师关闭了聊天区");
                        } else {
                            tvMessageDisable.setText("辅导老师关闭了聊天区");
                        }
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
    public void onOpenbarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public boolean onBack() {
//        ViewGroup parent = (ViewGroup) flowerContentView.getParent();
//        if (parent != null) {
//            removeFlowers();
//            return true;
//        }
        return super.onBack();
    }

    @Override
    public void onModeChange(final String mode) {

    }


    @Override
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean b, boolean zjlkOpenbarrage, boolean zjfdOpenbarrage) {

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

    /**
     * 暂不使用
     * @param id
     * @param sender
     * @param type
     * @param ftype
     */
    public void addFlowers(final String id, final String sender, final int type, final int ftype) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (liveMessageEntities.size() > 29) {
                    liveMessageEntities.remove(0);
                }
                boolean self = "-1".equals(id);
                liveMessageEntities.add(new LiveMessageEntity(self, sender, type, ftype));
                messageAdapter.notifyDataSetChanged();
                if (!isTouch) {
                    lvMessage.setSelection(lvMessage.getCount() - 1);
                }
            }
        });
    }

    @Override
    public void onGetMyGoldDataEvent(String goldNum) {
    }

    @Override
    public void onQuestionHide() {
        mView.findViewById(R.id.rl_livevideo_message_content2).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.rl_livevideo_message_status).setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuestionShow() {
        mView.findViewById(R.id.rl_livevideo_message_content2).setVisibility(View.INVISIBLE);
        mView.findViewById(R.id.rl_livevideo_message_status).setVisibility(View.INVISIBLE);
        KeyboardUtil.hideKeyboard(etMessageContent);
    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {
        this.otherMessageAdapter = otherMessageAdapter;
    }

    @Override
    public void onDestroy() {
        if (wechatDialog != null && wechatDialog.isDialogShow()){
            wechatDialog.cancelDialog();
        }
        if (cleanMessageDialog != null && cleanMessageDialog.isDialogShow()){
            cleanMessageDialog.cancelDialog();
        }
        super.onDestroy();
    }

    public void setWeChatEntity(LPWeChatEntity weChatEntity) {
        this.weChatEntity = weChatEntity;
    }

    /**
     * 显示联系老师弹窗
     */
    private void showWeChatDialog(){
        if (wechatDialog == null ){
            wechatDialog = new TeacherWechatDialog(mContext,mBaseApplication,weChatEntity.getTipType());
        }
        if (weChatEntity.getTipType() == TeacherWechatDialog.TYPE_WITH_HEAD){
            XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_008));
            XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_007));
        } else if (weChatEntity.getTipType() == TeacherWechatDialog.TYPE_WITH_QRCODE){
            XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_010));
            XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_009));
        }
        wechatDialog.setTeacherHead(weChatEntity.getTeacherImg()).setTeacherName(weChatEntity.getTeacherName())
                .setTeacherWechat(weChatEntity.getTeacherWx()).setQrcode(weChatEntity.getWxQrUrl()).setSubTitle(weChatEntity.getTipInfo());
        wechatDialog.showDialog();
    }

    /**
     * 键盘弹出收回改变UI
     * @param isShow
     */
    private void onKeyBoardShow(boolean isShow){
        if (isShow){
            if (ivMessageClean.getVisibility() == View.VISIBLE){
                ivMessageClean.setVisibility(View.GONE);
                cbMessageTeacher.setVisibility(View.GONE);
                tvTeacherWeChat.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btMessageExpress.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.removeRule(RelativeLayout.ALIGN_RIGHT);
                params.rightMargin = SizeUtils.Dp2Px(mContext,16);
                btMessageExpress.setLayoutParams(params);
                RelativeLayout.LayoutParams etParams = (RelativeLayout.LayoutParams) etMessageContent.getLayoutParams();
//            etParams.addRule(RelativeLayout.RIGHT_OF,R.id.bt_livevideo_message_express);
                etParams.rightMargin = SizeUtils.Dp2Px(mContext,56);
                etMessageContent.setLayoutParams(etParams);
            }
        }else {
            if (ivMessageClean.getVisibility() == View.GONE) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btMessageExpress.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.et_livevideo_message_content);
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.rightMargin = SizeUtils.Dp2Px(mContext, 8);
                btMessageExpress.setLayoutParams(params);
                RelativeLayout.LayoutParams etParams = (RelativeLayout.LayoutParams) etMessageContent.getLayoutParams();
//            etParams.addRule(RelativeLayout.RIGHT_OF,R.id.bt_livevideo_message_express);
                etParams.rightMargin = SizeUtils.Dp2Px(mContext, 16);
                etMessageContent.setLayoutParams(etParams);
                ivMessageClean.setVisibility(View.VISIBLE);
                cbMessageTeacher.setVisibility(View.VISIBLE);
                if (isShowWeChat){
                    tvTeacherWeChat.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    private void cleanMessage(){
        if (cleanMessageDialog == null){
            cleanMessageDialog = new VerifyCancelAlertDialog(mContext,mBaseApplication,false,VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cleanMessageDialog.initInfo("需要清空当前所有聊天消息吗？");
            cleanMessageDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XESToastUtils.showToast(mContext, "清屏成功！");
                    liveMessageEntities.clear();
                    otherLiveMessageEntities.clear();
                    messageAdapter.notifyDataSetChanged();
                    if (otherMessageAdapter != null) {
                        otherMessageAdapter.notifyDataSetChanged();
                    }
                    XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_015));
                }
            });
            cleanMessageDialog.setCancelBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_016));
                    cleanMessageDialog.cancelDialog();
                }
            });
        }
        cleanMessageDialog.showDialog();
    }
}


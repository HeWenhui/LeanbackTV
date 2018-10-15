package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.CommonWordItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * 小英LiveMessagePager，类似于LiveMessagePager，在其基础上面进行修改
 * 献花弹窗，弹幕，聊天信息界面，以及发送消息，小英区别于LiveMessagePager。
 */
public class SmallEnglishLiveMessagePager extends BaseSmallEnglishLiveMessagePager {
    //本组在线人数
    private TextView tvOnlineNum;
    private CommonAdapter<LiveMessageEntity> commonAdapter;

    private static String TAG = "LiveMessagePager";
    /** 聊天，默认开启 */
    private Button btMesOpen;
    /** 聊天常用语 */
    private Button btMsgCommon;
    private RelativeLayout rlLivevideoCommonWord;
    ListView lvCommonWord;
    /** 献花，默认关闭 */
    private Button btMessageFlowers;
    /** 聊天，默认打开 */
    private CheckBox cbMessageClock;
    /** 聊天人数 */
//    private TextView tvMessageCount;
    /** 聊天IRC一下状态，正在连接，在线等 */
//    private ImageView ivMessageOnline;
    /** 聊天消息 */
    private ListView lvMessage;
    private View rlInfo;
    //输入聊天信息时软键盘上面的显示框
    private View rlMessageContent;
    //确定献花的按钮
    private Button btMessageSend;
    //聊天表情的表情包
    private Button btMessageExpress;
    private CommonAdapter<LiveMessageEntity> messageAdapter;
    private CommonAdapter<LiveMessageEntity> otherMessageAdapter;
    private boolean isTouch = false;
    /** 聊天字体大小，最多13个汉字 */
    private int messageSize = 0;
    /** 献花 */
    private PopupWindow mFlowerWindow;
    //献花的弹窗
//    private View flowerContentView;
//    private TextView tvMessageGoldLable;
//    private TextView tvMessageGold;
    private String goldNum;
    /** 上次发送消息时间 */
    private long lastSendMsg;
    private BaseLiveMediaControllerBottom liveMediaControllerBottom;

    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    //        private ImageView ivExpressionCancle;
    private Activity liveVideoActivity;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    /** 竖屏的时候，也添加横屏的消息 */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;
    LiveAndBackDebug liveAndBackDebug;
    private String liveId;
    private String termId;
    private View mFloatView;
    private PopupWindow mPopupWindow;
    //是否是小英
    private SmallEnglishSendFlowerPager smallEnglishSendFlowerPager;
    //测试使用的布尔值，用来控制无限发送弹幕
    private boolean blTestSEBullet = false;
    //打开献花弹窗时，北京变为80%黑色透明，且不可点击.
    private FrameLayout frameLayout;
    //整个布局的根View,用来献花弹窗增加背景时使用
    private ViewGroup decorView;

    public SmallEnglishLiveMessagePager(Context context) {
        super(context);
    }

    public SmallEnglishLiveMessagePager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                        LiveAndBackDebug ums, BaseLiveMediaControllerBottom liveMediaControllerBottom,
                                        ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity>
                                                otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        this.keyboardShowingListener = keyboardShowingListener;
        this.liveAndBackDebug = ums;
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;

        btMesOpen = liveMediaControllerBottom.getBtMesOpen();
        btMsgCommon = liveMediaControllerBottom.getBtMsgCommon();
        btMessageFlowers = liveMediaControllerBottom.getBtMessageFlowers();
        cbMessageClock = liveMediaControllerBottom.getCbMessageClock();
        lvCommonWord = liveMediaControllerBottom.getLvCommonWord();

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                initListener();
                initData();
            }
        });
        setVideoLayout(LiveVideoPoint.getInstance());
        //重写color mine teacher class tip
        Resources resources = context.getResources();
        nameColors = new int[]{resources.getColor(R.color.COLOR_FFFFFF), resources.getColor(R.color.COLOR_FFB400),
                resources.getColor(R.color.COLOR_C3DAFF), resources.getColor(R.color.COLOR_FFB400)};
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View initView() {

        mView = View.inflate(mContext, R.layout.page_livevideo_small_english_message, null);
        lvMessage = mView.findViewById(R.id.lv_livevideo_small_english_live_message);
        tvOnlineNum = mView.findViewById(R.id.tv_livevideo_small_english_online_people_num);

//        tvMessageCount = (TextView) mView.findViewById(R.id.tv_livevideo_message_count);
//        ivMessageOnline = (ImageView) mView.findViewById(R.id.iv_livevideo_message_online);

        dvMessageDanmaku = mView.findViewById(R.id.dv_livevideo_small_english_message_danmaku);
        rlInfo = mView.findViewById(R.id.rl_livevideo_info);

        rlMessageContent = mView.findViewById(R.id.rl_livevideo_small_english_message_content2);

        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_small_english_message_content);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_small_english_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_small_english_message_send);

        switchFSPanelLinearLayout = mView.findViewById(R.id.rl_livevideo_small_english_message_panelroot);
//        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
//        int screenWidth = ScreenUtils.getScreenWidth();
//        int screenHeight = ScreenUtils.getScreenHeight();
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();

//        if (rlInfo == null) {
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
//                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
//        }
//        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
//        int hradio = (int) ((LiveVideoConfig.VIDEO_HEIGHT - LiveVideoConfig.VIDEO_HEAD_HEIGHT) * screenHeight /
//                LiveVideoConfig.VIDEO_HEIGHT);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        params.width = liveVideoPoint.getRightMargin();
        params.topMargin = liveVideoPoint.y3;
        logger.setLogMethod(false);
        logger.d("initView:width=" + liveVideoPoint.getRightMargin() + "," + liveVideoPoint.y3);

        decorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();

        return mView;
    }

    @Override
    public void initData() {
        long before = System.currentTimeMillis();
        super.initData();
        logger.i("initData:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        btMessageFlowers.setTag("0");
        btMessageFlowers.setAlpha(0.4f);
        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
//        ivExpressionCancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int action = KeyEvent.ACTION_DOWN;
//                int code = KeyEvent.KEYCODE_DEL;
//                KeyEvent event = new KeyEvent(action, code);
//                etMessageContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
//            }
//        });
        showExpressionView(true);

        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);


        logger.i("initData:minisize=" + minisize);
        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities) {
            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                return new AdapterItemInterface<LiveMessageEntity>() {
                    TextView tvMessageItem;

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_livevideo_message;
                    }

                    @Override
                    public void initViews(View root) {
                        tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
                        tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
                    }

                    @Override
                    public void bindListener() {

                    }

                    @Override
                    public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
                        String sender = entity.getSender();
                        SpannableString spanttt = new SpannableString(sender + ": ");
                        int color, messageColor;
                        switch (entity.getType()) {
                            case LiveMessageEntity.MESSAGE_MINE:
                                color = nameColors[entity.getType()];
                                messageColor = mContext.getResources().getColor(R.color.COLOR_FFFFFFFF);
//                                Log.w(TAG, "1:" + messageColor);
                                break;
                            case LiveMessageEntity.MESSAGE_TEACHER:
                                color = nameColors[entity.getType()];
                                messageColor = mContext.getResources().getColor(R.color.COLOR_FFC3DAFF);
//                                Log.w(TAG, "2:" + messageColor);
                                break;
                            case LiveMessageEntity.MESSAGE_TIP:
                                color = nameColors[entity.getType()];
                                messageColor = mContext.getResources().getColor(R.color.COLOR_FFFFFFFF);
//                                Log.w(TAG, "3:" + messageColor);
                                break;
                            case LiveMessageEntity.MESSAGE_CLASS:
                                color = nameColors[entity.getType()];
                                messageColor = mContext.getResources().getColor(R.color.COLOR_FFC3DAFF);
//                                Log.w(TAG, "4:" + messageColor);
                                break;
                            default:
                                color = nameColors[0];
                                messageColor = mContext.getResources().getColor(R.color.COLOR_FFFFFFFF);
//                                Log.w(TAG, "5:" + messageColor);
                                break;
                        }
                        SpannableStringBuilder messageSpan = new SpannableStringBuilder(entity.getText());
                        CharacterStyle characterStyle = new ForegroundColorSpan(color);
                        CharacterStyle messageStyle = new ForegroundColorSpan(messageColor);
                        spanttt.setSpan(characterStyle, 0, sender.length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        messageSpan.setSpan(messageStyle, 0, entity.getText().length(), Spanned
                                .SPAN_INCLUSIVE_EXCLUSIVE);
                        if (urlclick == 1 && LiveMessageEntity.MESSAGE_TEACHER == entity.getType()) {
                            tvMessageItem.setAutoLinkMask(Linkify.WEB_URLS);
                            tvMessageItem.setText(entity.getText());
                            urlClick(tvMessageItem);
//                            CharSequence text = tvMessageItem.getText();
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(messageSpan);
//                            Log.w(TAG, "6:" + messageColor + " " + entity.getText());
//                            tvMessageItem.append(text);
                        } else {
                            tvMessageItem.setAutoLinkMask(0);
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(messageSpan);
//                            Log.w(TAG, "7:" + messageColor + " " + entity.getText());
                        }
                    }
                };
            }
        };
        lvMessage.setAdapter(messageAdapter);
        logger.i("initData:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mView.post(new Runnable() {
            @Override
            public void run() {
                initDanmaku();
            }
        });
        logger.i("initData:time3=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        initCommonWord();
        logger.i("initData:time4=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
    }

    private void initCommonWord() {
        final ArrayList<String> words = new ArrayList<>();
        words.add("[e]em_1[e]");
        words.add("[e]em_11[e]");
        words.add("[e]em_16[e]");
        words.add("666");
        words.add("2");
        words.add("1");
        lvCommonWord.setAdapter(new CommonAdapter<String>(words) {
            @Override
            public AdapterItemInterface<String> getItemView(Object type) {
                return new CommonWordItem(mContext, this);
            }
        });
        lvCommonWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = words.get(position);
                if (ircState.openchat()) {
                    if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                        boolean send = ircState.sendMessage(msg, "");
                        if (send) {
                            etMessageContent.setText("");
                            addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, "");
                            lastSendMsg = System.currentTimeMillis();
                            onTitleShow(true);
                            rlLivevideoCommonWord.setVisibility(View.INVISIBLE);
                        } else {
                            XESToastUtils.showToast(mContext, "你已被禁言!");
                        }
                    } else {
                        //暂时去掉3秒发言，信息提示
//                                addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "3秒后才能再次发言，要认真听课哦!");
                        long timeDelay = (SEND_MSG_INTERVAL - System.currentTimeMillis() + lastSendMsg) / 1000;
                        timeDelay = timeDelay <= 0 ? 1 : timeDelay;
                        XESToastUtils.showToast(mContext, timeDelay + "秒后才能再次发言，要认真听课哦!");
                    }
                } else {
                    XESToastUtils.showToast(mContext, "老师未开启聊天");
                }
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        rlLivevideoCommonWord = (RelativeLayout) liveMediaControllerBottom.findViewById(R.id.rl_livevideo_common_word);
        //聊天，设置监听器
        btMesOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveMediaControllerBottom.onChildViewClick(v);
                rlMessageContent.setVisibility(View.VISIBLE);
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
            }
        });
        btMsgCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                liveMediaControllerBottom.onChildViewClick(v);
                LiveMediaController controller = liveMediaControllerBottom.getController();
                controller.show();
                if (rlLivevideoCommonWord.getVisibility() == View.VISIBLE) {
                    rlLivevideoCommonWord.setVisibility(View.INVISIBLE);
                    return;
                }
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                //在控件上方显示
                logger.i("onClick:Width=" + rlLivevideoCommonWord.getWidth() + ",Height=" + rlLivevideoCommonWord
                        .getHeight());
                rlLivevideoCommonWord.setVisibility(View.VISIBLE);
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
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                liveMediaControllerBottom.onChildViewClick(v);
                if (goldNum == null) {
                    OtherModulesEnter.requestGoldTotal(mContext);
                }
                QuestionStatic questionStatic = ProxUtil.getProxUtil().get(mContext, QuestionStatic.class);
                if (questionStatic != null && questionStatic.isAnaswer()) {
                    commonAction.isAnaswer();
                    return;
                }
                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                    if (!ircState.isOpenbarrage()) {
                        commonAction.clickIsnotOpenbarrage();
                        return;
                    }
                } else {
                    commonAction.clickTran();
                    return;
                }
                //小英
                if (smallEnglishSendFlowerPager != null) {
//                    RelativeLayout.LayoutParams flowerLayoutParams = artsSendFlowerPager
//                            .getCenterInVideoLayoutParams();
//                    liveMediaControllerBottom.addView(artsSendFlowerPager.getRootView(), flowerLayoutParams);
//                    WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
//                    lp.alpha = 0.8f; // 0.0~1.0
//                    ((Activity) mContext).getWindow().setAttributes(lp); //act 是上下文context

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                            .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    decorView.addView(frameLayout, layoutParams);
//                    mFlowerWindow.setBackgroundDrawable(dw);
//                    mFlowerWindow.setContentView(smallEnglishSendFlowerPager.getRootView());
//                    mFlowerWindow.showAtLocation(mView, Gravity.LEFT, 0, 0);
                }
                isHaveFlowers = true;
            }
        });
        cbMessageClock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    liveMediaControllerBottom.onChildViewClick(v);
                }
                return false;
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
                            long timeDelay = (SEND_MSG_INTERVAL - System.currentTimeMillis() + lastSendMsg) / 1000;
                            timeDelay = timeDelay <= 0 ? 1 : timeDelay;
                            XESToastUtils.showToast(mContext, timeDelay + "秒后才能再次发言，要认真听课哦!");
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
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtil.attach((Activity) mContext, switchFSPanelLinearLayout, new KeyboardUtil
                        .OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        logger.i("onKeyboardShowing:isShowing=" + isShowing);
                        if (!isShowing && switchFSPanelLinearLayout.getVisibility() == View.GONE) {
                            onTitleShow(true);
                        }
                        keyboardShowing = isShowing;
                        keyboardShowingListener.onKeyboardShowing(isShowing);
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
            }
        }, 10);

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
//                    lvMessage.setVisibility(View.GONE);
                    cbMessageClock.setChecked(true);
                } else {
//                    lvMessage.setVisibility(View.VISIBLE);
                    cbMessageClock.setChecked(false);
                }
            }
        });
    }


    @Override
    public boolean isCloseChat() {
        return cbMessageClock.isChecked();
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        {
            int wradio = liveVideoPoint.x4 - liveVideoPoint.x3;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
            if (wradio != params.width || params.rightMargin != liveVideoPoint.screenWidth - liveVideoPoint.x4) {
                //logger.e( "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
                // + ",wradio=" + wradio + "," + params.width);
                params.width = wradio;
                params.rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
//                rlInfo.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(rlInfo, params);
            }
            if (cbMessageClock != null) {
                int rightMargin = liveVideoPoint.getRightMargin();
                params = (RelativeLayout.LayoutParams) cbMessageClock.getLayoutParams();
                if (params.rightMargin != rightMargin) {
                    params.rightMargin = rightMargin;
//                cbMessageClock.setLayoutParams(params);
                    LayoutParamsUtil.setViewLayoutParams(cbMessageClock, params);
                }
            }
        }
        {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlInfo.getLayoutParams();
            int topMargin = liveVideoPoint.y3;
            if (topMargin != params.topMargin) {
                params.topMargin = topMargin;
//                rlInfo.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(rlInfo, params);
                logger.d("initView:width=" + liveVideoPoint.getRightMargin() + "," + liveVideoPoint.y3);
            }
            int bottomMargin = liveVideoPoint.y2;
            params = (ViewGroup.MarginLayoutParams) lvMessage.getLayoutParams();
            if (params.bottomMargin != bottomMargin) {
                params.bottomMargin = bottomMargin;
//                lvMessage.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(lvMessage, params);
                //logger.e( "setVideoWidthAndHeight:bottomMargin=" + bottomMargin);
            }
        }
    }


    @Override
    public void setVideoWidthAndHeight(int width, int height) {
        final View contentView = liveVideoActivity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        int screenHeight = ScreenUtils.getScreenHeight();
        if (width > 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            if (wradio != params.width) {
                //logger.e( "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
                // + ",wradio=" + wradio + "," + params.width);
                params.width = wradio;
//                rlInfo.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(rlInfo, params);
            }
            if (cbMessageClock != null) {
                params = (RelativeLayout.LayoutParams) cbMessageClock.getLayoutParams();
                if (params.rightMargin != wradio) {
                    params.rightMargin = wradio;
//                cbMessageClock.setLayoutParams(params);
                    LayoutParamsUtil.setViewLayoutParams(cbMessageClock, params);
                }
            }
        }
        if (height > 0) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlInfo.getLayoutParams();
            int topMargin = (int) ((LiveVideoConfig.VIDEO_HEIGHT - LiveVideoConfig.VIDEO_HEAD_HEIGHT) * height /
                    LiveVideoConfig.VIDEO_HEIGHT);
            topMargin = height - topMargin + (screenHeight - height) / 2;
            if (topMargin != params.topMargin) {
                params.topMargin = topMargin;
//                rlInfo.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(rlInfo, params);
                logger.e( "setVideoWidthAndHeight:topMargin=" + params.topMargin);
            }
            int bottomMargin = (ScreenUtils.getScreenHeight() - height) / 2;
            params = (ViewGroup.MarginLayoutParams) lvMessage.getLayoutParams();
            if (params.bottomMargin != bottomMargin) {
                params.bottomMargin = bottomMargin;
//                lvMessage.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(lvMessage, params);
                //logger.e( "setVideoWidthAndHeight:bottomMargin=" + bottomMargin);
            }
        }
    }

    /** 聊天开始连接 */
    @Override
    public void onStartConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
//                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
            }
        });
    }

    @Override
    public void setIsRegister(boolean isRegister) {
        super.setIsRegister(isRegister);
//        if (isRegister) {
//            ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
//        } else {
//            ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
//        }
    }

    @Override
    public void setHaveFlowers(boolean haveFlowers) {
        super.setHaveFlowers(haveFlowers);
//        if (mFlowerWindow != null) {
        if (frameLayout != null && decorView != null) {
            if (haveFlowers) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                decorView.addView(frameLayout, layoutParams);
//                mFlowerWindow.showAtLocation(btMessageFlowers, Gravity.BOTTOM, 0, 0);
            } else {
                if (frameLayout.getParent() == decorView) {
                    decorView.removeView(frameLayout);
                }

//                mFlowerWindow.dismiss();
            }
        }
    }

    /** 聊天连上 */
    @Override
    public void onConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
//                if (BuildConfig.DEBUG) {
//                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, getInfo.getTeacherId() + "_" + getInfo
// .getId());
//                }
//                if (!isRegister) {
//                    ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
//                }
            }
        });
    }

    @Override
    public void setGetInfo(LiveGetInfo getInfo) {
        super.setGetInfo(getInfo);
        if (getInfo != null) {
            String educationStage = getInfo.getEducationStage();
            initFlower(educationStage);
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    OtherModulesEnter.requestGoldTotal(mContext);
                }
            });
        }
    }


    // 03.16 设置模拟的聊天连接
    public void onConnects() {
        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
//        ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
    }

    /** 聊天进入房间 */
    @Override
    public void onRegister() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = true;
//                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
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
//                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void initFlower(String educationStage) {
        long before = System.currentTimeMillis();
        smallEnglishSendFlowerPager = new SmallEnglishSendFlowerPager(mContext);
        //打开献花弹窗时的布局初始化
        frameLayout = new FrameLayout(mContext);
        //80%透明
        frameLayout.setBackgroundColor(0xCC000000);
//        frameLayout.setAlpha(0.8f);
        frameLayout.setClickable(true);
        frameLayout.addView(smallEnglishSendFlowerPager.getRootView());

        //设置点击赠送的监听器
        smallEnglishSendFlowerPager.setSendFlowerListener(new SmallEnglishSendFlowerPager.SendFlowerListener() {
            @Override
            public void onTouch() {
                if (smallEnglishSendFlowerPager.getIsSelectFlower()) {
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        if (ircState.isOpenbarrage()) {
                            String educationStage = getInfo.getEducationStage();
                            ircState.praiseTeacher("", smallEnglishSendFlowerPager.getWhichFlower() + "",
                                    educationStage, new HttpCallBack(false) {
                                        @Override
                                        public void onPmSuccess(ResponseEntity responseEntity) {
                                            if (goldNum == null) {
                                                OtherModulesEnter.requestGoldTotal(mContext);
                                            } else {
                                                if (responseEntity.getJsonObject() instanceof JSONObject) {
                                                    try {
                                                        JSONObject jsonObject = (JSONObject) responseEntity
                                                                .getJsonObject();
                                                        int gold = Integer.parseInt(goldNum);
                                                        goldNum = ("" + (gold - jsonObject.getInt("gold")));
                                                        onGetMyGoldDataEvent(goldNum);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            addDanmaKuFlowers(smallEnglishSendFlowerPager
                                                    .getWhichFlower(), getInfo.getStuName());
                                            mView.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (frameLayout != null && frameLayout.getParent() == decorView) {
                                                        decorView.removeView(frameLayout);
                                                    }
//                                                    mFlowerWindow.dismiss();
                                                }
                                            }, 1000);
                                        }

                                        @Override
                                        public void onPmFailure(Throwable error, String msg) {
//                                            mFlowerWindow.dismiss();
                                            if (frameLayout.getParent() == decorView) {
                                                decorView.removeView(frameLayout);
                                            }
                                        }

                                        @Override
                                        public void onPmError(ResponseEntity responseEntity) {
//                                            mFlowerWindow.dismiss();
                                            if (frameLayout.getParent() == decorView) {
                                                decorView.removeView(frameLayout);
                                            }
                                        }
                                    });
//                        liveBll.sendFlowerMessage(entity.getFtype());
                        } else {
                            commonAction.clickIsnotOpenbarrage();
                        }
                    } else {
                        commonAction.clickTran();
                    }
                } else {
                    commonAction.clickNoChoice();
                }
            }
        });
        //设置点击取消献花弹窗后的监听器
        smallEnglishSendFlowerPager.setCloseFlowerListener(new SmallEnglishSendFlowerPager.CloseFlowerListener() {
            @Override
            public void onTouch() {
//                    liveMediaControllerBottom.removeAllViews();
                if (mFlowerWindow != null) {
                    //去掉背景色
                    if (frameLayout.getParent() == decorView) {
                        decorView.removeView(frameLayout);
                    }
                    mFlowerWindow.dismiss();
                }
            }
        });
        //测试时候使用
        if (blTestSEBullet) {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    while (blTestSEBullet) {
                        addDanmaKuFlowers(FLOWERS_SMALL, "zyy");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        PopupWindow flowerWindow = new PopupWindow(mContext);
        flowerWindow.setBackgroundDrawable(new BitmapDrawable());
        flowerWindow.setOutsideTouchable(false);
        flowerWindow.setFocusable(true);
//        flowerContentView = View.inflate(mContext, R.layout.pop_livevideo_message_flower, null);

//        flowerWindow.setContentView(flowerContentView);
        flowerWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isHaveFlowers = false;
            }
        });
//        tvMessageGold = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold);
//        tvMessageGoldLable = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold_lable);
//        final LinearLayout llMessageFlower = (LinearLayout) flowerContentView.findViewById(R.id
//                .ll_livevideo_message_flower);
//        final LayoutInflater factory = LayoutInflater.from(mContext);
//        final CompoundButtonGroup group = new CompoundButtonGroup();
        logger.i("initFlower:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();


        mFlowerWindow = flowerWindow;

//        for (int i = 0; i < flowerEntities.size(); i++) {
//            final int index = i;
//            mainHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    final FlowerEntity entity = flowerEntities.get(index);
//                    FlowerItem flowerItem = new FlowerItem(mContext);
//                    View root = factory.inflate(flowerItem.getLayoutResId(), llMessageFlower, false);
//                    flowerItem.initViews(root);
//                    flowerItem.updateViews(flowerEntities.get(index), index, null);
//                    llMessageFlower.addView(root);
//                    group.addCheckBox((CheckBox) root.findViewById(R.id.ck_livevideo_message_flower), new
//                            CompoundButton
//                                    .OnCheckedChangeListener() {
//                                @Override
//                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                    if (isChecked) {
//                                        flowerContentView.setTag(entity);
//                                    } else {
//                                        FlowerEntity entity2 = (FlowerEntity) flowerContentView.getTag();
//                                        if (entity == entity2) {
//                                            flowerContentView.setTag(null);
//                                        }
//                                    }
//                                }
//                            });
//                }
//            }, i * 10);
//        }
        logger.i("initFlower:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        flowerWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        flowerWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        logger.i("initFlower:time3=" + (System.currentTimeMillis() - before));
    }

    FlowerAction commonAction = new CommonDisable();

    class CommonDisable implements FlowerAction {

        @Override
        public void onOpenbarrage(boolean openbarrage) {
            if (openbarrage) {
                XESToastUtils.showToast(mContext, "老师开启了献花");
            } else {
                XESToastUtils.showToast(mContext, "老师关闭了献花");
            }
        }

        @Override
        public void isAnaswer() {
            XESToastUtils.showToast(mContext, "正在答题，不能献花");
        }

        @Override
        public void clickIsnotOpenbarrage() {
            XESToastUtils.showToast(mContext, "老师未开启献花");
        }

        @Override
        public void clickTran() {
            XESToastUtils.showToast(mContext, "辅导模式不能献花");
        }

        @Override
        public void clickNoChoice() {
            XESToastUtils.showToast(mContext, "请选择一束花");
        }

        @Override
        public SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable) {
            return SmallEnglishLiveMessagePager.super.createSpannable(ftype, name, drawable);
        }

        @Override
        public String getFlowerSendText() {
            return "发送";
        }
    }

    class GiftDisable implements FlowerAction {

        @Override
        public void onOpenbarrage(boolean openbarrage) {
            if (openbarrage) {
                XESToastUtils.showToast(mContext, "主讲老师开启了送礼物功能");
            } else {
                XESToastUtils.showToast(mContext, "主讲老师关闭了送礼物功能");
            }
        }

        @Override
        public void isAnaswer() {
            XESToastUtils.showToast(mContext, "正在答题，不能送礼物");
        }

        @Override
        public void clickIsnotOpenbarrage() {
            XESToastUtils.showToast(mContext, "老师未开启送礼物功能");
        }

        @Override
        public void clickTran() {
            XESToastUtils.showToast(mContext, "辅导模式不能送礼物");
        }

        @Override
        public void clickNoChoice() {
            XESToastUtils.showToast(mContext, "请选择一个礼物");
        }

        @Override
        public SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable) {
            String tip = "";
            switch (ftype) {
                case FLOWERS_SMALL:
                case FLOWERS_MIDDLE:
                case FLOWERS_BIG:
                    tip = flowsTips[ftype - 2];
                    break;
            }
            String msg = name + " " + tip;
            TypeSpannableStringBuilder spannableStringBuilder = new TypeSpannableStringBuilder(msg, name, ftype);
            spannableStringBuilder.append(msg);
            ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
            spannableStringBuilder.setSpan(span, msg.length(), spannableStringBuilder.length(), Spannable
                    .SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0,
// spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            return spannableStringBuilder;
        }

        @Override
        public String getFlowerSendText() {
            return "赠送";
        }
    }

    interface FlowerAction {
        /**
         * 开启关闭献花
         *
         * @param openbarrage
         */
        void onOpenbarrage(boolean openbarrage);

        /** 正在答题 */
        void isAnaswer();

        /** 点击没有开启献花时 */
        void clickIsnotOpenbarrage();

        /** 点击是辅导状态时 */
        void clickTran();

        /** 点击没有选择时 */
        void clickNoChoice();

        String getFlowerSendText();

        SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable);
    }


    @Override
    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
        if (smallEnglishSendFlowerPager != null) {
            smallEnglishSendFlowerPager.onGetMyGoldDataEvent(goldNum);
        }
    }


    @Override
    public void onUserList(String channel, final User[] users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
                    tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                    } else {
                        tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                    }
                }
            }
        });
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        logger.e( "=====>onMessage called");
        if (sender.startsWith(LiveIRCMessageBll.TEACHER_PREFIX)) {
            sender = "主讲老师";
        } else if (sender.startsWith(LiveIRCMessageBll.COUNTTEACHER_PREFIX)) {
            sender = "辅导老师";
        }
        addMessage(sender, LiveMessageEntity.MESSAGE_TEACHER, text, headurl);
    }

    public static class TypeSpannableStringBuilder extends SpannableStringBuilder {
        public int ftype;
        public String name;

        public TypeSpannableStringBuilder(CharSequence text, String name, int ftype) {
            super(text);
            this.name = name;
            this.ftype = ftype;
        }
    }

    @Override
    public void onPrivateMessage(boolean isSelf, final String sender, String login, String hostname, String target,
                                 final String message) {
        if (isCloseChat()) {
            return;
        }
        mainHandler.post(new Runnable() {
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
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
                    tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                    } else {
                        tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                    }
                }
            }
        });
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
                    tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                    } else {
                        tvOnlineNum.setText("(" + peopleCount + "/" + getInfo.getTeamStuIds().size() + ")");
                    }
                }
            }
        });
    }


    public void setLiveTermId(String liveId, String termId) {
        this.liveId = liveId;
        this.termId = termId;
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

    }

    /** 被禁言 */
    @Override
    public void onDisable(final boolean disable, final boolean fromNotice) {
        mainHandler.post(new Runnable() {
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
        mainHandler.post(new Runnable() {
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
        mainHandler.post(new Runnable() {

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
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                    if (openbarrage) {
                        if (fromNotice) {
                            commonAction.onOpenbarrage(true);
                        }
                        btMessageFlowers.setTag("1");
                        btMessageFlowers.setAlpha(1.0f);
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                    } else {
                        if (fromNotice) {
                            commonAction.onOpenbarrage(false);
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
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage,
                                    boolean isFDLKOpenbarrage) {

    }


    /*添加聊天信息，超过120，移除60个*/
    @Override
    public void addMessage(final String sender, final int type, final String text, final String headUrl) {
        final Exception e = new Exception();
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
                        LiveMessageEntity entity = new LiveMessageEntity(sender, type, sBuilder, headUrl);
                        liveMessageEntities.add(entity);
                        if (otherLiveMessageEntities != null) {
                            if (otherLiveMessageEntities.size() > 29) {
                                otherLiveMessageEntities.remove(0);
                            }
                            otherLiveMessageEntities.add(entity);
                        }
                        if (otherMessageAdapter != null) {
                            otherMessageAdapter.notifyDataSetChanged();
                        }
                        if (messageAdapter != null) {
                            messageAdapter.notifyDataSetChanged();
                        } else {
                            UmsAgentManager.umsAgentException(BaseApplication.getContext(), TAG + mContext + "," + sender + "," + type, e);
                        }
                        if (!isTouch) {
                            lvMessage.setSelection(lvMessage.getCount() - 1);
                        }
                    }
                });
            }
        });
        // 03.22 体验课播放器统计用户的发送信息
        if (liveAndBackDebug != null && type == LiveMessageEntity.MESSAGE_MINE) {
            StableLogHashMap logHashMap = new StableLogHashMap("LiveFreePlayUserMsg");
            logHashMap.put("LiveFreePlayUserMsg", text);
            logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_IMMSG);
            liveAndBackDebug.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_IMMSG, logHashMap.getData());
        }
        logger.e( "sender:" + sender);
    }

    @Override
    public void onOtherDisable(String id, String name, boolean disable) {

    }


    @Override
    public void onQuestionShow(boolean isShow) {

    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {
        this.otherMessageAdapter = otherMessageAdapter;
    }


//        flowerContentView.findViewById(R.id.tv_livevideo_message_gold_word).setVisibility(View.VISIBLE);


}/*
         "pageid" -> "LiveVideoActivity"
         "device" -> "8"
        "datalogid" -> "584eb97dc220f98e"
         "systemName" -> "android"
        "enstuId" -> "ZWVqaGqVZA==*c2q3/e8575061402a08edb52c78450b23c59f"
         "appChannel" -> "xesmarket"
         "liveId" -> "178233"
         "classId" -> "17981"
         "form" -> "2"
         "systemVersion" -> "7.0"
         "teacherId" -> "2471"
         "appVersion" -> "6.8.05.01"
         "teamId" -> "2"
         "requesttime" -> "1533037924201"
         "identifierForClient" -> "a0fd3d7887ab1db523d4d990c67af38d"
         "logid" -> "52ffc51bf1ae8fba"
         "appVersionNumber" -> "6080501"
         "device_token" -> "f0fa8c3d-9388-46fb-9c66-4c7ca56fbcc5"
         "stuCouId" -> "9616037"
         "courseId" -> "42040"
*/
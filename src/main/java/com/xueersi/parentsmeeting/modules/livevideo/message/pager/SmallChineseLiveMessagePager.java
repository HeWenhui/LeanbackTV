package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.CommonWordChsItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.UserGoldTotal;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

public class SmallChineseLiveMessagePager extends BaseSmallChineseLiveMessagePager {
    //本组在线人数
    private FangZhengCuYuanTextView tvOnlineNum;
    private static String TAG = "LiveMessagePager";
    /** 聊天，默认开启 */
    private Button btMesOpen;
    /** 聊天常用语 */
    private Button btMsgCommon;
    private RelativeLayout rlLivevideoCommonWord;
    /** 表情布局 */
    ListView lvCommonWord;
    /** 献花(送礼物)，默认关闭 */
    private Button btMessageFlowers;
    /** 聊天，默认打开 */
    private CheckBox cbMessageClock;
    /** 聊天人数 */
//    private TextView tvMessageCount;
    /** 聊天IRC一下状态，正在连接，在线等 */
//    private ImageView ivMessageOnline;
    private View rlInfo;
    //输入聊天信息时软键盘上面的显示框
    private View rlMessageContent;
    //确定献花的按钮
    private Button btMessageSend;
    //聊天表情的表情包
    private Button btMessageExpress;




    private String goldNum;
    /** 上次发送消息时间 */
    private long lastSendMsg;
    private BaseLiveMediaControllerBottom liveMediaControllerBottom;

    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;

    private String liveId;
    private String termId;
    //    private View mFloatView;
//    private PopupWindow mPopupWindow;
    //是否是小英
    private SmallChineseSendGiftPager smallChineseSendGiftPager;

    //整个布局的根View,用来献花弹窗增加背景时使用
    private ViewGroup decorView;
    /**
     * 聊天消息的颜色
     */
    private int[] messageColors;
    /** 战队PK背景imageView */
    private ImageView ivPkBackGround;
    /** 使用顶部布局 */
    private ImageView ivMessageTopIcon;
//    private Drawable messageBackgroundColors[];
    /** 小学语文测试，一直发弹幕， */
//    private boolean isSendFlower = false;
    /** 战队pk布局 */
    private TeamPkStateLayout teamPkStateLayout;


    public SmallChineseLiveMessagePager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                        LiveAndBackDebug ums, BaseLiveMediaControllerBottom liveMediaControllerBottom,
                                        ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity>
                                                otherLiveMessageEntities) {
        super(context);
        logger = LoggerFactory.getLogger(getClass().getSimpleName());
//        liveVideoActivity = (Activity) context;
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
        /**
         *重写说话人的color
         *0.mine
         *1.teacher
         *3.class
         *4.tip
         */

        Resources resources = context.getResources();
        nameColors = new int[]{
                resources.getColor(R.color.COLOR_FFFFFF),
                resources.getColor(R.color.COLOR_F0A61B),
                resources.getColor(R.color.COLOR_008B97),
                resources.getColor(R.color.COLOR_F0A61B)
        };
        messageColors = new int[]{
                resources.getColor(R.color.COLOR_FEFFFF),
                resources.getColor(R.color.COLOR_008B97),
                resources.getColor(R.color.COLOR_008B97),
                resources.getColor(R.color.COLOR_008B97),
        };
//        messageBackgroundColors = new Drawable[]{
//                mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_message_my_one_line_background),
//                mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_message_other_one_line_background),
//                mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_message_other_one_line_background),
//                mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_message_other_one_line_background)
//        };
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_small_chinese_live_message, null);
        lvMessage = mView.findViewById(R.id.lv_livevideo_small_chinese_live_message);
        tvOnlineNum = mView.findViewById(R.id.tzcytv_livevideo_small_chinese_live_message_online_people_num);

        rlInfo = mView.findViewById(R.id.rl_livevideo_info);

        rlMessageContent = mView.findViewById(R.id.rl_livevideo_small_chinese_message_content2);

        etMessageContent = mView.findViewById(R.id.et_livevideo_small_chinese_message_content);
        btMessageExpress = mView.findViewById(R.id.bt_livevideo_small_chinese_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_small_chinese_message_send);

        switchFSPanelLinearLayout = mView.findViewById(R.id.rl_livevideo_small_chinese_message_panelroot);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        params.width = liveVideoPoint.getRightMargin();
        params.topMargin = liveVideoPoint.y3;
        logger.d("initView:width=" + liveVideoPoint.getRightMargin() + "," + liveVideoPoint.y3);

        decorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
        ivPkBackGround = mView.findViewById(R.id.iv_livevideo_small_chinese_pk_background);
        ivMessageTopIcon = mView.findViewById(R.id.iv_livevideo_small_chinese_live_message_top_icon);

        teamPkStateLayout = mView.findViewById(R.id.tpkL_teampk_pkstate_root);
        return mView;
    }

    /** 动态调整排行榜背景高度 */
    private void dynamicChangeTopIcon() {

        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        int ivRealWid = liveVideoPoint.x4 - liveVideoPoint.x3;

//        /** 调整战队pk的高度 */
        Drawable teamPkBackground = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_team_pk_background);
        int pkBackgroundHeight = teamPkBackground.getIntrinsicHeight();
        int pkBackgroundWidh = teamPkBackground.getIntrinsicWidth();
        int pkRealWid = ivRealWid;
        double pkMag = ivRealWid * 1.0 / pkBackgroundWidh;
        int pkRealHeight = (int) (pkMag * pkBackgroundHeight);
        ViewGroup.LayoutParams pkLayoutParams = teamPkStateLayout.getLayoutParams();
        pkLayoutParams.height = pkRealHeight;
        pkLayoutParams.width = pkRealWid;
        teamPkStateLayout.setLayoutParams(pkLayoutParams);

        Drawable topIconDrawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_top_icon);
        int topIconHeight = topIconDrawable.getIntrinsicHeight();
        int topIconWid = topIconDrawable.getIntrinsicWidth();


        double mag = ivRealWid * 1.0 / topIconWid;
        int ivRealHeight = (int) (mag * topIconHeight);

        ViewGroup.LayoutParams layoutParams = ivPkBackGround.getLayoutParams();
        layoutParams.width = ivRealWid;
        layoutParams.height = ivRealHeight;
        ivPkBackGround.setLayoutParams(layoutParams);
        logger.i("wid = " + topIconWid + ", height = " + ", ivRealHeight = " + ivRealHeight + ", ivWid = " + ivRealWid + ",mag = " + mag);


        /** 聊天区顶部icon的高度 */
        RelativeLayout.LayoutParams rankLayout = (RelativeLayout.LayoutParams) ivMessageTopIcon.getLayoutParams();
        int btnTopMargin = (int) (SizeUtils.Dp2Px(mContext, 49) * mag);
        rankLayout.topMargin = btnTopMargin;
        ivMessageTopIcon.setLayoutParams(rankLayout);
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
        showExpressionView(true);

        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);
        logger.i("initData:minisize=" + minisize);
        dynamicChangeTopIcon();
        if (getInfo != null && getInfo.getIsAllowTeamPk() != null && getInfo.getIsAllowTeamPk().equals("1")) {

            ivPkBackGround.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_team_pk_background_icon));
        }
        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities) {
            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                return new AdapterItemInterface<LiveMessageEntity>() {
                    FangZhengCuYuanTextView tvMessageItem;

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_small_chinese_message_item;
                    }

                    @Override
                    public void initViews(View root) {
                        tvMessageItem = root.findViewById(R.id.tv_livevideo_small_chinese_message_item);
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
                                messageColor = messageColors[entity.getType()];
//                                Log.w(TAG, "1:" + messageColor);
                                break;
                            case LiveMessageEntity.MESSAGE_TEACHER:
                                color = nameColors[entity.getType()];
                                messageColor = messageColors[entity.getType()];
//                                Log.w(TAG, "2:" + messageColor);
                                break;
                            case LiveMessageEntity.MESSAGE_TIP:
                                color = nameColors[entity.getType()];
                                messageColor = messageColors[entity.getType()];
//                                Log.w(TAG, "3:" + messageColor);
                                break;
                            case LiveMessageEntity.MESSAGE_CLASS:
                                color = nameColors[entity.getType()];
                                messageColor = messageColors[entity.getType()];
//                                Log.w(TAG, "4:" + messageColor);
                                break;
                            default:
                                color = nameColors[0];
                                messageColor = messageColors[entity.getType()];
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
                        } else if (isOpenStimulation()) {
                            tvMessageItem.setAutoLinkMask(0);
                            SpannableString itemSpan;
                            SpannableString evenSpan = new SpannableString(EVEN_DRIVE_ICON);
                            itemSpan = addEvenDriveMessageNum(evenSpan, entity.getEvenNum(), entity.getType());
                            if (itemSpan != null) {
                                tvMessageItem.setText(itemSpan);
                                tvMessageItem.append(spanttt);
                            } else {
                                tvMessageItem.setText(spanttt);
                            }
                            tvMessageItem.append(entity.getText());
                            logger.i(tvMessageItem.getText());
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
        // initCommonWord();
        logger.i("initData:time4=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
    }


    private boolean commonWordInited = false;

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
                return new CommonWordChsItem(mContext, this);
            }
        });
        lvCommonWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = words.get(position);
                if (ircState.openchat()) {
                    if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                        boolean send = sendEvenDriveMessage(msg, "");
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
        commonWordInited = true;
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

                if (!commonWordInited) {
                    initCommonWord();
                }


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


                if (commonAction instanceof GiftDisable) {
                    //理科送礼物功能，主讲，辅导,都要先判断上课模式
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        if (!ircState.isOpenZJLKbarrage()) {
                            //主讲没有开启送礼物
                            ((GiftDisable) commonAction).clickIsnotOpenbarrage(ircState.getMode());
                            return;
                        }
                    } else {
                        if (!ircState.isOpenFDLKbarrage()) {
                            //辅导没有开启送礼物
                            ((GiftDisable) commonAction).clickIsnotOpenbarrage(ircState.getMode());
                            return;
                        }
                    }
                } else {
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        if (!ircState.isOpenbarrage()) {
                            commonAction.clickIsnotOpenbarrage();
                            return;
                        }
                    } else {
                        commonAction.clickTran();
                        return;
                    }
                }

//                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
//                    if (!ircState.isOpenbarrage()) {
//                        commonAction.clickIsnotOpenbarrage();
//                        return;
//                    }
//                } else {
//                    commonAction.clickTran();
//                    return;
//                }
                //小英
                if (smallChineseSendGiftPager != null) {
                    //添加到正中间
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                            .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    //如果giftPager的父布局为null才加入到decorView里面来
                    if (smallChineseSendGiftPager.getRootView().getParent() == null) {
                        decorView.addView(smallChineseSendGiftPager.getRootView(), layoutParams);
                    }
//                    mFlowerWindow.setBackgroundDrawable(dw);
//                    mFlowerWindow.setContentView(smallChineseSendGiftPager.getRootView());
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
                            boolean send = sendEvenDriveMessage(msg, "");
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
                int rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
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

    /** 聊天开始连接 */
    @Override
    public void onStartConnect() {
        logger.i("开始连接");
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
        if (decorView != null) {
            if (haveFlowers) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                decorView.addView(smallChineseSendGiftPager.getRootView(), layoutParams);
//                mFlowerWindow.showAtLocation(btMessageFlowers, Gravity.BOTTOM, 0, 0);
            } else {
                if (smallChineseSendGiftPager != null && smallChineseSendGiftPager.getRootView().getParent() == decorView) {
                    decorView.removeView(smallChineseSendGiftPager.getRootView());
                }

//                mFlowerWindow.dismiss();
            }
        }
    }

    /** 聊天连上 */
    @Override
    public void onConnect() {
        logger.i("聊天连接成功");
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
            onGetMyGoldDataEvent(goldNum);
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
        logger.i("聊天连接失败");
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
        smallChineseSendGiftPager = new SmallChineseSendGiftPager(mContext);
        //打开献花弹窗时的布局初始化
//        frameLayout = new FrameLayout(mContext);
        //80%透明
//        frameLayout.setBackgroundColor(0x000000);
//        frameLayout.setAlpha(0.6f);
//        frameLayout.setClickable(true);
//        frameLayout.addView(smallChineseSendGiftPager.getRootView());

        //设置点击赠送的监听器
//        smallChineseSendGiftPager.setSendFlowerListener(new SmallChineseSendGiftPager().SendFlowerListener()
//        {
//            @Override
//            public void onTouch () {
//        }
//        });
        //设置点击取消献花弹窗后的监听器
        smallChineseSendGiftPager.setListener(new SmallChineseSendGiftPager.GiftListaner() {
            @Override
            public void close() {
                //去掉背景色
                if (smallChineseSendGiftPager.getRootView().getParent() == decorView) {
                    decorView.removeView(smallChineseSendGiftPager.getRootView());
                }
            }

            @Override
            public void submit() {
                if (smallChineseSendGiftPager.isSelect()) {
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        if (ircState.isOpenbarrage()) {
                            String educationStage = "";
                            if (getInfo != null) {
                                educationStage = getInfo.getEducationStage();
                            }
                            int goldSend = 0;
                            if (smallChineseSendGiftPager.getWhich() == FLOWERS_SMALL) {
                                goldSend = 10;
                            } else if (smallChineseSendGiftPager.getWhich() == FLOWERS_MIDDLE) {
                                goldSend = 50;
                            } else if (smallChineseSendGiftPager.getWhich() == FLOWERS_BIG) {
                                goldSend = 100;
                            }
                            if (goldNum == null) {
                                OtherModulesEnter.requestGoldTotal(mContext);
                            } else {
                                try {
                                    int goldSum = Integer.parseInt(goldNum);
                                    if (goldSend > goldSum) {
                                        XESToastUtils.showToast(mContext, "当前金币余额不足");
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

//                            String educationStage = getInfo.getEducationStage();
                            String formWhichTeacher = LiveTopic.MODE_CLASS.equals(ircState.getMode()) ? "t" : "f";
                            ircState.praiseTeacher(formWhichTeacher, smallChineseSendGiftPager.getWhich() + "",
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
                                            addDanmaKuFlowers(smallChineseSendGiftPager
                                                    .getWhich(), getInfo.getStuName(), true);
                                            mView.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (smallChineseSendGiftPager != null && smallChineseSendGiftPager.getRootView().getParent() == decorView) {
                                                        decorView.removeView(smallChineseSendGiftPager.getRootView());
                                                    }
//                                                    mFlowerWindow.dismiss();
                                                }
                                            }, 1000);
                                        }

                                        @Override
                                        public void onPmFailure(Throwable error, String msg) {
                                            if (smallChineseSendGiftPager.getRootView() != null && smallChineseSendGiftPager.getRootView().getParent() == decorView) {
                                                decorView.removeView(smallChineseSendGiftPager.getRootView());
                                            }
                                        }

                                        @Override
                                        public void onPmError(ResponseEntity responseEntity) {
//                                            mFlowerWindow.dismiss();
                                            if (smallChineseSendGiftPager.getRootView() != null && smallChineseSendGiftPager.getRootView().getParent() == decorView) {
                                                decorView.removeView(smallChineseSendGiftPager.getRootView());
                                            }
                                        }
                                    });
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
        //测试时候使用
//        if (blTestSEBullet) {
//            liveThreadPoolExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    while (blTestSEBullet) {
//                        addDanmaKuFlowers(FLOWERS_SMALL, "订好了就不改了", isGuest);
//
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        addDanmaKuFlowers(FLOWERS_MIDDLE, "订好了就不改了", isGuest);
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        addDanmaKuFlowers(FLOWERS_BIG, "订好了就不改了", isGuest);
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        isGuest = !isGuest;
//                    }
//                }
//            });
//        }

        logger.i("initFlower:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();


        logger.i("initFlower:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();

        logger.i("initFlower:time3=" + (System.currentTimeMillis() - before));
        if (getInfo != null && getInfo.getIsAllowTeamPk() != null && ("1").equals(getInfo.getIsAllowTeamPk())) {
            if (ivPkBackGround != null) {
                ivPkBackGround.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_team_pk_background_icon));
            }
        }
    }

//    private boolean isGuest = false;
    //测试使用的布尔值，用来控制无限发送弹幕
//    private boolean blTestSEBullet = true;

    /** 移除View */
    private void removeView(View view, ViewGroup viewGroup) {
        if (view != null && viewGroup != null && view.getParent() == viewGroup) {
            viewGroup.removeView(view);
        }
    }

    FlowerAction commonAction = new GiftDisable();

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


        //重载，添加参数：讲课模式
        public void clickIsnotOpenbarrage(String classMode) {
            String teacher = LiveTopic.MODE_CLASS.equals(classMode) ? "主讲" : "辅导";
            XESToastUtils.showToast(mContext, teacher + "老师未开启送礼物功能");
        }


        @Override
        public void clickIsnotOpenbarrage() {

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
            String msg = name + "：" + tip;
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
        if (smallChineseSendGiftPager != null) {
            smallChineseSendGiftPager.onGetMyGoldDataEvent(goldNum);
        }
    }


    @Override
    public void onUserList(String channel, final User[] users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
                    tvOnlineNum.setText(String.valueOf(peopleCount));
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnlineNum.setText(String.valueOf(peopleCount));
                    } else {
                        tvOnlineNum.setText(String.valueOf(peopleCount));
                    }
                }
            }
        });
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        logger.e("=====>onMessage called");
        if (sender.startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
            sender = "主讲老师";
        } else if (sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
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
                        addEvenDriveMessage(jsonObject.getString("name"), LiveMessageEntity.MESSAGE_CLASS, jsonObject
                                        .getString("msg"), "",
                                jsonObject.optString("evenexc"));
                    } else if (type == XESCODE.FLOWERS) {
                        //{"ftype":2,"name":"林玉强","type":"110"}
                        addDanmaKuFlowers(jsonObject.getInt("ftype"), jsonObject.getString("name"), false);
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
                    tvOnlineNum.setText(String.valueOf(peopleCount));
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnlineNum.setText(String.valueOf(peopleCount));
                    } else {
                        tvOnlineNum.setText(String.valueOf(peopleCount));
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
                    tvOnlineNum.setText(String.valueOf(peopleCount));
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnlineNum.setText(String.valueOf(peopleCount));
                    } else {
                        tvOnlineNum.setText(String.valueOf(peopleCount));
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
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage,
                                    boolean isFDLKOpenbarrage) {

    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }



    @Override
    public void onOtherDisable(String id, String name, boolean disable) {

    }

    @Override
    public void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isShow) {

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
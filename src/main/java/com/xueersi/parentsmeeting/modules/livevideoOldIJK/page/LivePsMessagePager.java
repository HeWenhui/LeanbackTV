package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.CommonWordPsItem;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.HotWordLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * Created by David on 2018/8/1.
 */

public class LivePsMessagePager extends BasePrimaryScienceMessagePager {
    private static String TAG = "LivePsMessagePager";
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
    private TextView tvMessageCount;
    /** 聊天IRC一下状态，正在连接，在线等 */
    private ImageView ivMessageOnline;
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
    /** 献花 */
    private PopupWindow mFlowerWindow;
    private View flowerContentView;
    private TextView tvMessageGoldLable;
    private TextView tvMessageGold;
    private String goldNum;
    /** 上次发送消息时间 */
    private long lastSendMsg;
    private BaseLiveMediaControllerBottom liveMediaControllerBottom;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    private ImageView ivExpressionCancle;
    private Activity liveVideoActivity;
    private QuestionBll questionBll;
    /** 竖屏的时候，也添加横屏的消息 */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;
    LiveAndBackDebug liveAndBackDebug;
    private String liveId;
    private String termId;
    private View mFloatView;
    private PopupWindow mPopupWindow;
    private long mOldTime = 0;//记录点击赠送按钮那一刻的时间
    private ArrayList<FlowerEntity> mFlowerEntities;
    private ImageView mIce;
    private ImageView mCup;
    private ImageView mHeart;
    private Boolean first = false;

    public LivePsMessagePager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener, LiveAndBackDebug ums, BaseLiveMediaControllerBottom
            liveMediaControllerBottom, ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        this.keyboardShowingListener = keyboardShowingListener;
        this.liveAndBackDebug = new ContextLiveAndBackDebug(context);
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;
        Resources resources = context.getResources();
        nameColors[0] = resources.getColor(R.color.COLOR_FFFFFF);
        nameColors[1] = resources.getColor(R.color.COLOR_E74C3C);
        nameColors[2] = resources.getColor(R.color.COLOR_20ABFF);


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
    }

    @Override
    public void onFDOpenbarrage(final boolean openbarrage, final boolean fromNotice) {
        Loger.i("mqtt", ircState.getMode() + "老师" + openbarrage + "了献花 fromNotice = " + fromNotice + " ircState.getLKNoticeMode() = " + ircState.getLKNoticeMode());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                if (commonAction instanceof GiftDisable) {
                    Loger.i("mqtt", "理科");
                    if (!ircState.getMode().equals(ircState.getLKNoticeMode())) {
                        Loger.i("mqtt", "当前mode和notimode不一致，不再响应提示，但要根据当前mode改变礼物/鲜花状态");
                        //理科辅导送礼物功能
                        setOnlyFlowerIconState(openbarrage, fromNotice, ircState.getMode());
                        return;
                    }
                    Loger.i("mqtt", "理科当前mode和notimode一致，响应提示，改变当前mode改变礼物/鲜花状态");
                    //理科辅导送礼物功能
                    setFlowerIconState(openbarrage, fromNotice, ircState.getMode());
                } else {
                    //非理科辅导的走原来的逻辑
                    Loger.i("mqtt", "文科");
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        setFlowerIconState(openbarrage, fromNotice, null);
                    }
                }

            }
        });

    }

    /**
     * 理科，主讲和辅导切换的时候，给出提示（切流）
     *
     * @param oldMode
     * @param newMode
     * @param isShowNoticeTips  为false的时候，默认显示"已切换到 主讲/辅导模式"
     * @param iszjlkOpenbarrage
     * @param isFDLKOpenbarrage
     */
    @Override
    public void onTeacherModeChange(String oldMode, String newMode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage, boolean isFDLKOpenbarrage) {
        //理科辅导送礼物功能
        Loger.i("yzl_fd", "onTeacherModeChange 切流，使送礼物面板消失");
        if (LiveTopic.MODE_CLASS.equals(oldMode) && iszjlkOpenbarrage) {
            //主讲老师是开启状态，切辅导，提醒“已切换到主讲/辅导”
            Loger.i("yzl_fd", "主讲老师是开启状态，切辅导，提醒“已切换到" + newMode);
            setFlowerWindowDismiss(newMode, isShowNoticeTips);
            return;
        }
        if (LiveTopic.MODE_TRANING.equals(oldMode) && isFDLKOpenbarrage) {
            //辅导老师是开启状态，切主讲，提醒“已切换到主讲/辅导”
            Loger.i("yzl_fd", "主讲老师是开启状态，切辅导，提醒“已切换到" + newMode);
            setFlowerWindowDismiss(newMode, isShowNoticeTips);
            return;
        }

        if (LiveTopic.MODE_CLASS.equals(oldMode) && !iszjlkOpenbarrage) {
            //主讲老师是关闭状态，切辅导
            if (isFDLKOpenbarrage) {
                //如果辅导是开启，提醒：“辅导老师开启了礼物功能”；如果辅导是关闭，不做提醒
                Loger.i("yzl_fd", "如果辅导是开启，提醒：“辅导老师开启了礼物功能”；如果辅导是关闭，不做提醒newMode =" + newMode + " isFDLKOpenbarrage = " + isFDLKOpenbarrage);
                showLKTipsWhenOldModeCloseLW(newMode, isFDLKOpenbarrage);

            }
            return;
        }
        if (LiveTopic.MODE_TRANING.equals(oldMode) && !isFDLKOpenbarrage) {
            //辅导老师是关闭状态，切主讲
            if (iszjlkOpenbarrage) {
                //如果主讲是开启，提醒：“主讲老师开启了礼物功能”；如果主讲是关闭，不做提醒
                Loger.i("yzl_fd", "如果主讲是开启，提醒：“主讲老师开启了礼物功能”；如果主讲是关闭，不做提醒newMode =" + newMode + " iszjlkOpenbarrage = " + iszjlkOpenbarrage);
                showLKTipsWhenOldModeCloseLW(newMode, iszjlkOpenbarrage);
            }
            return;
        }
    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }

    private void showLKTipsWhenOldModeCloseLW(final String newMode, final boolean isOpenbarrage) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (commonAction instanceof GiftDisable) {

                    if (mFlowerWindow != null) {
                        mFlowerWindow.dismiss();
                    }

                    ((GiftDisable) commonAction).onOpenbarrage(isOpenbarrage, newMode);
                }

            }
        });
    }

    /**
     * 根据送 礼物/鲜花 开关来设置改icon的显示状态
     *
     * @param openbarrage
     * @param fromNotice
     * @param classMode
     */
    private void setOnlyFlowerIconState(boolean openbarrage, boolean fromNotice, String classMode) {
        if (openbarrage) {
            btMessageFlowers.setTag("1");
            btMessageFlowers.setAlpha(1.0f);
            btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
        } else {
            btMessageFlowers.setTag("0");
            btMessageFlowers.setAlpha(0.4f);
            btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
        }
    }

    /**
     * 根据送 礼物/鲜花 开关来设置改icon的显示状态和toast提示
     *
     * @param openbarrage
     * @param fromNotice
     * @param classMode   学科类型
     */
    private void setFlowerIconState(boolean openbarrage, boolean fromNotice, String classMode) {
        if (openbarrage) {
            if (fromNotice) {
                if (commonAction instanceof GiftDisable) {
                    //理科不区分主讲辅导
                    ((GiftDisable) commonAction).onOpenbarrage(true, classMode);

                } else {
                    commonAction.onOpenbarrage(true);
                }

            }
            btMessageFlowers.setTag("1");
            btMessageFlowers.setAlpha(1.0f);
            btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
        } else {
            if (fromNotice) {
                if (commonAction instanceof GiftDisable) {
                    //理科不区分主讲辅导
                    ((GiftDisable) commonAction).onOpenbarrage(false, classMode);
                    //只在理科，关闭鲜花的时候，去关闭鲜花面板，如果他还在显示的话
                    setFlowerWindowDismiss(classMode, true);
                } else {
                    commonAction.onOpenbarrage(false);
                }

            }
            btMessageFlowers.setTag("0");
            btMessageFlowers.setAlpha(0.4f);
            btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
        }
    }

    /**
     * 使送礼物面板消失
     *
     * @param mode
     * @param notShowTips
     */
    private void setFlowerWindowDismiss(final String mode, final boolean notShowTips) {
        //切记，此方法是在子线程中回调的，不会报错，但是mFlowerWindow就是无法消失，其方法返回的值都是错误的，无效的，方法调用也无效
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (commonAction instanceof GiftDisable) {
                    Loger.i("yzl_fd", "切流，使送礼物面板消失");
                    if (mFlowerWindow != null) {
                        mFlowerWindow.dismiss();
                    }
                    if (notShowTips) {
                        Loger.i("yzl_fd", "老师关闭了鲜花，切流，使送礼物面板消失,但不提示，切换主讲，辅导");
                        return;
                    }
                    ((GiftDisable) commonAction).changeTeacherMode(mode);
                }

            }
        });


    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_psmessage, null);
        tvMessageCount = (TextView) mView.findViewById(R.id.tv_livevideo_message_count);
        ivMessageOnline = (ImageView) mView.findViewById(R.id.iv_livevideo_message_online);
        lvMessage = (ListView) mView.findViewById(R.id.lv_livevideo_message);
        dvMessageDanmaku = mView.findViewById(R.id.dv_livevideo_message_danmaku);
        rlInfo = mView.findViewById(R.id.rl_livevideo_info);
        rlMessageContent = mView.findViewById(R.id.rl_livevideo_message_content2);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_message_send);
        switchFSPanelLinearLayout = (KPSwitchFSPanelLinearLayout) mView.findViewById(R.id
                .rl_livevideo_message_panelroot);
        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int hradio = (int) ((LiveVideoConfig.VIDEO_HEIGHT - LiveVideoConfig.VIDEO_HEAD_HEIGHT) * screenHeight /
                LiveVideoConfig.VIDEO_HEIGHT);
        params.width = wradio;
        params.topMargin = screenHeight - hradio;
        return mView;
    }

    @Override
    public void initListener() {
        rlLivevideoCommonWord = (RelativeLayout) liveMediaControllerBottom.findViewById(R.id.rl_livevideo_common_word);
//        int screenWidth = ScreenUtils.getScreenWidth();
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cbMessageClock.getLayoutParams();
//        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
//        params.rightMargin = wradio;
//        LayoutParamsUtil.setViewLayoutParams(cbMessageClock, params);
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
                if(!first){
                    initCommonWord();
                    first = true;
                }
                liveMediaControllerBottom.onChildViewClick(v);
                LiveMediaController controller = liveMediaControllerBottom.getController();
                controller.show();
                if (rlLivevideoCommonWord.getVisibility() == View.VISIBLE) {
                    rlLivevideoCommonWord.setVisibility(View.GONE);
                    return;
                }
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                //在控件上方显示
                logger.i( "onClick:Width=" + rlLivevideoCommonWord.getWidth() + ",Height=" + rlLivevideoCommonWord.getHeight());
                rlLivevideoCommonWord.setVisibility(View.VISIBLE);
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_livevideo_common_word.getLayoutParams();
//                int left = (location[0] + v.getWidth() / 2) - rl_livevideo_common_word.getWidth() / 2;
//                if (lp.leftMargin == left) {
//                    return;
//                }
//                lp.leftMargin = (location[0] + v.getWidth() / 2) - rl_livevideo_common_word.getWidth() / 2;
//                rl_livevideo_common_word.setLayoutParams(lp);
//                rl_livevideo_common_word.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        rl_livevideo_common_word.getViewTreeObserver().removeOnPreDrawListener(this);
//                        int[] location = new int[2];
//                        v.getLocationOnScreen(location);
//                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_livevideo_common_word.getLayoutParams();
//                        int left = (location[0] + v.getWidth() / 2) - rl_livevideo_common_word.getWidth() / 2;
//                        if (lp.leftMargin == left) {
//                            return false;
//                        }
//                        lp.leftMargin = (location[0] + v.getWidth() / 2) - rl_livevideo_common_word.getWidth() / 2;
//                        rl_livevideo_common_word.setLayoutParams(lp);
//                        logger.i( "onClick2:Width=" + rl_livevideo_common_word.getWidth() + ",Height=" + rlLivevideoCommonWord.getHeight());
//                        return true;
//                    }
//                });
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
                mFlowerWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
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
                logger.i( "onClick:time=" + (System.currentTimeMillis() - lastSendMsg));
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
                            //暂时去掉3秒发言，信息提示z
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
                        logger.i( "onKeyboardShowing:isShowing=" + isShowing);
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

    int c = 0;

    @Override
    public void initData() {
        long before = System.currentTimeMillis();
        super.initData();
        logger.i( "initData:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                LiveIRCMessageBll.requestGoldTotal(mContext);
            }
        }.start();
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
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);
        logger.i( "initData:minisize=" + minisize);
        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities) {
            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                return new AdapterItemInterface<LiveMessageEntity>() {
                    TextView tvMessageItem;

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_livevideo_psmessage;
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
                        // 将字体文件保存在assets/fonts/目录下，在程序中通过如下方式实例化自定义字体：
                        Typeface typeFace = FontCache.getTypeface(liveVideoActivity, "fangzhengcuyuan.ttf");
                        // 应用字体
                        tvMessageItem.setTypeface(typeFace);
                        if (LiveMessageEntity.MESSAGE_MINE == entity.getType()) {
                            tvMessageItem.setTextColor(0xffffffff);
                        } else {
                            tvMessageItem.setTextColor(0x7Fffffff);
                        }
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
        logger.i( "initData:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mView.post(new Runnable() {
            @Override
            public void run() {
                initDanmaku();
            }
        });
        logger.i( "initData:time3=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        logger.i( "initData:time4=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
    }

    @Override
    public void setGetInfo(LiveGetInfo getInfo) {
        super.setGetInfo(getInfo);
        if (getInfo != null) {
            String educationStage = getInfo.getEducationStage();
            initPrimaryFlower();
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    OtherModulesEnter.requestGoldTotal(mContext);
                }
            });
        }
    }

    private void initPrimaryFlower() {
        long before = System.currentTimeMillis();
        mFlowerEntities = new ArrayList<>();

        commonAction = new GiftDisable();
        flowsDrawTips[0] = R.drawable.primarypresentheart;
        flowsDrawTips[1] = R.drawable.primarypresentcup;
        flowsDrawTips[2] = R.drawable.primarypresentirc;

        flowsDrawLittleTips[0] = R.drawable.primarypresentheart;
        flowsDrawLittleTips[1] = R.drawable.primarypresentcup;
        flowsDrawLittleTips[2] = R.drawable.primarypresentirc;

        flowsTips[0] = "送老师一颗小心心，老师也喜欢你哟~";
        flowsTips[1] = "送老师一杯暖心茉莉茶，老师嗓子好舒服~";
        flowsTips[2] = "送老师一个冰淇淋，夏天好凉爽~";
        mFlowerEntities.add(new FlowerEntity(FLOWERS_SMALL, flowsDrawTips[0], "小心心", 10));
        mFlowerEntities.add(new FlowerEntity(FLOWERS_MIDDLE, flowsDrawTips[1], "暖心茉莉茶", 50));
        mFlowerEntities.add(new FlowerEntity(FLOWERS_BIG, flowsDrawTips[2], "冰淇淋", 100));
        flowerContentView = View.inflate(mContext, R.layout.pop_livevideo_message_primary_flower, null);
        PopupWindow flowerWindow = new PopupWindow(flowerContentView, dp2px(liveVideoActivity, 478), dp2px(liveVideoActivity, 347), false);
        flowerWindow.setBackgroundDrawable(new BitmapDrawable());
        flowerWindow.setOutsideTouchable(true);
        flowerWindow.setFocusable(true);
        flowerWindow.setContentView(flowerContentView);
        flowerWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isHaveFlowers = false;
            }
        });
        tvMessageGold = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold);
        tvMessageGoldLable = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold_lable);
        logger.i( "initFlower:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mFlowerWindow = flowerWindow;
        logger.i( "initFlower:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        Button flowerSend = flowerContentView.findViewById(R.id.bt_livevideo_message_flowersend);
        mIce = flowerContentView.findViewById(R.id.iv_present_ice);
        mCup = flowerContentView.findViewById(R.id.iv_present_cup);
        mHeart = flowerContentView.findViewById(R.id.iv_present_heart);
        ImageView close = flowerContentView.findViewById(R.id.iv_livevideo_present_close);
        RelativeLayout rl_heart = flowerContentView.findViewById(R.id.rl_heart);
        RelativeLayout rl_cup = flowerContentView.findViewById(R.id.rl_cup);
        RelativeLayout rl_ice = flowerContentView.findViewById(R.id.rl_ice);
        // 默认选中第一个礼物
        flowerContentView.setTag(mFlowerEntities.get(0));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.rl_heart) {
                    flowerContentView.setTag(mFlowerEntities.get(0));
                    mIce.setVisibility(View.GONE);
                    mCup.setVisibility(View.GONE);
                    mHeart.setVisibility(View.VISIBLE);
                } else if (v.getId() == R.id.rl_cup) {
                    flowerContentView.setTag(mFlowerEntities.get(1));
                    mIce.setVisibility(View.GONE);
                    mCup.setVisibility(View.VISIBLE);
                    mHeart.setVisibility(View.GONE);
                } else if (v.getId() == R.id.rl_ice) {
                    flowerContentView.setTag(mFlowerEntities.get(2));
                    mIce.setVisibility(View.VISIBLE);
                    mCup.setVisibility(View.GONE);
                    mHeart.setVisibility(View.GONE);
                } else if (v.getId() == R.id.iv_livevideo_present_close) {
                    mFlowerWindow.dismiss();
                    flowerContentView.setTag(mFlowerEntities.get(0));
                    mIce.setVisibility(View.GONE);
                    mCup.setVisibility(View.GONE);
                    mHeart.setVisibility(View.VISIBLE);
                }
            }
        };
        rl_heart.setOnClickListener(listener);
        rl_cup.setOnClickListener(listener);
        rl_ice.setOnClickListener(listener);
        close.setOnClickListener(listener);
//        rl_heart.setOnClickListener(new ON);
//        flowerSend.setText(commonAction.getFlowerSendText());
        flowerSend.setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                long curTime = System.currentTimeMillis();
                if (commonAction instanceof GiftDisable) {
                    if (mOldTime > 0 && (curTime - mOldTime) / 1000 <= 10) {
                        Loger.i("yzl_fd", "理科 ，10s之内不让重复点击");
                        return;
                    }
                } else {
                    if (mOldTime > 0 && (curTime - mOldTime) / 1000 <= 1) {
                        Loger.i("yzl_fd", "文科，1s之内不让重复点击 ");
                        return;
                    }
                }
                mOldTime = System.currentTimeMillis();
                final FlowerEntity entity = (FlowerEntity) flowerContentView.getTag();
                if (entity != null) {

                    if (goldNum == null || Integer.parseInt(goldNum) <= 0 || Integer.parseInt(goldNum) < entity.getGold()) {
                        XESToastUtils.showToast(mContext, "您的金币不足啦");
                        return;
                    }
                    if (commonAction instanceof GiftDisable) {
                        startCountDownFlowerIcon();
                        String formWhichTeacher = LiveTopic.MODE_CLASS.equals(ircState.getMode()) ? "t" : "f";
                        ////理科点击赠送的逻辑
                        Loger.i("yzl_fd", "理科，不区分主讲，点击了赠送，开始10s倒计时 formWhichTeacher = " + formWhichTeacher);
                        logicForOpenbarrageLike(entity, formWhichTeacher);
                    } else {
                        //文科点击赠送的逻辑
                        logicForChOnClickSendFlowerBt(entity);
                    }

                } else {
                    commonAction.clickNoChoice();
                }
            }
        });
//        flowerWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        flowerWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        logger.i( "initFlower:time3=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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
                return new CommonWordPsItem(mContext, this);
            }
        });
        lvCommonWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = words.get(position);
                upLoadHotWordLog(msg);
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

    /**
     * 热词埋点日志
     * @param hotwordCmd  热词指令
     */
    private void upLoadHotWordLog(String hotwordCmd) {
        try {
            //非语文半身直播  上传热词埋点数据
            if(getInfo != null && getInfo.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY && getInfo.getUseSkin()
                    != HalfBodyLiveConfig.SKIN_TYPE_CH){
                HotWordLog.hotWordSend(this.liveAndBackDebug,hotwordCmd,HotWordLog.LIVETYPE_PRESCHOOL,
                        getInfo.getStudentLiveInfo().getClassId(),getInfo.getStudentLiveInfo().getTeamId(),getInfo.getStudentLiveInfo().getCourseId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable) {
        return commonAction.createSpannable(ftype, name, drawable);
    }

    /**
     * 文科点击赠送按钮的逻辑
     *
     * @param entity
     */
    private void logicForChOnClickSendFlowerBt(final FlowerEntity entity) {
        if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
            logicForOpenbarrage(entity);
        } else {
            commonAction.clickTran();
        }
    }

    /**
     * 理科，当老师开启了送花或者送礼物开关后，点击赠送按钮之后的逻辑处理
     *
     * @param entity
     * @param formWhichTeacher
     */
    private void logicForOpenbarrageLike(final FlowerEntity entity, String formWhichTeacher) {
        //主讲或者辅导有任一个开启了献花
        if (ircState.isOpenZJLKbarrage() || ircState.isOpenFDLKbarrage()) {
            String educationStage = getInfo.getEducationStage();
            ircState.praiseTeacher(formWhichTeacher, entity.getFtype() + "", educationStage, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    if (goldNum == null) {
                        OtherModulesEnter.requestGoldTotal(mContext);
                    } else {
                        if (responseEntity.getJsonObject() instanceof JSONObject) {
                            try {
                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                int gold = Integer.parseInt(goldNum);
                                goldNum = ("" + (gold - jsonObject.getInt("gold")));
                                if (Integer.parseInt(goldNum) <= 0) {
                                    XESToastUtils.showToast(mContext, "您的金币不足啦");
                                    return;
                                }
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
                            flowerContentView.setTag(mFlowerEntities.get(0));
                            mIce.setVisibility(View.GONE);
                            mCup.setVisibility(View.GONE);
                            mHeart.setVisibility(View.VISIBLE);
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
            if (commonAction instanceof GiftDisable) {
                //理科要区分主讲和辅导，谁没有开启送礼物
                ((GiftDisable) commonAction).clickIsnotOpenbarrage(ircState.getMode());
            } else {
                commonAction.clickIsnotOpenbarrage();
            }

        }
    }

    /**
     * 文科，当老师开启了送花或者送礼物开关后，点击赠送按钮之后的逻辑处理
     *
     * @param entity
     */
    private void logicForOpenbarrage(final FlowerEntity entity) {
        if (ircState.isOpenbarrage()) {
            String educationStage = getInfo.getEducationStage();
            ircState.praiseTeacher(ircState.getMode(), entity.getFtype() + "", educationStage, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    if (goldNum == null) {
                        OtherModulesEnter.requestGoldTotal(mContext);
                    } else {
                        if (responseEntity.getJsonObject() instanceof JSONObject) {
                            try {
                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                int gold = Integer.parseInt(goldNum);
                                goldNum = ("" + (gold - jsonObject.getInt("gold")));
                                if (Integer.parseInt(goldNum) <= 0) {
                                    XESToastUtils.showToast(mContext, "您的金币不足啦");
                                    return;
                                }
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
            if (commonAction instanceof GiftDisable) {
                //理科要区分主讲和辅导，谁没有开启送礼物
                ((GiftDisable) commonAction).clickIsnotOpenbarrage(ircState.getMode());
            } else {
                commonAction.clickIsnotOpenbarrage();
            }

        }
    }

    /**
     * 开始 礼物/鲜花 的倒计时 10秒后恢复 礼物/鲜花 icon
     */
    private void startCountDownFlowerIcon() {
        CountDownTimer countDownTimer = new CountDownTimer(12000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (btMessageFlowers == null) {
                    //防止在倒计时结束前，当前界面销毁，而倒计时还在回调此方法，控件已经为空，发生空指针的问题
                    return;
                }
                btMessageFlowers.setEnabled(false);//倒计时的时候不可点击
                btMessageFlowers.setAlpha(0.4f);
                btMessageFlowers.setText(millisUntilFinished / 1000 + "");
                btMessageFlowers.setTextColor(Color.WHITE);
                btMessageFlowers.setBackgroundResource(R.drawable.shape_oval_black);

            }

            @Override
            public void onFinish() {
                if (btMessageFlowers == null) {
                    //防止在倒计时结束前，当前界面销毁，而倒计时还在回调此方法，控件已经为空，发生空指针的问题
                    return;
                }
                //只有理科才有倒计时的逻辑
                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                    if (ircState.isOpenZJLKbarrage()) {
                        setFlowerHalfAlpha(1.0f);
                    } else {
                        setFlowerHalfAlpha(0.4f);
                    }
                } else {
                    if (ircState.isOpenFDLKbarrage()) {
                        setFlowerHalfAlpha(1.0f);
                    } else {
                        setFlowerHalfAlpha(0.4f);
                    }
                }
                btMessageFlowers.setEnabled(true);//不管献花icon的透明度为多少，都要求改献花icon能够点击

            }
        };
        countDownTimer.start();
    }

    /**
     * 设置礼物icon的透明度
     *
     * @param alpha
     */
    private void setFlowerHalfAlpha(float alpha) {
        btMessageFlowers.setAlpha(alpha);
        btMessageFlowers.setText("");
        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
    }

    public void onTitleShow(boolean show) {
        if (rlMessageContent.getVisibility() != View.GONE) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
            rlMessageContent.setVisibility(View.GONE);
        }
        switchFSPanelLinearLayout.setVisibility(View.GONE);
    }

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
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
            }
        });
    }

    @Override
    public void setIsRegister(boolean isRegister) {
        super.setIsRegister(isRegister);
        if (isRegister) {
            ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
        } else {
            ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
        }
    }

    @Override
    public void setHaveFlowers(boolean haveFlowers) {
        super.setHaveFlowers(haveFlowers);
        if (mFlowerWindow != null) {
            if (haveFlowers) {
                mFlowerWindow.showAtLocation(btMessageFlowers, Gravity.BOTTOM, 0, 0);
            } else {
                mFlowerWindow.dismiss();
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
//                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, getInfo.getTeacherId() + "_" + getInfo.getId());
//                }
                if (!isRegister) {
                    ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
                }
            }
        });
    }

    // 03.16 设置模拟的聊天连接
    public void onConnects() {
        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
        ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
    }

    /** 聊天进入房间 */
    @Override
    public void onRegister() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = true;
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
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
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
            }
        });
    }

    @Override
    public void onUserList(String channel, final User[] users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
//                    tvMessageCount.setText("班内" + peopleCount + "人");
                    tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                } else {
                    if (ircState.isHaveTeam()) {
//                        tvMessageCount.setText("组内" + peopleCount + "人");
                        tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                    } else {
//                        tvMessageCount.setText(peopleCount + "人正在上课");
                        tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                    }
                }
            }
        });
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        Loger.e("LiveMessagerPager", "=====>onMessage called");
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
                    tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                    } else {
                        tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
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
                    tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                    } else {
                        tvMessageCount.setText("本班在线 " + "( " + peopleCount + " )");
                    }
                }
            }
        });
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

    }

    public void setLiveTermId(String liveId, String termId) {
        this.liveId = liveId;
        this.termId = termId;
    }

    /** 被禁言 */
    public void onDisable(final boolean disable, final boolean fromNotice) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    XESToastUtils.showToast(mContext, "你被老师禁言了");
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
                if (commonAction != null && commonAction instanceof GiftDisable) {
                    if (ircState.isOpenZJLKbarrage() || ircState.isOpenFDLKbarrage()) {
                        btMessageFlowers.setTag("1");
                        btMessageFlowers.setAlpha(1.0f);
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                    } else {
                        btMessageFlowers.setTag("0");
                        btMessageFlowers.setAlpha(0.4f);
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers);
                    }
                    Loger.i("yzl_fd", "理科，不区分主讲，onModeChange不再往下执行");
                    return;
                }

                // 主讲模式可以献花
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    Loger.i("yzl_fd", "文科，区分主讲，onModeChange");
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

    /** 关闭开启送花 */
    public void onOpenbarrage(final boolean openbarrage, final boolean fromNotice) {
        Loger.i("yzl_fd", ircState.getMode() + "老师" + openbarrage + "了献花 fromNotice = " + fromNotice + " liveBll.getLKNoticeMode()" + ircState.getLKNoticeMode());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                if (commonAction instanceof GiftDisable) {
                    Loger.i("yzl_fd", "理科");

                    if (!ircState.getMode().equals(ircState.getLKNoticeMode())) {
                        Loger.i("yzl_fd", "理科当前mode和notimode不一致，不再响应提示，但要根据当前mode改变礼物/鲜花状态");
                        //理科主讲送礼物功能
                        setOnlyFlowerIconState(openbarrage, fromNotice, ircState.getMode());
                        return;
                    }
                    Loger.i("yzl_fd", "理科当前mode和notimode一致，响应提示，改变当前mode改变礼物/鲜花状态");
                    //理科主讲送礼物功能
                    setFlowerIconState(openbarrage, fromNotice, ircState.getMode());

                } else {
                    Loger.i("yzl_fd", "文科");
                    //非理科主讲的走原来的逻辑
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        setFlowerIconState(openbarrage, fromNotice, null);
                    }
                }

            }
        });
    }

    @Override
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

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
            return LivePsMessagePager.super.createSpannable(ftype, name, drawable);
        }

        @Override
        public String getFlowerSendText() {
            return "发送";
        }
    }

    class GiftDisable implements FlowerAction {
        //重载，添加参数：讲课模式
        public void onOpenbarrage(boolean openbarrage, String classMode) {
            String teacher = LiveTopic.MODE_CLASS.equals(classMode) ? "主讲" : "辅导";
            if (openbarrage) {
                XESToastUtils.showToast(mContext, teacher + "老师开启了送礼物功能");
            } else {
                XESToastUtils.showToast(mContext, teacher + "老师关闭了送礼物功能");
            }
        }

        public void changeTeacherMode(String classMode) {
            String teacher = LiveTopic.MODE_CLASS.equals(classMode) ? "主讲" : "辅导";
            XESToastUtils.showToast(mContext, "已切换到" + teacher + "老师");

        }

        //重载，添加参数：讲课模式
        public void clickIsnotOpenbarrage(String classMode) {
            String teacher = LiveTopic.MODE_CLASS.equals(classMode) ? "主讲" : "辅导";
            XESToastUtils.showToast(mContext, teacher + "老师未开启送礼物功能");
        }

        @Override
        public void onOpenbarrage(boolean openbarrage) {

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
            SpannableStringBuilder spannable = new TypeSpannableStringBuilder(msg, name, ftype);
            spannable.append(msg).append("   ");
            ImageSpan imgSpan = new VerticalImageSpan(drawable);
            spannable.setSpan(imgSpan, 0, msg.length(), imgSpan.ALIGN_BASELINE);
            return spannable;
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
                            Loger.e(BaseApplication.getContext(), TAG, "" + mContext + "," + sender + "," + type, e, true);
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
        Loger.e("Duncan", "sender:" + sender);
    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {
        this.otherMessageAdapter = otherMessageAdapter;
    }

    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
        tvMessageGold.setText(goldNum);
        tvMessageGold.setVisibility(View.VISIBLE);
        tvMessageGoldLable.setVisibility(View.VISIBLE);
        View view = flowerContentView.findViewById(R.id.tv_livevideo_message_gold_word);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }


    // 03.16 模拟显示聊天人数
    public void showPeopleCount(int num) {
        tvMessageCount.setText(num + "人正在上课");
    }

    // 隐藏锁屏按钮
    public void hideclock() {
        cbMessageClock.setVisibility(View.GONE);
    }

    public Drawable getHeard(int type) {
        Drawable drawable;
        drawable = mContext.getResources().getDrawable(flowsDrawLittleTips[type]);
        return drawable;
    }

    public String getTips(int type) {
        StringBuffer tip = new StringBuffer();
        tip.append(getInfo.getStuName());
        tip.append(flowsTips[type]);
        return tip.toString();
    }
}

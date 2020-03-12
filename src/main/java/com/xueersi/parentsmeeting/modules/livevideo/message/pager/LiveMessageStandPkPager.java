package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.airbnb.lottie.AssertUtil;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.FlowerItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.UserGoldTotal;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.StandLiveMessOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.StandLiveMessSysItem;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.widget.button.CompoundButtonGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * @author linyuqiang
 * @date 2016/8/2
 * 直播聊天横屏-直播课和直播辅导
 */
public class LiveMessageStandPkPager extends BaseLiveMessagePager {
    private String TAG = getClass().getSimpleName();
    /** 聊天，默认开启 */
    private Button btMesOpen;
    /** 聊天输入框的关闭按钮 */
    private ImageView ivMessageClose;
    /** 左侧聊天区 */
    private View liveStandMessageContent;
    FrameAnimation btMesOpenAnimation;
    /** 献花，默认关闭 第一版没有这功能 */
    private Button btMessageFlowers;
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
    private ImageView ivExpressionCancle;
    private Activity liveVideoActivity;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    /** 竖屏的时候，也添加横屏的消息 */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;
    /** 是不是正在答题 */
    private boolean isAnaswer = false;
    LiveSoundPool liveSoundPool;

    public LiveMessageStandPkPager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                   BaseLiveMediaControllerBottom
                                           liveMediaControllerBottom, ArrayList<LiveMessageEntity> liveMessageEntities,
                                   ArrayList<LiveMessageEntity>
                                           otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        this.keyboardShowingListener = keyboardShowingListener;
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;
        Resources resources = context.getResources();
        nameColors[0] = resources.getColor(R.color.COLOR_32B16C);
        nameColors[1] = resources.getColor(R.color.COLOR_E74C3C);
        nameColors[2] = resources.getColor(R.color.COLOR_20ABFF);

//        btMesOpen = liveMediaControllerBottom.getBtMesOpen();
        btMessageFlowers = liveMediaControllerBottom.getBtMessageFlowers();
//        cbMessageClock = liveMediaControllerBottom.getCbMessageClock();
        SYSTEM_TIP = "爱豆";
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                initListener();
                initData();
            }
        });
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_message_stand_pk, null);
        tvMessageCount = (TextView) mView.findViewById(R.id.tv_livevideo_message_count);
        ivMessageOnline = (ImageView) mView.findViewById(R.id.iv_livevideo_message_online);
        lvMessage = (ListView) mView.findViewById(R.id.lv_livevideo_message);
        rlInfo = mView.findViewById(R.id.rl_livevideo_info);
        rlMessageContent = mView.findViewById(R.id.rl_livevideo_message_content2);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_message_send);
        switchFSPanelLinearLayout = (KPSwitchFSPanelLinearLayout) mView.findViewById(R.id
                .rl_livevideo_message_panelroot);
        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
        btMesOpen = mView.findViewById(R.id.bt_livevideo_message_open);
        ivMessageClose = mView.findViewById(R.id.iv_livevideo_message_close);
        liveStandMessageContent = mView.findViewById(R.id.rl_live_stand_message_content);
//        int screenWidth = ScreenUtils.getScreenWidth();
//        int screenHeight = ScreenUtils.getScreenHeight();
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
//        params.topMargin = ScreenUtils.getScreenHeight() / 2;
//        rlInfo.setLayoutParams(params);
//        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
//        int hradio = (int) ((LiveVideoActivity.VIDEO_HEIGHT - LiveVideoActivity.VIDEO_HEAD_HEIGHT) * screenHeight /
//                LiveVideoActivity.VIDEO_HEIGHT);
//        params.width = 300;
//        params.topMargin = screenHeight - hradio;
        return mView;
    }

    /**
     * 体验课走这里，为了隐藏临时的star和gold图片
     *
     * @param isVisible
     */
    public void setStarGoldImageViewVisible(boolean isVisible) {

        if (mView != null) {
            mView.findViewById(R.id.cl_stand_experience_temp_gold_star).setVisibility(isVisible ? View.VISIBLE : View
                    .GONE);
        }

    }

    /**
     * 设置聊天开启图片
     *
     * @param open
     */
    private void initOpenBt(boolean open) {
        InputStream inputStream = null;
        try {
            String fileName;
            if (open) {
                fileName = "live_stand/frame_anim/openmsg/message_open_00085.png";
            } else {
                fileName = "live_stand/frame_anim/openmsg/message_open_00074.png";
            }
            inputStream = AssertUtil.open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            bitmap.setDensity((int) (DisplayMetrics.DENSITY_MEDIUM * (FrameAnimation.IMAGE_HEIGHT / (float) com
// .xueersi.parentsmeeting.util.ScreenUtils.getScreenHeight(mView.getContext()))));
            bitmap.setDensity((int) (FrameAnimation.DEFAULT_DENSITY * 2.8f / ScreenUtils.getScreenDensity()));
            btMesOpen.setBackground(DrawableHelper.bitmap2drawable(bitmap));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 聊天打开的动画 */
    private void initBtMesOpenAnimation() {
        if (lvMessage.getVisibility() == View.GONE) {
            btMesOpen.setEnabled(false);
            ivMessageClose.setEnabled(false);
            logger.d("initBtMesOpenAnimation:false");
            btMesOpenAnimation = FrameAnimation.createFromAees(mContext, btMesOpen, "live_stand/frame_anim/openmsg",
                    50, false);
            btMesOpenAnimation.setDensity((int) (FrameAnimation.DEFAULT_DENSITY * 2.8f / ScreenUtils.getScreenDensity
                    ()));
//            btMesOpenAnimation.restartAnimation();
            btMesOpenAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
                @Override
                public void onAnimationStart() {
                    logger.d("onAnimationStart");
                }

                @Override
                public void onAnimationEnd() {
                    btMesOpen.setEnabled(true);
                    ivMessageClose.setEnabled(true);
                    initOpenBt(true);
                    logger.d("initBtMesOpenAnimation:true");
                }

                @Override
                public void onAnimationRepeat() {
                    logger.d("onAnimationRepeat");
                }
            });
            lvMessage.setVisibility(View.VISIBLE);
            rlMessageContent.setVisibility(View.VISIBLE);
        } else {
            initOpenBt(false);
            if (rlMessageContent.getVisibility() == View.GONE) {
                rlMessageContent.setVisibility(View.VISIBLE);
            } else {
                lvMessage.setVisibility(View.GONE);
                rlMessageContent.setVisibility(View.GONE);
                onTitleShow(true);
            }
//            liveMediaControllerBottom.onChildViewClick(btMesOpen);
        }
    }

    @Override
    public void initListener() {
//        int screenWidth = ScreenUtils.getScreenWidth();
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cbMessageClock.getLayoutParams();
//        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
//        params.rightMargin = wradio;
//        LayoutParamsUtil.setViewLayoutParams(cbMessageClock, params);
        btMesOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StandLiveMethod.onClickVoice(liveSoundPool);
                if (!ircState.openchat()) {
                    XESToastUtils.showToast(mContext, "已关闭聊天区");
                    return;
                } else {
                    if (ircState.isDisable()) {
                        addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "你被老师禁言了，请联系老师解除禁言！", "");
                        return;
                    }
                }
                if (btMesOpenAnimation != null) {
                    btMesOpenAnimation.pauseAnimation();
                }
                initBtMesOpenAnimation();
//                liveMediaControllerBottom.onChildViewClick(v);
//                rlMessageContent.setVisibility(View.VISIBLE);
//                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
            }
        });
        ivMessageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
                btMesOpen.performClick();
            }
        });
        etMessageContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isSend = (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() ==
                        KeyEvent
                                .KEYCODE_ENTER));
                if (isSend) {
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
                    XESToastUtils.showToast(mContext, "正在答题，不能献花");
                    return;
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
//        cbMessageClock.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    liveMediaControllerBottom.onChildViewClick(v);
//                }
//                return false;
//            }
//        });
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
                        StandLiveMethod.onClickVoice(liveSoundPool);
                        return;
                    }
                    boolean isSend = false;
                    if (ircState.openchat()) {
                        if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                            boolean send = ircState.sendMessage(msg, getInfo.getStandLiveName());
                            if (send) {
                                etMessageContent.setText("");
                                addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, getInfo.getHeadImgPath());
                                lastSendMsg = System.currentTimeMillis();
                                onTitleShow(true);
                                isSend = true;
                            } else {
//                                XESToastUtils.showToast(mContext, "你已被禁言!");
                                addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "你被老师禁言了，请联系老师解除禁言！", "");
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
                    if (isSend) {
                        StandLiveMethod.voiceSiu(liveSoundPool);
                    } else {
                        StandLiveMethod.onClickVoice(liveSoundPool);
                    }
                } else {
                    StandLiveMethod.onClickVoice(liveSoundPool);
                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_EMPTY, "");
                }
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
                            btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_expression);
                        }
                    }
                });
                KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, btMessageExpress, etMessageContent,
                        new KPSwitchConflictUtil.SwitchClickListener() {
                            @Override
                            public void onClickSwitch(boolean switchToPanel) {
//                                StandLiveMethod.onClickVoice(liveSoundPool);
                                if (switchToPanel) {
                                    StandLiveMethod.voicePopup(liveSoundPool);
                                    btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_input);
                                    etMessageContent.clearFocus();
                                } else {
                                    btMessageExpress.setBackgroundResource(R.drawable
                                            .selector_live_stand_chat_expression);
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
        logger.i("initData:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
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
                StandLiveMethod.onClickVoice(liveSoundPool);
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
        logger.i("initData:minisize=" + minisize);

//        liveMessageEntities.clear();
//        for (int i = 0; i < 3; i++) {
//            LiveMessageEntity liveMessageEntity = new LiveMessageEntity(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP,
//                    "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊" + i, "");
//            liveMessageEntity = new LiveMessageEntity(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP,
//                    "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊", "");
//            liveMessageEntities.add(liveMessageEntity);
//        }

        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities, 5) {
            String fileName = "live_stand_head.json";

            @Override
            public Object getItemViewType(LiveMessageEntity liveMessageEntity) {
                return liveMessageEntity.getType();
            }

            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                int typeInt = (int) type;
                if (typeInt == LiveMessageEntity.MESSAGE_TIP) {
                    return new StandLiveMessSysItem(mContext, fileName, messageSize);
                } else {
                    return new StandLiveMessOtherItem(mContext, fileName, messageSize, urlclick, new TextUrlClick() {
                        @Override
                        public void onUrlClick(TextView tvContent) {
                            urlClick(tvContent);
                        }
                    });
                }
            }
        };
        lvMessage.setVerticalFadingEdgeEnabled(false);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lvMessage.getLayoutParams();
        lp.topMargin = ScreenUtils.getScreenHeight() / 3;
        lvMessage.setLayoutParams(lp);
        lvMessage.setAdapter(messageAdapter);
//        mView.post(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    liveMessageEntities.remove(0);
//                }
//                messageAdapter.notifyDataSetChanged();
//            }
//        });
        liveSoundPool = LiveSoundPool.createSoundPool();
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
        logger.i("initData:time4=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mView.post(new Runnable() {
            @Override
            public void run() {
                initFlower();
            }
        });
        logger.i("initData:time5=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        initOpenBt(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveSoundPool != null) {
            liveSoundPool.release();
        }
    }

    private void initFlower() {
        long before = System.currentTimeMillis();
        final ArrayList<FlowerEntity> flowerEntities = new ArrayList<>();
        flowerEntities.add(new FlowerEntity(FLOWERS_SMALL, flowsDrawTips[0], "1支玫瑰", 10));
        flowerEntities.add(new FlowerEntity(FLOWERS_MIDDLE, flowsDrawTips[1], "1束玫瑰", 50));
        flowerEntities.add(new FlowerEntity(FLOWERS_BIG, flowsDrawTips[2], "1束蓝色妖姬", 100));
        PopupWindow flowerWindow = new PopupWindow(mContext);
        flowerWindow.setBackgroundDrawable(DrawableHelper.bitmap2drawable(null));
        flowerWindow.setOutsideTouchable(true);
        flowerWindow.setFocusable(true);
        flowerContentView = View.inflate(mContext, R.layout.pop_livevideo_message_flower, null);
        flowerWindow.setContentView(flowerContentView);
        flowerWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isHaveFlowers = false;
            }
        });
        tvMessageGold = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold);
        tvMessageGoldLable = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold_lable);
        final LinearLayout llMessageFlower = (LinearLayout) flowerContentView.findViewById(R.id
                .ll_livevideo_message_flower);
        final LayoutInflater factory = LayoutInflater.from(mContext);
        final CompoundButtonGroup group = new CompoundButtonGroup();
        logger.i("initFlower:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mFlowerWindow = flowerWindow;
        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < flowerEntities.size(); i++) {
            final int index = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final FlowerEntity entity = flowerEntities.get(index);
                    FlowerItem flowerItem = new FlowerItem(mContext);
                    View root = factory.inflate(flowerItem.getLayoutResId(), llMessageFlower, false);
                    flowerItem.initViews(root);
                    flowerItem.updateViews(flowerEntities.get(index), index, null);
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
            }, i * 10);
        }
        logger.i("initFlower:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        flowerContentView.findViewById(R.id.bt_livevideo_message_flowersend).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                                goldNum = ("" + (gold - jsonObject.getInt("gold")));
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
        flowerWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        flowerWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        logger.i("initFlower:time3=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
    }

    @Override
    public void onTitleShow(boolean show) {
//        if (rlMessageContent.getVisibility() != View.GONE) {
//            rlMessageContent.setVisibility(View.GONE);
//        }
        logger.d("onTitleShow:show=" + show + ",keyboardShowing=" + keyboardShowing);
        btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_expression);
        InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
        if (!keyboardShowing && switchFSPanelLinearLayout.getVisibility() != View.GONE) {
            switchFSPanelLinearLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchFSPanelLinearLayout.setVisibility(View.GONE);
                }
            }, 10);
        }
    }

    @Override
    protected void onExpressionClick(int position, int catogaryId, String expressionId, int exPressionUrl, String
            exPressionName, int exPressionGifUrl, int bottomId) {
        StandLiveMethod.onClickVoice(liveSoundPool);
    }

    @Override
    public void closeChat(final boolean close) {
//        mView.post(new Runnable() {
//            @Override
//            public void run() {
//                if (close) {
////                    lvMessage.setVisibility(View.GONE);
//                    cbMessageClock.setChecked(true);
//                } else {
////                    lvMessage.setVisibility(View.VISIBLE);
//                    cbMessageClock.setChecked(false);
//                }
//            }
//        });
    }

    @Override
    public boolean isCloseChat() {
//        return cbMessageClock.isChecked();
        return false;
    }

    /** 聊天开始连接 */
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
    public void onConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
//                if (BuildConfig.DEBUG) {
//                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, getInfo.getTeacherId() + "_" + getInfo
// .getId());
//                }
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
            }
        });
    }

    // 03.16 设置模拟的聊天连接
    public void onConnects() {
        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
        ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
    }

    /** 聊天进入房间 */
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
                if (ircState.isHaveTeam()) {
                    tvMessageCount.setText("组内" + peopleCount + "人");
                } else {
                    tvMessageCount.setText(peopleCount + "人正在上课");
                }
            }
        });
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        if (sender.startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
            sender = getInfo.getMainTeacherInfo().getTeacherName();
        } else if (sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
            sender = getInfo.getTeacherName();
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
                                .getString("msg"), jsonObject.getString("path"));
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
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isHaveTeam()) {
                    tvMessageCount.setText("组内" + peopleCount + "人");
                } else {
                    tvMessageCount.setText(peopleCount + "人正在上课");
                }
            }
        });
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isHaveTeam()) {
                    tvMessageCount.setText("组内" + peopleCount + "人");
                } else {
                    tvMessageCount.setText(peopleCount + "人正在上课");
                }
            }
        });
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

    }

    /** 被禁言 */
    public void onDisable(final boolean disable, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "你被老师禁言了");
                        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "你被老师禁言了，不能发言，请认真听课！", "");
                    }
//                    btMesOpen.setAlpha(0.4f);
//                    btMesOpen.setEnabled(false);
//                    btMesOpen.setBackgroundResource(R.drawable.bg_live_chat_input_open_normal);
                } else {
                    if (fromNotice) {
//                        XESToastUtils.showToast(mContext, "老师解除了你的禁言");
                        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "老师解除了你的禁言，注意文明讨论！", "");
                    }
//                    if (liveBll.openchat()) {
//                        btMesOpen.setAlpha(1.0f);
//                        btMesOpen.setEnabled(true);
//                        btMesOpen.setBackgroundResource(R.drawable.bg_live_chat_input_open_normal);
//                    } else {
//                        btMesOpen.setAlpha(0.4f);
//                        btMesOpen.setEnabled(false);
//                        btMesOpen.setBackgroundResource(R.drawable.bg_live_chat_input_open_normal);
//                    }
                }
            }
        });
    }

    @Override
    public void onOtherDisable(String id, final String name, final boolean disable) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "老师禁言了" + name + "，请大家文明讨论！", "");
//                    btMesOpen.setBackgroundResource(R.drawable.bg_live_chat_input_open_normal);
                } else {
                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "老师解除了" + name + "的禁言，请大家文明讨论！", "");
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

                } else {
                    if (openchat && !isAnaswer) {
                        if (liveStandMessageContent.getVisibility() != View.VISIBLE) {
                            liveStandMessageContent.setVisibility(View.VISIBLE);
                            StandLiveMethod.voicePopup(liveSoundPool);
                        }
                        //现在的隐藏显示和liveStandMessageContent一致
                        btMesOpen.setVisibility(View.VISIBLE);
                        logger.i("显示聊天框");
                    } else {
                        liveStandMessageContent.setVisibility(View.GONE);
                        //现在的隐藏显示和liveStandMessageContent一致
                        btMesOpen.setVisibility(View.GONE);
                        logger.i("隐藏聊天框");
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
    public void onOpenbarrage(final boolean openbarrage, final boolean fromNotice) {
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
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean b, boolean zjlkOpenbarrage, boolean
            zjfdOpenbarrage) {

    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }

    /*添加聊天信息，超过120，移除60个*/
    @Override
    public void addMessage(final String sender, final int type, final String text, final String headUrl) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                final SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                                .chatSendContentDeal(text), mContext,
                        messageSize);
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (liveMessageEntities.size() > 2) {
                            LiveMessageEntity entity = liveMessageEntities.remove(0);
                            //使用AutohListview会走这，现在listview不会
//                            StandLiveHeadView standLiveHeadView = entity.getStandLiveHeadView();
//                            boolean same = standLiveHeadView.getEntity() == entity;
//                            if (same) {
//                                standLiveHeadView.pauseAnimation();
//                                logger.d( "addMessage:pauseAnimation:entity=" + entity.getText());
//                            } else {
//                                logger.d( "addMessage:entity=" + standLiveHeadView.getEntity() + "," + entity
// .getText());
//                            }
                        }
                        LiveMessageEntity entity = new LiveMessageEntity(sender, type, sBuilder, headUrl);
                        liveMessageEntities.add(entity);
                        //站立直播不保留其他数据
//                        if (otherLiveMessageEntities != null) {
//                            if (otherLiveMessageEntities.size() > 29) {
//                                otherLiveMessageEntities.remove(0);
//                            }
//                            otherLiveMessageEntities.add(entity);
//                        }
//                        if (otherMessageAdapter != null) {
//                            otherMessageAdapter.notifyDataSetChanged();
//                        }
                        messageAdapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.smoothScrollToPosition(lvMessage.getCount() - 1);
                                lvMessage.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                            lvMessage.smoothScrollToPosition(lvMessage.getCount() - 1);
//                                            lvMessage.setSelection(lvMessage.getCount() - 1);
                                        int childrenCount = lvMessage.getChildCount();
                                        if (childrenCount > 0) {
                                            View child = lvMessage.getChildAt(childrenCount - 1);
                                            int childBottom = child.getBottom();
                                            int paddingBottom = lvMessage.getPaddingBottom();
//                                                logger.d( "addMessage:lvMessage=" + paddingBottom + "," +
// lvMessage.getScrollY() + "," + lvMessage.getHeight()
//                                                        + ",child=" + child.getHeight() + "," + childBottom);
                                            if (childBottom + paddingBottom > lvMessage.getHeight()) {
                                                int offset = (childBottom + paddingBottom) - lvMessage.getHeight();
                                                logger.d("addMessage:offset=" + offset);
                                                lvMessage.smoothScrollByOffset(offset);
                                            }
                                        }
                                    }
                                }, 200);
                            }
                        });
                    }
                });
            }
        });
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
    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
//        tvMessageGold.setText(goldNum);
//        tvMessageGold.setVisibility(View.VISIBLE);
//        tvMessageGoldLable.setVisibility(View.VISIBLE);
    }

    // 03.16 模拟读取历史聊天记录
    public void oldMessage() {
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                addMessage("Teacher", LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
            }
        }, 10000);
    }

    // 03.16 模拟显示聊天人数
    public void showPeopleCount(int num) {
        tvMessageCount.setText(num + "人正在上课");
    }

    @Override
    public void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, final boolean isShow) {
        isAnaswer = isShow;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    liveStandMessageContent.setVisibility(View.GONE);
                    //现在的隐藏显示和liveStandMessageContent一致
                    btMesOpen.setVisibility(View.GONE);
                    logger.i("隐藏聊天框");
                } else {
                    if (ircState.openchat()) {
                        if (liveStandMessageContent.getVisibility() != View.VISIBLE) {
                            liveStandMessageContent.setVisibility(View.VISIBLE);
                            StandLiveMethod.voicePopup(liveSoundPool);
                        }
                        logger.i("显示聊天框");
                        //现在的隐藏显示和liveStandMessageContent一致
                        btMesOpen.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}

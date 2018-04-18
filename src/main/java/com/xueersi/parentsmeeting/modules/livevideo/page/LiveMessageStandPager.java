package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
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

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.FlowerItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StandLiveHeadView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.adapter.CommonAdapter;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.RegexUtils;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.view.button.CompoundButtonGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import master.flame.danmaku.danmaku.ui.widget.DanmakuView;

/**
 * Created by linyuqiang on 2016/8/2.
 * 直播聊天横屏-直播课和直播辅导
 */
public class LiveMessageStandPager extends BaseLiveMessagePager {
    private String TAG = "LiveMessageStandPager";
    /** 聊天，默认开启 */
    private Button btMesOpen;
    /** 左侧聊天区 */
    private View rl_live_stand_message_content;
    FrameAnimation btMesOpenAnimation;
    /** 献花，默认关闭 */
    private Button btMessageFlowers;
//    /** 聊天，默认打开 */
//    private CheckBox cbMessageClock;
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
    private ImageView ivExpressionCancle;
    private Activity liveVideoActivity;
    private QuestionBll questionBll;
    /** 竖屏的时候，也添加横屏的消息 */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;
    /** 是不是正在答题 */
    private boolean isAnaswer = false;

    public LiveMessageStandPager(Context context, QuestionBll questionBll, BaseLiveMediaControllerBottom
            liveMediaControllerBottom, ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        this.questionBll = questionBll;
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;
        Resources resources = context.getResources();
        nameColors[0] = resources.getColor(R.color.COLOR_32B16C);
        nameColors[1] = resources.getColor(R.color.COLOR_E74C3C);
        nameColors[2] = resources.getColor(R.color.COLOR_20ABFF);

//        btMesOpen = liveMediaControllerBottom.getBtMesOpen();
        btMessageFlowers = liveMediaControllerBottom.getBtMessageFlowers();
//        cbMessageClock = liveMediaControllerBottom.getCbMessageClock();

        mView.post(new Runnable() {
            @Override
            public void run() {
                initListener();
                initData();
            }
        });
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_message_stand, null);
        tvMessageCount = (TextView) mView.findViewById(R.id.tv_livevideo_message_count);
        ivMessageOnline = (ImageView) mView.findViewById(R.id.iv_livevideo_message_online);
        lvMessage = (ListView) mView.findViewById(R.id.lv_livevideo_message);
        dvMessageDanmaku = (DanmakuView) mView.findViewById(R.id.dv_livevideo_message_danmaku);
        rlInfo = mView.findViewById(R.id.rl_livevideo_info);
        rlMessageContent = mView.findViewById(R.id.rl_livevideo_message_content2);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_message_send);
        switchFSPanelLinearLayout = (KPSwitchFSPanelLinearLayout) mView.findViewById(R.id
                .rl_livevideo_message_panelroot);
        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
        btMesOpen = mView.findViewById(R.id.bt_livevideo_message_open);
        rl_live_stand_message_content = mView.findViewById(R.id.rl_live_stand_message_content);
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open("live_stand/frame_anim/openmsg/message_open_00074.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(160);
            btMesOpen.setBackgroundDrawable(new BitmapDrawable(bitmap));
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

    void initBtMesOpenAnimation() {
        btMesOpenAnimation = FrameAnimation.createFromAees(mContext, btMesOpen, "live_stand/frame_anim/openmsg", 50, false);
//            btMesOpenAnimation.restartAnimation();
        btMesOpenAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                Log.d(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationEnd() {
                Log.d(TAG, "onAnimationEnd");
                liveMediaControllerBottom.onChildViewClick(btMesOpen);
                rlMessageContent.setVisibility(View.VISIBLE);
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
            }

            @Override
            public void onAnimationRepeat() {
                Log.d(TAG, "onAnimationRepeat");
            }
        });
        btMesOpen.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                Log.d(TAG, "btMesOpen.onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                Log.d(TAG, "btMesOpen.onViewDetachedFromWindow");
            }
        });
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
                if (!liveBll.openchat()) {
                    XESToastUtils.showToast(mContext, "已关闭聊天区");
                    return;
                } else {
                    if (liveBll.isDisable()) {
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
                if (questionBll.isAnaswer()) {
                    XESToastUtils.showToast(mContext, "正在答题，不能献花");
                    return;
                }
                if (LiveTopic.MODE_CLASS.equals(liveBll.getMode())) {
                    if (!liveBll.isOpenbarrage()) {
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
                Loger.i(TAG, "onClick:time=" + (System.currentTimeMillis() - lastSendMsg));
                Editable editable = etMessageContent.getText();
                String msg = editable.toString();
                if (!StringUtils.isSpace(msg)) {
                    if (getInfo != null && getInfo.getBlockChinese() && isChinese(msg)) {
                        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_CHINESE, "");
                        onTitleShow(true);
                        return;
                    }
                    if (liveBll.openchat()) {
                        if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                            boolean send = liveBll.sendMessage(msg);
                            if (send) {
                                etMessageContent.setText("");
                                addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, getInfo.getHeadImgPath());
                                lastSendMsg = System.currentTimeMillis();
                                onTitleShow(true);
                            } else {
//                                XESToastUtils.showToast(mContext, "你已被禁言!");
                                addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "你被老师禁言了，请联系老师解除禁言！", "");
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
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtil.attach((Activity) mContext, switchFSPanelLinearLayout, new KeyboardUtil
                        .OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        Loger.i(TAG, "onKeyboardShowing:isShowing=" + isShowing);
                        if (!isShowing && switchFSPanelLinearLayout.getVisibility() == View.GONE) {
                            onTitleShow(true);
                        }
                        keyboardShowing = isShowing;
                        questionBll.onKeyboardShowing(isShowing);
                        if (keyboardShowing) {
                            btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_expression);
                        }
                    }
                });
                KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, btMessageExpress, etMessageContent,
                        new KPSwitchConflictUtil.SwitchClickListener() {
                            @Override
                            public void onClickSwitch(boolean switchToPanel) {
                                if (switchToPanel) {
                                    btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_input);
                                    etMessageContent.clearFocus();
                                } else {
                                    btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_expression);
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
        Loger.i(TAG, "initData:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                OtherModulesEnter.requestGoldTotal(mContext);
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
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);
        Loger.i(TAG, "initData:minisize=" + minisize);

//        for (int i = 0; i < 10; i++) {
//            LiveMessageEntity liveMessageEntity = new LiveMessageEntity(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_CHINESE, "");
//            liveMessageEntities.add(liveMessageEntity);
//        }

        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities, 2) {
            String fileName = "live_stand_head.json";

            @Override
            public Object getItemViewType(LiveMessageEntity liveMessageEntity) {
                return liveMessageEntity.getType();
            }

            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                int typeInt = (int) type;
                if (typeInt == LiveMessageEntity.MESSAGE_TIP) {
                    return new AdapterItemInterface<LiveMessageEntity>() {
                        TextView tvMessageItem;
                        StandLiveHeadView standLiveHeadView;
                        LottieComposition mComposition;

                        @Override
                        public int getLayoutResId() {
                            return R.layout.item_livevideo_stand_message;
                        }

                        @Override
                        public void initViews(View root) {
                            Loger.d(TAG, "initViews");
                            tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
                            tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
                            standLiveHeadView = root.findViewById(R.id.slhv_livevideo_message_head);
                            standLiveHeadView.setHeadSys();
                            standLiveHeadView.addAnimatorListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    Log.d(TAG, "onAnimationEnd:progerss=" + standLiveHeadView.getProgress());
//                                standLiveHeadView.setProgress(1.0f);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                            initlottieAnim();
                        }

                        private void initlottieAnim() {
                            LottieComposition.Factory.fromAssetFileName(mContext, fileName, new OnCompositionLoadedListener() {
                                @Override
                                public void onCompositionLoaded(@Nullable LottieComposition composition) {
                                    Log.d(TAG, "onCompositionLoaded:composition=" + composition);
                                    if (composition == null) {
                                        return;
                                    }
                                    if (mComposition != null) {
                                        return;
                                    }
                                    mComposition = composition;
                                    standLiveHeadView.setImageAssetsFolder("live_stand/lottie/head");
                                    standLiveHeadView.setComposition(composition);
                                }
                            });
                        }

                        @Override
                        public void bindListener() {

                        }

                        @Override
                        public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
                            String sender = entity.getSender();
                            if (urlclick == 1 && LiveMessageEntity.MESSAGE_TEACHER == entity.getType()) {
                                tvMessageItem.setAutoLinkMask(Linkify.WEB_URLS);
                                tvMessageItem.setText(entity.getText());
                                urlClick(tvMessageItem);
                                CharSequence text = tvMessageItem.getText();
                                tvMessageItem.setText(text);
                            } else {
                                tvMessageItem.setAutoLinkMask(0);
                                tvMessageItem.setText(entity.getText());
                            }
                            boolean deng = standLiveHeadView.getEntity() == entity;
                            Loger.d(TAG, "updateViews:deng=" + deng + ",progress=" + standLiveHeadView.getProgress() + ",standLiveHeadView=" + standLiveHeadView.getEntity() + ",text=" + entity.getText());
                            standLiveHeadView.setIsMine(entity.getType() == LiveMessageEntity.MESSAGE_MINE);
//                        entity.setHeadUrl(getInfo.getHeadImgPath());
                            standLiveHeadView.setName(entity.getSender());
                            if (!entity.isPlayAnimation()) {
                                entity.setPlayAnimation(true);
                                standLiveHeadView.playAnimation();
                            } else {
                                standLiveHeadView.setProgress(1.0f);
                            }
                            entity.setStandLiveHeadView(standLiveHeadView);
                            standLiveHeadView.setEntity(entity);
                        }
                    };
                } else {
                    return new AdapterItemInterface<LiveMessageEntity>() {
                        TextView tvMessageItem;
                        StandLiveHeadView standLiveHeadView;
                        LottieComposition mComposition;

                        @Override
                        public int getLayoutResId() {
                            return R.layout.item_livevideo_stand_message;
                        }

                        @Override
                        public void initViews(View root) {
                            Loger.d(TAG, "initViews");
                            tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
                            tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
                            standLiveHeadView = root.findViewById(R.id.slhv_livevideo_message_head);
                            standLiveHeadView.addAnimatorListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    Log.d(TAG, "onAnimationEnd:progerss=" + standLiveHeadView.getProgress());
//                                standLiveHeadView.setProgress(1.0f);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                            initlottieAnim();
                        }

                        private void initlottieAnim() {
                            LottieComposition.Factory.fromAssetFileName(mContext, fileName, new OnCompositionLoadedListener() {
                                @Override
                                public void onCompositionLoaded(@Nullable LottieComposition composition) {
                                    Log.d(TAG, "onCompositionLoaded:composition=" + composition);
                                    if (composition == null) {
                                        return;
                                    }
                                    if (mComposition != null) {
                                        return;
                                    }
                                    mComposition = composition;
                                    standLiveHeadView.setImageAssetsFolder("live_stand/lottie/head");
                                    standLiveHeadView.setComposition(composition);
                                }
                            });
                        }

                        @Override
                        public void bindListener() {

                        }

                        @Override
                        public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
                            String sender = entity.getSender();
//                        switch (entity.getType()) {
//                            case LiveMessageEntity.MESSAGE_MINE:
//                            case LiveMessageEntity.MESSAGE_TEACHER:
//                            case LiveMessageEntity.MESSAGE_TIP:
//                            case LiveMessageEntity.MESSAGE_CLASS:
//                                color = nameColors[entity.getType()];
//                                break;
//                            default:
//                                color = nameColors[0];
//                                break;
//                        }
                            if (urlclick == 1 && LiveMessageEntity.MESSAGE_TEACHER == entity.getType()) {
                                tvMessageItem.setAutoLinkMask(Linkify.WEB_URLS);
                                tvMessageItem.setText(entity.getText());
                                urlClick(tvMessageItem);
                                CharSequence text = tvMessageItem.getText();
                                tvMessageItem.setText(text);
                            } else {
                                tvMessageItem.setAutoLinkMask(0);
                                tvMessageItem.setText(entity.getText());
                            }
//                        boolean deng = standLiveHeadView.getEntity() == entity;
//                        Loger.d(TAG, "updateViews:deng=" + deng + ",text=" + entity.getText());
//                        if (!deng) {
//                            standLiveHeadView.setIsMine(entity.getType() == LiveMessageEntity.MESSAGE_MINE);
////                        entity.setHeadUrl(getInfo.getHeadImgPath());
//                            standLiveHeadView.setName(entity.getSender());
//                            standLiveHeadView.setHead(entity.getHeadUrl());
//                            if (!entity.isPlayAnimation()) {
//                                entity.setPlayAnimation(true);
//                                standLiveHeadView.playAnimation();
//                            } else {
//                                standLiveHeadView.setProgress(1.0f);
//                            }
//                        } else {
//                            if (!entity.isPlayAnimation()) {
//                                Loger.d(TAG, "updateViews:isPlayAnimation=false");
//                                entity.setPlayAnimation(true);
//                                standLiveHeadView.playAnimation();
//                            } else {
//                                standLiveHeadView.setProgress(1.0f);
//                            }
//                        }
//                        if (!entity.isPlayAnimation()) {
//                            Loger.d(TAG, "updateViews:isPlayAnimation=false");
//                            entity.setPlayAnimation(true);
//                            standLiveHeadView.playAnimation();
//                        } else {
//                            Loger.d(TAG, "updateViews:equal=" + (standLiveHeadView.getEntity() != entity));
//                            if (standLiveHeadView.getEntity() != entity) {
////                                standLiveHeadView.setProgress(1.0f);
//                                standLiveHeadView.playAnimation();
//                            } else {
////                                standLiveHeadView.resumeAnimation();
//                            }
//                        }
                            boolean deng = standLiveHeadView.getEntity() == entity;
                            Loger.d(TAG, "updateViews:deng=" + deng + ",progress=" + standLiveHeadView.getProgress() + ",standLiveHeadView=" + standLiveHeadView.getEntity() + ",text=" + entity.getText());
                            standLiveHeadView.setIsMine(entity.getType() == LiveMessageEntity.MESSAGE_MINE);
//                        entity.setHeadUrl(getInfo.getHeadImgPath());
                            standLiveHeadView.setName(entity.getSender());
                            standLiveHeadView.setHead(entity.getHeadUrl());
                            if (!entity.isPlayAnimation()) {
                                entity.setPlayAnimation(true);
                                standLiveHeadView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        standLiveHeadView.playAnimation();
                                    }
                                }, 20);
                            } else {
                                standLiveHeadView.setProgress(1.0f);
                            }
                            entity.setStandLiveHeadView(standLiveHeadView);
                            standLiveHeadView.setEntity(entity);
                        }
                    };
                }

            }
        };
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
        Loger.i(TAG, "initData:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mView.post(new Runnable() {
            @Override
            public void run() {
                initDanmaku();
            }
        });
        Loger.i(TAG, "initData:time3=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        Loger.i(TAG, "initData:time4=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mView.post(new Runnable() {
            @Override
            public void run() {
                initFlower();
            }
        });
        Loger.i(TAG, "initData:time5=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
    }

    private void initFlower() {
        long before = System.currentTimeMillis();
        final ArrayList<FlowerEntity> flowerEntities = new ArrayList<>();
        flowerEntities.add(new FlowerEntity(FLOWERS_SMALL, flowsDrawTips[0], "1支玫瑰", 10));
        flowerEntities.add(new FlowerEntity(FLOWERS_MIDDLE, flowsDrawTips[1], "1束玫瑰", 50));
        flowerEntities.add(new FlowerEntity(FLOWERS_BIG, flowsDrawTips[2], "1束蓝色妖姬", 100));
        PopupWindow flowerWindow = new PopupWindow(mContext);
        flowerWindow.setBackgroundDrawable(new BitmapDrawable());
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
        final LinearLayout llMessageFlower = (LinearLayout) flowerContentView.findViewById(R.id.ll_livevideo_message_flower);
        final LayoutInflater factory = LayoutInflater.from(mContext);
        final CompoundButtonGroup group = new CompoundButtonGroup();
        Loger.i(TAG, "initFlower:time1=" + (System.currentTimeMillis() - before));
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
        Loger.i(TAG, "initFlower:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        flowerContentView.findViewById(R.id.bt_livevideo_message_flowersend).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                final FlowerEntity entity = (FlowerEntity) flowerContentView.getTag();
                if (entity != null) {
                    if (LiveTopic.MODE_CLASS.equals(liveBll.getMode())) {
                        if (liveBll.isOpenbarrage()) {
                            liveBll.praiseTeacher(entity.getFtype() + "", new HttpCallBack(false) {
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
        Loger.i(TAG, "initFlower:time3=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
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

    public void setVideoWidthAndHeight(int width, int height) {
//        final View contentView = liveVideoActivity.findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        int screenWidth = (r.right - r.left);
//        int screenHeight = ScreenUtils.getScreenHeight();
//        if (width > 0) {
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
//            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
//            wradio += (screenWidth - width) / 2;
//            if (wradio != params.width) {
//                //Loger.e(TAG, "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
//                // + ",wradio=" + wradio + "," + params.width);
//                params.width = wradio;
////                rlInfo.setLayoutParams(params);
//                LayoutParamsUtil.setViewLayoutParams(rlInfo, params);
//            }
//            params = (RelativeLayout.LayoutParams) cbMessageClock.getLayoutParams();
//            if (params.rightMargin != wradio) {
//                params.rightMargin = wradio;
////                cbMessageClock.setLayoutParams(params);
//                LayoutParamsUtil.setViewLayoutParams(cbMessageClock, params);
//            }
//        }
//        if (height > 0) {
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlInfo.getLayoutParams();
//            int topMargin = (int) ((LiveVideoActivity.VIDEO_HEIGHT - LiveVideoActivity.VIDEO_HEAD_HEIGHT) * height /
//                    LiveVideoActivity.VIDEO_HEIGHT);
//            topMargin = height - topMargin + (screenHeight - height) / 2;
//            if (topMargin != params.topMargin) {
//                params.topMargin = topMargin;
////                rlInfo.setLayoutParams(params);
//                LayoutParamsUtil.setViewLayoutParams(rlInfo, params);
//                Loger.e(TAG, "setVideoWidthAndHeight:topMargin=" + params.topMargin);
//            }
//            int bottomMargin = (ScreenUtils.getScreenHeight() - height) / 2;
//            params = (ViewGroup.MarginLayoutParams) lvMessage.getLayoutParams();
//            if (params.bottomMargin != bottomMargin) {
//                params.bottomMargin = bottomMargin;
////                lvMessage.setLayoutParams(params);
//                LayoutParamsUtil.setViewLayoutParams(lvMessage, params);
//                //Loger.e(TAG, "setVideoWidthAndHeight:bottomMargin=" + bottomMargin);
//            }
//        }
    }

    /** 聊天开始连接 */
    public void onStartConnect() {
        mView.post(new Runnable() {
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
        mView.post(new Runnable() {
            @Override
            public void run() {
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
//                if (BuildConfig.DEBUG) {
//                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, getInfo.getTeacherId() + "_" + getInfo.getId());
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
        mView.post(new Runnable() {
            @Override
            public void run() {
                isRegister = true;
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
            }
        });
    }

    /** 聊天断开 */
    public void onDisconnect() {
        mView.post(new Runnable() {
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
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (liveBll.isHaveTeam()) {
                    tvMessageCount.setText("组内" + peopleCount + "人");
                } else {
                    tvMessageCount.setText(peopleCount + "人正在上课");
                }
            }
        });
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        if (sender.startsWith("t")) {
            sender = getInfo.getMainTeacherInfo().getTeacherName();
        } else if (sender.startsWith("f")) {
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
                if (liveBll.isHaveTeam()) {
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
                if (liveBll.isHaveTeam()) {
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
    public void onopenchat(final boolean openchat, final String mode, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (liveBll.isDisable()) {
//                    btMesOpen.setAlpha(0.4f);
//                    btMesOpen.setEnabled(false);
//                    rl_live_stand_message_content.setVisibility(View.GONE);
//                    btMesOpen.setBackgroundResource(R.drawable.bg_live_chat_input_open_normal);
                } else {
                    if (openchat && !isAnaswer) {
//                        btMesOpen.setAlpha(1.0f);
//                        btMesOpen.setEnabled(true);
                        rl_live_stand_message_content.setVisibility(View.VISIBLE);
                        btMesOpen.setVisibility(View.VISIBLE);
//                        btMesOpen.setBackgroundResource(R.drawable.bg_live_chat_input_open_normal);
                    } else {
//                        btMesOpen.setAlpha(0.4f);
//                        btMesOpen.setEnabled(false);
                        rl_live_stand_message_content.setVisibility(View.GONE);
                        btMesOpen.setVisibility(View.GONE);
//                        btMesOpen.setBackgroundResource(R.drawable.bg_live_chat_input_open_normal);
                    }
//                    if (fromNotice) {
//                        if (LiveTopic.MODE_CLASS.equals(mode)) {
//                            XESToastUtils.showToast(mContext, "主讲老师" + (openchat ? "打开" : "关闭") + "了聊天区");
//                        } else {
//                            XESToastUtils.showToast(mContext, "辅导老师" + (openchat ? "打开" : "关闭") + "了聊天区");
//                        }
//                    }
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
                    if (liveBll.isOpenbarrage()) {
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
                if (LiveTopic.MODE_CLASS.equals(liveBll.getMode())) {
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
                        if (liveMessageEntities.size() > 29) {
                            LiveMessageEntity entity = liveMessageEntities.remove(0);
                            //使用AutohListview会走这，现在listview不会
//                            StandLiveHeadView standLiveHeadView = entity.getStandLiveHeadView();
//                            boolean same = standLiveHeadView.getEntity() == entity;
//                            if (same) {
//                                standLiveHeadView.pauseAnimation();
//                                Loger.d(TAG, "addMessage:pauseAnimation:entity=" + entity.getText());
//                            } else {
//                                Loger.d(TAG, "addMessage:entity=" + standLiveHeadView.getEntity() + "," + entity.getText());
//                            }
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
                        messageAdapter.notifyDataSetChanged();
                        if (!isTouch) {
//                            if (lvMessage.getChildCount() == 1) {
//                                lvMessage.scrollBy(0, lvMessage.getChildAt(0).getHeight());
//                            }
                            lvMessage.setSelection(lvMessage.getCount() - 1);
                        }
                    }
                });
            }
        });
    }

    /**
     * 把字符串进行转义（表情显示图片）
     *
     * @param content
     * @param mContext
     * @param bounds   噢！是写错了，已经加进去了[e]1f60a[/e][e]1f60a[/e][e]1f60a[/e][e]1f33a[/e][e]1f33a[/e]
     * @return [e]em_2[e] \[e\](.*?)\[/e\]
     */
    public static SpannableStringBuilder convertToHtml(String content, Context mContext, int bounds) {
        String regex = "\\[e\\]em(.*?)\\[e\\]";
        Pattern pattern = Pattern.compile(regex);
        String emo = "";
        Resources resources = mContext.getResources();
        Matcher matcher = pattern.matcher(content);
        SpannableStringBuilder sBuilder = new SpannableStringBuilder(content);
        Drawable drawable = null;
        ImageSpan span = null;
        while (matcher.find()) {
            emo = matcher.group();
            try {
                int id = map.get(emo);
                if (id != 0) {
                    drawable = resources.getDrawable(id);
                    if (drawable != null) {
                        drawable.setBounds(0, 0, bounds, bounds);
                        span = new VerticalImageSpan(drawable);
                        sBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        return sBuilder;
    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {
        this.otherMessageAdapter = otherMessageAdapter;
    }

    public static HashMap<String, Integer> map = new HashMap<>();

    static {
        map.put("[e]em_1[e]", R.drawable.emoji_1f60a);
        map.put("[e]em_2[e]", R.drawable.emoji_1f604);
        map.put("[e]em_3[e]", R.drawable.emoji_1f633);
        map.put("[e]em_4[e]", R.drawable.emoji_1f60c);
        map.put("[e]em_5[e]", R.drawable.emoji_1f601);
        map.put("[e]em_6[e]", R.drawable.emoji_1f61d);
        map.put("[e]em_7[e]", R.drawable.emoji_1f625);
        map.put("[e]em_8[e]", R.drawable.emoji_1f623);
        map.put("[e]em_9[e]", R.drawable.emoji_1f628);
        map.put("[e]em_10[e]", R.drawable.emoji_1f632);
        map.put("[e]em_11[e]", R.drawable.emoji_1f62d);
        map.put("[e]em_12[e]", R.drawable.emoji_1f602);
        map.put("[e]em_13[e]", R.drawable.emoji_1f631);
        map.put("[e]em_14[e]", R.drawable.emoji_1f47f);
        map.put("[e]em_15[e]", R.drawable.emoji_1f44d);
        map.put("[e]em_16[e]", R.drawable.emoji_1f44c);
        map.put("[e]em_17[e]", R.drawable.emoji_270c);
    }

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
    public void onQuestionShow(final boolean isShow) {
        isAnaswer = isShow;
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    rl_live_stand_message_content.setVisibility(View.GONE);
                    btMesOpen.setVisibility(View.GONE);
                } else {
                    if (liveBll.openchat()) {
                        rl_live_stand_message_content.setVisibility(View.VISIBLE);
                        btMesOpen.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}

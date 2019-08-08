package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.Constants;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.PermissionCallback;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.FlowerItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.UserGoldTotal;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.StandLiveMessOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.StandLiveMessSysItem;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.widget.button.CompoundButtonGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

//import com.xueersi.common.speech.SpeechConfig;
//import com.xueersi.common.speech.SpeechEvaluatorUtils;
//import com.xueersi.common.speech.SpeechUtils;

/**
 * @author linyuqiang
 * @date 2016/8/2
 * 直播聊天横屏-直播课和直播辅导
 * 修改请注意:直播体验课也走的这里
 */
public class ExperLiveMessageStandPager extends BaseLiveMessagePager implements LiveAndBackDebug {
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
    private Button btnVoiceMesOpen;
    private View rlMessageVoice;
    private TextView tvVoiceContent;
    private TextView tvVoiceCount;
    private VolumeWaveView vwvVoiceChatWave;
    private FangZhengCuYuanTextView tvVoiceChatCountdown;
    private View rlMessageText;

    /** 语音保存位置-目录 */
    File dir;
    /** 音量管理 */
    private AudioManager mAM;
    /** 最大音量 */
    private int mMaxVolume;
    /** 当前音量 */
    private int mVolume = 0;

    boolean isVoice = true;
    //当前语音输入转换的文本
    String mVoiceContent = "";
    String mMsgContent = "";
    /** 语音转文字的聊天是否已发送 */
    private boolean isVoiceMsgSend = true;
    /** 发送聊天数目 */
    private int mMsgCount = 0;
    /** 发送语音聊天数目 */
    private int mVoiceMsgCount = 0;
    /** 语音文件 */
    private File mVoiceFile;
    private AudioRequest mAudioRequest;
    /** 模型启动文案 */
    private String mSpeechFail = "模型正在启动，请稍后";
    /** 语音聊天是否完成 */
    boolean isSpeekDone = false;

    SpeechParamEntity mParam;

    private boolean isShowSpeechRecog = false;
    private long cpuRecogTime;
    //聊天/语音按钮被隐藏时输入框是否显示
    private boolean isMessageLayoutShow = false;

    private long mRecogtestBeginTime;
    private long mRecogtestEndTime;
    private ShareDataManager mSdm;

    public ExperLiveMessageStandPager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
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
        mView = View.inflate(mContext, R.layout.page_livevideo_stand_experience_livemessage_pager, null);
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
        btnVoiceMesOpen = mView.findViewById(R.id.bt_livevideo_message_voice_open);
        rlMessageVoice = mView.findViewById(R.id.rl_livevideo_voice_message_content);
        tvVoiceContent = mView.findViewById(R.id.tv_livevideo_voicechat_content);
        tvVoiceCount = mView.findViewById(R.id.tv_livevideo_voicechat_word_count);
        vwvVoiceChatWave = mView.findViewById(R.id.vwv_livevideo_voicechat_wave);
        tvVoiceChatCountdown = mView.findViewById(R.id.tv_livevideo_voicechat_countdown);
        rlMessageText = mView.findViewById(R.id.rl_livevideo_text_message_content);


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

        vwvVoiceChatWave.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vwvVoiceChatWave.setLinearGradient(new LinearGradient(0, 0, vwvVoiceChatWave.getMeasuredWidth(), 0,
                        new int[]{0xFFEA9CF9, 0xFF9DBBFA, 0xFF80F9FD}, new float[]{0, 0.5f, 1.0f}, Shader.TileMode
                        .CLAMP));
            }
        });
        vwvVoiceChatWave.setBackColor(Color.TRANSPARENT);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvVoiceChatWave.start();
            }
        }, 100);
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        tvVoiceCount.setTypeface(fontFace);
        tvVoiceContent.setTypeface(fontFace);
        tvMessageCount.setTypeface(fontFace);
        etMessageContent.setTypeface(fontFace);
        return mView;
    }

    /**
     * 体验课走这里，为了隐藏临时的star和gold图片
     *
     * @param isVisible
     */
    private boolean isNotExpericence = true;

    public void setStarGoldImageViewVisible(boolean isVisible) {
        isNotExpericence = isVisible;
        btnVoiceMesOpen.setVisibility(View.GONE);
        if (mView != null) {
            View view = mView.findViewById(R.id.cl_stand_experience_temp_gold_star);
            if (view != null) {
                view.setVisibility(isVisible ? View.VISIBLE : View
                        .GONE);
            }
        }

    }

    /**
     * 设置聊天开启图片
     *
     * @param open
     */
    private void initOpenBt(boolean open, boolean isVoice) {
        InputStream inputStream = null;
        try {
            String fileName;
            if (isVoice) {
                if (isShowSpeechRecog) {
                    if (open) {
                        fileName = "live_stand/frame_anim/openvoicemsg/polie_00082.png";
                    } else {
                        fileName = "live_stand/frame_anim/openvoicemsg/polie_00074.png";
                    }
                } else {
                    fileName = "live_stand/frame_anim/openvoicemsg/voice_btn_disable.webp";
                }

            } else {
                if (open) {
                    fileName = "live_stand/frame_anim/openmsg/message_open_00085.png";
                } else {
                    fileName = "live_stand/frame_anim/openmsg/message_open_00074.png";
                }
            }

            inputStream = AssertUtil.open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            bitmap.setDensity((int) (DisplayMetrics.DENSITY_MEDIUM * (FrameAnimation.IMAGE_HEIGHT / (float) com
// .xueersi.parentsmeeting.util.ScreenUtils.getScreenHeight(mView.getContext()))));
            bitmap.setDensity((int) (FrameAnimation.DEFAULT_DENSITY * 2.8f / ScreenUtils.getScreenDensity()));
            if (isVoice) {
//                btnVoiceMesOpen.setBackgroundDrawable(new BitmapDrawable(bitmap));
                btnVoiceMesOpen.setBackgroundDrawable(new BitmapDrawable(bitmap));
            } else {
//                btMesOpen.setBackgroundDrawable(new BitmapDrawable(bitmap));
                btMesOpen.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

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
    private void initBtMesOpenAnimation(boolean isvoice) {
        if (rlMessageContent.getVisibility() == View.GONE) {
            startOpenAnimation(isvoice);
            rlMessageContent.setVisibility(View.VISIBLE);
            lvMessage.setVisibility(View.VISIBLE);
            if (isvoice) {
                mMsgContent = "";
                mVoiceContent = "";
                startVoiceInput();
            } else {
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
            }
        } else {
//            if (rlMessageContent.getVisibility() == View.GONE) {
//                rlMessageText.setVisibility(View.VISIBLE);
//                rlMessageBackground.setVisibility(View.VISIBLE);
//            }
            stopEvaluator();
            if (isvoice) {
                if (rlMessageVoice.getVisibility() == View.VISIBLE) {
                    clearMsgView();
                } else {
                    startOpenAnimation(true);
                    startVoiceInput();
                }
            } else {
                if (rlMessageText.getVisibility() == View.VISIBLE) {
                    clearMsgView();
                } else {
                    startOpenAnimation(false);
                }
            }
//            liveMediaControllerBottom.onChildViewClick(btMesOpen);
        }
    }

    private void clearMsgView() {
        rlMessageContent.setVisibility(View.GONE);
        lvMessage.setVisibility(View.GONE);
        rlMessageText.setVisibility(View.GONE);
        rlMessageVoice.setVisibility(View.GONE);

        initOpenBt(false, true);
        initOpenBt(false, false);
        onTitleShow(true);
    }

    private void startOpenAnimation(final boolean isvoice) {
        btMesOpen.setEnabled(false);
        btnVoiceMesOpen.setEnabled(false);
        ivMessageClose.setEnabled(false);
        logger.d("initBtMesOpenAnimation:false");
        if (isvoice && isShowSpeechRecog && isNotExpericence) {
            btMesOpenAnimation = FrameAnimation.createFromAees(mContext, btnVoiceMesOpen,
                    "live_stand/frame_anim/openvoicemsg",
                    50, false);
        } else {
            btMesOpenAnimation = FrameAnimation.createFromAees(mContext, btMesOpen, "live_stand/frame_anim/openmsg",
                    50, false);
        }
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
                btnVoiceMesOpen.setEnabled(true);
                btMesOpen.setEnabled(true);
                ivMessageClose.setEnabled(true);
                if (isvoice) {
                    initOpenBt(true, isvoice);
                    initOpenBt(false, false);

                } else {
                    initOpenBt(false, true);
                    initOpenBt(true, false);
                }
                logger.d("initBtMesOpenAnimation:true");
            }


            @Override
            public void onAnimationRepeat() {
                logger.d("onAnimationRepeat");
            }
        });
        if (isvoice && isShowSpeechRecog) {
            rlMessageVoice.setVisibility(View.VISIBLE);
            rlMessageText.setVisibility(View.GONE);
        } else {
            rlMessageText.setVisibility(View.VISIBLE);
            rlMessageVoice.setVisibility(View.GONE);
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
                setSpeechFinishView("".equals(mVoiceContent) ? mMsgContent : mVoiceContent);
                initBtMesOpenAnimation(false);

//                liveMediaControllerBottom.onChildViewClick(v);
//                rlMessageContent.setVisibility(View.VISIBLE);
//                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
            }
        });
        btnVoiceMesOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowSpeechRecog) {
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
                    boolean hasPermission = XesPermission.hasSelfPermission(liveVideoActivity, Manifest.permission
                            .RECORD_AUDIO);
                    if (mSpeechUtils.isRecogOfflineSuccess()) {
                        if (!hasPermission) {
                            inspectMicPermission();
                        } else {
                            initBtMesOpenAnimation(true);
                        }
                    } else {
                        XESToastUtils.showToast(mContext, mSpeechFail);
                    }
                } else {
                    XESToastUtils.showToast(mContext, "设备状态暂不支持语音录入，请打字发言");
                }


            }
        });
        ivMessageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
//                btMesOpen.performClick();
                if (rlMessageVoice.getVisibility() == View.VISIBLE) {
                    stopEvaluator();
                    setSpeechFinishView(mVoiceContent);
                }
                mMsgContent = etMessageContent.getText().toString();
                clearMsgView();

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
                final String msg = editable.toString();
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
                                if (!isVoiceMsgSend) {
                                    isVoiceMsgSend = true;
                                    mVoiceMsgCount++;
                                    uploadLOG(msg);
                                }
                                mMsgCount++;
                                etMessageContent.setText("");
                                mVoiceContent = "";
                                mMsgContent = "";
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
        mSdm = ShareDataManager.getInstance();
        isShowSpeechRecog = mSdm.getBoolean(SpeechEvaluatorUtils.RECOG_RESULT, false, ShareDataManager.SHAREDATA_USER);
        cpuRecogTime = mSdm.getLong(SpeechEvaluatorUtils.RECOG_TIME, 2500l, ShareDataManager.SHAREDATA_USER);
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                UserGoldTotal.requestGoldTotal(mContext);
            }
        });
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
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mSpeechUtils == null) {
            mSpeechUtils = SpeechUtils.getInstance(mContext.getApplicationContext());
            mSpeechUtils.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_EN);
        }
        mParam = new SpeechParamEntity();
        mSpeechUtils.prepar(new SpeechEvaluatorUtils.OnFileSuccess() {
            @Override
            public void onFileInit(int code) {

            }

            @Override
            public void onFileSuccess() {
                mSpeechFail = "模型正在启动，请稍后";
                if (!isShowSpeechRecog) {
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpeechUtils.checkRecogCPUPerformance(new EvaluatorListener() {
                                @Override
                                public void onBeginOfSpeech() {
                                    mRecogtestBeginTime = System.currentTimeMillis();
                                }

                                @Override
                                public void onResult(ResultEntity result) {
                                    if (result.getStatus() == ResultEntity.EVALUATOR_ING) {
                                        mRecogtestEndTime = System.currentTimeMillis();
                                    }
                                    if (result.getStatus() == ResultEntity.SUCCESS) {
                                        isShowSpeechRecog = (mRecogtestEndTime - mRecogtestBeginTime) < 3000l ? true
                                                : false;
                                        if (isShowSpeechRecog) {
                                            mSdm.put(SpeechEvaluatorUtils.RECOG_RESULT, isShowSpeechRecog,
                                                    ShareDataManager.SHAREDATA_USER);
                                            initOpenBt(false, true);
                                        }
                                    }
                                }

                                @Override
                                public void onVolumeUpdate(int volume) {

                                }
                            });
                        }
                    }, 4000);
                }
            }

            @Override
            public void onFileFail() {
                mSpeechFail = "模型启动失败，请使用手动输入";
            }
        });
//        SpeechEvaluatorUtils.setOnFileSuccess(new SpeechEvaluatorUtils.OnFileSuccess() {
//            @Override
//            public void onFileSuccess() {
//                mSpeechFail = "模型正在启动，请稍后";
//            }
//
//            @Override
//            public void onFileFail() {
//                mSpeechFail = "模型启动失败，请使用手动输入";
//            }
//        });
        dir = LiveCacheFile.geCacheFile(mContext, "livevoice");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

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
        }

        ;
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
        mView.post(new

                           Runnable() {
                               @Override
                               public void run() {
                                   initDanmaku();
                               }
                           });
        logger.i("initData:time3=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        logger.i("initData:time4=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mView.post(new

                           Runnable() {
                               @Override
                               public void run() {
                                   initFlower();
                               }
                           });
        logger.i("initData:time5=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();

        initOpenBt(false, false);

        initOpenBt(false, true);

        mAudioRequest = ProxUtil.getProxUtil().

                get(liveVideoActivity, AudioRequest.class);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveSoundPool != null) {
            liveSoundPool.release();
        }
        if (noSpeechTimer != null) {
            noSpeechTimer.cancel();
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("userid", getInfo.getStuId());
        mData.put("liveid", getInfo.getId());
        mData.put("msgcount", String.valueOf(mMsgCount));
        mData.put("voicemsgcount", String.valueOf(mVoiceMsgCount));
        umsAgentDebugSys(LiveVideoConfig.LIVE_VOICE_CHAT, mData);
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
        final LinearLayout llMessageFlower = (LinearLayout) flowerContentView.findViewById(R.id
                .ll_livevideo_message_flower);
        final LayoutInflater factory = LayoutInflater.from(mContext);
        final CompoundButtonGroup group = new CompoundButtonGroup();
        logger.i("initFlower:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        mFlowerWindow = flowerWindow;
        for (int i = 0; i < flowerEntities.size(); i++) {
            final int index = i;
            LiveMainHandler.postDelayed(new Runnable() {
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
    @Override
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
                        if (isNotExpericence) {
                            btnVoiceMesOpen.setVisibility(View.VISIBLE);
                        }
                        Animation animation;
                        animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_live_stand_speech_voice);
                        if (isShowSpeechRecog) {
                            btnVoiceMesOpen.startAnimation(animation);
                        }
                        if (fromNotice && isMessageLayoutShow) {
                            btMesOpen.performClick();
                            isMessageLayoutShow = false;
                        }
                        lvMessage.setVisibility(View.VISIBLE);
                        logger.i("显示聊天框");
                    } else {
                        if (rlMessageVoice.getVisibility() == View.VISIBLE) {
                            isMessageLayoutShow = true;
                        }
                        liveStandMessageContent.setVisibility(View.GONE);
                        //现在的隐藏显示和liveStandMessageContent一致
                        btMesOpen.setVisibility(View.GONE);
                        btnVoiceMesOpen.setVisibility(View.GONE);
                        ivMessageClose.performClick();
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
    @Override
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
//        mView.post(new Runnable() {
//            @Override
//            public void run() {
//                ivMessageClose.performClick();
//            }
//        });
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
        logger.d("openvoice:" + openVoice + "from:" + type);
        if (openVoice) {
            ivMessageClose.performClick();
            if (!("ENGLISH_H5_COURSEWARE".equals(type) || "ARTS_H5_COURSEWARE".equals(type) ||
                    "EXAM_START".equals(type) || "ARTS_SEND_QUESTION".equals(type) || "SENDQUESTION"
                    .equals(type))) {
                btnVoiceMesOpen.setEnabled(false);
            }
        } else {
            btnVoiceMesOpen.setEnabled(true);
        }
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
                    //发题时关闭正在进行的语音聊天，
                    if (vwvVoiceChatWave.getVisibility() == View.VISIBLE) {
                        vwvVoiceChatWave.setVisibility(View.GONE);
                        stopEvaluator();
                        //判断聊天输入框状态，若为语音输入保存结果
                        if (rlMessageVoice.getVisibility() == View.VISIBLE) {
                            setSpeechFinishView(mVoiceContent);
                            isMessageLayoutShow = true;
                        }
                        clearMsgView();
                    }
                    liveStandMessageContent.setVisibility(View.GONE);
                    //现在的隐藏显示和liveStandMessageContent一致
                    btnVoiceMesOpen.setVisibility(View.GONE);
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
                        if (isNotExpericence) {
                            btnVoiceMesOpen.setVisibility(View.VISIBLE);
                        }
                        if (isMessageLayoutShow) {
                            btMesOpen.performClick();
                            isMessageLayoutShow = false;
                        }

                    }
                }
            }
        });
    }

    /**
     * ************************************************** 语音识别 **************************************************
     */
    /** 语音评测工具类 */
    private SpeechUtils mSpeechUtils;
    /** 是不是评测失败 */
    private boolean isSpeechError = false;
    /** 是不是评测成功 */
    private boolean isSpeechSuccess = false;
    private final static String VOICE_RECOG_HINT = "语音输入中，请大声说英语";
    private final static String VOICE_RECOG_NOVOICE_HINT = "抱歉没听清，请大点声重说哦";
    private final static String VOICE_RECOG_NORECOG_HINT = "请手动输入或重说";
    Runnable mHintRunnable = new Runnable() {
        @Override
        public void run() {
            if ("".equals(mVoiceContent)) {
                tvVoiceContent.setText(VOICE_RECOG_NOVOICE_HINT);
            }
        }
    };
    Runnable mNovoiceRunnable = new Runnable() {
        @Override
        public void run() {
            if ("".equals(mVoiceContent)) {
                tvVoiceContent.setText(VOICE_RECOG_NORECOG_HINT);
            }
        }
    };
    Runnable mNorecogRunnable = new Runnable() {
        @Override
        public void run() {
            if ("".equals(mVoiceContent)) {
                if (isVoice) {
                    stopEvaluator();
                    setSpeechFinishView(mVoiceContent);
                    btMesOpen.performClick();
                }
            }
        }
    };
    /** 计时器 */
    CountDownTimer noSpeechTimer = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (!liveVideoActivity.isFinishing()) {
                System.out.println("NOSPEECHTIMER:" + String.valueOf(millisUntilFinished / 1000));
                if (millisUntilFinished < 6000) {
                    tvVoiceChatCountdown.setVisibility(View.VISIBLE);
                    vwvVoiceChatWave.setVisibility(View.GONE);
                    tvVoiceChatCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                }
            }
        }

        @Override
        public void onFinish() {
            if (isVoice) {
                stopEvaluator();
                setSpeechFinishView(mVoiceContent);
                btMesOpen.performClick();
                tvVoiceChatCountdown.setVisibility(View.GONE);
            }
        }
    };

    private void startEvaluator() {
        mVoiceContent = "";
        logger.d("startEvaluator()" + mSpeechUtils.toString());
        mVoiceFile = new File(dir, "voicechat" + System.currentTimeMillis() + ".mp3");
        mParam.setRecogType(SpeechConfig.SPEECH_RECOGNITIYON_OFFINE);
        mParam.setLocalSavePath(mVoiceFile.getPath());
        mParam.setVad_pause_sec("1.2");
        mParam.setVad_max_sec("30");
        mSpeechUtils.startRecog(mParam, new EvaluatorListener() {
            @Override
            public void onBeginOfSpeech() {
                logger.d("onBeginOfSpeech");
                isSpeechError = false;
                isSpeekDone = false;
                isVoice = true;
                noSpeechTimer.start();
                //3秒没有检测到声音提示
                mainHandler.postDelayed(mHintRunnable, 3000);
                //6秒仍没检测到说话
                mainHandler.postDelayed(mNovoiceRunnable, 6000);
                //7秒没声音自动停止
                mainHandler.postDelayed(mNorecogRunnable, 7000);
            }

            @Override
            public void onResult(ResultEntity resultEntity) {
                logger.d("onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity
                        .getErrorNo());
                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    onEvaluatorSuccess(resultEntity, true);
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    onEvaluatorError(resultEntity);
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    if (resultEntity.getCurString() != null) {
                        onEvaluatorSuccess(resultEntity, false);
                    }
                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
                logger.d("onVolumeUpdate:volume=" + volume);
                vwvVoiceChatWave.setVolume(volume);
            }

        });
//        SpeechEvaluatorInter speechEvaluatorInter = mSpeechUtils.startSpeechRecognitionOffline(mVoiceFile
//                .getPath(), "2", "30", new EvaluatorListener() {
//            @Override
//            public void onBeginOfSpeech() {
//                logger.d("onBeginOfSpeech");
//                isSpeechError = false;
//                isSpeekDone = false;
//                isVoice = true;
//                noSpeechTimer.start();
//                //3秒没有检测到声音提示
//                mainHandler.postDelayed(mHintRunnable, 3000);
//                //6秒仍没检测到说话
//                mainHandler.postDelayed(mNovoiceRunnable, 6000);
//                //7秒没声音自动停止
//                mainHandler.postDelayed(mNorecogRunnable, 7000);
//            }
//
//            @Override
//            public void onResult(ResultEntity resultEntity) {
//                logger.d("onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity
//                        .getErrorNo());
//                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
//                    onEvaluatorSuccess(resultEntity, true);
//                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
//                    onEvaluatorError(resultEntity);
//                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
//                    if (resultEntity.getCurString() != null) {
//                        onEvaluatorSuccess(resultEntity, false);
//                    }
//                }
//            }
//
//            @Override
//            public void onVolumeUpdate(int volume) {
//                logger.d("onVolumeUpdate:volume=" + volume);
//                vwvVoiceChatWave.setVolume(volume);
//            }
//        });
        int v = (int) (0.1f * mMaxVolume);
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
    }

    public void stopEvaluator() {
        logger.d("stopEvaluator()");
        isSpeekDone = true;
        isVoice = false;
        mainHandler.removeCallbacks(mNorecogRunnable);
        mainHandler.removeCallbacks(mNovoiceRunnable);
        mainHandler.removeCallbacks(mHintRunnable);
        if (mAudioRequest != null) {
            mAudioRequest.release();
        }
        if (mSpeechUtils != null) {
            vwvVoiceChatWave.setVisibility(View.GONE);
            mSpeechUtils.cancel();
        }
        if (mAM != null) {
            mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
        }
        if (noSpeechTimer != null) {
            noSpeechTimer.cancel();
        }
        tvVoiceChatCountdown.setVisibility(View.GONE);
    }

    private void onEvaluatorSuccess(ResultEntity resultEntity, boolean isSpeechFinished) {
        logger.d("onEvaluatorSuccess():isSpeechFinish=" + isSpeechFinished);
        if (!isSpeekDone) {
            String content = resultEntity.getCurString();
            if (content.length() > 40) {
                content = content.substring(0, 40);
                isSpeechFinished = true;
                isSpeekDone = true;
                mVoiceContent = content;
            }
            if (!"".equals(content)) {
                isVoiceMsgSend = false;
                if (content.length() > 1) {
                    content = content.substring(0, 1).toUpperCase() + content.substring(1);
                } else {
                    content = content.toUpperCase();
                }
            }
            mVoiceContent = content;
            logger.d("=====speech evaluating" + content);
            if (isSpeechFinished) {
                if (noSpeechTimer != null) {
                    noSpeechTimer.cancel();
                }
                tvVoiceChatCountdown.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(content)) {
                    stopEvaluator();
                    setSpeechFinishView(content);
                    btMesOpen.performClick();
                }
            } else {
                if (!TextUtils.isEmpty(content)) {
                    tvVoiceCount.setVisibility(View.VISIBLE);
                    tvVoiceContent.setText(content);
                    tvVoiceCount.setText("(" + tvVoiceContent.getText().toString().length() + "/40)");
                }
            }
        }

    }

    private void onEvaluatorError(ResultEntity resultEntity) {
        logger.d("onEvaluatorError()");
        isSpeechError = true;

        if (resultEntity.getErrorNo() == ResultCode.SPEECH_START_FILE) {
            logger.d("识别失败，请检查存储权限！");
            XESToastUtils.showToast(mContext, "识别失败，请检查存储权限！");
        } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
            startVoiceInput();
            return;
        } else if (resultEntity.getErrorNo() == ResultCode.SPEECH_CANCLE) {
            logger.i("离线测评重新build，要取消到旧的！");
//            startVoiceInput();
            return;
        }
        if (!isSpeekDone) {
            stopEvaluator();
            setSpeechFinishView("");
            btMesOpen.performClick();
        }

    }

    private void setSpeechFinishView(String content) {
        etMessageContent.setText(content);
//        etMessageContent.requestFocus();
        etMessageContent.setSelection(etMessageContent.getText().toString().length());
    }

    private void startVoiceInput() {
        if (mSpeechUtils != null) {
            mSpeechUtils.cancel();
        }
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
        tvVoiceContent.setText(VOICE_RECOG_HINT);
        tvVoiceCount.setText("");
        tvVoiceCount.setVisibility(View.GONE);
        if (mAudioRequest != null) {
            mAudioRequest.request(new AudioRequest.OnAudioRequest() {
                @Override
                public void requestSuccess() {
                    startEvaluator();
                }
            });
        }
        vwvVoiceChatWave.setVisibility(View.VISIBLE);
    }

    /**
     * mic权限申请
     */
    private void inspectMicPermission() {
        XesPermission.checkPermissionNoAlert(mContext, new PermissionCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {
                btMesOpen.performClick();
            }

            @Override
            public void onGuarantee(String permission, int position) {
                initBtMesOpenAnimation(true);
            }
        }, PermissionConfig.PERMISSION_CODE_AUDIO);

    }

    private void uploadLOG(String msg) {
        final Map<String, String> mData = new HashMap<>();
        mData.put("userid", getInfo.getStuId());
        mData.put("liveid", getInfo.getId());
        mData.put("voicecontent", mVoiceContent);
        mData.put("sendmsg", msg);
        uploadCloud(mVoiceFile.getPath(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                XesCloudResult result = (XesCloudResult) objData[0];
                mData.put("url", result.getHttpPath());
                mData.put("upload", "success");
                umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_CHAT, mData);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                mData.put("url", "");
                mData.put("upload", "fail");
                umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_CHAT, mData);
            }
        });
    }

    XesCloudUploadBusiness uploadBusiness;

    private void uploadCloud(String path, final AbstractBusinessDataCallBack callBack) {
        if (uploadBusiness == null) {
            uploadBusiness = new XesCloudUploadBusiness(mContext);
        }
        final CloudUploadEntity entity = new CloudUploadEntity();
        entity.setFilePath(path);
        entity.setCloudPath(CloudDir.LIVE_VOICE_CHAT);
        entity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadBusiness.asyncUpload(entity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {

            }

            @Override
            public void onSuccess(XesCloudResult result) {
                logger.d("upload Success:" + result.getHttpPath());
                callBack.onDataSucess(result);
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.e("upload Error:" + result.getErrorMsg());
                callBack.onDataFail(0, result.getErrorMsg());
            }
        });
    }
}

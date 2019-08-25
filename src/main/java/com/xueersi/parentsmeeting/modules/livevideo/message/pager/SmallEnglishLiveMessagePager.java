package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.ISpeechRecogInterface;
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
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.CommonWordChsItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
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
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * 小英LiveMessagePager，类似于LiveMessagePager，在其基础上面进行修改
 * 献花弹窗，弹幕，聊天信息界面，以及发送消息，小英区别于LiveMessagePager。
 */
public class SmallEnglishLiveMessagePager extends BaseSmallEnglishLiveMessagePager implements LiveAndBackDebug {
    //本组在线人数
    private TextView tvOnlineNum;
    private CommonAdapter<LiveMessageEntity> commonAdapter;

    private static String TAG = "LiveMessagePager";
    /**
     * 聊天，默认开启
     */
    private Button btMesOpen;
    /**
     * 聊天常用语
     */
    private Button btMsgCommon;
    private RelativeLayout rlLivevideoCommonWord;
    ListView lvCommonWord;
    /**
     * 献花，默认关闭
     */
    private Button btMessageFlowers;
    /**
     * 聊天，默认打开
     */
    private CheckBox cbMessageClock;
    /** 聊天人数 */
//    private TextView tvMessageCount;
    /** 聊天IRC一下状态，正在连接，在线等 */
//    private ImageView ivMessageOnline;
    /**
     * 聊天消息
     */
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
    /**
     * 聊天字体大小，最多13个汉字
     */
    private int messageSize = 0;
    /**
     * 献花
     */
    private PopupWindow mFlowerWindow;
    //献花的弹窗
//    private View flowerContentView;
//    private TextView tvMessageGoldLable;
//    private TextView tvMessageGold;
    private String goldNum;
    /**
     * 上次发送消息时间
     */
    private long lastSendMsg;
    private BaseLiveMediaControllerBottom liveMediaControllerBottom;

    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    //        private ImageView ivExpressionCancle;
    private Activity liveVideoActivity;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    /**
     * 竖屏的时候，也添加横屏的消息
     */
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
    //切换语音和键盘输入
    private Button btnMessageSwitch;
    /**
     * 语音聊天输入时布局
     */
    private View rlMessageVoiceContent;
    private TextView tvMessageVoiceContent;
    private TextView tvMessageVoiceCount;
    private VolumeWaveView vwvVoiceChatWave;
    private FangZhengCuYuanTextView tvVoiceChatCountdown;
    /**
     * 语音聊天布局
     */
    private View rlMessageVoiceInput;
    private Button btnMessageStartVoice;
    /**
     * 普通聊天布局
     */
    private View rlMessageTextContent;


    /**
     * 语音保存位置-目录
     */
    File dir;
    /**
     * 音量管理
     */
    private AudioManager mAM;
    /**
     * 最大音量
     */
    private int mMaxVolume;
    /**
     * 当前音量
     */
    private int mVolume = 0;

    private TextView tvMessageCount;

    boolean isVoice = true;

    String mVoiceContent = "";
    String mMsgContent = "";
    /**
     * 语音转文字的聊天是否已发送
     */
    private boolean isVoiceMsgSend = true;
    /**
     * 发送聊天数目
     */
    private int mMsgCount = 0;
    /**
     * 发送语音聊天数目
     */
    private int mVoiceMsgCount = 0;
    private AudioRequest mAudioRequest;
    private String mSpeechFail = "模型正在启动，请稍后";
    /**
     * 是否结束说话
     */
    boolean isSpeekDone = false;
    boolean isRecogSpeeking = false;
    private SpeechUtils speechUtils;
    private ISpeechRecogInterface mISpeechRecogInterface;

    private boolean isShowSpeechRecog = false;
    private long cpuRecogTime;
    private long mRecogtestBeginTime;
    private long mRecogtestEndTime;
    private boolean isTipShow = false;
    private ShareDataManager mSdm;
    /** 是否阻塞中文的layout */
    private ConstraintLayout blockChineseLayout;

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

        rlInfo = mView.findViewById(R.id.rl_livevideo_info);

        rlMessageContent = mView.findViewById(R.id.rl_livevideo_small_english_message_content2);

        rlMessageVoiceContent = mView.findViewById(R.id.rl_livevideo_small_english_voice_message_content);
        tvMessageVoiceContent = mView.findViewById(R.id.tv_livevideo_voicechat_content);
        tvMessageVoiceCount = mView.findViewById(R.id.tv_livevideo_voicechat_word_count);
        vwvVoiceChatWave = mView.findViewById(R.id.vwv_livevideo_voicechat_wave);
        tvVoiceChatCountdown = mView.findViewById(R.id.tv_livevideo_voicechat_countdown);

        rlMessageVoiceInput = mView.findViewById(R.id.rl_livevideo_small_english_voice_message_input);
        btnMessageSwitch = mView.findViewById(R.id.btn_livevideo_small_english_message_switch);
        btnMessageStartVoice = mView.findViewById(R.id.btn_livevideo_small_english_start_voice);

        rlMessageTextContent = mView.findViewById(R.id.rl_livevideo_small_english_text_message_content);
//        btnMessageUseVoice = mView.findViewById(R.id.btn_livevideo_small_english_use_voice);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_small_english_message_content);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_small_english_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_small_english_message_send);
        tvMessageCount = mView.findViewById(R.id.tv_livevideo_small_english_message_count);

        switchFSPanelLinearLayout = mView.findViewById(R.id.rl_livevideo_small_english_message_panelroot);

        blockChineseLayout = mView.findViewById(R.id.layout_livevideo_small_english_block_chinese);
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
        logger.i("initView:width=" + liveVideoPoint.getRightMargin() + "," + liveVideoPoint.y3);
        setBack();
        decorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();

        int colors[] = {0x19FFA63C, 0x32FFA63C, 0x64FFC12C, 0x96FFC12C, 0xFFFFA200};
        vwvVoiceChatWave.setColors(colors);
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
        btnMessageStartVoice.setTypeface(fontFace);
        etMessageContent.setTypeface(fontFace);
        tvMessageCount.setTypeface(fontFace);
        tvMessageVoiceContent.setTypeface(fontFace);
        tvMessageVoiceCount.setTypeface(fontFace);
        return mView;
    }

    @Override
    public void initData() {
        long before = System.currentTimeMillis();
        super.initData();
        mSdm = ShareDataManager.getInstance();
        isShowSpeechRecog = mSdm.getBoolean(SpeechEvaluatorUtils.RECOG_RESULT, false, ShareDataManager
                .SHAREDATA_USER);
        logger.i("speech : isshow" + isShowSpeechRecog);
        cpuRecogTime = mSdm.getLong(SpeechEvaluatorUtils.RECOG_TIME, 2500l, ShareDataManager.SHAREDATA_NOT_CLEAR);
        speechUtils = SpeechUtils.getInstance(mContext.getApplicationContext());
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
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        Map<String, String> mData = new HashMap<>();
        mData.put("userid", getInfo.getStuId());
        mData.put("liveid", getInfo.getId());
        mData.put("volume", mVolume + "");
        mData.put("where", "initData");
        umsAgentDebugSys(LiveVideoConfig.LIVE_VOICE_VOLUME, mData);
        speechUtils.prepar(new SpeechEvaluatorUtils.OnFileSuccess() {
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
                            speechUtils.checkRecogCPUPerformance(new EvaluatorListener() {
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
                                        isShowSpeechRecog = (mRecogtestEndTime - mRecogtestBeginTime) < 3000l ? true : false;
                                        if (isShowSpeechRecog) {
                                            mSdm.put(SpeechEvaluatorUtils.RECOG_RESULT, isShowSpeechRecog, ShareDataManager.SHAREDATA_USER);
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
        dir = LiveCacheFile.geCacheFile(mContext, "livevoice");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
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
        mAudioRequest = ProxUtil.getProxUtil().get(liveVideoActivity, AudioRequest.class);
        if (mAudioRequest != null) {
            mAudioRequest.release();
        }
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
                return new CommonWordChsItem(mContext, this);
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
                mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
                Map<String, String> mData = new HashMap<>();
                mData.put("userid", getInfo.getStuId());
                mData.put("liveid", getInfo.getId());
                mData.put("volume", mVolume + "");
                mData.put("where", "mesopen");
                umsAgentDebugSys(LiveVideoConfig.LIVE_VOICE_VOLUME, mData);
                if (isShowSpeechRecog) {
                    btnMessageSwitch.setVisibility(View.VISIBLE);
                    liveMediaControllerBottom.onChildViewClick(v);
                    rlMessageContent.setVisibility(View.VISIBLE);
                    rlMessageVoiceInput.setVisibility(View.VISIBLE);
                    rlMessageVoiceContent.setVisibility(View.GONE);
                    rlMessageTextContent.setVisibility(View.GONE);
                    btnMessageSwitch.setBackgroundResource(R.drawable.selector_livevideo_small_english_keyborad);
                    isVoice = true;
                    liveMediaControllerBottom.onHide();
                } else {
                    liveMediaControllerBottom.onChildViewClick(v);
                    btnMessageSwitch.setVisibility(View.GONE);
                    rlMessageContent.setVisibility(View.VISIBLE);
                    rlMessageTextContent.setVisibility(View.VISIBLE);
                    rlMessageVoiceInput.setVisibility(View.GONE);
                    rlMessageVoiceContent.setVisibility(View.GONE);
                    KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
                    if (!isTipShow) {
                        XESToastUtils.showToast(mContext, "设备状态暂不支持语音录入，请打字发言");
                        isTipShow = true;
                    }

                }

//                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
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

        etMessageContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvMessageCount.setText(etMessageContent.getText().toString().length() + "/40");
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
                    if (frameLayout.getParent() == null) {
                        decorView.addView(frameLayout, layoutParams);
                    }

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
                                if (!isVoiceMsgSend) {
                                    isVoiceMsgSend = true;
                                    mVoiceMsgCount++;
                                    uploadLOG(msg);
                                }
                                mMsgCount++;
                                etMessageContent.setText("");
                                mVoiceContent = "";
                                mMsgContent = "";
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
                    XESToastUtils.showToast(mContext, "内容不能为空");
                }
            }
        });
        //切换到语音输入
//        btnMessageUseVoice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startVoiceInput();
//
//
//            }
//        });
        //键盘/语音输入切换
        btnMessageSwitch.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onUnDoubleClick(View v) {
                btnMessageSwitch.setEnabled(false);
                if (isShowSpeechRecog) {
                    if (isVoice) {
                        isVoice = false;
                        //隐藏
                        rlMessageVoiceInput.setVisibility(View.GONE);
                        rlMessageVoiceContent.setVisibility(View.GONE);

                        speechToKeyboard("".equals(mVoiceContent) ? mMsgContent : mVoiceContent);
                    } else {
                        QuestionStatic questionStatic = ProxUtil.getProxUtil().get(mContext, QuestionStatic.class);
                        if (questionStatic != null && !questionStatic.isAnaswer() && btnMessageStartVoice.isEnabled()) {
                            if (speechUtils.isRecogOfflineSuccess()) {
                                isVoice = true;
                                InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService
                                        (Context
                                                .INPUT_METHOD_SERVICE);
                                mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
                                switchFSPanelLinearLayout.setVisibility(View.GONE);
                                rlMessageTextContent.setVisibility(View.GONE);
                                btnMessageStartVoice.performClick();
                                btnMessageSwitch.setBackgroundResource(R.drawable
                                        .selector_livevideo_small_english_keyborad);
                            } else {
                                XESToastUtils.showToast(mContext, mSpeechFail);
                            }
                        }
                    }
                } else {
                    XESToastUtils.showToast(mContext, "配置过低无法启动语音聊天");
                }

                btnMessageSwitch.setEnabled(true);
            }
        });
        //开始语音输入
        btnMessageStartVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = XesPermission.hasSelfPermission(liveVideoActivity, Manifest.permission
                        .RECORD_AUDIO);
                QuestionStatic questionStatic = ProxUtil.getProxUtil().get(mContext, QuestionStatic.class);
                if (questionStatic != null && !questionStatic.isAnaswer()) {
                    if (speechUtils.isRecogOfflineSuccess()) {
                        if (!hasPermission) {
                            inspectMicPermission();
                        } else {
                            isRecogSpeeking = false;
                            startVoiceInput();
                        }
                    } else {
                        XESToastUtils.showToast(mContext, mSpeechFail);
                    }
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
                        setMessageLayout(LiveVideoPoint.getInstance(), isShowing);
                        keyboardShowing = isShowing;
                        keyboardShowingListener.onKeyboardShowing(isShowing);
                        if (keyboardShowing) {
                            btMessageExpress.setBackgroundResource(R.drawable
                                    .selector_livevideo_small_english_chat_expression);
                        }
                    }
                });
                KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, btMessageExpress, etMessageContent,
                        new KPSwitchConflictUtil.SwitchClickListener() {
                            @Override
                            public void onClickSwitch(boolean switchToPanel) {
                                if (switchToPanel) {
                                    btMessageExpress.setBackgroundResource(R.drawable
                                            .selector_livevideo_small_english_chat_keyborad_ic);
                                    etMessageContent.clearFocus();
                                } else {
                                    btMessageExpress.setBackgroundResource(R.drawable
                                            .selector_livevideo_small_english_chat_expression);
                                    etMessageContent.requestFocus();
                                }
                            }
                        });
            }
        }, 10);


        etMessageContent.addTextChangedListener(new TextWatcher() {
            private int selectionEnd;
            private int lengthBefore;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                selectionEnd = etMessageContent.getSelectionEnd();
                lengthBefore = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(charSequence)) {
                    btMessageSend.setEnabled(false);
                    btMessageSend.setBackgroundResource(R.drawable.play_chat_sent_btn_disabled);
                } else {
                    btMessageSend.setEnabled(true);
                    btMessageSend.setBackgroundResource(R.drawable.selector_livevideo_small_english_chat_send);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                String repickStr = str.replaceAll("[\u4e00-\u9fa5]", "");
                if (!repickStr.equals(str)) {
                    int selectionAdditon = repickStr.length() - lengthBefore;
                    blockChineseLayout.setVisibility(View.VISIBLE);
                    mView.removeCallbacks(setTipsGoneRunnable);
                    mView.postDelayed(setTipsGoneRunnable, 2000);
                    etMessageContent.removeTextChangedListener(this);
                    editable.replace(0, editable.length(), repickStr);
                    etMessageContent.setSelection(selectionEnd + selectionAdditon);
                    etMessageContent.addTextChangedListener(this);
                }
                if (StringUtils.isSpace(repickStr)) {
                    setBtnDisenable(btMessageSend);
                } else {
                    btMessageSend.setEnabled(true);
                    btMessageSend.setAlpha(1.0f);
                    btMessageSend.setTextColor(Color.WHITE);
                }
//                tvSpeechbulCount.setText(getSpannableText(repickStr.length(), false));
            }
        });
    }

    private Runnable setTipsGoneRunnable = new Runnable() {
        @Override
        public void run() {
            if (blockChineseLayout != null && blockChineseLayout.getVisibility() != View.GONE) {
                blockChineseLayout.setVisibility(View.GONE);
            }
        }
    };

    /**
     * 置灰按钮
     */
    private void setBtnDisenable(TextView view) {
        view.setEnabled(false);
        if (getInfo.getSmallEnglish()) {
            view.setAlpha(0.6f);
        } else {
            view.setTextColor(Color.parseColor("#73FFFFFF"));
        }
    }

    private void startVoiceInput() {
        rlMessageVoiceInput.setVisibility(View.GONE);
        rlMessageVoiceContent.setVisibility(View.VISIBLE);
        vwvVoiceChatWave.setVisibility(View.VISIBLE);
        if (speechUtils != null) {
            speechUtils.cancel();
        }
        tvMessageVoiceContent.setText(VOICE_RECOG_HINT);
        tvMessageVoiceCount.setText("");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAudioRequest != null) {
                    mAudioRequest.request(new AudioRequest.OnAudioRequest() {
                        @Override
                        public void requestSuccess() {
                            if (!isRecogSpeeking) {
                                startEvaluator();
                                isRecogSpeeking = true;
                            }
                        }
                    });
                }
            }
        }, 300);


    }

    @Override
    public void onTitleShow(boolean show) {
        if (rlMessageContent.getVisibility() != View.GONE) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
            if (speechUtils != null) {
                speechUtils.cancel();
            }
            mMsgContent = etMessageContent.getText().toString();
            rlMessageContent.setVisibility(View.GONE);
            rlMessageTextContent.setVisibility(View.GONE);
            rlMessageVoiceContent.setVisibility(View.GONE);
            rlMessageVoiceInput.setVisibility(View.GONE);
            vwvVoiceChatWave.setVisibility(View.GONE);
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

    private void setMessageLayout(LiveVideoPoint liveVideoPoint, boolean isKeyboardShow) {
        int margin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
        RelativeLayout.LayoutParams rmcLayoutParams = (RelativeLayout.LayoutParams) rlMessageContent
                .getLayoutParams();
        if (isKeyboardShow) {
            rmcLayoutParams.setMargins(0, 0, margin, 0);
        } else {
            rmcLayoutParams.setMargins(0, 0, margin, liveVideoPoint.y2);
        }
        rmcLayoutParams.height = SizeUtils.Dp2Px(mContext, 68);
        LayoutParamsUtil.setViewLayoutParams(rlMessageContent, rmcLayoutParams);

        RelativeLayout.LayoutParams repeatLayoutParams = (RelativeLayout.LayoutParams) btnMessageSwitch
                .getLayoutParams();
        int leftmargin = liveVideoPoint.x2 + SizeUtils.Dp2Px(mContext, 10);
        if (repeatLayoutParams.leftMargin != leftmargin) {
            repeatLayoutParams.leftMargin = leftmargin;
            LayoutParamsUtil.setViewLayoutParams(btnMessageSwitch, repeatLayoutParams);
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        {
            setMessageLayout(liveVideoPoint, false);
//
//            RelativeLayout.LayoutParams sendLayoutParams = (RelativeLayout.LayoutParams) btMessageSend
// .getLayoutParams();
//            sendLayoutParams.setMargins(0, 0, margin, 0);
//            btMessageSend.setLayoutParams(sendLayoutParams);
//            RelativeLayout.LayoutParams etLayoutParams = (RelativeLayout.LayoutParams) etMessageContent
// .getLayoutParams();
//            etLayoutParams.setMargins(SizeUtils.Dp2Px(mContext,10), 0, margin, 0);
//            etMessageContent.setLayoutParams(etLayoutParams);
//            RelativeLayout.LayoutParams msvLayoutParams = (RelativeLayout.LayoutParams) btnMessageStartVoice
// .getLayoutParams();
//            msvLayoutParams.setMargins(SizeUtils.Dp2Px(mContext,10), 0, margin, 0);
//            btnMessageStartVoice.setLayoutParams(etLayoutParams);

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
                logger.i("initView:width=" + liveVideoPoint.getRightMargin() + "," + liveVideoPoint.y3);
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
        setBack();
    }

    /**
     * app_livevideo_enteampk_bg_img1_nor
     */
    private void setBack() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        ImageView ivBack = mView.findViewById(R.id.iv_livevideo_message_small_bg);
        if (ivBack == null) {
            return;
        }
        RelativeLayout.LayoutParams bgParams = (RelativeLayout.LayoutParams) ivBack.getLayoutParams();
        int width = liveVideoPoint.x4 - liveVideoPoint.x3 + 2;
        int height = width * 287 / 501;
        int minHeight = SizeUtils.Dp2Px(mContext, 167 * 287 / 501);
        logger.d("setBack:height=" + height + ",minHeight=" + minHeight);
        if (height < minHeight) {
            height = minHeight;
        }
        if (bgParams.width != width || bgParams.height != height) {
            bgParams.width = width;
            bgParams.height = height;
            ivBack.setLayoutParams(bgParams);
        }
    }

    /**
     * 聊天开始连接
     */
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

    /**
     * 聊天连上
     */
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
        }
    }


    // 03.16 设置模拟的聊天连接
    public void onConnects() {
        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
//        ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
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
//                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
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

                            int goldSend = 0;
                            if (smallEnglishSendFlowerPager.getWhichFlower() == FLOWERS_SMALL) {
                                goldSend = 10;
                            } else if (smallEnglishSendFlowerPager.getWhichFlower() == FLOWERS_MIDDLE) {
                                goldSend = 50;
                            } else if (smallEnglishSendFlowerPager.getWhichFlower() == FLOWERS_BIG) {
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

        /**
         * 正在答题
         */
        void isAnaswer();

        /**
         * 点击没有开启献花时
         */
        void clickIsnotOpenbarrage();

        /**
         * 点击是辅导状态时
         */
        void clickTran();

        /**
         * 点击没有选择时
         */
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

    /**
     * 被禁言
     */
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

    /**
     * 关闭开启聊天
     */
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

    /**
     * 关闭开启弹幕
     */
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
    public void onOpenVoicebarrage(final boolean openbarrage, boolean fromNotice) {
        logger.i("OpenVoicebarrage:" + openbarrage);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (openbarrage) {
//                    speechToKeyboard(mVoiceContent);
                    stopEvaluator();
                    etMessageContent.setText(mVoiceContent);
                    etMessageContent.requestFocus();
                    etMessageContent.setSelection(etMessageContent.getText().toString().length());
                    btnMessageSwitch.setBackgroundResource(R.drawable.selector_livevideo_small_english_voice);
                    onTitleShow(false);
                    isVoice = false;
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

    @Override
    public void onOpenVoiceNotic(final boolean openVoice, final String type) {
        logger.i("openVoice:" + openVoice + " from:" + type);
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (openVoice) {
//                    speechToKeyboard(mVoiceContent);
                    if (rlMessageVoiceContent.getVisibility() != View.GONE) {
                        rlMessageVoiceContent.setVisibility(View.GONE);
                        vwvVoiceChatWave.setVisibility(View.GONE);
                        stopEvaluator();
                        etMessageContent.setText(mVoiceContent);
                        etMessageContent.requestFocus();
                        etMessageContent.setSelection(etMessageContent.getText().toString().length());
                        btnMessageSwitch.setBackgroundResource(R.drawable.selector_livevideo_small_english_voice);
                        onTitleShow(false);
                        btnMessageStartVoice.setEnabled(false);
                        isVoice = false;
                    }
                    InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService
                            (Context
                                    .INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
                    switchFSPanelLinearLayout.setVisibility(View.GONE);
                } else {
                    btnMessageStartVoice.setEnabled(true);
                }
            }
        });
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
                            UmsAgentManager.umsAgentException(ContextManager.getContext(), TAG + mContext + "," +
                                    sender + "," + type, e);
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
        logger.e("sender:" + sender);
    }

    @Override
    public void onOtherDisable(String id, String name, boolean disable) {

    }


    @Override
    public void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, final boolean isShow) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (!isShow) {
                    btnMessageStartVoice.setEnabled(true);
                }
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
    public void onDestroy() {
        super.onDestroy();
        if (noSpeechTimer != null) {
            noSpeechTimer.cancel();
        }
        if (speechUtils != null) {
            speechUtils.cancel();
        }
        if (vwvVoiceChatWave != null) {
            vwvVoiceChatWave.stop();
            vwvVoiceChatWave.setVisibility(View.GONE);
        }
        if (mAM != null) {
            mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
            Map<String, String> mData = new HashMap<>();
            mData.put("userid", getInfo.getStuId());
            mData.put("liveid", getInfo.getId());
            mData.put("volume", mVolume + "");
            mData.put("where", "onDestroy");
            umsAgentDebugSys(LiveVideoConfig.LIVE_VOICE_VOLUME, mData);
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("userid", getInfo.getStuId());
        mData.put("liveid", getInfo.getId());
        mData.put("msgcount", String.valueOf(mMsgCount));
        mData.put("voicemsgcount", String.valueOf(mVoiceMsgCount));
        umsAgentDebugSys(LiveVideoConfig.LIVE_VOICE_CHAT, mData);
    }
    /**
     * ************************************************** 语音识别 **************************************************
     */
    /** 语音评测工具类 */
//    private SpeechEvaluatorUtils speechUtils;
    /**
     * 是不是评测成功
     */
    private boolean isSpeechSuccess = false;
    private final static String VOICE_RECOG_HINT = "语音输入中，请大声说英语";
    private final static String VOICE_RECOG_NOVOICE_HINT = "抱歉没听清，请大点声重说哦";
    private final static String VOICE_RECOG_NORECOG_HINT = "请手动输入或重说";
    Runnable mHintRunnable = new Runnable() {
        @Override
        public void run() {
            if ("".equals(mVoiceContent)) {
                tvMessageVoiceContent.setText(VOICE_RECOG_NOVOICE_HINT);
            }
        }
    };
    Runnable mNovoiceRunnable = new Runnable() {
        @Override
        public void run() {
            if ("".equals(mVoiceContent)) {
                tvMessageVoiceContent.setText(VOICE_RECOG_NORECOG_HINT);
            }
        }
    };
    Runnable mNorecogRunnable = new Runnable() {
        @Override
        public void run() {
            if ("".equals(mVoiceContent)) {
//                btnMessageSwitch.performClick();
                speechToKeyboard("");
            }
        }
    };
    /**
     * 计时器 超过三十秒截停
     */
    CountDownTimer noSpeechTimer = new CountDownTimer(31000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (!liveVideoActivity.isFinishing()) {
                if (millisUntilFinished < 6000) {
                    tvVoiceChatCountdown.setVisibility(View.VISIBLE);
                    vwvVoiceChatWave.setVisibility(View.GONE);
                    tvVoiceChatCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                }
            }
        }

        @Override
        public void onFinish() {
            btnMessageSwitch.performClick();
            tvVoiceChatCountdown.setVisibility(View.GONE);
        }
    };

    File mVoiceFile;
    long mBeginOfSpeech = 0;

    private void startEvaluator() {
        mVoiceContent = "";
        logger.i("startEvaluator()");
        mVoiceFile = new File(dir, "voicechat" + System.currentTimeMillis() + ".mp3");
        SpeechParamEntity param = new SpeechParamEntity();
        param.setRecogType(SpeechConfig.SPEECH_RECOGNITIYON_OFFINE);
        param.setLocalSavePath(mVoiceFile.getPath());
        param.setVad_pause_sec("1.2");
        param.setVad_max_sec("30");
        speechUtils.startRecog(param, new EvaluatorListenerWithPCM() {
            @Override
            public void onBeginOfSpeech() {
                logger.i("onBeginOfSpeech");
                isSpeekDone = false;
                noSpeechTimer.start();
                //3秒没有检测到声音提示
                mainHandler.postDelayed(mHintRunnable, 3000);
                //6秒仍没检测到说话
                mainHandler.postDelayed(mNovoiceRunnable, 6000);
                //7秒没声音自动停止
                mainHandler.postDelayed(mNorecogRunnable, 7000);
                mBeginOfSpeech = System.currentTimeMillis();
            }

            @Override
            public void onResult(final ResultEntity resultEntity) {
                logger.i("onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity
                        .getErrorNo());
                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    onEvaluatorSuccess(resultEntity, true);
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    onEvaluatorError(resultEntity);
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    onEvaluatorSuccess(resultEntity, false);
                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
                logger.i("onVolumeUpdate:volume=" + volume);
                vwvVoiceChatWave.setVolume(volume * 2);
            }

            @Override
            public void onRecordPCMData(short[] pcmBuffer, int length) {

            }
        });
        int v = (int) (0.05f * mMaxVolume);
        mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
        Map<String, String> mData = new HashMap<>();
        mData.put("userid", getInfo.getStuId());
        mData.put("liveid", getInfo.getId());
        mData.put("volume", v + "");
        mData.put("where", "startEvaluator");
        umsAgentDebugSys(LiveVideoConfig.LIVE_VOICE_VOLUME, mData);
    }

    public void stopEvaluator() {
        logger.i("stopEvaluator()");
        if (isRecogSpeeking && mAudioRequest != null) {
            mAudioRequest.release();

        }
        if (isRecogSpeeking && mAM != null) {
            mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
            Map<String, String> mData = new HashMap<>();
            mData.put("userid", getInfo.getStuId());
            mData.put("liveid", getInfo.getId());
            mData.put("volume", mVolume + "");
            mData.put("where", "stopEvaluator");
            umsAgentDebugSys(LiveVideoConfig.LIVE_VOICE_VOLUME, mData);
        }
        isSpeekDone = true;
        isRecogSpeeking = false;
        mView.removeCallbacks(mHintRunnable);
        mView.removeCallbacks(mNorecogRunnable);
        mView.removeCallbacks(mNovoiceRunnable);
        vwvVoiceChatWave.setVisibility(View.GONE);
        if (speechUtils != null) {
            speechUtils.cancel();
        }
        if (noSpeechTimer != null) {
            noSpeechTimer.cancel();
        }
        tvVoiceChatCountdown.setVisibility(View.GONE);
    }

    private void onEvaluatorSuccess(ResultEntity resultEntity, boolean isSpeechFinished) {
        logger.i("onEvaluatorSuccess():isSpeechFinish=" + isSpeechFinished);
        String content = resultEntity.getCurString();
        //语音录入，限制40字以内
        if (!isSpeekDone) {
            if (content.length() > 40) {
                content = content.substring(0, 40);
                isSpeechFinished = true;
                isSpeekDone = true;
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
            logger.i("=====speech evaluating" + content);
            if (isSpeechFinished) {
                if (noSpeechTimer != null) {
                    noSpeechTimer.cancel();
                }
                tvVoiceChatCountdown.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(content)) {
                    speechToKeyboard(content);
                }
            } else {
                if (!TextUtils.isEmpty(content)) {
                    tvMessageVoiceContent.setText(content);
                    tvMessageVoiceCount.setText("(" + tvMessageVoiceContent.getText().toString().length() + "/40)");
                }
            }
        }

    }

    /**
     * 识别出错
     *
     * @param resultEntity
     */
    private void onEvaluatorError(ResultEntity resultEntity) {
        logger.i("onEvaluatorError()");
        if (resultEntity.getErrorNo() == ResultCode.SPEECH_START_FILE) {
            logger.i("识别失败，请检查存储权限！");
            XESToastUtils.showToast(mContext, "识别失败，请检查存储权限！");
        } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
            isRecogSpeeking = false;
            startVoiceInput();
            return;
        } else if (resultEntity.getErrorNo() == ResultCode.SPEECH_CANCLE) {
            logger.i("离线测评重新build，要取消到旧的！");
//            startVoiceInput();
            return;
        }
        btnMessageSwitch.performClick();
    }

    /**
     * 语音识别状态转键盘输入状态，
     *
     * @param content
     */
    private void speechToKeyboard(String content) {
        stopEvaluator();
        rlMessageVoiceInput.setVisibility(View.GONE);
        //隐藏语音识别内容布局
        rlMessageVoiceContent.setVisibility(View.GONE);
        vwvVoiceChatWave.setVisibility(View.GONE);
        //重置识别显示text内容
        tvMessageVoiceCount.setText("");
        //显示文本框布局
        rlMessageTextContent.setVisibility(View.VISIBLE);
        etMessageContent.setText(content);
        etMessageContent.requestFocus();
        etMessageContent.setSelection(etMessageContent.getText().toString().length());
        btnMessageSwitch.setBackgroundResource(R.drawable.selector_livevideo_small_english_voice);
        isVoice = false;
    }


    /**
     * Mic权限判定
     */
    private void inspectMicPermission() {

        XesPermission.checkPermissionNoAlert(mContext, new PermissionCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {
                rlMessageTextContent.setVisibility(View.VISIBLE);
                rlMessageVoiceInput.setVisibility(View.GONE);
                speechToKeyboard(etMessageContent.getText().toString());
            }

            @Override
            public void onGuarantee(String permission, int position) {
                isRecogSpeeking = false;
                startVoiceInput();
            }
        }, PermissionConfig.PERMISSION_CODE_AUDIO);
    }

    /**
     * 上传交互日志、阿里云
     *
     * @param msg
     */
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
                mData.put("upload", "fail");
                mData.put("url", "");
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
                logger.i("upload Success:" + result.getHttpPath());
                callBack.onDataSucess(result);
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.e("upload Error:" + result.getErrorMsg());
                callBack.onDataFail(0, result.getErrorMsg());
            }
        });

    }

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
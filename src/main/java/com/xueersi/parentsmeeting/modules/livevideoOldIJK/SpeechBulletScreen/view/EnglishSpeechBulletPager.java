package com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.Constants;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.Contract.EnglishSpeechBulletContract;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.page.EnglishSpeekPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.SmallEnglishMicTipDialog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import master.flame.danmaku.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.ui.widget.DanmakuView;

/**
 * Created by ZhangYuansun on 2018/9/14
 * 小英语音弹幕
 * MVP：View层
 */

public class EnglishSpeechBulletPager extends LiveBasePager implements EnglishSpeechBulletContract
        .EnglishSpeechBulletView {
    /**
     * MVP:Presenter层接口
     */
    private EnglishSpeechBulletContract.EnglishSpeechBulletPresenter presenter;
    /**
     * 小学英语
     */
    private boolean isSmallEnglish = false;
    /**
     * 直播布局
     */
    private RelativeLayout rootView;
    /**
     * 语音弹幕布局
     */
    private RelativeLayout rlSpeechBulContent;
    /**
     * 根布局
     */
    private RelativeLayout rlSpeechBulRoot;
    /**
     * 输入框布局
     */
    private RelativeLayout rlSpeechbulInputContent;
    /**
     * 语音录入标题
     */
    private TextView tvSpeechbulTitle;
    /**
     * 标题字数
     */
    private TextView tvSpeechbulTitleCount;
    /**
     * 音量波形
     */
    private VolumeWaveView vwvSpeechbulWave;
    /**
     * 30S截停倒计时
     */
    private TextView tvSpeechbulCountdown;
    /**
     * 输入框
     */
    private EditText etSpeechbulWords;
    /**
     * 输入字数
     */
    private TextView tvSpeechbulCount;
    /**
     * 重说按钮
     */
    private TextView tvSpeechbulRepeat;
    /**
     * 发送按钮
     */
    private TextView tvSpeechbulSend;
    /**
     * 倒计时提示
     */
    private TextView tvSpeechbulCloseTip;
    /**
     * 只能输入英文的提示
     */
    private RelativeLayout rlSpeechbulTips;
    /**
     * 软键盘等高布局
     */
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    /**
     * 弹幕
     */
    private DanmakuView mDanmakuView;
    /**
     * 弹幕上下文
     */
    protected DanmakuContext mDanmakuContext;
    /**
     * 点赞页面
     */
    private EnglishSpeekPager englishSpeekPager;

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
    /**
     * 语音评测工具类
     */
    private SpeechUtils mSpeechUtils;
    /**
     * 语音评测参数
     */
    private SpeechParamEntity mParam;
    /**
     * 语音保存位置
     */
    private File dir;
    /**
     * 语音保存文件
     */
    private File saveFile;
    /**
     * 语音请求和释放
     */
    private AudioRequest audioRequest;
    /**
     * 阿里云业务
     */
    private XesCloudUploadBusiness uploadBusiness;
    /**
     * 是否拥有麦克风权限
     */
    private boolean hasAudioPermission = false;
    /**
     * 是否正在开启语音弹幕
     */
    private boolean isShowingSpeechBullet = false;
    /**
     * 该设备是否支持语音识别
     */
    private boolean showSpeechRecog = false;
    /**
     * 是否有有效的语音识别结果
     */
    private boolean hasValidSpeechInput = false;
    /**
     * 是否恢复了音量
     */
    private boolean isVolumeResume = true;
    /**
     * 弱引用Handler
     */
    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });
    /**
     * 水平方向内边距
     */
    private int MARGIN_HORIZONTAL = 10;
    /**
     * 语音输入最大字符数
     */
    private int MAX_INPUT_CHAR_NUMBER = 60;
    /**
     * 初高中表扬
     */
    private ImageView ivPraise;

    public EnglishSpeechBulletPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    /**
     * 初始化布局
     */
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_english_speech_bullet, null);
        rlSpeechBulRoot = mView.findViewById(R.id.rl_livevideo_speechbul_root);
        tvSpeechbulCloseTip = mView.findViewById(R.id.tv_livevideo_speechbul_closetip);
        switchFSPanelLinearLayout = mView.findViewById(R.id.rl_livevideo_speechbul_panelroot);
        tvSpeechbulTitle = mView.findViewById(R.id.tv_livevideo_speechbul_title);
        tvSpeechbulTitleCount = mView.findViewById(R.id.tv_livevideo_speechbul_title_count);
        vwvSpeechbulWave = mView.findViewById(R.id.vwv_livevideo_speechbul_wave);
        tvSpeechbulCountdown = mView.findViewById(R.id.tv_livevideo_speechbul_countdown);
        etSpeechbulWords = mView.findViewById(R.id.et_livevideo_speechbul_words);
        tvSpeechbulCount = mView.findViewById(R.id.tv_livevideo_speechbul_count);
        tvSpeechbulRepeat = mView.findViewById(R.id.tv_livevideo_speechbul_repeat);
        tvSpeechbulSend = mView.findViewById(R.id.tv_livevideo_speechbul_send);
        rlSpeechbulInputContent = mView.findViewById(R.id.rl_livevideo_speechbul_input);
        rlSpeechbulTips = mView.findViewById(R.id.rl_livevideo_speechbul_tips);
        int colors[] = {0x19FFA63C, 0x32FFA63C, 0x64FFC12C, 0x96FFC12C, 0xFFFFA200};
        vwvSpeechbulWave.setColors(colors);
        vwvSpeechbulWave.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vwvSpeechbulWave.setLinearGradient(new LinearGradient(0, 0, vwvSpeechbulWave.getMeasuredWidth(), 0,
                        new int[]{0xFFEA9CF9, 0xFF9DBBFA, 0xFF80F9FD}, new float[]{0, 0.5f, 1.0f}, Shader.TileMode
                        .CLAMP));
                vwvSpeechbulWave.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        vwvSpeechbulWave.setBackColor(Color.TRANSPARENT);
        setVideoLayout(LiveVideoPoint.getInstance());
        return mView;
    }

    private void loadPrimarySkin() {
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        etSpeechbulWords.setTypeface(fontFace);
        tvSpeechbulTitle.setTypeface(fontFace);
        tvSpeechbulTitleCount.setTypeface(fontFace);
        tvSpeechbulCount.setTypeface(fontFace);
        tvSpeechbulCloseTip.setTypeface(fontFace);
    }

    /**
     * 加载初高中皮肤
     */
    private void loadJuniorSkin() {
        MARGIN_HORIZONTAL = 20;
        MAX_INPUT_CHAR_NUMBER = 80;
        DANMU_TEXT_COLOR = "#D9953D";
        //只能输入英文的提示
        RelativeLayout.LayoutParams tipsLayoutParams = (RelativeLayout.LayoutParams) rlSpeechbulTips.getLayoutParams();
        tipsLayoutParams.bottomMargin = SizeUtils.Dp2Px(mContext, 4);
        rlSpeechbulTips.setLayoutParams(tipsLayoutParams);
        rlSpeechbulTips.setBackgroundResource(R.drawable.vioceinput_tips_junior_english_bg);
        TextView textView = mView.findViewById(R.id.tv_livevideo_speechbul_tips_text);
        textView.setCompoundDrawables(null, null, null, null);
        textView.setTextSize(14);
        textView.setTextColor(mContext.getResources().getColor(R.color.white));
        textView.setPadding(0, 0, 0, SizeUtils.Dp2Px(mContext, 6));
        //重说换肤
        tvSpeechbulRepeat.setText("重说");
        tvSpeechbulRepeat.setTextColor(Color.parseColor("#F65345"));
        tvSpeechbulRepeat.setTextSize(16);
        tvSpeechbulRepeat.setGravity(Gravity.CENTER);
        tvSpeechbulRepeat.setBackgroundResource(R.drawable.selector_livevideo_junior_english_speechbul_repeat);
        RelativeLayout.LayoutParams repeatLayoutParams = (RelativeLayout.LayoutParams) tvSpeechbulRepeat
                .getLayoutParams();
        repeatLayoutParams.leftMargin = SizeUtils.Dp2Px(mContext, MARGIN_HORIZONTAL);
        repeatLayoutParams.width = SizeUtils.Dp2Px(mContext, 72);
        tvSpeechbulRepeat.setLayoutParams(repeatLayoutParams);
        //发送换肤
        tvSpeechbulSend.setText("发送");
        tvSpeechbulSend.setTextColor(mContext.getResources().getColor(R.color.white));
        tvSpeechbulSend.setTextSize(16);
        tvSpeechbulSend.setGravity(Gravity.CENTER);
        tvSpeechbulSend.setBackgroundResource(R.drawable.selector_livevideo_junior_english_chat_send);
        RelativeLayout.LayoutParams sendLayoutParams = (RelativeLayout.LayoutParams) tvSpeechbulSend.getLayoutParams();
        sendLayoutParams.rightMargin = SizeUtils.Dp2Px(mContext, MARGIN_HORIZONTAL);
        sendLayoutParams.width = SizeUtils.Dp2Px(mContext, 72);
        tvSpeechbulSend.setLayoutParams(sendLayoutParams);
        //输入框换肤
        etSpeechbulWords.setBackgroundResource(R.drawable.livevideo_btn_junior_repeat_normal);
        RelativeLayout.LayoutParams wordsLayoutParams = (RelativeLayout.LayoutParams) etSpeechbulWords
                .getLayoutParams();
        sendLayoutParams.leftMargin = SizeUtils.Dp2Px(mContext, 12);
        sendLayoutParams.rightMargin = SizeUtils.Dp2Px(mContext, 12);
        etSpeechbulWords.setLayoutParams(wordsLayoutParams);
        etSpeechbulWords.setFilters(new InputFilter[]{new InputFilter.LengthFilter(80)});
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        if (isSmallEnglish) {
            loadPrimarySkin();
        } else {
            loadJuniorSkin();
        }
        ShareDataManager sdm = ShareDataManager.getInstance();
        showSpeechRecog = sdm.getBoolean(SpeechEvaluatorUtils.RECOG_RESULT, false,
                ShareDataManager.SHAREDATA_USER);
        if (!showSpeechRecog) {
            XESToastUtils.showToast(mContext, "设备状态暂不支持语音录入，请打字发言");
            setBtnDisenable(tvSpeechbulRepeat);
            startTextInput("");
            return;
        }
        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeechbulWave.start();
                umsAgentDebugPvSno3();
            }
        }, 100);
        if (mSpeechUtils == null) {
            mSpeechUtils = SpeechUtils.getInstance(mContext.getApplicationContext());
            mSpeechUtils.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_EN);
        }
        mParam = new SpeechParamEntity();
        dir = LiveCacheFile.geCacheFile(mContext, "livevoice");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
        hasAudioPermission = XesPermission.hasSelfPermission(mContext, Manifest.permission.RECORD_AUDIO); // 检查用户麦克风权限
        if (!hasAudioPermission) {
            XesPermission.checkPermissionNoAlert(mContext, new LiveActivityPermissionCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onDeny(String permission, int position) {
                    setBtnDisenable(tvSpeechbulRepeat);
                    startTextInput("");
                }

                @Override
                public void onGuarantee(String permission, int position) {
                    startSpeechInput();
                }
            }, PermissionConfig.PERMISSION_CODE_AUDIO);
        } else {
            startSpeechInput();
        }
    }

    @Override
    public void initListener() {
        //输入框
        etSpeechbulWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etSpeechbulWords);
            }
        });
        //监听软键盘的状态变化
        etSpeechbulWords.addTextChangedListener(new TextWatcher() {
            private int selectionEnd;
            private int lengthBefore;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                selectionEnd = etSpeechbulWords.getSelectionEnd();
                lengthBefore = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                String repickStr = str.replaceAll("[\u4e00-\u9fa5]", "");
                if (!repickStr.equals(str)) {
                    int selectionAdditon = repickStr.length() - lengthBefore;
                    rlSpeechbulTips.setVisibility(View.VISIBLE);
                    mWeakHandler.removeCallbacks(setTipsGoneRunnable);
                    mWeakHandler.postDelayed(setTipsGoneRunnable, 2000);
                    etSpeechbulWords.removeTextChangedListener(this);
                    editable.replace(0, editable.length(), repickStr);
                    etSpeechbulWords.setSelection(selectionEnd + selectionAdditon);
                    etSpeechbulWords.addTextChangedListener(this);
                }
                if (StringUtils.isSpace(repickStr)) {
                    setBtnDisenable(tvSpeechbulSend);
                } else {
                    tvSpeechbulSend.setEnabled(true);
                    tvSpeechbulSend.setAlpha(1.0f);
                    tvSpeechbulSend.setTextColor(Color.WHITE);
                }
                tvSpeechbulCount.setText(repickStr.length() + "/" + MAX_INPUT_CHAR_NUMBER);
            }
        });
        //监听软键盘发送按钮
        etSpeechbulWords.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent
                        .KEYCODE_ENTER)) {
                    if (!StringUtils.isSpace(etSpeechbulWords.getText().toString())) {
                        tvSpeechbulSend.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
        //重新开启语音评测
        tvSpeechbulRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEvaluator();
                umsAgentDebugInterSno6();
            }
        });
        //发送语音弹幕
        tvSpeechbulSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fainalText = etSpeechbulWords.getText().toString();
                if (saveFile != null) {
                    uploadCloud(saveFile.getPath(), new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            XesCloudResult result = (XesCloudResult) objData[0];
                            aliyunUrl = result.getHttpPath();
                            umsAgentDebugInterSno7();
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            aliyunUrl = "";
                            umsAgentDebugInterSno7();
                        }
                    });
                } else {
                    aliyunUrl = "";
                    umsAgentDebugInterSno7();
                }
                closeSpeechBullet(false);
                addDanmaku("我", etSpeechbulWords.getText().toString(), presenter.getHeadImgUrl(), false);

                if (ShareDataManager.getInstance().getString(ShareBusinessConfig.SP_VOICE_BULLET_ID, "",
                        ShareDataManager.SHAREDATA_USER).equals(presenter.getVoiceId())) {
                    return;
                }
                ShareDataManager.getInstance().put(ShareBusinessConfig.SP_VOICE_BULLET_ID, presenter.getVoiceId(),
                        ShareDataManager.SHAREDATA_USER);
                presenter.uploadSpeechBulletScreen(etSpeechbulWords.getText().toString(), new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {

                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {

                    }
                });
            }
        });

        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtil.attach((Activity) mContext, switchFSPanelLinearLayout);
            }
        }, 10);

        //重要！键盘高度发生变化时，刷新键盘高度
        switchFSPanelLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (switchFSPanelLinearLayout.getHeight() != KeyboardUtil.getValidPanelHeight(mContext)) {
                    switchFSPanelLinearLayout.refreshHeight(KeyboardUtil.getValidPanelHeight(mContext));
                }
            }
        });

        rlSpeechBulRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
            }
        });
    }

    private Runnable setTipsGoneRunnable = new Runnable() {
        @Override
        public void run() {
            rlSpeechbulTips.setVisibility(View.GONE);
        }
    };

    /**
     * ************************************************** View层接口实现 **************************************************
     */

    /**
     * 展示语音弹幕
     */
    @Override
    public void showSpeechBullet(final RelativeLayout rootView) {
        if (isShowingSpeechBullet) {
            return;
        }
        logger.i("showSpeechBullet");
        this.rootView = rootView;
        isShowingSpeechBullet = true;
        showStartSpeechBulletToast("老师开启了语音弹幕");
        mWeakHandler.postDelayed(showSpeechBulletRunnable, 2000);
        initUmsAgentData();
        umsAgentDebugInterSno2();
    }

    /**
     * 展示老师开启语音弹幕Toast
     */
    private void showStartSpeechBulletToast(String tips) {
        if (isSmallEnglish) {
            SmallEnglishMicTipDialog startSpeechBulletToast = new SmallEnglishMicTipDialog(mContext);
            startSpeechBulletToast.setText(tips);
            startSpeechBulletToast.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf"));
            startSpeechBulletToast.showDialogAutoClose(2000);
        } else {
            Toast toast = new Toast(mContext);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            View view = LayoutInflater.from(mContext).inflate(R.layout.toast_livevideo_junior, null);
            TextView textView = view.findViewById(R.id.tv_toast_livevideo_junior_text);
            textView.setText(tips);
            toast.setView(view);
            toast.show();
        }
    }

    /**
     * 初始化日志数据
     */
    private void initUmsAgentData() {
        aliyunUrl = "";
        originalText = "";
        fainalText = "";
    }

    private Runnable showSpeechBulletRunnable = new Runnable() {
        @Override
        public void run() {
            if (rlSpeechBulContent == null) {
                rlSpeechBulContent = new RelativeLayout(mContext);
                rlSpeechBulContent.setId(R.id.rl_livevideo_content_speechbul);
                rootView.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
            if (mDanmakuView == null) {
                rlSpeechBulContent.addView(initDanmaku(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDanmakuView.getLayoutParams();
                layoutParams.topMargin = SizeUtils.Dp2Px(mContext, 10);
                mDanmakuView.setLayoutParams(layoutParams);
            }
            rlSpeechBulContent.addView(initView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rlSpeechBulContent.setVisibility(View.VISIBLE);
            initData();
            initListener();
        }
    };

    /**
     * 关闭语音弹幕
     *
     * @param hasTip 是否弹关闭提示
     */
    @Override
    public void closeSpeechBullet(boolean hasTip) {
        logger.i("closeSpeechBullet");
        if (hasTip) {
            mWeakHandler.removeCallbacks(showSpeechBulletRunnable);
            if (mDanmakuView != null) {
                mDanmakuView.removeAllDanmakus(false);
                latestDanmakuAddtime = -300;
                danmakuAddCount = 0;
            }
            showStartSpeechBulletToast("老师关闭了语音弹幕");
            umsAgentDebugInterSno9();
        }
        if (!isShowingSpeechBullet) {
            return;
        }
        isShowingSpeechBullet = false;

        if (rlSpeechBulRoot != null && rlSpeechBulContent != null) {
            KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
            rlSpeechBulContent.removeView(rlSpeechBulRoot);
            rlSpeechBulRoot.setClickable(false);
        }
        stopEvaluator();
    }

    /**
     * 添加弹幕
     *
     * @param name       名字
     * @param msg        内容
     * @param headImgUrl 头像Url
     * @param rootView
     */
    @Override
    public void receiveDanmakuMsg(String name, String msg, String headImgUrl, boolean isGuset, RelativeLayout
            rootView) {
        if (rlSpeechBulContent == null) {
            rlSpeechBulContent = new RelativeLayout(mContext);
            rlSpeechBulContent.setId(R.id.rl_livevideo_content_speechbul);
            rootView.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (mDanmakuView == null) {
            rlSpeechBulContent.addView(initDanmaku(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(msg) & !StringUtils.isEmpty(headImgUrl)) {
            addDanmaku(name, msg, headImgUrl, isGuset);
        }
    }

    /**
     * 表扬消息
     *
     * @param msg 内容
     */
    @Override
    public void receivePraiseMsg(String msg) {
        if (rlSpeechBulContent == null) {
            return;
        }
        if (isSmallEnglish) {
            if (englishSpeekPager == null) {
                englishSpeekPager = new EnglishSpeekPager(mContext);
            } else {
                //移出之前的弹窗
                if (englishSpeekPager.getRootView().getParent() == rlSpeechBulContent) {
                    rlSpeechBulContent.removeView(englishSpeekPager.getRootView());
                }
            }
            rlSpeechBulContent.removeCallbacks(removeViewRunnable);
            englishSpeekPager.updateStatus(EnglishSpeekPager.PRAISE);
            rlSpeechBulContent.addView(englishSpeekPager.getRootView(), englishSpeekPager.getLayoutParams());
            rlSpeechBulContent.postDelayed(removeViewRunnable, 1000);
        } else {
            if (ivPraise == null) {
                ivPraise = new ImageView(mContext);
                ivPraise.setImageResource(R.drawable.bg_livevideo_junior_praise);
                rlSpeechBulContent.addView(ivPraise, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivPraise.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                ivPraise.setLayoutParams(layoutParams);
            }
            ivPraise.setVisibility(View.VISIBLE);
            rlSpeechBulContent.removeCallbacks(removeViewRunnable);
            rlSpeechBulContent.postDelayed(removeViewRunnable, 1000);
        }
        umsAgentDebugPvSno8();
    }

    private Runnable removeViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (isSmallEnglish) {
                if (englishSpeekPager != null && englishSpeekPager.getRootView().getParent() == rlSpeechBulContent) {
                    rlSpeechBulContent.removeView(englishSpeekPager.getRootView());
                }
            } else {
                ivPraise.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (tvSpeechbulRepeat == null || tvSpeechbulSend == null) {
            return;
        }
        int marginLeft = liveVideoPoint.x2 + SizeUtils.Dp2Px(mContext, MARGIN_HORIZONTAL);
        int marginRight = liveVideoPoint.screenWidth - liveVideoPoint.x4 + SizeUtils.Dp2Px(mContext, MARGIN_HORIZONTAL);
        RelativeLayout.LayoutParams repeatLayoutParams = (RelativeLayout.LayoutParams) tvSpeechbulRepeat
                .getLayoutParams();
        repeatLayoutParams.leftMargin = marginLeft;
        tvSpeechbulRepeat.setLayoutParams(repeatLayoutParams);

        RelativeLayout.LayoutParams sendLayoutParams = (RelativeLayout.LayoutParams) tvSpeechbulSend.getLayoutParams();
        sendLayoutParams.rightMargin = marginRight;
        tvSpeechbulSend.setLayoutParams(sendLayoutParams);
    }

    @Override
    public void setSmallEnglish(boolean isSmallEnglish) {
        this.isSmallEnglish = isSmallEnglish;
    }

    /**
     * 设置presenter
     *
     * @param presenter
     */
    @Override
    public void setPresenter(EnglishSpeechBulletContract.EnglishSpeechBulletPresenter presenter) {
        this.presenter = presenter;
    }


    /**
     * ************************************************** 语音识别 **************************************************
     */
    private final static String VOICE_RECOG_HINT = "语音输入中，请大声说英语";
    private final static String VOICE_RECOG_NOVOICE_HINT = "抱歉没听清，请大点声重说哦";
    private final static String VOICE_RECOG_NORECOG_HINT = "请手动输入或重说";

    private Runnable mHintRunnable = new Runnable() {
        @Override
        public void run() {
            if (!hasValidSpeechInput) {
                tvSpeechbulTitle.setText(VOICE_RECOG_NOVOICE_HINT);
            }
        }
    };
    private Runnable mNovoiceRunnable = new Runnable() {
        @Override
        public void run() {
            if (!hasValidSpeechInput) {
                tvSpeechbulTitle.setText(VOICE_RECOG_NORECOG_HINT);
            }
        }
    };
    private Runnable mNorecogRunnable = new Runnable() {
        @Override
        public void run() {
            if (!hasValidSpeechInput) {
                startTextInput("");
            }
        }
    };

    /**
     * 计时器,30s截停
     */
    CountDownTimer stopSpeechTimer = new CountDownTimer(30050, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            int i = (int) (millisUntilFinished / 1000);
            if (i < 6) {
                tvSpeechbulCountdown.setVisibility(View.VISIBLE);
                vwvSpeechbulWave.setVisibility(View.GONE);
                tvSpeechbulCountdown.setText(i + "");
            }
        }

        @Override
        public void onFinish() {
            if (hasValidSpeechInput) {
                startTextInput(tvSpeechbulTitle.getText().toString());
            } else {
                startTextInput("");
            }
        }
    };

    /**
     * 开始语音识别
     */
    private void startEvaluator() {
        if (audioRequest != null) {
            audioRequest.request(new AudioRequest.OnAudioRequest() {
                @Override
                public void requestSuccess() {
                    saveFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
                    mParam.setRecogType(SpeechConfig.SPEECH_RECOGNITIYON_OFFINE);
                    mParam.setLocalSavePath(saveFile.getPath());
                    mParam.setVad_pause_sec("1.2");
                    mParam.setVad_max_sec("30");
                    mSpeechUtils.startRecog(mParam, new EvaluatorListener() {
                        @Override
                        public void onBeginOfSpeech() {
                            logger.i("onBeginOfSpeech()");
                            KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
                            tvSpeechbulTitle.setText(VOICE_RECOG_HINT);
                            tvSpeechbulTitleCount.setText("");
                            rlSpeechbulInputContent.setVisibility(View.GONE);
                            tvSpeechbulTitle.setVisibility(View.VISIBLE);
                            tvSpeechbulTitleCount.setVisibility(View.VISIBLE);
                            vwvSpeechbulWave.setVisibility(View.VISIBLE);
                            hasValidSpeechInput = false;

                            mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
                            if (mAM != null) {
                                mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
                                mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
                                mAM.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (0.0f * mMaxVolume), 0);
                                isVolumeResume = false;
                            }

                            stopSpeechTimer.start();
                            //3秒没有检测到声音提示
                            mWeakHandler.postDelayed(mHintRunnable, 3000);
                            //6秒仍没检测到说话
                            mWeakHandler.postDelayed(mNovoiceRunnable, 6000);
                            //7秒没声音自动停止
                            mWeakHandler.postDelayed(mNorecogRunnable, 7000);
                        }

                        @Override
                        public void onResult(ResultEntity resultEntity) {
                            logger.i("onResult:status = " + resultEntity.getStatus() + ", errorNo = " + resultEntity
                                    .getErrorNo());
                            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                                onEvaluatorSuccess(resultEntity.getCurString(), true);
                            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                                onEvaluatorError(resultEntity);
                            } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                                onEvaluatorSuccess(resultEntity.getCurString(), false);
                            }
                        }

                        @Override
                        public void onVolumeUpdate(int volume) {
                            logger.i("onVolumeUpdate: volume = " + volume);
                            vwvSpeechbulWave.setVolume(volume * 2);
                        }

                    });
                }
            });
        }
    }

    /**
     * 结束语音识别
     */
    private void stopEvaluator() {
        logger.i("stopEvaluator()");
        if (mSpeechUtils != null) {
            mSpeechUtils.cancel();
        }
        if (mAM != null && !isVolumeResume) {
            mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
            isVolumeResume = true;
        }
        if (stopSpeechTimer != null) {

            stopSpeechTimer.cancel();
        }
        if (audioRequest != null) {
            audioRequest.release();
        }
        hasValidSpeechInput = false;
        mWeakHandler.removeCallbacks(mHintRunnable);
        mWeakHandler.removeCallbacks(mNorecogRunnable);
        mWeakHandler.removeCallbacks(mNovoiceRunnable);
    }

    /**
     * 识别成功回调
     */
    private void onEvaluatorSuccess(String curContent, boolean isSpeechFinished) {
        logger.i("onEvaluatorSuccess(): isSpeechFinish = " + isSpeechFinished);
        if (curContent == null) {
            return;
        }
        String content = curContent;
        //语音录入，60个字符截停
        if (content.length() > 1) {
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
        } else {
            content = content.toUpperCase();
        }
        if (content.length() > MAX_INPUT_CHAR_NUMBER) {
            //首字母大写
            content = content.substring(0, MAX_INPUT_CHAR_NUMBER);
            tvSpeechbulTitle.setText(content);
            tvSpeechbulTitleCount.setText("（" + content.length() + "/" + MAX_INPUT_CHAR_NUMBER + "）");
            originalText = content;
            startTextInput(content);
        }
        logger.i("=====speech evaluating: " + content);
        if (isSpeechFinished) {
            startTextInput(content);
        } else {
            if (!StringUtils.isEmpty(content)) {
                tvSpeechbulTitle.setText(content);
                tvSpeechbulTitleCount.setText("（" + content.length() + "/" + MAX_INPUT_CHAR_NUMBER + "）");
                originalText = content;
                hasValidSpeechInput = true;
            }
        }
    }

    /**
     * 识别失败回调
     */
    private void onEvaluatorError(ResultEntity resultEntity) {
        Log.d(TAG, "onEvaluatorError()");
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode.MUTE) {
            logger.i("声音有点小，再来一次哦！");
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEvaluator();
                }
            }, 300);
            return;
        } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
            logger.i("麦克风不可用，快去检查一下！");
            startTextInput("");
        } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() ==
                ResultCode.NETWORK_FAIL
                || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
            int netWorkType = NetWorkHelper.getNetWorkState(mContext);
            if (netWorkType == NetWorkHelper.NO_NETWORK) {
                logger.i("好像没网了，快检查一下!");
            } else {
                logger.i("服务器连接不上!");
            }
            startTextInput("");

        } else if (resultEntity.getErrorNo() == ResultCode.SPEECH_CANCLE) {
            logger.i("离线测评重新build，要取消到旧的！");
            startTextInput("");
        } else {
            if (hasValidSpeechInput) {
                startTextInput(tvSpeechbulTitle.getText().toString());
            } else {
                startTextInput("");
            }
        }
    }

    /**
     * 结束语音输入，跳文本输入
     */
    private void startTextInput(String evaluateResult) {
        if (!StringUtils.isEmpty(evaluateResult)) {
            umsAgentDebugPvSno4("1");
        } else {
            umsAgentDebugPvSno4("0");
        }
        stopEvaluator();
        tvSpeechbulTitle.setVisibility(View.GONE);
        tvSpeechbulTitleCount.setVisibility(View.GONE);
        vwvSpeechbulWave.setVisibility(View.GONE);
        tvSpeechbulCountdown.setVisibility(View.GONE);
        rlSpeechbulInputContent.setVisibility(View.VISIBLE);
        tvSpeechbulRepeat.setVisibility(View.VISIBLE);
        etSpeechbulWords.setText(evaluateResult);
        tvSpeechbulCount.setText(evaluateResult.length() + "/" + MAX_INPUT_CHAR_NUMBER);
        etSpeechbulWords.requestFocus();
        etSpeechbulWords.setSelection(etSpeechbulWords.getText().toString().length());
        if (StringUtils.isSpace(etSpeechbulWords.getText().toString())) {
            setBtnDisenable(tvSpeechbulSend);
        } else {
            tvSpeechbulSend.setEnabled(true);
            tvSpeechbulSend.setAlpha(1.0f);
            tvSpeechbulSend.setTextColor(Color.WHITE);
        }
    }

    /**
     * 跳语音输入
     */
    private void startSpeechInput() {
        //判断模型是否初始化成功
        if (!mSpeechUtils.isRecogOfflineSuccess()) {
            XESToastUtils.showToast(mContext, "模型启动失败，请使用手动输入");
            setBtnDisenable(tvSpeechbulRepeat);
            startTextInput("");
        } else {
            startEvaluator();
        }
    }

    /**
     * 置灰按钮
     */
    private void setBtnDisenable(TextView view) {
        view.setEnabled(false);
        if (isSmallEnglish) {
            view.setAlpha(0.6f);
        } else {
            view.setTextColor(Color.parseColor("#73FFFFFF"));
        }
    }

    /**
     * ************************************************** 弹 幕 **************************************************
     */

    private static final long ADD_DANMU_TIME = 1200;
    private int BITMAP_WIDTH_GUEST = 34;//别人头像的宽度
    private int BITMAP_HEIGHT_GUEST = 34;//别人头像的高度
    private int BITMAP_WIDTH_ME = 34;//自己头像的宽度
    private int BITMAP_HEIGHT_ME = 34;//自己头像的高度
    private float DANMU_TEXT_SIZE = 14;//弹幕字体的大小
    private int DANMU_PADDING = 5;//控制两行弹幕之间的间距
    private int DANMU_RADIUS = 16;//圆角半径
    private int DANMU_BACKGROUND_HEIGHT = 33;
    private String DANMU_TEXT_COLOR = "#72DAFB";
    private Drawable DANMU_BACKGROUND = mContext.getResources().getDrawable(R.drawable.bg_livevideo_junior_danmaku);
    private long latestDanmakuAddtime = -300;
    private int danmakuAddCount = 0;

    /**
     * 初始化弹幕
     */
    protected View initDanmaku() {
        logger.i("initDanmaku()");
        transformSize(mContext);
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuView = new DanmakuView(mContext);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) // 越大速度越慢
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 图文混排使用BaseCacheStuffer,
                // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void drawingFinished() {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void prepared() {
                mDanmakuView.start();
                // generateSomeDanmaku();
            }
        });

        mDanmakuView.prepare(new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        mDanmakuView.showFPS(false);
        mDanmakuView.enableDanmakuDrawingCache(false);
        return mDanmakuView;
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    mWeakHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            addDanmaku(time + "", time + "", "", true);
                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * dp转成sp
     */
    private void transformSize(Context context) {
        BITMAP_WIDTH_GUEST = SizeUtils.Dp2Px(context, BITMAP_WIDTH_GUEST);
        BITMAP_HEIGHT_GUEST = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_GUEST);
        BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
        BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
        DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
        DANMU_RADIUS = SizeUtils.Dp2Px(context, DANMU_RADIUS);
        DANMU_TEXT_SIZE = SizeUtils.Dp2Px(context, DANMU_TEXT_SIZE);
        DANMU_BACKGROUND_HEIGHT = SizeUtils.Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
    }

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer实现自定义弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            float height = DANMU_BACKGROUND.getIntrinsicHeight();
            float offsetRight = (BITMAP_HEIGHT_ME - height) / 2;
            DANMU_BACKGROUND.setBounds(
                    (int) (left + danmaku.padding + offsetRight),
                    (int) (top + danmaku.padding + offsetRight),
                    (int) (left + danmaku.paintWidth),
                    (int) (top + height + offsetRight + danmaku.padding));
            DANMU_BACKGROUND.draw(canvas);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint
                paint) {
            // 禁用描边绘制
        }
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            // 根据你的条件检查是否需要需要更新弹幕
//            if (danmaku.text instanceof Spanned) {
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // 清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    private void addDanmaku(final String name, final String msg, final String headImgUrl, final boolean isGuest) {
        if (mDanmakuContext == null || mDanmakuView == null || !mDanmakuView.isPrepared()) {
            mWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addDanmaku(name, msg, headImgUrl, isGuest);
                }
            }, 100);
            return;
        }
        //如果长时间没有弹幕，可能会休眠
        if (mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
        final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }
        if (isGuest) {
            danmaku.priority = 0;
            danmaku.textColor = Color.WHITE;
        } else {
            mDanmakuView.pause();
            mDanmakuView.resume();
            danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕。但是会导致限制行数和禁止堆叠失效
            danmaku.textColor = Color.parseColor(DANMU_TEXT_COLOR);
        }
        danmaku.isGuest = isGuest;
        danmaku.isLive = true;
        danmaku.padding = DANMU_PADDING;
        danmaku.time = mDanmakuView.getCurrentTime() + ADD_DANMU_TIME;
        danmaku.textSize = DANMU_TEXT_SIZE;
        danmaku.textShadowColor = 0; // 如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        if (danmaku.time - latestDanmakuAddtime < 300 + msg.length() * 15) {
            //如果两条弹幕时间间隔太短
            danmaku.time = latestDanmakuAddtime + 300 + msg.length() * 15;
            latestDanmakuAddtime = danmaku.time;
            danmakuAddCount++;
        } else {
            danmakuAddCount = 0;
            latestDanmakuAddtime = danmaku.time;
        }
        ImageLoader.with(mContext).load(headImgUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                if (isGuest) {
                    drawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                } else {
                    drawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                }
                danmaku.text = createSpannable(name, msg, drawable);
                mDanmakuView.addDanmaku(danmaku);
            }

            @Override
            public void onFail() {
                Drawable circleDrawable;
                circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_livevideo_default_head_boy);
                if (isGuest) {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                } else {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                }
                danmaku.text = createSpannable(name, msg, circleDrawable);
                mDanmakuView.addDanmaku(danmaku);
            }
        });
    }

    protected SpannableStringBuilder createSpannable(String name, String msg, Drawable drawable) {
        String text = "  " + name + ": " + msg + "  ";
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.append(text);
        ImageSpan span = new VerticalImageSpan(drawable);
        spannable.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private void umsAgentDebugInterSno2() {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "openVoiceBullet");
        mData.put("sno", "2");
        mData.put("voiceId", presenter.getVoiceId());
        umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_BULLET, mData);
    }

    private void umsAgentDebugPvSno3() {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "voiceBulletWave");
        mData.put("sno", "3");
        mData.put("voiceId", presenter.getVoiceId());
        umsAgentDebugPv(LiveVideoConfig.LIVE_VOICE_BULLET, mData);
    }

    private void umsAgentDebugPvSno4(String isRecognizeSuccess) {
        Map<String, String> mData = new HashMap<>();
        mData.put("isRecognizeSuccess", isRecognizeSuccess);
        mData.put("logtype", "voiceBulletText");
        mData.put("sno", "4");
        mData.put("voiceId", presenter.getVoiceId());
        umsAgentDebugPv(LiveVideoConfig.LIVE_VOICE_BULLET, mData);
    }

    private void umsAgentDebugInterSno6() {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "reRecoding");
        mData.put("sno", "6");
        mData.put("voiceId", presenter.getVoiceId());
        umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_BULLET, mData);
    }

    private String isSend;
    private String aliyunUrl;
    private String originalText;
    private String fainalText;

    private void umsAgentDebugInterSno7() {
        Map<String, String> mData = new HashMap<>();
        if (fainalText != null && !fainalText.equals(originalText)) {
            mData.put("isModify", "1");
        } else {
            mData.put("isModify", "0");
        }
        mData.put("isSend", "1");
        mData.put("aliyunUrl", aliyunUrl);
        mData.put("originalText", originalText);
        mData.put("fainalText", fainalText);
        mData.put("logtype", "voiceBulletSend");
        mData.put("sno", "7");
        mData.put("voiceId", presenter.getVoiceId());
        umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_BULLET, mData);
    }

    private void umsAgentDebugPvSno8() {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "voiceBulletPraise");
        mData.put("sno", "8");
        mData.put("voiceId", presenter.getVoiceId());
        umsAgentDebugPv(LiveVideoConfig.LIVE_VOICE_BULLET, mData);
    }

    private void umsAgentDebugInterSno9() {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "closeVoiceBullet");
        mData.put("sno", "9");
        mData.put("voiceId", presenter.getVoiceId());
        umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_BULLET, mData);
    }

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

    @Override
    public void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.i("onDestroy()");
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        stopEvaluator();
    }
}

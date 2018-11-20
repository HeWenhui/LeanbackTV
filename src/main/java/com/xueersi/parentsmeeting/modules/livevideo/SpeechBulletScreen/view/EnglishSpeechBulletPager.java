package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.speechrecognizer.Constants;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechConfig;
import com.xueersi.common.speech.SpeechUtils;
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
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.EnglishSpeechBulletContract;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.page.EnglishSpeekPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.SmallEnglishMicTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CircleDrawable;
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
     * 是否正在开启语音弹幕
     */
    private boolean isShowingSpeechBullet = false;
    /**
     * 语音弹幕的布局
     */
    private RelativeLayout rlSpeechBulContent;
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
     * 截停倒计时
     */
    private TextView tvSpeechbulCountdown;
    /**
     * 话语编辑框
     */
    private EditText etSpeechbulWords;
    /**
     * 输入字数
     */
    private TextView tvSpeechbulCount;
    /**
     * 重说按钮
     */
    private ImageView tvSpeechbulRepeat;
    /**
     * 发送按钮
     */
    private ImageView tvSpeechbulSend;
    /**
     * 倒计时提示
     */
    private TextView tvSpeechbulCloseTip;
    /**
     * 直播布局
     */
    private RelativeLayout rootView;
    /**
     * 语音弹幕根布局
     */
    private RelativeLayout rlSpeechBulRoot;
    /**
     * 输入框布局
     */
    private RelativeLayout rlSpeechbulInputContent;
    /**
     * 底部布局
     */
    private RelativeLayout rlSpeechbulBottomContent;
    /**
     * 小提示 - 只能输入英文
     */
    private RelativeLayout rlSpeechbulTips;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    /**
     * 弹幕视图
     */
    private DanmakuView mDanmakuView;
    /**
     * 弹幕上下文
     */
    protected DanmakuContext mDanmakuContext;
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
     * 是不是恢复了音量
     */
    private boolean isVolumeResume = false;
    /**
     * 语音评测工具类
     */
//    private SpeechEvaluatorUtils mSpeechEvaluatorUtils;
    private SpeechUtils mSpeechUtils;
    /**
     * 语音保存位置-目录
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
     * 是否拥有麦克风权限
     */
    private boolean hasAudioPermission = false;
    /**
     * 声音池
     */
    private SoundPool soundPool;
    /**
     * 收音开始时的提示音
     */
    private int soundStartEvaluator = 0;
    /**
     * 阿里云业务
     */
    XesCloudUploadBusiness uploadBusiness;
    SpeechParamEntity mParam;
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
     * 点赞页面
     */
    private EnglishSpeekPager englishSpeekPager;

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
        rlSpeechbulBottomContent = mView.findViewById(R.id.rl_livevideo_speechbul_bottom_content);
        tvSpeechbulTitle = mView.findViewById(R.id.tv_livevideo_speechbul_title);
        tvSpeechbulTitleCount = mView.findViewById(R.id.tv_livevideo_speechbul_title_count);
        vwvSpeechbulWave = mView.findViewById(R.id.vwv_livevideo_speechbul_wave);
        tvSpeechbulCountdown = mView.findViewById(R.id.tv_livevideo_speechbul_countdown);
        etSpeechbulWords = mView.findViewById(R.id.et_livevideo_speechbul_words);
        tvSpeechbulCount = mView.findViewById(R.id.tv_livevideo_speechbul_count);
        tvSpeechbulRepeat = mView.findViewById(R.id.tv_livevideo_speechbul_repeat);
        tvSpeechbulSend = mView.findViewById(R.id.tv_livevideo_speechbul_send);
        rlSpeechbulInputContent = mView.findViewById(R.id.rl_livevideo_speechbul_input);
        rlSpeechbulTips = mView.findViewById(R.id.tv_livevideo_speechbul_tips);

        rlSpeechBulRoot.setClickable(true);
        rlSpeechbulBottomContent.setVisibility(View.VISIBLE);
        int colors[] = {0x19FFA63C, 0x32FFA63C, 0x64FFC12C, 0x96FFC12C, 0xFFFFA200};
        vwvSpeechbulWave.setColors(colors);
        vwvSpeechbulWave.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vwvSpeechbulWave.setLinearGradient(new LinearGradient(0, 0, vwvSpeechbulWave.getMeasuredWidth(), 0,
                        new int[]{0xFFEA9CF9, 0xFF9DBBFA, 0xFF80F9FD}, new float[]{0, 0.5f, 1.0f}, Shader.TileMode
                        .CLAMP));
            }
        });
        vwvSpeechbulWave.setBackColor(Color.TRANSPARENT);
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        etSpeechbulWords.setTypeface(fontFace);
        tvSpeechbulTitle.setTypeface(fontFace);
        tvSpeechbulTitleCount.setTypeface(fontFace);
        tvSpeechbulCount.setTypeface(fontFace);
        tvSpeechbulCloseTip.setTypeface(fontFace);
        setVideoLayout(LiveVideoPoint.getInstance());
        return mView;
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeechbulWave.start();
                umsAgentDebugPvSno3();
            }
        }, 100);

//        if (mSpeechEvaluatorUtils == null) {
//            mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
//        }
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
                    setRepeatBtnDisenable();
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
        //监听软件通
        etSpeechbulWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (StringUtils.isSpace(charSequence.toString())) {
                    tvSpeechbulSend.setEnabled(false);
                    tvSpeechbulSend.setAlpha(0.6f);
                } else {
                    tvSpeechbulSend.setEnabled(true);
                    tvSpeechbulSend.setAlpha(1.0f);
                }
                tvSpeechbulCount.setText(charSequence.toString().length() + "/60");
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String str = editable.toString();
//                for (int i = 0; i < str.length(); i++) {
//                    String subStr = str.substring(str.length() - 1 - i, str.length() - i);
//                    char tempChar = subStr.toCharArray()[0];
//                    if (isChineseChar(tempChar)) {
//                        editable.delete(str.length() - 1 - i, str.length() - i);
//                        rlSpeechbulTips.setVisibility(View.VISIBLE);
//                        mWeakHandler.removeCallbacks(setTipsGoneRunnable);
//                        mWeakHandler.postDelayed(setTipsGoneRunnable, 2000);
//                    }
//                }

//                char[] charStr = subStr.toCharArray();
//                int mid = charStr[0];
//                if (mid >= 32 && mid <= 127) {//数字，字母，符号
//                    return;
//                }
//                editable.delete(str.length() - 1, str.length());
//                rlSpeechbulTips.setVisibility(View.VISIBLE);
//                mWeakHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rlSpeechbulTips.setVisibility(View.GONE);
//                    }
//                }, 2000);
                String str = editable.toString();
                String repickStr = str.replaceAll("[\u4e00-\u9fa5]", "");
                if (!repickStr.equals(str)) {
                    rlSpeechbulTips.setVisibility(View.VISIBLE);
                    mWeakHandler.removeCallbacks(setTipsGoneRunnable);
                    mWeakHandler.postDelayed(setTipsGoneRunnable, 2000);
                }
                etSpeechbulWords.removeTextChangedListener(this);
                editable.replace(0, editable.length(), repickStr.trim());
                etSpeechbulWords.addTextChangedListener(this);
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

                addDanmaku("我", etSpeechbulWords.getText().toString(), presenter.getHeadImgUrl(), false);
                closeSpeechBullet(false);

                if (ShareDataManager.getInstance().getString(ShareBusinessConfig
                                .SP_VOICE_BULLET_ID, "",
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

    }

    private Runnable setTipsGoneRunnable = new Runnable() {
        @Override
        public void run() {
            rlSpeechbulTips.setVisibility(View.GONE);
        }
    };

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
        showStartSpeechBulletToast();
        mWeakHandler.postDelayed(showSpeechBulletRunnable, 2000);
        initUmsAgentData();
        umsAgentDebugInterSno2();
    }

    /**
     * 展示老师开启语音弹幕Toast
     */
    public void showStartSpeechBulletToast() {
        SmallEnglishMicTipDialog startSpeechBulletToast = new SmallEnglishMicTipDialog(mContext);
        startSpeechBulletToast.setText("老师开启了语音弹幕");
        startSpeechBulletToast.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf"));
        startSpeechBulletToast.showDialogAutoClose(2000);
    }

    /**
     * 初始化日志数据
     */
    public void initUmsAgentData() {
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
//                    RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) dvSpeechbulDanmaku
// .getLayoutParams();
//                    rp.setMargins(0, SizeUtils.Dp2Px(mContext, 0), 0, 0);
//                    dvSpeechbulDanmaku.setLayoutParams(rp);
            }
            rlSpeechBulContent.addView(initView().getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
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
        if (!isShowingSpeechBullet) {
            return;
        }
        isShowingSpeechBullet = false;
//        if (tvSpeechbulTitle.getVisibility() == View.VISIBLE) {
//            if (!StringUtils.isEmpty(tvSpeechbulTitleCount.getText())) {
//                startTextInput(tvSpeechbulTitle.getText().toString());
//            } else {
//                startTextInput("");
//            }
//        }
        if (hasTip) {
//            tvSpeechbulCloseTip.setVisibility(View.VISIBLE);
//            new CountDownTimer(5050, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    int i = (int) (millisUntilFinished / 1000);
//                    tvSpeechbulCloseTip.setText(i + "秒后结束语音弹幕");
//                }
//
//                @Override
//                public void onFinish() {
//                    KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
//                    rlSpeechBulContent.removeView(rlSpeechBulRoot);
//                    rlSpeechBulRoot.setClickable(false);
//                    stopEvaluator();
//                    isShowingSpeechBullet = false;
//                }
//            }.start();
            mWeakHandler.removeCallbacks(showSpeechBulletRunnable);
            SmallEnglishMicTipDialog startSpeechBulletToast = new SmallEnglishMicTipDialog(mContext);
            startSpeechBulletToast.setText("老师关闭了语音弹幕");
            startSpeechBulletToast.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf"));
            startSpeechBulletToast.showDialogAutoClose(2000);
            umsAgentDebugInterSno9();
        }
        if (rlSpeechBulRoot != null) {
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
        umsAgentDebugPvSno8();
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (tvSpeechbulRepeat == null || tvSpeechbulSend == null) {
            return;
        }
        int marginLeft = liveVideoPoint.x2;
        int marginRight = liveVideoPoint.screenWidth - liveVideoPoint.x4;
        RelativeLayout.LayoutParams repeatLayoutParams = (RelativeLayout.LayoutParams) tvSpeechbulRepeat
                .getLayoutParams();
        repeatLayoutParams.setMargins(marginLeft, 0, 0, 0);
        tvSpeechbulRepeat.setLayoutParams(repeatLayoutParams);

        RelativeLayout.LayoutParams sendLayoutParams = (RelativeLayout.LayoutParams) tvSpeechbulSend.getLayoutParams();
        sendLayoutParams.setMargins(0, 0, marginRight, 0);
        tvSpeechbulSend.setLayoutParams(sendLayoutParams);
    }

    private Runnable removeViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (englishSpeekPager != null && englishSpeekPager.getRootView().getParent() == rlSpeechBulContent) {
                rlSpeechBulContent.removeView(englishSpeekPager.getRootView());
            }
        }
    };

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
    private boolean hasValidSpeechInput = false;
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
                    saveFile = new File(dir, "speechbul" + System.currentTimeMillis() + ".mp3");
                    mParam.setRecogType(SpeechConfig.SPEECH_RECOGNITIYON_OFFINE);
                    mParam.setLocalSavePath(saveFile.getPath());
                    mParam.setVad_pause_sec("3");
                    mParam.setVad_max_sec("30");
                    mSpeechUtils.startRecog(mParam, new EvaluatorListenerWithPCM() {
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

                            //播放声音
                            if (soundPool == null)
                                soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                            if (soundStartEvaluator == 0) {
                                soundStartEvaluator = soundPool.load(mContext, R.raw.start_evaluator, 1);
                                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                        // TODO Auto-generated method stub
                                        soundPool.play(soundStartEvaluator, 1, 1, 0, 0, 1);
                                    }
                                });
                            } else {
                                soundPool.play(soundStartEvaluator, 1, 1, 0, 0, 1);
                            }
                            mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
                            mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
                            mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
                            mAM.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (0.0f * mMaxVolume), 0);
                            isVolumeResume = false;
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

                        @Override
                        public void onRecordPCMData(short[] pcmBuffer, int length) {

                        }
                    });
//                    mSpeechEvaluatorUtils.startSpeechRecognitionOffline(saveFile.getPath(), "3", "30",
//                            new EvaluatorListener() {
//                                @Override
//                                public void onBeginOfSpeech() {
//                                    logger.i("onBeginOfSpeech()");
//                                    KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
//                                    tvSpeechbulTitle.setText(VOICE_RECOG_HINT);
//                                    tvSpeechbulTitleCount.setText("");
//                                    rlSpeechbulInputContent.setVisibility(View.GONE);
//                                    tvSpeechbulTitle.setVisibility(View.VISIBLE);
//                                    tvSpeechbulTitleCount.setVisibility(View.VISIBLE);
//                                    vwvSpeechbulWave.setVisibility(View.VISIBLE);
//                                    hasValidSpeechInput = false;
//
//                                    //播放声音
//                                    if (soundPool == null)
//                                        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//                                    if (soundStartEvaluator == 0) {
//                                        soundStartEvaluator = soundPool.load(mContext, R.raw.start_evaluator, 1);
//                                        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                                            public void onLoadComplete(SoundPool soundPool, int sampleId, int
// status) {
//                                                // TODO Auto-generated method stub
//                                                soundPool.play(soundStartEvaluator, 1, 1, 0, 0, 1);
//                                            }
//                                        });
//                                    } else {
//                                        soundPool.play(soundStartEvaluator, 1, 1, 0, 0, 1);
//                                    }
//                                    mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
//                                    mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
//                                    mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
//                                    mAM.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (0.0f * mMaxVolume), 0);
//                                    isVolumeResume = false;
//                                    stopSpeechTimer.start();
//                                    //3秒没有检测到声音提示
//                                    mWeakHandler.postDelayed(mHintRunnable, 3000);
//                                    //6秒仍没检测到说话
//                                    mWeakHandler.postDelayed(mNovoiceRunnable, 6000);
//                                    //7秒没声音自动停止
//                                    mWeakHandler.postDelayed(mNorecogRunnable, 7000);
//                                }
//
//                                @Override
//                                public void onResult(ResultEntity resultEntity) {
//                                    logger.i("onResult:status = " + resultEntity.getStatus() + ", errorNo = " +
// resultEntity.getErrorNo());
//                                    if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
//                                        onEvaluatorSuccess(resultEntity.getCurString(), true);
//                                    } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
//                                        onEvaluatorError(resultEntity);
//                                    } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
//                                        onEvaluatorSuccess(resultEntity.getCurString(), false);
//                                    }
//                                }
//
//                                @Override
//                                public void onVolumeUpdate(int volume) {
//                                    logger.i("onVolumeUpdate: volume = " + volume);
//                                    vwvSpeechbulWave.setVolume(volume * 2);
//                                }
//                            });
                }
            });
        }
    }


    /**
     * 结束语音识别
     */
    private void stopEvaluator() {
        logger.i("stopEvaluator()");
//        if (mSpeechEvaluatorUtils != null) {
//            mSpeechEvaluatorUtils.cancel();
//        }
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
        String content = curContent;
        //语音录入，60个字符截停
        if (content.length() > 1) {
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
        } else {
            content = content.toUpperCase();
        }
        if (content.length() > 60) {
            //首字母大写
            content = content.substring(0, 60);
            tvSpeechbulTitle.setText(content);
            tvSpeechbulTitleCount.setText("（" + content.length() + "/60）");
            originalText = content;
            startTextInput(content);
        }
        logger.i("=====speech evaluating: " + content);
        if (isSpeechFinished) {
            startTextInput(content);
        } else {
            if (!StringUtils.isSpace(content)) {
                tvSpeechbulTitle.setText(content);
                tvSpeechbulTitleCount.setText("（" + content.length() + "/60）");
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
            startEvaluator();
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
        tvSpeechbulCount.setText(evaluateResult.length() + "/60");
        etSpeechbulWords.requestFocus();
        etSpeechbulWords.setSelection(etSpeechbulWords.getText().toString().length());
    }

    /**
     * 跳语音输入
     */
    private void startSpeechInput() {
        //判断模型是否初始化成功
        if (!mSpeechUtils.isRecogOfflineSuccess()) {
            XESToastUtils.showToast(mContext, "模型启动失败，请使用手动输入");
            setRepeatBtnDisenable();
            startTextInput("");
        } else {
            startEvaluator();
        }
    }

    /**
     * 置灰重说按钮
     */
    private void setRepeatBtnDisenable() {
        tvSpeechbulRepeat.setEnabled(false);
        tvSpeechbulRepeat.setAlpha(0.6f);
    }

    /**
     * ************************************************** 弹 幕 **************************************************
     */

    private static final long ADD_DANMU_TIME = 1200;
    private int BITMAP_WIDTH_GUEST = 34;//别人头像的宽度
    private int BITMAP_HEIGHT_GUEST = 34;//别人头像的高度
    private int BITMAP_WIDTH_ME = 42;//自己头像的宽度
    private int BITMAP_HEIGHT_ME = 42;//自己头像的高度
    private float DANMU_TEXT_SIZE = 14;//弹幕字体的大小
    private int DANMU_PADDING = 5;//控制两行弹幕之间的间距
    private int DANMU_RADIUS = 16;//圆角半径
    private int DANMU_BACKGROUND_HEIGHT = 33;
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
//                generateSomeDanmaku();
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
     * 转换数值
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
            paint.setColor(Color.BLACK);
            paint.setAlpha((int) (255 * 0.6)); //  透明度0.6

            if (danmaku.isGuest) {
                canvas.drawRoundRect(new RectF(left + danmaku.padding + 1, top + danmaku.padding +
                                (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT) / 2 + 1
                                , left + danmaku.paintWidth - danmaku.padding,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT) / 2 +
                                        1 + danmaku.padding),
                        DANMU_RADIUS, DANMU_RADIUS, paint);
            } else {
                canvas.drawRoundRect(new RectF(left + danmaku.padding + 1, top + danmaku.padding + (BITMAP_HEIGHT_ME
                                - DANMU_BACKGROUND_HEIGHT) / 2 + 1
                                , left + danmaku.paintWidth - danmaku.padding,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2 + 1
                                        + danmaku.padding),
                        DANMU_RADIUS, DANMU_RADIUS, paint);
            }
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
//            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void addDanmaku(final String name, final String msg, final String headImgUrl, final boolean isGuest) {
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
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
        final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }
        if (isGuest) {
            danmaku.priority = 0;
            danmaku.padding = DANMU_PADDING;
            danmaku.textColor = Color.WHITE;
        } else {
            danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕。但是会导致限制行数和禁止堆叠失效
            danmaku.padding = DANMU_PADDING - (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2;
            danmaku.textColor = Color.parseColor("#72DAFB");
        }
        danmaku.isGuest = isGuest;
        danmaku.isLive = true;
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
                Drawable cirDrawable = drawable;
                if (isGuest) {
                    cirDrawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                } else {
                    cirDrawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                }
                danmaku.text = createSpannable(name, msg, cirDrawable, isGuest);
                mDanmakuView.addDanmaku(danmaku);
            }

            @Override
            public void onFail() {
                Drawable circleDrawable;
                if ("0".equals(presenter.getStuSex())) {
                    circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_livevideo_default_head_girl);
                } else {
                    circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_livevideo_default_head_boy);
                }
                if (isGuest) {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                } else {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                }
                danmaku.text = createSpannable(name, msg, circleDrawable, isGuest);
                mDanmakuView.addDanmaku(danmaku);
            }
        });
    }

    protected SpannableStringBuilder createSpannable(String name, String msg, Drawable drawable, boolean isGuest) {
        String text = " " + name + " : " + msg + "  ";
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.append(text);
        ImageSpan span = new VerticalImageSpan(drawable, isGuest);
        spannable.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 使用该ImageSpan,可以使Image和文字拼接的时候在竖直方向居中对齐
     */
    public class VerticalImageSpan extends ImageSpan {
        boolean isGuset;

        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public VerticalImageSpan(Drawable drawable, boolean isGuset) {
            super(drawable);
            this.isGuset = isGuset;
        }

        /**
         * update the text line height
         */
        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.descent - fmPaint.ascent;
                int drHeight = rect.bottom - rect.top;
                int centerY = fmPaint.ascent + fontHeight / 2;

                fontMetricsInt.ascent = centerY - drHeight / 2;
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = centerY + drHeight / 2;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return rect.right;
        }

        /**
         * see detail message in android.text.TextLine
         *
         * @param canvas the canvas, can be null if not rendering
         * @param text   the text to be draw
         * @param start  the text start position
         * @param end    the text end position
         * @param x      the edge of the replacement closest to the leading margin
         * @param top    the top of the line
         * @param y      the baseline
         * @param bottom the bottom of the line
         * @param paint  the work paint
         */
        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {

            Drawable drawable = getDrawable();
            canvas.save();
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.descent - fmPaint.ascent;
            int centerY = y + fmPaint.descent - fontHeight / 2;
            int transY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();

            //如果是自己发的弹幕，给头像加一个黄圈
            if (!isGuset) {
                Paint circlePaint = new Paint();
                circlePaint.setAntiAlias(true);
                circlePaint.setStyle(Paint.Style.STROKE);
                circlePaint.setColor(Color.parseColor("#72DAFB"));
                circlePaint.setStrokeWidth(SizeUtils.Dp2Px(mContext, 1));
                canvas.drawCircle(x + BITMAP_WIDTH_ME / 2, transY + BITMAP_WIDTH_ME / 2, BITMAP_WIDTH_ME / 2,
                        circlePaint);
            }
        }

    }

    /**
     * 判断一个字符是否是汉字
     * PS：中文汉字的编码范围：[\u4e00-\u9fa5]
     *
     * @param c 需要判断的字符
     * @return 是汉字(true), 不是汉字(false)
     */
    public boolean isChineseChar(char c) {
        return String.valueOf(c).matches("[\u4e00-\u9fa5]");
    }

    /**
     * 缩放本地资源图片
     *
     * @param resId
     * @param w
     * @param h
     * @return
     */
    public CircleDrawable zoomCircleDrable(int resId, int w, int h) {
        Resources res = mContext.getResources();
        Bitmap oldBmp = BitmapFactory.decodeResource(res, resId);
        Bitmap newBmp = Bitmap.createScaledBitmap(oldBmp, w, h, true);
        CircleDrawable circleDrawable = new CircleDrawable(newBmp);
        return circleDrawable;
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
        if (soundPool != null)
            soundPool.release();
        stopEvaluator();
    }
}

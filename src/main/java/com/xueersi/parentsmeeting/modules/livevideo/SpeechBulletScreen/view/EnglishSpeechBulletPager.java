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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
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

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
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
import com.xueersi.parentsmeeting.modules.livevideo.dialog.SmallEnglishMicTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CircleDrawable;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

import java.io.File;
import java.util.HashMap;
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

public class EnglishSpeechBulletPager extends LiveBasePager implements EnglishSpeechBulletContract.EnglishSpeechBulletView {

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
     * 小提示
     */
    private RelativeLayout rlSpeechbulTips;

    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    /**
     * 弹幕视图
     */
    private DanmakuView dvSpeechbulDanmaku;
    /**
     * 弹幕上下文
     */
    protected DanmakuContext mDanmakuContext;
    /**
     * 弹幕解析器
     */
    private BaseDanmakuParser mParser;
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
    private SpeechEvaluatorUtils mSpeechEvaluatorUtils;
    /**
     * 语音保存位置-目录
     */
    private File dir;
    /**
     * 是不是评测失败
     */
    private boolean isSpeechError = false;
    /**
     * 是不是评测成功
     */
    private boolean isSpeechSuccess = false;
    /**
     * 是否拥有麦克风权限
     */
    private boolean hasAudioPermission = false;
    /**
     * 是否正在开启语音弹幕
     */
    private boolean isShowingSpeechBullet = false;

    /**
     * MVP:Presenter层接口
     */
    private EnglishSpeechBulletContract.EnglishSpeechBulletPresenter presenter;
    /**
     * 弱引用Handler
     */
    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    private EnglishSpeekPager englishSpeekPager;

    public EnglishSpeechBulletPager(Context context) {
        super(context);
    }

    /**
     * 初始化布局
     */
    public View prepareView() {
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
                        new int[]{0xFFFF00FF, 0xFF00FFFF}, new float[]{0, 1.0f}, Shader.TileMode.CLAMP));
            }
        });
        vwvSpeechbulWave.setBackColor(Color.TRANSPARENT);
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        etSpeechbulWords.setTypeface(fontFace);
        tvSpeechbulTitle.setTypeface(fontFace);
        tvSpeechbulTitleCount.setTypeface(fontFace);
        tvSpeechbulCount.setTypeface(fontFace);
        tvSpeechbulCloseTip.setTypeface(fontFace);

        return mView;
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);

        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeechbulWave.start();
            }
        }, 100);

        if (mSpeechEvaluatorUtils == null) {
            mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
        }
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
                    tvSpeechbulRepeat.setEnabled(false);
                    tvSpeechbulRepeat.setAlpha(0.6f);
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
                if (StringUtils.isEmpty(charSequence)) {
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
                //过滤中文字符
                String temp = editable.toString();
                if (temp.length() == 0) {
                    return;
                }
                String subTemp = temp.substring(temp.length() - 1, temp.length());
                char tempC = subTemp.toCharArray()[0];
                if (isChineseChar(tempC)) {
                    editable.delete(temp.length() - 1, temp.length());
                    rlSpeechbulTips.setVisibility(View.VISIBLE);
                    mWeakHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rlSpeechbulTips.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
            }
        });
        //监听软键盘发送按钮
        etSpeechbulWords.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent
                        .KEYCODE_ENTER)) {
                    if (!StringUtils.isEmpty(etSpeechbulWords.getText().toString())) {
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
                KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
                rlSpeechbulInputContent.setVisibility(View.GONE);
                tvSpeechbulTitle.setText(VOICE_RECOG_HINT);
                tvSpeechbulTitleCount.setText("");
                tvSpeechbulTitle.setVisibility(View.VISIBLE);
                tvSpeechbulTitleCount.setVisibility(View.VISIBLE);
                vwvSpeechbulWave.setVisibility(View.VISIBLE);
                startEvaluator();
            }
        });
        //发送语音弹幕
        tvSpeechbulSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSpeechBullet(false);
                isShowingSpeechBullet = false;

//                for (int i = 0; i < 30; i++) {
//                    mWeakHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            addDanmaKuSpeech("我", etSpeechbulWords.getText().toString(), presenter.getHeadImgUrl(), false);
//                        }
//                    }, i * 300);
//
//                }
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
        switchFSPanelLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                switchFSPanelLinearLayout.refreshHeight(KeyboardUtil.getValidPanelHeight(mContext));
            }
        });

    }

    /**
     * 展示语音弹幕
     */
    @Override
    public void showSpeechBullet(final RelativeLayout rootView) {
        if (isShowingSpeechBullet) {
            return;
        }
        logger.i("showSpeechBullet");
        isShowingSpeechBullet = true;
        showStartSpeechBulletToast();
        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rlSpeechBulContent == null) {
                    rlSpeechBulContent = new RelativeLayout(mContext);
                    rlSpeechBulContent.setId(R.id.rl_livevideo_content_speechbul);
                    rootView.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                if (dvSpeechbulDanmaku == null) {
                    rlSpeechBulContent.addView(initDanmaku(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) dvSpeechbulDanmaku.getLayoutParams();
//                    rp.setMargins(0, SizeUtils.Dp2Px(mContext, 0), 0, 0);
//                    dvSpeechbulDanmaku.setLayoutParams(rp);
                }
                rlSpeechBulContent.addView(prepareView().getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rlSpeechBulContent.setVisibility(View.VISIBLE);
                initData();
                initListener();
            }
        }, 2000);
    }

    /**
     * 关闭语音弹幕
     *
     * @param hasTip 是否弹关闭提示
     */
    @Override
    public void closeSpeechBullet(boolean hasTip) {
        if (!isShowingSpeechBullet) {
            return;
        }
        logger.i("closeSpeechBullet");
        if (tvSpeechbulTitle.getVisibility() == View.VISIBLE) {
            if (!StringUtils.isEmpty(tvSpeechbulTitleCount.getText())) {
                startTextInput(tvSpeechbulTitle.getText().toString());
            } else {
                startTextInput("");
            }
        }
        if (hasTip) {
            tvSpeechbulCloseTip.setVisibility(View.VISIBLE);
            new CountDownTimer(5050, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int i = (int) (millisUntilFinished / 1000);
                    tvSpeechbulCloseTip.setText(i + "秒后结束语音弹幕");
                }

                @Override
                public void onFinish() {
                    KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
                    rlSpeechBulContent.removeView(rlSpeechBulRoot);
                    rlSpeechBulRoot.setClickable(false);
                    stopEvaluator();
                    isShowingSpeechBullet = false;
                }
            }.start();
        } else {
            KeyboardUtil.hideKeyboard(rlSpeechBulRoot);
            rlSpeechBulContent.removeView(rlSpeechBulRoot);
            rlSpeechBulRoot.setClickable(false);
            stopEvaluator();
            isShowingSpeechBullet = false;
        }
    }

    /**
     * 添加弹幕
     *
     * @param name       名字
     * @param msg        内容
     * @param headImgUrl 头像Url
     */
    @Override
    public void receiveDanmakuMsg(String name, String msg, String headImgUrl ,RelativeLayout rootView) {
        if (rlSpeechBulContent == null) {
            rlSpeechBulContent = new RelativeLayout(mContext);
            rlSpeechBulContent.setId(R.id.rl_livevideo_content_speechbul);
            rootView.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (dvSpeechbulDanmaku == null) {
            rlSpeechBulContent.addView(initDanmaku(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) dvSpeechbulDanmaku.getLayoutParams();
//                    rp.setMargins(0, SizeUtils.Dp2Px(mContext, 0), 0, 0);
//                    dvSpeechbulDanmaku.setLayoutParams(rp);
        }

        if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(msg) & !StringUtils.isEmpty(headImgUrl)) {
            addDanmaKuSpeech(name, msg, headImgUrl, true);
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
     * 展示老师开启语音弹幕Toast
     */
    public void showStartSpeechBulletToast() {
        SmallEnglishMicTipDialog startSpeechBulletToast = new SmallEnglishMicTipDialog(mContext);
        startSpeechBulletToast.setText("老师开启了语音弹幕");
        startSpeechBulletToast.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf"));
        startSpeechBulletToast.showDialogAutoClose(2000);
    }

    /**
     * ************************************************** 语音识别 **************************************************
     */
    private final static String VOICE_RECOG_HINT = "语音输入中，请大声说英语";
    private final static String VOICE_RECOG_NOVOICE_HINT = "抱歉没听清，请大点声重说哦";
    private final static String VOICE_RECOG_NORECOG_HINT = "请手动输入或重说";

    /**
     * 计时器 超过三十秒截停
     */
    CountDownTimer stopSpeechTimer = new CountDownTimer(30050, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            int i = (int) (millisUntilFinished / 1000);
            logger.i("onTick(): " + i + "s");
            if (i < 6) {
                tvSpeechbulCountdown.setVisibility(View.VISIBLE);
                vwvSpeechbulWave.setVisibility(View.GONE);
                tvSpeechbulCountdown.setText(i + "");
            }
        }

        @Override
        public void onFinish() {
            if (!StringUtils.isEmpty(tvSpeechbulTitleCount.getText())) {
                startTextInput(etSpeechbulWords.getText().toString());
            } else {
                startTextInput("");
            }
        }
    };

    /**
     * 语音请求和释放
     */
    private AudioRequest audioRequest;
    private File saveFile;

    /**
     * 开始语音识别
     */
    private void startEvaluator() {
        if (audioRequest != null) {
            audioRequest.request(new AudioRequest.OnAudioRequest() {
                @Override
                public void requestSuccess() {
                    saveFile = new File(dir, "speechbul" + System.currentTimeMillis() + ".mp3");
                    mSpeechEvaluatorUtils.startSpeechRecognitionOffline(saveFile.getPath(), "3", "30",
//                    mSpeechEvaluatorUtils.startSpeechBulletScreenRecognize(saveFile.getPath(), SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                            new EvaluatorListener() {
                                @Override
                                public void onBeginOfSpeech() {
                                    logger.i("onBeginOfSpeech()");
                                    isSpeechError = false;
                                    mAM.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (0.5f * mMaxVolume), 0);
                                    stopSpeechTimer.start();
                                    //3秒没有检测到声音提示
                                    mWeakHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (StringUtils.isEmpty(tvSpeechbulTitleCount.getText())) {
                                                tvSpeechbulTitle.setText(VOICE_RECOG_NOVOICE_HINT);
                                            }
                                        }
                                    }, 3000);
                                    //6秒仍没检测到说话
                                    mWeakHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (StringUtils.isEmpty(tvSpeechbulTitleCount.getText())) {
                                                tvSpeechbulTitle.setText(VOICE_RECOG_NORECOG_HINT);
                                            }
                                        }
                                    }, 6000);
                                    //7秒没声音自动停止
                                    mWeakHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (StringUtils.isEmpty(tvSpeechbulTitleCount.getText())) {
                                                startTextInput("");
                                            }
                                        }
                                    }, 7000);
                                }

                                @Override
                                public void onResult(ResultEntity resultEntity) {
                                    logger.i("onResult:status = " + resultEntity.getStatus() + ", errorNo = " + resultEntity.getErrorNo());
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
                                    vwvSpeechbulWave.setVolume(volume * 3);
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
        if (mSpeechEvaluatorUtils != null) {
            mSpeechEvaluatorUtils.cancel();
        }
        if (mAM != null) {
            mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
        }
        if (stopSpeechTimer != null) {
            stopSpeechTimer.cancel();
        }
    }

    /**
     * 识别成功回调
     */
    private void onEvaluatorSuccess(String curContent, boolean isSpeechFinished) {
        logger.i("onEvaluatorSuccess(): isSpeechFinish = " + isSpeechFinished);
        String content = curContent;

//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(curContent);
//            content = jsonObject.optString("nbest");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        //语音录入，限制60字以内
        if (content.length() > 60) {
            content = content.substring(0, 60);
        }
        logger.i("=====speech evaluating: " + content);
        if (isSpeechFinished) {
            startTextInput(content);
        } else {
            if (!TextUtils.isEmpty(content)) {
                tvSpeechbulTitle.setText(content);
                tvSpeechbulTitleCount.setText("（" + content.length() + "/60）");
            }
        }
    }

    /**
     * 识别失败回调
     */
    private void onEvaluatorError(ResultEntity resultEntity) {
        Log.d(TAG, "onEvaluatorError()");
        isSpeechError = true;
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
        } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() == ResultCode.NETWORK_FAIL
                || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
            int netWorkType = NetWorkHelper.getNetWorkState(mContext);
            if (netWorkType == NetWorkHelper.NO_NETWORK) {
                logger.i("好像没网了，快检查一下");
            } else {
                logger.i("服务器连接不上");
            }
            startTextInput("");

        } else {
            if (!TextUtils.isEmpty(tvSpeechbulTitle.getText().toString()) && !VOICE_RECOG_HINT.equals(tvSpeechbulTitle.getText().toString())) {

            } else {
                startTextInput("");
            }
        }
    }

    /**
     * 结束语音输入，跳转文本输入
     */
    private void startTextInput(String evaluateResult) {
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
        if (!SpeechEvaluatorUtils.isRecogOfflineSuccess()) {
            XESToastUtils.showToast(mContext, "模型正在启动请稍后");
            SpeechEvaluatorUtils.setOnFileSuccess(new SpeechEvaluatorUtils.OnFileSuccess() {
                @Override
                public void onFileSuccess() {
                    XESToastUtils.showToast(mContext, "模型启动成功");
                    startEvaluator();
                }

                @Override
                public void onFileFail() {
                    XESToastUtils.showToast(mContext, "模型启动失败");
                    startTextInput("");
                }
            });
        } else {
            startEvaluator();
        }
    }

    /**
     * ************************************************** 弹 幕 **************************************************
     */

    private static final long ADD_DANMU_TIME = 2000;
    private int BITMAP_WIDTH_GUEST = 34;//头像的宽度
    private int BITMAP_HEIGHT_GUEST = 34;//头像的高度
    private int BITMAP_WIDTH_ME = 42;//头像的宽度
    private int BITMAP_HEIGHT_ME = 42;//头像的高度
    private float DANMU_TEXT_SIZE = 14;//弹幕字体的大小
    private int DANMU_PADDING = 5;//控制两行弹幕之间的间距
    private int DANMU_RADIUS = 16;//圆角半径
    private int DANMU_BACKGROUND_HEIGHT = 33;
//    private CircleDrawable defaultHeadMe;
//    private CircleDrawable defaultHeadGuest;

    /**
     * 初始化弹幕
     */
    protected View initDanmaku() {
        logger.i("initDanmaku()");
        dvSpeechbulDanmaku = new DanmakuView(mContext);
        transformSize(mContext);
//        prepareDefaultHeadImg();
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) // 越大速度越慢
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 图文混排使用BaseCacheStuffer,绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        dvSpeechbulDanmaku.setCallback(new DrawHandler.Callback() {
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
                dvSpeechbulDanmaku.start();
//                generateSomeDanmaku();
            }
        });

        dvSpeechbulDanmaku.prepare(new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        dvSpeechbulDanmaku.showFPS(false);
        dvSpeechbulDanmaku.enableDanmakuDrawingCache(false);
        return dvSpeechbulDanmaku;
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
                            addDanmaKuSpeech(time + "", "i am a speech bullets", "http://xesfile.xesimg.com/user/h/def10002.png", true);
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
     * 加载默认头像
     */
//    private void prepareDefaultHeadImg() {
//        defaultHeadMe = zoomCircleDrable(R.drawable.ic_default_head_square, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
//        defaultHeadMe.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
//        defaultHeadGuest = zoomCircleDrable(R.drawable.ic_default_head_square, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
//        defaultHeadGuest.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
//    }

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
                canvas.drawRoundRect(new RectF(left + danmaku.padding + 1, top + danmaku.padding + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT) / 2 + 1
                                , left + danmaku.paintWidth - danmaku.padding,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT) / 2 + 1 + danmaku.padding),
                        DANMU_RADIUS, DANMU_RADIUS, paint);
            } else {
                canvas.drawRoundRect(new RectF(left + danmaku.padding + 1, top + danmaku.padding + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2 + 1
                                , left + danmaku.paintWidth - danmaku.padding,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2 + 1 + danmaku.padding),
                        DANMU_RADIUS, DANMU_RADIUS, paint);
            }
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
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
            // 重要！清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void addDanmaKuSpeech(final String name, final String msg, final String headImgUrl, final boolean isGuest) {
        if (mDanmakuContext == null || dvSpeechbulDanmaku == null || !dvSpeechbulDanmaku.isPrepared()) {
            mWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addDanmaKuSpeech(name, msg, headImgUrl, isGuest);
                }
            }, 100);
            return;
        }
        final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || dvSpeechbulDanmaku == null) {
            return;
        }
        danmaku.isGuest = isGuest;
        if (isGuest) {
            danmaku.textColor = Color.WHITE;
            danmaku.priority = 0;
            danmaku.padding = DANMU_PADDING;
        } else {
            danmaku.textColor = Color.parseColor("#72DAFB");
            danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕,但会导致限制行数和禁止堆叠失效
            danmaku.padding = DANMU_PADDING - (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2;
        }
        danmaku.isLive = false;
        danmaku.time = dvSpeechbulDanmaku.getCurrentTime() + 1200;
        danmaku.textSize = SizeUtils.Dp2Px(mContext, 14);
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
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
                dvSpeechbulDanmaku.addDanmaku(danmaku);
            }

            @Override
            public void onFail() {
//                if (isGuest) {
//                    danmaku.text = createSpannable(name, msg, defaultHeadGuest, isGuest);
//                } else {
//                    danmaku.text = createSpannable(name, msg, defaultHeadMe, isGuest);
//                }
//                dvSpeechbulDanmaku.addDanmaku(danmaku);
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
                canvas.drawCircle(x + BITMAP_WIDTH_ME / 2, transY + BITMAP_WIDTH_ME / 2, BITMAP_WIDTH_ME / 2, circlePaint);
            }
        }
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

    public void onDestroy() {
        logger.i("onDestroy()");
        if (dvSpeechbulDanmaku != null) {
            dvSpeechbulDanmaku.release();
            dvSpeechbulDanmaku = null;
        }
        stopEvaluator();
    }
}

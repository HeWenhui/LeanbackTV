package com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import android.view.Gravity;
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
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.Contract.ScienceSpeechBullletContract;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.PrimaryChineseDialog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.PrimaryChineseToastDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCacheFile;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by ZhangYuansun on 2018/11/12
 * 小学语文语音弹幕
 * MVP：View层
 */

public class ChineseSpeechBulletPager extends LiveBasePager implements ScienceSpeechBullletContract.ScienceSpeechBulletView {
    /**
     * MVP:Presenter层接口
     */
    protected ScienceSpeechBullletContract.ScienceSpeechBulletPresenter presenter;
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
    private RelativeLayout root;
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
     * 切换手动输入
     */
    private FangZhengCuYuanTextView tvSpeechbulSwitch;
    /**
     * 关闭按钮
     */
    private ImageView ivSpeechbulClose;
    /**
     * 音量波形
     */
    private VolumeWaveView vwvSpeechbulWave;
    /**
     * 话语编辑框
     */
    private EditText etSpeechbulWords;
    /**
     * 计数
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
    private DanmakuContext mDanmakuContext;
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
    protected File dir;
    /**
     * 语音请求和释放
     */
    protected AudioRequest audioRequest;
    /**
     * 是否正在开启语音弹幕
     */
    private boolean isShowingSpeechBullet = false;
    /**
     * 是否恢复了音量
     */
    private boolean isVolumeResume = false;
    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    public ChineseSpeechBulletPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    /**
     * 日志数据
     */
    private String devicestatus = "0";
    private String issend = "0";
    private String ismodify = "0";
    private String isretalk = "0";
    private String isdirty = "0";
    private String text;
    private String aiText;
    private String sid;
    private String closetype;
    private String finalui;
    private String errtype;
    private String errcode;
    private String errmsg;

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_chinese_speech_bullet_screen, null);
        root = mView.findViewById(R.id.rl_livevideo_speechbul_root);
        switchFSPanelLinearLayout = mView.findViewById(R.id.rl_livevideo_speechbul_panelroot);
        tvSpeechbulTitle = mView.findViewById(R.id.tv_livevideo_speechbul_title);
        tvSpeechbulTitleCount = mView.findViewById(R.id.tv_livevideo_speechbul_title_count);
        tvSpeechbulSwitch = mView.findViewById(R.id.tv_livevideo_speechbul_title_switch);
        vwvSpeechbulWave = mView.findViewById(R.id.vwv_livevideo_speechbul_wave);
        ivSpeechbulClose = mView.findViewById(R.id.tv_livevideo_speechbul_close);
        etSpeechbulWords = mView.findViewById(R.id.et_livevideo_speechbul_words);
        tvSpeechbulCount = mView.findViewById(R.id.tv_livevideo_speechbul_count);
        tvSpeechbulRepeat = mView.findViewById(R.id.tv_livevideo_speechbul_repeat);
        tvSpeechbulSend = mView.findViewById(R.id.tv_livevideo_speechbul_send);
        rlSpeechbulInputContent = mView.findViewById(R.id.rl_livevideo_speechbul_input);
        vwvSpeechbulWave.setBackColor(Color.TRANSPARENT);
        vwvSpeechbulWave.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vwvSpeechbulWave.setLinearGradient(new LinearGradient(0, 0, vwvSpeechbulWave.getMeasuredWidth(), 0,
                        new int[]{0xFF86AD72, 0xFF86AD72, 0xFF86AD72}, new float[]{0, 0.5f, 1.0f}, Shader.TileMode.CLAMP));
            }
        });
        //设置方正粗圆字体
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        etSpeechbulWords.setTypeface(fontFace);
        tvSpeechbulTitle.setTypeface(fontFace);
        tvSpeechbulCount.setTypeface(fontFace);
        tvSpeechbulTitleCount.setTypeface(fontFace);
        root.setClickable(true);
        return mView;
    }

    private void initSkin() {
        tvSpeechbulTitle.setText(VOICE_RECOG_HINT);
        GradientDrawable mGroupDrawable = (GradientDrawable) etSpeechbulWords.getBackground();
        mGroupDrawable.setStroke(SizeUtils.Dp2Px(mContext, 0), 0xFFB9D396);
        tvSpeechbulSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTextInput("");
            }
        });
        vwvSpeechbulWave.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vwvSpeechbulWave.setLinearGradient(new LinearGradient(0, 0, vwvSpeechbulWave.getMeasuredWidth(), 0,
                        new int[]{0xFF86AD72, 0xFF86AD72, 0xFF86AD72}, new float[]{0, 0.5f, 1.0f}, Shader.TileMode.CLAMP));
            }
        });
    }

    @Override
    public void initData() {
        mView.postDelayed(new Runnable() {
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
        checkAudioPermission();

        //系统日志
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "voiceBarrageSwitch");
        mData.put("pageid", "voice_barrage");
        mData.put("voiceid", presenter.getVoiceId());
        mData.put("cmdtype", "1");
        mData.put("devicestatus", devicestatus);
        umsAgentDebugSys(LiveVideoConfig.LIVE_SPEECH_BULLETSCREEN, mData);
    }

    private void checkAudioPermission() {
        boolean hasAudidoPermission = XesPermission.hasSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
        // 检查用户麦克风权限
        if (hasAudidoPermission) {
            devicestatus = "1";
            startEvaluator();
        } else {
            //如果没有麦克风权限，申请麦克风权限
            devicestatus = "0";
            XesPermission.checkPermissionNoAlert(mContext, new LiveActivityPermissionCallback() {
                /**
                 * 结束
                 */
                @Override
                public void onFinish() {
                    logger.i("onFinish()");
                }

                /**
                 * 用户拒绝某个权限
                 */
                @Override
                public void onDeny(String permission, int position) {
                    logger.i("onDeny()");
                    startTextInput("");
                }

                /**
                 * 用户允许某个权限
                 */
                @Override
                public void onGuarantee(String permission, int position) {
                    logger.i("onGuarantee()");
                    startEvaluator();
                }
            }, PermissionConfig.PERMISSION_CODE_AUDIO);
        }
    }

    @Override
    public void initListener() {
        logger.i("initListener()");

        //关闭语音识别
        ivSpeechbulClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logger.i("onClick: ivSpeechbulClose");
                final PrimaryChineseDialog closeConfirmDialog = new PrimaryChineseDialog(mContext);
                closeConfirmDialog.setOnClickCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeConfirmDialog.cancelDialog();
                    }
                });
                closeConfirmDialog.setOnClickConfirmlListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeConfirmDialog.cancelDialog();
                        closetype = "activeClose";
                        closeSpeechBullet(false);
                    }
                });
                closeConfirmDialog.showDialog();
            }
        });

        //编辑话语
        etSpeechbulWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logger.i("onClick: etSpeechbulWords");
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etSpeechbulWords);
            }
        });
        etSpeechbulWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (StringUtils.isSpace(charSequence.toString())) {
                    tvSpeechbulSend.setEnabled(false);
                    tvSpeechbulSend.setAlpha(0.6f);
                    tvSpeechbulCount.setAlpha(0.6f);
                } else {
                    tvSpeechbulSend.setEnabled(true);
                    tvSpeechbulSend.setAlpha(1.0f);
                    tvSpeechbulCount.setAlpha(1.0f);
                }
                tvSpeechbulCount.setText(charSequence.toString().length() + "/15");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etSpeechbulWords.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //判断是否是“发送”键
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
                logger.i("onClick: tvSpeechbulRepeat");
                isretalk = "1";
                checkAudioPermission();
            }
        });
        //发送语音弹幕
        tvSpeechbulSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logger.i("onClick: tvSpeechbulSend");
                issend = "1";
                text = etSpeechbulWords.getText().toString();
                if (!text.equals(aiText)) {
                    ismodify = "1";
                }
                closetype = "sendSuccessClose";
                closeSpeechBullet(false);
                addDanmaKu("我", etSpeechbulWords.getText().toString(), presenter.getHeadImgUrl(),
                        false);
                presenter.sendDanmakuMessage(etSpeechbulWords.getText().toString());
                presenter.uploadSpeechBulletScreen(etSpeechbulWords.getText().toString(), new
                        HttpCallBack(false) {
                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                Map<String, String> mData = new HashMap<>();
                                mData.put("logtype", "voiceBarrageSuccess");
                                mData.put("pageid", "voice_barrage");
                                mData.put("voiceid", presenter.getVoiceId());
                                umsAgentDebugSys(LiveVideoConfig.LIVE_SPEECH_BULLETSCREEN, mData);
                            }

                            @Override
                            public void onPmError(ResponseEntity responseEntity) {
                                Map<String, String> mData = new HashMap<>();
                                mData.put("logtype", "voiceBarrageError");
                                mData.put("pageid", "voice_barrage");
                                mData.put("voiceid", presenter.getVoiceId());
                                mData.put("errtype", "uploaderror");
                                mData.put("errmsg", responseEntity.getErrorMsg());
                                umsAgentDebugSys(LiveVideoConfig.LIVE_SPEECH_BULLETSCREEN, mData);
                            }

                            @Override
                            public void onPmFailure(Throwable error, String msg) {
                                Map<String, String> mData = new HashMap<>();
                                mData.put("logtype", "voiceBarrageError");
                                mData.put("pageid", "voice_barrage");
                                mData.put("voiceid", presenter.getVoiceId());
                                mData.put("errtype", "uploaderror");
                                mData.put("errmsg", msg);
                                umsAgentDebugSys(LiveVideoConfig.LIVE_SPEECH_BULLETSCREEN, mData);
                            }
                        });
            }
        });

        mWeakHandler.postDelayed(new Runnable() {
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

    /**
     * ************************************************** View层接口实现 **************************************************
     */
    @Override
    public void showSpeechBullet(RelativeLayout rootView) {
        logger.i("showSpeechBullet");
        this.rootView = rootView;
        if (rlSpeechBulContent == null) {
            rlSpeechBulContent = new RelativeLayout(mContext);
            rlSpeechBulContent.setId(R.id.rl_livevideo_content_speechbul);
            rootView.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        showStartSpeechBulletToast();
        initUmsAgentData();
        closeSpeechBullet(false);
        mWeakHandler.postDelayed(showSpeechBulletRunnable, 2000);

    }

    private Runnable showSpeechBulletRunnable = new Runnable() {
        @Override
        public void run() {
            isShowingSpeechBullet = true;
            if (mDanmakuView == null) {
                rlSpeechBulContent.addView(initDanmaku(), new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
            rlSpeechBulContent.addView(initView().getRootView(), new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rlSpeechBulContent.setVisibility(View.VISIBLE);
            initSkin();
            initData();
            initListener();
        }
    };

    @Override
    public void closeSpeechBullet(boolean hasTip) {
        logger.i("closeSpeechBullet");
        if (rootView == null || rlSpeechBulContent == null) {
            return;
        }
        if (hasTip) {
            mWeakHandler.removeCallbacks(showSpeechBulletRunnable);
            if (tvSpeechbulCloseTip == null) {
                tvSpeechbulCloseTip = new FangZhengCuYuanTextView(mContext);
                tvSpeechbulCloseTip.setBackgroundResource(R.drawable.shellwindow_forbbiden_voicebullet_board);
                tvSpeechbulCloseTip.setTextColor(Color.parseColor("#FF755942"));
                tvSpeechbulCloseTip.setTextSize(19);
                tvSpeechbulCloseTip.setGravity(Gravity.CENTER);
                rlSpeechBulContent.addView(tvSpeechbulCloseTip, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvSpeechbulCloseTip.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                tvSpeechbulCloseTip.setLayoutParams(layoutParams);
                tvSpeechbulCloseTip.setVisibility(View.GONE);
            }
            if (!closetype.equals("sendSuccessClose") && !closetype.equals("activeClose")) {
                //已发送和主动关闭，不弹倒计时提示
                countDownTimer.start();
            }

            //系统日志
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "voiceBarrageSwitch");
            mData.put("pageid", "voice_barrage");
            mData.put("voiceid", presenter.getVoiceId());
            mData.put("cmdtype", "0");
            mData.put("devicestatus", devicestatus);
            umsAgentDebugSys(LiveVideoConfig.LIVE_SPEECH_BULLETSCREEN, mData);

        } else {
            removeSpeechBullet();
        }
    }

    private CountDownTimer countDownTimer = new CountDownTimer(2100, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            int i = (int) (millisUntilFinished / 1000);
            if (i > 0) {
                tvSpeechbulCloseTip.setVisibility(View.VISIBLE);
                tvSpeechbulCloseTip.setText(i + "S后将禁止发弹幕");
            }
        }

        @Override
        public void onFinish() {
            closetype = "passiveClose";
            tvSpeechbulCloseTip.setVisibility(View.GONE);
            removeSpeechBullet();
        }
    };

    private void removeSpeechBullet() {
        if (!isShowingSpeechBullet) {
            return;
        }
        isShowingSpeechBullet = false;

        KeyboardUtil.hideKeyboard(root);
        rlSpeechBulContent.removeView(root);
        root.setClickable(false);
        stopEvaluator();

        //交互日志
        if (tvSpeechbulTitle.getVisibility() == View.VISIBLE) {
            finalui = "recogWidget";
        } else {
            finalui = "inputWidget";
        }
        Map<String, String> mDataInter = new HashMap<>();
        mDataInter.put("logtype", "voiceBarrageOperation");
        mDataInter.put("pageid", "voice_barrage");
        mDataInter.put("voiceid", presenter.getVoiceId());
        mDataInter.put("issend", issend);
        mDataInter.put("ismodify", ismodify);
        mDataInter.put("isretalk", isretalk);
        mDataInter.put("isdirty", isdirty);
        mDataInter.put("text", text);
        mDataInter.put("aitext", aiText);
        mDataInter.put("sid", sid);
        mDataInter.put("closetype", closetype);
        mDataInter.put("finalui", finalui);
        umsAgentDebugInter(LiveVideoConfig.LIVE_SPEECH_BULLETSCREEN, mDataInter);
    }

    @Override
    public void receiveDanmakuMsg(String name, String msg, String headImgUrl, boolean isGuset, RelativeLayout rootView) {
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
            addDanmaKu(name, msg, headImgUrl, isGuset);
        }
    }

    @Override
    public void receivePraiseMsg(String msg) {

    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {

    }

    @Override
    public void setPresenter(ScienceSpeechBullletContract.ScienceSpeechBulletPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * 初始化日志数据
     */
    private void initUmsAgentData() {
        devicestatus = "0";
        issend = "0";
        ismodify = "0";
        isretalk = "0";
        isdirty = "0";
        text = "";
        aiText = "";
        sid = "";
        closetype = "";
        finalui = "";
        errtype = "";
        errcode = "";
        errmsg = "";
    }

    private void showStartSpeechBulletToast() {
        PrimaryChineseToastDialog startSpeechBulletToast = new PrimaryChineseToastDialog(mContext);
        startSpeechBulletToast.setText("老师开启了语音弹幕");
        startSpeechBulletToast.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf"));
        startSpeechBulletToast.showDialogAutoClose(2000);
    }

    /**
     * ************************************************** 语音识别 **************************************************
     */
    private final static String VOICE_RECOG_HINT = "语音录入中（15字以内）";
    private final static String VOICE_RECOG_NOVOICE_HINT = "没听清，请重说或切换";

    private int START = 1;
    private int RECOGNIZING = 2;
    private int PLEASE_SAY_AGGIN = 3;
    private int TEXT_INPUT = 4;
    private int recoginzeStatus = 0;

    private Runnable pleaseSayAgainRunnable = new Runnable() {
        @Override
        public void run() {
            pleaseSayAgain();
        }
    };

    protected void startEvaluator() {
        logger.i("startEvaluator()");
        recoginzeStatus = START;

        File saveFile = new File(dir, "speechbul" + System.currentTimeMillis() + ".mp3");
        mSpeechEvaluatorUtils.startChineseSpeechBulletRecognize(saveFile.getPath(), SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                new EvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        logger.i("onBeginOfSpeech()");

                        if (recoginzeStatus == START) {
                            tvSpeechbulTitle.setText(VOICE_RECOG_HINT);
                            mWeakHandler.postDelayed(pleaseSayAgainRunnable, 5000);
                        }

                        KeyboardUtil.hideKeyboard(mView);
                        tvSpeechbulTitleCount.setText("");
                        rlSpeechbulInputContent.setVisibility(View.GONE);
                        tvSpeechbulTitle.setVisibility(View.VISIBLE);
                        tvSpeechbulTitleCount.setVisibility(View.VISIBLE);
                        vwvSpeechbulWave.setVisibility(View.VISIBLE);

                        //静音处理
                        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
                        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
                        mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
                        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (0.0f * mMaxVolume), 0);
                        isVolumeResume = false;
                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {
                        logger.i("onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo() + ",sid=" + resultEntity.getSid());
                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                            if (resultEntity.getSid() != null) {
                            }
                            onEvaluatorSuccess(resultEntity.getCurString(), true);
                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            if (resultEntity.getSid() != null) {
                            }
                            onEvaluatorError(resultEntity);
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                            onEvaluatorSuccess(resultEntity.getCurString(), false);
                        }
                    }

                    @Override
                    public void onVolumeUpdate(int volume) {
                        logger.d("onVolumeUpdate:volume=" + volume);
                        vwvSpeechbulWave.setVolume(volume * 3);
                    }
                });
    }

    private void stopEvaluator() {
        logger.i("stopEvaluator()");
        if (mSpeechEvaluatorUtils != null) {
            mSpeechEvaluatorUtils.cancel();
        }
        if (mAM != null && !isVolumeResume) {
            mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
            isVolumeResume = true;
        }
        recoginzeStatus = 0;
        mWeakHandler.removeCallbacks(pleaseSayAgainRunnable);
    }

    protected void onEvaluatorSuccess(String str, boolean isSpeechFinished) {
        logger.i("onEvaluatorSuccess():isSpeechFinish=" + isSpeechFinished);
        try {
            JSONObject jsonObject = new JSONObject(str);
            String content = jsonObject.optString("nbest");
            JSONArray array = jsonObject.optJSONArray("sensitiveWords");
            if (array != null && array.length() > 0) {
            }
            if (array != null && array.length() > 0) {
                for (int i = array.length() - 1; i >= 0; i--) {
                    StringBuilder star = new StringBuilder();
                    for (int j = 0; j < array.getString(i).length(); j++) {
                        star.append("*");
                    }
                    content = content.replaceAll(array.getString(i), star.toString());
                }
            }
            content = content.replaceAll("。", "");
            //语音录入，限制15字以内
            if (content.length() > 15) {
                content = content.substring(0, 15);
            }
            logger.i("=====speech evaluating" + content);
            if (isSpeechFinished) {
                if (!StringUtils.isSpace(content)) {
                    startTextInput(content);
                } else {
                    pleaseSayAgain();
                }
            } else {
                if (!TextUtils.isEmpty(content)) {
                    tvSpeechbulTitle.setText(content);
                    tvSpeechbulTitleCount.setText("（" + content.length() + "/15）");
                    tvSpeechbulSwitch.setVisibility(View.GONE);
                    recoginzeStatus = RECOGNIZING;
                    mWeakHandler.removeCallbacks(pleaseSayAgainRunnable);

                    //15字截停
                    if (content.length() == 15) {
                        final String finalContent = content;
                        mWeakHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (recoginzeStatus == RECOGNIZING) {
                                    startTextInput(finalContent);
                                }
                            }
                        }, 3000);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onEvaluatorError(ResultEntity resultEntity) {
        logger.i("onEvaluatorError()");
        String content = tvSpeechbulTitle.getText().toString();
        if (!StringUtils.isSpace(content) && !content.equals(VOICE_RECOG_HINT) && !content.equals(VOICE_RECOG_NOVOICE_HINT)) {
            startTextInput(content);
            return;
        }
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode.MUTE) {
            logger.i("声音有点小，再来一次哦！");
            mWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEvaluator();
                    pleaseSayAgain();
                }
            }, 300);
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
            if (recoginzeStatus == RECOGNIZING) {
                startTextInput(tvSpeechbulTitle.getText().toString());
            } else {
                startTextInput("");
            }
        }

        //系统日志
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "voiceBarrageError");
        mData.put("pageid", "voice_barrage");
        mData.put("voiceid", presenter.getVoiceId());
        mData.put("errtype", "recogerror");
        mData.put("errcode", resultEntity.getErrorNo() + "");
        umsAgentDebugSys(LiveVideoConfig.LIVE_SPEECH_BULLETSCREEN, mData);
    }

    /**
     * 结束语音输入，跳转文本输入
     */
    private void startTextInput(String evaluateResult) {
        stopEvaluator();
        recoginzeStatus = TEXT_INPUT;

        tvSpeechbulTitle.setVisibility(View.GONE);
        tvSpeechbulTitleCount.setVisibility(View.GONE);
        vwvSpeechbulWave.setVisibility(View.GONE);
        rlSpeechbulInputContent.setVisibility(View.VISIBLE);
        tvSpeechbulRepeat.setVisibility(View.VISIBLE);
        etSpeechbulWords.setText(evaluateResult);
        tvSpeechbulCount.setText(evaluateResult.length() + "/15");
        etSpeechbulWords.requestFocus();
        etSpeechbulWords.setSelection(etSpeechbulWords.getText().toString().length());
        tvSpeechbulSwitch.setVisibility(View.GONE);
        if (StringUtils.isSpace(evaluateResult)) {
            tvSpeechbulSend.setEnabled(false);
            tvSpeechbulSend.setAlpha(0.6f);
            tvSpeechbulCount.setAlpha(0.6f);
        } else {
            tvSpeechbulSend.setEnabled(true);
            tvSpeechbulSend.setAlpha(1.0f);
            tvSpeechbulCount.setAlpha(1.0f);
        }
        aiText = evaluateResult;
    }

    /**
     * 没听清，请重说
     */
    private void pleaseSayAgain() {
        logger.i("pleaseSayAgain()");
        recoginzeStatus = PLEASE_SAY_AGGIN;
        tvSpeechbulTitle.setText(VOICE_RECOG_NOVOICE_HINT);
        tvSpeechbulSwitch.setVisibility(View.VISIBLE);
    }

    /**
     * ************************************************** 弹 幕 **************************************************
     */

    private static final long ADD_DANMU_TIME = 1200;
    private int BITMAP_WIDTH_GUEST = 34;//头像的宽度
    private int BITMAP_HEIGHT_GUEST = 34;//头像的高度
    private int BITMAP_WIDTH_ME = 42;//头像的宽度
    private int BITMAP_HEIGHT_ME = 42;//头像的高度
    private float DANMU_TEXT_SIZE = 14;//弹幕字体的大小
    private int DANMU_PADDING = 5;//控制两行弹幕之间的间距
    private int DANMU_RADIUS = 16;//圆角半径
    private int DANMU_BACKGROUND_HEIGHT = 33;

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
    protected void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    mWeakHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            addDanmaKu(time + "", time + "", "", true);
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
     * 对数值进行转换，适配手机，必须在初始化之前，否则有些数据不会起作用
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
    protected class BackgroundCacheStuffer extends SpannedCacheStuffer {
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
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    private void addDanmaKu(final String name, final String msg, final String headImgUrl, final boolean isGuest) {
        if (mDanmakuContext == null || mDanmakuView == null || !mDanmakuView.isPrepared()) {
            mWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addDanmaKu(name, msg, headImgUrl, isGuest);
                }
            }, 100);
            return;
        }
        //如果长时间没有弹幕，可能会休眠
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
        if (!isGuest) {
            mDanmakuView.pause();
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
            danmaku.textColor = Color.YELLOW;
        }
        danmaku.isGuest = isGuest;
        danmaku.isLive = false;
        danmaku.time = mDanmakuView.getCurrentTime() + ADD_DANMU_TIME;
        danmaku.textSize = SizeUtils.Dp2Px(mContext, 14f);
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        ImageLoader.with(mContext).load(headImgUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Drawable circleDrawable = drawable;
                if (isGuest) {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                } else {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                }
                danmaku.text = createSpannable(name, msg, circleDrawable, isGuest);
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
    protected class VerticalImageSpan extends ImageSpan {
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
                circlePaint.setColor(Color.YELLOW);
                circlePaint.setStrokeWidth(SizeUtils.Dp2Px(mContext, 1));
                canvas.drawCircle(x + BITMAP_WIDTH_ME / 2, transY + BITMAP_WIDTH_ME / 2, BITMAP_WIDTH_ME / 2,
                        circlePaint);
            }
        }

    }

    @Override
    public void onStop() {
        if (!isShowingSpeechBullet) {
            return;
        }
        if (recoginzeStatus == RECOGNIZING) {
            startTextInput(tvSpeechbulTitle.getText().toString());
        } else if (recoginzeStatus == START || recoginzeStatus == PLEASE_SAY_AGGIN) {
            startTextInput("");
        }
    }

    public void onDestroy() {
        logger.i("onDestroy()");
        super.onDestroy();
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        stopEvaluator();
    }
}

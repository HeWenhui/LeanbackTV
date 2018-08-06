package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.common.data.AppCacheData;
import com.xueersi.common.permission.PermissionCallback;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.CheckUtil;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenBll;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenHttp;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page.BaseSpeechBulletScreenPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RoomAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.CloseConfirmDialog;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.ShortToastDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.KeyboardPopWindow;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;
import com.xueersi.parentsmeeting.widget.blurpopupwindow.BlurPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;
import master.flame.danmaku.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.danmaku.ui.widget.DanmakuTextureView;
import master.flame.danmaku.danmaku.ui.widget.DanmakuView;

/**
 * 语音弹幕页面
 * Created by Zhang Yuansun on 2018/7/11.
 */

public class SpeechBulletScreenPager extends BaseSpeechBulletScreenPager implements RoomAction, KeyboardPopWindow.KeyboardObserver {
    /** 语音录入标题 */
    TextView tvSpeechbulTitle;
    /** 关闭按钮 */
    ImageView ivSpeechbulClose;
    /** 音量波形 */
    VolumeWaveView vwvSpeechbulWave;
    /** 话语编辑框 */
    EditText etSpeechbulWords;
    /** 计数 */
    TextView tvSpeechbulCount;
    /** 重说按钮 */
    ImageView tvSpeechbulRepeat;
    /** 发送按钮 */
    ImageView tvSpeechbulSend;
    /** 输入模块的布局 */
    RelativeLayout rlSpeechbulInput;
    /** 底部语音识别模块的布局 */
    RelativeLayout rlSpeechbulBottomContent;
    /** 根布局 */
    RelativeLayout root;
    private View mCloseDialog;
    private KeyboardPopWindow keyboardPopWindow;
    private LiveMessageBll liveMessageBll;

    private SpeechBulletScreenHttp speechBulletScreenHttp;
    public void setSpeechBulletScreenHttp(SpeechBulletScreenHttp speechBulletScreenHttp) {
        this.speechBulletScreenHttp = speechBulletScreenHttp;
    }

    // 面板View
//    private KPSwitchFSPanelLinearLayout mPanelLayout;

    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    private DanmakuView dvSpeechbulDanmaku;
    protected DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;

    /** 语音评测工具类 */
    private SpeechEvaluatorUtils mSpeechEvaluatorUtils;
    /** 是不是评测失败 */
    private boolean isSpeechError = false;
    /** 是不是评测成功 */
    private boolean isSpeechSuccess = false;
    SpeechBulletScreenBll speechBulletScreenBll;
    private int status;
    public final static int RECORDING = 1;
    public final static int ERROR = 2;
    public final static int NOPERMISSION = 3;

    public SpeechBulletScreenPager(Context context, SpeechBulletScreenBll speechBulletScreenBll) {
        super(context);
        this.speechBulletScreenBll = speechBulletScreenBll;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        Log.d(TAG,"initView()");

        View view = View.inflate(mContext, R.layout.page_livevideo_speech_bullet_screen,null);
        tvSpeechbulTitle = (TextView) view.findViewById(R.id.tv_livevideo_speechbul_title);
        ivSpeechbulClose = (ImageView) view.findViewById(R.id.tv_livevideo_speechbul_close);
        vwvSpeechbulWave = (VolumeWaveView) view.findViewById(R.id.vwv_livevideo_speechbul_wave);
        etSpeechbulWords = (EditText) view.findViewById(R.id.et_livevideo_speechbul_words);
        tvSpeechbulCount = (TextView) view.findViewById(R.id.tv_livevideo_speechbul_count);
        tvSpeechbulRepeat = (ImageView) view.findViewById(R.id.tv_livevideo_speechbul_repeat);
        tvSpeechbulSend = (ImageView) view.findViewById(R.id.tv_livevideo_speechbul_send);
        dvSpeechbulDanmaku =  view.findViewById(R.id.dv_livevideo_speechbul_danmaku);
        rlSpeechbulInput = view.findViewById(R.id.rl_livevideo_speechbul_input);
        rlSpeechbulBottomContent = view.findViewById(R.id.rl_livevideo_speechbul_bottom_content);
        root = view.findViewById(R.id.rl_livevideo_speechbul_root);
        int colors[] = {0x19FFA63C, 0x32FFA63C, 0x64FFC12C, 0x96FFC12C, 0xFFFFA200};
        vwvSpeechbulWave.setColors(colors);
        vwvSpeechbulWave.setBackColor(Color.TRANSPARENT);
//        mPanelLayout = (KPSwitchFSPanelLinearLayout)view.findViewById(R.id.rl_livevideo_speechbul_panelroot);

        keyboardPopWindow = new KeyboardPopWindow((LiveVideoActivity)mContext);
        keyboardPopWindow.showAtLocation(root, Gravity.BOTTOM,0,0);

        return view;
    }

    @Override
    public void initData() {
        Log.d(TAG,"initData()");
//        mWeakHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                vwvSpeechbulWave.start();
//                if (mSpeechEvaluatorUtils == null) {
//                    mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
//                }
//                startEvaluator();
//            }
//        });
        XesPermission.checkPermission(mContext, new LiveActivityPermissionCallback() {
            /**
             * 结束
             */
            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish()");
            }

            /**
             * 用户拒绝某个权限
             */
            @Override
            public void onDeny(String permission, int position) {
                Log.d(TAG, "onDeny()");
            }

            /**
             * 用户允许某个权限
             */
            @Override
            public void onGuarantee(String permission, int position) {
                Log.d(TAG, "onGuarantee()");
            }
        }, PermissionConfig.PERMISSION_CODE_AUDIO);
        liveMessageBll = ProxUtil.getProxUtil().get(mContext, LiveMessageBll.class);
        initDanmaku();
    }

    @Override
    public void initListener() {
        Log.d(TAG,"initListener()");
        super.initListener();

        //关闭语音识别
        ivSpeechbulClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: ivSpeechbulClose");

//                final CloseConfirmDialog  closeConfirmDialog = new CloseConfirmDialog(mContext);
//                closeConfirmDialog.setOnClickCancelListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        closeConfirmDialog.cancelDialog();
//                    }
//                });
//                closeConfirmDialog.setOnClickConfirmlListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        root.removeView(mCloseDialog);
//                        root.removeView(rlSpeechbulBottomContent);
//                        stopEvaluator();
//                    }
//                });
//                closeConfirmDialog.showDialog();

                KeyboardUtil.hideKeyboard(root);

                mCloseDialog = ((Activity)mContext).getLayoutInflater().inflate(R.layout.dialog_livevideo_speechbul_close,
                        root,
                        false);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCloseDialog.getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                root.addView(mCloseDialog, params);
                mCloseDialog.findViewById(R.id.iv_livevideo_speechbul_close_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        root.removeView(mCloseDialog);
                    }
                });
                mCloseDialog.findViewById(R.id.iv_livevideo_speechbul_close_confim).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        root.removeView(mCloseDialog);
                        root.removeView(rlSpeechbulBottomContent);
                        stopEvaluator();
                    }
                });
            }
        });
        //编辑话语
        etSpeechbulWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: etSpeechbulWords");
//                KPSwitchConflictUtil.showKeyboard(mPanelLayout, etSpeechbulWords);
            }
        });
        //重新开启语音评测
        tvSpeechbulRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: tvSpeechbulRepeat");
                vwvSpeechbulWave.setVisibility(View.VISIBLE);
                tvSpeechbulTitle.setVisibility(View.VISIBLE);
                rlSpeechbulInput.setVisibility(View.GONE);
                startEvaluator();
            }
        });
        //发送语音弹幕
        tvSpeechbulSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: tvSpeechbulSend");
                addDanmaKuFlowers( "我", etSpeechbulWords.getText().toString(),"http://xesfile.xesimg.com/user/h/def10002.png" ,false);
            }
        });

        keyboardPopWindow.setKeyboardObserver(this);
    }

    private void showShortToast(String tips) {
        ShortToastDialog shortToastDialog= new ShortToastDialog(mContext);
        shortToastDialog.setTips(tips);
        shortToastDialog.showDialog();
    }



    /**
     * ************************************************** 语音识别工具 **************************************************
     */

    /**
     * 设置状态：录音/失败/无权限
     */
    private void setStatus(int status){
        this.status = status;
        switch (status) {
            case RECORDING:
                break;
            case ERROR:
                break;
            case NOPERMISSION:
                break;
            default:
                break;
        }
    }

    private void startEvaluator() {
        Log.d(TAG,"startEvaluator()");
        File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/voice/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String saveFile = dir + "/speechbul.mp3";
        mSpeechEvaluatorUtils.startOnlineRecognize(saveFile, SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                new EvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        Log.d(TAG, "onBeginOfSpeech");
                        isSpeechError = false;
                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {
                        Log.d(TAG, "onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo());
                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                            if (resultEntity.getErrorNo() > 0) {
                                onEvaluatorError(resultEntity);
                            } else {
                                onEvaluatorSuccess(resultEntity.getCurString(), true);
                            }
                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            onEvaluatorError(resultEntity);
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                            onEvaluatorSuccess(resultEntity.getCurString(), false);
                        }
                    }

                    @Override
                    public void onVolumeUpdate(int volume) {
                        Loger.d(TAG, "onVolumeUpdate:volume=" + volume);
                        vwvSpeechbulWave.setVolume(volume * 3);
                    }
                });
    }

    @Override
    public void stopEvaluator() {
        Log.d(TAG,"stopEvaluator()");
        if (mSpeechEvaluatorUtils != null) {
            mSpeechEvaluatorUtils.stop();
        }
    }

    private void onEvaluatorSuccess(String str, boolean isSpeechFinished) {
        Log.d(TAG,"onEvaluatorSuccess():isSpeechFinish=" + isSpeechFinished);
        try {
            JSONObject jsonObject = new JSONObject(str);
            String content = jsonObject.optString("nbest");
            JSONArray array = jsonObject.optJSONArray("sensitiveWords");
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
            if (!TextUtils.isEmpty(content)) {
                Log.d(TAG,"=====speech evaluating" + content);
                tvSpeechbulTitle.setText(content);
            }
            if (isSpeechFinished ) {
                mSpeechEvaluatorUtils.cancel();
                vwvSpeechbulWave.setVisibility(View.GONE);
                tvSpeechbulTitle.setVisibility(View.GONE);
                rlSpeechbulInput.setVisibility(View.VISIBLE);
                etSpeechbulWords.setText(content);
                tvSpeechbulCount.setText(getChineseCharNumber(content)+"/15");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onEvaluatorError(ResultEntity resultEntity) {
        Log.d(TAG,"onEvaluatorError()");
        isSpeechError = true;
        if (TextUtils.isEmpty(tvSpeechbulTitle.getText().toString())
                || tvSpeechbulTitle.getText().toString().equals("语音录入中（15字以内）")){
            if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode.MUTE) {
                Log.d(TAG,"声音有点小，再来一次哦！");
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startEvaluator();
                    }
                }, 300);
                return;
            } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
                Log.d(TAG,"麦克风不可用，快去检查一下！");
            } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() == ResultCode.NETWORK_FAIL
                    || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
                int netWorkType = NetWorkHelper.getNetWorkState(mContext);
                if (netWorkType == NetWorkHelper.NO_NETWORK) {
                    Log.d(TAG,"好像没网了，快检查一下");
                } else {
                    Log.d(TAG,"服务器连接不上");
                }
            }
        } else {
            onEvaluatorSuccess(str2json(tvSpeechbulTitle.getText().toString()), true);
        }
    }

    private String str2json(String str) {
        JSONObject object = new JSONObject();
        try {
            object.put("nbest", str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 统计汉字数量
     */
    private int getChineseCharNumber(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char tmp = str.charAt(i);
            if (CheckUtil.isChinese(tmp)){
                count++;
            }
        }
        return count;
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        Rect r = new Rect();

        //View在屏幕中的位置
        rlSpeechbulBottomContent.getGlobalVisibleRect(r);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlSpeechbulBottomContent.getLayoutParams();
        //计算需要的偏移量
//        int offset = height - (((LiveVideoActivity)mContext).getWindowManager().getDefaultDisplay().getHeight() - r.bottom);
        if (height == 0){
            params.bottomMargin = 0;
            tvSpeechbulRepeat.setVisibility(View.VISIBLE);
        } else if (height>100){
            params.bottomMargin = height - SizeUtils.Dp2Px(mContext,37);
            tvSpeechbulRepeat.setVisibility(View.GONE);
        }

        //通过设置View的bottomMargin改变其位置
        Loger.d("____软键盘  view margin：  "+params.bottomMargin);
        rlSpeechbulBottomContent.setLayoutParams(params);
    }

    /**
     * ************************************************** 弹 幕 **************************************************
     */

    private static final long ADD_DANMU_TIME = 2000;
    private int   BITMAP_WIDTH_GUEST    = 34;//头像的宽度
    private int   BITMAP_HEIGHT_GUEST   = 34;//头像的高度
    private int   BITMAP_WIDTH_ME       = 42;//头像的宽度
    private int   BITMAP_HEIGHT_ME      = 42;//头像的高度
    private float DANMU_TEXT_SIZE       = 14;//弹幕字体的大小
    private int DANMU_PADDING           = 11;//控制两行弹幕之间的间距
    private int DANMU_RADIUS            = 16;//圆角半径
    private int DANMU_BACKGROUND_HEIGHT = 33;

    /**
     * 对数值进行转换，适配手机，必须在初始化之前，否则有些数据不会起作用
     */
    private void setSize(Context context) {
        BITMAP_WIDTH_GUEST = SizeUtils.Dp2Px(context, BITMAP_WIDTH_GUEST);
        BITMAP_HEIGHT_GUEST = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_GUEST);
        BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
        BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
        DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
        DANMU_RADIUS = SizeUtils.Dp2Px(context, DANMU_RADIUS);
        DANMU_TEXT_SIZE = SizeUtils.Dp2Px(context, DANMU_TEXT_SIZE);
        DANMU_BACKGROUND_HEIGHT = SizeUtils.Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
    }

    /** 同学献花提示 */
    public String[] flowsTips = {"老师真赞", "老师太棒了", "老师辛苦了"};
    /** 同学献花花的资源,小，弹幕和聊天中用 */
    public int[] flowsDrawLittleTips = {R.drawable.bg_livevideo_flower_small2, R.drawable.bg_livevideo_flower_middle2, R.drawable.bg_livevideo_flower_big2,};
    /** 同学献花,小中大 */
    public static final int FLOWERS_SMALL = 2, FLOWERS_MIDDLE = 3, FLOWERS_BIG = 4;
    /** 献花字体颜色 */
    public int[] flowsDrawColors = {0xFF1a8615, Color.RED, 0xFFff00ea};

    protected void initDanmaku() {
        setSize(mContext);
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
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
            }

            @Override
            public void prepared() {
                dvSpeechbulDanmaku.start();
                generateSomeDanmaku();
            }
        });

        dvSpeechbulDanmaku.prepare(new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        dvSpeechbulDanmaku.showFPS(false);
        dvSpeechbulDanmaku.enableDanmakuDrawingCache(true);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    final int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    mWeakHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            addDanmaKuFlowers(time + "",time + "","http://xesfile.xesimg.com/user/h/def10002.png" ,true);
                        }
                    });
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onUserList(String channel, User[] users) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, final String sender, String login, String hostname, String target, final String message) {
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    int type = jsonObject.getInt("type");
                    if (type == XESCODE.XCR_ROOM_DANMU_SEND) {
//                        {
//                                "type":"261",
//                                "headImg":"http://xesfile.xesimg.com/user/h/def10002.png",
//                                "senderId":"s_3_205592_17600_1",
//                                "name":"张远荪",
//                                "msg":"老师好"
//                        }
                        String name = jsonObject.optString("name");
                        String headImgUrl = jsonObject.optString("headImg");
                        String msg = jsonObject.optString("msg");
                        addDanmaKuFlowers(name, msg, headImgUrl,true);
                    }
                } catch (JSONException e) {
                    liveMessageBll.addMessage("",0,"");
                }
            }
        });
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onDisable(boolean disable, boolean fromNotice) {

    }

    @Override
    public void onOtherDisable(String id, String name, boolean disable) {

    }

    @Override
    public void onopenchat(boolean openchat, String mode, boolean fromNotice) {

    }

    @Override
    public void onOpenbarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void videoStatus(String status) {

    }

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//=            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setAlpha((int)(255*0.6)); //  透明度0.6

            if (danmaku.isGuest) {
                canvas.drawRoundRect(new RectF(left + danmaku.padding, top + danmaku.padding + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT)/2 + 1
                                , left + danmaku.paintWidth - danmaku.padding ,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT)/2 + 1 + danmaku.padding),
                        DANMU_RADIUS, DANMU_RADIUS, paint);
            }
            else {
                canvas.drawRoundRect(new RectF(left + danmaku.padding, top + danmaku.padding + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT)/2 + 1
                                , left + danmaku.paintWidth - danmaku.padding ,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT)/2 + 1 + danmaku.padding),
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
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void addDanmaKuFlowers(final String name, final String msg, final String headImgUrl , final boolean isGuest) {
        if (mDanmakuContext == null) {
            mWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addDanmaKuFlowers(name, msg, headImgUrl, isGuest);
                }
            }, 20);
            return;
        }
        final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || dvSpeechbulDanmaku == null) {
            return;
        }
        danmaku.isGuest = isGuest;
        Glide.with(mContext).load(headImgUrl).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                Drawable drawable = resource;
                if (isGuest) {
                    drawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                    danmaku.textColor = Color.WHITE;
                    danmaku.priority = 0;
                    danmaku.padding = DANMU_PADDING;
                }
                else {
                    drawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                    danmaku.textColor = Color.YELLOW;
                    danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕,但会导致限制行数和禁止堆叠失效
                    danmaku.padding = DANMU_PADDING - (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2;
                }
                SpannableStringBuilder spannable = createSpannable(name, msg, drawable);
                danmaku.text = spannable;

                danmaku.isLive = false;
                danmaku.time = dvSpeechbulDanmaku.getCurrentTime() + 1200;
                danmaku.textSize = SizeUtils.Dp2Px(mContext,14f);
                danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
//                danmaku.underlineColor = Color.GREEN;
                dvSpeechbulDanmaku.addDanmaku(danmaku);
            }
        });
    }

    protected SpannableStringBuilder createSpannable(String name, String msg, Drawable drawable) {
//        Loger.i(TAG, "createSpannable:name=" + name + ",ftype=" + ftype);

        String text = " " + name + " : " + msg + "  ";
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.append(text);
        ImageSpan span = new VerticalImageSpan(drawable);
        spannable.setSpan(span, 0, text.length() , Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannable;
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

    /**
     * 垂直方向居中对齐的ImageSpan
     */
    public class VerticalImageSpan extends ImageSpan {

        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
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
         * @param text the text to be draw
         * @param start the text start position
         * @param end the text end position
         * @param x the edge of the replacement closest to the leading margin
         * @param top the top of the line
         * @param y the baseline
         * @param bottom the bottom of the line
         * @param paint the work paint
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
        }

    }
}

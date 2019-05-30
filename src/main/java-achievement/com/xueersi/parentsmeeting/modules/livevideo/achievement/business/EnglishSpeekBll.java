package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.AssertUtil;
import com.tal.speech.language.LanguageEncodeThread;
import com.tal.speech.language.LanguageListener;
import com.tal.speech.language.TalLanguage;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.page.EnglishSpeekPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2017/10/31.
 * 三分屏直播说英语
 */
public class EnglishSpeekBll extends BaseEnglishStandSpeekBll implements EnglishSpeekAction {
    static int staticInt = 0;
    String TAG = "EnglishSpeekBll" + staticInt++;
    protected Logger logger = LoggerFactory.getLogger(TAG);
    private Activity activity;
    private EnglishSpeekHttp liveBll;
    private LiveAndBackDebug liveAndBackDebug;
    String eventId = LiveVideoConfig.LIVE_ENGLISH_SPEEK;
    private static final String ENGLISH_TIP = LiveVideoConfig.LIVE_ENGLISH_TIP;
    private int MAX_TIPS = 1;
    protected ShareDataManager mShareDataManager;
    LiveGetInfo.TotalOpeningLength totalOpeningLength;
    boolean isDestory = false;
    /** 静态destory */
    static boolean isDestory2 = false;
    boolean isAudioStart = false;
    RelativeLayout bottomContent;
    private ViewGroup myView;
    /**
     * 能量条进度
     */
    private View rl_livevideo_english_speak_content;
    /**
     * 没有权限设置提醒
     */
    private View rl_livevideo_english_speak_error;
    private View rl_livevideo_english_stat;
    private TextView tv_livevideo_english_time;
    private ProgressBar tv_livevideo_english_prog;
    private TextView tv_livevideo_english_time2;
    int praiseWidth;
    File s_language;
    private TalLanguage talLanguage;
    boolean dbStart = false;
    long lastDBTime;
    /** 打点期间时长 */
    int dbDuration = 0;
    int sendDbDuration = 1;
    int lastdbDuration = 1;
    /** 打点开始开口时长 */
    int dbSecond;
    /** 打点开始开口次数 */
    int dbStartEnSegNum;
    /** 上次开口时长 */
    int lastSecond;
    /** 打点开始开口次数 */
    int lastEnSegNum;
    int totalEn_seg_num;
    StringBuilder totalEn_seg_len = new StringBuilder();
    int second15;
    int MAX_SECOND = 15;
    String mode;
    boolean showTip = false;
    int tips;
    AudioRequest.OnAudioRequest onAudioRequest;
    LogToFile mLogtf;
    //用来判断是否是小英
    private LiveGetInfo liveGetInfo;
    boolean isSmallEnglish = false;
    /**
     * 是否显示能量条
     */
    private boolean isStarLottieVisible = false;

    public EnglishSpeekBll(Activity activity, LiveGetInfo liveGetInfo) {
        if (staticInt > 5) {
            staticInt = 0;
        }
        this.activity = activity;
        this.liveGetInfo = liveGetInfo;
        if (liveGetInfo != null) {
            isSmallEnglish = liveGetInfo.getSmallEnglish();
        }
        mLogtf = new LogToFile(activity, TAG);
        if (isDestory2) {
            mLogtf.d("EnglishSpeekBll:isDestory2=true");
        }
        isDestory2 = false;
        isSmallEnglish = liveGetInfo.getSmallEnglish();
    }

    public void setLiveBll(EnglishSpeekHttp liveBll) {
        if (liveBll instanceof LiveAndBackDebug) {
            liveAndBackDebug = (LiveAndBackDebug) liveBll;
        }
        this.liveBll = liveBll;
    }

    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        this.liveAndBackDebug = liveAndBackDebug;
    }

    public void setmShareDataManager(ShareDataManager mShareDataManager) {
        this.mShareDataManager = mShareDataManager;
        tips = mShareDataManager.getInt(ENGLISH_TIP, 0, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (tips < MAX_TIPS) {
            if (LiveTopic.MODE_CLASS.equals(mode)) {
                showTip = true;
                setFirstTip();
            }
        } else {
            showTip = true;
        }
    }

    public void setTotalOpeningLength(LiveGetInfo.TotalOpeningLength totalOpeningLength) {
        this.totalOpeningLength = totalOpeningLength;
        int d = (int) totalOpeningLength.duration;
        second15 = d % 60 % 15;
        setEnglishTime(d / 60, d % 60);
        tv_livevideo_english_prog.setProgress(second15 * 3);
        setTime(MAX_SECOND - second15);
        if (!StringUtils.isEmpty(totalOpeningLength.speakingLen)) {
            totalEn_seg_len.append(totalOpeningLength.speakingLen);
        }
//        lastSecond = (int) totalOpeningLength.duration;
    }

    public boolean initView(RelativeLayout bottomContent, String mode, TalLanguage talLanguage, final AtomicBoolean audioRequest, RelativeLayout mContentView) {
        if (speakerRecognitioner != null) {

        } else {
            loadLibrary();
            if (!loadSuccess) {
                mLogtf.d("initView:loadSuccess=false");
                return false;
            }
            long before = System.currentTimeMillis();
            if (talLanguage == null) {
                saveFile();
                if (!initLanuage()) {
                    return false;
                }
//                talAsrJni.LangIDReset(0);
            }
            if (talLanguage == null) {
                this.talLanguage = new TalLanguage(activity);
            } else {
                this.talLanguage = talLanguage;
            }
            logger.d( "initView:time1=" + (System.currentTimeMillis() - before));
        }
        this.bottomContent = bottomContent;
        myView = (ViewGroup) activity.findViewById(R.id.rl_livevideo_english_content);
        //使用Fragment以后 ，这可能为空
        if (myView == null) {
            myView = mContentView.findViewById(R.id.rl_livevideo_english_content);
        }
        myView.setVisibility(View.VISIBLE);
        final View layout_livevideo_stat_gold;
        if (!isSmallEnglish) {
            layout_livevideo_stat_gold = LayoutInflater.from(activity).inflate(R.layout
                    .layout_livevideo_english_speek, myView, false);
        } else {
            layout_livevideo_stat_gold = LayoutInflater.from(activity).inflate(R.layout
                    .layout_livevideo_small_english_english_speek, myView, false);
        }
        myView.addView(layout_livevideo_stat_gold);
        rl_livevideo_english_speak_content = layout_livevideo_stat_gold.findViewById(R.id
                .rl_livevideo_english_speak_content);
        rl_livevideo_english_speak_error = layout_livevideo_stat_gold.findViewById(R.id
                .rl_livevideo_english_speak_error);
        rl_livevideo_english_stat = layout_livevideo_stat_gold.findViewById(R.id.rl_livevideo_english_stat);
        tv_livevideo_english_time = (TextView) layout_livevideo_stat_gold.findViewById(R.id.tv_livevideo_english_time);
        tv_livevideo_english_prog = (ProgressBar) layout_livevideo_stat_gold.findViewById(R.id
                .tv_livevideo_english_prog);
        tv_livevideo_english_time2 = (TextView) layout_livevideo_stat_gold.findViewById(R.id
                .tv_livevideo_english_time2);
        layout_livevideo_stat_gold.findViewById(R.id.bt_livevideo_english_speak_set).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean have = XesPermission.checkPermission(activity, new LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        if (speakerRecognitioner == null) {
                            if (!initLanuage()) {
                                return;
                            }
//                            talAsrJni.LangIDReset(0);
                        }
                        if (isStarLottieVisible) {
                        rl_livevideo_english_speak_content.setVisibility(View.VISIBLE);
                        rl_livevideo_english_speak_error.setVisibility(View.GONE);
                        }
                        isDestory = false;
                        isDestory2 = false;
                        if (!audioRequest.get()) {
                            start();
                        }
                    }
                }, PermissionConfig.PERMISSION_CODE_AUDIO);
                if (have) {
                    if (speakerRecognitioner == null) {
                        if (!initLanuage()) {
                            return;
                        }
//                        talAsrJni.LangIDReset(0);
                    }
                    if (isStarLottieVisible) {
                    rl_livevideo_english_speak_content.setVisibility(View.VISIBLE);
                    rl_livevideo_english_speak_error.setVisibility(View.GONE);
                    }
                    isDestory = false;
                    isDestory2 = false;
                    if (!audioRequest.get()) {
                        start();
                    }
                }
//                Intent intent = new Intent();
//                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
//                activity.startActivity(intent);
//                activity.startActivityForResult(intent,100);
            }
        });
        layout_livevideo_stat_gold.findViewById(R.id.bt_livevideo_english_speak_close).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.removeView(layout_livevideo_stat_gold);
                isDestory = true;
                isDestory2 = true;
            }
        });
        this.mode = mode;
        //辅导态，去掉能量条，开口时长
        if (LiveTopic.MODE_TRANING.equals(mode)) {
//            tv_livevideo_english_time.setVisibility(isSmallEnglish ? View.GONE : View.GONE);
//            if (isSmallEnglish) {
//            tv_livevideo_english_time.setVisibility(View.GONE);
//            }
            if (isStarLottieVisible) {
            rl_livevideo_english_speak_content.setVisibility(View.GONE);
            }
//            tv_livevideo_english_prog.setVisibility(View.GONE);
//            rl_livevideo_english_stat.setVisibility(View.GONE);

        } else {
            if (isStarLottieVisible) {
            rl_livevideo_english_speak_content.setVisibility(View.VISIBLE);
            }
//            tv_livevideo_english_time.setVisibility(View.VISIBLE);
//            tv_livevideo_english_prog.setVisibility(View.VISIBLE);
//            rl_livevideo_english_stat.setVisibility(View.VISIBLE);
            if (!audioRequest.get()) {
                start();
            }
        }
        //view或者gone之后需要刷新下界面，因为布局中可能存在相对位置
        activity.getWindow().getDecorView().invalidate();
        return true;
    }

    @Override
    public TalLanguage getTalLanguage() {
        return talLanguage;
    }

    private void setFirstTip() {
        final ViewGroup rl_livevideo_info = (ViewGroup) activity.findViewById(R.id.rl_livevideo_info);
        final View english_speek_tip;
//        if (!isSmallEnglish) {
        english_speek_tip = LayoutInflater.from(activity).inflate(R.layout
                .layout_livevideo_english_speek_tip, rl_livevideo_info, false);
//        } else {
//            english_speek_tip = LayoutInflater.from(activity).inflate(R.layout
//                    .layout_livevideo_small_english_english_speek, rl_livevideo_info, false);
//        }
        rl_livevideo_info.addView(english_speek_tip);
        english_speek_tip.findViewById(R.id.bt_livevideo_english_tip_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareDataManager.put(ENGLISH_TIP, tips + 1, ShareDataManager.SHAREDATA_NOT_CLEAR);
                rl_livevideo_info.removeView(english_speek_tip);
            }
        });
        tv_livevideo_english_prog.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                tv_livevideo_english_prog.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) english_speek_tip.getLayoutParams();
                lp.topMargin = (int) (myView.getTop() + tv_livevideo_english_prog.getTop() +
                        tv_livevideo_english_prog.getHeight() + 5 * ScreenUtils.getScreenDensity());
//                english_speek_tip.setLayoutParams(lp);
                LayoutParamsUtil.setViewLayoutParams(english_speek_tip, lp);
                return false;
            }
        });
    }

    private void setTime(int second) {
        SpannableString sp;
        if (!isSmallEnglish) {
            sp = new SpannableString("再说" + second + "秒获得");
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color
                    .COLOR_FFFF00));
            sp.setSpan(foregroundColorSpan, 2, 2 + ("" + second).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            sp = new SpannableString("继续说" + second + "秒可获得");
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color
                    .COLOR_FFFF00));
            sp.setSpan(foregroundColorSpan, 3, 3 + ("" + second).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        tv_livevideo_english_time2.setText(sp);
    }

    private void setEnglishTime(int min, int second) {
        String mingStr;
        if (min < 10) {
            mingStr = "0" + min;
        } else {
            mingStr = "" + min;
        }
        String secStr;
        if (second < 10) {
            secStr = "0" + second;
        } else {
            secStr = "" + second;
        }
        tv_livevideo_english_time.setText(mingStr + ":" + secStr);
    }

    @Override
    public void start() {
        mLogtf.d("start:isDestory=" + isDestory + ",isDestory2=" + isDestory2 + ",mode=" + mode);
        if (isDestory) {
            return;
        }
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            return;
        }
        if (speakerRecognitioner != null) {
            isAudioStart = true;
            speakerRecognitioner.setSpeakerPredict(this);
            speakerRecognitioner.start();
        } else {
            try {
                isAudioStart = true;
                talLanguage.start(new EngLanguageListener());
            } catch (Exception e) {
                isAudioStart = false;
                mLogtf.e("start", e);
                XESToastUtils.showToast(activity, "能量条启动失败，打开录音权限或者关闭其他录音程序");
            }
        }
    }

    @Override
    public void stop(AudioRequest.OnAudioRequest onAudioRequest) {
        mLogtf.d("stop:isAudioStart=" + isAudioStart + ",talLanguage=null?" + (talLanguage == null));
        this.onAudioRequest = onAudioRequest;
        if (!isAudioStart) {
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        }
        if (speakerRecognitioner != null) {
            speakerRecognitioner.setSpeakerPredict(null);
            speakerRecognitioner.stop();
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        } else {
            if (talLanguage != null) {
                talLanguage.stop();
//            talLanguage = null;
            } else {
                if (onAudioRequest != null) {
                    onAudioRequest.requestSuccess();
                }
            }
        }
    }

    @Override
    public void destory() {
        logger.d( "destory:isDestory=" + isDestory + ",isDestory2=" + isDestory2);
        isDestory = true;
        isDestory2 = true;
        stop(null);
    }

    String lastduration;
    ValueAnimator lastValueAnimator;

    private class EngLanguageListener implements LanguageListener {

        @Override
        public void onVolumeUpdate(int volume) {
//                            sb.append("音量:" + volume);
//                            tvInfo.setText(sb);
        }

        //没有权限
        @Override
        public void onError(ResultEntity result) {
            isAudioStart = false;
            mLogtf.d("onError:isDestory=" + isDestory + ",isDestory2=" + isDestory2 + ",result=" + result
                    .getErrorNo());
            isDestory = true;
            isDestory2 = true;
            if (isStarLottieVisible) {
            rl_livevideo_english_speak_content.setVisibility(View.INVISIBLE);
            //这里不能改为GONE，因为rl_livevideo_english_speak_error布局和rl_livevideo_english_speak_content在同一个高度和底部
            rl_livevideo_english_speak_error.setVisibility(View.VISIBLE);
            }

            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
                onAudioRequest = null;
            }
//            talAsrJni.LangIDFree();
        }

        @Override
        public void onProcessData(final String out) {
            mLogtf.debugSave("onProcessData:out=" + out);
            if (totalOpeningLength == null) {
                return;
            }
            tv_livevideo_english_time.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject("{" + out + "}");
                        String time_len = jsonObject.getString("time_len");
                        int en_seg_num = jsonObject.getInt("en_seg_num");
                        totalEn_seg_num += en_seg_num;
//                                logger.d( "onProcessData:out=" + out);
                        String duration = getDuration(time_len);
                        if (duration == null || duration.equals(lastduration)) {
                            return;
                        }
                        String en_seg_len = jsonObject.optString("en_seg_len");
                        lastduration = duration;
//                            logger.d( "onProcessData:en_seg_num=" + en_seg_num + ",duration=" + duration);
                        String[] split = duration.split("\\.");
                        if (split.length == 2) {
                            int totalSecond = Integer.parseInt(split[0]);
                            if (dbStart) {
                                dbDuration = totalSecond - dbSecond;
                                long nowTime = System.currentTimeMillis();
                                if (nowTime - lastDBTime >= 3000) {
                                    sendDbDuration = dbDuration;
                                    liveBll.sendDBStudent(dbDuration);
                                    lastDBTime = nowTime;
                                    logger.d( "onProcessData(sendDBStudent):dbDuration=" + dbDuration);
                                }
                            }
                            if (totalOpeningLength.duration == 0) {
                                setEnglishTime(totalSecond / 60, totalSecond % 60);
                            } else {
                                int d = (int) totalOpeningLength.duration;
                                setEnglishTime((totalSecond + d) / 60, (totalSecond + d) % 60);
                            }
//                                        logger.d( "onProcessData:totalSecond=" + totalSecond);
                            second15 += totalSecond - lastSecond;
                            int oldProgress = tv_livevideo_english_prog.getProgress();
                            if (second15 * 3 != oldProgress) {
                                int second = MAX_SECOND - second15;
                                float startProgress = oldProgress;
                                float newProgress;
                                if (second < 0) {
//                                                newProgress = (second15 - 15) * 3;
//                                                setTime(2 * MAX_SECOND - second15);
                                    newProgress = (second15 % MAX_SECOND) * 3;
                                    setTime(MAX_SECOND - second15 % MAX_SECOND);
//                                                        logger.d( "onProcessData(<0):oldProgress=" + oldProgress
// + ",second15=" + second15);
                                } else {
                                    newProgress = second15 * 3;
                                    if (second15 != 15) {
                                        setTime(MAX_SECOND - second15);
                                    } else {
                                        setTime(MAX_SECOND);
                                    }
                                }
                                logger.d( "onProcessData:second=" + second + ",oldProgress=" + oldProgress
                                        + ",newProgress=" + newProgress);
                                if (newProgress != 45) {
                                    setProg(startProgress, newProgress);
                                } else {
                                    if (lastValueAnimator != null) {
                                        lastValueAnimator.cancel();
                                        lastValueAnimator = null;
                                    }
                                    tv_livevideo_english_prog.setProgress(0);
                                }
                            }
                            if (!"".equals(en_seg_len)) {
                                totalEn_seg_len.append(en_seg_len).append(",");
                            }
                            if (second15 >= 15) {
                                second15 = second15 % MAX_SECOND;
                                double douduration = Double.parseDouble(duration);
                                int location[] = new int[2];
                                tv_livevideo_english_prog.getLocationInWindow(location);
                                String speakingLen = totalEn_seg_len.toString();
                                liveBll.setTotalOpeningLength(1000, "" + (douduration + totalOpeningLength
                                                .duration),
                                        "" + (totalEn_seg_num + totalOpeningLength.speakingNum), speakingLen,
                                        location[0] + tv_livevideo_english_prog.getWidth(), location[1]);
                            }
                            lastSecond = totalSecond;
                            lastEnSegNum = totalEn_seg_num;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private String getDuration(String out) {
            int index = out.indexOf("3:");
            if (index != -1) {
                out = out.substring(index + 2);
                return out;
            }
            return null;
        }

        @Override
        public void onProcessEnd(LanguageEncodeThread languageEncodeThread) {
            mLogtf.d("onProcessEnd:Free:isDestory=" + isDestory + ",isDestory2=" + isDestory2);
            isAudioStart = false;
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
                onAudioRequest = null;
            }
//            if (isDestory && isDestory2) {
//                talAsrJni.LangIDFree();
//            }
        }
    }

    @Override
    public void onPredict(String predict) {
        logger.d( "onPredict:predict=" + predict);
        if (totalOpeningLength == null) {
            return;
        }
        super.onPredict(predict);
        try {
            JSONObject jsonObject = new JSONObject(predict);
            int en_seg_num = 0;
            totalEn_seg_num += en_seg_num;
//                                logger.d( "onProcessData:out=" + out);
            final String duration = jsonObject.getString("time");
            if (duration == null || duration.equals(lastduration)) {
                return;
            }
            final String en_seg_len = jsonObject.optString("duration");
            lastduration = duration;
            double time = Double.parseDouble(duration);
            final int totalSecond = (int) time;
            if (dbStart) {
                dbDuration = totalSecond - dbSecond;
                long nowTime = System.currentTimeMillis();
                if (nowTime - lastDBTime >= 3000) {
                    sendDbDuration = dbDuration;
                    liveBll.sendDBStudent(dbDuration);
                    lastDBTime = nowTime;
                    logger.d( "onProcessData(sendDBStudent):dbDuration=" + dbDuration);
                }
            }
            second15 += totalSecond - lastSecond;
            final int oldProgress = tv_livevideo_english_prog.getProgress();
            tv_livevideo_english_time.post(new Runnable() {
                @Override
                public void run() {
                    if (totalOpeningLength.duration == 0) {
                        setEnglishTime(totalSecond / 60, totalSecond % 60);
                    } else {
                        int d = (int) totalOpeningLength.duration;
                        setEnglishTime((totalSecond + d) / 60, (totalSecond + d) % 60);
                    }
                    if (second15 * 3 != oldProgress) {
                        int second = MAX_SECOND - second15;
                        float startProgress = oldProgress;
                        float newProgress;
                        if (second < 0) {
//                                                newProgress = (second15 - 15) * 3;
//                                                setTime(2 * MAX_SECOND - second15);
                            newProgress = (second15 % MAX_SECOND) * 3;
                            setTime(MAX_SECOND - second15 % MAX_SECOND);
//                                                        logger.d( "onProcessData(<0):oldProgress=" + oldProgress
// + ",second15=" + second15);
                        } else {
                            newProgress = second15 * 3;
                            if (second15 != 15) {
                                setTime(MAX_SECOND - second15);
                            } else {
                                setTime(MAX_SECOND);
                            }
                        }
                        logger.d( "onProcessData:second=" + second + ",oldProgress=" + oldProgress
                                + ",newProgress=" + newProgress);
                        if (newProgress != 45) {
                            setProg(startProgress, newProgress);
                        } else {
                            if (lastValueAnimator != null) {
                                lastValueAnimator.cancel();
                                lastValueAnimator = null;
                            }
                            tv_livevideo_english_prog.setProgress(0);
                        }
                    }
                    if (!"".equals(en_seg_len)) {
                        totalEn_seg_len.append(en_seg_len).append(",");
                    }
                    if (second15 >= 15) {
                        second15 = second15 % MAX_SECOND;
                        double douduration = Double.parseDouble(duration);
                        int location[] = new int[2];
                        tv_livevideo_english_prog.getLocationInWindow(location);
                        String speakingLen = totalEn_seg_len.toString();
                        liveBll.setTotalOpeningLength(1000, "" + (douduration + totalOpeningLength
                                        .duration),
                                "" + (totalEn_seg_num + totalOpeningLength.speakingNum), speakingLen,
                                location[0] + tv_livevideo_english_prog.getWidth(), location[1]);
                    }
                    lastSecond = totalSecond;
                    lastEnSegNum = totalEn_seg_num;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置能量条
     *
     * @param startProgress
     * @param newProgress
     */
    private void setProg(final float startProgress, float newProgress) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(startProgress,
                newProgress);
        final float finalNewProgress = newProgress;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                float oldProgress = startProgress + (finalNewProgress -
                        startProgress) * fraction;
                tv_livevideo_english_prog.setProgress((int) oldProgress);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                                                        logger.i( "onAnimationEnd:equal=" + (lastValueAnimator
// == valueAnimator));
                if (lastValueAnimator == valueAnimator) {
                    lastValueAnimator = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                logger.i( "onAnimationCancel:equal=" + (lastValueAnimator ==
                        valueAnimator));
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(80);
        if (lastValueAnimator != null) {
            lastValueAnimator.cancel();
        }
        valueAnimator.start();
        lastValueAnimator = valueAnimator;
    }

    private void saveFile() {
        File dir = new File(activity.getCacheDir(), "taltest/language");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        s_language = new File(dir, "s_language");
        try {
            //InputStream inputStream = activity.AssertUtil.open("s_language");
            InputStream inputStream = AssertUtil.open("s_language");
            FileOutputStream outputStream = new FileOutputStream(s_language);
            byte b[] = new byte[10240];
            int length = -1;
            while ((length = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            s_language = null;
        }
    }

    private boolean initLanuage() {
        if (s_language == null) {
            return false;
        }
//        talAsrJni.LangIDSetParam(1);
        s_language = new File("/storage/emulated/0/record/s_shurufa_1011");
//        int AssessInitial = talAsrJni.LangIDInitial(s_language.getPath());
//        mLogtf.d("initLanuage:AssessInitial=" + AssessInitial);
//        return AssessInitial == 0;
        return true;
    }

    @Override
    public void onDBStart() {
        logger.d( "onDBStart:dbStart=" + dbStart);
        if (!dbStart) {
            dbStart = true;
            dbSecond = lastSecond;
            dbStartEnSegNum = lastEnSegNum;
            dbDuration = 0;
            sendDbDuration = 0;
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "start");
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
        }
    }

    @Override
    public void onDBStop() {
        logger.d( "onDBStop:dbStart=" + dbStart + ",dbDuration=" + dbDuration + ",sendDbDuration=" + sendDbDuration);
        if (dbStart) {
            dbStart = false;
            LiveMessageBll liveMessageBll = ProxUtil.getProxUtil().get(activity, LiveMessageBll.class);
            if (sendDbDuration == 0) {
                liveBll.setNotOpeningNum();
                if (liveMessageBll != null) {
                    liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                            "大声的说出来，老师很想听到你的声音哦~");
                }
            } else {
                if (lastdbDuration == 0 && liveMessageBll != null) {
                    liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                            "没错，就是这样，继续坚持下去！");
                }
            }
            lastdbDuration = sendDbDuration;
            Map<String, String> mData = new HashMap<>();
            mData.put("duration", "" + dbDuration);
            mData.put("speakNum", "" + (lastEnSegNum - dbStartEnSegNum));
            mData.put("logtype", "stop");
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
        }
    }

    @Override
    public void onModeChange(final String mode, final boolean audioRequest) {
        logger.d( "onModeChange:mode=" + mode + ",audioRequest=" + audioRequest);
        this.mode = mode;
        myView.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_TRANING.equals(mode)) {
                    if (isStarLottieVisible) {
                    rl_livevideo_english_speak_content.setVisibility(View.GONE);
                    }
//                    tv_livevideo_english_prog.setVisibility(View.GONE);
//                    rl_livevideo_english_stat.setVisibility(View.GONE);
                    stop(null);
                } else {
//                    tv_livevideo_english_prog.setVisibility(View.VISIBLE);
//                    rl_livevideo_english_stat.setVisibility(View.VISIBLE);
                    if (isStarLottieVisible) {
                    if (rl_livevideo_english_speak_error.getVisibility() != View.VISIBLE) {
                        rl_livevideo_english_speak_content.setVisibility(View.VISIBLE);

                        }
                    }
                    if (!showTip) {
                        showTip = true;
//                        int tips = mShareDataManager.getInt(ENGLISH_TIP, 0, ShareDataManager.SHAREDATA_NOT_CLEAR);
//                        if (tips < MAX_TIPS) {
//                            setFirstTip();
//                            mShareDataManager.put(ENGLISH_TIP, tips + 1, ShareDataManager.SHAREDATA_NOT_CLEAR);
//                        }
                        setFirstTip();
                    }
                    if (!audioRequest) {
                        start();
                    }
                }
            }
        });
    }

    public void setVideoWidthAndHeight(int width, int height) {
        final View contentView = activity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        if (width > 0) {
            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            praiseWidth = wradio;
        }
    }

    private EnglishSpeekPager englishSpeekPager;

    @Override
    public void praise(int answer) {

        logger.d( "praise:dbDuration=" + sendDbDuration + ",answer=" + answer);
        if (sendDbDuration >= answer) {
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "sendPraise");
            mData.put("answer", "" + answer);
            mData.put("duration", "" + sendDbDuration);
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    final View view;
                    RelativeLayout.LayoutParams lp;
                    if (!isSmallEnglish) {
                        view = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_english_speek_praise,
                                bottomContent, false);
                        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                        ImageView imageView = view.findViewById(R.id.iv_livevideo_english_praise);
                        TextView tv_livevideo_english_praise = (TextView) view.findViewById(R.id
                                .tv_livevideo_english_praise);
//                        ImageView ivSmallEnglish = view.findViewById(R.id.iv_livevideo_small_english_english_speek);
                        RelativeLayout rlRemindOrPraise = view.findViewById(R.id
                                .rl_livevideo_english_speek_remind_or_praise);
                        //小学英语
//                        if (isSmallEnglish) {
//
//                        } else {//其他
//                        ivSmallEnglish.setVisibility(View.GONE);
                        rlRemindOrPraise.setVisibility(View.VISIBLE);
                        lp.rightMargin = praiseWidth;
                        tv_livevideo_english_praise.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.bg_livevideo_english_speek_praise);

                        tv_livevideo_english_praise.setText("老师表扬了你！");
                        bottomContent.addView(view, lp);
                        bottomContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomContent.removeView(view);
                            }
                        }, 1000);
//                        }
                    } else {

                        if (englishSpeekPager == null) {
                            englishSpeekPager = new EnglishSpeekPager(activity);
                        } else {
                            //移出之前的弹窗
                            if (englishSpeekPager.getRootView().getParent() == bottomContent) {
                                bottomContent.removeView(englishSpeekPager.getRootView());
                            }
                        }
                        bottomContent.removeCallbacks(removeViewRunnable);
                        view = englishSpeekPager.getRootView();
                        englishSpeekPager.updateStatus(EnglishSpeekPager.PRAISE);
                        lp = englishSpeekPager.getLayoutParams();
                        bottomContent.addView(view, lp);
                        bottomContent.postDelayed(removeViewRunnable, 1000);
//                        rlRemindOrPraise.setVisibility(View.GONE);
//                        ivSmallEnglish.setVisibility(View.VISIBLE);
//                        tv_livevideo_english_praise.setVisibility(View.GONE);
//                        ivSmallEnglish.setImageResource(R.drawable.bg_small_english_livevideo_english_speek_praise);
//                        ivSmallEnglish.setBackground(activity.getResources().getDrawable(R.color.COLOR_000000));
                        //让弹窗居中显示

//                        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
//                        Drawable drawable = activity.getResources().getDrawable(R.drawable
//                                .bg_small_english_livevideo_english_speek_praise);
//                        int wight = (liveVideoPoint.x3 - liveVideoPoint.x2 - drawable.getIntrinsicWidth()) / 2;
//                        Log.e("EnglishSpeekBll", wight + " " + liveVideoPoint.x3 + " " + liveVideoPoint.x2 + " " +
//                                drawable.getIntrinsicWidth());
//                        lp.leftMargin = wight;
                    }


                }
            });
        }
    }

    Runnable removeViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (englishSpeekPager != null && englishSpeekPager.getRootView().getParent() == bottomContent) {
                bottomContent.removeView(englishSpeekPager.getRootView());
            }
        }
    };

    @Override
    public void remind(int answer) {

        logger.d( "remind:sendDbDuration=" + sendDbDuration + ",answer=" + answer);

        if (sendDbDuration <= answer) {
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "sendRemind");
            mData.put("answer", "" + answer);
            mData.put("duration", "" + sendDbDuration);
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
            bottomContent.post(new Runnable() {

                @Override
                public void run() {
                    final View view;
                    RelativeLayout.LayoutParams lp;
                    //不是小英
                    if (!isSmallEnglish) {
                        view = LayoutInflater.from(activity).inflate(R.layout
                                .layout_livevideo_english_speek_praise, bottomContent, false);
                        ImageView imageView = (ImageView) view.findViewById(R.id.iv_livevideo_english_praise);
                        TextView tv_livevideo_english_praise = (TextView) view.findViewById(R.id
                                .tv_livevideo_english_praise);
//                    ImageView ivSmallEnglish = view.findViewById(R.id.iv_livevideo_small_english_english_speek);
                        RelativeLayout rlRemindOrPraise = view.findViewById(R.id
                                .rl_livevideo_english_speek_remind_or_praise);
                        rlRemindOrPraise.setVisibility(View.VISIBLE);
                        imageView.setBackgroundResource(R.drawable.bg_livevideo_english_speek_remind);
                        tv_livevideo_english_praise.setText("大声说英语啦！");
                        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        lp.rightMargin = praiseWidth;
                        bottomContent.addView(view, lp);
                        bottomContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomContent.removeView(view);
                            }
                        }, 1000);

                    } else {

                        if (englishSpeekPager == null) {
                            englishSpeekPager = new EnglishSpeekPager(activity);
                        } else {
                            //移出之前的弹窗
                            if (englishSpeekPager.getRootView().getParent() == bottomContent) {
                                bottomContent.removeView(englishSpeekPager.getRootView());
                            }
                        }
                        bottomContent.removeCallbacks(removeViewRunnable);
                        view = englishSpeekPager.getRootView();
                        englishSpeekPager.updateStatus(EnglishSpeekPager.REMIND);
                        lp = englishSpeekPager.getLayoutParams();
                        bottomContent.addView(view, lp);
                        bottomContent.postDelayed(removeViewRunnable, 1000);
                    }

                    //小英
//                    if (isSmallEnglish) {
                    //让弹窗全屏居中显示
//                        rlRemindOrPraise.setVisibility(View.GONE);
//                        tv_livevideo_english_praise.setVisibility(View.GONE);
////                        ivSmallEnglish.setBackground(activity.getResources().getDrawable(R.color.COLOR_000000));
//                        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
//                        Drawable drawable = activity.getResources().getDrawable(R.drawable
//                                .bg_small_english_livevideo_english_speek_remind);
//                        int wight = (liveVideoPoint.x3 - liveVideoPoint.x2 - drawable.getIntrinsicWidth()) / 2;
//                        lp.leftMargin = wight;
//                        Log.e("EnglishSpeekBll", wight + " " + liveVideoPoint.x3 + " " + liveVideoPoint.x2 + " " +
//                                drawable.getIntrinsicWidth());
//                    } else {
//                        ivSmallEnglish.setVisibility(View.GONE);

//                    }

                }
            });
        }
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.tal.speech.language.LanguageEncodeThread;
import com.tal.speech.language.LanguageListener;
import com.tal.speech.language.TalLanguage;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageSend;
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
 * 站立直播说英语
 */
public class EnglishStandSpeekBll extends BaseEnglishStandSpeekBll implements EnglishSpeekAction {
    static int staticInt = 0;
    String TAG = "EnglishStandSpeekBll" + staticInt++;
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
    private LottieAnimationView starLottieAnimationView;
    private LottieAnimationView goldLottieAnimationView;
    AtomicBoolean haveGold = new AtomicBoolean(false);
    int praiseWidth;
    File s_language;
    TalLanguage talLanguage;
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
    int tips;
    AudioRequest.OnAudioRequest onAudioRequest;
    LogToFile mLogtf;

    /**
     * 判断是否隐藏星星的lottie动画
     */
    private boolean isStarLottieVisible = true;

    /**
     * 使用星星数量
     */
//    private TextView tvStarCount;
    public EnglishStandSpeekBll(Activity activity) {
        if (staticInt > 5) {
            staticInt = 0;
        }
        this.activity = activity;
        mLogtf = new LogToFile(activity, TAG);
        if (isDestory2) {
            mLogtf.d("EnglishSpeekBll:isDestory2=true");
        }
        isDestory2 = false;
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
//        if (tips < MAX_TIPS) {
//            if (LiveTopic.MODE_CLASS.equals(mode)) {
//                showTip = true;
//                setFirstTip();
//            }
//        } else {
//            showTip = true;
//        }
    }

    public void setTotalOpeningLength(LiveGetInfo.TotalOpeningLength totalOpeningLength) {
        this.totalOpeningLength = totalOpeningLength;
        int d = (int) totalOpeningLength.duration;
        second15 = d % 60 % 15;
        if (!StringUtils.isEmpty(totalOpeningLength.speakingLen)) {
            totalEn_seg_len.append(totalOpeningLength.speakingLen);
        }
//        lastSecond = (int) totalOpeningLength.duration;
    }

    public boolean initView(RelativeLayout bottomContent, String mode, TalLanguage talLanguage, AtomicBoolean
            audioRequest, RelativeLayout mContentView) {
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
            logger.d("initView:time1=" + (System.currentTimeMillis() - before));
            if (talLanguage == null) {
                this.talLanguage = new TalLanguage(activity);
            } else {
                this.talLanguage = talLanguage;
            }
        }
        this.bottomContent = bottomContent;
        myView = (ViewGroup) activity.findViewById(R.id.rl_livevideo_english_content);
        if (myView == null) {
            myView = mContentView.findViewById(R.id.rl_livevideo_english_content);
        }
        myView.setVisibility(View.VISIBLE);
        final View layout_livevideo_stat_gold = LayoutInflater.from(activity).inflate(R.layout
                .layout_livevideo_stand_english_speek, myView, false);
        myView.addView(layout_livevideo_stat_gold);
        goldLottieAnimationView = layout_livevideo_stat_gold.findViewById(R.id.lav_live_stand_english_gold);
//        tv_livevideo_english_prog = (ProgressBar) layout_livevideo_stat_gold.findViewById(R.id
// .tv_livevideo_english_prog);
        starLottieAnimationView = activity.findViewById(R.id.lav_livevideo_chievement);

//        tvStarCount = layout_livevideo_stat_gold.findViewById(R.id.tv_livevideo_star_count);

        layout_livevideo_stat_gold.findViewById(R.id.bt_livevideo_english_speak_set).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                activity.startActivity(intent);
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
        if (LiveTopic.MODE_TRANING.equals(mode)) {

        } else {
//            tv_livevideo_english_prog.setVisibility(View.VISIBLE);
            if (!audioRequest.get()) {
                start();
            }
        }
        initlottieAnim();
        return true;
    }

    private void initlottieAnim() {
        if (isStarLottieVisible) {
            starLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    logger.d("onCompositionLoaded:onAnimationStart");
//                    if (tvStarCount != null) {
//                        String tv = tvStarCount.getText().toString();
//                        tvStarCount.setText();
//                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    logger.d("onCompositionLoaded:onAnimationEnd");
                    starLottieAnimationView.setProgress(0);
                    haveGold.set(false);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            starLottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = animation.getAnimatedFraction();
//                logger.d( "onProcessData:fraction=" + fraction + ",progress=" + starLottieAnimationView.getProgress
// ());
                    if (!haveGold.get() && starLottieAnimationView.getProgress() > 0.32f) {
                        goldLottieAnimationView.playAnimation();
                        haveGold.set(true);
                    }
                }
            });
        } else {

        }
        final String fileName = "live_stand/lottie/live_stand_jindu_gold.json";
        final HashMap<String, String> assetFolders = new HashMap<String, String>();
        assetFolders.put(fileName, "live_stand/lottie/jindu_gold");
        LottieComposition.Factory.fromAssetFileName(activity, fileName, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition composition) {
                logger.d("onCompositionLoaded:composition=" + composition);
                if (composition == null) {
//                    Toast.makeText(activity, "加载失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                goldLottieAnimationView.setImageAssetsFolder(assetFolders.get(fileName));
                goldLottieAnimationView.setComposition(composition);
            }
        });
    }

    @Override
    public TalLanguage getTalLanguage() {
        return talLanguage;
    }

    @Override
    public void start() {
        logger.d("start:isDestory=" + isDestory + ",isDestory2=" + isDestory2 + ",mode=" + mode);
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
        logger.d("destory:isDestory=" + isDestory + ",isDestory2=" + isDestory2);
        isDestory = true;
        isDestory2 = true;
        stop(null);
    }

    @Override
    public void onAddTotalOpeningLength(double speechDuration) {

    }

    String lastduration;
    int oldProgress;

    private class EngLanguageListener implements LanguageListener {

        @Override
        public void onVolumeUpdate(int volume) {
//                            sb.append("音量:" + volume);
//                            tvInfo.setText(sb);
        }

        @Override
        public void onError(ResultEntity result) {
            isAudioStart = false;
            mLogtf.d("onError:isDestory=" + isDestory + ",isDestory2=" + isDestory2 + ",result=" + result);
            isDestory = true;
            isDestory2 = true;
//                    rl_livevideo_english_speak_error.setVisibility(View.VISIBLE);
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
            myView.post(new Runnable() {

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
                                    logger.d("onProcessData(sendDBStudent):dbDuration=" + dbDuration);
                                }
                            }
//                                        logger.d( "onProcessData:totalSecond=" + totalSecond);
                            second15 += totalSecond - lastSecond;
                            if (second15 * 3 != oldProgress) {
                                int second = MAX_SECOND - second15;
                                float newProgress;
                                if (second < 0) {
//                                                newProgress = (second15 - 15) * 3;
//                                                setTime(2 * MAX_SECOND - second15);
                                    newProgress = (second15 % MAX_SECOND) * 3;
//                                                        logger.d( "onProcessData(<0):oldProgress=" + oldProgress +
// ",second15=" + second15);
                                } else {
                                    newProgress = second15 * 3;
                                }
                                oldProgress = (int) newProgress;
                                float progress = newProgress / 45 * 0.32f;
                                logger.d("onProcessData:second=" + second + ",oldProgress=" + oldProgress + "," +
                                        "newProgress=" + newProgress + ",progress=" + progress);
                                if (newProgress < 45) {
                                    if (isStarLottieVisible) {
                                        starLottieAnimationView.cancelAnimation();
                                        starLottieAnimationView.setProgress(progress);
                                    } else {

                                    }
                                } else {
                                    if (isStarLottieVisible) {
                                        starLottieAnimationView.resumeAnimation();
                                    } else {

                                    }
                                }
                            }
                            if (!"".equals(en_seg_len)) {
                                totalEn_seg_len.append(en_seg_len).append(",");
                            }
                            if (second15 >= 15) {
                                second15 = second15 % MAX_SECOND;
                                double douduration = Double.parseDouble(duration);
                                String speakingLen = totalEn_seg_len.toString();
                                liveBll.setTotalOpeningLength(1000, "" + (douduration + totalOpeningLength.duration),
                                        "" + (totalEn_seg_num + totalOpeningLength.speakingNum), speakingLen, 0, 0);
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
        super.onPredict(predict);
        if (totalOpeningLength == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(predict);
            int en_seg_num = 0;
            totalEn_seg_num += en_seg_num;
//          logger.d( "onProcessData:out=" + out);
            final String duration = jsonObject.getString("time");
            if (duration == null || duration.equals(lastduration)) {
                return;
            }
            final String en_seg_len = jsonObject.optString("en_seg_len");
            lastduration = duration;
//          logger.d( "onProcessData:en_seg_num=" + en_seg_num + ",duration=" + duration);
            double time = Double.parseDouble(duration);
            final int totalSecond = (int) time;
            if (dbStart) {
                dbDuration = totalSecond - dbSecond;
                long nowTime = System.currentTimeMillis();
                if (nowTime - lastDBTime >= 3000) {
                    sendDbDuration = dbDuration;
                    liveBll.sendDBStudent(dbDuration);
                    lastDBTime = nowTime;
                    logger.d("onProcessData(sendDBStudent):dbDuration=" + dbDuration);
                }
            }
//          logger.d( "onProcessData:totalSecond=" + totalSecond);
            second15 += totalSecond - lastSecond;
            if (second15 * 3 != oldProgress) {
                int second = MAX_SECOND - second15;
                final float newProgress;
                if (second < 0) {
//                                                newProgress = (second15 - 15) * 3;
//                                                setTime(2 * MAX_SECOND - second15);
                    newProgress = (second15 % MAX_SECOND) * 3;
//                                                        logger.d( "onProcessData(<0):oldProgress=" + oldProgress +
// ",second15=" + second15);
                } else {
                    newProgress = second15 * 3;
                }
                oldProgress = (int) newProgress;
                final float progress = newProgress / 45 * 0.32f;
                logger.d("onProcessData:second=" + second + ",oldProgress=" + oldProgress + ",newProgress=" +
                        newProgress + ",progress=" + progress);
                myView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (newProgress < 45) {
                            if (isStarLottieVisible) {
                                starLottieAnimationView.cancelAnimation();
                                starLottieAnimationView.setProgress(progress);
                            } else {

                            }
                        } else {
                            if (isStarLottieVisible) {
                                starLottieAnimationView.resumeAnimation();
                            } else {

                            }
                        }
                        if (!"".equals(en_seg_len)) {
                            totalEn_seg_len.append(en_seg_len).append(",");
                        }
                        if (second15 >= 15) {
                            second15 = second15 % MAX_SECOND;
                            double douduration = Double.parseDouble(duration);
                            String speakingLen = totalEn_seg_len.toString();
                            liveBll.setTotalOpeningLength(1000, "" + (douduration + totalOpeningLength.duration),
                                    "" + (totalEn_seg_num + totalOpeningLength.speakingNum), speakingLen, 0, 0);
                        }
                        lastSecond = totalSecond;
                        lastEnSegNum = totalEn_seg_num;
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        File dir = new File(activity.getCacheDir(), "taltest/language");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        s_language = new File(dir, "s_language");
        try {
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
////        s_language = new File("/storage/emulated/0/record/s_shurufa_1011");
////        int AssessInitial = talAsrJni.LangIDInitial(s_language.getPath());
////        logger.d( "initLanuage:AssessInitial=" + AssessInitial);
////        return AssessInitial == 0;
        return true;
    }

    @Override
    public void onDBStart() {
        logger.d("onDBStart:dbStart=" + dbStart);
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
        logger.d("onDBStop:dbStart=" + dbStart + ",dbDuration=" + dbDuration + ",sendDbDuration=" + sendDbDuration);
        if (dbStart) {
            dbStart = false;
            LiveMessageSend liveMessageSend = ProxUtil.getProvide(activity, LiveMessageSend.class);
            if (sendDbDuration == 0) {
                liveBll.setNotOpeningNum();
                if (liveMessageSend != null) {
                    liveMessageSend.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                            "大声的说出来，老师很想听到你的声音哦~");
                }

            } else {
                if (lastdbDuration == 0 && liveMessageSend != null) {
                    liveMessageSend.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
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
        logger.d("onModeChange:mode=" + mode + ",audioRequest=" + audioRequest);
        this.mode = mode;
        myView.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_TRANING.equals(mode)) {
                    stop(null);
                } else {
//                    tv_livevideo_english_prog.setVisibility(View.VISIBLE);
//                    if (!showTip) {
//                        showTip = true;
////                        int tips = mShareDataManager.getInt(ENGLISH_TIP, 0, ShareDataManager.SHAREDATA_NOT_CLEAR);
////                        if (tips < MAX_TIPS) {
////                            setFirstTip();
////                            mShareDataManager.put(ENGLISH_TIP, tips + 1, ShareDataManager.SHAREDATA_NOT_CLEAR);
////                        }
//                        setFirstTip();
//                    }
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

    @Override
    public void praise(int answer) {
        logger.d("praise:dbDuration=" + sendDbDuration + ",answer=" + answer);
        if (sendDbDuration >= answer) {
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "sendPraise");
            mData.put("answer", "" + answer);
            mData.put("duration", "" + sendDbDuration);
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    final View view = LayoutInflater.from(activity).inflate(R.layout
                            .layout_livevideo_english_speek_praise, bottomContent, false);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                            .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    lp.rightMargin = praiseWidth;
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_livevideo_english_praise);
                    imageView.setImageResource(R.drawable.bg_livevideo_english_speek_praise);
                    TextView tv_livevideo_english_praise = (TextView) view.findViewById(R.id
                            .tv_livevideo_english_praise);
                    tv_livevideo_english_praise.setText("老师表扬了你！");
                    bottomContent.addView(view, lp);
                    bottomContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomContent.removeView(view);
                        }
                    }, 1000);
                }
            });
        }
    }

    @Override
    public void remind(int answer) {
        logger.d("remind:sendDbDuration=" + sendDbDuration + ",answer=" + answer);
        if (sendDbDuration <= answer) {
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "sendRemind");
            mData.put("answer", "" + answer);
            mData.put("duration", "" + sendDbDuration);
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    final View view = LayoutInflater.from(activity).inflate(R.layout
                            .layout_livevideo_english_speek_praise, bottomContent, false);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                            .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    lp.rightMargin = praiseWidth;
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_livevideo_english_praise);
                    imageView.setImageResource(R.drawable.bg_livevideo_english_speek_remind);
                    TextView tv_livevideo_english_praise = (TextView) view.findViewById(R.id
                            .tv_livevideo_english_praise);
                    tv_livevideo_english_praise.setText("大声说英语啦！");
                    bottomContent.addView(view, lp);
                    bottomContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomContent.removeView(view);
                        }
                    }, 1000);
                }
            });
        }
    }
}

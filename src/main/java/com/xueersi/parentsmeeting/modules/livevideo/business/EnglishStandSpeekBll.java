package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.tal.speech.asr.talAsrJni;
import com.tal.speech.language.LanguageEncodeThread;
import com.tal.speech.language.LanguageListener;
import com.tal.speech.language.TalLanguage;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyqai on 2017/10/31.
 */
public class EnglishStandSpeekBll implements EnglishSpeekAction {
    static int staticInt = 0;
    String TAG = "EnglishSpeekBll" + staticInt++;
    static boolean loadSuccess = false;
    private Activity activity;
    private LiveBll liveBll;
    String eventId = LiveVideoConfig.LIVE_ENGLISH_SPEEK;
    private static final String ENGLISH_TIP = LiveVideoConfig.LIVE_ENGLISH_TIP;
    private int MAX_TIPS = 1;
    protected ShareDataManager mShareDataManager;
    LiveMessageBll liveMessageBll;
    LiveGetInfo.TotalOpeningLength totalOpeningLength;
    boolean isDestory = false;
    /** 静态destory */
    static boolean isDestory2 = false;
    boolean isAudioStart = false;
    RelativeLayout bottomContent;
    private ViewGroup myView;
    private View rl_livevideo_english_speak_error;
    private ProgressBar tv_livevideo_english_prog;
    private LottieAnimationView lottieAnimationView;
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
    boolean showTip = false;
    int tips;
    AudioRequest.OnAudioRequest onAudioRequest;
    LogToFile mLogtf;

    static {
        try {
            Loger.i("EnglishSpeekBll", "loadLibrary");
            System.loadLibrary(SpeechEvaluatorUtils.TAL_ASSESS_LIB);
            Loger.i("EnglishSpeekBll", "loadLibrary ok");
            loadSuccess = true;
        } catch (Throwable e) {
            loadSuccess = false;
            Loger.e("EnglishSpeekBll", "loadLibrary", e);
        }
    }

    public EnglishStandSpeekBll(Activity activity) {
        this.activity = activity;
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        if (isDestory2) {
            mLogtf.d("EnglishSpeekBll:isDestory2=true");
        }
        isDestory2 = false;
    }

    public void setLiveBll(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    public void setLiveMessageBll(LiveMessageBll liveMessageBll) {
        this.liveMessageBll = liveMessageBll;
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
        tv_livevideo_english_prog.setProgress(second15 * 3);
        if (!StringUtils.isEmpty(totalOpeningLength.speakingLen)) {
            totalEn_seg_len.append(totalOpeningLength.speakingLen);
        }
//        lastSecond = (int) totalOpeningLength.duration;
    }

    public boolean initView(RelativeLayout bottomContent, String mode, TalLanguage talLanguage) {
        if (!loadSuccess) {
            return false;
        }
        long before = System.currentTimeMillis();
        if (talLanguage == null) {
            saveFile();
            if (!initLanuage()) {
                return false;
            }
            talAsrJni.LangIDReset(0);
        }
        Loger.d(TAG, "initView:time1=" + (System.currentTimeMillis() - before));
        this.bottomContent = bottomContent;
        myView = (ViewGroup) activity.findViewById(R.id.rl_livevideo_english_content);
        myView.setVisibility(View.VISIBLE);
        final View layout_livevideo_stat_gold = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_stand_english_speek, myView, false);
        myView.addView(layout_livevideo_stat_gold);
        rl_livevideo_english_speak_error = layout_livevideo_stat_gold.findViewById(R.id.rl_livevideo_english_speak_error);
//        tv_livevideo_english_prog = (ProgressBar) layout_livevideo_stat_gold.findViewById(R.id.tv_livevideo_english_prog);
        tv_livevideo_english_prog = (ProgressBar) activity.findViewById(R.id.tv_livevideo_english_prog);
        lottieAnimationView = activity.findViewById(R.id.lav_livevideo_chievement);
        layout_livevideo_stat_gold.findViewById(R.id.bt_livevideo_english_speak_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                activity.startActivity(intent);
//                activity.startActivityForResult(intent,100);
            }
        });
        layout_livevideo_stat_gold.findViewById(R.id.bt_livevideo_english_speak_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.removeView(layout_livevideo_stat_gold);
                isDestory = true;
                isDestory2 = true;
            }
        });
        if (talLanguage == null) {
            this.talLanguage = new TalLanguage(activity);
        } else {
            this.talLanguage = talLanguage;
        }
        this.mode = mode;
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            tv_livevideo_english_prog.setVisibility(View.GONE);
        } else {
            tv_livevideo_english_prog.setVisibility(View.VISIBLE);
            start();
        }
        return true;
    }

    @Override
    public TalLanguage getTalLanguage() {
        return talLanguage;
    }

    private void setFirstTip() {
        final ViewGroup rl_livevideo_info = (ViewGroup) activity.findViewById(R.id.rl_livevideo_info);
        final View english_speek_tip = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_english_speek_tip, rl_livevideo_info, false);
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
                lp.topMargin = (int) (myView.getTop() + tv_livevideo_english_prog.getTop() + tv_livevideo_english_prog.getHeight() + 5 * ScreenUtils.getScreenDensity());
//                english_speek_tip.setLayoutParams(lp);
                LayoutParamsUtil.setViewLayoutParams(english_speek_tip, lp);
                return false;
            }
        });
    }

    @Override
    public void start() {
        Loger.d(TAG, "start:isDestory=" + isDestory + ",isDestory2=" + isDestory2 + ",mode=" + mode);
        if (isDestory) {
            return;
        }
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            return;
        }
        try {
            isAudioStart = true;
            talLanguage.start(new LanguageListener() {
                ValueAnimator lastValueAnimator;

                @Override
                public void onVolumeUpdate(int volume) {
//                            sb.append("音量:" + volume);
//                            tvInfo.setText(sb);
                }

                @Override
                public void onError(ResultEntity result) {
                    Loger.d(TAG, "onError:isDestory=" + isDestory + ",isDestory2=" + isDestory2 + ",result=" + result);
                    isDestory = true;
                    isDestory2 = true;
                    rl_livevideo_english_speak_error.setVisibility(View.VISIBLE);
                    if (onAudioRequest != null) {
                        onAudioRequest.requestSuccess();
                        onAudioRequest = null;
                    }
                    isAudioStart = false;
                    talAsrJni.LangIDFree();
                }

                @Override
                public void onProcessData(final String out) {
                    myView.post(new Runnable() {
                        String lastduration;

                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject("{" + out + "}");
                                String time_len = jsonObject.getString("time_len");
                                int en_seg_num = jsonObject.getInt("en_seg_num");
                                totalEn_seg_num += en_seg_num;
//                                Loger.d(TAG, "onProcessData:out=" + out);
                                String duration = getDuration(time_len);
                                if (duration == null || duration.equals(lastduration)) {
                                    return;
                                }
                                String en_seg_len = jsonObject.optString("en_seg_len");
                                lastduration = duration;
//                            Loger.d(TAG, "onProcessData:en_seg_num=" + en_seg_num + ",duration=" + duration);
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
                                            Loger.d(TAG, "onProcessData(sendDBStudent):dbDuration=" + dbDuration);
                                        }
                                    }
//                                        Loger.d(TAG, "onProcessData:totalSecond=" + totalSecond);
                                    second15 += totalSecond - lastSecond;
                                    int oldProgress = tv_livevideo_english_prog.getProgress();
                                    if (second15 * 3 != oldProgress) {
                                        int second = MAX_SECOND - second15;
                                        final float startProgress = oldProgress;
                                        float newProgress;
                                        if (second < 0) {
//                                                newProgress = (second15 - 15) * 3;
//                                                setTime(2 * MAX_SECOND - second15);
                                            newProgress = (second15 % MAX_SECOND) * 3;
//                                                        Loger.d(TAG, "onProcessData(<0):oldProgress=" + oldProgress + ",second15=" + second15);
                                        } else {
                                            newProgress = second15 * 3;
                                        }
                                        float progress = newProgress / 45 * 0.32f;
                                        Loger.d(TAG, "onProcessData:second=" + second + ",oldProgress=" + oldProgress + ",newProgress=" + newProgress + ",progress=" + progress);
                                        if (newProgress < 45) {
                                            lottieAnimationView.setProgress(progress);
                                            final ValueAnimator valueAnimator = ValueAnimator.ofFloat(startProgress, newProgress);
                                            final float finalNewProgress = newProgress;
                                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animation) {
                                                    float fraction = animation.getAnimatedFraction();
                                                    float oldProgress = startProgress + (finalNewProgress - startProgress) * fraction;
                                                    tv_livevideo_english_prog.setProgress((int) oldProgress);
                                                }
                                            });
                                            valueAnimator.addListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
//                                                        Loger.i(TAG, "onAnimationEnd:equal=" + (lastValueAnimator == valueAnimator));
                                                    if (lastValueAnimator == valueAnimator) {
                                                        lastValueAnimator = null;
                                                    }
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {
                                                    Loger.i(TAG, "onAnimationCancel:equal=" + (lastValueAnimator == valueAnimator));
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
                                        } else {
                                            lottieAnimationView.resumeAnimation();
//                                            lottieAnimationView.setProgress(0);
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
                                        liveBll.setTotalOpeningLength(1000, "" + (douduration + totalOpeningLength.duration),
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
                    if (isDestory && isDestory2) {
                        talAsrJni.LangIDFree();
                    }
                }
            });
        } catch (IOException e) {
            Loger.e(TAG, "start", e);
        }
    }

    @Override
    public void stop(AudioRequest.OnAudioRequest onAudioRequest) {
        Loger.d(TAG, "stop:isAudioStart=" + isAudioStart);
        this.onAudioRequest = onAudioRequest;
        if (!isAudioStart) {
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        }
        if (talLanguage != null) {
            talLanguage.stop();
//            talLanguage = null;
        } else {
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        }
    }

    @Override
    public void destory() {
        Loger.d(TAG, "destory:isDestory=" + isDestory + ",isDestory2=" + isDestory2);
        isDestory = true;
        isDestory2 = true;
        stop(null);
    }

    private void saveFile() {
        File dir = new File(activity.getCacheDir(), "taltest/language");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        s_language = new File(dir, "s_language");
        try {
            InputStream inputStream = activity.getAssets().open("s_language");
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
        talAsrJni.LangIDSetParam(1);
        int AssessInitial = talAsrJni.LangIDInitial(s_language.getPath());
        Loger.d(TAG, "initLanuage:AssessInitial=" + AssessInitial);
        return AssessInitial == 0;
    }

    @Override
    public void onDBStart() {
        Loger.d(TAG, "onDBStart:dbStart=" + dbStart);
        if (!dbStart) {
            dbStart = true;
            dbSecond = lastSecond;
            dbStartEnSegNum = lastEnSegNum;
            dbDuration = 0;
            sendDbDuration = 0;
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "start");
            liveBll.umsAgentDebug(eventId, mData);
        }
    }

    @Override
    public void onDBStop() {
        Loger.d(TAG, "onDBStop:dbStart=" + dbStart + ",dbDuration=" + dbDuration + ",sendDbDuration=" + sendDbDuration);
        if (dbStart) {
            dbStart = false;
            if (sendDbDuration == 0) {
                liveBll.setNotOpeningNum();
                liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "大声的说出来，老师很想听到你的声音哦~");
            } else {
                if (lastdbDuration == 0) {
                    liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "没错，就是这样，继续坚持下去！");
                }
            }
            lastdbDuration = sendDbDuration;
            Map<String, String> mData = new HashMap<>();
            mData.put("duration", "" + dbDuration);
            mData.put("speakNum", "" + (lastEnSegNum - dbStartEnSegNum));
            mData.put("logtype", "stop");
            liveBll.umsAgentDebug(eventId, mData);
        }
    }

    @Override
    public void onModeChange(final String mode, final boolean audioRequest) {
        Loger.d(TAG, "onModeChange:mode=" + mode + ",audioRequest=" + audioRequest);
        this.mode = mode;
        myView.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_TRANING.equals(mode)) {
                    tv_livevideo_english_prog.setVisibility(View.GONE);
                    stop(null);
                } else {
                    tv_livevideo_english_prog.setVisibility(View.VISIBLE);
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
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            praiseWidth = wradio;
        }
    }

    public void praise(int answer) {
        Loger.d(TAG, "praise:dbDuration=" + sendDbDuration + ",answer=" + answer);
        if (sendDbDuration >= answer) {
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "sendPraise");
            mData.put("answer", "" + answer);
            mData.put("duration", "" + sendDbDuration);
            liveBll.umsAgentDebug(eventId, mData);
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    final View view = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_english_speek_praise, bottomContent, false);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    lp.rightMargin = praiseWidth;
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_livevideo_english_praise);
                    imageView.setImageResource(R.drawable.bg_livevideo_english_speek_praise);
                    TextView tv_livevideo_english_praise = (TextView) view.findViewById(R.id.tv_livevideo_english_praise);
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

    public void remind(int answer) {
        Loger.d(TAG, "remind:sendDbDuration=" + sendDbDuration + ",answer=" + answer);
        if (sendDbDuration <= answer) {
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "sendRemind");
            mData.put("answer", "" + answer);
            mData.put("duration", "" + sendDbDuration);
            liveBll.umsAgentDebug(eventId, mData);
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    final View view = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_english_speek_praise, bottomContent, false);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    lp.rightMargin = praiseWidth;
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_livevideo_english_praise);
                    imageView.setImageResource(R.drawable.bg_livevideo_english_speek_remind);
                    TextView tv_livevideo_english_praise = (TextView) view.findViewById(R.id.tv_livevideo_english_praise);
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
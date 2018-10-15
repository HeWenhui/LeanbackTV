package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.tal.speech.speechrecognizer.TalSpeech;
import com.umeng.analytics.MobclickAgent;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.OnSpeechEval;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StartProgress;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2017/2/24.
 * 语音评测-二期
 */
public class SpeechAssAutoPager extends BaseSpeechAssessmentPager {
    public static boolean DEBUG = false;
    String eventId = LiveVideoConfig.LIVE_SPEECH_TEST2;
    /** 语音保存位置 */
    private String id;
    /** 时间倒计时，表情 */
    ImageView ivSpeectevalTimeEmoji;
    /** 时间倒计时 */
    TextView tvSpeectevalTime;
    /** 时间倒计时，进度条 */
    ProgressBar progressBar;
    /** 倒数1.5秒，三个点的外层 */
    LinearLayout llSpeectevalPoints;
    /** 评测内容 */
    TextView tvSpeectevalContent;
    /** great鼓励外层 */
    RelativeLayout rlSpeectevalEncourage;
    /** great文字 */
    TextView tvSpeectevalEncourage;
    /** 结果页星星进度条和一些动画 */
    StartProgress spStarResult;
    /** 提示和波浪线的外层 */
    RelativeLayout rlSpeectevalBg;
    /** 波浪线 */
    VolumeWaveView vwvSpeectevalWave;
    /** 提示外层 */
    RelativeLayout rlSpeectevalError;
    /** 提示图标 */
    ImageView ivSpeectevalError;
    /** 提示文字 */
    TextView tvSpeectevalError;
    TextView tv_livevideo_speecteval_countdown;
    ArrayList<TextView> tvCountDown = new ArrayList<>();
    /** great动画 */
    Animation animSpeechEncourage;
    Animation animTimeCountDown;
    int timeCount = 1;
    /** 评测成功 */
    private boolean speechSuccess = false;
    /** 语音保存位置 */
    private File saveVideoFile;
    private SpeechEvalAction speechEvalAction;
    private SpeechEvaluatorInter speechEvaluatorInter;
    /** 在线语音失败次数 */
    int onLineError = 0;
    private long entranceTime;
    /** 是不是已经开始 */
    private boolean isSpeechStart = false;
    /** 是不是考试结束 */
    private boolean isEnd = false;
    /** 是不是评测失败 */
    private boolean isSpeechError = false;
    /** 是不是评测得到结果 */
    private boolean isSpeechSuccess = false;
    /** 是不是直播 */
    private boolean isLive;
    /** 评测内容 */
    private String content;
    /** 评测内容-换行后 */
    private String content2;
    private String nonce;
    /** 评测进度条颜色 */
    int startProgColor;
    /** 评测进度条变色 */
    int progColor;
    /** 评测进度条预估时间 */
    int time;
    /** 回放，强行提交 */
    int examSubmit;
    /** great的分数 */
    int encourageScore = 80;
    /** great 超过6个的 */
    ArrayList<String> point90WordArrayList = new ArrayList<String>();
    /** great 超过3个的 */
    ArrayList<String> point30WordArrayList = new ArrayList<String>();
    /** 用户按了返回 */
    boolean userBack = false;
    /** 已经作答 */
    boolean haveAnswer;
    String learning_stage;

    public SpeechAssAutoPager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity, String liveid, String testId,
                              String nonce, String content, int time, boolean haveAnswer, String learning_stage, SpeechEvalAction speechEvalAction, LivePagerBack livePagerBack) {
        super(context);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
        this.isLive = true;
        this.id = testId;
        this.nonce = nonce;
        this.speechEvalAction = speechEvalAction;
        mLogtf.i("SpeechAssessmentPager:id=" + id);
        startProgColor = context.getResources().getColor(R.color.COLOR_6462A2);
        progColor = 0;
        this.haveAnswer = haveAnswer;
        this.learning_stage = learning_stage;
//        content = "You are very good,You are very clever,welcome to my home";
//        content = "welcome to my home";
//        this.content = "C" + content.substring(1);
        this.content = content;
        this.time = time;
        entranceTime = System.currentTimeMillis();
        this.livePagerBack = livePagerBack;
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "receiveVoiceTest");
        mData.put("live", "" + isLive);
        mData.put("testtype", "4");
        mData.put("testid", id);
        mData.put("answer", content);
        mData.put("answertime", "" + time);
        umsAgentDebugPv(eventId, mData);
    }

    public SpeechAssAutoPager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity, String liveid, String testId,
                              String nonce, String content, int time, int examSubmit, String learning_stage, SpeechEvalAction speechEvalAction, LivePagerBack livePagerBack) {
        super(context);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
        this.isLive = false;
        this.id = testId;
        this.nonce = nonce;
        this.speechEvalAction = speechEvalAction;
        mLogtf.i("SpeechAssessmentPager:id=" + id);
        startProgColor = context.getResources().getColor(R.color.COLOR_6462A2);
        progColor = 0;
//        content = "You are very good,You are very good";
//        this.content = "C" + content.substring(1);
        this.content = content;
        this.time = time;
        this.examSubmit = examSubmit;
        this.learning_stage = learning_stage;
        entranceTime = System.currentTimeMillis();
        setLivePagerBack(livePagerBack);
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "receiveVoiceTest");
        mData.put("live", "" + isLive);
        mData.put("testtype", "4");
        mData.put("testid", id);
        mData.put("answer", content);
        mData.put("answertime", "" + time);
        umsAgentDebugPv(eventId, mData);
    }

    public String getId() {
        return id;
    }

    @Override
    public void jsExamSubmit() {
        userBack = true;
        if (mIse != null) {
            mIse.stop();
        }
    }

    @Override
    public void stopPlayer() {

    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livebackvideo_speecheval_auto_question, null);
        ivSpeectevalTimeEmoji = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_time_emoji);
        tvSpeectevalTime = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_time);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_livevideo_speecteval_time_prog);
        llSpeectevalPoints = (LinearLayout) view.findViewById(R.id.ll_livevideo_speecteval_points);
        tvSpeectevalContent = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_content);
        rlSpeectevalEncourage = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_encourage);
        tvSpeectevalEncourage = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_encourage);
        rlSpeectevalBg = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_bg);
        vwvSpeectevalWave = (VolumeWaveView) view.findViewById(R.id.vwv_livevideo_speecteval_wave);
        vwvSpeectevalWave.setBackColor(0xffeaebf9);
        spStarResult = (StartProgress) view.findViewById(R.id.sp_live_star_result);
        rlSpeectevalError = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_error);
        ivSpeectevalError = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_error);
        tvSpeectevalError = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_error);
        tv_livevideo_speecteval_countdown = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_countdown);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_live_star_result_bg);
        if (bitmap != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) spStarResult.getLayoutParams();
            lp.width = bitmap.getWidth();
            lp.height = bitmap.getHeight();
            spStarResult.setLayoutParams(lp);
            bitmap.recycle();
        }
        return view;
    }

    @Override
    public void initData() {
        TextView tv_livevideo_speecteval_countdown2 = (TextView) mView.findViewById(R.id.tv_livevideo_speecteval_countdown2);
        TextView tv_livevideo_speecteval_countdown3 = (TextView) mView.findViewById(R.id.tv_livevideo_speecteval_countdown3);
        TextView tv_livevideo_speecteval_countdown4 = (TextView) mView.findViewById(R.id.tv_livevideo_speecteval_countdown4);
        tvCountDown.add(tv_livevideo_speecteval_countdown);
        tvCountDown.add(tv_livevideo_speecteval_countdown2);
        tvCountDown.add(tv_livevideo_speecteval_countdown3);
        tvCountDown.add(tv_livevideo_speecteval_countdown4);
        animTimeCountDown = AnimationUtils.loadAnimation(mContext, R.anim.anim_live_speech_countdown);

        animSpeechEncourage = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_speech_encourage);
        animSpeechEncourage.setInterpolator(new OvershootInterpolator());
//        tvSpeectevalContent.setText(Html.fromHtml(content));
        tvSpeectevalContent.setText(content);
        content2 = content.replace("\n", " ");
        String[] split = content.split(" ");
        if (split.length == 1) {
            spStarResult.setIsWord();
        }
        Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
        tvSpeectevalEncourage.setTypeface(fontFace);
        File dir = LiveCacheFile.geCacheFile(mContext, "liveSpeech");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        int second = time;
        tvSpeectevalTime.setText("预计时间" + second + "秒哦~");
        progressBar.setMax(second);
        progressBar.setProgress(second);
        LayerDrawable drawable = (LayerDrawable) progressBar.getProgressDrawable();
        ScaleDrawable scaleDrawable = (ScaleDrawable) drawable.getDrawable(1);
        GradientDrawable gradientDrawable = (GradientDrawable) scaleDrawable.getDrawable();
        gradientDrawable.setColor(startProgColor);
//        prepareSpeech();
//        mView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                vwvSpeectevalWave.start();
//            }
//        }, 100);
//        for (int i = 0; i < tvCountDown.size(); i++) {
//            final Animation animTimeCountDown = AnimationUtils.loadAnimation(mContext, R.anim.anim_live_speech_countdown);
//            final TextView textView = tvCountDown.get(i);
//            mView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    CountDownListener countDownListener = new CountDownListener(textView);
//                    animTimeCountDown.setAnimationListener(countDownListener);
//                    textView.startAnimation(animTimeCountDown);
//                }
//            }, i * 810);
//        }

        final TextView textView = tvCountDown.remove(0);
        CountDownListener countDownListener = new CountDownListener(textView);
        animTimeCountDown.setAnimationListener(countDownListener);
        textView.startAnimation(animTimeCountDown);
//        mView.post(new Runnable() {
//            @Override
//            public void run() {
//                vwvSpeectevalWave.start();
//            }
//        });
//        mView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setAudioRequest();
//            }
//        }, 2300);
    }

    class CountDownListener implements Animation.AnimationListener {
        TextView textView;

        public CountDownListener(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onAnimationStart(Animation animation) {
//            textView.setX(0.8f);
//            textView.setY(0.8f);
            logger.d( "onAnimationStart:" + textView.getText());
            textView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            textView.setVisibility(View.GONE);
            if (!tvCountDown.isEmpty()) {
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView = tvCountDown.remove(0);
                        textView.startAnimation(animTimeCountDown);
                    }
                });
            } else {
                RelativeLayout rl_livevideo_speecteval_countdown = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_speecteval_countdown);
                rl_livevideo_speecteval_countdown.removeAllViews();
                ViewGroup group = (ViewGroup) rl_livevideo_speecteval_countdown.getParent();
                group.removeView(rl_livevideo_speecteval_countdown);
                logger.d( "onAnimationEnd:isEnd=" + isEnd);
                if (!isEnd) {
                    isSpeechStart = true;
                    setAudioRequest();
                    vwvSpeectevalWave.start();
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public void setAudioRequest() {
        logger.d( "setAudioRequest:userBack=" + userBack + ",isEnd=" + isEnd);
        if (userBack) {
            return;
        }
        //语音评测开始
        if (mIse == null) {
            mIse = new SpeechEvaluatorUtils(true);
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "startRecord");
        mData.put("testid", id);
        mData.put("islive", "" + isLive);
        umsAgentDebugInter(eventId, mData);
        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, new EvaluatorListener() {
            int lastVolume = 0;

            @Override
            public void onBeginOfSpeech() {
                logger.d( "onBeginOfSpeech");
                isSpeechError = false;
            }

            @Override
            public void onResult(ResultEntity resultEntity) {
                if (userBack) {
                    return;
                }
                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    Map<String, String> mData = new HashMap<>();
                    mData.put("logtype", "voiceTestClose");
                    mData.put("islive", "" + isLive);
                    mData.put("testid", "" + id);
                    umsAgentDebugInter(eventId, mData);
                    onEvaluatorSuccess(resultEntity, this);

//                    resultEntity.setStatus(ResultEntity.ERROR);
////                    resultEntity.setErrorNo(ResultCode.MUTE_AUDIO);
//                    resultEntity.setErrorNo(ResultCode.WEBSOCKET_TIME_OUT);
//                    isSpeechError = true;
//                    onEvaluatorError(resultEntity, this);
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    isSpeechError = true;
                    onEvaluatorError(resultEntity, this);
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    onEvaluatorIng(resultEntity);
                }
            }

            @Override
            public void onVolumeUpdate(final int volume) {
//                logger.d( "onVolumeUpdate:volume=" + volume);
                vwvSpeectevalWave.setVolume(volume * 3);
                lastVolume = volume;
            }
        });
        if (!isLive) {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    examSubmitAll();
                }
            }, examSubmit * 1000);
        }
        Random random = new Random();
        if (haveAnswer) {
            ivSpeectevalError.setImageResource(R.drawable.bg_livevideo_speecteval_error);
            errorSetVisible();
            tvSpeectevalError.setText("题目已作答");
        } else {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechEvalAction.speechIsAnswered(id, new SpeechEvalAction.SpeechIsAnswered() {
                        @Override
                        public void isAnswer(boolean answer) {
                            SpeechAssAutoPager.this.haveAnswer = answer;
                            if (answer) {
                                ivSpeectevalError.setImageResource(R.drawable.bg_livevideo_speecteval_error);
                                errorSetVisible();
                                tvSpeectevalError.setText("题目已作答");
                            }
                        }
                    });
                }
            }, random.nextInt(2000));
        }
    }

    private void prepareSpeech() {
        //倒计时文字
        final int second = time;
        final AtomicInteger atomicInteger = new AtomicInteger();
        for (int i = 0; i < llSpeectevalPoints.getChildCount(); i++) {
            View child = llSpeectevalPoints.getChildAt(i);
            child.setBackgroundResource(R.drawable.shape_livevideo_speech_point_bg);
        }
        llSpeectevalPoints.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (atomicInteger.get() == 3) {
                    llSpeectevalPoints.setVisibility(View.INVISIBLE);
//                    llSpeectevalPoints.removeAllViews();
                    startSpeech(second);
                } else {
                    View child = llSpeectevalPoints.getChildAt(2 - atomicInteger.get());
//                    child.setBackgroundResource(R.drawable.shape_livevideo_speech_point_empty_bg);
                    child.setVisibility(View.INVISIBLE);
                    atomicInteger.set(atomicInteger.get() + 1);
                    llSpeectevalPoints.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    ProgressBarRun progressBarRun = new ProgressBarRun();

    private class ProgressBarRun implements Runnable {
        LayerDrawable drawable;
        private int second;

        @Override
        public void run() {
            ScaleDrawable scaleDrawable = (ScaleDrawable) drawable.getDrawable(1);
            GradientDrawable gradientDrawable = (GradientDrawable) scaleDrawable.getDrawable();
            progressBar.setProgress(progressBar.getProgress() - 1);
            if (progressBar.getProgress() > 0) {
                if (second > 5) {
                    if (progressBar.getProgress() >= second / 2) {
                        if (progColor != startProgColor) {
                            progColor = startProgColor;
                            gradientDrawable.setColor(progColor);
                        }
                        tvSpeectevalTime.setText("预计时间" + progressBar.getProgress() + "秒哦~");
                    } else if (progressBar.getProgress() >= second * 0.2) {
                        if (progColor != Color.GREEN) {
                            progColor = Color.GREEN;
                            gradientDrawable.setColor(progColor);
                            tvSpeectevalTime.setText("加油~加油~");
                            ivSpeectevalTimeEmoji.setImageResource(R.drawable.bg_livevideo_speecteval_time_emoji3);
                        }
                    } else {
                        if (progColor != Color.RED) {
                            progColor = Color.RED;
                            gradientDrawable.setColor(progColor);
                            ivSpeectevalTimeEmoji.setImageResource(R.drawable.bg_livevideo_speecteval_time_emoji2);
                            tvSpeectevalTime.setTextColor(mContext.getResources().getColor(R.color.COLOR_E74C3C));
                            tvSpeectevalTime.setText("要快点读啦~");
                        }
                    }
                } else {
                    if (progColor != startProgColor) {
                        progColor = startProgColor;
                        gradientDrawable.setColor(progColor);
                    }
                    tvSpeectevalTime.setText("预计时间" + progressBar.getProgress() + "秒哦~");
                }
                if (getRootView().getParent() != null) {
                    progressBar.postDelayed(this, 1000);
                }
            } else {
                if (speechSuccess) {
                    ivSpeectevalTimeEmoji.setImageResource(R.drawable.bg_livevideo_speecteval_time_emoji4);
                    tvSpeectevalTime.setTextColor(mContext.getResources().getColor(R.color.COLOR_6462A2));
                    tvSpeectevalTime.setText("完成啦！");
                } else {
                    ivSpeectevalTimeEmoji.setImageResource(R.drawable.bg_livevideo_speecteval_time_emoji2);
                    tvSpeectevalTime.setTextColor(mContext.getResources().getColor(R.color.COLOR_E74C3C));
                    tvSpeectevalTime.setText("要快点读啦~");
                }
                progressBar.setVisibility(View.GONE);
//                mIse.stop();
            }
        }
    }


    private void startSpeech(final int second) {
        if (getRootView().getParent() != null) {
//            LayerDrawable drawable = (LayerDrawable) progressBar.getProgressDrawable();
//            progressBarRun.drawable = drawable;
//            progressBarRun.second = second;
//            progressBar.postDelayed(progressBarRun, 1000);
        }
    }

    private void errorSetVisible() {
        rlSpeectevalError.setVisibility(View.VISIBLE);
    }

    private void errorSetGone() {
        rlSpeectevalError.setVisibility(View.GONE);
    }

    private void onEvaluatorSuccess(final ResultEntity resultEntity, final EvaluatorListener evaluatorListener) {
        final int score = resultEntity.getScore();
        if (!isEnd) {
            if (score == 1) {
                errorSetVisible();
                tvSpeectevalError.setText("要认真些，再来一次哦！");
                spStarResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorSetGone();
                        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
                    }
                }, 500);
                return;
            } else if (score < 60) {
                errorSetVisible();
                tvSpeectevalError.setText("你可以说的更好，再来一次哦！");
                spStarResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorSetGone();
                        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
                    }
                }, 500);
                return;
            }
        }
        tvSpeectevalError.removeCallbacks(autoUploadRunnable);
        ivSpeectevalError.setImageResource(R.drawable.bg_livevideo_speecteval_upload);
        errorSetVisible();
        tvSpeectevalError.setTextColor(mContext.getResources().getColor(R.color.COLOR_6462A2));
        tvSpeectevalError.setText("录音上传中");
        speechSuccess = true;
        List<PhoneScore> lstPhonemeScore = resultEntity.getLstPhonemeScore();
        String nbest = "";
        for (int i = 0; i < lstPhonemeScore.size(); i++) {
            PhoneScore phoneScore = lstPhonemeScore.get(i);
            nbest += phoneScore.getWord() + ":" + phoneScore.getScore();
            if (i != lstPhonemeScore.size() - 1) {
                nbest += ",";
            }
        }
        try {
            int wordChangeColor = wordChangeColor(score, lstPhonemeScore);
            if (wordChangeColor != 0) {
                if (speechEvaluatorInter == null) {
                    mLogtf.d("onEvaluatorSuccess:Inter=null,sid=" + resultEntity.getSid() + ",score=" + score + ",error=" + content + "-" + nbest);
                } else {
                    mLogtf.d("onEvaluatorSuccess:Inter=" + speechEvaluatorInter.getClass().getSimpleName() + ",sid=" + resultEntity.getSid() + ",score=" + score + ",error=" + content + "-" + nbest);
                }
            }
        } catch (Exception e) {
            MobclickAgent.reportError(mContext, new Error(content + "-" + nbest, e));
        }
        mLogtf.d("onEvaluatorSuccess:content=" + content + ",sid=" + resultEntity.getSid() + ",score=" + score + ",haveAnswer=" + haveAnswer + ",nbest=" + nbest);
        if (haveAnswer) {
            onSpeechEvalSuccess(resultEntity, 0);
        } else {
            try {
                final JSONObject answers = new JSONObject();
                JSONObject answers1 = new JSONObject();
                entranceTime = System.currentTimeMillis() - entranceTime;
                answers1.put("entranceTime", (int) (entranceTime / 1000));
                answers1.put("score", score);
                JSONObject detail = new JSONObject();
                detail.put("cont_score", score);
                detail.put("level", resultEntity.getLevel());
                JSONArray nbestArray = new JSONArray();
                nbestArray.put(nbest);
                detail.put("nbest", nbestArray);
                detail.put("pron_score", score);
                detail.put("total_score", score);
                answers1.put("detail", detail);
                answers.put("1", answers1);
                speechEvalAction.sendSpeechEvalResult2(id, answers.toString(), new OnSpeechEval() {
                    OnSpeechEval onSpeechEval = this;

                    @Override
                    public void onSpeechEval(Object object) {
                        JSONObject jsonObject = (JSONObject) object;
                        int gold = jsonObject.optInt("gold");
                        haveAnswer = jsonObject.optInt("isAnswered", 0) == 1;
                        onSpeechEvalSuccess(resultEntity, gold);
                        speechEvalAction.onSpeechSuccess(id);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        spStarResult.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                speechEvalAction.sendSpeechEvalResult2(id, answers.toString(), onSpeechEval);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        rlSpeectevalEncourage.setVisibility(View.INVISIBLE);
        vwvSpeectevalWave.stop();
    }

    private void onSpeechEvalSuccess(ResultEntity resultEntity, int gold) {
        isSpeechSuccess = true;
        rlSpeectevalBg.setVisibility(View.GONE);
        rlSpeectevalBg.removeAllViews();
        ivSpeectevalTimeEmoji.setImageResource(R.drawable.bg_livevideo_speecteval_time_emoji4);
        tvSpeectevalTime.setTextColor(mContext.getResources().getColor(R.color.COLOR_6462A2));
        tvSpeectevalTime.setText("完成啦!");
//        progressBar.removeCallbacks(progressBarRun);
        progressBar.setVisibility(View.GONE);
        final View v_live_star_result_out = mView.findViewById(R.id.v_live_star_result_out);
        v_live_star_result_out.setVisibility(View.VISIBLE);
        v_live_star_result_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup group = (ViewGroup) spStarResult.getParent();
                if (group != null) {
                    group.removeView(spStarResult);
                    if (isEnd) {
                        speechEvalAction.stopSpeech(SpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                    }
                }
                group = (ViewGroup) v_live_star_result_out.getParent();
                if (group != null) {
                    group.removeView(v_live_star_result_out);
                }
            }
        });
        int score = resultEntity.getScore();
        spStarResult.setVisibility(View.VISIBLE);
        spStarResult.setBackgroundResource(R.drawable.bg_live_star_result_bg);
        if (haveAnswer) {
            spStarResult.setAnswered();
        }
//        spStarResult.setAnswered();
        spStarResult.setSorce(score);
        spStarResult.setStarCount(gold);
        spStarResult.setFluent(resultEntity.getContScore());
        spStarResult.setAccuracy(resultEntity.getPronScore());
        spStarResult.setClickable(true);
        int progress;
        if (score < 40) {
            progress = 1;
        } else if (score < 60) {
            progress = 2;
        } else if (score < 75) {
            progress = 3;
        } else if (score < 90) {
            progress = 4;
        } else {
            progress = 5;
        }
        spStarResult.setProgress(progress);
        spStarResult.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup group = (ViewGroup) spStarResult.getParent();
                if (group != null) {
                    group.removeView(spStarResult);
                    if (isEnd) {
                        speechEvalAction.stopSpeech(SpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                    }
                }
                group = (ViewGroup) v_live_star_result_out.getParent();
                if (group != null) {
                    group.removeView(v_live_star_result_out);
                }
            }
        }, 3000);
        speechEvalAction.onSpeechSuccess(id);
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "voiceTestResult");
        mData.put("islive", "" + isLive);
        mData.put("testid", id);
        mData.put("goldnum", "" + gold);
        mData.put("starnum", "" + progress);
        mData.put("totalscore", "" + score);
        mData.put("speaktime", "" + resultEntity.getSpeechDuration());
        if (haveAnswer) {
            mData.put("state", "noSubmit");
        } else {
            mData.put("state", isEnd ? "endPublish" : "autoSubmit");
        }
        umsAgentDebugPv(eventId, mData);
    }

    private void onEvaluatorError(final ResultEntity resultEntity, final EvaluatorListener evaluatorListener) {
        mLogtf.d("onResult:ERROR:ErrorNo=" + resultEntity.getErrorNo() + ",isEnd=" + isEnd + ",isOfflineFail=" + SpeechEvaluatorUtils.isOfflineFail());
        tvSpeectevalError.removeCallbacks(autoUploadRunnable);
        ivSpeectevalError.setImageResource(R.drawable.bg_livevideo_speecteval_error);
        errorSetVisible();
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode
                .MUTE) {
//                            XESToastUtils.showToast(mContext, "声音有点小，大点声哦！");
            tvSpeectevalError.setText("声音有点小，再来一次哦！");
            if (!isEnd) {
                spStarResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorSetGone();
                        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
                    }
                }, 1000);
                return;
            }
        } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
            tvSpeectevalError.setText("麦克风不可用，快去检查一下");
        } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() == ResultCode.NETWORK_FAIL
                || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
            tvSpeectevalError.setText("好像没网了，快检查一下");
            if (!SpeechEvaluatorUtils.isOfflineFail()) {
                if (speechEvaluatorInter instanceof TalSpeech) {
                    onLineError++;
                    if (onLineError == 1) {
                        if (!isEnd) {
                            spStarResult.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorSetGone();
                                    speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
                                }
                            }, 1000);
                            return;
                        }
                    }
                }
            }
        } else {
            tvSpeectevalError.setText("测评君罢工了，程序员哥哥会尽快修复（" + resultEntity.getErrorNo() + "）");
            if (!SpeechEvaluatorUtils.isOfflineFail()) {
                if (speechEvaluatorInter instanceof TalSpeech) {
                    onLineError++;
                    if (onLineError == 1) {
                        if (!isEnd) {
                            spStarResult.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorSetGone();
                                    speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
                                }
                            }, 1000);
                            return;
                        }
                    }
                }
            }
        }
//        progressBar.removeCallbacks(progressBarRun);
//        progressBar.setVisibility(View.GONE);
//        vwvSpeectevalWave.setVisibility(View.INVISIBLE);
        vwvSpeectevalWave.stop();
        if (isEnd) {
            spStarResult.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechEvalAction.stopSpeech(SpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                }
            }, 1000);
        }
    }

    private void onEvaluatorIng(ResultEntity resultEntity) {
        List<PhoneScore> lstPhonemeScore = resultEntity.getLstPhonemeScore();
        if (!lstPhonemeScore.isEmpty()) {
            String nbest = "";
            int count90 = 0;
            for (int i = 0; i < lstPhonemeScore.size(); i++) {
                PhoneScore phoneScore = lstPhonemeScore.get(i);
                nbest += phoneScore.getWord() + ":" + phoneScore.getScore();
                if (i != lstPhonemeScore.size() - 1) {
                    nbest += ",";
                }
                if (phoneScore.getScore() > encourageScore) {
                    count90++;
                }
            }
            if (count90 <= 3) {
                mLogtf.d("onEvaluatorIng:nbest=" + nbest);
                return;
            }
            Point90 point90_6 = null;
            Point90 point90_3 = null;
            ArrayList<Point90> arrayList90_6 = new ArrayList<Point90>();
            ArrayList<Point90> arrayList90_3 = new ArrayList<Point90>();
            for (int i = 0; i < lstPhonemeScore.size(); i++) {
                PhoneScore phoneScore = lstPhonemeScore.get(i);
                if (phoneScore.getScore() > encourageScore && !point90WordArrayList.contains("" + i)) {
                    if (point90_6 == null) {
                        point90_6 = new Point90();
                        point90_6.left = point90_6.right = i;
                        point90_6.words.add(phoneScore.getWord());
                    } else {
                        point90_6.right = i;
                        point90_6.words.add(phoneScore.getWord());
                    }
                } else {
                    if (point90_6 != null) {
                        logger.d( "onEvaluatorIng:point90_61=" + point90_6);
                        if (point90_6.right - point90_6.left >= 5) {
                            arrayList90_6.add(point90_6);
                        }
                    }
                    point90_6 = null;
                }
                if (i == lstPhonemeScore.size() - 1) {
                    if (point90_6 != null) {
                        logger.d( "onEvaluatorIng:point90_62=" + point90_6);
                        if (point90_6.right - point90_6.left >= 5) {
                            arrayList90_6.add(point90_6);
                        }
                    }
                }
                if (phoneScore.getScore() > encourageScore && !point30WordArrayList.contains("" + i)) {
                    if (point90_3 == null) {
                        point90_3 = new Point90();
                        point90_3.left = point90_3.right = i;
                        point90_3.words.add(phoneScore.getWord());
                    } else {
                        point90_3.right = i;
                        point90_3.words.add(phoneScore.getWord());
                    }
                } else {
                    if (point90_3 != null) {
                        logger.d( "onEvaluatorIng:point90_31=" + point90_3);
                        if (point90_3.right - point90_3.left >= 2 && point90_3.right - point90_3.left < 5) {
                            arrayList90_3.add(point90_3);
                        }
                    }
                    point90_3 = null;
                }
                if (i == lstPhonemeScore.size() - 1) {
                    if (point90_3 != null) {
                        logger.d( "onEvaluatorIng:point90_32=" + point90_3);
                        if (point90_3.right - point90_3.left >= 2 && point90_3.right - point90_3.left < 5) {
                            arrayList90_3.add(point90_3);
                        }
                    }
                }
            }
            logger.d( "onResult:onEvaluatorIng:arrayList90_6=" + arrayList90_6.size() + ",arrayList90_3=" + arrayList90_3.size());
//            ArrayList<Point90> point90_6s = new ArrayList<Point90>();
//            ArrayList<Point90> point90_3s = new ArrayList<Point90>();
            ArrayList<Point90> point90_6s = arrayList90_6;
            ArrayList<Point90> point90_3s = arrayList90_3;
//            for (int i = 0; i < arrayList90_6.size(); i++) {
//                Point90 lstPhonemeScore902 = arrayList90_6.get(i);
//                if (lstPhonemeScore902.right - lstPhonemeScore902.left >= 5) {
//                    point90_6s.add(lstPhonemeScore902);
//                }
//            }
//            for (int i = 0; i < arrayList90_6.size(); i++) {
//                Point90 lstPhonemeScore902 = arrayList90_6.get(i);
//                if (lstPhonemeScore902.right - lstPhonemeScore902.left >= 2) {
//                    point90_3s.add(lstPhonemeScore902);
//                }
//            }
            if (!point90_6s.isEmpty()) {
                for (int i = 0; i < point90_6s.size(); i++) {
                    Point90 point901 = point90_6s.get(i);
                    for (int j = point901.left; j <= point901.right; j++) {
                        point90WordArrayList.add("" + j);
                        point30WordArrayList.add("" + j);
                    }
                }
                for (int i = 0; i < point90_3s.size(); i++) {
                    Point90 point901 = point90_3s.get(i);
                    for (int j = point901.left; j <= point901.right; j++) {
                        point90WordArrayList.add("" + j);
                        point30WordArrayList.add("" + j);
                    }
                }
                rlSpeectevalEncourage.removeCallbacks(encourageRun);
                rlSpeectevalEncourage.startAnimation(animSpeechEncourage);
//                ivSpeectevalEncourage.setImageResource(R.drawable.bg_livevideo_speecteval_encourage90);
                tvSpeectevalEncourage.setText("Perfect!");
                rlSpeectevalEncourage.postDelayed(encourageRun, 3000);
                mLogtf.d("onEvaluatorIng(perfect):nbest=" + nbest);
            } else if (!point90_3s.isEmpty()) {
                for (int i = 0; i < point90_3s.size(); i++) {
                    Point90 point901 = point90_3s.get(i);
                    for (int j = point901.left; j <= point901.right; j++) {
                        point30WordArrayList.add("" + j);
//                        point90WordArrayList.add(lstPhonemeScore.get(j).getWord());
                    }
                }
                rlSpeectevalEncourage.removeCallbacks(encourageRun);
                rlSpeectevalEncourage.startAnimation(animSpeechEncourage);
//                ivSpeectevalEncourage.setImageResource(R.drawable.bg_livevideo_speecteval_encourage60);
                tvSpeectevalEncourage.setText("Great!");
                rlSpeectevalEncourage.postDelayed(encourageRun, 3000);
                mLogtf.d("onEvaluatorIng(great):nbest=" + nbest);
            }
            logger.d( "onEvaluatorIng:count90=" + count90 + ",point90WordArrayList=" + point90WordArrayList.size() + ",point90_6s=" + point90_6s.size() + ",point90_3s=" + point90_3s.size() + ",nbest=" + nbest);
        }
    }

    private Runnable encourageRun = new Runnable() {
        @Override
        public void run() {
            rlSpeectevalEncourage.setVisibility(View.INVISIBLE);
        }
    };

    static class Point90 {
        int left = 0;
        int right = 0;
        ArrayList<String> words = new ArrayList<>();

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point90) {
                Point90 other = (Point90) obj;
                return left == other.left && right == other.right;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "left=" + left + ",right=" + right + ",words=" + words.size();
        }
    }

    private int wordChangeColor(int score, List<PhoneScore> lstPhonemeScore) {
        int COLOR_FF0000 = mContext.getResources().getColor(R.color.COLOR_FF4343);
        int COLOR_333333 = mContext.getResources().getColor(R.color.COLOR_333333);
        int COLOR_32B16C = 0xff2A9933;
        if (lstPhonemeScore.isEmpty()) {
            if (score > encourageScore) {
                tvSpeectevalContent.setTextColor(COLOR_32B16C);
            } else if (score < 60) {
                tvSpeectevalContent.setTextColor(COLOR_FF0000);
            } else {
                tvSpeectevalContent.setTextColor(COLOR_333333);
            }
            return 1;
        } else {
            String stemText = content;
            String bigStemText = stemText.toUpperCase();
            String subtemText = bigStemText;
            int lastSub = 0;
            SpannableStringBuilder spannable = new SpannableStringBuilder(stemText);
            boolean fail = false;
            for (int i = 0; i < lstPhonemeScore.size(); i++) {
                String word = lstPhonemeScore.get(i).getWord();
                int index = subtemText.indexOf(word);
                if (index == -1) {
                    fail = true;
                    continue;
                }
                int left = index + lastSub;
                int right = left + word.length();
                subtemText = subtemText.substring(index);
                lastSub += index;
                if (lstPhonemeScore.get(i).getScore() > encourageScore) {
                    //显示绿色
                    spannable.setSpan(new ForegroundColorSpan(COLOR_32B16C), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (lstPhonemeScore.get(i).getScore() < 60) {
                    // 显示红色
                    spannable.setSpan(new ForegroundColorSpan(COLOR_FF0000), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    // 显示黑色
                    spannable.setSpan(new ForegroundColorSpan(COLOR_333333), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            tvSpeectevalContent.setText(spannable);
            if (fail) {
                return 2;
            }
        }
        return 0;
    }

    int count = 3;
    private Runnable autoUploadRunnable = new Runnable() {
        @Override
        public void run() {
            tvSpeectevalError.setText(count + "秒后自动提交");
            if (count > 0) {
                tvSpeectevalError.postDelayed(this, 1000);
            } else {
                errorSetGone();
                if (mIse != null) {
                    mIse.stop();
                }
                if (isSpeechError || isSpeechSuccess) {
                    speechEvalAction.stopSpeech(SpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                }
            }
            count--;
        }
    };

    public void examSubmitAll() {
        isEnd = true;
        ViewGroup group = (ViewGroup) mView.getParent();
        mLogtf.d("examSubmitAll:mIse=" + (mIse != null) + ",Success=" + speechSuccess + ",group=" + (group == null));
        if (group == null) {
            return;
        }
        if (!isSpeechStart || isSpeechError || isSpeechSuccess) {
            speechEvalAction.stopSpeech(SpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
        } else {
            ivSpeectevalError.setImageResource(R.drawable.bg_livevideo_speecteval_error);
            errorSetVisible();
            tvSpeectevalError.setText(count + "秒后自动提交");
            tvSpeectevalError.postDelayed(autoUploadRunnable, 1000);
            count--;
        }
//        if (group != null) {
//            group.removeView(mView);
//        }
//        vwvSpeectevalWave.stop();
//        speechEvalAction.stopSpeech(id);
    }

}
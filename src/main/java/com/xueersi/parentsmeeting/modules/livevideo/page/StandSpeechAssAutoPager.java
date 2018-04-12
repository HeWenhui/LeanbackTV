package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.tal.speech.speechrecognizer.TalSpeech;
import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandSpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.OnSpeechEval;
import com.xueersi.parentsmeeting.modules.livevideo.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ReadyGoImageView;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.xesalib.utils.file.FileUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by linyuqiang on 2017/2/24.
 * 语音评测-站立直播
 */
public class StandSpeechAssAutoPager extends BaseSpeechAssessmentPager {
    public static boolean DEBUG = false;
    String eventId = LiveVideoConfig.LIVE_SPEECH_TEST2;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    /** 语音保存位置 */
    private String id;
    ReadyGoImageView rgiv_livevideo_stand_readygo;
    /** great鼓励外层 */
    RelativeLayout rlSpeectevalEncourage;
    /** great文字 */
    TextView tvSpeectevalEncourage;
    /** 提示和波浪线的外层 */
    RelativeLayout rlSpeectevalBg;
    LottieAnimationView lav_livevideo_voiceans_team_mine;
    ImageView iv_livevideo_voiceans_team_mine;
    RelativeLayout rl_livevideo_voiceans_content;
    /** 组内战况-左边 */
    LinearLayout ll_livevideo_voiceans_team_left;
    /** 组内战况-右边 */
    LinearLayout ll_livevideo_voiceans_team_right;
    /** 波浪线 */
    ImageView iv_livevideo_speecteval_wave;
    /** 提示外层 */
    RelativeLayout rlSpeectevalError;
    /** 提示图标 */
    ImageView ivSpeectevalError;
    /** 提示文字 */
    TextView tvSpeectevalError;
    TextView tv_livevideo_speecteval_countdown;
    /** great动画 */
    Animation animSpeechEncourage;
    int timeCount = 1;
    /** 评测成功 */
    private boolean speechSuccess = false;
    /** 语音保存位置 */
    private File saveVideoFile;
    private SpeechEvalAction speechEvalAction;
    private SpeechEvaluatorInter speechEvaluatorInter;
    /** 在线语音失败次数 */
    int onLineError = 0;
    private LogToFile logToFile;
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
    private String headUrl;
    private Bitmap headBitmap;
    private String userName;
    private String learning_stage;
    Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(),
            "fangzhengyouyuan.ttf");

    public StandSpeechAssAutoPager(Context context, String liveid, String testId,
                                   String nonce, String content, int time, boolean haveAnswer, SpeechEvalAction speechEvalAction, String userName, String headUrl, String learning_stage) {
        super(context);
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        this.isLive = true;
        this.id = testId;
        this.nonce = nonce;
        this.speechEvalAction = speechEvalAction;
        logToFile.i("SpeechAssessmentPager:id=" + id);
        startProgColor = context.getResources().getColor(R.color.COLOR_6462A2);
        progColor = 0;
        this.haveAnswer = haveAnswer;
        this.learning_stage = learning_stage;
//        content = "You are very good,You are very clever,welcome to my home";
//        content = "welcome to my home";
//        this.content = "C" + content.substring(1);
        this.content = content;
        this.time = time;
        this.userName = userName;
        this.headUrl = headUrl;
        entranceTime = System.currentTimeMillis();
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "receiveVoiceTest");
        mData.put("live", "" + isLive);
        mData.put("testtype", "4");
        mData.put("testid", id);
        mData.put("answer", content);
        mData.put("answertime", "" + time);
        speechEvalAction.umsAgentDebug3(eventId, mData);
    }

    public StandSpeechAssAutoPager(Context context, String liveid, String testId,
                                   String nonce, String content, int time, int examSubmit, SpeechEvalAction speechEvalAction, String userName, String headUrl, String learning_stage) {
        super(context);
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        this.isLive = false;
        this.id = testId;
        this.nonce = nonce;
        this.speechEvalAction = speechEvalAction;
        logToFile.i("SpeechAssessmentPager:id=" + id);
        startProgColor = context.getResources().getColor(R.color.COLOR_6462A2);
        progColor = 0;
//        content = "You are very good,You are very good";
//        this.content = "C" + content.substring(1);
        this.content = content;
        this.time = time;
        this.examSubmit = examSubmit;
        this.userName = userName;
        this.headUrl = headUrl;
        this.learning_stage = learning_stage;
        entranceTime = System.currentTimeMillis();
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "receiveVoiceTest");
        mData.put("live", "" + isLive);
        mData.put("testtype", "4");
        mData.put("testid", id);
        mData.put("answer", content);
        mData.put("answertime", "" + time);
        speechEvalAction.umsAgentDebug3(eventId, mData);
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
        View view = View.inflate(mContext, R.layout.page_live_stand_speecheval_auto_question, null);
        rgiv_livevideo_stand_readygo = view.findViewById(R.id.rgiv_livevideo_stand_readygo);
        lav_livevideo_voiceans_team_mine = view.findViewById(R.id.lav_livevideo_voiceans_team_mine);
        iv_livevideo_voiceans_team_mine = view.findViewById(R.id.iv_livevideo_voiceans_team_mine);
        rl_livevideo_voiceans_content = view.findViewById(R.id.rl_livevideo_voiceans_content);
        ll_livevideo_voiceans_team_left = view.findViewById(R.id.ll_livevideo_voiceans_team_left);
        ll_livevideo_voiceans_team_right = view.findViewById(R.id.ll_livevideo_voiceans_team_right);
        rlSpeectevalEncourage = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_encourage);
        tvSpeectevalEncourage = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_encourage);
        rlSpeectevalBg = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_bg);
        iv_livevideo_speecteval_wave = view.findViewById(R.id.iv_livevideo_speecteval_wave);
        rlSpeectevalError = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_error);
        ivSpeectevalError = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_error);
        tvSpeectevalError = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_error);
        tv_livevideo_speecteval_countdown = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_countdown);
//        iv_livevideo_speecteval_wave.setBackgroundResource(R.drawable.bg_livevideo_speecteval_wave);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                Loger.d(TAG, "onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                Loger.d(TAG, "onViewDetachedFromWindow:frameAnimations=" + frameAnimations.size());
                for (int i = 0; i < frameAnimations.size(); i++) {
                    FrameAnimation animation = frameAnimations.get(i);
                    animation.destory();
                }
            }
        });
        return view;
    }

    @Override
    public void initData() {
        animSpeechEncourage = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_speech_encourage);
        animSpeechEncourage.setInterpolator(new OvershootInterpolator());
        content2 = content.replace("\n", " ");
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(),
                "fangzhengyouyuan.ttf");
        tvSpeectevalEncourage.setTypeface(fontFace);
        File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/livevideo/");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ImageLoader.with(mContext).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                headBitmap = ((BitmapDrawable) drawable).getBitmap();
            }

            @Override
            public void onFail() {

            }
        });
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        rgiv_livevideo_stand_readygo.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                isSpeechStart = true;
                setAudioRequest();
                FrameAnimation frameAnimation1 = createFromAees("Images/voice_answer/1_enter", false);
                frameAnimations.add(frameAnimation1);
                frameAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        FrameAnimation frameAnimation2 = createFromAees("Images/voice_answer/2_loop", true);
                        frameAnimations.add(frameAnimation2);
                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        rgiv_livevideo_stand_readygo.start();
        if (speechEvalAction instanceof LiveStandSpeechEvalAction) {
            final LiveStandSpeechEvalAction liveStandSpeechEvalAction = (LiveStandSpeechEvalAction) speechEvalAction;
            mView.postDelayed(new Runnable() {
                Runnable r = this;
                Random random = new Random();
                int leftOrRight = 0;
                Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(),
                        "fangzhengyouyuan.ttf");
                int leftMaxChild = -1;
                int rightMaxChild = -1;

                @Override
                public void run() {
                    liveStandSpeechEvalAction.getSpeechEvalAnswerTeamStatus(id, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            GoldTeamStatus entity = (GoldTeamStatus) objData[0];
                            ArrayList<GoldTeamStatus.Student> students = entity.getStudents();
                            for (int i = 0; i < students.size(); i++) {
                                GoldTeamStatus.Student student = students.get(i);
                                LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
                                String path = "live_stand_voice_team_right.json";
                                lottieAnimationView.setImageAssetsFolder("Images/voice_answer/team_right");
                                LottieComposition.Factory.fromAssetFileName(mContext, path, new TeamOnCompositionLoadedListener(student, lottieAnimationView));
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.weight = 1;
                                int countLeft = ll_livevideo_voiceans_team_left.getChildCount();
                                int countRight = ll_livevideo_voiceans_team_right.getChildCount();
                                if (leftOrRight % 2 == 1) {
                                    if (countRight > 0) {
                                        int rightWidth = ((View) ll_livevideo_voiceans_team_right.getParent()).getWidth();
                                        View child = ll_livevideo_voiceans_team_right.getChildAt(0);
                                        int childWidth = child.getWidth();
                                        while (childWidth * (countRight + 1) > rightWidth) {
                                            countRight--;
                                            ll_livevideo_voiceans_team_right.removeViewAt(0);
                                        }
                                    }
                                    ll_livevideo_voiceans_team_right.addView(lottieAnimationView, lp);
                                } else {
                                    if (countLeft > 0) {
                                        int leftWidth = ((View) ll_livevideo_voiceans_team_left.getParent()).getWidth();
                                        View child = ll_livevideo_voiceans_team_left.getChildAt(countLeft - 1);
                                        int childWidth = child.getWidth();
                                        while (childWidth * (countLeft + 1) > leftWidth) {
                                            countLeft--;
                                            ll_livevideo_voiceans_team_left.removeViewAt(ll_livevideo_voiceans_team_left.getChildCount() - 1);
                                        }
                                    }
                                    ll_livevideo_voiceans_team_left.addView(lottieAnimationView, 0, lp);
                                }
                                leftOrRight++;
                            }
                            onFinish();
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            super.onDataFail(errStatus, failMsg);
                            onFinish();
                        }

                        private void onFinish() {
                            if (isSpeechSuccess) {
                                return;
                            }
                            mView.postDelayed(r, 3000);
                        }
                    });
                }
            }, 3000);

        }
    }

    private void setAudioRequest() {
        Loger.d(TAG, "setAudioRequest:userBack=" + userBack + ",isEnd=" + isEnd);
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
        speechEvalAction.umsAgentDebug2(eventId, mData);
        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, new EvaluatorListener() {
            int lastVolume = 0;

            @Override
            public void onBeginOfSpeech() {
                Loger.d(TAG, "onBeginOfSpeech");
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
                    speechEvalAction.umsAgentDebug2(eventId, mData);
                    onEvaluatorSuccess(resultEntity, this);

//                    resultEntity.setStatus(ResultEntity.ERROR);
////                    resultEntity.setErrorNo(ResultCode.MUTE_AUDIO);
//                    resultEntity.setErrorNo(ResultCode.WEBSOCKET_TIME_OUT);
//                    isSpeechError = true;
//                    onEvaluatorError(resultEntity, this);
                    onEvaluatorIng(resultEntity);
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    isSpeechError = true;
                    onEvaluatorError(resultEntity, this);
                }
//                else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
//                    onEvaluatorIng(resultEntity);
//                }
            }

            @Override
            public void onVolumeUpdate(final int volume) {
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
                            StandSpeechAssAutoPager.this.haveAnswer = answer;
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

    private void errorSetVisible() {
//        rlSpeectevalError.setVisibility(View.VISIBLE);
    }

    private void errorSetGone() {
//        rlSpeectevalError.setVisibility(View.GONE);
    }

    private void onEvaluatorSuccess(final ResultEntity resultEntity, final EvaluatorListener evaluatorListener) {
        final int score = resultEntity.getScore();
        if (!isEnd) {
            if (score == 1) {
                errorSetVisible();
                tvSpeectevalError.setText("要认真些，再来一次哦！");
                mView.postDelayed(new Runnable() {
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
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorSetGone();
                        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
                    }
                }, 500);
                return;
            }
        }
//        TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = (TeamOnCompositionLoadedListener) lav_livevideo_voiceans_team_mine.getTag();
//        if (teamOnCompositionLoadedListener != null) {
//            teamOnCompositionLoadedListener.updateScore(mContext, lav_livevideo_voiceans_team_mine, "" + resultEntity.getScore());
//        }
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
        logToFile.d("onEvaluatorSuccess:content=" + content + ",sid=" + resultEntity.getSid() + ",score=" + score + ",haveAnswer=" + haveAnswer + ",nbest=" + nbest);
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
                        mView.postDelayed(new Runnable() {
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
//        iv_livevideo_speecteval_wave.stop
        iv_livevideo_speecteval_wave.setVisibility(View.INVISIBLE);
    }

    private Bitmap updateHead(final FrameAnimation frameAnimation, ResultEntity resultEntity, final String file, boolean havename, int gold) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(160);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(160);
            Canvas canvas = new Canvas(canvasBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            final Bitmap head = headBitmap;
            if (head != null && !head.isRecycled()) {
                float scaleWidth = 148f / head.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                Bitmap scalHeadBitmap = Bitmap.createBitmap(head, 0, 0, head.getWidth(), head.getHeight(), matrix, true);
                scalHeadBitmap.setDensity(160);
                float left = (bitmap.getWidth() - scalHeadBitmap.getWidth()) / 2;
                float top;
                left += 3f;
                top = (bitmap.getHeight() - scalHeadBitmap.getHeight()) / 2 - 30;
                canvas.drawBitmap(scalHeadBitmap, left, top - 2, null);
                scalHeadBitmap.recycle();
            } else {
                Activity activity = (Activity) mContext;
                if (!activity.isFinishing()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageLoader.with(mContext).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
                                @Override
                                public void onSuccess(Drawable drawable) {
                                    Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                                    StandSpeechAssAutoPager.this.headBitmap = headBitmap;
                                    frameAnimation.removeBitmapCache(file);
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        }
                    });
                }
            }
            bitmap.recycle();
            //画名字和金币数量
            if (havename) {
                String strGold = "+" + gold;
                View layout_live_stand_red_mine1 = LayoutInflater.from(mContext).inflate(R.layout.layout_live_stand_red_mine2, null);
                TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_name);
                tv_livevideo_redpackage_name.setText("" + userName);
                TextView tv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_num);
                ImageView iv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.iv_livevideo_redpackage_num);
                tv_livevideo_redpackage_num.setText(strGold);
                tv_livevideo_redpackage_name.setTextSize(12.5f);
                tv_livevideo_redpackage_num.setTextSize(12.5f);
                tv_livevideo_redpackage_name.setTextColor(0xff97091D);
                tv_livevideo_redpackage_num.setTextColor(0xff97091D);
                iv_livevideo_redpackage_num.setImageResource(R.drawable.bg_live_stand_red_gold_big);
                layout_live_stand_red_mine1.measure(canvasBitmap.getWidth(), canvasBitmap.getHeight());
                layout_live_stand_red_mine1.layout(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight());

                canvas.save();
                canvas.translate((canvasBitmap.getWidth() - layout_live_stand_red_mine1.getMeasuredWidth()) / 2, 350);
                layout_live_stand_red_mine1.draw(canvas);
                canvas.restore();
            }
            return canvasBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void onSpeechEvalSuccess(final ResultEntity resultEntity, final int gold) {
        isSpeechSuccess = true;
        iv_livevideo_speecteval_wave.setVisibility(View.INVISIBLE);
        rlSpeectevalBg.setVisibility(View.GONE);
        rlSpeectevalBg.removeAllViews();
        int score = resultEntity.getScore();
        final RelativeLayout group = (RelativeLayout) mView;
        final View resultMine = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_stand_speech_mine, group, false);
        LinearLayout ll_livevideo_speecteval_result_mine = resultMine.findViewById(R.id.ll_livevideo_speecteval_result_mine);
//        bg_livevideo_speecteval_result_number_0
        for (int i = 0; i < ("" + score).length(); i++) {
            char c = ("" + score).charAt(i);
            ImageView imageView = new ImageView(mContext);
            String name = "bg_livevideo_speecteval_result_number_" + c;
            imageView.setImageResource(mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName()));
            ll_livevideo_speecteval_result_mine.addView(imageView);
        }
        ImageView imageViewScore = new ImageView(mContext);
        imageViewScore.setImageResource(R.drawable.bg_livevideo_speecteval_result_number_unit);
        ll_livevideo_speecteval_result_mine.addView(imageViewScore);
        resultMine.findViewById(R.id.iv_livevideo_speecteval_result_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.removeView(resultMine);
                if (isEnd || !isLive) {
                    speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, id);
                }
            }
        });
        final ImageView lottieAnimationView = resultMine.findViewById(R.id.iv_livevideo_speecteval_result_mine);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        group.addView(resultMine, lp);

        final FrameAnimation frameAnimation = FrameAnimation.createFromAees(mContext, lottieAnimationView, "Images/speech/mine_score", 50, false);
        frameAnimations.add(frameAnimation);
        frameAnimation.setBitmapCreate(new FrameAnimation.BitmapCreate() {
            @Override
            public Bitmap onAnimationCreate(String file) {
                if (file.contains("WDDFruchang_00169") || file.contains("WDDFruchang_00170") || file.contains("WDDFruchang_00171")) {
                    return null;
                }
                boolean havename = true;
                if (file.contains("_00172") || file.contains("_00173") || file.contains("_00174") || file.contains("_00175")
                        || file.contains("_00176") || file.contains("_00177")) {
                    havename = false;
                }
                return updateHead(frameAnimation, resultEntity, file, havename, gold);
            }
        });
        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                final FrameAnimation frameAnimation2 = FrameAnimation.createFromAees(mContext, lottieAnimationView, "Images/speech/mine_score_loop", 50, true);
                frameAnimations.add(frameAnimation2);
                frameAnimation2.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                    @Override
                    public Bitmap onAnimationCreate(String file) {
                        return updateHead(frameAnimation2, resultEntity, file, true, gold);
                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        if (haveAnswer) {

        }
//        spStarResult.setAnswered();

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
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                group.removeView(resultMine);
                if (isEnd || !isLive) {
                    speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, id);
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
        speechEvalAction.umsAgentDebug3(eventId, mData);
    }

    private void onEvaluatorError(final ResultEntity resultEntity, final EvaluatorListener evaluatorListener) {
        logToFile.d("onResult:ERROR:ErrorNo=" + resultEntity.getErrorNo() + ",isEnd=" + isEnd + ",isOfflineFail=" + SpeechEvaluatorUtils.isOfflineFail());
        tvSpeectevalError.removeCallbacks(autoUploadRunnable);
        ivSpeectevalError.setImageResource(R.drawable.bg_livevideo_speecteval_error);
        errorSetVisible();
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode
                .MUTE) {
//                            XESToastUtils.showToast(mContext, "声音有点小，大点声哦！");
            tvSpeectevalError.setText("声音有点小，再来一次哦！");
            if (!isEnd) {
                mView.postDelayed(new Runnable() {
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
                            mView.postDelayed(new Runnable() {
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
                            mView.postDelayed(new Runnable() {
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
//        vwvSpeectevalWave.stop();
        if (isEnd) {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, id);
                }
            }, 1000);
        }
    }

    private FrameAnimation createFromAees(String path, boolean isRepeat) {
        return FrameAnimation.createFromAees(mContext, iv_livevideo_speecteval_wave, path, 50, isRepeat);
    }

    private void onEvaluatorIng(ResultEntity resultEntity) {
        if (lav_livevideo_voiceans_team_mine.getVisibility() == View.VISIBLE) {
            TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = (TeamOnCompositionLoadedListener) lav_livevideo_voiceans_team_mine.getTag();
            teamOnCompositionLoadedListener.updateScore(mContext, lav_livevideo_voiceans_team_mine, "" + resultEntity.getScore());
            return;
        }
        lav_livevideo_voiceans_team_mine.setVisibility(View.VISIBLE);
        iv_livevideo_voiceans_team_mine.setVisibility(View.VISIBLE);
        iv_livevideo_voiceans_team_mine.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                iv_livevideo_voiceans_team_mine.getViewTreeObserver().removeOnPreDrawListener(this);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_live_stand_speech_mine_light_rotate);
                LinearInterpolator lin = new LinearInterpolator();
                animation.setInterpolator(lin);
                iv_livevideo_voiceans_team_mine.startAnimation(animation);
                return false;
            }
        });
        String path = "live_stand_voice_team_right.json";
        lav_livevideo_voiceans_team_mine.setImageAssetsFolder("Images/voice_answer/team_right");
        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
        student.setNickname(userName);
        student.setScore("" + resultEntity.getScore());
        student.setAvatar_path(headUrl);
        TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = new TeamOnCompositionLoadedListener(student, lav_livevideo_voiceans_team_mine);
        teamOnCompositionLoadedListener.isMe = true;
        LottieComposition.Factory.fromAssetFileName(mContext, path, teamOnCompositionLoadedListener);
        lav_livevideo_voiceans_team_mine.setTag(teamOnCompositionLoadedListener);
//        List<PhoneScore> lstPhonemeScore = resultEntity.getLstPhonemeScore();
//        if (!lstPhonemeScore.isEmpty()) {
//            String nbest = "";
//            int count90 = 0;
//            for (int i = 0; i < lstPhonemeScore.size(); i++) {
//                PhoneScore phoneScore = lstPhonemeScore.get(i);
//                nbest += phoneScore.getWord() + ":" + phoneScore.getScore();
//                if (i != lstPhonemeScore.size() - 1) {
//                    nbest += ",";
//                }
//                if (phoneScore.getScore() > encourageScore) {
//                    count90++;
//                }
//            }
//            if (count90 <= 3) {
//                logToFile.d("onEvaluatorIng:nbest=" + nbest);
//                return;
//            }
//            Point90 point90_6 = null;
//            Point90 point90_3 = null;
//            ArrayList<Point90> arrayList90_6 = new ArrayList<Point90>();
//            ArrayList<Point90> arrayList90_3 = new ArrayList<Point90>();
//            for (int i = 0; i < lstPhonemeScore.size(); i++) {
//                PhoneScore phoneScore = lstPhonemeScore.get(i);
//                if (phoneScore.getScore() > encourageScore && !point90WordArrayList.contains("" + i)) {
//                    if (point90_6 == null) {
//                        point90_6 = new Point90();
//                        point90_6.left = point90_6.right = i;
//                        point90_6.words.add(phoneScore.getWord());
//                    } else {
//                        point90_6.right = i;
//                        point90_6.words.add(phoneScore.getWord());
//                    }
//                } else {
//                    if (point90_6 != null) {
//                        Loger.d(TAG, "onEvaluatorIng:point90_61=" + point90_6);
//                        if (point90_6.right - point90_6.left >= 5) {
//                            arrayList90_6.add(point90_6);
//                        }
//                    }
//                    point90_6 = null;
//                }
//                if (i == lstPhonemeScore.size() - 1) {
//                    if (point90_6 != null) {
//                        Loger.d(TAG, "onEvaluatorIng:point90_62=" + point90_6);
//                        if (point90_6.right - point90_6.left >= 5) {
//                            arrayList90_6.add(point90_6);
//                        }
//                    }
//                }
//                if (phoneScore.getScore() > encourageScore && !point30WordArrayList.contains("" + i)) {
//                    if (point90_3 == null) {
//                        point90_3 = new Point90();
//                        point90_3.left = point90_3.right = i;
//                        point90_3.words.add(phoneScore.getWord());
//                    } else {
//                        point90_3.right = i;
//                        point90_3.words.add(phoneScore.getWord());
//                    }
//                } else {
//                    if (point90_3 != null) {
//                        Loger.d(TAG, "onEvaluatorIng:point90_31=" + point90_3);
//                        if (point90_3.right - point90_3.left >= 2 && point90_3.right - point90_3.left < 5) {
//                            arrayList90_3.add(point90_3);
//                        }
//                    }
//                    point90_3 = null;
//                }
//                if (i == lstPhonemeScore.size() - 1) {
//                    if (point90_3 != null) {
//                        Loger.d(TAG, "onEvaluatorIng:point90_32=" + point90_3);
//                        if (point90_3.right - point90_3.left >= 2 && point90_3.right - point90_3.left < 5) {
//                            arrayList90_3.add(point90_3);
//                        }
//                    }
//                }
//            }
//            Loger.d(TAG, "onResult:onEvaluatorIng:arrayList90_6=" + arrayList90_6.size() + ",arrayList90_3=" + arrayList90_3.size());
////            ArrayList<Point90> point90_6s = new ArrayList<Point90>();
////            ArrayList<Point90> point90_3s = new ArrayList<Point90>();
//            ArrayList<Point90> point90_6s = arrayList90_6;
//            ArrayList<Point90> point90_3s = arrayList90_3;
////            for (int i = 0; i < arrayList90_6.size(); i++) {
////                Point90 lstPhonemeScore902 = arrayList90_6.get(i);
////                if (lstPhonemeScore902.right - lstPhonemeScore902.left >= 5) {
////                    point90_6s.add(lstPhonemeScore902);
////                }
////            }
////            for (int i = 0; i < arrayList90_6.size(); i++) {
////                Point90 lstPhonemeScore902 = arrayList90_6.get(i);
////                if (lstPhonemeScore902.right - lstPhonemeScore902.left >= 2) {
////                    point90_3s.add(lstPhonemeScore902);
////                }
////            }
//            if (!point90_6s.isEmpty()) {
//                for (int i = 0; i < point90_6s.size(); i++) {
//                    Point90 point901 = point90_6s.get(i);
//                    for (int j = point901.left; j <= point901.right; j++) {
//                        point90WordArrayList.add("" + j);
//                        point30WordArrayList.add("" + j);
//                    }
//                }
//                for (int i = 0; i < point90_3s.size(); i++) {
//                    Point90 point901 = point90_3s.get(i);
//                    for (int j = point901.left; j <= point901.right; j++) {
//                        point90WordArrayList.add("" + j);
//                        point30WordArrayList.add("" + j);
//                    }
//                }
//                rlSpeectevalEncourage.removeCallbacks(encourageRun);
//                rlSpeectevalEncourage.startAnimation(animSpeechEncourage);
////                ivSpeectevalEncourage.setImageResource(R.drawable.bg_livevideo_speecteval_encourage90);
//                tvSpeectevalEncourage.setText("Perfect!");
//                rlSpeectevalEncourage.postDelayed(encourageRun, 3000);
//                logToFile.d("onEvaluatorIng(perfect):nbest=" + nbest);
//            } else if (!point90_3s.isEmpty()) {
//                for (int i = 0; i < point90_3s.size(); i++) {
//                    Point90 point901 = point90_3s.get(i);
//                    for (int j = point901.left; j <= point901.right; j++) {
//                        point30WordArrayList.add("" + j);
////                        point90WordArrayList.add(lstPhonemeScore.get(j).getWord());
//                    }
//                }
//                rlSpeectevalEncourage.removeCallbacks(encourageRun);
//                rlSpeectevalEncourage.startAnimation(animSpeechEncourage);
////                ivSpeectevalEncourage.setImageResource(R.drawable.bg_livevideo_speecteval_encourage60);
//                tvSpeectevalEncourage.setText("Great!");
//                rlSpeectevalEncourage.postDelayed(encourageRun, 3000);
//                logToFile.d("onEvaluatorIng(great):nbest=" + nbest);
//            }
//            Loger.d(TAG, "onEvaluatorIng:count90=" + count90 + ",point90WordArrayList=" + point90WordArrayList.size() + ",point90_6s=" + point90_6s.size() + ",point90_3s=" + point90_3s.size() + ",nbest=" + nbest);
//        }
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
                    speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, id);
                }
            }
            count--;
        }
    };

    public void examSubmitAll() {
        isEnd = true;
        ViewGroup group = (ViewGroup) mView.getParent();
        logToFile.d("examSubmitAll:mIse=" + (mIse != null) + ",Success=" + speechSuccess + ",group=" + (group == null));
        if (group == null) {
            return;
        }
        if (!isSpeechStart || isSpeechError || isSpeechSuccess) {
            speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, id);
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

    class TeamOnCompositionLoadedListener implements OnCompositionLoadedListener {
        GoldTeamStatus.Student student;
        LottieAnimationView lottieAnimationView;
        String lastScore = "";
        boolean isMe = false;

        public TeamOnCompositionLoadedListener(GoldTeamStatus.Student student, LottieAnimationView lottieAnimationView) {
            this.student = student;
            this.lottieAnimationView = lottieAnimationView;
        }

        void updateName() {
            InputStream inputStream = null;
            try {
                if (isMe) {
                    inputStream = mContext.getAssets().open("Images/voice_answer/team_right/img_11.png");
                } else {
                    inputStream = mContext.getAssets().open("Images/voice_answer/team_right/img_1.png");
                }
                Bitmap headBack = BitmapFactory.decodeStream(inputStream);
                Bitmap creatBitmap = Bitmap.createBitmap(headBack.getWidth(), headBack.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(creatBitmap);
                canvas.drawBitmap(headBack, 0, 0, null);
                String name = student.getNickname();
                Paint paint = new Paint();
                paint.setTextSize(20);
                if (isMe) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(0xffA56202);
                }
                float width = paint.measureText(name);
                canvas.drawText(name, headBack.getWidth() / 2 - width / 2, headBack.getHeight() / 2 + paint.measureText("a") / 2, paint);
                lottieAnimationView.updateBitmap("image_1", creatBitmap);
                headBack.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        void updateHead() {
            Activity activity = (Activity) mContext;
            if (!activity.isFinishing()) {
                ImageLoader.with(mContext).load(student.getAvatar_path()).asCircle().asBitmap(new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                        InputStream inputStream = null;
                        try {
                            inputStream = mContext.getAssets().open("Images/voice_answer/team_right/img_2.png");
                            Bitmap headBack = BitmapFactory.decodeStream(inputStream);
                            Bitmap creatBitmap = Bitmap.createBitmap(headBack.getWidth(), headBack.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(creatBitmap);
                            canvas.drawBitmap(headBack, 0, 0, null);
                            int left = headBack.getWidth() / 2 - headBitmap.getWidth() / 2;
                            canvas.drawBitmap(headBitmap, left, left, null);
                            lottieAnimationView.updateBitmap("image_2", creatBitmap);
                            headBack.recycle();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
        }

        private void updateScore(Context context, LottieAnimationView lottieAnimationView, String score) {
            if (lastScore.equals(score)) {
                return;
            }
            lastScore = score;
            String text = score;
            AssetManager manager = context.getAssets();
            Bitmap img_7Bitmap;
            try {
                img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/voice_answer/team_right/img_0.png"));
                Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(creatBitmap);
                Paint paintInner = new Paint();
                paintInner.setTextSize(25);
                paintInner.setColor(0xffFE5C03);
                paintInner.setTypeface(fontFace);

                Paint paintOut = new Paint();
                paintOut.setStyle(Paint.Style.STROKE);
                paintOut.setTextSize(25);
                paintOut.setStrokeWidth(3);
                paintOut.setColor(0xffffffff);
                paintOut.setTypeface(fontFace);

                float widthOut = paintOut.measureText(text);

                float x = (creatBitmap.getWidth() - widthOut) / 2;
                float y = (creatBitmap.getHeight() + paintOut.measureText("a")) / 2;
                Paint mPaint = paintOut;
                int strokeSize = 3;
                if (strokeSize > 0 && strokeSize < 4) {
                    canvas.drawText(text, x, y - strokeSize, mPaint);
                    canvas.drawText(text, x, y + strokeSize, mPaint);
                    canvas.drawText(text, x + strokeSize, y, mPaint);
                    canvas.drawText(text, x + strokeSize, y + strokeSize, mPaint);
                    canvas.drawText(text, x + strokeSize, y - strokeSize, mPaint);
                    canvas.drawText(text, x - strokeSize, y, mPaint);
                    canvas.drawText(text, x - strokeSize, y + strokeSize, mPaint);
                    canvas.drawText(text, x - strokeSize, y - strokeSize, mPaint);
                    canvas.drawText(text, x, y, paintInner);
                }

                img_7Bitmap.recycle();
                img_7Bitmap = creatBitmap;
            } catch (IOException e) {
                Log.e(TAG, "updateScore", e);
                return;
            }
            lottieAnimationView.updateBitmap("image_0", img_7Bitmap);
        }

        @Override
        public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
            if (lottieComposition == null) {
                return;
            }
            lottieAnimationView.setComposition(lottieComposition);
            lottieAnimationView.playAnimation();
            updateHead();
            updateName();
            updateScore(mContext, lottieAnimationView, student.getScore());
        }
    }
}

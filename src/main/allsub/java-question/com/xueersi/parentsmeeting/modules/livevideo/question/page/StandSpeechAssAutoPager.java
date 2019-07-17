package com.xueersi.parentsmeeting.modules.livevideo.question.page;

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
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.speechrecognizer.TalSpeech;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.LiveStandSpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.OnSpeechEval;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.EngForceSubmit;
import com.xueersi.parentsmeeting.modules.livevideo.question.view.StandSpeechResult;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.SpeechStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;
import com.xueersi.parentsmeeting.modules.livevideo.util.TextStrokeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ReadyGoImageView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StandLiveTextView;

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
 * @author linyuqiang
 * @date 2017/2/24
 * 语音评测-站立直播
 */
public class StandSpeechAssAutoPager extends BaseSpeechAssessmentPager {
    public static boolean DEBUG = false;
    String eventId = LiveVideoConfig.LIVE_STAND_SPEECH_TEST;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    /** 组内战况已经被加入的学生 */
    private ArrayList<GoldTeamStatus.Student> addStudents = new ArrayList<>();
    /** 组内战况已经弹过 */
    private boolean teamStatus = false;
    /** 语音保存位置 */
    private String id;
    /** ReadyGo 动画 */
    ReadyGoImageView rgivLivevideoStandReadygo;
    /** great鼓励外层 */
    RelativeLayout rlSpeectevalEncourage;
    /** great文字 */
    TextView tvSpeectevalEncourage;
    /** 提示和波浪线的外层 */
    RelativeLayout rlSpeectevalBg;
    LottieAnimationView lavLivevideoVoiceansTeamMine;
    ImageView ivLivevideoVoiceansTeamMine;
    RelativeLayout rlLivevideoVoiceansContent;
    /** 组内战况-左边 */
    LinearLayout llLivevideoVoiceansTeamLeft;
    /** 组内战况-右边 */
    LinearLayout llLivevideoVoiceansTeamRight;
    /** 波浪线 */
    ImageView ivLivevideoSpeectevalWave;
    /** 倒计时-暂时没用 */
    TextView tvLivevideoSpeectevalCountdown;
    /** great动画 */
    Animation animSpeechEncourage;
    /** 错误提示 */
    RelativeLayout rlSpeectevalTip;
    /** 错误提示-文字 */
    TextView tvSpeectevalTip;
    /** 认真些，再来一次吧 */
    private int errorTip1 = R.drawable.live_stand_answer_voice_caution_01;
    /** 声音有点小，再来一次哦！ */
    private int errorTip2 = R.drawable.live_stand_answer_voice_caution_02;
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
    /** 是不是显示评测结果页 */
    private boolean isSpeechResult = false;
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
    Typeface fontFace;
    String file1 = "live_stand/frame_anim/voice_answer/1_enter";
    String file2 = "live_stand/frame_anim/voice_answer/2_loop";
    String file3 = "live_stand/frame_anim/speech/mine_score";
    String file4 = "live_stand/frame_anim/speech/mine_score_loop";
    LiveSoundPool liveSoundPool;
    //语音识别参数
    SpeechParamEntity mParam;
    /** 是不是新课件 */
    final boolean isNewArts;

    /** 语音答题直播 */
    public StandSpeechAssAutoPager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity, String liveid,
                                   String testId,
                                   String nonce, String content, int time, boolean haveAnswer, SpeechEvalAction
                                           speechEvalAction, String userName, String headUrl, String learning_stage,
                                   LivePagerBack livePagerBack) {
        super(context);
        isNewArts = baseVideoQuestionEntity.isNewArtsH5Courseware();
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
        this.userName = StandLiveTextView.getShortName(userName);
        this.headUrl = headUrl;
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

    public boolean isNewArt() {
        return isNewArts;
    }
    /** 语音答题回放 */
    public StandSpeechAssAutoPager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity, String liveid,
                                   String testId,
                                   String nonce, String content, int time, int examSubmit, SpeechEvalAction
                                           speechEvalAction, String userName, String headUrl, String learning_stage) {
        super(context);
        isNewArts = baseVideoQuestionEntity.isNewArtsH5Courseware();
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
        this.userName = StandLiveTextView.getShortName(userName);
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
        View view = View.inflate(mContext, R.layout.page_live_stand_speecheval_auto_question, null);
        rgivLivevideoStandReadygo = view.findViewById(R.id.rgiv_livevideo_stand_readygo);
        lavLivevideoVoiceansTeamMine = view.findViewById(R.id.lav_livevideo_voiceans_team_mine);
        ivLivevideoVoiceansTeamMine = view.findViewById(R.id.iv_livevideo_voiceans_team_mine);
        rlLivevideoVoiceansContent = view.findViewById(R.id.rl_livevideo_voiceans_content);
        llLivevideoVoiceansTeamLeft = view.findViewById(R.id.ll_livevideo_voiceans_team_left);
        llLivevideoVoiceansTeamRight = view.findViewById(R.id.ll_livevideo_voiceans_team_right);
        rlSpeectevalEncourage = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_encourage);
        tvSpeectevalEncourage = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_encourage);
        rlSpeectevalBg = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_bg);
        ivLivevideoSpeectevalWave = view.findViewById(R.id.iv_livevideo_speecteval_wave);
        rlSpeectevalTip = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_tip);
        tvSpeectevalTip = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_tip);
        tvLivevideoSpeectevalCountdown = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_countdown);
//        ivLivevideoSpeectevalWave.setBackgroundResource(R.drawable.bg_livevideo_speecteval_wave);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                logger.d("onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                logger.d("onViewDetachedFromWindow:frameAnimations=" + frameAnimations.size());
                for (int i = 0; i < frameAnimations.size(); i++) {
                    FrameAnimation animation = frameAnimations.get(i);
                    animation.destory();
                }
                if (liveSoundPool != null) {
                    liveSoundPool.release();
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
        fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
        tvSpeectevalEncourage.setTypeface(fontFace);
        mParam = new SpeechParamEntity();
        File dir = LiveCacheFile.geCacheFile(mContext, "liveSpeech");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ImageLoader.with(BaseApplication.getContext()).load(headUrl).asCircle().asBitmap(new SingleConfig
                .BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "initData", headUrl);
            }

            @Override
            public void onFail() {

            }
        });
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        rgivLivevideoStandReadygo.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                ViewGroup group = (ViewGroup) rgivLivevideoStandReadygo.getParent();
                if (group != null) {
                    group.removeView(rgivLivevideoStandReadygo);
                }
                rgivLivevideoStandReadygo.destory();
                if (mContext instanceof Activity) {
                    Activity activity = (Activity) mContext;
                    if (activity.isFinishing()) {
                        return;
                    }
                }
                afterReadyGo();
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        liveSoundPool = LiveSoundPool.createSoundPool();
        rgivLivevideoStandReadygo.start(liveSoundPool);
        if (speechEvalAction instanceof LiveStandSpeechEvalAction) {
            final LiveStandSpeechEvalAction liveStandSpeechEvalAction = (LiveStandSpeechEvalAction) speechEvalAction;
            mView.postDelayed(new Runnable() {
                Runnable r = this;
                Random random = new Random();
                int leftOrRight = 0;
                ArrayList<View> leftView = new ArrayList<>();
                ArrayList<View> rightView = new ArrayList<>();

                @Override
                public void run() {
                    liveStandSpeechEvalAction.getSpeechEvalAnswerTeamStatus(isNewArts,id, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            GoldTeamStatus entity = (GoldTeamStatus) objData[0];
                            ArrayList<GoldTeamStatus.Student> students = entity.getStudents();
                            if (!teamStatus && !students.isEmpty()) {
                                teamStatus = true;
                                SpeechStandLog.sno4(StandSpeechAssAutoPager.this, id);
                            }
                            long delayMillis = liveStandSpeechEvalAction.getRequestTime();
                            int addCount = 0;
                            for (int i = 0; i < students.size(); i++) {
                                final GoldTeamStatus.Student student = students.get(i);
                                if (student.isMe()) {
                                    continue;
                                }
                                if (addStudents.contains(student)) {
                                    continue;
                                }
                                delayMillis += 300;
                                addStudents.add(student);
                                mView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
                                        String path = "live_stand/lottie/live_stand_voice_team_right.json";
                                        lottieAnimationView.setImageAssetsFolder
                                                ("live_stand/lottie/voice_answer/team_right");
                                        LottieComposition.Factory.fromAssetFileName(mContext, path, new
                                                TeamOnCompositionLoadedListener(student, lottieAnimationView));
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(RelativeLayout
                                                .LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                lp.weight = 1;
                                        int countLeft = llLivevideoVoiceansTeamLeft.getChildCount();
                                        int countRight = llLivevideoVoiceansTeamRight.getChildCount();
                                        if (leftOrRight % 2 == 1) {
                                            boolean remove = false;
                                            int index = 0;
                                            if (countRight > 0) {
                                                int rightWidth = ((View) llLivevideoVoiceansTeamRight.getParent())
                                                        .getWidth();
                                                View child = llLivevideoVoiceansTeamRight.getChildAt(0);
                                                int childWidth = child.getWidth();
                                                while (childWidth * (countRight + 1) > rightWidth) {
                                                    countRight--;
                                                    View view = rightView.remove(0);
                                                    lp.width = childWidth;
                                                    index = llLivevideoVoiceansTeamRight.indexOfChild(view);
                                                    llLivevideoVoiceansTeamRight.removeViewInLayout(view);
                                                    remove = true;
                                                }
                                            }
                                            if (remove) {
                                                llLivevideoVoiceansTeamRight.addView(lottieAnimationView, index, lp);
                                            } else {
                                                llLivevideoVoiceansTeamRight.addView(lottieAnimationView, lp);
                                            }
                                            rightView.add(lottieAnimationView);
                                        } else {
                                            boolean remove = false;
                                            int index = 0;
                                            if (countLeft > 0) {
                                                int leftWidth = ((View) llLivevideoVoiceansTeamLeft.getParent())
                                                        .getWidth();
                                                View child = llLivevideoVoiceansTeamLeft.getChildAt(countLeft - 1);
                                                int childWidth = child.getWidth();
                                                while (childWidth * (countLeft + 1) > leftWidth) {
                                                    countLeft--;
                                                    remove = true;
                                                    View view = leftView.remove(0);
                                                    lp.width = childWidth;
                                                    index = llLivevideoVoiceansTeamLeft.indexOfChild(view);
                                                    llLivevideoVoiceansTeamLeft.removeViewInLayout(view);
                                                }
                                            }
                                            if (remove) {
                                                llLivevideoVoiceansTeamLeft.addView(lottieAnimationView, index, lp);
                                            } else {
                                                llLivevideoVoiceansTeamLeft.addView(lottieAnimationView, 0, lp);
                                            }
                                            leftView.add(lottieAnimationView);
                                        }
                                        leftOrRight++;
                                    }
                                }, 300 * addCount);
                                addCount++;
                            }
                            onFinish(delayMillis);
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            super.onDataFail(errStatus, failMsg);
                            onFinish(3000);
                        }

                        private void onFinish(long delayMillis) {
                            if (mView.getParent() == null) {
                                return;
                            }
                            mView.postDelayed(r, delayMillis);
                        }
                    });
                }
            }, 3000);
        }
    }

    /**
     * readygo 以后。检查权限
     */
    private void afterReadyGo() {
        boolean have = XesPermission.checkPermission(mContext, new LiveActivityPermissionCallback() {

            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {
                isSpeechError = true;
                SpeechStandLog.sno3(StandSpeechAssAutoPager.this, id, false);
            }

            @Override
            public void onGuarantee(String permission, int position) {
                startVoice();
            }
        }, PermissionConfig.PERMISSION_CODE_AUDIO);
        if (have) {
            startVoice();
        }
    }

    /**
     * 权限申请后，开始语音
     */
    private void startVoice() {
        isSpeechStart = true;
        SpeechStandLog.sno3(this, id, true);
        setAudioRequest();
        FrameAnimation frameAnimation1 = createFromAees(file1, false);
        frameAnimations.add(frameAnimation1);
        frameAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                FrameAnimation frameAnimation2 = createFromAees(file2, true);
                frameAnimations.add(frameAnimation2);
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private void setAudioRequest() {
        logger.d("setAudioRequest:userBack=" + userBack + ",isEnd=" + isEnd);
        if (userBack) {
            return;
        }
        //语音评测开始
        if (mIse == null) {
            mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
            mIse.prepar();
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "startRecord");
        mData.put("testid", id);
        mData.put("islive", "" + isLive);
        umsAgentDebugInter(eventId, mData);
        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        mParam.setStrEvaluator(content2);
        mParam.setLocalSavePath(saveVideoFile.getPath());
        mParam.setMultRef(false);
        mParam.setLearning_stage(learning_stage);
        mIse.startRecog(mParam, new EvaluatorListener() {
            int lastVolume = 0;

            @Override
            public void onBeginOfSpeech() {
                logger.d("onBeginOfSpeech");
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
                }
//                else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
//                    onEvaluatorIng(resultEntity);
//                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
                lastVolume = volume;
            }

        });
//        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(), false,
// learning_stage, new EvaluatorListener() {
//            int lastVolume = 0;
//
//            @Override
//            public void onBeginOfSpeech() {
//                logger.d( "onBeginOfSpeech");
//                isSpeechError = false;
//            }
//
//            @Override
//            public void onResult(ResultEntity resultEntity) {
//                if (userBack) {
//                    return;
//                }
//                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
//                    Map<String, String> mData = new HashMap<>();
//                    mData.put("logtype", "voiceTestClose");
//                    mData.put("islive", "" + isLive);
//                    mData.put("testid", "" + id);
//                    umsAgentDebugInter(eventId, mData);
//                    onEvaluatorSuccess(resultEntity, this);
//
////                    resultEntity.setStatus(ResultEntity.ERROR);
//////                    resultEntity.setErrorNo(ResultCode.MUTE_AUDIO);
////                    resultEntity.setErrorNo(ResultCode.WEBSOCKET_TIME_OUT);
////                    isSpeechError = true;
////                    onEvaluatorError(resultEntity, this);
//                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
//                    isSpeechError = true;
//                    onEvaluatorError(resultEntity, this);
//                }
////                else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
////                    onEvaluatorIng(resultEntity);
////                }
//            }
//
//            @Override
//            public void onVolumeUpdate(final int volume) {
//                lastVolume = volume;
//            }
//
//        });
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
//            errorSetVisible();
//            tvSpeectevalTip.setText("题目已作答");
        } else {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechEvalAction.speechIsAnswered(isNewArts,id, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            boolean answer = (boolean) objData[0];
                            StandSpeechAssAutoPager.this.haveAnswer = answer;
                        }
                    });
                }
            }, random.nextInt(2000));
        }
    }

    private void errorSetVisible() {
        rlSpeectevalTip.setVisibility(View.VISIBLE);
    }

    private void errorSetGone() {
        rlSpeectevalTip.setVisibility(View.GONE);
    }

    private void onEvaluatorSuccess(final ResultEntity resultEntity, final EvaluatorListener evaluatorListener) {
        final int score = resultEntity.getScore();
        mLogtf.d("onEvaluatorSuccess:score=" + score + ",isEnd=" + isEnd);
        if (!isEnd) {
            if (score == 1) {
                errorSetVisible();
//                tvSpeectevalTip.setText("要认真些，再来一次哦！");
                tvSpeectevalTip.setBackgroundResource(errorTip1);
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorSetGone();
                        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
                        mParam.setStrEvaluator(content2);
                        mParam.setLocalSavePath(saveVideoFile.getPath());
                        mParam.setMultRef(false);
                        mParam.setLearning_stage(learning_stage);
                        mIse.startRecog(mParam, evaluatorListener);
                    }
                }, 500);
                return;
            } else if (score < 60) {
                errorSetVisible();
//                tvSpeectevalTip.setText("你可以说的更好，再来一次哦！");
                tvSpeectevalTip.setBackgroundResource(errorTip1);
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorSetGone();
                        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
                        mParam.setStrEvaluator(content2);
                        mParam.setLocalSavePath(saveVideoFile.getPath());
                        mParam.setMultRef(false);
                        mParam.setLearning_stage(learning_stage);
                        mIse.startRecog(mParam, evaluatorListener);
//                        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(),
// false, learning_stage, evaluatorListener);
                    }
                }, 500);
                return;
            }
        }
//        TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = (TeamOnCompositionLoadedListener)
// lavLivevideoVoiceansTeamMine.getTag();
//        if (teamOnCompositionLoadedListener != null) {
//            teamOnCompositionLoadedListener.updateScore(mContext, lavLivevideoVoiceansTeamMine, "" + resultEntity
// .getScore());
//        }
        //强制收卷，继续显示倒计时
        if (!isEnd) {
            handler.removeCallbacks(autoUploadRunnable);
        }
//        errorSetVisible();
//        tvSpeectevalTip.setTextColor(mContext.getResources().getColor(R.color.COLOR_6462A2));
//        tvSpeectevalTip.setText("录音上传中");
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
        mLogtf.d("onEvaluatorSuccess:content=" + content + ",sid=" + resultEntity.getSid() + ",score=" + score + "," +
                "haveAnswer=" + haveAnswer + ",nbest=" + nbest);
        if (haveAnswer) {
            onSpeechEvalSuccess(resultEntity, 0, 0);
        } else {
            try {
                final JSONObject answers = new JSONObject();
                JSONObject answers1 = new JSONObject();
                try {
                    answers1.put("entranceTimeLog", entranceTime + "," + System.currentTimeMillis());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                long entranceTime2 = System.currentTimeMillis() - entranceTime;
                answers1.put("entranceTime", (int) resultEntity.getSpeechDuration());
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
                final String isSubmit = EngForceSubmit.getSubmit(isNewArts, false);
                speechEvalAction.sendSpeechEvalResult2(id, (VideoQuestionLiveEntity) baseVideoQuestionEntity, answers.toString(), isSubmit, new AbstractBusinessDataCallBack() {
                    AbstractBusinessDataCallBack onSpeechEval = this;

                    @Override
                    public void onDataSucess(Object... objData) {
                        final JSONObject jsonObject = (JSONObject) objData[0];
                        logger.d("sendSpeechEvalResult2:onSpeechEval:object=" + jsonObject);
                        if (count < 3 && count > 0) {
                            long delayMillis = count * 1000;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onSuccess(jsonObject);
                                }
                            }, delayMillis);
                        } else {
                            onSuccess(jsonObject);
                        }
                    }

                    private void onSuccess(JSONObject jsonObject) {
                        int gold = jsonObject.optInt("gold");
                        int energy = jsonObject.optInt("energy");
                        haveAnswer = jsonObject.optInt("isAnswered", 0) == 1;
                        logger.d("onSpeechEval:jsonObject=" + jsonObject);
                        onSpeechEvalSuccess(resultEntity, gold, energy);
                        speechEvalAction.onSpeechSuccess(id);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        if (errStatus == LiveHttpConfig.HTTP_ERROR_FAIL) {
                            logger.d("sendSpeechEvalResult2:onPmFailure:msg=" + failMsg);
                            if (isEnd) {
                                speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                            } else {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        speechEvalAction.sendSpeechEvalResult2(id, (VideoQuestionLiveEntity) baseVideoQuestionEntity, answers.toString(), isSubmit, onSpeechEval);
                                    }
                                }, 1000);
                            }
                        } else {
                            logger.d("sendSpeechEvalResult2:onPmError:error=" + failMsg);
                        }
                    }
                });
            } catch (JSONException e) {
                logger.e("sendSpeechEvalResult2", e);
            }
        }
        rlSpeectevalEncourage.setVisibility(View.INVISIBLE);
//        ivLivevideoSpeectevalWave.stop
        ivLivevideoSpeectevalWave.setVisibility(View.INVISIBLE);
    }

    private Bitmap updateHead(final FrameAnimation frameAnimation, ResultEntity resultEntity, final String file,
                              boolean havename, int gold) {
        InputStream inputStream = null;
        try {
            inputStream = FrameAnimation.getInputStream(mContext, file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Canvas canvas = new Canvas(canvasBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            final Bitmap head = headBitmap;
            if (head != null && !head.isRecycled()) {
                float scaleWidth = 148f / head.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                Bitmap scalHeadBitmap = Bitmap.createBitmap(head, 0, 0, head.getWidth(), head.getHeight(), matrix,
                        true);
                scalHeadBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
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
                            ImageLoader.with(mContext).load(headUrl).asCircle().asBitmap(new SingleConfig
                                    .BitmapListener() {
                                @Override
                                public void onSuccess(Drawable drawable) {
                                    Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "updateHead",
                                            headUrl);
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
//                View layout_live_stand_red_mine1 = StandSpeechResult.resultViewName(mContext, "" + gold, fontFace, userName);
                View layout_live_stand_red_mine1 = StandSpeechResult.resultViewNameEnergy(mContext, userName);
                canvas.save();
                canvas.translate((canvasBitmap.getWidth() - layout_live_stand_red_mine1.getMeasuredWidth()) / 2, 348);
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

    private void onSpeechEvalSuccess(final ResultEntity resultEntity, final int gold, int energy) {
        ivLivevideoSpeectevalWave.setVisibility(View.INVISIBLE);
        rlSpeectevalBg.setVisibility(View.GONE);
        rlSpeectevalBg.removeAllViews();
        //直播中显示左下角自己的分数
        if (isLive) {
            onSpeechEvalSuccessMe(resultEntity);
        }
        StandLiveMethod.leaderBoard(liveSoundPool);
        int score = resultEntity.getScore();
        final RelativeLayout group = (RelativeLayout) mView;
//        final View resultMine = StandSpeechResult.resultViewScore(mContext, group, gold, energy, score);
        final View resultMine = StandSpeechResult.resultViewScoreEnergy(mContext, group, gold, energy, score);
        resultMine.findViewById(R.id.iv_livevideo_speecteval_result_close).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                StandLiveMethod.onClickVoice(liveSoundPool);
                group.removeView(resultMine);
                isSpeechResult = true;
                if (isEnd || !isLive) {
                    speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                }
            }
        });
        final ImageView lottieAnimationView = resultMine.findViewById(R.id.iv_livevideo_speecteval_result_mine);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        group.addView(resultMine, lp);

        final FrameAnimation frameAnimation = FrameAnimation.createFromAees(mContext, lottieAnimationView, file3, 50,
                false);
        frameAnimations.add(frameAnimation);
        frameAnimation.setBitmapCreate(new FrameAnimation.BitmapCreate() {
            @Override
            public Bitmap onAnimationCreate(String file) {
                if (file.contains("WDDFruchang_00169") || file.contains("WDDFruchang_00170") || file.contains
                        ("WDDFruchang_00171")) {
                    return null;
                }
                boolean havename = true;
                if (file.contains("_00172") || file.contains("_00173") || file.contains("_00174") || file.contains
                        ("_00175")
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
                final FrameAnimation frameAnimation2 = FrameAnimation.createFromAees(mContext, lottieAnimationView,
                        file4, 50, true);
                frameAnimations.add(frameAnimation2);
                frameAnimation2.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                    @Override
                    public Bitmap onAnimationCreate(String file) {
                        return updateHead(frameAnimation2, resultEntity, file, true, gold);
                    }
                });
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        group.removeView(resultMine);
                        frameAnimation2.destory();
                        isSpeechResult = true;
                        if (isEnd || !isLive) {
                            speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                        }
                    }
                }, 3000);
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
        speechEvalAction.onSpeechSuccess(id);
        String state;
        if (haveAnswer) {
            state = "noSubmit";
        } else {
            state = isEnd ? "endPublish" : "autoSubmit";
        }
        SpeechStandLog.sno5(this, id, state, gold, progress, score, resultEntity.getSpeechDuration());
    }

    private void onEvaluatorError(final ResultEntity resultEntity, final EvaluatorListener evaluatorListener) {
        mLogtf.d("onResult:ERROR:ErrorNo=" + resultEntity.getErrorNo() + ",isEnd=" + isEnd + ",isOfflineFail=" + mIse
                .isOfflineFail());
        handler.removeCallbacks(autoUploadRunnable);
        errorSetVisible();
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode
                .MUTE) {
//                            XESToastUtils.showToast(mContext, "声音有点小，大点声哦！");
//            tvSpeectevalTip.setText("声音有点小，再来一次哦！");
            tvSpeectevalTip.setBackgroundResource(errorTip2);
            if (!isEnd) {
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorSetGone();
                        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
                        mParam.setStrEvaluator(content2);
                        mParam.setLocalSavePath(saveVideoFile.getPath());
                        mParam.setMultRef(false);
                        mParam.setLearning_stage(learning_stage);
                        mIse.startRecog(mParam, evaluatorListener);
//                        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2, saveVideoFile.getPath(),
// false, learning_stage, evaluatorListener);
                    }
                }, 1000);
                return;
            }
        } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
//            tvSpeectevalTip.setText("麦克风不可用，快去检查一下");
            XESToastUtils.showToast(mContext, "麦克风不可用，快去检查一下");
        } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() ==
                ResultCode.NETWORK_FAIL
                || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
//            tvSpeectevalTip.setText("好像没网了，快检查一下");
            XESToastUtils.showToast(mContext, "好像没网了，快检查一下");
            if (!mIse.isOfflineFail()) {
                if (speechEvaluatorInter instanceof TalSpeech) {
                    onLineError++;
                    if (onLineError == 1) {
                        if (!isEnd) {
                            mView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorSetGone();
                                    mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
                                    mParam.setStrEvaluator(content2);
                                    mParam.setLocalSavePath(saveVideoFile.getPath());
                                    mParam.setMultRef(false);
                                    mParam.setLearning_stage(learning_stage);
                                    mIse.startRecog(mParam, evaluatorListener);
//                                    speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2,
// saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
                                }
                            }, 1000);
                            return;
                        }
                    }
                }
            }
        } else if (resultEntity.getErrorNo() == ResultCode.SPEECH_CANCLE) {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isEnd) {
                        errorSetGone();
                        if (isAttach()) {
                            mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
                            mParam.setStrEvaluator(content2);
                            mParam.setLocalSavePath(saveVideoFile.getPath());
                            mParam.setMultRef(false);
                            mParam.setLearning_stage(learning_stage);
                            mIse.startRecog(mParam, evaluatorListener);
                        }
                    }
                }
            }, 1000);
        } else {
//            tvSpeectevalTip.setText("测评君罢工了，程序员哥哥会尽快修复（" + resultEntity.getErrorNo() + "）");
            XESToastUtils.showToast(mContext, "测评君罢工了，程序员哥哥会尽快修复（" + resultEntity.getErrorNo() + "）");
            if (!mIse.isOfflineFail()) {
                if (speechEvaluatorInter instanceof TalSpeech) {
                    onLineError++;
                    if (onLineError == 1) {
                        if (!isEnd) {
                            mView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    errorSetGone();
                                    mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
                                    mParam.setStrEvaluator(content2);
                                    mParam.setLocalSavePath(saveVideoFile.getPath());
                                    mParam.setMultRef(false);
                                    mParam.setLearning_stage(learning_stage);
                                    mIse.startRecog(mParam, evaluatorListener);
//                                    speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(content2,
// saveVideoFile.getPath(), false, learning_stage, evaluatorListener);
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
            forceSubmit(resultEntity);
//            mView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
//                }
//            }, 1000);
        }
    }

    private void forceSubmit(ResultEntity resultEntity) {
        mLogtf.d("forceSubmit:haveAnswer=" + haveAnswer);
        if (haveAnswer) {
            speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
        } else {
            try {
                final JSONObject answers = new JSONObject();
                JSONObject answers1 = new JSONObject();
                entranceTime = System.currentTimeMillis() - entranceTime;
                answers1.put("entranceTime", (int) resultEntity.getSpeechDuration());
                answers1.put("score", 0);
                JSONObject detail = new JSONObject();
                detail.put("cont_score", 0);
                detail.put("level", 0);
                JSONArray nbestArray = new JSONArray();
                detail.put("nbest", nbestArray);
                detail.put("pron_score", 0);
                detail.put("total_score", 0);
                answers1.put("detail", detail);
                answers.put("1", answers1);
                String isSubmit = EngForceSubmit.getSubmit(isNewArts, true);
                speechEvalAction.sendSpeechEvalResult2(id, (VideoQuestionLiveEntity) baseVideoQuestionEntity, answers.toString(), isSubmit, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        final JSONObject jsonObject = (JSONObject) objData[0];
                        logger.d("sendSpeechEvalResult2:onSpeechEval:object=" + jsonObject);
                        speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        logger.d("sendSpeechEvalResult2:onPmFailure:msg=" + failMsg);
                        speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
                    }
                });
            } catch (JSONException e) {
                logger.e("sendSpeechEvalResult2", e);
            }
        }
    }

    private FrameAnimation createFromAees(String path, boolean isRepeat) {
        return FrameAnimation.createFromAees(mContext, ivLivevideoSpeectevalWave, path, 50, isRepeat);
    }

    /**
     * 在左下角显示自己分数
     *
     * @param resultEntity
     */
    private void onSpeechEvalSuccessMe(ResultEntity resultEntity) {
        if (lavLivevideoVoiceansTeamMine.getVisibility() == View.VISIBLE) {
            TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = (TeamOnCompositionLoadedListener)
                    lavLivevideoVoiceansTeamMine.getTag();
            teamOnCompositionLoadedListener.updateScore(mContext, lavLivevideoVoiceansTeamMine, "" + resultEntity
                    .getScore());
            return;
        }
        lavLivevideoVoiceansTeamMine.setVisibility(View.VISIBLE);
        ivLivevideoVoiceansTeamMine.setVisibility(View.VISIBLE);
        ivLivevideoVoiceansTeamMine.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener
                () {
            @Override
            public boolean onPreDraw() {
                ivLivevideoVoiceansTeamMine.getViewTreeObserver().removeOnPreDrawListener(this);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim
                        .anim_live_stand_speech_mine_light_rotate);
                LinearInterpolator lin = new LinearInterpolator();
                animation.setInterpolator(lin);
                ivLivevideoVoiceansTeamMine.startAnimation(animation);
                return false;
            }
        });
        String path = "live_stand/lottie/live_stand_voice_team_right.json";
        lavLivevideoVoiceansTeamMine.setImageAssetsFolder("live_stand/lottie/voice_answer/team_right");
        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
        student.setNickname(userName);
        student.setScore("" + resultEntity.getScore());
        student.setAvatar_path(headUrl);
        TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = new TeamOnCompositionLoadedListener
                (student, lavLivevideoVoiceansTeamMine);
        teamOnCompositionLoadedListener.isMe = true;
        LottieComposition.Factory.fromAssetFileName(mContext, path, teamOnCompositionLoadedListener);
        lavLivevideoVoiceansTeamMine.setTag(teamOnCompositionLoadedListener);
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
//                        logger.d( "onEvaluatorIng:point90_61=" + point90_6);
//                        if (point90_6.right - point90_6.left >= 5) {
//                            arrayList90_6.add(point90_6);
//                        }
//                    }
//                    point90_6 = null;
//                }
//                if (i == lstPhonemeScore.size() - 1) {
//                    if (point90_6 != null) {
//                        logger.d( "onEvaluatorIng:point90_62=" + point90_6);
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
//                        logger.d( "onEvaluatorIng:point90_31=" + point90_3);
//                        if (point90_3.right - point90_3.left >= 2 && point90_3.right - point90_3.left < 5) {
//                            arrayList90_3.add(point90_3);
//                        }
//                    }
//                    point90_3 = null;
//                }
//                if (i == lstPhonemeScore.size() - 1) {
//                    if (point90_3 != null) {
//                        logger.d( "onEvaluatorIng:point90_32=" + point90_3);
//                        if (point90_3.right - point90_3.left >= 2 && point90_3.right - point90_3.left < 5) {
//                            arrayList90_3.add(point90_3);
//                        }
//                    }
//                }
//            }
//            logger.d( "onResult:onEvaluatorIng:arrayList90_6=" + arrayList90_6.size() + ",arrayList90_3=" +
// arrayList90_3.size());
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
//            logger.d( "onEvaluatorIng:count90=" + count90 + ",point90WordArrayList=" + point90WordArrayList.size()
// + ",point90_6s=" + point90_6s.size() + ",point90_3s=" + point90_3s.size() + ",nbest=" + nbest);
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
//            tvSpeectevalTip.setText(count + "秒后自动提交");
//            XESToastUtils.showToast(mContext, count + "秒后自动提交");
            if (count > 0) {
                tvSpeectevalTip.postDelayed(this, 1000);
            } else {
                errorSetGone();
                //不在倒计时结束的时候停止录音了
//                if (mIse != null) {
//                    mIse.stop();
//                }
//                if (isSpeechError || isSpeechSuccess) {
//                    speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
//                }
            }
            count--;
        }
    };

    public void examSubmitAll() {
        isEnd = true;
        ViewGroup group = (ViewGroup) mView.getParent();
        mLogtf.d("examSubmitAll:mIse=" + (mIse != null) + ",Start=" + isSpeechStart + ",error=" + isSpeechError + ",Result=" + isSpeechResult + ",group=" + (group == null));
        if (group == null) {
            return;
        }
        if (!isSpeechStart || isSpeechError || isSpeechResult) {
            speechEvalAction.stopSpeech(StandSpeechAssAutoPager.this, getBaseVideoQuestionEntity(), id);
        } else {
//            errorSetVisible();
//            tvSpeectevalTip.setText(count + "秒后自动提交");
//            XESToastUtils.showToast(mContext, count + "秒后自动提交");
            //倒计时直接停止录音
            if (mIse != null) {
                mIse.stop();
            }
            handler.postDelayed(autoUploadRunnable, 1000);
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

        public TeamOnCompositionLoadedListener(GoldTeamStatus.Student student, LottieAnimationView
                lottieAnimationView) {
            this.student = student;
            this.lottieAnimationView = lottieAnimationView;
        }

        void updateName() {
            InputStream inputStream = null;
            try {
                if (isMe) {
                    inputStream = AssertUtil.open("live_stand/lottie/voice_answer/team_right/img_11.png");
                } else {
                    inputStream = AssertUtil.open("live_stand/lottie/voice_answer/team_right/img_1.png");
                }
                Bitmap headBack = BitmapFactory.decodeStream(inputStream);
                Bitmap creatBitmap = Bitmap.createBitmap(headBack.getWidth(), headBack.getHeight(), Bitmap.Config
                        .ARGB_8888);
                Canvas canvas = new Canvas(creatBitmap);
                canvas.drawBitmap(headBack, 0, 0, null);
                String name = student.getShowName();
                Paint paint = new Paint();
                paint.setTextSize(22);
                paint.setTypeface(fontFace);
                if (isMe) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(0xffA56202);
                }
                float width = paint.measureText(name);
                canvas.drawText(name, headBack.getWidth() / 2 - width / 2, headBack.getHeight() / 2 + paint
                        .measureText("a") / 2, paint);
                lottieAnimationView.updateBitmap("image_1", creatBitmap);
                headBack.recycle();
            } catch (Exception e) {
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
                ImageLoader.with(activity.getApplication()).load(student.getAvatar_path()).asCircle().asBitmap(new SingleConfig
                        .BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "updateHead", student
                                .getAvatar_path());
                        if (headBitmap == null) {
                            return;
                        }
                        InputStream inputStream = null;
                        try {
                            inputStream = AssertUtil.open
                                    ("live_stand/lottie/voice_answer/team_right/img_2.png");
                            Bitmap headBack = BitmapFactory.decodeStream(inputStream);
                            Bitmap creatBitmap = Bitmap.createBitmap(headBack.getWidth(), headBack.getHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(creatBitmap);
                            canvas.drawBitmap(headBack, 0, 0, null);

                            float scaleWidth = (float) (headBack.getWidth() - 10) / (float) headBitmap.getWidth();
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleWidth, scaleWidth);
                            Bitmap scalHeadBitmap = Bitmap.createBitmap(headBitmap, 0, 0, headBitmap.getWidth(),
                                    headBitmap.getHeight(), matrix, true);

                            int left = (creatBitmap.getWidth() - scalHeadBitmap.getWidth()) / 2;
                            int top = (creatBitmap.getHeight() - scalHeadBitmap.getHeight()) / 2;
                            canvas.drawBitmap(scalHeadBitmap, left, top, null);
                            scalHeadBitmap.recycle();
                            lottieAnimationView.updateBitmap("image_2", creatBitmap);
                            headBack.recycle();
                        } catch (Exception e) {
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
            Bitmap img7Bitmap;
            try {
                img7Bitmap = BitmapFactory.decodeStream(AssertUtil.open
                        ("live_stand/lottie/voice_answer/team_right/img_0.png"));
                Bitmap creatBitmap = Bitmap.createBitmap(img7Bitmap.getWidth(), img7Bitmap.getHeight(), Bitmap.Config
                        .ARGB_8888);
                Canvas canvas = new Canvas(creatBitmap);
                int textColor;
                if (isMe) {
                    textColor = 0xffF3057D;
                } else {
                    textColor = 0xffD05D5D;
                }
                float textSize = 30;
                int strokeColor = Color.WHITE;
                Bitmap textStrokeBitmap = TextStrokeUtil.createTextStroke(score, fontFace, textSize, textColor, 4,
                        strokeColor);
                int x = (creatBitmap.getWidth() - textStrokeBitmap.getWidth()) / 2;
                int y = (creatBitmap.getHeight() - textStrokeBitmap.getHeight()) / 2;
                canvas.drawBitmap(textStrokeBitmap, x, y, null);
                textStrokeBitmap.recycle();
                img7Bitmap.recycle();
                img7Bitmap = creatBitmap;
            } catch (IOException e) {
                logger.e("updateScore", e);
                return;
            }
            lottieAnimationView.updateBitmap("image_0", img7Bitmap);
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
//    一些提示
//    声音有点小，再来一次哦！
//    认真些，再来一次吧
//    麦克风不可用，快去检查一下
//    好像没网了，快检查一下
//    服务器连接不上，切换手动答题
//    语音输入有点小问题，先手动答题哦（" + resultEntity.getErrorNo() + ")");
//    认真些，\n再来一次吧(" + resultEntity.getCurStatus() + ")
//    提交失败，请重读
//    count + "秒后自动提交

}

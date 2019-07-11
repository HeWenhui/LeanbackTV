package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;

import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.LiveStandQuestionSwitch;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionSwitch;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.EngForceSubmit;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;
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
import java.util.List;
import java.util.Random;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * 站立直播-语音答题
 *
 * @author linyuqiang
 * @date 2017/12/5
 */
public class VoiceAnswerStandPager extends BaseVoiceAnswerPager {
    //    private SpeechEvaluatorUtils mIse;
    private SpeechUtils mIse;
    /** 所有帧动画 */
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    /** 组内战况已经被加入的学生 */
    private ArrayList<GoldTeamStatus.Student> addStudents = new ArrayList<>();
    /** 组内战况已经弹过 */
    private boolean teamStatus = false;
    /** 手动答题切换动画 */
    FrameAnimation switchFrameAnimation;
    /** readygo动画 */
    ReadyGoImageView rgivStandReadygo;
    /** 我的分数lottie动画 */
    LottieAnimationView lavLivevideoVoiceansTeamMine;
    /** 我的分数lottie动画背后光圈 */
    ImageView ivLivevideoVoiceansTeamMine;
    RelativeLayout rlLivevideoVoiceansVontent;
    /** 组内战况-左边 */
    LinearLayout llLivevideoVoiceansTeamLeft;
    /** 组内战况-右边 */
    LinearLayout llLivevideoVoiceansTeamRight;
    /** 错误提示 */
    RelativeLayout rlSpeectevalTip;
    /** 错误提示-文字 */
    TextView tvSpeectevalTip;
    /** 认真些，再来一次吧 */
    int errorTip1 = R.drawable.live_stand_answer_voice_caution_01;
    /** 声音有点小，再来一次哦！ */
    int errorTip2 = R.drawable.live_stand_answer_voice_caution_02;
    /** 波形 */
    ImageView ivLivevideoSpeectevalWave;
    /** 答题切换 */
    ImageView ivVoiceansSwitch;
    QuestionSwitch questionSwitch;
    /** 语音保存位置-目录 */
    File dir;
    /** 语音保存位置 */
    private File saveVideoFile;
    /** 是不是评测失败 */
    private boolean isSpeechError = false;
    private boolean isSpeechSuccess = false;
    /** 评测文本 */
    JSONObject assess_ref;
    String answer;
    boolean multRef = true;
    /**
     * 是不是结束答题
     */
    boolean isEnd = false;
    String endnonce;
    /**
     * 是不是用户返回
     */
    boolean userBack = false;
    /**
     * 是不是用户切换答题
     */
    boolean userSwitch = false;
    /** 是不是新课件 */
    private final boolean isNewArts;
    String type;
    ScoreAndIndex lastMaxScoreAndIndex = null;
    int netWorkType = NetWorkHelper.WIFI_STATE;
    Typeface fontFace;
    private String headUrl;
    private String userName;
    private long entranceTime;
    String file1 = "live_stand/frame_anim/voice_answer/1_enter";
    String file2 = "live_stand/frame_anim/voice_answer/2_loop";
    String file3 = "live_stand/frame_anim/voice_answer/3_switch_loop";
    String file4 = "live_stand/frame_anim/voice_answer/4_switch";
    LiveSoundPool liveSoundPool;
    /** 当前答题结果 */
    private AnswerResultEntity mAnswerReulst;
    private VideoQuestionLiveEntity mDetail;

    public VoiceAnswerStandPager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity, JSONObject assess_ref, String type, QuestionSwitch questionSwitch, String headUrl, String userName) {
        super(context);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
        this.mDetail = baseVideoQuestionEntity;
        isNewArts = mDetail.isNewArtsH5Courseware();
        this.questionSwitch = questionSwitch;
        this.type = type;
        this.assess_ref = assess_ref;
        this.headUrl = headUrl;
        this.userName = StandLiveTextView.getShortName(userName);
        if (LiveVideoConfig.isNewArts) {
            try {
                answer = assess_ref.getJSONArray("answer").getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
                try {
                    answer = assess_ref.getJSONArray("answer").getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONArray array = assess_ref.getJSONArray("options");
                    answer = array.getJSONObject(0).getJSONArray("content").getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mLogtf.d("VoiceAnswerPager:answer=" + answer);
        initListener();
        initData();
    }

    @Override
    public BaseVideoQuestionEntity getBaseVideoQuestionEntity() {
        return baseVideoQuestionEntity;
    }

    @Override
    public void setAudioRequest() {
        mLogtf.d("setAudioRequest:mIse=" + (mIse == null));
        mView.post(new Runnable() {
            @Override
            public void run() {
//                if (mIse == null) {
//                    mIse = new SpeechEvaluatorUtils(true);
//                }
                if (mIse == null) {
                    mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                    mIse.prepar();
                }
                rgivStandReadygo.setAnimationListener(new FrameAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        ViewGroup group = (ViewGroup) rgivStandReadygo.getParent();
                        if (group != null) {
                            group.removeView(rgivStandReadygo);
                        }
                        rgivStandReadygo.destory();
                        rlLivevideoVoiceansVontent.setVisibility(View.VISIBLE);
                        afterReadGo();
                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                rgivStandReadygo.start(liveSoundPool);
            }
        });
    }

    /**
     * readygo 以后。检查权限
     */
    private void afterReadGo() {
        boolean have = XesPermission.checkPermission(mContext, new LiveActivityPermissionCallback() {

            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {
                isSpeechError = true;
                VoiceAnswerStandLog.sno3(VoiceAnswerStandPager.this, baseVideoQuestionEntity.getvQuestionID(), false);
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
        mView.post(new Runnable() {
            @Override
            public void run() {
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
        });
        VoiceAnswerStandLog.sno3(VoiceAnswerStandPager.this, baseVideoQuestionEntity.getvQuestionID(), true);
        startEvaluator();
        switchFrameAnimation =
                FrameAnimation.createFromAees(mContext, ivVoiceansSwitch, file3, 50, true);
        frameAnimations.add(switchFrameAnimation);
        if (questionSwitch instanceof LiveStandQuestionSwitch) {
            final LiveStandQuestionSwitch liveStandQuestionSwitch = (LiveStandQuestionSwitch) questionSwitch;
            mView.postDelayed(new Runnable() {
                Runnable r = this;
                Random random = new Random();
                int leftOrRight = 0;
                ArrayList<View> leftView = new ArrayList<>();
                ArrayList<View> rightView = new ArrayList<>();

                @Override
                public void run() {
                    liveStandQuestionSwitch.getTestAnswerTeamStatus(baseVideoQuestionEntity, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            GoldTeamStatus entity = (GoldTeamStatus) objData[0];
                            ArrayList<GoldTeamStatus.Student> students = entity.getStudents();
                            if (!teamStatus && !students.isEmpty()) {
                                teamStatus = true;
                                VoiceAnswerStandLog.sno4(VoiceAnswerStandPager.this, baseVideoQuestionEntity.getvQuestionID());
                            }
                            long delayMillis = liveStandQuestionSwitch.getRequestTime();
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
                                        final LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
                                        String path;
                                        if (student.isRight()) {
                                            path = "live_stand/lottie/live_stand_voice_team_right.json";
                                            lottieAnimationView.setImageAssetsFolder("live_stand/lottie/voice_answer/team_right");
                                        } else {
                                            path = "live_stand/lottie/live_stand_voice_team_wrong.json";
                                            lottieAnimationView.setImageAssetsFolder("live_stand/lottie/voice_answer/team_wrong");
                                        }
                                        LottieComposition.Factory.fromAssetFileName(mContext, path, new TeamOnCompositionLoadedListener(student, lottieAnimationView));
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                lp.weight = 1;
                                        int countLeft = llLivevideoVoiceansTeamLeft.getChildCount();
                                        int countRight = llLivevideoVoiceansTeamRight.getChildCount();
                                        if (leftOrRight % 2 == 1) {
                                            boolean remove = false;
                                            int index = 0;
                                            if (countRight > 0) {
                                                int rightWidth = ((View) llLivevideoVoiceansTeamRight.getParent()).getWidth();
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
                                                int leftWidth = ((View) llLivevideoVoiceansTeamLeft.getParent()).getWidth();
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

    public void setIse(SpeechUtils ise) {
        this.mIse = ise;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_stand_voice_answer, null);
        rgivStandReadygo = view.findViewById(R.id.rgiv_livevideo_stand_readygo);
        lavLivevideoVoiceansTeamMine = view.findViewById(R.id.lav_livevideo_voiceans_team_mine);
        ivLivevideoVoiceansTeamMine = view.findViewById(R.id.iv_livevideo_voiceans_team_mine);
        rlLivevideoVoiceansVontent = view.findViewById(R.id.rl_livevideo_voiceans_content);
        llLivevideoVoiceansTeamLeft = view.findViewById(R.id.ll_livevideo_voiceans_team_left);
        llLivevideoVoiceansTeamRight = view.findViewById(R.id.ll_livevideo_voiceans_team_right);
        rlSpeectevalTip = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_tip);
        tvSpeectevalTip = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_tip);
        ivLivevideoSpeectevalWave = view.findViewById(R.id.iv_livevideo_speecteval_wave);
        ivVoiceansSwitch = view.findViewById(R.id.iv_livevideo_voiceans_switch);
//        tvSpeectevalTip.setText("语音输入有点小问题，\n先手动答题哦（1131)");
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            long before;

            @Override
            public void onViewAttachedToWindow(View view) {
                logger.d("onViewAttachedToWindow");
                before = System.currentTimeMillis();
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                logger.d("onViewDetachedFromWindow:frameAnimations=" + frameAnimations.size());
                for (int i = 0; i < frameAnimations.size(); i++) {
                    FrameAnimation animation = frameAnimations.get(i);
                    int destory = animation.destory();
                    logger.d("onViewDetachedFromWindow:animation=" + animation.path + ",destory=" + destory);
                }
                if (liveSoundPool != null) {
                    liveSoundPool.release();
                }
                mLogtf.d("onViewDetachedFromWindow:time=" + (System.currentTimeMillis() - before));
            }
        });
        return view;
    }

    @Override
    public void initListener() {
        ivVoiceansSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVoiceansSwitch.setClickable(false);
                StandLiveMethod.onClickVoice(liveSoundPool);
                VoiceAnswerStandLog.sno7(VoiceAnswerStandPager.this, baseVideoQuestionEntity.getvQuestionID(), "" + (System.currentTimeMillis() - entranceTime) / 1000);
                FrameAnimation frameAnimation1 =
                        FrameAnimation.createFromAees(mContext, v, file4, 50, false);
                if (frameAnimation1 != null) {
                    frameAnimations.add(frameAnimation1);
                    frameAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
                        @Override
                        public void onAnimationStart() {
                            switchFrameAnimation.destory();
                        }

                        @Override
                        public void onAnimationEnd() {
                            boolean switchQuestion = switchQuestion("onAnimationEnd");
                            if (!switchQuestion) {
                                ivVoiceansSwitch.setClickable(true);
                            }
                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });
                } else {
                    switchQuestion("Animation");
                }
            }
        });
//        tvVoiceansTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mIse != null) {
//                    startEvaluator();
//                }
//            }
//        });
    }

    private FrameAnimation createFromAees(String path, boolean isRepeat) {
        FrameAnimation btframeAnimation1 = FrameAnimation.createFromAees(mContext, ivLivevideoSpeectevalWave, path, 50, isRepeat);
        return btframeAnimation1;
    }

    private boolean switchQuestion(String method) {
        mLogtf.d("switchQuestion:method=" + method + ",isEnd=" + isEnd);
        if (isEnd) {
            return false;
        }
        BasePager basePager = questionSwitch.questionSwitch(this, baseVideoQuestionEntity);
        if (basePager == null) {
            XESToastUtils.showToast(mContext, "切换失败");
            return false;
        } else {
            userSwitch = true;
            if (mIse != null) {
                mIse.stop();
            }
            return true;
        }
    }

    @Override
    public void initData() {
        entranceTime = System.currentTimeMillis();
        final String questionID = baseVideoQuestionEntity.getvQuestionID();
        logger.d("initData:questionID=" + questionID);
        fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
        dir = LiveCacheFile.geCacheFile(mContext, "livevoice");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        liveSoundPool = LiveSoundPool.createSoundPool();
        //        questionSwitch.getQuestion(baseVideoQuestionEntity, new QuestionSwitch.OnQuestionGet() {
//            @Override
//            public void onQuestionGet(BaseVideoQuestionEntity baseQuestionEntity) {
//                mView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        XESToastUtils.showToast(mContext, "得到试题");
//                        logger.d( "initData:audioRequest=" + audioRequest);
//                        quesRequest = true;
//                        if (audioRequest) {
//                            mIse = new SpeechEvaluatorUtils(mContext);
//                            mIse.startEnglishEvaluatorOffline(assess_ref.toString(), saveVideoFile.getPath(), multRef, listener);
//                        }
//                    }
//                });
//            }
//        });
    }

    @Override
    public void stopPlayer() {
        if (mIse != null) {
            mIse.stop();
        }
    }

    int count = 3;
    private Runnable autoUploadRunnable = new Runnable() {
        @Override
        public void run() {
//            tvSpeectevalTip.setText(count + "秒后自动提交");
//            XESToastUtils.showToast(mContext, count + "秒后自动提交");
            if (count > 0) {
                tvSpeectevalTip.setTag("200");
                tvSpeectevalTip.postDelayed(this, 1000);
            } else {
                tvSpeectevalTip.setTag("0");
                errorSetGone();
                if (mIse != null) {
                    mIse.stop();
                }
                if (isSpeechError || isSpeechSuccess) {
                    questionSwitch.stopSpeech(VoiceAnswerStandPager.this, baseVideoQuestionEntity);
                }
            }
            count--;
        }
    };

    @Override
    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public void setEnd() {
        isEnd = true;
        mLogtf.d("setEnd");
    }

    @Override
    public void examSubmitAll(String method, String nonce) {
        isEnd = true;
        endnonce = nonce;
        ViewGroup group = (ViewGroup) mView.getParent();
        logger.d("examSubmitAll:method=" + method + ",group=" + (group == null) + ",error=" + isSpeechError + ",success=" + isSpeechSuccess);
        if (isSpeechError || isSpeechSuccess) {
            questionSwitch.stopSpeech(VoiceAnswerStandPager.this, baseVideoQuestionEntity);
        } else {
//            errorSetVisible();
//            tvSpeectevalTip.setText(count + "秒后自动提交");
//            tvSpeectevalTip.setTag("200");
//            XESToastUtils.showToast(mContext, count + "秒后自动提交");
            tvSpeectevalTip.postDelayed(autoUploadRunnable, 1000);
            count--;
        }
    }

    @Override
    public void onUserBack() {
        userBack = true;
        if (mIse != null) {
            mIse.stop();
        }
    }

    class VoiceEvaluatorListener implements EvaluatorListener {
        File saveVideoFile;

        @Override
        public void onBeginOfSpeech() {
            isSpeechError = false;
            logger.d("onBeginOfSpeech");
        }

        @Override
        public void onResult(ResultEntity resultEntity) {
            logger.d("onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo() + ",isEnd=" + isEnd);
            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                onEvaluatorSuccess(resultEntity);
            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                onEvaluatorError(resultEntity);
            }
        }

        @Override
        public void onVolumeUpdate(int volume) {

        }
    }
//    class VoiceEvaluatorListener implements EvaluatorListener {
//        File saveVideoFile;
//
//        @Override
//        public void onBeginOfSpeech() {
//            isSpeechError = false;
//            logger.d( "onBeginOfSpeech");
//        }
//
//        @Override
//        public void onResult(ResultEntity resultEntity) {
//            logger.d( "onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo() + ",isEnd=" + isEnd);
//            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
//                onEvaluatorSuccess(resultEntity);
//            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
//                onEvaluatorError(resultEntity);
//            }
//        }
//
//        @Override
//        public void onVolumeUpdate(int volume) {
//        }
//    }

    VoiceEvaluatorListener listener = new VoiceEvaluatorListener();

    private void onEvaluatorError(final ResultEntity resultEntity) {
        mLogtf.d("onEvaluatorError:userSwitch=" + userSwitch + ",userBack=" + userBack + ",isEnd=" + isEnd + ",errorNo=" + resultEntity.getErrorNo());
        isSpeechError = true;
        if (userSwitch || userBack) {
            return;
        }
        if (isEnd) {
            //语音答题也强制提交
            //LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_VOICE.equals(type)
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_VOICE.equals(type)) {
                submitQuestionSelect("", false, resultEntity.getSpeechDuration());
            } else {
                try {
                    JSONArray options = assess_ref.getJSONArray("options");
                    JSONObject jsonObject = options.getJSONObject(0);
                    JSONArray content1 = jsonObject.getJSONArray("content");
                    submitQuestionBlack(content1.getString(0), "", false, resultEntity.getSpeechDuration());
                } catch (Exception e) {
                    mLogtf.e("onEvaluatorError:submitQuestionBlack", e);
                }
            }
//            VideoResultEntity entity = new VideoResultEntity();
//            if (LiveVideoConfig.isNewArts) {
//                entity.setResultType(0);
//            } else {
//                entity.setResultType(VideoResultEntity.QUE_RES_TYPE2);
//            }
//            entity.setStandardAnswer(answer);
//            questionSwitch.onAnswerTimeOutError(baseVideoQuestionEntity, entity);
//            mView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    questionSwitch.stopSpeech(VoiceAnswerStandPager.this, baseVideoQuestionEntity);
//                }
//            }, 3000);
            return;
        }
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode
                .MUTE) {
            errorSetVisible();
//            tvSpeectevalTip.setText("声音有点小，\n再来一次哦！");
            tvSpeectevalTip.setBackgroundResource(errorTip2);
//            tvSpeectevalTip.setTag("2");
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEvaluator();
                }
            }, 300);
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    rlSpeectevalTip.setVisibility(View.GONE);
                    rlSpeectevalTipGone();
                }
            }, 1500);
            return;
        } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
//            errorSetVisible();
//            tvSpeectevalTip.setText("麦克风不可用，\n快去检查一下");
//            tvSpeectevalTip.setTag("3");
            XESToastUtils.showToast(mContext, "麦克风不可用，\n快去检查一下");
//            tvSpeectevalError.setText("好像没网了，快检查一下");
//            mView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    rlSpeectevalTip.setVisibility(View.GONE);
//                    switchQuestion();
//                }
//            }, 1500);
        } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() == ResultCode.NETWORK_FAIL
                || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
            int netWorkType = NetWorkHelper.getNetWorkState(mContext);
            if (netWorkType == NetWorkHelper.NO_NETWORK) {
//                errorSetVisible();
//                tvSpeectevalTip.setText("好像没网了，\n快检查一下");
//                tvSpeectevalTip.setTag("100");
                XESToastUtils.showToast(mContext, "好像没网了，\n快检查一下");
            } else {
//                errorSetVisible();
//                tvSpeectevalTip.setText("服务器连接不上，\n切换手动答题");
//                tvSpeectevalTip.setTag("4");
                XESToastUtils.showToast(mContext, "服务器连接不上，\n切换手动答题");
//            tvSpeectevalError.setText("好像没网了，快检查一下");
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        rlSpeectevalTip.setVisibility(View.GONE);
                        rlSpeectevalTipGone();
                        switchQuestion("NO_NETWORK");
                    }
                }, 1500);
            }
        } else if (resultEntity.getErrorNo() == ResultCode.SPEECH_CANCLE) {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEvaluator();
                }
            }, 1000);
        } else {
//            errorSetVisible();
//            tvSpeectevalTip.setText("语音输入有点小问题，\n先手动答题哦（" + resultEntity.getErrorNo() + ")");
//            tvSpeectevalTip.setTag("5");
            XESToastUtils.showToast(mContext, "语音输入有点小问题，\n先手动答题哦（" + resultEntity.getErrorNo() + ")");
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchQuestion("Other");
                }
            }, 1500);
        }
    }

    private void onEvaluatorSuccess(final ResultEntity resultEntity) {
        if (userSwitch || userBack) {
            return;
        }
        List<PhoneScore> phoneScores = resultEntity.getLstPhonemeScore();
        int[] scores = resultEntity.getScores();
        if (phoneScores.isEmpty()) {
            logger.d("onResult(SUCCESS):phoneScores.isEmpty");
        } else {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_VOICE.equals(type)) {
                int rightIndex = -1;
                int rightCount = 0;
                String sss = "";
                for (int i = 0; i < phoneScores.size(); i++) {
                    PhoneScore phoneScore = phoneScores.get(i);
                    if (phoneScore.getScore() == 1) {
                        if (rightIndex == -1) {
                            rightIndex = i;
                        }
                        sss += phoneScore.getScore() + ",";
                        rightCount++;
                    }
                }
                logger.d("onResult(SUCCESS):scores=" + sss + ",rightIndex=" + rightIndex + ",rightCount=" + rightCount + ",isEnd=" + isEnd);
                if (rightCount > 1) {
                    errorSetVisible();
//                    tvSpeectevalTip.setText("认真些，\n再来一次吧（" + resultEntity.getCurStatus() + ")");
                    tvSpeectevalTip.setBackgroundResource(errorTip1);
                    tvSpeectevalTip.setTag("6");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            rlSpeectevalTip.setVisibility(View.GONE);
                            rlSpeectevalTipGone();
                        }
                    }, 1500);
                    logger.d("onResult(SUCCESS):more");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startEvaluator();
                        }
                    }, 200);
                } else if (rightCount == 1) {
                    try {
                        JSONArray options = assess_ref.getJSONArray("options");
                        JSONObject jsonObject = options.getJSONObject(rightIndex);
                        final String option = jsonObject.optString("option");
//                            XESToastUtils.showToast(mContext, "你的答案" + option);
                        ivVoiceansSwitch.setVisibility(View.GONE);
                        questionSwitch.uploadVoiceFile(saveVideoFile);
                        isSpeechSuccess = true;
                        boolean isRight = option.equalsIgnoreCase(answer);
                        submitQuestionSelect(option, isRight, resultEntity.getSpeechDuration());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isEnd) {
                        submitQuestionSelect("", false, resultEntity.getSpeechDuration());
                    } else {
//                        XESToastUtils.showToast(mContext, "重读");
                        errorSetVisible();
//                        tvSpeectevalTip.setText("认真些，\n再来一次吧(" + resultEntity.getCurStatus() + ")");
                        tvSpeectevalTip.setBackgroundResource(errorTip1);
                        tvSpeectevalTip.setTag("8");
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rlSpeectevalTipGone();
                            }
                        }, 1500);
                        logger.d("onResult(SUCCESS):reread");
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startEvaluator();
                            }
                        }, 200);
                    }
                }
            } else {
                int score = phoneScores.get(0).getScore();
                boolean isRight = score > 0;
                logger.d("onResult(SUCCESS):score=" + score);
                if (!isEnd && !isRight && resultEntity.getCurStatus() == 5) {
                    errorSetVisible();
//                    tvSpeectevalTip.setText("认真些，\n再来一次吧");
                    tvSpeectevalTip.setBackgroundResource(errorTip1);
                    tvSpeectevalTip.setTag("9");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rlSpeectevalTipGone();
                        }
                    }, 1500);
                    logger.d("onResult(SUCCESS):reread");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startEvaluator();
                        }
                    }, 200);
                    return;
                }
                String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                try {
                    JSONArray options = assess_ref.getJSONArray("options");
                    JSONObject jsonObject = options.getJSONObject(0);
                    JSONArray content1 = jsonObject.getJSONArray("content");
//                    XESToastUtils.showToast(mContext, "你的答案" + answer);
                    ivVoiceansSwitch.setVisibility(View.GONE);
                    questionSwitch.uploadVoiceFile(saveVideoFile);
                    isSpeechSuccess = true;
                    submitQuestionBlack(content1.getString(0), content1.getString(0), isRight, resultEntity.getSpeechDuration());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void submitQuestionSelect(final String option, boolean isRight, final double speechDuration) {
        String isSubmit = EngForceSubmit.getSubmit(isNewArts, isEnd);
        questionSwitch.onPutQuestionResult(this, baseVideoQuestionEntity, answer, option, 1, isRight, speechDuration, isSubmit, new QuestionSwitch.OnAnswerReslut() {
            @Override
            public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                if (entity != null) {
                    entity.setYourAnswer(option);
                    entity.setStandardAnswer(answer);
                    ivLivevideoSpeectevalWave.setVisibility(View.INVISIBLE);
                    onCommit(entity, speechDuration);
                }
            }

            @Override
            public void onAnswerFailure() {
                isSpeechSuccess = false;
                if (isEnd) {
                    questionSwitch.stopSpeech(VoiceAnswerStandPager.this, baseVideoQuestionEntity);
                } else {
                    ivVoiceansSwitch.setVisibility(View.VISIBLE);
//                                    errorSetVisible();
//                                    tvSpeectevalTip.setText("提交失败，请重读");
//                                    tvSpeectevalTip.setTag("7");
                    XESToastUtils.showToast(mContext, "提交失败，请重读");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rlSpeectevalTipGone();
                        }
                    }, 1500);
                    logger.d("onResult(SUCCESS):onAnswerFailure");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startEvaluator();
                        }
                    }, 200);
                }
            }
        });
    }

    private void submitQuestionBlack(final String answer, final String result, boolean isRight, final double speechDuration) {
        String isSubmit = EngForceSubmit.getSubmit(isNewArts, isEnd);
        questionSwitch.onPutQuestionResult(this, baseVideoQuestionEntity, answer, result, 1, isRight, speechDuration, isSubmit, new QuestionSwitch.OnAnswerReslut() {
            @Override
            public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                if (entity != null) {
                    entity.setStandardAnswer(answer);
                    ivLivevideoSpeectevalWave.setVisibility(View.INVISIBLE);
                    onCommit(entity, speechDuration);
                }
            }

            @Override
            public void onAnswerFailure() {
                isSpeechSuccess = false;
                if (isEnd) {
                    questionSwitch.stopSpeech(VoiceAnswerStandPager.this, baseVideoQuestionEntity);
                } else {
                    ivVoiceansSwitch.setVisibility(View.VISIBLE);
                    XESToastUtils.showToast(mContext, "提交失败，请重读");
                    logger.d("onResult(SUCCESS):onAnswerFailure");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startEvaluator();
                        }
                    }, 200);
                }
            }
        });
    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            if ("100".equals(tvSpeectevalTip.getTag())) {
                rlSpeectevalTipGone();
                startEvaluator();
            }
        }
    }

    private void rlSpeectevalTipGone() {
        if (!"200".equals(tvSpeectevalTip.getTag())) {
            errorSetGone();
        }
    }

    private void startEvaluator() {
        if (isEnd) {
            ViewGroup group = (ViewGroup) mView.getParent();
            mLogtf.d("examSubmitAll:group=" + group);
            if (group == null) {
                return;
            }
            questionSwitch.stopSpeech(VoiceAnswerStandPager.this, baseVideoQuestionEntity);
            return;
        }
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        listener.saveVideoFile = saveVideoFile;
        SpeechParamEntity param = new SpeechParamEntity();
        param.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        param.setLocalSavePath(saveVideoFile.getPath());
        param.setStrEvaluator(assess_ref.toString());
        param.setMultRef(multRef);
        mIse.startRecog(param, listener);
//        mIse.startEnglishEvaluatorOffline(assess_ref.toString(), saveVideoFile.getPath(), multRef, listener);
    }

    private void errorSetVisible() {
        rlSpeectevalTip.setVisibility(View.VISIBLE);
    }

    private void errorSetGone() {
        rlSpeectevalTip.setVisibility(View.GONE);
    }

    static class ScoreAndIndex {
        int score;
        int index;

        @Override
        public String toString() {
            return "" + score;
        }
    }

    private void onCommit(VideoResultEntity entity, double speechDuration) {
        boolean isRight;
        if (LiveVideoConfig.isNewArts) {
            if (entity.getResultType() == 2) {
                isRight = true;
            } else {
                isRight = false;
            }
        } else {
            if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
                isRight = true;
            } else {
                isRight = false;
            }
        }

        if (lavLivevideoVoiceansTeamMine.getVisibility() == View.VISIBLE) {
            TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = (TeamOnCompositionLoadedListener) lavLivevideoVoiceansTeamMine.getTag();
            return;
        }
        lavLivevideoVoiceansTeamMine.setVisibility(View.VISIBLE);
        String path;
        if (isRight) {
            path = "live_stand/lottie/live_stand_voice_team_right.json";
            lavLivevideoVoiceansTeamMine.setImageAssetsFolder("live_stand/lottie/voice_answer/team_right");
        } else {
            path = "live_stand/lottie/live_stand_voice_team_wrong.json";
            lavLivevideoVoiceansTeamMine.setImageAssetsFolder("live_stand/lottie/voice_answer/team_wrong");
        }
        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
        student.setNickname(userName);
        student.setAvatar_path(headUrl);
        TeamOnCompositionLoadedListener teamOnCompositionLoadedListener = new TeamOnCompositionLoadedListener(student, lavLivevideoVoiceansTeamMine) {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                super.onCompositionLoaded(lottieComposition);
                if (lottieComposition != null) {
                    ivLivevideoVoiceansTeamMine.setVisibility(View.VISIBLE);
                    ivLivevideoVoiceansTeamMine.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ivLivevideoVoiceansTeamMine.getViewTreeObserver().removeOnPreDrawListener(this);
                            ivLivevideoVoiceansTeamMine.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_live_stand_speech_mine_light_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    animation.setInterpolator(lin);
                                    ivLivevideoVoiceansTeamMine.startAnimation(animation);
                                }
                            }, 300);
                            return false;
                        }
                    });
                }
            }
        };
        teamOnCompositionLoadedListener.isMe = true;
        LottieComposition.Factory.fromAssetFileName(mContext, path, teamOnCompositionLoadedListener);
        lavLivevideoVoiceansTeamMine.setTag(teamOnCompositionLoadedListener);
        VoiceAnswerStandLog.sno5(VoiceAnswerStandPager.this, baseVideoQuestionEntity.getvQuestionID(), isEnd ? "endPublish" : "autoSubmit", entity.getGoldNum(), isRight, speechDuration);
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
                    inputStream = AssertUtil.open("live_stand/lottie/voice_answer/team_right/img_11.png");
                } else {
                    inputStream = AssertUtil.open("live_stand/lottie/voice_answer/team_right/img_1.png");
                }
                Bitmap headBack = BitmapFactory.decodeStream(inputStream);
                Bitmap creatBitmap = Bitmap.createBitmap(headBack.getWidth(), headBack.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(creatBitmap);
                canvas.drawBitmap(headBack, 0, 0, null);
                String name = student.getShowName();
                Paint paint = new Paint();
                paint.setTextSize(22);
                if (isMe) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(0xffA56202);
                }
                float width = paint.measureText(name);
                canvas.drawText(name, headBack.getWidth() / 2 - width / 2, headBack.getHeight() / 2 + paint.measureText("a") / 2, paint);
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
                ImageLoader.with(mContext).load(student.getAvatar_path()).asCircle().asBitmap(new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "updateHead", student.getAvatar_path());
                        InputStream inputStream = null;
                        try {
                            inputStream = AssertUtil.open("live_stand/lottie/voice_answer/team_right/img_2.png");
                            Bitmap headBack = BitmapFactory.decodeStream(inputStream);
                            Bitmap creatBitmap = Bitmap.createBitmap(headBack.getWidth(), headBack.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(creatBitmap);
                            canvas.drawBitmap(headBack, 0, 0, null);

                            float scaleWidth = (float) (headBack.getWidth() - 10) / (float) headBitmap.getWidth();
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleWidth, scaleWidth);
                            Bitmap scalHeadBitmap = Bitmap.createBitmap(headBitmap, 0, 0, headBitmap.getWidth(), headBitmap.getHeight(), matrix, true);

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

        @Override
        public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
            if (lottieComposition == null) {
                return;
            }
            lottieAnimationView.setComposition(lottieComposition);
            lottieAnimationView.playAnimation();
            updateHead();
            updateName();
        }
    }

//    private void onEvaluatorSuccess2(final ResultEntity resultEntity) {
//        if (userSwitch || userBack) {
//            return;
//        }
//        int[] scores = resultEntity.getScores();
//        if (scores == null) {
//            logger.d( "onResult(SUCCESS):scores.null");
//        } else {
//            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
//                String sss = "";
//                ScoreAndIndex maxScoreAndIndex = null;
//                ScoreAndIndex secScoreAndIndex = null;
//                for (int i = 0; i < scores.length; i++) {
//                    int score = scores[i];
//                    sss += score + ",";
//                    if (score >= 70) {
//                        ScoreAndIndex scoreAndIndex = new ScoreAndIndex();
//                        scoreAndIndex.score = score;
//                        scoreAndIndex.index = i;
//                        if (maxScoreAndIndex == null) {
//                            maxScoreAndIndex = scoreAndIndex;
//                        } else {
//                            if (maxScoreAndIndex.score < score) {
//                                secScoreAndIndex = maxScoreAndIndex;
//                                maxScoreAndIndex = scoreAndIndex;
//                            } else {
//                                if (secScoreAndIndex == null) {
//                                    secScoreAndIndex = scoreAndIndex;
//                                } else if (secScoreAndIndex.score < score) {
//                                    secScoreAndIndex = scoreAndIndex;
//                                }
//                            }
//                        }
//                    }
//                }
//                logger.d( "onResult(SUCCESS):scores=" + sss + ",max=" + maxScoreAndIndex + ",sec=" + secScoreAndIndex + ",isEnd=" + isEnd);
//                if (maxScoreAndIndex != null) {
//                    boolean isAnswer = false;
//                    if (secScoreAndIndex == null) {
//                        isAnswer = true;
//                    } else {
//                        if (lastMaxScoreAndIndex != null) {
//                            isAnswer = true;
//                        } else {
//                            if (maxScoreAndIndex.score - secScoreAndIndex.score >= 10) {
//                                isAnswer = true;
//                            } else {
//                                lastMaxScoreAndIndex = maxScoreAndIndex;
//                                if (isEnd) {
//                                    isAnswer = true;
//                                } else {
////                                XESToastUtils.showToast(mContext, "你的答案多读");
//                                    rlSpeectevalTip.setVisibility(View.VISIBLE);
//                                    tvSpeectevalTip.setText("要认真作答哦，\n赶紧再来一次！");
//                                    tvSpeectevalTip.setTag("6");
//                                    mView.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            rlSpeectevalTip.setVisibility(View.GONE);
//                                        }
//                                    }, 1500);
//                                    logger.d( "onResult(SUCCESS):more");
//                                    mView.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            startEvaluator();
//                                        }
//                                    }, 200);
//                                }
//                            }
//                        }
//                    }
//                    if (isAnswer) {
//                        try {
//                            JSONArray options = assess_ref.getJSONArray("options");
//                            JSONObject jsonObject = options.getJSONObject(maxScoreAndIndex.index);
//                            String option = jsonObject.optString("option");
////                            XESToastUtils.showToast(mContext, "你的答案" + option);
//                            tvVoiceansSwitch.setVisibility(View.GONE);
//                            questionSwitch.uploadVoiceFile(saveVideoFile);
//                            isSpeechSuccess = true;
//                            questionSwitch.onPutQuestionResult(baseVideoQuestionEntity, answer, option, maxScoreAndIndex.score, false, resultEntity.getSpeechDuration(), isEnd ? "1" : "0", new QuestionSwitch.OnAnswerReslut() {
//                                @Override
//                                public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
//
//                                }
//
//                                @Override
//                                public void onAnswerFailure() {
//                                    isSpeechSuccess = false;
//                                    if (isEnd) {
//                                        questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
//                                    } else {
//                                        tvVoiceansSwitch.setVisibility(View.VISIBLE);
////                                        XESToastUtils.showToast(mContext, "提交失败，请重读");
//                                        rlSpeectevalTip.setVisibility(View.VISIBLE);
//                                        tvSpeectevalTip.setText("提交失败，请重读");
//                                        tvSpeectevalTip.setTag("7");
//                                        mView.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                rlSpeectevalTip.setVisibility(View.GONE);
//                                            }
//                                        }, 1500);
//                                        logger.d( "onResult(SUCCESS):onAnswerFailure");
//                                        mView.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                startEvaluator();
//                                            }
//                                        }, 200);
//                                    }
//                                }
//                            });
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    if (isEnd) {
//                        questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
//                    } else {
////                        XESToastUtils.showToast(mContext, "重读");
//                        rlSpeectevalTip.setVisibility(View.VISIBLE);
//                        tvSpeectevalTip.setText("要认真作答哦，\n赶紧再来一次");
//                        tvSpeectevalTip.setTag("8");
//                        mView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                rlSpeectevalTip.setVisibility(View.GONE);
//                            }
//                        }, 1500);
//                        logger.d( "onResult(SUCCESS):reread");
//                        mView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startEvaluator();
//                            }
//                        }, 200);
//                    }
//                }
//            } else {
//                int score = scores[0];
//                logger.d( "onResult(SUCCESS):score=" + score);
//                try {
//                    JSONArray options = assess_ref.getJSONArray("options");
//                    JSONObject jsonObject = options.getJSONObject(0);
//                    JSONArray content1 = jsonObject.getJSONArray("content");
////                    XESToastUtils.showToast(mContext, "你的答案" + answer);
//                    tvVoiceansSwitch.setVisibility(View.GONE);
//                    questionSwitch.uploadVoiceFile(saveVideoFile);
//                    isSpeechSuccess = true;
//                    questionSwitch.onPutQuestionResult(baseVideoQuestionEntity, content1.getString(0), content1.getString(0), score, false, resultEntity.getSpeechDuration(), isEnd ? "1" : "0", new QuestionSwitch.OnAnswerReslut() {
//                        @Override
//                        public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
//
//                        }
//
//                        @Override
//                        public void onAnswerFailure() {
//                            isSpeechSuccess = false;
//                            if (isEnd) {
//                                questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
//                            } else {
//                                tvVoiceansSwitch.setVisibility(View.VISIBLE);
//                                XESToastUtils.showToast(mContext, "提交失败，请重读");
//                                logger.d( "onResult(SUCCESS):onAnswerFailure");
//                                mView.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        startEvaluator();
//                                    }
//                                }, 200);
//                            }
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//    一些提示
//    count + "秒后自动提交");
//    声音有点小，再来一次哦！");
//    麦克风不可用，快去检查一下");
//    好像没网了，快检查一下");
//    测评君罢工了，程序员哥哥会尽快修复（" + resultEntity.getErrorNo() + "）");
//    你可以说的更好，再来一次哦！");
//    录音上传中");
//    题目已作答");
}

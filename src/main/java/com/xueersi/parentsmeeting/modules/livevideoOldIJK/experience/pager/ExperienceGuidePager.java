package com.xueersi.parentsmeeting.modules.livevideoOldIJK.experience.pager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.ImageHintDialog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.experience.bussiness.IPagerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.util.Random;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

public class ExperienceGuidePager extends LiveBasePager {

    private View view;
    /**
     * 新手引导首页
     */
    private RelativeLayout rlGuideHomePager;
    private ImageView ivHomeQuit;
    private LinearLayout llHomeUnknow;
    private LinearLayout llHomeKnow;
    private LinearLayout llHomeKnowWell;
    /**
     * 新手引导区域背景
     */
    private RelativeLayout rlGuideBg;
    private ImageView ivTeacher;
    private ImageView ivCourseware;
    private RelativeLayout rlInformation;
    private ImageView ivInforBg;
    private TextView tvMessageMe;
    private TextView tvMessageTeacher;
    /**
     * 新手引导index
     */
    private LinearLayout llGuideIndex;
    private TextView tvIndexTitle;
    private Button btnIndexNext;
    /**
     * 退出
     */
    private TextView tvQuit;
    /**
     * 新手引导第一步
     */
    private LinearLayout llCoursewareIntroduce;
    private Button btnStepOneNext1;
    private RelativeLayout rlOtherIntroduce;
    private TextView tvOtherTitle;
    private TextView tvOtherContent;
    private Button btnStepOneNext2;
    private ImageView ivIntroduceTeacher;
    private ImageView ivIntroduceMessage;
    /**
     * 新手试题
     */
    private RelativeLayout rlQuestion;
    private TextView tvQuestionTitle;
    private TextView tvQuestionContent;
    /**
     * 新手引导第二步
     */
    private RelativeLayout rlStepTwo;
    private RelativeLayout rlOption;
    private ImageView ivOptionHand;
    private CheckBox cbOptionB;
    private Button btnOptionSubmit;
    private ImageView ivOptionSubmitHand;
    /**
     * 新手引导第三步
     */
    private RelativeLayout rlStepThree;
    private RelativeLayout rlVoiceAnswer;
    private VolumeWaveView vwvVoiceAnswer;
    private TextView tvVoiceTip;
    private ImageView ivVoiceArrows;
    private LinearLayout llSpeechFollow;
    private ImageView ivStartSpeech;
    private TextView tvSpeechFollow;
    private ImageView ivSpeechHand;
    private TextView tvSpeechStart;
    private TextView tvSpeechTip;
    /**
     * 新手引导第四步
     */
    private RelativeLayout rlStepFour;
    private Button btnMessageOpen;
    private Button btnMessageCommon;
    private ImageView ivOpenChickHand;
    private RelativeLayout rlMessage;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    private RelativeLayout rlMessageInput;
    private EditText etMessageInput;
    private Button btnMessageSend;
    private ImageView ivMessageHand;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    /**
     * 进入直播视频倒计时显示
     */
    private TextView tvCountDown;


    private IPagerControl pagerControl;
    private int step;
    private int introduceStep = 1;
    private SpannableStringBuilder spannableStringBuilder;
    private ForegroundColorSpan colorOrange;
    //理科2，英语3，语文1
    private String subject;
    private long visitTime;
    private ImageHintDialog imageDialog;
    private boolean isSpeechRecog;
    private CountDownTimer speechCountDownTimer;
    private SpannableStringBuilder speechSpan;
    private AlphaAnimation alphaAnimation;
    private Runnable indexNextRunable;
    private Animation scalAnimation;
    private CountDownTimer vwvCountDownTimer;
    private CountDownTimer quitCountDownTimer;
    private VerifyCancelAlertDialog quitDialog;

    private final static int VOLUME_WAVE_AMPLITUDE = 55;
    private final static int VOLUME_WAVE_COUNTDOWN_TIME = 8000;
    private final static int VOLUME_WAVE_COUNTDOWN_INTERVAL = 100;
    private final static int VOLUME_WAVE_COUNTDOWN_UNTILFINISHED_UP = 5000;
    private final static int VOLUME_WAVE_COUNTDOWN_UNTILFINISHED_DOWN = 1000;

    private final static int QUIT_COUNTDOWN_MILLISECOND = 1000;
    private final static int QUIT_COUNTDOWN_INTERVAL = 500;
    private final static int QUIT_COUNTDOWN_UNTILFINISHED_UP = 16000;

    private final static int SPEECH_COUNTDOWN_TIME = 5000;
    private final static int SPEECH_COUNTDOWN_INTERVAL = 1000;
    private final static int SPEECH_COUNTDOWN_UNTILFINISHED_UP = 4000;
    private final static int SPEECH_COUNTDOWN_UNTILFINISHED_DOWN = 2000;

    public ExperienceGuidePager(Context context) {
        super(context);
    }

    public ExperienceGuidePager(Context context, IPagerControl iPagerControl, long visitTime, String subject) {
        super(context);
        this.subject = subject;
        this.visitTime = visitTime;
        pagerControl = iPagerControl;
        initTimer();
    }

    @Override
    public View initView() {
        view = LayoutInflater.from(mContext).inflate(R.layout.pager_livevideo_experience_guide, null);
        //倒计时
        tvCountDown = view.findViewById(R.id.tv_experience_guide_count_down);
        //新手引导首页
        rlGuideHomePager = view.findViewById(R.id.rl_experience_guide_home_pager);
        ivHomeQuit = view.findViewById(R.id.iv_experience_guide_home_back);
        llHomeUnknow = view.findViewById(R.id.ll_experience_guide_option_unknown);
        llHomeKnow = view.findViewById(R.id.ll_experience_guide_option_known);
        llHomeKnowWell = view.findViewById(R.id.ll_experience_guide_option_knownwell);
        //新手引导区域背景
        rlGuideBg = view.findViewById(R.id.rl_experience_guide_bg);
        ivTeacher = view.findViewById(R.id.iv_experience_guide_teacher);
        ivCourseware = view.findViewById(R.id.iv_experience_guide_courseware);
        rlInformation = view.findViewById(R.id.rl_experience_guide_info);
        ivInforBg = view.findViewById(R.id.iv_experience_guide_info_bg);
        tvMessageMe = view.findViewById(R.id.tv_experience_guide_message_me);
        tvMessageTeacher = view.findViewById(R.id.tv_experience_guide_message_teacher);
        //新手引导index
        llGuideIndex = view.findViewById(R.id.ll_experience_guide_step_index);
        tvIndexTitle = view.findViewById(R.id.tv_experience_guide_index_title);
        btnIndexNext = view.findViewById(R.id.btn_experience_guide_index_next);
        //退出
        tvQuit = view.findViewById(R.id.tv_experience_guide_quit);
        //新手引导第一步
        llCoursewareIntroduce = view.findViewById(R.id.ll_experience_guide_courseware_introduce);
        btnStepOneNext1 = view.findViewById(R.id.btn_experience_guide_step1_next1);
        rlOtherIntroduce = view.findViewById(R.id.rl_experience_guide_other_introduce);
        tvOtherTitle = view.findViewById(R.id.tv_experience_guide_introduce_title);
        tvOtherContent = view.findViewById(R.id.tv_experience_guide_introduce_content);
        btnStepOneNext2 = view.findViewById(R.id.btn_experience_guide_step1_next2);
        ivIntroduceTeacher = view.findViewById(R.id.iv_experience_guide_teacher_arrows);
        ivIntroduceMessage = view.findViewById(R.id.iv_experience_guide_message_arrows);
        //新手引导试题
        rlQuestion = view.findViewById(R.id.rl_experience_guide_question);
        tvQuestionTitle = view.findViewById(R.id.tv_experience_guide_question_title);
        tvQuestionContent = view.findViewById(R.id.tv_experience_guide_question_content);
        //新手引导第二步
        rlStepTwo = view.findViewById(R.id.rl_experience_guide_step2);
        rlOption = view.findViewById(R.id.rl_experience_guide_option);
        ivOptionHand = view.findViewById(R.id.iv_experience_guide_option_hand);
        cbOptionB = view.findViewById(R.id.cb_experience_guide_optionb);
        btnOptionSubmit = view.findViewById(R.id.bt_experience_guide_option_submit);
        ivOptionSubmitHand = view.findViewById(R.id.iv_experience_guide_option_submit_hand);
        //新手引导第三步
        rlStepThree = view.findViewById(R.id.rl_experience_guide_step3);
        //语音答题
        rlVoiceAnswer = view.findViewById(R.id.rl_experience_guide_voice_answer);
        vwvVoiceAnswer = view.findViewById(R.id.vwv_experience_guide_wave);
        tvVoiceTip = view.findViewById(R.id.tv_experience_guide_voice_tip);
        ivVoiceArrows = view.findViewById(R.id.iv_experience_guide_voice_arrows);
        //语文跟读
        llSpeechFollow = view.findViewById(R.id.ll_experience_guide_voice_evaluate);
        ivStartSpeech = view.findViewById(R.id.iv_experience_guide_voicetest_record);
        tvSpeechFollow = view.findViewById(R.id.tv_experience_guide_speech_follow);
        ivSpeechHand = view.findViewById(R.id.iv_experience_guide_speech_hand);
        tvSpeechStart = view.findViewById(R.id.tv_experience_guide_speech_start);
        tvSpeechTip = view.findViewById(R.id.tv_experience_guide_speech_tip);
        //新手引导第四步
        rlStepFour = view.findViewById(R.id.rl_experience_guide_step4);
        btnMessageOpen = view.findViewById(R.id.btn_experience_guide_message_open);
        btnMessageCommon = view.findViewById(R.id.btn_experience_guide_message_common);
        ivOpenChickHand = view.findViewById(R.id.iv_experience_guide_chick);
        rlMessage = view.findViewById(R.id.rl_experience_guide_message);
        switchFSPanelLinearLayout = view.findViewById(R.id.ll_livevideo_experience_guide_message_panelroot);
        rlMessageInput = view.findViewById(R.id.rl_experience_guide_message_input);
        etMessageInput = view.findViewById(R.id.et_experience_guide_message_content);
        btnMessageSend = view.findViewById(R.id.bt_experience_guide_message_send);
        ivMessageHand = view.findViewById(R.id.iv_experience_guide_message_submit_hand);
        initAnimation();
        initListener();
        initRunable();
        return view;
    }

    private void initRunable() {
        indexNextRunable = new ViewClickRunnable(btnIndexNext);
    }

    @Override
    public void initListener() {
        //引导首页
        ivHomeQuit.setOnClickListener(new QuitOnClickListener(false));
        llHomeKnowWell.setOnClickListener(new QuitOnClickListener(false));
        llHomeUnknow.setOnClickListener(new UnknowOnClickListener());
        llHomeKnow.setOnClickListener(new UnknowOnClickListener());
        tvQuit.setOnClickListener(new QuitOnClickListener(true));
        //index中按钮监听
        btnIndexNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIndexNext.clearAnimation();
                btnIndexNext.setAlpha(1.0f);
                if (indexNextRunable != null) {
                    view.removeCallbacks(indexNextRunable);
                }
                switch (step) {
                    case 1:
                        setAreaBackground(false, true, true);
                        setIndexView(false);
                        llCoursewareIntroduce.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        setAreaBackground(false, false, false);
                        setIndexView(false);
                        rlQuestion.setVisibility(View.VISIBLE);
                        rlStepTwo.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        setAreaBackground(false, false, false);
                        setIndexView(false);
                        rlStepThree.setVisibility(View.VISIBLE);
                        if ("3".equals(subject)) {
                            rlQuestion.setVisibility(View.VISIBLE);
                            tvQuestionTitle.setText("1. what is the setting of the story ?");
                            tvQuestionContent.setText("A. a bedroom at night\nB. a bam at night\nC. a dinner at noon ");
                            rlVoiceAnswer.setVisibility(View.VISIBLE);
                            setVoiceAnswer();
                        } else if ("1".equals(subject)) {
                            tvQuit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.experience_guide_back_icon_gray, 0, 0, 0);
                            tvQuit.setTextColor(0xFF666666);
                            llSpeechFollow.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 4:
                        setAreaBackground(false, false, false);
                        setIndexView(false);
                        rlStepFour.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        //直播区域介绍按钮监听
        btnStepOneNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAreaBackground(true, false, true);
                llCoursewareIntroduce.setVisibility(View.GONE);
                rlOtherIntroduce.setVisibility(View.VISIBLE);
            }
        });
        btnStepOneNext2.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View v) {
                if (1 == introduceStep) {
                    setAreaBackground(true, true, false);
                    tvOtherTitle.setText("聊天区");
                    tvOtherContent.setText("这里是同学们畅所欲言的地方");
                    rlInformation.setBackgroundResource(R.drawable.shape_corners_10dp_stroke_000000);
                    ivIntroduceMessage.setVisibility(View.VISIBLE);
                    ivIntroduceTeacher.setVisibility(View.GONE);
                    introduceStep++;
                } else if (2 == introduceStep) {
                    setAreaBackground(true, true, true);
                    rlInformation.setBackgroundResource(R.color.COLOR_333333);
                    rlOtherIntroduce.setVisibility(View.GONE);
                    setIndexView(true);
                }
            }
        });
        //选择题选择提交按钮监听
        cbOptionB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnOptionSubmit.setEnabled(true);
                    btnOptionSubmit.setTextColor(0xFFFF6E1A);
                    ivOptionHand.clearAnimation();
                    ivOptionHand.setVisibility(View.GONE);
                    ivOptionSubmitHand.setVisibility(View.VISIBLE);
                } else {
                    btnOptionSubmit.setEnabled(false);
                    btnOptionSubmit.setTextColor(0xFF666666);
                    ivOptionHand.setVisibility(View.VISIBLE);
                    ivOptionSubmitHand.clearAnimation();
                    ivOptionSubmitHand.setVisibility(View.GONE);
                }
            }
        });
        btnOptionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivOptionSubmitHand.clearAnimation();
                ivOptionSubmitHand.setVisibility(View.GONE);
                rlQuestion.setVisibility(View.GONE);
                rlStepTwo.setVisibility(View.GONE);
                showResultDialog("哦耶，答对了！");
            }
        });
        //语文跟读按钮监听
        ivStartSpeech.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View v) {
                setSpeechAnswer();
            }
        });
        //引导第四步 消息按钮监听
        btnMessageOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivOpenChickHand.clearAnimation();
                ivOpenChickHand.setVisibility(View.GONE);
                rlMessage.setVisibility(View.VISIBLE);
                switchFSPanelLinearLayout.setVisibility(View.VISIBLE);
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageInput);
            }
        });
        btnMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(etMessageInput.getWindowToken(), 0);
                rlStepFour.setVisibility(View.GONE);
                switchFSPanelLinearLayout.setVisibility(View.GONE);
                ivMessageHand.clearAnimation();
                ivMessageHand.setVisibility(View.GONE);
                rlMessage.setVisibility(View.GONE);
                SpannableStringBuilder meSpan = new SpannableStringBuilder("我：" + etMessageInput.getText().toString());
                meSpan.setSpan(new ForegroundColorSpan(0xFFFF8036), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvMessageMe.setText(meSpan);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SpannableStringBuilder teacherSpan = new SpannableStringBuilder("主讲老师：同学们好，欢迎大家来上课！");
                        teacherSpan.setSpan(new ForegroundColorSpan(0xFFFF8036), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvMessageTeacher.setText(teacherSpan);
                    }
                }, 1000);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showCompleteDialog();
                    }
                }, 2000);


            }
        });

        etMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(charSequence)) {
                    btnMessageSend.setEnabled(false);
                    btnMessageSend.setBackgroundResource(R.drawable.play_chat_sent_btn_disabled);
                    ivMessageHand.clearAnimation();
                    ivMessageHand.setVisibility(View.GONE);
                } else {
                    btnMessageSend.setEnabled(true);
                    btnMessageSend.setBackgroundResource(R.drawable.selector_livevideo_small_english_chat_send);
                    ivMessageHand.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        KeyboardUtil.attach((Activity) mContext, switchFSPanelLinearLayout, new KeyboardUtil
                .OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                logger.i("onKeyboardShowing:isShowing=" + isShowing);
            }
        });

        vwvVoiceAnswer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vwvVoiceAnswer.setLinearGradient(new LinearGradient(0, 0, vwvVoiceAnswer.getMeasuredWidth(), 0,
                        new int[]{0xFFEA9CF9, 0xFF9DBBFA, 0xFF80F9FD}, new float[]{0, 0.5f, 1.0f}, Shader.TileMode
                        .CLAMP));
            }
        });
        vwvVoiceAnswer.setBackColor(Color.TRANSPARENT);
        vwvVoiceAnswer.start();
        ivOptionHand.getViewTreeObserver().addOnGlobalLayoutListener(new HandOnDrawListener(ivOptionHand));
        ivOptionSubmitHand.getViewTreeObserver().addOnGlobalLayoutListener(new HandOnDrawListener(ivOptionSubmitHand));
        ivSpeechHand.getViewTreeObserver().addOnGlobalLayoutListener(new HandOnDrawListener(ivSpeechHand));
        ivMessageHand.getViewTreeObserver().addOnGlobalLayoutListener(new HandOnDrawListener(ivMessageHand));
        ivOpenChickHand.getViewTreeObserver().addOnGlobalLayoutListener(new HandOnDrawListener(ivOpenChickHand));
        super.initListener();
    }

    private void initTimer() {
        if ("3".equals(subject)) {
            final Random random = new Random(100);
            vwvCountDownTimer = new CountDownTimer(VOLUME_WAVE_COUNTDOWN_TIME, VOLUME_WAVE_COUNTDOWN_INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished < VOLUME_WAVE_COUNTDOWN_UNTILFINISHED_UP && millisUntilFinished > VOLUME_WAVE_COUNTDOWN_UNTILFINISHED_DOWN) {
                        tvVoiceTip.setVisibility(View.GONE);
                        ivVoiceArrows.setVisibility(View.GONE);
                        vwvVoiceAnswer.setVolume(random.nextFloat() * VOLUME_WAVE_AMPLITUDE);
                    } else {
                        vwvVoiceAnswer.setVolume(0);
                    }
                }

                @Override
                public void onFinish() {
                    vwvVoiceAnswer.stop();
                    vwvVoiceAnswer.setVisibility(View.GONE);
                    rlStepThree.setVisibility(View.GONE);
                    rlQuestion.setVisibility(View.GONE);
                    showResultDialog("恭喜你，完成测试！");
                }
            };
        }
        quitCountDownTimer = new CountDownTimer(visitTime * QUIT_COUNTDOWN_MILLISECOND, QUIT_COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < QUIT_COUNTDOWN_UNTILFINISHED_UP) {
                    tvCountDown.setVisibility(View.VISIBLE);
                    tvCountDown.setText(millisUntilFinished / QUIT_COUNTDOWN_MILLISECOND + "S后将开启体验课");
                }
            }

            @Override
            public void onFinish() {
                InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(etMessageInput.getWindowToken(), 0);
                ivHomeQuit.performClick();
            }
        };
        quitCountDownTimer.start();


    }

    class HandOnDrawListener implements ViewTreeObserver.OnGlobalLayoutListener {
        View view;

        HandOnDrawListener(View view) {
            this.view = view;
        }

        @Override
        public void onGlobalLayout() {
            if (view.getVisibility() == View.VISIBLE) {
                view.startAnimation(scalAnimation);
            } else {
                view.clearAnimation();
            }
        }
    }

    /**
     * 引导页index
     *
     * @param isShow
     */
    private void setIndexView(boolean isShow) {
        if (isShow) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startIndexNextAnim();
                    btnIndexNext.setEnabled(true);
                }
            }, 1000);
            view.postDelayed(indexNextRunable, 5000);
            llGuideIndex.setVisibility(View.VISIBLE);
            setAreaBackground(true, true, true);
            if (spannableStringBuilder == null) {
                spannableStringBuilder = new SpannableStringBuilder();
                colorOrange = new ForegroundColorSpan(0xFFF0773C);
            }
            switch (step) {
                case 0:
                    spannableStringBuilder.append("1.直播区域介绍");
                    spannableStringBuilder.setSpan(colorOrange, 4, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvIndexTitle.setText(spannableStringBuilder);
                    break;
                case 1:
                    spannableStringBuilder.clear();
                    spannableStringBuilder.append("2.如何做选择题？");
                    spannableStringBuilder.setSpan(colorOrange, 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvIndexTitle.setText(spannableStringBuilder);
                    break;
                case 2:
                    if ("3".equals(subject) || "1".equals(subject)) {
                        spannableStringBuilder.clear();
                        spannableStringBuilder.append("3.如何做语音题？");
                        spannableStringBuilder.setSpan(colorOrange, 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvIndexTitle.setText(spannableStringBuilder);
                        break;
                    }
                case 3:
                    spannableStringBuilder.clear();
                    spannableStringBuilder.append(String.valueOf(++step));
                    spannableStringBuilder.append(".如何在聊天区和老师互动呢？");
                    spannableStringBuilder.setSpan(colorOrange, 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFFF0773C), 9, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvIndexTitle.setText(spannableStringBuilder);
                    step = 3;
                    break;
            }
            step++;
        } else {
            llGuideIndex.setVisibility(View.GONE);
            btnIndexNext.setEnabled(false);
            btnIndexNext.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 英语语音答题显示
     */
    private void setVoiceAnswer() {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvVoiceAnswer.setVisibility(View.VISIBLE);
                ivVoiceArrows.setVisibility(View.VISIBLE);
                vwvCountDownTimer.start();
            }
        }, 5);

    }

    /**
     * 语文跟读显示
     */
    private void setSpeechAnswer() {
        if (!isSpeechRecog) {
            tvSpeechStart.setText("点击结束");
            tvSpeechTip.setVisibility(View.GONE);
            ivStartSpeech.setEnabled(false);
            ivSpeechHand.clearAnimation();
            ivSpeechHand.setVisibility(View.GONE);
            isSpeechRecog = true;
            speechSpan = new SpannableStringBuilder();
            speechCountDownTimer = new CountDownTimer(SPEECH_COUNTDOWN_TIME, SPEECH_COUNTDOWN_INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished > SPEECH_COUNTDOWN_UNTILFINISHED_UP && millisUntilFinished < SPEECH_COUNTDOWN_TIME) {
                        speechSpan.append("清明 唐 杜牧\n");
                        tvSpeechFollow.setText(speechSpan);
                    } else if (millisUntilFinished > SPEECH_COUNTDOWN_UNTILFINISHED_DOWN && millisUntilFinished < SPEECH_COUNTDOWN_UNTILFINISHED_UP) {
                        speechSpan.clear();
                        speechSpan.append("清明 唐 杜牧\n清明时节雨纷纷，路上行人欲断魂。\n");
                        speechSpan.setSpan(new ForegroundColorSpan(0xFFFF0000), 11, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvSpeechFollow.setText(speechSpan);
                    } else if (millisUntilFinished < SPEECH_COUNTDOWN_UNTILFINISHED_DOWN) {
                        speechSpan.clear();
                        speechSpan.append("清明 唐 杜牧\n清明时节雨纷纷，路上行人欲断魂。\n借问酒家何处有，牧童遥指杏花村。");
                        speechSpan.setSpan(new ForegroundColorSpan(0xFFFF0000), 11, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvSpeechFollow.setText(speechSpan);
                    }
                }

                @Override
                public void onFinish() {
                    tvSpeechTip.setVisibility(View.VISIBLE);
                    tvSpeechTip.setText("再次点击则结束录音");
                    ivStartSpeech.setEnabled(true);
                    ivSpeechHand.setVisibility(View.VISIBLE);
                }
            };
            speechCountDownTimer.start();
        } else {
            ivSpeechHand.clearAnimation();
            ivSpeechHand.setVisibility(View.GONE);
            tvSpeechTip.setVisibility(View.GONE);
            rlQuestion.setVisibility(View.GONE);
            rlStepThree.setVisibility(View.GONE);
            tvQuit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.experience_guide_back_icon_white, 0, 0, 0);
            tvQuit.setTextColor(0xFFFFFFFF);
            showResultDialog("恭喜你，完成测试！");
        }
    }

    /**
     * 显示答题结果弹窗
     *
     * @param showtext 提示文字
     */
    private void showResultDialog(String showtext) {
        if (imageDialog == null) {
            imageDialog = new ImageHintDialog(mContext, (Application) mContext.getApplicationContext(), false);
            imageDialog.setDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    setIndexView(true);
                }
            });
        }
        imageDialog.setImageResource(R.drawable.lead_right_monkey_img_nor).setText(showtext);
        imageDialog.showDialog();
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imageDialog != null) {
                    imageDialog.cancelDialog();
                }
            }
        }, 3000);
    }

    /**
     * 完成引导弹窗
     */
    private void showCompleteDialog() {
        VerifyCancelAlertDialog completeDialog = new VerifyCancelAlertDialog(mContext, (Application) mContext.getApplicationContext(), false, VerifyCancelAlertDialog.TITLE_MESSAGE_VERIFY_TYPE);
        SpannableStringBuilder completeTitleSpan = new SpannableStringBuilder("新手引导完成！");
        completeTitleSpan.setSpan(new ForegroundColorSpan(0xFF333333), 0, completeTitleSpan.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        completeTitleSpan.setSpan(new AbsoluteSizeSpan(SizeUtils.Sp2Px(mContext, 17)), 0, completeTitleSpan.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder completeMessageSpan = new SpannableStringBuilder("你一定迫不及待的想去上课了吧！");
        completeMessageSpan.setSpan(new ForegroundColorSpan(0xFF666666), 0, completeMessageSpan.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        completeMessageSpan.setSpan(new AbsoluteSizeSpan(SizeUtils.Sp2Px(mContext, 13)), 0, completeMessageSpan.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        completeDialog.setVerifyBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHomeQuit.performClick();
            }
        });

        completeDialog.setVerifyShowText("去上课").initInfo
                (completeTitleSpan, completeMessageSpan, VerifyCancelAlertDialog.VERIFY_SELECTED).showDialog();

    }

    /**
     * 退出引导监听
     */
    class QuitOnClickListener implements View.OnClickListener {
        boolean isDialog;

        QuitOnClickListener(boolean isDialog) {
            this.isDialog = isDialog;
        }

        @Override
        public void onClick(View v) {
            if (!isDialog) {
                if (vwvCountDownTimer != null) {
                    vwvCountDownTimer.cancel();
                }
                if (quitCountDownTimer != null) {
                    quitCountDownTimer.cancel();
                }
                if (quitDialog != null) {
                    quitDialog.cancelDialog();
                }
                if (imageDialog != null) {
                    imageDialog.cancelDialog();
                }
                if (vwvVoiceAnswer != null) {
                    vwvVoiceAnswer.stop();
                    vwvVoiceAnswer.setVisibility(View.GONE);
                }
                pagerControl.removePager();
            } else {
                if (quitDialog == null) {
                    quitDialog = new VerifyCancelAlertDialog(mContext, (Application) mContext.getApplicationContext(), false, VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    quitDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            quitDialog.cancelDialog();
                            if (speechCountDownTimer != null) {
                                speechCountDownTimer.cancel();
                                speechCountDownTimer = null;
                            }
                            if (vwvCountDownTimer != null) {
                                vwvCountDownTimer.cancel();
                            }
                            if (quitCountDownTimer != null) {
                                quitCountDownTimer.cancel();
                            }
                            if (vwvVoiceAnswer != null) {
                                vwvVoiceAnswer.stop();
                                vwvVoiceAnswer.setVisibility(View.GONE);
                            }
                            pagerControl.removePager();
                        }
                    });
                    quitDialog.setCancelBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            quitDialog.cancelDialog();
                        }
                    });
                    quitDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo
                            ("确定退出新手指导？", VerifyCancelAlertDialog.VERIFY_SELECTED);
                }
                quitDialog.showDialog();
            }
        }
    }

    /**
     * 了解一点和完全不了解点击监听
     */
    class UnknowOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            rlGuideHomePager.setVisibility(View.GONE);
            setAreaBackground(true, true, true);
//            btnIndexNext.setAlpha(0.0f);
            btnIndexNext.setVisibility(View.INVISIBLE);
            setIndexView(true);
        }
    }

    class ViewClickRunnable implements Runnable {

        View view;

        ViewClickRunnable(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            view.performClick();
        }
    }

    /**
     * 设置蒙尘
     *
     * @param isCourseware 课件区是否蒙尘
     * @param isTeacher    教师区是否蒙尘
     * @param isMessage    聊天区是否蒙尘
     */
    private void setAreaBackground(boolean isCourseware, boolean isTeacher, boolean isMessage) {
        if (isCourseware) {
            ivCourseware.setImageResource(R.color.COLOR_CC000000);

        } else {
            ivCourseware.setImageResource(R.color.COLOR_00000000);
        }
        if (isTeacher) {
            ivTeacher.setImageResource(R.color.COLOR_CC000000);
        } else {
            ivTeacher.setImageResource(R.color.COLOR_00000000);
        }
        if (isMessage) {
            ivInforBg.setImageResource(R.color.COLOR_CC000000);
        } else {
            ivInforBg.setImageResource(R.color.COLOR_00000000);
        }

    }

    /**
     * 开始index继续按钮动画
     */
    private void startIndexNextAnim() {
        btnIndexNext.startAnimation(alphaAnimation);
        btnIndexNext.setVisibility(View.VISIBLE);
    }

    private void initAnimation() {
        alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim.anim_experience_guide_alpha);
        scalAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_experience_guide_hand);
    }
}

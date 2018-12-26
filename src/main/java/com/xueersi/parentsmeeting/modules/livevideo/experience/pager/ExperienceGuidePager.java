package com.xueersi.parentsmeeting.modules.livevideo.experience.pager;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness.IPagerControl;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

public class ExperienceGuidePager extends LiveBasePager {

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
    /**
     * 新手引导第三步
     */
    private RelativeLayout rlStepThree;
    private RelativeLayout rlVoiceAnswer;
    private VolumeWaveView vwvVoiceAnswer;
    private ImageView ivStartSpeech;
    private TextView tvSpeechFollow;
    private ImageView ivVoiceHand;
    /**
     * 新手引导第四步
     */
    private RelativeLayout rlStepFour;
    private Button btnMessageOpen;
    private Button btnMessageCommon;
    private ImageView ivOpenChick;
    private RelativeLayout rlMessage;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    private RelativeLayout rlMessageInput;
    private EditText etMessageInput;
    private Button btnSend;


    IPagerControl pagerControl;



    public ExperienceGuidePager(Context context) {
        super(context);
    }
    public ExperienceGuidePager(Context context, IPagerControl iPagerControl) {
        super(context);
        pagerControl = iPagerControl;
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_livevideo_experience_guide, null);
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
        //新手引导第三步
        rlStepThree = view.findViewById(R.id.rl_experience_guide_step3);
          //语音答题
        rlVoiceAnswer = view.findViewById(R.id.rl_experience_guide_voice_answer);
        vwvVoiceAnswer = view.findViewById(R.id.vwv_experience_guide_wave);
          //语文跟读
        ivStartSpeech = view.findViewById(R.id.iv_experience_guide_voicetest_record);
        tvSpeechFollow = view.findViewById(R.id.tv_experience_guide_speech_follow);
        ivVoiceHand = view.findViewById(R.id.iv_experience_guide_voice_hand);
        //新手引导第四步
        rlStepFour = view.findViewById(R.id.rl_experience_guide_step4);
        btnMessageOpen = view.findViewById(R.id.btn_experience_guide_message_open);
        btnMessageCommon = view.findViewById(R.id.btn_experience_guide_message_common);
        ivOpenChick = view.findViewById(R.id.iv_experience_guide_chick);
        rlMessage = view.findViewById(R.id.rl_experience_guide_message);
        switchFSPanelLinearLayout = view.findViewById(R.id.ll_livevideo_experience_guide_message_panelroot);
        rlMessageInput = view.findViewById(R.id.rl_experience_guide_message_input);
        etMessageInput = view.findViewById(R.id.et_experience_guide_message_content);
        btnSend = view.findViewById(R.id.bt_experience_guide_message_send);
        return view;
    }

    @Override
    public void initListener() {
        //引导首页
        ivHomeQuit.setOnClickListener(new QuitOnClickListener());
        llHomeKnowWell.setOnClickListener(new QuitOnClickListener());
        llHomeUnknow.setOnClickListener(new UnknowOnClickListener());
        llHomeKnow.setOnClickListener(new UnknowOnClickListener());

        tvQuit.setOnClickListener(new QuitOnClickListener());
        super.initListener();
    }

    class QuitOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            pagerControl.removePager();
        }
    }
    class UnknowOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            rlGuideHomePager.setVisibility(View.GONE);
            setAreaBackground(true,true,true);
            llGuideIndex.setVisibility(View.VISIBLE);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("1.直播区域介绍");
            ForegroundColorSpan colorOrange = new ForegroundColorSpan(0xFFF0773C);
            spannableStringBuilder.setSpan(colorOrange,4,5,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvIndexTitle.setText(spannableStringBuilder);
        }
    }

    /**
     * 设置蒙尘
     * @param isCourseware 课件区是否蒙尘
     * @param isTeacher 教师区是否蒙尘
     * @param isMessage 聊天区是否蒙尘
     */
    private void setAreaBackground(boolean isCourseware,boolean isTeacher,boolean isMessage){
        if(isCourseware){
            ivCourseware.setImageResource(0xCC000000);
        }else {
            ivCourseware.setImageResource(0x00000000);
        }
        if(isTeacher){
            ivTeacher.setImageResource(0xCC000000);
        }else {
            ivTeacher.setImageResource(0x00000000);
        }
        if(isMessage){
            ivInforBg.setImageResource(0xCC000000);
        }else {
            ivInforBg.setImageResource(0x00000000);
        }



    }
}

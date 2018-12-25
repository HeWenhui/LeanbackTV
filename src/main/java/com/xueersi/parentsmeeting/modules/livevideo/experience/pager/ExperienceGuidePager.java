package com.xueersi.parentsmeeting.modules.livevideo.experience.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

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
    private ImageView ivTeacher;
    private ImageView ivCourseware;
    private RelativeLayout rlInformation;
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
    private LinearLayout llOtherIntroduce;
    private TextView tvOtherTitle;
    private TextView tvOtherContent;
    private Button btnStepOneNext2;
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


    public ExperienceGuidePager(Context context) {
        super(context);
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
        ivTeacher = view.findViewById(R.id.iv_experience_guide_teacher);
        ivCourseware = view.findViewById(R.id.iv_experience_guide_courseware);
        rlInformation = view.findViewById(R.id.rl_experience_guide_info);
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
        llOtherIntroduce = view.findViewById(R.id.ll_experience_guide_other_introduce);
        tvOtherTitle = view.findViewById(R.id.tv_experience_guide_introduce_title);
        tvOtherContent = view.findViewById(R.id.tv_experience_guide_introduce_content);
        btnStepOneNext2 = view.findViewById(R.id.btn_experience_guide_step1_next2);
        //新手引导第二步
        //新手引导第三步
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
        super.initListener();
    }
}

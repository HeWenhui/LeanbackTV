package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.ui.widget.button.progressbutton.CircularProgressButton;

import org.greenrobot.eventbus.EventBus;

/**
 * 录播课程选择题互动题页面
 *
 * @author hua
 */
public class QuestionSelectLivePager extends BaseLiveQuestionPager {

    /** 答题四个选项 */
    private Button perBt;
    private Button btnA;
    private Button btnB;
    private Button btnC;
    private Button btnD;
    private CircularProgressButton btnSubmit;

    /** 隐藏试题图标布局 */
    RelativeLayout rlDown;

    /** 提交 */
    private String mAnswer = "A";

    /** 隐藏试题布局 */
    ImageView mImgDown;
    /** 互动题 */
    private BaseVideoQuestionEntity baseVideoQuestionEntity;
    private RelativeLayout rlQuestionContent;
    private RelativeLayout rlQuestionHide;
    private ImageView ivQuestionVisible;

    public QuestionSelectLivePager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity) {
        super(context);
        this.baseVideoQuestionEntity = baseVideoQuestionEntity;
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_selectquestion, null);
        rlQuestionContent = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_content);
        rlQuestionHide = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_hide);
        mImgDown = (ImageView) mView.findViewById(R.id.iv_livevideo_question_fillin_down);
        ivQuestionVisible = (ImageView) mView.findViewById(R.id.iv_pop_question_visible);
        btnA = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questiona);
        btnB = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questionb);
        btnC = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questionc);
        btnD = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questiond);
        rlDown = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_fillin_down);
        btnSubmit = (CircularProgressButton) mView.findViewById(R.id.btn_livevideo_question_fillin_submit);
        btnSubmit.setIndeterminateProgressMode(true);
        return mView;
    }

    public void initListener() {
        btnA.setOnClickListener(new AnswerOnClickListener());
        btnB.setOnClickListener(new AnswerOnClickListener());
        btnC.setOnClickListener(new AnswerOnClickListener());
        btnD.setOnClickListener(new AnswerOnClickListener());
        btnSubmit.setOnClickListener(new SubmitAnswerOnClickListener());
        rlDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseVideoQuestionEntity instanceof VideoQuestionLiveEntity) {
                    rlQuestionContent.setVisibility(View.INVISIBLE);
                    rlQuestionHide.setVisibility(View.VISIBLE);
                } else {
                    EventBus.getDefault().post(new PlaybackVideoEvent.OnQuesionDown(baseVideoQuestionEntity));
                    rlQuestionContent.setVisibility(View.INVISIBLE);
                    rlQuestionHide.setVisibility(View.VISIBLE);
                }
            }
        });
        ivQuestionVisible.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rlQuestionContent.setVisibility(View.VISIBLE);
                rlQuestionHide.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void initData() {
        String stuAnswer = baseVideoQuestionEntity.getStuAnswer();
        // 如果没有答案或答案为A
        if (TextUtils.isEmpty(stuAnswer) || stuAnswer.equals("A")) {
            btnA.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnA.setTextColor(Color.WHITE);
            perBt = btnA;
            // 如果答案为B
        } else if (stuAnswer.equals("B")) {
            btnB.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnB.setTextColor(Color.WHITE);
            perBt = btnB;
            // 如果答案为C
        } else if (stuAnswer.equals("C")) {
            btnC.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnC.setTextColor(Color.WHITE);
            perBt = btnC;
            // 如果答案为D
        } else if (stuAnswer.equals("D")) {
            btnD.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnD.setTextColor(Color.WHITE);
            perBt = btnD;
        }
    }

    /** 答案选择 */
    private class AnswerOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            perBt.setBackgroundResource(R.drawable.shape_question_answer_normal);
            perBt.setTextColor(Color.parseColor("#666666"));
            v.setBackgroundResource(R.drawable.shape_question_answer_selected);
            perBt = (Button) v;
            perBt.setTextColor(Color.WHITE);
            mAnswer = perBt.getText().toString();
            baseVideoQuestionEntity.setStuAnswer(mAnswer);
        }

    }

    /** 提交答案 */
    private class SubmitAnswerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (baseVideoQuestionEntity instanceof VideoQuestionLiveEntity) {
                if (btnSubmit.getProgress() == 0) {
                    btnSubmit.setProgress(50);
                } else if (btnSubmit.getProgress() == 100) {
                    btnSubmit.setProgress(0);
                } else {
                    btnSubmit.setProgress(100);
                }
            }
            if (putQuestion != null) {
                putQuestion.onPutQuestionResult(QuestionSelectLivePager.this, baseVideoQuestionEntity, mAnswer);
            }
        }
    }

    /**
     * 提交互动题成功
     */
    public void onSubSuccess() {
        btnSubmit.setProgress(100);
    }

    /**
     * 提交互动题失败
     */
    public void onSubFailure() {
        btnSubmit.setProgress(-1);
    }
}

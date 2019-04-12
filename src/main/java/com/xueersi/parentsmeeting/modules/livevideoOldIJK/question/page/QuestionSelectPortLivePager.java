package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.ui.widget.button.progressbutton.CircularProgressButton;

/**
 * 录播课程选择题互动题页面
 *
 * @author hua
 */
public class QuestionSelectPortLivePager extends BaseLiveQuestionPager {

    /** 答题四个选项 */
    private Button btnA;
    private Button btnB;
    private Button btnC;
    private Button btnD;
    private CircularProgressButton btnSubmit;
    private TextView tvQuestionAnswer;

    /** 提交 */
    private String mAnswer = "A";

    /** 隐藏试题布局 */
    ImageView mImgDown;
    /** 互动题 */
    private VideoQuestionLiveEntity interQues;

    public QuestionSelectPortLivePager(Context context, VideoQuestionLiveEntity interQues) {
        super(context);
        this.interQues = interQues;
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_selectquestion_port, null);
        mImgDown = (ImageView) mView.findViewById(R.id.iv_livevideo_question_fillin_down);
        tvQuestionAnswer = (TextView) mView.findViewById(R.id.tv_livevideo_question_sele_answer);
        btnA = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questiona);
        btnB = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questionb);
        btnC = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questionc);
        btnD = (Button) mView.findViewById(R.id.btn_livevideo_question_select_questiond);
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
    }

    @Override
    public void initData() {
        String stuAnswer = interQues.getStuAnswer();
        // 如果没有答案或答案为A
        if (TextUtils.isEmpty(stuAnswer) || stuAnswer.equals("A")) {
            btnA.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnA.setTextColor(Color.WHITE);
            // 如果答案为B
        } else if (stuAnswer.equals("B")) {
            btnB.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnB.setTextColor(Color.WHITE);
            // 如果答案为C
        } else if (stuAnswer.equals("C")) {
            btnC.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnC.setTextColor(Color.WHITE);
            // 如果答案为D
        } else if (stuAnswer.equals("D")) {
            btnD.setBackgroundResource(R.drawable.shape_question_answer_selected);
            btnD.setTextColor(Color.WHITE);
        }
        if (TextUtils.isEmpty(stuAnswer)) {
            tvQuestionAnswer.setText("A");
        } else {
            tvQuestionAnswer.setText(stuAnswer);
        }
    }

    /** 答案选择 */
    private class AnswerOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            btnA.setBackgroundResource(R.drawable.shape_question_answer_normal);
            btnA.setTextColor(Color.parseColor("#666666"));
            btnB.setBackgroundResource(R.drawable.shape_question_answer_normal);
            btnB.setTextColor(Color.parseColor("#666666"));
            btnC.setBackgroundResource(R.drawable.shape_question_answer_normal);
            btnC.setTextColor(Color.parseColor("#666666"));
            btnD.setBackgroundResource(R.drawable.shape_question_answer_normal);
            btnD.setTextColor(Color.parseColor("#666666"));
            v.setBackgroundResource(R.drawable.shape_question_answer_selected);
            ((Button) v).setTextColor(Color.WHITE);
            mAnswer = ((Button) v).getText().toString();
            interQues.setStuAnswer(mAnswer);
            tvQuestionAnswer.setText(mAnswer);
        }

    }

    public VideoQuestionLiveEntity getInterQues() {
        return interQues;
    }

    /** 提交答案 */
    private class SubmitAnswerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (btnSubmit.getProgress() == 0) {
                btnSubmit.setProgress(50);
            } else if (btnSubmit.getProgress() == 100) {
                btnSubmit.setProgress(0);
            } else {
                btnSubmit.setProgress(100);
            }
            if (putQuestion != null) {
                putQuestion.onPutQuestionResult(QuestionSelectPortLivePager.this, interQues, mAnswer);
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

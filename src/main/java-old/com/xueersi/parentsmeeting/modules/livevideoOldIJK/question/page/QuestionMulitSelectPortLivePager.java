package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;
import com.xueersi.ui.adapter.XsBaseAdapter;
import com.xueersi.ui.widget.button.progressbutton.CircularProgressButton;

import java.util.List;

/**
 * 录播课程选择题互动题页面
 *
 * @author linyuqiang
 */
public class QuestionMulitSelectPortLivePager extends BaseLiveQuestionPager {
    RelativeLayout rlQuestionContentQuestion;
    /** 答案列表 */
    List<AnswerEntity> mAnswerEntityLst;
    private CircularProgressButton btnSubmit;
    /** 提交 */
    private String mAnswer = "";
    /** 互动题 */
    private BaseVideoQuestionEntity baseVideoQuestionEntity;
    private RelativeLayout rlQuestionContent;
    private RelativeLayout rlQuestionHide;
    private float screenDensity;
    private GridView gvQuestion;
    private TextView tvQuestionAnswer;

    public QuestionMulitSelectPortLivePager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity) {
        super(context);
        this.baseVideoQuestionEntity = baseVideoQuestionEntity;
        mAnswerEntityLst = baseVideoQuestionEntity.getAnswerEntityLst();
        //测试多选题
//        mAnswerEntityLst.addAll(mAnswerEntityLst);
//        mAnswerEntityLst.addAll(mAnswerEntityLst);
//        for (int i = 0; i < mAnswerEntityLst.size(); i++) {
//            if (i % 2 == 0) {
//                mAnswerEntityLst.get(i).setStuAnswer("1");
//            }
//        }
        screenDensity = ScreenUtils.getScreenDensity();
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_mulitselectportquestion, null);
        rlQuestionContentQuestion = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_content_question);
        tvQuestionAnswer = (TextView) mView.findViewById(R.id.tv_livevideo_question_sele_answer);
        rlQuestionContent = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_content);
        rlQuestionHide = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_hide);
        gvQuestion = (GridView) mView.findViewById(R.id.gv_livevideo_question_mulitSelect);
        btnSubmit = (CircularProgressButton) mView.findViewById(R.id.btn_livevideo_question_fillin_submit);
        btnSubmit.setIndeterminateProgressMode(true);
        return mView;
    }

    public void initListener() {
        btnSubmit.setOnClickListener(new SubmitAnswerOnClickListener());
    }

    @Override
    public void initData() {
        MulitSelectAdapter mulitSelectAdapter = new MulitSelectAdapter(mContext, mAnswerEntityLst);
        gvQuestion.setAdapter(mulitSelectAdapter);
        //选项少于两行，重新设置高度
        if (mAnswerEntityLst.size() < 6) {
            ViewGroup.LayoutParams lp = rlQuestionContentQuestion.getLayoutParams();
            lp.height = (int) (64 * screenDensity);
//            rlQuestionContentQuestion.setLayoutParams(lp);
            LayoutParamsUtil.setViewLayoutParams(rlQuestionContentQuestion, lp);
            ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) btnSubmit.getLayoutParams();
            lp2.topMargin = (int) (ScreenUtils.getScreenDensity() * 10);
//            btnSubmit.setLayoutParams(lp2);
            LayoutParamsUtil.setViewLayoutParams(btnSubmit, lp2);
        }
        String answer = "";
        for (int i = 0; i < mAnswerEntityLst.size(); i++) {
            AnswerEntity answerEntity = mAnswerEntityLst.get(i);
            if ("1".equals(answerEntity.getStuAnswer())) {
                answer += (char) ('A' + i) + ",";
            }
        }
        if ("".equals(answer)) {
            btnSubmit.setEnabled(false);
            tvQuestionAnswer.setText(answer);
        } else {
            btnSubmit.setEnabled(true);
            tvQuestionAnswer.setText(answer.substring(0, answer.length() - 1));
        }
    }

    /** 答案选择 */
    private class AnswerOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Integer isSelect = (Integer) v.getTag();
            if (isSelect == null) {
                isSelect = 1;
            } else {
                if (isSelect == 1) {
                    isSelect = 0;
                } else {
                    isSelect = 1;
                }
            }
            v.setTag(isSelect);
            int position = (int) v.getTag(R.id.btn_livevideo_question_select_questiona);
            if (isSelect == 1) {
                mAnswerEntityLst.get(position).setStuAnswer("1");
                ((Button) v).setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.shape_question_mulitselect_answer_selected);
            } else {
                mAnswerEntityLst.get(position).setStuAnswer("0");
                ((Button) v).setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
                v.setBackgroundResource(R.drawable.shape_question_mulitselect_answer_normal);
            }
            mAnswer = "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mAnswerEntityLst.size(); i++) {
                View child = gvQuestion.getChildAt(i);
                Button button = (Button) child.findViewById(R.id.btn_livevideo_question_select_questiona);
                isSelect = (Integer) button.getTag();
                if (isSelect != null && isSelect == 1) {
                    sb.append(button.getText());
                    sb.append(",");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            mAnswer = sb.toString();
            baseVideoQuestionEntity.setStuAnswer(mAnswer);
            if ("".equals(mAnswer)) {
                btnSubmit.setEnabled(false);
            } else {
                mAnswer = mAnswer.substring(0, mAnswer.length() - 1);
                btnSubmit.setEnabled(true);
            }
            tvQuestionAnswer.setText(mAnswer);
        }
    }

    /** 提交答案 */
    private class SubmitAnswerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (StringUtils.isSpace(mAnswer)) {
                XESToastUtils.showToast(mContext, "请选择答案");
                return;
            }
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
                putQuestion.onPutQuestionResult(QuestionMulitSelectPortLivePager.this, baseVideoQuestionEntity, mAnswer);
            }
        }
    }

    private class MulitSelectAdapter extends XsBaseAdapter {

        public MulitSelectAdapter(Context context, List list) {
            super(context, list);
        }

        @Override
        public AnswerEntity getItem(int position) {
            return mAnswerEntityLst.get(position);
        }

        @Override
        public int getCount() {
            return mAnswerEntityLst.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                mAnswer = "";
                logger.i( "getView:position=0");
            }
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_selectportquestion, parent, false);
            Button button = (Button) convertView.findViewById(R.id.btn_livevideo_question_select_questiona);
            button.setText("" + ((char) ('A' + position)));
            button.setTag(R.id.btn_livevideo_question_select_questiona, position);
            AnswerEntity answerLiveEntity = getItem(position);
            if ("1".equals(answerLiveEntity.getStuAnswer())) {
                button.setTextColor(Color.WHITE);
                button.setTag(1);
                mAnswer += button.getText() + ",";
                button.setBackgroundResource(R.drawable.shape_question_mulitselect_answer_selected);
            } else {
                button.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
                button.setBackgroundResource(R.drawable.shape_question_mulitselect_answer_normal);
            }
            button.setOnClickListener(new AnswerOnClickListener());
            logger.i( "getView:position=" + position + ",mAnswer=" + mAnswer);
            return convertView;
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

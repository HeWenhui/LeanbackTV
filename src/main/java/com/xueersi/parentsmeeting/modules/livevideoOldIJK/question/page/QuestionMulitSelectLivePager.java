package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;
import com.xueersi.ui.adapter.XsBaseAdapter;
import com.xueersi.ui.widget.button.progressbutton.CircularProgressButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 录播课程选择题互动题页面
 *
 * @author linyuqiang
 */
public class QuestionMulitSelectLivePager extends BaseLiveQuestionPager {
    RelativeLayout rlQuestionContentQuestion;
    /** 答案列表 */
    List<AnswerEntity> mAnswerEntityLst;
    private CircularProgressButton btnSubmit;
    /** 隐藏试题图标布局 */
    RelativeLayout rlDown;
    /** 提交 */
    private String mAnswer = "";
    /** 隐藏试题布局 */
    ImageView mImgDown;
    /** 互动题 */
    private BaseVideoQuestionEntity baseVideoQuestionEntity;
    private RelativeLayout rlQuestionContent;
    private RelativeLayout rlQuestionHide;
    private ImageView ivQuestionVisible;
    private float screenDensity;
    private GridView gvQuestion;

    public QuestionMulitSelectLivePager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity) {
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
        mView = View.inflate(mContext, R.layout.page_livevideo_mulitselectquestion, null);
        rlQuestionContentQuestion = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_content_question);
        rlQuestionContent = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_content);
        rlQuestionHide = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_hide);
        mImgDown = (ImageView) mView.findViewById(R.id.iv_livevideo_question_fillin_down);
        ivQuestionVisible = (ImageView) mView.findViewById(R.id.iv_pop_question_visible);
        rlDown = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_fillin_down);
        gvQuestion = (GridView) mView.findViewById(R.id.gv_livevideo_question_mulitSelect);
        btnSubmit = (CircularProgressButton) mView.findViewById(R.id.btn_livevideo_question_fillin_submit);
        btnSubmit.setIndeterminateProgressMode(true);
        return mView;
    }

    public void initListener() {
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
                btnSubmit.setEnabled(true);
            }
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
                putQuestion.onPutQuestionResult(QuestionMulitSelectLivePager.this, baseVideoQuestionEntity, mAnswer);
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
            }
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_selectquestion, parent, false);
            Button button = (Button) convertView.findViewById(R.id.btn_livevideo_question_select_questiona);
            button.setText("" + ((char) ('A' + position)));
            button.setTag(R.id.btn_livevideo_question_select_questiona, position);
            AnswerEntity answerLiveEntity = getItem(position);
            if ("1".equals(answerLiveEntity.getStuAnswer())) {
                button.setTextColor(Color.WHITE);
                mAnswer += button.getText() + ",";
                button.setBackgroundResource(R.drawable.shape_question_mulitselect_answer_selected);
            } else {
                button.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
                button.setBackgroundResource(R.drawable.shape_question_mulitselect_answer_normal);
            }
            button.setOnClickListener(new AnswerOnClickListener());
            if ("".equals(mAnswer)) {
                btnSubmit.setEnabled(false);
            } else {
                btnSubmit.setEnabled(true);
            }
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

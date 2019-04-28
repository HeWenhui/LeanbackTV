package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.QuesReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;
import com.xueersi.ui.adapter.XsBaseAdapter;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
import com.xueersi.ui.widget.button.progressbutton.CircularProgressButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author linyuqiang 填空题
 */
@SuppressWarnings("rawtypes")
public class QuestionFillInBlankLivePager extends BaseLiveQuestionPager {

    /** 答案列表 */
    List<AnswerEntity> mAnswerEntityLst;
    /** 试题试图 */
    GridView gvFillBlank;
    /** 试题数量 */
    int mQuestionSize;
    /** 隐藏试题布局 */
    ImageView mImgDown;
    /** 填空题个数 */
    int mBlankSize = 0;
    /** 隐藏试题图标布局 */
    RelativeLayout rlDown;
    /** 互动题 */
    BaseVideoQuestionEntity baseVideoQuestionEntity;
    private RelativeLayout rlQuestionContent;
    private RelativeLayout rlQuestionHide;
    private ImageView ivQuestionVisible;
    private View v_livevideo_question_content_bord;
    /** 提交 */
    public CircularProgressButton btnSubmit;

    public QuestionFillInBlankLivePager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity) {
        super(context);
        this.baseVideoQuestionEntity = baseVideoQuestionEntity;
        mAnswerEntityLst = baseVideoQuestionEntity.getAnswerEntityLst();
        mQuestionSize = mAnswerEntityLst.size();
        mBlankSize = baseVideoQuestionEntity.getvBlankSize();
        initData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevodeo_question_fillinblanks, null);
        rlQuestionContent = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_content);
        rlQuestionHide = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_hide);
        gvFillBlank = (GridView) mView.findViewById(R.id.gv_livevideo_question_fillin);
        mImgDown = (ImageView) mView.findViewById(R.id.iv_livevideo_question_fillin_down);
        rlDown = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_fillin_down);
        ivQuestionVisible = (ImageView) mView.findViewById(R.id.iv_pop_question_visible);
        btnSubmit = (CircularProgressButton) mView.findViewById(R.id.btn_livevideo_question_fillin_submit);
        v_livevideo_question_content_bord = mView.findViewById(R.id.v_livevideo_question_content_bord);
//        btnSubmit.setEnabled(false);
        return mView;
    }

    @Override
    public void initData() {
        FillBlankAdapter fillBlankAdapter = new FillBlankAdapter(mContext, mAnswerEntityLst);
        gvFillBlank.setAdapter(fillBlankAdapter);
        rlDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputMode();
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
        btnSubmit.setOnClickListener(submitClickListener);
        ViewGroup group = (ViewGroup) btnSubmit.getParent();
        if (mBlankSize < 5) {
            ViewGroup.LayoutParams lp = group.getLayoutParams();
            lp.height = (int) (ScreenUtils.getScreenDensity() * 60);
//            group.setLayoutParams(lp);
            LayoutParamsUtil.setViewLayoutParams(group, lp);
            ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) btnSubmit.getLayoutParams();
            lp2.topMargin = (int) (ScreenUtils.getScreenDensity() * 10);
//            btnSubmit.setLayoutParams(lp2);
            LayoutParamsUtil.setViewLayoutParams(btnSubmit, lp2);
        }
    }

    private class FillBlankAdapter extends XsBaseAdapter {

        public FillBlankAdapter(Context context, List list) {
            super(context, list);
        }

        @Override
        public AnswerEntity getItem(int position) {
            return mAnswerEntityLst.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final BlankViewHolder holder;
            AnswerEntity question = mAnswerEntityLst.get(position);
            if (convertView == null) {
                holder = new BlankViewHolder();
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_fillinquestion_input, parent, false);
                holder.etFillBlank = (EditText) view.findViewById(R.id.et_livevideo_question_fillin_input);
                convertView = view;
                convertView.setTag(holder);
            } else {
                holder = (BlankViewHolder) convertView.getTag();
            }
            holder.etFillBlank.setOnClickListener(editClickListener);
            holder.etFillBlank.setHint((position + 1) + ".");
            // holder.etFillBlank.setOnFocusChangeListener(editOnFocusChangeListener);
            TextWatcher textWatcher = (TextWatcher) holder.etFillBlank.getTag(R.id.et_livevideo_question_fillin_input);
            if (textWatcher != null) {
                holder.etFillBlank.removeTextChangedListener(textWatcher);
            }
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable edit) {
                    //TODO removeTextChangedListener后，应该不会发生了
//                        if (!holder.etFillBlank.getTag().equals(position)) {
//                            logger.i( "getView:position=" + position + "," + holder.etFillBlank.getTag() + ",afterTextChanged:edit=" + edit);
//                            return;
//                        }
                    // 保存答案
                    mAnswerEntityLst.get(position).setStuAnswer(edit.toString());
//                    if (TextUtils.isEmpty(chkReslut())) {
//                        btnSubmit.setEnabled(false);
//                    } else {
//                        btnSubmit.setEnabled(true);
//                    }
                }
            };
            holder.etFillBlank.setTag(R.id.et_livevideo_question_fillin_input, textWatcher);
            holder.etFillBlank.addTextChangedListener(textWatcher);
            holder.etFillBlank.setTag(position);
            // 如果题目数是偶数并且是倒数第二个则隐藏
            if (question.getvQuestionInvisiable() == View.INVISIBLE) {
                holder.etFillBlank.setVisibility(View.INVISIBLE);
            } else {
                holder.etFillBlank.setVisibility(View.VISIBLE);
                holder.etFillBlank.setText(mAnswerEntityLst.get(position).getStuAnswer());
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return mAnswerEntityLst.size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    /**
     * 填空题上移
     */
    private OnClickListener editClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            gvFillBlank.smoothScrollToPosition((Integer) v.getTag());
        }
    };


    /**
     * 提交填空题答案
     */
    private OnClickListener submitClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final QuesReslutEntity quesReslutEntity = chkReslut();
            if (quesReslutEntity.isHaveEmpty()) {
                VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(mContext, (BaseApplication) BaseApplication.getContext(), false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                cancelDialog.setVerifyBtnListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commit(quesReslutEntity);
                    }
                });
                cancelDialog.initInfo("题目尚未做完，是否确认提交？").showDialog();
            } else {
                commit(quesReslutEntity);
            }
        }
    };

    private void commit(QuesReslutEntity quesReslutEntity) {
        hideInputMode();
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
            mLogtf.d("commit:result=" + quesReslutEntity.getResult());
            putQuestion.onPutQuestionResult(this, baseVideoQuestionEntity, quesReslutEntity.getResult());
        }
    }

    /** 填空题校验 */
    private QuesReslutEntity chkReslut() {
        QuesReslutEntity quesReslutEntity = new QuesReslutEntity();
        String[] result = new String[mBlankSize];
        for (int i = 0; i < mBlankSize; i++) {
            String stuAnswer = mAnswerEntityLst.get(i).getStuAnswer();
            if (StringUtils.isSpace(stuAnswer)) {
                quesReslutEntity.setHaveEmpty(true);
            }
            result[i] = stuAnswer.trim();
        }
        quesReslutEntity.setResult(JSONObject.toJSONString(result));
        return quesReslutEntity;
    }

    /**
     * 复用的填空题布局
     */
    class BlankViewHolder {
        /** 填空题 */
        public EditText etFillBlank;
    }

    /** 键盘隐藏 */
    public void hideInputMode() {

        // 隐藏虚拟键盘
        InputMethodManager inputmanger = (InputMethodManager) mImgDown.getContext().getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(mImgDown.getWindowToken(), 0);

    }

    boolean keyIsShowing;

    @Override
    public void onKeyboardShowing(boolean isShowing) {
//        keyIsShowing = isShowing;
//        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v_livevideo_question_content_bord.getLayoutParams();
//        final int bottomMargin;
//        if (isShowing) {
//            bottomMargin = KeyboardUtil.getValidPanelHeight(mContext);
//        } else {
//            bottomMargin = 0;
//        }
//        if (bottomMargin != lp.height) {
//            ValueAnimator valueAnimator = ValueAnimator.ofInt(bottomMargin);
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    if (keyIsShowing) {
//                        float fraction = valueAnimator.getAnimatedFraction();
//                        lp.height = (int) (bottomMargin * fraction);
//                    } else {
//                        lp.height = 0;
//                        valueAnimator.cancel();
//                    }
//                    LayoutParamsUtil.setViewLayoutParams(v_livevideo_question_content_bord, lp);
//                }
//            });
//            valueAnimator.setDuration(100);
//            valueAnimator.start();
//        }
    }

    @Override
    public void onKeyboardShowing(boolean isShowing, int height) {
//        keyIsShowing = isShowing;
//        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v_livevideo_question_content_bord.getLayoutParams();
//        final int bottomMargin;
//        if (isShowing) {
//            bottomMargin = height;
//        } else {
//            bottomMargin = 0;
//        }
//        if (bottomMargin != lp.height) {
//            ValueAnimator valueAnimator = ValueAnimator.ofInt(bottomMargin);
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    if (keyIsShowing) {
//                        float fraction = valueAnimator.getAnimatedFraction();
//                        lp.height = (int) (bottomMargin * fraction);
//                    } else {
//                        lp.height = 0;
//                        valueAnimator.cancel();
//                    }
//                    LayoutParamsUtil.setViewLayoutParams(v_livevideo_question_content_bord, lp);
//                }
//            });
//            valueAnimator.setDuration(100);
//            valueAnimator.start();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            hideInputMode();
        } catch (Exception ex) {
            ex.printStackTrace();
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

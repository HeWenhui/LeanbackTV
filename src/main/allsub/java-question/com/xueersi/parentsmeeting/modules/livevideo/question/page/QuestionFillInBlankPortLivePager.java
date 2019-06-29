package com.xueersi.parentsmeeting.modules.livevideo.question.page;

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
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.QuesReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.ui.adapter.XsBaseAdapter;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
import com.xueersi.ui.widget.button.progressbutton.CircularProgressButton;

import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * @author linyuqiang 填空题
 */
@SuppressWarnings("rawtypes")
public class QuestionFillInBlankPortLivePager extends BaseLiveQuestionPager {

    /** 答案列表 */
    List<AnswerEntity> mAnswerEntityLst;
    /** 试题试图 */
    ListView gvFillBlank;
    /** 试题数量 */
    int mQuestionSize;
    /** 填空题个数 */
    int mBlankSize = 0;
    /** 互动题 */
    VideoQuestionLiveEntity videoQuestionLiveEntity;
    /** 提交 */
    public CircularProgressButton btnSubmit;

    public QuestionFillInBlankPortLivePager(Context context, VideoQuestionLiveEntity videoQuestionLiveEntity) {
        super(context);
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        mAnswerEntityLst = videoQuestionLiveEntity.getAnswerEntityLst();
        mQuestionSize = mAnswerEntityLst.size();
        mBlankSize = (int) videoQuestionLiveEntity.num;
        initData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevodeo_question_fillinblanks_port, null);
        gvFillBlank = (ListView) mView.findViewById(R.id.lv_livevideo_question_fillin);
        btnSubmit = (CircularProgressButton) mView.findViewById(R.id.btn_livevideo_question_fillin_submit);
//        btnSubmit.setEnabled(false);
        return mView;
    }

    @Override
    public void initData() {
        btnSubmit.setOnClickListener(submitClickListener);
        FillBlankAdapter fillBlankAdapter = new FillBlankAdapter(mContext, mAnswerEntityLst);
        gvFillBlank.setAdapter(fillBlankAdapter);
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                logger.i( "onViewAttachedToWindow:v=" + (v == mView));
                KeyboardUtil.registKeyboardShowingListener(onKeyboardShowingListener);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                logger.i( "onViewDetachedFromWindow:v=" + (v == mView));
                mView.removeOnAttachStateChangeListener(this);
                KeyboardUtil.unRegistKeyboardShowingListener(onKeyboardShowingListener);
            }
        });
    }

    /** 上次键盘是不是弹出 */
    private boolean lastShowing = false;
    /** 上次gvFillBlank scrollBy的距离 */
    private int scrollBy;
    /** 注册键盘显示隐藏 */
    private KeyboardUtil.OnKeyboardShowingListener onKeyboardShowingListener = new KeyboardUtil.OnKeyboardShowingListener() {
        @Override
        public void onKeyboardShowing(boolean isShowing) {
            if (lastShowing != isShowing) {
                lastShowing = isShowing;
                View focused = gvFillBlank.getFocusedChild();
                logger.i( "onKeyboardShowing:isShowing=" + isShowing + ",KeyboardHeight=" + KeyboardUtil.getKeyboardHeight(mContext));
                if (isShowing) {
                    if (focused != null) {
                        scrollBy = focused.getTop();
                        logger.i( "onKeyboardShowing:focused=" + scrollBy);
                        gvFillBlank.scrollBy(0, scrollBy);
                    }
                } else {
                    gvFillBlank.scrollBy(0, -scrollBy);
                }
            }
        }
    };

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
            final AnswerEntity question = mAnswerEntityLst.get(position);
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
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void afterTextChanged(Editable edit) {
                    //TODO removeTextChangedListener后，应该不会发生了
//                    if (!holder.etFillBlank.getTag().equals(position)) {
//                        logger.i( "getView:position=" + position + "," + holder.etFillBlank.getTag() + ",afterTextChanged:edit=" + edit);
//                        return;
//                    }
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
            holder.etFillBlank.setVisibility(View.VISIBLE);
            holder.etFillBlank.setText(question.getStuAnswer());
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
        if (btnSubmit.getProgress() == 0) {
            btnSubmit.setProgress(50);
        } else if (btnSubmit.getProgress() == 100) {
            btnSubmit.setProgress(0);
        } else {
            btnSubmit.setProgress(100);
        }
        if (putQuestion != null) {
            putQuestion.onPutQuestionResult(this, videoQuestionLiveEntity, quesReslutEntity.getResult());
        }
    }

    public VideoQuestionLiveEntity getInterQues() {
        return videoQuestionLiveEntity;

    }

    /** 填空题校验 */
    private QuesReslutEntity chkReslut() {
        QuesReslutEntity quesReslutEntity = new QuesReslutEntity();
        String[] result = new String[mBlankSize];
        for (int i = 0; i < mBlankSize; i++) {
            String stuAnswer = "" + mAnswerEntityLst.get(i).getStuAnswer();
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
        EditText etFillBlank;
    }

    /** 键盘隐藏 */
    public void hideInputMode() {
        // 隐藏虚拟键盘
        InputMethodManager inputmanger = (InputMethodManager) mView.getContext().getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(mView.getWindowToken(), 0);
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

package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.QuesReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.BigResultLog;
import com.xueersi.ui.adapter.XsBaseAdapter;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * Created by linyuqiang on 2019/4/15.  大题互动填空题
 */
@SuppressWarnings("rawtypes")
public class BigQuestionFillInBlankLivePager extends BaseLiveBigQuestionPager implements KeyboardUtil.OnKeyboardShowingListener {

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
    /** 用户答案 */
    private ArrayList<String> answers = new ArrayList<>();
    /** 隐藏试题图标布局 */
    RelativeLayout rlDown;
    /** 互动题 */
//    BaseVideoQuestionEntity baseVideoQuestionEntity;
    private RelativeLayout rlQuestionContent;
    private RelativeLayout rlQuestionHide;
    private ImageView ivQuestionVisible;
    private KPSwitchFSPanelLinearLayout v_livevideo_question_content_bord;
    /** 提交 */
    public Button btnSubmit;
    private long startTime;
    private final int MAX_CHAR_NUM = 20;
    private BigResultPager resultPager;

    public BigQuestionFillInBlankLivePager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity, boolean isPlayback) {
        super(context);
        videoQuestionLiveEntity = baseVideoQuestionEntity;
        this.baseVideoQuestionEntity = baseVideoQuestionEntity;
        mAnswerEntityLst = baseVideoQuestionEntity.getAnswerEntityLst();
        mQuestionSize = mAnswerEntityLst.size();
        mBlankSize = baseVideoQuestionEntity.getvBlankSize();
        this.isPlayback = isPlayback;
        BigResultLog.sno4(baseVideoQuestionEntity, getLiveAndBackDebug());
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevodeo_question_big_fillinblanks, null);
        rlQuestionContent = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_content);
        rlQuestionHide = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_hide);
        gvFillBlank = (GridView) mView.findViewById(R.id.gv_livevideo_question_fillin);
        mImgDown = (ImageView) mView.findViewById(R.id.iv_livevideo_question_fillin_down);
        rlDown = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_question_fillin_down);
        ivQuestionVisible = (ImageView) mView.findViewById(R.id.iv_pop_question_visible);
        btnSubmit = mView.findViewById(R.id.btn_livevideo_question_fillin_submit);
        v_livevideo_question_content_bord = mView.findViewById(R.id.v_livevideo_question_content_bord);
//        btnSubmit.setEnabled(false);
        return mView;
    }

    @Override
    public void initData() {
        startTime = System.currentTimeMillis();
        gvFillBlank.setNumColumns(mBlankSize);
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
        ViewGroup.LayoutParams lp = gvFillBlank.getLayoutParams();
        float width = mContext.getResources().getDimension(R.dimen.livevideo_big_fillin_allwidth);
        lp.width = (int) (width * mBlankSize);
        logger.d("initData:width=" + width + ",all=" + lp.width);
        LayoutParamsUtil.setViewLayoutParams(gvFillBlank, lp);
        if (isPlayback) {
            KeyboardUtil.attach((Activity) mContext, v_livevideo_question_content_bord, new KeyboardUtil.OnKeyboardShowingListener() {
                @Override
                public void onKeyboardShowing(boolean isShowing) {
                    BigQuestionFillInBlankLivePager.this.onKeyboardShowing(isShowing);
                }
            });
        } else {
            KeyboardUtil.registKeyboardShowingListener(this);
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
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_fillin_bigquestion_input, parent, false);
                holder.tvInputIndex = view.findViewById(R.id.tv_livevideo_question_fillin_input_index);
                holder.etFillBlank = view.findViewById(R.id.et_livevideo_question_fillin_input);
                convertView = view;
                convertView.setTag(holder);
            } else {
                holder = (BlankViewHolder) convertView.getTag();
            }
            holder.tvInputIndex.setText((position + 1) + "");
            holder.etFillBlank.setHint("请输入");
            // holder.etFillBlank.setOnFocusChangeListener(editOnFocusChangeListener);
            TextWatcher textWatcher = (TextWatcher) holder.etFillBlank.getTag(R.id.et_livevideo_question_fillin_input);
            if (textWatcher != null) {
                holder.etFillBlank.removeTextChangedListener(textWatcher);
            }
            holder.etFillBlank.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v_livevideo_question_content_bord.getVisibility() == View.VISIBLE) {
                        return false;
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            onKeyboardShowing(true);
                            KPSwitchConflictUtil.showKeyboard(v_livevideo_question_content_bord, holder.etFillBlank);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            });
//            holder.etFillBlank.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (v_livevideo_question_content_bord.getVisibility() == View.VISIBLE) {
//
//                    } else {
//                        onKeyboardShowing(true);
//                        KPSwitchConflictUtil.showKeyboard(v_livevideo_question_content_bord, holder.etFillBlank);
//                    }
//                }
//            });
            textWatcher = new TextWatcher() {
                int selectionEnd;
                boolean isDelete = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (!isDelete) {
                        selectionEnd = holder.etFillBlank.getSelectionEnd();
                    }
                    logger.d("beforeTextChanged:selectionEnd=" + selectionEnd);
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
                    int charSequenceCount = countChineseChar(edit);
                    logger.d("afterTextChanged:" + edit + ",count=" + charSequenceCount);
                    if (edit.length() + charSequenceCount > MAX_CHAR_NUM) {
                        isDelete = true;
                        holder.etFillBlank.setText(edit.subSequence(0, edit.length() - 1));
                        return;
                    }
                    if (isDelete) {
                        holder.etFillBlank.setSelection(selectionEnd);
                    }
                    isDelete = false;
                    mAnswerEntityLst.get(position).setStuAnswer(edit.toString());
                    QuesReslutEntity quesReslutEntity = chkReslut();
                    if (quesReslutEntity.isHaveEmpty()) {
                        btnSubmit.setEnabled(false);
                    } else {
                        btnSubmit.setEnabled(true);
                    }
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
                if (!TextUtils.equals(holder.etFillBlank.getText(), mAnswerEntityLst.get(position).getStuAnswer())) {
                    holder.etFillBlank.setText(mAnswerEntityLst.get(position).getStuAnswer());
                }
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
        submitBigTestInteraction(0);
    }

    private void submitBigTestInteraction(final int isForce) {
        mLogtf.d("submitBigTestInteraction:isForce=" + isForce + ",resultPager=null?" + (resultPager == null));
        if (resultPager != null) {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rlQuestionResContent.removeView(resultPager.getRootView());
                    onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
                }
            }, LiveQueConfig.BIG_TEST_CLOSE);
            return;
        }
        //回放不强制提交
        if (isForce == 1 && isPlayback) {
            onSubmit.onSubmit(BigQuestionFillInBlankLivePager.this);
            onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
            return;
        }
        final JSONArray userAnswer = new JSONArray();
        if (answers.isEmpty()) {
            userAnswer.put("");
        } else {
            for (int i = 0; i < answers.size(); i++) {
                userAnswer.put(answers.get(i));
            }
        }
        KeyboardUtil.hideKeyboard(getRootView());
        questionSecHttp.submitBigTestInteraction(videoQuestionLiveEntity, userAnswer, startTime, isForce, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                BigResultLog.sno5(videoQuestionLiveEntity, userAnswer, true, getLiveAndBackDebug());
                org.json.JSONObject jsonObject = (org.json.JSONObject) objData[0];
                int toAnswered = jsonObject.optInt("toAnswered");
                if (toAnswered == 2) {
                    XESToastUtils.showToast(mContext, "已作答");
                    onSubmit.onSubmit(BigQuestionFillInBlankLivePager.this);
                    onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
                    return;
                }
                questionSecHttp.getStuInteractionResult(videoQuestionLiveEntity, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        BigResultLog.sno6(videoQuestionLiveEntity, objData[1], true, getLiveAndBackDebug());
                        BigResultEntity bigResultEntity = (BigResultEntity) objData[0];
                        showResult(bigResultEntity, isForce);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        BigResultLog.sno6(videoQuestionLiveEntity, failMsg, false, getLiveAndBackDebug());
                        XESToastUtils.showToast(mContext, failMsg);
                        if (isForce == 1) {
                            onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
                        }
                    }
                });
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                BigResultLog.sno5(videoQuestionLiveEntity, userAnswer, false, getLiveAndBackDebug());
                XESToastUtils.showToast(mContext, failMsg);
                if (isForce == 1) {
                    onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
                }
//                onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
//                if (com.xueersi.common.config.AppConfig.DEBUG) {
//                    BigResultEntity bigResultEntity = new BigResultEntity();
//                    ArrayList<BigResultItemEntity> bigResultEntities = bigResultEntity.getBigResultItemEntityArrayList();
//                    for (int i = 0; i < 10; i++) {
//                        BigResultItemEntity bigResultItemEntity = new BigResultItemEntity();
//                        bigResultItemEntity.standAnswer = "A啊A啊A啊A啊";
//                        bigResultItemEntity.youAnswer = "A啊A啊A啊A啊";
//                        if (i % 2 == 0) {
//                            bigResultItemEntity.rightType = LiveQueConfig.DOTTYPE_ITEM_RESULT_RIGHT;
//                        } else {
//                            bigResultItemEntity.rightType = LiveQueConfig.DOTTYPE_ITEM_RESULT_WRONG;
//                        }
//                        bigResultEntities.add(bigResultItemEntity);
//                    }
//                    showResult(bigResultEntity);
//                }
            }
        });
    }

    private void showResult(BigResultEntity bigResultEntity, int isForce) {
        mView.setVisibility(View.GONE);
        onSubmit.onSubmit(this);
        resultPager = new BigResultPager(mContext, rlQuestionResContent, bigResultEntity);
        rlQuestionResContent.addView(resultPager.getRootView());
        resultPager.setOnPagerClose(new OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                rlQuestionResContent.removeView(basePager.getRootView());
                onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
            }
        });
        //强制提交，结果页关闭
        if (isForce == 1) {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (resultPager.isAttach()) {
                        rlQuestionResContent.removeView(resultPager.getRootView());
                        onPagerClose.onClose(BigQuestionFillInBlankLivePager.this);
                    }
                }
            }, LiveQueConfig.BIG_TEST_CLOSE);
        }
    }

    /** 填空题校验 */
    private QuesReslutEntity chkReslut() {
        QuesReslutEntity quesReslutEntity = new QuesReslutEntity();
        String[] result = new String[mBlankSize];
        answers.clear();
        for (int i = 0; i < mBlankSize; i++) {
            String stuAnswer = mAnswerEntityLst.get(i).getStuAnswer();
            if (StringUtils.isSpace(stuAnswer)) {
                quesReslutEntity.setHaveEmpty(true);
                result[i] = "";
            } else {
                result[i] = stuAnswer.trim();
            }
            answers.add(result[i]);
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
        TextView tvInputIndex;
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
        keyIsShowing = isShowing;
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v_livevideo_question_content_bord.getLayoutParams();
        final int bottomMargin;
        if (isShowing) {
            v_livevideo_question_content_bord.setVisibility(View.VISIBLE);
            bottomMargin = KeyboardUtil.getValidPanelHeight(mContext);
        } else {
            v_livevideo_question_content_bord.setVisibility(View.GONE);
            bottomMargin = 0;
        }
        if (bottomMargin != lp.height) {
            lp.height = bottomMargin;
            LayoutParamsUtil.setViewLayoutParams(v_livevideo_question_content_bord, lp);
        }
        logger.d("onKeyboardShowing:isShowing=" + isShowing + ",bottomMargin=" + bottomMargin);
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
    public void submitData() {
        super.submitData();
        submitBigTestInteraction(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isPlayback) {

        } else {
            KeyboardUtil.unRegistKeyboardShowingListener(this);
        }
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

    }

    /**
     * 提交互动题失败
     */
    public void onSubFailure() {

    }

    /**
     *      * 计算中文字符
     *      *
     *      * @param sequence
     *      * @return
     *     
     */
    private int countChineseChar(CharSequence sequence) {

        if (TextUtils.isEmpty(sequence)) {
            return 0;
        }
        int charNum = 0;
        for (int i = 0; i < sequence.length(); i++) {
            char word = sequence.charAt(i);
            if (isChineseChar(word)) {//中文
                charNum++;
            }
        }
        return charNum;
    }

    /**
     *      * 判断是否是中文
     *      * @param c
     *      * @return
     *     
     */
    public static boolean isChineseChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
}

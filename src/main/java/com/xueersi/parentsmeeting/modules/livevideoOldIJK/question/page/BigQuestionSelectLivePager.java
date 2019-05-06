package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.ui.adapter.XsBaseAdapter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 录播课程选择题互动题页面
 *
 * @author hua
 */
public class BigQuestionSelectLivePager extends BaseLiveBigQuestionPager {
    private RelativeLayout rlQuestionContentQuestion;
    /** 答案列表 */
    private List<AnswerEntity> mAnswerEntityLst;
    private Button btnSubmit;
    /** 隐藏试题图标布局 */
    private RelativeLayout rlDown;
    /** 用户答案 */
    private ArrayList<String> answers = new ArrayList<>();
    /** 隐藏试题布局 */
    private ImageView mImgDown;
    private RelativeLayout rlQuestionContent;
    private RelativeLayout rlQuestionHide;
    private ImageView ivQuestionVisible;
    private GridView gvQuestion;
    private int dotType;
    private long startTime;
    private BigResultPager resultPager;

    public BigQuestionSelectLivePager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity) {
        super(context);
        videoQuestionLiveEntity = baseVideoQuestionEntity;
        this.baseVideoQuestionEntity = baseVideoQuestionEntity;
        mAnswerEntityLst = baseVideoQuestionEntity.getAnswerEntityLst();
        dotType = baseVideoQuestionEntity.getDotType();
        //测试多选题
//        mAnswerEntityLst.addAll(mAnswerEntityLst);
//        mAnswerEntityLst.addAll(mAnswerEntityLst);
//        for (int i = 0; i < mAnswerEntityLst.size(); i++) {
//            if (i % 2 == 0) {
//                mAnswerEntityLst.get(i).setStuAnswer("1");
//            }
//        }
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevodeo_question_big_fillinblanks, null);
        rlQuestionContentQuestion = mView.findViewById(R.id.rl_livevideo_question_content_question);
        rlQuestionContent = mView.findViewById(R.id.rl_livevideo_question_content);
        rlQuestionHide = mView.findViewById(R.id.rl_livevideo_question_hide);
        mImgDown = mView.findViewById(R.id.iv_livevideo_question_fillin_down);
        ivQuestionVisible = mView.findViewById(R.id.iv_pop_question_visible);
        rlDown = mView.findViewById(R.id.rl_livevideo_question_fillin_down);
        gvQuestion = mView.findViewById(R.id.gv_livevideo_question_fillin);
        btnSubmit = mView.findViewById(R.id.btn_livevideo_question_fillin_submit);
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
        startTime = System.currentTimeMillis();
        int size = mAnswerEntityLst.size();
        gvQuestion.setNumColumns(size);
        MulitSelectAdapter mulitSelectAdapter = new MulitSelectAdapter(mContext, mAnswerEntityLst);
        gvQuestion.setAdapter(mulitSelectAdapter);
        ViewGroup.LayoutParams lp = gvQuestion.getLayoutParams();
        float width = mContext.getResources().getDimension(R.dimen.livevideo_big_select_allwidth);
        lp.width = (int) (width * size);
        logger.d("initData:width=" + width + ",all=" + lp.width);
        LayoutParamsUtil.setViewLayoutParams(gvQuestion, lp);
    }

    private Button lastBt;

    /** 答案选择-单选 */
    private class AnswerSelOnClickListener implements OnClickListener {

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
            Button thisButton = (Button) v;
            int position = (int) v.getTag(R.id.btn_livevideo_question_select_questiona);
            if (isSelect == 1) {
                if (lastBt != null) {
                    lastBt.setTag(0);
                    int lastPosition = (int) lastBt.getTag(R.id.btn_livevideo_question_select_questiona);
                    mAnswerEntityLst.get(lastPosition).setStuAnswer("0");
                    lastBt.setTextColor(mContext.getResources().getColor(R.color.white));
                    lastBt.setBackgroundResource(R.drawable.shape_big_question_select_answer_normal);
                }
                mAnswerEntityLst.get(position).setStuAnswer("1");
                thisButton.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
                thisButton.setBackgroundResource(R.drawable.shape_big_question_select_answer_sel);
                lastBt = thisButton;
            } else {
                mAnswerEntityLst.get(position).setStuAnswer("0");
                thisButton.setTextColor(mContext.getResources().getColor(R.color.white));
                thisButton.setBackgroundResource(R.drawable.shape_big_question_select_answer_normal);
                if (lastBt == thisButton) {
                    lastBt = null;
                }
            }
            answers.clear();
            for (int i = 0; i < mAnswerEntityLst.size(); i++) {
                View child = gvQuestion.getChildAt(i);
                Button button = child.findViewById(R.id.btn_livevideo_question_select_questiona);
                isSelect = (Integer) button.getTag();
                if (isSelect != null && isSelect == 1) {
                    answers.add("" + button.getText());
                }
            }
            if (answers.isEmpty()) {
                btnSubmit.setEnabled(false);
            } else {
                btnSubmit.setEnabled(true);
            }
        }
    }

    /** 答案选择- 多选 */
    private class AnswerMulSelOnClickListener implements OnClickListener {

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
                ((Button) v).setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
                v.setBackgroundResource(R.drawable.shape_big_question_select_answer_sel);
            } else {
                mAnswerEntityLst.get(position).setStuAnswer("0");
                ((Button) v).setTextColor(mContext.getResources().getColor(R.color.white));
                v.setBackgroundResource(R.drawable.shape_big_question_select_answer_normal);
            }
            answers.clear();
            for (int i = 0; i < mAnswerEntityLst.size(); i++) {
                View child = gvQuestion.getChildAt(i);
                Button button = child.findViewById(R.id.btn_livevideo_question_select_questiona);
                isSelect = (Integer) button.getTag();
                if (isSelect != null && isSelect == 1) {
                    answers.add("" + button.getText());
                }
            }
            if (answers.isEmpty()) {
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
            if (answers.isEmpty()) {
                XESToastUtils.showToast(mContext, "请选择答案");
                return;
            }
            submitBigTestInteraction(0);
        }
    }

    private void submitBigTestInteraction(final int isForce) {
        mLogtf.d("submitBigTestInteraction:isForce=" + isForce + ",resultPager=null?" + (resultPager == null));
        if (resultPager != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (resultPager.isAttach()) {
                        rlQuestionResContent.removeView(resultPager.getRootView());
                        onPagerClose.onClose(BigQuestionSelectLivePager.this);
                    }
                }
            }, LiveQueConfig.BIG_TEST_CLOSE);
            return;
        }
        JSONArray userAnswer = new JSONArray();
        if (answers.isEmpty()) {
            userAnswer.put("");
        } else {
            for (int i = 0; i < answers.size(); i++) {
                userAnswer.put(answers.get(i));
            }
        }
        questionSecHttp.submitBigTestInteraction(videoQuestionLiveEntity, userAnswer, startTime, isForce, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                JSONObject jsonObject = (JSONObject) objData[0];
                int toAnswered = jsonObject.optInt("toAnswered");
                if (toAnswered == 2) {
                    XESToastUtils.showToast(mContext, "已作答");
                    onPagerClose.onClose(BigQuestionSelectLivePager.this);
                    return;
                }
                questionSecHttp.getStuInteractionResult(videoQuestionLiveEntity, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        BigResultEntity bigResultEntity = (BigResultEntity) objData[0];
                        showResult(bigResultEntity, isForce);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        XESToastUtils.showToast(mContext, failMsg);
                        if (isForce == 1) {
                            onPagerClose.onClose(BigQuestionSelectLivePager.this);
                        }
                    }
                });
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                XESToastUtils.showToast(mContext, failMsg);
                if (isForce == 1) {
                    onPagerClose.onClose(BigQuestionSelectLivePager.this);
                }
//                if (com.xueersi.common.config.AppConfig.DEBUG) {
//                    BigResultEntity bigResultEntity = new BigResultEntity();
//                    ArrayList<BigResultItemEntity> bigResultEntities = bigResultEntity.getBigResultItemEntityArrayList();
//                    for (int i = 0; i < 10; i++) {
//                        BigResultItemEntity bigResultItemEntity = new BigResultItemEntity();
//                        bigResultItemEntity.standAnswer = "A啊A啊A啊A啊";
//                        bigResultItemEntity.youAnswer = "A啊A啊A啊A啊";
//                        bigResultItemEntity.standAnswer = "A啊";
//                        bigResultItemEntity.youAnswer = "A啊";
//                        if (i % 2 == 0) {
//                            bigResultItemEntity.rightType = LiveQueConfig.DOTTYPE_ITEM_RESULT_RIGHT;
//                        } else {
//                            bigResultItemEntity.rightType = LiveQueConfig.DOTTYPE_ITEM_RESULT_WRONG;
//                        }
//                        bigResultEntities.add(bigResultItemEntity);
//                    }
//                    showResult(bigResultEntity);
//                }
//                onPagerClose.onClose(BigQuestionSelectLivePager.this);
            }
        });
    }

    private void showResult(BigResultEntity bigResultEntity, int isForce) {
        mView.setVisibility(View.GONE);
        onSubmit.onSubmit(this);
        resultPager = new BigResultPager(mContext, rlQuestionResContent, bigResultEntity);
        rlQuestionResContent.addView(resultPager.getRootView());
        resultPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                rlQuestionResContent.removeView(basePager.getRootView());
                onPagerClose.onClose(BigQuestionSelectLivePager.this);
            }
        });
        if (isForce == 1) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (resultPager.isAttach()) {
                        rlQuestionResContent.removeView(resultPager.getRootView());
                        onPagerClose.onClose(BigQuestionSelectLivePager.this);
                    }
                }
            }, LiveQueConfig.BIG_TEST_CLOSE);
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
                answers.clear();
            }
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_select_bigquestion, parent, false);
            Button button = convertView.findViewById(R.id.btn_livevideo_question_select_questiona);
            button.setText("" + ((char) ('A' + position)));
            button.setTag(R.id.btn_livevideo_question_select_questiona, position);
            AnswerEntity answerLiveEntity = getItem(position);
            if ("1".equals(answerLiveEntity.getStuAnswer())) {
                button.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
                button.setBackgroundResource(R.drawable.shape_big_question_select_answer_sel);
                answers.add("" + button.getText());
            } else {
                button.setTextColor(mContext.getResources().getColor(R.color.white));
                button.setBackgroundResource(R.drawable.shape_big_question_select_answer_normal);
            }
            if (dotType == LiveQueConfig.DOTTYPE_SELE) {
                button.setOnClickListener(new AnswerSelOnClickListener());
            } else {
                button.setOnClickListener(new AnswerMulSelOnClickListener());
            }
            if (answers.isEmpty()) {
                btnSubmit.setEnabled(false);
            } else {
                btnSubmit.setEnabled(true);
            }
            return convertView;
        }
    }

    @Override
    public void submitData() {
        submitBigTestInteraction(1);
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
}

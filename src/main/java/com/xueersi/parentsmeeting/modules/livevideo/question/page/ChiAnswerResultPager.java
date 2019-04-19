package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerResultStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.IArtsAnswerRsultDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * FileName: ChiAnswerResultPager
 * Author: WangDe
 * Date: 2019/4/8 18:28
 * Description: ${DESCRIPTION}
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class ChiAnswerResultPager extends BasePager implements IArtsAnswerRsultDisplayer {


    private View resultDetailRootView;
    private ImageView ivClose;
    private RecyclerView recyclerView;
    private AnswerResultAdapter mAdapter;
    private static final int SPAN_COUNT = 1;


    private static final int RESULT_TYPE_CORRECT = 2;
    private static final int RESULT_TYPE_PART_CORRECT = 1;
    private static final int RESULT_TYPE_ERRRO = 0;

    private int mReusltType;

    private static final String BG_COLOR = "#CC000000";
    private ChineseAISubjectResultEntity mData;
    private final AnswerResultStateListener mStateListenr;

    /**
     * 金币数量
     */
    TextView tvGoldCount;

    /**
     * 奖励布局
     */
    LinearLayout llRewardInfo;
    private TextView tvScore;

    public ChiAnswerResultPager(Context context, ChineseAISubjectResultEntity data, AnswerResultStateListener listener) {
        super(context);
        mData = data;
        mStateListenr = listener;
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_chi_anwserresult_ai, null);
        resultDetailRootView = view.findViewById(R.id.rl_chi_answer_result_resultdetail);
        tvGoldCount = view.findViewById(R.id.tv_chi_normal_answer_result_gold_count);
        llRewardInfo = view.findViewById(R.id.ll_chi_normal_answer_result_gold_info);
        tvScore = view.findViewById(R.id.tv_chi_answer_result_score);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getMeasuredWidth() > 0) {
                    showAnswerReuslt();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }

            }
        });

        return view;
    }


    @Override
    public void initData() {

    }


    /**
     * 显示 提示学生提交答题 toast
     */
    @Override
    public void remindSubmit() {

    }

    @Override
    public View getRootLayout() {

        return this.getRootView();
    }


    private class ItemHolder extends RecyclerView.ViewHolder {


        private TextView tvRightAnswer;
        private TextView tvUserAnswer;

        ImageView ivAnswerIcon;


        public ItemHolder(View itemView) {
            super(itemView);
            tvUserAnswer = itemView.findViewById(R.id.tv_chi_answer_result_item_muti_user_answer);

            tvRightAnswer = itemView.findViewById(R.id.tv_chi_answer_result_item_muti_right_answer);

            ivAnswerIcon = itemView.findViewById(R.id.iv_chi_answer_result_item_muti_right_answer);
        }

        public void bindData(List<String> rightData,List<ChineseAISubjectResultEntity.StuAnswer> stuData, int position) {
//            if (mData.getAnswerList().size() > 1) {
//                tvIndex.setText((position + 1) + ".");
//                tvIndex.setVisibility(View.VISIBLE);
//            } else {
//                tvIndex.setVisibility(View.GONE);
//            }
            String standerAnswerText = "";
            SpannableStringBuilder myAnswerText = markScoreKey(stuData, ";");
            standerAnswerText = listToStr(rightData, ";");

            int iconResId = 0;
            if (mData.getIsRight() == RESULT_TYPE_ERRRO) {
                iconResId = R.drawable.icon_live_wrong;
            } else if (mData.getIsRight() == RESULT_TYPE_PART_CORRECT) {
                iconResId = R.drawable.icon_live_prart_correct;
            } else if (mData.getIsRight() == RESULT_TYPE_CORRECT) {
                iconResId = R.drawable.icon_live_correct;
            }
            if (iconResId != 0) {
                ivAnswerIcon.setBackgroundResource(iconResId);
            }

            if (TextUtils.isEmpty(myAnswerText)) {
                myAnswerText = new SpannableStringBuilder("我的答案:未作答或未按时提交;");
            }

            tvUserAnswer.setText(myAnswerText);
            tvRightAnswer.setText("正确答案:" + standerAnswerText);

        }

        /**
         * 数组转换为 字符串
         *
         * @param data
         * @param spiltStr
         * @return
         */
        private String listToStr(List<String> data, String spiltStr) {
            StringBuilder stringBuilder = new StringBuilder();
            if (data != null) {
                boolean isAllSpace = true;
                for (int i = 0; i < data.size(); i++) {
                    String answer = data.get(i);
                    if (TextUtils.isEmpty(answer)) {
                        answer = "空";
                    } else {
                        isAllSpace = false;
                    }
                    if (i != 0 && !TextUtils.isEmpty(spiltStr)) {
                        stringBuilder.append(spiltStr);
                    }
                    stringBuilder.append(answer);
                }
                if (isAllSpace) {
                    return "空";
                }
            }
            return stringBuilder.toString();
        }
    }

    /**
     * 关键字标记
     *
     * @param
     * @param
     * @return
     */
    private SpannableStringBuilder markScoreKey(List<ChineseAISubjectResultEntity.StuAnswer> data, String spiltStr) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder("你的答案:");
        if (data != null) {
            boolean isAllSpace = true;
            for (int i = 0; i < data.size(); i++) {
                ChineseAISubjectResultEntity.StuAnswer answer = data.get(i);
                String temp = answer.getAnswer();
                if (TextUtils.isEmpty(answer.getAnswer())) {
                    temp = "空";
                } else {
                    isAllSpace = false;
                    if (i != 0 && !TextUtils.isEmpty(spiltStr)) {
                        stringBuilder.append(spiltStr);
                    }
                }

                if (!"空".equals(temp)){
                    String [] keys = answer.getScoreKey().split(";");
                    Map<Integer,String> indexs = new TreeMap<>();
                    for (int j = 0; j < keys.length; j++) {
                        indexs.putAll(getIndex(temp,keys[j]));
                    }
                    int firstIndex = 0;
                    for(int key:indexs.keySet()){
                        SpannableString span = new SpannableString(indexs.get(key));
                        span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5DA741")), 0, span.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE );
                        if (key > firstIndex){
                            stringBuilder.append(temp.substring(firstIndex,key));
                            stringBuilder.append(span);
                        } else if (key == firstIndex){
                            stringBuilder.append(span);
                        }
                        firstIndex = key+indexs.get(key).length();
                    }
                    if (firstIndex < temp.length()){
                        stringBuilder.append(temp.substring(firstIndex));
                    }
                }
//                stringBuilder.append(temp.substring());
            }
            if (isAllSpace) {
                return new SpannableStringBuilder("你的答案:未作答或未按时提交;");
            }
        }
        return stringBuilder;
    }

    //获取匹配到的字符串下标
    public Map<Integer,String> getIndex(String strings, String str){
        Map<Integer,String> keyIndex=new TreeMap<>();
        int offset = 0;
        while (strings.indexOf(str)!=-1){
            keyIndex.put(strings.indexOf(str)+offset,str);
            offset = strings.indexOf(str)+str.length();
            strings=strings.substring(strings.indexOf(str)+str.length());
        }
        return keyIndex;
    }

    private int getColor(int corlorId) {
        return mContext.getResources().getColor(corlorId);
    }

    private class AnswerResultAdapter extends RecyclerView.Adapter {

        final int ITEM_TYPE_SINGLE = 1;
        final int ITEM_TYPE_MULTI = 2;
        List<String> rightData;
        List<ChineseAISubjectResultEntity.StuAnswer> stuData;

        public AnswerResultAdapter(List<String> rightData,List<ChineseAISubjectResultEntity.StuAnswer> stuData) {
            this.rightData = rightData;
            this.stuData = stuData;
        }


        @Override
        public int getItemViewType(int position) {

            return getItemCount() == 1 ? ITEM_TYPE_SINGLE : ITEM_TYPE_MULTI;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ItemHolder(View.inflate(parent.getContext(), R.layout.item_chi_answerresult_multi, null));

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ItemHolder) holder).bindData(rightData,stuData, position);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }


    int singLeItemTopMargin;

    /**
     * 隐藏答题详情页面
     */
    private void closeResultUi() {
      /*  mView.setBackgroundColor(Color.TRANSPARENT);
        resultDetailRootView.setVisibility(View.INVISIBLE);
        ivResultBtn.setVisibility(View.VISIBLE);*/
        if (mStateListenr != null) {
            mStateListenr.onCloseByUser();
        }
    }

    @Override
    public void showAnswerReuslt() {
        {
            tvGoldCount.setText("+" + mData.getGold());
            if (0 == mData.getTotalScore()){
                tvScore.setText("0分");
            }else {
                tvScore.setText(mData.getTotalScore()+"分");
            }
            mReusltType = mData.getIsRight();
            tvGoldCount.setVisibility(View.VISIBLE);
            llRewardInfo.setVisibility(View.VISIBLE);

            mView.setBackgroundColor(Color.parseColor(BG_COLOR));
            resultDetailRootView.setVisibility(View.VISIBLE);

            if (recyclerView == null) {
                ivClose = resultDetailRootView.findViewById(R.id.iv_chi_answer_result_detail_close_btn);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        close();
                    }
                });

                TextView tvState = resultDetailRootView.findViewById(R.id.tv_chi_answer_result_detail_title);
                ImageView ivHead = resultDetailRootView.findViewById(R.id.iv_chi_answer_result_detail_head);
                View bgView = resultDetailRootView.findViewById(R.id.v_chi_answer_result_detail_bg);
//                TextView tvScore = resultDetailRootView.findViewById(R.id.tv_chi_answer)
                if (RESULT_TYPE_CORRECT == mReusltType) {
                    bgView.setBackgroundResource(R.drawable.arts_answer_result_correct_bg_big);
                    tvState.setTextColor(Color.parseColor("#F13232"));
                    ivHead.setImageResource(R.drawable.chi_answer_correct_head);
                    tvState.setText("完美!完全正确!");
                } else if (RESULT_TYPE_PART_CORRECT == mReusltType) {

                    bgView.setBackgroundResource(R.drawable.arts_answer_result_partcorrect_bg_big);
                    tvState.setTextColor(Color.parseColor("#F0773c"));
                    ivHead.setImageResource(R.drawable.chi_answer_error_head);
                    tvState.setText("哎呀!好可惜!");
                } else {
                    bgView.setBackgroundResource(R.drawable.arts_answer_result_error_bg_big);
                    tvState.setTextColor(Color.parseColor("#327AF0"));
                    ivHead.setImageResource(R.drawable.chi_answer_partcorrect_head);
                    tvState.setText("加油!再接再厉!");
                }

                recyclerView = resultDetailRootView.findViewById(R.id.rcl_chi_answer_result_detail);
                recyclerView.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.VERTICAL,
                        false));

                mAdapter = new AnswerResultAdapter(mData.getRightAnswers(),mData.getStuAnswers());
                recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
            mStateListenr.onCompeletShow();
        }
    }


    @Override
    public void close() {
        if (mView != null) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    closeResultUi();
                    if (mView.getParent() != null) {
                        ((ViewGroup) mView.getParent()).removeView(mView);
                    }
                }
            });
        }
    }
}

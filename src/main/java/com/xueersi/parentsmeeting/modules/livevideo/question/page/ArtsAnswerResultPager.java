package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerResultStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.IArtsAnswerRsultDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ArtsAnswerTextView;

import java.util.List;


/**
 * 文科答题结果页
 *
 * @author chenkun
 * @version 1.0, 2018/7/26 下午1:59
 */

public class ArtsAnswerResultPager extends BasePager implements IArtsAnswerRsultDisplayer {
    /**游戏试题类型*/
    private static final int TYPE_GAME = 12;
    private View resultStateRootView;
    private View resultDetailRootView;
    private static final long ANSWER_DETAIL_SHOW_DELAY = 3000;
    private ImageView ivClose;
    private RecyclerView recyclerView;
    private AnswerResultAdapter mAdapter;
    private static final int SPAN_COUNT = 1;


    private static final int RESULT_TYPE_CORRECT = 2;
    private static final int RESULT_TYPE_PART_CORRECT = 1;
    private static final int RESULT_TYPE_ERRRO = 0;

    private int mReusltType;
    private ImageView ivResultBtn;

    /**单选题 答案 展示item  距离顶部的  距离*/
    private final int SINGLE_ANSWER_TOPMARGIN = 30;

    private static final String BG_COLOR = "#CC000000";
    private AnswerResultEntity mData;
    private final AnswerResultStateListener mStateListenr;

    public ArtsAnswerResultPager(Context context, AnswerResultEntity data, AnswerResultStateListener listener) {
        super(context);
        mData = data;
        mStateListenr = listener;
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_arts_anwserresult_normal, null);
        resultStateRootView = view.findViewById(R.id.rl_arts_answer_result_state);
        resultDetailRootView = view.findViewById(R.id.rl_arts_answer_result_resultdetail);
        ivResultBtn = view.findViewById(R.id.iv_arts_answer_result_answer_btn);

        ivResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disPlayDetailUI();
            }
        });

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



    private Runnable showAnswerDetailTask = new Runnable() {
        @Override
        public void run() {
            disPlayDetailUI();
        }
    };


    private static class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvStanderAnswer;
        private ArtsAnswerTextView tvAnswer;
        private TextView tvStanderAnswerBelow;
        private TextView tvIndex;


        public ItemHolder(View itemView) {
            super(itemView);
            tvStanderAnswer = itemView.findViewById(R.id.tv_arts_answer_result_stander_answer);
            tvAnswer = itemView.findViewById(R.id.tv_arts_answer_result_answer);
            tvStanderAnswerBelow = itemView.findViewById(R.id.tv_arts_answer_result_stander_answer_below);
            tvIndex = itemView.findViewById(R.id.tv_arts_answer_result_answer_index);
        }

        public void bindData(AnswerResultEntity.Answer data,int position) {
            if (tvIndex != null) {
                tvIndex.setText((position + 1) + ".");
            }
            int iconResId = 0;
            if(data.getIsRight() == RESULT_TYPE_ERRRO){
                iconResId = R.drawable.icon_live_wrong;
            }else if(data.getIsRight() == RESULT_TYPE_PART_CORRECT){
                iconResId = R.drawable.icon_live_prart_correct;
            }else if(data.getIsRight() == RESULT_TYPE_CORRECT){
                iconResId = R.drawable.icon_live_correct;
            }

            if(isSelect(data)){
                tvStanderAnswer.setVisibility(data.getIsRight() ==2 ?View.GONE:View.VISIBLE);
                tvStanderAnswer.setText(listToStr(data.getRightAnswers(),null));
                tvStanderAnswerBelow.setVisibility(View.GONE);
                tvAnswer.setTextWithIcon("你的答案:"+listToStr(data.getChoiceList(),null));
            }else{
                tvStanderAnswer.setVisibility(View.GONE);
                tvAnswer.setTextWithIcon(listToStr(data.getBlankList(),"、"));
                tvStanderAnswerBelow.setVisibility(data.getIsRight() ==2?View.GONE:View.VISIBLE);
                tvStanderAnswerBelow.setText(listToStr(data.getRightAnswers(),"、"));
            }

            if(iconResId != 0){
                tvAnswer.setIconResId(iconResId);
            }
        }

        /**是否是填空题*/
        private boolean isSelect(AnswerResultEntity.Answer data) {
            return data.getTestType() == 2;
        }

        /**
         * 数组转换为 字符串
         * @param data
         * @param spiltStr
         * @return
         */
        private String listToStr (List<String> data,String spiltStr){
            StringBuilder stringBuilder = new StringBuilder();
            if(data != null){
                for (int i = 0; i < data.size(); i++) {
                    if(i < (data.size() -1) && spiltStr != null){
                        stringBuilder.append(data.get(i)).append(spiltStr);
                    }else{
                        stringBuilder.append(data.get(i));
                    }
                }
            }
            return  stringBuilder.toString();
        }
    }


    private static class AnswerResultAdapter extends RecyclerView.Adapter {

        final int ITEM_TYPE_SINGLE = 1;
        final int ITEM_TYPE_MULTI = 2;
        List<AnswerResultEntity.Answer> data;
        public AnswerResultAdapter(List<AnswerResultEntity.Answer> data){
           this.data = data;
        }


        @Override
        public int getItemViewType(int position) {

            return getItemCount() == 1?ITEM_TYPE_SINGLE:ITEM_TYPE_MULTI;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (ITEM_TYPE_SINGLE == viewType) {
                return new ItemHolder(View.inflate(parent.getContext(), R.layout.item_arts_answerresult_single, null));
            } else {
                return new ItemHolder(View.inflate(parent.getContext(), R.layout.item_arts_answerresult_multi, null));
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            AnswerResultEntity.Answer answer = data.get(position);
            ((ItemHolder) holder).bindData(answer,position);
        }

        @Override
        public int getItemCount() {
            return data == null?0:data.size();
        }
    }


    int singLeItemTopMargin;

    /**
     * 展示答案详情
     */
    private void disPlayDetailUI() {
        // 游戏题不展示 统计面板 只展示 获得金币UI
        if(isGameResult()){
            if(getRootView() != null && getRootView().getParent() != null){
                ((ViewGroup)getRootView().getParent()).removeView(getRootView());
            }
            mStateListenr.onCompeletShow();
            return;
        }
        resultStateRootView.setVisibility(View.GONE);
        mView.setBackgroundColor(Color.parseColor(BG_COLOR));
        ivResultBtn.setVisibility(View.GONE);
        resultDetailRootView.setVisibility(View.VISIBLE);

        if (recyclerView == null) {
            ivClose = resultDetailRootView.findViewById(R.id.iv_arts_answer_result_detail_close_btn);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideResultUi();
                }
            });

            TextView tvState = resultDetailRootView.findViewById(R.id.tv_arts_answer_result_detail_title);
            ImageView ivHead = resultDetailRootView.findViewById(R.id.iv_arts_answer_result_detail_head);
            View bgView = resultDetailRootView.findViewById(R.id.v_arts_answer_result_detail_bg);
            if (RESULT_TYPE_CORRECT == mReusltType) {
                bgView.setBackgroundResource(R.drawable.arts_answer_result_correct_bg);
                tvState.setTextColor(Color.parseColor("#F13232"));
                ivHead.setImageResource(R.drawable.arts_answer_correct_head);
                tvState.setText("完美!完全正确!");
            } else if (RESULT_TYPE_PART_CORRECT == mReusltType) {

                bgView.setBackgroundResource(R.drawable.arts_answer_result_partcorrect_bg);
                tvState.setTextColor(Color.parseColor("#F0773c"));
                ivHead.setImageResource(R.drawable.arts_answer_partcorrect_head);
                tvState.setText("哎呀!好可惜!");
            } else {
                bgView.setBackgroundResource(R.drawable.arts_answer_result_error_bg);
                tvState.setTextColor(Color.parseColor("#327AF0"));
                ivHead.setImageResource(R.drawable.arts_answer_error_head);
                tvState.setText("加油!再接再厉!");
            }

            recyclerView = resultDetailRootView.findViewById(R.id.rcl_arts_answer_result_detail);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.VERTICAL,
                    false));

            mAdapter = new AnswerResultAdapter(mData.getAnswerList());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    if (mAdapter.getItemCount() > 1) {
                        int itemPosition = parent.getChildAdapterPosition(view);
                        int left = 0;
                        int right = 0;
                        int top = 0;
                        int bottom = 0;
                        if (itemPosition >= SPAN_COUNT) {
                            top = SizeUtils.Dp2Px(mContext, 8);
                        }
                        outRect.set(left, top, right, bottom);
                    }else{
                        int widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
                        int heightSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                        view.measure(widthSpec,heightSpec);
                        int topMargin = (recyclerView.getLayoutParams().height - view.getMeasuredHeight())/2;
                        outRect.set(0, topMargin<0?0:topMargin, 0, 0);
                    }
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
        mStateListenr.onCompeletShow();
    }

    private boolean isGameResult() {

        return mData != null && mData.getType() == TYPE_GAME;
    }


    /**
     * 隐藏答题详情页面
     */
    private void hideResultUi() {
        mView.setBackgroundColor(Color.TRANSPARENT);
        resultDetailRootView.setVisibility(View.INVISIBLE);
        ivResultBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAnswerReuslt() {


        mView.setBackgroundColor(Color.parseColor(BG_COLOR));
        // 测试代码
        mReusltType = mData.getIsRight();
        resultStateRootView.setVisibility(View.VISIBLE);
        resultDetailRootView.setVisibility(View.GONE);

        TextView tvState = resultStateRootView.findViewById(R.id.tv_arts_answer_result_state);
        ImageView ivHead = resultStateRootView.findViewById(R.id.iv_arts_answer_result_state_head);
        TextView tvGold = resultStateRootView.findViewById(R.id.tv_arts_answer_result_gold);
        tvGold.setText("+"+mData.getGold());
        if (RESULT_TYPE_CORRECT == mReusltType) {
            tvState.setBackgroundResource(R.drawable.arts_answer_result_correct_bg);
            tvState.setTextColor(Color.parseColor("#F13232"));
            ivHead.setImageResource(R.drawable.arts_answer_correct_head);
            tvState.setText("完美!完全正确!");
        } else if (RESULT_TYPE_PART_CORRECT == mReusltType) {

            tvState.setBackgroundResource(R.drawable.arts_answer_result_partcorrect_bg);
            tvState.setTextColor(Color.parseColor("#F0773c"));
            ivHead.setImageResource(R.drawable.arts_answer_partcorrect_head);
            tvState.setText("哎呀!好可惜!");
        } else {
            tvState.setBackgroundResource(R.drawable.arts_answer_result_error_bg);
            tvState.setTextColor(Color.parseColor("#327AF0"));
            ivHead.setImageResource(R.drawable.arts_answer_error_head);
            tvState.setText("加油!再接再厉!");
        }
        mView.postDelayed(showAnswerDetailTask, ANSWER_DETAIL_SHOW_DELAY);
    }


    @Override
    public void close() {
        mView.removeCallbacks(showAnswerDetailTask);
        mView.post(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
        });
    }
}

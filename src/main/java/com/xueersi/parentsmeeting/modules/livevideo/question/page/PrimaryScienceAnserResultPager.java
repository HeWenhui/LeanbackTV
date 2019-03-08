package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FastScrollableRecyclerView;

import java.util.List;

/**
 * Created by ZhangYuansun on 2019/3/6
 *
 * 小学理科 互动题结果页
 */
public class PrimaryScienceAnserResultPager extends LiveBasePager {
    PrimaryScienceAnswerResultEntity mEnytity;
    AnswerResultAdapter mAdapter;

    public PrimaryScienceAnserResultPager(Context context, PrimaryScienceAnswerResultEntity enytity) {
        super(context);
        this.mEnytity = enytity;
        initData();
        initListener();
    }

    LottieAnimationView lavGameRight;
    LottieAnimationView lavActiveRight;
    FastScrollableRecyclerView mRecycleView;

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_primaryscience_anwserresult, null);
        lavGameRight = mView.findViewById(R.id.lav_livevideo_primaryscience_anwserrsult_game_right);
        lavActiveRight = mView.findViewById(R.id.lav_livevideo_primaryscience_anwserrsult_active_right);
        mRecycleView = mView.findViewById(R.id.frv_livevideo_primaryscience_anwserrsult);
        return mView;
    }

    @Override
    public void initData() {
//        if (mEnytity.getType() == 1) {
//            lavGameRight.setVisibility(View.VISIBLE);
//            lavGameRight.playAnimation();
//        }
//
//        if (mEnytity.getType() == 2) {
//            lavActiveRight.setVisibility(View.VISIBLE);
//            lavActiveRight.playAnimation();
//        }

        mAdapter = new AnswerResultAdapter(mEnytity.getAnswerList());
        mRecycleView.setLayoutManager(new GridLayoutManager(mContext, 1, LinearLayoutManager.VERTICAL,
                false));
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {

    }

    class AnswerResultAdapter extends RecyclerView.Adapter {
        public AnswerResultAdapter(List<PrimaryScienceAnswerResultEntity.Answer> answerList) {
            this.answerList = answerList;
        }

        List<PrimaryScienceAnswerResultEntity.Answer> answerList;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AnswerViewHolder(View.inflate(parent.getContext(), R.layout.item_livevideo_primaryscience_answerresult_answerlist, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PrimaryScienceAnswerResultEntity.Answer data = answerList.get(position);
            ((AnswerViewHolder) holder).bindData(data, position);
        }

        @Override
        public int getItemCount() {
            return answerList.size();
        }
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnswerNumber;
        TextView tvRightAnswer;
        TextView tvMyAnswer;
        ImageView ivRight;

        AnswerViewHolder(View itemView) {
            super(itemView);
            tvAnswerNumber = itemView.findViewById(R.id.tv_livevideo_primaryscience_answerresult_answer_number);
            tvRightAnswer = itemView.findViewById(R.id.tv_livevideo_primaryscience_answerresult_right_answer);
            tvMyAnswer = itemView.findViewById(R.id.tv_livevideo_primaryscience_answerresult_my_answer);
            ivRight = itemView.findViewById(R.id.iv_livevideo_primaryscience_answerresult_answer_result);
        }

        public void bindData(PrimaryScienceAnswerResultEntity.Answer data, int position) {
            tvAnswerNumber.setText(data.getAmswerNumber()+"");
            tvRightAnswer.setText(data.getRightAnswer());
            tvMyAnswer.setText(data.getMyAnswer());
            switch (data.getRight()) {
                case 0: {
                    ivRight.setImageResource(R.drawable.live_interact_primary_right);
                    break;
                }
                case 1: {
                    ivRight.setImageResource(R.drawable.live_interact_primary_wrong);
                    break;
                }
                case 2: {
                    ivRight.setImageResource(R.drawable.live_interact_primary_middle);
                    break;
                }
                default:
                    break;
            }
        }
    }
}

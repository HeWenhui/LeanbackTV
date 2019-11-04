package com.xueersi.parentsmeeting.modules.livevideo.question.item;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultItemEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import java.util.ArrayList;
import java.util.List;

public class ExperCourseResultAdapter extends RecyclerView.Adapter {
    private List<PrimaryScienceAnswerResultEntity.Answer> answerList;
    boolean standAnswerLeft = false;
    boolean youAnswerLeft = false;

    public ExperCourseResultAdapter(List<PrimaryScienceAnswerResultEntity.Answer> answerList) {
        this.answerList = answerList;
        for (int entityIndex = 0; entityIndex < answerList.size(); entityIndex++) {
            PrimaryScienceAnswerResultEntity.Answer answer = answerList.get(entityIndex);
            String standAnswer = answer.getRightAnswer();
            String youAnswer = answer.getMyAnswer();
            if (standAnswer != null && !standAnswerLeft) {
                char[] chars = standAnswer.toCharArray();
                int standAnswerLenght = 0;
                for (int j = 0; j < chars.length; j++) {
                    byte[] bytes = ("" + chars[j]).getBytes();
                    standAnswerLenght += bytes.length;
                }
                if (standAnswerLenght > 12) {
                    standAnswerLeft = true;
                }
            }
            if (youAnswer != null && !youAnswerLeft) {
                char[] chars = youAnswer.toCharArray();
                int youAnswerLenght = 0;
                for (int j = 0; j < chars.length; j++) {
                    byte[] bytes = ("" + chars[j]).getBytes();
                    youAnswerLenght += bytes.length;
                }
                if (youAnswerLenght > 12) {
                    youAnswerLeft = true;
                }
            }
            if (standAnswerLeft && youAnswerLeft) {
                break;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livevideo_exper_course_result, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PrimaryScienceAnswerResultEntity.Answer answer = answerList.get(position);
        ((ItemHolder) holder).bindData(answer, position);
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_livevideo_bigque_result_num;
        TextView tv_livevideo_bigque_result_stand;
        TextView tv_livevideo_bigque_result_your;
        ImageView iv_livevideo_bigque_result_type;

        public ItemHolder(View itemView) {
            super(itemView);
            tv_livevideo_bigque_result_num = itemView.findViewById(R.id.tv_livevideo_bigque_result_num);
            tv_livevideo_bigque_result_stand = itemView.findViewById(R.id.tv_livevideo_bigque_result_stand);
//            if (standAnswerLeft) {
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_bigque_result_stand.getLayoutParams();
//                lp.width = SizeUtils.Dp2Px(itemView.getContext(), 140);
//                LayoutParamsUtil.setViewLayoutParams(tv_livevideo_bigque_result_stand, lp);
//                tv_livevideo_bigque_result_stand.setGravity(Gravity.LEFT);
//            }
            tv_livevideo_bigque_result_your = itemView.findViewById(R.id.tv_livevideo_bigque_result_your);
//            if (youAnswerLeft) {
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_bigque_result_your.getLayoutParams();
//                lp.width = SizeUtils.Dp2Px(itemView.getContext(), 140);
//                lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.tv_livevideo_bigque_result_your_hind);
//                LayoutParamsUtil.setViewLayoutParams(tv_livevideo_bigque_result_your, lp);
//            }
            iv_livevideo_bigque_result_type = itemView.findViewById(R.id.iv_livevideo_bigque_result_type);
        }

        public void bindData(PrimaryScienceAnswerResultEntity.Answer answer, int position) {
            tv_livevideo_bigque_result_num.setText("" + (position + 1));
            tv_livevideo_bigque_result_stand.setText(answer.getRightAnswer());
            tv_livevideo_bigque_result_your.setText(answer.getMyAnswer());
            if (answer.getRight() == PrimaryScienceAnswerResultEntity.ABSLUTELY_RIGHT) {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_right);
            } else if (answer.getRight() == PrimaryScienceAnswerResultEntity.PARTIALLY_RIGHT) {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_part_right);
            } else {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_wrong);
            }
        }
    }
}

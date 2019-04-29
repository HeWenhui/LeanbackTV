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

import java.util.ArrayList;

public class BigResultAdapter extends RecyclerView.Adapter {
    private ArrayList<BigResultItemEntity> bigResultEntities;
    boolean standAnswerLeft = false;
    boolean youAnswerLeft = false;

    public BigResultAdapter(ArrayList<BigResultItemEntity> bigResultEntities) {
        this.bigResultEntities = bigResultEntities;
        for (int entityIndex = 0; entityIndex < bigResultEntities.size(); entityIndex++) {
            BigResultItemEntity bigResultItemEntity = bigResultEntities.get(entityIndex);
            String standAnswer = bigResultItemEntity.standAnswer;
            String youAnswer = bigResultItemEntity.youAnswer;
            if (standAnswer != null && !standAnswerLeft) {
                char[] chars = standAnswer.toCharArray();
                int standAnswerLenght = 0;
                for (int j = 0; j < chars.length; j++) {
                    byte[] bytes = ("" + chars[j]).getBytes();
                    standAnswerLenght += bytes.length;
                }
                if (standAnswerLenght > 8) {
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
                if (youAnswerLenght > 8) {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livevideo_bigques_result, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BigResultItemEntity bigResultEntity = bigResultEntities.get(position);
        ((ItemHolder) holder).bindData(bigResultEntity, position);
    }

    @Override
    public int getItemCount() {
        return bigResultEntities.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_livevideo_bigque_result_stand;
        TextView tv_livevideo_bigque_result_your;
        ImageView iv_livevideo_bigque_result_type;

        public ItemHolder(View itemView) {
            super(itemView);
            tv_livevideo_bigque_result_stand = itemView.findViewById(R.id.tv_livevideo_bigque_result_stand);
            if (standAnswerLeft) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_bigque_result_stand.getLayoutParams();
                lp.width = SizeUtils.Dp2Px(itemView.getContext(), 140);
                tv_livevideo_bigque_result_stand.setLayoutParams(lp);
                tv_livevideo_bigque_result_stand.setGravity(Gravity.LEFT);
            } else {

            }
            tv_livevideo_bigque_result_your = itemView.findViewById(R.id.tv_livevideo_bigque_result_your);
            if (youAnswerLeft) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_bigque_result_your.getLayoutParams();
                lp.width = SizeUtils.Dp2Px(itemView.getContext(), 140);
                lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.tv_livevideo_bigque_result_your_hind);
                tv_livevideo_bigque_result_your.setLayoutParams(lp);
            }
            iv_livevideo_bigque_result_type = itemView.findViewById(R.id.iv_livevideo_bigque_result_type);
        }

        public void bindData(BigResultItemEntity bigResultEntity, int position) {
            tv_livevideo_bigque_result_stand.setText(bigResultEntity.standAnswer);
            tv_livevideo_bigque_result_your.setText(bigResultEntity.youAnswer);
            if (bigResultEntity.rightType == LiveQueConfig.DOTTYPE_ITEM_RESULT_RIGHT) {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_right);
            } else if (bigResultEntity.rightType == LiveQueConfig.DOTTYPE_ITEM_PART_RIGHT) {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_part_right);
            } else {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_wrong);
            }
        }
    }
}

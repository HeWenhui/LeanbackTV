package com.xueersi.parentsmeeting.modules.livevideo.question.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultItemEntity;

import java.util.ArrayList;

public class BigResultAdapter extends RecyclerView.Adapter {
    private ArrayList<BigResultItemEntity> bigResultEntities;

    public BigResultAdapter(ArrayList<BigResultItemEntity> bigResultEntities) {
        this.bigResultEntities = bigResultEntities;
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
            tv_livevideo_bigque_result_your = itemView.findViewById(R.id.tv_livevideo_bigque_result_your);
            iv_livevideo_bigque_result_type = itemView.findViewById(R.id.iv_livevideo_bigque_result_type);
        }

        public void bindData(BigResultItemEntity bigResultEntity, int position) {
            tv_livevideo_bigque_result_stand.setText(bigResultEntity.standAnswer);
            tv_livevideo_bigque_result_your.setText(bigResultEntity.youAnswer);
            if (bigResultEntity.rightType == LiveQueConfig.DOTTYPE_ITEM_RESULT_RIGHT) {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_right);
            } else {
                iv_livevideo_bigque_result_type.setImageResource(R.drawable.bg_livevideo_bigque_result_wrong);
            }
        }
    }
}

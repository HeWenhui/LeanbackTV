package com.xueersi.parentsmeeting.modules.livevideo.adapter;

/**
 * Created by huadl on 2017/6/30.
 * 互动题目排名
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.UserScoreEntity;


public class QuestionRateHolder extends RecyclerView.ViewHolder {
    /**
     * 名字
     */
    private TextView tvUserName;
    /**
     * 正确率
     */
    private TextView tvRate;
    /**
     * 排名
     */
    private TextView tvIndex;
    /**
     * 排名
     */
    private ImageView ivIndex;

    QuestionRateHolder(View itemView) {
        super(itemView);
        tvIndex = (TextView) itemView.findViewById(R.id.tv_item_audit_class_room_ranking_head);
        ivIndex = (ImageView) itemView.findViewById(R.id.iv_item_audit_class_room_ranking_head);
        tvUserName = (TextView) itemView.findViewById(R.id.tv_item_audit_class_room_ranking_name);
        tvRate = (TextView) itemView.findViewById(R.id.tv_item_audit_class_room_ranking);

    }

    void bind(Context context, UserScoreEntity entity, int position) {
        tvUserName.setText(entity.getShowName());
        tvRate.setText(entity.getCorrectRate());
        if(position ==0) {
            tvIndex.setVisibility(View.GONE);
            ivIndex.setVisibility(View.VISIBLE);
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_interactive_ranking_one);
            ivIndex.setBackgroundDrawable(drawable);
        } else  if(position ==1) {
            ivIndex.setVisibility(View.VISIBLE);
            tvIndex.setVisibility(View.GONE);
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_interactive_ranking_two);
            ivIndex.setBackgroundDrawable(drawable);
        } else  if(position ==2) {
            ivIndex.setVisibility(View.VISIBLE);
            tvIndex.setVisibility(View.GONE);
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_interactive_ranking_three);
            ivIndex.setBackgroundDrawable(drawable);
        } else {
            ivIndex.setVisibility(View.GONE);
            tvIndex.setVisibility(View.VISIBLE);
            tvIndex.setText(entity.getIndex()+"");
        }
        if(entity.isMyScore()) {
            int color = context.getResources().getColor(R.color.COLOR_FF943F);
            tvIndex.setTextColor(color);
            tvUserName.setTextColor(color);
            tvRate.setTextColor(color);
        } else {
            int color = context.getResources().getColor(R.color.COLOR_333333);
            tvIndex.setTextColor(color);
            tvUserName.setTextColor(color);
            tvRate.setTextColor(color);
        }

    }
}
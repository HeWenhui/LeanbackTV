package com.xueersi.parentsmeeting.modules.livevideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.UserScoreEntity;

import java.util.List;

/**
 * Created by huadl on 2017/6/30.
 * 互动题排名适配
 */

public class QuestionRateAdapter extends   RecyclerView.Adapter<RecyclerView.ViewHolder> {

        /**
         * 排名
         */
        List<UserScoreEntity> lstUserScore;
        /**
         * 上下文
         */
        Context context;

        public QuestionRateAdapter(Context context, List<UserScoreEntity> lstUserScore) {
            this.context = context;
            this.lstUserScore = lstUserScore;

        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audit_class_room_ranking, parent, false);
            return new QuestionRateHolder(itemView);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((QuestionRateHolder) holder).bind(context, lstUserScore.get(position), position);

        }

        @Override
        public int getItemCount() {
            return lstUserScore.size();
        }

        public void upDataList(List<UserScoreEntity> lstUserScore) {
            this.lstUserScore = lstUserScore;
        }
    }

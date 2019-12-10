package com.xueersi.parentsmeeting.modules.livevideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.AuditRoomConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.UserScoreEntity;

import java.util.List;

/**
 * Created by huadl on 2017/6/30.
 * 互动题对错情况adapter
 *
 * @author hua
 */
public class QuestionRateDetailAdapter extends BaseAdapter {
    // 课程服务
    List<UserScoreEntity> lstUserScore;
    // 上下文
    Context mContext;
    boolean isBigLive;

    public QuestionRateDetailAdapter(Context context, List<UserScoreEntity>
            lstUserScore, boolean isBigLive) {
        mContext = context;
        this.lstUserScore = lstUserScore;
        this.isBigLive = isBigLive;
    }

    @Override
    public int getCount() {
        return lstUserScore.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_audit_class_room_question_detail, null);
            holder.tvQuestionIndex = (TextView) convertView.findViewById(R.id.tv_item_audit_class_room_question_detail_index);
            holder.tvQuestionStatus = (TextView) convertView.findViewById(R.id.tv_item_audit_class_room_question_detail_status);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        setData(lstUserScore.get(position), holder);
        return convertView;
    }

    private void setData(UserScoreEntity entity, Holder holder) {
        holder.tvQuestionIndex.setText(entity.getQuestionId() + "");
        int color = mContext.getResources().getColor(R.color.COLOR_20ABFF);
        if (isBigLive) {
            holder.tvQuestionStatus.setText(entity.getBigQuestionStatusText());
            if (entity.getQuestionStatus() == AuditRoomConfig.QUESTION_BIG_WRONG) {
                color = mContext.getResources().getColor(R.color.COLOR_FF4343);
            } else if (entity.getQuestionStatus() == AuditRoomConfig.QUESTION_BIG_RIGHT) {
                color = mContext.getResources().getColor(R.color.COLOR_6AC00B);
            }
        } else {
            holder.tvQuestionStatus.setText(entity.getQuestionStatusText());
            if (entity.getQuestionStatus() == AuditRoomConfig.QUESTION_WRONG) {
                color = mContext.getResources().getColor(R.color.COLOR_FF4343);
            } else if (entity.getQuestionStatus() == AuditRoomConfig.QUESTION_RIGHT) {
                color = mContext.getResources().getColor(R.color.COLOR_6AC00B);
            }
        }
        holder.tvQuestionIndex.setTextColor(color);
    }


    class Holder {
        private TextView tvQuestionIndex;
        private TextView tvQuestionStatus;
    }
}
package com.xueersi.parentsmeeting.modules.livevideoOldIJK.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.UserScoreEntity;

import java.util.List;

/**
 * Created by huadl on 2017/6/30.
 * 语音题adapter
 *
 * @author hua
 */
public class VoiceQuestionDetailAdapter extends BaseAdapter {
    // 课程服务
    List<UserScoreEntity> lstUserScore;
    // 上下文
    Context mContext;

    public VoiceQuestionDetailAdapter(Context context, List<UserScoreEntity>
            lstUserScore) {
        mContext = context;
        this.lstUserScore = lstUserScore;
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
            convertView = View.inflate(mContext, R.layout.item_audit_class_room_voice_question_detail, null);
            holder.tvQuestionIndex = (TextView) convertView.findViewById(R.id.tv_item_audit_class_room_voice_question_index);
            holder.tvQuestionScore = (TextView) convertView.findViewById(R.id.tv_item_audit_class_room_voice_question_score);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        setData(lstUserScore.get(position), holder);
        return convertView;
    }

    private void setData(UserScoreEntity entity, Holder holder) {
        holder.tvQuestionIndex.setText("第"+entity.getQuestionId()+"题");
        String score = entity.getScore();
        if (!TextUtils.isEmpty(score)) {
            int color = mContext.getResources().getColor(R.color.COLOR_FBAD38);
            SpannableString sp = new SpannableString(score);
            sp.setSpan(new ForegroundColorSpan(color), 0, score.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(25, true), 0, score.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvQuestionScore.setText(sp);
        }
    }


    class Holder {
        private TextView tvQuestionIndex;
        private TextView tvQuestionScore;
    }
}
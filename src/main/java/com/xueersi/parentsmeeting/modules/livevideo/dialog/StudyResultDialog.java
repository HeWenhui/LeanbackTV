package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.widget.RoundProgressBar;

/**
 * Created by yuanwei2 on 2019/5/31.
 */

public class StudyResultDialog extends Dialog implements View.OnClickListener {

    public static final int BUTTON_SHUT = R.id.ib_shut;

    public static final int BUTTON_CHAT = R.id.bt_chat;

    public static final int BUTTON_APPLY = R.id.bt_apply;

    private DialogInterface.OnClickListener clickListener;

    public StudyResultDialog(@NonNull Context context) {
        this(context, R.style.style_live_compat);
    }

    public StudyResultDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_study_result);
    }

    public void setOnClickListener(DialogInterface.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setStudyResult(ExperienceResult mData) {

        RoundProgressBar mProgressbar = findViewById(R.id.roundProgressBar);
        TextView recommand = findViewById(R.id.tv_detail_result);
        TextView beat = findViewById(R.id.tv_result);
        TextView totalscore = findViewById(R.id.tv_total_score);

        beat.setText("恭喜，你打败了" + mData.getBeat() + "%的学生");

        if (TextUtils.isEmpty(mData.getRecommend())) {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("赶快去报班继续提高成绩吧");
        } else {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("推荐您报名" + mData.getRecommend());
        }

        totalscore.setText(mData.getCorrect() + "%");
        mProgressbar.setMax(100);

        if (mData.getCorrect() > 0) {
            mProgressbar.setProgress(mData.getCorrect());
        } else {
            mProgressbar.setProgress(0);
        }

        Button chat = findViewById(R.id.bt_chat);
        if (TextUtils.isEmpty(mData.getWechatNum())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.ib_shut).setOnClickListener(this);
        findViewById(R.id.bt_chat).setOnClickListener(this);
        findViewById(R.id.bt_apply).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onClick(this, v.getId());
        }
    }
}

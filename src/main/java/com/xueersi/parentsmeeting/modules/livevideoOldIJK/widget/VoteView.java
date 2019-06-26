package com.xueersi.parentsmeeting.modules.livevideoOldIJK.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.HashMap;
import java.util.Map;

public class VoteView extends LinearLayout {

    public VoteView(Context context) {
        super(context);
    }

    public VoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void initVew(HashMap<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_vote_progressbar, null);
            TextView proTextView = (TextView) view.getChildAt(0);
            proTextView.setText(entry.getValue() + "%");
            ProgressBar progressBar = (ProgressBar) view.getChildAt(1);
            progressBar.setProgress(entry.getValue());
            TextView textView = (TextView) view.getChildAt(2);
            textView.setText(entry.getKey());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            addView(view, params);
        }
    }

    public void updateVote(HashMap<String, Integer> hashMap) {
        for (int i = 0; i < getChildCount(); i++) {
            LinearLayout view = (LinearLayout) getChildAt(i);
            TextView proTextView = (TextView) view.getChildAt(0);
            final ProgressBar progressBar = (ProgressBar) view.getChildAt(1);
            final TextView textView = (TextView) view.getChildAt(2);
            if (hashMap.containsKey(textView.getText())) {
                proTextView.setText(hashMap.get(textView.getText()) + "%");
                ValueAnimator valueAnimator = ValueAnimator.ofInt(progressBar.getProgress(), hashMap.get(textView.getText()));
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        progressBar.setProgress((int) animation.getAnimatedValue());
                    }
                });
                valueAnimator.setDuration(1000);
                valueAnimator.start();
            }
        }
    }

}

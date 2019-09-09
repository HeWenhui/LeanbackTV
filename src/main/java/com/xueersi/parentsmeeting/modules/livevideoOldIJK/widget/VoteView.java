package com.xueersi.parentsmeeting.modules.livevideoOldIJK.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import java.util.HashMap;
import java.util.Map;

public class VoteView extends LinearLayout {

    public VoteView(Context context) {
        super(context);
    }

    public VoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void initVew(HashMap<String, Integer> map, String answer) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_vote_progressbar, null);
            FangZhengCuYuanTextView proTextView = (FangZhengCuYuanTextView) view.getChildAt(0);
            proTextView.setText(entry.getValue() + "%");
            FangZhengCuYuanTextView textView = (FangZhengCuYuanTextView) view.getChildAt(2);
            textView.setText(entry.getKey());
            final ProgressBar progressBar = (ProgressBar) view.getChildAt(1);
            if (TextUtils.equals(answer, entry.getKey())) {
                Drawable drawable = getResources().getDrawable(R.drawable.shape_live_votesubject_prog_answer);
                progressBar.setProgressDrawable(drawable);
                proTextView.setTextColor(getResources().getColor(R.color.COLOR_EA8235));
                textView.setTextColor(getResources().getColor(R.color.COLOR_EA8335));
            }
            int pro = entry.getValue();
            if (pro > 0 && pro <= 5) {
                pro = 10;
            }
            if (pro == 6 || pro == 7) {
                pro = 11;
            }
            if (pro == 8 || pro == 9) {
                pro = 12;
            }
            if (pro == 10 || pro == 11) {
                pro = 13;
            }
            if (pro == 12 || pro == 13) {
                pro = 14;
            }
            if (pro == 14 || pro == 15) {
                pro = 15;
            }
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, pro);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progressBar.setProgress((int) animation.getAnimatedValue());
                }
            });
            valueAnimator.setDuration(1000);
            valueAnimator.start();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            addView(view, params);
        }
    }

    public void updateVote(HashMap<String, Integer> hashMap, String answer) {
        if (getChildCount() == 0) {
            initVew(hashMap, answer);
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            LinearLayout view = (LinearLayout) getChildAt(i);
            FangZhengCuYuanTextView proTextView = (FangZhengCuYuanTextView) view.getChildAt(0);
            final ProgressBar progressBar = (ProgressBar) view.getChildAt(1);
            FangZhengCuYuanTextView textView = (FangZhengCuYuanTextView) view.getChildAt(2);
            if (hashMap.containsKey(textView.getText())) {
                proTextView.setText(hashMap.get(textView.getText()) + "%");
                int pro = hashMap.get(textView.getText());
                if (pro <= 5) {
                    pro = 10;
                }
                if (pro == 6 || pro == 7) {
                    pro = 11;
                }
                if (pro == 8 || pro == 9) {
                    pro = 12;
                }
                if (pro == 10 || pro == 11) {
                    pro = 13;
                }
                if (pro == 12 || pro == 13) {
                    pro = 14;
                }
                if (pro == 14 || pro == 15) {
                    pro = 15;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofInt(progressBar.getProgress(), pro);
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

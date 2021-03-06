package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScienceVotePager extends BasePager implements View.OnClickListener {
    private ObjectAnimator animation;
    private ObjectAnimator animationDown;
    private ObjectAnimator animationUp;
    View contentView;
    ImageView ivScienceVoteOpen;
    Button btScienceVoteSubmit;
    LinearLayout llScienceVote;
    LinearLayout llScienceVoteSelect;
    String userAnswer;
    JSONArray optionsJSONArray;
    ScienceVoteBll.ScienceVoteBllBack callback;

    public ScienceVotePager(Context context, JSONArray jsonArray, ScienceVoteBll.ScienceVoteBllBack callback) {
        super(context);
        optionsJSONArray = jsonArray;
        this.callback = callback;
        initData();
    }

    @Override
    public View initView() {
        contentView = View.inflate(mContext, R.layout.page_livevideo_science_vote_select, null);
        ivScienceVoteOpen = contentView.findViewById(R.id.iv_page_livevideo_science_vote_open);
        btScienceVoteSubmit = contentView.findViewById(R.id.bt_livevideo_science_vote_select);
        llScienceVoteSelect = contentView.findViewById(R.id.ll_page_livevideo_science_vote_select);
        llScienceVote = contentView.findViewById(R.id.ll_science_vote);
        ivScienceVoteOpen.setOnClickListener(this);
        btScienceVoteSubmit.setOnClickListener(this);
        btScienceVoteSubmit.setEnabled(false);
        return contentView;
    }

    @Override
    public void initData() {
        initOptionView();
    }

    private void initOptionView() {
        try {
            for (int i = 0; i < optionsJSONArray.length(); i++) {
                JSONObject optionsJSONObject = optionsJSONArray.getJSONObject(i);
                FangZhengCuYuanTextView optionView = new FangZhengCuYuanTextView(mContext);
                optionView.setText(optionsJSONObject.optString("option"));
                int color = Color.parseColor("#9e4d1f");
                optionView.setTextColor(color);
                optionView.setTextSize(25);
                optionView.setBackgroundResource(R.drawable.selector_livevideo_science_vote_option);
                optionView.setGravity(Gravity.CENTER);
                optionView.setOnClickListener(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if(optionsJSONArray.length()==2){
                    params.setMargins(dp2px(60, mContext), 0, dp2px(60, mContext), 0);
                }else if(optionsJSONArray.length()==3){
                    params.setMargins(dp2px(20, mContext), 0, dp2px(20, mContext), 0);
                }else {
                    params.setMargins(dp2px(10, mContext), 0, dp2px(10, mContext), 0);
                }
                optionView.setLayoutParams(params);
                llScienceVote.addView(optionView, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_page_livevideo_science_vote_open) {
            int span;
            if (ivScienceVoteOpen.isSelected()) {
                ivScienceVoteOpen.setSelected(false);
                if (animationUp == null) {
                    span = llScienceVoteSelect.getHeight() - dp2px(9, mContext);
                    animationUp = ObjectAnimator.ofFloat(contentView, View.TRANSLATION_Y, span, 0);
                    animationUp.setDuration(200);
                    animation = animationUp;
                } else {
                    animation = animationUp;
                }
            } else {
                ivScienceVoteOpen.setSelected(true);
                if (animationDown == null) {
                    span = llScienceVoteSelect.getHeight() - dp2px(9, mContext);
                    animationDown = ObjectAnimator.ofFloat(contentView, View.TRANSLATION_Y, 0, span);
                    animationDown.setDuration(200);
                    animation = animationDown;
                } else {
                    animation = animationDown;
                }
            }
            if (animation != null) {
                animation.start();
            }
        } else if (view.getId() == R.id.bt_livevideo_science_vote_select) {
            callback.submit();
        } else {
            for (int i = 0; i < llScienceVote.getChildCount(); i++) {
                FangZhengCuYuanTextView fv = (FangZhengCuYuanTextView) llScienceVote.getChildAt(i);
                if (view == fv) {
                    if (fv.isSelected()) {
                        userAnswer = "";
                        fv.setSelected(false);
                        btScienceVoteSubmit.setEnabled(false);
                    } else {
                        userAnswer = fv.getText().toString();
                        fv.setSelected(true);
                        btScienceVoteSubmit.setEnabled(true);
                    }
                } else {
                    fv.setSelected(false);
                }
            }
        }
    }

    private static int dp2px(int value, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
    }

    public void destroyView() {
        contentView = null;
        animation = null;
        animationUp = null;
        animationDown = null;
        userAnswer = "";
    }
}

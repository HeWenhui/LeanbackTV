package com.xueersi.parentsmeeting.modules.livevideo.business;

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
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScienceVotePager extends BasePager implements View.OnClickListener {
    private Toast toast_submit;
    private LottieAnimationView lottieAnimationView;
    private ObjectAnimator animation;
    private ObjectAnimator animationDown;
    private ObjectAnimator animationUp;
    View contentView;
    ImageView ivScienceVoteOpen;
    Button btScienceVoteSubmit;
    LinearLayout llScienceVote;
    RelativeLayout rlScienceVoteSelect;
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
        rlScienceVoteSelect = contentView.findViewById(R.id.rl_page_livevideo_science_vote_select);
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
                params.setMargins(dp2px(10, mContext), 0, dp2px(10, mContext), 0);
                optionView.setLayoutParams(params);
                llScienceVote.addView(optionView, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_page_livevideo_science_vote_open) {
            int span;
            if (ivScienceVoteOpen.isSelected()) {
                ivScienceVoteOpen.setSelected(false);
                if (animationUp == null) {
                    span = rlScienceVoteSelect.getHeight() - dp2px(9, mContext);
                    animationUp = ObjectAnimator.ofFloat(contentView, View.TRANSLATION_Y, span, 0);
                    animationUp.setDuration(200);
                    animation = animationUp;
                } else {
                    animation = animationUp;
                }
            } else {
                ivScienceVoteOpen.setSelected(true);
                if (animationDown == null) {
                    span = rlScienceVoteSelect.getHeight() - dp2px(9, mContext);
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

    public void submitSuccess(int type) {
        if (toast_submit == null) {
            RelativeLayout relativeLayout =
                    (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_science_vote_submit, null);
            lottieAnimationView = relativeLayout.findViewById(R.id.livevideo_science_vote_lottie);
            toast_submit = new Toast(mContext);
            toast_submit.setView(relativeLayout);
            toast_submit.setGravity(Gravity.CENTER, 0, 0);

            String resPath = "";
            String jsonPath = "";
            if (type == 0) {
                resPath = "vote_submit_success/images";
                jsonPath = "vote_submit_success/data.json";
            } else if (type == 1) {
                resPath = "vote_submit_thumb_up/images";
                jsonPath = "vote_submit_thumb_up/data.json";
            } else if (type == 2) {
                resPath = "vote_submit_come_on/images";
                jsonPath = "vote_submit_come_on/data.json";
            }
            final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
            lottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext));
            lottieAnimationView.useHardwareAcceleration(true);
            ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    String fileName = lottieImageAsset.getFileName();
                    Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(lottieAnimationView, fileName,
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                            mContext);
                    return bitmap;
                }
            };
            lottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
            toast_submit.show();
            lottieAnimationView.playAnimation();

        } else {
            toast_submit.show();
            lottieAnimationView.playAnimation();
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
        toast_submit = null;
    }
}

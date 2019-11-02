package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    FangZhengCuYuanTextView fvScienceVoteA;
    FangZhengCuYuanTextView fvScienceVoteB;
    FangZhengCuYuanTextView fvScienceVoteC;
    FangZhengCuYuanTextView fvScienceVoteD;
    FangZhengCuYuanTextView fvScienceVoteE;
    FangZhengCuYuanTextView fvScienceVoteF;
    FangZhengCuYuanTextView fvScienceVoteYes;
    FangZhengCuYuanTextView fvScienceVoteNo;
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
//        contentView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
        ivScienceVoteOpen = contentView.findViewById(R.id.iv_page_livevideo_science_vote_open);
        btScienceVoteSubmit = contentView.findViewById(R.id.bt_livevideo_science_vote_select);
        fvScienceVoteA = contentView.findViewById(R.id.fv_science_vote_a);
        fvScienceVoteB = contentView.findViewById(R.id.fv_science_vote_b);
        fvScienceVoteC = contentView.findViewById(R.id.fv_science_vote_c);
        fvScienceVoteD = contentView.findViewById(R.id.fv_science_vote_d);
        fvScienceVoteE = contentView.findViewById(R.id.fv_science_vote_e);
        fvScienceVoteF = contentView.findViewById(R.id.fv_science_vote_f);
        fvScienceVoteYes = contentView.findViewById(R.id.fv_science_vote_yes);
        fvScienceVoteNo = contentView.findViewById(R.id.fv_science_vote_no);
        rlScienceVoteSelect = contentView.findViewById(R.id.rl_page_livevideo_science_vote_select);
        ivScienceVoteOpen.setOnClickListener(this);
        btScienceVoteSubmit.setOnClickListener(this);
        fvScienceVoteA.setOnClickListener(this);
        fvScienceVoteB.setOnClickListener(this);
        fvScienceVoteC.setOnClickListener(this);
        fvScienceVoteD.setOnClickListener(this);
        fvScienceVoteE.setOnClickListener(this);
        fvScienceVoteF.setOnClickListener(this);
        fvScienceVoteYes.setOnClickListener(this);
        fvScienceVoteNo.setOnClickListener(this);
        btScienceVoteSubmit.setEnabled(false);
        return contentView;
    }

    @Override
    public void initData() {
        try {
            if (optionsJSONArray.length() == 2) {
                JSONObject optionsJSONObject = optionsJSONArray.getJSONObject(0);
                if (TextUtils.equals(optionsJSONObject.optString("option"), "A")) {
                    showChoice(0);
                } else {
                    showChoice(optionsJSONArray.length());
                }
            } else {
                showChoice(optionsJSONArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showChoice(int length) {
        if (length == 0) {
            fvScienceVoteYes.setVisibility(View.GONE);
            fvScienceVoteNo.setVisibility(View.GONE);
            fvScienceVoteA.setVisibility(View.VISIBLE);
            fvScienceVoteB.setVisibility(View.VISIBLE);
            fvScienceVoteC.setVisibility(View.GONE);
            fvScienceVoteD.setVisibility(View.GONE);
            fvScienceVoteE.setVisibility(View.GONE);
            fvScienceVoteF.setVisibility(View.GONE);
        } else if (length == 2) {
            fvScienceVoteYes.setVisibility(View.VISIBLE);
            fvScienceVoteNo.setVisibility(View.VISIBLE);
            fvScienceVoteA.setVisibility(View.GONE);
            fvScienceVoteB.setVisibility(View.GONE);
            fvScienceVoteC.setVisibility(View.GONE);
            fvScienceVoteD.setVisibility(View.GONE);
            fvScienceVoteE.setVisibility(View.GONE);
            fvScienceVoteF.setVisibility(View.GONE);
        } else if (length == 3) {
            fvScienceVoteYes.setVisibility(View.GONE);
            fvScienceVoteNo.setVisibility(View.GONE);
            fvScienceVoteA.setVisibility(View.VISIBLE);
            fvScienceVoteB.setVisibility(View.VISIBLE);
            fvScienceVoteC.setVisibility(View.VISIBLE);
            fvScienceVoteD.setVisibility(View.GONE);
            fvScienceVoteE.setVisibility(View.GONE);
            fvScienceVoteF.setVisibility(View.GONE);
        } else if (length == 4) {
            fvScienceVoteYes.setVisibility(View.GONE);
            fvScienceVoteNo.setVisibility(View.GONE);
            fvScienceVoteA.setVisibility(View.VISIBLE);
            fvScienceVoteB.setVisibility(View.VISIBLE);
            fvScienceVoteC.setVisibility(View.VISIBLE);
            fvScienceVoteD.setVisibility(View.VISIBLE);
            fvScienceVoteE.setVisibility(View.GONE);
            fvScienceVoteF.setVisibility(View.GONE);
        } else if (length == 5) {
            fvScienceVoteYes.setVisibility(View.GONE);
            fvScienceVoteNo.setVisibility(View.GONE);
            fvScienceVoteA.setVisibility(View.VISIBLE);
            fvScienceVoteB.setVisibility(View.VISIBLE);
            fvScienceVoteC.setVisibility(View.VISIBLE);
            fvScienceVoteD.setVisibility(View.VISIBLE);
            fvScienceVoteE.setVisibility(View.VISIBLE);
            fvScienceVoteF.setVisibility(View.GONE);
        } else if (length == 6) {
            fvScienceVoteYes.setVisibility(View.GONE);
            fvScienceVoteNo.setVisibility(View.GONE);
            fvScienceVoteA.setVisibility(View.VISIBLE);
            fvScienceVoteB.setVisibility(View.VISIBLE);
            fvScienceVoteC.setVisibility(View.VISIBLE);
            fvScienceVoteD.setVisibility(View.VISIBLE);
            fvScienceVoteE.setVisibility(View.VISIBLE);
            fvScienceVoteF.setVisibility(View.VISIBLE);
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
        } else if (view.getId() == R.id.fv_science_vote_a) {
            if (fvScienceVoteA.isSelected()) {
                userAnswer = "";
                fvScienceVoteA.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "A";
                fvScienceVoteA.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteB.setSelected(false);
            fvScienceVoteC.setSelected(false);
            fvScienceVoteD.setSelected(false);
            fvScienceVoteE.setSelected(false);
            fvScienceVoteF.setSelected(false);
        } else if (view.getId() == R.id.fv_science_vote_b) {
            if (fvScienceVoteB.isSelected()) {
                userAnswer = "";
                fvScienceVoteB.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "B";
                fvScienceVoteB.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteA.setSelected(false);
            fvScienceVoteC.setSelected(false);
            fvScienceVoteD.setSelected(false);
            fvScienceVoteE.setSelected(false);
            fvScienceVoteF.setSelected(false);
        } else if (view.getId() == R.id.fv_science_vote_c) {
            if (fvScienceVoteC.isSelected()) {
                userAnswer = "";
                fvScienceVoteC.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "C";
                fvScienceVoteC.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteA.setSelected(false);
            fvScienceVoteB.setSelected(false);
            fvScienceVoteD.setSelected(false);
            fvScienceVoteE.setSelected(false);
            fvScienceVoteF.setSelected(false);
        } else if (view.getId() == R.id.fv_science_vote_d) {
            if (fvScienceVoteD.isSelected()) {
                userAnswer = "";
                fvScienceVoteD.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "D";
                fvScienceVoteD.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteA.setSelected(false);
            fvScienceVoteB.setSelected(false);
            fvScienceVoteC.setSelected(false);
            fvScienceVoteE.setSelected(false);
            fvScienceVoteF.setSelected(false);
        } else if (view.getId() == R.id.fv_science_vote_e) {
            if (fvScienceVoteE.isSelected()) {
                userAnswer = "";
                fvScienceVoteE.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "E";
                fvScienceVoteE.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteA.setSelected(false);
            fvScienceVoteB.setSelected(false);
            fvScienceVoteC.setSelected(false);
            fvScienceVoteD.setSelected(false);
            fvScienceVoteF.setSelected(false);
        } else if (view.getId() == R.id.fv_science_vote_f) {
            if (fvScienceVoteF.isSelected()) {
                userAnswer = "";
                fvScienceVoteF.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "F";
                fvScienceVoteF.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteA.setSelected(false);
            fvScienceVoteB.setSelected(false);
            fvScienceVoteC.setSelected(false);
            fvScienceVoteD.setSelected(false);
            fvScienceVoteE.setSelected(false);
        } else if (view.getId() == R.id.fv_science_vote_yes) {
            if (fvScienceVoteYes.isSelected()) {
                userAnswer = "";
                fvScienceVoteYes.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "是";
                fvScienceVoteYes.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteNo.setSelected(false);
        } else if (view.getId() == R.id.fv_science_vote_no) {
            if (fvScienceVoteNo.isSelected()) {
                userAnswer = "";
                fvScienceVoteNo.setSelected(false);
                btScienceVoteSubmit.setEnabled(false);
            } else {
                userAnswer = "否";
                fvScienceVoteNo.setSelected(true);
                btScienceVoteSubmit.setEnabled(true);
            }
            fvScienceVoteYes.setSelected(false);
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

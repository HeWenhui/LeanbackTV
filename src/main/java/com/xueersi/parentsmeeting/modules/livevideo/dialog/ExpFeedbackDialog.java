package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by yuanwei2 on 2019/5/31.
 */

public class ExpFeedbackDialog extends Dialog implements View.OnClickListener {

    public static final int BUTTON_CLOSE = R.id.imgbtn_experience_feedback_close;

    public static final int BUTTON_SUBMIT = R.id.btn_experience_feedback_submit;

    private RadioGroup rgDifficulty;
    private RadioGroup rgSatisficing;
    private EditText etSuggest;
    private ImageButton imgbtnClose;
    private Button btnSubmit;

    /**
     * 选择1 课程难度评价
     */
    private String mDifficulty = "-1";

    /**
     * 选择2 课程满意度评价
     */
    private String mSatisficing = "-1";

    private DialogInterface.OnClickListener clickListener;

    public ExpFeedbackDialog(@NonNull Context context) {
        this(context, R.style.style_live_compat);
    }

    public ExpFeedbackDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_exp_feedback);

        rgDifficulty = findViewById(R.id.rg_experience_feedback_difficulty);
        rgSatisficing = findViewById(R.id.rg_experience_feedback_satisficing);
        etSuggest = findViewById(R.id.et_experience_feedback_suggest);
        imgbtnClose = findViewById(R.id.imgbtn_experience_feedback_close);
        btnSubmit = findViewById(R.id.btn_experience_feedback_submit);
        btnSubmit.setEnabled(false);

        rgDifficulty.clearCheck();
        rgSatisficing.clearCheck();

        rgDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_difficulty_1) {
                    mDifficulty = "1";
                } else if (checkedId == R.id.rbtn_difficulty_2) {
                    group.check(R.id.rbtn_difficulty_2);
                    mDifficulty = "2";
                } else if (checkedId == R.id.rbtn_difficulty_3) {
                    group.check(R.id.rbtn_difficulty_3);
                    mDifficulty = "3";
                } else {
                    mDifficulty = "-1";
                }
                if (!"-1".equals(mDifficulty) && !"-1".equals(mSatisficing)) {
                    btnSubmit.setBackgroundColor(0xFFF13232);
                    btnSubmit.setEnabled(true);
                }
            }
        });

        rgSatisficing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_satisficing_1) {
                    mSatisficing = "1";
                } else if (checkedId == R.id.rbtn_satisficing_2) {
                    mSatisficing = "2";
                } else if (checkedId == R.id.rbtn_satisficing_3) {
                    mSatisficing = "3";
                } else {
                    mSatisficing = "-1";
                }
                if (!"-1".equals(mDifficulty) && !"-1".equals(mSatisficing)) {
                    btnSubmit.setBackgroundColor(0xFFF13232);
                    btnSubmit.setEnabled(true);
                }
            }
        });

        imgbtnClose.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    public String getDifficulty(){
        return mDifficulty;
    }

    public String getSatisficing(){
        return mSatisficing;
    }

    public String getSuggest(){
        return etSuggest.getText().toString();
    }

    public void setClickListener(DialogInterface.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onClick(this, v.getId());
        }
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.pager;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.IButtonOnClick;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.IShowEvaluateAction;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.entity.EvaluateOptionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by：WangDe on 2018/11/30 18:27
 */

public class BaseEvaluateTeacherPaper extends LiveBasePager {
    protected RelativeLayout rlBackground;
    /** 关闭 */
    protected ImageView ivCloseClass;
    /** 提交 */
    protected RelativeLayout rlSubmit;
    /** 评价布局 */
    protected LinearLayout llEvaluate;
    /** 主副讲头像姓名 */
    protected ImageView ivMain;
    protected ImageView ivTutor;
    protected TextView tvMainName;
    protected TextView tvTutorName;
    /** 主讲评价 */
    protected RadioGroup rgMainEvaluate;
    protected RadioButton rbMainUnSat;
    protected RadioButton rbMainSat;
    protected RadioButton rbMainVerySat;
    protected CheckBox cbMainOpt1;
    protected CheckBox cbMainOpt2;
    protected CheckBox cbMainOpt3;
    protected CheckBox cbMainOpt4;
    /** 辅导评价 */
    protected RadioGroup rgTutorEvaluate;
    protected RadioButton rbTutorUnSat;
    protected RadioButton rbTutorSat;
    protected RadioButton rbTutorVerySat;
    protected CheckBox cbTutorOpt1;
    protected CheckBox cbTutorOpt2;
    protected CheckBox cbTutorOpt3;
    protected CheckBox cbTutorOpt4;
    /** 提交结果页 */
    protected RelativeLayout llResult;
    protected ImageView ivResult;
    protected TextView tvResultCountDown;
    protected RelativeLayout rlReSubmit;

    protected LinearLayout llMainEvaluate;
    protected LinearLayout llTutorEvaluate;

    public IButtonOnClick buttonOnClick;

    protected Map<String, String> mainEva = new HashMap<>();
    protected Map<String, String> tutorEva = new HashMap<>();

    public EvaluateOptionEntity optionEntity;

    protected CountDownCallback countDownCallback;

    public boolean isShow = false;
    public boolean showEvaluate = false;
    IShowEvaluateAction iShowEvaluateAction;
    public LiveGetInfo getInfo;
    public List<String> mainOptionUnSat;
    public List<String> mainOptionSat;
    public List<String> mainOptionVerySat;
    public List<String> tutorOptionUnSat;
    public List<String> tutorOptionSat;
    public List<String> tutorOptionVerySat;

    public int optCheckCorlor = 0xFFF13232;
    public int optUncheckColor = 0xFF666666;
    public int scoreCheckColor = 0xFF333333;
    public int scoreUncheckColor = 0xFF666666;
    public float submitAlpha = 1.0f;

    public BaseEvaluateTeacherPaper(Context context) {
        super(context);
    }

    public BaseEvaluateTeacherPaper(Context context, LiveGetInfo getInfo) {
        super(context);
        this.getInfo = getInfo;
    }

    @Override
    public View initView() {
        rlBackground = mView.findViewById(R.id.rl_livevideo_evaluate);
        ivCloseClass = mView.findViewById(R.id.iv_livevideo_evaluate_close);
        rlSubmit = mView.findViewById(R.id.rl_livevideo_evaluate_submit);
        /** 评价布局 */
        llEvaluate = mView.findViewById(R.id.ll_livevideo_evaluate);
        /** 主副讲头像姓名*/
        ivMain = mView.findViewById(R.id.iv_livevideo_evaluate_main);
        ivTutor = mView.findViewById(R.id.iv_livevideo_evaluate_tutor);
        tvMainName = mView.findViewById(R.id.tv_livevideo_evaluate_main_name);
        tvTutorName = mView.findViewById(R.id.tv_livevideo_evaluate_tutor_name);
        /** 主讲评价*/
        rgMainEvaluate = mView.findViewById(R.id.rg_livevideo_evaluate_main);
        rbMainUnSat = mView.findViewById(R.id.rb_livevideo_evaluate_main_unsatisfactory);
        rbMainSat = mView.findViewById(R.id.rb_livevideo_evaluate_main_satisfactory);
        rbMainVerySat = mView.findViewById(R.id.rb_livevideo_evaluate_main_very_satisfactory);
        llMainEvaluate = mView.findViewById(R.id.ll_livevideo_evaluate_main_option);
        cbMainOpt1 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_1);
        cbMainOpt2 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_2);
        cbMainOpt3 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_3);
        cbMainOpt4 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_4);
        /** 辅导评价*/
        rgTutorEvaluate = mView.findViewById(R.id.rg_livevideo_evaluate_tutor);
        rbTutorUnSat = mView.findViewById(R.id.rb_livevideo_evaluate_tutor_unsatisfactory);
        rbTutorSat = mView.findViewById(R.id.rb_livevideo_evaluate_tutor_satisfactory);
        rbTutorVerySat = mView.findViewById(R.id.rb_livevideo_evaluate_tutor_very_satisfactory);
        llTutorEvaluate = mView.findViewById(R.id.ll_livevideo_evaluate_tutor_option);
        cbTutorOpt1 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_1);
        cbTutorOpt2 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_2);
        cbTutorOpt3 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_3);
        cbTutorOpt4 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_4);

        /** 提交结果页*/
        llResult = mView.findViewById(R.id.rl_livevideo_evaluate_submit_result);
        ivResult = mView.findViewById(R.id.iv_livevideo_evaluate_submit_result);
        tvResultCountDown = mView.findViewById(R.id.tv_livevideo_evaluate_count_down);
        rlReSubmit = mView.findViewById(R.id.rl_livevideo_evaluate_tryagain);
        rlSubmit.setEnabled(false);
        return mView;
    }

    @Override
    public void initData() {
        loadImage(ivMain, getInfo.getMainTeacherInfo().getTeacherImg(), R.drawable.bg_main_default_head_image);
        loadImage(ivTutor, getInfo.getTeacherIMG(), R.drawable.bg_tutor_default_head_imge);
        String mainName = getInfo.getMainTeacherInfo().getTeacherName();
        if (mainName != null) {
            tvMainName.setText(mainName);
        }
        String tutorName = getInfo.getTeacherName();
        if (tutorName != null) {
            tvTutorName.setText(getInfo.getTeacherName());
        }
    }

    @Override
    public void initListener() {
        cbMainOpt1.setOnCheckedChangeListener(new MainOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        cbMainOpt2.setOnCheckedChangeListener(new MainOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        cbMainOpt3.setOnCheckedChangeListener(new MainOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        cbMainOpt4.setOnCheckedChangeListener(new MainOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        cbTutorOpt1.setOnCheckedChangeListener(new TutorOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        cbTutorOpt2.setOnCheckedChangeListener(new TutorOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        cbTutorOpt3.setOnCheckedChangeListener(new TutorOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        cbTutorOpt4.setOnCheckedChangeListener(new TutorOptListener(optCheckCorlor, optUncheckColor,submitAlpha));
        rbMainUnSat.setOnCheckedChangeListener(new ScoreListener(scoreCheckColor, scoreUncheckColor));
        rbMainSat.setOnCheckedChangeListener(new ScoreListener(scoreCheckColor, scoreUncheckColor));
        rbMainVerySat.setOnCheckedChangeListener(new ScoreListener(scoreCheckColor, scoreUncheckColor));
        rbTutorUnSat.setOnCheckedChangeListener(new ScoreListener(scoreCheckColor, scoreUncheckColor));
        rbTutorSat.setOnCheckedChangeListener(new ScoreListener(scoreCheckColor, scoreUncheckColor));
        rbTutorVerySat.setOnCheckedChangeListener(new ScoreListener(scoreCheckColor, scoreUncheckColor));
        ivCloseClass.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick.close();
            }
        });
        rgMainEvaluate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                llMainEvaluate.setVisibility(View.VISIBLE);
                cbMainOpt1.setChecked(false);
                cbMainOpt2.setChecked(false);
                cbMainOpt3.setChecked(false);
                cbMainOpt4.setChecked(false);
                rlSubmit.setEnabled(false);
                if (i == R.id.rb_livevideo_evaluate_main_unsatisfactory) {
                    mainEva.put("eva", rbMainUnSat.getText().toString());
                    cbMainOpt1.setText(mainOptionUnSat.get(0));
                    cbMainOpt2.setText(mainOptionUnSat.get(1));
                    cbMainOpt3.setText(mainOptionUnSat.get(2));
                    cbMainOpt4.setText(mainOptionUnSat.get(3));
                } else if (i == R.id.rb_livevideo_evaluate_main_satisfactory) {
                    mainEva.put("eva", rbMainSat.getText().toString());
                    cbMainOpt1.setText(mainOptionSat.get(0));
                    cbMainOpt2.setText(mainOptionSat.get(1));
                    cbMainOpt3.setText(mainOptionSat.get(2));
                    cbMainOpt4.setText(mainOptionSat.get(3));
                } else if (i == R.id.rb_livevideo_evaluate_main_very_satisfactory) {
                    mainEva.put("eva", rbMainVerySat.getText().toString());
                    cbMainOpt1.setText(mainOptionVerySat.get(0));
                    cbMainOpt2.setText(mainOptionVerySat.get(1));
                    cbMainOpt3.setText(mainOptionVerySat.get(2));
                    cbMainOpt4.setText(mainOptionVerySat.get(3));
                }
            }
        });
        rgTutorEvaluate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                llTutorEvaluate.setVisibility(View.VISIBLE);
                cbTutorOpt1.setChecked(false);
                cbTutorOpt2.setChecked(false);
                cbTutorOpt3.setChecked(false);
                cbTutorOpt4.setChecked(false);
                rlSubmit.setEnabled(false);
                if (i == R.id.rb_livevideo_evaluate_tutor_unsatisfactory) {
                    tutorEva.put("eva", rbTutorUnSat.getText().toString());
                    cbTutorOpt1.setText(tutorOptionUnSat.get(0));
                    cbTutorOpt2.setText(tutorOptionUnSat.get(1));
                    cbTutorOpt3.setText(tutorOptionUnSat.get(2));
                    cbTutorOpt4.setText(tutorOptionUnSat.get(3));
                } else if (i == R.id.rb_livevideo_evaluate_tutor_satisfactory) {
                    tutorEva.put("eva", rbTutorSat.getText().toString());
                    cbTutorOpt1.setText(tutorOptionSat.get(0));
                    cbTutorOpt2.setText(tutorOptionSat.get(1));
                    cbTutorOpt3.setText(tutorOptionSat.get(2));
                    cbTutorOpt4.setText(tutorOptionSat.get(3));
                } else if (i == R.id.rb_livevideo_evaluate_tutor_very_satisfactory) {
                    tutorEva.put("eva", rbTutorVerySat.getText().toString());
                    cbTutorOpt1.setText(tutorOptionVerySat.get(0));
                    cbTutorOpt2.setText(tutorOptionVerySat.get(1));
                    cbTutorOpt3.setText(tutorOptionVerySat.get(2));
                    cbTutorOpt4.setText(tutorOptionVerySat.get(3));
                }
            }
        });
        rlReSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                rlReSubmit.setEnabled(false);
                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        super.initListener();
    }

    CountDownTimer timer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            String time = String.valueOf(millisUntilFinished / 1000);
            tvResultCountDown.setText(time + "s后退出直播间");
        }

        @Override
        public void onFinish() {
            if (countDownCallback != null) {
                countDownCallback.finishVideo();
            }
        }
    };

    public void setOptionEntity(EvaluateOptionEntity optionEntity) {
        this.optionEntity = optionEntity;
        Map<String, String> score = optionEntity.getEvaluateScore();
        rbMainUnSat.setText(score.get("choose1"));
        rbMainSat.setText(score.get("choose2"));
        rbMainVerySat.setText(score.get("choose3"));
        rbTutorUnSat.setText(score.get("choose1"));
        rbTutorSat.setText(score.get("choose2"));
        rbMainVerySat.setText(score.get("choose3"));
        Map<String, List<String>> mainOption = optionEntity.getTeacherEvaluOption();
        mainOptionUnSat = mainOption.get("choose1");
        mainOptionSat = mainOption.get("choose2");
        mainOptionVerySat = mainOption.get("choose3");
        Map<String, List<String>> tutorOption = optionEntity.getTutorEvaluOption();
        tutorOptionUnSat = tutorOption.get("choose1");
        tutorOptionSat = tutorOption.get("choose2");
        tutorOptionVerySat = tutorOption.get("choose3");
        //打乱顺序
        Collections.shuffle(mainOptionUnSat);
        Collections.shuffle(mainOptionSat);
        Collections.shuffle(mainOptionVerySat);
        Collections.shuffle(tutorOptionUnSat);
        Collections.shuffle(tutorOptionSat);
        Collections.shuffle(tutorOptionVerySat);
        showEvaluate = true;
    }

    public void setIShowEvaluateAction(IShowEvaluateAction iShowEvaluateAction) {
        this.iShowEvaluateAction = iShowEvaluateAction;
    }

    public void setButtonOnClick(IButtonOnClick buttonOnClick) {
        this.buttonOnClick = buttonOnClick;
    }

    public void showSuccessPager(EvaluateTeacherPager.CountDownCallback callback) {
        llEvaluate.setVisibility(View.GONE);
        llResult.setVisibility(View.VISIBLE);
        tvResultCountDown.setVisibility(View.VISIBLE);
        rlReSubmit.setVisibility(View.GONE);
        countDownCallback = callback;
        timer.start();
    }

    public void showUploadFailPager() {
        llEvaluate.setVisibility(View.GONE);
        llResult.setVisibility(View.VISIBLE);
        tvResultCountDown.setVisibility(View.GONE);
        rlReSubmit.setVisibility(View.VISIBLE);
    }
    public void setReUpload(){
        rlReSubmit.setEnabled(true);
    }
    public interface CountDownCallback {
        void finishVideo();
    }

    @Override
    public boolean onUserBackPressed() {
        if (showEvaluate) {
            if (!isShow) {
                isShow = iShowEvaluateAction.showPager();
            } else {
                isShow = iShowEvaluateAction.removePager();
            }
            return isShow;
        } else {
            return false;
        }
    }

    protected void loadImage(final ImageView imageView, final String imgURL, int defaultHeadImg) {
        if (TextUtils.isEmpty(imgURL)) {
            // 如果图片URL为空则直接加载默认图片，因为图片加载框架对空字符串的路径加载会加载到其它图片上，故这样解决
            imageView.setImageResource(defaultHeadImg);
            return;
        }
        ImageLoader.with(BaseApplication.getContext()).asCircle().load(imgURL).error(defaultHeadImg)
                .placeHolder(defaultHeadImg).into(imageView);
    }

    //评价主讲响应事件
    class MainOptListener implements CompoundButton.OnCheckedChangeListener {
        int checkColor;
        int uncheckColor;
        float submitAlpha;

        MainOptListener(int checkColor, int uncheckColor,float submitAlpha) {
            this.checkColor = checkColor;
            this.uncheckColor = uncheckColor;
            this.submitAlpha = submitAlpha;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                mainEva.put(compoundButton.getText().toString(), "1");
                compoundButton.setTextColor(checkColor);
                if (cbTutorOpt1.isChecked() || cbTutorOpt2.isChecked() || cbTutorOpt3.isChecked() ||
                        cbTutorOpt4.isChecked()) {
                    rlSubmit.setEnabled(true);
                    rlSubmit.setAlpha(1.0f);
                }
            } else {
                mainEva.put(compoundButton.getText().toString(), "0");
                compoundButton.setTextColor(uncheckColor);
                if (!cbMainOpt1.isChecked() && !cbMainOpt2.isChecked() && !cbMainOpt3.isChecked() &&
                        !cbMainOpt4.isChecked()) {
                    rlSubmit.setEnabled(false);
                    rlSubmit.setAlpha(submitAlpha);
                }
            }
        }
    }

    //评价辅导响应事件
    class TutorOptListener implements CompoundButton.OnCheckedChangeListener {
        int checkColor;
        int uncheckColor;
        float submitAlpha;

        TutorOptListener(int checkColor, int uncheckColor,float submitAlpha) {
            this.checkColor = checkColor;
            this.uncheckColor = uncheckColor;
            this.submitAlpha = submitAlpha;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                tutorEva.put(compoundButton.getText().toString(), "1");
                compoundButton.setTextColor(checkColor);
                if (cbMainOpt1.isChecked() || cbMainOpt2.isChecked() || cbMainOpt3.isChecked() ||
                        cbMainOpt4.isChecked()) {
                    rlSubmit.setEnabled(true);
                    rlSubmit.setAlpha(1.0f);
                }
            } else {
                tutorEva.put(compoundButton.getText().toString(), "0");
                compoundButton.setTextColor(uncheckColor);
                if (!cbTutorOpt1.isChecked() && !cbTutorOpt2.isChecked() && !cbTutorOpt3.isChecked() &&
                        !cbTutorOpt4.isChecked()) {
                    rlSubmit.setEnabled(false);
                    rlSubmit.setAlpha(submitAlpha);
                }
            }
        }
    }

    //主辅评价分数
    class ScoreListener implements CompoundButton.OnCheckedChangeListener {
        int checkColor;
        int uncheckColor;

        ScoreListener(int checkColor, int uncheckColor) {
            this.checkColor = checkColor;
            this.uncheckColor = uncheckColor;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                compoundButton.setTextColor(checkColor);
            } else {
                compoundButton.setTextColor(uncheckColor);
            }
        }
    }
}

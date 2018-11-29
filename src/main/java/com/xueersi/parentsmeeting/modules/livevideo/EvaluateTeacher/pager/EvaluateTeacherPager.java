package com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.bussiness.IButtonOnClick;
import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.bussiness.IShowEvaluateAction;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by：WangDe on 2018/11/27 15:58
 */
public class EvaluateTeacherPager extends LiveBasePager {

    IButtonOnClick buttonOnClick;
    /** 关闭 */
    private ImageView ivCloseClass;
    /** 提交 */
    private RelativeLayout rlSubmit;
    /** 评价布局 */
    private LinearLayout llEvaluate;
    /** 主副讲头像姓名*/
    private ImageView ivMain;
    private ImageView ivTutor;
    private TextView tvMainName;
    private TextView tvTutorName;
    /** 主讲评价*/
    private RadioGroup rgMainEvaluate;
    private RadioButton rbMainUnSat;
    private RadioButton rbMainSat;
    private RadioButton rbMainVerySat;
    private CheckBox cbMainOpt1;
    private CheckBox cbMainOpt2;
    private CheckBox cbMainOpt3;
    private CheckBox cbMainOpt4;
    /** 辅导评价*/
    private RadioGroup rgTutorEvaluate;
    private RadioButton rbTutorUnSat;
    private RadioButton rbTutorSat;
    private RadioButton rbTutorVerySat;
    private CheckBox cbTutorOpt1;
    private CheckBox cbTutorOpt2;
    private CheckBox cbTutorOpt3;
    private CheckBox cbTutorOpt4;
    /** 提交结果页*/
    private LinearLayout llResult;
    private ImageView ivResult;
    private TextView tvResultStatus;
    private TextView tvResultCountDown;
    private Button btnReSubmit;

    boolean isShow = false;
    boolean finishViedo = false;

    public EvaluateTeacherPager(Context context) {
        super(context);
    }
    public EvaluateTeacherPager(Context context,LiveGetInfo getInfo) {
        super(context);
        this.getInfo = getInfo;
    }
    IShowEvaluateAction iShowEvaluateAction;
    LiveGetInfo getInfo;
    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_evaluate_teacher,null);
        ivCloseClass = mView.findViewById(R.id.iv_livevideo_evaluate_close);
        rlSubmit = mView.findViewById(R.id.ll_livevideo_evaluate_submit);
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
        cbMainOpt1 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_1);
        cbMainOpt2 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_2);
        cbMainOpt3 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_3);
        cbMainOpt4 = mView.findViewById(R.id.cb_livevideo_evaluate_main_option_4);
        /** 辅导评价*/
        rgTutorEvaluate = mView.findViewById(R.id.rg_livevideo_evaluate_tutor);
        rbTutorUnSat = mView.findViewById(R.id.rb_livevideo_evaluate_main_unsatisfactory);
        rbTutorSat = mView.findViewById(R.id.rb_livevideo_evaluate_main_satisfactory);
        rbTutorVerySat = mView.findViewById(R.id.rb_livevideo_evaluate_main_very_satisfactory);
        cbTutorOpt1 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_1);
        cbTutorOpt2 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_2);
        cbTutorOpt3 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_3);
        cbTutorOpt4 = mView.findViewById(R.id.cb_livevideo_evaluate_tutor_option_4);
        /** 提交结果页*/
        llResult = mView.findViewById(R.id.ll_livevideo_evaluate_submit_result);
        ivResult = mView.findViewById(R.id.iv_livevideo_evaluate_submit_result);
        tvResultStatus = mView.findViewById(R.id.tv_livevideo_evaluate_submit_status);
        tvResultCountDown = mView.findViewById(R.id.tv_livevideo_evaluate_count_down);
        btnReSubmit = mView.findViewById(R.id.btn_livevideo_evaluate_tryagain);
        initListener();
        return mView;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        rlSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick.submit();
            }
        });
        ivCloseClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick.backClass();
            }
        });
        rgMainEvaluate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

            }
        });
        super.initListener();
    }

    @Override
    public boolean onUserBackPressed() {
        if (!finishViedo){
            if (!isShow){
                isShow =  iShowEvaluateAction.showPager();
            }else{
                isShow =  iShowEvaluateAction.removePager();
            }
            return isShow;
        } else {
            return false;
        }


    }

    public void setIShowEvaluateAction(IShowEvaluateAction iShowEvaluateAction){
        this.iShowEvaluateAction = iShowEvaluateAction;
    }

    public void setData(LiveGetInfo getInfo){
        this.getInfo = getInfo;
    }

    public void setButtonOnClick(IButtonOnClick buttonOnClick){
        this.buttonOnClick = buttonOnClick;
    }
}

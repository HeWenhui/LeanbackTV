package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.pager;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

/**
 * Created by：WangDe on 2018/12/2 15:18
 */
public class SmallEnglishEvaluateTeacherPager extends BaseEvaluateTeacherPaper {

    private ImageView ivLoading;
    private ImageView ivTryagaingLoading;

    public SmallEnglishEvaluateTeacherPager(Context context, LiveGetInfo getInfo) {
        super(context, getInfo);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_smallenglish_evaluate_teacher, null);
        ivLoading = mView.findViewById(R.id.iv_livevideo_smallenglish_evaluate_loading);
        ivTryagaingLoading = mView.findViewById(R.id.iv_livevideo_evaluate_tryagain_loading);
        super.initView();
        return mView;
    }

    @Override
    public void initListener() {
        rlSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                rlSubmit.setBackgroundResource(R.drawable.livevideo_evaluateteacher_shellwindow_tijiao_btn_nor);
                ivLoading.setVisibility(View.VISIBLE);
                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        rlReSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                ivTryagaingLoading.setVisibility(View.VISIBLE);
                rlReSubmit.setEnabled(false);
                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        super.initListener();
    }

    @Override
    public void initData() {
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        optCheckCorlor = 0xFFB73800;
        optUncheckColor = 0xFF7B6E6E;
        scoreCheckColor = 0xFFB73800;
        scoreUncheckColor = 0xFF7B6E6E;
        tvMainName.setTypeface(fontFace);
        tvTutorName.setTypeface(fontFace);
        tvResultCountDown.setTypeface(fontFace);
        rbMainUnSat.setTypeface(fontFace);
        rbMainSat.setTypeface(fontFace);
        rbMainVerySat.setTypeface(fontFace);
        rbTutorSat.setTypeface(fontFace);
        rbTutorUnSat.setTypeface(fontFace);
        rbTutorVerySat.setTypeface(fontFace);
        cbMainOpt1.setTypeface(fontFace);
        cbMainOpt2.setTypeface(fontFace);
        cbMainOpt3.setTypeface(fontFace);
        cbMainOpt4.setTypeface(fontFace);
        cbTutorOpt1.setTypeface(fontFace);
        cbTutorOpt2.setTypeface(fontFace);
        cbTutorOpt3.setTypeface(fontFace);
        cbTutorOpt4.setTypeface(fontFace);
        super.initData();
    }

    @Override
    public void showUploadFailPager() {
        logger.i("showUploadFailPager");
        rlBackground.setBackgroundResource(R.drawable.livevideo_evaluateteacher_bg1_nor);
        ivResult.setImageResource(R.drawable.livevideo_evaluateteacher_shibai_img_nor);
        super.showUploadFailPager();
    }

    @Override
    public void showSuccessPager(CountDownCallback callback) {
        logger.i("showUploadSuccessPager");
        rlBackground.setBackgroundResource(R.drawable.livevideo_evaluateteacher_bg1_nor);
        ivResult.setImageResource(R.drawable.livevideo_evaluateteacher_ganxie_img_nor);
        super.showSuccessPager(callback);
    }

    @Override
    public void setReUpload() {
        ivTryagaingLoading.setVisibility(View.GONE);
        rlReSubmit.setEnabled(true);
        super.setReUpload();
    }
}

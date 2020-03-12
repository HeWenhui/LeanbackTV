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
public class PrimaryScienceEvaluateTeacherPager extends BaseEvaluateTeacherPaper {

    private ImageView ivLoading;
    private ImageView ivTryagaingLoading;

    public PrimaryScienceEvaluateTeacherPager(Context context, LiveGetInfo getInfo) {
        super(context, getInfo);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_primaryscience_evaluate_teacher, null);
        ivLoading = mView.findViewById(R.id.iv_livevideo_primaryscience_evaluate_loading);
        ivTryagaingLoading = mView.findViewById(R.id.iv_livevideo_evaluate_tryagain_loading);
        super.initView();
        setTextTypeFace();
        return mView;
    }

    @Override
    public void initListener() {
        rlSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                ivLoading.setVisibility(View.VISIBLE);
                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        rlReSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                rlReSubmit.setEnabled(false);
                ivTryagaingLoading.setVisibility(View.VISIBLE);

                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        super.initListener();
    }
    private void setTextTypeFace(){
//        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
//        tvMainName.setTypeface(fontFace);
//        tvTutorName.setTypeface(fontFace);
//        tvResultCountDown.setTypeface(fontFace);
//        rbMainUnSat.setTypeface(fontFace);
//        rbMainSat.setTypeface(fontFace);
//        rbMainVerySat.setTypeface(fontFace);
//        rbTutorSat.setTypeface(fontFace);
//        rbTutorUnSat.setTypeface(fontFace);
//        rbTutorVerySat.setTypeface(fontFace);
//        cbMainOpt1.setTypeface(fontFace);
//        cbMainOpt2.setTypeface(fontFace);
//        cbMainOpt3.setTypeface(fontFace);
//        cbMainOpt4.setTypeface(fontFace);
//        cbTutorOpt1.setTypeface(fontFace);
//        cbTutorOpt2.setTypeface(fontFace);
//        cbTutorOpt3.setTypeface(fontFace);
//        cbTutorOpt4.setTypeface(fontFace);
    }
    @Override
    public void initData() {

        optCheckCorlor = 0xFFFF7403;
        optUncheckColor = 0xFFBC7D57;
        scoreCheckColor = 0xFFFF7403;
        scoreUncheckColor = 0xFFBC7D57;
        super.initData();
    }

    @Override
    public void showUploadFailPager() {
        logger.i("showUploadFailPager");
        rlBackground.setBackgroundResource(R.drawable.lspj_pingjia_thanks_bg_img_normal);
        ivResult.setImageResource(R.drawable.lspj_pingjia_result_failure_img_normal);
        super.showUploadFailPager();
    }

    @Override
    public void showSuccessPager(CountDownCallback callback) {
        logger.i("showUploadSuccessPager");
        rlBackground.setBackgroundResource(R.drawable.lspj_pingjia_thanks_bg_img_normal);
        ivResult.setImageResource(R.drawable.lspj_pingjia_result_thanks_img_normal);
        super.showSuccessPager(callback);
    }

    @Override
    public void setReUpload() {
        ivTryagaingLoading.setVisibility(View.GONE);
        super.setReUpload();
    }
}

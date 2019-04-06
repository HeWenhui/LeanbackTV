package com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.pager;

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
public class PrimaryChineseEvaluateTeacherPager extends BaseEvaluateTeacherPaper {

    private ImageView ivLoading;
    private ImageView ivTryagaingLoading;

    public PrimaryChineseEvaluateTeacherPager(Context context, LiveGetInfo getInfo) {
        super(context, getInfo);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_primarychinese_evaluate_teacher, null);
        ivLoading = mView.findViewById(R.id.iv_livevideo_primarychinese_evaluate_loading);
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
                rlSubmit.setBackgroundResource(R.drawable.chinese_evaluate_shellwindow_nimingtijao_nor);
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
        optCheckCorlor = 0xFF755942;
        optUncheckColor = 0xFF9D7F66;
        scoreCheckColor = 0xFF755942;
        scoreUncheckColor = 0xFF9D7F66;
        super.initData();
    }
    private void setTextTypeFace(){
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
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
    }

    @Override
    public void showUploadFailPager() {
        logger.i("showUploadFailPager");
//        rlBackground.setBackgroundResource(R.drawable.livevideo_evaluateteacher_bg1_nor);
        ivResult.setImageResource(R.drawable.chinese_evaluate_shellwindow_criterion_tijiaosibai_bg);
        super.showUploadFailPager();
    }

    @Override
    public void showSuccessPager(CountDownCallback callback) {
        logger.i("showUploadSuccessPager");
//        rlBackground.setBackgroundResource(R.drawable.livevideo_evaluateteacher_bg1_nor);
        ivResult.setImageResource(R.drawable.chinese_evaluate_shellwindow_criterion_ganxiepingjia_bg);
        super.showSuccessPager(callback);
    }

    @Override
    public void setReUpload() {
        ivTryagaingLoading.setVisibility(View.GONE);
        rlReSubmit.setEnabled(true);
        super.setReUpload();
    }
}

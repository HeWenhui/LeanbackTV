package com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager;

import android.content.Context;
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

    public PrimaryScienceEvaluateTeacherPager(Context context, LiveGetInfo getInfo) {
        super(context, getInfo);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_primaryscience_evaluate_teacher, null);
        ivLoading = mView.findViewById(R.id.iv_livevideo_smallenglish_evaluate_loading);
        super.initView();
        return mView;
    }

    @Override
    public void initListener() {
        rlSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                rlSubmit.setBackgroundResource(R.drawable.lspj_tanchuang_btn_tijiao_loading);
                ivLoading.setVisibility(View.VISIBLE);
                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        super.initListener();
    }

    @Override
    public void initData() {
        optCheckCorlor = 0xFFFF7403;
        optUncheckColor = 0xFFBC7D57;
        scoreCheckColor = 0xFF8F4D26;
        scoreUncheckColor = 0xFFBC7D57;
        super.initData();
    }
    @Override
    public void showUploadFailPager() {
        rlBackground.setBackgroundResource(R.drawable.lspj_pingjia_thanks_bg_img_normal);
        ivResult.setImageResource(R.drawable.lspj_pingjia_result_failure_img_normal);
        super.showUploadFailPager();
    }

    @Override
    public void showSuccessPager(CountDownCallback callback) {
        super.showSuccessPager(callback);
    }
}

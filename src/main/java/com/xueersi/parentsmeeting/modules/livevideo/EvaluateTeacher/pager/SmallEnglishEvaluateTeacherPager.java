package com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

/**
 * Created by：WangDe on 2018/12/2 15:18
 */
public class SmallEnglishEvaluateTeacherPager extends BaseEvaluateTeacherPaper {

    private ImageView ivLoading;

    public SmallEnglishEvaluateTeacherPager(Context context, LiveGetInfo getInfo) {
        super(context, getInfo);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_smallenglish_evaluate_teacher, null);
        ivLoading = mView.findViewById(R.id.iv_livevideo_smallenglish_evaluate_loading);
        super.initView();
        return mView;
    }

    @Override
    public void initListener() {
        rlSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                rlSubmit.setBackgroundResource(R.drawable.livevideo_evaluateteacher_shellwindow_tijiao_btn_loading);
                ivLoading.setVisibility(View.VISIBLE);
                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        super.initListener();
    }

    @Override
    public void initData() {
        optCheckCorlor = 0xFFB73800;
        optUncheckColor = 0xFF7B6E6E;
        scoreCheckColor = 0xFFB73800;
        scoreUncheckColor = 0xFF7B6E6E;

        super.initData();
    }
    @Override
    public void showUploadFailPager() {
        rlBackground.setBackgroundResource(R.drawable.livevideo_evaluateteacher_bg1_nor);
        ivResult.setImageResource(R.drawable.livevideo_evaluateteacher_shibai_img_nor);
        super.showUploadFailPager();
    }

    @Override
    public void showSuccessPager(CountDownCallback callback) {
        super.showSuccessPager(callback);
    }
}

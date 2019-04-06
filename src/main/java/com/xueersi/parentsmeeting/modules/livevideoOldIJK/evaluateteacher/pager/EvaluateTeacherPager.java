package com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.pager;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

/**
 * Created by：WangDe on 2018/11/27 15:58
 */
public class EvaluateTeacherPager extends BaseEvaluateTeacherPaper {

    private TextView tvResultStatus;
    private ProgressBar pbLoading;

    public EvaluateTeacherPager(Context context) {
        super(context);
    }

    public EvaluateTeacherPager(Context context, LiveGetInfo getInfo) {
        super(context, getInfo);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_evaluate_teacher, null);
        tvResultStatus = mView.findViewById(R.id.tv_livevideo_evaluate_submit_status);
        pbLoading = mView.findViewById(R.id.pb_livevideo_evaluate_submit_loading);
        super.initView();
        rlSubmit.setAlpha(0.6f);
        return mView;
    }

    @Override
    public void initData() {
        submitAlpha = 0.6f;
        super.initData();
    }

    @Override
    public void initListener() {
        rlSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                rlSubmit.setAlpha(0.6f);
                pbLoading.setVisibility(View.VISIBLE);
                buttonOnClick.submit(mainEva, tutorEva);

            }
        });
        rlReSubmit.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View view) {
                rlReSubmit.setEnabled(false);
                rlReSubmit.setAlpha(0.6f);
                buttonOnClick.submit(mainEva, tutorEva);
            }
        });
        super.initListener();
    }

    @Override
    public void showSuccessPager(CountDownCallback callback) {
        logger.i("showUploadSuccessPager");
        tvResultStatus.setText("感谢评价");
        ivResult.setImageResource(R.drawable.ic_monkey_success_img_normal);
        super.showSuccessPager(callback);
    }

    public void showUploadFailPager() {
        logger.i("showUploadFailPager");
        tvResultStatus.setText("提交失败，请重试!");
        ivResult.setImageResource(R.drawable.ic_monkey_img_normal);
        super.showUploadFailPager();
    }

    @Override
    public void setReUpload() {
        rlReSubmit.setAlpha(1.0f);
        super.setReUpload();
    }
}

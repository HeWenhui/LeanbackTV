package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LearnPsReportBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.PsLearnReportTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business.LearnReportHttp;
import com.xueersi.ui.widget.RatingBar;

/**
 * Created by David on 2018/7/16.
 */

public class LearnPsReportPager extends BasePager {
    String TAG = "LearnPsReportPager";
    LearnReportEntity reportEntity;
    LearnReportHttp liveBll;
    LearnPsReportBll learnReportBll;
    LinearLayout firstview, secondview;
    /** 查看评价，查看按钮 */
    Button btLearnreportCheck, btLearnreportSubmit;
    /** 学习时长 */
    TextView tvLearnfeedbackDuration;
    /** 答题正确率 */
    TextView tvLearnfeedbackAccuracy;
    /** 平均正确率 */
    TextView tvLearnfeedbackAccuracyAverage;
    /** 你的排名 */
    TextView tvLearnfeedbackRanking;
    /** 星星评价 */
    RatingBar rbEvaluateStar, rbEvaluateStar2, rbEvaluateStar3;
    /** 星星评价的数量 */
    int starCount = 5, starCount2 = 5, starCount3 = 5;
    private Activity activity;
    ImageView close;
    private LogToFile logToFile;

    public LearnPsReportPager(Context context, LearnReportEntity reportEntity, LearnReportHttp liveBll, LearnPsReportBll learnReportBll) {
        super(context);
        this.reportEntity = reportEntity;
        this.liveBll = liveBll;
        this.learnReportBll = learnReportBll;
        this.activity = (Activity) context;
        logToFile = new LogToFile(context, TAG);
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_learnpsreport, null);
        close = (ImageView) mView.findViewById(R.id.iv_livevideo_pslearnreport_close);
        firstview = (LinearLayout) mView.findViewById(R.id.ll_pslearnreport_firstview);
        secondview = (LinearLayout) mView.findViewById(R.id.ll_pslearnreport_secondview);
        tvLearnfeedbackDuration = (TextView) mView.findViewById(R.id.tv_learningtime);
        tvLearnfeedbackAccuracy = (TextView) mView.findViewById(R.id.tv_yourcorrectrate);
        tvLearnfeedbackAccuracyAverage = (TextView) mView.findViewById(R.id.tv_classcorrectrate);
        tvLearnfeedbackRanking = (TextView) mView.findViewById(R.id.tv_yourranking);
        btLearnreportCheck = (Button) mView.findViewById(R.id.bt_livevideo_psreport_evaluation);
        btLearnreportSubmit = (Button) mView.findViewById(R.id.bt_livevideo_psreport_submit);
        rbEvaluateStar = (RatingBar) mView.findViewById(R.id.rb_livevideo_evaluate_star);
        rbEvaluateStar2 = (RatingBar) mView.findViewById(R.id.rb_livevideo_evaluate_star2);
        rbEvaluateStar3 = (RatingBar) mView.findViewById(R.id.rb_livevideo_evaluate_star3);
        registerListener();
        return mView;
    }

    private void registerListener() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                learnReportBll.stopLearnReport();
            }
        });
        btLearnreportCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstview.setVisibility(View.GONE);
                btLearnreportCheck.setVisibility(View.GONE);
                secondview.setVisibility(View.VISIBLE);
                btLearnreportSubmit.setVisibility(View.VISIBLE);
            }
        });
        rbEvaluateStar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {

            @Override
            public void onRatingChange(int RatingCount) {
                starCount = RatingCount;
            }
        });
        rbEvaluateStar2.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {

            @Override
            public void onRatingChange(int RatingCount) {
                starCount2 = RatingCount;
            }
        });
        rbEvaluateStar3.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {

            @Override
            public void onRatingChange(int RatingCount) {
                starCount3 = RatingCount;
            }
        });
        btLearnreportSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] score = {starCount, starCount2, starCount3};
                liveBll.sendTeacherEvaluate(score, new HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        XesMobAgent.liveLearnReport("feedback-ok");
                        logToFile.d("feedback-ok");
                        learnReportBll.stopLearnReport();
                        PsLearnReportTipDialog micTipDialogs = new PsLearnReportTipDialog(activity);
                        micTipDialogs.showDialog();
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XesMobAgent.liveLearnReport("feedback-fail");
                        logToFile.d("feedback-fail");
                        showToast("提交失败,请重试");
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        XesMobAgent.liveLearnReport("feedback-error");
                        logToFile.d("feedback-error");
                        liveBll.showToast(responseEntity.getErrorMsg());
                    }
                });
            }
        });
    }

    @Override
    public void initData() {
        LearnReportEntity.ReportEntity stu = reportEntity.getStu();
        int time = stu.getTime() / 60;
        tvLearnfeedbackDuration.setText(time + "分钟");
        tvLearnfeedbackAccuracy.setText("0".equals(stu.getRate()) ? "0%" : stu.getRate());
        tvLearnfeedbackAccuracyAverage.setText("0".equals(stu.getAverageRate()) ? "0%" : "" + stu.getAverageRate());
        tvLearnfeedbackRanking.setText("第" + stu.getRank() + "名");
    }
}

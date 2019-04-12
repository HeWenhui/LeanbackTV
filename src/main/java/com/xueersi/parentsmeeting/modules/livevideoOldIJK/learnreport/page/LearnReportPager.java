package com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.page;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business.LearnReportBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business.LearnReportHttp;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoFloatTitle;
import com.xueersi.ui.widget.RatingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linyuqiang 学习报告
 */
public class LearnReportPager extends LiveBasePager {
    String TAG = "LearnReportPager";
    LearnReportEntity reportEntity;
    /** 学习报告,学习反馈和教学评价 */
    ViewPager vpLearnreport;
    ViewPagerAdapter mAdapter;
    /** 学习报告,老师提示页面，显示在ViewPager上面 */
    RelativeLayout rlLearnreport;
    View vLearnReportCheck;
    List<View> reportViewList = new ArrayList<View>();
    /** 学习报告 */
    View vLearnFeedback;
    /** 直播任务 */
    View vLearnTask;
    /** 教师评价 */
    View vEvaluate;
    /** 查看评价，评价提示1 */
    TextView tvLearnreportCheckTitle;
    TextView tvLearnreportCheckTip1;
    /** 查看评价，查看按钮 */
    Button btLearnreportCheck;
    /** 学习时长 */
    TextView tvLearnfeedbackDuration;
    /** 答题正确率 */
    TextView tvLearnfeedbackAccuracy;
    /** 平均正确率 */
    TextView tvLearnfeedbackAccuracyAverage;
    /** 你的排名 */
    TextView tvLearnfeedbackRanking;
    /** 较上节课 */
    TextView tvLearnfeedbackRankingTolastLable, tvLearnfeedbackRankingTolast;
    TextView tvEvaluateUser;
    /** 星星评价 */
    RatingBar rbEvaluateStar, rbEvaluateStar2, rbEvaluateStar3;
    /** 星星评价的数量 */
    int starCount = 5, starCount2 = 5, starCount3 = 5;
    /** 左右箭头 */
    View leftView, rightView;
    /** 教师评价输入提交 */
    Button btEvaluateSubmit;
    /** 教师评价提交成功 */
    View vEvaluateSuccess;
    /** 评价提交关闭 */
    Button btEvaluateSubmitClose;
    LearnReportHttp liveBll;
    LearnReportBll learnReportBll;
    private LogToFile logToFile;

    public LearnReportPager(Context context, LearnReportEntity reportEntity, LearnReportHttp liveBll, LearnReportBll learnReportBll) {
        super(context);
        this.reportEntity = reportEntity;
        this.liveBll = liveBll;
        this.learnReportBll = learnReportBll;
        logToFile = new LogToFile(context, TAG);
        initData();
    }

    @Override
    public View initView() {
        reportViewList = new ArrayList<View>();
        mView = View.inflate(mContext, R.layout.page_livevodeo_learnrepost, null);
        vLearnReportCheck = View.inflate(mContext, R.layout.page_livevideo_learning_check, null);

        tvLearnreportCheckTitle = (TextView) vLearnReportCheck.findViewById(R.id.tv_livevideo_learnreport_check_title);
        tvLearnreportCheckTip1 = (TextView) vLearnReportCheck.findViewById(R.id.tv_livevideo_learnreport_check_tip1);
        btLearnreportCheck = (Button) vLearnReportCheck.findViewById(R.id.bt_livevideo_learnreport_check);

        vpLearnreport = (ViewPager) mView.findViewById(R.id.vp_livevideo_learnreport);
        rlLearnreport = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_learnreport);
        leftView = mView.findViewById(R.id.iv_livevideo_learnfeedback_left);
        rightView = mView.findViewById(R.id.iv_livevideo_learnfeedback_right);

        vLearnFeedback = View.inflate(mContext, R.layout.page_livevideo_learning_feedback, null);
        tvLearnfeedbackDuration = (TextView) vLearnFeedback.findViewById(R.id.tv_livevideo_learnfeedback_duration);
        tvLearnfeedbackAccuracy = (TextView) vLearnFeedback.findViewById(R.id.tv_livevideo_learnfeedback_accuracy);
        tvLearnfeedbackAccuracyAverage = (TextView) vLearnFeedback.findViewById(R.id
                .tv_livevideo_learnfeedback_accuracy_average);
        tvLearnfeedbackRanking = (TextView) vLearnFeedback.findViewById(R.id.tv_livevideo_learnfeedback_ranking);
        tvLearnfeedbackRankingTolastLable = (TextView) vLearnFeedback.findViewById(R.id
                .tv_livevideo_learnfeedback_ranking_tolast_lable);
        tvLearnfeedbackRankingTolast = (TextView) vLearnFeedback.findViewById(R.id
                .tv_livevideo_learnfeedback_ranking_tolast);
        //直播任务
        vLearnTask = View.inflate(mContext, R.layout.page_livevideo_learning_task, null);
        //教师评价
        vEvaluate = View.inflate(mContext, R.layout.page_livevideo_evaluate, null);
        tvEvaluateUser = (TextView) vEvaluate.findViewById(R.id.tv_livevideo_evaluate_user);
        rbEvaluateStar = (RatingBar) vEvaluate.findViewById(R.id.rb_livevideo_evaluate_star);
        rbEvaluateStar2 = (RatingBar) vEvaluate.findViewById(R.id.rb_livevideo_evaluate_star2);
        rbEvaluateStar3 = (RatingBar) vEvaluate.findViewById(R.id.rb_livevideo_evaluate_star3);
        btEvaluateSubmit = (Button) vEvaluate.findViewById(R.id.bt_livevideo_evaluate_submit);
        //提交关闭页面
        vEvaluateSuccess = View.inflate(mContext, R.layout.page_livevideo_evaluate_success, null);
        btEvaluateSubmitClose = (Button) vEvaluateSuccess.findViewById(R.id.bt_livevideo_evaluate_submit_close);
        vpLearnreport.setPageMargin(26);
        return mView;
    }

    @Override
    public void initData() {
        rlLearnreport.addView(vLearnReportCheck);
        mAdapter = new ViewPagerAdapter(reportViewList);
        reportViewList.add(vLearnFeedback);
        //暂时去掉直播挑战
        //reportViewList.add(vLearnTask);
        reportViewList.add(vEvaluate);
        vpLearnreport.setAdapter(mAdapter);
        tvLearnreportCheckTitle.setText(String.format("%s同学，我是%s", reportEntity.getStu().getStuName(),
                reportEntity.getStu().getTeacherName()));
        tvLearnreportCheckTip1.setText("同时，请别忘了对本次直播课进行点评");
        btLearnreportCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XesMobAgent.liveLearnReport("check");
                logToFile.d("check");
                rlLearnreport.removeAllViews();
                rightView.setVisibility(View.VISIBLE);
                vpLearnreport.setVisibility(View.VISIBLE);
            }
        });
        leftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpLearnreport.setCurrentItem(vpLearnreport.getCurrentItem() - 1, true);
            }
        });
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpLearnreport.setCurrentItem(vpLearnreport.getCurrentItem() + 1, true);
            }
        });
        vpLearnreport.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == mAdapter.getCount() - 1) {
                    leftView.setVisibility(View.VISIBLE);
                    rightView.setVisibility(View.INVISIBLE);
                } else if (i == 0) {
                    leftView.setVisibility(View.INVISIBLE);
                    rightView.setVisibility(View.VISIBLE);
                } else {
                    leftView.setVisibility(View.VISIBLE);
                    rightView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        btEvaluateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ViewGroup group = (ViewGroup) vEvaluate;
//                group.removeAllViews();
//                group.addView(vEvaluateSuccess);
                int[] score = {starCount, starCount2, starCount3};
                liveBll.sendTeacherEvaluate(score, new HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        XesMobAgent.liveLearnReport("feedback-ok");
                        logToFile.d("feedback-ok");
                        ViewGroup group = (ViewGroup) vEvaluate;
                        group.removeAllViews();
                        group.addView(vEvaluateSuccess);
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
        btEvaluateSubmitClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                learnReportBll.stopLearnReport();
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
        LearnReportEntity.ReportEntity stu = reportEntity.getStu();
        int time = stu.getTime() / 60;
        tvLearnfeedbackDuration.setText(time + "分钟");
        tvLearnfeedbackAccuracy.setText(stu.getRate());
        tvLearnfeedbackAccuracyAverage.setText("" + stu.getAverageRate());
        tvLearnfeedbackRanking.setText("第" + stu.getRank() + "名");
        int lastRank = stu.getLastRank();
        if (lastRank == 0) {
            //tvLearnfeedbackRankingTolastLable.setVisibility(View.INVISIBLE);
            tvLearnfeedbackRankingTolast.setVisibility(View.INVISIBLE);
        } else {
            int difference = stu.getRank() - lastRank;
            if (difference < 0) {
                tvLearnfeedbackRankingTolast.setText("提高" + (-difference) + "名");
            } else if (difference > 0) {
                tvLearnfeedbackRankingTolast.setText("退步" + difference + "名");
            } else {
                tvLearnfeedbackRankingTolast.setText("排名相同");
            }
        }
        tvEvaluateUser.setText(reportEntity.getStu().getStuName() + " 你好，");
        rbEvaluateStar.setStar(5);
        rbEvaluateStar2.setStar(5);
        rbEvaluateStar3.setStar(5);
        vEvaluate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean hide = mInputMethodManager.hideSoftInputFromWindow(vpLearnreport.getWindowToken(), 0);
                return false;
            }
        });
        ((LiveVideoFloatTitle) vEvaluateSuccess.findViewById(R.id.lrf_livevideo_evaluate_title)).setOnCancleClick(new LiveVideoFloatTitle.OnCancleClick() {
            @Override
            public void onCancleClick() {

            }
        });
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public ViewPagerAdapter(List<View> list) {
            this.mViewList = list;
        }

        @Override
        public int getCount() {
            if (mViewList != null && mViewList.size() > 0) {
                return mViewList.size();
            } else {
                return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViewList.get(position);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }
}

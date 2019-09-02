package com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.page.LearnReportPager;

/**
 * Created by linyuqiang on 2016/9/23.
 */
public class LearnReportBll implements LearnReportAction, Handler.Callback {
    String TAG = "LearnReportBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    private LearnReportHttp mLiveBll;
    /** 学习报告的布局 */
    private RelativeLayout rlLearnReportContent;
    /** 学习报告 */
    private LearnReportPager mLearnReport;
    /** 显示学习报告 */
    private static final int SHOW_LEARNREPORT = 4;
    /** 隐藏学习报告 */
    private static final int NO_LEARNREPORT = 5;
    /** 当前是否正在显示学习报告 */
    private boolean mIsShowLearnReport = false;

    public LearnReportBll(Activity activity) {
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
    }

    public void setLiveBll(LearnReportHttp mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_LEARNREPORT: {
                String s = "handleMessage:SHOW_LEARNREPORT:mIsShow=" + mIsShowLearnReport;
                if (!mIsShowLearnReport) {
                    mIsShowLearnReport = true;
                }
                mLogtf.d(s);
            }
            case NO_LEARNREPORT: {
                String s = "handleMessage:NO_LEARNREPORT:mIsShow=" + mIsShowLearnReport;
                if (mIsShowLearnReport) {
                    mIsShowLearnReport = false;
                    learnReportViewGone();
                }
                mLogtf.d(s);
            }
        }
        return false;
    }

    public void initView(RelativeLayout bottomContent) {
        //学习报告
        rlLearnReportContent = new RelativeLayout(activity);
        rlLearnReportContent.setId(R.id.rl_livevideo_content_learnreport);
        bottomContent.addView(rlLearnReportContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onLearnReport(final LearnReportEntity reportEntity) {
        mVPlayVideoControlHandler.post(new Runnable() {

            @Override
            public void run() {
                mLearnReport = new LearnReportPager(activity, reportEntity, mLiveBll, LearnReportBll.this);
                rlLearnReportContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                rlLearnReportContent.addView(mLearnReport.getRootView(), params);
                rlLearnReportContent.setVisibility(View.VISIBLE);
                activity.findViewById(R.id.rl_livevideo_content_question).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.rl_livevideo_content_readpackage).setVisibility(View.INVISIBLE);
//                rlQuestionContent.setVisibility(View.INVISIBLE);
//                rlRedpacketContent.setVisibility(View.INVISIBLE);
                mLogtf.d("onLearnReport");
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
        mVPlayVideoControlHandler.sendEmptyMessage(SHOW_LEARNREPORT);
    }

    /**
     * 停止显示学习报告
     */
    public void stopLearnReport() {
        mVPlayVideoControlHandler.post(new Runnable() {

            @Override
            public void run() {
                mLearnReport = null;
                rlLearnReportContent.removeAllViews();
                rlLearnReportContent.setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.rl_livevideo_content_question).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.rl_livevideo_content_readpackage).setVisibility(View.VISIBLE);
//                rlQuestionContent.setVisibility(View.VISIBLE);
//                rlRedpacketContent.setVisibility(View.VISIBLE);
            }
        });
        mVPlayVideoControlHandler.sendEmptyMessage(NO_LEARNREPORT);
    }

    /**
     * 学习报告隐藏
     */
    private void learnReportViewGone() {
        mIsShowLearnReport = false;
    }

}

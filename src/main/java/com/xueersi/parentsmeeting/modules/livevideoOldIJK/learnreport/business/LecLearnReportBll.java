package com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.leclearnreport.business.LecLearnReportHttp;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LecLearnReportPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by linyuqiang on 2016/9/23.
 */
public class LecLearnReportBll implements LecLearnReportAction, Handler.Callback {
    String TAG = "LecLearnReportBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    private LecLearnReportHttp mLiveBll;
    /** 学习报告的布局 */
    private RelativeLayout rlLearnReportContent;
    /** 学习报告 */
    private LecLearnReportPager mLearnReport;
    /** 显示学习报告 */
    private static final int SHOW_LEARNREPORT = 4;
    /** 隐藏学习报告 */
    private static final int NO_LEARNREPORT = 5;
    /** 当前是否正在显示学习报告 */
    private boolean mIsShowLearnReport = false;
    protected ShareDataManager mShareDataManager;
    /**
     * 存学习报告
     */
    private static final String lecLearnReport = LiveVideoConfig.LEC_LEARN_REPORT;
    boolean isGetReport = false;
    String liveId;

    public LecLearnReportBll(Activity activity) {
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public void setLiveBll(LecLearnReportHttp mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void setmShareDataManager(ShareDataManager mShareDataManager) {
        this.mShareDataManager = mShareDataManager;
        String learn = mShareDataManager.getString(lecLearnReport, "{}", ShareDataManager.SHAREDATA_NOT_CLEAR);
        try {
            JSONObject jsonObject = new JSONObject(learn);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date date = new Date();
            String dayStr = dateFormat.format(date);
            JSONObject dayObj;
            if (jsonObject.has(dayStr)) {
                dayObj = jsonObject.getJSONObject(dayStr);
                if (dayObj.has(liveId)) {
//                    isGetReport = true;
                    mLogtf.d("setmShareDataManager:isGetReport=true");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            break;
            case NO_LEARNREPORT: {
                String s = "handleMessage:NO_LEARNREPORT:mIsShow=" + mIsShowLearnReport;
                if (mIsShowLearnReport) {
                    mIsShowLearnReport = false;
                    learnReportViewGone();
                }
                mLogtf.d(s);
            }
            break;
            default:
                break;
        }
        return false;
    }

    public void initView(RelativeLayout bottomContent) {
        //学习报告
        if (rlLearnReportContent == null) {
            rlLearnReportContent = new RelativeLayout(activity);
            rlLearnReportContent.setId(R.id.rl_livevideo_content_learnreport);
        } else {
            ViewGroup group = (ViewGroup) rlLearnReportContent.getParent();
            if (group != null) {
                group.removeView(rlLearnReportContent);
            }
        }
        bottomContent.addView(rlLearnReportContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (mLearnReport != null) {
            rlLearnReportContent.removeAllViews();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            rlLearnReportContent.addView(mLearnReport.getRootView(), params);
            rlLearnReportContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLearnReport(final String liveId) {
        if (isGetReport) {
            return;
        }
        isGetReport = true;
        mLiveBll.getLecLearnReport(1000, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                try {
                    String learn = mShareDataManager.getString(lecLearnReport, "{}", ShareDataManager.SHAREDATA_NOT_CLEAR);
                    JSONObject jsonObject = new JSONObject(learn);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    Date date = new Date();
                    String dayStr = dateFormat.format(date);
                    JSONObject dayObj;
                    if (jsonObject.has(dayStr)) {
                        dayObj = jsonObject.getJSONObject(dayStr);
                    } else {
                        dayObj = new JSONObject();
                    }
                    dayObj.put(liveId, "true");
                    jsonObject = new JSONObject();
                    jsonObject.put(dayStr, dayObj);
                    mShareDataManager.put(lecLearnReport, jsonObject.toString(), ShareDataManager.SHAREDATA_NOT_CLEAR);
                    mLogtf.d("getLecLearnReport:onDataSucess");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LearnReportEntity reportEntity = (LearnReportEntity) objData[0];
                mLearnReport = new LecLearnReportPager(activity, reportEntity, LecLearnReportBll.this);
                rlLearnReportContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                rlLearnReportContent.addView(mLearnReport.getRootView(), params);
                rlLearnReportContent.setVisibility(View.VISIBLE);
                activity.findViewById(R.id.rl_livevideo_content_question).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.rl_livevideo_content_readpackage).setVisibility(View.INVISIBLE);
//                rlQuestionContent.setVisibility(View.INVISIBLE);
//                rlRedpacketContent.setVisibility(View.INVISIBLE);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
                mVPlayVideoControlHandler.sendEmptyMessage(SHOW_LEARNREPORT);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                mLogtf.d("getLecLearnReport:onDataFail");
                isGetReport = false;
            }
        });
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

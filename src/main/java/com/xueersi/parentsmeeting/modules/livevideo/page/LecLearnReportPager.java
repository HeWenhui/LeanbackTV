package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LearnReportBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LecLearnReportBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoFloatTitle;
import com.xueersi.xesalib.view.ratingbar.RatingBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linyuqiang 学习报告
 */
public class LecLearnReportPager extends BasePager {
    String TAG = "LecLearnReportPager";
    LearnReportEntity reportEntity;
    LiveBll liveBll;
    LecLearnReportBll learnReportBll;
    private LogToFile logToFile;
    TextView tv_livelec_dialog_xxsc_text;
    TextView tv_livelec_dialog_zhengquelv_text;
    TextView tv_livelec_dialog_zhengquelv_text2;
    TextView tv_livelec_dialog_paiming_text;
    TextView tv_livelec_dialog_paiming_text2;

    public LecLearnReportPager(Context context, LearnReportEntity reportEntity, LiveBll liveBll, LecLearnReportBll learnReportBll) {
        super(context);
        if (reportEntity == null) {
            reportEntity = new LearnReportEntity();
            LearnReportEntity.ReportEntity stu = new LearnReportEntity.ReportEntity();
            stu.setTime(111111);
            stu.setRate("20%");
            stu.setAverageRate("25%");
            stu.setRank(12);
            stu.setLastRank(11);
            reportEntity.setStu(stu);
        }
        this.reportEntity = reportEntity;
        this.liveBll = liveBll;
        this.learnReportBll = learnReportBll;
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevodeo_leclearnrepost, null);
        tv_livelec_dialog_xxsc_text = (TextView) mView.findViewById(R.id.tv_livelec_dialog_xxsc_text);
        tv_livelec_dialog_zhengquelv_text = (TextView) mView.findViewById(R.id.tv_livelec_dialog_zhengquelv_text);
        tv_livelec_dialog_zhengquelv_text2 = (TextView) mView.findViewById(R.id.tv_livelec_dialog_zhengquelv_text2);
        tv_livelec_dialog_paiming_text = (TextView) mView.findViewById(R.id.tv_livelec_dialog_paiming_text);
        tv_livelec_dialog_paiming_text2 = (TextView) mView.findViewById(R.id.tv_livelec_dialog_paiming_text2);
        return mView;
    }

    @Override
    public void initData() {
        LearnReportEntity.ReportEntity stu = reportEntity.getStu();
        int time = stu.getTime() / 60;
        tv_livelec_dialog_xxsc_text.setText(time + "分钟");
        tv_livelec_dialog_zhengquelv_text.setText(stu.getRate());
        tv_livelec_dialog_zhengquelv_text2.setText(stu.getAverageRate());
        tv_livelec_dialog_paiming_text.setText("" + stu.getRank());
        int lastRank = stu.getLastRank();
        if (lastRank == 0) {
            tv_livelec_dialog_paiming_text2.setVisibility(View.INVISIBLE);
        } else {
            int difference = stu.getRank() - lastRank;
            if (difference < 0) {
                tv_livelec_dialog_paiming_text2.setText("提高" + (-difference) + "名");
            } else if (difference > 0) {
                tv_livelec_dialog_paiming_text2.setText("退步" + difference + "名");
            } else {
                tv_livelec_dialog_paiming_text2.setText("排名相同");
            }
        }
    }

}

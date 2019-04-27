package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page.SpeechCollectiveNo2Pager;

/**
 * Created by linyuqiang on 2019/4/26.
 * 集体发言2期
 */
public class SpeechCollectiveNo2Bll {
    private RelativeLayout mRootView;
    private String TAG = "SpeechCollectiveNo2Bll";
    private Logger logger = LoggerFactory.getLogger(TAG);
    private Context context;
    private LogToFile mLogtf;

    public SpeechCollectiveNo2Bll(Context context) {
        this.context = context;
        mLogtf = new LogToFile(context, TAG);
    }

    public void start(String roomId) {
        mLogtf.d("start:roomId=" + roomId);
        SpeechCollectiveNo2Pager speechCollectiveNo2Pager = new SpeechCollectiveNo2Pager(context, mRootView);
        speechCollectiveNo2Pager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                mRootView.removeView(basePager.getRootView());
            }
        });
        mRootView.addView(speechCollectiveNo2Pager.getRootView());
        speechCollectiveNo2Pager.start();
    }

    public void stop() {
        mLogtf.d("start:stop");
//        if (swvView != null) {
//            swvView.setStart(false);
//        }
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void setBottomContent(RelativeLayout mRootView) {
        this.mRootView = mRootView;
    }


}

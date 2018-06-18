package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseNbH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.NbH5CoursewareX5Pager;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import java.io.File;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5课件业务类
 */
public class H5CoursewareBll implements H5CoursewareAction {
    String TAG = "H5CoursewareBll";
    Context context;
    Handler handler = new Handler(Looper.getMainLooper());
    BaseNbH5CoursewarePager h5CoursewarePager;
    private LogToFile logToFile;
    RelativeLayout bottomContent;
    LiveVideoActivityBase liveVideoActivityBase;

    public H5CoursewareBll(Context context) {
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        this.context = context;
        liveVideoActivityBase = (LiveVideoActivityBase) context;
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
        if (h5CoursewarePager != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bottomContent.addView(h5CoursewarePager.getRootView(), lp);
        }
    }

    public boolean onBack() {
        if (h5CoursewarePager != null) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(context, (BaseApplication) BaseApplication.getContext(), false,
                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (h5CoursewarePager != null) {
                        bottomContent.removeView(h5CoursewarePager.getRootView());
                        h5CoursewarePager = null;
                    }
                }
            });
            cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("您正在做互动实验，是否结束？",
                    VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onH5Courseware(final String url, final String status) {
//        logToFile.i("onH5Courseware:url=" + url + ",status=" + status);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if ("on".equals(status)) {
                    if (h5CoursewarePager != null) {
                        if (h5CoursewarePager.getUrl().equals(url)) {
                            logToFile.i("onH5Courseware:url.equals");
                            return;
                        } else {
                            logToFile.i("onH5Courseware:url=" + h5CoursewarePager.getUrl());
                            bottomContent.removeView(h5CoursewarePager.getRootView());
                        }
                    }
                    h5CoursewarePager = new NbH5CoursewareX5Pager(context, url);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    bottomContent.addView(h5CoursewarePager.getRootView(), lp);
                    liveVideoActivityBase.setAutoOrientation(false);
                    liveVideoActivityBase.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    if (h5CoursewarePager != null) {
                        liveVideoActivityBase.setAutoOrientation(true);
                        bottomContent.removeView(h5CoursewarePager.getRootView());
                        h5CoursewarePager = null;
                    }
                }
            }
        });
    }
}

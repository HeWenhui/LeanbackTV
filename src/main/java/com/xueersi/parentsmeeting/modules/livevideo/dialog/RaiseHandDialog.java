package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tencent.bugly.crashreport.BuglyLog;
import com.xueersi.common.base.XrsCrashReport;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by Administrator on 2017/5/8.
 * 接麦举手
 */
public class RaiseHandDialog extends BaseAlertDialog {
    private String TAG = "RaiseHandDialog";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    private RaiseHandGiveup raiseHandGiveup;
    private TextView tvRaiseHandsCount;
    private FrameLayout flRaiseHandsContent;
    /** 举手等待 */
    View rlRaiseHandsWait;
    /** 举手放弃 */
    View rlRaiseHandsGiveup;
    /** 举手失败 */
    View rlRaiseHandsFail;
    /** 举手成功 */
    View rlRaiseHandsSuccess;
    public final static int WAIT = 0;
    public final static int GIVE_UP = 1;
    public final static int FAIL = 2;
    public final static int SUCCESS = 3;
    public int status = WAIT;
    int count;

    public RaiseHandDialog(Context context, Application application) {
        super(context, application, false);
    }

    private static int showCount = 0;

    @Override
    public void showDialog() {
        showDialog("");
    }

    public void showDialog(String method) {
        super.showDialog();
        BuglyLog.d(TAG, "showDialog:showCount=" + showCount + ",method=" + method);
        showCount++;
        if (showCount > 1) {
            XrsCrashReport.postCatchedException(new Exception());
        }
    }

    @Override
    public void cancelDialog() {
        cancelDialog("");
    }

    public void cancelDialog(String method) {
        super.cancelDialog();
        BuglyLog.d(TAG, "cancelDialog:showCount=" + showCount + ",method=" + method);
        showCount--;
        if (showCount != 0) {
            XrsCrashReport.postCatchedException(new Exception());
        }
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = mInflater.inflate(R.layout.dialog_livevideo_raisehand, null);
        flRaiseHandsContent = (FrameLayout) view.findViewById(R.id.fl_livevideo_raise_hands_content);
        rlRaiseHandsWait = view.findViewById(R.id.rl_livevideo_raise_hands_wait);
        rlRaiseHandsGiveup = view.findViewById(R.id.rl_livevideo_raise_hands_giveup);
        rlRaiseHandsFail = view.findViewById(R.id.rl_livevideo_raise_hands_fail);
        rlRaiseHandsSuccess = view.findViewById(R.id.rl_livevideo_raise_hands_success);
        tvRaiseHandsCount = (TextView) view.findViewById(R.id.tv_livevideo_raise_hands_count);
        view.findViewById(R.id.bt_livevideo_raise_hands_giveup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = GIVE_UP;
                rlRaiseHandsWait.setVisibility(View.GONE);
                rlRaiseHandsGiveup.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.bt_livevideo_raise_hands_giveup_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = WAIT;
                rlRaiseHandsWait.setVisibility(View.VISIBLE);
                rlRaiseHandsGiveup.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.bt_livevideo_raise_hands_giveup_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (raiseHandGiveup != null) {
                    raiseHandGiveup.onGiveup();
                }
            }
        });
        return view;
    }

    public void setRaiseHandsCount(int count) {
        if (status == WAIT || status == GIVE_UP) {
            if (this.count != count) {
                this.count = count;
                tvRaiseHandsCount.setText(count + "人");
            }
        }
    }

    public boolean setFail() {
        logger.i("setFail:status=" + status);
        int oldStatus = status;
        status = FAIL;
        flRaiseHandsContent.removeView(rlRaiseHandsWait);
        flRaiseHandsContent.removeView(rlRaiseHandsGiveup);
        flRaiseHandsContent.removeView(rlRaiseHandsSuccess);
        rlRaiseHandsFail.setVisibility(View.VISIBLE);
        return oldStatus != status;
    }

    public boolean setSuccess() {
        logger.i("setSuccess:status=" + status);
        int oldStatus = status;
        status = SUCCESS;
        flRaiseHandsContent.removeView(rlRaiseHandsWait);
        flRaiseHandsContent.removeView(rlRaiseHandsGiveup);
        flRaiseHandsContent.removeView(rlRaiseHandsFail);
        rlRaiseHandsSuccess.setVisibility(View.VISIBLE);
        return oldStatus != status;
    }

    public void setRaiseHandGiveup(RaiseHandGiveup raiseHandGiveup) {
        this.raiseHandGiveup = raiseHandGiveup;
    }

    public interface RaiseHandGiveup {
        void onGiveup();
    }
}

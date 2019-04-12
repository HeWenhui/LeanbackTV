package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by Administrator on 2017/5/8.
 * 接麦其他提示
 */
public class MicTipDialog extends BaseAlertDialog {
    private String TAG = "MicTipDialog";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    private TextView tvRaiseHandsCount;
    private FrameLayout flRaiseHandsContent;
    /** 举手失败 */
    View rlRaiseHandsFail;
    /** 举手成功 */
    View rlRaiseHandsSuccess;
    TextView tv_livevideo_raise_hands_fail_content;
    TextView tv_livevideo_raise_hands_success;
    TextView tv_livevideo_raise_hands_success_content;
    public final static int WAIT = 0;
    public final static int GIVE_UP = 1;
    public final static int FAIL = 2;
    public final static int SUCCESS = 3;
    public int status = WAIT;
    int count;

    public MicTipDialog(Context context) {
        super(context, (Application) BaseApplication.getContext(), false);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = mInflater.inflate(R.layout.dialog_livevideo_micdialog, null);
        flRaiseHandsContent = (FrameLayout) view.findViewById(R.id.fl_livevideo_raise_hands_content);
        rlRaiseHandsFail = view.findViewById(R.id.rl_livevideo_raise_hands_fail);
        tv_livevideo_raise_hands_fail_content = (TextView) view.findViewById(R.id.tv_livevideo_raise_hands_fail_content);
        tv_livevideo_raise_hands_success = (TextView) view.findViewById(R.id.tv_livevideo_raise_hands_success);
        tv_livevideo_raise_hands_success_content = (TextView) view.findViewById(R.id.tv_livevideo_raise_hands_success_content);
        rlRaiseHandsSuccess = view.findViewById(R.id.rl_livevideo_raise_hands_success);
        tvRaiseHandsCount = (TextView) view.findViewById(R.id.tv_livevideo_raise_hands_count);
        return view;
    }

    public boolean setFail(String msg) {
        logger.i( "setFail:status=" + status);
        int oldStatus = status;
        status = FAIL;
        flRaiseHandsContent.removeView(rlRaiseHandsSuccess);
        rlRaiseHandsFail.setVisibility(View.VISIBLE);
        tv_livevideo_raise_hands_fail_content.setText(msg);
        return oldStatus != status;
    }


    public boolean setSuccessTip(String msg) {
        logger.i( "setSuccessTip:status=" + status);
        int oldStatus = status;
        status = SUCCESS;
        flRaiseHandsContent.removeView(rlRaiseHandsFail);
        rlRaiseHandsSuccess.setVisibility(View.VISIBLE);
        tv_livevideo_raise_hands_success.setVisibility(View.INVISIBLE);
        tv_livevideo_raise_hands_success_content.setText(msg);
        return oldStatus != status;
    }

    public boolean setSuccess(String msg) {
        logger.i( "setSuccess:status=" + status);
        int oldStatus = status;
        status = SUCCESS;
        flRaiseHandsContent.removeView(rlRaiseHandsFail);
        rlRaiseHandsSuccess.setVisibility(View.VISIBLE);
        tv_livevideo_raise_hands_success.setVisibility(View.VISIBLE);
        tv_livevideo_raise_hands_success_content.setText(msg);
        return oldStatus != status;
    }

    @Override
    public void showDialog() {
        super.showDialog(false, false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelDialog();
            }
        }, 3000);
    }
}

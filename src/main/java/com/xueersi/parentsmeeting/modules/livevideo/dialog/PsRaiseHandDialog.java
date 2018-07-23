package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.view.alertdialog.BaseAlertDialog;

/**
 * Created by David on 2018/7/21.
 */

public class PsRaiseHandDialog extends BaseAlertDialog {
    private TextView tip;
    @Override
    protected View initDialogLayout(int i) {
        View view = mInflater.inflate(R.layout.dialog_livevideo_psraisehand, null);
        tip = (TextView) view.findViewById(R.id.tv_tip_detail);
        return view;
    }
    public void setRaiseHandsCount(int count) {
        tip.setText(count + "人");
    }

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

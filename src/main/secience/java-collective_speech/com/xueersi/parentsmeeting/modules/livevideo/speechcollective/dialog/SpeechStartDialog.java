package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.dialog;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by linyuqiang on 2019/4/29.
 * 集体发言
 */
public class SpeechStartDialog extends BaseAlertDialog {
    private TextView tip;

    public SpeechStartDialog(Context context) {
        super(context, (Application) context.getApplicationContext(), false);
    }

    @Override
    protected View initDialogLayout(int i) {
        View view = mInflater.inflate(R.layout.dialog_livevideo_psraisehand, null);
        tip = view.findViewById(R.id.tv_tip_detail);
        return view;
    }

    @Override
    protected void createDialog(View alertView, boolean isSystem) {
        super.createDialog(alertView, isSystem);
        Window window = mAlertDialog.getWindow();
        if (window != null) {
            window.setDimAmount(0.0f);
        }
    }

    public void setStart() {
        tip.setText("老师开启了集体发言\n" +
                "踊跃参与吧！");
        showDialog(false, true);
    }

    public void setSop() {
        tip.setText("老师结束了集体发言");
        showDialog(false, true);
    }

}

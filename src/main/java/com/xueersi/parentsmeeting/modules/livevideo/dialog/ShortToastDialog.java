package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by Zhang Yuansun on 2018/7/31.
 */

public class ShortToastDialog extends BaseAlertDialog {
    public TextView tips;
    public ShortToastDialog(Context context) {
        super(context, (Application) BaseApplication.getContext(), false);
    }
    @Override
    protected View initDialogLayout(int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_livevideo_short_toast, null);
        tips = (TextView)view.findViewById(R.id.tv_shorttoast_tip);
        return view;
    }

    public void setTips(String msg){
        tips.setText(msg);
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
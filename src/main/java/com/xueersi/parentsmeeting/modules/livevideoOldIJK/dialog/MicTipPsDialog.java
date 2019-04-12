package com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog;

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
 * Created by David on 2018/7/16.
 */

public class MicTipPsDialog extends BaseAlertDialog {
    public TextView tips;
    public MicTipPsDialog(Context context) {
        super(context, (Application) BaseApplication.getContext(), false);
    }
    @Override
    protected View initDialogLayout(int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_livevideo_psmictips, null);
        tips = (TextView)view.findViewById(R.id.tv_tip_detail);
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

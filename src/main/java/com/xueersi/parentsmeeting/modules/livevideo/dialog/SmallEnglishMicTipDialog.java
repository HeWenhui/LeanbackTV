package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.ui.dialog.BaseAlertDialog;
import com.xueersi.ui.dialog.BaseDialog;

public class SmallEnglishMicTipDialog extends BaseAlertDialog {
    private TextView tvArtsMic;
    public final static int WAIT = 0;
    public final static int GIVE_UP = 1;
    public final static int FAIL = 2;
    public final static int SUCCESS = 3;
    public int status = WAIT;

    public SmallEnglishMicTipDialog(Context context) {
        super(context, (Application) BaseApplication.getContext(), false);
    }


    @Override
    protected View initDialogLayout(int type) {

        View view = mInflater.inflate(R.layout.dialog_livevideo_arts_micdialog, null);
        tvArtsMic = view.findViewById(R.id.iv_livevideo_small_english_raise_hand);

//        LiveVideoConfig
//        LiveVideoPointN
        return view;
    }

    public void setText(String newText) {
        tvArtsMic.setText(newText);
    }

    //    @Override
//    public void showDialog() {
//        super.showDialog(false, false);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cancelDialog();
//            }
//        }, 3000);
//    }
    public boolean setFail(String text, int seconds) {
        setText(text);
        int oldStatus = status;
        status = FAIL;
        if (!isDialogShow()) {
            showDialogAutoClose(3000);
        }
        return oldStatus != status;
    }

    public boolean setSuccess(String text, int seconds) {
        setText(text);
        int oldStatus = status;
        status = SUCCESS;
        if (!isDialogShow()) {
            showDialogAutoClose(3000);
        }
        return oldStatus != status;
    }

    public void showDialogAutoClose(int seconds) {
        super.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelDialog();
            }
        }, seconds);

    }

}

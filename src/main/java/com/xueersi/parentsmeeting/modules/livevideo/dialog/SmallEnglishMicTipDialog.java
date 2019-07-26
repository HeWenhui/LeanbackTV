package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

public class SmallEnglishMicTipDialog extends BaseAlertDialog {
    private TextView tvArtsMic;
    public final static int WAIT = 0;
    public final static int GIVE_UP = 1;
    public final static int FAIL = 2;
    public final static int SUCCESS = 3;
    public int status = WAIT;

    public SmallEnglishMicTipDialog(Context context) {
        super(context, (Application) ContextManager.getContext(), false);
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

    public void setTypeface(Typeface typeface) {
        tvArtsMic.setTypeface(typeface);
    }

    public boolean setFail(String text, int seconds) {
        setText(text);
        int oldStatus = status;
        status = FAIL;
        if (!isDialogShow()) {
            showDialogAutoClose(seconds);
        }
        return oldStatus != status;
    }

    public boolean setSuccess(String text, int seconds) {
        setText(text);
        int oldStatus = status;
        status = SUCCESS;
        if (!isDialogShow()) {
            showDialogAutoClose(seconds);
        }
        return oldStatus != status;
    }

    //过一段时间自动消失,左半屏居中
    public void showDialogAutoClose(int seconds) {
//        Window window = mAlertDialog.getWindow();
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
////        WindowManager.LayoutParams lp = window.getAttributes();
////        lp.alpha = 0.8f;
//
//        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
//        int width = (liveVideoPoint.x3 - liveVideoPoint.x2 - lp.width) / 2;
//        lp.x = width;
//
//        lp.y = SizeUtils.Dp2Px(mContext, 139);
//
//        window.setAttributes(lp);
        super.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelDialog();
            }
        }, seconds);

    }

//    public void showDialog() {
//
//    }

}

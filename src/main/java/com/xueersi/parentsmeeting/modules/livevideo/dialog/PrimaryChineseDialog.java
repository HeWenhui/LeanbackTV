package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by Zhang Yuansun on 2019/1/29.
 */

public class PrimaryChineseDialog extends BaseAlertDialog {
    public ImageView tvCancel;
    public ImageView tvConfirm;

    public PrimaryChineseDialog(Context context) {
        super(context, (Application) BaseApplication.getContext(), false);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_livevideo_primary_chinese, null);
        tvCancel = view.findViewById(R.id.iv_livevideo_speechbul_close_cancel);
        tvConfirm = view.findViewById(R.id.iv_livevideo_speechbul_close_confim);
        return view;
    }

    public void setOnClickCancelListener(View.OnClickListener listener){
        tvCancel.setOnClickListener(listener);
    }

    public void setOnClickConfirmlListener(View.OnClickListener listener){
        tvConfirm.setOnClickListener(listener);
    }

    public void showDialog() {
        super.showDialog(false, false);
    }
}

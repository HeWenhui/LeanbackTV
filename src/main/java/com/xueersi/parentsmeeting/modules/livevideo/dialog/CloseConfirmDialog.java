package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by Zhang Yuansun on 2018/8/1.
 */

public class CloseConfirmDialog extends BaseAlertDialog {
    private ImageView tvCancel;
    private ImageView tvConfirm;
    private TextView tvTitle;
    private TextView tvContent;

    public CloseConfirmDialog(Context context) {
        super(context, (Application) ContextManager.getContext(), false);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_livevideo_speechbul_close, null);
        tvCancel = view.findViewById(R.id.iv_livevideo_speechbul_close_cancel);
        tvConfirm = view.findViewById(R.id.iv_livevideo_speechbul_close_confim);
        tvTitle = view.findViewById(R.id.tv_livevideo_speechbul_close_title);
        tvContent = view.findViewById(R.id.tv_livevideo_speechbul_close_content);
        return view;
    }

    public void setOnClickCancelListener(View.OnClickListener listener){
        tvCancel.setOnClickListener(listener);
    }

    public void setOnClickConfirmlListener(View.OnClickListener listener){
        tvConfirm.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTitleGravaty(int gravaty) {
        tvTitle.setGravity(gravaty);
    }

    public void hideContent() {
        tvContent.setVisibility(View.GONE);
    }

    public void showDialog() {
        super.showDialog(false, false);
    }
}

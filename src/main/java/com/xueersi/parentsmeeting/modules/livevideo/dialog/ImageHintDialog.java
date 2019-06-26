package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

public class ImageHintDialog extends BaseAlertDialog {

    private ImageView ivHint;
    private TextView tvHint;
    public ImageHintDialog(Context context, Application application, boolean isSystem){
        super(context, application,isSystem);
    }
    @Override
    protected View initDialogLayout(int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_imagehint_view, null);
        ivHint = view.findViewById(R.id.iv_dialog_image_hint);
        tvHint = view.findViewById(R.id.tv_dialog_image_hint);
        return view;
    }

    public ImageHintDialog setImageResource(int id){
        ivHint.setImageResource(id);
        return this;
    }
    public ImageHintDialog setText(String text){
        tvHint.setText(text);
        return this;
    }
}

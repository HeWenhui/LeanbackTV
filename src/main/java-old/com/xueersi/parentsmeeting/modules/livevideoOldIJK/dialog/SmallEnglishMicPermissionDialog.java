package com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by：WangDe on 2018/10/9 14:12
 */
public class SmallEnglishMicPermissionDialog extends BaseAlertDialog {

    private FangZhengCuYuanTextView tvTitle;
    private FangZhengCuYuanTextView tvContent;
    private ImageView imCancel;
    private ImageView imConfirm;

    public SmallEnglishMicPermissionDialog(Context context) {
        super(context, (Application) BaseApplication.getContext(), false);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_small_english_permission,null);
        tvTitle = view.findViewById(R.id.tv_dialog_small_english_title);
        tvContent = view.findViewById(R.id.tv_dialog_small_english_content);
        imCancel = view.findViewById(R.id.iv_dialog_small_english_cancel);
        imConfirm = view.findViewById(R.id.iv_dialog_small_english_confirm);
        return view;
    }

    public void setTitleText(String text){
        tvTitle.setText(text);
    }

    public void setContentText(String text){
        tvContent.setText(text);
    }

    public void showDialog() {
        super.showDialog(false, false);
    }

    public void setOnClickCancelListener(View.OnClickListener listener){
        imCancel.setOnClickListener(listener);
    }

    public void setOnClickConfirmlListener(View.OnClickListener listener){
        imConfirm.setOnClickListener(listener);
    }


}

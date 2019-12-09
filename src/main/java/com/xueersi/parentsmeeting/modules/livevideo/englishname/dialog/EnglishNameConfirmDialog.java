package com.xueersi.parentsmeeting.modules.livevideo.englishname.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.utils.EnglishNameListener;
import com.xueersi.ui.dialog.BaseAlertDialog;

public class EnglishNameConfirmDialog extends BaseAlertDialog {
    /**
     * 错误信息
     */
    private TextView tvMsg;
    /** 确认 */
    private TextView tvSubmit;

    /** 取消 */
    private TextView tvCancel;
    EnglishNameListener englishNameListener;

    public EnglishNameConfirmDialog(Context context, BaseApplication application, boolean isSystem) {
        super(context, application, isSystem);
    }

    @Override
    protected void createDialog(View alertView, boolean isSystem) {
        mDialogWidth = 0.8f;
        super.createDialog(alertView, isSystem);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = mInflater.inflate(R.layout.dialog_english_name_confirm, null);
        tvMsg =  view.findViewById(R.id.tv_setting_english_name_dialog_tip);
        tvSubmit =  view.findViewById(R.id.tv_dialog_setting_english_name_sumit_data);
        tvCancel=  view.findViewById(R.id.tv_dialog_setting_english_name_cancel);
        initLisenter();
        return view;
    }

    public void initData(String msg, EnglishNameListener englishNameListener){
        tvMsg.setText("不选择名字会默认设置名字为"+msg+"哦~");
        this.englishNameListener = englishNameListener;
    }

    private void initLisenter() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(englishNameListener!=null) {
                    englishNameListener.dialogCancel();
                }
            }
        });
    }

    private void close(){
        cancelDialog();
    }

}

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
        String contentText = "不选择名字可能错失点名机会哦~ 您可以在个人中心重新选择英文名。";
        if(mContext != null){
            contentText = mContext.getResources().getString(R.string.english_group_name_skip);
        }
        tvMsg.setText(contentText);
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

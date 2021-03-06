package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by linyuqiang on 2018/1/1.
 * 投票锁屏对话框
 */
public class VoteWaitDialog extends BaseAlertDialog {
    public VoteWaitDialog(Context context, Application application, boolean isSystem) {
        super(context, application, isSystem);
    }

    @Override
    protected View initDialogLayout(int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_live_vote_wait, null);
        return view;
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.view.alertdialog.BaseAlertDialog;

/**
 * Created by lyqai on 2018/1/1.
 */

public class VoteDialog extends BaseAlertDialog {
    public VoteDialog(Context context, BaseApplication application, boolean isSystem) {
        super(context, application, isSystem);
    }

    @Override
    protected View initDialogLayout(int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_live_vote_wait, null);
        return view;
    }

}

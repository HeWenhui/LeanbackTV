package com.xueersi.parentsmeeting.modules.livevideoOldIJK.switchflow;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.ui.dialog.BaseAlertDialog;

public class SwitchRouteSuccessDialog extends BaseAlertDialog {

    private SwitchFlowRouteSuccessView mView;

    public SwitchRouteSuccessDialog(Context mContext) {
        super(mContext, (Application) BaseApplication.getContext(), false);
    }

    @Override
    protected View initDialogLayout(int type) {
        mView = new SwitchFlowRouteSuccessView(mContext, true);
        return mView.getRootView();
    }

    public void updateView(int pos) {
        mView.updateView(pos);
    }

    //过一段时间自动消失,左半屏居中
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

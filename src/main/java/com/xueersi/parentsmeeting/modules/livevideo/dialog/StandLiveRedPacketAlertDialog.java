package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.content.Context;
import android.view.View;
import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.entity.MyUserInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.RedPackagePage;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.view.alertdialog.BaseAlertDialog;

/**
 * 视频观看红包弹窗
 * Created by ZouHao on 2016/4/19.
 */
public class StandLiveRedPacketAlertDialog extends BaseAlertDialog {
    RedPackagePage redPackagePage;

    private RedPackagePage.RedPackagePageAction redPackageAction;

    public StandLiveRedPacketAlertDialog(Context context, BaseApplication application, boolean isSystem) {
        super(context, application, isSystem);
    }

    @Override
    protected View initDialogLayout(int type) {
        MyUserInfoEntity mMyInfo = UserBll.getInstance().getMyUserInfoEntity();
        redPackagePage = new RedPackagePage(mContext, 0, new RedPackagePage.RedPackagePageAction() {
            @Override
            public void onPackageClick(int operateId) {
                mAlertDialog.cancel();
                redPackageAction.onPackageClick(operateId);
            }

            @Override
            public void onPackageClose(int operateId) {
                mAlertDialog.cancel();
                redPackageAction.onPackageClose(operateId);
            }
        }, "", mMyInfo.getHeadImg());
        View view = redPackagePage.getRootView();
        return view;
    }

    /**
     * 设置红包确认监听
     *
     * @param redPackageAction
     * @return
     */
    public StandLiveRedPacketAlertDialog setRedPacketConfirmListener(RedPackagePage.RedPackagePageAction redPackageAction) {
        this.redPackageAction = redPackageAction;
        return this;
    }

    @Override
    public void showDialog() {
        redPackagePage.initEnter();
        super.showDialog();
    }
}

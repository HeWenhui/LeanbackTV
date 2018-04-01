package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.RedPackagePage;
import com.xueersi.xesalib.view.alertdialog.BaseAlertDialog;

/**
 * 视频观看红包弹窗
 * Created by ZouHao on 2016/4/19.
 */
public class StandLiveRedPacketAlertDialog extends BaseAlertDialog {
    Button btnRedPacket;
    Button btnRedClose;

    public StandLiveRedPacketAlertDialog(Context context, BaseApplication application, boolean isSystem) {
        super(context, application, isSystem);
    }

    private View.OnClickListener mClickListener;

    @Override
    protected View initDialogLayout(int type) {
        RedPackagePage redPackagePage = new RedPackagePage(mContext, 0, new RedPackagePage.RedPackagePageAction() {
            @Override
            public void onPackageClick(int operateId) {
                mAlertDialog.cancel();
                if (mClickListener != null) {
                    mClickListener.onClick(btnRedPacket);
                }
            }

            @Override
            public void onPackageClose(int operateId) {
                mAlertDialog.cancel();
                if (mClickListener != null) {
                    mClickListener.onClick(btnRedClose);
                }
            }
        }, "");
        View view = redPackagePage.getRootView();
        btnRedPacket = view.findViewById(R.id.bt_livevideo_redpackage_cofirm);
        btnRedClose = view.findViewById(R.id.iv_livevideo_redpackage_close);
        return view;
    }

    /**
     * 设置红包确认监听
     *
     * @param listener
     * @return
     */
    public StandLiveRedPacketAlertDialog setRedPacketConfirmListener(View.OnClickListener listener) {
        this.mClickListener = listener;
        return this;
    }

}

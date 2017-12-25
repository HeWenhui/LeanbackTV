package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.xesalib.view.alertdialog.BaseAlertDialog;

/**
 * 视频观看红包弹窗
 * Created by ZouHao on 2016/4/19.
 */
public class RedPacketAlertDialog extends BaseAlertDialog {

    private Button btnRedPacket;

    public RedPacketAlertDialog(Context context, BaseApplication application, boolean isSystem) {
        super(context, application, isSystem);
    }

    private View.OnClickListener mClickListener;

    @Override
    protected View initDialogLayout(int type) {
        View view = mInflater.inflate(R.layout.dialog_red_packet_view, null);
        btnRedPacket = (Button) view.findViewById(R.id.bt_livevideo_redpackage_cofirm);
        btnRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.cancel();
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.cancel();
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
        return view;
    }

    /**
     * 设置红包确认监听
     *
     * @param listener
     * @return
     */
    public RedPacketAlertDialog setRedPacketConfirmListener(View.OnClickListener listener) {
        this.mClickListener = listener;
        return this;
    }

}
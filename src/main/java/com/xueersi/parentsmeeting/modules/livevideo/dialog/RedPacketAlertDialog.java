package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * 视频观看红包弹窗
 * Created by ZouHao on 2016/4/19.
 */
public class RedPacketAlertDialog extends BaseAlertDialog {

    private Button btnRedPacket;
    private View mView;

    public RedPacketAlertDialog(Context context, Application application, boolean isSystem) {
        super(context, application, isSystem);
    }

    private View.OnClickListener mClickListener;

    @Override
    protected View initDialogLayout(int type) {
        View view = mInflater.inflate(R.layout.dialog_red_packet_view, null);
        ImageView imageView = view.findViewById(R.id.iv_livevideo_redpackage_monkey);
        try {
//            Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_redpackage_monkey);
            imageView.setImageResource(R.drawable.bg_livevideo_redpackage_monkey);
        } catch (Exception e) {
        }
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

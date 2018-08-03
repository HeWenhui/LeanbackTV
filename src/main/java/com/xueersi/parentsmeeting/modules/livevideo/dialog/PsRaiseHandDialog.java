package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.view.alertdialog.BaseAlertDialog;

/**
 * Created by David on 2018/7/21.
 */

public class PsRaiseHandDialog extends BaseAlertDialog {
    private RaiseHandGiveup raiseHandGiveup;
    public PsRaiseHandDialog(Context context, BaseApplication application) {
        super(context, application, false);
    }
    private TextView tip;
    @Override
    protected View initDialogLayout(int i) {
        View view = mInflater.inflate(R.layout.dialog_livevideo_psraisehand, null);
        tip = (TextView) view.findViewById(R.id.tv_tip_detail);
        return view;
    }
    public void setRaiseHandsCount(int count) {
        tip.setText("当前举手人数:" + count + "人");
    }

    public void setSuccess(){
        tip.setText("恭喜你！\n 你已经被老师选中，\n请准备一下等待接麦吧");
    }

    public void showDialog() {
        super.showDialog(false, false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelDialog();
            }
        }, 3000);
    }

    public void setRaiseHandGiveup(RaiseHandGiveup raiseHandGiveup) {
        this.raiseHandGiveup = raiseHandGiveup;
    }

    public interface RaiseHandGiveup {
        void onGiveup();
    }
}

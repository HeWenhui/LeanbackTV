package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by David on 2018/7/21.
 */

public class PsRaiseHandDialog extends BaseAlertDialog {
    private RaiseHandGiveup raiseHandGiveup;
    public final static int WAIT = 0;
    public final static int GIVE_UP = 1;
    public final static int FAIL = 2;
    public final static int SUCCESS = 3;
    int count;
    public int status = WAIT;
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

    public void setDefault(int count){
        tip.setText("已举手，现在有" + count + "位\n小朋友在排队哦");
    }
    public void setRaiseHandsCount(int count) {
//        if(status == WAIT){
//            if(this.count != count){
//                this.count = count;
//                tip.setText("当前举手人数:" + count + "人");
//            }
//        }
        tip.setText("当前举手人数:" + count + "人");

    }

    public void setSuccess(){
        status = SUCCESS;
        tip.setText("恭喜你！\n 你已经被老师选中，\n请准备一下等待接麦吧");
    }

    public void setFail(){
        status = FAIL;
        tip.setText("本次没有被选中哦，\n下次还有机会");
    }

    public void showDialog() {
        super.showDialog(false, false);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cancelDialog();
//            }
//        }, 3000);
    }

    public void setRaiseHandGiveup(RaiseHandGiveup raiseHandGiveup) {
        this.raiseHandGiveup = raiseHandGiveup;
    }

    public interface RaiseHandGiveup {
        void onGiveup();
    }

    public void showDefaultDialog(){
        super.showDialog(false, false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelDialog();
            }
        }, 3000);
    }
}

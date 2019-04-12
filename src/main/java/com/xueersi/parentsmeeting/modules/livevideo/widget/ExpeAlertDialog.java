package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

/**
 * Created by linyuqiang on 2017/7/18.
 */
public class ExpeAlertDialog extends BaseAlertDialog {
    String buyCourseUrl;
    TextView tv_livevideo_buycourse_info;
    Activity activity;

    public ExpeAlertDialog(Context context, BaseApplication application, boolean isSystem, final String buyCourseUrl) {
        super(context, application, isSystem);
        activity = (Activity) context;
        this.buyCourseUrl = buyCourseUrl;
        mAlertDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = mInflater.inflate(R.layout.dialog_live_expe_view, null);
        tv_livevideo_buycourse_info = (TextView) view.findViewById(R.id.tv_livevideo_buycourse_info);
        Button btnRedPacket = (Button) view.findViewById(R.id.bt_livevideo_buycourse_cofirm);
        if (StringUtils.isEmpty(buyCourseUrl)) {
            btnRedPacket.setText("继续选课");
        } else {
            btnRedPacket.setText("立即报名");
        }
        btnRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
                BrowserActivity.openBrowser(activity, buyCourseUrl);
            }
        });
        return view;
    }

    public ExpeAlertDialog initInfo(String s) {
        tv_livevideo_buycourse_info.setText(s);
        return this;
    }
}

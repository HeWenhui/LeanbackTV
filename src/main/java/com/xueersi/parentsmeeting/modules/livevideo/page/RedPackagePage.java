package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by lyqai on 2018/3/21.
 */

public class RedPackagePage extends BasePager {
    Context context;
    int operateId;
    RedPackagePageAction redPackageAction;

    public RedPackagePage(Context context, int operateId, RedPackagePageAction redPackageAction) {
        super(context);
        this.operateId = operateId;
        this.redPackageAction = redPackageAction;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.dialog_red_packet_view, null);
        return mView;
    }

    @Override
    public void initData() {
        Button btnRedPacket = mView.findViewById(R.id.bt_livevideo_redpackage_cofirm);
        btnRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redPackageAction.onPackageClick(operateId);
            }
        });
        mView.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redPackageAction.onPackageClose(operateId);
            }
        });
    }

    public interface RedPackagePageAction {
        void onPackageClick(int operateId);

        void onPackageClose(int operateId);
    }
}

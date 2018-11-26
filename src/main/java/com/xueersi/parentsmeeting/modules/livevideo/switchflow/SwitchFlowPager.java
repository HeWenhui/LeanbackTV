package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SwitchFlowPager extends BasePager {

    public SwitchFlowPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_switch_flow, null);
        view.findViewById(R.id.tv_livevideo_switch_flow_route1);
        view.findViewById(R.id.tv_livevideo_switch_flow_route2);
        view.findViewById(R.id.tv_livevideo_switch_flow_route3);
        view.findViewById(R.id.tv_livevideo_switch_flow_route4);

        return view;
    }

    @Override
    public void initData() {

    }
}

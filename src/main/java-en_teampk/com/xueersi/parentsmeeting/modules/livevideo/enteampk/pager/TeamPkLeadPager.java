package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

public class TeamPkLeadPager extends LiveBasePager {

    public TeamPkLeadPager(Context context) {
        super(context);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view= LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_lead,null);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}

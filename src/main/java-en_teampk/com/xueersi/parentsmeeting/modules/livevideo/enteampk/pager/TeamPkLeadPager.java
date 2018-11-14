package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_lead, null);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup group = (ViewGroup) mView;
                final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_win, group, false);
                group.addView(view);
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup group = (ViewGroup) mView;
                        group.removeView(view);
                    }
                }, 2500);
            }
        }, 2000);
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}

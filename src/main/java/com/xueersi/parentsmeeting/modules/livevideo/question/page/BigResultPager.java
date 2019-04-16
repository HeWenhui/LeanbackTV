package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

public class BigResultPager extends LiveBasePager {
    public BigResultPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_bigques_result, null);
        return mView;
    }

    @Override
    public void initData() {
        super.initData();
    }
}

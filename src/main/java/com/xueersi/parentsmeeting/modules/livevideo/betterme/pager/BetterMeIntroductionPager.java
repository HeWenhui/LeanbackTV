package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 英语小目标 小目标介绍
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeIntroductionPager extends BasePager {
    public BetterMeIntroductionPager(Context context) {
        super(context);
    }

    public BetterMeIntroductionPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_introduction, null);

        return view;
    }

    @Override
    public void initData() {

    }
}

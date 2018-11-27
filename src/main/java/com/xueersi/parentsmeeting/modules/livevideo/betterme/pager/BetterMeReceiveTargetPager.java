package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 英语小目标 本场小目标
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeReceiveTargetPager extends BasePager {
    public BetterMeReceiveTargetPager(Context context) {
        super(context);
    }

    public BetterMeReceiveTargetPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_receive_target, null);
        return view;
    }

    @Override
    public void initData() {

    }
}

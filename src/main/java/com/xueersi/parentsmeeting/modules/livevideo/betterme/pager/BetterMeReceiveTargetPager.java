package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.OnPagerClose;

/**
 * 英语小目标 本场小目标
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeReceiveTargetPager extends BasePager {

    private ImageView ivReceivetarReady;
    private OnPagerClose mOnpagerClose;

    public BetterMeReceiveTargetPager(Context context) {
        super(context);
    }

    public BetterMeReceiveTargetPager(Context context, OnPagerClose onPagerClose) {
        super(context);
        this.mOnpagerClose = onPagerClose;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_receive_target, null);
        ivReceivetarReady = view.findViewById(R.id.iv_livevideo_betterme_receivetarget_ready);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        ivReceivetarReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnpagerClose.onClose(BetterMeReceiveTargetPager.this);
            }
        });
    }
}

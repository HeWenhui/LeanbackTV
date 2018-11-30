package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 英语小目标 小目标介绍
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeIntroductionPager extends BasePager {
    /**
     * 查看段位
     */
    private TextView tvViewLevel;
    /**
     * 知道啦
     */
    private Button btnGotit;

    public BetterMeIntroductionPager(Context context) {
        super(context);
    }

    public BetterMeIntroductionPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_introduction, null);
        tvViewLevel = view.findViewById(R.id.tv_livevideo_betterme_introduction_viewlevel);
        btnGotit = view.findViewById(R.id.btn_livevideo_better_introduction_gotit);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        tvViewLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnGotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

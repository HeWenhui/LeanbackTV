package com.xueersi.parentsmeeting.modules.livevideo.rollcall.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SmallChineseClassSignPager extends BasePager {

    private ImageView ivSignStatus;

    private ImageView ivClose;

    public SmallChineseClassSignPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_chinese_sign, null);
        ivSignStatus = view.findViewById(R.id.bg_livevideo_small_chinses_sign_status);
        ivClose = view.findViewById(R.id.iv_livevideo_small_chinese_sign_close);


        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

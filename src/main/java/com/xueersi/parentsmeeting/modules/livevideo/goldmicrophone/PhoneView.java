package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class PhoneView extends BasePager implements GoldPhoneContract.GoldPhoneView {

    private GoldPhoneContract.GoldPhonePresenter mPresenter;

    private ImageView ivClose;

    public PhoneView(Context context, GoldPhoneContract.GoldPhonePresenter presenter) {
        super(context, false);
        this.mPresenter = presenter;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chinese_gold_microphone, null);
        ivClose = view.findViewById(R.id.iv_livevideo_gold_microphone_cancel);
        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void initData() {

    }
}

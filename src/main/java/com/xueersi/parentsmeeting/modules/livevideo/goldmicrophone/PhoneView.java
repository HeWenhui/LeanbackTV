package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class PhoneView extends BasePager implements GoldPhoneContract.GoldPhoneView {


    public PhoneView(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chinese_gold_microphone, null);

        return view;
    }

    @Override
    public void initData() {

    }
}

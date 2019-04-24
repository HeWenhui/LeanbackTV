package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class MicroPhoneCloseTipPager extends BasePager implements GoldPhoneContract.CloseTipView {
    private ImageView ivCloseBtnYes, ivCloseBtnNo;

    private GoldPhoneContract.CloseTipPresenter presenter;

    public MicroPhoneCloseTipPager(Context context, GoldPhoneContract.CloseTipPresenter presenter) {
        super(context);
        this.presenter = presenter;
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_kindergarten_primaryschool_close_microphone, null);
        ivCloseBtnNo = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_no);
        ivCloseBtnYes = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_yes);
        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivCloseBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickNo();
            }
        });
        ivCloseBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickYes();
            }
        });
    }

    @Override
    public void initData() {

    }

    private void clickYes() {
        presenter.removeCloseTipView(mView);
        presenter.removeGoldView();
    }

    private void clickNo() {
        presenter.removeCloseTipView(mView);
    }
}

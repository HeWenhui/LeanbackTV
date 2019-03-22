package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class MicroPhoneCloseTipView extends BasePager implements GoldPhoneContract.CloseTipView {
    private ImageView ivCloseBtnYes, ivCloseBtnNo;

    private ImageView iv;

    private GoldPhoneContract.CloseTipPresenter presenter;

    public MicroPhoneCloseTipView(Context context, GoldPhoneContract.CloseTipPresenter presenter) {
        super(context);
        this.presenter = presenter;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_kindergarten_primaryschool_close_microphone, null);
        ivCloseBtnNo = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_no);
        ivCloseBtnYes = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_yes);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void clickYes() {
        presenter.removeGoldView();
    }

    @Override
    public void clickNo() {
        presenter.removeCloseView();
    }
}

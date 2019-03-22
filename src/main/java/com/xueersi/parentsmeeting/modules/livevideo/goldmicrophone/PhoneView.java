package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class PhoneView extends BasePager implements GoldPhoneContract.GoldPhoneView, GoldPhoneContract.CloseTipPresenter {

    private GoldPhoneContract.GoldPhonePresenter mPresenter;

    //    private GoldPhoneContract.CloseTipPresenter tipPresenter;
    private ImageView ivClose;
    private TextView tvTipWindow;

    private ImageView ivSetting;

    private Group settingGroup;
    private GoldPhoneContract.CloseTipView tipView;

    public PhoneView(Context context, PhoneBll presenter) {
        super(context, false);
        this.mPresenter = presenter;
        initListener();
//        this.tipPresenter = presenter;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chinese_gold_microphone, null);
        ivClose = view.findViewById(R.id.iv_livevideo_gold_microphone_cancel);
        tvTipWindow = view.findViewById(R.id.tv_gold_microphone_teacher_tip);
        ivSetting = view.findViewById(R.id.iv_livevideo_gold_microphone_setting);
        settingGroup = view.findViewById(R.id.iv_livevideo_gold_microphone_setting_group);
        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipView == null) {
                    tipView = new MicroPhoneCloseTipView(mContext, PhoneView.this);
                    ((ViewGroup) mView).addView(tipView.getRootView());
                }
            }
        });
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void removeCloseView(View view) {
        if (view.getParent() == mView) {
            ((ViewGroup) mView).removeView(view);
        }
    }

    @Override
    public void removeGoldView() {
        mPresenter.remove(mView);
    }

    @Override
    public void showCloseView() {
        tvTipWindow.setText("老师关闭了金话筒");
    }

    @Override
    public void showSettingView(boolean isVisible) {
        settingGroup.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}

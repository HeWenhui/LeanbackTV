package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SuperSpeakerRedPackagePager extends BasePager implements ISuperSpeakerContract.IRedPackageView {

    private TextView tvMoney;

    private ImageView ivCloseBtn;

    public SuperSpeakerRedPackagePager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_red_package, null);
        tvMoney = view.findViewById(R.id.fzcy_livevideo_record_video_redpackage_money);
        ivCloseBtn = view.findViewById(R.id.iv_livevideo_super_speaker_video_tip);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        ivCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void updateNum(String num) {
        tvMoney.setText(String.valueOf(num));
    }

    @Override
    public View getView() {
        return getRootView();
    }
}

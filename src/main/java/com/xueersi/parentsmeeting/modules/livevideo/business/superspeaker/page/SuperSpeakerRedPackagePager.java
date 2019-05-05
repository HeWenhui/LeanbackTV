package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;

public class SuperSpeakerRedPackagePager extends BasePager implements ISuperSpeakerContract.IRedPackageView {

    private TextView tvMoney;

    private ImageView ivCloseBtn;

    private ISuperSpeakerContract.IRedPackagePresenter packagePresenter;

    public SuperSpeakerRedPackagePager(Context context, ISuperSpeakerContract.IRedPackagePresenter presenter) {
        super(context);
        this.packagePresenter = presenter;
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
                if (mView != null) {
                    mView.removeCallbacks(closeRunnable);
                }
                if (packagePresenter != null) {
                    packagePresenter.removeView(mView);
                }
            }
        });
    }

    private Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            if (packagePresenter != null && mView != null) {
                packagePresenter.removeView(mView);
            }
        }
    };

    @Override
    public void updateNum(String num) {
        tvMoney.setText(String.valueOf(num));
        mView.postDelayed(closeRunnable, 5000);
//        Observable.timer(5, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                if (packagePresenter != null) {
//                    packagePresenter.removeView(mView);
//                }
//            }
//        });
    }

    @Override
    public View getView() {
        return getRootView();
    }
}

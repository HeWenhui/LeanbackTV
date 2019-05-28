package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;

public class SuperSpeakerCameraBackPager extends BasePager {

    private ImageView ivYes;
    private ImageView ivNo;
    private TextView tvContentTip;

    private TextView tvTittle;
//    private ISuperSpeakerContract.ICameraBackPresenter presenter;

    public SuperSpeakerCameraBackPager(Context context) {
        super(context);
//        this.presenter = presenter;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_camera_back_pop_window, null);
        tvTittle = view.findViewById(R.id.tv_livevideo_super_speaker_back_camera_tip);
        ivNo = view.findViewById(R.id.iv_livevideo_super_speaker_back_pop_window_cancel);
        ivYes = view.findViewById(R.id.iv_livevideo_super_speaker_back_pop_yes);
        tvContentTip = view.findViewById(R.id.tv_livevideo_super_speaker_back_camera_content_tip);
        initListener();
        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (presenter != null) {
//                    presenter.removeCameraView();
//                }
                if (iClickListener != null) {
                    iClickListener.onYesClick();
                }
            }
        });
        ivNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (presenter != null) {
//                    presenter.removeView(mView);
//                }
                if (iClickListener != null) {
                    iClickListener.onNoClick();
                }
            }
        });
    }

    public void setTextContentTip(String textTip) {
        tvContentTip.setText(textTip);
    }

    public void setTvTittle(String tittletip) {
        tvTittle.setText(tittletip);
    }

    @Override
    public void initData() {

    }

    IClickListener iClickListener;

    public void setiClickListener(IClickListener iClickListener) {
        this.iClickListener = iClickListener;
    }

    public interface IClickListener {

        void onNoClick();

        void onYesClick();
    }
}

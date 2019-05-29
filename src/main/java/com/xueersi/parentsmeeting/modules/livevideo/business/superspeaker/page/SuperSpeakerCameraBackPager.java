package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SuperSpeakerCameraBackPager extends BasePager {

    private ImageView ivYes;
    private ImageView ivNo;
    private TextView tvContentTip;

    private TextView tvTittle;
    //1直播,2回放
    private int livevideo;
//    private ISuperSpeakerContract.ICameraBackPresenter presenter;

    public SuperSpeakerCameraBackPager(Context context, int livevieo) {
        super(context);
        this.livevideo = livevieo;
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
                //预览页面退出
                if (mContext.getString(R.string.super_speaker_back_camera_content_tip).equals(tvContentTip)) {
                    if (livevideo == 1) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715011));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716011));
                    }
                } else {//录制前
                    if (livevideo == 1) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715009));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716009));
                        ;
                    }
                }
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
                //预览页面退出
                if (mContext.getString(R.string.super_speaker_back_camera_content_tip).equals(tvContentTip)) {
                    if (livevideo == 1) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715010));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716010));
                    }
                } else {
                    if (livevideo == 1) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715008));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716008));
                    }
                }
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

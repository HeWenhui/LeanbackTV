package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

public class SuperSpeakerCommonTipPager extends BasePager implements ISuperSpeakerContract.ICommonTip {

    private ISuperSpeakerContract.ICommonPresenter iCommonPresenter;
    //tv_livevideo_super_speaker_time_up_title 时间到，视频没有完成哦
    private TextView tvTimeUpTittle;

    private Group groupSubmitVideo, groupCancelVideo;
    /** 点击取消按钮 */
    private ImageView ivYesBtn;
    /** 提交按钮 */
    private ImageView ivYesSubmitBtn;
    /** 不提交按钮 */
    private ImageView ivNoSubmitBtn;
    /** 倒计时 */
    private TextView tvTimeCountDown;

    private FangZhengCuYuanTextView tvTimeUpContent;

    private FangZhengCuYuanTextView tvTimeUpNotFinishTitle;

    public SuperSpeakerCommonTipPager(Context context, ISuperSpeakerContract.ICommonPresenter iCommonPresenter) {
        super(context);
        this.iCommonPresenter = iCommonPresenter;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_record_video_tip, null);
        tvTimeUpNotFinishTitle = view.findViewById(R.id.tv_livevideo_super_speaker_time_up_title);
        tvTimeUpTittle = view.findViewById(R.id.tv_livevideo_super_speaker_tittle_tip);
        tvTimeUpContent = view.findViewById(R.id.tv_livevideo_super_speaker_content_tip);
        ivYesBtn = view.findViewById(R.id.iv_livevideo_super_speaker_yes_btn);
        ivYesSubmitBtn = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_yes);
        ivNoSubmitBtn = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_no);
        tvTimeCountDown = view.findViewById(R.id.tv_livevideo_super_speaker_countdown_second);

        groupSubmitVideo = view.findViewById(R.id.group_livevideo_super_speaker_submit_video);
        groupCancelVideo = view.findViewById(R.id.group_livevideo_super_speaker_cancel_video);
        initListener();
        return view;
    }

    @Override
    public void initData() {

    }

    /**
     *
     */
    public void initListener() {
        ivYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommonPresenter != null) {
                    mView.removeCallbacks(timeDownRunnable);
                    iCommonPresenter.removeCameraView();
                }
            }
        });
        ivYesSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommonPresenter != null) {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715015));
                    iCommonPresenter.submitSpeechShow("1");
                    mView.removeCallbacks(timeDownRunnable);
                }
            }
        });
        ivNoSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommonPresenter != null) {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715014));
                    mView.removeCallbacks(timeDownRunnable);
                    iCommonPresenter.removeCameraView();
                }
            }
        });


    }

    /** 视频录制的时间 */
//    private long recordTime;
    @Override
    public void timeUp(boolean notFinish) {
        //如果录制时间小于1s
        this.notFinish = notFinish;
        if (notFinish) {
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715017));
            groupSubmitVideo.setVisibility(View.GONE);
            groupCancelVideo.setVisibility(View.VISIBLE);
            tvTimeUpNotFinishTitle.setText(mContext.getString(R.string.super_speaker_time_up_not_record_finish_tip));
            timeDownSchduler();
        } else {
            groupSubmitVideo.setVisibility(View.VISIBLE);
            groupCancelVideo.setVisibility(View.GONE);
            tvTimeUpTittle.setText(mContext.getString(R.string.super_speaker_time_up_record_finish_tip_title));
            tvTimeUpContent.setText(mContext.getString(R.string.super_speaker_time_up_record_finish_tip_content));
            timeDownSchduler();
        }
    }

    protected boolean notFinish = false;

    @Override
    public View getView() {
        return getRootView();
    }

    private int timeDown = 5;
    /** 秒数间隔 */
    private final static int TIME_INTERVAL_SECOND = 1;
    private Runnable timeDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (tvTimeCountDown != null) {
                if (timeDown == 0 && iCommonPresenter != null) {
                    if (!notFinish) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715016));
                        iCommonPresenter.submitSpeechShow("1");
                    }
                    iCommonPresenter.removeCameraView();
                    return;
                }
                logger.i(timeDown + "s后自动返回直播间");
                tvTimeCountDown.setText("" + (--timeDown) + "s后自动返回直播间");
                mView.postDelayed(this, TIME_INTERVAL_SECOND * 1000);
            }
        }
    };

    /**
     * 启动时间倒计时器
     */
    private void timeDownSchduler() {
        if (tvTimeCountDown == null) {
            return;
        }
        timeDown = 5;
        if (tvTimeCountDown.getVisibility() != View.VISIBLE) {
            tvTimeCountDown.setVisibility(View.VISIBLE);
        }
        tvTimeCountDown.setText("" + timeDown + "s后自动返回直播间");
        tvTimeCountDown.postDelayed(timeDownRunnable, TIME_INTERVAL_SECOND * 1000);
    }
}

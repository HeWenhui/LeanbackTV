package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.RECORD_TIME;

public class SuperSpeakerCommonTipPager extends BasePager implements ISuperSpeakerContract.ICommonTip {

    private ISuperSpeakerContract.ICommonPresenter iCommonPresenter;
    //tv_livevideo_super_speaker_time_up_title 时间到，视频没有完成哦
    private TextView tvTimeUpTittle;

    private Group groupSubmitVideo, groupCancelVideo;

    private ImageView ivYesBtn, ivSubmitBtn, ivNoSubmitBtn;


    public SuperSpeakerCommonTipPager(Context context, ISuperSpeakerContract.ICommonPresenter iCommonPresenter) {
        super(context);
        this.iCommonPresenter = iCommonPresenter;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_record_video_tip, null);
        tvTimeUpTittle = view.findViewById(R.id.tv_livevideo_super_speaker_time_up_title);
        ivYesBtn = view.findViewById(R.id.iv_livevideo_super_speaker_yes_btn);
        ivSubmitBtn = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_yes);
        ivNoSubmitBtn = view.findViewById(R.id.iv_livevideo_gold_microphone_close_btn_no);

        groupSubmitVideo = view.findViewById(R.id.group_livevideo_super_speaker_submit_video);
        groupCancelVideo = view.findViewById(R.id.group_livevideo_super_speaker_cancel_video);
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
                    iCommonPresenter.removeView(mView);
                }
            }
        });
        ivSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommonPresenter != null) {
                    iCommonPresenter.submitSpeechShow("2");
                }
            }
        });
        ivNoSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommonPresenter != null) {
                    iCommonPresenter.removeView(mView);
                }
            }
        });

    }

    /** 视频录制的时间 */
    private long recordTime;

    @Override
    public void timeUp() {
        //如果录制时间小于1s
        if (recordTime < RECORD_TIME) {
            groupSubmitVideo.setVisibility(View.GONE);
            groupCancelVideo.setVisibility(View.VISIBLE);
            tvTimeUpTittle.setText("时间到，视频没有完成哦");
        } else {
            groupSubmitVideo.setVisibility(View.VISIBLE);
            groupCancelVideo.setVisibility(View.GONE);

        }


    }
}

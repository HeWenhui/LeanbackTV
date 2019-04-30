package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class CustomVideoController extends BasePager {
    private TextView tvTotalTime, tvCurrentTime;
    private String totalTime, currentTime;

    private ImageView ivProgressBar;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_custom_videoview, null);
        tvCurrentTime = view.findViewById(R.id.tv_livevideo_super_speaker_video_bottom_time);
        tvTotalTime = view.findViewById(R.id.tv_livevideo_super_speaker_video_bottom_total_time);
        ivProgressBar = view.findViewById(R.id.iv_livevideo_super_speaker_video_controller_bottom_progress_bar);
        view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {

            }
        });
        return view;
    }

    /**
     * 设置总体时间
     *
     * @param totalTime
     */
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
        tvTotalTime.setText(totalTime);
    }

    /**
     * 设置当前时间
     *
     * @param currentTime
     */
    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
        tvCurrentTime.setText(currentTime);

    }

    @Override
    public void initData() {

    }
}

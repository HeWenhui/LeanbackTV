package com.xueersi.parentsmeeting.modules.livevideo.miracast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;

public class MiracastLiveMediaControllerTop extends BaseLiveMediaControllerTop {
    private ImageView mTvPlayIv;
    private TvBtnClicklistener mTvBtnClickListener;

    public MiracastLiveMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController.MediaPlayerControl mPlayer) {
        super(context, controller, mPlayer);
    }


    @Override
    protected View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_letoulivemediacontroller_top, this);
    }

    @Override
    protected void findViewItems() {
        super.findViewItems();
        mTvPlayIv = findViewById(R.id.iv_video_tv_player);
        mTvPlayIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvBtnClickListener != null) {
                    mTvBtnClickListener.onTvPlayClick(v);
                }
            }
        });
    }

    public void setTvPlayClickListener(TvBtnClicklistener listener) {
        mTvBtnClickListener = listener;
    }

    public interface TvBtnClicklistener {
        void onTvPlayClick(View view);
    }

    public void changeLOrP() {
        mPlayer.changeLOrP();
    }
}

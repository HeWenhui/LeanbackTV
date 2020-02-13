package com.xueersi.parentsmeeting.modules.livevideo.miracast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;

public class LetouLivePlaybackMediaController extends LivePlaybackMediaController {
    private ImageView mTvPlayerBtn;
    private TvPlayClickListener mTvPlayerCkiclListener;

    public LetouLivePlaybackMediaController(Context context, BackMediaPlayerControl player, boolean mIsLand) {
        super(context, player, mIsLand);
    }

    @Override
    protected View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.pop_letou_mediacontroller, this);
    }

    @Override
    protected void findViewItems() {
        super.findViewItems();
        mTvPlayerBtn = findViewById(R.id.iv_tv_player);
        mTvPlayerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvPlayerCkiclListener!=null){
                    mTvPlayerCkiclListener.onTvClick(v);
                }
            }
        });
    }

    public void setTvPlayBtnClickListener(TvPlayClickListener clickListener) {
        mTvPlayerCkiclListener = clickListener;
    }

    public interface TvPlayClickListener {
        void onTvClick(View view);
    }
}

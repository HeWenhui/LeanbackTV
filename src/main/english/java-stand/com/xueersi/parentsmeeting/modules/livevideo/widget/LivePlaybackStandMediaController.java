package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.media.MediaControllerBottom2;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 站立直播回放
 * Created by linyuqiang on 2018/8/3.
 * 新站立直播的控制
 */
public class LivePlaybackStandMediaController extends LivePlaybackMediaController {

    public LivePlaybackStandMediaController(Context context, BackMediaPlayerControl player, boolean mIsLand) {
        super(context, player, mIsLand);
    }

    @Override
    protected View inflateLayout() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_livestand_mediacontroller, this);
        return view;
    }

    @Override
    protected void addBottom() {
        controllerBottom = new LiveBackStandMediaControllerBottom(getContext(), this, mPlayer);
        setControllerBottom(controllerBottom);
        mediaControllerBottom = (MediaControllerBottom2) controllerBottom;
        rlKeyPoints = (RelativeLayout) mediaControllerBottom.findViewById(R.id.rl_video_mediacontroller_keypoints);
        rlKeytip = (RelativeLayout) mediaControllerBottom.findViewById(R.id.rl_video_mediacontroller_keytip);
    }
}

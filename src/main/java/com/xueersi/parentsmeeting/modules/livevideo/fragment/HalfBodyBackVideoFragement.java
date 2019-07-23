package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;

import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackHalfBodyMedianController;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;

/**
 * 半身直播  直播回放
 *
 * @author chekun
 * created  at 2018/11/15 15:43
 */
public class HalfBodyBackVideoFragement extends LiveBackVideoFragment {

    @Override
    protected LivePlaybackMediaController createLivePlaybackMediaController() {
        LivePlaybackHalfBodyMedianController playbackHalfBodyMedianController = new
                LivePlaybackHalfBodyMedianController(activity, liveBackPlayVideoFragment, mIsLand.get());
        return playbackHalfBodyMedianController;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }
}

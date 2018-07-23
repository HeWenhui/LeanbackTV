package com.xueersi.parentsmeeting.modules.livevideo.fragment;

/**
 * Created by lyqai on 2018/7/23.
 */

public class LivePlaybackVideoActivity extends LiveVideoActivityBase {

    @Override
    protected LiveVideoFragmentBase getFragment() {
        return new LiveBackVideoFragment();
    }
}

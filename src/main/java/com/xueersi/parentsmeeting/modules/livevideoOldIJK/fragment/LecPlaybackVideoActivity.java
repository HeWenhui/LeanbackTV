package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

/**
 * Created by lyqai on 2018/7/23.
 */

public class LecPlaybackVideoActivity extends LiveBackVideoActivityBase {

    @Override
    protected LiveBackVideoFragmentBase getFragment() {
        return new LiveBackVideoFragment();
    }
}
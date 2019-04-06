package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

public class EngineConfig {
    public int mClientRole;

    public int mVideoProfile;

    public int mUid;

    public String mChannel;

    public void reset() {
        mChannel = null;
    }

    EngineConfig() {
    }
}

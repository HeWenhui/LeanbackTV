package com.xueersi.parentsmeeting.modules.livevideo.fragment;

public interface LivePlayAction {
    void setVolume(float left, float right);

    void showLongMediaController();

    void stopPlayer();

    void rePlay(boolean modechange);

    void changeNowLine();
}

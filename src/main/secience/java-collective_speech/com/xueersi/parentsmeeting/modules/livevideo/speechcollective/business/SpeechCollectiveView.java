package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business;

import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;

import java.util.List;

public interface SpeechCollectiveView {

    void addRipple(int level);

    List<SoundWaveView.Circle> getRipples();

    void onDeny();

    void start();

    void setStart(boolean start);

    void onHaveVolume(OnTipHide onTipHide);

    void onNoVolume(OnTipHide onTipHide);

    void onHaveVolume();

    View getRootView();

    interface OnTipHide {
        void hide();
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business;

import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;

import java.util.List;

public interface SpeechCollectiveView {

    void addRipple(int level);

    List<SoundWaveView.Circle> getRipples();

    void start();

    void onNoVolume();
}

package com.xueersi.parentsmeeting.modules.livevideoOldIJK.speechfeedback.page;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

/**
 * Created by linyuqiang on 2018/1/11.
 */

public class SpeechFeedBackPager extends BasePager {
    VolumeWaveView vwvSpeectevalWave;

    public SpeechFeedBackPager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_speech_feedback, null);
        vwvSpeectevalWave = (VolumeWaveView) view.findViewById(R.id.vwv_livevideo_speecteval_wave);
        return view;
    }

    @Override
    public void initData() {
        int colors[] = {0x19F13232, 0x32F13232, 0x64F13232, 0x96F13232, 0xFFF13232};
        vwvSpeectevalWave.setColors(colors);
//        vwvSpeectevalWave.setBackColor(0xffeaebf9);
        vwvSpeectevalWave.setBackColor(Color.TRANSPARENT);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeectevalWave.start();
            }
        }, 10);
    }

    public void setVolume(float volume) {
        vwvSpeectevalWave.setVolume(volume);
    }
}

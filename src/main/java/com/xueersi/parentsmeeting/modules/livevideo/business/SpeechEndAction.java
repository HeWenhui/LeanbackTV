package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.widget.RelativeLayout;

/**
 * Created by linyuqiang on 2018/4/10.
 * 语音评测结束事件
 */
public interface SpeechEndAction {
    void examSubmitAll(String num);

    void initView(RelativeLayout bottomContent);

    void onStopSpeech(String num);
}

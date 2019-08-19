package com.xueersi.parentsmeeting.modules.livevideoOldIJK.worddictation.business;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.worddictation.entity.WordStatisticInfo;

/**
 * Created by linyuqiang on 2018/9/4.
 */
public interface WordDictationAction {
    void onStart(WordStatisticInfo wordStatisticInfo);

    void onStop();

    void onDestroy();
}

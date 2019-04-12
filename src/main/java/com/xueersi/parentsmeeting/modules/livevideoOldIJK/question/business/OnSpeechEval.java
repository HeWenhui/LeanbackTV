package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import com.xueersi.common.http.ResponseEntity;

/**
 * Created by linyuqiang on 2017/2/27.
 */

public interface OnSpeechEval {
    void onSpeechEval(Object object);

    void onPmFailure(Throwable error, String msg);

    void onPmError(ResponseEntity responseEntity);
}

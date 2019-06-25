package com.xueersi.parentsmeeting.modules.livevideo.videochat.business;

import com.xueersi.common.http.HttpCallBack;

/**
 * Created by linyuqiang on 2018/7/11.
 */

public interface VideoChatHttp {
    void requestMicro(String nonce, String from);

    void giveupMicro(String from);

    void chatHandAdd(HttpCallBack call);

}

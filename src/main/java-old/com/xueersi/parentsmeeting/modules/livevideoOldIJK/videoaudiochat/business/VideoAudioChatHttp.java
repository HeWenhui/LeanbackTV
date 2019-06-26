package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videoaudiochat.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;

/**
 * Created by linyuqiang on 2018/10/25.
 */
public interface VideoAudioChatHttp {
    void requestMicro(String nonce, String room, String from);

    void giveupMicro(String from);

    void praise(String uid, int likes);

    void sendNetWorkQuality(int quality);

    void chatHandAdd(HttpCallBack call);

    void getStuInfoByIds(String uid, AbstractBusinessDataCallBack abstractBusinessDataCallBack);
}

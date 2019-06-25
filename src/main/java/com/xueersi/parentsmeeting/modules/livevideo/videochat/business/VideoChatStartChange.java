package com.xueersi.parentsmeeting.modules.livevideo.videochat.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

/**
 * Created by linyuqiang on 2018/7/11.
 */

public interface VideoChatStartChange extends LiveProvide {

    void addVideoChatStatrtChange(ChatStartChange chatStartChange);

    void removeVideoChatStatrtChange(ChatStartChange chatStartChange);

    interface ChatStartChange {
        void onVideoChatStartChange(boolean start);
    }
}

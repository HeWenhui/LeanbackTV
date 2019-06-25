package com.xueersi.parentsmeeting.modules.livevideo.videochat.business;

/**
 * Created by linyuqiang on 2018/7/11.
 */

public interface VideoChatStartChange {

    void addVideoChatStatrtChange(ChatStartChange chatStartChange);

    void removeVideoChatStatrtChange(ChatStartChange chatStartChange);

    interface ChatStartChange {
        void onVideoChatStartChange(boolean start);
    }
}

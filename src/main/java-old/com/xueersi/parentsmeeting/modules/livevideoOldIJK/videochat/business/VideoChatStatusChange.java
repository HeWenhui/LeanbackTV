package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.business;

/**
 * Created by linyuqiang on 2018/7/11.
 */

public interface VideoChatStatusChange {

    void addVideoChatStatusChange(ChatStatusChange chatStatusChange);

    void removeVideoChatStatusChange(ChatStatusChange chatStatusChange);

    interface ChatStatusChange {
        void onVideoChatStatusChange(String voiceChatStatus);
    }
}

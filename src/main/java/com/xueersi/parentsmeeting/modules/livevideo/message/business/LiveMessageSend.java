package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

public interface LiveMessageSend extends LiveProvide {
    void addMessage(String sender, int type, String text);
}

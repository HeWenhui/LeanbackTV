package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.content.Intent;

import com.xueersi.parentsmeeting.modules.livevideo.business.BusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatIRCBll;

public class VideoChatCreat implements BusinessCreat {
    @Override
    public Class<? extends LiveBaseBll> getClassName(Intent intent) {
        int allowLinkMicNew = intent.getIntExtra("allowLinkMicNew", 0);
        LiveLoggerFactory.getLogger(this).d("getClass:allowLinkMicNew=" + allowLinkMicNew);
        if (allowLinkMicNew == 1) {
            return VideoAudioChatIRCBll.class;
        } else {
            return VideoChatIRCBll.class;
        }
    }
}

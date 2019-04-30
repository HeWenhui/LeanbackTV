package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class UploadVideoService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadVideoService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}

package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.support.annotation.NonNull;

public class LiveThread extends Thread {

    public LiveThread() {
        super();
    }

    public LiveThread(Runnable target) {
        super(target);
    }

    public LiveThread(@NonNull String name) {
        super(name);
    }
}

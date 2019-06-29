package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Intent;

public interface BackBusinessCreat {
    Class<? extends LiveBackBaseBll> getClassName(Intent intent);

    Class[] reloadClass();
}

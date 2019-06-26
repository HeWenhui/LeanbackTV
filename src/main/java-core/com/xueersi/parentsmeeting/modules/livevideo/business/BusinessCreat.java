package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Intent;

public interface BusinessCreat {
    Class<? extends LiveBaseBll> getClassName(Intent intent);
}

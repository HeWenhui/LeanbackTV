package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

/**
 * Created by Administrator on 2017/5/9.
 */

public interface LicodeToken {
    void onToken(String token);

    void onError(Throwable ex);
}

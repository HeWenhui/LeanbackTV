package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

import android.text.TextUtils;

/**
 * Created by linyuqiang on 2018/9/8.
 * 日志统计
 */
public class LogErrorEntity {
    public int count;
    public String url;
    public long firstTime;
    public long lastTime;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LogErrorEntity)) {
            return false;
        }
        LogErrorEntity other = (LogErrorEntity) obj;
        return TextUtils.equals(url, other.url);
    }
}

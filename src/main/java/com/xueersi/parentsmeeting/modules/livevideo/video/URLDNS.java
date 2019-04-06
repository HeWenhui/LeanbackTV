package com.xueersi.parentsmeeting.modules.livevideo.video;


/**
 * Created by linyuqiang on 2018/9/5.
 * url和dns，解析时间
 */
public class URLDNS {
    public String url;
    public String ip;
    public long time = 0;

    @Override
    public String toString() {
        return "ip:" + ip + ",time=" + time;
    }
}

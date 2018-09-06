package com.xueersi.parentsmeeting.modules.livevideo.video;

/**
 * Created by linyuqiang on 2018/8/6.
 * 播放误码
 */
public class PlayFailCode {
    public static int TIME_OUT = 15;
    int code;
    String tip;

    PlayFailCode(int code, String tip) {
        this.code = code;
        this.tip = tip;
    }

    public int getCode() {
        return code;
    }

    public String getTip() {
        return tip;
    }

}

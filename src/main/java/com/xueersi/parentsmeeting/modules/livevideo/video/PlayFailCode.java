package com.xueersi.parentsmeeting.modules.livevideo.video;

/**
 * Created by linyuqiang on 2018/8/6.
 * 播放误码
 */
public class PlayFailCode {
    public static int TIME_OUT = 15;
    public static PlayFailCode PlayFailCode0 = new PlayFailCode(0, "success");
    public static PlayFailCode PlayFailCode10 = new PlayFailCode(10, "Failed to resolve hostname");
    public static PlayFailCode PlayFailCode15 = new PlayFailCode(15, "Connection timed out");
    public static PlayFailCode PlayFailCode20 = new PlayFailCode(20, "Server Error");
    public int code;
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

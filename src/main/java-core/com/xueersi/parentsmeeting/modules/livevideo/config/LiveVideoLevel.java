package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * 直播间布局层级
 *
 * @author linyuqiang
 * created  at 2019/7/11
 */
public class LiveVideoLevel {
    /** 弹幕布局 */
    public static LiveVideoLevel LEVEL_DANMU = new LiveVideoLevel(-10);
    /** 聊天布局 */
    public static LiveVideoLevel LEVEL_MES = new LiveVideoLevel(0);
    /** 视频控制栏 */
    public static LiveVideoLevel LEVEL_CTRl = new LiveVideoLevel(10);
    /** 互动题 */
    public static LiveVideoLevel LEVEL_QUES = new LiveVideoLevel(20);
    private int level;

    public LiveVideoLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "level=" + level;
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.config;

public class LiveVideoLevel {
    public static LiveVideoLevel LEVEL_MES = new LiveVideoLevel(0);

    public static LiveVideoLevel LEVEL_CTRl = new LiveVideoLevel(10);
    public static LiveVideoLevel LEVEL_QUES = new LiveVideoLevel(20);
    private int level;

    public LiveVideoLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

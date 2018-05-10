package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 站立直播一些字段
 *
 * @author linyuqiang
 * @date 2018/5/9
 */
public class StandLiveConfig {

    /**
     * 背景音乐 占系统音量的 比列
     */
    public static final float MUSIC_VOLUME_RATIO_BG = 0.3f;
    /**
     * 前景音效的比列
     */
    public static final float MUSIC_VOLUME_RATIO_FRONT = 0.6f;
    public static String version = "2018041510";
    public static VoicePath voicePath;
    private static String voiceDir;

    public static void createVoice(Context context) {
        if (voicePath != null) {
            return;
        }
        File alldir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/live_stand");
        if (alldir == null) {
            alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/live_stand");
        }
        File externalFilesDir = new File(alldir, version + "/live_stand");
        File saveFile = new File(externalFilesDir, "frame_anim");
        voiceDir = new File(saveFile, "voice").getPath();
        voicePath = new VoicePath();
    }

    /**
     * 站立直播音效文件
     */
    public static class VoicePath {
        /**
         * 转换场景
         */
        public final String VOICE_CHANGE_SCENE = voiceDir + "/" + "changescene.mp3";
        /**
         * 点击
         */
        public final String VOICE_CLICK_BUTTON = voiceDir + "/" + "clickbutton.mp3";
        /**
         * 排行榜
         */
        public final String VOICE_LEADER_BOARD = voiceDir + "/" + "leaderboard.mp3";
        /**
         * 加载中
         */
        public final String VOICE_LOADING = voiceDir + "/" + "loading.mp3";
        /**
         * 弹出
         */
        public final String VOICE_POPUP = voiceDir + "/" + "popup.mp3";
        /**
         * readygo
         */
        public final String VOICE_READYGO = voiceDir + "/" + "readygo.mp3";
        /**
         * 红包出场动画
         */
        public final String VOICE_RED_POCKET = voiceDir + "/" + "redpocket-01.mp3";
        /**
         * 红包中间点击
         */
        public final String VOICE_RED_FLY = voiceDir + "/" + "redpocket-02.mp3";
        /**
         * 正确声音
         */
        public final String VOICE_RIGHT = voiceDir + "/" + "right.mp3";
        /**
         * 红包小组成员动画的音效
         */
        public final String VOICE_SHIP_FLOATING = voiceDir + "/" + "shipfloating.mp3";
        /**
         * siu的声音
         */
        public final String VOICE_SIU = voiceDir + "/" + "siu.mp3";
        /**
         * 错误声音
         */
        public final String VOICE_WRONG = voiceDir + "/" + "wrong.mp3";
    }
}

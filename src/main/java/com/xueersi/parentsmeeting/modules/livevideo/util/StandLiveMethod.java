package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.xueersi.parentsmeeting.modules.livevideo.config.StandLiveConfig;

/**
 * @author linyuqiang
 * @date 2018/5/9
 * 站立直播一些静态方法
 */
public class StandLiveMethod {
    public static LiveSoundPool.SoundPlayTask onClickVoice(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_CLICK_BUTTON;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask changeScene(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_CHANGE_SCENE;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask floatFloadating(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_SHIP_FLOATING;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, true);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask redPocket(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_RED_POCKET;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask readyGo(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_READYGO;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }
}
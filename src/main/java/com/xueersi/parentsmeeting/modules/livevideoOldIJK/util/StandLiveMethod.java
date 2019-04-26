package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.config.StandLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;

/**
 * @author linyuqiang
 * @date 2018/5/9
 * 站立直播一些静态方法
 */
public class StandLiveMethod {
    public static LiveSoundPool.SoundPlayTask onClickVoice(LiveSoundPool soundPool) {
        if (StandLiveConfig.voicePath == null) {
            StandLiveConfig.createVoice(BaseApplication.getContext());
        }
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
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask redPocket(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_RED_POCKET;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask redFly(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_RED_FLY;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask voiceSiu(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_SIU;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask readyGo(LiveSoundPool soundPool) {
        if (StandLiveConfig.voicePath == null) {
            StandLiveConfig.createVoice(BaseApplication.getContext());
        }
        String path = StandLiveConfig.voicePath.VOICE_READYGO;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask leaderBoard(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_LEADER_BOARD;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask voiceRight(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_RIGHT;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask voiceWrong(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_WRONG;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }

    public static LiveSoundPool.SoundPlayTask voicePopup(LiveSoundPool soundPool) {
        String path = StandLiveConfig.voicePath.VOICE_POPUP;
        LiveSoundPool.SoundPlayTask task = new LiveSoundPool.SoundPlayTask(path, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        int soundId = LiveSoundPool.play(null, soundPool, task);
        return task;
    }
}

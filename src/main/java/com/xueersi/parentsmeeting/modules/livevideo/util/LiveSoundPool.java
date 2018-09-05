package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.xueersi.parentsmeeting.modules.livevideo.entity.SoundInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.util.HashMap;

/**
 * SoundPool的一些方法
 *
 * @author linyuqiang
 * @date 2018/5/9
 */
public class LiveSoundPool {
    static String TAG = "LiveSoundPool";
    private SoundPool soundPool;
    private HashMap<SoundPlayTask, SoundInfo> mSoundInfoMap;

    private LiveSoundPool() {

    }

    private int load(String path, int i) {
        if (soundPool == null) {
            Loger.e(TAG, "load:path=" + path, new Exception());
            return -1;
        }
        return soundPool.load(path, i);
    }

    private int load(Context context, int resId, int i) {
        if (soundPool == null) {
            Loger.e(TAG, "load:resId=" + resId, new Exception());
            return -1;
        }
        return soundPool.load(context, resId, i);
    }

    public void release() {
        if (soundPool == null) {
            Loger.e(TAG, "release", new Exception());
            return;
        }
        soundPool.release();
    }

    public void pause(SoundPlayTask task) {
        SoundInfo soundInfo = mSoundInfoMap.get(task);
        if (soundInfo != null) {
            stop(soundInfo.getStreamId());
            Loger.d(TAG, "pause:task=" + task.path + ",streamId=" + soundInfo.getStreamId());
        }
    }

    public void pause(int soundId) {
        if (soundPool == null) {
            Loger.e(TAG, "pause", new Exception());
            return;
        }
        soundPool.pause(soundId);
    }

    public void stop(SoundPlayTask task) {
        SoundInfo soundInfo = mSoundInfoMap.remove(task);
        if (soundInfo != null) {
            stop(soundInfo.getStreamId());
            Loger.d(TAG, "stop:task=" + task.path + ",streamId=" + soundInfo.getStreamId());
        }
    }

    public void stop(int streamId) {
        if (soundPool == null) {
            Loger.e(TAG, "stop", new Exception());
            return;
        }
        soundPool.stop(streamId);
    }

    public static int play(Context context, final LiveSoundPool liveSoundPool, final SoundPlayTask task) {
        final int soundId;
        if (task.resId != 0) {
            soundId = liveSoundPool.load(context, task.resId, 1);
            Loger.d(TAG, "play:resId=" + task.resId + ",soundId=" + soundId);
        } else {
            soundId = liveSoundPool.load(task.path, 1);
            Loger.d(TAG, "play:path=" + task.path + ",soundId=" + soundId);
        }
        liveSoundPool.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    int streamID = soundPool.play(sampleId, task.volume, task.volume, 0, task.loop ? -1 : 0, 1);
                    SoundInfo soundInfo = new SoundInfo(sampleId, streamID);
                    liveSoundPool.mSoundInfoMap.put(task, soundInfo);
                    if (task.resId != 0) {
                        Loger.d(TAG, "onLoadComplete:resId=" + task.resId + ",streamID=" + streamID);
                    } else {
                        Loger.d(TAG, "onLoadComplete:path=" + task.path + ",streamID=" + streamID);
                    }
                } else {
                    if (task.resId != 0) {
                        Loger.d(TAG, "onLoadComplete:resId=" + task.resId + ",status=" + status);
                    } else {
                        Loger.d(TAG, "onLoadComplete:path=" + task.path + ",status=" + status);
                    }
                }
            }
        });
        return soundId;
    }

    public static LiveSoundPool createSoundPool() {
        return createSoundPool(2, AudioManager.STREAM_MUSIC);
    }

    public static LiveSoundPool createSoundPool(int maxStreams, int streamType) {
        LiveSoundPool liveSoundPool = new LiveSoundPool();
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(maxStreams);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(streamType);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(maxStreams, streamType, 5);
        }
        liveSoundPool.soundPool = soundPool;
        liveSoundPool.mSoundInfoMap = new HashMap<>();
        return liveSoundPool;
    }

    public static class SoundPlayTask {
        /** 工程资源id */
        int resId = 0;
        /** 存储路径 */
        String path;
        /** 声音 */
        float volume;
        /** 是否循环 */
        boolean loop;

        public SoundPlayTask(int resId, float volume, boolean loop) {
            this.resId = resId;
            this.volume = volume;
            this.loop = loop;
        }

        public SoundPlayTask(String path, float volume, boolean loop) {
            this.path = path;
            this.volume = volume;
            this.loop = loop;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            if (resId == 0) {
                return path.hashCode();
            }
            return resId;
        }
    }
}

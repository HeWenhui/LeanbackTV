package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.media.MediaPlayer;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanwei2 on 2019/4/25.
 */

public class SoundEffectPlayer {


    static Handler mHandler;

    private int soundIndex;

    private boolean isPlaying;

    private MediaPlayer mediaPlayer;

    private List<String> soundFiles;

    private SoundPlayListener playListener;

    public SoundEffectPlayer(String targetFile) {
        this.soundFiles = new ArrayList<>();
        this.soundFiles.add(targetFile);
    }

    public SoundEffectPlayer(List<String> soundFiles) {
        this.soundFiles = soundFiles;
    }

    public void setPlayListener(SoundPlayListener playListener) {
        this.playListener = playListener;
    }

    public void start() {

        if (isPlaying || fileCount() == 0) {
            return;
        }

        isPlaying = true;
        playSound(0);
    }

    public int fileCount() {
        return soundFiles != null ? soundFiles.size() : 0;
    }

    public void cancle() {
        if (mediaPlayer != null) {
            playListener = null;
            mediaPlayer.stop();
            recyclePlayer();
        }
    }

    private void playSound(int index) {

        this.soundIndex = index;

        mediaPlayer = new MediaPlayer();

        try {
            final String filepath = soundFiles.get(index);
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    recyclePlayer();

                    int fileCount = fileCount();
                    if (soundIndex + 1 < fileCount) {
                        postPlayNext();
                    } else {
                        postComplete();
                    }
                }
            });

        } catch (Exception e) {
            recyclePlayer();

            int fileCount = fileCount();
            if (soundIndex + 1 < fileCount) {
                postPlayNext();
            } else {
                postComplete();
            }
        }
    }

    private void recyclePlayer() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void postPlayNext() {

        if (mHandler == null) {
            mHandler = new Handler();
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                playSound(soundIndex + 1);
            }
        };

        mHandler.post(action);
    }

    private void postComplete() {
        if (mHandler == null) {
            mHandler = new Handler();
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                if (playListener != null) {
                    playListener.onSoundFinish();
                    playListener = null;
                }
            }
        };

        mHandler.post(action);
    }

    public interface SoundPlayListener {
        void onSoundFinish();
    }

}

package com.xueersi.parentsmeeting.modules.livevideoOldIJK.manager;/*
package com.xueersi.parentsmeeting.modules.livevideoOldIJK.manager;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.xueersi.lib.log.Loger;


*/
/**
 * Created by yzl on 2018/7/9.
 *//*


public class ExoPlayerManager {
    private ExoPlayerManager() {
    }

    private static ExoPlayerManager mInstance = null;
    private SimpleExoPlayer mExoplayer;//ExoPlayer

    public static ExoPlayerManager getmInstance() {
        if (mInstance == null) {
            synchronized (ExoPlayerManager.class) {
                if (mInstance == null) {
                    mInstance = new ExoPlayerManager();
                }
            }
        }
        return mInstance;
    }

    */
/**
     * @param playCorrectVoiceUrl 音频的地址（网络，本地都可以播放）
     * @param speed               播放的速度
     * @param pitch               播放的音调
     * @param playerCallback      播放回调
     *//*

    public SimpleExoPlayer startWithSpeed(Context context, final String playCorrectVoiceUrl, float speed, float
            pitch, long curPos) {

        try {
            mExoplayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());

            DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, "audio/mpeg");
            //audio/mpeg

            ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(); //创建一个媒体连接源


            MediaSource mediaSource1 = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(Uri.parse(playCorrectVoiceUrl)); //创建一个播放数据源

            concatenatingMediaSource.addMediaSource((MediaSource) mediaSource1);
            //把数据源添加到concatenatingMediaSource里面，相当于添加到一个播放池


            mExoplayer.setPlaybackParameters(new PlaybackParameters(speed, pitch));
            mExoplayer.seekTo(curPos);

            mExoplayer.prepare(concatenatingMediaSource); //把Player和数据源关联起来

        } catch (Exception e) {
            String err = e.toString();
        } finally {
            String s = "v";
        }

        return mExoplayer;
    }

    */
/**
     * 默认1.0速度的播放器
     * @param context
     * @param absolutePath
     * @param curpos
     * @param playerCallback
     * @return
     *//*

    public SimpleExoPlayer startWithSpeed(Context context, String absolutePath, int curpos, final OnPlayCallback playerCallback) {
        mExoplayer =  startWithSpeed(context,absolutePath,1.0f,1.0f,curpos);
        mExoplayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Loger.i("yzl", "playbackState = "+playbackState);
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        if(playerCallback != null){
                            playerCallback.onIdle();
                        }
                        break;
                    case ExoPlayer.STATE_READY:
                        if(playerCallback != null){
                            playerCallback.onReady();
                        }
                        break;
                    case ExoPlayer.STATE_ENDED:
                        if(playerCallback != null){
                            playerCallback.onCompletion();
                        }

                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if(playerCallback != null){
                    playerCallback.onPlayError(error);
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Loger.i("yzl","onPlaybackParametersChanged");
            }

            @Override
            public void onSeekProcessed() {

            }

        });
        return mExoplayer;
    }

    public interface OnPlayCallback {
        void onCompletion();

        void onPlayError(ExoPlaybackException error);

        void onReady();

        void onIdle();
    }
}
*/

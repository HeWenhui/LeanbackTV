package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.rtc.IRtcEngineEventHandler;

public class MyEngineEventHandler {
    public static final String TAG = "MyEngineEventHandler";
    protected Logger logger = LiveLoggerFactory.getLogger(this.getClass().getSimpleName());
    boolean feadback;
    private OnLastmileQuality onLastmileQuality;

    public MyEngineEventHandler(Context ctx, EngineConfig config, boolean feadback) {
        this.mContext = ctx;
        this.mConfig = config;
        this.feadback = feadback;
    }

    public void setOnLastmileQuality(OnLastmileQuality onLastmileQuality) {
        this.onLastmileQuality = onLastmileQuality;
    }

    private final EngineConfig mConfig;

    private final Context mContext;

    private final ConcurrentHashMap<AGEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap<>();

    public void addEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.put(handler, 0);
    }

    public void removeEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.remove(handler);
    }

    public void setFeadback(boolean feadback) {
        this.feadback = feadback;
    }

    final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        public void onAudioRouteChanged(int routing) {
            logger.d("onAudioRouteChanged:routing=" + routing);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            if (feadback) {
                for (AudioVolumeInfo info : speakers) {
                    if (info.uid == 0) {
                        for (AGEventHandler handler : mEventHandlerList.keySet()) {
                            handler.onVolume(info.volume);
                        }
                    }
//                    logger.d("onAudioVolumeIndication:info=" + info.uid + "," + info.volume);
                }
            }
        }

        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            logger.d("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + width + " " + height + " " + elapsed);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            }
        }

        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state,reason,elapsed);
            logger.d("onRemoteVideoStateChanged:uid=" + uid + ",state=" + state);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            }
        }


        @Override
        public void onFirstRemoteAudioFrame(int uid, int elapsed) {
            super.onFirstRemoteAudioFrame(uid, elapsed);
            logger.d("onFirstRemoteAudioFrame:uid=" + uid + ",elapsed=" + elapsed);
        }

        @Override
        public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
            logger.d("onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            logger.d("onUserJoined:uid=" + uid + ",elapsed=" + elapsed);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onUserJoined(uid, elapsed);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // FIXME this callback may return times
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onUserOffline(uid, reason);
            }
        }

        @Override
        public void onUserMuteVideo(int uid, boolean muted) {
            logger.d("onUserMuteVideo:uid=" + uid + ",muted=" + muted);
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            logger.d("onUserMuteAudio:uid=" + uid + ",muted=" + muted);
        }

        public void onUserEnableLocalVideo(int uid, boolean enabled) {
            logger.d("onUserEnableLocalVideo:uid=" + uid + ",enabled=" + enabled);
        }

        @Override
        public void onRtcStats(RtcStats stats) {
        }


        @Override
        public void onLeaveChannel(RtcStats stats) {

        }

        @Override
        public void onLastmileQuality(int quality) {
            logger.d("onLastmileQuality:quality=" + quality);
            if (onLastmileQuality != null) {
                onLastmileQuality.onLastmileQuality(quality);
            }
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            logger.e("onError:err=" + err);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onError(err);
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            logger.d("onJoinChannelSuccess:channel=" + channel + ",uid=" + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }

        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            logger.d("onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
        }

        public void onWarning(int warn) {
            logger.e("onWarning " + warn);
        }
    };

    public interface OnLastmileQuality {
        void onLastmileQuality(int quality);

        void onQuit();
    }
}

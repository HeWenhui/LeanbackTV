package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;

import com.xueersi.xesalib.utils.log.Loger;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.rtc.IRtcEngineEventHandler;

public class MyEngineEventHandler {
    public static final String TAG = "MyEngineEventHandler";

    public MyEngineEventHandler(Context ctx, EngineConfig config) {
        this.mContext = ctx;
        this.mConfig = config;
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

    final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        public void onAudioRouteChanged(int routing) {
            Loger.d(TAG, "onAudioRouteChanged:routing=" + routing);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            for (AudioVolumeInfo info:speakers) {
                if(info.uid==0){
                    for(AGEventHandler handler:mEventHandlerList.keySet()){
                        handler.onVolume(info.volume);
                    }
                }
                Loger.d(TAG, "onAudioVolumeIndication:info=" + info.uid + "," + info.volume);
            }
        }

        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            Loger.d(TAG, "onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + width + " " + height + " " + elapsed);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            }
        }

        @Override
        public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
            Loger.d(TAG, "onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Loger.d(TAG, "onUserJoined:uid=" + uid + ",elapsed=" + elapsed);
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
        }

        @Override
        public void onRtcStats(RtcStats stats) {
        }


        @Override
        public void onLeaveChannel(RtcStats stats) {

        }

        @Override
        public void onLastmileQuality(int quality) {
            Loger.d(TAG, "onLastmileQuality:quality=" + quality);
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Loger.e(TAG, "onError:err=" + err);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onError(err);
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Loger.d(TAG, "onJoinChannelSuccess:channel=" + channel + ",uid=" + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }

        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            Loger.d(TAG, "onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
        }

        public void onWarning(int warn) {
            Loger.e(TAG, "onWarning " + warn);
        }
    };

}

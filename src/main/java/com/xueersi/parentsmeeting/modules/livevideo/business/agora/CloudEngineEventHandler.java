package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;

import com.xes.ps.rtcstream.RTCEngine;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by linyuqiang on 2019/5/7.
 * 云平台的接麦回调
 */
public class CloudEngineEventHandler {
    public static final String TAG = "CloudEngineEventHandler";
    protected Logger logger = LiveLoggerFactory.getLogger(this.getClass().getSimpleName());
    private final Context mContext;

    public CloudEngineEventHandler(Context ctx) {
        this.mContext = ctx;
    }

    private final ConcurrentHashMap<RTCEngine.IRtcEngineEventListener, Integer> mEventHandlerList = new ConcurrentHashMap<>();

    public void addEventHandler(RTCEngine.IRtcEngineEventListener handler) {
        this.mEventHandlerList.put(handler, 0);
    }

    public void removeEventHandler(RTCEngine.IRtcEngineEventListener handler) {
        this.mEventHandlerList.remove(handler);
    }

    final RTCEngine.IRtcEngineEventListener rtcEngineEventListener = new RTCEngine.IRtcEngineEventListener() {

        @Override
        public void remotefirstVideoRecvWithUid(int uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.remotefirstVideoRecvWithUid(uid);
            }
        }

        @Override
        public void remoteUserJoinWitnUid(int uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.remoteUserJoinWitnUid(uid);
            }
        }

        @Override
        public void didOfflineOfUid(int uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.didOfflineOfUid(uid);
            }
        }

        @Override
        public void didAudioMuted(int uid, boolean muted) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.didAudioMuted(uid, muted);
            }
        }

        @Override
        public void didVideoMuted(int uid, boolean muted) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.didVideoMuted(uid, muted);
            }
        }

        @Override
        public void didOccurError(RTCEngine.RTCEngineErrorCode code) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.didOccurError(code);
            }
        }

        @Override
        public void onConnectionLost() {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.onConnectionLost();
            }
        }

        @Override
        public void localUserJoindWithUid(int uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.localUserJoindWithUid(uid);
            }
        }

        @Override
        public void reportAudioVolumeOfSpeaker(int uid, int volume) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.reportAudioVolumeOfSpeaker(uid, volume);
            }
        }

        @Override
        public void remotefirstAudioRecvWithUid(int uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.remotefirstAudioRecvWithUid(uid);
            }
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.onRemoteVideoStateChanged(uid, state);
            }
        }

        @Override
        public void onOnceLastMileQuality(RTCEngine.RTC_LASTMILE_QUALITY lastmileQuality) {

        }
    };
}

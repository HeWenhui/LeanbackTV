package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;

import com.xes.ps.rtcstream.RTCEngine;
import com.xes.ps.rtcstream.listener.RTCConnectionStateType;
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
    private MyEngineEventHandler.OnLastmileQuality onLastmileQuality;

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
        public void remotefirstVideoRecvWithUid(long uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.remotefirstVideoRecvWithUid(uid);
            }
        }

        @Override
        public void remoteUserJoinWitnUid(long uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.remoteUserJoinWitnUid(uid);
            }
        }

        @Override
        public void didOfflineOfUid(long uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.didOfflineOfUid(uid);
            }
        }

        @Override
        public void didAudioMuted(long uid, boolean muted) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.didAudioMuted(uid, muted);
            }
        }

        @Override
        public void didVideoMuted(long uid, boolean muted) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.didVideoMuted(uid, muted);
            }
        }

        @Override
        public void reportRtcStats(RTCEngine.ReportRtcStats stats) {

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
        public void connectionChangedToState(RTCConnectionStateType state, String reason){
            // 原onConnectionLost逻辑
            if(state!= null && (state.equals(RTCConnectionStateType.RTCConnectionStateTypeDisconnected)
                    || state.equals(RTCConnectionStateType.RTCConnectionStateTypeFailed))) {
                Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
                while (it.hasNext()) {
                    RTCEngine.IRtcEngineEventListener handler = it.next();
                    handler.connectionChangedToState(state, reason);
                }
            }
        }

        @Override
        public void localUserJoindWithUid(long uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.localUserJoindWithUid(uid);
            }
        }

        @Override
        public void reportAudioVolumeOfSpeaker(long uid, int volume) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.reportAudioVolumeOfSpeaker(uid, volume);
            }
        }

        @Override
        public void remotefirstAudioRecvWithUid(long uid) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.remotefirstAudioRecvWithUid(uid);
            }
        }

        @Override
        public void onRemoteVideoStateChanged(long uid, int state) {
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.onRemoteVideoStateChanged(uid, state);
            }
        }

        @Override
        public void onOnceLastMileQuality(RTCEngine.RTC_LASTMILE_QUALITY lastmileQuality) {
            if (onLastmileQuality != null){
                onLastmileQuality.onLastmileQuality(lastmileQuality.getValue());
            }
            Iterator<RTCEngine.IRtcEngineEventListener> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                RTCEngine.IRtcEngineEventListener handler = it.next();
                handler.onOnceLastMileQuality(lastmileQuality);
            }

        }


    };

    public void setOnLastmileQuality(MyEngineEventHandler.OnLastmileQuality onLastmileQuality) {
        this.onLastmileQuality = onLastmileQuality;
    }

}

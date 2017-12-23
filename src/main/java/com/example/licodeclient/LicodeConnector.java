//package com.example.licodeclient;
//
//import android.app.Activity;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.util.Base64;
//import android.util.Log;
//
//import com.koushikdutta.async.http.AsyncHttpClient;
//import com.koushikdutta.async.http.socketio.Acknowledge;
//import com.koushikdutta.async.http.socketio.ConnectCallback;
//import com.koushikdutta.async.http.socketio.DisconnectCallback;
//import com.koushikdutta.async.http.socketio.EventCallback;
//import com.koushikdutta.async.http.socketio.SocketIOClient;
//import com.koushikdutta.async.http.spdy.SpdyMiddleware;
//import com.xueersi.parentsmeeting.modules.livevideo.BuildConfig;
//
//import org.appspot.apprtc.AppRTCAudioManager;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.webrtc.AudioTrack;
//import org.webrtc.Camera1Enumerator;
//import org.webrtc.CameraEnumerator;
//import org.webrtc.DataChannel;
//import org.webrtc.EglBase;
//import org.webrtc.IceCandidate;
//import org.webrtc.Logging;
//import org.webrtc.MediaConstraints;
//import org.webrtc.MediaStream;
//import org.webrtc.PeerConnection;
//import org.webrtc.PeerConnection.IceConnectionState;
//import org.webrtc.PeerConnection.IceGatheringState;
//import org.webrtc.PeerConnection.SignalingState;
//import org.webrtc.PeerConnectionFactory;
//import org.webrtc.RtpReceiver;
//import org.webrtc.SdpObserver;
//import org.webrtc.SessionDescription;
//import org.webrtc.StatsObserver;
//import org.webrtc.StatsReport;
//import org.webrtc.VideoCapturer;
//import org.webrtc.VideoFileRenderer;
//import org.webrtc.VideoRenderer;
//import org.webrtc.VideoSource;
//import org.webrtc.VideoTrack;
//import org.webrtc.voiceengine.WebRtcAudioRecord;
//
//import java.io.UnsupportedEncodingException;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
///**
// * A simple class to connect to a licode server and provides callbacks for the
// * standard events associated with this.
// */
//public class LicodeConnector implements VideoConnectorInterface {
//    static String TAG = "LicodeConnector";
//    public static final String VIDEO_TRACK_TYPE = "video";
//    /**
//     * flag to store if basic initialization has happened
//     */
//    private static boolean sInitializedAndroidGlobals;
//    /**
//     * socket.io client
//     */
//    volatile SocketIOClient mIoClient = null;
//    /**
//     * lock object for socket communication
//     */
//    private Object mSocketLock = new Object();
//    /**
//     * current state of the connection
//     */
//    volatile State mState = State.kUninitialized;
//
//    /**
//     * default video bandwidth
//     */
//    int mDefaultVideoBW;
//    /**
//     * max video bandwidth
//     */
//    int mMaxVideoBW = 75;
//    /**
//     * max audio bandwidth
//     */
//    int mMaxAudioBW = 25;
//    private boolean videoCallEnabled;
//    /**
//     * list of the streams
//     */
//    ConcurrentHashMap<String, StreamDescriptionInterface> mRemoteStream = new ConcurrentHashMap<>();
//    /**
//     * list of the streams
//     */
//    HashMap<String, StreamDescription> mLocalStream = new HashMap<String, StreamDescription>();
//    String mLocalStreamId;
//    /**
//     * current room id
//     */
//    String mRoomId;
//    /**
//     * list of all current observers
//     */
//    ConcurrentLinkedQueue<RoomObserver> mObservers = new ConcurrentLinkedQueue<LicodeConnector.RoomObserver>();
//    /**
//     * local video stream
//     */
//    private VideoSource mVideoSource;
//    /**
//     * local video capturer
//     */
//    private VideoCapturer mVideoCapturer;
//    AppRTCAudioManager audioManager;
//    /**
//     * if local video stream was paused
//     */
//    private boolean mVideoStopped = false;
//    /**
//     * factory for peer connections
//     */
//    private static PeerConnectionFactory sFactory;
//    /**
//     * list of stun and turn servers available for all connections
//     */
//    volatile ArrayList<PeerConnection.IceServer> mIceServers = new ArrayList<PeerConnection.IceServer>();
//    /**
//     * the handler for the special video chat thread
//     */
//    private static Handler sVcHandler = null;
//    static boolean createPeerConnectionPost = true;
//    /**
//     * special lock object when accessing the vc handler instance
//     */
//    private static Object sVcLock = new Object();
//    /**
//     * server confirmed rights
//     */
//    private boolean mPermissionPublish, mPermissionSubscribe;
//    StreamDescription localStream;
//    SessionDescription localSessionDescription;
//    boolean isProbeEnd = false;
//    int probeNet = 1;
//    ConcurrentHashMap<String, StreamDescriptionInterface> mNeedSubscribe = new ConcurrentHashMap<>();
//    public static LogCallback logCallback;
//
//    /**
//     * helper class - runnable that can be cancelled
//     */
//    private static interface CancelableRunnable extends Runnable {
//        /**
//         * cancels the runnable
//         */
//        void cancel();
//    }
//
//    /**
//     * refresh token runnable
//     */
//    private CancelableRunnable mRefreshTokenRunnable;
//
//
//    public interface LogCallback {
//        void i(String tag, String msg);
//
//        void e(String tag, String msg, Throwable tr);
//    }
//
//    /**
//     * may or may not provide logging output - as desired
//     */
//    static void log(String s) {
//        if (logCallback != null) {
//            logCallback.i(TAG, s);
//        } else {
//            Log.i(TAG, "" + s);
//        }
//    }
//
//    static void log(String s, Throwable t) {
//        if (logCallback != null) {
//            logCallback.e(TAG, "" + s, t);
//        } else {
//            Log.e(TAG, "" + s, t);
//        }
//    }
//
//    EventCallback mOnAddStream = new EventCallback() {
//        @Override
//        public void onEvent(JSONArray args, Acknowledge ack) {
//            // [{"data":true,"id":331051653483882560,"screen":"","audio":true,"video":true}]
//            log("mOnAddStream:onEvent:args=" + args);
//
//            try {
//                StreamDescription stream = StreamDescription.parseJson(args
//                        .getJSONObject(0));
//
//                boolean isLocal = mLocalStream.get(stream.getId()) != null;
//                log("mOnAddStream:onEvent:isLocal=" + isLocal);
//                if (!isLocal) {
//                    mRemoteStream.put(stream.getId(), stream);
//                    triggerStreamAdded(stream);
//                }
//            } catch (JSONException e) {
//            }
//        }
//    };
//    EventCallback mOnSubscribeP2P = new EventCallback() {
//        @Override
//        public void onEvent(JSONArray args, Acknowledge ack) {
//            Log.i(TAG, "mOnSubscribeP2P:onEvent=" + args);
//            // not yet relevant
//        }
//    };
//    EventCallback mOnPublishP2P = new EventCallback() {
//        @Override
//        public void onEvent(JSONArray args, Acknowledge ack) {
//            // not yet relevant
//        }
//    };
//    EventCallback mOnDataStream = new EventCallback() {
//        @Override
//        public void onEvent(JSONArray args, Acknowledge ack) {
//            Log.i(TAG, "mOnDataStream");
//
//            try {
//                JSONObject param = args.getJSONObject(0);
//                String streamId = param.getString("id");
//                String message = param.getString("msg");
//                StreamDescriptionInterface stream = mRemoteStream.get(streamId);
//                for (RoomObserver obs : mObservers) {
//                    obs.onStreamData(message, stream);
//                }
//            } catch (JSONException e) {
//            }
//        }
//    };
//    EventCallback mOnRemoveStream = new EventCallback() {
//        @Override
//        public void onEvent(JSONArray args, Acknowledge ack) {
//            // [{"id":331051653483882560}]
//            log("mOnRemoveStream:args=" + args);
//
//            try {
//                JSONObject param = args.getJSONObject(0);
//                String streamId = param.getString("id");
//                StreamDescription stream = (StreamDescription) mRemoteStream
//                        .get(streamId);
//
//                if (stream != null) {
//                    removeStream(stream);
//                    mRemoteStream.remove(streamId);
//                    triggerStreamRemoved("mOnRemoveStream", stream);
//                }
//                log("mOnRemoveStream:end");
//            } catch (JSONException e) {
//                log("mOnRemoveStream", e);
//            }
//        }
//    };
//    EventCallback mDisconnect = new EventCallback() {
//        @Override
//        public void onEvent(JSONArray args, Acknowledge ack) {
//            log("mDisconnect");
//            disconnect("mDisconnect");
//        }
//    };
//
//    EventCallback signaling_message_erizo = new EventCallback() {
//        @Override
//        public void onEvent(JSONArray argument, Acknowledge acknowledge) {
//            Log.i(TAG, "signaling_message_erizo:argument=" + argument);
//            try {
//                JSONObject jsonObject = argument.getJSONObject(0);
//                final JSONObject messObject = jsonObject.getJSONObject("mess");
//                final String type = messObject.getString("type");
//                if ("answer".equals(type)) {
//                    if (jsonObject.has("streamId")) {
//                        String streamId = jsonObject.getString("streamId");
//                        Log.i(TAG, "signaling_message_erizo:streamId="
//                                + streamId + ",mLocalStream=" + mLocalStream.size());
//                        final StreamDescriptionInterface descriptionInterface = mLocalStream.get(streamId);
//                        Log.i(TAG, "signaling_message_erizo:descriptionInterface=null?"
//                                + (descriptionInterface == null));
//                        if (descriptionInterface != null) {
//                            final StreamDescription streamDescription = (StreamDescription) descriptionInterface;
//                            String sdp = messObject.getString("sdp");
//                            final SessionDescription sdpAnswer = new SessionDescription(
//                                    SessionDescription.Type.fromCanonicalForm(type), sdp);
//                            sVcHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    streamDescription.setRemoteDescription(streamDescription.sdpObserver, sdpAnswer);
////                                    streamDescription.setRemoteDescription(new LicodeSdpObserver(streamDescription, false), sdpAnswer);
//                                }
//                            });
//                        }
//                    } else if (jsonObject.has("peerId")) {
//                        String peerId = jsonObject.getString("peerId");
//                        String sdp = messObject.getString("sdp");
//                        final SessionDescription sdpAnswer = new SessionDescription(
//                                SessionDescription.Type.fromCanonicalForm(type), sdp);
//                        StreamDescriptionInterface descriptionInterface = mRemoteStream.get(peerId);
//                        if (descriptionInterface != null) {
//                            Log.i(TAG, "signaling_message_erizo(answer):setRemoteDescription");
//                            final StreamDescription streamDescription = (StreamDescription) descriptionInterface;
//                            sVcHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    streamDescription.setRemoteDescription(streamDescription.sdpObserver, sdpAnswer);
////                                    streamDescription.setRemoteDescription(new LicodeSdpObserver(streamDescription, false), sdpAnswer);
//                                }
//                            });
//                        } else {
//                            Log.i(TAG, "signaling_message_erizo:setRemoteDescription:peerId="
//                                    + peerId + ",mRemoteStream=" + mRemoteStream.size());
//                        }
//                    }
//                } else if ("started".equals(type)) {
//                    StreamDescriptionInterface descriptionInterface;
//                    if (jsonObject.has("peerId")) {
//                        String peerId = jsonObject.getString("peerId");
//                        descriptionInterface = mRemoteStream.get(peerId);
//                    } else {
//                        String streamId = jsonObject.getString("streamId");
//                        descriptionInterface = mLocalStream.get(streamId);
//                    }
////                                                Log.i(TAG, "signaling_message_erizo(started):descriptionInterface=null?" + (descriptionInterface == null));
//                }
//            } catch (JSONException e) {
//                log("signaling_message_erizo", e);
//            }
//        }
//    };
//    long lastReport;
//
//    /**
//     * peer connection observer
//     */
//    private class MyPcObserver implements PeerConnection.Observer {
//        /**
//         * the associated sdp observer
//         */
//        private LicodeSdpObserver mSdpObserver;
//        /**
//         * stream description
//         */
//        private StreamDescriptionInterface mDesc;
//        IceConnectionState iceConnectionState;
//
//        public MyPcObserver(LicodeSdpObserver observer,
//                            StreamDescriptionInterface desc) {
//            mSdpObserver = observer;
//            mDesc = desc;
//        }
//
//        public LicodeSdpObserver getSdpObserver() {
//            return mSdpObserver;
//        }
//
//        @Override
//        public void onSignalingChange(SignalingState arg0) {
//            Log.i(TAG, "MyPcObserver:onSignalingChange:arg0=" + arg0 + ",isLocal=" + mDesc.isLocal());
//        }
//
//        @Override
//        public void onRemoveStream(MediaStream arg0) {
//            Log.i(TAG, "MyPcObserver:onRemoveStream:onRemoveStream");
//            // stream gone?
//        }
//
//        @Override
//        public void onIceGatheringChange(IceGatheringState iceGatherState) {
//            log("MyPcObserver:isLocal=" + mDesc.isLocal() + ",onIceGatheringChange=" + iceGatherState);
//            if (iceGatherState == IceGatheringState.COMPLETE) {
//                mSdpObserver.iceReady();
//            }
//        }
//
//        @Override
//        public void onIceConnectionChange(IceConnectionState newState) {
//            final StreamDescription streamDescription = (StreamDescription) mDesc;
//            Log.d(TAG, "MyPcObserver:onIceConnectionChange:newState=" + newState + ",isLocal=" + streamDescription.isLocal() + ",nick=" + streamDescription.getNick());
//            iceConnectionState = newState;
//            if (newState == IceConnectionState.CONNECTED) {
//                if ("admin".equals(streamDescription.getRole())) {
//                    Log.d(TAG, "MyPcObserver:onIceConnectionChange:admin");
//                    if (!BuildConfig.DEBUG) {
//                        return;
//                    }
//                }
//                sVcHandler.post(new Runnable() {
//                    String lastvalue = "0";
//                    int maxValue = 0;
//
//                    @Override
//                    public void run() {
//                        if (iceConnectionState == IceConnectionState.CLOSED || streamDescription.getPeerConnection() == null) {
//                            return;
//                        }
//                        AudioTrack audioTrack = null;
//                        if (streamDescription.getMedia().audioTracks.size() > 0) {
//                            audioTrack = streamDescription.getMedia().audioTracks.get(0);
//                        }
//                        streamDescription.getPeerConnection().getStats(new StatsObserver() {
//                            @Override
//                            public void onComplete(StatsReport[] reports) {
//                                for (StatsReport report : reports) {
////                                   type ssrc
////                                    id ssrc_1684030328_recv
//                                    if (report.type.equals("ssrc") && report.id.contains("ssrc") && report.id.contains("recv")) {
//                                        StatsReport.Value[] values = report.values;
//                                        //name audioOutputLevel
//                                        for (StatsReport.Value value : values) {
//                                            if ("audioOutputLevel".equals(value.name)) {
//                                                if (Integer.parseInt(value.value) > maxValue) {
//                                                    maxValue = Integer.parseInt(value.value);
//                                                    Log.i(TAG, "onIceConnectionChange:audioOutputLevel=" + value.value + "," + Math.sqrt(Integer.parseInt(value.value)));
//                                                }
//                                                lastvalue = value.value;
//                                                for (RoomObserver obs : mObservers) {
//                                                    obs.audioOutputLevel(streamDescription.getId(), value.value);
//                                                }
//                                                return;
//                                            }
//                                        }
//                                        break;
//                                        //ssrc_3590971819_send
//                                    } else if (report.type.equals("ssrc") && report.id.contains("ssrc") && report.id.contains("send")) {
//                                        StatsReport.Value[] values = report.values;
//                                        for (StatsReport.Value value : values) {
//                                            if ("audioInputLevel".equals(value.name)) {
//                                                if (Integer.parseInt(value.value) > maxValue) {
//                                                    maxValue = Integer.parseInt(value.value);
//                                                    Log.i(TAG, "onIceConnectionChange:audioInputLevel=" + value.value + "," + Math.sqrt(Integer.parseInt(value.value)));
//                                                }
//                                                lastvalue = value.value;
//                                                for (RoomObserver obs : mObservers) {
//                                                    obs.audioOutputLevel(streamDescription.getId(), value.value);
//                                                }
//                                                return;
//                                            }
//                                        }
//                                        break;
//                                    }
//                                }
//                                lastvalue = "0";
//                                for (RoomObserver obs : mObservers) {
//                                    obs.audioOutputLevel(streamDescription.getId(), "0");
//                                }
//                            }
//                        }, audioTrack);
//                        if ("0".equals(lastvalue)) {
//                            sVcHandler.postDelayed(this, 1000);
//                        } else {
//                            sVcHandler.postDelayed(this, 150);
//                        }
//                    }
//                });
//            } else if (newState == IceConnectionState.DISCONNECTED) {
//
//            } else if (newState == IceConnectionState.FAILED) {
//
//            } else {
//
//            }
//        }
//
//        @Override
//        public void onIceConnectionReceivingChange(boolean receiving) {
////            log("MyPcObserver:onIceConnectionReceivingChange:receiving=" + receiving);
//        }
//
//        @Override
//        public void onIceCandidate(final IceCandidate iceCandidate) {
//            sVcHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mSdpObserver.queuedRemoteCandidates != null) {
//                        log("MyPcObserver:onIceCandidate:queuedRemoteCandidates.add");
//                        mSdpObserver.queuedRemoteCandidates.add(iceCandidate);
//                    } else {
//                        log("MyPcObserver:onIceCandidate:getPeerConnection.add");
//                        StreamDescription streamDescription = (StreamDescription) mDesc;
//                        if (streamDescription.getPeerConnection() != null) {
//                            streamDescription.getPeerConnection().addIceCandidate(iceCandidate);
//                        }
//                    }
//                }
//            });
////            final StreamDescription streamDescription = (StreamDescription) mDesc;
////            boolean add = streamDescription.pc.addIceCandidate(iceCandidate);
////            log("onIceCandidate:add=" + add + ",iceCandidate=" + iceCandidate.sdp);
//            JSONObject json = new JSONObject();
//            jsonPut(json, "type", "candidate");
////            jsonPut(json, "sdpMLineIndex", iceCandidate.sdpMLineIndex);
////            jsonPut(json, "sdpMid", iceCandidate.sdpMid);
////            jsonPut(json, "candidate", iceCandidate.sdp);
//            JSONObject jsonObject = new JSONObject();
//            try {
//                JSONObject candidateObject = new JSONObject();
//                candidateObject.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
//                candidateObject.put("sdpMid", iceCandidate.sdpMid);
//                candidateObject.put("candidate", iceCandidate.sdp);
//                json.put("candidate", candidateObject);
//                jsonObject.put("msg", json);
//                String streamId;
//                if (mSdpObserver.mStream.isLocal()) {
//                    streamId = mLocalStreamId;
//                } else {
//                    streamId = mSdpObserver.mStream.getId();
//                }
//                jsonObject.put("streamId", streamId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
////            JSONArray jsonArgs = new JSONArray();
////            jsonArgs.put(jsonObject);
////            sendMessageSocket("signaling_message", jsonObject, new Acknowledge() {
////                @Override
////                public void acknowledge(JSONArray arguments) {
////                    log("onIceCandidate:acknowledge:arguments=" + arguments);
////                }
////            });
//            sendSDPSocket("signaling_message", jsonObject, null, new Acknowledge() {
//                @Override
//                public void acknowledge(JSONArray arguments) {
//                    log("onIceCandidate:acknowledge:arguments=" + arguments);
//                }
//            });
////            sendSDPSocket(mSdpObserver.mSignalChannel, jsonObject, jsonObject, new Acknowledge() {
////                @Override
////                public void acknowledge(JSONArray arguments) {
////                    log("onIceCandidate:acknowledge:arguments=" + arguments);
////                }
////            });
////            sendMessageSocket("signaling_message", jsonObject, new Acknowledge() {
////                @Override
////                public void acknowledge(JSONArray arguments) {
////                    log("onIceCandidate:acknowledge:arguments=" + arguments);
////                }
////            });
//        }
//
//        @Override
//        public void onIceCandidatesRemoved(IceCandidate[] candidates) {
//            log("MyPcObserver:onIceCandidatesRemoved:candidates=" + candidates.length);
//            StreamDescription streamDescription = (StreamDescription) mDesc;
//            PeerConnection pc = streamDescription.getPeerConnection();
//            if (pc != null) {
//                if (mSdpObserver.queuedRemoteCandidates != null) {
//                    for (IceCandidate candidate : mSdpObserver.queuedRemoteCandidates) {
//                        pc.addIceCandidate(candidate);
//                    }
//                    mSdpObserver.queuedRemoteCandidates = null;
//                }
//                pc.removeIceCandidates(candidates);
//            }
//        }
//
//        @Override
//        public void onDataChannel(DataChannel arg0) {
//        }
//
//        @Override
//        public void onAddStream(final MediaStream media) {
//            Log.i(TAG, "MyPcObserver:onAddStream:isLocal=" + mSdpObserver.isLocal());
////            AudioManager audioManager = ((AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE));
////            audioManager.setMode(AudioManager.MODE_IN_CALL);
////            audioManager.setSpeakerphoneOn(true);
////            audioManager.setMicrophoneMute(true);
//            if (mSdpObserver.isLocal()) {
//                return;
//            }
//            sVcHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mDesc != null) {
//                        StreamDescription streamDescription = (StreamDescription) mDesc;
//                        streamDescription.setMedia(media);
//                        Log.i(TAG, "onAddStream:video=" + media.videoTracks.size() + ",audio=" + media.audioTracks.size());
//                        if (media.videoTracks.size() > 0) {
//                            media.videoTracks.get(0).setEnabled(true);
//                        }
//                        if (media.audioTracks.size() > 0) {
//                            media.audioTracks.get(0).setEnabled(true);
//                        }
//                        boolean add = streamDescription.getPeerConnection().addStream(media);
//                        Log.i(TAG, "onAddStream:mId=" + streamDescription.getId() + ",add=" + add);
//                        triggerMediaAvailable(mDesc);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onRenegotiationNeeded() {
//            log("MyPcObserver:PeerConnectionObserver.onRenegotiationNeeded");
//        }
//
//        @Override
//        public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
//            log("MyPcObserver:onAddTrack");
//        }
//    }
//
//    ;
//
//    /**
//     * context/activity
//     */
//    private volatile Activity mActivity;
//    /**
//     * local media stream
//     */
//    private MediaStream lMS;
//    /**
//     * the currently active nick
//     */
//    private String mNick;
//    PeerConnectionParameters peerConnectionParameters;
//    VideoFileRenderer videoFileRenderer;
//    ProxyRenderer remoteProxyRenderer;
//    EglBase.Context sharedContext;
//
//    public LicodeConnector() {
//        WebRtcAudioRecord.webRtcAudioRecordCreate = new WebRtcAudioRecord.OnWebRtcAudioRecordCreate() {
//            @Override
//            public void onWebRtcAudioRecordCreate(WebRtcAudioRecord webRtcAudioRecord) {
//                Log.i(TAG, "onWebRtcAudioRecordCreate");
//                webRtcAudioRecord.setErrorCallback(new WebRtcAudioRecord.WebRtcAudioRecordErrorCallback() {
//
//                    @Override
//                    public void onWebRtcAudioRecordInitError(String errorMessage) {
//                        Log.i(TAG, "onWebRtcAudioRecordInitError:errorMessage=" + errorMessage);
//                    }
//
//                    @Override
//                    public void onWebRtcAudioRecordStartError(String errorMessage) {
//                        Log.i(TAG, "onWebRtcAudioRecordStartError:errorMessage=" + errorMessage);
//                    }
//
//                    @Override
//                    public void onWebRtcAudioRecordError(String errorMessage) {
//                        Log.i(TAG, "onWebRtcAudioRecordError:errorMessage=" + errorMessage);
//                    }
//                });
//            }
//        };
//    }
//
//    public void setSharedContext(EglBase.Context sharedContext) {
//        this.sharedContext = sharedContext;
//    }
//
//    public void setRemoteProxyRenderer(ProxyRenderer remoteProxyRenderer) {
//        this.remoteProxyRenderer = remoteProxyRenderer;
//    }
//
//    @Override
//    public void onPause() {
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mVideoSource != null) {
////                    mVideoSource.stop();
//                    mVideoStopped = true;
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onResume() {
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mVideoSource != null && mVideoStopped) {
////                    mVideoSource.restart();
//                    mVideoStopped = false;
//                }
//            }
//        });
//    }
//
//    @Override
//    public State getState() {
//        return mState;
//    }
//
//    @Override
//    public boolean isConnected() {
//        return mState == State.kConnected || mState == State.kConnecting;
//    }
//
//    public void init(Activity context, String nick, PeerConnectionParameters peerConnectionParameters) {
//        synchronized (sVcLock) {
//            if (sVcHandler == null) {
//                HandlerThread vcthread = new HandlerThread(
//                        "LicodeConnectorThread");
//                vcthread.start();
//                sVcHandler = new Handler(vcthread.getLooper());
////                vcthread.getLooper().setMessageLogging(new Printer() {
////                    @Override
////                    public void println(String x) {
////                        Log.i(TAG, "sVcHandler:println:x=" + x);
////                    }
////                });
//            }
//        }
//        if (context == null) {
//            throw new NullPointerException(
//                    "Failed to initialize LicodeConnector. Activity is required.");
//        }
//        mActivity = context;
//        mState = State.kDisconnected;
//        mNick = nick;
//        this.peerConnectionParameters = peerConnectionParameters;
//        videoCallEnabled = peerConnectionParameters.videoCallEnabled;
//        Runnable init = new Runnable() {
//            @Override
//            public void run() {
//                if (!sInitializedAndroidGlobals) {
//                    sInitializedAndroidGlobals = true;
//                    // newer libjingle versions have options for video and audio
//                    PeerConnectionFactory.initializeAndroidGlobals(mActivity, true, true, true);// ,
//                    // true,
//                    // true);
//                }
//                if (sFactory == null) {
//                    sFactory = new PeerConnectionFactory();
//                }
//            }
//        };
//        sVcHandler.post(init);
//    }
//
//    @Override
//    public void setBandwidthLimits(int video, int audio) {
//        mMaxVideoBW = video;
//        mMaxAudioBW = audio;
//    }
//
//    @Override
//    public void connect(final String token) {
//        if (mState == State.kUninitialized) {
//            return;
//        }
//        if (isConnected()) {
//            return;
//        }
//
//        mState = State.kConnecting;
//        mActivity.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                String createTokenError = createToken(token);
//                if (createTokenError != null) {
//                    for (RoomObserver obs : mObservers) {
//                        obs.onTokenError(createTokenError);
//                    }
//                }
//            }
//        });
//    }
//
//    /**
//     * sends a token - when required
//     */
//    public void refreshVideoToken(String token) {
//        token = LicodeConnector.decodeToken(token);
//        if (token == null) {
//            return;
//        }
//
//        try {
//            JSONObject jsonToken = new JSONObject(token);
//            handleTokenRefresh(jsonToken);
//
//            sendMessageSocket("refreshToken", jsonToken, new Acknowledge() {
//                @Override
//                public void acknowledge(JSONArray arg0) {
//                    // read publish right from result
//                    log("Refresh token Acknowledge: " + arg0.toString());
//                    parseVideoTokenResponse(arg0);
//
//                    if (mPermissionPublish) {
//                        triggerPublishAllowed();
//                    } else {
//                        unpublish();
//                    }
//                }
//            });
//        } catch (JSONException e) {
//        }
//    }
//
//    @Override
//    public void disconnect(String method) {
//        Log.i(TAG, "disconnect:method=" + method);
//        if (mState == State.kUninitialized || mState == State.kDisconnected
//                || mState == State.kDisconnecting) {
//            return;
//        }
//        if (mState == State.kConnecting) {
//            // TODO dk: figure out how to handle this!
//        }
//
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                doDisconnect();
//            }
//        });
//    }
//
//    /**
//     * handle actual disconnecting - from ui thread only
//     */
//    void doDisconnect() {
//        mState = State.kDisconnecting;
//        for (RoomObserver obs : mObservers) {
//            obs.onRoomDisconnected();
//        }
//        Set<String> keyset = mRemoteStream.keySet();
//        for (String key : keyset) {
//            StreamDescription stream = (StreamDescription) mRemoteStream
//                    .get(key);
//            removeStream(stream);
//            triggerStreamRemoved("doDisconnect", stream);
//        }
//        mRemoteStream.clear();
//
//        if (mLocalStream.size() > 0) {
//            unpublish();
//        }
//
//        synchronized (mSocketLock) {
//            if (mIoClient != null) {
//                mIoClient.disconnect();
//                mIoClient = null;
//            }
//        }
//
//        mState = State.kDisconnected;
//    }
//
//    /**
//     * handles time based refreshing of tokens - when they have a duration
//     */
//    void handleTokenRefresh(JSONObject jsonToken) {
//        int duration = 0;
//
//        try {
//            duration = jsonToken.getInt("duration");
//        } catch (JSONException e) {
//            duration = 0;
//        }
//
//        if (duration > 0) {
//            if (mRefreshTokenRunnable != null) {
//                mRefreshTokenRunnable.cancel();
//            }
//            mRefreshTokenRunnable = new CancelableRunnable() {
//                /**
//                 * keeps track if this is still to be run, or has been cancelled
//                 */
//                private volatile boolean mIsActive = true;
//
//                @Override
//                public void run() {
//                    if (!mIsActive) {
//                        return;
//                    }
//
//                    triggerRequestVideoToken();
//                }
//
//                @Override
//                public void cancel() {
//                    mIsActive = false;
//                }
//            };
//            long refreshTime = duration - 10;
//            if (refreshTime < 1) {
//                refreshTime = 1;
//            }
//            sVcHandler.postDelayed(mRefreshTokenRunnable, refreshTime * 1000L);
//        }
//    }
//
//    /**
//     * decodes a video token into a string which can then be turned into a json
//     * object, returns null on errors
//     */
//    private static final String decodeToken(String result) {
//        try {
//            String token = new String(Base64.decode(result.getBytes(),
//                    Base64.DEFAULT), "UTF-8");
//            log("Licode token decoded: " + token);
//            return token;
//        } catch (UnsupportedEncodingException e) {
//            log("Failed to decode token: " + e.getMessage());
//        }
//        return null;
//    }
//
//    /**
//     * called with the connection token
//     */
//    String createToken(String result) {
//        if (result == null) {
//            return "result == null";
//        }
//        final String token = LicodeConnector.decodeToken(result);
//        if (token == null) {
//            return "token == null";
//        }
//
//        try {
//            mRemoteStream.clear();
//            final JSONObject jsonToken = new JSONObject(token);
//            String host = jsonToken.getString("host");
//            if (host.startsWith("103.37.144.6")) {
//                host = "https://test.webrtc.xesv5.com:8080";
//            }
//            if (!host.startsWith("https://")) {
//                host = "https://" + host;
//            }
//            Log.i(TAG, "createToken:host=" + host);
//            handleTokenRefresh(jsonToken);
//            AsyncHttpClient asyncHttpClient = AsyncHttpClient.getDefaultInstance();
//            SpdyMiddleware sslSocketMiddleware = asyncHttpClient.getSSLSocketMiddleware();
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    Log.i(TAG, "createToken:getAcceptedIssuers");
//                    return new java.security.cert.X509Certificate[]{};
//                }
//
//                public void checkClientTrusted(X509Certificate[] chain,
//                                               String authType) throws CertificateException {
//                    Log.i(TAG, "createToken:checkClientTrusted");
//                }
//
//                public void checkServerTrusted(X509Certificate[] chain,
//                                               String authType) throws CertificateException {
//                    Log.i(TAG, "createToken:checkServerTrusted");
//                }
//            }};
////            sslSocketMiddleware.setTrustManagers(trustAllCerts);
////            sslSocketMiddleware.setHostnameVerifier(DO_NOT_VERIFY);
//            SocketIOClient.connect(asyncHttpClient, host,
//                    new ConnectCallback() {
//                        @Override
//                        public void onConnectCompleted(Exception err,
//                                                       SocketIOClient client) {
//                            if (err != null) {
//                                log("onConnectCompleted", err);
//                                for (RoomObserver obs : mObservers) {
//                                    obs.onRoomConnectedFail(err);
//                                }
//                                return;
//                            }
//
//                            try {
//                                // workaround - 2nd connection event
//                                JSONObject jsonParam = new JSONObject();
//                                jsonParam.put("reconnect", false);
//                                jsonParam.put("secure",
//                                        jsonToken.getBoolean("secure"));
//                                jsonParam.put("force new connection", true);
//
//                                JSONArray arg = new JSONArray();
//                                arg.put(jsonParam);
//                                client.emit("connection", arg, null);
//                            } catch (JSONException e) {
//                                log("onConnectCompleted", e);
//                            }
//                            log("sendMessageSocket:token");
//                            synchronized (mSocketLock) {
//                                mIoClient = client;
//                                client.setDisconnectCallback(new DisconnectCallback() {
//                                    @Override
//                                    public void onDisconnect(boolean fromRemote, Exception e) {
//                                        if (e == null) {
//                                            log("onDisconnect:fromRemote=" + fromRemote);
//                                        } else {
//                                            log("onDisconnect:e=" + e.getMessage());
//                                        }
//                                        for (RoomObserver obs : mObservers) {
//                                            obs.onDisconnect(fromRemote, e);
//                                        }
//                                    }
//                                });
//                                client.on("onAddStream", mOnAddStream);
//                                client.on("onSubscribeP2P", mOnSubscribeP2P);
//                                client.on("onPublishP2P", mOnPublishP2P);
//                                client.on("onDataStream", mOnDataStream);
//                                client.on("onRemoveStream", mOnRemoveStream);
//                                client.on("disconnect", mDisconnect);
//                                client.on("connection", new EventCallback() {
//
//                                    @Override
//                                    public void onEvent(JSONArray argument, Acknowledge acknowledge) {
//                                        log("onConnectCompleted:onEvent:arguments=" + argument);
//                                    }
//                                });
//                                client.on("signaling_message_erizo", signaling_message_erizo);
//                            }
//
//                            try {
//                                jsonToken.put("maxMember", 5);
//                                jsonToken.put("probeNet", probeNet);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            sendMessageSocket("token", jsonToken,
//                                    new Acknowledge() {
//                                        @Override
//                                        public void acknowledge(JSONArray result) {
//                                            log("onConnectCompleted:(token)acknowledge:result=" + result.toString());
//                                            try {
//                                                // ["success",{"maxVideoBW":300,"id":"5384684c918b864466c853d6","streams":[],"defaultVideoBW":300,"turnServer":{"password":"","username":"","url":""},"stunServerUrl":"stun:stun.l.google.com:19302"}]
//                                                // ["success",{"maxVideoBW":300,"id":"5384684c918b864466c853d6","streams":[{"data":true,"id":897203996079042600,"screen":"","audio":true,"video":true},{"data":true,"id":841680482029914900,"screen":"","audio":true,"video":true}],"defaultVideoBW":300,"turnServer":{"password":"","username":"","url":""},"stunServerUrl":"stun:stun.l.google.com:19302"}]
//                                                if (!"success".equalsIgnoreCase(result.getString(0))) {
//                                                    log("onConnectCompleted:acknowledg:!success");
//                                                    if (mIoClient != null) {
//                                                        mIoClient.disconnect();
//                                                        mIoClient = null;
//                                                    }
//                                                    return;
//                                                }
//
//                                                JSONObject jsonObject = result
//                                                        .getJSONObject(1);
//                                                parseVideoTokenResponse(result);
//
////                                                JSONArray iceServers = jsonObject.getJSONArray("iceServers");
////                                                for (int i = 0; i < iceServers.length(); i++) {
////                                                    JSONObject iceObj = iceServers.getJSONObject(i);
////                                                    mIceServers.add(new PeerConnection.IceServer(iceObj.getString("url")));
////                                                }
//                                                if (jsonObject
//                                                        .has("defaultVideoBW")) {
//                                                    mDefaultVideoBW = jsonObject
//                                                            .getInt("defaultVideoBW");
//                                                }
//                                                if (jsonObject
//                                                        .has("maxVideoBW")) {
//                                                    mMaxVideoBW = jsonObject
//                                                            .getInt("maxVideoBW");
//                                                }
//
//                                                mState = State.kConnected;
//
//                                                // update room id
//                                                mRoomId = jsonObject.getString("id");
//                                                log("onConnectCompleted:acknowledg:mObservers=" + mObservers.size());
//                                                for (RoomObserver obs : mObservers) {
//                                                    obs.onRoomConnected(mRemoteStream);
//                                                }
//
//                                                // retrieve list of streams
//                                                JSONArray streams = jsonObject
//                                                        .getJSONArray("streams");
//                                                log("onConnectCompleted:acknowledg:streams=" + streams.length());
//                                                for (int index = 0, n = streams.length(); index < n; ++index) {
//                                                    // {"data":true,"id":897203996079042600,"screen":"","audio":true,"video":true}
//                                                    JSONObject arg = streams
//                                                            .getJSONObject(index);
//                                                    StreamDescription stream = StreamDescription
//                                                            .parseJson(arg);
//                                                    mRemoteStream.put(stream.getId(), stream);
//                                                    triggerStreamAdded(stream);
//                                                }
//                                            } catch (JSONException e) {
//                                                log("onConnectCompleted:acknowledge", e);
//                                            }
//                                        }
//                                    });
//                        }
//                    });
//            return null;
//        } catch (JSONException e) {
//            return e.getMessage();
//        }
//    }
//
//    /**
//     * send a json something on the specified channel via socket.io
//     */
//    void sendMessageSocket(final String channel, Object param, Acknowledge ack) {
//        synchronized (mSocketLock) {
//            if (mIoClient == null) {
//                log("sendMessageSocket:mIoClient=null,channel=" + channel);
//                return;
//            }
//            JSONArray jsonArgs = new JSONArray();
//            jsonArgs.put(param);
//            if (ack == null) {
//                ack = new Acknowledge() {
//                    @Override
//                    public void acknowledge(JSONArray arg0) {
//                        log("LicodeConnector:channel=" + channel + " No one interested in response: "
//                                + arg0.toString());
//                    }
//                };
//            }
//            mIoClient.emit(channel, jsonArgs, ack);
//        }
//    }
//
//    void sendSDPSocket(String type, JSONObject param0, JSONObject param1,
//                       Acknowledge ack) {
//        synchronized (mSocketLock) {
//            if (mIoClient == null) {
//                log("sendSDPSocket:mIoClient=null?" + (mIoClient == null));
//                return;
//            }
//            JSONArray jsonArgs = new JSONArray();
//            jsonArgs.put(param0);
//            jsonArgs.put(param1);
//            mIoClient.emit(type, jsonArgs, ack);
//        }
//    }
//
//    void sendSDPSocket(String type, JSONArray params, Acknowledge ack) {
//        synchronized (mSocketLock) {
//            if (mIoClient == null) {
//                return;
//            }
//            mIoClient.emit(type, params, ack);
//        }
//    }
//
//    void sendDataSocket(String streamId, String message) {
//        JSONObject param = new JSONObject();
//        try {
//            param.put("id", streamId);
//            param.put("msg", message);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        sendMessageSocket("sendDataStream", param, null);
//    }
//
//    void removeStream(StreamDescription stream) {
//        stream.onClosing();
//        triggerStreamRemoved("removeStream", stream);
//    }
//
//    @Override
//    public void unsubscribe(String streamId) {
//        StreamDescription stream = (StreamDescription) mRemoteStream
//                .get(streamId);
//
//        if (stream != null) {
//            disable(stream);
//        }
//    }
//
//    @Override
//    public void addObserver(final RoomObserver observer) {
//        mObservers.add(observer);
//
//        if (isConnected()) {
//            mActivity.getWindow().getDecorView().post(new Runnable() {
//                @Override
//                public void run() {
//                    observer.onRoomConnected(mRemoteStream);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void removeObserver(RoomObserver observer) {
//        mObservers.remove(observer);
//    }
//
//    /**
//     * get access to the camera
//     */
//    private VideoCapturer getVideoCapturer() {
////        String[] cameraFacing = {"front", "back"};
////        int[] cameraIndex = {0, 1};
////        int[] cameraOrientation = {0, 90, 180, 270};
////        for (String facing : cameraFacing) {
////            for (int index : cameraIndex) {
////                for (int orientation : cameraOrientation) {
////                    String name = "Camera " + index + ", Facing " + facing
////                            + ", Orientation " + orientation;
////                    VideoCapturer capturer = VideoCapturer.create(name);
////                    if (capturer != null) {
////                        log("getVideoCapturer:name=" + name);
////                        return capturer;
////                    }
////                }
////            }
////        }
//        throw new RuntimeException("Failed to open capturer");
//    }
//
//    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
//        final String[] deviceNames = enumerator.getDeviceNames();
//
//        // First, try to find front facing camera
//        log("Looking for front facing cameras.");
//        for (String deviceName : deviceNames) {
//            if (enumerator.isFrontFacing(deviceName)) {
//                log("Creating front facing camera capturer.");
//                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
//
//                if (videoCapturer != null) {
//                    return videoCapturer;
//                }
//            }
//        }
//
//        // Front facing camera not found, try something else
//        log("Looking for other cameras.");
//        for (String deviceName : deviceNames) {
//            if (!enumerator.isFrontFacing(deviceName)) {
//                log("Creating other camera capturer.");
//                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
//
//                if (videoCapturer != null) {
//                    return videoCapturer;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private class LicodeSdpObserver implements SdpObserver {
//        private LinkedList<IceCandidate> queuedRemoteCandidates;
//        /**
//         * the sdp created locally
//         */
//        SessionDescription mLocalSdp = null;
//        /**
//         * whether or not this is a publish attempt
//         */
//        boolean mIsPublish = false;
//        /**
//         * the associated stream
//         */
//        final StreamDescription mStream;
//        /**
//         * tracks if ice candidates are all collected
//         */
//        boolean mIceReady = false;
//        /**
//         * the current signalling channel on socket.io
//         */
//        String mSignalChannel = "subscribe";
//
//        /**
//         * create an observer for given stream
//         */
//        LicodeSdpObserver(StreamDescription stream, boolean publishing) {
//            mStream = stream;
//            mIsPublish = publishing;
//            mSignalChannel = mIsPublish ? "publish" : "subscribe";
//            queuedRemoteCandidates = new LinkedList<IceCandidate>();
//        }
//
//        public boolean isLocal() {
//            return mStream == null ? false : mStream.isLocal();
//        }
//
//        /**
//         * waits for ice candidates to be gathered before triggering release
//         */
//        public void iceReady() {
//            log("LicodeSdpObserver:iceReady");
//            mIceReady = true;
//            startConnecting();
//        }
//
//        private void startConnecting() {
//            log("LicodeSdpObserver:startConnecting:mStream=" + mStream.getId());
//            mStream.getPeerConnection().createOffer(this, mStream.sdpConstraints());
//        }
//
//        @Override
//        public void onCreateFailure(String arg0) {
//            log("LicodeSdpObserver:onCreateFailure:arg0=" + arg0);
//        }
//
////        private SessionDescription modifySdpMaxBW(SessionDescription sdp) {
////            StringBuffer desc = new StringBuffer();
////            int audioLine = -1;
////            int videoLine = -1;
////            ArrayList<Integer> bLines = new ArrayList<Integer>();
////            String[] lines = sdp.description.split("\r\n");
////            for (int i = 0; i < lines.length; ++i) {
////                if (lines[i].startsWith("m=audio")) {
////                    audioLine = i;
////                } else if (lines[i].startsWith("m=video")) {
////                    videoLine = i;
////                } else if (lines[i].startsWith("b=AS:")) {
////                    bLines.add(i);
////                }
////            }
////            // TODO dk: this may want to check for existing B-Lines!
////            boolean addVideoB = mMaxVideoBW > 0;
////            boolean addAudioB = mMaxAudioBW > 0;
////            for (int i = 0; i < lines.length; ++i) {
////                desc.append(lines[i]);
////                desc.append("\r\n");
////                if (i == audioLine && addAudioB) {
////                    desc.append("b=AS:" + mMaxAudioBW + "\r\n");
////                } else if (i == videoLine && addVideoB) {
////                    desc.append("b=AS:" + mMaxVideoBW + "\r\n");
////                }
////            }
////
////            return new SessionDescription(sdp.type, desc.toString());
////        }
//
//        @Override
//        public void onCreateSuccess(SessionDescription origSdp) {
//            if (mLocalSdp != null) {
//                Log.i(TAG, "LicodeSdpObserver:onCreateSuccess:return");
//                return;
//            }
//            Log.i(TAG, "LicodeSdpObserver:onCreateSuccess:mIceReady=" + mIceReady);
//            final boolean isLocal = isLocal();
//            Log.i(TAG, "LicodeSdpObserver:onCreateSuccess:mStream=" + mStream.getId() + ",isLocal=" + isLocal + ",origSdp=" + origSdp.type);
////            Log.i(TAG, "onCreateSuccess:origSdp=" + origSdp.type + ",description=" + origSdp.description);
//            if (mIceReady) {
//                mLocalSdp = origSdp;
//            }
////            mLocalSdp = origSdp;
//
//            String sdpDescription = origSdp.description;
//            final SessionDescription finalSdp = new SessionDescription(origSdp.type, sdpDescription);
////            final SessionDescription finalSdp = modifySdpMaxBW(origSdp);
//
////            if (isLocal) {
////                localSessionDescription = finalSdp;
////            }
//            localSessionDescription = finalSdp;
//            sVcHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mStream.getState() == StreamDescriptionInterface.StreamState.DESTROYED) {
//                        log("LicodeSdpObserver:onCreateSuccess:pc=DESTROYED");
//                    } else {
//                        log("LicodeSdpObserver:onCreateSuccess:setLocal:isLocal=" + isLocal + ",start");
////                        mStream.setLocalDescription(new WrapSdpObserver(LicodeSdpObserver.this), finalSdp);
//                        if (isLocal) {
//                            mStream.setLocalDescription(new WrapSdpObserver(LicodeSdpObserver.this), finalSdp);
//                        } else {
//                            mStream.setLocalDescription(new WrapSdpObserver(LicodeSdpObserver.this), finalSdp);
//                        }
//                        log("LicodeSdpObserver:onCreateSuccess:setLocal:isLocal=" + isLocal + ",end");
////                        mStream.pc.setLocalDescription(new LicodeSdpObserver(mStream, false), finalSdp);
////                        if (mIsPublish) {
////                            mStream.pc.setLocalDescription(new LicodeSdpObserver(mStream, false), finalSdp);
////                        } else {
////                            mStream.pc.setRemoteDescription(new LicodeSdpObserver(mStream, false), finalSdp);
////                        }
////                        if (mStream.pc.getRemoteDescription() == null) {
////                            Log.i(TAG, "onCreateSuccess:setLocalDescription");
////                            mStream.pc.setLocalDescription(LicodeSdpObserver.this,
////                                    finalSdp);
//////                        sendLocalDescription(finalSdp);
////                        } else {
////                            Log.i(TAG, "onCreateSuccess:getRemoteDescription=null");
////                        }
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onSetFailure(String arg0) {
//            log("LicodeSdpObserver:onSetFailure:mIsPublish=" + mIsPublish + ",arg0=" + arg0);
//        }
//
//        @Override
//        public void onSetSuccess() {
//            log("LicodeSdpObserver:onSetSuccess:this=" + hashCode() + ",mStream.getId=" + mStream.getId() + ",mLocalSdp=null?" + (mLocalSdp == null));
//            if (mLocalSdp == null) {
//                return;
//            }
////            Log.i(TAG, "onLocalDescription:sdp=" + mLocalSdp.description);
//            sVcHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mStream.getState() == StreamDescriptionInterface.StreamState.DESTROYED) {
//                        log("onSetSuccess:DESTROYED");
//                        return;
//                    }
//                    SessionDescription sessionDescription = mStream.getPeerConnection().getRemoteDescription();
//                    log("onSetSuccess:getRemoteDescription=null?" + (sessionDescription == null) + ",setRemote=" + mStream.setRemote);
//                    if (sessionDescription == null) {
////                        mLocalStream.put(mStream.getId(), mLocalSdp);
//                        sendLocalDescription(mLocalSdp);
////                        SessionDescription sdpAnswer = new SessionDescription(SessionDescription.Type.ANSWER, mLocalSdp.description);
////                        Log.i(TAG, "sendLocalDescription:setRemoteDescription3");
////                        mStream.pc.setRemoteDescription(LicodeSdpObserver.this, sdpAnswer);
//                    } else {
//                        // drain remote candidates?!
//                        // also confirm exchange with licode server!
////                        sendConfirmation();
//                        if (queuedRemoteCandidates != null) {
//                            log("onSetSuccess:queuedRemoteCandidates=" + queuedRemoteCandidates.size());
//                            for (IceCandidate candidate : queuedRemoteCandidates) {
//                                mStream.getPeerConnection().addIceCandidate(candidate);
//                            }
//                            queuedRemoteCandidates = null;
//                        }
//                    }
//                }
//            });
////            mStream.pc.createAnswer(new SdpObserver() {
////                @Override
////                public void onCreateSuccess(SessionDescription sdp) {
////                    Log.i(TAG, "onSetSuccess(createAnswer):onCreateSuccess");
////                }
////
////                @Override
////                public void onSetSuccess() {
////                    Log.i(TAG, "onSetSuccess(createAnswer):onSetSuccess");
////                }
////
////                @Override
////                public void onCreateFailure(String error) {
////                    Log.i(TAG, "onSetSuccess(createAnswer):onCreateFailure:error=" + error);
////                }
////
////                @Override
////                public void onSetFailure(String error) {
////                    Log.i(TAG, "onSetSuccess(createAnswer):onSetFailure:error=" + error);
////                }
////            }, mStream.sdpConstraints());
//        }
//
////        void sendRemoteDescription(SessionDescription origSdp) {
////            try {
////                JSONObject jsonObject = new JSONObject();
////                JSONObject msgObj = new JSONObject();
////                msgObj.put("sdp", origSdp.description);
////                msgObj.put("type", "ok");
////                jsonObject.put("streamId", mStream.getId());
////                jsonObject.put("msg", msgObj);
////                sendSDPSocket("signaling_message", jsonObject, null, new Acknowledge() {
////                    @Override
////                    public void acknowledge(JSONArray arguments) {
////                        log("onCreateSuccess:acknowledge2:arguments=" + arguments);
////                    }
////                });
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////        }
//
//        void sendLocalDescription(SessionDescription origSdp) {
//            try {
//                JSONObject jsonObject = new JSONObject();
//                JSONObject msgObj = new JSONObject();
//                msgObj.put("sdp", origSdp.description);
//                msgObj.put("type", "offer");
//                String streamId;
//                if (mStream.isLocal()) {
//                    streamId = mLocalStreamId;
//                    jsonObject.put("streamId", streamId);
//                } else {
//                    streamId = mStream.getId();
//                    jsonObject.put("streamId", streamId);
//                }
//                jsonObject.put("msg", msgObj);
//                log("sendLocalDescription:streamId=" + streamId);
//                sendSDPSocket("signaling_message", jsonObject, null, new Acknowledge() {
//                    @Override
//                    public void acknowledge(JSONArray arguments) {
//                        log("sendLocalDescription:acknowledge2:arguments=" + arguments);
//                    }
//                });
////                sendMessageSocket("signaling_message", jsonObject, new Acknowledge() {
////                    @Override
////                    public void acknowledge(JSONArray arguments) {
////                        log("sendLocalDescription:acknowledge3:arguments=" + arguments);
////                    }
////                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
////        void sendLocalDescription2() {
////            JSONObject desc = null;
////            if (mIsPublish) {
//////                desc = mStream.toJsonOffer("erizo");
////                desc = mStream.toJsonOffer("offer");
////            } else {
////                desc = mStream.toJsonOffer(null);
////                try {
////                    desc.put("streamId", mStream.getId());
////                } catch (JSONException e) {
////                }
////            }
////            JSONObject p1 = new JSONObject();
////            try {
////                p1.put("messageType", "offer");
////                p1.put("sdp", mLocalSdp.description);
////                p1.put("tiebreaker",
////                        (int) (Math.random() * (Integer.MAX_VALUE - 2)) + 1);
////                p1.put("offererSessionId", mOffererSessionId); // hardcoded in
////                // Licode?
////                p1.put("seq", 1); // should not be hardcoded, but works for now
////            } catch (JSONException e) {
////            }
////            final String signalChannel = mSignalChannel;
////            log("SdpObserver#sendLocalDescription; to: " + mSignalChannel + ",desc=" + desc
////                    + "; msg: " + p1.toString());
////            sendSDPSocket(mSignalChannel, desc, p1, new Acknowledge() {
////                @Override
////                public void acknowledge(JSONArray arg0) {
////                    log("SdpObserver.sendLocalDescription:acknowledge:signalChannel=" + signalChannel + ",arg0=" + arg0);
//////                    SessionDescription sdpAnswer = new SessionDescription(
//////                            SessionDescription.Type.fromCanonicalForm("answer"), mLocalSdp.description);
//////                    mStream.pc.setRemoteDescription(new SdpObserver() {
//////                        @Override
//////                        public void onCreateSuccess(SessionDescription sessionDescription) {
//////                            log("SdpObserver.sendLocalDescription:onCreateSuccess");
//////                        }
//////
//////                        @Override
//////                        public void onSetSuccess() {
//////                            log("SdpObserver.sendLocalDescription:onSetSuccess");
//////                        }
//////
//////                        @Override
//////                        public void onCreateFailure(String s) {
//////                            log("SdpObserver.sendLocalDescription:onCreateFailure:s=" + s);
//////                        }
//////
//////                        @Override
//////                        public void onSetFailure(String s) {
//////                            log("SdpObserver.sendLocalDescription:onSetFailure:s=" + s);
//////                        }
//////                    },sdpAnswer);
//////                    try {
//////                        String string = arg0.getString(0);
//////                        log("SdpObserver.sendLocalDescription:acknowledge:string=" + string);
//////                        if (!"true".equals(string)) {
//////                            SessionDescription remoteSdp = new SessionDescription(SessionDescription.Type.ANSWER,
//////                                    arg0.getString(0));
//////                            log("SdpObserver.sendLocalDescription:acknowledge:setRemoteDescription");
//////                            mStream.pc.setRemoteDescription(
//////                                    new SdpObserver() {
//////                                        @Override
//////                                        public void onCreateSuccess(SessionDescription sessionDescription) {
//////                                            log("SdpObserver.sendLocalDescription:onCreateSuccess");
//////                                        }
//////
//////                                        @Override
//////                                        public void onSetSuccess() {
//////                                            log("SdpObserver.sendLocalDescription:onSetSuccess");
//////                                        }
//////
//////                                        @Override
//////                                        public void onCreateFailure(String s) {
//////                                            log("SdpObserver.sendLocalDescription:onCreateFailure:s=" + s);
//////                                        }
//////
//////                                        @Override
//////                                        public void onSetFailure(String s) {
//////                                            log("SdpObserver.sendLocalDescription:onSetFailure:s=" + s);
//////                                        }
//////                                    }, remoteSdp);
//////                        }
//////                    } catch (JSONException e) {
//////                        log("SdpObserver.sendLocalDescription:acknowledge", e);
//////                    }
////                    JSONObject json = new JSONObject();
////                    JSONObject jsonObject = new JSONObject();
////                    try {
////                        json.put("messageType", "offer");
////                        json.put("sdp", mLocalSdp.description);
////                        jsonObject.put("msg", json);
////                        jsonObject.put("streamId", mStream.getId());
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                    JSONArray jsonArgs = new JSONArray();
////                    jsonArgs.put(jsonObject);
////                    sendMessageSocket("signaling_message", jsonArgs, new Acknowledge() {
////                        @Override
////                        public void acknowledge(JSONArray arguments) {
////                            log("SdpObserver.sendLocalDescription2:acknowledge:signalChannel=" + signalChannel + ",arguments=" + arguments);
////                        }
////                    });
//////                    sendSDPSocket(mSignalChannel, jsonArgs, new Acknowledge() {
//////                        @Override
//////                        public void acknowledge(JSONArray arguments) {
//////                            log("SdpObserver.sendLocalDescription2:acknowledge:signalChannel=" + signalChannel + ",arguments=" + arguments);
//////                        }
//////                    });
//////                    String streamId = null;
//////                    SessionDescription remoteSdp;
//////                    try {
//////                        // log(arg0.getString(0));
//////                        // JSONObject jsonAnswer = arg0.getJSONObject(0);
//////                        // licode server sends answer as string which is
//////                        // basically a json string, though
//////                        JSONObject jsonAnswer = new JSONObject(arg0
//////                                .getString(0));
//////                        boolean answer = "ANSWER".equals(jsonAnswer
//////                                .getString("messageType"));
//////                        if (!answer) {
//////                            log("SdpObserver: expected ANSWER, got: "
//////                                    + jsonAnswer.getString("messageType"));
//////                        }
//////                        remoteSdp = new SessionDescription(Type.ANSWER,
//////                                jsonAnswer.getString("sdp"));
//////
//////                        if (mIsPublish) {
//////                            streamId = arg0.getString(1);
//////                        }
//////
//////                        mAnswererSessionId = jsonAnswer
//////                                .getInt("answererSessionId");
//////                    } catch (JSONException e1) {
//////                        Log.e(TAG, "SdpObserver:sendLocalDescription:acknowledge", e1);
//////                    }
////
//////                    if (mIsPublish) {
//////                        mStream.setId(streamId);
//////                        mLocalStream.put(streamId, mStream);
//////                    }
////
//////                    final SessionDescription finalRemoteSdp = remoteSdp;
//////                    if (finalRemoteSdp != null) {
//////                        mActivity.runOnUiThread(new Runnable() {
//////                            @Override
//////                            public void run() {
//////                                Log.i(TAG,"sendLocalDescription:setRemoteDescription1");
//////                                mStream.pc.setRemoteDescription(
//////                                        LicodeSdpObserver.this, finalRemoteSdp);
//////                            }
//////                        });
//////                    } else {
//////                        mActivity.runOnUiThread(new Runnable() {
//////                            @Override
//////                            public void run() {
//////                                SessionDescription remoteSdp = new SessionDescription(Type.ANSWER,
//////                                        mLocalSdp.description);
//////                                Log.i(TAG,"sendLocalDescription:setRemoteDescription2");
//////                                mStream.pc.setRemoteDescription(
//////                                        LicodeSdpObserver.this, remoteSdp);
//////                            }
//////                        });
//////                        Log.e(TAG, "SdpObserver:sendLocalDescription:finalRemoteSdp=null");
//////                    }
////                }
////            });
////        }
//
////        void sendConfirmation() {
////            JSONObject p0 = mStream.toJsonOffer("ok");
////            try {
////                p0.put("streamId", mStream.getId());
////                p0.put("messageType", "OK");
////                p0.put("offererSessionId", mOffererSessionId);
////                p0.put("answererSessionId", mAnswererSessionId);
////                p0.put("seq", 1);
////                // p0.put("sdp", " ");
////            } catch (JSONException e) {
////            }
////            log("sendConfirmation:p0=" + p0);
////            sendSDPSocket(mSignalChannel, p0, p0, new Acknowledge() {
////
////                @Override
////                public void acknowledge(JSONArray arguments) {
////                    log("sendConfirmation:arguments=" + arguments);
////                }
////            });
////        }
//    }
//
//    public MediaConstraints makePcConstraints() {
//        Log.i(TAG, "makePcConstraints");
//        MediaConstraints pcConstraints = new MediaConstraints();
////        pcConstraints.optional.add(new MediaConstraints.KeyValuePair(
////                "RtpDataChannels", "true"));
////        pcConstraints.optional.add(new MediaConstraints.KeyValuePair(
////                "EnableDtlsSrtp", "true"));
//        pcConstraints.optional.add(new MediaConstraints.KeyValuePair(
//                "DtlsSrtpKeyAgreement", "true"));
//        return pcConstraints;
//    }
//
//    @Override
//    public void publish() {
//        Log.i(TAG, "publish:mPermissionPublish=" + mPermissionPublish);
//        if (mPermissionPublish) {
//            sVcHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    doPublish();
//                }
//            });
//        }
//    }
//
//    void doPublish() {
//        if (mVideoCapturer != null) {
//            return;
//        }
//        if (videoCallEnabled) {
//            MediaConstraints videoConstraints = new MediaConstraints();
//            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                    "maxWidth", peerConnectionParameters.videoWidth + ""));
//            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                    "maxHeight", peerConnectionParameters.videoHeight + ""));
//            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                    "maxFrameRate", peerConnectionParameters.videoFps + ""));
//        }
//
//        MediaConstraints audioConstraints = new MediaConstraints();
//        audioConstraints.optional.add(new MediaConstraints.KeyValuePair(
//                "googEchoCancellation2", "true"));
//        audioConstraints.optional.add(new MediaConstraints.KeyValuePair(
//                "googNoiseSuppression", "true"));
//        lMS = sFactory.createLocalMediaStream("ARDAMS");
//
//        if (videoCallEnabled) {
//            mVideoCapturer = createCameraCapturer(new Camera1Enumerator(false));
//            Log.i(TAG, "doPublish:mVideoCapturer=null?" + (mVideoCapturer == null));
//            mVideoSource = sFactory.createVideoSource(mVideoCapturer);
//            VideoTrack videoTrack = sFactory.createVideoTrack("ARDAMSv0",
//                    mVideoSource);
//            videoTrack.setEnabled(true);
//            lMS.addTrack(videoTrack);
//        }
//
//        AudioTrack audioTrack = sFactory.createAudioTrack("ARDAMSa0",
//                sFactory.createAudioSource(audioConstraints));
//        audioTrack.setEnabled(true);
//        lMS.addTrack(audioTrack);
//
//        final MediaConstraints pcConstraints = makePcConstraints();
//        if (videoCallEnabled) {
//            sFactory.setVideoHwAccelerationOptions(sharedContext, sharedContext);
//        }
//
////        final PeerConnection.RTCConfiguration rtcConfig =
////                new PeerConnection.RTCConfiguration(mIceServers);
////        // TCP candidates are only useful when connecting to a server that supports
////        // ICE-TCP.
////        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
////        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
////        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
////        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
////        // Use ECDSA encryption.
////        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
//        if (videoCallEnabled) {
//            mVideoCapturer.startCapture(320, 240, 10);
//        }
//
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (audioManager != null) {
//                    Log.i(TAG, "doPublish:audioManager!=null");
//                    return;
//                }
//                audioManager = AppRTCAudioManager.create(mActivity.getApplication());
//                audioManager.start(new AppRTCAudioManager.AudioManagerEvents() {
//                    // This method will be called each time the number of available audio
//                    // devices has changed.
//                    @Override
//                    public void onAudioDeviceChanged(
//                            AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
//                        onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
//                    }
//                });
//            }
//        });
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("audio", "true");
//            jsonObject.put("data", "true");
//            jsonObject.put("state", "erizo");
//            jsonObject.put("video", videoCallEnabled ? "true" : "false");
//            jsonObject.put("attributes", peerConnectionParameters.attributesObj);
//            JSONObject metadataObj = new JSONObject();
//            metadataObj.put("type", "publisher");
//            jsonObject.put("metadata", metadataObj);
//            sendSDPSocket("publish", jsonObject, null, new Acknowledge() {
//                @Override
//                public void acknowledge(final JSONArray arguments) {
//                    log("doPublish:acknowledge:arguments=" + arguments);
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                mLocalStreamId = arguments.getString(0);
//                                StreamDescription stream = new StreamDescription(mLocalStreamId, true, true, true,
//                                        false, peerConnectionParameters.attributesObj, mNick, peerConnectionParameters.attributesObj.optString("imagUrl")
//                                        , peerConnectionParameters.attributesObj.optString("userId"), peerConnectionParameters.attributesObj.optString("role"));
//                                localStream = stream;
//                                mLocalStream.put(mLocalStreamId, stream);
//                                MyPcObserver pcObs = new MyPcObserver(new LicodeSdpObserver(stream,
//                                        true), stream);
//                                PeerConnection pc = sFactory.createPeerConnection(mIceServers,
//                                        pcConstraints, pcObs);
//                                pc.addStream(lMS);
//                                stream.setMedia(lMS);
//                                stream.initLocal(pc, pcObs.getSdpObserver());
//                                if (videoCallEnabled) {
//                                    stream.attachRenderer(remoteProxyRenderer);
//                                }
//                                for (RoomObserver obs : mObservers) {
//                                    obs.onPublish(mLocalStreamId, stream);
//                                }
//                                subscribe("publish", stream);
//                            } catch (Exception e) {
//                                log("doPublish:acknowledge", e);
//                            }
//                        }
//                    };
//                    if (createPeerConnectionPost) {
//                        sVcHandler.post(runnable);
//                    } else {
//                        runnable.run();
//                    }
//                }
//            });
//        } catch (JSONException e) {
//
//        }
//    }
//
//    private void onAudioManagerDevicesChanged(
//            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
//        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
//                + "selected: " + device);
//        // TODO(henrika): add callback handler.
//    }
//
//    public static class ProxyRenderer implements VideoRenderer.Callbacks {
//        private VideoRenderer.Callbacks target;
//
//        synchronized public void renderFrame(VideoRenderer.I420Frame frame) {
//            if (target == null) {
//                Logging.d(TAG, "Dropping frame in proxy because target is null.");
//                VideoRenderer.renderFrameDone(frame);
//                return;
//            }
//
//            target.renderFrame(frame);
//        }
//
//        synchronized public void setTarget(VideoRenderer.Callbacks target) {
//            this.target = target;
//        }
//    }
////    void doPublish3(VideoStreamsView view) {
////        if (mVideoCapturer != null) {
////            return;
////        }
////
////        MediaConstraints videoConstraints = new MediaConstraints();
////        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
////                "maxWidth", "320"));
////        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
////                "maxHeight", "240"));
////        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
////                "maxFrameRate", "10"));
////        MediaConstraints audioConstraints = new MediaConstraints();
////        audioConstraints.optional.add(new MediaConstraints.KeyValuePair(
////                "googEchoCancellation2", "true"));
////        audioConstraints.optional.add(new MediaConstraints.KeyValuePair(
////                "googNoiseSuppression", "true"));
////        lMS = sFactory.createLocalMediaStream("ARDAMS");
////
////        if (videoConstraints != null) {
////            mVideoCapturer = getVideoCapturer();
////            Log.i(TAG, "doPublish:mVideoCapturer=null?" + (mVideoCapturer == null));
////            mVideoSource = sFactory.createVideoSource(mVideoCapturer,
////                    videoConstraints);
////            VideoTrack videoTrack = sFactory.createVideoTrack("ARDAMSv0",
////                    mVideoSource);
////            videoTrack.setEnabled(true);
////            lMS.addTrack(videoTrack);
////        }
////        if (audioConstraints != null) {
////            AudioTrack audioTrack = sFactory.createAudioTrack("ARDAMSa0",
////                    sFactory.createAudioSource(audioConstraints));
////            lMS.addTrack(audioTrack);
//////            audioTrack.setEnabled(false);
////            audioTrack.setEnabled(true);
////        }
////
////        final StreamDescription stream = new StreamDescription("local", false, true, true,
////                false, null, mNick);
////        localStream = stream;
////        MediaConstraints pcConstraints = makePcConstraints();
////        MyPcObserver pcObs = new MyPcObserver(new LicodeSdpObserver(stream,
////                true), stream);
////
////        final PeerConnection pc = sFactory.createPeerConnection(mIceServers,
////                pcConstraints, pcObs);
////        pc.addStream(lMS, new MediaConstraints());
////
////        stream.setMedia(lMS);
////        if (view != null) {
////            stream.attachRenderer(new VideoCallbacks(view,
////                    LOCAL_STREAM_ID));
////        }
////        stream.initLocal(pc, pcObs.getSdpObserver());
////        JSONObject jsonObject = new JSONObject();
////        JSONObject attributesObj = new JSONObject();
////        try {
////            jsonObject.put("audio", "true");
////            jsonObject.put("data", "true");
////            jsonObject.put("state", "erizo");
////            jsonObject.put("video", "true");
////            attributesObj.put("actualName", username);
////            attributesObj.put("name", username);
////            attributesObj.put("type", "public");
////            jsonObject.put("attributes", attributesObj);
////            sendSDPSocket("publish", jsonObject, null, new Acknowledge() {
////                @Override
////                public void acknowledge(JSONArray arguments) {
////                    log("doPublish:acknowledge:arguments=" + arguments);
////                    try {
////                        stream.setId(arguments.getString(0));
////                        mLocalStream.put(stream.getId(), stream);
////                        pc.addStream(lMS, new MediaConstraints());
////                        JSONObject jsonObject = new JSONObject();
////                        jsonObject.put("streamId", stream.getId());
////                        jsonObject.put("msg", arguments);
////                        sendSDPSocket("signaling_message", jsonObject, null, new Acknowledge() {
////                            @Override
////                            public void acknowledge(JSONArray arguments) {
////                                log("doPublish:acknowledge2:arguments=" + arguments);
////                            }
////                        });
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////
////                }
////            });
////        } catch (JSONException e) {
////
////        }
////    }
//
//    @Override
//    public void unpublish() {
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                doUnpublish();
//            }
//        });
//    }
//
//    /**
//     * stop all streams from being cast to the server
//     */
//    void doUnpublish() {
//        Log.i(TAG, "doUnpublish");
//        for (String key : mLocalStream.keySet()) {
//            final StreamDescription stream = mLocalStream.get(key);
//            if (stream != null && stream.isLocal()) {
//                stream.getPeerConnection().removeStream(lMS);
//                for (RoomObserver obs : mObservers) {
//                    obs.onStreamRemoved(stream);
//                }
//
//                if (mObservers.size() == 0) {
//                    destroy("doUnpublish", stream);
//                }
//            }
//        }
//        mLocalStream.clear();
//
//        if (lMS != null) {
//            lMS.dispose();
//        }
//
//        if (mVideoCapturer != null) {
//            try {
//                mVideoCapturer.stopCapture();
//            } catch (InterruptedException e) {
//                Log.e(TAG, "doUnpublish:stopCapture", e);
//            }
//        }
//
//        if (mVideoCapturer != null) {
//            mVideoCapturer.dispose();
//        }
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (audioManager != null) {
//                    audioManager.stop();
//                    audioManager = null;
//                }
//            }
//        });
//        lMS = null;
//        mVideoCapturer = null;
//        if (mVideoSource != null && !mVideoStopped) {
////            mVideoSource.stop();
//        }
//        mVideoSource = null;
//    }
//
//    @Override
//    public void subscribe(StreamDescriptionInterface stream) {
//
//    }
//
//    public void subscribe(final String method, final StreamDescriptionInterface stream) {
//        log("subscribe:method=" + method);
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (stream.isLocal()) {
//                    doSubscribe((StreamDescription) stream);
//                } else {
//                    log("subscribe:method=" + method + ",isProbeEnd=" + isProbeEnd + ",probeNet=" + probeNet);
//                    if (!isProbeEnd && probeNet > 0) {
//                        mNeedSubscribe.put(stream.getId(), stream);
//                    } else {
//                        doSubscribe((StreamDescription) stream);
//                    }
//                }
//            }
//        });
//    }
//
//    void doSubscribe(final StreamDescription stream) {
//        log("doSubscribe:isLocal=" + stream.isLocal() + "," + stream.getId());
//        if (stream.isLocal()) {
//            if (probeNet > 0) {
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("streamId", stream.getId());
//                    JSONObject metadataObj = new JSONObject();
//                    metadataObj.put("type", "subscriber");
//                    jsonObject.put("metadata", metadataObj);
////            jsonObject.put("browser", "android");
//                    sendSDPSocket("subscribe", jsonObject, null, new Acknowledge() {
//                        @Override
//                        public void acknowledge(JSONArray arguments) {
//                            Log.i(TAG, "doSubscribe(local):acknowledge:arguments=" + arguments);
//                            isProbeEnd = true;
//                            sVcHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    sendMessageSocket("unsubscribe", stream.getId(), new Acknowledge() {
//                                        @Override
//                                        public void acknowledge(JSONArray arguments) {
//                                            Log.i(TAG, "doSubscribe(local)(unsubscribe):acknowledge:arguments=" + arguments);
//                                        }
//                                    });
//                                    Log.i(TAG, "doSubscribe(local):acknowledge:mNeedSubscribe=" + mNeedSubscribe.size());
//                                    if (mNeedSubscribe.size() > 0) {
//                                        Set<String> keyset = mNeedSubscribe.keySet();
//                                        for (String key : keyset) {
//                                            StreamDescription stream = (StreamDescription) mRemoteStream
//                                                    .get(key);
//                                            subscribe("doSubscribe(local)", stream);
//                                        }
//                                    }
//                                    mNeedSubscribe.clear();
//                                }
//                            });
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            return;
//        }
//        log("doSubscribe:getMedia=null?" + (stream.getMedia() == null));
//        if (stream.getMedia() != null) {
//            // already subscribed!
//            triggerMediaAvailable(stream);
//            return;
//        }
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("streamId", stream.getId());
//            JSONObject metadataObj = new JSONObject();
//            metadataObj.put("type", "subscriber");
//            jsonObject.put("metadata", metadataObj);
//            sendSDPSocket("subscribe", jsonObject, null, new Acknowledge() {
//                @Override
//                public void acknowledge(JSONArray arguments) {
//                    Log.i(TAG, "doSubscribe:acknowledge:arguments=" + arguments);
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
////                            PeerConnection.RTCConfiguration rtcConfig =
////                                    new PeerConnection.RTCConfiguration(mIceServers);
////                            // TCP candidates are only useful when connecting to a server that supports
////                            // ICE-TCP.
////                            rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
////                            rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
////                            rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
////                            rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
////                            // Use ECDSA encryption.
////                            rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
//
//                            MyPcObserver pcObs = new MyPcObserver(new LicodeSdpObserver(stream,
//                                    false), stream);
//                            PeerConnection pc = sFactory.createPeerConnection(mIceServers,
//                                    makePcConstraints(), pcObs);
//                            stream.initRemote(pc, pcObs.getSdpObserver());
//                        }
//                    };
//                    if (createPeerConnectionPost) {
//                        sVcHandler.post(runnable);
//                    } else {
//                        runnable.run();
//                    }
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        // Uncomment to get ALL WebRTC tracing and SENSITIVE libjingle logging.
//        // NOTE: this _must_ happen while |factory| is alive!
//        // Logging.enableTracing("logcat:",
//        // EnumSet.of(Logging.TraceLevel.TRACE_ALL),
//        // Logging.Severity.LS_SENSITIVE);
//    }
//
//    /**
//     * triggers the event that a stream was added - will eventually happen with
//     * delay
//     */
//    void triggerStreamAdded(StreamDescription stream) {
//        Log.i(TAG, "triggerStreamAdded:mObservers=" + mObservers.size());
//        for (RoomObserver obs : mObservers) {
//            obs.onStreamAdded(stream);
//        }
//    }
//
//    /**
//     * triggers the event that a stream was removed
//     */
//    void triggerStreamRemoved(String method, StreamDescription stream) {
//        Log.i(TAG, "triggerStreamRemoved:method=" + method + ",mObservers=" + mObservers.size());
//        for (RoomObserver obs : mObservers) {
//            obs.onStreamRemoved(stream);
//        }
//        if (mObservers.size() == 0) {
//            destroy("triggerStreamRemoved", stream);
//        }
//    }
//
//    /**
//     * triggers the event that publish has been allowed now
//     */
//    void triggerPublishAllowed() {
//        for (RoomObserver obs : mObservers) {
//            obs.onPublishAllowed();
//        }
//    }
//
//    /**
//     * triggers that subscribe was successful, and media is now available to
//     * stream
//     */
//    void triggerMediaAvailable(StreamDescriptionInterface stream) {
//        for (RoomObserver obs : mObservers) {
//            obs.onStreamMediaAvailable(stream);
//        }
//    }
//
//    /**
//     * triggers that a new video token is required - very soon - or the
//     * connection will end
//     */
//    void triggerRequestVideoToken() {
//        for (RoomObserver obs : mObservers) {
//            obs.onRequestRefreshToken();
//        }
//    }
//
//    @Override
//    public void destroy(String method, final StreamDescriptionInterface param0) {
//        log("destroy:method=" + method);
//        final StreamDescription stream = (StreamDescription) param0;
//        if (stream == null) {
//            log("destroy:stream == null");
//            return;
//        }
//        if (stream.getState() == StreamDescriptionInterface.StreamState.DESTROYED) {
//            log("destroy:State == DESTROYED");
//            return;
//        }
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (stream.getPeerConnection() != null) {
//                    stream.getPeerConnection().dispose();
//                    stream.setPc(null);
//                }
//                stream.onDestroyed();
//
//                if (stream.isLocal()) {
//                    sendMessageSocket("unpublish", stream.getId(), new Acknowledge() {
//                        @Override
//                        public void acknowledge(JSONArray arguments) {
//                            Log.i(TAG, "destroy:acknowledge:arguments=" + arguments);
//                        }
//                    });
//                }
////                stream.pc = null;
//            }
//        });
//    }
//
//    @Override
//    public void disable(final StreamDescriptionInterface param0) {
//        final StreamDescription stream = (StreamDescription) param0;
//        if (stream.isLocal()) {
//            return;
//        }
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                sendMessageSocket("unsubscribe", stream.getId(), null);
//                stream.detachRenderer();
//
////                stream.getPeerConnection().dispose();
////                stream.onDisable();
//            }
//        });
//    }
//
//    @Override
//    public void setAudioEnabled(boolean enabled) {
//        if (mState != State.kConnected || lMS == null) {
//            return;
//        }
//
//        for (AudioTrack audioTrack : lMS.audioTracks) {
//            audioTrack.setEnabled(enabled);
//        }
//    }
//
//    @Override
//    public void setActivity(Activity activity) {
//        mActivity = activity;
//    }
//
//    @Override
//    public Map<String, StreamDescriptionInterface> getRemoteStreams() {
//        return mRemoteStream;
//    }
//
//    @Override
//    public boolean isPublishing() {
//        Log.i(TAG, "isPublishing:mLocalStream=" + mLocalStream);
//        return mLocalStream.size() > 0;
//    }
//
//    @Override
//    public void detachLocalStream() {
//        sVcHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                for (String key : mLocalStream.keySet()) {
//                    StreamDescriptionInterface stream = mLocalStream.get(key);
//                    if (stream != null) {
//                        stream.detachRenderer();
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public void post(Runnable r) {
//        sVcHandler.post(r);
//    }
//
//    @Override
//    public void setNick(String nickname) {
//        mNick = nickname;
//    }
//
//    @Override
//    public boolean requestPublish() {
//        if (mPermissionPublish) {
//            sVcHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    triggerPublishAllowed();
//                }
//            });
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * parse an acknowledge to a token sent, analyze for permissions, disconnect
//     * on error
//     */
//    protected void parseVideoTokenResponse(JSONArray arg) {
//        // TODO dk: parse all the other things that come with the response? TURN
//        // Server, etc?
//        boolean success = false;
//        String message = "";
//        try {
//            success = "success".equalsIgnoreCase(arg.getString(0));
//            if (success) {
//                JSONObject obj = arg.getJSONObject(1);
//                boolean subscribe = false;
//                boolean publish = false;
////                if (obj.has("permissions")) {
////                    JSONObject permissions = obj.getJSONObject("permissions");
////                    subscribe = permissions.has("subscribe")
////                            && permissions.getBoolean("subscribe");
////                    publish = permissions.has("publish")
////                            && permissions.getBoolean("publish");
////                }
//                mPermissionSubscribe = true;
//                mPermissionPublish = true;
//            } else {
//                message = arg.get(1).toString();
//            }
//        } catch (JSONException e) {
//            log(e.getMessage());
//        }
//
//        if (!success) {
//            log("Token failed: " + message);
//            disconnect("parseVideoTokenResponse");
//        }
//    }
//
//    // Put a |key|->|value| mapping in |json|.
//    private static void jsonPut(JSONObject json, String key, Object value) {
//        try {
//            json.put(key, value);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private SessionDescription modifySdpMaxBW(SessionDescription sdp) {
//        StringBuffer desc = new StringBuffer();
//        int audioLine = -1;
//        int videoLine = -1;
//        ArrayList<Integer> bLines = new ArrayList<Integer>();
//        String[] lines = sdp.description.split("\r\n");
//        for (int i = 0; i < lines.length; ++i) {
//            if (lines[i].startsWith("m=audio")) {
//                audioLine = i;
//            } else if (lines[i].startsWith("m=video")) {
//                videoLine = i;
//            } else if (lines[i].startsWith("b=AS:")) {
//                bLines.add(i);
//            }
//        }
//        // TODO dk: this may want to check for existing B-Lines!
//        boolean addVideoB = mMaxVideoBW > 0;
//        boolean addAudioB = mMaxAudioBW > 0;
//        for (int i = 0; i < lines.length; ++i) {
//            desc.append(lines[i]);
//            desc.append("\r\n");
//            if (i == audioLine && addAudioB) {
//                desc.append("b=AS:" + mMaxAudioBW + "\r\n");
//            } else if (i == videoLine && addVideoB) {
//                desc.append("b=AS:" + mMaxVideoBW + "\r\n");
//            }
//        }
//
//        return new SessionDescription(sdp.type, desc.toString());
//    }
//
//    /**
//     * Peer connection parameters.
//     */
//    public static class PeerConnectionParameters {
//        public final boolean videoCallEnabled;
//        public final int videoWidth;
//        public final int videoHeight;
//        public final int videoFps;
//        public JSONObject attributesObj;
//
//        public PeerConnectionParameters(boolean videoCallEnabled,
//                                        int videoWidth, int videoHeight, int videoFps, JSONObject attributesObj) {
//            this.videoCallEnabled = videoCallEnabled;
//            this.videoWidth = videoWidth;
//            this.videoHeight = videoHeight;
//            this.videoFps = videoFps;
//            this.attributesObj = attributesObj;
//        }
//    }
//}

//package com.example.licodeclient;
//
//import android.util.Log;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.webrtc.MediaConstraints;
//import org.webrtc.MediaStream;
//import org.webrtc.PeerConnection;
//import org.webrtc.SdpObserver;
//import org.webrtc.SessionDescription;
//import org.webrtc.VideoRenderer;
//
//public class StreamDescription implements StreamDescriptionInterface {
//    String TAG = "StreamDescription";
//    /**
//     * current state of the stream
//     */
//    private StreamState mState = StreamState.UNKNOWN;
//
//    /**
//     * identifier for this stream
//     */
//    private String mId;
//
//    /**
//     * has data?
//     */
//    private boolean mData;
//    /**
//     * has video?
//     */
//    private boolean mVideo;
//    /**
//     * has screen stream?
//     */
//    private boolean mScreen;
//    /**
//     * has audio?
//     */
//    private boolean mAudio;
//
//    /**
//     * the attribute information
//     */
//    private JSONObject mAttributes = new JSONObject();
//
//    /**
//     * the nick attached to this stream - if any
//     */
//    private String mNick;
//    private String imagUrl;
//    private String userId;
//    private String role;
//    /**
//     * flag to store if stream is outgoing (true, local) or incoming (false,
//     * remote)
//     */
//    private boolean mLocal;
//
//    /**
//     * sdp constraints for the sdp
//     */
//    private MediaConstraints mSdpConstraints;
//
//    /**
//     * flag - stores if audio is currently allowed to play, or not
//     */
//    private boolean mAudioActive = true;
//
//    public static StreamDescription parseJson(JSONObject arg) {
//        String id = null;
//        boolean data = false;
//        boolean video = false;
//        boolean audio = false;
//        boolean screen = false;
//        JSONObject attr = null;
//        String nick = null;
//        String imagUrl = null;
//        String userId = null;
//        String role = null;
//        try {
//            id = arg.getString("id");
//        } catch (JSONException e) {
//        }
//        try {
//            data = arg.getBoolean("data");
//        } catch (JSONException e3) {
//        }
//        try {
//            video = arg.getBoolean("video");
//        } catch (JSONException e2) {
//        }
//        try {
//            audio = arg.getBoolean("audio");
//        } catch (JSONException e1) {
//        }
//        try {
//            screen = arg.getBoolean("screen");
//        } catch (JSONException e) {
//        }
//        try {
//            attr = arg.getJSONObject("attributes");
//            if (attr != null) {
//                nick = attr.optString("name");
//                imagUrl = attr.optString("imagUrl");
//                userId = attr.optString("userId");
//                role = attr.optString("role");
//            }
//        } catch (JSONException e) {
//        }
//        return new StreamDescription(id, data, video, audio, screen, attr, nick, imagUrl, userId, role);
//    }
//
//    public StreamDescription(String id, boolean data, boolean video,
//                             boolean audio, boolean screen, JSONObject attr, String nick, String imagUrl, String userId, String role) {
//        mId = id;
//        mData = data;
//        mVideo = video;
//        mAudio = audio;
//        mScreen = screen;
//        if (attr != null) {
//            mAttributes = attr;
//        }
//        mNick = nick;
//        this.imagUrl = imagUrl;
//        this.userId = userId;
//        this.role = role;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#toJson()
//     */
//    @Override
//    public JSONObject toJson() {
//        JSONObject result = new JSONObject();
//        try {
//            result.put("data", mData);
//            result.put("video", mVideo);
//            result.put("audio", mAudio);
//            result.put("screen", mScreen);
//            if (mAttributes == null) {
//                mAttributes = new JSONObject();
//            }
//            mAttributes.put("name", mNick);
//            result.put("attributes", mAttributes);
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#getId()
//     */
//    @Override
//    public String getId() {
//        return mId;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#setId
//     * (java.lang.String)
//     */
//    @Override
//    public void setId(String newId) {
//        if (mLocal) {
//            mId = newId;
//        } else {
//            throw new UnsupportedOperationException(
//                    "May not change id of a non-local stream!");
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#isLocal
//     * ()
//     */
//    @Override
//    public boolean isLocal() {
//        return mLocal;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#toJsonOffer
//     * (java.lang.String)
//     */
//    @Override
//    public JSONObject toJsonOffer(String state) {
//        JSONObject result = new JSONObject();
//        try {
//            if (state != null) {
//                result.put("state", state);
//            }
//            result.put("data", mData);
//            result.put("audio", mAudio);
//            result.put("video", mVideo);
//            if (mAttributes == null) {
//                mAttributes = new JSONObject();
//            }
//            mAttributes.put("nick", mNick);
//            result.put("attributes", mAttributes);
//        } catch (JSONException jex) {
//            // TODO
//            jex.printStackTrace();
//        }
//
//        return result;
//    }
//
//    private PeerConnection pc = null;
//    /**
//     * the active media stream
//     */
//    private volatile MediaStream mMediaStream;
//    /**
//     * currently set video renderer
//     */
//    private VideoRenderer mRenderer;
//    public boolean setRemote = false;
//    public boolean setLocal = false;
//
//    /**
//     * access the sdp's constraints
//     */
//    public MediaConstraints sdpConstraints() {
//        return mSdpConstraints;
//    }
//
//    public SdpObserver sdpObserver;
//
//    public void initLocal(PeerConnection pc, SdpObserver sdpObserver) {
//        mLocal = true;
//        mState = StreamState.LOCAL;
//        this.pc = pc;
//        mSdpConstraints = new MediaConstraints();
//        mSdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                "OfferToReceiveAudio", "true"));
//        mSdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                "OfferToReceiveVideo", "true"));
//        this.sdpObserver = sdpObserver;
//        pc.createOffer(sdpObserver, mSdpConstraints);
//    }
//
//    public void initRemote(PeerConnection pc, SdpObserver sdpObserver) {
//        mLocal = false;
//        mState = StreamState.OPENING;
//        this.pc = pc;
//        mSdpConstraints = new MediaConstraints();
//        mSdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                "OfferToReceiveAudio", "true"));
//        mSdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                "OfferToReceiveVideo", "true"));
//        this.sdpObserver = sdpObserver;
//        pc.createOffer(sdpObserver, mSdpConstraints);
//    }
//
//    public PeerConnection getPeerConnection() {
////        Log.i(TAG, "getPeerConnection");
//        return pc;
//    }
//
//    public void setPc(PeerConnection pc) {
//        this.pc = pc;
//    }
//
//    public void setLocalDescription(SdpObserver observer, SessionDescription sdp) {
//        Log.i(TAG, "setLocalDescription:pc=null?" + (pc == null) + ",setLocal=" + setLocal);
//        if (pc == null) {
//            return;
//        }
//        setLocal = true;
//        pc.setLocalDescription(observer, sdp);
//    }
//
//    public void setRemoteDescription(SdpObserver observer, SessionDescription sdp) {
//        Log.i(TAG, "setRemoteDescription:pc=null?" + (pc == null) + ",setRemote=" + setRemote);
//        if (pc == null) {
//            return;
//        }
//        setRemote = true;
//        pc.setRemoteDescription(observer, sdp);
//    }
//
//    /**
//     * sets the associated media stream - if prepared
//     */
//    public void setMedia(MediaStream media) {
//        mMediaStream = media;
//    }
//
//    /**
//     * access the media stream - may be null
//     */
//    public MediaStream getMedia() {
//        return mMediaStream;
//    }
//
//    public synchronized void attachRenderer(
//            VideoRenderer.Callbacks videoCallbacks) {
//        if (mRenderer != null) {
//            return;
//        }
//        if (mMediaStream != null && mMediaStream.videoTracks.size() == 1) {
//            mState = StreamState.ACTIVE;
//            mRenderer = new VideoRenderer(videoCallbacks);
//            mMediaStream.videoTracks.get(0).addRenderer(mRenderer);
//        }
//    }
//
//    /**
//     * attaches a complete renderer
//     */
//    public synchronized void attachLocalRenderer(VideoRenderer renderer) {
//        if (mRenderer != null) {
//            return;
//        }
//
//        mRenderer = renderer;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.knuddels.android.activities.webrtc.StreamDescriptionInteface#
//     * detachRenderer()
//     */
//    @Override
//    public synchronized void detachRenderer() {
//        if (mRenderer != null && mMediaStream != null
//                && mMediaStream.videoTracks.size() == 1) {
//            mMediaStream.videoTracks.get(0).removeRenderer(mRenderer);
//        }
//        mRenderer = null;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#onClosing
//     * ()
//     */
//    @Override
//    public void onClosing() {
//        mState = StreamState.CLOSING;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#onDestroyed
//     * ()
//     */
//    @Override
//    public void onDestroyed() {
//        mState = StreamState.DESTROYED;
//        mMediaStream = null;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#onDisable
//     * ()
//     */
//    @Override
//    public void onDisable() {
//        mState = StreamState.BLOCKED;
//        mMediaStream = null;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#getState
//     * ()
//     */
//    @Override
//    public StreamState getState() {
//        return mState;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.knuddels.android.activities.webrtc.StreamDescriptionInteface#toggleAudio
//     * ()
//     */
//    @Override
//    public void toggleAudio() {
//        setAudioActive(!mAudioActive);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.knuddels.android.activities.webrtc.StreamDescriptionInteface#
//     * setAudioActive(boolean)
//     */
//    @Override
//    public void setAudioActive(boolean audioActive) {
//        mAudioActive = audioActive;
//        Log.i(TAG, "setAudioActive:mAudioActive=" + mAudioActive);
//        if (mMediaStream != null && mMediaStream.audioTracks.size() == 1) {
//            mMediaStream.audioTracks.get(0).setEnabled(mAudioActive);
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.knuddels.android.activities.webrtc.StreamDescriptionInteface#
//     * isAudioActive()
//     */
//    @Override
//    public boolean isAudioActive() {
//        return mAudioActive;
//    }
//
//    @Override
//    public String getNick() {
//        if (mNick != null) {
//            return mNick;
//        }
//        if (mAttributes != null) {
//            try {
//                return mAttributes.getString("name");
//            } catch (JSONException e) {
//            }
//        }
//        return null;
//    }
//
//    public String getImagUrl() {
//        if (imagUrl != null) {
//            return imagUrl;
//        }
//        if (mAttributes != null) {
//            try {
//                return mAttributes.getString("imagUrl");
//            } catch (JSONException e) {
//            }
//        }
//        return "";
//    }
//
//    public String getUserId() {
//        if (userId != null) {
//            return userId;
//        }
//        if (mAttributes != null) {
//            try {
//                return mAttributes.getString("userId");
//            } catch (JSONException e) {
//            }
//        }
//        return "";
//    }
//
//    public String getRole() {
//        if (role != null) {
//            return role;
//        }
//        if (mAttributes != null) {
//            try {
//                return mAttributes.getString("role");
//            } catch (JSONException e) {
//            }
//        }
//        return "";
//    }
//
//    /**
//     * check if this stream has been abandoned by the video server
//     */
//    public boolean isClosing() {
//        return mState == StreamState.CLOSING;
//    }
//
//}

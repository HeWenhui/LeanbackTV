//package com.example.licodeclient;
//
//import android.util.Log;
//
//import org.webrtc.SdpObserver;
//import org.webrtc.SessionDescription;
//
///**
// * Created by Administrator on 2017/5/5.
// */
//
//public class WrapSdpObserver implements SdpObserver {
//    String TAG = "WrapSdpObserver";
//    public SdpObserver other;
//
//    public WrapSdpObserver(SdpObserver other) {
//        this.other = other;
//    }
//
//    @Override
//    public void onCreateSuccess(SessionDescription sdp) {
//        Log.i(TAG, "onCreateSuccess");
//        other.onCreateSuccess(sdp);
//    }
//
//    @Override
//    public void onSetSuccess() {
//        Log.i(TAG, "onSetSuccess");
//        other.onSetSuccess();
//    }
//
//    @Override
//    public void onCreateFailure(String error) {
//        Log.i(TAG, "onCreateFailure:error=" + error);
//        other.onCreateFailure(error);
//    }
//
//    @Override
//    public void onSetFailure(String error) {
//        Log.i(TAG, "onSetFailure:error=" + error);
//        other.onSetFailure(error);
//    }
//}

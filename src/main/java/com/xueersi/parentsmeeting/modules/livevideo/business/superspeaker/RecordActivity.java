package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.Activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class RecordActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Camera camera = Camera.open(0);
        if(camera!=null) {
            Log.e("AAAAA","摄像头打开成功");
        }else{
            Log.e("BBBBB","摄像头打开成功");
        }
    }
}

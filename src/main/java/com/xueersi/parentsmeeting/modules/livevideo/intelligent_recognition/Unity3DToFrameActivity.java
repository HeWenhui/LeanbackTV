package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xueersi.common.base.XrsBaseFragmentActivity;
import com.xueersi.common.base.XrsUiManagerInterface;
import com.xueersi.lib.framework.UIData;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.widget.unity.UnityControler;

@Route(path = "/english/intelligent_recognition_frame")
public class Unity3DToFrameActivity extends Activity {

    private UnityControler unityControler;

//    @Override
//    protected void initView() {
//
//    }
//
//    @Override
//    protected void initData(Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    protected UIData initUIData() {
//        return null;
//    }
//
//    @Override
//    protected XrsUiManagerInterface initUi() {
//        return null;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity3_dto_frame);
        unityControler = findViewById(R.id.unity_controller);
        String resDir = Environment.getExternalStorageDirectory() + "/parentsmeeting/yueyi";

        unityControler.loadActions(resDir);

        unityControler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                unityControler.playAction("action1");
            }
        });
    }
}

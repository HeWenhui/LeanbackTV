package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.widget.unity.UnityControler;
import com.xueersi.ui.widget.unity.UnityFrameList;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

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
        String resDir = Environment.getExternalStorageDirectory() + File.separator + "english";

        unityControler.loadActions(resDir);

        unityControler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                unityControler.playAction("action1");

                UnityFrameList f1 = unityControler.queryFrame(ActionConfig.A_MON_BH_C);
//                final UnityFrameList f2 = unityControler.queryFrame(ActionConfig.SPEEK_LOOP).setLoopMode(true);
                final UnityFrameList f2 = unityControler.queryFrame(ActionConfig.A_MON_LH_U).setLoopMode(true);
                final UnityFrameList f3 = unityControler.queryFrame(ActionConfig.A_MON_RH_E).setLoopMode(true);

                unityControler.playAction(f1, f2, f3);

                Observable.just(true).
                        delay(10, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
                        doOnNext(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                f2.setLoopMode(false);
                            }
                        }).
                        delay(10, TimeUnit.SECONDS).
                        subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                f3.setLoopMode(false);
                            }
                        });
            }
        });


//        unityControler.playDefault();

    }

    static class ActionConfig {
        //hands claps在胸前拍手（抬手，保持动作，放手）
        final static String A_MON_BH_C = "A_MON_BH_C";
        final static String A_MON_LH_U = "A_MON_LH_U";
        //右手抬起放在耳侧（抬手，保持动作，放手）
        final static String A_MON_RH_E = "A_MON_RH_E";
        final static String A_MON_RH_W = "A_MON_RH_W";
        final static String A_MON_T_U = "A_MON_T_U";
        final static String DEFAULT = "DEFAULT";
        final static String SPEEK_LOOP = "SPEEK_LOOP";

    }
}

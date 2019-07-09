package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.unity3d.player.UnityPlayer;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.lib.unity3d.UnityCommandPlay;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.Unity3DPlayManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 英语语音测评使用的Activity
 */
@Route(path = "/english/intelligent_recognition")
public class IntelligentRecognitionActivity extends AppCompatActivity {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private FrameLayout frameLayout;
    private UnityPlayer unityPlayer;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelligent_recognition);
        addUnity();
        addFragment();
//        handleUnity3D();
    }

    private void handleUnity3D() {
        if (AppConfig.DEBUG) {
            Observable.
                    <Boolean>just(true).
                    delay(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            logger.i("UnityCommandPlay.playBodyActionSingle");
                            UnityCommandPlay.playBodyActionSingle(Unity3DPlayManager.IntelligentUnity3DParam.A_MON_RH_CL);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.e(throwable.getStackTrace());
                            throwable.printStackTrace();
                        }
                    });
        }
    }

    private void addUnityView() {
        frameLayout = findViewById(R.id.unity_container);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        unityPlayer = new UnityPlayer(this);
        frameLayout.addView(unityPlayer, lp);

    }

    private void addUnity() {
        addUnityView();
        unityInit();
    }

    /**
     * Unity初始化
     */
    private void unityInit() {
        UnityCommandPlay.downloadModel("https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monscene6");
        UnityCommandPlay.downloadModel("https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monavater7");
    }

    private void addFragment() {
        IntelligentRecognitionRecord intelligentRecognitionRecord =
                getIntent().getParcelableExtra("intelligentRecognitionRecord");
        Bundle bundle = new Bundle();
        bundle.putParcelable("intelligentRecognitionRecord", intelligentRecognitionRecord);
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_container,
                        IntelligentRecognitionFragment.newInstance(bundle),
                        "f1")
                .commit();
    }

    /**
     * 该方法是unity返回回调不要删除
     */
    public void FailedLoad(String model) {
        logger.e("FailedLoad = " + model);
    }


    /**
     * {@link #ParternerConfig}
     * 该方法是unity返回回调不要删除
     */
    public void onLoadedEnd(String model) {

        logger.i("onLoadedEnd " + model);

        int width = ScreenUtils.getScreenWidth();
        int height = ScreenUtils.getScreenHeight();
        int resolutionX = Math.max(width, height);
        int resolutionY = Math.min(width, height);

        UnityCommandPlay.cameraAnimatorAll();//场景缩小

        UnityCommandPlay.setResolutionRatio(resolutionX + "/" + resolutionY);
        UnityCommandPlay.setScreenOrientation("LandscapeLeft/false");
        UnityCommandPlay.setScreenOrientation("LandscapeRight/false");
        UnityCommandPlay.setScreenOrientation("Portrait/false");
        UnityCommandPlay.setScreenOrientation("PortraitUpsideDown/false");

        logger.i("onLoadedEnd");


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (unityPlayer != null) {
            unityPlayer.resume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        AitalkLog.log("onStart");
        if (unityPlayer != null) {
            unityPlayer.start();
        }


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        unityPlayer.lowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            unityPlayer.lowMemory();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        unityPlayer.configurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (unityPlayer != null) {
            unityPlayer.windowFocusChanged(hasFocus);
        }

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return unityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (unityPlayer != null) {
            return unityPlayer.injectEvent(event);
        }
        return super.onKeyUp(keyCode, event);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            backAitalk();//处理back 键
            System.exit(0);
            return true;
        }
        if (unityPlayer != null) {
            return unityPlayer.injectEvent(event);
        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (unityPlayer != null) {
            return unityPlayer.injectEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unityPlayer.quit();
    }
}

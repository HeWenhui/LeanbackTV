package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.unity3d.player.UnityPlayer;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.lib.unity3d.UnityCommandPlay;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget.IntelligentRecognitionFragment;

/**
 * 英语语音测评使用的Activity
 */
public class IntelligentRecognitionActivity extends AppCompatActivity {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private FrameLayout frameLayout;
    private UnityPlayer unityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelligent_recognition);
        frameLayout = findViewById(R.id.container);
        addUnityView();
//        addFragment();
    }

    private void addUnityView() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        unityPlayer = new UnityPlayer(this);
        frameLayout.addView(unityPlayer, lp);
        unityInit();
    }

    /**
     * Unity初始化
     */
    private void unityInit() {

        UnityCommandPlay.downloadModel("https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monscene6");
        UnityCommandPlay.downloadModel("https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monavater7");

        int width = ScreenUtils.getScreenWidth();
        int height = ScreenUtils.getScreenHeight();
        int resolutionX = Math.max(width, height);
        int resolutionY = Math.min(width, height);

        UnityCommandPlay.setResolutionRatio(resolutionX + "/" + resolutionY);
        UnityCommandPlay.setScreenOrientation("LandscapeLeft/false");
        UnityCommandPlay.setScreenOrientation("LandscapeRight/false");
        UnityCommandPlay.setScreenOrientation("Portrait/false");
        UnityCommandPlay.setScreenOrientation("PortraitUpsideDown/false");
    }

    private void addFragment() {
        IntelligentRecognitionRecord intelligentRecognitionRecord =
                getIntent().getParcelableExtra("intelligentRecognitionRecord");
        Bundle bundle = new Bundle();
        bundle.putParcelable("intelligentRecognitionRecord", intelligentRecognitionRecord);
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.container,
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
     * 该方法是unity返回回调不要删除
     */
    public void onLoadedEnd(String model) {
        logger.i("onLoadedEnd");
    }

}

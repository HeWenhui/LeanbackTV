package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.unity3d.player.UnityPlayer;
import com.xueersi.common.base.XrsBaseFragmentActivity;
import com.xueersi.common.base.XrsUiManagerInterface;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.UIData;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.ZipExtractorTask;
import com.xueersi.lib.framework.utils.ZipProg;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.lib.unity3d.UnityCommandPlay;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.rxutils.CommonRxObserver;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.AudioEvaluationDownload;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentLocalFileManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.audio.EvaluationAudioPlayerDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.unity_3d.Unity3DPlayManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.AUDIO_EVALUATE_FILE_NAME;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.AUDIO_EVALUATE_URL;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.UNITY3D_FILE_NAME_1_V_1;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.UNITY3D_FILE_NAME_2_V_1;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.UNITY_3D_FILE1_URL;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.UNITY_3D_FILE2_URL;

/**
 * 英语语音测评使用的Activity
 */
@Route(path = "/english/intelligent_recognition")
public class IntelligentRecognitionActivity extends XrsBaseFragmentActivity {

    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private FrameLayout frameLayout;
    private UnityPlayer unityPlayer;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected UIData initUIData() {
        return null;
    }

    @Override
    protected XrsUiManagerInterface initUi() {
        return null;
    }

    private IntelligentRecognitionViewModel mViewModel;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelligent_recognition);
        addViewModelData();
        performAddUnity();
        addFragment();
        handleUnity3D();
    }

    /**
     * 添加ViewModel数据
     */
    private void addViewModelData() {
        IntelligentRecognitionRecord intelligentRecognitionRecord =
                getIntent().getParcelableExtra("intelligentRecognitionRecord");
        mViewModel = ViewModelProviders.of(this).get(IntelligentRecognitionViewModel.class);
        mViewModel.setRecordData(intelligentRecognitionRecord);
    }

    /**
     * 处理Unity3D的消息
     */
    private void handleUnity3D() {
        if (AppConfig.DEBUG) {
            Observable.
                    <Boolean>just(true).
                    delay(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            logger.i("UnityCommandPlay.playBodyActionSingle");
                            Unity3DPlayManager.playUnity3D(0);
//                            UnityCommandPlay.playBodyActionSingle(Unity3DPlayManager.IntelligentUnity3DBodyParam.A_MON_RH_CL);
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
//        unityPlayer.setStateListAnimator();
    }

    /**
     * 添加unity3D的资源文件，没有的话从远程下载
     */
    private void performAddUnity() {
        addUnityView();
        String unity3DUrl1 = IntelligentLocalFileManager.getInstance(this).getUnity3DEvaluateFile()
                + File.separator + UNITY3D_FILE_NAME_1_V_1;
        String unity3DUrl2 = IntelligentLocalFileManager.getInstance(this).getUnity3DEvaluateFile()
                + File.separator + UNITY3D_FILE_NAME_2_V_1;
        File unity3DFile1 = new File(unity3DUrl1);
        File unity3DFile2 = new File(unity3DUrl2);
//        try {
//            MD5Utils.bytesToHex(new FileInputStream(unity3DFile1)., false);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        ByteString byteString = new ByteString();
//        byteString.toByteArray()
        logger.i(unity3DUrl1 + ":" + unity3DFile1.exists() + " " + unity3DUrl2 + ":" + unity3DFile2.exists());
        logger.i(unity3DFile1.getTotalSpace() + " " + unity3DFile2.getTotalSpace());
        if (unity3DFile1.exists() && unity3DFile2.exists()) {
            handleAddUnity(unity3DUrl1);
            handleAddUnity(unity3DUrl2);
        } else {
            downLoadUnityFromAli(unity3DUrl1, unity3DUrl2);
        }
        String audioZipPath = IntelligentLocalFileManager.getInstance(this).getAudioEvaluateFile()
                + File.separator + AUDIO_EVALUATE_FILE_NAME;
//        File audioFileZip = new File(audioPath);
        File audioFile = IntelligentLocalFileManager.getInstance(IntelligentRecognitionActivity.this).getAudioEvaluateFile();
        if (!audioFile.exists()) {
            downloadAudioFromAli(audioZipPath);
        } else {
            initAudioPath();
        }
//        unityInit();
    }

    private void handleAddUnity(String url) {
        UnityCommandPlay.downloadModel(url);
    }

    /**
     * 从阿里服务器下载语音测评文件
     */
    private void downloadAudioFromAli(final String audioPath) {
//        final String audioPath = IntelligentLocalFileManager.getInstance(this).getAudioEvaluateFile()
//                + File.separator + AUDIO_EVALUATE_FILE_NAME;
        AudioEvaluationDownload.startDownLoad(audioPath, AUDIO_EVALUATE_URL).
                subscribe(new CommonRxObserver<String>() {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        handleUnzip(audioPath);
                    }
                });
    }

    private void handleUnzip(final String audioPath) {
        //下载完成解压
        new ZipExtractorTask(new File(audioPath),
                IntelligentLocalFileManager.getInstance(IntelligentRecognitionActivity.this).getAudioEvaluateFile(),
                true,
                new ZipProg() {
                    @Override
                    public void onProgressUpdate(Integer... values) {

                    }

                    @Override
                    public void onPostExecute(Exception exception) {
                        logger.i("start unzip : " + audioPath);
                        if (exception != null) {
                            logger.i("zip fail ");
                            logger.e(exception.getMessage());
                        } else {
                            logger.i("zip success");
                            initAudioPath();
                        }
                    }

                    @Override
                    public void setMax(int max) {

                    }
                }).execute();
    }
//    private boolean judgeAudioReady = false;

    private void initAudioPath() {
        logger.i("initAudioPath");
        EvaluationAudioPlayerDataManager.
                getInstance(this).
                initAudioPath().
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new CommonRxObserver<File>() {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        logger.i("initAudioPath postValue:true");
                        mViewModel.getIsEvaluationReady().setValue(true);
                    }
                });

    }

    /**
     * 从阿里云服务器下载unity3D资源
     */
    private void downLoadUnityFromAli(String url1, String url2) {

//        AudioEvaluationDownload.
//                startDownLoad(this,
//                        IntelligentLocalFileManager.getInstance(this).getUnity3DEvaluateFile()
//                                + File.separator + "monscene6",
//                        "https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monscene6").
//                doOnNext(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        UnityCommandPlay.downloadModel(s);
//                    }
//                }).
//                observeOn(AndroidSchedulers.mainThread()).
//                subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        addUnityView();
//                    }
//                });
//        AudioEvaluationDownload.
//                startDownLoad(this,
//                        IntelligentLocalFileManager.getInstance(this).getUnity3DEvaluateFile()
//                                + File.separator + "monavater7",
//                        "https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monavater7").
//                subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        UnityCommandPlay.downloadModel(s);
//                    }
//                });

        AudioEvaluationDownload.startDownLoad(url1, UNITY_3D_FILE1_URL).
                doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        logger.i(UNITY3D_FILE_NAME_1_V_1 + " " + s);
                        UnityCommandPlay.downloadModel(s);
                    }
                }).mergeWith(
                AudioEvaluationDownload.startDownLoad(url2, UNITY_3D_FILE2_URL).
                        doOnNext(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                logger.i(UNITY3D_FILE_NAME_2_V_1 + " " + s);
                                UnityCommandPlay.downloadModel(s);
                            }
                        })).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new CommonRxObserver<String>() {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }

    /**
     * Unity初始化
     */
    private void unityInit() {

//        AudioEvaluationDownload.startDownLoad(getApplicationContext(),);
        UnityCommandPlay.downloadModel("https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monscene6");
        UnityCommandPlay.downloadModel("https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monavater7");
    }

    private void addFragment() {

//        Bundle bundle = new Bundle();
//        bundle.putParcelable("intelligentRecognitionRecord", intelligentRecognitionRecord);

        getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_container,
                        IntelligentRecognitionFragment.newInstance(),
                        "f1")
                .commit();
    }

    /**
     * 该方法是unity返回回调不要删除
     */
    public void FailedLoad(String model) {
        if (UNITY3D_FILE_NAME_1_V_1.equals(model)) {
            unity3DFile1LoadSuccess = false;
        } else if (UNITY3D_FILE_NAME_2_V_1.equals(model)) {
            unity3DFile2LoadSuccess = false;
        }
        logger.e("unity3D FailedLoad = " + model);
    }

    /**
     * unity3D是否加载成功
     */
    private boolean unity3DFile1LoadSuccess;
    private boolean unity3DFile2LoadSuccess;

    /**
     * {@link #ParternerConfig}
     * 该方法是unity返回回调不要删除
     */
    public void onLoadedEnd(String model) {
        if (UNITY3D_FILE_NAME_1_V_1.equals(model)) {
            unity3DFile1LoadSuccess = true;
        } else if (UNITY3D_FILE_NAME_2_V_1.equals(model)) {
            unity3DFile2LoadSuccess = true;
        }
        logger.i("unity3D onLoadedEnd " + model);

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
    public void onPointerCaptureChanged(boolean hasCapture) {

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

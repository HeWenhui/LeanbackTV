package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.AssertUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.StandLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Ansen on 2015/5/14 23:30.
 *
 * @E-mail: ansen360@126.com
 * @Blog: http://blog.csdn.net/qq_25804863
 * @Github: https://github.com/ansen360
 * @PROJECT_NAME: FrameAnimation
 * @PACKAGE_NAME: com.ansen.frameanimation.sample
 * @Description: TODO
 */
public class FrameAnimation {
    static String TAG = "FrameAnimation";
    protected static Logger logger = LoggerFactory.getLogger(TAG);
    static String eventId = LiveVideoConfig.LIVE_FRAME_ANIM;
    /** 是不是循环播放 */
    private boolean mIsRepeat;
    /** 是循环播放的时候，是不是缓存图片 */
    private boolean mCache = true;
    private AnimationListener mAnimationListener;

    private ImageView mImageView;
    private View mView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int[] mFrameRess;
    private String[] files;
    public String path;
    /**
     * 每帧动画的播放间隔数组
     */
    private int[] mDurations;

    /**
     * 每帧动画的播放间隔
     */
    private int mDuration;
    private BitmapCreate bitmapCreate;

    /**
     * 下一遍动画播放的延迟时间
     */
    private int mDelay;

    private int mLastFrame;

    private boolean mNext;

    private boolean mPause;

    private int mCurrentSelect;

    private int mCurrentFrame;

    public static final float IMAGE_HEIGHT = 750f;

    private static final int SELECTED_A = 1;

    private static final int SELECTED_B = 2;

    private static final int SELECTED_C = 3;

    private static final int SELECTED_D = 4;
    private HashMap<String, Bitmap> bitmapHashMap = new HashMap<>();
    private static HashMap<String, Bitmap> allBitmapHashMap = new HashMap<>();
    private String drawFile = "";
    private boolean destory = false;
    private ThreadPoolExecutor executor;
    long beginTime;
    /** 图片的密度 */
    private int mDensity;
    /** 1倍图片的密度 */
    public static int DEFAULT_DENSITY = DisplayMetrics.DENSITY_MEDIUM;

    /**
     * @param iv       播放动画的控件
     * @param frameRes 播放的图片数组
     * @param duration 每帧动画的播放间隔(毫秒)
     * @param isRepeat 是否循环播放
     */
    public FrameAnimation(ImageView iv, int[] frameRes, int duration, boolean isRepeat) {
        this.mImageView = iv;
        this.mFrameRess = frameRes;
        this.mDuration = duration;
        this.mLastFrame = frameRes.length - 1;
        this.mIsRepeat = isRepeat;
        play(0);
    }

    public FrameAnimation(View iv, String[] files, int duration, boolean isRepeat) {
        this.mView = iv;
        this.files = files;
        this.mDuration = duration;
        this.mLastFrame = files.length - 1;
        this.mIsRepeat = isRepeat;
        mDensity = (int) (DEFAULT_DENSITY * (IMAGE_HEIGHT / (float) ScreenUtils.getScreenHeight()));
        if (files.length > 0) {
            play(0);
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
            executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                }
            });
        }
    }

    /**
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param isRepeat  是否循环播放
     */
    public FrameAnimation(ImageView iv, int[] frameRess, int[] durations, boolean isRepeat) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mLastFrame = frameRess.length - 1;
        this.mIsRepeat = isRepeat;
        playByDurations(0);
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param duration  每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FrameAnimation(ImageView iv, int[] frameRess, int duration, int delay) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDuration = duration;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
        playAndDelay(0);
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FrameAnimation(ImageView iv, int[] frameRess, int[] durations, int delay) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
        playByDurationsAndDelay(0);
    }

    public BitmapCreate getBitmapCreate() {
        return bitmapCreate;
    }

    public void setBitmapCreate(BitmapCreate bitmapCreate) {
        this.bitmapCreate = bitmapCreate;
    }

    private void playByDurationsAndDelay(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {   // 暂停和播放需求
                    mCurrentSelect = SELECTED_A;
                    mCurrentFrame = i;
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setBackgroundResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playByDurationsAndDelay(0);
                } else {
                    playByDurationsAndDelay(i + 1);
                }
            }
        }, mNext && mDelay > 0 ? mDelay : mDurations[i]);

    }

    private void playAndDelay(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_B;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                mNext = false;
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setBackgroundResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playAndDelay(0);
                } else {
                    playAndDelay(i + 1);
                }
            }
        }, mNext && mDelay > 0 ? mDelay : mDuration);

    }

    private void playByDurations(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_C;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setBackgroundResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        playByDurations(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                            mPause = true;
                        }
                    }
                } else {

                    playByDurations(i + 1);
                }
            }
        }, mDurations[i]);

    }

    private void play(final int i) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_D;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                    beginTime = System.currentTimeMillis();
                }
//                mImageView.setBackgroundResource(mFrameRess[i]);
                final String file = files[i];
                Bitmap bitmap = bitmapHashMap.get(file);
                if (bitmap != null) {
                    if (mView instanceof ImageView) {
                        ImageView imageView = (ImageView) mView;
                        imageView.setImageDrawable(new FrameBitmapDrawable(bitmap, mView, file, i));
                    } else {
                        mView.setBackgroundDrawable(new FrameBitmapDrawable(bitmap, mView, file, i));
                    }
                    if (i == mLastFrame) {
                        if (mIsRepeat) {
                            if (mAnimationListener != null) {
                                mAnimationListener.onAnimationRepeat();
                            }
                            play(0);
                        } else {
                            if (mAnimationListener != null) {
                                mAnimationListener.onAnimationEnd();
                                mPause = true;
                            }
                        }
                    } else {
                        play(i + 1);
                    }
                } else {
                    Runnable thread = new Runnable() {
                        @Override
                        public void run() {
                            if (destory) {
                                return;
                            }
                            InputStream inputStream = null;
                            Bitmap bitmap = null;
                            Throwable throwable = null;
                            try {
                                if (bitmapCreate != null) {
                                    bitmap = bitmapCreate.onAnimationCreate(file);
                                    if (bitmap != null) {
                                        if (destory) {
                                            bitmap.recycle();
                                            return;
                                        }
                                        bitmap.setDensity(mDensity);
                                        bitmapHashMap.put(file, bitmap);
                                        allBitmapHashMap.put(file, bitmap);
                                    }
                                }
                                if (bitmap == null) {
//                                    inputStream = mView.getContext().AssertUtil.open(file);
                                    inputStream = getInputStream(mView.getContext(), file);
                                    bitmap = BitmapFactory.decodeStream(inputStream);
                                    if (bitmap != null) {
                                        if (destory) {
                                            bitmap.recycle();
                                            return;
                                        }
                                        bitmapHashMap.put(file, bitmap);
                                        allBitmapHashMap.put(file, bitmap);
//                                        bitmap.setDensity(160);
                                        bitmap.setDensity(mDensity);
                                    }
                                }
                                if (bitmap != null) {
                                    final Bitmap finalBitmap = bitmap;
                                    mView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (destory) {
                                                finalBitmap.recycle();
                                                return;
                                            }
//                                            mView.setBackgroundDrawable(new FrameBitmapDrawable(finalBitmap, mView, file, i));
                                            if (mView instanceof ImageView) {
                                                ImageView imageView = (ImageView) mView;
                                                imageView.setImageDrawable(new FrameBitmapDrawable(finalBitmap, mView, file, i));
                                            } else {
                                                mView.setBackgroundDrawable(new FrameBitmapDrawable(finalBitmap, mView, file, i));
                                            }
                                            if (!mIsRepeat || !mCache) {
                                                if (i > 0) {
                                                    mView.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int index = 0; index < i - 1; index++) {
                                                                String f = files[index];
                                                                Bitmap bitmap1 = bitmapHashMap.get(f);
                                                                if (f.equals(drawFile)) {
                                                                    logger.d( "setBackgroundDrawable:recycle");
                                                                }
                                                                if (bitmap1 != null) {
                                                                    bitmap1.recycle();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                throwable = e;
                            } catch (OutOfMemoryError e) {
                                throwable = e;
                                logger.d("play:OutOfMemoryError:file=" + file);
                            } finally {
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            final Throwable finalThrowable = throwable;
                            mView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (i == mLastFrame) {
                                        HashMap<String, String> map = new HashMap<>();
                                        long totaltime = (System.currentTimeMillis() - beginTime);
                                        map.put("logtype", "frameend");
                                        map.put("totaltime", "" + totaltime);
                                        map.put("totaltime2", "" + (mDuration * files.length));
                                        map.put("frames", "" + files.length);
                                        map.put("fps", "" + (files.length * 1000 / totaltime));
                                        map.put("path", "" + path);
                                        if (finalThrowable != null) {
                                            map.put("throwable", "" + finalThrowable);
                                        }
                                        Runtime runtime = Runtime.getRuntime();
                                        map.put("totalMemory", "" + (runtime.totalMemory() / 1024 / 1024));
                                        map.put("freeMemory", "" + (runtime.freeMemory() / 1024 / 1024));
                                        UmsAgentManager.umsAgentDebug(mView.getContext(), eventId, map);
                                    }
                                    if (i == mLastFrame) {
                                        if (mIsRepeat) {
                                            if (mAnimationListener != null) {
                                                mAnimationListener.onAnimationRepeat();
                                            }
                                            play(0);
                                        } else {
                                            if (mAnimationListener != null) {
                                                mAnimationListener.onAnimationEnd();
                                                mPause = true;
                                            }
                                        }
                                    } else {
                                        play(i + 1);
                                    }
                                }
                            });
                        }
                    };
                    executor.execute(thread);
                }
            }
        }, mDuration);
    }

    public static interface AnimationListener {

        /**
         * <p>Notifies the start of the animation.</p>
         */
        void onAnimationStart();

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         */
        void onAnimationEnd();

        /**
         * <p>Notifies the repetition of the animation.</p>
         */
        void onAnimationRepeat();
    }

    public interface BitmapCreate {
        Bitmap onAnimationCreate(String file);
    }

    /**
     * <p>Binds an animation mListener to this animation. The animation mListener
     * is notified of animation events such as the end of the animation or the
     * repetition of the animation.</p>
     *
     * @param listener the animation mListener to be notified
     */
    public void setAnimationListener(AnimationListener listener) {
        this.mAnimationListener = listener;
        if (files != null && files.length == 0) {
            listener.onAnimationStart();
            listener.onAnimationEnd();
        }
    }

    public void release() {
        pauseAnimation();
    }

    public void pauseAnimation() {
        this.mPause = true;
    }

    public boolean isPause() {
        return this.mPause;
    }

    public void startAnimation() {
        mCurrentFrame = 0;
        mPause = false;
        play(0);
    }

    public void restartAnimation() {
        if (mPause) {
            mPause = false;
            switch (mCurrentSelect) {
                case SELECTED_A:
                    playByDurationsAndDelay(mCurrentFrame);
                    break;
                case SELECTED_B:
                    playAndDelay(mCurrentFrame);
                    break;
                case SELECTED_C:
                    playByDurations(mCurrentFrame);
                    break;
                case SELECTED_D:
                    play(mCurrentFrame);
                    break;
                default:
                    break;
            }
        }
    }

    public void setDensity(int density) {
        this.mDensity = density;
    }

    public void setCache(boolean mCache) {
        this.mCache = mCache;
    }

    public void removeBitmapCache(String file) {
        if (mIsRepeat) {
            bitmapHashMap.remove(file);
        }
    }

    public int destory() {
        destory = true;
        if (executor != null) {
            executor.shutdownNow();
        }
        pauseAnimation();
        Set<String> keys = bitmapHashMap.keySet();
        int recycle = 0;
        for (String k : keys) {
            Bitmap bitmap = bitmapHashMap.get(k);
            if (!bitmap.isRecycled()) {
                bitmapHashMap.get(k).recycle();
                recycle++;
            }
        }
        bitmapHashMap.clear();
        return recycle;
    }

    class FrameBitmapDrawable extends BitmapDrawable {
        String file;
        int index;
        View view;

        public FrameBitmapDrawable(Bitmap bitmap, View view, String file, int index) {
            super(bitmap);
            this.file = file;
            this.index = index;
            this.view = view;
        }

        @Override
        public void draw(Canvas canvas) {
            drawFile = file;
            try {
                if (getBitmap().isRecycled()) {
                    logger.e( "setBackgroundDrawable:file=" + file + ",index=" + index);
                    return;
                }
                super.draw(canvas);
            } catch (Exception e) {
                logger.e( "setBackgroundDrawable:file=" + file);
            }
        }
    }

    public static FrameAnimation createFromAees(Context mContext, View iv, String path, int duration, boolean isRepeat) {
        try {
            String[] files = {};
            File alldir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/live_stand");
            if (alldir == null) {
                alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/live_stand");
            }
            File externalFilesDir = new File(new File(alldir.getPath() + "/" + StandLiveConfig.version), path);
            if (externalFilesDir.exists()) {
                files = externalFilesDir.list();
                if (files != null) {
                    Arrays.sort(files);
                    logger.d( "createFromAees:path=" + path + ",files=" + files.length);
                    for (int i = 0; i < files.length; i++) {
                        files[i] = new File(externalFilesDir, files[i]).getPath();
                    }
                } else {
                    logger.d( "createFromAees:path=" + path + ",files=null");
                }
            }
            if (files == null || files.length == 0) {
                files = mContext.getAssets().list(path);
                for (int i = 0; i < files.length; i++) {
                    files[i] = path + "/" + files[i];
                }
            }
            try {
                StableLogHashMap stableLogHashMap = new StableLogHashMap("create_frame");
                stableLogHashMap.put("length", "" + files.length);
                stableLogHashMap.put("path", "" + path);
                UmsAgentManager.umsAgentDebug(mContext, eventId, stableLogHashMap.getData());
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
            }
            FrameAnimation btframeAnimation1 = new FrameAnimation(iv, files, duration, isRepeat);
            btframeAnimation1.path = path;
            return btframeAnimation1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        FrameAnimation btframeAnimation1 = new FrameAnimation(iv, new String[0], duration, false);
        try {
            StableLogHashMap stableLogHashMap = new StableLogHashMap("create_frame");
            stableLogHashMap.put("length", "0");
            stableLogHashMap.put("path", "" + path);
            UmsAgentManager.umsAgentDebug(mContext, eventId, stableLogHashMap.getData());
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
        return btframeAnimation1;
    }

    public static InputStream getInputStream(Context context, String file) throws IOException {
        if (file.contains(StandLiveConfig.version)) {
            FileInputStream fileInputStream = new FileInputStream(file);
            return fileInputStream;
        }
        InputStream inputStream = AssertUtil.open(file);
        return inputStream;
    }

    public static void allRecycle() {
        Set<String> keys = allBitmapHashMap.keySet();
        for (String k : keys) {
            Bitmap bitmap = allBitmapHashMap.get(k);
            if (!bitmap.isRecycled()) {
                logger.d( "allRecycle:k=" + k);
            }
        }
        logger.d( "allRecycle:allBitmapHashMap=" + allBitmapHashMap.size());
        allBitmapHashMap.clear();
    }
}

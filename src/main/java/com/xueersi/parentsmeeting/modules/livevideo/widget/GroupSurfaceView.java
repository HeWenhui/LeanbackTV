package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupItem;

public class GroupSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    boolean start = true;
    private LottieAnimationView animationView;
    private SurfaceHolder mSurfaceHolder;
    private Wave wave;

    public GroupSurfaceView(Context context) {
        super(context);
        init();
    }

    public GroupSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        String lottieResPath = "group_game_mult/images";
        String lottieJsonPath = "group_game_mult/data.json";
        final LottieEffectInfo lottieEffectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        animationView = new LottieAnimationView(getContext());
        animationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(getContext()), "group_game_mult");
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return lottieEffectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        getContext());
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (start) {
            if (wave != null) {
                wave.isRun = false;
                wave = null;
            }
            wave = new Wave();
            wave.isRun = true;
            new Thread(wave).start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (wave != null) {
            wave.isRun = false;
            wave = null;
        }
    }

    public void onVolumeUpdate(int volume) {
        if (wave != null) {
            wave.lastVolume = volume;
        }
    }

    private float voiceStart = 13.5f / 22f;

    class Wave implements Runnable {
        boolean isRun = false;
        int volume;
        int lastVolume;
        //清除canvas内容
        Paint clearPaint = new Paint();

        @Override
        public void run() {
            while (isRun) {
                Canvas canvas = mSurfaceHolder.lockCanvas();
                try {
//                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                    canvas.drawPaint(clearPaint);
//                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                    int drawVolume;
                    if (lastVolume > volume) {
                        drawVolume = volume++;
                    } else if (lastVolume < volume) {
                        drawVolume = volume--;
                    } else {
                        drawVolume = volume;
                    }
                    animationView.setProgress(voiceStart + (float) drawVolume / 30.f);
//                    animationView.setProgress((float) drawVolume / 30.f);
                    animationView.draw(canvas);
                } catch (Exception e) {

                } finally {
                    try {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {

                    }
                }
                try {
                    Thread.sleep(330);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

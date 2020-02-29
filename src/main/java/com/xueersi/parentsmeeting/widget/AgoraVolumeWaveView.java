package com.xueersi.parentsmeeting.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2018/10/22
 * 接麦的波浪线
 */
public class AgoraVolumeWaveView extends SurfaceView implements SurfaceHolder.Callback {
    String TAG = "AgoraVolumeWaveView";
    Logger logger = LoggerFactory.getLogger(TAG);
    SurfaceHolder mSurfaceHolder;
    boolean start = false;
    Wave wave;
    private float volume = 0f;
    private float newVolume = 0f;
    private float speed = .1f;
    private int backColor;
    private int colors[] = {0x196462a2, 0x326462a2, 0x646462a2, 0x966462a2, 0xFF6462a2};
    private LinearGradient linearGradient;
    private Bitmap back;

    public AgoraVolumeWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    public void setIsOnTop(boolean isOnTop) {
        setZOrderOnTop(isOnTop);
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setLinearGradient(LinearGradient linearGradient) {
        this.linearGradient = linearGradient;
    }

    public void setVolume(float volume) {
        this.newVolume = volume;
//        this.volume = volume;
//        Log.i(TAG, "setVolume:volume=" + volume);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        Log.i(TAG, "setPadding:left=" + left);
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    class Wave implements Runnable {
        private int K = 2;
        private int width = 800;
        private int height = 800;
        private int board = 5;
        private int F = 6;
        private double phase = 0;
        Paint paint = new Paint();
        Paint backPaint = new Paint();
        //清除canvas内容
        Paint clearPaint = new Paint();
        boolean isRun = false;
        int attenuations[] = {-2, -6, 4, 2, 1};
        ArrayList<Path> paths = new ArrayList<>();

        @Override
        public void run() {
            paint.setStrokeWidth(1.5f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            backPaint.setColor(Color.RED);
            if (paths.isEmpty()) {
                for (int i = 0; i < colors.length; i++) {
                    Path path = new Path();
                    paths.add(path);
                }
            }
            int count = 1;
            long before = System.currentTimeMillis();
            while (isRun) {
                long before2 = System.currentTimeMillis();
                int viewWidth = getWidth();
                int viewHeight = getHeight();
                board = (int) (viewWidth * 4.0f / 88.0f);
                width = viewWidth - 2 * board;
                height = viewHeight - 2 * board;
//                Rect dirty = new Rect();
//                dirty.top = 3;
//                dirty.left = 3;
//                dirty.right = dirty.left + width;
//                dirty.bottom = dirty.top + width;
                Canvas canvas = mSurfaceHolder.lockCanvas();
                long time1 = System.currentTimeMillis() - before2;
                before2 = System.currentTimeMillis();
                try {
                    if (canvas != null) {

                        float drawVolume = newVolume;
                        if (newVolume > volume) {
                            drawVolume = volume++;
                        } else if (newVolume < volume) {
                            drawVolume = volume--;
                        } else {
                            drawVolume = volume;
                        }

                        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        canvas.drawPaint(clearPaint);
                        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//                        canvas.drawColor(backColor);
                        if (back == null) {
                            Bitmap back2 = DrawableHelper.bitmapFromResource(getResources(), R.drawable.live_task_hongse_icon_normal);
                            if (back2 != null) {
                                back = Bitmap.createScaledBitmap(back2, viewWidth, viewHeight, true);
                                if (back != back2) {
                                    back2.recycle();
                                }
                            }
                        }
                        if (back != null) {
                            canvas.drawBitmap(back, 0, 0, null);
                        }
//                        canvas.drawCircle(width / 2, height / 2, width / 2, backPaint);
//                        canvas.drawColor(Color.TRANSPARENT);
                        //
                        phase = ((this.phase + speed) % (Math.PI * 64));
//                        _drawLine(canvas, drawVolume, -2, 0x196462a2);
//                        _drawLine(canvas, drawVolume, -6, 0x326462a2);
//                        _drawLine(canvas, drawVolume, 4, 0x646462a2);
//                        _drawLine(canvas, drawVolume, 2, 0x966462a2);
//                        _drawLine(canvas, drawVolume, 1, 0xff6462a2);
                        _drawLine(canvas, drawVolume);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Wave", e);
                } finally {
                    long time2 = System.currentTimeMillis() - before2;
                    before2 = System.currentTimeMillis();
                    if (canvas != null) {
                        try {
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {

                        }
                    }
                    long time3 = System.currentTimeMillis() - before2;
//                    Loger.d(TAG, "Wave.run:time1=" + time1 + "," + time2 + "," + time3);
                }
                count++;
                if (count % 100 == 0) {
                    logger.d("Wave.run:time=" + (System.currentTimeMillis() - before));
                    before = System.currentTimeMillis();
                }
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            Canvas canvas = mSurfaceHolder.lockCanvas();
            try {
                if (canvas != null) {
                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    canvas.drawPaint(clearPaint);
                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                    canvas.drawColor(0xffeaebf9);
                    canvas.drawLine(0, height / 2 - 2, width, height / 2 - 2, paint);
                }
            } catch (Exception e) {

            } finally {
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            if (back != null) {
                back.recycle();
                back = null;
            }
        }

        void _drawLine(Canvas canvas, float drawVolume, float attenuation, int color) {
//            paint.setColor(Color.RED);
            paint.setColor(color);
            Path path = new Path();
            path.moveTo(0, height / 2);
            float x, y;
            for (float i = -this.K; i <= this.K; i += 0.01f) {
                x = this.width * ((i + this.K) / (this.K * 2));
                y = (float) (this.height / 2 + drawVolume * this._globalAttenuationFn(i) * (1 / attenuation) * Math
                        .sin(this.F * i - this.phase));
//                y -= 120;
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paint);
        }

        void _drawLine(Canvas canvas, float drawVolume) {
            for (int i = 0; i < colors.length; i++) {
                Path path = paths.get(i);
                path.reset();
                path.moveTo(board, height / 2 + board);
            }
            float x, y;
            for (float i = -this.K; i <= this.K; i += 0.01f) {
                x = this.width * ((i + this.K) / (this.K * 2));
                for (int j = 0; j < colors.length; j++) {
                    float attenuation = attenuations[j];
                    y = (float) (this.height / 2 + drawVolume * this._globalAttenuationFn(i) * (1 / attenuation) *
                            Math.sin(this.F * i - this.phase));
                    Path path = paths.get(j);
                    path.lineTo(x + board, y + board);
                }
            }
            for (int i = 0; i < colors.length; i++) {
                paint.setColor(colors[i]);
                if (linearGradient != null) {
                    paint.setShader(linearGradient);
                }
                Path path = paths.get(i);
                canvas.drawPath(path, paint);
            }
        }

        private double _globalAttenuationFn(float x) {
            return Math.pow(this.K * 4 / (this.K * 4 + Math.pow(x, 4)), this.K * 2);
        }
    }

    public void start() {
        start = true;
        if (wave != null) {
            wave.isRun = false;
            wave = null;
        }
        wave = new Wave();
        wave.isRun = true;
        new Thread(wave).start();
    }

    public void stop() {
        start = false;
        if (wave != null) {
            wave.isRun = false;
            wave = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (wave != null) {
            wave.isRun = false;
            wave = null;
        }
    }

}

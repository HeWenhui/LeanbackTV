package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xueersi.xesalib.utils.log.Loger;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2017/1/3.
 */

public class VolumeWaveView extends SurfaceView implements SurfaceHolder.Callback {
    String TAG = "VolumeWaveView";
    SurfaceHolder mSurfaceHolder;
    boolean start = false;
    Wave wave;
    private float volume = 0f;
    private float newVolume = 0f;
    private float speed = .1f;
    private int backColor;
    int colors[] = {0x196462a2, 0x326462a2, 0x646462a2, 0x966462a2, 0xFF6462a2};

    public VolumeWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setVolume(float volume) {
        this.newVolume = volume;
//        this.volume = volume;
        Log.i(TAG, "setVolume:volume=" + volume);
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
        private int F = 6;
        private double phase = 0;
        Paint paint = new Paint();
        boolean isRun = false;
        int attenuations[] = {-2, -6, 4, 2, 1};

        @Override
        public void run() {
            paint.setStrokeWidth(4);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            int count = 1;
            long before = System.currentTimeMillis();
            while (isRun) {
                long before2 = System.currentTimeMillis();
                width = getWidth();
                height = getHeight();
                Canvas canvas = mSurfaceHolder.lockCanvas();
                long time1 = System.currentTimeMillis() - before2;
                before2 = System.currentTimeMillis();
                try {
                    if (canvas != null) {
//                        canvas.drawColor(0xffeaebf9);
                        float drawVolume;
                        if (newVolume > volume) {
                            drawVolume = volume++;
                        } else if (newVolume < volume) {
                            drawVolume = volume--;
                        } else {
                            drawVolume = volume;
                        }
                        //清除canvas内容
                        Paint clearPaint = new Paint();
                        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        canvas.drawPaint(clearPaint);
                        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                        canvas.drawColor(backColor);
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
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    long time3 = System.currentTimeMillis() - before2;
//                    Loger.d(TAG, "Wave.run:time1=" + time1 + "," + time2 + "," + time3);
                }
                count++;
                if (count % 100 == 0) {
                    Loger.d(TAG, "Wave.run:time=" + (System.currentTimeMillis() - before));
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
                    //清除canvas内容
                    Paint clearPaint = new Paint();
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
        }

        void _drawLine(Canvas canvas, float drawVolume, float attenuation, int color) {
//            paint.setColor(Color.RED);
            paint.setColor(color);
            Path path = new Path();
            path.moveTo(0, height / 2);
            float x, y;
            for (float i = -this.K; i <= this.K; i += 0.01f) {
                x = this.width * ((i + this.K) / (this.K * 2));
                y = (float) (this.height / 2 + drawVolume * this._globalAttenuationFn(i) * (1 / attenuation) * Math.sin(this.F * i - this.phase));
//                y -= 120;
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paint);
        }

        void _drawLine(Canvas canvas, float drawVolume) {
            ArrayList<Path> paths = new ArrayList<>();
            for (int i = 0; i < colors.length; i++) {
                Path path = new Path();
                path.moveTo(0, height / 2);
                paths.add(path);
            }
            float x, y;
            for (float i = -this.K; i <= this.K; i += 0.01f) {
                x = this.width * ((i + this.K) / (this.K * 2));
                for (int j = 0; j < colors.length; j++) {
                    float attenuation = attenuations[j];
                    y = (float) (this.height / 2 + drawVolume * this._globalAttenuationFn(i) * (1 / attenuation) * Math.sin(this.F * i - this.phase));
                    Path path = paths.get(j);
                    path.lineTo(x, y);
                }
            }
            for (int i = 0; i < colors.length; i++) {
                paint.setColor(colors[i]);
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

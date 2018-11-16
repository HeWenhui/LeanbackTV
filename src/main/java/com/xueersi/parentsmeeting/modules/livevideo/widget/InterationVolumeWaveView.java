package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2017/1/3.
 */

public class InterationVolumeWaveView extends TextureView implements TextureView.SurfaceTextureListener {
    String TAG = "VolumeWaveView";
    Logger logger = LoggerFactory.getLogger(TAG);
    boolean start = false;
    Wave wave;
    private float volume = 0f;
    private float newVolume = 0f;
    private float speed = .1f;
    private int backColor;
    int colors[] = {0x196462a2, 0x326462a2, 0x646462a2, 0x966462a2, 0xFF6462a2};
    LinearGradient linearGradient;
    private Paint circleBackPaint;

    public InterationVolumeWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setSurfaceTextureListener(this);

    }


    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setLinearGradient(LinearGradient linearGradient) {
        this.linearGradient = linearGradient;
    }

    public void setVolume(float volume) {
        this.newVolume = volume;
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

    public void setCircleBack(Paint paint) {
        circleBackPaint = paint;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

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
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (wave != null) {
            wave.isRun = false;
            wave = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    class Wave implements Runnable {
        private int K = 2;
        private int width = 800;
        private int height = 800;
        private int F = 6;
        private double phase = 0;
        Paint paint = new Paint();
        //清除canvas内容
        Paint clearPaint = new Paint();
        boolean isRun = false;
        int attenuations[] = {-2, -6, 4, 2, 1};
        ArrayList<Path> paths = new ArrayList<>();

        @Override
        public void run() {
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
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
                width = getWidth();
                height = getHeight();
                Canvas canvas = InterationVolumeWaveView.this.lockCanvas();
                long time1 = System.currentTimeMillis() - before2;
                before2 = System.currentTimeMillis();
                try {
                    if (canvas != null) {
                        float drawVolume;
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
                        if (circleBackPaint != null) {
                            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, circleBackPaint);
                        } else {
                            canvas.drawColor(backColor);
                        }
                        phase = ((this.phase + speed) % (Math.PI * 64));

                        _drawLine(canvas, drawVolume);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Wave", e);
                } finally {
                    long time2 = System.currentTimeMillis() - before2;
                    before2 = System.currentTimeMillis();
                    if (canvas != null) {
                        try {
                            InterationVolumeWaveView.this.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {

                        }
                    }
                    long time3 = System.currentTimeMillis() - before2;
                }
                count++;
                if (count % 100 == 0) {
                    logger.d("Wave.run:time=" + (System.currentTimeMillis() - before));
                    before = System.currentTimeMillis();
                }
            }
            Canvas canvas = InterationVolumeWaveView.this.lockCanvas();
            try {
                if (canvas != null) {
                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    canvas.drawPaint(clearPaint);
                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                    canvas.drawColor(0xffa84300);
                    canvas.drawLine(0, height / 2 - 2, width, height / 2 - 2, paint);
                }
            } catch (Exception e) {

            } finally {
                if (canvas != null) {
                    InterationVolumeWaveView.this.unlockCanvasAndPost(canvas);
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
                y = (float) (this.height / 2 + drawVolume * this._globalAttenuationFn(i) * (1 / attenuation) * Math
                        .sin(this.F * i - this.phase));
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paint);
        }

        void _drawLine(Canvas canvas, float drawVolume) {
            for (int i = 0; i < colors.length; i++) {
                Path path = paths.get(i);
                path.reset();
                path.moveTo(0, height / 2);
            }
            float x, y;
            for (float i = -this.K; i <= this.K; i += 0.01f) {
                x = this.width * ((i + this.K) / (this.K * 2));
                for (int j = 0; j < colors.length; j++) {
                    float attenuation = attenuations[j];
                    y = (float) (this.height / 2 + drawVolume * this._globalAttenuationFn(i) * (1 / attenuation) *
                            Math.sin(this.F * i - this.phase));
                    Path path = paths.get(j);
                    path.lineTo(x, y);
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


}

package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 三分屏裁剪
 */
public class LiveCutImage {

    public static Bitmap cutBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            return null;
        }
        int width = (int) (bitmap.getWidth() * (LiveVideoConfig.VIDEO_WIDTH - LiveVideoConfig.VIDEO_HEAD_WIDTH) / LiveVideoConfig.VIDEO_WIDTH);
        int height = bitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        bitmap.recycle();
        return newBitmap;
    }

    public static Bitmap cutBitmap(Bitmap oldBitmap) {
        int width = (int) (oldBitmap.getWidth() * (LiveVideoConfig.VIDEO_WIDTH - LiveVideoConfig.VIDEO_HEAD_WIDTH) / LiveVideoConfig.VIDEO_WIDTH);
        int height = oldBitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height);
        return newBitmap;
    }

    /**
     * 保存ImageBitmap到文件夹下
     *
     * @param bitmap
     * @param saveFile
     */
    public static void saveImage(Bitmap bitmap, String saveFile) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 截图，截取当前屏幕的图片
     *
     * @param view
     * @param stringBuilder
     * @param creatBitmap
     * @return
     */
    public static Bitmap getViewBitmap(View view, StringBuilder stringBuilder, AtomicBoolean creatBitmap) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmpScreen = view.getDrawingCache();
        if (bmpScreen == null) {
            int width = view.getWidth();
            int height = view.getHeight();
            stringBuilder.append(",width=" + width + ",height=" + height);
            if (width > 0 && height > 0) {
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                bmpScreen = bitmap;
                creatBitmap.set(true);
            }
        }
        return bmpScreen;
    }


    /**
     * 生成圆形图片
     *
     * @param input  原始图片
     * @param radius 半径
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap input, int radius) {
        Bitmap result = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect src = new Rect(0, 0, input.getWidth(), input.getHeight());
        Rect dst = new Rect(0, 0, radius * 2, radius * 2);
        Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);
        canvas.clipPath(path);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(input, src, dst, paint);
        return result;
    }
}

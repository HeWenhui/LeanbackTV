package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.config.LiveVideoConfig;

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
}

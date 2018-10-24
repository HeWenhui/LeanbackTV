package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 三分屏裁剪
 */
public class LiveCutImage {

    public static Bitmap cutBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            return null;
        }
        int width = Math.min(bitmap.getWidth(), (int) (LiveVideoConfig.VIDEO_WIDTH - LiveVideoConfig.VIDEO_HEAD_WIDTH));
        int height = Math.min(bitmap.getHeight(), (int) LiveVideoConfig.VIDEO_HEIGHT);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        bitmap.recycle();
        return newBitmap;
    }

    public static Bitmap cutBitmap(Bitmap oldBitmap) {
        int width = Math.min(oldBitmap.getWidth(), (int) (LiveVideoConfig.VIDEO_WIDTH - LiveVideoConfig.VIDEO_HEAD_WIDTH));
        int height = Math.min(oldBitmap.getHeight(), (int) LiveVideoConfig.VIDEO_HEIGHT);
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
}

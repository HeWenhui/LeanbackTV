package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.File;

public class CameraViewUtils {

    protected static Logger logger = LoggerFactory.getLogger(CameraViewUtils.class.getSimpleName());

    /**
     * 按照相机的比例缩小放大这个View
     *
     * @param size
     * @param view
     */
    public static ViewGroup.LayoutParams handleSize(Context mContext, Camera.Size size, View view) {
        if (size == null || view == null) return null;
        int width = size.width;
        int height = size.height;
        if (width > 0 && height > 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                try {
                    Display defaultDisplay = ((Activity) mContext).getWindowManager().getDefaultDisplay();
                    Point point = new Point();
                    defaultDisplay.getSize(point);
                    int screenWidth = point.y;
                    int screenHeight = point.x;
                    logger.i("screenWidth = " + screenWidth + " screenHeight = " + screenHeight);
                    if (screenWidth < screenHeight) {
                        int a = screenHeight;
                        screenHeight = screenWidth;
                        screenWidth = a;
                    }
                    if (height > width) {
                        int a = height;
                        height = width;
                        width = a;
                    }
                    double dw = screenHeight * 1.0 / height;
                    double dh = screenWidth * 1.0 / width;
                    double dd = dw > dh ? dh : dw;
                    logger.i("dd = " + dd);
                    layoutParams.width = (int) (width * dd);
                    layoutParams.height = (int) (height * dd);
                    view.setLayoutParams(layoutParams);
                    return layoutParams;
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.e(e);
                }
            }
        }
        return null;
    }

    private static ContentValues getVideoContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/3gp");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }

    /**
     * 视频发送到相册中
     *
     * @param videoUrl
     */
    public static void sendVideoAlbum(Context mContext, String videoUrl) {
        File file = new File(videoUrl);
        if (file == null) {
            return;
        }
        //通知相册更新
        ContentResolver localContentResolver = mContext.getContentResolver();
        ContentValues localContentValues = CameraViewUtils.getVideoContentValues(mContext, file, System.currentTimeMillis());
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
    }

    /** 删除旧的文件夹 */
    public static void deleteOldDir() {
        File file = new File(StorageUtils.getVideoPath());
        if (!file.exists()) {
            return;
        }
        file.delete();
    }

}

package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;

public class GlideDrawableUtil {
    public static Bitmap getBitmap(Drawable drawable, LogToFile logToFile, String method, String headUrl) {
        Bitmap headBitmap = null;
        if (drawable instanceof GifDrawable) {
            GifDrawable d = (GifDrawable) drawable;
            headBitmap = d.getFirstFrame();
            logToFile.d(method + ":headUrl=" + headUrl + ",headBitmap=" + (headBitmap == null));
        } else if (drawable instanceof BitmapDrawable) {
            headBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            logToFile.d(method + ":headUrl=" + headUrl + ",drawable=" + drawable);
        }
        return headBitmap;
    }
}

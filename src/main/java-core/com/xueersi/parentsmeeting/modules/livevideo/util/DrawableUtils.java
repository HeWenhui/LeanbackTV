package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;


public class DrawableUtils {

    public static final Bitmap drawable2bitmap(Drawable drawable) {

        if (drawable instanceof GifDrawable) {
            GifDrawable gif = (GifDrawable) drawable;
            return gif.getFirstFrame();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        if (width <= 0 || height <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect bounds = drawable.getBounds();

        if (bounds != null) {
            int left = bounds.left;
            int right = bounds.right;
            int top = bounds.top;
            int bottom = bounds.bottom;

            bounds.left = 0;
            bounds.right = width;
            bounds.top = 0;
            bounds.bottom = height;
            drawable.setBounds(bounds);
            drawable.draw(canvas);

            bounds.left = left;
            bounds.right = right;
            bounds.top = top;
            bounds.bottom = bottom;
            drawable.setBounds(bounds);
        } else {
            bounds = new Rect();
            bounds.left = 0;
            bounds.right = width;
            bounds.top = 0;
            bounds.bottom = height;
            drawable.setBounds(bounds);
            drawable.draw(canvas);
        }

        return bitmap;
    }

    /**
     * 获取一个透明的drawable
     *
     * @return
     */
    public static final Drawable getTransparent() {
        return null;
    }

}

package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageScale {
    //            ivLivePrimaryClassKuangjiaImgNormal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    ivLivePrimaryClassKuangjiaImgNormal.getViewTreeObserver().removeOnPreDrawListener(this);
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivLivePrimaryClassKuangjiaImgNormal.getLayoutParams();
////            lp.height = bitmap.getHeight();
//                    lp.width = (int) ((float) ivLivePrimaryClassKuangjiaImgNormal.getHeight() / (float) bitmap.getHeight() * (float) bitmap.getWidth());
//                    ivLivePrimaryClassKuangjiaImgNormal.setLayoutParams(lp);
//                    return false;
//                }
//            });
    public static void setImageViewWidth(final ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        {
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
//            lp.height = bitmap.getHeight();
                    lp.width = (int) ((float) imageView.getHeight() / (float) bitmap.getHeight() * (float) bitmap.getWidth());
                    imageView.setLayoutParams(lp);
                    return false;
                }
            });
        }
    }
}

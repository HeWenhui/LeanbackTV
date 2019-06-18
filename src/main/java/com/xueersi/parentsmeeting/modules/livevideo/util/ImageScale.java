package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

public class ImageScale {
    static Logger logger = LoggerFactory.getLogger("ImageScale");

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

    public static void setTeamPkRight(final View tpkL_teampk_pkstate_root, final ImageView iv_live_primary_class_kuangjia_img_normal) {
        iv_live_primary_class_kuangjia_img_normal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                iv_live_primary_class_kuangjia_img_normal.getViewTreeObserver().removeOnPreDrawListener(this);
                int[] losFirst = new int[2];
                iv_live_primary_class_kuangjia_img_normal.getLocationInWindow(losFirst);
                final Bitmap bitmap = ((BitmapDrawable) iv_live_primary_class_kuangjia_img_normal.getDrawable()).getBitmap();
                float scale = (float) bitmap.getWidth() / 1328f;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tpkL_teampk_pkstate_root.getLayoutParams();
                lp.rightMargin = (int) (237 * scale) + losFirst[0];
                lp.topMargin = losFirst[1] + SizeUtils.Dp2Px(iv_live_primary_class_kuangjia_img_normal.getContext(), 11);
                tpkL_teampk_pkstate_root.setLayoutParams(lp);
                logger.d("setTeamPkRight:rightMargin=" + lp.rightMargin + ",top=" + lp.topMargin);
                return false;
            }
        });
    }
}

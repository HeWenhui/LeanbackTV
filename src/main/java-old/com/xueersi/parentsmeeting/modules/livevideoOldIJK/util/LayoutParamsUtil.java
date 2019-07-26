package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.view.View;
import android.view.ViewGroup;

import com.xueersi.lib.framework.utils.ScreenUtils;

/**
 * Created by linyuqiang on 2017/11/7.
 */

public class LayoutParamsUtil {

    public static void setViewLayoutParams(View view, ViewGroup.LayoutParams lp) {
        view.setLayoutParams(lp);
    }

    /**
     * 设置view的布局。使图片不缩放拉伸，且全屏
     *
     * @param view
     */
    public static void setViewFullScreen(View view) {
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        float density = ScreenUtils.getScreenDensity();
        int bitmapW = (int) (density * 1280);
        int bitmapH = (int) (density * 720);
        float screenRatio = (float) screenWidth / (float) screenHeight;
        int newWidth = screenWidth;
        int newHeight = screenHeight;
        if (screenRatio > (float) 16 / (float) 9) {
            newHeight = (int) ((float) screenWidth * (float) bitmapH / (float) bitmapW);
        } else if (screenRatio < (float) 16 / (float) 9) {
            newWidth = (int) ((float) screenHeight * (float) bitmapW / (float) bitmapH);
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (params.width != newWidth || params.height != newHeight) {
            params.width = newWidth;
            params.height = newHeight;
            LayoutParamsUtil.setViewLayoutParams(view, params);
        }
    }
}

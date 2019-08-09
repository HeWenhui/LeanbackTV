package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.graphics.Paint;

/**
 * Created by linyuqiang on 2018/5/6.
 * 用画笔得到文字一些尺寸
 */
public class PaintTextUtil {

    /** 用画笔得到文字高度 */
    public static float getTextHeitht(Paint paint) {
        float heightOut = -paint.ascent() + paint.descent();
        return heightOut;
    }

    /** 用画笔得到文字底线 */
    public static int getBaseline(float heightOut, Paint paint) {
        int baseline = (int) ((heightOut - (paint.descent() - paint.ascent())) / 2 - paint.ascent());
        return baseline;
    }
}

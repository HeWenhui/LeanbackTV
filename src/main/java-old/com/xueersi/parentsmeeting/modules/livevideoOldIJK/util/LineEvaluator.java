package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.animation.TypeEvaluator;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;

/**
 * Created by linyuqiang on 2017/9/26.
 * 线性位移
 */
public class LineEvaluator implements TypeEvaluator<LineEvaluator.PointAndFloat> {
    private String TAG = "LineEvaluator";

    public LineEvaluator() {

    }

    @Override
    public PointAndFloat evaluate(float t, PointAndFloat startValue, PointAndFloat endValue) {
        int x = 0;
        int y = 0;
        try {
            Point point = startValue.point;
            float startX = point.getX();
            float startY = point.getY();
            x = (int) (startX + (endValue.point.getX() - startX) * t);
            y = (int) (startY + (endValue.point.getY() - startY) * t);
        } catch (Exception e) {
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        PointAndFloat pointAndFloat = new PointAndFloat();
        pointAndFloat.fraction = t;
        pointAndFloat.point = new Point(x, y);
        return pointAndFloat;
    }

    public static class PointAndFloat {
        public float fraction;
        public Point point;

        public PointAndFloat() {

        }

        public PointAndFloat(Point point) {
            this.point = point;
        }
    }
}

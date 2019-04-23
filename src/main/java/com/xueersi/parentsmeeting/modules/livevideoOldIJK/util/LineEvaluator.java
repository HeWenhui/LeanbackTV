package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.animation.TypeEvaluator;

/**
 * Created by linyuqiang on 2017/9/26.
 * 线性位移
 */
public class LineEvaluator implements TypeEvaluator<LineEvaluator.PointAndFloat> {

    public LineEvaluator() {

    }

    @Override
    public PointAndFloat evaluate(float t, PointAndFloat startValue, PointAndFloat endValue) {
        int x = (int) (startValue.point.getX() + (endValue.point.getX() - startValue.point.getX()) * t);
        int y = (int) (startValue.point.getY() + (endValue.point.getY() - startValue.point.getY()) * t);
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

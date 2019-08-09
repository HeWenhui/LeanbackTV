package com.xueersi.parentsmeeting.modules.livevideoOldIJK.widget;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * @author chekun
 * created  at 2019/2/17 11:36
 */
public class BezierEvaluator implements TypeEvaluator<Point> {
    private Point controlPoint;

    public BezierEvaluator(Point controlPoint) {
        this.controlPoint = controlPoint;
    }

    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        int x = (int) ((1 - fraction) * (1 - fraction) * startValue.x + 2 * fraction * (1 - fraction) *
                controlPoint.x + fraction * fraction * endValue.x);
        int y = (int) ((1 - fraction) * (1 - fraction) * startValue.y + 2 * fraction * (1 - fraction) *
                controlPoint.y + fraction * fraction * endValue.y);
        return new Point(x, y);
    }
}

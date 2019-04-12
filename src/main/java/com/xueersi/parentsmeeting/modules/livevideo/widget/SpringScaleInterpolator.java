package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.view.animation.Interpolator;

/**
 * 弹性效果 差值器
 *   可配合 帧动画，属性动画 实现 弹性效果
 * @author chenkun
 * @version 1.0, 2018/4/14 下午3:27
 */


public class SpringScaleInterpolator implements Interpolator {
    //弹性因子  值越大 动画效果越慢
    private float mFactor;

    public  SpringScaleInterpolator(float factor){
        this.mFactor = factor;
    }


    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - mFactor / 4) * (2 * Math.PI) / mFactor) + 1);
    }
}

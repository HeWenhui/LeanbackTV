package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;


/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 2018/7/9 下午5:40
 */

public class PraiseBtnAnimLayout extends FrameLayout {


    /**点击时间结束后 多少秒恢复 自动缩放动画*/
    private static final int RECOVER_REPEAT_ANIM_DELAY = 2000;

    private Animation animation;
    private boolean autoStart;

    public PraiseBtnAnimLayout(@NonNull Context context) {
        this(context, null);
    }

    public PraiseBtnAnimLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseBtnAnimLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PraiseBtnAnimLayout, defStyleAttr, 0);
        autoStart = a.getBoolean(R.styleable.PraiseBtnAnimLayout_auto_star,true);
        a.recycle();

        init();
    }

    private void init() {

        if(autoStart){
            this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (getMeasuredWidth() > 0) {
                        startAnim();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            PraiseBtnAnimLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            PraiseBtnAnimLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                }
            });
        }
    }


    /**
     * 开启呼吸动画
     */
    public void startAnim() {

        animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_livevideo_praise_zoom_repeat);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        this.startAnimation(animation);

      /*  AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this.getContext(),R.animator.animator_praise_btn_repeat);
        set.setTarget(this);
        set.start();*/

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startNarrowAnim();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startEnlargeAnim();
                recoverRepeatAnim(RECOVER_REPEAT_ANIM_DELAY);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 缩小动画
     */
    private void startNarrowAnim() {
        cancleRepeatAnim();
        Animation animIn = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_livevide_praise_zoom_in);
        animIn.setFillAfter(true);
        this.startAnimation(animIn);
    }

    private void cancleRepeatAnim() {
        this.removeCallbacks(recoverRepeatAnimTask);
        if (animation != null) {
            animation.cancel();
        }

    }


    private Runnable recoverRepeatAnimTask = new Runnable() {
        @Override
        public void run() {
            startAnim();
        }
    };


    private void recoverRepeatAnim(long delay) {
        cancleRepeatAnim();
        this.postDelayed(recoverRepeatAnimTask, delay);

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancleRepeatAnim();
    }

    /**
     * 放大动画
     */
    private void startEnlargeAnim() {
        Animation animOut = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_livevideo_praise_zoom_out);
        animOut.setFillAfter(true);
        this.startAnimation(animOut);
    }

}

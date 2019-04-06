package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.Queue;

public class CustomVerticalBannerView extends RelativeLayout {
    private String TAG = getClass().getSimpleName();
    private Logger logger = LoggerFactory.getLogger(TAG);

    public static int Sp2Px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale);
    }

    public CustomVerticalBannerView(Context context) {
        super(context);
    }

    public CustomVerticalBannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVerticalBannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomVerticalBannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    Queue<SpannableString> list;

    final int layoutHeight = 40;

    public Drawable getLeftDrawable() {
        return leftDrawable;
    }

    public void setLeftDrawable(Drawable leftDrawable) {
        this.leftDrawable = leftDrawable;
    }

    public void setList(Queue<SpannableString> list) {
        this.list = list;
    }

    private boolean isOwn = false;

    public void setOwn(boolean own) {
        isOwn = own;
        if (outAnimotor.isRunning()) {//如果正在进行动画,等这个动画结束了会调用onAnimationEnd函数

        }
        if (getVisibility() == GONE) {//当前页面处于GONE，即处于上一个动画结束和下一个动画开始之间
            setVisibility(VISIBLE);
//            if (backGround != null) {
//                backGround.setVisibility(true);
//            }
            removeCallbacks(visibilty);
            removeCallbacks(runnable);
            //满足碰撞的条件，先展示这一条，再展示下一条
            long nowTime = System.currentTimeMillis();
            if (pauseTime + lastTime <= nowTime + animotorTime + remainTime + animotorTime) {
                runnable.run();
                isShowTwo = true;
                isOwn = false;
            } else {//否则只展示这一条
                runnable.run();
                isOwn = false;
            }
        }
    }

    //动画展示时间
    final int animotorTime = 1000;
    //两分钟后再次显示
    final int pauseTime = 1000 * 120;
    //banner停留的时间
    final int remainTime = 1000 * 5;
    long lastTime = 0;
    //    是否显示两次
    private boolean isShowTwo = false;

    private int centerMargin = 0;

    public int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * dp单位转换成px单位
     *
     * @param context
     * @param dp
     * @return
     */
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    Drawable leftDrawable;

    public void startAnim() {
        inAnimotor = new ObjectAnimator();
        inAnimotor.setPropertyName("translationY");

        inAnimotor.setDuration(animotorTime);

        outAnimotor = new ObjectAnimator();
        outAnimotor.setPropertyName("translationY");

        outAnimotor.setDuration(animotorTime);


//        leftDrawable = getContext().getResources().getDrawable(R.drawable
//                .bg_livevideo_stand_experience_advertise_horn);

        Drawable backGround = getResources().getDrawable(R.drawable
                .shape_livevideo_stand_experience_recommond_course_banner_message_background);

        backGround.setAlpha(153);

        setBackgroundDrawable(backGround);
//        leftDrawable = getContext().getResources().getDrawable(R.drawable
//                .bg_livevideo_stand_experience_advertise_horn);
//        leftDrawable.setBounds(0, 0, 60, 48);
        outAnimotor.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logger.i("动画结束");
                if (!isOwn) {//不是自己买课
                    if (!isShowTwo) {//如果不展示两次
                        setVisibility(GONE);
//                        if (backGround != null) {
//                            backGround.setVisibility(false);
//                        }
//                    removeCallbacks(runnable);
                        postDelayed(visibilty, pauseTime);
                        postDelayed(runnable, pauseTime);
                    } else {
                        runnable.run();
                        isShowTwo = false;
                    }
                } else {
                    runnable.run();
                    isOwn = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        postDelayed(runnable, pauseTime);

    }


    Rect rect = new Rect();
    //    Paint mPaint;
    ObjectAnimator inAnimotor;
    ObjectAnimator outAnimotor;// = ObjectAnimator.ofFloat(view, , 100, -100);
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
//                if (backGround != null) {
//                    backGround.setVisibility(true);
//                }
            }
            TextView tv = null;
            if (outAnimotor != null &&
                    outAnimotor.getTarget() instanceof TextView &&
                    (tv = (TextView) outAnimotor.getTarget()).getParent() == CustomVerticalBannerView.this) {
                logger.i("回收" + tv.getText());
                removeView(tv);
            }
            lastTime = System.currentTimeMillis();
            if (list.size() > 0) {
                SpannableString text = list.poll();
                TextView view = createView(text);

                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                Paint mPaint = view.getPaint();
                mPaint.getTextBounds(text.toString(), 0, text.length(), rect);

                int textHeight = rect.height();//文字高
                int topMargin = (Dp2Px(getContext(), layoutHeight) - textHeight) >> 1;

                centerMargin = topMargin;//动画的瞄准点就是文字的上面基线
                int topAnim = -Dp2Px(getContext(), layoutHeight / 2);
                int bottomAnim = Dp2Px(getContext(), layoutHeight);
                inAnimotor.setFloatValues(bottomAnim, centerMargin);
                outAnimotor.setFloatValues(centerMargin, topAnim);
                addView(view);
                inAnimotor.setTarget(view);
                inAnimotor.start();
                outAnimotor.setTarget(view);
                //延迟开始的animotorTime和banner显示的delayTime再开始结束动画
                postDelayed(animotorRun, animotorTime + remainTime);
            }
        }
    };
    Runnable animotorRun = new Runnable() {
        @Override
        public void run() {
            if (!outAnimotor.isStarted()) {
                outAnimotor.start();
            }
        }
    };
    private Runnable visibilty = new Runnable() {
        @Override
        public void run() {
            setVisibility(VISIBLE);
//            if (backGround != null) {
//                backGround.setVisibility(true);
//            }
        }
    };

    TextView createView(SpannableString text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextColor(0xFFFFFFFF);
        tv.setIncludeFontPadding(false);//尽量消除padding
        tv.setCompoundDrawables(leftDrawable, null, null, null);
        tv.setCompoundDrawablePadding(Dp2Px(getContext(), 3));
        return tv;
    }

//    public interface IbackGround {
//        void setVisibility(boolean isShow);
//    }

//    public void setBackGround(IbackGround backGround) {
//        this.backGround = backGround;
//    }

//    private IbackGround backGround;
}

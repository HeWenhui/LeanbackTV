package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SoundWaveView extends View {
    public SoundWaveView(Context context) {
        this(context, null);
    }

    private List<Circle> mRipples;
    private Context mContext;
    private Paint mPaint;
    private int mColor;
    private int mSpeed, mDensity;
    boolean mIsFill, mIsAlpha;

    public SoundWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取用户配置属性
        TypedArray tya = context.obtainStyledAttributes(attrs, R.styleable.RingView);
        mColor = tya.getColor(R.styleable.RingView_cColor, Color.BLUE);
        mSpeed = tya.getInt(R.styleable.RingView_cSpeed, 1);
        mDensity = tya.getInt(R.styleable.RingView_cDensity, 10);
        mIsFill = tya.getBoolean(R.styleable.RingView_cIsFill, false);
        mIsAlpha = tya.getBoolean(R.styleable.RingView_cIsAlpha, false);
        innerRadius = SizeUtils.Dp2Px(getContext(), tya.getInteger(R.styleable.RingView_cInnerRaidus, 35));
        tya.recycle();

        init();
    }

    private void init() {
        mContext = getContext();
        // 设置画笔样式
        mPaint = new Paint();
//        mPaint.setColor(mColor);
//        mPaint.setStrokeWidth(DensityUtil.dip2px(mContext, 20));
//        if (mIsFill) {
//            mPaint.setStyle(Paint.Style.FILL);
//        } else {
//        mPaint.setStyle(Paint.Style.STROKE);
//        }
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setAntiAlias(true);
        // 添加第一个圆圈
        mRipples = new CopyOnWriteArrayList<>();
//        Circle c = new Circle(0, 3);
//        mRipples.add(c);
        mDensity = SizeUtils.Dp2Px(mContext, mDensity);
        // 设置View的圆为半透明
//        setBackgroundColor(Color.TRANSPARENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SoundWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int mWidth, mHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int myWidthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int myWidthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int myHeightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int myHeightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

        // 获取宽度
        if (myWidthSpecMode == View.MeasureSpec.EXACTLY) {
            // match_parent
            mWidth = myWidthSpecSize;

        } else {
            // wrap_content
            mWidth = SizeUtils.Dp2Px(getContext(), 120);
        }

        // 获取高度
        if (myHeightSpecMode == View.MeasureSpec.EXACTLY) {
            mHeight = myHeightSpecSize;
        } else {
            // wrap_content
            mHeight = SizeUtils.Dp2Px(getContext(), 120);
        }

        // 设置该view的宽高
        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    private final int oneRadius = 50, twoRadius = 50, threeRadius = 50;

    private final int oneSpeed = 5, twoSpeed = 10, threeSpeed = 15;
    private int innerRadius;
    private int oneColor = R.color.COLOR_F7E1A8, twoColor = R.color.COLOR_F7E1A8, threeColor = R.color.COLOR_F7E1A8;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Circle c : mRipples) {
//            canvas.drawCircle(mWidth / 2, mHeight / 2, c.width - mPaint.getStrokeWidth(), mPaint);
//            logger.i(TAG, "onMeasure: top" + getTop() + " left" + getLeft() + " bottom" + getBottom() + " right" + getRight());
//            double ss = Math.sqrt(2) / 2.0 * c.width;
            float left = (float) (mWidth / 2 - c.width / 2 - innerRadius);

            float right = (float) (mWidth / 2 + c.width / 2 + innerRadius);
            float top = (float) (mWidth / 2 - c.width / 2 - innerRadius);
            float bottom = (float) (mWidth / 2 + c.width / 2 + innerRadius);
            RectF rectF = new RectF(left, top, right, bottom);
//            Log.i(TAG, "drawInCircle: " + left + " " + top + " " + right + " " + bottom + " " + c.width + " " + getWidth());
            mPaint.setAntiAlias(true);//取消锯齿
//        mPaint.setStyle(Paint.Style.FILL);//设置画圆弧的画笔的属性为描边(空心)，个人喜欢叫它描边，叫空心有点会引起歧义
            mPaint.setStrokeWidth(c.width);

            mPaint.setStyle(Paint.Style.STROKE);

            if (c.level == 1) {
//                mPaint.setAlpha((int) (255));
                mPaint.setColor(mContext.getResources().getColor(R.color.COLOR_99F7E1A8));
            } else if (c.level == 2) {
//                mPaint.setAlpha((int) (0.7 * 255));
                mPaint.setColor(mContext.getResources().getColor(R.color.CLOR_66F7E1A8));
            } else if (c.level == 3) {
//                mPaint.setAlpha((int) (0.4 * 255f));
                mPaint.setColor(mContext.getResources().getColor(R.color.COLOR_33F7E1A8));
//                if (c.width > 20) {

//                    mPaint.setColor(Color.CYAN);

//                }
//                if (c.width < innerRadius + oneRadius) {
//                    Paint onePaint = new Paint(mPaint);
//                    onePaint.setColor(oneColor);
//                    onePaint.setStrokeWidth(c.width - innerRadius);
//                    canvas.drawArc(rectF, 0, 360, false, onePaint);
//                } else if (c.width < innerRadius + oneRadius + twoRadius) {
//                    Paint onePaint = new Paint(mPaint);
//                    onePaint.setColor(oneColor);
//                    onePaint.setStrokeWidth(c.width - innerRadius - oneRadius);
//                    canvas.drawArc(rectF, 0, 360, false, onePaint);
//                    Paint twoPaint = new Paint(mPaint);
//                    twoPaint.setColor(twoColor);
//                    twoPaint.setStrokeWidth(twoRadius);
//                    RectF twoR = new RectF();
//                    canvas.drawArc(rectF, 0, 360, false, twoPaint);
//                } else {
//                    Paint onePaint = new Paint(mPaint);
//                    onePaint.setColor(oneColor);
//                    onePaint.setStrokeWidth(c.width - innerRadius - oneRadius - twoRadius);
//                    canvas.drawArc(rectF, 0, 360, false, onePaint);
//                    Paint twoPaint = new Paint(mPaint);
//                    twoPaint.setColor(twoColor);
//                    twoPaint.setStrokeWidth(twoRadius);
//                    canvas.drawArc(rectF, 0, 360, false, twoPaint);
//                    Paint threePaint = new Paint(mPaint);
//                    threePaint.setStrokeWidth(threeRadius);
//                    threePaint.setColor(threeColor);
//                    canvas.drawArc(rectF, 0, 360, false, threePaint);
//                }

            }
            if (c.level != 0) {
                canvas.drawArc(rectF, 0, 360, false, mPaint);
            }
            // 当圆超出View的宽度后删除
            if (c.width + innerRadius > (mWidth / 2 - innerRadius) / 3 * c.level + innerRadius) {
                mRipples.remove(0);

            } else {
                // 修改这个值控制速度
                c.width += mSpeed * c.level;
//            }
            }
            // 里面添加圆
//        if (mRipples.size() > 0) {
//            // 控制第二个圆出来的间距
//            if (mRipples.get(mRipples.size() - 1).width > DensityUtil.dip2px(mContext, mDensity)) {
//                mRipples.add(new Circle(0, (add++) % 3 + 1));
//            }
        }

        invalidate();
        canvas.save();
        canvas.restore();
    }

    public static class Circle {
        Circle(int width, int level) {
            this.width = width;
//            this.alpha = alpha;
            this.level = level;
//            nowLevel = 3;
        }

        int width;

        int level;
    }

    public void addRipple(Circle circle) {
        mRipples.add(circle);
        postInvalidate();
    }
}

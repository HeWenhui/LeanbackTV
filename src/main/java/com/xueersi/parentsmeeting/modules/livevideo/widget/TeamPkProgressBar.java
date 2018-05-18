package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.xueersi.parentsmeeting.modules.livevideo.R;


/**
 * 战队 PK 进度条
 * <p>
 * 注：滑动头  图片的高度 就是整个控件的高度
 * <p>
 * 当设置了 缩放背景图时 实际 measure 宽度 会 比在 布局文件中设置的大，为了显示缩放动画
 *
 * @author chenkun
 * @version 1.0, 2018/4/12 下午6:35
 */


public class TeamPkProgressBar extends View {

    /**
     * 边框 画笔
     */
    private Paint borderPaint;
    /**
     * 边线 宽度
     */
    private float strokeWidth = 5;
    private Paint currentProgressPaint;
    private Paint totalProgressPaint;

    /**
     * 进度增加默认时间
     */
    private static final long ANIM_DURATION = 1000;

    /**
     * 整个控件的高度
     */
    private int mHeight;

    /**
     * 当前进度
     */
    private int mProgress;
    /**
     * 总进度
     */
    private int mMaxProgress = 100;
    private static final int DEFUALT_INNERBAR_HEIGHT = 100;
    private RectF borderRect;
    private RectF progressRect;
    private LinearGradient totalProgressPaintShader;
    private LinearGradient currentProgressPaintShader;
    private RectF currentPorgressRect;
    private int slidHearResId;
    private Bitmap mSlidHeader;
    private float innerBarHeight;
    private int mSliderWidth;
    private ProgressAnim anim;
    private Bitmap sliderBg;
    private int sliderBgWidth;
    private int sliderBgHeight;

    /**
     * 滑动头 底部背景缩放比例
     */
    private float sliderBgScaleRatio = 0;

    /**
     * 滑动头底部背景 最大放大比列
     */
    private static final float MAX_SCALE_RATIO = 1.50f;
    private int mSliderHeight;
    private int sliderBgResId;

    /**
     * 当前滑动头的偏移位置
     */
    private int mSbOffsetX;
    private int mSbOffsetY;

    private Matrix sliderBgMatrix;
    /**
     * 动画执行期间 新缓存待设置的 进度
     */
    private int mCacheProgress = -1;

    /**
     * 为显示完整缩放动画 左右增加额外空间
     */
    int animExtraSpace;
    int realMeasureWidth;

    /**
     * 当前进度 绘制右边界
     */
    private float progressRightBound = -1;

    public TeamPkProgressBar(Context context) {
        this(context, null);
    }

    public TeamPkProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TeamPkProgressBar);
        innerBarHeight = typedArray.getDimension(R.styleable.TeamPkProgressBar_innerProgressBarHeight,
                DEFUALT_INNERBAR_HEIGHT);
        slidHearResId = typedArray.getResourceId(R.styleable.TeamPkProgressBar_sliderHeader, -1);
        sliderBgResId = typedArray.getResourceId(R.styleable.TeamPkProgressBar_sliderHeaderBg, -1);
        typedArray.recycle();

        initSlidHeader();
    }


    private void initSlidHeader() {
        if (slidHearResId != -1) {
            mSlidHeader = BitmapFactory.decodeResource(getContext().getResources(), slidHearResId);
            mHeight = mSlidHeader.getHeight();
            mSliderWidth = mSlidHeader.getWidth();
            mSliderHeight = mSlidHeader.getHeight();
        }
        if (sliderBgResId != -1) {
            sliderBg = BitmapFactory.decodeResource(getResources(), sliderBgResId);
            sliderBgWidth = sliderBg.getWidth();
            sliderBgHeight = sliderBg.getHeight();
            animExtraSpace = (int) (sliderBgWidth * MAX_SCALE_RATIO);
        }

        if (mHeight < sliderBgHeight * MAX_SCALE_RATIO) {
            mHeight = (int) (sliderBgHeight * MAX_SCALE_RATIO);
        }

        if (mHeight < innerBarHeight) {
            mHeight = (int) innerBarHeight;
        }

    }

    public TeamPkProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    public void setProgress(int progress) {
        if (!isAnimRunning()) {
            this.mProgress = progress;
            setProgressRightBound(-1);
            invalidate();
        } else {
            cacheProgress(progress);
        }
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public int getProgress() {
        return mProgress > mMaxProgress ? mMaxProgress : mProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }


    private void initPaint() {

        borderPaint = new Paint();
        borderPaint.setFilterBitmap(true);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(strokeWidth);
        borderPaint.setColor(Color.BLACK);


        currentProgressPaint = new Paint();
        currentProgressPaint.setStyle(Paint.Style.FILL);
        currentProgressPaint.setAntiAlias(true);
        currentProgressPaint.setFilterBitmap(true);


        totalProgressPaint = new Paint();
        totalProgressPaint.setStyle(Paint.Style.FILL);
        totalProgressPaint.setAntiAlias(true);
        totalProgressPaint.setFilterBitmap(true);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int realHeightMeasureSpec;
        int orignalWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (mHeight > 0) {
            realHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        } else {
            realHeightMeasureSpec = heightMeasureSpec;
        }

        if (sliderBg != null) {
            if (realMeasureWidth == 0) {
                realMeasureWidth = orignalWidth + animExtraSpace;
            }
            setMeasuredDimension(realMeasureWidth, mHeight);
        } else {
            super.onMeasure(widthMeasureSpec, realHeightMeasureSpec);

        }
    }


    static class ProgressAnim {

        float startOffsetX;
        float endOffsetX;
        long startTime;
        float currentOffsetX;
        float animRatio;
        private ProgressAnimListener mAnimListener;

        ProgressAnim(float startOffsetX, float endOffsetX) {
            this.startOffsetX = startOffsetX;
            this.endOffsetX = endOffsetX;
            startTime = AnimationUtils.currentAnimationTimeMillis();
        }

        public float computeProgress() {
            if (currentOffsetX < endOffsetX) {
                long timeSpend = AnimationUtils.currentAnimationTimeMillis() - startTime;
                float ratio = timeSpend / (float) ANIM_DURATION;
                currentOffsetX = startOffsetX + (endOffsetX - startOffsetX) * ratio;
                animRatio = ratio;
                return currentOffsetX;
            }
            if (mAnimListener != null) {
                mAnimListener.onAnimFinish();
            }
            return 0;
        }


        public float getAnimRatio() {
            return animRatio;
        }


        public void cancel() {
            currentOffsetX = endOffsetX;
            mAnimListener = null;
        }


        public void setAnimListener(ProgressAnimListener listener) {
            mAnimListener = listener;
        }


        interface ProgressAnimListener {
            /**
             * 动画执行结束
             */
            void onAnimFinish();
        }
    }


    /**
     * 平滑增加 进度
     *
     * @param progress 进度增量
     */
    public void smoothAddProgress(int progress) {
        canceled = false;
        mProgress += progress;
        float endBound = progressRect.width() * getProgress() / getMaxProgress();
        if (anim != null) {
            anim.cancel();
            anim = null;
        }
        anim = new ProgressAnim(getProgressRightBound(), endBound);
        anim.setAnimListener(new ProgressAnim.ProgressAnimListener() {
            @Override
            public void onAnimFinish() {
                if (mCacheProgress != -1) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setProgress(mCacheProgress);
                            mCacheProgress = -1;
                            invalidate();
                        }
                    },100);
                }
            }
        });
        invalidate();
    }

    /**
     * @param cacheProgress
     */
    private void cacheProgress(int cacheProgress) {
        mCacheProgress = cacheProgress;
    }

    boolean animRunning;
    boolean canceled;

    public boolean isAnimRunning() {
        return animRunning;
    }

    public void cancle() {
        canceled = true;
        animRunning = false;
        setProgress(mProgress);
    }

    float tempOffsetX;
     private static final float HALF_PROGRESS = 0.5f;
    @Override
    public void computeScroll() {

        if (anim != null && (tempOffsetX = anim.computeProgress()) > 0 && !canceled) {
            animRunning = true;
            //动画时间进行了一半
            if (anim.getAnimRatio() > HALF_PROGRESS) {

                sliderBgScaleRatio = Math.abs(1.0f - anim.getAnimRatio()) * 2 * MAX_SCALE_RATIO;

            } else {
                sliderBgScaleRatio = anim.getAnimRatio() * 2 * MAX_SCALE_RATIO;
            }
            setProgressRightBound(tempOffsetX);
            invalidate();
        } else {
            animRunning = false;
        }
    }


    private float getProgressRightBound() {

        return progressRightBound > (getMeasuredWidth() - 2 * strokeWidth) ?
                (getMeasuredWidth() - 2 * strokeWidth) : progressRightBound;
    }

    private void setProgressRightBound(float rightBound) {

        this.progressRightBound = rightBound;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        try {
            // step 1 draw border
            if (borderRect == null) {
                borderRect = new RectF();
                borderRect.left = animExtraSpace / 2 + strokeWidth;
                borderRect.top = (getMeasuredHeight() - innerBarHeight) / 2;
                borderRect.right = getMeasuredWidth() - strokeWidth - animExtraSpace / 2;
                borderRect.bottom = borderRect.top + innerBarHeight;
            }
            canvas.drawRoundRect(borderRect, borderRect.height() / 2, borderRect.height() / 2, borderPaint);
            // step 2 draw totalProgress
            if (progressRect == null) {
                progressRect = new RectF();
                progressRect.left = borderRect.left;
                progressRect.top = borderRect.top;
                progressRect.right = borderRect.right;
                progressRect.bottom = borderRect.bottom;
            }

            if (totalProgressPaintShader == null) {
                totalProgressPaintShader = new LinearGradient(progressRect.left, progressRect.height() / 2
                        , progressRect.right, progressRect.height() / 2, Color.parseColor("#55B1f4"),
                        Color.parseColor("#5278DC"), Shader.TileMode.CLAMP);
                totalProgressPaint.setShader(totalProgressPaintShader);
            }

            canvas.drawRoundRect(progressRect, progressRect.height() / 2, progressRect.height() / 2,
                    totalProgressPaint);
            // step3 draw currentProgress
            float offsetX = getProgressRightBound();

            // 首次绘制时 初始化
            if (offsetX == -1) {
                offsetX = progressRect.width() * getProgress() / getMaxProgress();
                setProgressRightBound(offsetX);
            }
            currentProgressPaintShader = new LinearGradient(progressRect.left, progressRect.height() / 2,
                    offsetX, progressRect.height() / 2,
                    Color.parseColor("#F3AD45"),
                    Color.parseColor("#DA722c"), Shader.TileMode.CLAMP);
            currentProgressPaint.setShader(currentProgressPaintShader);

            if (currentPorgressRect == null) {
                currentPorgressRect = new RectF();
            }
            currentPorgressRect.left = progressRect.left;
            currentPorgressRect.top = progressRect.top;
            currentPorgressRect.right = progressRect.left + offsetX;
            currentPorgressRect.bottom = progressRect.bottom;

            canvas.drawRoundRect(currentPorgressRect, currentPorgressRect.height() / 2,
                    currentPorgressRect.height() / 2, currentProgressPaint);

            // step 4 draw slider bg
            if (sliderBg != null && (int) (sliderBgScaleRatio * sliderBgWidth) > 0) {
                if (sliderBgMatrix == null) {
                    sliderBgMatrix = new Matrix();
                }
                sliderBgMatrix.reset();
                sliderBgMatrix.postScale(sliderBgScaleRatio, sliderBgScaleRatio);

                int sliderBgWidth = (int) (sliderBg.getWidth() * sliderBgScaleRatio);
                int sliderBgHeight = (int) (sliderBg.getHeight() * sliderBgScaleRatio);

                float startX = currentPorgressRect.right - sliderBgWidth / 2;
                float startY = (getMeasuredHeight() - sliderBgHeight) / 2;

                float rightBound = getMeasuredWidth() - (animExtraSpace + mSliderWidth) / 2;
                if (startX > rightBound) {
                    startX = rightBound;
                }
                sliderBgMatrix.postTranslate(startX, startY);
                canvas.drawBitmap(sliderBg, sliderBgMatrix, currentProgressPaint);
            }
            // step 5 draw mSlidHeader

            if (mSlidHeader != null) {
                float startX = currentPorgressRect.right - mSliderWidth / 2;
                float startY = (getMeasuredHeight() - mSliderHeight) / 2;

                float  leftBound = animExtraSpace / 2;
                if (animExtraSpace != 0) {
                    if (startX < leftBound) {
                        startX = leftBound;
                    }
                } else {
                    if (startX < 0) {
                        startX = 0;
                    }
                }
                float rightBound = getMeasuredWidth() - animExtraSpace / 2 - mSliderWidth;
                if (startX > rightBound) {
                    startX = rightBound;
                }

                // 记录当前滑动头的位置
                mSbOffsetX = (int) startX;
                mSbOffsetY = (int) startY;
                canvas.drawBitmap(mSlidHeader, startX, startY, currentProgressPaint);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回 滑动头在屏幕上的的绘制区域
     *
     * @return
     */
    public Rect getSliderDrawRect() {
        Rect rect = null;
        if (mSlidHeader != null) {
            rect = new Rect();
            int[] location = new int[2];
            this.getLocationInWindow(location);

            rect.left = location[0] + mSbOffsetX;
            rect.top = location[1] + mSbOffsetY;
            rect.right = rect.left + mSlidHeader.getWidth();
            rect.bottom = rect.top + mSlidHeader.getHeight();
        }
        return rect;
    }
}





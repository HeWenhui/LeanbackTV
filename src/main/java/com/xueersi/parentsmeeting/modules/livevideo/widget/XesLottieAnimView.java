package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.view.MotionEvent;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;
import com.airbnb.lottie.model.animatable.AnimatablePathValue;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.layer.Layer;
import com.airbnb.lottie.utils.Utils;
import com.airbnb.lottie.value.Keyframe;
import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * lottieAnim 封装 支持区域点击
 * （注：发现某些 动画元素 真实显示位置信息 同 json中解析获取到的不一直(因为动效有父级的原因)
 * ，导致点击热区计算误差，需和动效老师沟通能进行交互的元素 不能有父级（位置信息参考 画布左上角））
 *
 * @author chekun
 * created  at 2019/7/19 14:03
 */
public class XesLottieAnimView extends LottieAnimationView {

    private Paint mPaint;
    private Paint mTextPaint;
    private String mTargetImgName;
    private ImgClickListener mImgClickListener;
    private int targetImgDesinWidth;
    private int targetImgDesinHeight;
    /**
     * 绘制能点击的图片：避免用户无法交互的问题（待线上测试稳定后可去掉）
     */
    private static final boolean DRAW_FAKE_IMG = true;
    private boolean drawFakImg;
    /**
     * 目标图片点击区域是否已经初始化
     **/
    boolean areaClickInfoInited = false;
    private PointF targetImgCenterPoint;
    private RectF mTargetClickRect;
    /**
     * 设计对应的 画布尺寸
     **/
    private int mWantedWidth;
    private int mWantedHeight;
    /**是否开启调试模式(绘制点击热区)**/
    private boolean debug;
    /**
     * 目标图片区域状态
     **/
    private static final int STATE_PRESS = 1;
    private static final int STATE_NOR = 2;

    /**
     * 目标图片当前点击状态
     **/
    private int mTargetImgState = STATE_NOR;
    private Bitmap targetBitmapNor;
    private Bitmap targetBitmapPres;

    private Bitmap fakeBitmap;
    private int mTargetImgNorResId;
    private int mTargetImgClickResId;

    /**
     * 动画脚本是否加载完毕
     **/
    private boolean lottieCompostionLoaded;
    /**
     * 目标图片透明站位图
     **/
    private Bitmap mTransparent;
    private Paint mBitmapPaint;
    private Rect mFakeBitmapRect;

    public XesLottieAnimView(Context context) {
        this(context, null);
    }

    public XesLottieAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XesLottieAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XesLottieAnimView, defStyleAttr, 0);
        mTargetImgNorResId = array.getResourceId(R.styleable.XesLottieAnimView_nor_img_res, 0);
        mTargetImgClickResId = array.getResourceId(R.styleable.XesLottieAnimView_click_img_res, 0);
        array.recycle();
        init();
    }

    private void init() {
        this.addLottieOnCompositionLoadedListener(new LottieOnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                lottieCompostionLoaded = true;
                //Log.e("ckTrac", "=====>onCompositionLoaded");
                resetInfo();
                initTragetImgClickInfo();
            }
        });

        if (DRAW_FAKE_IMG) {
            this.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //Log.e("ckTrac", "=====>onAnimationStart");
                    drawFakImg = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!TextUtils.isEmpty(mTargetImgName)) {
                        if (targetImgDesinWidth != 0 && targetImgDesinHeight != 0) {
                            if (mTransparent == null) {
                                mTransparent = Bitmap.createBitmap(targetImgDesinWidth, targetImgDesinHeight,
                                        Bitmap.Config.ARGB_4444);
                            }
                           // Log.e("ckTrac", "=====>onAnimationEnd");
                            drawFakImg = true;
                            updateBitmap(mTargetImgName, mTransparent);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

    }

    @Override
    public void setAnimation(String assetName) {
        super.setAnimation(assetName);
        lottieCompostionLoaded = false;
    }

    @Override
    public void setAnimation(JsonReader reader, @Nullable String cacheKey) {
        super.setAnimation(reader, cacheKey);
        lottieCompostionLoaded = false;

    }

    @Override
    public void setAnimationFromJson(String jsonString, @Nullable String cacheKey) {
        super.setAnimationFromJson(jsonString, cacheKey);
        lottieCompostionLoaded = false;

    }

    @Override
    public void setAnimationFromUrl(String url) {
        super.setAnimationFromUrl(url);
        lottieCompostionLoaded = false;

    }

    @Override
    public void setAnimationFromJson(String jsonString) {
        super.setAnimationFromJson(jsonString);
        lottieCompostionLoaded = false;
    }

    private void resetInfo() {
        mTargetImgState = STATE_NOR;
        areaClickInfoInited = false;

        targetImgDesinWidth = 0;
        targetImgDesinHeight = 0;
    }

    /**
     * 绘制点击热区
     */
    private void drawClickArea(Canvas canvas) {
        initPaintInneed();
        if (mTargetClickRect != null) {
            canvas.drawRect(mTargetClickRect, mPaint);
        }
    }

    private void initPaintInneed() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor("#80ff0000"));
        }
    }

    /**
     * 是否开启调试模式
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public static interface ImgClickListener {
        /**
         * 目标图片被点击
         */
        void onImgClick();
    }

    /**
     * 设置 目标图片点击事件
     *
     * @param imgName  图片名称
     * @param listener
     */
    public void setImgClickListener(String imgName, ImgClickListener listener) {
        if (!TextUtils.isEmpty(imgName)) {
            if (imgName.contains(".")) {
                imgName = imgName.substring(0, imgName.lastIndexOf("."));
            } else {
                imgName = imgName;
            }
            //设置新目标图片时 需重新 初始化位置信息
            if (!imgName.equals(mTargetImgName)) {
                if (lottieCompostionLoaded) {
                    resetInfo();
                    //恢复之前替换的图片
                    updateTargeImgArea(STATE_NOR);
                    //回收之前设置的点击态图片
                    if (targetBitmapPres != null) {
                        targetBitmapPres.recycle();
                        targetBitmapPres = null;
                    }
                    mTargetImgName = imgName;
                    initTragetImgClickInfo();
                } else {
                    mTargetImgName = imgName;
                }
                //刷新
                invalidate();
            }
        }
        mImgClickListener = listener;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        calculateClickRect(canvas);
        if (debug) {
            drawClickArea(canvas);
        }
        if (DRAW_FAKE_IMG) {
            drawFakeImg(canvas);
        }
    }

    /**
     * 重新绘制目标图片
     **/
    private void drawFakeImg(Canvas canvas) {
        initBitmPaintInneed();
        if (mTargetClickRect != null && drawFakImg && fakeBitmap != null) {
            if (mFakeBitmapRect == null) {
                mFakeBitmapRect = new Rect();
            }
            mFakeBitmapRect.left = 0;
            mFakeBitmapRect.top = 0;
            mFakeBitmapRect.right = fakeBitmap.getWidth();
            mFakeBitmapRect.bottom = fakeBitmap.getHeight();
            canvas.drawBitmap(fakeBitmap, mFakeBitmapRect, mTargetClickRect, mBitmapPaint);
        }
    }

    private void initBitmPaintInneed() {
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint();
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setFilterBitmap(true);
        }
    }


    /**
     * 计算目标图片可点击区域
     */
    private void calculateClickRect(Canvas canvas) {
        if (!TextUtils.isEmpty(mTargetImgName) && mImgClickListener != null) {
            if (targetImgCenterPoint != null && this.getComposition() != null) {
                if (!areaClickInfoInited) {
                    areaClickInfoInited = true;

                    int widthWanted = mWantedWidth;
                    int heihtWanted = mWantedHeight;

                    // 当前缩放比例
                    float maxScaleX = widthWanted / (float) this.getComposition().getBounds().width();
                    float maxScaleY = heihtWanted / (float) this.getComposition().getBounds().height();
                    float maxScale = Math.min(maxScaleX, maxScaleY);
                 /*   Log.e("ckTrac", "===>calculateClickRect:" + maxScale +":" + this.getComposition().getBounds()
                   .width() + ":" + this.getComposition().getBounds().height());*/
                    // 设计稿上对应的 坐标位置
                    float centerX = targetImgCenterPoint.x * maxScale;
                    float centerY = targetImgCenterPoint.y * maxScale;

                    //真实设备上的缩放比例
                    float scaleX = this.getMeasuredWidth() / (float) widthWanted;
                    float scaleY = this.getMeasuredHeight() / (float) heihtWanted;
                    float realScale = Math.min(scaleX, scaleY);

                  /*  Log.e("ckTrac", "====>calculateClickRect canvas_W=" + canvas.getWidth()
                            + ":canvas_H=" + canvas.getHeight());
                    Log.e("ckTrac", "====>calculateClickRect scaleX=" + scaleX + ":scaleY=" + scaleY + ":" +
                    realScale);*/

                    if (scaleX > scaleY) {
                        centerX = centerX * realScale + (scaleX - scaleY) * widthWanted / 2;
                        centerY = centerY * realScale;
                    } else if (scaleY > scaleX) {
                        centerY = centerY * realScale + (scaleY - scaleX) * heihtWanted / 2;
                        centerX = centerX * realScale;
                    } else {
                        centerX = centerX * realScale;
                        centerY = centerY * realScale;
                    }

                    if (mTargetClickRect == null) {
                        mTargetClickRect = new RectF();
                    }

                    float imgWidht = targetImgDesinWidth;
                    float imgHeight = targetImgDesinHeight;

                    imgWidht = imgWidht * realScale;
                    imgHeight = imgHeight * realScale;

                    mTargetClickRect.left = centerX - imgWidht / 2.0f;
                    mTargetClickRect.top = centerY - imgHeight / 2.0f;
                    mTargetClickRect.right = mTargetClickRect.left + imgWidht;
                    mTargetClickRect.bottom = mTargetClickRect.top + imgHeight;
                    //Log.e("ckTrac","=====>clickArea:"+mTargetClickRect);
                }
            }
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            areaClickInfoInited = false;
            // Log.e("ckTrac", "====>onSizeChanged");
        }
    }

    /**
     * 读取 目标点击图片的 位置，尺寸信息
     */
    private void initTragetImgClickInfo() {
        if (!TextUtils.isEmpty(mTargetImgName) && mImgClickListener != null) {
            // step 1 获取目标图片尺寸信息
            getDesinSizeInfo();
            // 生成点击态图片资源
            generateTargetImgStateRes(targetImgDesinWidth, targetImgDesinHeight);
            // step 2 找到目标图片对应的layer 信息
            Layer targetLayer = findTargetLayer();
            // step 3  读取layer中的 动画结束Position(中心位置)
            targetImgCenterPoint = findTargetImgEndPoint(targetLayer);
            //Log.e("ckTrac","=====>initTragetImgClickInfo:targetImgCenterPoint="+targetImgCenterPoint);
        }
    }

    /**
     * 设置目标图片点击态资源
     *
     * @param norResId  非点击态资源图片id
     * @param presResId 点击态资源图片id
     */
    public void setTargeImgRes(int norResId, int presResId) {
        mTargetImgClickResId = presResId;
        mTargetImgNorResId = norResId;
        if (targetImgDesinWidth != 0 && targetImgDesinHeight != 0) {
            generateTargetImgStateRes(targetImgDesinWidth, targetImgDesinHeight);
        }
    }

    /**
     * 找到目标图片 动画结束时 中心位置
     *
     * @param layer
     * @return
     */
    private PointF findTargetImgEndPoint(Layer layer) {
        PointF centerPoint = null;
        if (layer != null) {
            try {
                Class clzz = layer.getClass();
                Field transform = clzz.getDeclaredField("transform");
                transform.setAccessible(true);
                AnimatableTransform data = (AnimatableTransform) transform.get(layer);
                AnimatableValue<PointF, PointF> positions = data.getPosition();

                if (positions instanceof AnimatablePathValue) {
                    try {
                        AnimatablePathValue pathValue = (AnimatablePathValue) positions;
                        Class clzz2 = positions.getClass();
                        Field keyframes = clzz2.getDeclaredField("keyframes");
                        keyframes.setAccessible(true);
                        List<Keyframe<PointF>> frames = (List<Keyframe<PointF>>) keyframes.get(pathValue);
                        if (frames.size() > 0) {
                            Keyframe<PointF> lastFram = frames.get(frames.size() - 1);
                            centerPoint = new PointF();
                            centerPoint.x = lastFram.endValue.x;
                            centerPoint.y = lastFram.endValue.y;
                            //Log.e("ckTrac","====>keyframe:"+lastFram);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return centerPoint;
    }

    /**
     * 找到目标图片对应的layer
     *
     * @return
     */
    private Layer findTargetLayer() {
        Layer targetLayer = null;
        LottieComposition composition = this.getComposition();
        if (composition != null && composition.getLayers() !=
                null && composition.getLayers().size() > 0) {
            Layer layer = null;
            for (int i = 0; i < composition.getLayers().size(); i++) {
                if (isTargetLayer(composition.getLayers().get(i))) {
                    targetLayer = composition.getLayers().get(i);
                    break;
                }
            }
        }
        return targetLayer;
    }

    /**
     * 判断是否是目标layer
     *
     * @param layer
     * @return
     */
    private boolean isTargetLayer(Layer layer) {
        boolean isTargetLayer = false;
        try {
            Class clzz = layer.getClass();
            Field refId = clzz.getDeclaredField("refId");
            refId.setAccessible(true);
            String refIdStr = (String) refId.get(layer);
            isTargetLayer = mTargetImgName.equals(refIdStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTargetLayer;
    }

    /**
     * 获取目标点击图片 的设计尺寸信息
     */
    private void getDesinSizeInfo() {
        if (targetImgDesinWidth == 0 || targetImgDesinHeight == 0) {
            if (this.getComposition() != null) {
                Map<String, LottieImageAsset> imgMap = this.getComposition().getImages();
                LottieImageAsset imageAsset = null;
                if (imgMap != null && imgMap.size() > 0  && (imageAsset=imgMap.get(mTargetImgName))!= null) {
                    targetImgDesinWidth = imageAsset.getWidth();
                    targetImgDesinHeight = imageAsset.getHeight();
                }
                Rect bounds = this.getComposition().getBounds();
                //json 文件中的 画布尺寸
                mWantedWidth = (int) (bounds.width() / Utils.dpScale());
                mWantedHeight = (int) ((bounds.height()) / Utils.dpScale());
            }
        }
    }


    /**
     * 读取 设置的点击态图片资源，并缩放为 目标图片尺寸
     *
     * @param imgWidth  :目标图片宽
     * @param imgHeight :目标图片高
     */
    private void generateTargetImgStateRes(int imgWidth, int imgHeight) {
        if (imgWidth > 0 && imgHeight > 0) {
            if (mTargetImgClickResId != 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                targetBitmapPres = DrawableHelper.bitmapFromResource(getResources(), mTargetImgClickResId);
            }
            if (mTargetImgNorResId != 0) {
                targetBitmapNor = DrawableHelper.bitmapFromResource(getResources(), mTargetImgNorResId);
            }
            //缩放到目标图片尺寸
            if (targetBitmapPres != null) {
                targetBitmapPres = Bitmap.createScaledBitmap(targetBitmapPres, imgWidth, imgHeight, true);
            }
            if (targetBitmapNor != null) {
                targetBitmapNor = Bitmap.createScaledBitmap(targetBitmapNor, imgWidth, imgHeight, true);
                fakeBitmap = targetBitmapNor;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!TextUtils.isEmpty(mTargetImgName)) {
            boolean enventCounsumed = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (postionInTargetImgArea(event)) {
                        updateTargeImgArea(STATE_PRESS);
                        enventCounsumed = true;
                    } else {
                        enventCounsumed = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    updateTargeImgArea(STATE_NOR);
                    if (postionInTargetImgArea(event)) {
                        if (mImgClickListener != null) {
                            mImgClickListener.onImgClick();
                        }
                        enventCounsumed = true;
                    } else {
                        enventCounsumed = false;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    updateTargeImgArea(STATE_NOR);
                    break;
                default:
                    break;
            }
            return enventCounsumed;
        } else {
            return super.onTouchEvent(event);
        }
    }

    /**
     * 更新目标区域点击态的绘制
     *
     * @param state
     */
    private void updateTargeImgArea(int state) {
        if (mTargetImgState != state) {
            mTargetImgState = state;
            if (mTargetImgState == STATE_NOR && targetBitmapNor != null) {
                if (!DRAW_FAKE_IMG) {
                    updateBitmap(mTargetImgName, targetBitmapNor);
                } else {
                    fakeBitmap = targetBitmapNor;
                    invalidate();
                }
            } else if (mTargetImgState == STATE_PRESS && targetBitmapPres != null) {
                if (!DRAW_FAKE_IMG) {
                    updateBitmap(mTargetImgName, targetBitmapPres);
                } else {
                    fakeBitmap = targetBitmapPres;
                    invalidate();
                }
            }
        }
    }

    /**
     * 交互事件是否在目标图片区域
     *
     * @param event
     * @return
     */
    private boolean postionInTargetImgArea(MotionEvent event) {
        return mTargetClickRect != null && mTargetClickRect.contains(event.getX(), event.getY());
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (targetBitmapNor != null) {
            targetBitmapNor.recycle();
            targetBitmapNor = null;
        }
        if (targetBitmapPres != null) {
            targetBitmapPres.recycle();
            targetBitmapPres = null;
        }
        if (mFakeBitmapRect != null) {
            mFakeBitmapRect = null;
        }
        mImgClickListener = null;
    }
}

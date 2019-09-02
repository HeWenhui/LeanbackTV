package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

/**
 * Created by linyuqiang on 2018/5/3.
 * 站立直播聊天记录渐隐
 */
public class StandLiveEdgeListView extends ListView {
    public String TAG = "StandLiveEdgeListView";
    protected Logger logger = LiveLoggerFactory.getLogger(getClass().getSimpleName());
    public Paint paint;
    public Matrix matrix;
    public Shader shader;
    boolean drawShader = false;
    /** 阴影高度 */
    int drawHeight = 0;
    /** 最上面child 布局的top */
    int drawTop = 0;
    boolean isos9 = false;

    public StandLiveEdgeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setVerticalFadingEdgeEnabled(true);
    }

    private void init() {
        paint = new Paint();
        matrix = new Matrix();
        shader = new LinearGradient(0, 0, 0, 1, 0xff000000, 0, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            isos9 = true;
//            return;
        }
        setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {

            @Override
            public void onChildViewAdded(View parent, View child) {
                getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        int childCount = getChildCount();
                        if (childCount == 0) {
                            return false;
                        }
                        int height = getHeight();
                        int totalChildHeight = 0;
                        int screenHeight = ScreenUtils.getScreenHeight();
                        int index = -1;
                        for (int i = childCount - 1; i >= 0; i--) {
                            totalChildHeight += getChildAt(i).getHeight();
                            if (totalChildHeight > screenHeight / 2) {
                                if (index == -1) {
                                    index = i;
                                }
                            }
                        }
                        logger.d("onPreDraw:height=" + height + ",i=" + index + ",totalChildHeight=" + totalChildHeight);
                        if (totalChildHeight > ScreenUtils.getScreenHeight() / 2) {
                            drawShader = true;
                            drawHeight = height - ScreenUtils.getScreenHeight() / 2;
                            logger.d("onPreDraw:drawTop=" + drawTop + ",drawHeight=" + drawHeight);

                            matrix.setScale(1, drawHeight);
                            matrix.postTranslate(getLeft(), 0);
                            shader.setLocalMatrix(matrix);

                            invalidate();
                        } else {
                            boolean olddrawShader = drawShader;
                            drawShader = false;
                            if (olddrawShader) {
                                invalidate();
                            }
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawShader) {
            int saveCount = canvas.getSaveCount();//好像没用
            if (isos9) {
                RectF bounds = new RectF(getLeft(), 0, getRight(), getBottom());
                canvas.saveLayerAlpha(bounds, 255);
            } else {
                final int flags = Canvas.HAS_ALPHA_LAYER_SAVE_FLAG;
                canvas.saveLayer(getLeft(), 0, getRight(), drawHeight, null, flags);
            }
            super.draw(canvas);
            logger.d("draw:top=" + getTop());
            canvas.drawRect(0, 0, getWidth(), drawHeight, paint);
            canvas.restoreToCount(saveCount);
        } else {
            super.draw(canvas);
        }
    }
}

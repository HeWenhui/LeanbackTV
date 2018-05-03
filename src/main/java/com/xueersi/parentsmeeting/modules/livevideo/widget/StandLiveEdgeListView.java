package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

/**
 * Created by linyuqiang on 2018/5/3.
 * 站立直播聊天记录渐隐
 */
public class StandLiveEdgeListView extends ListView {
    public String TAG = "StandLiveEdgeListView";
    public Paint paint;
    public Matrix matrix;
    public Shader shader;
    boolean drawShader = false;
    /**阴影高度*/
    int drawHeight = 0;
    /**最上面child 布局的top*/
    int drawTop = 0;

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
                        Loger.d(TAG, "onPreDraw:height=" + height + ",i=" + index + ",totalChildHeight=" + totalChildHeight);
                        if (index != -1) {
                            View child = getChildAt(0);
                            int[] outLocation = new int[2];
                            child.getLocationOnScreen(outLocation);
                            drawTop = outLocation[1];
                            child = getChildAt(index);
                            child.getLocationOnScreen(outLocation);

                            drawShader = true;
                            drawHeight = outLocation[1] + child.getHeight();
                            Loger.d(TAG, "onPreDraw:drawTop=" + drawTop + ",outLocation=" + outLocation[1] + "," + child.getHeight() + ",drawHeight=" + drawHeight);

                            matrix.setScale(1, drawHeight);
                            matrix.postTranslate(getLeft(), getTop());
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
    public void draw(Canvas canvas) {
        if (drawShader) {
            int saveCount = canvas.getSaveCount();//好像没用
            final int flags = Canvas.HAS_ALPHA_LAYER_SAVE_FLAG;
            canvas.saveLayer(getLeft(), getTop(), getRight(), getTop() + drawHeight + drawTop, null, flags);
            super.draw(canvas);
            Loger.d(TAG, "draw");
            canvas.drawRect(0, drawTop, getWidth(), drawHeight, paint);
            canvas.restoreToCount(saveCount);
        } else {
            super.draw(canvas);
        }
    }
}

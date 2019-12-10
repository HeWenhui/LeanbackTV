package com.xueersi.parentsmeeting.modules.livevideo.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.widget.TextGradientDrawable;
import com.xueersi.parentsmeeting.widget.VericalImageSpan;

public class DrawUtil {

    private static final String TAG = "DrawUtil";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    public static class DrawProperty {
        String label;
        int bgColor;
        int stColor;
        int tColor;
        //dp
        int tH = 16;
        int tW = 16;
        int tSz = 11;
        int stW = 2;
        int cR = 4;

        boolean autoMeasure = false;
        //dp
        int measureExtraWidth;

        public DrawProperty setLabelText(String labelText) {
            this.label = labelText;
            return this;
        }

        public DrawProperty setLabelBackgroundColor(int color) {
            this.bgColor = color;
            return this;
        }

        public DrawProperty setLabelStrokeColor(int color) {
            this.stColor = color;
            return this;
        }

        public DrawProperty setLabelTextColor(int color) {
            this.tColor = color;
            return this;
        }

        public DrawProperty setLabelTextWidth(int elementWidth) {
            this.tW = elementWidth;
            return this;
        }

        public DrawProperty setLabelTextHeight(int elementHeight) {
            this.tH = elementHeight;
            return this;
        }

        public DrawProperty setLabelTextSize(int textSize) {
            this.tSz = textSize;
            return this;
        }

        public DrawProperty setLabelStrokeWidth(int strokeWidth) {
            this.stW = strokeWidth;
            return this;
        }

        public DrawProperty setLabelCornerRadius(int cornerRadius) {
            this.cR = cornerRadius;
            return this;
        }

        public DrawProperty setLabelAutoMeasureWidth(boolean autoMeasure) {
            this.autoMeasure = autoMeasure;
            return this;
        }

        public DrawProperty setLabelMeasureExtraWidth(int extraWidth) {
            this.measureExtraWidth = extraWidth;
            return this;
        }
    }

    public static DrawProperty createDrawProperty() {
        return new DrawProperty();
    }

    public static Drawable createDrawable(String label, int bgColor, int textColor, int strokeColor) {
//        int length = label.length();
//        TextGradientDrawable draw = new TextGradientDrawable();
//        draw.setShape(GradientDrawable.RECTANGLE);//形状
//        draw.setColor(bgColor);//填充颜色
//        draw.setCornerRadius(4);//圆角
//        draw.setStroke(2, strokeColor);
//        int width = SizeUtils.Dp2Px(ContextManager.getContext(), 14 * length);
//        int height = SizeUtils.Dp2Px(ContextManager.getContext(), 16);
//        draw.setSize(width, height);
//        draw.setDrawText(label);
//        draw.setDrawTextColor(textColor);
//        draw.setDrawTextSize(SizeUtils.Dp2Px(ContextManager.getContext(), 11));
//        draw.setBounds(0, 0, draw.getIntrinsicWidth(), draw.getIntrinsicHeight());
        return createDrawable(label, bgColor, textColor, strokeColor, 16, 16, 11, 2, 4);
    }

    public static Drawable createDrawable(String label, int bgColor, int textColor, int tH, int tW) {
        return createDrawable(label, bgColor, textColor, bgColor, tH, tW, 11, 2, 4);
    }

    public static Drawable createDrawable(String label, int bgColor, int textColor, int strokeColor, int tH, int tW, int tSz, int stW, int cR) {
        int length = label.length();
        TextGradientDrawable draw = new TextGradientDrawable();
        draw.setShape(GradientDrawable.RECTANGLE);//形状
        draw.setColor(bgColor);//填充颜色
        draw.setCornerRadius(cR);//圆角
        draw.setStroke(stW, strokeColor);
        int width = SizeUtils.Dp2Px(ContextManager.getContext(), tW * length);
        int height = SizeUtils.Dp2Px(ContextManager.getContext(), tH);
        draw.setSize(width, height);
        draw.setDrawText(label);
        draw.setDrawTextColor(textColor);
        draw.setDrawTextSize(SizeUtils.Dp2Px(ContextManager.getContext(), tSz));
        draw.setBounds(0, 0, draw.getIntrinsicWidth(), draw.getIntrinsicHeight());
        return draw;
    }

    public static Drawable createDrawable(DrawProperty drawProperty) {
        int length = drawProperty.label.length();
        TextGradientDrawable draw = new TextGradientDrawable();
        draw.setShape(GradientDrawable.RECTANGLE);//形状
        draw.setColor(drawProperty.bgColor);//填充颜色
        draw.setCornerRadius(drawProperty.cR);//圆角
        draw.setStroke(drawProperty.stW, drawProperty.stColor);
        draw.setDrawText(drawProperty.label);
        draw.setDrawTextColor(drawProperty.tColor);
        draw.setDrawTextSize(SizeUtils.Dp2Px(ContextManager.getContext(), drawProperty.tSz));

        int width = SizeUtils.Dp2Px(ContextManager.getContext(), drawProperty.tW * length);
        int height = SizeUtils.Dp2Px(ContextManager.getContext(), drawProperty.tH);
        logger.d("calculate text width: " + width);
        if (drawProperty.autoMeasure) {
            Paint paint = draw.getTextPaint();
            width = (int) paint.measureText(drawProperty.label) + SizeUtils.Dp2Px(ContextManager.getContext(), drawProperty.measureExtraWidth);
            logger.d("measure text width: " + width);
        }
        draw.setSize(width, height);
        draw.setBounds(0, 0, draw.getIntrinsicWidth(), draw.getIntrinsicHeight());
        return draw;
    }


    public static Drawable create(String name, int bgColorId, int textColorId) {
        Drawable drawable = null;
        try {
            DrawUtil.DrawProperty drawProperty = DrawUtil.createDrawProperty();
            drawProperty.setLabelBackgroundColor(BaseApplication.getContext().getResources().getColor(bgColorId));
            drawProperty.setLabelTextColor(BaseApplication.getContext().getResources().getColor(textColorId));
            drawProperty.setLabelCornerRadius(SizeUtils.Dp2Px(BaseApplication.getContext(), 2));
            drawProperty.setLabelAutoMeasureWidth(true);
            drawProperty.setLabelText(name);
            drawProperty.setLabelMeasureExtraWidth(SizeUtils.Dp2Px(BaseApplication.getContext(), 2));
            drawable = DrawUtil.createDrawable(drawProperty);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

}

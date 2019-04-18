package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

/**
 * Created by linyuqiang on 2018/5/4.
 * 数字描边
 */
public class TextStrokeUtil {
    static String TAG = "TextStrokeUtil";
    protected static Logger logger = LoggerFactory.getLogger(TAG);
    public static Bitmap createTextStroke(String num, Typeface fontFace, float textSize, int textColor, int stroke, int strokeColor) {
        Paint paintOut2 = new Paint();
        paintOut2.setAntiAlias(true);
        paintOut2.setTextSize(textSize);
        paintOut2.setStrokeWidth(1);
        paintOut2.setTypeface(fontFace);
        paintOut2.setFakeBoldText(true);

        float widthOut = paintOut2.measureText(num) + stroke * 2;
        float heightOut = PaintTextUtil.getTextHeitht(paintOut2);

        float textHeight = heightOut + stroke * 2;
        int baseline = PaintTextUtil.getBaseline(textHeight, paintOut2);

        int x, y;

        Bitmap bitmap = Bitmap.createBitmap((int) (widthOut), (int) textHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);

        paintOut2.setColor(textColor);
        canvas2.drawText(num, stroke, baseline, paintOut2);

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//        paintOut2.setStyle(Paint.Style.FILL);
//        paintOut2.setColor(Color.CYAN);
//        canvas2.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paintOut2);

        paintOut2.setColor(strokeColor);
        long before = System.currentTimeMillis();
        for (int j = 0; j < pixels.length; j++) {
            int pixel = pixels[j];
            x = j % bitmap.getWidth();
            y = j / bitmap.getWidth();
            if (pixel == textColor) {
                canvas2.drawCircle(x, y, stroke, paintOut2);
            }
        }
//        paintOut2.setColor(bianColor);
//        canvas2.drawText(num, stroke, baseline, paintOut2);
        logger.d( "createTextStroke:time=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int noStrokeCount = 0;
        for (int j = 0; j < pixels.length; j++) {
            int pixel = pixels[j];
            x = j % bitmap.getWidth();
            y = j / bitmap.getWidth();
            int index = y * bitmap.getWidth() + x;
            if (pixel != strokeColor) {
                noStrokeCount++;
                boolean isInnerX = false;
                boolean isInnerY = false;
                boolean left = false;
                boolean right = false;
                boolean top = false;
                boolean bottom = false;
                int range = (int) (textHeight / 2);
                for (int k = 0; k < range; k++) {
                    int newX = x - k;
                    if (newX <= 0 || newX >= bitmap.getWidth()) {
                        isInnerX = false;
                        break;
                    } else {
                        int pixel2 = bitmap.getPixel(newX, (int) y);
                        if (pixel2 == strokeColor) {
                            left = true;
                            break;
                        }
                    }
                }
                if (left) {
                    for (int k = 0; k < range; k++) {
                        int newX = x + k;
                        if (newX <= 0 || newX >= bitmap.getWidth()) {
                            isInnerX = false;
                            break;
                        } else {
                            int pixel2 = bitmap.getPixel(newX, (int) y);
                            if (pixel2 == strokeColor) {
                                right = true;
                                break;
                            }
                        }
                    }
                    if (right) {
                        isInnerX = true;
                        for (int k = 0; k < range; k++) {
                            int newY = y - k;
                            if (newY <= 0 || newY >= bitmap.getHeight()) {
                                isInnerY = false;
                                break;
                            } else {
                                int pixel2 = bitmap.getPixel((int) x, newY);
                                if (pixel2 == strokeColor) {
                                    top = true;
                                    break;
                                }
                            }
                        }
                        for (int k = 0; k < range; k++) {
                            int newY = y + k;
                            if (newY <= 0 || newY >= bitmap.getHeight()) {
                                isInnerY = false;
                                break;
                            } else {
                                int pixel2 = bitmap.getPixel((int) x, newY);
                                if (pixel2 == strokeColor) {
                                    bottom = true;
                                    break;
                                }
                            }
                        }
                        if (top && bottom) {
                            isInnerY = true;
                        }
                    }
                }
                if (isInnerX && isInnerY) {
                    paintOut2.setColor(strokeColor);
                    canvas2.drawPoint(x, y, paintOut2);
                }
            }
        }
        logger.d( "createTextStroke:noStrokeCount=" + noStrokeCount);
        logger.d( "createTextStroke:time2=" + (System.currentTimeMillis() - before));
        paintOut2.setColor(textColor);
        canvas2.drawText(num, stroke, baseline, paintOut2);
        return bitmap;
    }
}

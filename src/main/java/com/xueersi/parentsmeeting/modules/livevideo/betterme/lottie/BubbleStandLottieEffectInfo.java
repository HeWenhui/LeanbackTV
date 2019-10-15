package com.xueersi.parentsmeeting.modules.livevideo.betterme.lottie;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.framework.drawable.DrawableHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * @Date on 2019/7/5 19:11
 * @Author zhangyuansun
 * @Description
 */
public class BubbleStandLottieEffectInfo extends LottieEffectInfo {
    private static String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/bubble_stand";
    private static String IMAGE_RES_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
    private static String JSON_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
    private Context mContext;
    String current;
    String target;
    boolean isIncrease;
    boolean isDecrease;


    public BubbleStandLottieEffectInfo(Context context, String current, String target, boolean isIncrease, boolean
            isDecrease) {
        super(IMAGE_RES_PATH, JSON_PATH, "img_0.png", "img_1.png", "img_2.png", "img_3.png");
        this.mContext = context;
        this.current = current;
        this.target = target;
        this.isIncrease = isIncrease;
        this.isDecrease = isDecrease;
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        if ("img_3.png".equals(fileName)) {
            Bitmap bitmap = createBitmap(width, height);
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }

    public Bitmap createBitmap(int width, int height) {
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            SpannableString message;
            if (current != null) {
                String currentMessage = current;
                String targetMessage = target;
                message = new SpannableString(currentMessage + "  " + targetMessage);
                if (isIncrease) {
                    float iconHeight = height * 12f / 37f;
                    Bitmap increaseBitmap = DrawableHelper.bitmapFromResource(mContext.getResources(), R.drawable
                            .app_livevideo_enteampk_benchangchengjiu_tips_rise_bg_nor);
                    increaseBitmap = Bitmap.createScaledBitmap(increaseBitmap, (int) (iconHeight * 8 / 9), (int)
                            iconHeight, true);
                    ImageSpan imageSpan = new ImageSpan(mContext, increaseBitmap, ImageSpan.ALIGN_BASELINE);
                    message.setSpan(imageSpan, currentMessage.length(), currentMessage.length() + 1, SpannableString
                            .SPAN_INCLUSIVE_EXCLUSIVE);
                } else if (isDecrease) {
                    float iconHeight = height * 12f / 37f;
                    Bitmap decreaseBitmap = DrawableHelper.bitmapFromResource(mContext.getResources(), R.drawable
                            .app_livevideo_enteampk_benchangchengjiu_tips_drop_bg_nor);
                    decreaseBitmap = Bitmap.createScaledBitmap(decreaseBitmap, (int) (iconHeight * 8 / 9), (int)
                            iconHeight, true);
                    ImageSpan imageSpan = new ImageSpan(mContext, decreaseBitmap, ImageSpan.ALIGN_BASELINE);
                    message.setSpan(imageSpan, currentMessage.length(), currentMessage.length() + 1, SpannableString
                            .SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    message.setSpan("", currentMessage.length(), currentMessage.length() + 1, SpannableString
                            .SPAN_INCLUSIVE_EXCLUSIVE);
                }
            } else {
                message = new SpannableString(target);
            }
            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout
                    .layout_en_betterme_bubble_stand, null);
            textView.setText(message);
            float size = height * 12f / 37f / ScreenUtils.getScreenDensity();
            textView.setTextSize(size);
            textView.setPadding(0, (int) (height * 15f / 37f), 0, 0);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
            textView.layout(0, 0, width, height);
            textView.draw(canvas);
            int a = 0;
            int b = a;
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

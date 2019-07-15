package com.xueersi.parentsmeeting.modules.livevideo.betterme.lottie;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * @Date on 2019/7/5 19:11
 * @Author zhangyuansun
 * @Description
 */
public class BubbleLottieEffectInfo extends LottieEffectInfo {
    private static String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/bubble";
    private static String IMAGE_RES_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
    private static String JSON_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
    private Context mContext;
    private String message;

    public BubbleLottieEffectInfo(Context context, String message) {
        super(IMAGE_RES_PATH, JSON_PATH, "img_0.png", "img_1.png");
        this.mContext = context;
        this.message = message;
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        if ("img_1.png".equals(fileName)) {
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
            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_en_betterme_bubble,
                    null);
            textView.setText(message);
            float size = height * 10f / 26f / ScreenUtils.getScreenDensity();
            textView.setTextSize(size);
            textView.setPadding(0, (int) (height * 6f / 26f), 0, 0);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
            textView.layout(0, 0, width, height);
            textView.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

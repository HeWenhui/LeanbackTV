package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.layer.Layer;
import com.xueersi.xesalib.utils.log.Loger;

import java.io.IOException;
import java.util.List;

/**
 * Created by lyqai on 2018/3/23.
 */

public class StandLiveHeadView extends LottieAnimationView {
    Paint paint;
    private String TAG = "StandLiveHeadView";
    String name;

    public StandLiveHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextSize(24);
        paint.setColor(Color.WHITE);
    }

    public void setHead(String url) {

    }

    public void setName(String name) {
        this.name = name;
        if (getComposition() == null) {
            return;
        }
        updateName();
        updateHead();
    }

    public void setComposition(@NonNull LottieComposition composition) {
        super.setComposition(composition);
        updateName();
        updateHead();
        List<Layer> layers = composition.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Loger.d(TAG, "setComposition:layer=" + layer.getLayerType());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LottieComposition composition = getComposition();
        if (composition == null) {
            return;
        }
        List<Layer> layers = composition.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Loger.d(TAG, "onDraw:layer=" + layer.getLayerType());
        }
    }

    /**
     * 设置名字
     */
    public void updateName() {
        String num = "abcdefghijk";
        num = name;
        AssetManager manager = getContext().getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/head/img_3.png"));
            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("Images/head/img_1.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(24);
            paint.setColor(Color.WHITE);
            float width = paint.measureText(num);
            canvas.drawText(num, img_3Bitmap.getWidth() / 2 + 5, img_7Bitmap.getHeight() / 2 + paint.measureText("a") / 2, paint);
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
//            e.printStackTrace();
            return;
        }
        updateBitmap("image_3", img_7Bitmap);
    }

    public void updateHead() {
        String num = "abcdefghijk";
        num = name;
        AssetManager manager = getContext().getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/head/img_1.png"));
            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("Images/head/img_1.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(24);
            paint.setColor(Color.WHITE);
            float width = paint.measureText(num);
            canvas.drawText(num, img_3Bitmap.getWidth() / 2 + 5, img_7Bitmap.getHeight() / 2 + paint.measureText("a") / 2, paint);
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
//            e.printStackTrace();
            return;
        }
        updateBitmap("image_1", img_7Bitmap);
    }
}

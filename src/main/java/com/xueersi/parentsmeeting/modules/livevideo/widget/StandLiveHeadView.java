package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.layer.Layer;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.io.IOException;
import java.util.List;

/**
 * Created by linyuqiang on 2018/3/23.
 * 站立直播聊天消息头像Lottie动画
 */
public class StandLiveHeadView extends LottieAnimationView {
    Paint paint;
    private String TAG = "StandLiveHeadView";
    String name;
    String headUrl;
    int sysHeadId = R.drawable.bg_live_stand_message_sys;
    boolean isMine = true;
    LiveMessageEntity entity;
    boolean isSystem = false;

    public StandLiveHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextSize(24);
        paint.setColor(Color.WHITE);
    }

    public LiveMessageEntity getEntity() {
        return entity;
    }

    public void setEntity(LiveMessageEntity entity) {
        this.entity = entity;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public void setHead(String url) {
        this.headUrl = url;
        if (getComposition() == null) {
            return;
        }
        updateHeadUrl();
    }

    public void setHeadSys() {
        this.isSystem = true;
        if (getComposition() == null) {
            return;
        }
        updateHeadSys();
    }

    public void setName(String name) {
        this.name = name;
        if (getComposition() == null) {
            return;
        }
        updateName();
    }

    public void setComposition(@NonNull LottieComposition composition) {
        super.setComposition(composition);
        updateName();
        if (isSystem) {
            updateHeadSys();
        } else {
            updateHeadUrl();
        }
//        List<Layer> layers = composition.getLayers();
//        for (int i = 0; i < layers.size(); i++) {
//            Layer layer = layers.get(i);
//            Loger.d(TAG, "setComposition:layer=" + layer.getLayerType());
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LottieComposition composition = getComposition();
        if (composition == null) {
            return;
        }
//        List<Layer> layers = composition.getLayers();
//        for (int i = 0; i < layers.size(); i++) {
//            Layer layer = layers.get(i);
//            Loger.d(TAG, "onDraw:layer=" + layer.getLayerType());
//        }
    }

    public void updateBack() {
        Bitmap img_7Bitmap;
        AssetManager manager = getContext().getAssets();
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("live_stand/lottie/head/img_4.png"));
            updateBitmap("image_3", img_7Bitmap);
        } catch (IOException e) {
            e.printStackTrace();
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
            if (isMine) {
                img_7Bitmap = BitmapFactory.decodeStream(manager.open("live_stand/lottie/head/img_3.png"));
            } else {
                img_7Bitmap = BitmapFactory.decodeStream(manager.open("live_stand/lottie/head/img_4.png"));
            }
            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("live_stand/lottie/head/img_1.png"));
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

    public void updateHeadUrl() {
        Bitmap headBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        updateHead(headBitmap);
        ImageLoader.with(getContext()).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                updateHead(headBitmap);
            }

            @Override
            public void onFail() {
                Loger.e(TAG, "onFail");
            }
        });
    }

    public void updateHeadSys() {
        Bitmap headBitmap = BitmapFactory.decodeResource(getResources(), sysHeadId);
        updateHead(headBitmap);
    }

    private void updateHead(Bitmap headBitmap) {
        AssetManager manager = getContext().getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("live_stand/lottie/head/img_1.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(24);
            paint.setColor(Color.WHITE);

            float scaleWidth = ((float) img_7Bitmap.getHeight() - 5) / headBitmap.getHeight();
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleWidth);
            Bitmap scalHeadBitmap = Bitmap.createBitmap(headBitmap, 0, 0, headBitmap.getWidth(), headBitmap.getHeight(), matrix, true);
            int left = (img_7Bitmap.getWidth() - scalHeadBitmap.getWidth()) / 2;
            canvas.drawBitmap(scalHeadBitmap, left, left, null);
            scalHeadBitmap.recycle();
//                    headBitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            Loger.e(TAG, "updateHead", e);
//            e.printStackTrace();
            return;
        }
        updateBitmap("image_1", img_7Bitmap);
    }
}

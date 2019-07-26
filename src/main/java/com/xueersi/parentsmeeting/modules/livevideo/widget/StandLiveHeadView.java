package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;

import java.io.IOException;

/**
 * Created by linyuqiang on 2018/3/23.
 * 站立直播聊天消息头像Lottie动画
 */
public class StandLiveHeadView extends LottieAnimationView {
    Paint paint;
    private String TAG = "StandLiveHeadView";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    String name;
    String headUrl;
    int sysHeadId = R.drawable.bg_live_stand_message_sys;
    boolean isMine = true;
    LiveMessageEntity entity;
    LiveMessageEntity lastEntity;
    boolean isSystem = false;
    private LogToFile logToFile;

    public StandLiveHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextSize(24);
        paint.setColor(Color.WHITE);
        logToFile = new LogToFile(context,TAG);
    }

    public LiveMessageEntity getEntity() {
        return entity;
    }

    public void setEntity(LiveMessageEntity entity) {
        lastEntity = this.entity;
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

    @Override
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
//            logger.d( "setComposition:layer=" + layer.getLayerType());
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
//            logger.d( "onDraw:layer=" + layer.getLayerType());
//        }
    }

    /**
     * 设置名字
     */
    public void updateName() {
        String num = StandLiveTextView.getShortName(name);
        AssetManager manager = getContext().getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/image/img_3.png"));
            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/image/img_1.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(24);
            if (isMine) {
                paint.setColor(Color.WHITE);
            } else {
                paint.setColor(0xffA56202);
            }
            float width = paint.measureText(num);
            canvas.drawText(num, img_3Bitmap.getWidth() / 2 + 5, img_7Bitmap.getHeight() / 2 + paint.measureText("学") / 2 - 5, paint);
            Bitmap oldBitmap = img_7Bitmap;
            img_7Bitmap = creatBitmap;
            oldBitmap.recycle();
            img_3Bitmap.recycle();
        } catch (IOException e) {
//            e.printStackTrace();
            return;
        }
        updateBitmap("image_3", img_7Bitmap);
    }

    public void updateTail(){

    }

    public void updateHeadUrl() {
        if ((entity != null && lastEntity != null) && (!("" + entity.getHeadUrl()).equals(lastEntity.getHeadUrl()))) {
            Bitmap headBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            updateHead(headBitmap);
        }
        final String finalHeadUrl = headUrl;
        ImageLoader.with(ContextManager.getContext()).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                if (("" + headUrl).equals(finalHeadUrl)) {
                    Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, logToFile, "updateHeadUrl", finalHeadUrl);
                    if (headBitmap == null) {
                        return;
                    }
                    updateHead(headBitmap);
                } else {
                    logger.d( "updateHeadUrl2:headUrl=" + headUrl + ",finalHeadUrl=" + finalHeadUrl);
                }
            }

            @Override
            public void onFail() {
                logger.e( "onFail");
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
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/image/img_0.png"));
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
            Bitmap oldBitmap = img_7Bitmap;
            img_7Bitmap = creatBitmap;
            oldBitmap.recycle();
//            if (!isSystem) {
//                headBitmap.recycle();
//            }
        } catch (IOException e) {
            logger.e( "updateHead", e);
//            e.printStackTrace();
            return;
        }
        updateBitmap("image_0", img_7Bitmap);
    }

    private void updateSegment(){
        Bitmap bitmap;
        try {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(BetterMeConfig.LEVEL_IMAGE_RES_NOSTAR[entity.getSegmentType()]);
            imageView.setBackgroundResource(BetterMeConfig.STAR_IMAGE_RES[entity.getSegmentType()][entity.getStar()]);

            Bitmap segmentBitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/image/img_1.png"));
            bitmap = Bitmap.createBitmap(segmentBitmap.getWidth(), segmentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(segmentBitmap.getWidth(), View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(segmentBitmap.getHeight(), View.MeasureSpec.EXACTLY);
            imageView.measure(widthMeasureSpec, heightMeasureSpec);
            imageView.layout(0, 0, segmentBitmap.getWidth(), segmentBitmap.getHeight());
            imageView.draw(canvas);
        } catch (IOException e) {
            logger.e( "updateSegment", e);
            return;
        }
        updateBitmap("image_1", bitmap);
    }
}

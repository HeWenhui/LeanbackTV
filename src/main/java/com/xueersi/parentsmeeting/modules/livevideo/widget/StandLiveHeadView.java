package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;

import java.io.IOException;

/**
 * Created by linyuqiang on 2018/3/23.
 * 站立直播聊天消息头像Lottie动画
 */
public class StandLiveHeadView extends LottieAnimationView {
    private String TAG = "StandLiveHeadView";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    int sysHeadId = R.drawable.bg_live_stand_message_sys;
    boolean isMine = true;
    LiveMessageEntity entity;
    LiveMessageEntity lastEntity;

    public void setSystem(boolean system) {
        isSystem = system;
    }

    boolean isSystem = false;
    private LogToFile logToFile;

    public StandLiveHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        logToFile = new LogToFile(context,TAG);
    }

    public LiveMessageEntity getEntity() {
        return entity;
    }

    public void setEntity(LiveMessageEntity entity) {
        lastEntity = this.entity;
        this.entity = entity;
        if (isSystem) {
            updateHeadSys();
        } else {
            updateHeadUrl();
        }
        updateName();
        updateSegment();
        updateTail();
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    @Override
    public void setComposition(@NonNull LottieComposition composition) {
        super.setComposition(composition);
        if (entity == null) {
           return;
        }
        if (isSystem) {
            updateHeadSys();
        } else {
            updateHeadUrl();
        }
        updateName();
        updateSegment();
        updateTail();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void updateHeadUrl() {
        if ((entity != null && lastEntity != null) && (!("" + entity.getHeadUrl()).equals(lastEntity.getHeadUrl()))) {
            Bitmap headBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            updateHead(headBitmap);
        }
        final String headUrl = entity.getHeadUrl();
        ImageLoader.with(ContextManager.getContext()).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                if (("" + headUrl).equals(entity.getHeadUrl())) {
                    Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, logToFile, "updateHeadUrl", headUrl);
                    if (headBitmap != null) {
                        updateHead(headBitmap);
                    }
                } else {
                    logger.d("updateHeadUrl:headUrl=" + entity.getHeadUrl() + ",finalHeadUrl=" + headUrl);
                }
            }

            @Override
            public void onFail() {
                logger.e( "onFail");
            }
        });
    }

    /**
     * 更新名字
     */
    public void updateName() {
        String shortName = StandLiveTextView.getShortName(entity.getSender());
        Bitmap bitmap;
        try {
            NoPaddingTextview textView = new NoPaddingTextview(getContext());
            Bitmap nameBitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/images/img_2.png"));
            bitmap = Bitmap.createBitmap(nameBitmap.getWidth(), nameBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(shortName);
            textView.setTextSize(nameBitmap.getHeight() * 9.8f / 10f / ScreenUtils.getScreenDensity());
            if (isMine) {
                textView.setTextColor(Color.WHITE);
            } else {
                textView.setTextColor(0xffA56202);
            }
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(nameBitmap.getWidth(), View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(nameBitmap.getHeight(), View.MeasureSpec.EXACTLY);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
            textView.layout(0, 0, nameBitmap.getWidth(), nameBitmap.getHeight());
            textView.draw(canvas);
        } catch (IOException e) {
            return;
        }
        updateBitmap("image_2", bitmap);
    }

    public void updateHeadSys() {
        Bitmap headBitmap = BitmapFactory.decodeResource(getResources(), sysHeadId);
        updateHead(headBitmap);
    }

    /**
     * 更新头像
     */
    private void updateHead(Bitmap headBitmap) {
        try {
            Bitmap priviousHeadBitmap = BitmapFactory.decodeStream(AssertUtil.open
                    ("live_stand/head_segment/images/img_0.png"));
            headBitmap = Bitmap.createScaledBitmap(headBitmap, priviousHeadBitmap.getWidth(), priviousHeadBitmap
                    .getHeight(), true);
        } catch (IOException e) {
            return;
        }
        updateBitmap("image_0", headBitmap);
    }

    /**
     * 更新段位徽章
     */
    private void updateSegment(){
        Bitmap bitmap;
        try {
            ImageView imageView = new ImageView(getContext());
            BetterMeUtil.addSegment(imageView, entity.getSegmentType(), entity.getStar());
            Bitmap segmentBitmap = BitmapFactory.decodeStream(AssertUtil.open
                    ("live_stand/head_segment/images/img_1.png"));
            bitmap = Bitmap.createBitmap(segmentBitmap.getWidth(), segmentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(segmentBitmap.getWidth(), View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(segmentBitmap.getHeight(), View.MeasureSpec.EXACTLY);
            imageView.measure(widthMeasureSpec, heightMeasureSpec);
            imageView.layout(0, 0, segmentBitmap.getWidth(), segmentBitmap.getHeight());
            imageView.draw(canvas);
        } catch (IOException e) {
            return;
        }
        updateBitmap("image_1", bitmap);
    }

    /**
     * 更新飘带
     */
    public void updateTail() {
        Bitmap bitmap;
        try {
            switch (entity.getSegmentType()) {
                case 1:
                    bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/images/tail_qingtong" +
                            ".png"));
                    break;
                case 2:
                    bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/images/tail_baiyin" +
                            ".png"));
                    break;
                case 3:
                    bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/images/tail_huangjin" +
                            ".png"));
                    break;
                case 4:
                    bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/images/tail_baijin" +
                            ".png"));
                    break;
                case 5:
                    bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/images/tail_zuanshi" +
                            ".png"));
                    break;
                case 6:
                    bitmap = BitmapFactory.decodeStream(AssertUtil.open
                            ("live_stand/head_segment/images/tail_zuiqiangxueba.png"));
                    break;
                default:
                    bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/head_segment/images/tail_huangjin" +
                            ".png"));
            }
        } catch (IOException e) {
            return;
        }
        updateBitmap("image_3", bitmap);
    }
}

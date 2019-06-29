package com.xueersi.parentsmeeting.modules.livevideo.question.page;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerResultStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.IArtsAnswerRsultDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;

import java.io.IOException;
import java.io.InputStream;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by David on 2018/11/20.
 */

public class StandLiveH5ResultPager extends BasePager implements IArtsAnswerRsultDisplayer {
    private AnswerResultEntity mData;

    public StandLiveH5ResultPager(Context context, AnswerResultEntity entity) {
        super(context);
        mData = entity;
        VideoResultEntity entitys = new VideoResultEntity();
        entitys.setResultType(mData.getResultType());
        entitys.setGoldNum(mData.getGold());
        onCommit(entitys,100);
    }
    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.layout_livevideo_stand_voice_result, null);
        return view;
    }

    @Override
    public void initData() {

    }

    private void onCommit(final VideoResultEntity entity, double speechDuration) {
        if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
            String path;
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals("1")) {
//                questionBll.initSelectAnswerRightResultVoice(entity);
                path = "live_stand_voice_my_right.json";
            } else {
//                questionBll.initFillinAnswerRightResultVoice(entity);
                path = "live_stand_voice_my_right.json";
            }
            LottieComposition.Factory.fromAssetFileName(mContext, path, new OnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                    if (lottieComposition == null) {
//                    if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
////                        questionBll.initSelectAnswerRightResultVoice(entity);
//                    } else {
////                        questionBll.initFillinAnswerRightResultVoice(entity);
//                    }
                        return;
                    }
                    final RelativeLayout rlResult = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_stand_voice_result, null);
                    LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
                    lottieAnimationView.setImageAssetsFolder("live_stand/lottie/voice_answer/my_right");
                    lottieAnimationView.setComposition(lottieComposition);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    rlResult.addView(lottieAnimationView, lp);
//                    final ViewGroup group = (ViewGroup) baseVoiceAnswerPager.getRootView();
//                    group.addView(rlResult);
//                questionBll.initQuestionAnswerReslut(rlResult);
                    lottieAnimationView.playAnimation();
                    setRightGold(mContext, lottieAnimationView, entity.getGoldNum());
                    final LiveSoundPool liveSoundPool = LiveSoundPool.createSoundPool();
                    final LiveSoundPool.SoundPlayTask task = StandLiveMethod.voiceRight(liveSoundPool);
                    rlResult.findViewById(R.id.iv_livevideo_speecteval_result_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            group.removeView(rlResult);
                            StandLiveMethod.onClickVoice(liveSoundPool);
//                        questionBll.removeQuestionAnswerReslut(rlResult);
                        }
                    });
//                    rlResult.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            group.removeView(rlResult);
//                        }
//                    }, 3200);
                    rlResult.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            logger.d("onViewDetachedFromWindow right");
//                        questionBll.removeBaseVoiceAnswerPager(baseVoiceAnswerPager);
                            liveSoundPool.stop(task);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    liveSoundPool.release();
                                }
                            }, 500);
                        }
                    });
                }
            });
        }
    }



    private void setRightGold(Context context, LottieAnimationView lottieAnimationView, int goldCount) {
        String num = "获得 " + goldCount + " 枚金币";
        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/voice_answer/my_right/img_22.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(48);
            paint.setColor(0xffCC6E12);
            Typeface fontFace = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, (img_7Bitmap.getHeight() + paint.measureText("a")) / 2, paint);
            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e( "setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_22", img_7Bitmap);
    }


    @Override
    public void showAnswerReuslt() {

    }

    @Override
    public void close() {

    }

    @Override
    public void remindSubmit() {

    }

    @Override
    public View getRootLayout() {
        return this.getRootView();
    }
}

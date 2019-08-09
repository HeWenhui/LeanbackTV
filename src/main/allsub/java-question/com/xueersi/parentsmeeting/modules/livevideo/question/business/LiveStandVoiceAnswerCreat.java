package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.CreateAnswerReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.VoiceAnswerStandPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;

import org.json.JSONObject;

import java.io.IOException;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by linyuqiang on 2018/4/3.
 * 直播创建语音答题
 */
public class LiveStandVoiceAnswerCreat implements BaseVoiceAnswerCreat {
    private static String TAG = "LiveStandVoiceAnswerCreat";
    protected static Logger logger = LoggerFactory.getLogger(TAG);
    private QuestionSwitch questionSwitch;
    private String headUrl;
    private String userName;
    private LiveAndBackDebug liveAndBackDebug;
    LivePagerBack livePagerBack;
    Context context;

    public LiveStandVoiceAnswerCreat(Context context, QuestionSwitch questionSwitch, LiveAndBackDebug liveAndBackDebug) {
        this.context = context;
        this.questionSwitch = questionSwitch;
        this.liveAndBackDebug = liveAndBackDebug;
    }

    public LiveStandVoiceAnswerCreat(Context context, LiveAndBackDebug liveAndBackDebug, QuestionSwitch questionSwitch, String headUrl, String userName) {
        this.context = context;
        this.liveAndBackDebug = liveAndBackDebug;
        this.questionSwitch = questionSwitch;
        this.headUrl = headUrl;
        this.userName = userName;
    }

    public void setLivePagerBack(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public BaseVoiceAnswerPager create(Context activity, VideoQuestionLiveEntity videoQuestionLiveEntity, JSONObject assess_ref, String type,
                                       LiveViewAction liveViewAction, SpeechUtils mIse) {
        VoiceAnswerStandLog.sno2(this.liveAndBackDebug, videoQuestionLiveEntity);
        VoiceAnswerStandPager voiceAnswerPager2 = new VoiceAnswerStandPager(activity, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.type, questionSwitch, headUrl, userName);
        voiceAnswerPager2.setIse(mIse);
        voiceAnswerPager2.setLivePagerBack(livePagerBack);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        liveViewAction.addView(LiveVideoLevel.LEVEL_QUES, voiceAnswerPager2.getRootView(), params);
        return voiceAnswerPager2;
    }

    @Override
    public void setViewLayoutParams(BaseVoiceAnswerPager baseVoiceAnswerPager, int rightMargin) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView().getLayoutParams();
//        if (rightMargin != params.rightMargin) {
//            params.rightMargin = rightMargin;
//            LayoutParamsUtil.setViewLayoutParams(baseVoiceAnswerPager.getRootView(), params);
//        }
    }

    @Override
    public CreateAnswerReslutEntity onAnswerReslut(final Context context, final AnswerRightResultVoice questionBll, final BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, final VideoResultEntity entity) {
        CreateAnswerReslutEntity createAnswerReslutEntity = new CreateAnswerReslutEntity();
        boolean isSuccess = false;
        final String type;
        boolean isNewArt = false;
        if (baseVideoQuestionEntity instanceof VideoQuestionLiveEntity) {
            final VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
            type = videoQuestionLiveEntity.type;
            isNewArt = videoQuestionLiveEntity.isNewArtsH5Courseware();
        } else {
            VideoQuestionEntity questionEntity = (VideoQuestionEntity) baseVideoQuestionEntity;
            if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == questionEntity.getvCategory()) {
                type = questionEntity.getVoiceQuestiontype();
            } else {
                type = questionEntity.getvQuestionType();
            }
            isNewArt = questionEntity.isNewArtsH5Courseware();
        }
        if ((isNewArt && entity.getResultType() == 2) || (!isNewArt && (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4))) {
            String path;
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
//                questionBll.initSelectAnswerRightResultVoice(entity);
                path = "live_stand_voice_my_right.json";
            } else {
//                questionBll.initFillinAnswerRightResultVoice(entity);
                path = "live_stand_voice_my_right.json";
            }
            final RelativeLayout rlResult = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_livevideo_stand_voice_result, null);
            createAnswerReslutEntity.resultView = rlResult;
            entity.setNewArt(isNewArt);
            LottieComposition.Factory.fromAssetFileName(context, path, new OnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                    if (lottieComposition == null) {

                        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
                            questionBll.initSelectAnswerRightResultVoice(entity);
                        } else {
                            questionBll.initFillinAnswerRightResultVoice(entity);
                        }
                        return;
                    }
                    LottieAnimationView lottieAnimationView = new LottieAnimationView(context);
                    lottieAnimationView.setImageAssetsFolder("live_stand/lottie/voice_answer/my_right");
                    lottieAnimationView.setComposition(lottieComposition);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    rlResult.addView(lottieAnimationView, lp);
//                    final ViewGroup group = (ViewGroup) baseVoiceAnswerPager.getRootView();
//                    group.addView(rlResult);
                    questionBll.initQuestionAnswerReslut(rlResult);
                    lottieAnimationView.playAnimation();
//                    setRightGold(context, lottieAnimationView, entity.getGoldNum(), entity.getEnergy());
                    setRightGoldEnergy(context, lottieAnimationView, entity.getGoldNum(), entity.getEnergy());
                    final LiveSoundPool liveSoundPool = LiveSoundPool.createSoundPool();
                    final LiveSoundPool.SoundPlayTask task = StandLiveMethod.voiceRight(liveSoundPool);
                    rlResult.findViewById(R.id.iv_livevideo_speecteval_result_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            group.removeView(rlResult);
                            StandLiveMethod.onClickVoice(liveSoundPool);
                            questionBll.removeQuestionAnswerReslut(rlResult);
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
                            questionBll.removeBaseVoiceAnswerPager(baseVoiceAnswerPager);
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
            isSuccess = true;
            UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(context, UpdateAchievement.class);
            if (updateAchievement != null) {
                updateAchievement.getStuGoldCount("onAnswerReslut", UpdateAchievement.GET_TYPE_QUE);
            }
            // 回答错误提示
        } else if ((isNewArt && entity.getResultType() == 0) || (!isNewArt && entity.getResultType() == QUE_RES_TYPE2)) {
            String path;
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
//                questionBll.initSelectAnswerWrongResultVoice(entity);
                path = "live_stand_voice_my_wrong.json";
            } else {
//                questionBll.initFillAnswerWrongResultVoice(entity);
                path = "live_stand_voice_my_wrong.json";
            }
            final RelativeLayout rlResult = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_livevideo_stand_voice_result_wrong, null);
            createAnswerReslutEntity.resultView = rlResult;
            LottieComposition.Factory.fromAssetFileName(context, path, new OnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                    if (lottieComposition == null) {
                        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
                            questionBll.initSelectAnswerWrongResultVoice(entity);
                        } else {
                            questionBll.initFillAnswerWrongResultVoice(entity);
                        }
                        return;
                    }
                    LottieAnimationView lottieAnimationView = new LottieAnimationView(context);
                    lottieAnimationView.setImageAssetsFolder("live_stand/lottie/voice_answer/my_wrong");
                    lottieAnimationView.setComposition(lottieComposition);
                    lottieAnimationView.playAnimation();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    rlResult.addView(lottieAnimationView, lp);
//                    final ViewGroup group = (ViewGroup) baseVoiceAnswerPager.getRootView();
//                    group.addView(rlResult);
                    questionBll.initQuestionAnswerReslut(rlResult);
                    setWrongTipEnergy(context, lottieAnimationView, entity.getStandardAnswer(), entity.getGoldNum(), entity.getEnergy());
//                    setWrongTip(context, lottieAnimationView, entity.getStandardAnswer());
                    final LiveSoundPool liveSoundPool = LiveSoundPool.createSoundPool();
                    final LiveSoundPool.SoundPlayTask task = StandLiveMethod.voiceWrong(liveSoundPool);
                    rlResult.findViewById(R.id.iv_livevideo_speecteval_result_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            group.removeView(rlResult);
                            StandLiveMethod.onClickVoice(liveSoundPool);
                            questionBll.removeQuestionAnswerReslut(rlResult);
                        }
                    });
//                    group.postDelayed(new Runnable() {
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
                            logger.d("onViewDetachedFromWindow error");
                            questionBll.removeBaseVoiceAnswerPager(baseVoiceAnswerPager);
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
            // 填空题部分正确提示
        }
        createAnswerReslutEntity.isSuccess = isSuccess;
        return createAnswerReslutEntity;
    }

    public static void setRightGold(Context context, LottieAnimationView lottieAnimationView, int goldCount, int energy) {
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
            logger.e("setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_22", img_7Bitmap);
    }

    public static void setRightGoldEnergy(Context context, LottieAnimationView lottieAnimationView, int goldCount, int energy) {
        View resultMine = LayoutInflater.from(context).inflate(R.layout.layout_live_stand_voice_right, null);
        TextView tv_livevideo_speecteval_result_gold = resultMine.findViewById(R.id.tv_livevideo_speecteval_result_gold);
        tv_livevideo_speecteval_result_gold.setText("+" + goldCount);
        TextView tv_livevideo_speecteval_result_energy = resultMine.findViewById(R.id.tv_livevideo_speecteval_result_energy);
        tv_livevideo_speecteval_result_energy.setText("+" + energy);

        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/voice_answer/my_right/img_22.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);

            int width = img_7Bitmap.getWidth();
            int height = img_7Bitmap.getHeight();
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            resultMine.measure(widthMeasureSpec, heightMeasureSpec);
            resultMine.layout(0, 0, width, height);
            resultMine.draw(canvas);

            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e("setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_22", img_7Bitmap);
    }

    public static void setWrongTipEnergy(Context context, LottieAnimationView lottieAnimationView, String standardAnswer, int goldCount, int energy) {
        String num = "正确答案: " + standardAnswer;
        View resultMine = LayoutInflater.from(context).inflate(R.layout.layout_live_stand_voice_wrong, null);
        TextView tv_livevideo_speecteval_result_answer = resultMine.findViewById(R.id.tv_livevideo_speecteval_result_answer);
        tv_livevideo_speecteval_result_answer.setText(num);
        TextView tv_livevideo_speecteval_result_gold = resultMine.findViewById(R.id.tv_livevideo_speecteval_result_gold);
        tv_livevideo_speecteval_result_gold.setText("+" + goldCount);
        TextView tv_livevideo_speecteval_result_energy = resultMine.findViewById(R.id.tv_livevideo_speecteval_result_energy);
        tv_livevideo_speecteval_result_energy.setText("+" + energy);

        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/voice_answer/my_wrong/img_5.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);

            int width = img_7Bitmap.getWidth();
            int height = img_7Bitmap.getHeight();
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            resultMine.measure(widthMeasureSpec, heightMeasureSpec);
            resultMine.layout(0, 0, width, height);
            resultMine.draw(canvas);

            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e("setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_5", img_7Bitmap);
    }

    public static void setWrongTip(Context context, LottieAnimationView lottieAnimationView, String standardAnswer) {
        String num = "正确答案: " + standardAnswer;
        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/voice_answer/my_wrong/img_5.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(48);
            paint.setColor(0xff5586A3);
            Typeface fontFace = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, (img_7Bitmap.getHeight() + paint.measureText("a")) / 2, paint);
            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e("setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_5", img_7Bitmap);
    }
}

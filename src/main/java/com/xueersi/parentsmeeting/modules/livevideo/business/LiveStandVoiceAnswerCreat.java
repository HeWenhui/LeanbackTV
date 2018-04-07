package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerStandPager;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;

import org.json.JSONObject;

import java.io.IOException;

import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by linyuqiang on 2018/4/3.
 * 直播创建语音答题
 */
public class LiveStandVoiceAnswerCreat implements BaseVoiceAnswerCreat {
    String TAG = "LiveStandVoiceAnswerCreat";

    @Override
    public BaseVoiceAnswerPager create(Activity activity, BaseVideoQuestionEntity baseVideoQuestionEntity, JSONObject assess_ref, String type,
                                       QuestionBll questionBll, RelativeLayout rlQuestionContent, SpeechEvaluatorUtils mIse, LiveAndBackDebug liveAndBackDebug) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        QuestionSwitch questionSwitch = questionBll.new LiveStandQuestionSwitchImpl();
        VoiceAnswerStandPager voiceAnswerPager2 = new VoiceAnswerStandPager(activity, baseVideoQuestionEntity, assess_ref, videoQuestionLiveEntity.type, questionSwitch, liveAndBackDebug);
        voiceAnswerPager2.setIse(mIse);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlQuestionContent.addView(voiceAnswerPager2.getRootView(), params);
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
    public boolean onAnswerReslut(final Context context, final QuestionBll questionBll, BaseVideoQuestionEntity baseVideoQuestionEntity, final VideoResultEntity entity) {
        boolean isSuccess = false;
        final VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
            String path;
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
//                questionBll.initSelectAnswerRightResultVoice(entity);
                path = "live_stand_voice_my_right.json";
            } else {
//                questionBll.initFillinAnswerRightResultVoice(entity);
                path = "live_stand_voice_my_right.json";
            }
            LottieComposition.Factory.fromAssetFileName(context, path, new OnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                    if (lottieComposition == null) {
                        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                            questionBll.initSelectAnswerRightResultVoice(entity);
                        } else {
                            questionBll.initFillinAnswerRightResultVoice(entity);
                        }
                        return;
                    }
                    LottieAnimationView lottieAnimationView = new LottieAnimationView(context);
                    lottieAnimationView.setImageAssetsFolder("Images/voice_answer/my_right");
                    lottieAnimationView.setComposition(lottieComposition);
                    questionBll.initQuestionAnswerReslut(lottieAnimationView);
                    lottieAnimationView.playAnimation();
                    setRightGold(context, lottieAnimationView, entity.getGoldNum());
                }
            });
            isSuccess = true;
            // 回答错误提示
        } else if (entity.getResultType() == QUE_RES_TYPE2) {
            String path;
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
//                questionBll.initSelectAnswerWrongResultVoice(entity);
                path = "live_stand_voice_my_wrong.json";
            } else {
//                questionBll.initFillAnswerWrongResultVoice(entity);
                path = "live_stand_voice_my_wrong.json";
            }
            LottieComposition.Factory.fromAssetFileName(context, path, new OnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                    if (lottieComposition == null) {
                        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                            questionBll.initSelectAnswerWrongResultVoice(entity);
                        } else {
                            questionBll.initFillAnswerWrongResultVoice(entity);
                        }
                        return;
                    }
                    LottieAnimationView lottieAnimationView = new LottieAnimationView(context);
                    lottieAnimationView.setImageAssetsFolder("Images/voice_answer/my_wrong");
                    lottieAnimationView.setComposition(lottieComposition);
                    lottieAnimationView.playAnimation();
                    questionBll.initQuestionAnswerReslut(lottieAnimationView);
                    setWrongTip(context, lottieAnimationView, entity.getStandardAnswer());
                }
            });
            // 填空题部分正确提示
        }
        return isSuccess;
    }

    private void setRightGold(Context context, LottieAnimationView lottieAnimationView, int goldCount) {
        String num = "获得 " + goldCount + " 枚金币";
        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/voice_answer/my_right/img_22.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(48);
            paint.setColor(0xffCC6E12);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, (img_7Bitmap.getHeight() + paint.measureText("a")) / 2, paint);
            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e(TAG, "setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_22", img_7Bitmap);
    }

    private void setWrongTip(Context context, LottieAnimationView lottieAnimationView, String standardAnswer) {
        String num = "正确答案: " + standardAnswer;
        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/voice_answer/my_wrong/img_5.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(48);
            paint.setColor(0xffCC6E12);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, 260 + (img_7Bitmap.getHeight() + paint.measureText("a")) / 2, paint);
            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e(TAG, "setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_5", img_7Bitmap);
    }
}

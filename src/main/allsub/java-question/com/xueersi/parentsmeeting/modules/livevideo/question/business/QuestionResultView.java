package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExperCourseResultPager;

import java.util.ArrayList;
import java.util.List;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by linyuqiang on 2017/12/19.
 * 语音答题结果显示
 */
public class QuestionResultView {

    public static View initSelectAnswerRightResultVoice(Context context, VideoResultEntity entity) {
//        int goldNum = entity.getGoldNum();
//        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout.pop_question_select_answer_voice_right, null, false);
//        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_anwer);
//        TextView tv_pop_question_answer_your_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_your_anwer);
//        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
//        tv_pop_question_answer_your_anwer.setText(entity.getYourAnswer());
//        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
//        tvGoldHint.setText("+" + goldNum);
//        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_right);
//                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
//                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
//                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
//                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight();
//                group.setLayoutParams(lp);
//                return false;
//            }
//        });
//        return popupWindow_view;
        if (entity.isExperience()) {
            return createViceResultViewExper(context, entity);
        }
        return createViceResultView(context, entity);

    }

    public static View initFillinAnswerRightResultVoice(Context context, VideoResultEntity entity) {
//        int goldNum = entity.getGoldNum();
//        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout.pop_question_fillin_answer_voice_right, null, false);
//        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_anwer);
//        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
//        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
//        tvGoldHint.setText("+" + goldNum);
//        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_right);
//                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
//                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
//                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
//                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight() / 2;
//                group.setLayoutParams(lp);
//                return false;
//            }
//        });
//        return popupWindow_view;
        if (entity.isExperience()) {
            return createViceResultViewExper(context, entity);
        }
        return createViceResultView(context, entity);

    }

    /** 语音答题回答错误 */
    public static View initSelectAnswerWrongResultVoice(Context context, VideoResultEntity entity) {
//        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout
// .pop_question_select_answer_wrong, null, false);
////        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id
// .tv_pop_question_answer_right_anwer);
////        TextView tv_pop_question_answer_your_anwer = (TextView) popupWindow_view.findViewById(R.id
// .tv_pop_question_answer_your_anwer);
////        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
////        tv_pop_question_answer_your_anwer.setText(entity.getYourAnswer());
////        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
////            @Override
////            public boolean onPreDraw() {
////                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_wrong);
////                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
////                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
////                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
////                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
////                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight();
////                group.setLayoutParams(lp);
////                return false;
////            }
////        });
////        return popupWindow_view;
        if (entity.isExperience()) {
            return createViceResultViewExper(context, entity);
        }
        return createViceResultView(context, entity);

    }

    /** 语音答题回答错误 */
    public static View initFillAnswerWrongResultVoice(Context context, VideoResultEntity entity) {
//        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout.pop_question_fillin_answer_voice_wrong, null, false);
//        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_anwer);
//        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
//        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_wrong);
//                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
//                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
//                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
//                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight() / 2;
//                group.setLayoutParams(lp);
//                return false;
//            }
//        });
//        return popupWindow_view;
        if (entity.isExperience()) {
            return createViceResultViewExper(context, entity);
        }
        return createViceResultView(context, entity);
    }

    /**
     * 语音答题结果
     *
     * @param context
     * @param entity
     * @return
     */
    private static View createViceResultView(Context context, VideoResultEntity entity) {
        UmsAgentManager.umsAgentDebug(context, "createViceResultView_result1", JSON.toJSONString(entity));
        AnswerResultEntity resultEntity = new AnswerResultEntity();
        resultEntity.setGold(entity.getGoldNum());
        AnswerResultEntity.Answer answer = new AnswerResultEntity.Answer();
        resultEntity.setEnergy(entity.getEnergy());
        resultEntity.isVoice = 1;
        // 用户答案
        if (!TextUtils.isEmpty(entity.getYourAnswer())) {
            List<String> userAnswer = new ArrayList<>();
            userAnswer.add(entity.getYourAnswer());
            answer.setBlankList(userAnswer);
        }
        // 标准答案
        if (!TextUtils.isEmpty(entity.getStandardAnswer())) {
            List<String> userAnswer = new ArrayList<>();
            userAnswer.add(entity.getStandardAnswer());
            answer.setRightAnswers(userAnswer);
        }
        // 如果是文科平台
        if (entity.isNewArt()) {
            if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE2) {
                resultEntity.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_CORRECT);
                answer.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_CORRECT);
            } else {
                resultEntity.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_ERRRO);
                answer.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_ERRRO);
            }
        } else {
            if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
                resultEntity.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_CORRECT);
                answer.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_CORRECT);
            } else {
                resultEntity.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_ERRRO);
                answer.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_ERRRO);

            }
        }

        List<AnswerResultEntity.Answer> answerList = new ArrayList<>();
        answerList.add(answer);
        resultEntity.setAnswerList(answerList);

        if (entity.isPreEnglish()) {
            ArtsPSEAnswerResultPager mDsipalyer = new ArtsPSEAnswerResultPager(context, resultEntity, null);
            return mDsipalyer.getRootView();
        } else {
            ArtsAnswerResultPager mDsipalyer = new ArtsAnswerResultPager(context, resultEntity, null);
            return mDsipalyer.getRootView();
        }
    }

    /**
     * 语音答题结果
     *
     * @param context
     * @param entity
     * @return
     */
    private static View createViceResultViewExper(Context context, VideoResultEntity entity) {
        UmsAgentManager.umsAgentDebug(context, "createViceResultView_result1", JSON.toJSONString(entity));
        AnswerResultEntity resultEntity = new AnswerResultEntity();
        resultEntity.setGold(entity.getGoldNum());
        PrimaryScienceAnswerResultEntity.Answer answer = new PrimaryScienceAnswerResultEntity.Answer();
        resultEntity.setEnergy(entity.getEnergy());
        resultEntity.isVoice = 1;
        // 用户答案
        if (!TextUtils.isEmpty(entity.getYourAnswer())) {
            answer.setMyAnswer(entity.getYourAnswer());
        }
        // 标准答案
        if (!TextUtils.isEmpty(entity.getStandardAnswer())) {
            answer.setRightAnswer(entity.getStandardAnswer());
        }
        PrimaryScienceAnswerResultEntity primaryScienceAnswerResultEntity = new PrimaryScienceAnswerResultEntity();
        primaryScienceAnswerResultEntity.setGold(entity.getGoldNum());
        // 如果是文科平台
        if (entity.isNewArt()) {
            if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE2) {
                primaryScienceAnswerResultEntity.setType(PrimaryScienceAnswerResultEntity.ABSLUTELY_RIGHT);
                answer.setRight(PrimaryScienceAnswerResultEntity.ABSLUTELY_RIGHT);
            } else {
                resultEntity.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_ERRRO);
            }
        } else {
            if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
                primaryScienceAnswerResultEntity.setType(PrimaryScienceAnswerResultEntity.ABSLUTELY_RIGHT);
                answer.setRight(PrimaryScienceAnswerResultEntity.ABSLUTELY_RIGHT);
            } else {
                resultEntity.setIsRight(ArtsAnswerResultPager.RESULT_TYPE_ERRRO);
            }
        }
        List<PrimaryScienceAnswerResultEntity.Answer> answerList = primaryScienceAnswerResultEntity.getAnswerList();
        answerList.add(answer);

        ExperCourseResultPager experCourseResultPager = new ExperCourseResultPager(context, null, primaryScienceAnswerResultEntity);
        return experCourseResultPager.getRootLayout();
    }

    public static View initArtsAnswerRightResultVoice(Context context, AnswerResultEntity answerResultEntity, AnswerResultStateListener stateListener) {
//        answerResultEntity.setIsRight(ArtsPSEAnswerResultPager.RESULT_TYPE_CORRECT);
//        ArrayList<AnswerResultEntity.Answer> answerList = new ArrayList<>();
//        AnswerResultEntity.Answer answer = new AnswerResultEntity.Answer();
//        answerList.add(answer);
//        answerResultEntity.setAnswerList(answerList);
        ArtsPSEAnswerResultPager artsPSEAnswerResultPager = new ArtsPSEAnswerResultPager(context, answerResultEntity, stateListener);
        return artsPSEAnswerResultPager.getRootView();
    }

}

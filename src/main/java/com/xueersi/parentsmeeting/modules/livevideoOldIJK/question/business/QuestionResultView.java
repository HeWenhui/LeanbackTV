package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.ArtsPSEAnswerResultPager;

/**
 * Created by lyqai on 2017/12/19.
 * 语音答题结果显示
 */
public class QuestionResultView {

    public static View initSelectAnswerRightResultVoice(Context context, VideoResultEntity entity) {
        int goldNum = entity.getGoldNum();
        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout.pop_question_select_answer_voice_right, null, false);
        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_anwer);
        TextView tv_pop_question_answer_your_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_your_anwer);
        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
        tv_pop_question_answer_your_anwer.setText(entity.getYourAnswer());
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
        tvGoldHint.setText("+" + goldNum);
        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_right);
                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight();
                group.setLayoutParams(lp);
                return false;
            }
        });
        return popupWindow_view;
    }

    public static View initFillinAnswerRightResultVoice(Context context, VideoResultEntity entity) {
        int goldNum = entity.getGoldNum();
        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout.pop_question_fillin_answer_voice_right, null, false);
        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_anwer);
        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
        tvGoldHint.setText("+" + goldNum);
        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_right);
                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight() / 2;
                group.setLayoutParams(lp);
                return false;
            }
        });
        return popupWindow_view;
    }

    /** 语音答题回答错误 */
    public static View initSelectAnswerWrongResultVoice(Context context, VideoResultEntity entity) {
        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout.pop_question_select_answer_wrong, null, false);
        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_anwer);
        TextView tv_pop_question_answer_your_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_your_anwer);
        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
        tv_pop_question_answer_your_anwer.setText(entity.getYourAnswer());
        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_wrong);
                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight();
                group.setLayoutParams(lp);
                return false;
            }
        });
        return popupWindow_view;
    }

    /** 语音答题回答错误 */
    public static View initFillAnswerWrongResultVoice(Context context, VideoResultEntity entity) {
        final View popupWindow_view = LayoutInflater.from(context).inflate(R.layout.pop_question_fillin_answer_voice_wrong, null, false);
        final TextView tv_pop_question_answer_right_anwer = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_anwer);
        tv_pop_question_answer_right_anwer.setText(entity.getStandardAnswer());
        popupWindow_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ImageView imageView = (ImageView) popupWindow_view.findViewById(R.id.iv_pop_question_answer_wrong);
                final Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
                popupWindow_view.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewGroup group = (ViewGroup) tv_pop_question_answer_right_anwer.getParent();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) group.getLayoutParams();
                lp.topMargin = bitmap.getHeight() * 222 / 322 - group.getHeight() / 2;
                group.setLayoutParams(lp);
                return false;
            }
        });
        return popupWindow_view;
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

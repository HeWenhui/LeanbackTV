package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;

import org.json.JSONObject;

/**
 * @author linyuqiang
 * @date 2018/4/3
 * 语音答题普通直播和站立直播
 */
public interface BaseVoiceAnswerCreat {
    /**
     * 创建语音答题
     *
     * @param activity
     * @param baseVideoQuestionEntity
     * @param assess_ref
     * @param type
     * @param rlQuestionContent
     * @param mIse
     * @param liveAndBackDebug
     * @return
     */
    BaseVoiceAnswerPager create(Context activity, BaseVideoQuestionEntity baseVideoQuestionEntity, JSONObject assess_ref, String type,
                                RelativeLayout rlQuestionContent, SpeechEvaluatorUtils mIse, LiveAndBackDebug liveAndBackDebug);

    /**
     * 设置语音答题页面宽高
     *
     * @param baseVoiceAnswerPager
     * @param rightMargin
     */
    void setViewLayoutParams(BaseVoiceAnswerPager baseVoiceAnswerPager, int rightMargin);

    /**
     * 回答结果显示
     *
     * @param context
     * @param questionBll
     * @param baseVoiceAnswerPager
     * @param baseVideoQuestionEntity
     * @param entity                  @return
     */
    boolean onAnswerReslut(Context context, AnswerRightResultVoice questionBll, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity);

    /**
     * 语音带结果回调
     */
    interface AnswerRightResultVoice {
        /**
         * 把结果页显示到布局上面
         *
         * @param popupWindowView
         */
        void initQuestionAnswerReslut(View popupWindowView);

        /**
         * 把结果页从布局上面移除
         *
         * @param popupWindowView
         */
        void removeQuestionAnswerReslut(View popupWindowView);

        /**
         * 语音答题结束，移除答题界面
         *
         * @param voiceAnswerPager
         */
        void removeBaseVoiceAnswerPager(BaseVoiceAnswerPager voiceAnswerPager);

        /**
         * 选择题正确
         *
         * @param entity
         */
        void initSelectAnswerRightResultVoice(VideoResultEntity entity);

        /**
         * 填空题正确
         *
         * @param entity
         */
        void initFillinAnswerRightResultVoice(VideoResultEntity entity);

        /**
         * 选择题错误
         *
         * @param entity
         */
        void initSelectAnswerWrongResultVoice(VideoResultEntity entity);

        /**
         * 填空题错误
         *
         * @param entity
         */
        void initFillAnswerWrongResultVoice(VideoResultEntity entity);
    }
}

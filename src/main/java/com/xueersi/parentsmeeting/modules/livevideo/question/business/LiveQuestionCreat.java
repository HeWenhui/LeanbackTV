package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionFillInBlankLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionFillInBlankPortLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionMulitSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionMulitSelectPortLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionSelectPortLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionSubjectivePager;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/8/3.
 * 普通互动题创建
 */
public class LiveQuestionCreat {
    String TAG = "LiveQuestionCreat";
    Activity activity;
    private LogToFile mLogtf;
    AtomicBoolean isAbLand = new AtomicBoolean();
    private QuestionHttp questionHttp;
    String mVSectionID;
    LivePagerBack livePagerBack;

    LiveQuestionCreat(Activity activity, AtomicBoolean isAbLand, LivePagerBack livePagerBack) {
        this.activity = activity;
        this.isAbLand = isAbLand;
        this.livePagerBack = livePagerBack;
        mLogtf = new LogToFile(activity, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
    }

    public void setmVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
    }

    public void setQuestionHttp(QuestionHttp questionHttp) {
        this.questionHttp = questionHttp;
    }

    /**
     * 填空题
     */
    public BaseLiveQuestionPager showFillBlankQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        BaseLiveQuestionPager baseQuestionPager;
        long before = System.currentTimeMillis();
        if (isAbLand.get()) {
            baseQuestionPager = new QuestionFillInBlankLivePager(activity, videoQuestionLiveEntity);
        } else {
            baseQuestionPager = new QuestionFillInBlankPortLivePager(activity, videoQuestionLiveEntity);
        }
        baseQuestionPager.setPutQuestion(mPutQuestion);
        baseQuestionPager.setLivePagerBack(livePagerBack);
        return baseQuestionPager;
    }

    /**
     * 显示选择题
     */
    public BaseLiveQuestionPager showSelectQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        BaseLiveQuestionPager baseQuestionPager;
        long before = System.currentTimeMillis();
        if (isAbLand.get()) {
            baseQuestionPager = new QuestionSelectLivePager(activity, videoQuestionLiveEntity);
        } else {
            baseQuestionPager = new QuestionSelectPortLivePager(activity, videoQuestionLiveEntity);
        }
        baseQuestionPager.setPutQuestion(mPutQuestion);
        baseQuestionPager.setLivePagerBack(livePagerBack);
        return baseQuestionPager;
    }

    /**
     * 显示多选择题
     */
    public BaseLiveQuestionPager showMulitSelectQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        BaseLiveQuestionPager baseQuestionPager;
        if (isAbLand.get()) {
            baseQuestionPager = new QuestionMulitSelectLivePager(activity, videoQuestionLiveEntity);
        } else {
            baseQuestionPager = new QuestionMulitSelectPortLivePager(activity, videoQuestionLiveEntity);
        }
        baseQuestionPager.setPutQuestion(mPutQuestion);
        baseQuestionPager.setLivePagerBack(livePagerBack);
        return baseQuestionPager;
    }

    /**
     * 文科主观题
     */
    public BaseLiveQuestionPager showSubjectiveQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        BaseLiveQuestionPager baseQuestionPager = new QuestionSubjectivePager(activity, videoQuestionLiveEntity);
        baseQuestionPager.setPutQuestion(mPutQuestion);
        baseQuestionPager.setLivePagerBack(livePagerBack);
        return baseQuestionPager;
    }

    /**
     * 填完互动题回调,提交测试题
     */
    private PutQuestion mPutQuestion = new PutQuestion() {

        @Override
        public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity2, String result) {
            questionHttp.liveSubmitTestAnswer((VideoQuestionLiveEntity) videoQuestionLiveEntity2, mVSectionID, result,
                    false, false, new QuestionSwitch.OnAnswerReslut() {
                        @Override
                        public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                            //onSubmit();
                        }

                        @Override
                        public void onAnswerFailure() {

                        }
                    });
        }
    };

}

package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.PutQuestion;

/**
 * Created by linyuqiang on 2016/12/19.
 */
public abstract class BaseLiveQuestionPager extends LiveBasePager {
    protected PutQuestion putQuestion;

    public BaseLiveQuestionPager(Context context) {
        super(context);
    }

    public void onSubSuccess() {
    }

    public void onSubFailure() {
    }

    public void setPutQuestion(PutQuestion putQuestion) {
        this.putQuestion = putQuestion;
    }

    public void hideInputMode() {
    }
}

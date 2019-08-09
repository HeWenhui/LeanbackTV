package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

/**
 * Created by linyuqiang on 2018/7/12.
 */

public interface QuestionShowReg extends LiveProvide {
    void registQuestionShow(QuestionShowAction questionShowAction);

    void unRegistQuestionShow(QuestionShowAction questionShowAction);
}

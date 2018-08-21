package com.xueersi.parentsmeeting.modules.livevideo.question.business;

/**
 * 答题结果接口
 * @author chenkun
 * @version 1.0, 2018/7/30 下午2:35
 */

public interface IAnswerResultAction {


    /**
     * H5 答题结果  回调
     * @param result
     */
     void onAnswerResult(String result);


    /**
     * 关闭答题结果页
     */
    void closeAnswerResult();


    /**
     * 提醒提交答案
     */
    void remindSubmit();

}

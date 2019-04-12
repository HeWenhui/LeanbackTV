package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

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
      * @param forceClose
     */
    void closeAnswerResult(boolean forceClose);


    /**
     * 提醒提交答案
     */
    void remindSubmit();


    /**
     * 老师表扬答题学生
     */
    void teacherPraise();
}

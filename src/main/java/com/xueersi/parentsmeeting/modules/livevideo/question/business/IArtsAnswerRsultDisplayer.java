package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.view.View;

/**
 * 问题答题结果展示器
 * @author chenkun
 * @version 1.0, 2018/7/28 下午8:33
 */

public interface IArtsAnswerRsultDisplayer {

    /**
     * 显示答题结果
     */
    void showAnswerReuslt();


    /**
     * 关闭答题结果
     */
    void close();


    /**
     * 提示提交答案
     */
    void remindSubmit();


    /**
     * 获取根 布局
     * @return
     */
    View getRootLayout();



}

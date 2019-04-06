package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.BasePager;

/**
 * 文理答题统计面板 状态
 *
 * @author chekun
 * created  at 2018/8/23 18:37
 */
public interface AnswerResultStateListener {

    /** UI 完全显示 **/
    void onCompeletShow();

    /**
     * 自动关闭
     * @param basePager
     */
    void onAutoClose(BasePager basePager);


    /**用户主动关闭答题结果页面**/
    void onCloseByUser();


}

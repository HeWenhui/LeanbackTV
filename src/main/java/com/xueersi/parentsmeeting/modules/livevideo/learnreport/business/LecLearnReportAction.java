package com.xueersi.parentsmeeting.modules.livevideo.learnreport.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;

/**
 * Created by linyuqiang on 2016/9/23.
 * 学习报告事件
 */
public interface LecLearnReportAction {
    /**
     * 学习报告
     *
     * @param liveId
     */
    void onLearnReport(String liveId);

}

package com.xueersi.parentsmeeting.modules.livevideo.event;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

/**
 * 文科答题结果 展示完全事件
 *
 * @author chekun
 * created  at 2018/9/6 14:19
 */
public class AnswerResultCplShowEvent {
    private String TAG = "AnswerResultCplShowEvent";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private String method;
    /** 页面 */
    private BasePager basePager;

    public AnswerResultCplShowEvent(String method) {
        this.method = method;
        logger.d(TAG + ":method=" + method);
    }

    public AnswerResultCplShowEvent(BasePager basePager, String method) {
        this.basePager = basePager;
        this.method = method;
        logger.d(TAG + ":method=" + method);
    }

    public BasePager getBasePager() {
        return basePager;
    }

    public String getMethod() {
        return method;
    }
}

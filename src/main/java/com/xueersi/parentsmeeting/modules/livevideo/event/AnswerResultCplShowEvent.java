package com.xueersi.parentsmeeting.modules.livevideo.event;

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

    public AnswerResultCplShowEvent(String method) {
        this.method = method;
        logger.d(TAG + ":method=" + method);
    }

    public String getMethod() {
        return method;
    }
}

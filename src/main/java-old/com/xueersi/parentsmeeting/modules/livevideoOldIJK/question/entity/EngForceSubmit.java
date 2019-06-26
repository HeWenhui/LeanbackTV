package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.entity;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveLoggerFactory;

public class EngForceSubmit {

    /**
     * @param isNewArts 是不是新课件
     * @param isEnd     是不是强制提交
     * @return
     */
    public static String getSubmit(boolean isNewArts, boolean isEnd) {
        String isSubmit;
        if (isNewArts) {
            isSubmit = isEnd ? "2" : "1";
        } else {
            isSubmit = isEnd ? "1" : "0";
        }
        Logger logger = LiveLoggerFactory.getLogger("EngForceSubmit");
        logger.d("getSubmit:isNewArts=" + isNewArts + ",isEnd=" + isEnd + ",isSubmit=" + isSubmit);
        return isSubmit;
    }
}

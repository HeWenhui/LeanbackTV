package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness;

import java.util.Map;

/**
 * Created by：WangDe on 2018/11/28 11:57
 */
public interface IButtonOnClick {
    void submit(Map<String,String> mainEva,Map<String,String> tutorEva);

    void close();
}

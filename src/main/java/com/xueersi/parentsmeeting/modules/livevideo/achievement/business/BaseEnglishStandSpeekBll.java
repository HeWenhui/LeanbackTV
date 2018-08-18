package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.tal.speech.asr.talAsrJni;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

/**
 * Created by linyuqiang on 2018/7/29.
 * 语音能量条
 */
public class BaseEnglishStandSpeekBll {
    protected static boolean loadSuccess = false;

    static {
        try {
            Loger.i("BaseEnglishStandSpeekBll", "loadLibrary");
            System.loadLibrary(SpeechEvaluatorUtils.TAL_ASSESS_LIB);
            Loger.i("BaseEnglishStandSpeekBll", "loadLibrary ok");
            loadSuccess = true;
        } catch (Throwable e) {
            loadSuccess = false;
            Loger.e(BaseApplication.getContext(), "BaseEnglishStandSpeekBll", "loadLibrary", e, true);
        }
    }

}

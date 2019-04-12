package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.xueersi.common.base.BaseApplication;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

/**
 * Created by linyuqiang on 2018/7/29.
 * 语音能量条
 */
public class BaseEnglishStandSpeekBll implements SpeakerRecognitioner.SpeakerPredict {
    protected static boolean loadSuccess = false;
    private static Logger logger = LiveLoggerFactory.getLogger("BaseEnglishStandSpeekBll");
    protected SpeakerRecognitioner speakerRecognitioner;

    public void setSpeakerRecognitioner(SpeakerRecognitioner speakerRecognitioner) {
        this.speakerRecognitioner = speakerRecognitioner;
    }

    @Override
    public void onPredict(String predict) {

    }

    protected void loadLibrary() {
        if (loadSuccess) {
            return;
        }
//        try {
//            logger.i("loadLibrary");
////            System.loadLibrary(SpeechEvaluatorUtils.TAL_ASSESS_LIB);
//            logger.i("loadLibrary ok");
//            loadSuccess = true;
//        } catch (Throwable e) {
//            loadSuccess = false;
//            UmsAgentManager.umsAgentException(BaseApplication.getContext(), "BaseEnglishStandSpeekBll" + "loadLibrary", e);
//        }
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;

import org.json.JSONObject;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.intelligent_recognition_sign;

public class IntelligentRecognition extends LiveBaseBll implements NoticeAction {
    public IntelligentRecognition(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        int pType = data.optInt("ptype");
        switch (type) {
            case XESCODE.ARTS_SEND_QUESTION: {
//                handle1103();
                if (pType == 28) {
                    Bundle bundle = new Bundle();
                    XueErSiRouter.startModule(activity, "/english/intelligent_recognition", bundle);
                }
                break;
            }
            case XESCODE.ARTS_STOP_QUESTION: {
//                handle1104();
                if (pType == 28) {
                    Intent intent = new Intent(FILTER_ACTION);
                    intent.putExtra(intelligent_recognition_sign, data.toString());
                    activity.sendBroadcast(intent);
                }
                break;
            }
        }
    }

//    private void handle1103() {

//    }

//    private void handle1104() {

//    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.ARTS_SEND_QUESTION, XESCODE.ARTS_STOP_QUESTION};
    }
}

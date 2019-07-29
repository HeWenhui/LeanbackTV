package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.intelligent_recognition_sign;

public class IntelligentRecognitionBll extends LiveBaseBll implements NoticeAction {

    public IntelligentRecognitionBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        int pType = data.optInt("ptype");
        switch (type) {
            case XESCODE.ARTS_SEND_QUESTION: {
                if (pType == 28) {
                    Bundle bundle = new Bundle();
                    IntelligentRecognitionRecord intelligentRecognitionRecord = new IntelligentRecognitionRecord();
                    intelligentRecognitionRecord.setAnswerTime(data.optString("time"));
                    intelligentRecognitionRecord.setStuId(mGetInfo.getStuId());
                    intelligentRecognitionRecord.setStuCouId(mGetInfo.getStuCouId());
                    intelligentRecognitionRecord.setLiveId(mGetInfo.getId());
                    intelligentRecognitionRecord.setContent(data.optString("answer"));
                    JSONArray jsonArray = data.optJSONArray("id");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        intelligentRecognitionRecord.setMaterialId(jsonArray.optString(0));
                    }
                    intelligentRecognitionRecord.setIsPlayBack("1");
                    if (mGetInfo.getStudentLiveInfo() != null) {
                        intelligentRecognitionRecord.setClassId(mGetInfo.getStudentLiveInfo().getClassId());
                        intelligentRecognitionRecord.setTeamId(mGetInfo.getStudentLiveInfo().getTeamId());
                    }
                    bundle.putParcelable("intelligentRecognitionRecord", intelligentRecognitionRecord);
                    XueErSiRouter.startModule(activity, "/english/intelligent_recognition", bundle);
                }
                break;
            }
            case XESCODE.ARTS_STOP_QUESTION: {
                if (pType == 28) {
                    Intent intent = new Intent(FILTER_ACTION);
                    intent.putExtra(intelligent_recognition_sign, data.toString());
                    activity.sendBroadcast(intent);
                }
                break;
            }
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.ARTS_SEND_QUESTION, XESCODE.ARTS_STOP_QUESTION};
    }
}

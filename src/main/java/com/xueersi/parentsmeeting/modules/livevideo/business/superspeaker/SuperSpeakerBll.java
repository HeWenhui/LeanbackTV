package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;

import org.json.JSONObject;

public class SuperSpeakerBll extends LiveBaseBll implements NoticeAction {
    public SuperSpeakerBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    public void preCamera() {

    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.SUPER_SPEAKER_TAKE_CAMERA: {

                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public int[] getNoticeFilter() {
        //学生点赞
        return new int[]{
                XESCODE.SUPER_SPEAKER_TAKE_CAMERA};
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import org.json.JSONObject;

/**
 * Created by Zhang Yuansun on 2018/7/12.
 */

public class SpeechBulletScreenIRCBll extends LiveBaseBll implements TopicAction, NoticeAction{
    SpeechBulletScreenAction speechBulletScreenAction;

    public SpeechBulletScreenIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (speechBulletScreenAction == null) {
            SpeechBulletScreenBll speechBulletScreenBll = new SpeechBulletScreenBll(activity);
            speechBulletScreenBll.initView(mRootView);
            speechBulletScreenAction = speechBulletScreenBll;
        }
        speechBulletScreenAction.onStartSpeechBulletScreen();
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.XCR_ROOM_DANMU_OPEN: {
                if (speechBulletScreenAction == null) {
                    SpeechBulletScreenBll speechBulletScreenBll = new SpeechBulletScreenBll(activity);
                    speechBulletScreenBll.initView(mRootView);
                    speechBulletScreenAction = speechBulletScreenBll;
                }
                speechBulletScreenAction.onStartSpeechBulletScreen();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[0];
    }
}

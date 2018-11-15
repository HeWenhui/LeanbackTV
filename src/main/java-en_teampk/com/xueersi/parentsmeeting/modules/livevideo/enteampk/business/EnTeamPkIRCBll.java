package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

/**
 * @author linyuqiang
 * created  at 2018/11/6
 * 英语战队PK 相关业务处理
 */
public class EnTeamPkIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    EnTeamPkAction enTeamPkAction;

    public EnTeamPkIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        logger.d("onLiveInited");
        EnTeamPkBll teamPkBll = new EnTeamPkBll(activity);
        teamPkBll.setRootView(mRootView);
        enTeamPkAction = teamPkBll;
        enTeamPkAction.onRankStart();
//        enTeamPkAction.onRankResult();
//        enTeamPkAction.onRankLead();
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {

    }

    @Override
    public int[] getNoticeFilter() {
        return new int[0];
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }
}

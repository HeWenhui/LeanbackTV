package com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter;

import android.app.Activity;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeLevelDisplayPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

/**
 * 英语小目标 presenter层
 *
 * @author zhangyuansun
 * created  at 2018/11/28
 */
public class BetterMeIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    public BetterMeIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {

    }

    @Override
    public int[] getNoticeFilter() {
        return new int[0];
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        mRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootView.addView(new BetterMeLevelDisplayPager(mContext, true).getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }, 3000);

    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }
}

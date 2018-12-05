package com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeLevelDisplayPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
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
public class BetterMeIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, BetterMeContract.BetterMePresenter {
    BetterMeContract.BetterMeView mBetterMeView;

    public BetterMeIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mBetterMeView = new BetterMePager(mContext);
        mBetterMeView.setPresenter(this);
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
        mBetterMeView.setRootView(mRootView);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBetterMeView.showIntroductionPager();
            }
        }, 3000);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }
}

package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.util.Log;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 文科答题统计面板 直播回放业务处理类
 *
 * @author chekun
 * created  at 2018/9/29 18:54
 */
public class ArtsAnswerResultPlayBackBll extends LiveBackBaseBll {
    private ArtsAnswerResultBll mAnswerResultBll;

    /**
     * 0 liveback
     * 1 experience
     *
     * @param activity
     * @param liveBackBll
     */
    public ArtsAnswerResultPlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void initView() {
        mAnswerResultBll = new ArtsAnswerResultBll((Activity) mContext, liveGetInfo.getId(), liveGetInfo.getLiveType());
        mAnswerResultBll.setLiveViewAction(getLiveViewAction());
        mAnswerResultBll.onLiveInited(liveGetInfo);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveBackQuestionEvent(LiveBackQuestionEvent event) {
        if (event.getEnvetnType() == LiveBackQuestionEvent.QUSTIONS_SHOW) {
            mAnswerResultBll.closeAnswerResult(false, "onLiveBackQuestionEvent1");
        } else if (event.getEnvetnType() == LiveBackQuestionEvent.QUSTION_CLOSE) {
            if (liveGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
                EventBus.getDefault().post(new AnswerResultCplShowEvent("onLiveBackQuestionEvent"));
                Log.e("mqtt", "submitData" + "hahaha");
            } else {
                mAnswerResultBll.closeAnswerResult(true, "onLiveBackQuestionEvent2");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAnswerResultBll != null) {
            mAnswerResultBll.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }
}

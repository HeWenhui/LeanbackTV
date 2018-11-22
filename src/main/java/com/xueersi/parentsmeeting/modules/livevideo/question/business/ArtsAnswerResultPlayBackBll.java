package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.util.Log;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 文科答题统计面板 直播回放业务处理类
 * @author chekun
 * created  at 2018/9/29 18:54
*/
public class ArtsAnswerResultPlayBackBll extends LiveBackBaseBll {
    private ArtsAnswerResultBll mAnswerResultBll;
    /**
     * 0 liveback
     * 1 experience
     * @param activity
     * @param liveBackBll
     */
    public ArtsAnswerResultPlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void initView() {
        mAnswerResultBll = new ArtsAnswerResultBll((Activity) mContext,liveGetInfo.getId(),liveGetInfo.getLiveType(),mRootView);
        mAnswerResultBll.onLiveInited(liveGetInfo);
        Log.e("ArtsAnswerResultPlayBackBll","=====>initView called");
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveBackQuestionEvent(LiveBackQuestionEvent event) {
        Log.e("ArtsAnswerResultPlayBackBll", "=====>onLiveBackQuestionEvent:"+ event.getEnvetnType());
        if(event.getEnvetnType() == LiveBackQuestionEvent.QUSTIONS_SHOW){
            mAnswerResultBll.closeAnswerResult(false);
        }else if(event.getEnvetnType() == LiveBackQuestionEvent.QUSTION_CLOSE){
            mAnswerResultBll.closeAnswerResult(true);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if(mAnswerResultBll != null){
            mAnswerResultBll.onDestory();
        }
        EventBus.getDefault().unregister(this);
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ScoreRange;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.VoiceAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        mAnswerResultBll = new ArtsAnswerResultBll((Activity) mContext,liveGetInfo.getId(),liveGetInfo.getLiveType(),mRootView);
        mAnswerResultBll.onLiveInited(liveGetInfo);
        Log.e("ArtsAnswerResultPlayBackBll","=====>onCreate called");
    }
    /**EventCategory*/
    int[] category = new int[]{
            1000
    };
    @Override
    public int[] getCategorys() {
        return category;
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {
        Log.e("ArtsAnswerResultPlayBackBll","======>showQuestion called:"+mAnswerResultBll);
        if(mAnswerResultBll != null){
            mAnswerResultBll.closeAnswerResult(false);
        }
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        Log.e("ArtsAnswerResultPlayBackBll","======>onQuestionEnd called:"+mAnswerResultBll);
        if(mAnswerResultBll != null){
            mAnswerResultBll.closeAnswerResult(true);
        }
    }


    @Override
    public void onDestory() {
        super.onDestory();
        if(mAnswerResultBll != null){
            mAnswerResultBll.onDestory();
        }
    }
}

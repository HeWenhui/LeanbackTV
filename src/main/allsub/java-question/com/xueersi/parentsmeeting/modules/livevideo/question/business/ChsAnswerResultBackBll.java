package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ChsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ChiAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * FileName: ChsAnswerResultBackBll 回放
 * Author: linyuqiang
 * Date: 2019/7/26 13:03
 * Description: ${DESCRIPTION}
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class ChsAnswerResultBackBll extends LiveBackBaseBll implements AnswerResultStateListener {

    private static final String TAG = "ArtsAnswerResultBll";
    private IArtsAnswerRsultDisplayer mDsipalyer;
    boolean forceSumbmit;
    private final long AUTO_CLOSE_DELAY = 2000;

    public ChsAnswerResultBackBll(Activity context, LiveBackBll liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        attachToView();
    }

    private void attachToView() {
        EventBus.getDefault().register(this);
    }

    private void showAnswerReulst(ChsAnswerResultEvent event, ChineseAISubjectResultEntity mAnswerReulst) {

        if (mDsipalyer != null) {
            return;
        }
        mDsipalyer = new ChiAnswerResultPager(mContext, mAnswerReulst, ChsAnswerResultBackBll.this);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                addView(mDsipalyer.getRootLayout(), layoutParams);
            }
        }, 100);
        VideoQuestionLiveEntity detailInfo = event.getDetailInfo();
        if (detailInfo != null) {
            NewCourseLog.sno8(liveBackBll, NewCourseLog.getNewCourseTestIdSec(detailInfo, LiveVideoSAConfig.ART_CH), event.isIspreload(), 0, detailInfo.isTUtor());
        }
    }

    /**
     * 关闭作答结果页面
     *
     * @param forceSumbmit
     */
    public void closeAnswerResult(boolean forceSumbmit) {
        //logger.e( "=====>closeAnswerResult:" + forceSumbmit + ":" + mDsipalyer);
        // 已展示过答题结果
        if (mDsipalyer != null) {
            mDsipalyer.close();
            mDsipalyer = null;
            EventBus.getDefault().post(new AnswerResultCplShowEvent("closeAnswerResult1"));
        }


        // logger.e("=====>closeAnswerResult:" + forceSumbmit + ":" + this);
        this.forceSumbmit = forceSumbmit;
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        logger.d("onQuestionEnd:vCategory=" + vCategory);
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE:
                closeAnswerResult(true);
                break;
        }
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE};
    }

    @Override
    public void onCompeletShow() {
        if (forceSumbmit) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new AnswerResultCplShowEvent("onCompeletShow"));
                }
            }, AUTO_CLOSE_DELAY);
        }
    }

    @Override
    public void onAutoClose(BasePager basePager) {
        if (mDsipalyer != null) {
            removeView(mDsipalyer.getRootLayout());
            mDsipalyer = null;
        }
    }

    @Override
    public void onCloseByUser() {
        closeAnswerResult(false);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAnswerResult(ChsAnswerResultEvent event) {
        if (ChsAnswerResultEvent.TYPE_AI_CHINESE_ANSWERRESULT == event.getmType()) {
            ChineseAISubjectResultEntity mAnswerReulst = event.getResultEntity();
            showAnswerReulst(event, mAnswerReulst);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebviewClose(LiveRoomH5CloseEvent event) {
        //logger.e( "=======>onWebviewClose called");
        //mArtsAnswerResultEvent = null;
        closeAnswerResult(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

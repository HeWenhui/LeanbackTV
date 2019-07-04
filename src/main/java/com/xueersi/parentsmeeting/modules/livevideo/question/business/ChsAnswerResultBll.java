package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ChsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ChiAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

/**
 * FileName: ChsAnswerResultBll
 * Author: WangDe
 * Date: 2019/4/25 13:03
 * Description: ${DESCRIPTION}
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class ChsAnswerResultBll extends LiveBaseBll implements NoticeAction, AnswerResultStateListener {

    private static final String TAG = "ArtsAnswerResultBll";
    private RelativeLayout rlAnswerResultLayout;
    private IArtsAnswerRsultDisplayer mDsipalyer;
    boolean forceSumbmit;
    /** 当前答题结果 */
    private ChineseAISubjectResultEntity mAnswerReulst;
    private final long AUTO_CLOSE_DELAY = 2000;

    public ChsAnswerResultBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    public ChsAnswerResultBll(Activity context, String liveId, int liveType, RelativeLayout rootView){
        super(context,liveId,liveType);
        mRootView = rootView;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        attachToView();
    }

    private void attachToView() {
        EventBus.getDefault().register(this);
        rlAnswerResultLayout = mRootView;
    }

    private void showAnswerReulst(ChsAnswerResultEvent event) {

        if (mDsipalyer != null) {
            return;
        }
        mDsipalyer = new ChiAnswerResultPager(mContext, mAnswerReulst, ChsAnswerResultBll.this);
        mRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rlAnswerResultLayout.addView(mDsipalyer.getRootLayout(), layoutParams);
            }
        },100);
        VideoQuestionLiveEntity detailInfo = event.getDetailInfo();
        if (detailInfo != null) {
            NewCourseLog.sno8(mLiveBll, NewCourseLog.getNewCourseTestIdSec(detailInfo, LiveVideoSAConfig.ART_CH), event.isIspreload(), 0,detailInfo.isTUtor());
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
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.MULTIPLE_H5_COURSEWARE:
                boolean status = data.optBoolean("open");
                if (!status){
                    closeAnswerResult(true);
                }else {
                    forceSumbmit = false;
                }
                break;
            default:
                    break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.MULTIPLE_H5_COURSEWARE};
    }

    @Override
    public void onCompeletShow() {
        if (forceSumbmit) {
            mRootView.postDelayed(new Runnable() {
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
            rlAnswerResultLayout.removeView(mDsipalyer.getRootLayout());
            mDsipalyer = null;
        }
    }

    @Override
    public void onCloseByUser() {
        closeAnswerResult(false);
    }

    @Override
    public void onUpdateVoteFoldCount(String count) {

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAnswerResult(ChsAnswerResultEvent event) {
        if (ChsAnswerResultEvent.TYPE_AI_CHINESE_ANSWERRESULT == event.getmType()){
            mAnswerReulst = event.getResultEntity();
            showAnswerReulst(event);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebviewClose(LiveRoomH5CloseEvent event) {
        //logger.e( "=======>onWebviewClose called");
        //mArtsAnswerResultEvent = null;
        closeAnswerResult(false);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mAnswerReulst = null;
        EventBus.getDefault().unregister(this);
    }
}

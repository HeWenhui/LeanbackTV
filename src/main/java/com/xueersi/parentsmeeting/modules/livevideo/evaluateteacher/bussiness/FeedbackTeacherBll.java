package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.FeedBackTeacherInterface;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.http.EvaluateResponseParser;

import java.util.HashMap;

public class FeedbackTeacherBll extends LiveBaseBll {
    LivePlayAction livePlayAction;
    FeedBackEntity mFeedBackEntity;
    LiveFeedBackPager pager = null;
    EvaluateResponseParser mParser;
    public FeedbackTeacherBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    public FeedbackTeacherBll(Activity context, String liveId, int liveType) {
        super(context, liveId, liveType);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        livePlayAction = getInstance(LivePlayAction.class);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo != null && getInfo.getIsArts() == LiveVideoSAConfig.ART_SEC) {
            mParser =new EvaluateResponseParser();

            showFeedBack();
        }
    }

    private void showFeedBack() {
        getHttpManager().getFeedBack(mLiveId, mGetInfo.getStudentLiveInfo().getCourseId(), "0", new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("showFeedBack => onPmSuccess: error = " + responseEntity.getJsonObject().toString());
                mFeedBackEntity = mParser.parseFeedBackContent(responseEntity);
                if(mFeedBackEntity == null) {
                    return;
                }
                mGetInfo.setShowHightFeedback(true);
                pager = new LiveFeedBackPager(mContext, mLiveId, mFeedBackEntity, mGetInfo, mLiveBll
                        .getHttpManager());
                pager.setOnPagerClose(onPagerClose);
                pager.setFeedbackSelectInterface(feedBackTeacherInterface);


            }
        });

    }

    LiveBasePager.OnPagerClose onPagerClose = new LiveBasePager.OnPagerClose() {
        @Override
        public void onClose(LiveBasePager basePager) {
            removeView(basePager.getRootView());
        }
    };

    private void quitLive() {
        logger.i("quit livevideo");

        if (mLiveBll.getmIsLand().get()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.finish();
        } else {
            activity.finish();
        }
    }

    public boolean showFeedbackPager() {
//        if (pager != null && mFeedBackEntity != null) {

        if (pager!=null && mFeedBackEntity != null && System.currentTimeMillis() / 1000 > mFeedBackEntity.getEvaluateTime()) {
            logger.i("showEvaluateTeacher");
            logger.i("currenttime:" + System.currentTimeMillis() + "  getEvaluatetime:" + mFeedBackEntity
                    .getEvaluateTime());

            livePlayAction.stopPlayer();
            mLiveBll.onIRCmessageDestory();
            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(pager.getRootView(), params);
            return true;
        } else {
            return false;
        }

    }

    FeedBackTeacherInterface feedBackTeacherInterface = new FeedBackTeacherInterface() {
        @Override
        public void onClose() {
            quitLive();
        }

        @Override
        public boolean removeView() {
            return false;
        }

        @Override
        public boolean showPager() {
            return showFeedbackPager();
        }


    };

}

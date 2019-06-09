package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.FeedBackTeacherInterface;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.http.EvaluateResponseParser;

import java.util.concurrent.atomic.AtomicBoolean;

public class FeedbackTeacherBll extends LiveBaseBll {
    RelativeLayout bottomContent;
    LiveVideoFragment liveFragment;
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
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo != null && (getInfo.getIsArts() == LiveVideoSAConfig.ART_SEC
                && (LiveVideoConfig.EDUCATION_STAGE_3.equals(mGetInfo.getEducationStage())
                || LiveVideoConfig.EDUCATION_STAGE_4.equals(mGetInfo.getEducationStage())))) {
            mParser =new EvaluateResponseParser();

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFeedBack(bottomContent);
                }
            }, 10000);
        }
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        this.bottomContent = bottomContent;
    }

    private void showFeedBack(final RelativeLayout bottomContent) {
        getHttpManager().getFeedBack(mLiveId, mGetInfo.getStudentLiveInfo().getCourseId(), "0", new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("showFeedBack => onPmSuccess: error = " + responseEntity.getJsonObject().toString());
                mFeedBackEntity = mParser.parseFeedBackContent(responseEntity);


            }
        });

    }

    com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager.OnPagerClose onPagerClose = new com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager.OnPagerClose() {
        @Override
        public void onClose(com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager basePager) {
            bottomContent.removeView(basePager.getRootView());
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

    public void setLiveFragment(LiveVideoFragment liveFragment) {
        this.liveFragment = liveFragment;
    }

    public boolean showFeedbackPager() {
//        if (pager != null && mFeedBackEntity != null) {

        if (pager!=null && mFeedBackEntity != null && System.currentTimeMillis() / 1000 > mFeedBackEntity.getEvaluateTime()) {
            logger.i("showEvaluateTeacher");
            logger.i("currenttime:" + System.currentTimeMillis() + "  getEvaluatetime:" + mFeedBackEntity
                    .getEvaluateTime());
            pager = new LiveFeedBackPager(mContext, mLiveId, mFeedBackEntity, mGetInfo, bottomContent, mLiveBll
                    .getHttpManager());
            pager.setOnPagerClose(onPagerClose);
            pager.setFeedbackSelectInterface(feedBackTeacherInterface);

            liveFragment.stopPlayer();
            mLiveBll.onIRCmessageDestory();
            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bottomContent.addView(pager.getRootView(), params);
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

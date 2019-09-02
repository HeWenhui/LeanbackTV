package com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveBackPlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.http.EvaluateResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FeedbackTeacherLiveBackBll extends LiveBackBaseBll {
    RelativeLayout bottomContent;
    LiveBackPlayerFragment liveBackPlayVideoFragment;
    FeedBackEntity mFeedBackEntity;

    LiveFeedBackPager pager = null;
    LiveHttpManager mHttpManager;
    EvaluateResponseParser mParser;

    VideoLivePlayBackEntity mVideoEntity;
    public FeedbackTeacherLiveBackBll(Activity context, LiveBackBll liveBll) {
        super(context, liveBll);
    }


    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        mHttpManager = liveBackBll.getmHttpManager();
        this.mVideoEntity = mVideoEntity;
        mParser = new EvaluateResponseParser();
        if (liveGetInfo != null && liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC) {
//      if (liveGetInfo != null){
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFeedBack(bottomContent);
                }
            }, 10000);
        }
    }

    private void showFeedBack(final RelativeLayout bottomContent) {
//        try {
//            JSONObject jsonObject = new JSONObject("{\"evluateConf\":{\"evaluateIsOpen\":1,\"evaluateTimePer\":0.7," +
//                    "\"isHavecounselor\":1,\"popRate\":2,\"isHaveInput\":1,\"evaluateTime\":1559613630}," +
//                    "\"evaluateContent\":{\"evaluateScore\":[\"不满意\",\"有待提高\",\"满意\"]," +
//                    "\"teacherEvaluOption\":{\"choose1\":[\"讲得太快了\",\"讲得太慢了\",\"内容讲错了\",\"闲话有点多\",\"只会读课件\",\"课堂闷无聊\"]," +
//                    "\"choose2\":[\"希望慢一点\",\"希望快一点\",\"有点听不懂\",\"内容偏简单\",\"板书一般\",\"状态一般\"],\"choose3\":[\"爱上学习\"," +
//                    "\"进步很大\",\"深受启发\",\"收获颇多\",\"重点突出\",\"板书漂亮\"]},\"tutorEvaluOption\":{\"choose1\":[\"回复太慢\",\"没听明白\"," +
//                    "\"很少沟通\",\"爱答不理\"],\"choose2\":[\"回复及时\",\"听懂了\",\"及时督促\",\"平易近人\"],\"choose3\":[\"消息秒回\",\"清晰易懂\"," +
//                    "\"主动关注\",\"积极热情\"]}}}");
//            ResponseEntity responseEntity = new ResponseEntity();
//            responseEntity.setJsonObject(jsonObject);
//            mFeedBackEntity = mParser.parseFeedBackContent(responseEntity);
//
//            pager = new LiveFeedBackPager(mContext, liveGetInfo.getId(), mFeedBackEntity, liveGetInfo,
//                    bottomContent, mHttpManager);
//            pager.setOnPagerClose(onPagerClose);
//            pager.setCourseId(mVideoEntity.getCourseId());
//            pager.setFeedbackSelectInterface(feedBackTeacherInterface);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if(true) {
//            return;
//        }

        mHttpManager.getFeedBack(liveGetInfo.getId(), mVideoEntity.getCourseId(), "1", new
                HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        //     mLogtf.d("showFeedBack => onPmSuccess: error = " + responseEntity.getJsonObject()
                        // .toString());
                        mFeedBackEntity = mParser.parseFeedBackContent(responseEntity);
                        if(mFeedBackEntity == null) {
                            return;
                        }
                        liveGetInfo.setShowHightFeedback(true);
                        pager = new LiveFeedBackPager(mContext, liveGetInfo.getId(), mFeedBackEntity, liveGetInfo,
                                bottomContent, mHttpManager);
                        pager.setCourseId(mVideoEntity.getCourseId());
                        pager.setOnPagerClose(onPagerClose);
                        pager.setFeedbackSelectInterface(feedBackTeacherInterface);
                    }
                });

    }

    com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager.OnPagerClose onPagerClose = new com.xueersi
            .parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager.OnPagerClose() {
        @Override
        public void onClose(com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager basePager) {
            mRootView.removeView(basePager.getRootView());
        }
    };

    private void quitLive() {
        logger.i("quit livevideo");
        if (liveBackPlayVideoFragment.isLandSpace()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.finish();
        } else {
            activity.finish();
        }
    }

    public void setLiveFragment(LiveBackPlayerFragment liveFragment) {
        this.liveBackPlayVideoFragment = liveFragment;
    }

    public boolean showFeedbackPager() {
//        if(pager != null){
        if (pager != null && 0 != mFeedBackEntity.getEvaluateTimePer() && ((liveBackBll.getvPlayer().getCurrentPosition() + 0.0) /
                liveBackBll.getvPlayer().getDuration()) > mFeedBackEntity.getEvaluateTimePer()){
            logger.i("showEvaluateTeacher");
            liveBackBll.getvPlayer().stop();
            liveBackBll.getvPlayer().release();
            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRootView.addView(pager.getRootView(), params);
            return true;
        } else{
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

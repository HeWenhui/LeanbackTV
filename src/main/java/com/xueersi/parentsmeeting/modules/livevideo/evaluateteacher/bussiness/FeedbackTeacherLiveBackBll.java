package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveFeedBackSecondPager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveBackPlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.FeedBackTeacherInterface;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.http.EvaluateResponseParser;

import org.json.JSONObject;

import java.util.HashMap;

public class FeedbackTeacherLiveBackBll extends LiveBackBaseBll {
    RelativeLayout bottomContent;
    LiveBackPlayerFragment liveBackPlayVideoFragment;

    FeedBackEntity mFeedBackEntity;

    LiveFeedBackPager pager = null;
    LiveHttpManager mHttpManager;
    EvaluateResponseParser mParser;
    /**
     * 所有教师评价是h5页面
     */
    LiveFeedBackSecondPager pagerNew = null;

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
        if (liveGetInfo != null && liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC ||
                liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_EN || liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH
                || liveGetInfo.getEducationStage().equals("4")) {
//      if (liveGetInfo != null){
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   // showFeedBack(bottomContent);

                    checkIfShowFeedback();
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
                                mHttpManager);
                        pager.setCourseId(mVideoEntity.getCourseId());
                        pager.setOnPagerClose(onPagerClose);
                        pager.setFeedbackSelectInterface(feedBackTeacherInterface);
                    }
                });

    }
    private void checkIfShowFeedback(){
        mHttpManager.checkFeedBack(liveGetInfo.getId(), liveGetInfo.getStudentLiveInfo().getCourseId(), new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                int status = responseEntity.getmStatus();
                if (status == 1) {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    //is_trigger 是否触发，1：可以，0：不可以
                    int is_trigger = jsonObject.optInt("isTrigger");
                    String url = null;
                    if (is_trigger == 1) {
                        if (liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_EN) {
                            //英语
                            url = jsonObject.getJSONObject("app").optString("english");
                        } else if (liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC) {
                            //理科
                            url = jsonObject.getJSONObject("app").optString("science");
                        } else if (liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH) {
                            //语文
                            url = jsonObject.getJSONObject("app").optString("chinese");
                        } else if (liveGetInfo.getEducationStage().equals("4")) {
                            //高中
                            url = jsonObject.getJSONObject("app").optString("highSchool");
                        }
                        url = url+"?courseId="+liveGetInfo.getStudentLiveInfo().getCourseId()+"&planId="+liveGetInfo.getId();
                        pagerNew = new LiveFeedBackSecondPager(mContext, liveGetInfo, url);
                        pagerNew.setOnPagerClose(onPagerClose);
                        pagerNew.setFeedbackSelectInterface(feedBackTeacherInterface);

                    }
                }



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

        if (pagerNew != null && ((liveBackBll.getvPlayer().getCurrentPosition() + 0.0) /
                liveBackBll.getvPlayer().getDuration()) > 0.7){
            logger.i("showEvaluateTeacher");
            liveBackBll.getvPlayer().stop();
            liveBackBll.getvPlayer().release();
            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(pagerNew.getRootView(), params);
            pagerNew.startLoading();
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

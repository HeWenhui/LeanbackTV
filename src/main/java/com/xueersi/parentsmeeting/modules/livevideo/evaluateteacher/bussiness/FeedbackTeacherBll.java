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
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveFeedBackSecondPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.FeedBackTeacherInterface;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.http.EvaluateResponseParser;

import org.json.JSONObject;

import java.util.HashMap;

public class FeedbackTeacherBll extends LiveBaseBll {
    LivePlayAction livePlayAction;
    FeedBackEntity mFeedBackEntity;
    LiveFeedBackPager pager = null;
    /**
     * 所有教师评价是h5页面
     */
    LiveFeedBackSecondPager pagerNew = null;

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
        if (getInfo != null && (getInfo.getIsArts() == LiveVideoSAConfig.ART_SEC ||
                getInfo.getIsArts() == LiveVideoSAConfig.ART_EN || getInfo.getIsArts() == LiveVideoSAConfig.ART_CH
                || getInfo.getEducationStage().equals("4"))) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    long before = System.currentTimeMillis();
                    //耗时20-100ms
                   // showFeedBack();
                    checkIfShowFeedback();
                    logger.d("onLiveInited:showFeedBack:time=" + (System.currentTimeMillis() - before));
                }
            }, 500);
        }
    }

    private void showFeedBack() {
        getHttpManager().getFeedBack(mLiveId, mGetInfo.getStudentLiveInfo().getCourseId(), "0", new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("showFeedBack => onPmSuccess: error = " + responseEntity.getJsonObject().toString());
                EvaluateResponseParser mParser = new EvaluateResponseParser();
                mFeedBackEntity = mParser.parseFeedBackContent(responseEntity);
                if (mFeedBackEntity == null) {
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

    private void checkIfShowFeedback(){
        getHttpManager().checkFeedBack(mLiveId, mGetInfo.getStudentLiveInfo().getCourseId(), new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                int status = responseEntity.getmStatus();
                if (status == 1) {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    //is_trigger 是否触发，1：可以，0：不可以
                   int is_trigger= jsonObject.optInt("is_trigger");
                   String url = null;

                    if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_EN) {
                      //英语
                        url = jsonObject.getJSONObject("app").optString("english");
                    } else if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC) {
                        //理科
                        url = jsonObject.getJSONObject("app").optString("science");
                    } else if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH) {
                        //语文
                        url = jsonObject.getJSONObject("app").optString("chinese");
                    } else if(mGetInfo.getEducationStage().equals("4")){
                        //高中
                        url = jsonObject.getJSONObject("app").optString("highSchool");
                    }
                    pagerNew = new LiveFeedBackSecondPager(mContext, mGetInfo,url);

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

        if (mLiveBll.getmIsLand().get()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.finish();
        } else {
            activity.finish();
        }
    }

    public boolean showFeedbackPager() {
//        if (pager != null && mFeedBackEntity != null) {

//        if (pager != null && mFeedBackEntity != null && System.currentTimeMillis() / 1000 > mFeedBackEntity.getEvaluateTime()) {
            if (pager != null && mFeedBackEntity != null) {
            logger.i("showEvaluateTeacher");
            logger.i("currenttime:" + System.currentTimeMillis() + "  getEvaluatetime:" + mFeedBackEntity
                    .getEvaluateTime());

            livePlayAction.stopPlayer();
            mLiveBll.onIRCmessageDestory();
            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
           // addView(pager.getRootView(), params);
            addView(pagerNew.getRootView(), params);
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

package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.recommodcourse;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.event.MiniEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RecommondCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoBannerBuyCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.StandExperienceRecommondCourseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.VideoPopView;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RecommondCourseBll extends StandExperienceEventBaseBll {
    LiveAndBackDebug liveAndBackDebug;
    private RecommondCoursePager mPager;
    private VideoPopView turnToOrder;

    public RecommondCourseBll(Activity activity, StandExperienceLiveBackBll liveBackBll, VideoView videoView) {
        super(activity, liveBackBll);
        turnToOrder = new VideoPopView((Activity) mContext, videoView);
    }

    @Override
    public void initView() {
        super.initView();
        mPager = new RecommondCoursePager(mContext);
        initListener();
        registerInBllHideView();
    }

    /**
     * 将这个bll注册在所有的Bll中，在各种其他Bll（目前只有QuestionBll，EnglishH5CoursewareBll）显示时做出相应操作（目前是隐藏聊天区的View）
     */
    private void registerInBllHideView() {
        //在QuestionShowReg中注册(也就是QuestionShowReg唯一实现类QuestionBLl中注册)，为了在QuestionBll显示时隐藏该聊天区
        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(mPager);
        }
        //在EnglishShowReg中注册(也就是EnglishShowReg唯一实现类EnglishH5CoursewareBll中注册)，为了在EnglishH5CoursewareBll显示时隐藏该聊天区
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(mPager);
        }
    }

    private void initListener() {
        if (mPager != null) {
            mPager.setClickListener(new RecommondCoursePager.ClickListener() {
                //跳转到购课页面
                @Override
                public void clickBuyCourse() {
                    logger.i("courseId = " + mRecommondCourseEntity.getCourseId() + " classId = " +
                            mRecommondCourseEntity.getClassId());
//                    EventBus.getDefault().post(new StandExperienceRecommondCourseEvent("Order", mVideoEntity
//                            .getCourseId(), mVideoEntity.getClassId()));
                    turnToOrder.turnToOrder(new StandExperienceRecommondCourseEvent("Order", mRecommondCourseEntity
                            .getCourseId(), mRecommondCourseEntity.getClassId()));
                }
            });
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(StandExperienceRecommondCourseEvent event) {
//        logger.i("跳转到了EventBus这里");
//    }

    //推荐课程信息
    private RecommondCourseEntity mRecommondCourseEntity;
    //录播消息信息
    private VideoBannerBuyCourseEntity bannerBuyCourseEntity;
    //解析器
    private LivePlayBackHttpResponseParser livePlayBackHttpResponseParser;
    //10秒后发送请求获取购课Banner的http请求
    private final int delayHttpTime = 1000 * 10;

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
        logger.i("显示推荐课程");
        if (mPager == null) {
            mPager = new RecommondCoursePager(mContext);
        }
        if (livePlayBackHttpResponseParser == null) {
            livePlayBackHttpResponseParser = getCourseHttpResponseParser();
        }
        HttpCallBack httpCallBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mRecommondCourseEntity = livePlayBackHttpResponseParser.parseRecommondCourseInfo(responseEntity);
                mPager.updateView(mRecommondCourseEntity);
                mRootView.addView(mPager.getRootView(), RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout
                        .LayoutParams.MATCH_PARENT);
            }
        };
        //发送http请求，得到推荐课程数据
        getCourseHttpManager().getRecommondCourseInfo(
                mVideoEntity.getRecommendClassUrl(),
                mVideoEntity.getTeacherId(),
                mVideoEntity.getGradId(),
                mVideoEntity.getSubjectId(),
                mVideoEntity.getChapterId(),
                httpCallBack
        );
        //添加窗口
        final HttpCallBack bannerMessageHttpCallBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                bannerBuyCourseEntity = livePlayBackHttpResponseParser.parseBannerBuyCourseEntity(responseEntity);
                if (bannerBuyCourseEntity.getBannerMessages() != null) {
                    if (mPager != null) {
                        //开始录播
                        mPager.startBanner(bannerBuyCourseEntity.getBannerMessages());
                    }
                }
            }
        };

        mPager.getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCourseHttpManager().getBuyCourseBannerInfo(
                        mVideoEntity.getPaidBannerInfoUrl(),
                        mVideoEntity.getSubjectId(),
                        mVideoEntity.getChapterId(),
                        bannerMessageHttpCallBack
                );
            }
        }, delayHttpTime);

    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (mPager != null) {
            mPager.onDestroy();
            mPager = null;
        }
    }

    @Override
    public int[] getCategorys() {
        return new int[]{
                LocalCourseConfig.CATEGORY_RECOMMOND_COURSE
        };

    }

    /**
     * 购课成功后的回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(MiniEvent event) {
        if ("OrderPaySuccess".equals(event.getMin())) {
            // 添加用户购买成功的日志
//            StableLogHashMap logHashMap = new StableLogHashMap("purchaseSucceed");
//            logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
//            logHashMap.addSno("7").addStable("2");
//            logHashMap.put("orderid", event.getCourseId());
//            logHashMap.put("extra", "用户支付成功");
//            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
            buyRecommondCourseComplete(true);

        }
    }

    /**
     * 购买课程之后的回调
     *
     * @param isSuccess 购课成功的回调
     */
    public void buyRecommondCourseComplete(Boolean isSuccess) {
        if (isSuccess) {
            if (mPager != null && mPager.getRootView().getParent() == mRootView) {
                mRootView.removeView(mPager.getRootView());
            }
        }
    }

    public void onResume() {
        turnToOrder.onResume();
    }
}

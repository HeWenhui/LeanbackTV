package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.recommodcourse;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.RelativeLayout;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RecommondCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoBannerBuyCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.StandExperienceRecommondCourseEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.VideoPopView;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionShowReg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * 课中推荐课程的业务逻辑
 */
public class StandExperienceRecommondBll extends StandExperienceEventBaseBll {
    LiveAndBackDebug liveAndBackDebug;
    private StandExperienceRecommondPager mPager;
    private VideoPopView turnToOrder;
    private SharedPreferences sharedPreferences;

    private final String spFileName = "xes_stand_experience_is_buy_recommond_course";
    private final String SharedPreferenceKey = "IS_STAND_EXPERIENCE_BUY_RECOMMOND_COURSE";
    //是否购买成功
    private Boolean isBuyRecommondCourse;

    public StandExperienceRecommondBll(Activity activity, StandExperienceLiveBackBll liveBackBll, VideoView videoView) {
        super(activity, liveBackBll);


        turnToOrder = new VideoPopView((Activity) mContext, videoView);
        logger.i("注册EventBus");
        EventBus.getDefault().register(this);

    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);

        String fileName = spFileName + liveGetInfo.getStuId();
        sharedPreferences = mContext.getApplicationContext().getSharedPreferences
                (fileName, MODE_PRIVATE);
        isBuyRecommondCourse = sharedPreferences.getBoolean(SharedPreferenceKey, false);
    }

    @Override
    public void initView() {
        super.initView();
        mPager = new StandExperienceRecommondPager(mContext, isBuyRecommondCourse, liveGetInfo.getUname());


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
            mPager.setClickListener(new StandExperienceRecommondPager.ClickListener() {
                //跳转到购课页面
                @Override
                public void clickBuyCourse() {
                    logger.i("courseId = " + mRecommondCourseEntity.getCourseId() + " classId = " +
                            mRecommondCourseEntity.getClassId());
                    turnToOrder.turnToOrder(new StandExperienceRecommondCourseEvent("Order", mRecommondCourseEntity
                            .getCourseId(), mRecommondCourseEntity.getClassId()));
                }
            });
        }
    }

    //推荐课程信息
    private RecommondCourseEntity mRecommondCourseEntity;
    //录播消息信息
    private VideoBannerBuyCourseEntity bannerBuyCourseEntity;
    //解析器
    private LivePlayBackHttpResponseParser livePlayBackHttpResponseParser;
    //10秒后发送请求获取购课Banner的http请求
    private final int delayHttpTime = 1000 * 3;

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
        logger.i("显示推荐课程");
//        if (getIsResultComplete()) {
//            return;
//        }
        if (mPager == null) {
            logger.i("isBuyRecommondCourse" + isBuyRecommondCourse);
            mPager = new StandExperienceRecommondPager(mContext, isBuyRecommondCourse, liveGetInfo.getUname());
            initListener();
            registerInBllHideView();
        }
        if (livePlayBackHttpResponseParser == null) {
            livePlayBackHttpResponseParser = getCourseHttpResponseParser();
        }
        HttpCallBack httpCallBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                if (!getIsResultComplete()) {
                if (responseEntity != null) {
                    logger.i(responseEntity.toString());
                }
                mRecommondCourseEntity = livePlayBackHttpResponseParser.parseRecommondCourseInfo(responseEntity);
                mPager.updateView(mRecommondCourseEntity);
                mRootView.addView(mPager.getRootView(), RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout
                        .LayoutParams.MATCH_PARENT);
                logger.i("添加弹窗");
//                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.i("onPmFailure");
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.i("onPmError");
            }
        };
        //发送http请求，得到推荐课程数据
        getCourseHttpManager().getRecommondCourseInfo(
                mVideoEntity.getRecommendClassUrl(),
                mVideoEntity.getTeacherId(),
//                "2769",
                mVideoEntity.getGradId(),
                mVideoEntity.getSubjectId(),
                mVideoEntity.getChapterId(),
                httpCallBack
        );
        //添加窗口
        final HttpCallBack bannerMessageHttpCallBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                if (getIsResultComplete()) {
//                    return;
//                }
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
        }, 0);

    }

    @Override
    public void onDestory() {
        super.onDestory();
        logger.i("移出EventBus");
        EventBus.getDefault().unregister(this);
        removeView();
        if (mPager != null) {
            mPager.onDestroy();
            mPager = null;
        }
        if (turnToOrder != null) {
            turnToOrder.onDestroy();
        }
    }

    @Override
    public int[] getCategorys() {
        return new int[]{
                LocalCourseConfig.CATEGORY_RECOMMOND_COURSE
        };

    }

    private void removeView() {
        if (mPager != null && mPager.getRootView().getParent() == mRootView) {
            logger.i("移除了RecommondCoursePager");
            mRootView.removeView(mPager.getRootView());
        }
    }

    /**
     * 购课成功后的回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnPaySuccessEvent event) {
        logger.i("发布到EventBus这里来了");
//        if ("OrderPaySuccess".equals(event.getMin())) {
        // 添加用户购买成功的日志
//            StableLogHashMap logHashMap = new StableLogHashMap("purchaseSucceed");
//            logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
//            logHashMap.addSno("7").addStable("2");
//            logHashMap.put("orderid", event.getCourseId());
//            logHashMap.put("extra", "用户支付成功");
//            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
        logger.i("购课成功");

        isBuyRecommondCourse = true;
        //写入数据库中
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SharedPreferenceKey, isBuyRecommondCourse);
        editor.commit();
        buyRecommondCourseComplete(isBuyRecommondCourse);
//        }
    }

    /**
     * 购买课程之后的回调
     *
     * @param isSuccess 购课成功的回调
     */
    public void buyRecommondCourseComplete(Boolean isSuccess) {
        if (isSuccess) {
            if (mPager != null && mPager.getRootView().getParent() == mRootView) {
                logger.i("购课成功");
                mPager.buyCourseSuccess();
            }
        }
    }

    public void onResume() {
        turnToOrder.onResume();
        //埋点
        //埋点这里有个小忧患，isBuyRecommondCourse是在EventBus中赋值的，EventBus是接收到支付成功的广播时发送的，
        // 可能涉及到1.Fragment.onResume()方法和上面2.EventBus的接受方法onEvent的  先后调用顺序
        //但是在这里用户在支付完成后会停留在支付完成页一段时间才返回到视频中(这段时间足够发送广播和广播中的EventBus)，所以这里认为onEvent会在onResume()前面调用
        if (isBuyRecommondCourse) {//购课成功，即从支付成功页面回来的
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                    .stand_experience_1706001));
        } else {//没有支付，即从确认订单页面回来
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                    .stand_experience_1705001));
        }
    }
}

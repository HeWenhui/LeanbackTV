package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.recommodcourse;

import android.app.Activity;
import android.os.Build;
import android.view.ViewGroup;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.event.MiniEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.parentsmeeting.module.videoplayer.entity.RecommondCourseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoBannerBuyCourseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoInter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatWindowManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RecommondCourseBll extends LiveBackBaseBll {
    LiveAndBackDebug liveAndBackDebug;
    private RecommondCoursePager mPager;

    public RecommondCourseBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        initListener();
    }

    @Override
    public void initView() {
        super.initView();
        mPager = new RecommondCoursePager(mContext);

    }

    private void initListener() {
        if (mPager != null) {
            mPager.setClickListener(new RecommondCoursePager.ClickListener() {
                //跳转到购课页面
                @Override
                public void clickBuyCourse() {

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

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
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
                mRootView.addView(mPager.getRootView());
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
        HttpCallBack bannerMessageHttpCallBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                bannerBuyCourseEntity = livePlayBackHttpResponseParser.parseBannerBuyCourseEntity(responseEntity);
                if (bannerBuyCourseEntity.getBannerMessages() != null) {
                    if (mPager != null) {
                        mRootView.post(mPager.getBannerMessageRunnable());
                    }
                }
            }
        };
        getCourseHttpManager().getBuyCourseBannerInfo(
                mVideoEntity.getPaidBannerInfoUrl(),
                mVideoEntity.getSubjectId(),
                mVideoEntity.getChapterId(),
                bannerMessageHttpCallBack
        );
        //添加窗口

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
}

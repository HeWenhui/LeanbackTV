package com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/1/15.
 */

public class LecBackAdvertBll {
    String TAG = "LecBackAdvertBll";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    LecBackAdvertHttp lecBackAdvertHttp;
    WeakHandler handler = new WeakHandler(null);
    Activity activity;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    /** 讲座购课广告的页面 */
    private LecAdvertPager lecAdvertPager;
    LecBackAdvertPopBll lecBackAdvertPopBll;

    public LecBackAdvertBll(Activity activity) {
        this.activity = activity;
    }

    public void setLecBackAdvertPopBll(LecBackAdvertPopBll lecBackAdvertPopBll) {
        this.lecBackAdvertPopBll = lecBackAdvertPopBll;
    }

    public void setmVideoEntity(VideoLivePlayBackEntity mVideoEntity) {
        this.mVideoEntity = mVideoEntity;
    }

    public void setLecBackAdvertHttp(LecBackAdvertHttp lecBackAdvertHttp) {
        this.lecBackAdvertHttp = lecBackAdvertHttp;
    }

    public void initView(RelativeLayout rlQuestionContent, AtomicBoolean mIsLand) {
        this.rlQuestionContent = rlQuestionContent;
    }

    public LecAdvertPager getLecAdvertPager() {
        return lecAdvertPager;
    }

    /** 讲座广告 */
    public void showLecAdvertPager(final VideoQuestionEntity questionEntity) {
        final LecAdvertEntity lecAdvertEntity = new LecAdvertEntity();
        lecAdvertEntity.course_id = questionEntity.getvQuestionType();
        lecAdvertEntity.id = questionEntity.getvQuestionID();
//        PageDataLoadEntity mPageDataLoadEntity = new PageDataLoadEntity(rlQuestionContent, R.id.fl_livelec_advert_content, DataErrorManager.IMG_TIP_BUTTON);
//        PageDataLoadManager.newInstance().loadDataStyle(mPageDataLoadEntity.beginLoading());
        lecBackAdvertHttp.getAdOnLL(mVideoEntity.getLiveId(), lecAdvertEntity, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                if (lecAdvertEntity.isLearn == 1) {
                    return;
                }
                lecAdvertPager = new LecAdvertPager(activity, lecAdvertEntity, new LecAdvertPagerClose() {

                    @Override
                    public void close(boolean land) {
                        if (lecAdvertPager != null) {
                            lecAdvertPager.onDestroy();
                            rlQuestionContent.removeView(lecAdvertPager.getRootView());
                        }
                        logger.d("showLecAdvertPager:close=" + (questionEntity == null));
                        lecAdvertPager = null;
                        if (land) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    }

                    @Override
                    public void onPaySuccess(LecAdvertEntity lecAdvertEntity) {

                    }
                }, mVideoEntity.getLiveId());
                rlQuestionContent.removeAllViews();
                rlQuestionContent.addView(lecAdvertPager.getRootView(), new ViewGroup.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rlQuestionContent.setVisibility(View.VISIBLE);
                lecAdvertPager.initStep1();
                // 04.12 更多课程的列表刷新
                lecBackAdvertPopBll.getMoreCourseChoices();
            }
        });
    }

    void onDestroy() {

    }
}

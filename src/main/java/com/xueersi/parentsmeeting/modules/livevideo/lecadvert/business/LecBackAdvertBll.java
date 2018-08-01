package com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.event.MiniEvent;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoViewActivity;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatWindowManager;
import com.xueersi.ui.adapter.CommonAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile.liveBll;

/**
 * Created by lyqai on 2018/1/15.
 */

public class LecBackAdvertBll {
    String TAG = "LecBackAdvertBll";
    LecBackAdvertHttp lecBackAdvertHttp;
    WeakHandler handler = new WeakHandler(null);
    Activity activity;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    /** 讲座购课广告的页面 */
    private LecAdvertPager lecAdvertPager;
    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;
    LecBackAdvertPopBll lecBackAdvertPopBll;

    public LecBackAdvertBll(Activity activity) {
        this.activity = activity;
        lecBackAdvertPopBll = new LecBackAdvertPopBll(activity);
    }

    public void setmVideoEntity(VideoLivePlayBackEntity mVideoEntity) {
        this.mVideoEntity = mVideoEntity;
    }

    public void setLecBackAdvertHttp(LecBackAdvertHttp lecBackAdvertHttp) {
        this.lecBackAdvertHttp = lecBackAdvertHttp;
        lecBackAdvertPopBll.setLecBackAdvertHttp(lecBackAdvertHttp);
    }

    public void initView(RelativeLayout rlQuestionContent, AtomicBoolean mIsLand) {
        this.rlQuestionContent = rlQuestionContent;
        lecBackAdvertPopBll.initView(rlQuestionContent, mIsLand);
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
        lecBackAdvertPopBll.setVideoView(videoView);
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
                    public void close() {
                        if (lecAdvertPager != null) {
                            lecAdvertPager.onDestroy();
                            rlQuestionContent.removeView(lecAdvertPager.getRootView());
                        }
                        Loger.d(TAG, "showLecAdvertPager:close=" + (questionEntity == null));
                        lecAdvertPager = null;
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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

    public void onConfigurationChanged(Configuration newConfig) {
        lecBackAdvertPopBll.onConfigurationChanged(newConfig);
    }

    protected void onRestart() {
        lecBackAdvertPopBll.onRestart();
    }
    void onDestory(){
        lecBackAdvertPopBll.onDestory();
    }
}

package com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.experience.pager.ExperienceGuidePager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;

import java.util.HashMap;

public class ExperienceGuideBll extends LiveBackBaseBll implements IPagerControl,IPlayStatus {

    ExperienceGuidePager mGuidePager;
    RelativeLayout rlViewContent;
    boolean iShowGuidePager = false;
    VideoLivePlayBackEntity mVideoEntity;
    LivePlayBackHttpManager livePlayBackHttpManager;
    private static final long COUNTDOWN_TIME = 900;
    private static final int MIN_TIME = 60;
    private boolean isExperienceGuideShow = false;
    private long startTime;
    private PlayerService mPlayer;

    Runnable mPauseRunnable =  new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null){
                mPlayer.pause();
            }
        }
    };

    public ExperienceGuideBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        this.mVideoEntity = mVideoEntity;
        //根据接口返回字段判断是否可以弹出新手引导
        if (!mVideoEntity.isNoviceGuide()) {
            mGuidePager = new ExperienceGuidePager(mContext, this, COUNTDOWN_TIME - Long.valueOf(mVideoEntity.getVisitTimeKey()), mVideoEntity.getSubjectId());
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void initView() {
        //正式课前60s以上才可以进入新手引导
        if (mRootView != null && mGuidePager != null && COUNTDOWN_TIME - Long.valueOf(mVideoEntity.getVisitTimeKey()) > MIN_TIME) {
            showPager();
            submitNovicGuide();
        } else {
            liveBackBll.removeBusinessBll(this);
        }
    }

    @Override
    public boolean showPager() {
        isExperienceGuideShow = true;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (rlViewContent == null) {
            rlViewContent = new RelativeLayout(activity);
            rlViewContent.setId(R.id.rl_livevideo_experience_feedback_quit);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.addView(rlViewContent, params);
        } else {
            rlViewContent.removeAllViews();
        }
        View view = mGuidePager.getRootView();
        logger.i("showpager");
        rlViewContent.addView(view, params);
        return false;
    }

    @Override
    public boolean removePager() {
        if (rlViewContent != null) {
            rlViewContent.removeAllViews();
            mRootView.removeCallbacks(mPauseRunnable);
            isExperienceGuideShow = false;
            if (mPlayer != null){
                mPlayer.start();
                mPlayer.seekTo(Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000 + (System
                        .currentTimeMillis() - startTime));
            }
        }
        return false;
    }

    private void submitNovicGuide() {
        livePlayBackHttpManager = new LivePlayBackHttpManager(mContext);
        livePlayBackHttpManager.sumbitExperienceNoviceGuide(LiveAppUserInfo.getInstance().getStuId(), mVideoEntity.getChapterId(), mVideoEntity.getSubjectId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("submitNovicGuide");
            }
        });
    }


    @Override
    public void onPlayOpenStart() {

    }

    @Override
    public void onPlaySuccess(PlayerService vPlayer) {
        //暂停播放的视频
        mPlayer = vPlayer;
        if (vPlayer != null && isExperienceGuideShow) {
            mRootView.post(mPauseRunnable);
        }
    }

    @Override
    public void onPlayingPosition(long currentPosition, long duration) {

    }

    @Override
    public void onPlayComplete() {

    }
}

package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.leclearnreport.business.LecLearnReportIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

/**
 * Created by linyuqiang on 2018/7/18.
 * 讲座布局
 */
public class LectureLiveVideoFrame extends LiveFragmentBase {
    private String TAG = "LectureLiveVideoFrameLog";
    protected LiveIRCMessageBll liveIRCMessageBll;
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    protected BaseLiveMediaControllerBottom liveMediaControllerBottom;
    RelativeLayout bottomContent;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        boolean onVideoCreate = super.onVideoCreate(savedInstanceState);
        if (onVideoCreate) {
            long before = System.currentTimeMillis();
            createLiveVideoAction();
            liveVideoAction.setFirstParam(LiveVideoPoint.getInstance());
            initAllBll();
            logger.d("onVideoCreate:time2=" + (System.currentTimeMillis() - before));
            before = System.currentTimeMillis();
            addBusiness(activity, bottomContent);
            logger.d("onVideoCreate:time3=" + (System.currentTimeMillis() - before));
        }
        return onVideoCreate;
    }

    private void initAllBll() {
        logger.d("====>initAllBll:" + bottomContent);
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        setMediaControllerBottomParam();
        videoFragment.setIsAutoOrientation(true);
    }

    @Override
    protected void startGetInfo() {
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
        LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(liveType + "-" + mVSectionID);
        mLiveBll.getInfo(mGetInfo);
    }

    @Override
    protected void onGlobalLayoutListener() {
        super.onGlobalLayoutListener();
        setMediaControllerBottomParam();
    }

    /**
     * 控制栏下面距离视频底部的尺寸
     */
    public void setMediaControllerBottomParam() {
        //控制栏下面距离视频底部的尺寸
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
        int topGap = liveVideoPoint.y2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    @Override
    public boolean initData() {
        Intent intent = activity.getIntent();
        mVideoType = MobEnumUtil.VIDEO_LIVE;
        mVSectionID = intent.getStringExtra("vSectionID");
        if (TextUtils.isEmpty(mVSectionID)) {
            Toast.makeText(activity, "直播场次不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
        XesMobAgent.enterLiveRoomFrom(from);
        mLiveBll = new LiveBll2(activity, mVSectionID, liveType, from);
        ProxUtil.getProxUtil().put(activity, LiveBll2.class, mLiveBll);
        return true;
    }

    @Override
    public void rePlay(boolean modechange) {
        if (mGetInfo == null) {//上次初始化尚未完成
            return;
        }
        liveVideoAction.rePlay(modechange);
        mLiveVideoBll.rePlay(modechange);
    }

    protected void addBusiness(Activity activity, RelativeLayout bottomContent) {
        liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll, bottomContent);
        liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
        mLiveBll.addBusinessBll(liveIRCMessageBll);
        mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new LecAdvertIRCBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new LecLearnReportIRCBll(activity, mLiveBll, bottomContent));
        mLiveBll.setLiveIRCMessageBll(liveIRCMessageBll);
    }

    @Override
    protected void initView() {
        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        logger.e("========>:initView:" + bottomContent);
        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
        createMediaControllerBottom();
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
    }

    protected void createMediaControllerBottom() {
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mHaveStop) {
            mHaveStop = false;
            setFirstBackgroundVisible(View.VISIBLE);
            new Thread() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                rePlay(false);
                            }
                        });
                    }
                }
            }.start();
        }
        if (mLiveBll != null) {
            mLiveBll.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHaveStop = true;
        new Thread() {
            @Override
            public void run() {
                synchronized (mIjkLock) {
                    if (isInitialized()) {
                        if (openSuccess) {
                            mLiveVideoBll.stopPlayDuration();
                            Loger.d(TAG, "onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
                        }
                        vPlayer.releaseSurface();
                        vPlayer.stop();
                    }
                    isPlay = false;
                }
            }
        }.start();
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        liveMediaControllerBottom.setVisibility(View.VISIBLE);
        long before = System.currentTimeMillis();
        mMediaController.setFileName(getInfo.getName());
        Loger.d(TAG, "onLiveInit:time3=" + (System.currentTimeMillis() - before));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}

package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseBll;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/17.
 */

public class LiveBackBaseBll extends BaseBll {
    protected Logger logger = LoggerFactory.getLogger(TAG);
    LiveBackBll liveBackBll;
    protected Activity activity;
    protected RelativeLayout mRootView;
    /** 视频节对象 */
    protected VideoLivePlayBackEntity mVideoEntity;
    private AtomicBoolean mIsLand;
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    public LiveBackBaseBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity);
        this.activity = activity;
        this.liveBackBll = liveBackBll;
    }

    public final void onCreateF(VideoLivePlayBackEntity mVideoEntity, HashMap<String, Object> businessShareParamMap) {
        this.mVideoEntity = mVideoEntity;
        onCreate(mVideoEntity, businessShareParamMap);
    }

    public final void initViewF(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        mRootView = bottomContent;
        this.mIsLand = mIsLand;
        initView(bottomContent, mIsLand);
    }

    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {

    }

    public void onCreate(VideoLivePlayBackEntity mVideoEntity, HashMap<String, Object> businessShareParamMap) {

    }

    public int[] getCategorys() {
        return new int[0];
    }

    public void onQuestionEnd(VideoQuestionEntity questionEntity) {

    }

    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity) {

    }

    public LivePlayBackHttpManager getCourseHttpManager() {
        return liveBackBll.getCourseHttpManager();
    }

    public LivePlayBackHttpResponseParser getCourseHttpResponseParser() {
        return liveBackBll.getCourseHttpResponseParser();
    }
}

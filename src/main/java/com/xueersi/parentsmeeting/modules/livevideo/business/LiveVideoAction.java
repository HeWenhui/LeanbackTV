package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity2;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * Created by lyqai on 2018/7/18.
 */

public class LiveVideoAction implements VideoAction {
    private final String TAG = "LiveVideoAction";
    Logger logger = LoggerFactory.getLogger(TAG);
    protected WeakHandler mHandler = new WeakHandler(null);
    Activity activity;
    /** 初始进入播放器时的预加载界面 */
    protected RelativeLayout rlFirstBackgroundView;
    /** 老师不在直播间 */
    protected ImageView ivTeacherNotpresent;
    RelativeLayout mContentView;
    private TextView tvLoadingHint;
    /** 缓冲提示 */
    private ImageView ivLoading;
    /** 连接老师加载-主讲 */
    private final String mainTeacherLoad = "正在连接主讲老师，请耐心等候";
    /** 连接老师加载-辅导 */
    private final String coachTeacherLoad = "正在连接辅导老师，请耐心等候";
    /** 直播类型 */
    private int liveType;
    protected LiveGetInfo mGetInfo;
    private LiveBll2 mLiveBll;
    protected LogToFile mLogtf;

    public LiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView) {
        this.activity = activity;
        this.mLiveBll = mLiveBll;
        this.mContentView = mContentView;
        rlFirstBackgroundView = mContentView.findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = mContentView.findViewById(R.id.iv_course_video_teacher_notpresent);
        tvLoadingHint = mContentView.findViewById(R.id.tv_course_video_loading_content);
        ivLoading = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        tvLoadingHint.setText("获取课程信息");
        mLogtf = new LogToFile(mLiveBll, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        logger.setLogMethod(false);
        updateLoadingImage();
    }

    /**
     * 设置蓝屏界面
     */
    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlFirstBackgroundView.getLayoutParams();
        int rightMargin = liveVideoPoint.getRightMargin();
        logger.d("setFirstParam:point=" + liveVideoPoint.videoWidth + "," + rightMargin);
        int topMargin = liveVideoPoint.y2;
        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
            params.rightMargin = rightMargin;
            params.bottomMargin = params.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
        }
    }

    public void onPlayError() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                tvLoadingHint.setText("您的手机暂时不支持播放直播");
                mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void rePlay(boolean modechange) {
        new Thread() {
            @Override
            public void run() {
                boolean isPresent = mLiveBll.isPresent();
                if (isPresent) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (tvLoadingHint != null) {
                                if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                                    tvLoadingHint.setText(mainTeacherLoad);
                                } else {
                                    tvLoadingHint.setText(coachTeacherLoad);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
    }

    public void onFail(int arg1, final int arg2) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (tvLoadingHint != null) {
                    String errorMsg = null;
                    AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
                    if (error != null) {
                        errorMsg = error.getNum() + " (" + error.getTag() + ")";
                    }
                    TextView tvFail = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_fail);
                    if (errorMsg != null) {
                        if (tvFail != null) {
                            tvFail.setVisibility(View.VISIBLE);
                            tvFail.setText(errorMsg);
                        }
                    } else {
                        if (tvFail != null) {
                            tvFail.setVisibility(View.INVISIBLE);
                        }
                    }
                    if (mLiveBll.isPresent()) {
                        if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                            tvLoadingHint.setText(mainTeacherLoad);
                        } else {
                            tvLoadingHint.setText(coachTeacherLoad);
                        }
                    }
                    LiveTopic.RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
                    if (status != null) {
                        mLogtf.d("onFail:classbegin=" + status.isClassbegin());
                    }
                }
            }
        });
    }

    @Override
    public void onTeacherNotPresent(boolean isBefore) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                } else {
                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
                    ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onTeacherQuit(boolean isQuit) {

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        this.mGetInfo = getInfo;
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        final AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (change.get()) {
                    setFirstBackgroundVisible(View.VISIBLE);
                }
                if (tvLoadingHint != null) {
                    if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        });
    }

    @Override
    public void onClassTimoOut() {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "你来晚了，下课了，等着看回放吧";
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                setFirstBackgroundVisible(View.VISIBLE);
                if (isPresent) {
                    if (tvLoadingHint != null) {
                        if (LiveTopic.MODE_CLASS.endsWith(mode)) {
                            tvLoadingHint.setText(mainTeacherLoad);
                        } else {
                            tvLoadingHint.setText(coachTeacherLoad);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "" + responseEntity.getErrorMsg();
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    @Override
    public void onLiveDontAllow(final String msg) {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
        XESToastUtils.showToast(activity, "将在3秒内退出");
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("msg", msg);
                activity.setResult(ShareBusinessConfig.LIVE_USER_ERROR, intent);
                activity.finish();
            }
        }, 3000);
    }

    public void updateLoadingImage() {
        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
            }
        }
    }

    public void setFirstBackgroundVisible(int visible) {
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }
}

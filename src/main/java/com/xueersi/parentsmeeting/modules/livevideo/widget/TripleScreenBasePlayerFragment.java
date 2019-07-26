package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.LiveLogUtils;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.ps.PSIJK;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 三分屏直播的基础播放Fragment，这里主要作用是现实自定义的加载中
 */
public class TripleScreenBasePlayerFragment extends BasePlayerFragment {

    /** 三分屏使用的loading页面 */
    private ConstraintLayout layoutLoading;
    /** 决定加载中的loading是否显示 1为显示 */
    private int iVisibible = 1;
    /** 用来判断是否使用新的加载中View,1显示语文三分屏的View，2显示三分屏的View */
    private int judgeView = ORDINARY_LOADING;
    private ImageView ivLoading;
    /** 显示初高中三分屏的加载 */
    public static final int TRIPLE_SCREEN_MIDDLE_LOADING = 1;
    /** 显示小学语文三分屏的加载 */
    public static final int TRIPLE_SCREEN_PRIMARY_CHINESE_LOADING = 2;
    /** 显示小学理科三分屏的加载 */
    public static final int TRIPLE_SCREEN_PRIMARY_SCIENCE_LOADING = 3;
    /** 显示小学英语三分屏加载 */
    public static final int TRIPLE_SCREEN_PRIMARY_ENGLISH_LOADING = 4;
    /** 其他直播使用普通加载 */
    public static final int ORDINARY_LOADING = 0;
    Drawable loadingDrawable;

    protected int pattern = 0;

    private boolean isFirstShow = true;

//    private TextView tvVideoLoadingText;

//    private RelativeLayout videoLoadingLayout;

    /** 设置加载中的动画 */
    public void setLoadingAnimation(int view) {
        this.judgeView = view;

        switch (view) {
            case TRIPLE_SCREEN_MIDDLE_LOADING:
//                loadingDrawable = getActivity().getResources().getDrawable(R.drawable.anim_livevideo_triple_screen_loading);
                break;
            case TRIPLE_SCREEN_PRIMARY_CHINESE_LOADING:
                loadingDrawable = getActivity().getResources().getDrawable(R.drawable.anim_livevideo_triple_screen_primary_chinese_loading);
                break;
            case TRIPLE_SCREEN_PRIMARY_ENGLISH_LOADING:
                loadingDrawable = getActivity().getResources().getDrawable(R.drawable.anim_livevideo_triple_screen_primary_english_loading);
                break;
            case TRIPLE_SCREEN_PRIMARY_SCIENCE_LOADING:
                loadingDrawable = getActivity().getResources().getDrawable(R.drawable.anim_livevideo_triple_screen_primary_science_loading);
                break;
            case ORDINARY_LOADING:
                break;
            default:
                break;
        }
    }

    boolean isSmallEnglish;
    private ViewGroup loadingLayout;
    private RelativeLayout rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        logger.d("onCreateView");
        Intent paramIntent = getActivity().getIntent();
        isSmallEnglish = paramIntent.getBooleanExtra("isSmallEnglish", false);
        pattern = activity.getIntent().getIntExtra("pattern", 2);
//        if (isSmallEnglish || LiveVideoConfig.isPrimary || LiveVideoConfig.isSmallChinese) {
//
//            rootView = getActivity().findViewById(R.id.rl_course_video_live_question_content);
//
//            // 播放器所在的io.vov.vitamio.widget.CenterLayout
//            viewRoot = (ViewGroup) inflater.inflate(R.layout.layout_livevideo_triple_screen_player, container, false);
//            videoView = viewRoot.findViewById(R.id.vv_course_video_video); // 播放器的videoView
//            videoView.initialize(activity, this, mIsHWCodec); // 初始化播放器所在的画布
//            tvVideoLoadingText = viewRoot.findViewById(R.id.tv_course_video_loading_tip); // 加载进度文字框
//            videoLoadingLayout = viewRoot.findViewById(R.id.rl_course_video_loading); // 加载进度动画
//
//            loadingLayout = (ViewGroup) inflater.inflate(R.layout.layout_livevideo_triple_screen_load_player, null);
//            layoutLoading = loadingLayout.findViewById(R.id.layout_livevideo_triple_screen_loading);
//            ivLoading = loadingLayout.findViewById(R.id.iv_livevideo_triple_screen_loading);
//

//            return viewRoot;
//        } else if (pattern == 1) {
//
//        } else {
//            return super.onCreateView(inflater, container, savedInstanceState);
//        }
        ViewGroup viewGroup = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);

        return viewGroup;
    }

    private final void setDrawable() {
        ivLoading.setBackground(loadingDrawable);
    }

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_FILE:
                    // 打开新的视频时长统计初始化
                    // 准备开始播放指定视频
                    synchronized (mOpenLock) {
                        if (!mOpened.get() && vPlayer != null) {
                            if (!MediaPlayer.getIsNewIJK()) {
                                mOpened.set(true);
                                vPlayer.setVPlayerListener(vPlayerServiceListener);
                                if (vPlayer.isInitialized()) {
                                    //这个地方可能会播放错误的地址，参照TripleScreenBasePlayerFragment
                                    Uri olduri = vPlayer.getUri();
                                    logger.d("playNewVideo:olduri=" + olduri);
                                    vPlayer.release();
                                    vPlayer.releaseContext();
                                }

                                if (videoView != null) {
                                    vPlayer.setDisplay(videoView.getHolder());
                                }
                                if (mUri != null) {
                                    vPlayer.initialize(mUri, video, getStartPosition(), vPlayerServiceListener, mIsHWCodec);
                                    initCallBack();
                                }
                            } else {
                                mOpened.set(true);
                                vPlayer.setVPlayerListener(vPlayerServiceListener);
                                if (videoView != null) {
                                    vPlayer.setDisplay(videoView.getHolder());
                                }
                                boolean isPlayerCreated = vPlayer.psInit(MediaPlayer.VIDEO_PLAYER_NAME, getStartPosition(), vPlayerServiceListener, mIsHWCodec);
                                setVideoConfig();
                                if (isChangeLine) {
                                    try {
                                        vPlayer.changeLine(changeLinePos, protocol);
                                        isChangeLine = false;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Map<String, String> map = new HashMap<>();
                                        map.put("changeLinePos", changeLinePos + "");
                                        map.put("protocol", protocol + "");
                                        map.put(LiveLogUtils.EXCEPTION_MESSAGE, Log.getStackTraceString(e));
                                        map.put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.CHANGE_LINE_EXCEPTION);
                                        if (getActivity() != null) {
                                            UmsAgentManager.umsAgentDebug(getActivity(), LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map);
                                        }
                                    }
                                } else {
                                    String userName = null;
                                    String userId = null;
                                    try {
                                        userName = AppBll.getInstance().getAppInfoEntity().getChildName();
                                        userId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                                        if (TextUtils.isEmpty(userName)) {
                                            userName = "";
                                        }
                                        if (videoConfigEntity != null) {
                                            videoConfigEntity.setUserName(userName);
                                            videoConfigEntity.setUserId(userId);
                                        }
                                        if (vPlayer.getPlayer() instanceof PSIJK) {
                                            vPlayer.getPlayer().setUserInfo(userName, userId);
                                        }
                                        vPlayer.playPSVideo(streamId, protocol);
                                    } catch (IOException e) {
                                        vPlayerHandler.sendEmptyMessage(OPEN_FAILED);
                                        StableLogHashMap map = new StableLogHashMap();
                                        map.put("userName", userName).
                                                put("userId", userId + "").
                                                put("streamId", streamId).
                                                put("protocol", String.valueOf(protocol)).
                                                put("isPlayerCreated", String.valueOf(isPlayerCreated)).
                                                put("initPlayer", String.valueOf(vPlayer.checkNotNull())).
                                                put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.PLAY_EXCEPTION).
                                                put(LiveLogUtils.EXCEPTION_MESSAGE, Log.getStackTraceString(e));
                                        if (getActivity() != null) {
                                            UmsAgentManager.umsAgentDebug(getActivity(), LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map.getData());
                                        }
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        if (videoConfigEntity != null) {
                                            recordFailData(videoConfigEntity.toJSONObject().toString());
                                        } else {
                                            StableLogHashMap map = new StableLogHashMap();
                                            map.put("userName", userName).
                                                    put("userId", userId).
                                                    put("streamId", streamId).
                                                    put("protocol", String.valueOf(protocol)).
                                                    put("isPlayerCreated", String.valueOf(isPlayerCreated)).
                                                    put("initPlayer", String.valueOf(vPlayer.checkNotNull())).
                                                    put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.PLAY_EXCEPTION).
                                                    put(LiveLogUtils.EXCEPTION_MESSAGE, Log.getStackTraceString(e));
                                            if (getActivity() != null) {
                                                UmsAgentManager.umsAgentDebug(getActivity(), LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map.getData());
                                            }
                                        }
                                        e.printStackTrace();
                                        LiveCrashReport.postCatchedException(new LiveException(getClass().getSimpleName(), e));
                                    }
                                }
                                initCallBack();
                            }
                        }
                    }
                    break;
                case OPEN_START:
                    // 统计播放器初始化成功
                    XesMobAgent.userMarkVideoInit();
                    // 播放器初始化完毕准备开始加载指定视频
                    tvVideoLoadingText.setText(R.string.video_layout_loading);
                    onPlayOpenStart();
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    break;
                case OPEN_SUCCESS:
                    // 统计播放开始
                    XesMobAgent.userMarkVideoPlay();
                    // 视频加载成功开始初始化一些播放参数，并开始播放和加载控制栏
                    loadVPlayerPrefs();
                    onPlayOpenSuccess();
                    setVideoLoadingLayoutVisibility(View.GONE);
                    setVideoLayout();
                    vPlayer.start();
                    showLongMediaController();
                    break;
                case OPEN_FAILED:
                    // 视频打开失败
                    int arg1 = msg.arg1, arg2 = msg.arg2;
                    resultFailed(arg1, arg2);
                    break;
                case STOP_PLAYER:
                    // 暂停播放
                    stopPlayer();
                    break;
                case SEEK_COMPLETE:
                    // seek完成
                    onSeekComplete();
                    break;
                case BUFFER_START:
                    // 网络视频缓冲开始
                    if (!isFirstShow) {
                        setVideoLoadingLayoutVisibility(View.VISIBLE);
                    } else {
                        if (getActivity() != null) {
                            rootView = getActivity().findViewById(R.id.rl_course_video_live_question_content);
                            if (isSmallEnglish || LiveVideoConfig.isPrimary || LiveVideoConfig.isSmallChinese) {
                                loadingLayout = (ViewGroup) View.inflate(getActivity(), R.layout.layout_livevideo_triple_screen_load_player, null);
                                layoutLoading = loadingLayout.findViewById(R.id.layout_livevideo_triple_screen_loading);
                                ivLoading = loadingLayout.findViewById(R.id.iv_livevideo_triple_screen_loading);
                                setDrawable();
                            } else {
                                loadingLayout = (ViewGroup) View.inflate(getActivity(), R.layout.layout_livevideo_triple_screen_middle_school_load_playerload, null);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                            rootView.addView(loadingLayout, layoutParams);

                            if (isSmallEnglish || LiveVideoConfig.isPrimary || LiveVideoConfig.isSmallChinese) {
                                setLayoutLoadingVisible(true);
                            }
                            isFirstShow = false;
                            vPlayerHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                removeLoadingView();
                                    removeLoadingView();
//                                setLayoutLoadingVisible(false);
                                }
                            }, 2000);
                        }
                    }
                    vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                    break;
                case BUFFER_PROGRESS:
                    // 视频缓冲中进行进度更新
                    if (!vPlayer.isBuffering() || vPlayer.getBufferProgress() >= 100) {
                        setVideoLoadingLayoutVisibility(View.GONE);
                    } else {
                        // 视频缓冲中进行进度更新,tvVideoLoadingText.getVisibility()==View.GONE
//                        tvVideoLoadingText.setText(getString(R.string.video_layout_buffering_progress,
//                                vPlayer.getBufferProgress()));
                        vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                    }
                    break;
                case BUFFER_COMPLETE:
                    // 缓冲完毕
                    setVideoLoadingLayoutVisibility(View.GONE);
                    vPlayerHandler.removeMessages(BUFFER_PROGRESS);
                    break;
                case CLOSE_START:
                    // 开始退出播放
                    tvVideoLoadingText.setText(R.string.closing_file);
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    break;
                case CLOSE_COMPLETE:
                    // 播放器退出完毕，设置相应Boolean值
                    mCloseComplete = true;
                    break;
                case ON_PLAYING_POSITION:
                    // 播放中获取实时的进度
                    long[] arrPosition = (long[]) msg.obj;
                    if (arrPosition != null && arrPosition.length == 2) {
                        playingPosition(arrPosition[0], arrPosition[1]);
                    }
                    break;
                case HW_FAILED:
                    // 硬解码失败,尝试使用软解码初始化播放器
                    if (videoView != null) {
                        videoView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.initialize(activity, TripleScreenBasePlayerFragment.this, false);
                    }
                    break;
                case LOAD_PREFS:
                    // 初始化一些播放器的配置参数
                    loadVPlayerPrefs();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    /** 重写回调 */
    private void initCallBack() {

    }

    /** 重写这个Handler.CallBack */
    public void overrideHandlerCallBack() {
        if (LiveVideoConfig.isSmallChinese || LiveVideoConfig.isPrimary || isSmallEnglish) {
            vPlayerHandler = new WeakHandler(callback);
        }
    }

    @Override
    public void removeLoadingView() {
        super.removeLoadingView();
        if (loadingLayout != null && loadingLayout.getParent() == rootView) {
            rootView.removeView(loadingLayout);
        }
    }

    public void setLayoutLoadingVisible(boolean isShow) {
        if (isShow) {
            layoutLoading.setVisibility(iVisibible == 1 ? View.VISIBLE : View.GONE);
            startAnim();
        } else {
            layoutLoading.setVisibility(View.GONE);
            stopAnim();
        }
    }

    private void startAnim() {
//        ivLoading.setVisibility(View.VISIBLE);
        ivLoading.setImageResource(R.drawable.anim_livevideo_triple_screen_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getDrawable();
        animationDrawable.start();
    }

    private void stopAnim() {
//        ivLoading.setVisibility(View.GONE);
        ivLoading.setImageResource(R.drawable.anim_livevideo_triple_screen_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getDrawable();
        animationDrawable.stop();
    }

}

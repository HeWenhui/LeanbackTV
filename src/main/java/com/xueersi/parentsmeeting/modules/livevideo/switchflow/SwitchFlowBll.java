package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.MainThread;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStartChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.List;

public class SwitchFlowBll extends LiveBaseBll implements BaseLiveMediaControllerBottom.MediaChildViewClick {
    //    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private Context mContext;
    /** 出现时从右往左侧滑的动画 */
    private ObjectAnimator animationIn;
    /** 消失时从左往右侧滑的动画 */
    private ObjectAnimator animationOut;

    private SwitchFlowRoutePager mPager;

//    private List<PlayServerEntity.PlayserverEntity> listRoute;

    private boolean isRoutePagerShow = false;

//    private RelativeLayout bottomContent;
    /** 举麦是否打开 */
    private boolean isVoiceOn = false;

    public SwitchFlowBll(Activity mContext, LiveBll2 bll2) {
        super(mContext, bll2);
        this.mContext = mContext;
//        this.bottomContent = baseLiveMediaControllerBottom;
//        initView();
//        initData();
    }

    private int route = 0;

    private SwitchFlowView mView;

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);

        BaseLiveMediaControllerBottom.RegMediaChildViewClick regMediaChildViewClick = ProxUtil.getProxUtil().get
                (activity, BaseLiveMediaControllerBottom.RegMediaChildViewClick.class);
        if (regMediaChildViewClick != null) {
            regMediaChildViewClick.regMediaViewClick(this);
        }
        RegMediaPlayerControl regMediaPlayerControl = getInstance(RegMediaPlayerControl.class);
        regMediaPlayerControl.addMediaPlayerControl(new LiveMediaController.SampleMediaPlayerControl() {
            @Override
            public void onTitleShow(boolean show) {
                SwitchFlowBll.this.onTitleShow(show);
            }
        });
        VideoChatStartChange videoChatBll = getInstance(VideoChatStartChange.class);
        if (videoChatBll != null) {
            videoChatBll.addVideoChatStatrtChange(new VideoChatStartChange.ChatStartChange() {
                @Override
                public void onVideoChatStartChange(boolean start) {
                    isVoiceOn = start;
                    if (mView != null) {
                        mView.setIsVoiceOn(start);
                    }
                }
            });
        }
    }

    private void onTitleShow(boolean isShow) {
        if (animationOut != null) {
            if (isRoutePagerShow) {
                animationOut.start();
                isRoutePagerShow = false;
            }
        }
    }

    private List<PlayServerEntity.PlayserverEntity> listRoute;

    public void setListRoute(int total) {

        if (total != 0) {
            this.route = total < 4 ? total : 4;
        }
//        else if (total == 0 && mGetInfo != null) {
//            this.route = mGetInfo.getRtmpUrls().length;
//            mLogtf.i("switchFlowBll ,list.size()=" + mGetInfo.getRtmpUrls().length);
//        }
        else {
            route = 0;
        }
        if (mPager != null) {
            mPager.setRouteSum(route);
        }
    }

    public void setListRoute(List<PlayServerEntity.PlayserverEntity> listRoute) {
        this.listRoute = listRoute;

        if (listRoute == null) {
            logger.i("listRoute为null");
            mLogtf.i("switchFlowBll listRoute=null");
        } else {
            logger.i("listRoute数量为" + listRoute.size());
            mLogtf.i("switchFlowBll,list.size==" + listRoute.size());
        }
        if (listRoute != null && listRoute.size() != 0) {
            route = listRoute.size() < 4 ? listRoute.size() : 4;
        } else if (mGetInfo != null) {
            route = mGetInfo.getRtmpUrls().length;
            mLogtf.i("switchFlowBll ,list.size()=" + mGetInfo.getRtmpUrls().length);
        } else {
            route = 0;
        }
        if (mPager != null) {
            mPager.setRouteSum(route);
        }
    }

    private SwitchFlowRoutePager.ItemClickListener itemClickListener;

    public void setmView(final SwitchFlowView mView, final BaseLiveMediaControllerBottom liveMediaControllerBottom, final SwitchFlowView.IReLoad iReLoad, SwitchFlowRoutePager.ItemClickListener itemClickListener) {
        this.mView = mView;
        if (mView == null) {
            return;
        }
        mView.setSwitchFlowWholeVisible(true);
        mView.setIsVoiceOn(isVoiceOn);
        this.itemClickListener = itemClickListener;
        mView.setiSwitchFlow(new SwitchFlowView.ISwitchFlow() {
            @Override
            public void reLoad() {
//                if (!mLiveBll.isPresent()) {
//                    XESToastUtils.showToast(mContext, "老师不在直播间，请稍后再试");
//                    return;
//                }
                iReLoad.reLoad();
            }

            @Override
            public void switchRoute() {
//                if (!mLiveBll.isPresent()) {
//                    XESToastUtils.showToast(mContext, "老师不在直播间，请稍后再试");
//                    return;
//                }
                if (animationOut == null || animationIn == null || mPager == null) {
                    initPager();
                }
                if (!isRoutePagerShow) {
//                    mPager.initData();
                    logger.i("显示动画开始");
                    mLogtf.i("animator in start");
                    animationIn.start();
                    isRoutePagerShow = true;
                } else {
                    animationOut.start();
                    logger.i("关闭动画开始");
                    mLogtf.i("animator out start");
                    isRoutePagerShow = false;
                }

                LiveMediaController controller = liveMediaControllerBottom.getController();
                controller.show();
            }
        });
        mView.setClickListener(new SwitchFlowView.ISwitchFlowClickListener() {
            @Override
            public void click(View v) {
                liveMediaControllerBottom.onChildViewClick(v);
                LiveMediaController controller = liveMediaControllerBottom.getController();
                if (liveMediaControllerBottom.getLlMarkPopMenu() != null) {
                    liveMediaControllerBottom.getLlMarkPopMenu().setVisibility(View.GONE);
                }
                controller.show();
            }
        });
    }

    @MainThread
    private void initPager() {
        initView();
        addView();

        if (!MediaPlayer.getIsNewIJK()) {
            if (listRoute == null) {
                logger.i("listRoute为null");
            } else {
                logger.i("listRoute数量为" + listRoute.size());
            }
            if (listRoute != null && listRoute.size() != 0) {
                route = listRoute.size() < 4 ? listRoute.size() : 4;
            } else if (mGetInfo != null) {
                route = mGetInfo.getRtmpUrls().length;
            } else {
                route = 0;
            }
        }
        mPager.setRouteSum(route);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        animationIn = ObjectAnimator.ofFloat(mPager.getRootView(), "translationX", liveVideoPoint.x4 - liveVideoPoint.x3, 0);
        animationOut = ObjectAnimator.ofFloat(mPager.getRootView(), "translationX", 0, liveVideoPoint.x4 - liveVideoPoint.x3);
        animationIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mPager.getRootView().getVisibility() != View.VISIBLE) {
                    mPager.getRootView().setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animationOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mPager.getRootView().getVisibility() != View.GONE) {
                    mPager.getRootView().setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        initListener();
    }

    private void initListener() {
        mPager.setItemClickListener(new SwitchFlowRoutePager.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                if (mView != null) {
                    mView.setSwitchFlowPopWindowVisible(false);
                }
                animationOut.start();
                isRoutePagerShow = false;
                itemClickListener.itemClick(pos);
            }
        });
    }

    @MainThread
    private void initView() {
        mPager = new SwitchFlowRoutePager(mContext, false);
        mPager.init();
    }

    /** 把弹窗加入到RelativeLayout里面 */
    private void addView() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.width = liveVideoPoint.x4 - liveVideoPoint.x3;
        lp.rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
//        lp.rightMargin = liveVideoPoint.getRightMargin();
        mRootView.addView(mPager.getRootView(), lp);
//        setViewLayout();
    }

    /** 点击视频区域d调用这个方法 */
    @Override
    public void onMediaViewClick(View child) {
        onTitleShow(true);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        onTitleShow(true);
    }
}

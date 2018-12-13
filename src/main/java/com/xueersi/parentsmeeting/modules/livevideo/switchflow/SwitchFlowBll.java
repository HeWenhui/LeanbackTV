package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
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

    public SwitchFlowBll(Activity mContext, LiveBll2 bll2) {
        super(mContext, bll2);
        this.mContext = mContext;
//        this.bottomContent = baseLiveMediaControllerBottom;
//        initView();
//        initData();
    }

    private int route = 0;

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
    }

    private void onTitleShow(boolean isShow) {
        if (animationOut != null) {
            if (isRoutePagerShow) {
                animationOut.start();
                isRoutePagerShow = false;
            }
        }
    }

    public void setListRoute(List<PlayServerEntity.PlayserverEntity> listRoute) {
//        this.listRoute = listRoute;
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
        if (mPager != null) {
            mPager.setRouteSum(route);
        }
    }

    private SwitchFlowRoutePager.ItemClickListener itemClickListener;

    public void setmView(final SwitchFlowView mView, final BaseLiveMediaControllerBottom liveMediaControllerBottom, final SwitchFlowView.IReLoad iReLoad, SwitchFlowRoutePager.ItemClickListener itemClickListener) {
//        this.mView = mView;
        if (mView == null) {
            return;
        }
        this.itemClickListener = itemClickListener;
        mView.setiSwitchFlow(new SwitchFlowView.ISwitchFlow() {
            @Override
            public void reLoad() {
                iReLoad.reLoad();
            }

            @Override
            public void switchRoute() {
                if (animationOut == null || animationIn == null || mPager == null) {
                    initPager();
                }
                if (!isRoutePagerShow) {
//                    mPager.initData();
                    logger.i("显示动画开始");
                    animationIn.start();
                    isRoutePagerShow = true;
                } else {
                    animationOut.start();
                    logger.i("关闭动画开始");
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

    private void initPager() {
        initView();
        addView();
        mPager.setRouteSum(route);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        animationIn = ObjectAnimator.ofFloat(mPager.getRootView(), "translationX", liveVideoPoint.x4 - liveVideoPoint.x3, 0);
        animationOut = ObjectAnimator.ofFloat(mPager.getRootView(), "translationX", 0, liveVideoPoint.x4 - liveVideoPoint.x3);
        initListener();
    }

    private void initListener() {
        mPager.setItemClickListener(new SwitchFlowRoutePager.ItemClickListener() {
            @Override
            public void itemClick(int pos) {
                animationOut.start();
                itemClickListener.itemClick(pos);
            }
        });
    }

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

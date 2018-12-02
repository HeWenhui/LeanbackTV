package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;

import java.util.List;

public class SwitchFlowBll {
    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private Context mContext;
    /** 出现时从右往左侧滑的动画 */
    private ObjectAnimator animationIn;

    /** 消失时从左往右侧滑的动画 */
    private ObjectAnimator animationOut;

    private SwitchFlowRoutePager mPager;
    private SwitchFlowView mView;

    private List<PlayServerEntity.PlayserverEntity> listRoute;

    private boolean isRoutePagerShow = false;

    private RelativeLayout bottomContent;

    public SwitchFlowBll(Context mContext, RelativeLayout baseLiveMediaControllerBottom) {
        this.mContext = mContext;
        this.bottomContent = baseLiveMediaControllerBottom;
//        initView();
//        initData();
    }

    public void setListRoute(List<PlayServerEntity.PlayserverEntity> listRoute) {
        this.listRoute = listRoute;
        if (listRoute != null) {
            mPager.setRouteSum(listRoute.size() < 4 ? listRoute.size() : 4);
        }
    }

    private SwitchFlowRoutePager.ItemClickListener itemClickListener;

    public void setmView(SwitchFlowView mView, final SwitchFlowView.IReLoad iReLoad, SwitchFlowRoutePager.ItemClickListener itemClickListener) {
        this.mView = mView;
        this.itemClickListener = itemClickListener;
        mView.setiSwitchFlow(new SwitchFlowView.ISwitchFlow() {
            @Override
            public void reLoad() {
                iReLoad.reLoad();
            }

            @Override
            public void switchRoute() {
                if (animationOut == null || animationIn == null) {
                    initData();
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
            }
        });
    }

    private void initView() {
        mPager = new SwitchFlowRoutePager(mContext, false);
        mPager.setItemClickListener(itemClickListener);
        mPager.init();

    }


    private void initData() {
        if (mPager == null) {
            initView();
            addView();
        }

        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        animationIn = ObjectAnimator.ofFloat(mPager.getRootView(), "translationX", liveVideoPoint.x4 - liveVideoPoint.x3, 0);
        animationOut = ObjectAnimator.ofFloat(mPager.getRootView(), "translationX", 0, liveVideoPoint.x4 - liveVideoPoint.x3);
    }

    /** 把弹窗加入到RelativeLayout里面 */
    private void addView() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.width = liveVideoPoint.x4 - liveVideoPoint.x3;
//        lp.rightMargin = liveVideoPoint.getRightMargin();
        bottomContent.addView(mPager.getRootView(), lp);
//        setViewLayout();
    }
}

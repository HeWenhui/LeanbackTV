package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
import com.xueersi.xesalib.view.layout.dataload.DataErrorManager;
import com.xueersi.xesalib.view.layout.dataload.PageDataLoadEntity;
import com.xueersi.xesalib.view.layout.dataload.PageDataLoadManager;

/**
 * Created by lyqai on 2018/1/15.
 */

public class LecAdvertBll implements LecAdvertAction, LecAdvertPagerClose {
    String TAG = "LecAdvertBll";
    Context context;
    RelativeLayout bottomContent;
    LecAdvertPager lecAdvertager;
    LiveBll liveBll;

    public LecAdvertBll(Context context) {
        this.context = context;
    }

    public void setLiveBll(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    public void initView(RelativeLayout bottomContent, boolean isLand) {
        this.bottomContent = bottomContent;
        if (lecAdvertager != null) {
            ViewGroup group = (ViewGroup) lecAdvertager.getRootView().getParent();
            if (group != null) {
                group.removeView(lecAdvertager.getRootView());
            }
            if (!isLand) {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                bottomContent.addView(lecAdvertager.getRootView(), lp);
            } else {
                int step = lecAdvertager.getStep();
                if (step == 1) {
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    bottomContent.addView(lecAdvertager.getRootView(), lp);
                }
            }
        }
    }

    @Override
    public void start(final LecAdvertEntity lecAdvertEntity) {
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                if (lecAdvertager != null) {
                    return;
                }
//                PageDataLoadEntity mPageDataLoadEntity = new PageDataLoadEntity(lecAdvertager.getRootView(), R.id.fl_livelec_advert_content, DataErrorManager.IMG_TIP_BUTTON);
//                PageDataLoadManager.newInstance().loadDataStyle(mPageDataLoadEntity.beginLoading());
                liveBll.getAdOnLL(lecAdvertEntity, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        if (lecAdvertager != null) {
                            return;
                        }
                        lecAdvertager = new LecAdvertPager(context, lecAdvertEntity, LecAdvertBll.this);
                        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        bottomContent.addView(lecAdvertager.getRootView(), lp);
                        lecAdvertager.initStep1();
                    }
                });
            }
        });
    }

    @Override
    public void close() {
        if (lecAdvertager != null) {
            bottomContent.removeView(lecAdvertager.getRootView());
            lecAdvertager = null;
            if (context instanceof ActivityChangeLand) {
                ActivityChangeLand activityChangeLand = (ActivityChangeLand) context;
                activityChangeLand.setAutoOrientation(true);
            }
        }
    }

}
